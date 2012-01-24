#include "common.h"
#include "Exchange.h"
#include "MAPIMessage.h"
#include "MAPIRfc2445.h"
#include "MAPITask.h"

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MAPIAppointmentException
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPITaskException::MAPITaskException(HRESULT hrErrCode, LPCWSTR
    lpszDescription): GenericException(hrErrCode, lpszDescription)
{
    //
}

MAPITaskException::MAPITaskException(HRESULT hrErrCode, LPCWSTR lpszDescription, int
    nLine, LPCSTR strFile): GenericException(hrErrCode, lpszDescription, nLine, strFile)
{
    //
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MAPITask
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//bool MAPITask::m_bNamedPropsInitialized = false;

MAPITask::MAPITask(Zimbra::MAPI::MAPISession &session, Zimbra::MAPI::MAPIMessage &mMessage)
                  : MAPIRfc2445 (session, mMessage)
{
    //if (MAPITask::m_bNamedPropsInitialized == false)
    //{
        pr_isrecurringt = 0;
	pr_status = 0;
	pr_percentcomplete = 0;
	pr_taskstart = 0;
	pr_taskdue = 0;
	pr_totalwork = 0;
	pr_actualwork = 0;
	pr_companies = 0;
	pr_mileage = 0;
	pr_billinginfo = 0;
	InitNamedPropsForTask();
    //}

    m_pSubject = L"";
    m_pImportance = L"";
    m_pTaskStart = L"";
    m_pTaskDue = L"";
    m_pStatus = L"";
    m_pPercentComplete = L"";
    m_pTotalWork = L"";
    m_pActualWork = L"";
    m_pCompanies = L"";
    m_pMileage = L"";
    m_pBillingInfo = L"";

    SetMAPITaskValues();
}

MAPITask::~MAPITask()
{
    if (m_pPropVals)
    {
        MAPIFreeBuffer(m_pPropVals);
    }
    m_pPropVals = NULL;
}

HRESULT MAPITask::InitNamedPropsForTask()
{
    // init named props
    nameIds[0] = 0x8126;
    nameIds[1] = 0x8101;
    nameIds[2] = 0x8102;
    nameIds[3] = 0x8104;
    nameIds[4] = 0x8105;
    nameIds[5] = 0x8111;
    nameIds[6] = 0x8110;

    nameIdsC[0] = 0x8539;
    nameIdsC[1] = 0x8534;
    nameIdsC[2] = 0x8535;

    HRESULT hr = S_OK;
    Zimbra::Util::ScopedBuffer<SPropValue> pPropValMsgClass;

    if (FAILED(hr = HrGetOneProp(m_pMessage, PR_MESSAGE_CLASS, pPropValMsgClass.getptr())))
        throw MAPITaskException(hr, L"InitNamedPropsForTask(): HrGetOneProp Failed.", __LINE__, __FILE__);

    // initialize the MAPINAMEID structure GetIDsFromNames requires
    LPMAPINAMEID ppNames[N_NUMTASKPROPS] = { 0 };
    for (int i = 0; i < N_NUMTASKPROPS; i++)
    {
        MAPIAllocateBuffer(sizeof (MAPINAMEID), (LPVOID *)&(ppNames[i]));
        ppNames[i]->ulKind = MNID_ID;
        ppNames[i]->lpguid = (LPGUID)(&PS_OUTLOOK_TASK);
        ppNames[i]->Kind.lID = nameIds[i];
    }

    LPMAPINAMEID ppNamesC[N_NUMCOMMONTPROPS] = { 0 };
    for (int i = 0; i < N_NUMCOMMONTPROPS; i++)
    {
        MAPIAllocateBuffer(sizeof (MAPINAMEID), (LPVOID *)&(ppNamesC[i]));
        ppNamesC[i]->ulKind = MNID_ID;
        ppNamesC[i]->lpguid = (LPGUID)(&PS_OUTLOOK_COMMON);
        ppNamesC[i]->Kind.lID = nameIdsC[i];
    }

    // get the real prop tag ID's
    LPSPropTagArray pTaskTags  = NULL;
    LPSPropTagArray pTaskTagsC = NULL;

    if (FAILED(hr = m_pMessage->GetIDsFromNames(N_NUMTASKPROPS, ppNames, MAPI_CREATE,
            &pTaskTags)))
        throw MAPITaskException(hr, L"Init(): GetIDsFromNames on pTaskTags Failed.", __LINE__, __FILE__);

    if (FAILED(hr = m_pMessage->GetIDsFromNames(N_NUMCOMMONTPROPS, ppNamesC, MAPI_CREATE,
            &pTaskTagsC)))
        throw MAPITaskException(hr, L"Init(): GetIDsFromNames on pAppointmentTagsC Failed.", __LINE__, __FILE__);

    // give the prop tag ID's a type
    pr_isrecurringt = SetPropType(pTaskTags->aulPropTag[N_ISRECURT], PT_BOOLEAN);
    pr_status = SetPropType(pTaskTags->aulPropTag[N_STATUS], PT_LONG);
    pr_percentcomplete = SetPropType(pTaskTags->aulPropTag[N_PERCENTCOMPLETE], PT_DOUBLE);
    pr_taskstart = SetPropType(pTaskTags->aulPropTag[N_TASKSTART], PT_SYSTIME);
    pr_taskdue = SetPropType(pTaskTags->aulPropTag[N_TASKDUE], PT_SYSTIME);
    pr_totalwork = SetPropType(pTaskTags->aulPropTag[N_TOTALWORK], PT_LONG);
    pr_actualwork = SetPropType(pTaskTags->aulPropTag[N_ACTUALWORK], PT_LONG);
    pr_companies = SetPropType(pTaskTagsC->aulPropTag[N_COMPANIES], PT_MV_TSTRING);
    pr_mileage = SetPropType(pTaskTagsC->aulPropTag[N_MILEAGE], PT_TSTRING);
    pr_billinginfo = SetPropType(pTaskTagsC->aulPropTag[N_BILLING], PT_TSTRING);

    // free the memory we allocated on the head
    for (int i = 0; i < N_NUMTASKPROPS; i++)
    {
        MAPIFreeBuffer(ppNames[i]);
    }
    for (int i = 0; i < N_NUMCOMMONTPROPS; i++)
    {
        MAPIFreeBuffer(ppNamesC[i]);
    }
    MAPIFreeBuffer(pTaskTags);
    MAPIFreeBuffer(pTaskTagsC);

    //MAPITask::m_bNamedPropsInitialized = true;

    return S_OK;
}

HRESULT MAPITask::SetMAPITaskValues()
{
    SizedSPropTagArray(T_NUMALLTASKPROPS, taskProps) = {
	T_NUMALLTASKPROPS, {
	    PR_SUBJECT, PR_BODY, PR_HTML, PR_IMPORTANCE, pr_isrecurringt, pr_status,
	    pr_percentcomplete, pr_taskstart, pr_taskdue, pr_totalwork,
	    pr_actualwork, pr_companies, pr_mileage, pr_billinginfo
	}
    };

    HRESULT hr = S_OK;
    ULONG cVals = 0;

    if (FAILED(hr = m_pMessage->GetProps((LPSPropTagArray) & taskProps, fMapiUnicode, &cVals,
            &m_pPropVals)))
        throw MAPITaskException(hr, L"SetMAPITaskValues(): GetProps Failed.", __LINE__, __FILE__);

    if (m_pPropVals[T_ISRECURT].ulPropTag == taskProps.aulPropTag[T_ISRECURT]) // do this first to set dates correctly
    {
	m_bIsRecurring = (m_pPropVals[T_ISRECURT].Value.b == 1);
    }
    if (m_pPropVals[T_SUBJECT].ulPropTag == taskProps.aulPropTag[T_SUBJECT])
    {
	SetSubject(m_pPropVals[T_SUBJECT].Value.lpszW);
    }
    if (m_pPropVals[T_IMPORTANCE].ulPropTag == taskProps.aulPropTag[T_IMPORTANCE])
    {
	SetImportance(m_pPropVals[T_IMPORTANCE].Value.l);
    }
    if (m_pPropVals[T_STATUS].ulPropTag == taskProps.aulPropTag[T_STATUS])
    {
	SetTaskStatus(m_pPropVals[T_STATUS].Value.l);
    }
    if (m_pPropVals[T_PERCENTCOMPLETE].ulPropTag == taskProps.aulPropTag[T_PERCENTCOMPLETE])
    {
	SetPercentComplete(m_pPropVals[T_PERCENTCOMPLETE].Value.dbl);
    }
    if (m_pPropVals[T_TASKSTART].ulPropTag == taskProps.aulPropTag[T_TASKSTART])
    {
	SetTaskStart(m_pPropVals[T_TASKSTART].Value.ft);
    }
    if (m_pPropVals[T_TASKDUE].ulPropTag == taskProps.aulPropTag[T_TASKDUE])
    {
        SetTaskDue(m_pPropVals[T_TASKDUE].Value.ft);
    }
    if (m_pPropVals[T_TOTALWORK].ulPropTag == taskProps.aulPropTag[T_TOTALWORK])
    {
	SetTotalWork(m_pPropVals[T_TOTALWORK].Value.l);
    }
    if (m_pPropVals[T_ACTUALWORK].ulPropTag == taskProps.aulPropTag[T_ACTUALWORK])
    {
	SetActualWork(m_pPropVals[T_ACTUALWORK].Value.l);
    }
    if (m_pPropVals[T_COMPANIES].ulPropTag == taskProps.aulPropTag[T_COMPANIES])
    {
	SetCompanies(m_pPropVals[T_COMPANIES].Value.MVszW.lppszW[0]);	// get first one for now
    }
    if (m_pPropVals[T_MILEAGE].ulPropTag == taskProps.aulPropTag[T_MILEAGE])
    {
	SetMileage(m_pPropVals[T_MILEAGE].Value.lpszW);
    }
    if (m_pPropVals[T_BILLING].ulPropTag == taskProps.aulPropTag[T_BILLING])
    {
	SetBillingInfo(m_pPropVals[T_BILLING].Value.lpszW);
    }

    SetPlainTextFileAndContent();
    SetHtmlFileAndContent();

    //if (m_bIsRecurring)
    //{
    //    ...
    //}

    return hr;
}

void MAPITask::SetSubject(LPTSTR pStr)
{
    m_pSubject = pStr;
}

void MAPITask::SetImportance(long importance)
{
    switch (importance)
    {
        case oImportanceLow:    m_pImportance = L"9";   break;
        case oImportanceNormal: m_pImportance = L"5";   break;
        case oImportanceHigh:   m_pImportance = L"1";   break;
        default:                m_pImportance = L"5";
    }
}

void MAPITask::SetTaskStatus(long taskstatus)
{
    switch (taskstatus)
    {
	case oTaskNotStarted:	m_pStatus = L"NEED";	    break;
	case oTaskInProgress:	m_pStatus = L"INPR";	    break;
	case oTaskComplete:	m_pStatus = L"COMP";	    break;
	case oTaskWaiting:	m_pStatus = L"WAITING";	    break;
	case oTaskDeferred:	m_pStatus = L"DEFERRED";    break;
	default:		m_pStatus = L"NEED";
    }
}

void MAPITask::SetPercentComplete(double percentcomplete)
{
    long lPercent = (long)(percentcomplete * 100);
    WCHAR pwszTemp[10];
    _ltow(lPercent, pwszTemp, 10);
    m_pPercentComplete = pwszTemp;
}

void MAPITask::SetTaskStart(FILETIME ft)
{
    SYSTEMTIME st;

    FileTimeToSystemTime(&ft, &st);
    m_pTaskStart = Zimbra::Util::FormatSystemTime(st, FALSE, FALSE);
    m_pTaskStartCommon = Zimbra::MAPI::Util::CommonDateString(m_pPropVals[T_TASKSTART].Value.ft);
}

void MAPITask::SetTaskDue(FILETIME ft)
{
    SYSTEMTIME st;

    FileTimeToSystemTime(&ft, &st);
    m_pTaskDue = Zimbra::Util::FormatSystemTime(st, FALSE, FALSE);
}

void MAPITask::SetTotalWork(long totalwork)
{
    WCHAR pwszTemp[10];
    _ltow(totalwork, pwszTemp, 10);
    m_pTotalWork = pwszTemp;
}

void MAPITask::SetActualWork(long actualwork)
{
    WCHAR pwszTemp[10];
    _ltow(actualwork, pwszTemp, 10);
    m_pActualWork = pwszTemp;
}

void MAPITask::SetCompanies(LPTSTR pStr)    // deal with more than one at some point
{
    m_pCompanies = pStr;
}

void MAPITask::SetMileage(LPTSTR pStr)
{
    m_pMileage = pStr;
}

void MAPITask::SetBillingInfo(LPTSTR pStr)
{
    m_pBillingInfo = pStr;
}

void MAPITask::SetPlainTextFileAndContent()
{
    m_pPlainTextFile = Zimbra::MAPI::Util::SetPlainText(m_pMessage, &m_pPropVals[T_BODY]);
}

void MAPITask::SetHtmlFileAndContent()
{
    m_pHtmlFile = Zimbra::MAPI::Util::SetHtml(m_pMessage, &m_pPropVals[T_HTMLBODY]);
}

wstring MAPITask::GetSubject() { return m_pSubject; }
wstring MAPITask::GetImportance() { return m_pImportance; }
wstring MAPITask::GetTaskStatus() { return m_pStatus; }
wstring MAPITask::GetPercentComplete() { return m_pPercentComplete; }
wstring MAPITask::GetTaskStart() { return m_pTaskStart; }
wstring MAPITask::GetTaskStartCommon() { return m_pTaskStartCommon; }
wstring MAPITask::GetTaskDue() { return m_pTaskDue; }
wstring MAPITask::GetTotalWork() { return m_pTotalWork; }
wstring MAPITask::GetActualWork() { return m_pActualWork; }
wstring MAPITask::GetMileage() { return m_pMileage; }
wstring MAPITask::GetCompanies() { return m_pCompanies; }
wstring MAPITask::GetBillingInfo() { return m_pBillingInfo; }
wstring MAPITask::GetPlainTextFileAndContent() { return m_pPlainTextFile; }
wstring MAPITask::GetHtmlFileAndContent() { return m_pHtmlFile; }

