#pragma once
// #include "Rpc.h"
#include <list>
#include "edkmdb.h"

#define PR_RESPROP_DISPLAYNAME_A        PROP_TAG(PT_STRING8, 0001)
#define PR_RESPROP_DISPLAYNAME_W        PROP_TAG(PT_UNICODE, 0001)

typedef ACTIONS FAR *LPACTIONS;


/**
 * Defines supported filter conditions. The condition strings coming in from
 * the server are mapped to this enum.
 */
enum RuleCondition
{
    InvalidRuleCondition = 0, From, To, Cc, Subject, HeaderNamed, Size, Date, Body, Attachment,
    AddressIn, MeetingReq, MeetingRsp, ReadReceipt, LastRuleCondition = ReadReceipt
};

/**
 * List of possible condition ops for a rule
 */
enum RuleConditionOp
{
    InvalidConditionOp = 0, ExactMatch, NotExactMatch, Contains, DoesNotContain, MatchesPattern,
    DoesNotMatchPattern, Exists, DoesNotExist, Under, NotUnder, Over, NotOver, Before,
    NotBefore, After, NotAfter, In, NotIn, IsMeetingReq, IsNotMeetingReq, IsMeetingRsp,
    IsNotMeetingRsp, IsReadReceipt, IsNotReadReceipt, LastRuleConditionOp = IsNotReadReceipt
};

/**
 * Possible actions for a rule
 */
enum RuleAction
{
    InvalidRuleAction = 0, KeepInInbox, Discard, FileIntoFolder, TagWith, Mark, ForwardTo, Stop,
    LastRuleAction = Stop
};

struct RuleActionInfo
{
    RuleActionInfo(): m_ruleAction(InvalidRuleAction) {}
    RuleAction m_ruleAction;
    std::wstring m_wstrArg;
};

typedef std::list<RuleActionInfo> CListRuleActions;

struct RuleConditionInfo
{
    RuleConditionInfo(): m_ruleCondition(InvalidRuleCondition), m_ruleConditionOp(
        InvalidConditionOp) {}

    RuleCondition m_ruleCondition;
    RuleConditionOp m_ruleConditionOp;
    std::wstring m_wstrTestName;
    std::wstring m_wstrTest;
    std::wstring m_wstrValue;
    BOOL m_bNegative;
};

typedef std::list<RuleConditionInfo> CListRuleConditions;

/**
 * This class manages the list of actions and conditions associated with a rule
 */
class CRule
{
public:
    CRule();
    ~CRule();

    BOOL GetConditions(CListRuleConditions &listRuleConditions) const;
    BOOL SetConditions(const CListRuleConditions &listRuleConditions);
    BOOL AddCondition(const RuleConditionInfo &ruleConditionInfo);

    BOOL GetActions(CListRuleActions &listRuleActions) const;
    BOOL SetActions(const CListRuleActions &listRuleActions);
    BOOL AddAction(RuleAction action, LPCWSTR pwzArg);
    BOOL AddAction(const RuleActionInfo &actionInfo);

    long GetActive() const
    {
        return m_lActive;
    }

    void SetActive(long lActive)
    {
        m_lActive = lActive;
    }

    bool GetProcessAdditionalRules() const
    {
        return m_bProcessAdditionalRules;
    }

    void ProcessAdditionalRules(bool bProcessAdditionalRules)
    {
        m_bProcessAdditionalRules = bProcessAdditionalRules;
    }

    void GetName(std::wstring &wstrRuleName) const
    {
        wstrRuleName = m_wstrRuleName;
    }

    bool SetName(LPCWSTR pwzRuleName)
    {
        if (!pwzRuleName)
            return false;
        m_wstrRuleName = pwzRuleName;
        return TRUE;
    }

    bool SetRuleCond(LPCWSTR pwzRuleCond)
    {
        if (!pwzRuleCond)
            return false;
        m_wstrRuleCond = pwzRuleCond;
        return true;
    }

    void GetRuleCond(std::wstring &wstrRuleCond) const
    {
        wstrRuleCond = m_wstrRuleCond;
    }

    void Clear();

    bool GetOrProcessed()
    {
        return m_bOrProcessed;
    }

    void SetOrProcessed()
    {
        m_bOrProcessed = true;
    }

    bool GetToOrCc()
    {
        return m_bToOrCc;
    }

    void SetToOrCc()
    {
        m_bToOrCc = true;
    }

    bool GetNotOr()
    {
        return m_bNotOr;
    }

    void SetNotOr()
    {
        m_bNotOr = true;
    }

protected:
    CListRuleActions m_listRuleActions;
    CListRuleConditions m_listRuleConditions;
    bool m_bProcessAdditionalRules;
    long m_lActive;
    std::wstring m_wstrRuleName;
    std::wstring m_wstrRuleCond;
    bool m_bOrProcessed;
    bool m_bToOrCc;
    bool m_bNotOr;
};

typedef std::list<RuleConditionOp> CRuleConditionOps;

BOOL GetOpsForCondition(RuleCondition condition, CRuleConditionOps &listConditionOps);

BOOL ConditionNameToString(RuleCondition condition, RuleConditionOp conditionOp,
    std::wstring &wstrName);
BOOL ConditionComparisonToString(RuleConditionOp op, std::wstring &wstrName);

RuleCondition ConditionFromString(LPCWSTR pwzConditionName, std::wstring &wstrTest);

BOOL RuleActionToString(RuleAction ruleAction, std::wstring &wstrRuleAction);
RuleAction RuleActionFromString(LPCWSTR pwzActionName);

BOOL ConditionInfoToDetails(const RuleConditionInfo &ruleConditionInfo,
    std::wstring &wstrConditionName, std::wstring &wstrConditionComparison,
    std::wstring &wstrTestName, std::wstring &wstrTest, std::wstring &wstrValue,
    BOOL &bNegative);

BOOL ConditionDetailsToInfo(LPCWSTR pwzTestName, LPCWSTR pwzConditionName, LPCWSTR
    pwzConditionComparison, LPCWSTR pwzTest, LPCWSTR pwzValue, BOOL bNegative,
    RuleConditionInfo &ruleConditionInfo);

typedef std::list<CComPtr<IXMLDOMNode> > CListNodes;

class CRuleProcessor
{
public:
    CRuleProcessor(Zimbra::MAPI::MAPISession* session, Zimbra::MAPI::MAPIStore* store);
    ~CRuleProcessor();

private:
     Zimbra::MAPI::MAPISession* m_session;
     Zimbra::MAPI::MAPIStore* m_userStore;

public:
    CString MakeFolderPath(LPMAPIFOLDER pFolder, std::vector<CString> &vFolders);
    bool ProcessRestrictions(CRule &rule, LPSRestriction pRestriction, bool bNegative, ULONG
        ulCallingType);
    bool ProcessActions(CRule &rule, LPACTIONS pActions);

private:
    LPBYTE memmem(const void *buf, size_t buf_len, const void *byte_sequence, size_t byte_sequence_len);    
};

class CRuleMap
{
public:
    CRuleMap();
    ~CRuleMap();

public:
    void WriteFilterRule(CRule &rule, LPWSTR &filterRule);
    void WriteFilterTests(CRule &rule, LPWSTR &filterTests);
    void WriteFilterActions(CRule &rule, LPWSTR &filterActions); 
};

