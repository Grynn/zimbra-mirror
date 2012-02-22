#pragma once

// MAPITaskException class
class MAPITaskException: public GenericException
{
public:
    MAPITaskException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    MAPITaskException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine, LPCSTR strFile);
    virtual ~MAPITaskException() {}
};

// MAPITask class
class MAPITask : public MAPIRfc2445
{
private:
    //static bool m_bNamedPropsInitialized;
    bool m_bIsTaskReminderSet;

    // prop tags for named properties
    ULONG pr_isrecurringt, pr_recurstreamt, pr_status, pr_percentcomplete, pr_taskstart, pr_taskdue, pr_totalwork, pr_actualwork, pr_companies,
	  pr_mileage, pr_billinginfo, pr_taskreminderset, pr_taskflagdueby;

    // index of props
    typedef enum _TaskPropIdx
    {
        N_ISRECURT, N_RECURSTREAMT, N_STATUS, N_PERCENTCOMPLETE, N_TASKSTART, N_TASKDUE, N_TOTALWORK, N_ACTUALWORK, N_NUMTASKPROPS
    } TaskPropIdx;

    typedef enum _CommonTPropIdx
    {
        N_COMPANIES, N_MILEAGE, N_BILLING, N_TASKREMINDERSET, N_TASKFLAGDUEBY, N_NUMCOMMONTPROPS
    } CommonTPropIdx;

    // this enum lists all the props
    enum
    {
        T_SUBJECT, T_BODY, T_HTMLBODY, T_IMPORTANCE, T_ISRECURT, T_RECURSTREAMT, T_STATUS, T_PERCENTCOMPLETE,
        T_TASKSTART, T_TASKDUE, T_TOTALWORK, T_ACTUALWORK, T_COMPANIES, T_MILEAGE, T_BILLING, T_TASKREMINDERSET,
        T_TASKFLAGDUEBY, T_NUMALLTASKPROPS
    };

    // these are the named property id's
    LONG nameIds[N_NUMTASKPROPS];
    LONG nameIdsC[N_NUMCOMMONTPROPS];

    // task data members (represented both by regular and named props
    wstring m_pSubject;
    wstring m_pImportance;
    wstring m_pTaskStart;
    wstring m_pTaskStartCommon;
    wstring m_pTaskDue;
    wstring m_pStatus;
    wstring m_pPercentComplete;
    wstring m_pTotalWork;
    wstring m_pActualWork;
    wstring m_pCompanies;
    wstring m_pMileage;
    wstring m_pBillingInfo;
    wstring m_pTaskFlagDueBy;
    wstring m_pPlainTextFile;
    wstring m_pHtmlFile;
    //

public:
    MAPITask(Zimbra::MAPI::MAPISession &session, Zimbra::MAPI::MAPIMessage &mMessage);
    ~MAPITask();
    HRESULT InitNamedPropsForTask();
    HRESULT SetMAPITaskValues();
    void SetSubject(LPTSTR pStr);
    void SetImportance(long importance);
    void SetTaskStatus(long taskstatus);
    void SetPercentComplete(double percentcomplete);
    void SetTaskStart(FILETIME ft);
    void SetTaskDue(FILETIME ft);
    void SetTotalWork(long totalwork);
    void SetActualWork(long actualwork);
    void SetCompanies(LPTSTR pStr);
    void SetMileage(LPTSTR pStr);
    void SetBillingInfo(LPTSTR pStr);
    void SetTaskFlagDueBy(FILETIME ft);
    void SetPlainTextFileAndContent();
    void SetHtmlFileAndContent();
    void SetRecurValues();

    bool IsTaskReminderSet();
    wstring GetSubject();
    wstring GetImportance();
    wstring GetTaskStatus();
    wstring GetPercentComplete();
    wstring GetTaskStart();
    wstring GetTaskStartCommon();
    wstring GetTaskDue();
    wstring GetTotalWork();
    wstring GetActualWork();
    wstring GetMileage();
    wstring GetCompanies();
    wstring GetBillingInfo();
    wstring GetTaskFlagDueBy();
    wstring GetPlainTextFileAndContent();
    wstring GetHtmlFileAndContent();
};
