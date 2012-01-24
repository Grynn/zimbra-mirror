#include "common.h"
#include "Exchange.h"
#include "MAPIMessage.h"

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

