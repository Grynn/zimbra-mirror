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
    HRESULT SetMAPIAppointmentValues();
    void SetSubject(LPTSTR pStr);
    void SetStartDate(FILETIME ft);
    void SetEndDate(FILETIME ft);
    void SetInstanceUID(LPTSTR pStr);
    void SetLocation(LPTSTR pStr);
    void SetBusyStatus(long busystatus);
    void SetAllday(unsigned short usAllday);
    void SetTransparency(LPTSTR pStr);
    void SetResponseStatus(long responsestatus);
    HRESULT SetOrganizerInfo();
    void SetReminderMinutes(long reminderminutes);
    void SetPrivate(unsigned short usPrivate);
    void SetPlainTextFileAndContent();
    void SetHtmlFileAndContent();
    HRESULT SetAppointmentAttachment(wstring &wstrAttachmentPath);
    bool TextBody(LPTSTR *ppBody, unsigned int &nTextChars);
    bool HtmlBody(LPVOID *ppBody, unsigned int &nHtmlBodyLen);
    LPWSTR WriteContentsToFile(LPTSTR pBody, bool isAscii);

    wstring GetSubject();
    wstring GetStartDate();
    wstring GetEndDate();
    wstring GetInstanceUID();
    wstring GetLocation();
    wstring GetBusyStatus();
    wstring GetAllday();
    wstring GetTransparency();
    wstring GetReminderMinutes();
    wstring GetResponseStatus();
    wstring GetOrganizerName();
    wstring GetOrganizerAddr();
    wstring GetPrivate();
    wstring GetPlainTextFileAndContent();
    wstring GetHtmlFileAndContent();

};
