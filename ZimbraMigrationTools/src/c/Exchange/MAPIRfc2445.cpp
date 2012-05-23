#include "common.h"
#include "Exchange.h"
#include "MAPIMessage.h"
#include "Logger.h"
#include "Zimbra\MimeConverter.h"

MAPIRfc2445::MAPIRfc2445(Zimbra::MAPI::MAPISession &session, Zimbra::MAPI::MAPIMessage &mMessage) :
    m_session(&session), m_mapiMessage(&mMessage)
{
    m_pMessage = m_mapiMessage->InternalMessageObject();
    m_pPropVals = NULL;
}

MAPIRfc2445::~MAPIRfc2445()
{
    if (m_pPropVals)
    {
        MAPIFreeBuffer(m_pPropVals);
    }
    m_pPropVals = NULL;
}

void MAPIRfc2445::IntToWstring(int src, wstring& dest)
{
    WCHAR pwszTemp[10];
    _ltow(src, pwszTemp, 10);
    dest = pwszTemp;
}

vector<AttachmentInfo*> MAPIRfc2445::GetAttachmentInfo() { return m_vAttachments; }

bool MAPIRfc2445::IsRecurring() {return m_bIsRecurring; }
wstring MAPIRfc2445::GetRecurPattern() { return m_pRecurPattern; }
wstring MAPIRfc2445::GetRecurInterval() { return m_pRecurInterval; }
wstring MAPIRfc2445::GetRecurCount() { return m_pRecurCount; }
wstring MAPIRfc2445::GetRecurWkday() { return m_pRecurWkday; }
wstring MAPIRfc2445::GetRecurEndType() { return m_pRecurEndType; };
wstring MAPIRfc2445::GetRecurEndDate() { return m_pRecurEndDate; };
wstring MAPIRfc2445::GetRecurDayOfMonth() { return m_pRecurDayOfMonth; };
wstring MAPIRfc2445::GetRecurMonthOccurrence() { return m_pRecurMonthOccurrence; };
wstring MAPIRfc2445::GetRecurMonthOfYear() { return m_pRecurMonthOfYear; };
Tz MAPIRfc2445::GetRecurTimezone() { return m_timezone; };

HRESULT ConvertIt(LPMESSAGE pMsg, IStream** ppszMimeMsg, UINT& mimeLength )                                                                       
{
    HRESULT hr = S_OK;

    Zimbra::Util::ScopedInterface<IConverterSession> pConvSess;
    hr = CreateConverterSession( pConvSess.getptr() );
    if( FAILED(hr))
    {
        return hr;
    }

    pConvSess->SetSaveFormat( SAVE_RFC1521 );
    IStream * iStream = NULL;				
    hr = OpenStreamOnFile(Zimbra::Mapi::Memory::AllocateBuffer, Zimbra::Mapi::Memory::FreeBuffer, 
                          STGM_READWRITE | STGM_CREATE | SOF_UNIQUEFILENAME | STGM_DELETEONRELEASE,										  
                          NULL,  (LPWSTR)L"mig", &iStream);
    if( FAILED(hr))
    {
        return hr;
    }

    HGLOBAL hGlobal = GlobalAlloc(GMEM_FIXED, 0 );
    Zimbra::Util::ScopedInterface<IStream> pStream;
    CreateStreamOnHGlobal( hGlobal, TRUE, pStream.getptr() );

    UINT uFlags = CCSF_NO_MSGID |  CCSF_SMTP | CCSF_NOHEADERS ;
	
    hr = pConvSess->MAPIToMime( pMsg, iStream, uFlags);
    if( FAILED(hr) ) {
	    return hr;
    }
    if( !iStream) {
	    return E_FAIL;
    }

    // get the stream size
    STATSTG stat;
    LARGE_INTEGER li = { 0 };
    hr = iStream->Seek( li, STREAM_SEEK_SET, NULL ); 	
    if( FAILED(hr) ) {
	    return hr;
    }

    hr = iStream->Stat( &stat, STATFLAG_DEFAULT );
    if( FAILED(hr) ) {
	    return hr;
    }

    ULONG size = (UINT)stat.cbSize.QuadPart;
    *ppszMimeMsg = iStream;
    mimeLength = (UINT)size;
	
    return hr;
}

int MAPIRfc2445::GetNumHiddenAttachments()
{
    int retval = 0;
    HRESULT hr = S_OK;
    LPCWSTR errMsg;
    Zimbra::Util::ScopedInterface<IStream> pSrcStream;
    Zimbra::Util::ScopedRowSet pAttachRows;
    Zimbra::Util::ScopedInterface<IMAPITable> pAttachTable;

    SizedSPropTagArray(1, attachProps) = {
        1, { PR_ATTACHMENT_HIDDEN }
    };

    hr = m_pMessage->GetAttachmentTable(MAPI_UNICODE, pAttachTable.getptr());
    if (SUCCEEDED(hr))
    {
        if (FAILED(hr = pAttachTable->SetColumns((LPSPropTagArray) &attachProps, 0)))
        {
            errMsg = FormatExceptionInfo(hr, L"Error setting attachment table columns", __FILE__, __LINE__);
            dlogw(errMsg);
            return 0;
        }
        ULONG ulRowCount = 0;
        if (FAILED(hr = pAttachTable->GetRowCount(0, &ulRowCount)))
        {
            errMsg = FormatExceptionInfo(hr, L"Error getting attachment table row count", __FILE__, __LINE__);
            dlogw(errMsg);
            return 0;
        }
        if (FAILED(hr = pAttachTable->QueryRows(ulRowCount, 0, pAttachRows.getptr())))
        {
            errMsg = FormatExceptionInfo(hr, L"Error querying attachment table rows", __FILE__, __LINE__);
            dlogw(errMsg);
            return 0;
        }
        if (SUCCEEDED(hr))
        {
            hr = MAPI_E_NOT_FOUND;
            for (unsigned int i = 0; i < pAttachRows->cRows; i++)
            {
                // if property couldn't be found or returns error, skip it
                if ((pAttachRows->aRow[i].lpProps[0].ulPropTag != PT_ERROR) &&
                    (pAttachRows->aRow[i].lpProps[0].Value.err != MAPI_E_NOT_FOUND))
                {
                    if (pAttachRows->aRow[i].lpProps[0].Value.b)
                    {
                        retval++;
                    }
                }
            }
        }
    }
    else
    {
        errMsg = FormatExceptionInfo(hr, L"Error getting attachment tables", __FILE__, __LINE__);
        dlogw(errMsg);
        return 0;
    }
    return retval;
}

HRESULT MAPIRfc2445::ExtractAttachments()
{
    // may need to break this up so we can call for exceptions, cancel exceptions

    LPCWSTR errMsg;
    Zimbra::Util::ScopedInterface<IStream> pIStream;
    UINT mimeLen = 0;
    HRESULT hr = ConvertIt( m_pMessage, pIStream.getptr(), mimeLen );
    if (FAILED(hr))
    {
        errMsg = FormatExceptionInfo(hr, L"Mime conversion of message with attachments failed", __FILE__, __LINE__);
        dlogw(errMsg);
        return hr;
    }

    mimepp::Message mimeMsg;
    Zimbra::Util::ScopedBuffer<CHAR> pszMimeMsg;

    // go to the beginning of the stream
    LARGE_INTEGER li = { 0 };

    hr = pIStream->Seek(li, STREAM_SEEK_SET, NULL);
    if (FAILED(hr))
    {
        errMsg = FormatExceptionInfo(hr, L"Stream seek failed", __FILE__, __LINE__);
        dlogw(errMsg);
        return hr;
    }

    // +1 for NULL terminator
    Zimbra::Mapi::Memory::AllocateBuffer(mimeLen + 1, (LPVOID *)pszMimeMsg.getptr());
    if (!pszMimeMsg.get())
    {
        errMsg = FormatExceptionInfo(S_OK, L"Mime msg Memory alloc failed", __FILE__, __LINE__);
        dlogw(errMsg);
        return hr;
    }

    ULONG ulNumRead = 0;

    hr = pIStream->Read((LPVOID)(pszMimeMsg.get()), mimeLen, &ulNumRead);
    if (FAILED(hr))
    {
        errMsg = FormatExceptionInfo(hr, L"Mime msg read failed", __FILE__, __LINE__);
        dlogw(errMsg);
        return hr;
    }
    if (ulNumRead != mimeLen)
    {
        errMsg = FormatExceptionInfo(hr, L"Mime msg read error", __FILE__, __LINE__);
        dlogw(errMsg);
        return hr;
    }

    // terminating string
    pszMimeMsg.get()[mimeLen] = '\0';

    mimeMsg.setString(pszMimeMsg.get());
    mimeMsg.parse();

    // let's see if this message is a multipart alternative before we continue
    mimepp::Headers &theHeaders = mimeMsg.headers();

    LPSTR pszContentType;
    GetContentType(theHeaders, &pszContentType);

    if(strncmp(pszContentType, "multipart/mixed", strlen("multipart/mixed")) != 0) 
    {
        // not what we are looking for
        delete[] pszContentType;
        return S_OK;
    }

    const mimepp::Body& theBody = mimeMsg.body();
    int numParts = theBody.numBodyParts();

    // FBS bug 73682 -- 5/23/12
    int numHiddenAttachments = GetNumHiddenAttachments();
    int totalAttachments = numParts - 1;
    if (totalAttachments == numHiddenAttachments)
    {
        return S_OK;
    }

    // let's look for a multipart mixed and grab the attachments
    int ctr = numHiddenAttachments;
    for(int i = 0; i < numParts; i++) 
    {
        // now look for attachments
        const mimepp::BodyPart& thePart = theBody.bodyPartAt(i);
        mimepp::DispositionType& disposition = thePart.headers().contentDisposition();
        if(disposition.asEnum() == mimepp::DispositionType::ATTACHMENT) 
        {
            const mimepp::String& theFilename = disposition.filename();

            LPSTR pszAttachContentType;
            LPSTR pszCD;
            LPSTR lpszRealName = new char[256];
            GetContentType(thePart.headers(), &pszAttachContentType);

            // FBS bug 73682 -- Exceptions are at the beginning.  Don't make attachments for those
            if (ctr > 0)
            {
                if (0 == strcmpi(pszAttachContentType, "message/rfc822"))
                {
                    ctr--;
                    continue;
                }
            }
            //

            if((LPSTR)theFilename.length()>0)
            {
                GenerateContentDisposition(&pszCD, (LPSTR)theFilename.c_str());
                strcpy(lpszRealName, (LPSTR)theFilename.c_str());
            }
            else
            {
                char cfilename[64];
                sprintf(cfilename,"attachment-%d",i);
                GenerateContentDisposition(&pszCD, cfilename);
                strcpy(lpszRealName, cfilename);
            }

            // now deal with the encoding
            LPSTR pContent = NULL;
            const mimepp::String &theContent = thePart.body().getString();
            mimepp::String outputString;
            UINT size = 0;

            mimepp::TransferEncodingType& transferEncoding = thePart.headers().contentTransferEncoding();

            if(transferEncoding.asEnum() == mimepp::TransferEncodingType::BASE64) 
            {
                // let's decode the buffer
                mimepp::Base64Decoder decoder;
                outputString = decoder.decode(theContent);
                pContent = (LPSTR)outputString.c_str();
                size = (UINT)outputString.size();
            } 
            else if(transferEncoding.asEnum() == mimepp::TransferEncodingType::QUOTED_PRINTABLE) 
            {
                mimepp::QuotedPrintableDecoder decoder;
                outputString  = decoder.decode(theContent);
                pContent = (LPSTR)outputString.c_str();
                size = (UINT)outputString.size();
            } 
            else 
            {
                pContent = (LPSTR)theContent.c_str();
                size = (UINT)theContent.size();
            }

	    // Save stream to temp file in temp dir.  We'll delete in ZimbraAPI //
            LPCWSTR errMsg;
            HRESULT hr = S_OK;

            wstring wstrTempAppDirPath;
            LPSTR lpszFQFileName = new char[256];
            LPSTR lpszDirName = NULL;
            LPSTR lpszUniqueName = NULL;
            Zimbra::Util::ScopedInterface<IStream> pStream;

            if (!Zimbra::MAPI::Util::GetAppTemporaryDirectory(wstrTempAppDirPath))
            {
                errMsg = FormatExceptionInfo(S_OK, L"GetAppTemporaryDirectory Failed", __FILE__, __LINE__);
                dloge("MAPIRfc2445 -- exception");
                dloge(errMsg);
                return E_FAIL;
            }
            WtoA((LPWSTR)wstrTempAppDirPath.c_str(), lpszDirName);
            WtoA((LPWSTR)Zimbra::MAPI::Util::GetUniqueName().c_str(), lpszUniqueName);
            strcpy(lpszFQFileName, lpszDirName);
            strcat(lpszFQFileName, "\\");
            strcat(lpszFQFileName, lpszUniqueName);

            SafeDelete(lpszDirName);
            SafeDelete(lpszUniqueName);

            // Open stream on file
            if (FAILED(hr = OpenStreamOnFile(MAPIAllocateBuffer, MAPIFreeBuffer, STGM_CREATE |
                    STGM_READWRITE, (LPTSTR)lpszFQFileName, NULL, pStream.getptr())))
            {
                errMsg = FormatExceptionInfo(hr, L"Error: OpenStreamOnFile Failed.", __FILE__, __LINE__);
                dloge("MAPIRfc2445 -- exception");
                dloge(errMsg);
                return hr;
            }

            ULONG nBytesToWrite = size;
            ULONG nBytesWritten = 0;
            LPBYTE pCur = (LPBYTE)pContent;
            while (!FAILED(hr) && nBytesToWrite > 0) 
            {
                hr = pStream->Write(pCur, nBytesToWrite, &nBytesWritten);
                pCur += nBytesWritten;
                nBytesToWrite -= nBytesWritten;
            }
            if (FAILED(hr = pStream->Commit(0)))
            {
                errMsg = FormatExceptionInfo(hr, L"Error: Stream Write Failed.", __FILE__, __LINE__);
                dloge("MAPIRfc2445 -- exception");
                dloge(errMsg);
            }
            ///////////

            // delete all this in MAPIAccessWrap
            AttachmentInfo* pAttachmentInfo = new AttachmentInfo();
            pAttachmentInfo->pszTempFile = lpszFQFileName;
            pAttachmentInfo->pszRealName = lpszRealName;
            pAttachmentInfo->pszContentDisposition = pszCD;
            pAttachmentInfo->pszContentType = pszAttachContentType;
            m_vAttachments.push_back(pAttachmentInfo);
        }
    }
    delete[] pszContentType;
    return S_OK;
}

void MAPIRfc2445::GenerateContentDisposition(LPSTR *ppszCD, LPSTR pszFilename)
{
    mimepp::String theCD;
    theCD.append("Content-Disposition: form-data; name=\"");
    theCD.append(pszFilename);
    theCD.append("\"; filename=\"");
    theCD.append(pszFilename);
    theCD.append("\"");

    const char *pFinal = theCD.c_str();
    Zimbra::Util::CopyString(*ppszCD, (LPSTR)pFinal);
}

void MAPIRfc2445::GetContentType(mimepp::Headers& headers, LPSTR *ppStr)
{
    mimepp::MediaType &theMediaType = headers.contentType();
    const mimepp::String &contentType = theMediaType.type();
    const mimepp::String &contentSubType = theMediaType.subtype();

    mimepp::String finalContentType;
    finalContentType.append(contentType);
    finalContentType.append("/");
    finalContentType.append(contentSubType);

    const char *pType = finalContentType.c_str();
    Zimbra::Util::CopyString(*ppStr, (LPSTR)pType);
}
