#pragma once

DEFINE_GUID(PS_OUTLOOK_APPT, 0x00062002, 0x0000, 0x0000, 0xC0, 0x00, 0x00, 0x00, 0x00, 0x00,
    0x00, 0x46);
#define PR_CLEAN_GLOBAL_OBJID	PROP_TAG( PT_BINARY,	0x0023)

enum OutlookBusyStatus
{
    oFree = 0,
    oTentative = 1,
    oBusy = 2,
    oOutOfOffice = 3
};

enum OutlookResponseStatus
{
    oResponseNone = 0,
    oResponseOrganized = 1,
    oResponseTentative = 2,
    oResponseAccepted = 3,
    oResponseDeclined = 4,
    oResponseNotResponded = 5
};


// MAPIAppointmentException class
class MAPIAppointmentException: public GenericException
{
public:
    MAPIAppointmentException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    MAPIAppointmentException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine, LPCSTR strFile);
    virtual ~MAPIAppointmentException() {}
};

// MAPIAppointment class
class MAPIAppointment
{
private:
    //static bool m_bNamedPropsInitialized;

    // prop tags for named properties
    ULONG pr_appt_start, pr_appt_end, pr_location, pr_busystatus, pr_allday, pr_responsestatus,
	  pr_reminderminutes, pr_private;

    // index of props
    typedef enum _AppointmentPropIdx
    {
        N_APPTSTART, N_APPTEND, N_LOCATION, N_BUSYSTATUS, N_ALLDAY, N_RESPONSESTATUS, N_NUMAPPTPROPS
    } AppointmentPropIdx;

    typedef enum _CommonPropIdx
    {
        N_REMINDERMINUTES, N_PRIVATE, N_NUMCOMMONPROPS
    } CommonPropIdx;

    // this enum lists all the props
    enum
    {
        C_SUBJECT, C_BODY, C_HTMLBODY, C_UID, C_START, C_END, C_LOCATION, C_BUSYSTATUS, C_ALLDAY,  
	C_RESPONSESTATUS, C_REMINDERMINUTES, C_PRIVATE, C_NUMALLAPPTPROPS
	//org stuff later
    };

    // these are the named property id's
    LONG nameIds[N_NUMAPPTPROPS];
    LONG nameIdsC[N_NUMCOMMONPROPS];
    Zimbra::MAPI::MAPIMessage *m_mapiMessage;
    Zimbra::MAPI::MAPISession *m_session;
    LPMESSAGE m_pMessage;
    LPSPropValue m_pPropVals;

    // appointment data members (represented both by regular and named props
    wstring m_pSubject;
    wstring m_pInstanceUID;
    wstring m_pLocation;
    wstring m_pStartDate;
    wstring m_pEndDate;
    wstring m_pBusyStatus;
    wstring m_pAllday;
    wstring m_pTransparency;
    wstring m_pResponseStatus;
    wstring m_pOrganizerName;
    wstring m_pOrganizerAddr;
    wstring m_pReminderMinutes;
    wstring m_pPrivate;
    wstring m_pPlainTextFile;
    wstring m_pHtmlFile;

public:
    MAPIAppointment(Zimbra::MAPI::MAPISession &session, Zimbra::MAPI::MAPIMessage &mMessage);
    ~MAPIAppointment();
    HRESULT InitNamedPropsForAppt();
    HRESULT GetApptValues();
    HRESULT GetAppointmentAttachment(wstring &wstrAttachmentPath);
    bool TextBody(LPTSTR *ppBody, unsigned int &nTextChars);
    bool HtmlBody(LPVOID *ppBody, unsigned int &nHtmlBodyLen);
    LPWSTR WriteContentsToFile(LPTSTR pBody, bool isAscii);
    void SetPlainTextFileAndContent();
    void SetHtmlFileAndContent();

    void SetSubject(LPTSTR pStr)
    {
        m_pSubject = pStr;
    }

    void SetStartDate(FILETIME ft)
    {
	SYSTEMTIME st;

	FileTimeToSystemTime(&ft, &st);
	m_pStartDate = Zimbra::Util::FormatSystemTime(st, TRUE, TRUE);
    }

    void SetEndDate(FILETIME ft)
    {
	SYSTEMTIME st;

	FileTimeToSystemTime(&ft, &st);
	m_pEndDate = Zimbra::Util::FormatSystemTime(st, TRUE, TRUE);
    }

    void SetInstanceUID(LPTSTR pStr)
    {
        m_pInstanceUID = pStr;
    }

    void SetLocation(LPTSTR pStr)
    {
        m_pLocation = pStr;
    }

    void SetBusyStatus(long busystatus)
    {
	switch (busystatus)
	{
	    case oFree:		m_pBusyStatus = L"F";	break;
	    case oTentative:	m_pBusyStatus = L"T";	break;
	    case oBusy:		m_pBusyStatus = L"B";	break;
	    case oOutOfOffice:	m_pBusyStatus = L"O";	break;
	    default:		m_pBusyStatus = L"T";
	}
    }

    void SetAllday(unsigned short usAllday)
    {
        m_pAllday = (usAllday == 1) ? L"1" : L"0";
    }

    void SetTransparency(LPTSTR pStr)
    {
        m_pTransparency = pStr;
    }

    void SetResponseStatus(long responsestatus)
    {
	switch (responsestatus)
	{
	    case oResponseNone:		m_pResponseStatus = L"NE";	break;
	    case oResponseOrganized:	m_pResponseStatus = L"OR";	break;	    // OR????  -- temporary
	    case oResponseTentative:	m_pResponseStatus = L"TE";	break;
	    case oResponseAccepted:	m_pResponseStatus = L"AC";	break;
	    case oResponseDeclined:	m_pResponseStatus = L"DE";	break;
	    case oResponseNotResponded:	m_pResponseStatus = L"NE";	break;
	    default:			m_pResponseStatus = L"NE";
	}
    }

    void SetOrganizerName(LPTSTR pStr)
    {
        m_pOrganizerName = pStr;
    }

    void SetOrganizerAddr(LPTSTR pStr)
    {
        m_pOrganizerAddr = pStr;
    }

    void SetReminderMinutes(long reminderminutes)
    {
	WCHAR pwszTemp[10];
	_ltow(reminderminutes, pwszTemp, 10);
	m_pReminderMinutes = pwszTemp;
    }

    void SetPrivate(unsigned short usPrivate)
    {
	m_pPrivate = (usPrivate == 1) ? L"1" : L"0";
    }

    wstring GetSubject() { return m_pSubject; }
    wstring GetStartDate() { return m_pStartDate; }
    wstring GetEndDate() { return m_pEndDate; }
    wstring GetInstanceUID() { return m_pInstanceUID; }
    wstring GetLocation() { return m_pLocation; }
    wstring GetBusyStatus() { return m_pBusyStatus; }
    wstring GetAllday() { return m_pAllday; }
    wstring GetTransparency() { return m_pTransparency; }
    wstring GetReminderMinutes() { return m_pReminderMinutes; }
    wstring GetResponseStatus() { return m_pResponseStatus; }
    wstring GetOrganizerName() { return m_pOrganizerName; }
    wstring GetOrganizerAddr() { return m_pOrganizerAddr; }
    wstring GetPrivate() { return m_pPrivate; }
    wstring GetPlainTextFileAndContent() { return m_pPlainTextFile; }
    wstring GetHtmlFileAndContent() { return m_pHtmlFile; }

};
