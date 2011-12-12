#pragma once

DEFINE_GUID(PS_OUTLOOK_APPT, 0x00062002, 0x0000, 0x0000, 0xC0, 0x00, 0x00, 0x00, 0x00, 0x00,
    0x00, 0x46);
DEFINE_GUID(PS_OUTLOOK_MTG, 0x6ED8DA90, 0x450B, 0x101B, 0x98, 0xDA, 0x00, 0xAA, 0x00, 0x3F,
    0x13, 0x05);

enum OutlookBusyStatus
{
    oFree = 0,
    oTentative = 1,
    oBusy = 2,
    oOutOfOffice = 3
};

enum OutlookMeetingRecipientType
{
    oOrganizer = 0,
    oRequired = 1,
    oOptional = 2,
    oResource = 3
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

enum OutlookRecurrenceType
{
    oRecursDaily = 0,
    oRecursWeekly = 1,
    oRecursMonthly = 2,
    oRecursMonthNth = 3,
    oRecursYearly = 5,
    oRecursYearNth = 6
};

enum OutlookRecurrenceEndType
{
    oetNotDefined = 0x00000000,
    oetEndDate	  = 0x00002021,
    oetEndAfterN  = 0x00002022,
    oetNoEnd      = 0x00002023,
    oetNoEnd2     = 0xFFFFFFFF
};

typedef enum
{
    etNotDefined = 0x00000000, etEndDate = 0x00002021, etEndAfterN = 0x00002022, etNoEnd =
        0x00002023, etNoEnd2 = 0xFFFFFFFF
} RecurrenceEndType;

enum OutlookMaskWeekday
{
    wdmUndefined    = 0x00000000,
    wdmSunday       = 0x00000001,
    wdmMonday       = 0x00000002,
    wdmTuesday      = 0x00000004,
    wdmWednesday    = 0x00000008,
    wdmThursday     = 0x00000010,
    wdmFriday       = 0x00000020,
    wdmSaturday     = 0x00000040
};

typedef struct _Organizer
{
    wstring nam;
    wstring addr;
} Organizer;

typedef struct _Attendee
{
    wstring nam;
    wstring addr;
    wstring role;
    wstring partstat;
} Attendee;


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
    ULONG pr_clean_global_objid, pr_appt_start, pr_appt_end, pr_location, pr_busystatus, pr_allday, pr_isrecurring,
	  pr_recurstream, pr_responsestatus, pr_reminderminutes, pr_private;

    // index of props
    typedef enum _AppointmentPropIdx
    {
        N_UID, N_APPTSTART, N_APPTEND, N_LOCATION, N_BUSYSTATUS, N_ALLDAY, N_ISRECUR, N_RECURSTREAM, N_RESPONSESTATUS, N_NUMAPPTPROPS
    } AppointmentPropIdx;

    typedef enum _CommonPropIdx
    {
        N_REMINDERMINUTES, N_PRIVATE, N_NUMCOMMONPROPS
    } CommonPropIdx;

    // this enum lists all the props
    enum
    {
        C_SUBJECT, C_BODY, C_HTMLBODY, C_UID, C_START, C_END, C_LOCATION, C_BUSYSTATUS, C_ALLDAY, C_ISRECUR, C_RECURSTREAM,
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

    bool m_bIsRecurring;

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
    vector<Attendee*> m_vAttendees;
    wstring m_pReminderMinutes;
    wstring m_pPrivate;
    wstring m_pPlainTextFile;
    wstring m_pHtmlFile;

    // recurrence stuff
    wstring m_pRecurPattern;
    wstring m_pRecurInterval;
    wstring m_pRecurWkday;
    wstring m_pRecurEndType;
    wstring m_pRecurCount;
    wstring m_pRecurEndDate;
    wstring m_pRecurDayOfMonth;
    wstring m_pRecurMonthOccurrence;
    //

public:
    MAPIAppointment(Zimbra::MAPI::MAPISession &session, Zimbra::MAPI::MAPIMessage &mMessage);
    ~MAPIAppointment();
    HRESULT InitNamedPropsForAppt();
    HRESULT SetMAPIAppointmentValues();
    void SetSubject(LPTSTR pStr);
    void SetStartDate(FILETIME ft);
    void SetEndDate(FILETIME ft, bool bAllday);
    void SetInstanceUID(LPSBinary bin);
    void SetLocation(LPTSTR pStr);
    void SetBusyStatus(long busystatus);
    void SetAllday(unsigned short usAllday);
    void SetTransparency(LPTSTR pStr);
    void SetResponseStatus(long responsestatus);
    wstring ConvertValueToRole(long role);
    wstring ConvertValueToPartStat(long ps);
    HRESULT SetOrganizerAndAttendees();
    void SetReminderMinutes(long reminderminutes);
    void SetPrivate(unsigned short usPrivate);
    void SetPlainTextFileAndContent();
    void SetHtmlFileAndContent();
    void SetRecurValues();
    HRESULT SetAppointmentAttachment(wstring &wstrAttachmentPath);
    bool TextBody(LPTSTR *ppBody, unsigned int &nTextChars);
    bool HtmlBody(LPVOID *ppBody, unsigned int &nHtmlBodyLen);
    LPWSTR WriteContentsToFile(LPTSTR pBody, bool isAscii);

    bool IsRecurring();

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
    wstring GetRecurPattern();
    wstring GetRecurInterval();
    wstring GetRecurWkday();
    wstring GetRecurEndType();
    wstring GetRecurCount();
    wstring GetRecurEndDate();
    wstring GetRecurDayOfMonth();
    wstring GetRecurMonthOccurrence();
    vector<Attendee*> GetAttendees();

};
