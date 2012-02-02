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

MAPIAppointment::MAPIAppointment(Zimbra::MAPI::MAPISession &session,  Zimbra::MAPI::MAPIMessage &mMessage, int exceptionType)
                                : MAPIRfc2445 (session, mMessage)
{
    m_iExceptionType = exceptionType;
    SetExceptionType(exceptionType);

    //if (MAPIAppointment::m_bNamedPropsInitialized == false)
    //{
	pr_clean_global_objid = 0;
	pr_appt_start = 0;
	pr_appt_end = 0;
	pr_location = 0;
	pr_busystatus = 0;
	pr_allday = 0;
	pr_isrecurring = 0;
	pr_recurstream = 0;
	pr_timezoneid = 0;
	pr_reminderminutes = 0;
	pr_responsestatus = 0;
        pr_exceptionreplacetime = 0;
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

    SetMAPIAppointmentValues();
}

MAPIAppointment::~MAPIAppointment()
{
    if (m_pPropVals)
    {
        MAPIFreeBuffer(m_pPropVals);
    }
    m_pPropVals = NULL;
}

HRESULT MAPIAppointment::InitNamedPropsForAppt()
{
    // init named props
    nameIds[0] = 0x0023;
    nameIds[1] = 0x820D;
    nameIds[2] = 0x820E;
    nameIds[3] = 0x8208;
    nameIds[4] = 0x8205;
    nameIds[5] = 0x8215;
    nameIds[6] = 0x8223;
    nameIds[7] = 0x8216;
    nameIds[8] = 0x8234;
    nameIds[9] = 0x8218;
    nameIds[10] = 0x8228;

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
        ppNames[i]->lpguid = (i == N_UID) ? (LPGUID)(&PS_OUTLOOK_MTG) : (LPGUID)(&PS_OUTLOOK_APPT);
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
    pr_clean_global_objid = SetPropType(pAppointmentTags->aulPropTag[N_UID], PT_BINARY);
    pr_appt_start = SetPropType(pAppointmentTags->aulPropTag[N_APPTSTART], PT_SYSTIME);
    pr_appt_end = SetPropType(pAppointmentTags->aulPropTag[N_APPTEND], PT_SYSTIME);
    pr_location = SetPropType(pAppointmentTags->aulPropTag[N_LOCATION], PT_TSTRING);
    pr_busystatus = SetPropType(pAppointmentTags->aulPropTag[N_BUSYSTATUS], PT_LONG);
    pr_allday = SetPropType(pAppointmentTags->aulPropTag[N_ALLDAY], PT_BOOLEAN);
    pr_isrecurring = SetPropType(pAppointmentTags->aulPropTag[N_ISRECUR], PT_BOOLEAN);
    pr_recurstream = SetPropType(pAppointmentTags->aulPropTag[N_RECURSTREAM], PT_BINARY);
    pr_timezoneid = SetPropType(pAppointmentTags->aulPropTag[N_TIMEZONEID], PT_TSTRING);
    pr_responsestatus = SetPropType(pAppointmentTags->aulPropTag[N_RESPONSESTATUS], PT_LONG);
    pr_exceptionreplacetime = SetPropType(pAppointmentTags->aulPropTag[N_EXCEPTIONREPLACETIME], PT_SYSTIME);
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

HRESULT MAPIAppointment::SetMAPIAppointmentValues()
{
    SizedSPropTagArray(C_NUMALLAPPTPROPS, appointmentProps) = {
	C_NUMALLAPPTPROPS, {
	    PR_SUBJECT, PR_BODY, PR_HTML, pr_clean_global_objid,
	    pr_appt_start, pr_appt_end, pr_location, pr_busystatus, pr_allday,
	    pr_isrecurring, pr_recurstream, pr_timezoneid, pr_responsestatus,
            pr_exceptionreplacetime,
	    pr_reminderminutes, pr_private
	}
    };

    HRESULT hr = S_OK;
    ULONG cVals = 0;
    bool bAllday = false;
    m_bIsRecurring = false;

    if (FAILED(hr = m_pMessage->GetProps((LPSPropTagArray) & appointmentProps, fMapiUnicode, &cVals,
            &m_pPropVals)))
        throw MAPIAppointmentException(hr, L"SetMAPIAppointmentValues(): GetProps Failed.", __LINE__, __FILE__);

    if (m_pPropVals[C_ISRECUR].ulPropTag == appointmentProps.aulPropTag[C_ISRECUR]) // do this first to set dates correctly
    {
	m_bIsRecurring = (m_pPropVals[C_ISRECUR].Value.b == 1);
    }
    if (m_pPropVals[C_ALLDAY].ulPropTag == appointmentProps.aulPropTag[C_ALLDAY])
    {
        SetAllday(m_pPropVals[C_ALLDAY].Value.b);
        bAllday = (m_pPropVals[C_ALLDAY].Value.b == 1);
    }
    if (m_pPropVals[C_SUBJECT].ulPropTag == appointmentProps.aulPropTag[C_SUBJECT])
    {
	SetSubject(m_pPropVals[C_SUBJECT].Value.lpszW);
    }
    if (m_pPropVals[C_UID].ulPropTag == appointmentProps.aulPropTag[C_UID])
    {
	SetInstanceUID(&m_pPropVals[C_UID].Value.bin);
    }
    if (m_pPropVals[C_START].ulPropTag == appointmentProps.aulPropTag[C_START])
    {
	SetStartDate(m_pPropVals[C_START].Value.ft);
    }
    if (m_pPropVals[C_END].ulPropTag == appointmentProps.aulPropTag[C_END])
    {
        SetEndDate(m_pPropVals[C_END].Value.ft, bAllday);
    }
    if (m_pPropVals[C_LOCATION].ulPropTag == appointmentProps.aulPropTag[C_LOCATION])
    {
	SetLocation(m_pPropVals[C_LOCATION].Value.lpszW);
    }
    if (m_pPropVals[C_BUSYSTATUS].ulPropTag == appointmentProps.aulPropTag[C_BUSYSTATUS])
    {
	SetBusyStatus(m_pPropVals[C_BUSYSTATUS].Value.l);
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

    SetOrganizerAndAttendees();

    if ((m_bIsRecurring) && (m_iExceptionType != CANCEL_EXCEPTION))
    {
	if (m_pPropVals[C_RECURSTREAM].ulPropTag == appointmentProps.aulPropTag[C_RECURSTREAM])
	{
	    // special case for timezone id
	    if (m_pPropVals[C_TIMEZONEID].ulPropTag == appointmentProps.aulPropTag[C_TIMEZONEID])
	    {
		SetTimezoneId(m_pPropVals[C_TIMEZONEID].Value.lpszW);
	    }
	    //

	    int numExceptions = SetRecurValues(); // returns null if no exceptions
            if (numExceptions > 0)
            {
                SetExceptions();
            }
	}
    }
    return hr;
}

void MAPIAppointment::SetTimezoneId(LPTSTR pStr)
{
    m_pTimezoneId = pStr;
    size_t nPos = m_pTimezoneId.find(_T(":"), 0);
    if (nPos != std::string::npos)
    {
	m_pTimezoneId.replace(nPos, 1, L".");
    }
}

 int MAPIAppointment::SetRecurValues()
{
    Zimbra::Util::ScopedInterface<IStream> pRecurrenceStream;
    HRESULT hResult = m_pMessage->OpenProperty(pr_recurstream, &IID_IStream, 0, 0,
						(LPUNKNOWN *)pRecurrenceStream.getptr());
    if (FAILED(hResult))
    {
	return 0;
    }
    LPSTREAM pStream = pRecurrenceStream.get();
    Zimbra::Mapi::Appt OlkAppt(m_pMessage, NULL);
    Zimbra::Mapi::COutlookRecurrencePattern &recur = OlkAppt.GetRecurrencePattern();
    hResult = recur.ReadRecurrenceStream(pStream);
    if (FAILED(hResult))
    {
	return 0;
    }

    // Set Timezone info
    SYSTEMTIME stdTime;
    SYSTEMTIME dsTime;
    const Zimbra::Mail::TimeZone &tzone = recur.GetTimeZone();
    m_timezone.id = m_pTimezoneId;  // don't use m_timezone.id = tzone.GetId()
    IntToWstring(tzone.GetStandardOffset(), m_timezone.standardOffset);
    IntToWstring(tzone.GetDaylightOffset(), m_timezone.daylightOffset);
    tzone.GetStandardStart(stdTime);
    tzone.GetDaylightStart(dsTime);
    IntToWstring(stdTime.wDay, m_timezone.standardStartWeek);
    IntToWstring(stdTime.wDayOfWeek + 1, m_timezone.standardStartWeekday);  // note the + 1 -- bumping weekday
    IntToWstring(stdTime.wMonth, m_timezone.standardStartMonth);
    IntToWstring(stdTime.wHour, m_timezone.standardStartHour);
    IntToWstring(stdTime.wMinute, m_timezone.standardStartMinute);
    IntToWstring(stdTime.wSecond, m_timezone.standardStartSecond);
    IntToWstring(dsTime.wDay, m_timezone.daylightStartWeek);
    IntToWstring(dsTime.wDayOfWeek + 1, m_timezone.daylightStartWeekday);   // note the + 1 -- bumping weekday
    IntToWstring(dsTime.wMonth, m_timezone.daylightStartMonth);
    IntToWstring(dsTime.wHour, m_timezone.daylightStartHour);
    IntToWstring(dsTime.wMinute, m_timezone.daylightStartMinute);
    IntToWstring(dsTime.wSecond, m_timezone.daylightStartSecond);
    //

    ULONG ulType = recur.GetRecurrenceType();
    switch (ulType)
    {
	case oRecursDaily:
	    m_pRecurPattern = L"DAI";
	    break;
	case oRecursWeekly:
	    m_pRecurPattern = L"WEE";
	    break;
	case oRecursMonthly:
	case oRecursMonthNth:
	    m_pRecurPattern = L"MON";
	    break;
	case oRecursYearly:
	case oRecursYearNth:
	    m_pRecurPattern = L"YEA";
	    break;
	default: ;
    }
    IntToWstring(recur.GetInterval(), m_pRecurInterval);

    ULONG ulDayOfWeekMask = recur.GetDayOfWeekMask();
    if (ulDayOfWeekMask & wdmSunday)    m_pRecurWkday += L"SU";
    if (ulDayOfWeekMask & wdmMonday)    m_pRecurWkday += L"MO";
    if (ulDayOfWeekMask & wdmTuesday)   m_pRecurWkday += L"TU";
    if (ulDayOfWeekMask & wdmWednesday) m_pRecurWkday += L"WE";
    if (ulDayOfWeekMask & wdmThursday)  m_pRecurWkday += L"TH";
    if (ulDayOfWeekMask & wdmFriday)    m_pRecurWkday += L"FR";
    if (ulDayOfWeekMask & wdmSaturday)  m_pRecurWkday += L"SA";

    if ((m_pRecurPattern == L"DAI") && (m_pRecurWkday.length() > 0))	// every weekday
    {
	m_pRecurPattern = L"WEE";
    }

    if (m_pRecurPattern == L"MON")
    {
	if (ulType == oRecursMonthly)
	{
	    IntToWstring(recur.GetDayOfMonth(), m_pRecurDayOfMonth);
	}
	else
	if (ulType == oRecursMonthNth)
	{
	    ULONG ulMonthOccurrence = recur.GetInstance();
	    if (ulMonthOccurrence == 5)	    // last
	    {
		m_pRecurMonthOccurrence = L"-1";
	    }
	    else
	    {
		IntToWstring(ulMonthOccurrence, m_pRecurMonthOccurrence);
	    }
	}
    }

    if (m_pRecurPattern == L"YEA")
    {
	ULONG ulMonthOfYear = recur.GetMonthOfYear();
	IntToWstring(ulMonthOfYear, m_pRecurMonthOfYear);
	if (ulType == oRecursYearly)
	{
	    IntToWstring(recur.GetDayOfMonth(), m_pRecurDayOfMonth);
	}
	else
	if (ulType == oRecursYearNth)
	{
	    ULONG ulMonthOccurrence = recur.GetInstance();
	    if (ulMonthOccurrence == 5)	    // last
	    {
		m_pRecurMonthOccurrence = L"-1";
	    }
	    else
	    {
		IntToWstring(ulMonthOccurrence, m_pRecurMonthOccurrence);
	    }
	}
    }

    ULONG ulRecurrenceEndType = recur.GetEndType();
    if (ulRecurrenceEndType == oetEndAfterN)
    {
	IntToWstring(recur.GetOccurrences(), m_pRecurCount);
    }
    else
    if (ulRecurrenceEndType == oetEndDate)
    {
	SYSTEMTIME st;
	Zimbra::Mapi::CRecurrenceTime rtEndDate = recur.GetEndDate();
	Zimbra::Mapi::CFileTime ft = (FILETIME)rtEndDate;
	FileTimeToSystemTime(&ft, &st);
	wstring temp = Zimbra::Util::FormatSystemTime(st, TRUE, TRUE);
	m_pRecurEndDate = temp.substr(0, 8);
    }
    return recur.GetExceptionCount();  
}

void MAPIAppointment::SetExceptions()
{
    Zimbra::Util::ScopedInterface<IStream> pRecurrenceStream;
    HRESULT hResult = m_pMessage->OpenProperty(pr_recurstream, &IID_IStream, 0, 0,
						(LPUNKNOWN *)pRecurrenceStream.getptr());
    if (FAILED(hResult))
    {
	return;
    }
    LPSTREAM pStream = pRecurrenceStream.get();
    Zimbra::Mapi::Appt OlkAppt(m_pMessage, NULL);
    Zimbra::Mapi::COutlookRecurrencePattern &recur = OlkAppt.GetRecurrencePattern();
    hResult = recur.ReadRecurrenceStream(pStream);
    if (FAILED(hResult))
    {
	return;
    }
    LONG lExceptionCount = recur.GetExceptionCount();

    for (LONG i = 0; i < lExceptionCount; i++)
    {
        Zimbra::Mapi::CRecurrenceTime rtDate = recur.GetExceptionOriginalDate(i);
        Zimbra::Mapi::CFileTime ftOrigDate = (FILETIME)rtDate;

        Zimbra::Mapi::COutlookRecurrenceException *lpException = recur.GetException(i);
        if (lpException != NULL)    
        {
            Zimbra::Util::ScopedInterface<IMessage> lpExceptionMessage;
            Zimbra::Util::ScopedInterface<IAttach> lpExceptionAttach;
            HRESULT hResult = lpException->OpenAppointment((LPMESSAGE)OlkAppt.MapiMsg(),
                lpExceptionMessage.getptr(), lpExceptionAttach.getptr(), pr_exceptionreplacetime);

            if (FAILED(hResult))
            {
                //dlogd(L"could not open appointment message for this occurrence"));
                return;
            }

            // We have everything for the object
            Zimbra::Mapi::Appt pOccurrence(lpExceptionMessage.get(), OlkAppt.GetStore(),
                                           lpException, lpExceptionAttach.get(),
                                           OlkAppt.MapiMsg());
            MAPIMessage exMAPIMsg;
            exMAPIMsg.Initialize(lpExceptionMessage.get(), *m_session);
            MAPIAppointment* pEx = new MAPIAppointment(*m_session, exMAPIMsg, NORMAL_EXCEPTION);   // delete done in CMapiAccessWrap::GetData
            FillInExceptionAppt(pEx, lpException);
            m_vExceptions.push_back(pEx);
        }
        else
        {
            MAPIAppointment* pEx = new MAPIAppointment(*m_session, *m_mapiMessage, CANCEL_EXCEPTION);
            FillInCancelException(pEx, ftOrigDate);
            m_vExceptions.push_back(pEx);
        }
    }
}

void MAPIAppointment::FillInExceptionAppt(MAPIAppointment* pEx, Zimbra::Mapi::COutlookRecurrenceException* lpException)
{
    // PST seems to find the MAPI message, thereby eventually filling in the pEx.  Server does not, but the
    // info is in the lpException
    if (pEx->m_pStartDate.length() == 0)
    {
        Zimbra::Mapi::CRecurrenceTime rtStartDate = lpException->GetStartDateTime();
        Zimbra::Mapi::CFileTime ftStartDate = (FILETIME)rtStartDate;
        pEx->m_pStartDate = MakeDateFromExPtr(ftStartDate);
        pEx->m_pStartDateCommon = Zimbra::MAPI::Util::CommonDateString(ftStartDate);

    }
    if (pEx->m_pEndDate.length() == 0)
    {
        Zimbra::Mapi::CRecurrenceTime rtEndDate = lpException->GetEndDateTime();
        Zimbra::Mapi::CFileTime ftEndDate = (FILETIME)rtEndDate;
        pEx->m_pEndDate = MakeDateFromExPtr(ftEndDate);
    }
    if (pEx->m_pSubject.length() == 0)
    {
        pEx->m_pSubject = (wcslen(lpException->GetSubject()) > 0) ? lpException->GetSubject() : m_pSubject;
    }
    if (pEx->m_pLocation.length() == 0)
    {
        pEx->m_pLocation = (wcslen(lpException->GetLocation()) > 0) ? lpException->GetLocation() : m_pLocation;
    }
    if (pEx->m_pBusyStatus.length() == 0)
    {
        if (this->InterpretBusyStatus() !=  lpException->GetBusyStatus())
        {
            pEx->SetBusyStatus(lpException->GetBusyStatus());
        }
        else
        {
            pEx->m_pBusyStatus = m_pBusyStatus;
        }
    }
    if (pEx->m_pAllday.length() == 0)
    {
        if (this->InterpretAllday() != lpException->GetAllDay())
        {
            pEx->SetAllday((unsigned short)lpException->GetAllDay());
        }
        else
        {
            pEx->m_pAllday = m_pAllday;
        }
    }
    if (pEx->m_pResponseStatus.length() == 0)
    {
        pEx->m_pResponseStatus = m_pResponseStatus;
    }
    if (pEx->m_pOrganizerName.length() == 0)
    {
        pEx->m_pOrganizerName = m_pOrganizerName;
    }
    if (pEx->m_pOrganizerAddr.length() == 0)
    {
        pEx->m_pOrganizerAddr = m_pOrganizerAddr;
    }

    // attendees?

    if (pEx->m_pReminderMinutes.length() == 0)
    {
        pEx->m_pReminderMinutes = m_pReminderMinutes;
    }
    if (pEx->m_pPrivate.length() == 0)
    {
        pEx->m_pPrivate = m_pPrivate;
    }
    if (pEx->m_pPlainTextFile.length() == 0)
    {
        pEx->m_pPlainTextFile = m_pPlainTextFile;
    }
    if (pEx->m_pHtmlFile.length() == 0)
    {
        pEx->m_pHtmlFile = m_pHtmlFile;
    }
}

void MAPIAppointment::FillInCancelException(MAPIAppointment* pEx, Zimbra::Mapi::CFileTime cancelDate)
{
    // should really use a copy constructor
    pEx->m_pStartDate = MakeDateFromExPtr(cancelDate);
    pEx->m_pSubject = m_pSubject;
    pEx->m_pLocation = m_pLocation;
    pEx->m_pBusyStatus = m_pBusyStatus;
    pEx->m_pAllday = m_pAllday;
    pEx->m_pResponseStatus = m_pResponseStatus;
    pEx->m_pOrganizerName = m_pOrganizerName;
    pEx->m_pOrganizerAddr = m_pOrganizerAddr;
    pEx->m_pReminderMinutes = m_pReminderMinutes;
    pEx->m_pPrivate = m_pPrivate;
    pEx->m_pPlainTextFile = m_pPlainTextFile;
    pEx->m_pHtmlFile = m_pHtmlFile;
}

void MAPIAppointment::SetSubject(LPTSTR pStr)
{
    m_pSubject = pStr;
}

void MAPIAppointment::SetStartDate(FILETIME ft)
{
    SYSTEMTIME st, localst;
    BOOL bUseLocal = false;

    FileTimeToSystemTime(&ft, &st);
    if ((m_bIsRecurring) || (m_iExceptionType == NORMAL_EXCEPTION))
    {
	TIME_ZONE_INFORMATION localTimeZone = {0};
	GetTimeZoneInformation(&localTimeZone);	
	bUseLocal = SystemTimeToTzSpecificLocalTime(&localTimeZone, &st, &localst);
    }
    m_pStartDate = (bUseLocal) ? Zimbra::Util::FormatSystemTime(localst, FALSE, TRUE)
			       : Zimbra::Util::FormatSystemTime(st, TRUE, TRUE);
    m_pStartDateCommon = Zimbra::MAPI::Util::CommonDateString(m_pPropVals[C_START].Value.ft);   // may have issue with recur/local
}

LPWSTR MAPIAppointment::MakeDateFromExPtr(FILETIME ft)
{
    SYSTEMTIME st;

    FileTimeToSystemTime(&ft, &st);
    return Zimbra::Util::FormatSystemTime(st, FALSE, TRUE);			       
}

void MAPIAppointment::SetEndDate(FILETIME ft, bool bAllday)
{
    SYSTEMTIME st, localst;
    BOOL bUseLocal = false;

    FileTimeToSystemTime(&ft, &st);

    if (bAllday)    // if AllDay appt, subtract one from the end date for Zimbra friendliness
    {
	double dat = -1;
	if (SystemTimeToVariantTime(&st, &dat))
	{
	    dat -= 1;
	    VariantTimeToSystemTime(dat, &st);
	}
    }
    else
    {
	if ((m_bIsRecurring) || (m_iExceptionType == NORMAL_EXCEPTION))
	{
	    TIME_ZONE_INFORMATION localTimeZone = {0};
	    GetTimeZoneInformation(&localTimeZone);	
	    bUseLocal = SystemTimeToTzSpecificLocalTime(&localTimeZone, &st, &localst);
	}
    }
    m_pEndDate = (bUseLocal) ? Zimbra::Util::FormatSystemTime(localst, FALSE, TRUE)
			     : Zimbra::Util::FormatSystemTime(st, TRUE, TRUE);
}

void MAPIAppointment::SetInstanceUID(LPSBinary bin)
{
    Zimbra::Util::ScopedArray<CHAR> spUid(new CHAR[(bin->cb * 2) + 1]);
    if (spUid.get() != NULL)
    {
	Zimbra::Util::HexFromBin(bin->lpb, bin->cb, spUid.get());
    }
    m_pInstanceUID = Zimbra::Util::AnsiiToUnicode(spUid.get());
}

void MAPIAppointment::SetLocation(LPTSTR pStr)
{
    m_pLocation = pStr;
}

void MAPIAppointment::SetBusyStatus(long busystatus)
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

ULONG MAPIAppointment::InterpretBusyStatus()
{
    long retval;
    if (m_pBusyStatus == L"F") 
    {
        retval = oFree;
    }
    if (m_pBusyStatus == L"T") 
    {
        retval = oTentative;
    }
    if (m_pBusyStatus == L"B") 
    {
        retval = oBusy;
    }
    if (m_pBusyStatus == L"O") 
    {
        retval = oOutOfOffice;
    }
    else
    {
        retval = oFree;
    }
    return retval;
}

wstring MAPIAppointment::ConvertValueToRole(long role)
{
    wstring retval = L"REQ";
    switch (role)
    {
	case oOrganizer:    retval = L"CHAIR";	break;
	case oRequired:	    retval = L"REQ";	break;
	case oOptional:	    retval = L"OPT";	break;
	case oResource:	    retval = L"NON";	break;
	default:	    ;
    }
    return retval;
}

wstring MAPIAppointment::ConvertValueToPartStat(long ps)
{
    wstring retval = L"NE";
    switch (ps)
    {
	case oResponseNone:	    retval = L"NE";	break;
	case oResponseOrganized:    retval = L"OR";	break;
	case oResponseTentative:    retval = L"TE";	break;
	case oResponseAccepted:	    retval = L"AC";	break;
	case oResponseDeclined:	    retval = L"DE";	break;
	case oResponseNotResponded: retval = L"NE";	break;
	default:		    ;
    }
    return retval;
}

void MAPIAppointment::SetAllday(unsigned short usAllday)
{
    m_pAllday = (usAllday == 1) ? L"1" : L"0";
}

BOOL MAPIAppointment::InterpretAllday()
{
    return (m_pAllday == L"1");
}

void MAPIAppointment::SetTransparency(LPTSTR pStr)
{
    m_pTransparency = pStr;
}

void MAPIAppointment::SetResponseStatus(long responsestatus)
{
    switch (responsestatus)
    {
	case oResponseNone:		m_pResponseStatus = L"NE";	break;
	case oResponseOrganized:	m_pResponseStatus = L"OR";	break;	    // OR????  -- temporary
	case oResponseTentative:	m_pResponseStatus = L"TE";	break;
	case oResponseAccepted:		m_pResponseStatus = L"AC";	break;
	case oResponseDeclined:		m_pResponseStatus = L"DE";	break;
	case oResponseNotResponded:	m_pResponseStatus = L"NE";	break;
	default:			m_pResponseStatus = L"NE";
    }
}

void MAPIAppointment::SetReminderMinutes(long reminderminutes)
{
    WCHAR pwszTemp[10];
    _ltow(reminderminutes, pwszTemp, 10);
    m_pReminderMinutes = pwszTemp;
}

void MAPIAppointment::SetPrivate(unsigned short usPrivate)
{
    m_pPrivate = (usPrivate == 1) ? L"1" : L"0";
}

void MAPIAppointment::SetPlainTextFileAndContent()
{
    m_pPlainTextFile = Zimbra::MAPI::Util::SetPlainText(m_pMessage, &m_pPropVals[C_BODY]);
}

void MAPIAppointment::SetHtmlFileAndContent()
{
    m_pHtmlFile = Zimbra::MAPI::Util::SetHtml(m_pMessage, &m_pPropVals[C_HTMLBODY]);
}

void MAPIAppointment::SetExceptionType(int type)
{
    if (type == NORMAL_EXCEPTION)
    {
        m_pExceptionType = L"except";
    }
    else
    if (type == CANCEL_EXCEPTION)
    {
        m_pExceptionType = L"cancel";
    }
    else
    {
        m_pExceptionType = L"none";
    }
}

HRESULT MAPIAppointment::SetOrganizerAndAttendees()
{
    Zimbra::Util::ScopedInterface<IMAPITable> pRecipTable;
    HRESULT hr = 0;

    hr = m_pMessage->GetRecipientTable(fMapiUnicode, pRecipTable.getptr());
    if (FAILED(hr))
    {
        return hr;
    }

    typedef enum _AttendeePropTagIdx
    {
        AT_DISPLAY_NAME, AT_SMTP_ADDR, AT_RECIPIENT_FLAGS, AT_RECIPIENT_TYPE, AT_RECIPIENT_TRACKSTATUS, AT_NPROPS
    } AttendeePropTagIdx;

    SizedSPropTagArray(5, reciptags) = {
        5, { PR_DISPLAY_NAME_W, PR_SMTP_ADDRESS_W, PR_RECIPIENT_FLAGS, PR_RECIPIENT_TYPE, PR_RECIPIENT_TRACKSTATUS }
    };

    ULONG ulRows = 0;
    Zimbra::Util::ScopedRowSet pRecipRows;

    hr = pRecipTable->SetColumns((LPSPropTagArray) & reciptags, 0);
    if (FAILED(hr))
    {
	//LOG_ERROR(_T("could not get the recipient table, hr: %x"), hr);
        return hr;
    }
    hr = pRecipTable->GetRowCount(0, &ulRows);
    if (FAILED(hr))
    {
	//LOG_ERROR(_T("could not get the recipient table row count, hr: %x"), hr);
        return hr;
    }
    hr = pRecipTable->QueryRows(ulRows, 0, pRecipRows.getptr());
    if (FAILED(hr))
    {
        //LOG_ERROR(_T("Failed to query table rows. hr: %x"), hr);
        return hr;
    }
    if (pRecipRows != NULL)
    {
	for (ULONG iRow = 0; iRow < pRecipRows->cRows; iRow++)
        {
	    if (pRecipRows->aRow[iRow].lpProps[AT_RECIPIENT_FLAGS].ulPropTag ==
                reciptags.aulPropTag[AT_RECIPIENT_FLAGS])
            {
                if (pRecipRows->aRow[iRow].lpProps[AT_RECIPIENT_FLAGS].Value.l == 3)
		{
                    if (PROP_TYPE(pRecipRows->aRow[iRow].lpProps[AT_DISPLAY_NAME].ulPropTag) != PT_ERROR)
                    {
		        m_pOrganizerName = pRecipRows->aRow[iRow].lpProps[AT_DISPLAY_NAME].Value.lpszW;
                    }
                    if (PROP_TYPE(pRecipRows->aRow[iRow].lpProps[AT_SMTP_ADDR].ulPropTag) != PT_ERROR)
                    {
		        m_pOrganizerAddr = pRecipRows->aRow[iRow].lpProps[AT_SMTP_ADDR].Value.lpszW;
                    }
		}
		else
		{
		    Attendee* pAttendee = new Attendee();   // delete done in CMapiAccessWrap::GetData after we allocate dict string for ZimbraAPI
		    pAttendee->nam = pRecipRows->aRow[iRow].lpProps[AT_DISPLAY_NAME].Value.lpszW;
		    pAttendee->addr = pRecipRows->aRow[iRow].lpProps[AT_SMTP_ADDR].Value.lpszW;
		    pAttendee->role = ConvertValueToRole(pRecipRows->aRow[iRow].lpProps[AT_RECIPIENT_TYPE].Value.l);
		    pAttendee->partstat = ConvertValueToPartStat(pRecipRows->aRow[iRow].lpProps[AT_RECIPIENT_TRACKSTATUS].Value.l);
		    m_vAttendees.push_back(pAttendee);
		}
	    }
        }
    }
    return hr;
}

HRESULT MAPIAppointment::SetAppointmentAttachment(wstring &wstrAttachmentPath)
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

wstring MAPIAppointment::GetSubject() { return m_pSubject; }
wstring MAPIAppointment::GetStartDate() { return m_pStartDate; }
wstring MAPIAppointment::GetStartDateCommon() { return m_pStartDateCommon; }
wstring MAPIAppointment::GetEndDate() { return m_pEndDate; }
wstring MAPIAppointment::GetInstanceUID() { return m_pInstanceUID; }
wstring MAPIAppointment::GetLocation() { return m_pLocation; }
wstring MAPIAppointment::GetBusyStatus() { return m_pBusyStatus; }
wstring MAPIAppointment::GetAllday() { return m_pAllday; }
wstring MAPIAppointment::GetTransparency() { return m_pTransparency; }
wstring MAPIAppointment::GetReminderMinutes() { return m_pReminderMinutes; }
wstring MAPIAppointment::GetResponseStatus() { return m_pResponseStatus; }
wstring MAPIAppointment::GetOrganizerName() { return m_pOrganizerName; }
wstring MAPIAppointment::GetOrganizerAddr() { return m_pOrganizerAddr; }
wstring MAPIAppointment::GetPrivate() { return m_pPrivate; }
wstring MAPIAppointment::GetPlainTextFileAndContent() { return m_pPlainTextFile; }
wstring MAPIAppointment::GetHtmlFileAndContent() { return m_pHtmlFile; }
vector<Attendee*> MAPIAppointment::GetAttendees() { return m_vAttendees; }
vector<MAPIAppointment*> MAPIAppointment::GetExceptions() { return m_vExceptions; }
wstring MAPIAppointment::GetExceptionType() { return m_pExceptionType; }
