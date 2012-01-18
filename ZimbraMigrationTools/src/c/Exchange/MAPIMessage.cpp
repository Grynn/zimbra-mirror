#include "common.h"
#include "Exchange.h"
#include "MAPIMessage.h"
#include <Mshtml.h>

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MAPIMessageException
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIMessageException::MAPIMessageException(HRESULT hrErrCode, LPCWSTR
    lpszDescription): GenericException(hrErrCode, lpszDescription)
{
    //
}

MAPIMessageException::MAPIMessageException(HRESULT hrErrCode, LPCWSTR lpszDescription, int
    nLine, LPCSTR strFile): GenericException(hrErrCode, lpszDescription, nLine, strFile)
{
    //
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MAPIMessage
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIMessage::MessagePropTags MAPIMessage::m_messagePropTags = {
    NMSGPROPS, {
        PR_MESSAGE_CLASS, PR_MESSAGE_FLAGS, PR_CLIENT_SUBMIT_TIME, PR_SENDER_ADDRTYPE,
        PR_SENDER_EMAIL_ADDRESS, PR_SENDER_NAME, PR_SENDER_ENTRYID, PR_SUBJECT, PR_BODY,
        PR_BODY_HTML, PR_INTERNET_CPID, PR_MESSAGE_CODEPAGE, PR_LAST_VERB_EXECUTED,
        PR_FLAG_STATUS, PR_ENTRYID, PR_SENT_REPRESENTING_ADDRTYPE,
        PR_SENT_REPRESENTING_ENTRYID, PR_SENT_REPRESENTING_EMAIL_ADDRESS,
        PR_SENT_REPRESENTING_NAME, PR_REPLY_RECIPIENT_NAMES, PR_REPLY_RECIPIENT_ENTRIES,
        PR_TRANSPORT_MESSAGE_HEADERS_A, PR_IMPORTANCE, PR_INTERNET_MESSAGE_ID_A,
        PR_MESSAGE_DELIVERY_TIME, PR_URL_NAME, PR_MESSAGE_SIZE, PR_STORE_SUPPORT_MASK,
        PR_RTF_IN_SYNC
    }
};
MAPIMessage::RecipientPropTags MAPIMessage::m_recipientPropTags = {
    RNPROPS, {
        PR_DISPLAY_NAME, PR_ENTRYID, PR_ADDRTYPE, PR_EMAIL_ADDRESS, PR_RECIPIENT_TYPE
    }
};
MAPIMessage::ReplyToPropTags MAPIMessage::m_replyToPropTags = {
    NREPLYTOPROPS, {
        PR_DISPLAY_NAME, PR_ENTRYID, PR_ADDRTYPE, PR_EMAIL_ADDRESS
    }
};
MAPIMessage::MAPIMessage(): m_pMessage(NULL), m_pMessagePropVals(NULL), m_pRecipientRows(NULL)
{
    m_EntryID.cb = 0;
    m_EntryID.lpb = NULL;

    m_pDateTimeStr[0] = '\0';
    m_pDeliveryDateTimeStr[0] = '\0';
    m_pDeliveryUnixDateTimeStr[0] = '\0';

    // initialize the RTF tags.
    RTFElement.push_back("{");
    RTFElement.push_back("}");
    RTFElement.push_back("\\*\\htmltag");
    RTFElement.push_back("\\*\\mhtmltag");
    RTFElement.push_back("\\par");
    RTFElement.push_back("\\tab");
    RTFElement.push_back("\\li");
    RTFElement.push_back("\\fi-");
    RTFElement.push_back("\\'");
    RTFElement.push_back("\\pntext");
    RTFElement.push_back("\\htmlrtf");
    RTFElement.push_back("\\{");
    RTFElement.push_back("\\}");
    RTFElement.push_back("");
    RTFElement.push_back("\\htmlrtf0");
}

MAPIMessage::~MAPIMessage()
{
    InternalFree();
}

void MAPIMessage::Initialize(LPMESSAGE pMessage, MAPISession &session)
{
    m_session = &session;

    HRESULT hr = S_OK;
    ULONG cVals = 0;
    LPMAPITABLE pRecipTable = NULL;

    __try
    {
        InternalFree();
        m_pMessage = pMessage;
        if (FAILED(hr = m_pMessage->GetProps((LPSPropTagArray) & m_messagePropTags,
                fMapiUnicode, &cVals, &m_pMessagePropVals)))
            throw MAPIMessageException(E_FAIL, L"Initialize(): GetProps Failed.", __LINE__,
                __FILE__);
        if (FAILED(hr = m_pMessage->GetRecipientTable(fMapiUnicode, &pRecipTable)))
        {
            throw MAPIMessageException(E_FAIL, L"Initialize(): GetRecipientTable Failed.",
                __LINE__, __FILE__);
        }

        ULONG ulRecips = 0;

        if (FAILED(hr = pRecipTable->GetRowCount(0, &ulRecips)))
            throw MAPIMessageException(E_FAIL, L"Initialize(): GetRowCount Failed.", __LINE__,
                __FILE__);
        if (ulRecips > 0)
        {
            if (FAILED(hr = pRecipTable->SetColumns((LPSPropTagArray) & m_recipientPropTags,
                    0)))
            {
                throw MAPIMessageException(E_FAIL, L"Initialize(): SetColumns Failed.",
                    __LINE__, __FILE__);
            }
            if (FAILED(hr = pRecipTable->QueryRows(ulRecips, 0, &m_pRecipientRows)))
                throw MAPIMessageException(E_FAIL, L"Initialize(): QueryRows Failed.", __LINE__,
                    __FILE__);
        }
    }
    __finally
    {
        if (pRecipTable != NULL)
            UlRelease(pRecipTable);
    }
    m_EntryID = m_pMessagePropVals[ENTRYID].Value.bin;
}

void MAPIMessage::InternalFree()
{
    if (m_pRecipientRows != NULL)
    {
        FreeProws(m_pRecipientRows);
        m_pRecipientRows = NULL;
    }
    if (m_pMessagePropVals != NULL)
    {
        MAPIFreeBuffer(m_pMessagePropVals);
        m_pMessagePropVals = NULL;
    }
    if (m_pMessage != NULL)
    {
        UlRelease(m_pMessage);
        m_pMessage = NULL;
    }
}

unsigned int MAPIMessage::CodePageId()
{
    if (PROP_TYPE(m_pMessagePropVals[INTERNET_CPID].ulPropTag) != PT_ERROR)
    {
        return m_pMessagePropVals[INTERNET_CPID].Value.ul;
    }
    else if (PROP_TYPE(m_pMessagePropVals[MESSAGE_CODEPAGE].ulPropTag) != PT_ERROR)
    {
        return m_pMessagePropVals[MESSAGE_CODEPAGE].Value.ul;
    }
    else
    {
        // return the current ansii code page of the system
        return GetACP();
    }
}

bool MAPIMessage::Subject(LPTSTR *ppSubject)
{
    if (PROP_TYPE(m_pMessagePropVals[SUBJECT].ulPropTag) != PT_ERROR)
    {
        CopyString(*ppSubject, m_pMessagePropVals[SUBJECT].Value.LPSZ);
        return true;
    }
    return false;
}

ZM_ITEM_TYPE MAPIMessage::ItemType()
{
    if (PROP_TYPE(m_pMessagePropVals[MESSAGE_CLASS].ulPropTag) != PT_ERROR)
    {
        if ((_tcsnicmp(m_pMessagePropVals[MESSAGE_CLASS].Value.LPSZ, _TEXT("IPM.NOTE"), 8) ==
            0) || (_tcsnicmp(m_pMessagePropVals[MESSAGE_CLASS].Value.LPSZ, _TEXT("IPM.POST"),
            8) == 0))
            return ZT_MAIL;
        else if ((_tcsnicmp(m_pMessagePropVals[MESSAGE_CLASS].Value.LPSZ, _TEXT("IPM.CONTACT"),
            11) == 0) || (_tcsnicmp(m_pMessagePropVals[MESSAGE_CLASS].Value.LPSZ, _TEXT(
            "IPM.DISTLIST"), 12) == 0))
            return ZT_CONTACTS;
        else if (_tcsnicmp(m_pMessagePropVals[MESSAGE_CLASS].Value.LPSZ, _TEXT(
            "IPM.APPOINTMENT"), 15) == 0)
            return ZT_APPOINTMENTS;
        else if (_tcsicmp(m_pMessagePropVals[MESSAGE_CLASS].Value.LPSZ, _TEXT("IPM.TASK")) == 0)
            return ZT_TASKS;
        else if (_tcsstr(m_pMessagePropVals[MESSAGE_CLASS].Value.LPSZ, _TEXT("IPM.Schedule")))
            return ZT_MEETREQ;
    }
    return ZT_NONE;
}

bool MAPIMessage::IsFlagged()
{
    if (PROP_TYPE(m_pMessagePropVals[FLAG_STATUS].ulPropTag) != PT_ERROR)
        return m_pMessagePropVals[FLAG_STATUS].Value.ul == 2;
    return false;
}

bool MAPIMessage::GetURLName(LPTSTR *pstrUrlName)
{
    if (PROP_TYPE(m_pMessagePropVals[URL_NAME].ulPropTag) != PT_ERROR)
    {
        CopyString(*pstrUrlName, m_pMessagePropVals[URL_NAME].Value.LPSZ);
        return true;
    }
    return false;
}

bool MAPIMessage::IsDraft()
{
    if (PROP_TYPE(m_pMessagePropVals[MESSAGE_FLAGS].ulPropTag) != PT_ERROR)
    {
        if (m_pMessagePropVals[MESSAGE_FLAGS].Value.ul & MSGFLAG_UNSENT)
            return true;
    }
    return false;
}

BOOL MAPIMessage::IsFromMe()
{
    if (PROP_TYPE(m_pMessagePropVals[MESSAGE_FLAGS].ulPropTag) != PT_ERROR)
        return m_pMessagePropVals[MESSAGE_FLAGS].Value.ul & MSGFLAG_FROMME;
    return FALSE;
}

BOOL MAPIMessage::IsUnread()
{
    if (PROP_TYPE(m_pMessagePropVals[MESSAGE_FLAGS].ulPropTag) != PT_ERROR)
        return !(m_pMessagePropVals[MESSAGE_FLAGS].Value.ul & MSGFLAG_READ);
    return FALSE;
}

BOOL MAPIMessage::Forwarded()
{
    if (PROP_TYPE(m_pMessagePropVals[LAST_VERB_EXECUTED].ulPropTag) != PT_ERROR)
        return m_pMessagePropVals[LAST_VERB_EXECUTED].Value.ul == EXCHIVERB_FORWARD;
    return FALSE;
}

BOOL MAPIMessage::RepliedTo()
{
    if (PROP_TYPE(m_pMessagePropVals[LAST_VERB_EXECUTED].ulPropTag) != PT_ERROR)
        return (m_pMessagePropVals[LAST_VERB_EXECUTED].Value.ul == EXCHIVERB_REPLYTOALL) ||
               (m_pMessagePropVals[LAST_VERB_EXECUTED].Value.ul == EXCHIVERB_REPLYTOSENDER);
    return FALSE;
}

bool MAPIMessage::HasAttach()
{
    if ((PROP_TYPE(m_pMessagePropVals[MESSAGE_FLAGS].ulPropTag) != PT_ERROR) &&
        ((m_pMessagePropVals[MESSAGE_FLAGS].Value.l & MSGFLAG_HASATTACH) != 0))
        return true;
    return false;
}

BOOL MAPIMessage::IsUnsent()
{
    if ((PROP_TYPE(m_pMessagePropVals[MESSAGE_FLAGS].ulPropTag) != PT_ERROR) &&
        ((m_pMessagePropVals[MESSAGE_FLAGS].Value.l & MSGFLAG_UNSENT) != 0))
        return true;
    return false;
}

bool MAPIMessage::HasHtmlPart()
{
    if ((m_pMessagePropVals[HTML_BODY].ulPropTag == PR_BODY_HTML) || ((PROP_TYPE(
        m_pMessagePropVals[HTML_BODY].ulPropTag) == PT_ERROR) &&
        (m_pMessagePropVals[HTML_BODY].Value.l == E_OUTOFMEMORY)))
        return true;
    return false;
}

bool MAPIMessage::HasTextPart()
{
    if ((m_pMessagePropVals[TEXT_BODY].ulPropTag == PR_BODY) || ((PROP_TYPE(
        m_pMessagePropVals[TEXT_BODY].ulPropTag) == PT_ERROR) &&
        (m_pMessagePropVals[TEXT_BODY].Value.l == E_OUTOFMEMORY)))
        return true;
    return false;
}

SBinary &MAPIMessage::UniqueId()
{
    return m_pMessagePropVals[ENTRYID].Value.bin;
}

__int64 MAPIMessage::Date()
{
    // calculate the unix date
    if (PROP_TYPE(m_pMessagePropVals[MESSAGE_DATE].ulPropTag) != PT_ERROR)
    {
        __int64 ft = m_pMessagePropVals[MESSAGE_DATE].Value.ft.dwHighDateTime;

        ft <<= 32;
        ft |= m_pMessagePropVals[MESSAGE_DATE].Value.ft.dwLowDateTime;
        return ft;
    }
    return -1;
}

__int64 MAPIMessage::DeliveryDate()
{
    // calculate the unix date
    if (PROP_TYPE(m_pMessagePropVals[DELIVERY_DATE].ulPropTag) != PT_ERROR)
    {
        __int64 ft = m_pMessagePropVals[DELIVERY_DATE].Value.ft.dwHighDateTime;

        ft <<= 32;
        ft |= m_pMessagePropVals[DELIVERY_DATE].Value.ft.dwLowDateTime;
        return ft;
    }
    return -1;
}

LPSTR MAPIMessage::DateString()
{
    if (PROP_TYPE(m_pMessagePropVals[MESSAGE_DATE].ulPropTag) == PT_ERROR)
    {
        strcpy(m_pDateTimeStr, "No Date");
    }
    else if (m_pDateTimeStr[0] == '\0')
    {
        // convert the filetime to a system time.
        SYSTEMTIME st;

        FileTimeToSystemTime(&(m_pMessagePropVals[MESSAGE_DATE].Value.ft), &st);

        // build the GMT date/time string
        int nWritten = GetDateFormatA(MAKELCID(MAKELANGID(LANG_ENGLISH, SUBLANG_ENGLISH_US),
            SORT_DEFAULT), LOCALE_USE_CP_ACP, &st, "ddd, d MMM yyyy", m_pDateTimeStr, 32);

        GetTimeFormatA(MAKELCID(MAKELANGID(LANG_ENGLISH, SUBLANG_ENGLISH_US), SORT_DEFAULT),
            LOCALE_USE_CP_ACP, &st, " HH:mm:ss -0000", (m_pDateTimeStr + nWritten - 1), 32 -
            nWritten + 1);
    }
    return m_pDateTimeStr;
}

DWORD MAPIMessage::Size()
{
    if (PROP_TYPE(m_pMessagePropVals[MESSAGE_SIZE].ulPropTag) == PT_ERROR)
        return 0;
    else
        return m_pMessagePropVals[MESSAGE_SIZE].Value.l;
}

LPSTR MAPIMessage::DeliveryDateString()
{
    if (PROP_TYPE(m_pMessagePropVals[DELIVERY_DATE].ulPropTag) == PT_ERROR)
    {
        strcpy(m_pDeliveryDateTimeStr, "No Date");
    }
    else if (m_pDeliveryDateTimeStr[0] == '\0')
    {
        // convert the filetime to a system time.
        SYSTEMTIME st;

        FileTimeToSystemTime(&(m_pMessagePropVals[DELIVERY_DATE].Value.ft), &st);

        // build the GMT date/time string
        int nWritten = GetDateFormatA(MAKELCID(MAKELANGID(LANG_ENGLISH, SUBLANG_ENGLISH_US),
            SORT_DEFAULT), LOCALE_USE_CP_ACP, &st, "ddd, d MMM yyyy", m_pDeliveryDateTimeStr,
            32);

        GetTimeFormatA(MAKELCID(MAKELANGID(LANG_ENGLISH, SUBLANG_ENGLISH_US), SORT_DEFAULT),
            LOCALE_USE_CP_ACP, &st, " HH:mm:ss -0000", (m_pDeliveryDateTimeStr + nWritten - 1),
            32 - nWritten + 1);
    }
    return m_pDeliveryDateTimeStr;
}

LPSTR MAPIMessage::DeliveryUnixString()
{
    if (PROP_TYPE(m_pMessagePropVals[DELIVERY_DATE].ulPropTag) == PT_ERROR)
    {
        strcpy(m_pDeliveryUnixDateTimeStr, "No Date");
    }
    else if (m_pDeliveryUnixDateTimeStr[0] == '\0')
    {
        __int64 unixTime;
        Zimbra::Util::FileTimeToUnixTime64(m_pMessagePropVals[DELIVERY_DATE].Value.ft,
            unixTime);                          // server wants this time format

        _i64toa(unixTime, m_pDeliveryUnixDateTimeStr, 10);
        strcat(m_pDeliveryUnixDateTimeStr, "000");
    }
    return m_pDeliveryUnixDateTimeStr;
}

bool MAPIMessage::TextBody(LPTSTR *ppBody, unsigned int &nTextChars)
{
    if (m_pMessagePropVals[TEXT_BODY].ulPropTag == PR_BODY)
    {
        LPTSTR pBody = m_pMessagePropVals[TEXT_BODY].Value.LPSZ;
        int nLen = (int)_tcslen(pBody);

        MAPIAllocateBuffer((nLen + 1) * sizeof (TCHAR), (LPVOID FAR *)ppBody);
        _tcscpy(*ppBody, pBody);
        nTextChars = nLen;
        return true;
    }
    else if ((PROP_TYPE(m_pMessagePropVals[TEXT_BODY].ulPropTag) == PT_ERROR) &&
        (m_pMessagePropVals[TEXT_BODY].Value.l == E_OUTOFMEMORY))
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

void ParseHTML(LPWSTR pBuff, LPWSTR *pOutbuff, size_t *oLen)
{
    IHTMLDocument2 *pDoc = NULL;

    CoInitialize(NULL);
    CoCreateInstance(CLSID_HTMLDocument, NULL, CLSCTX_INPROC_SERVER, IID_IHTMLDocument2,
        (LPVOID *)&pDoc);
    if (pDoc)
    {
        IPersistStreamInit *pPersist = NULL;

        pDoc->QueryInterface(IID_IPersistStreamInit, (LPVOID *)&pPersist);
        if (pPersist)
        {
            IMarkupServices *pMS = NULL;

            pPersist->InitNew();
            pPersist->Release();

            pDoc->QueryInterface(IID_IMarkupServices, (LPVOID *)&pMS);
            if (pMS)
            {
                IMarkupContainer *pMC = NULL;
                IMarkupPointer *pMkStart = NULL;
                IMarkupPointer *pMkFinish = NULL;

                pMS->CreateMarkupPointer(&pMkStart);
                pMS->CreateMarkupPointer(&pMkFinish);

                pMS->ParseString(pBuff, 0, &pMC, pMkStart, pMkFinish);
                if (pMC)
                {
                    IHTMLDocument2 *pNewDoc = NULL;

                    pMC->QueryInterface(IID_IHTMLDocument, (LPVOID *)&pNewDoc);
                    if (pNewDoc)
                    {
                        // do anything with pNewDoc, in this case
                        // get the body innerText.

                        IHTMLElement *pBody;

                        pNewDoc->get_body(&pBody);
                        if (pBody)
                        {
                            BSTR strText;

                            pBody->get_innerText(&strText);
                            if (strText != NULL)
                            {
                                size_t blen = wcslen(strText);

                                *pOutbuff = new WCHAR[blen + 1];
                                ZeroMemory(*pOutbuff, blen + 1);
                                swprintf(*pOutbuff, blen + 1, L"%s", strText);
                                *oLen = blen;
                            }
                            pBody->Release();
                            SysFreeString(strText);
                        }
                        pNewDoc->Release();
                    }
                    pMC->Release();
                }
                if (pMkStart)
                    pMkStart->Release();
                if (pMkFinish)
                    pMkFinish->Release();
                pMS->Release();
            }
        }
        pDoc->Release();
    }
    CoUninitialize();
}

bool MAPIMessage::UTF8EncBody(LPTSTR *ppBody, unsigned int &nTextChars)
{
    *ppBody = NULL;

    HRESULT hr = S_OK;

    // must use the stream property
    IStream *pIStream = NULL;
    const ULONG PR_HTML_BODY = 0x1013001E;

    hr = m_pMessage->OpenProperty(PR_HTML_BODY, &IID_IStream, STGM_READ, 0, (LPUNKNOWN
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

    // download the text
    ULONG cb;
    // hr = pIStream->Read(*ppBody, statstg.cbSize.LowPart, &cb);
    // allocate buffer for incoming body data
    char *tBuff = new char[bodySize + 10];

    ZeroMemory(tBuff, bodySize + 10);
    hr = pIStream->Read(tBuff, statstg.cbSize.LowPart, &cb);
    if (FAILED(hr) || (cb != statstg.cbSize.LowPart))
    {
        pIStream->Release();
        pIStream = NULL;
        return false;
    }

    LPWSTR pTempBuff = NULL;
    int cbuf = MultiByteToWideChar(CodePageId(), 0, tBuff, cb, NULL, 0);

    hr = MAPIAllocateBuffer((sizeof (WCHAR) * cbuf) + 10, (LPVOID FAR *)&pTempBuff);
    ZeroMemory(pTempBuff, (sizeof (WCHAR) * cbuf) + 10);

    int rbuf = MultiByteToWideChar(CodePageId(), 0, tBuff, cb, pTempBuff, cbuf);

    UNREFERENCED_PARAMETER(rbuf);
    // Zimbra::Rpc::Connection::LogRawText(tBuff,cb,"HTML");
    delete[] tBuff;

    size_t nLen = 0;

    ParseHTML(pTempBuff, ppBody, &nLen);
    if ((*ppBody == NULL) || !(nLen))
        return false;

    int ctbuf = WideCharToMultiByte(CodePageId(), 0, (LPCWSTR)*ppBody, (int)nLen, NULL, 0, NULL,
        NULL);

    tBuff = new char[(ctbuf + 5) * sizeof (WCHAR)];
    ZeroMemory(tBuff, (ctbuf + 5) * sizeof (WCHAR));
    WideCharToMultiByte(CodePageId(), 0, (LPCWSTR)*ppBody, (int)nLen, tBuff, ctbuf, NULL, NULL);
    // Zimbra::Rpc::Connection::LogRawText(tBuff,ctbuf,"EXFROMHTML_");
    delete[] tBuff;
    MAPIFreeBuffer(pTempBuff);

    // close the stream
    pIStream->Release();
    pIStream = NULL;
    nTextChars = (unsigned int)_tcslen(*ppBody);
    return true;
}

bool MAPIMessage::IsRTFHTML(const char *buf)
{
    // We look for the "\fromhtml" somewhere in the data.
    // If the rtf encodes text rather than html, then instead
    // it will only find "\fromtext".
    const char *pFromPtr = strstr(buf, "\\from");

    if (!pFromPtr)
        return false;
    return !strncmp(pFromPtr, "\\fromhtml", 9);
}

Zimbra::MAPI::MAPIMessage::EnumRTFElement MAPIMessage::MatchRTFElement(const char *psz)
{
    for (int i = 0; i < END; i++)
    {
        if (!strncmp(psz, RTFElement[i].c_str(), RTFElement[i].length()))
            return (EnumRTFElement)i;
    }
    return NOTFOUND;
}

const char *MAPIMessage::Advance(const char *psz, const char *pszCharSet)
{
    bool b = false;
    const char *pszI = NULL;

    while (*psz)
    {
        for (b = false, pszI = pszCharSet; *pszI; b |= (*psz == *pszI), pszI++)
            ;
        if (b)
            psz++;
        else
            return psz;
    }
    return psz;
}

bool MAPIMessage::DecodeRTF2HTML(char *buf, unsigned int *len)
{
 #define WHITESPACE " \t"
    if (!IsRTFHTML(buf))
        return false;

    // pIn -- pointer to where we're reading from
    // pOut -- pointer to where we're writing to. Invariant: d<c
    // pMax -- how far we can read from (i.e. to the end of the original rtf)
    // nIgnoreRTFElement -- stores 'N': after \mhtmlN, we will ignore the subsequent \htmlN.
    char *pOut = buf, *pIn = buf, *pMax = buf + *len;
    int nRTFElement = 0, nIgnoreRTFElement = -1, i = 0, j = 0;

    // First, we skip forwards to the first \htmltag.
    pIn = strstr(pIn, RTFElement[HTMLTAG].c_str());
    if (!(pIn))
        pIn = pMax;
    // * Ignore { and }. These are part of RTF markup.
    // * Ignore \htmlrtf...\htmlrtf0. This is how RTF keeps its equivalent markup separate from the html.
    // * Ignore \r and \n. The real carriage returns are stored in \par tags.
    // * Ignore \pntext{..} and \liN and \fi-N. These are RTF junk.
    // * Convert \par and \tab into \r\n and \t
    // * Convert \'XX into the ascii character indicated by the hex number XX
    // * Convert \{ and \} into { and }. This is how RTF escapes its curly braces.
    // * When we get \*\mhtmltagN, keep the tag, but ignore the subsequent \*\htmltagN
    // * When we get \*\htmltagN, keep the tag as long as it isn't subsequent to a \*\mhtmltagN
    // * All other text should be kept as it is.
    while (pIn < pMax)
    {
        EnumRTFElement rtfElem = NOTFOUND;

        switch (rtfElem = MatchRTFElement(pIn))
        {
        case OPENBRACE:
        case CLOSEBRACE:
        {
            pIn++;
            break;
        }
        case HTMLTAG:
        {
            nRTFElement = strtol(pIn += RTFElement[HTMLTAG].length(), &pIn, 10);
            pIn = (char *)Advance(pIn, WHITESPACE);
            if (nRTFElement == nIgnoreRTFElement)
                for (; *pIn != '}' && *pIn; pIn++)
                    ;
            nIgnoreRTFElement = -1;
            break;
        }
        case MHTMLTAG:
        {
            nRTFElement = strtol(pIn += RTFElement[MHTMLTAG].length(), &pIn, 10);
            pIn = (char *)Advance(pIn, WHITESPACE);
            nIgnoreRTFElement = nRTFElement;
            break;
        }
        case PAR:
        {
            strcpy(pOut, "\r\n");
            pOut += 2;
            pIn += RTFElement[PAR].length();
            break;
        }
        case TAB:
        {
            strcpy(pOut, "  ");
            pOut += 2;
            pIn += RTFElement[TAB].length();
            break;
        }
        case LI:
        case FI:
        {
            pIn = (char *)Advance(pIn += RTFElement[rtfElem].length(), "0123456789");
            break;
        }
        case HEXCHAR:
        {
            *((unsigned char *)pOut) = (unsigned char)strtol(pIn +=
                RTFElement[HEXCHAR].length(), &pIn, 16);
            break;
        }
        case PNTEXT:
        {
            for (pIn += RTFElement[PNTEXT].length(); *pIn != '}' && *pIn; pIn++)
                ;
            break;
        }
        case HTMLRTF:
        {
            pIn = strstr(pIn, RTFElement[HTMLRTF0].c_str());
            if (!pIn)
                pIn = pMax;
            else
                pIn += RTFElement[HTMLRTF0].length();
            break;
        }
        case OPENBRACEESC:
        case CLOSEBRACEESC:
        {
            pIn += RTFElement[rtfElem].length();
            j = rtfElem - OPENBRACEESC + OPENBRACE;
            strncpy(pOut, RTFElement[j].c_str(), i = (int)RTFElement[j].length());
            pOut += i;
            break;
        }
        default:
        {
            *pOut = *pIn;
            pIn++;
            pOut++;
        }
        }
        if (rtfElem != NOTFOUND)
            pIn = (char *)Advance(pIn, WHITESPACE);
    }
    *pOut = 0;
    pOut++;
    *len = (unsigned int)(pOut - buf);
    return pOut != buf;
}

bool MAPIMessage::HtmlBody(LPVOID *ppBody, unsigned int &nHtmlBodyLen)
{
    if (m_pMessagePropVals[HTML_BODY].ulPropTag == PR_BODY_HTML)
    {
        LPVOID pBody = m_pMessagePropVals[HTML_BODY].Value.bin.lpb;

        if (pBody)
        {
            size_t nLen = m_pMessagePropVals[HTML_BODY].Value.bin.cb;

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

    hr = m_pMessage->OpenProperty(PR_BODY_HTML, &IID_IStream, STGM_READ, 0, (LPUNKNOWN
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
    else if ((m_pMessagePropVals[STORE_SUPPORT_MASK].ulPropTag == PR_STORE_SUPPORT_MASK) ||
        (m_pMessagePropVals[RTF_IN_SYNC].ulPropTag == PR_RTF_IN_SYNC))
    {
        // Get the compresed rich text data
        HRESULT hr = S_OK;
        IStream *pIStream = NULL;

        hr = m_pMessage->OpenProperty(PR_RTF_COMPRESSED, &IID_IStream, STGM_READ, 0, (LPUNKNOWN
            FAR *)&pIStream);
        if (pIStream)
        {
            IStream *pUnComIStream = NULL;      // for the uncompressed stream

            // Uncompress the rich text
            WrapCompressedRTFStream(pIStream, 0, &pUnComIStream);
            pIStream->Release();
            if (pUnComIStream)
            {
                int nBufSize = 10240;
                LPSTR pRTFData = new char[nBufSize];
                unsigned int nRTFSize = 0;
                bool bDone = false;

                // We dont know the size of the stream, so kepp reading unless it returns
                // success along with less number of bytes than requested.
                while (!bDone)
                {
                    ULONG ulRead = 0;

                    hr = pUnComIStream->Read(pRTFData + nRTFSize, nBufSize - nRTFSize, &ulRead);
                    if (hr != S_OK)
                    {
                        pRTFData[nRTFSize] = 0;
                        bDone = true;
                    }
                    else
                    {
                        nRTFSize += ulRead;
                        bDone = (ulRead < nBufSize - nRTFSize);
                        if (!bDone)
                        {
                            unsigned int nNewSize = 2 * nRTFSize;
                            char *pNewBuf = new char[nNewSize];

                            memcpy(pNewBuf, pRTFData, nRTFSize);
                            delete[] pRTFData;
                            pRTFData = pNewBuf;
                            nBufSize = nNewSize;
                        }
                    }
                }
                pRTFData[nRTFSize] = 0;
                // close the stream
                pUnComIStream->Release();
                // Conver the RTF data into HTML
                if (DecodeRTF2HTML(pRTFData, &nRTFSize))
                {
                    MAPIAllocateBuffer((ULONG)(nRTFSize + 10), (LPVOID FAR *)ppBody);
                    ZeroMemory(*ppBody, (nRTFSize + 10));
                    memcpy(*ppBody, pRTFData, nRTFSize);
                    nHtmlBodyLen = (UINT)nRTFSize;
                    delete[] pRTFData;
                    return true;
                }
                delete[] pRTFData;
            }
        }
    }
    // some other error occurred?
    // i.e., some messages do not have a body
    *ppBody = NULL;
    nHtmlBodyLen = 0;
    return false;
}

mimepp::Mailbox *Zimbra::MAPI::MakeMimePPMailbox(LPTSTR pDisplayName, LPTSTR pSmtpAddress)
{
    // scan the display name and replace any non-displayable characters with a space
    LPTSTR p = pDisplayName;

    while (p && *p)
    {
        if (*p < 20)
            *p = _T(' ');
        p++;
    }

    int cbBuf = 0;
    LPSTR pBuf = NULL;
    mimepp::Mailbox *pMbx = new mimepp::Mailbox();

    if (pDisplayName != NULL)
    {
        int nDNLen = (int)_tcslen(pDisplayName);

#if UNICODE
        cbBuf = WideCharToMultiByte(CP_UTF8, 0, (LPCWSTR)pDisplayName, nDNLen, NULL, 0, NULL,
            NULL);

        pBuf = new CHAR[cbBuf + 1];
        ZeroMemory(pBuf, cbBuf + 1);

        WideCharToMultiByte(CP_UTF8, 0, (LPCWSTR)pDisplayName, nDNLen, pBuf, cbBuf, NULL, NULL);
        pMbx->setDisplayNameUtf8(pBuf);
        delete[] pBuf;
#else
        pMbx->setDisplayNameUtf8(pDisplayName);
#endif
    }
    else
    {}
#if UNICODE
    int nSALen = (int)_tcslen(pSmtpAddress);

    cbBuf = WideCharToMultiByte(CP_ACP, 0, (LPCWSTR)pSmtpAddress, nSALen, NULL, 0, NULL, NULL);

    pBuf = new CHAR[cbBuf + 1];
    ZeroMemory(pBuf, cbBuf + 1);

    WideCharToMultiByte(CP_ACP, 0, (LPCWSTR)pSmtpAddress, nSALen, pBuf, cbBuf, NULL, NULL);
#else
    LPSTR pBuf = pSmtpAddress;
#endif

    // encode the sender as BASE64
    CHAR *pDomain = strchr(pBuf, '@');

    if (pDomain != NULL)
    {
        pMbx->setDomain(pDomain + 1);
        *pDomain = '\0';
        pMbx->setLocalPart(pBuf);
    }
    else
    {
        pMbx->setLocalPart(pBuf);
    }
#if UNICODE
    delete[] pBuf;
#endif

    return pMbx;
}

int nKnownHeaders = 15;
LPSTR pKnownHeaders[] = {
    "MIME-Version", "Date", "Sender", "From", "To", "Cc", "Bcc", "Reply-To", "Subject",
    "Content-Type", "Content-Transfer-Encoding", "X-Priority", "Message-ID", "X-Unsent",
    "Received"
};

BOOL IsKnownHeader(LPSTR pHeader)
{
    for (int i = 0; i < nKnownHeaders; i++)
    {
        if (stricmp(pHeader, pKnownHeaders[i]) == 0)
            return true;
    }
    return false;
}

inline LPSTR MapInvalid(LPSTR psz)
{
    LPSTR pHead = psz;

    while (psz && *psz)
    {
        if (*psz < 0x20)
            *psz = ' ';
        psz++;
    }
    return pHead;
}

inline LPWSTR MapInvalid(LPWSTR psz)
{
    LPWSTR pHead = psz;

    while (psz && *psz)
    {
        if (*psz < 0x20)
            *psz = L' ';
        psz++;
    }
    return pHead;
}

void AddExtraHeaders(mimepp::Message &msg, LPSTR pExtraHeaders)
{
    mimepp::Headers headers(pExtraHeaders);

    headers.parse();
    headers.assemble();
    for (int i = 0; i < headers.numFields(); i++)
    {
        mimepp::Field &f = headers.fieldAt(i);
        const mimepp::String &name = f.fieldName();

        if (!IsKnownHeader((LPSTR)name.c_str()) && (name.length() > 0))
            msg.headers().fieldBody(name.c_str()).setText(MapInvalid(
                (LPSTR)f.fieldBody().text().c_str()));
    }
}

void MAPIMessage::ToMimePPMessage(mimepp::Message &msg)
{
    RECIP_INFO tempRecip;
    RECIP_INFO tempRecip1;
    HRESULT hr = S_OK;

    msg.headers().fieldBody("MIME-Version").setText("1.0");
    // TODO: PR_READ_RECEIPT_REQUESTED/PR_READ_RECEIPT_ENTRYID - Disposition-Notificaiton-To:
    // grab any additional headers from the mime headers
    if (PROP_TYPE(m_pMessagePropVals[MIME_HEADERS].ulPropTag) != PT_ERROR)
        AddExtraHeaders(msg, m_pMessagePropVals[MIME_HEADERS].Value.lpszA);

    // set the date header
    __int64 date = Date();

    if (date != -1)
    {
        // build a custom date header because mime-pp can't represent dates before 1970
        msg.headers().fieldBody("Date").setText(DateString());
    }
    if (DeliveryDate() != -1)
        msg.headers().fieldBody("X-Zimbra-Received").setText(DeliveryDateString());
    tempRecip.pAddrType = NULL;
    tempRecip.pEmailAddr = NULL;
    tempRecip.cbEid = 0;
    tempRecip.pEid = NULL;

    tempRecip1.pAddrType = NULL;
    tempRecip1.pEmailAddr = NULL;
    tempRecip1.cbEid = 0;
    tempRecip1.pEid = NULL;

    LPTSTR pSenderEmailAdd = NULL;
    LPTSTR pFromEmailAdd = NULL;

    // sender
    if (PROP_TYPE(m_pMessagePropVals[SENDER_ADDRTYPE].ulPropTag) != PT_ERROR)
        tempRecip.pAddrType = MapInvalid(m_pMessagePropVals[SENDER_ADDRTYPE].Value.LPSZ);
    if (PROP_TYPE(m_pMessagePropVals[SENDER_EMAIL_ADDR].ulPropTag) != PT_ERROR)
    {
        tempRecip.pEmailAddr = MapInvalid(m_pMessagePropVals[SENDER_EMAIL_ADDR].Value.LPSZ);
    }
    else                                        // PR_sender_entryid
    {
        if (PROP_TYPE(m_pMessagePropVals[SENDER_ENTRYID].ulPropTag) != PT_ERROR)
        {
            if (m_pMessagePropVals[SENDER_ENTRYID].Value.bin.cb > 28)
            {
                AtoW((LPSTR)((m_pMessagePropVals[SENDER_ENTRYID].Value.bin.lpb) + 28),
                    pSenderEmailAdd);
                tempRecip.pEmailAddr = pSenderEmailAdd;
                tempRecip.pAddrType = _T("EX");
            }
        }
    }
    if (PROP_TYPE(m_pMessagePropVals[SENDER_ENTRYID].ulPropTag) != PT_ERROR)
    {
        tempRecip.cbEid = m_pMessagePropVals[SENDER_ENTRYID].Value.bin.cb;
        tempRecip.pEid = (LPENTRYID)(m_pMessagePropVals[SENDER_ENTRYID].Value.bin.lpb);
    }
    // from
    if (PROP_TYPE(m_pMessagePropVals[SENT_ADDRTYPE].ulPropTag) != PT_ERROR)
        tempRecip1.pAddrType = MapInvalid(m_pMessagePropVals[SENT_ADDRTYPE].Value.LPSZ);
    if (PROP_TYPE(m_pMessagePropVals[SENT_EMAIL_ADDR].ulPropTag) != PT_ERROR)
    {
        tempRecip1.pEmailAddr = MapInvalid(m_pMessagePropVals[SENT_EMAIL_ADDR].Value.LPSZ);
    }
    else                                        // PR_sent_representing_entryid
    {
        if (PROP_TYPE(m_pMessagePropVals[SENT_ENTRYID].ulPropTag) != PT_ERROR)
        {
            if (m_pMessagePropVals[SENT_ENTRYID].Value.bin.cb > 28)
            {
                AtoW((LPSTR)((m_pMessagePropVals[SENT_ENTRYID].Value.bin.lpb) + 28),
                    pFromEmailAdd);
                tempRecip1.pEmailAddr = pFromEmailAdd;
                tempRecip1.pAddrType = _T("EX");
            }
        }
    }
    if (PROP_TYPE(m_pMessagePropVals[SENT_ENTRYID].ulPropTag) != PT_ERROR)
    {
        tempRecip1.cbEid = m_pMessagePropVals[SENT_ENTRYID].Value.bin.cb;
        tempRecip1.pEid = (LPENTRYID)(m_pMessagePropVals[SENT_ENTRYID].Value.bin.lpb);
    }

    BOOL bSameSenderFrom = TRUE;

    if (((PROP_TYPE(m_pMessagePropVals[SENDER_ADDRTYPE].ulPropTag) != PT_ERROR) && (PROP_TYPE(
        m_pMessagePropVals[SENT_ADDRTYPE].ulPropTag) != PT_ERROR)) || ((PROP_TYPE(
        m_pMessagePropVals[SENT_ENTRYID].ulPropTag) != PT_ERROR) && (PROP_TYPE(
        m_pMessagePropVals[SENDER_ENTRYID].ulPropTag) != PT_ERROR)))
        bSameSenderFrom = Zimbra::MAPI::Util::CompareRecipients(*m_session, tempRecip,
            tempRecip1);
    // only add the sender header if its different from the from header
    if (((PROP_TYPE(m_pMessagePropVals[SENDER_ADDRTYPE].ulPropTag) != PT_ERROR) || (PROP_TYPE(
        m_pMessagePropVals[SENDER_ENTRYID].ulPropTag) != PT_ERROR)) && !bSameSenderFrom)
    {
        wstring strSenderEmail(_TEXT(""));

        hr = Zimbra::MAPI::Util::HrMAPIGetSMTPAddress(*m_session, tempRecip, strSenderEmail);

        mimepp::Mailbox *pMbx = MakeMimePPMailbox(MapInvalid(
            m_pMessagePropVals[SENDER_NAME].Value.LPSZ), (LPTSTR)strSenderEmail.c_str());

        msg.headers().sender() = *pMbx;
        delete pMbx;
    }
    else if ((PROP_TYPE(m_pMessagePropVals[SENDER_NAME].ulPropTag) != PT_ERROR) &&
        (tempRecip.pEmailAddr == NULL))         // if no email address, add name only
    {
        wstring strSenderEmail(_TEXT(""));
        mimepp::Mailbox *pMbx = MakeMimePPMailbox(MapInvalid(
            m_pMessagePropVals[SENDER_NAME].Value.LPSZ), (LPTSTR)strSenderEmail.c_str());

        msg.headers().sender() = *pMbx;
        delete pMbx;
        // TRACE( _T("Sender(%s) email address not found."),m_pMessagePropVals[SENDER_NAME].Value.LPSZ);
    }
    // set the "FROM" header
    if ((PROP_TYPE(m_pMessagePropVals[SENT_ADDRTYPE].ulPropTag) != PT_ERROR) || (PROP_TYPE(
        m_pMessagePropVals[SENT_ENTRYID].ulPropTag) != PT_ERROR))
    {
        wstring strSenderEmail(_TEXT(""));

        hr = Zimbra::MAPI::Util::HrMAPIGetSMTPAddress(*m_session, tempRecip1, strSenderEmail);

        mimepp::Mailbox *pMbx = NULL;

        if (PROP_TYPE(m_pMessagePropVals[SENT_NAME].ulPropTag) == PT_ERROR)
        {
            pMbx = MakeMimePPMailbox(NULL, (LPTSTR)strSenderEmail.c_str());
        }
        else
        {
            pMbx = MakeMimePPMailbox(MapInvalid(m_pMessagePropVals[SENT_NAME].Value.LPSZ),
                (LPTSTR)strSenderEmail.c_str());
        }
        msg.headers().from().addMailbox(pMbx);
    }
    if (pFromEmailAdd)
        delete[] pFromEmailAdd;
    if (pSenderEmailAdd)
        delete[] pSenderEmailAdd;
    // add each recipient (you can have no recipients!)
    if (m_pRecipientRows != NULL)
    {
        for (unsigned int i = 0; i < m_pRecipientRows->cRows; i++)
        {
            SRow *pRow = &(m_pRecipientRows->aRow[i]);
            wstring strRecipEmail(_TEXT(""));
            CString strDispName;

            if (pRow->lpProps[RDISPLAY_NAME].ulPropTag == PR_DISPLAY_NAME)
                strDispName = MapInvalid(pRow->lpProps[RDISPLAY_NAME].Value.LPSZ);
            if ((pRow->lpProps[RADDRTYPE].ulPropTag == PR_ADDRTYPE) &&
                (pRow->lpProps[REMAIL_ADDRESS].ulPropTag == PR_EMAIL_ADDRESS) &&
                (pRow->lpProps[RENTRYID].ulPropTag == PR_ENTRYID))
            {
                tempRecip.pAddrType = MapInvalid(pRow->lpProps[RADDRTYPE].Value.LPSZ);
                tempRecip.pEmailAddr = MapInvalid(pRow->lpProps[REMAIL_ADDRESS].Value.LPSZ);
                tempRecip.cbEid = pRow->lpProps[RENTRYID].Value.bin.cb;
                tempRecip.pEid = (LPENTRYID)(pRow->lpProps[RENTRYID].Value.bin.lpb);

                hr = Zimbra::MAPI::Util::HrMAPIGetSMTPAddress(*m_session, tempRecip,
                    strRecipEmail);
            }
            else
            {
                // If message has no information about E-mail ID
                // but Display name is non empty
                if (!strDispName.IsEmpty())
                {
                    // Here we try to figure out whether the display name contains e-mail address also
                    // This is possible in case of drafts.
                    int nStart = strDispName.Find('<') + 1;
                    int nRevFind = strDispName.ReverseFind('<') + 1;

                    // If there is single '<' in the display name
                    if (nStart && (nStart == nRevFind))
                    {
                        int nEnd = strDispName.Find('>');

                        nRevFind = strDispName.ReverseFind('>');
                        // If there is single '>' in the display name appearing after '<'
                        if ((nEnd == nRevFind) && (nStart < nEnd) && (-1 != nEnd))
                        {
                            strRecipEmail = wstring(strDispName.Mid(nStart, nEnd -
                                nStart).GetString());
                            strDispName.Truncate(nStart - 1);
                        }
                    }
                }
                else
                {
                    continue;
                }
            }

            ULONG mapiRecipType = pRow->lpProps[RRECIPIENT_TYPE].Value.l;
            mimepp::Mailbox *pMbx = MakeMimePPMailbox((LPWSTR)strDispName.GetString(),
                (LPTSTR)strRecipEmail.c_str());

            if (mapiRecipType == MAPI_TO)
                msg.headers().to().addAddress(pMbx);
            else if (mapiRecipType == MAPI_CC)
                msg.headers().cc().addAddress(pMbx);
            else if (mapiRecipType == MAPI_BCC)
                msg.headers().bcc().addAddress(pMbx);
            else
                delete pMbx;
        }
    }
    // add all the reply-to's
    if ((PROP_TYPE(m_pMessagePropVals[REPLY_NAMES].ulPropTag) != PT_ERROR) && (PROP_TYPE(
        m_pMessagePropVals[REPLY_ENTRIES].ulPropTag) != PT_ERROR))
    {
        // LPTSTR pNames = _pMessagePropVals[REPLY_NAMES].Value.LPSZ;

        FLATENTRYLIST *pEntryList =
            (FLATENTRYLIST *)m_pMessagePropVals[REPLY_ENTRIES].Value.bin.lpb;
        FLATENTRY *pEntry = (FLATENTRY *)pEntryList->abEntries;

        for (ULONG i = 0; i < pEntryList->cEntries; i++)
        {
            IMailUser *pUser = NULL;
            ULONG ulObjType = 0;

            m_session->OpenEntry(pEntry->cb, (LPENTRYID)pEntry->abEntry, NULL, MAPI_BEST_ACCESS,
                &ulObjType, (LPUNKNOWN *)&pUser);
            if (pUser == NULL)
                continue;

            LPSPropValue pReplyToPropVals = NULL;
            ULONG cVals = 0;

            pUser->GetProps((LPSPropTagArray) & m_replyToPropTags, fMapiUnicode, &cVals,
                &pReplyToPropVals);

            ULONG ulRefCount = pUser->Release();

            UNREFERENCED_PARAMETER(ulRefCount);
            // DTRACE( _T("LEEKDIAG: %x %d"), pUser, ulRefCount );
            if (cVals != NREPLYTOPROPS)
            {
                if (pReplyToPropVals != NULL)
                    MAPIFreeBuffer(pReplyToPropVals);
                continue;
            }
            //
            tempRecip.pAddrType = MapInvalid(pReplyToPropVals[REPLYTO_ADDRTYPE].Value.LPSZ);
            tempRecip.pEmailAddr = MapInvalid(
                pReplyToPropVals[REPLYTO_EMAIL_ADDRESS].Value.LPSZ);
            tempRecip.cbEid = pReplyToPropVals[REPLYTO_ENTRYID].Value.bin.cb;
            tempRecip.pEid = (LPENTRYID)(pReplyToPropVals[REPLYTO_ENTRYID].Value.bin.lpb);

            wstring strRecipEmail(_TEXT(""));

            hr = Zimbra::MAPI::Util::HrMAPIGetSMTPAddress(*m_session, tempRecip, strRecipEmail);

            mimepp::Mailbox *pMbx = MakeMimePPMailbox(MapInvalid(
                pReplyToPropVals[REPLYTO_DISPLAY_NAME].Value.LPSZ),
                (LPTSTR)strRecipEmail.c_str());

            msg.headers().replyTo().addAddress(pMbx);

            MAPIFreeBuffer(pReplyToPropVals);

            LPBYTE pTemp = (LPBYTE)pEntry;
            ULONG offset = (pEntry->cb + sizeof (pEntry->cb));

            if ((offset & 3) != 0)
                offset = (offset & ~3) + 4;
            pTemp += offset;
            pEntry = (FLATENTRY *)pTemp;
        }
    }

    // add the subject
    LPTSTR pSubject = NULL;

    if (Subject(&pSubject))
    {
        int nSubjLen = (int)_tcslen(pSubject);

        if (nSubjLen > 0)
        {
            LPSTR pMimeSubject = NULL;
            Zimbra::MAPI::Util::CreateMimeSubject(pSubject, CodePageId(), &pMimeSubject);
            mimepp::String subjStr(pMimeSubject);

            msg.headers().subject().setText(subjStr);
            if (pMimeSubject != NULL)
                delete[] pMimeSubject;
        }
        MAPIFreeBuffer(pSubject);
    }
    // add X-Priority
    if (PROP_TYPE(m_pMessagePropVals[IMPORTANCE].ulPropTag) != PT_ERROR)
    {
        switch (m_pMessagePropVals[IMPORTANCE].Value.l)
        {
        case IMPORTANCE_HIGH:
            msg.headers().fieldBody("X-Priority").setText("1");
            break;
        case IMPORTANCE_NORMAL:
            msg.headers().fieldBody("X-Priority").setText("3");
            break;
        case IMPORTANCE_LOW:
            msg.headers().fieldBody("X-Priority").setText("5");
            break;
        }
    }
    // add Message-Id
    if (PROP_TYPE(m_pMessagePropVals[INTERNET_MESSAGE_ID].ulPropTag) != PT_ERROR)
        msg.headers().messageId().setString(MapInvalid(
            m_pMessagePropVals[INTERNET_MESSAGE_ID].Value.lpszA));
    // add X-Unsent
    if (IsUnsent())
        msg.headers().fieldBody("X-Unsent").setText("1");

    // add the body - but it may not be available as a prop...
    LPTSTR pTextBody = NULL;
    LPVOID pHtmlBody = NULL;
    unsigned int nHtmlLen = 0;
    unsigned int nTextChars = 0;
    LPSTR pCharset = NULL;
    Zimbra::MAPI::Util::CharsetUtil::CharsetStringFromCodePageId(CodePageId(), &pCharset);
    LPMESSAGE pMsg = InternalMessageObject();
    ULONG nBody = 0;
    bool nunicodemsg = false;

    if (pMsg)
    {
        Zimbra::MAPI::Util::StoreUtils *storeUtils =
            Zimbra::MAPI::Util::StoreUtils::getInstance();

        if (storeUtils->Init())
        {
            nunicodemsg = storeUtils->GetAnsiStoreMsgNativeType(pMsg, &nBody);
        }
        else
        {
            // TRACE(_T("storeUtils::Init Failed"));
        }
    }

    // For nonunicode pst, if charset is utf-8, PR_HTML is read. PR_BODY & PR_RTF_COMPRESSED doesnt read accented characters
    // in correct way (bug#19913)
    bool bProcessNUnicode = false;

    if ((nunicodemsg) && ((nBody == MAPI_NATIVE_BODY_TYPE_HTML) || (nBody ==
        MAPI_NATIVE_BODY_TYPE_PLAINTEXT)) && (strcmp(pCharset, "utf-8") == 0))
        bProcessNUnicode = UTF8EncBody(&pTextBody, nTextChars);
    if (!bProcessNUnicode)
    {
        TextBody(&pTextBody, nTextChars);
        HtmlBody(&pHtmlBody, nHtmlLen);
    }

    // Differed allocation of memory to avoid memory leaks
    mimepp::BodyPart *pTextPart = NULL;
    mimepp::BodyPart *pHtmlPart = NULL;

    // fill in the BodyPart for the text memo
    if (pTextBody != NULL)
    {
        pTextPart = new mimepp::BodyPart;

        mimepp::String ct("text/plain; charset=");

        ct += pCharset;
        ct += ";";

        pTextPart->headers().contentType().setString(ct);

        int nMBBody = WideCharToMultiByte(CodePageId(), 0, pTextBody, nTextChars, NULL, 0, NULL,
            NULL);
        LPSTR pMBBody = new CHAR[nMBBody + 1];

        ZeroMemory(pMBBody, nMBBody + 1);
        WideCharToMultiByte(CodePageId(), 0, pTextBody, nTextChars, pMBBody, nMBBody, NULL,
            NULL);

        Zimbra::MAPI::Util::AddBodyToPart(pTextPart, pMBBody, nMBBody);

        pTextPart->body().assemble();
        if (pMBBody != NULL)
            delete[] pMBBody;
    }
    // fill in the BodyPart for the html memo
    if (pHtmlBody != NULL)
    {
        pHtmlPart = new mimepp::BodyPart;

        mimepp::String ct("text/html; charset=");

        ct += pCharset;
        ct += ";";

        pHtmlPart->headers().contentType().setString(ct);

        Zimbra::MAPI::Util::AddBodyToPart(pHtmlPart, (LPSTR)pHtmlBody, nHtmlLen);

        pHtmlPart->body().assemble();
    }

    // points to the part that contains the memo
    mimepp::Entity *pMemoPart = NULL;

    // set the content type of the message if it has an attachment
    if (HasAttach())
    {
        msg.headers().contentType().setString("multipart/mixed");
        msg.headers().contentType().parse();
        msg.headers().contentType().createBoundary();

        mimepp::BodyPart *pTemp = new mimepp::BodyPart;

        msg.body().addBodyPart(pTemp);
        pMemoPart = pTemp;
    }
    else
    {
        pMemoPart = &(msg);
        //
    }
    if ((pTextBody != NULL) && (pHtmlBody != NULL))
    {
        pMemoPart->headers().contentType().setString("multipart/alternative");
        pMemoPart->headers().contentType().parse();
        pMemoPart->headers().contentType().createBoundary();
        pMemoPart->body().addBodyPart(pTextPart);
        pMemoPart->body().addBodyPart(pHtmlPart);
        pMemoPart->body().assemble();
    }
    // only text exists
    else if (pTextBody != NULL)
    {
        pMemoPart->headers().contentType().setString(
            pTextPart->headers().contentType().getString());
        pMemoPart->headers().contentTransferEncoding().setString(
            pTextPart->headers().contentTransferEncoding().getString());
        pMemoPart->body().setString(pTextPart->body().getString());
        pMemoPart->body().assemble();
        delete pTextPart;
        delete pHtmlPart;
    }
    if (HasAttach())
    {
        LPSRowSet pAttachRows = NULL;
        LPMAPITABLE pAttachTable = NULL;

        SizedSPropTagArray(2, attachProps) = {
            2, { PR_ATTACH_NUM, PR_ATTACH_SIZE }
        };

        hr = m_pMessage->GetAttachmentTable(fMapiUnicode, &pAttachTable);
        if (FAILED(hr))
        {
            throw MAPIMessageException(E_FAIL, L"ToMimePPMessage(): GetAttachmentTable Failed.",
                __LINE__, __FILE__);
        }
        hr = HrQueryAllRows(pAttachTable, (LPSPropTagArray) & attachProps, NULL, NULL, 0,
            &pAttachRows);
        if (FAILED(hr))
        {
            pAttachTable->Release();
            throw MAPIMessageException(E_FAIL, L"ToMimePPMessage(): HrQueryAllRows Failed.",
                __LINE__, __FILE__);
        }
// Has been changed to MAX_MESSAGE_SIZE and made global
// const ULONG MAX_ATTACH_SIZE = (1024 * 1024 * 5 );
        for (unsigned int i = 0; i < pAttachRows->cRows; i++)
        {
            mimepp::BodyPart *pAttachPart = NULL;
            ULONG attachSize = pAttachRows->aRow[i].lpProps[1].Value.l;

            UNREFERENCED_PARAMETER(attachSize);

            LPATTACH pAttach = NULL;

            hr = m_pMessage->OpenAttach(pAttachRows->aRow[i].lpProps[0].Value.l, NULL, 0,
                &pAttach);
            if (FAILED(hr))
                continue;
// Now checking overall messagesize instead of attachment size

/*			if( PROP_TYPE(pAttachRows->aRow[i].lpProps[1].ulPropTag) != PT_ERROR && attachSize > MAX_ATTACH_SIZE )
 *                      {
 *                              pAttachPart = AttachTooLargeAttachPart( attachSize, pAttach, pCharset );
 *                      }
 *                      else*/
            try
            {
                pAttachPart = Zimbra::MAPI::Util::AttachPartFromIAttach(*m_session, pAttach,
                    pCharset, CodePageId());
            }
            catch (...)
            {
                // TRACE(_T("**Error** Exception in AttachPartFromIAttach occurred"));
                pAttachPart = NULL;
            }
            if (pAttachPart != NULL)
                msg.body().addBodyPart(pAttachPart);

            ULONG ulRefCount = pAttach->Release();

            UNREFERENCED_PARAMETER(ulRefCount);
        }
        FreeProws(pAttachRows);

        ULONG ulRefCount = pAttachTable->Release();

        UNREFERENCED_PARAMETER(ulRefCount);
    }
    // assemble the message
    try
    {
        msg.assemble();
    }
    catch (...)
    {
        // TRACE(_T("**Error** Exception in msg.assemble occurred"));
    }
    if (pTextBody != NULL)
        MAPIFreeBuffer(pTextBody);
    if (pHtmlBody != NULL)
        MAPIFreeBuffer(pHtmlBody);
    if (pCharset != NULL)
        delete[] pCharset;
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MessageIterator
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MessageIterator::MIRestriction MessageIterator::m_restriction;
MessageIterator::MessageIterPropTags MessageIterator::m_props = {
    NMSGPROPS, { PR_ENTRYID, PR_LONGTERM_ENTRYID_FROM_TABLE, PR_CLIENT_SUBMIT_TIME,
                 PR_MESSAGE_CLASS }
};
MessageIterator::MessageIterSortOrder MessageIterator::m_sortOrder = {
    1, 0, 0, { PR_MESSAGE_DELIVERY_TIME, TABLE_SORT_ASCEND }
};
MessageIterator::MessageIterator() {}

MessageIterator::~MessageIterator() {}

LPSPropTagArray MessageIterator::GetProps()
{
    return (LPSPropTagArray) & m_props;
}

LPSSortOrderSet MessageIterator::GetSortOrder()
{
    return (LPSSortOrderSet) & m_sortOrder;
}

LPSRestriction MessageIterator::GetRestriction(ULONG TypeMask, FILETIME startDate)
{
    return m_restriction.GetRestriction(TypeMask, startDate);
}

BOOL MessageIterator::GetNext(MAPIMessage &msg)
{
    SRow *pRow = MAPITableIterator::GetNext();

    if (pRow == NULL)
        return FALSE;

    LPMESSAGE pMessage = NULL;
    HRESULT hr = S_OK;
    ULONG objtype;
    ULONG cb = pRow->lpProps[MI_ENTRYID].Value.bin.cb;
    LPENTRYID peid = (LPENTRYID)(pRow->lpProps[MI_ENTRYID].Value.bin.lpb);

    if (FAILED(hr = m_pParentFolder->OpenEntry(cb, peid, NULL, MAPI_BEST_ACCESS, &objtype,
            (LPUNKNOWN *)&pMessage)))
        throw GenericException(hr, L"MessageIterator::GetNext():OpenEntry Failed.", __LINE__,
            __FILE__);
    msg.Initialize(pMessage, *m_session);

    return TRUE;
}

BOOL MessageIterator::GetNext(__int64 &date, SBinary &bin)
{
    UNREFERENCED_PARAMETER(date);
    UNREFERENCED_PARAMETER(bin);
    return false;
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MessageIterator::MIRestriction
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
Zimbra::MAPI::MessageIterator::MIRestriction::MIRestriction()
{
    // Task
    _pTaskClass = new WCHAR[10];
    wcscpy(_pTaskClass, L"ipm.task");
    _propValTask.dwAlignPad = 0;
    _propValTask.ulPropTag = PR_MESSAGE_CLASS;
    _propValTask.Value.lpszW = _pTaskClass;

    // Appointment
    _pApptClass = new WCHAR[20];
    wcscpy(_pApptClass, L"ipm.appointment");
    _propValAppt.dwAlignPad = 0;
    _propValAppt.ulPropTag = PR_MESSAGE_CLASS;
    _propValAppt.Value.lpszW = _pApptClass;

    // Meeting Request and responses
    _pReqAndResClass = new WCHAR[15];
    wcscpy(_pReqAndResClass, L"ipm.schedule");
    _propValReqAndRes.dwAlignPad = 0;
    _propValReqAndRes.ulPropTag = PR_MESSAGE_CLASS;
    _propValReqAndRes.Value.lpszW = _pReqAndResClass;

    // Mails
    _pMailClass = new WCHAR[10];
    wcscpy(_pMailClass, L"ipm.note");
    _propValMail.dwAlignPad = 0;
    _propValMail.ulPropTag = PR_MESSAGE_CLASS;
    _propValMail.Value.lpszW = _pMailClass;

    // Messages with Message class "IMP" were getting skipped. Bug 21064
    _propValCanbeMail.dwAlignPad = 0;
    _propValCanbeMail.ulPropTag = PR_MESSAGE_CLASS;
    _propValCanbeMail.Value.lpszW = L"ipm";

    // Messages with Message class "IPM.POST" were getting skipped. Bug 36277
    _propValCanbeMailPost.dwAlignPad = 0;
    _propValCanbeMailPost.ulPropTag = PR_MESSAGE_CLASS;
    _propValCanbeMailPost.Value.lpszW = L"ipm.post";

    // Distribution List
    _pDistListClass = new WCHAR[15];
    wcscpy(_pDistListClass, L"ipm.distlist");
    _propValDistList.dwAlignPad = 0;
    _propValDistList.ulPropTag = PR_MESSAGE_CLASS;
    _propValDistList.Value.lpszW = _pDistListClass;

    // Contacts
    _pContactClass = new WCHAR[15];
    wcscpy(_pContactClass, L"ipm.contact");
    _propValCont.dwAlignPad = 0;
    _propValCont.ulPropTag = PR_MESSAGE_CLASS;
    _propValCont.Value.lpszW = _pContactClass;

    _propValSTime.dwAlignPad = 0;
    _propValSTime.ulPropTag = PR_CLIENT_SUBMIT_TIME;
    _propValSTime.Value.ft.dwHighDateTime = 0;
    _propValSTime.Value.ft.dwLowDateTime = 0;

    _propValCTime.dwAlignPad = 0;
    _propValCTime.ulPropTag = PR_CREATION_TIME;
    _propValCTime.Value.ft.dwHighDateTime = 0;
    _propValCTime.Value.ft.dwLowDateTime = 0;

    // Property value structure for a named property which specifies
    // that whether the mail is completely downloaded or not in case of IMAP
    // Being named property, Property tag is initialized with PR_NULL and
    // needs to be set with appropriate value before use
    _propValIMAPHeaderOnly.dwAlignPad = 0;
    _propValIMAPHeaderOnly.ulPropTag = PR_NULL;
    _propValIMAPHeaderOnly.Value.ul = 0;

    pR[0].rt = RES_AND;
    pR[0].res.resAnd.cRes = 2;
    pR[0].res.resAnd.lpRes = &pR[1];

    pR[1].rt = RES_OR;
    pR[1].res.resOr.cRes = 2;
    pR[1].res.resOr.lpRes = &pR[5];

    pR[5].rt = RES_AND;
    pR[5].res.resAnd.cRes = 2;
    pR[5].res.resAnd.lpRes = &pR[7];

    pR[7].rt = RES_EXIST;
    pR[7].res.resExist.ulPropTag = PR_CLIENT_SUBMIT_TIME;

    pR[8].rt = RES_PROPERTY;
    pR[8].res.resProperty.relop = RELOP_GE;
    pR[8].res.resProperty.ulPropTag = PR_CLIENT_SUBMIT_TIME;
    pR[8].res.resProperty.lpProp = &_propValSTime;

    pR[6].rt = RES_AND;
    pR[6].res.resAnd.cRes = 3;
    pR[6].res.resAnd.lpRes = &pR[9];

    pR[9].rt = RES_NOT;
    pR[9].res.resNot.lpRes = &pR[12];

    pR[12].rt = RES_EXIST;
    pR[12].res.resExist.ulPropTag = PR_CLIENT_SUBMIT_TIME;

    pR[10].rt = RES_EXIST;
    pR[10].res.resExist.ulPropTag = PR_CREATION_TIME;

    pR[11].rt = RES_PROPERTY;
    pR[11].res.resProperty.relop = RELOP_GE;
    pR[11].res.resProperty.ulPropTag = PR_CREATION_TIME;
    pR[11].res.resProperty.lpProp = &_propValCTime;

    pR[2].rt = RES_OR;
    pR[2].res.resOr.cRes = 7;
    pR[2].res.resOr.lpRes = &pR[13];

    // pR[13] will be set in GetRestriction
    // pR[14] will be set in GetRestriction
    // pR[15] will be set in GetRestriction
    // pR[16] will be set in GetRestriction
    // pR[17] will be set in GetRestriction
    // pR[18] will be set in GetRestriction
    // pR[19] will be set in GetRestriction

    // Restriction for selecting mails which are completely downloaded in case of IMAP
    pR[3].rt = RES_OR;
    pR[3].res.resOr.cRes = 2;
    pR[3].res.resOr.lpRes = &pR[20];

    // Either the property should not exit
    pR[20].rt = RES_NOT;
    pR[20].res.resNot.lpRes = &pR[4];

    pR[4].rt = RES_EXIST;

    // pR[4].res.resExist.ulPropTag will be set in GetRestriction

    // if exists, it's value should be zero
    pR[21].rt = RES_AND;
    pR[21].res.resAnd.cRes = 2;
    pR[21].res.resAnd.lpRes = &pR[22];

    pR[22].rt = RES_EXIST;

    // pR[22].res.resExist.ulPropTag will be set in GetRestriction

    pR[23].rt = RES_PROPERTY;
    pR[23].res.resProperty.relop = RELOP_EQ;

    // pR[23].res.resProperty.ulPropTag will be set in GetRestriction
    // pR[23].res.resProperty.lpProp will be set in GetRestriction
}

MessageIterator::MIRestriction::~MIRestriction()
{
    delete[] _pContactClass;
    delete[] _pMailClass;
    delete[] _pApptClass;
    delete[] _pReqAndResClass;
    delete[] _pTaskClass;
    delete[] _pDistListClass;
}

LPSRestriction MessageIterator::MIRestriction::GetRestriction(ULONG TypeMask, FILETIME
    startDate)
{
    int iCounter = 13;
    int iNumRes = 0;

    if (TypeMask & ZCM_MAIL)                    // mail
    {
        pR[iCounter].rt = RES_CONTENT;
        pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_SUBSTRING;
        pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
        pR[iCounter].res.resContent.lpProp = &_propValMail;

        iCounter++;
        iNumRes++;

        pR[iCounter].rt = RES_CONTENT;
        pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_FULLSTRING;
        pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
        pR[iCounter].res.resContent.lpProp = &_propValCanbeMail;

        iCounter++;
        iNumRes++;

        pR[iCounter].rt = RES_CONTENT;
        pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_FULLSTRING;
        pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
        pR[iCounter].res.resContent.lpProp = &_propValCanbeMailPost;

        iCounter++;
        iNumRes++;
    }
    if (TypeMask & ZCM_CONTACTS)
    {
        pR[iCounter].rt = RES_CONTENT;
        pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_SUBSTRING;
        pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
        pR[iCounter].res.resContent.lpProp = &_propValCont;
        iCounter++;
        iNumRes++;

        pR[iCounter].rt = RES_CONTENT;
        pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_SUBSTRING;
        pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
        pR[iCounter].res.resContent.lpProp = &_propValDistList;
        iCounter++;
        iNumRes++;
    }
    // If we are using Outlook Object Model to extract calendar data,
    // ImportApptments will always return TRUE but if we are using CDOEX
    // to extract calendar data, and fail to retrieve base forder URL of Exchange Server,
    // ImportApptments will return FALSE
    if (TypeMask > 0)                           // TODO true xxxxxxxxxxxxxxxxxx
    {
        if (TypeMask & ZCM_TASKS)
        {
            pR[iCounter].rt = RES_CONTENT;
            pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_SUBSTRING;
            pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
            pR[iCounter].res.resContent.lpProp = &_propValTask;
            iCounter++;
            iNumRes++;
        }
        if (TypeMask & ZCM_APPOINTMENTS)
        {
            pR[iCounter].rt = RES_CONTENT;
            pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_SUBSTRING;
            pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
            pR[iCounter].res.resContent.lpProp = &_propValAppt;
            iCounter++;
            iNumRes++;

            pR[iCounter].rt = RES_CONTENT;
            pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_SUBSTRING;
            pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
            pR[iCounter].res.resContent.lpProp = &_propValReqAndRes;
            iCounter++;
            iNumRes++;
        }
    }
    else
    {
        if (TypeMask & ZCM_APPOINTMENTS)
        {
            pR[iCounter].rt = RES_CONTENT;
            pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_SUBSTRING;
            pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
            pR[iCounter].res.resContent.lpProp = &_propValReqAndRes;
            iCounter++;
            iNumRes++;
        }
    }
    pR[2].res.resOr.cRes = iNumRes;

    ULONG ulIMAPHeaderInfoPropTag = g_ulIMAPHeaderInfoPropTag;

    if (_propValIMAPHeaderOnly.ulPropTag == PR_NULL)
    {
        _propValIMAPHeaderOnly.ulPropTag = ulIMAPHeaderInfoPropTag;
        pR[4].res.resExist.ulPropTag = pR[22].res.resExist.ulPropTag =
                _propValIMAPHeaderOnly.ulPropTag;
        _propValIMAPHeaderOnly.Value.ul = 0;

        pR[23].res.resProperty.ulPropTag = ulIMAPHeaderInfoPropTag;
        pR[23].res.resProperty.lpProp = &_propValIMAPHeaderOnly;
    }

    bool bUseStartDate = false;
    bool bIgnoreBodyLessMessage = false;

    if ((bUseStartDate && (!(TypeMask & ZCM_CONTACTS))) && bIgnoreBodyLessMessage)
    {
        FILETIME &ft = startDate;

        _propValCTime.Value.ft.dwHighDateTime = ft.dwHighDateTime;
        _propValCTime.Value.ft.dwLowDateTime = ft.dwLowDateTime;

        _propValSTime.Value.ft.dwHighDateTime = ft.dwHighDateTime;
        _propValSTime.Value.ft.dwLowDateTime = ft.dwLowDateTime;
        if (ulIMAPHeaderInfoPropTag)
            pR[0].res.resAnd.cRes = 3;

        else
            pR[0].res.resAnd.cRes = 2;
        pR[0].res.resAnd.lpRes = &pR[1];

        return &pR[0];
    }
    // Applying date restriction to messages other than contact
    else if ((bUseStartDate && (!(TypeMask & ZCM_CONTACTS))) && !bIgnoreBodyLessMessage)
    {
        FILETIME &ft = startDate;

        _propValCTime.Value.ft.dwHighDateTime = ft.dwHighDateTime;
        _propValCTime.Value.ft.dwLowDateTime = ft.dwLowDateTime;

        _propValSTime.Value.ft.dwHighDateTime = ft.dwHighDateTime;
        _propValSTime.Value.ft.dwLowDateTime = ft.dwLowDateTime;

        pR[0].res.resAnd.cRes = 2;
        pR[0].res.resAnd.lpRes = &pR[1];

        return &pR[0];
    }
    else if (!(bUseStartDate && (!(TypeMask & ZCM_CONTACTS))) && bIgnoreBodyLessMessage &&
        ulIMAPHeaderInfoPropTag)
    {
        pR[0].res.resAnd.cRes = 2;
        pR[0].res.resAnd.lpRes = &pR[2];

        return &pR[0];
    }
    else
    {
        return &pR[2];
    }
}
