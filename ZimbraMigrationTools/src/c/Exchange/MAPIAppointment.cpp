#include "common.h"
#include "Exchange.h"
#include "MAPIMessage.h"
#include "MAPIAppointment.h"

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MAPIAppointmentException
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIAppointmentException::MAPIAppointmentException(HRESULT hrErrCode, LPCWSTR
    lpszDescription): GenericException(hrErrCode, lpszDescription)
{
    //
}

MAPIAppointmentException::MAPIAppointmentException(HRESULT hrErrCode, LPCWSTR lpszDescription, int
    nLine, LPCSTR strFile): GenericException(hrErrCode, lpszDescription, nLine, strFile)
{
    //
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MAPIAppointment
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//bool MAPIAppointment::m_bNamedPropsInitialized = false;

MAPIAppointment::MAPIAppointment(Zimbra::MAPI::MAPISession &session,
    Zimbra::MAPI::MAPIMessage &mMessage)
{
    m_session = &session;
    m_mapiMessage = &mMessage;
    m_pMessage = m_mapiMessage->InternalMessageObject();
    m_pPropVals = NULL;

    //if (MAPIAppointment::m_bNamedPropsInitialized == false)
    //{
	pr_appt_start = 0;
	pr_appt_end = 0;
	pr_location = 0;
	pr_busystatus = 0;
	pr_allday = 0;
	pr_reminderminutes = 0;
	pr_responsestatus = 0;
	InitNamedPropsForAppt();
    //}

    m_pSubject = L"";
    m_pStartDate = L"";
    m_pEndDate = L"";
    m_pInstanceUID = L"";
    m_pLocation = L"";
    m_pBusyStatus = L"";
    m_pAllday = L"";
    m_pTransparency = L"";
    m_pReminderMinutes = L"";
    m_pResponseStatus = L"";
    m_pOrganizerName = L"";
    m_pOrganizerAddr = L"";
    m_pPrivate = L"";

    GetApptValues();
}

MAPIAppointment::~MAPIAppointment()
{
    if (m_pPropVals)
        MAPIFreeBuffer(m_pPropVals);
    m_pPropVals = NULL;
}

HRESULT MAPIAppointment::InitNamedPropsForAppt()
{
    // init named props
    nameIds[0] = 0x820D;
    nameIds[1] = 0x820E;
    nameIds[2] = 0x8208;
    nameIds[3] = 0x8205;
    nameIds[4] = 0x8215;
    nameIds[5] = 0x8218;

    nameIdsC[0] = 0x8501;
    nameIdsC[1] = 0x8506;

    HRESULT hr = S_OK;
    Zimbra::Util::ScopedBuffer<SPropValue> pPropValMsgClass;

    if (FAILED(hr = HrGetOneProp(m_pMessage, PR_MESSAGE_CLASS, pPropValMsgClass.getptr())))
        throw MAPIAppointmentException(hr, L"InitNamedPropsForAppt(): HrGetOneProp Failed.", __LINE__, __FILE__);

    // initialize the MAPINAMEID structure GetIDsFromNames requires
    LPMAPINAMEID ppNames[N_NUMAPPTPROPS] = { 0 };
    for (int i = 0; i < N_NUMAPPTPROPS; i++)
    {
        MAPIAllocateBuffer(sizeof (MAPINAMEID), (LPVOID *)&(ppNames[i]));
        ppNames[i]->ulKind = MNID_ID;
        ppNames[i]->lpguid = (LPGUID)(&PS_OUTLOOK_APPT);
        ppNames[i]->Kind.lID = nameIds[i];
    }

    LPMAPINAMEID ppNamesC[N_NUMCOMMONPROPS] = { 0 };
    for (int i = 0; i < N_NUMCOMMONPROPS; i++)
    {
        MAPIAllocateBuffer(sizeof (MAPINAMEID), (LPVOID *)&(ppNamesC[i]));
        ppNamesC[i]->ulKind = MNID_ID;
        ppNamesC[i]->lpguid = (LPGUID)(&PS_OUTLOOK_COMMON);
        ppNamesC[i]->Kind.lID = nameIdsC[i];
    }

    // get the real prop tag ID's
    LPSPropTagArray pAppointmentTags = NULL;
    LPSPropTagArray pAppointmentTagsC = NULL;

    if (FAILED(hr = m_pMessage->GetIDsFromNames(N_NUMAPPTPROPS, ppNames, MAPI_CREATE,
            &pAppointmentTags)))
        throw MAPIAppointmentException(hr, L"Init(): GetIDsFromNames on pAppointmentTags Failed.", __LINE__, __FILE__);

    if (FAILED(hr = m_pMessage->GetIDsFromNames(N_NUMCOMMONPROPS, ppNamesC, MAPI_CREATE,
            &pAppointmentTagsC)))
        throw MAPIAppointmentException(hr, L"Init(): GetIDsFromNames on pAppointmentTagsC Failed.", __LINE__, __FILE__);

    // give the prop tag ID's a type
    pr_appt_start = SetPropType(pAppointmentTags->aulPropTag[N_APPTSTART], PT_SYSTIME);
    pr_appt_end = SetPropType(pAppointmentTags->aulPropTag[N_APPTEND], PT_SYSTIME);
    pr_location = SetPropType(pAppointmentTags->aulPropTag[N_LOCATION], PT_TSTRING);
    pr_busystatus = SetPropType(pAppointmentTags->aulPropTag[N_BUSYSTATUS], PT_LONG);
    pr_allday = SetPropType(pAppointmentTags->aulPropTag[N_ALLDAY], PT_BOOLEAN);
    pr_responsestatus = SetPropType(pAppointmentTags->aulPropTag[N_RESPONSESTATUS], PT_LONG);
    pr_reminderminutes = SetPropType(pAppointmentTagsC->aulPropTag[N_REMINDERMINUTES], PT_LONG);
    pr_private = SetPropType(pAppointmentTagsC->aulPropTag[N_PRIVATE], PT_BOOLEAN);

    // free the memory we allocated on the head
    for (int i = 0; i < N_NUMAPPTPROPS; i++)
    {
        MAPIFreeBuffer(ppNames[i]);
    }
    for (int i = 0; i < N_NUMCOMMONPROPS; i++)
    {
        MAPIFreeBuffer(ppNamesC[i]);
    }
    MAPIFreeBuffer(pAppointmentTags);
    MAPIFreeBuffer(pAppointmentTagsC);

    //MAPIAppointment::m_bNamedPropsInitialized = true;

    return S_OK;
}

HRESULT MAPIAppointment::GetApptValues()
{
    SizedSPropTagArray(C_NUMALLAPPTPROPS, appointmentProps) = {
	C_NUMALLAPPTPROPS, {
	    PR_SUBJECT, PR_BODY, PR_HTML, PR_CLEAN_GLOBAL_OBJID,
	    pr_appt_start, pr_appt_end, pr_location, pr_busystatus,
	    pr_allday, pr_responsestatus, pr_reminderminutes, pr_private
	}
    };

    HRESULT hr = S_OK;
    ULONG cVals = 0;

    if (FAILED(hr = m_pMessage->GetProps((LPSPropTagArray) & appointmentProps, fMapiUnicode, &cVals,
            &m_pPropVals)))
        throw MAPIAppointmentException(hr, L"GetApptValues(): GetProps Failed.", __LINE__, __FILE__);

    if (m_pPropVals[C_SUBJECT].ulPropTag == appointmentProps.aulPropTag[C_SUBJECT])
    {
	SetSubject(m_pPropVals[C_SUBJECT].Value.lpszW);
    }
    if (m_pPropVals[C_UID].ulPropTag == appointmentProps.aulPropTag[C_UID])
    {
	//SetInstanceUID(m_pPropVals[C_UID].Value.bin);
    }
    if (m_pPropVals[C_START].ulPropTag == appointmentProps.aulPropTag[C_START])
    {
	SetStartDate(m_pPropVals[C_START].Value.ft);
    }
    if (m_pPropVals[C_END].ulPropTag == appointmentProps.aulPropTag[C_END])
    {
	SetEndDate(m_pPropVals[C_END].Value.ft);
    }
    if (m_pPropVals[C_LOCATION].ulPropTag == appointmentProps.aulPropTag[C_LOCATION])
    {
	SetLocation(m_pPropVals[C_LOCATION].Value.lpszW);
    }
    if (m_pPropVals[C_BUSYSTATUS].ulPropTag == appointmentProps.aulPropTag[C_BUSYSTATUS])
    {
	SetBusyStatus(m_pPropVals[C_BUSYSTATUS].Value.l);
    }
    if (m_pPropVals[C_ALLDAY].ulPropTag == appointmentProps.aulPropTag[C_ALLDAY])
    {
	SetAllday(m_pPropVals[C_ALLDAY].Value.b);
    }
    if (m_pPropVals[C_RESPONSESTATUS].ulPropTag == appointmentProps.aulPropTag[C_RESPONSESTATUS])
    {
	SetResponseStatus(m_pPropVals[C_RESPONSESTATUS].Value.l);
    }
    if (m_pPropVals[C_REMINDERMINUTES].ulPropTag == appointmentProps.aulPropTag[C_REMINDERMINUTES])
    {
	SetReminderMinutes(m_pPropVals[C_REMINDERMINUTES].Value.l);
    }
    if (m_pPropVals[C_PRIVATE].ulPropTag == appointmentProps.aulPropTag[C_PRIVATE])
    {
	SetPrivate(m_pPropVals[C_PRIVATE].Value.b);
    }
    SetTransparency(L"O");
    SetPlainTextFileAndContent();
    SetHtmlFileAndContent();
    return hr;
}

bool MAPIAppointment::TextBody(LPTSTR *ppBody, unsigned int &nTextChars)
{
    if (m_pPropVals[C_BODY].ulPropTag == PR_BODY)
    {
        LPTSTR pBody = m_pPropVals[C_BODY].Value.LPSZ;
        int nLen = (int)_tcslen(pBody);

        MAPIAllocateBuffer((nLen + 1) * sizeof (TCHAR), (LPVOID FAR *)ppBody);
        _tcscpy(*ppBody, pBody);
        nTextChars = nLen;
        return true;
    }
    else if ((PROP_TYPE(m_pPropVals[C_BODY].ulPropTag) == PT_ERROR) &&
        (m_pPropVals[C_BODY].Value.l == E_OUTOFMEMORY))
    {
        HRESULT hr = S_OK;

        // must use the stream property
        IStream *pIStream = NULL;

        hr = m_pMessage->OpenProperty(PR_BODY, &IID_IStream, STGM_READ, 0, (LPUNKNOWN
            FAR *)&pIStream);
        if (FAILED(hr))
            return false;

        // discover the size of the incoming body
        STATSTG statstg;

        hr = pIStream->Stat(&statstg, STATFLAG_NONAME);
        if (FAILED(hr))
        {
            pIStream->Release();
            pIStream = NULL;
            return false;
        }

        unsigned bodySize = statstg.cbSize.LowPart;

        // allocate buffer for incoming body data
        hr = MAPIAllocateBuffer(bodySize + 10, (LPVOID FAR *)ppBody);
        ZeroMemory(*ppBody, bodySize + 10);
        if (FAILED(hr))
        {
            pIStream->Release();
            pIStream = NULL;
            return false;
        }

        // download the text
        ULONG cb;

        hr = pIStream->Read(*ppBody, statstg.cbSize.LowPart, &cb);
        if (FAILED(hr))
        {
            pIStream->Release();
            pIStream = NULL;
            return false;
        }
        if (cb != statstg.cbSize.LowPart)
        {
            pIStream->Release();
            pIStream = NULL;
            return false;
        }
        // close the stream
        pIStream->Release();
        pIStream = NULL;
        nTextChars = (unsigned int)_tcslen(*ppBody);
        return true;
    }
    // some other error occurred?
    // i.e., some messages do not have a body
    *ppBody = NULL;
    return false;
}

bool MAPIAppointment::HtmlBody(LPVOID *ppBody, unsigned int &nHtmlBodyLen)
{
    if (m_pPropVals[C_HTMLBODY].ulPropTag == PR_HTML)
    {
        LPVOID pBody = m_pPropVals[C_HTMLBODY].Value.bin.lpb;

        if (pBody)
        {
            size_t nLen = m_pPropVals[C_HTMLBODY].Value.bin.cb;

            MAPIAllocateBuffer((ULONG)(nLen + 10), (LPVOID FAR *)ppBody);
            ZeroMemory(*ppBody, (nLen + 10));
            memcpy(*ppBody, pBody, nLen);
            nHtmlBodyLen = (UINT)nLen;
            return true;
        }
    }

    // Try to extract HTML BODY using the stream property.
    HRESULT hr;
    IStream *pIStream;

    hr = m_pMessage->OpenProperty(PR_HTML, &IID_IStream, STGM_READ, 0, (LPUNKNOWN
        FAR *)&pIStream);
    if (SUCCEEDED(hr))
    {
        // discover the size of the incoming body
        STATSTG statstg;

        hr = pIStream->Stat(&statstg, STATFLAG_NONAME);
        if (FAILED(hr))
            throw MAPIMessageException(E_FAIL, L"HtmlBody(): pIStream->Stat Failed.", __LINE__,
                __FILE__);

        unsigned bodySize = statstg.cbSize.LowPart;

        nHtmlBodyLen = bodySize;

        // allocate buffer for incoming body data
        hr = MAPIAllocateBuffer(bodySize + 10, ppBody);
        ZeroMemory(*ppBody, bodySize + 10);
        if (FAILED(hr))
            throw MAPIMessageException(E_FAIL, L"HtmlBody(): ZeroMemory Failed.", __LINE__,
                __FILE__);

        // download the text
        ULONG cb;

        hr = pIStream->Read(*ppBody, statstg.cbSize.LowPart, &cb);
        if (FAILED(hr))
            throw MAPIMessageException(E_FAIL, L"HtmlBody(): pIStream->Read Failed.", __LINE__,
                __FILE__);
        if (cb != statstg.cbSize.LowPart)
        {
            throw MAPIMessageException(E_FAIL, L"HtmlBody(): statstg.cbSize.LowPart Failed.",
                __LINE__, __FILE__);
        }
        // close the stream
        pIStream->Release();
        return true;
    }

    // some other error occurred?
    // i.e., some messages do not have a body
    *ppBody = NULL;
    nHtmlBodyLen = 0;
    return false;
}

LPWSTR MAPIAppointment::WriteContentsToFile(LPTSTR pBody, bool isAscii)
{
    LPSTR pTemp = NULL;
    int nBytesToBeWritten;

    pTemp = (isAscii) ? (LPSTR)pBody : Zimbra::Util::UnicodeToAnsii(pBody);
    nBytesToBeWritten = strlen(pTemp);
   
    wstring wstrTempAppDirPath;
    if (!Zimbra::MAPI::Util::GetAppTemporaryDirectory(wstrTempAppDirPath))
    {
	return L"";
    }

    char *lpszDirName = NULL;
    char *lpszUniqueName = NULL;
    Zimbra::Util::ScopedInterface<IStream> pStream;
    ULONG nBytesWritten = 0;
    ULONG nTotalBytesWritten = 0;
    HRESULT hr = S_OK;

    WtoA((LPWSTR)wstrTempAppDirPath.c_str(), lpszDirName);

    string strFQFileName = lpszDirName;

    WtoA((LPWSTR)Zimbra::MAPI::Util::GetUniqueName().c_str(), lpszUniqueName);
    strFQFileName += "\\";
    strFQFileName += lpszUniqueName;
    SafeDelete(lpszDirName);
    SafeDelete(lpszUniqueName);
    // Open stream on file
    if (FAILED(hr = OpenStreamOnFile(MAPIAllocateBuffer, MAPIFreeBuffer, STGM_CREATE |
            STGM_READWRITE, (LPTSTR)strFQFileName.c_str(), NULL, pStream.getptr())))
    {
	return L"";
    }
    // write to file
    while (!FAILED(hr) && nBytesToBeWritten > 0)
    {

	hr = pStream->Write(pTemp, nBytesToBeWritten, &nBytesWritten);
	pTemp += nBytesWritten;
        nBytesToBeWritten -= nBytesWritten;
        nTotalBytesWritten += nBytesWritten;
        nBytesWritten = 0;
    }
    if (FAILED(hr = pStream->Commit(0)))
        return L"";

    LPWSTR lpwstrFQFileName = NULL;

    AtoW((LPSTR)strFQFileName.c_str(), lpwstrFQFileName);
    return lpwstrFQFileName;
}

void MAPIAppointment::SetPlainTextFileAndContent()
{
    LPTSTR pBody = NULL;
    UINT nText = 0;
    m_pPlainTextFile = L"";

    bool bRet = TextBody(&pBody, nText);
    if (bRet)
    {
	LPWSTR lpwszTempFile = WriteContentsToFile(pBody, false);
	m_pPlainTextFile = lpwszTempFile;
	SafeDelete(lpwszTempFile);
    }
}

void MAPIAppointment::SetHtmlFileAndContent()
{
    LPVOID pBody = NULL;
    UINT nText = 0;
    m_pHtmlFile = L"";

    bool bRet = HtmlBody(&pBody, nText);
    if (bRet)
    {
	LPWSTR lpwszTempFile = WriteContentsToFile((LPTSTR)pBody, true);
	m_pHtmlFile = lpwszTempFile;
	SafeDelete(lpwszTempFile);
    }
}

HRESULT MAPIAppointment::GetAppointmentAttachment(wstring &wstrAttachmentPath)
{
    HRESULT hr = S_OK;
    /*
    Zimbra::Util::ScopedInterface<IStream> pSrcStream;
    {
        Zimbra::Util::ScopedRowSet pAttachRows;
        Zimbra::Util::ScopedInterface<IMAPITable> pAttachTable;

        SizedSPropTagArray(3, attachProps) = {
            3, { PR_ATTACH_NUM, PR_ATTACH_SIZE, PR_ATTACH_LONG_FILENAME }
        };

        hr = m_pMessage->GetAttachmentTable(MAPI_UNICODE, pAttachTable.getptr());
        if (SUCCEEDED(hr))
        {
            if (FAILED(hr = pAttachTable->SetColumns((LPSPropTagArray) & attachProps, 0)))
                return hr;
            ULONG ulRowCount = 0;
            if (FAILED(hr = pAttachTable->GetRowCount(0, &ulRowCount)))
                return hr;
            if (FAILED(hr = pAttachTable->QueryRows(ulRowCount, 0, pAttachRows.getptr())))
                return hr;
            if (SUCCEEDED(hr))
            {
                hr = MAPI_E_NOT_FOUND;
                for (unsigned int i = 0; i < pAttachRows->cRows; i++)
                {
                    // if property couldn't be found or returns error, skip it
                    if ((pAttachRows->aRow[i].lpProps[2].ulPropTag == PT_ERROR) ||
                        (pAttachRows->aRow[i].lpProps[2].Value.err == MAPI_E_NOT_FOUND))
                        continue;
                    // Discard the attachmetnt if its not contact picture
                    if (_tcscmp(pAttachRows->aRow[i].lpProps[2].Value.LPSZ, _T(
                        "ContactPicture.jpg")))
                        continue;
                    Zimbra::Util::ScopedInterface<IAttach> pAttach;

                    if (FAILED(hr = m_pMessage->OpenAttach(
                            pAttachRows->aRow[i].lpProps[0].Value.l, NULL, 0,
                            pAttach.getptr())))
                        continue;
                    if (FAILED(hr = pAttach->OpenProperty(PR_ATTACH_DATA_BIN, &IID_IStream,
                            STGM_READ, 0, (LPUNKNOWN FAR *)pSrcStream.getptr())))
                        return hr;
                    break;
                }
            }
        }
    }

    if (hr != S_OK)
        return hr;

    // copy image to file
    wstring wstrTempAppDirPath;
    char *lpszDirName = NULL;
    char *lpszUniqueName = NULL;
    Zimbra::Util::ScopedInterface<IStream> pDestStream;

    if (!Zimbra::MAPI::Util::GetAppTemporaryDirectory(wstrTempAppDirPath))
        return MAPI_E_ACCESS_DENIED;
    WtoA((LPWSTR)wstrTempAppDirPath.c_str(), lpszDirName);

    string strFQFileName = lpszDirName;

    WtoA((LPWSTR)Zimbra::MAPI::Util::GetUniqueName().c_str(), lpszUniqueName);
    strFQFileName += "\\ZmContact_";
    strFQFileName += lpszUniqueName;
    strFQFileName += ".jpg";
    SafeDelete(lpszDirName);
    SafeDelete(lpszUniqueName);
    // Open stream on file
    if (FAILED(hr = OpenStreamOnFile(MAPIAllocateBuffer, MAPIFreeBuffer, STGM_CREATE |
            STGM_READWRITE, (LPTSTR)strFQFileName.c_str(), NULL, pDestStream.getptr())))
        return hr;
    ULARGE_INTEGER liAll = { 0 };
    liAll.QuadPart = (ULONGLONG)-1;
    if (FAILED(hr = pSrcStream->CopyTo(pDestStream.get(), liAll, NULL, NULL)))
        return hr;
    if (FAILED(hr = pDestStream->Commit(0)))
    {
        return hr;
        ;
    }

    // mime file path
    LPWSTR lpwstrFQFileName = NULL;

    AtoW((LPSTR)strFQFileName.c_str(), lpwstrFQFileName);
    wstrImagePath = lpwstrFQFileName;
    SafeDelete(lpwstrFQFileName);
    */

    wstrAttachmentPath = L"";	// GET RID OF THIS
    return hr;
}
