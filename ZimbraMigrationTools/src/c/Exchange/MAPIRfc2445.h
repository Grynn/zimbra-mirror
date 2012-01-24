#pragma once

DEFINE_GUID(PS_OUTLOOK_APPT, 0x00062002, 0x0000, 0x0000, 0xC0, 0x00, 0x00, 0x00, 0x00, 0x00,
    0x00, 0x46);
DEFINE_GUID(PS_OUTLOOK_MTG, 0x6ED8DA90, 0x450B, 0x101B, 0x98, 0xDA, 0x00, 0xAA, 0x00, 0x3F,
    0x13, 0x05);
DEFINE_GUID(PS_OUTLOOK_TASK, 0x00062003, 0x0000, 0x0000, 0xC0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x46);

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

typedef struct _Tz
{
    wstring id;
    wstring standardOffset;
    wstring daylightOffset;
    wstring standardStartWeek;
    wstring standardStartWeekday;
    wstring standardStartMonth;
    wstring standardStartHour;
    wstring standardStartMinute;
    wstring standardStartSecond;
    wstring daylightStartWeek;
    wstring daylightStartWeekday;
    wstring daylightStartMonth;
    wstring daylightStartHour;
    wstring daylightStartMinute;
    wstring daylightStartSecond;
} Tz;

enum OutlookTaskStatus
{
    oTaskNotStarted = 0,
    oTaskInProgress = 1,
    oTaskComplete = 2,
    oTaskWaiting = 3,
    oTaskDeferred = 4
};

enum OutlookImportance
{
    oImportanceLow = 0,
    oImportanceNormal = 1,
    oImportanceHigh = 2
};


class MAPIRfc2445
{
protected:
    Zimbra::MAPI::MAPIMessage *m_mapiMessage;
    Zimbra::MAPI::MAPISession *m_session;
    LPMESSAGE m_pMessage;
    LPSPropValue m_pPropVals;
    bool m_bIsRecurring;

    // recurrence stuff
    wstring m_pRecurPattern;
    wstring m_pRecurInterval;
    wstring m_pRecurWkday;
    wstring m_pRecurEndType;
    wstring m_pRecurCount;
    wstring m_pRecurEndDate;
    wstring m_pRecurDayOfMonth;
    wstring m_pRecurMonthOccurrence;
    wstring m_pRecurMonthOfYear;
    wstring m_pTimezoneId;
    Tz m_timezone;

    void IntToWstring(int src, wstring& dest);

public:
    MAPIRfc2445(Zimbra::MAPI::MAPISession &session, Zimbra::MAPI::MAPIMessage &mMessage);
    ~MAPIRfc2445();

    bool IsRecurring();
    wstring GetRecurPattern();
    wstring GetRecurInterval();
    wstring GetRecurWkday();
    wstring GetRecurEndType();
    wstring GetRecurCount();
    wstring GetRecurEndDate();
    wstring GetRecurDayOfMonth();
    wstring GetRecurMonthOccurrence();
    wstring GetRecurMonthOfYear();
    Tz GetRecurTimezone();
};
