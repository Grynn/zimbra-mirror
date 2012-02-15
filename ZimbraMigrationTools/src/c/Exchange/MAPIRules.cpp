#include "common.h"
#include "Exchange.h"
#include "MAPIRules.h"
// #include <strsafe.h>

/**
 * CRule member defines
 */

CRule::CRule(): m_bProcessAdditionalRules(true), m_lActive(false), m_bOrProcessed(false),
    m_bToOrCc(false), m_bNotOr(false) {}

CRule::~CRule() {}

BOOL CRule::AddAction(RuleAction action, LPCWSTR pwzArg)
{
    if ((action <= InvalidRuleAction) || (action > LastRuleAction))
        return FALSE;

    RuleActionInfo actionInfo;

    actionInfo.m_ruleAction = action;
    actionInfo.m_wstrArg = pwzArg;

    return AddAction(actionInfo);
}

BOOL CRule::AddCondition(const RuleConditionInfo &ruleConditionInfo)
{
    m_listRuleConditions.push_back(ruleConditionInfo);
    return TRUE;
}

BOOL CRule::AddAction(const RuleActionInfo &actionInfo)
{
    m_listRuleActions.push_back(actionInfo);
    return TRUE;
}

BOOL CRule::GetActions(CListRuleActions &listRuleActions) const
{
    listRuleActions = m_listRuleActions;
    return TRUE;
}

BOOL CRule::GetConditions(CListRuleConditions &listRuleConditions) const
{
    listRuleConditions = m_listRuleConditions;
    return TRUE;
}

BOOL CRule::SetActions(const CListRuleActions &listRuleActions)
{
    m_listRuleActions.clear();
    m_listRuleActions = listRuleActions;
    return TRUE;
}

BOOL CRule::SetConditions(const CListRuleConditions &listRuleConditions)
{
    m_listRuleConditions.clear();
    m_listRuleConditions = listRuleConditions;
    return TRUE;
}

void CRule::Clear()
{
    m_listRuleActions.clear();
    m_listRuleConditions.clear();
    m_bProcessAdditionalRules = false;
    m_lActive = false;
    m_wstrRuleName.clear();
    m_wstrRuleCond.clear();
}

/**
 * Helper functions
 */

/**
 * This function converts the RuleCondition enum to a string, which represents
 * the condition name on the server. It uses a combination of the RuleCondition
 * enum and RuleConditionOp for conversion.
 */
BOOL ConditionNameToString(RuleCondition condition, RuleConditionOp conditionOp,
    std::wstring &wstrName)
{
    if ((condition <= InvalidConditionOp) || (condition > LastRuleCondition))
        return FALSE;
    if ((Attachment == condition) && (DoesNotExist == conditionOp))
    {
        wstrName = L"not attachment";
        return TRUE;
    }

    static LPCWSTR s_pwzRuleCondition[] = {
        L"Invalid Condition", L"header", L"header", L"header", L"header", L"header", L"size",
        L"date", L"body", L"attachment", L"addressbook", L"meetingrequest",
        L"meetingresponse", L"header"
    };

    wstrName = s_pwzRuleCondition[condition];
    return TRUE;
}

BOOL ConditionComparisonToString(RuleConditionOp conditionOp, std::wstring &wstrName)
{
    if ((conditionOp <= InvalidConditionOp) || (conditionOp > LastRuleConditionOp))
        return FALSE;

    static LPCWSTR s_pwzRuleConditionOp[] = {
        L"Invalid ConditionOp", L"is", L"is", L"contains", L"contains", L"matches", L"matches",
        L"", L"", L"under", L"under", L"over", L"over", L"before", L"before", L"after",
        L"after", L"in", L"in", L"", L"", L"", L"", L"contains", L"contains"
    };

    wstrName = s_pwzRuleConditionOp[conditionOp];
    return TRUE;
}

RuleConditionOp ConditionComparisonFromString(LPCWSTR pwzConditionOp, LPCWSTR pwzConditionName,
    ULONG ulConditionVal, BOOL bNegative)
{
    if (pwzConditionOp)
    {
        if (0 == lstrcmpiW(pwzConditionOp, L"is"))
            return (bNegative) ? NotExactMatch : ExactMatch;
        else if (0 == lstrcmpiW(pwzConditionOp, L"contains"))
            return (bNegative) ? DoesNotContain : Contains;
        else if (0 == lstrcmpiW(pwzConditionOp, L"under"))
            return (bNegative) ? NotUnder : Under;
        else if (0 == lstrcmpiW(pwzConditionOp, L"over"))
            return (bNegative) ? NotOver : Over;
        else if (0 == lstrcmpiW(pwzConditionOp, L"before"))
            return (bNegative) ? NotBefore : Before;
        else if (0 == lstrcmpiW(pwzConditionOp, L"after"))
            return (bNegative) ? NotAfter : After;
        else if (0 == lstrcmpiW(pwzConditionOp, L"matches"))
            return (bNegative) ? DoesNotMatchPattern : MatchesPattern;
        else if (0 == lstrcmpiW(pwzConditionOp, L"in"))
            return (bNegative) ? NotIn : In;
    }
    if (pwzConditionName)
    {
        if ((0 == lstrcmpiW(pwzConditionName, L"attachment")) || (0 == lstrcmpiW(
            pwzConditionName, L"exists")))
        {
            return (bNegative) ? DoesNotExist : Exists;
        }
        else if (0 == lstrcmpiW(pwzConditionName, L"invite"))
        {
            if (ulConditionVal == MeetingReq)
                return (bNegative) ? IsNotMeetingReq : IsMeetingReq;
            else
                return (bNegative) ? IsNotMeetingRsp : IsMeetingRsp;
        }
    }
    return InvalidConditionOp;
}

RuleCondition ConditionFromString(LPCWSTR pwzConditionName, std::wstring &wstrTest)
{
    if (!pwzConditionName)
        return InvalidRuleCondition;
    if (0 == lstrcmpiW(pwzConditionName, L"header"))
    {
        if (wstrTest.length())
        {
            if (0 == lstrcmpiW(wstrTest.c_str(), L"From"))
            {
                wstrTest.clear();
                return From;
            }
            else if (0 == lstrcmpiW(wstrTest.c_str(), L"To"))
            {
                wstrTest.clear();
                return To;
            }
            else if (0 == lstrcmpiW(wstrTest.c_str(), L"Cc"))
            {
                wstrTest.clear();
                return Cc;
            }
            else if (0 == lstrcmpiW(wstrTest.c_str(), L"Subject"))
            {
                wstrTest.clear();
                return Subject;
            }
            else if (0 == lstrcmpiW(wstrTest.c_str(), L"Content-Type"))
            {
                // wstrTest.clear() ;
                return ReadReceipt;
            }
            else
            {
                return HeaderNamed;
            }
        }
    }
    else if (0 == lstrcmpiW(pwzConditionName, L"size"))
    {
        return Size;
    }
    else if (0 == lstrcmpiW(pwzConditionName, L"date"))
    {
        return Date;
    }
    else if (0 == lstrcmpiW(pwzConditionName, L"body"))
    {
        return Body;
    }
    else if (0 == lstrcmpiW(pwzConditionName, L"attachment"))
    {
        return Attachment;
    }
    else if (0 == lstrcmpiW(pwzConditionName, L"addressbook"))
    {
        return AddressIn;
    }
    else if (0 == lstrcmpiW(pwzConditionName, L"invite"))
    {
        if (0 == lstrcmpiW(wstrTest.c_str(), L"anyreply"))
            return MeetingRsp;
        else
            return MeetingReq;
    }
    else if (0 == lstrcmpiW(pwzConditionName, L"exists"))
    {
        return HeaderNamed;
    }
    return InvalidRuleCondition;
}

BOOL RuleActionToString(RuleAction ruleAction, std::wstring &wstrRuleAction)
{
    if ((ruleAction <= InvalidRuleAction) || (ruleAction > LastRuleAction))
        return FALSE;

    static LPCWSTR s_pwzArrActions[] = {
        L"InvalidAction", L"keep", L"discard", L"fileInto", L"tag", L"flag", L"redirect",
        L"stop"
    };

    wstrRuleAction = s_pwzArrActions[ruleAction];
    return TRUE;
}

RuleAction RuleActionFromString(LPCWSTR pwzActionName)
{
    if (!pwzActionName)
        return InvalidRuleAction;
    if (0 == lstrcmpiW(pwzActionName, L"keep"))
        return KeepInInbox;
    else if (0 == lstrcmpiW(pwzActionName, L"discard"))
        return Discard;
    else if (0 == lstrcmpiW(pwzActionName, L"fileInto"))
        return FileIntoFolder;
    else if (0 == lstrcmpiW(pwzActionName, L"tag"))
        return TagWith;
    else if (0 == lstrcmpiW(pwzActionName, L"flag"))
        return Mark;
    else if (0 == lstrcmpiW(pwzActionName, L"redirect"))
        return ForwardTo;
    else if (0 == lstrcmpiW(pwzActionName, L"stop"))
        return Stop;
    return InvalidRuleAction;
}

BOOL GetOpsForCondition(RuleCondition condition, CRuleConditionOps &listConditionOps)
{
    switch (condition)
    {
    case From:
    case To:
    case Cc:
    case Subject:
    case HeaderNamed:
    {
        listConditionOps.push_back(ExactMatch);
        listConditionOps.push_back(NotExactMatch);
        listConditionOps.push_back(Contains);
        listConditionOps.push_back(DoesNotContain);
        listConditionOps.push_back(MatchesPattern);
        listConditionOps.push_back(DoesNotMatchPattern);
        if (HeaderNamed == condition)
        {
            listConditionOps.push_back(Exists);
            listConditionOps.push_back(DoesNotExist);
        }
        break;
    }

    case Size:
    {
        listConditionOps.push_back(Under);
        listConditionOps.push_back(NotUnder);
        listConditionOps.push_back(Over);
        listConditionOps.push_back(NotOver);
        break;
    }

    case Date:
    {
        listConditionOps.push_back(Before);
        listConditionOps.push_back(NotBefore);
        listConditionOps.push_back(After);
        listConditionOps.push_back(NotAfter);
        break;
    }

    case Body:
    {
        listConditionOps.push_back(Contains);
        listConditionOps.push_back(DoesNotContain);
    }

    case Attachment:
    {
        listConditionOps.push_back(Exists);
        listConditionOps.push_back(DoesNotExist);
        break;
    }

    case AddressIn:
    {
        listConditionOps.push_back(In);
        listConditionOps.push_back(NotIn);
        break;
    }

    default:
    {
        return FALSE;
    }
    }
    return TRUE;
}

BOOL ConditionDetailsToInfo(LPCWSTR pwzTestName, LPCWSTR pwzConditionName, LPCWSTR
    pwzConditionComparison, LPCWSTR pwzTest, LPCWSTR pwzValue, BOOL bNegative,
    RuleConditionInfo &ruleConditionInfo)
{
    if (pwzTestName)
        ruleConditionInfo.m_wstrTestName = (LPCWSTR)pwzTestName;
    if (pwzTest)
        ruleConditionInfo.m_wstrTest = (LPCWSTR)pwzTest;
    if (pwzValue)
        ruleConditionInfo.m_wstrValue = (LPCWSTR)pwzValue;
    if (!pwzConditionName)
        return FALSE;
    ruleConditionInfo.m_bNegative = bNegative;

    ruleConditionInfo.m_ruleCondition = ConditionFromString(pwzConditionName,
        ruleConditionInfo.m_wstrTest);
    ruleConditionInfo.m_ruleConditionOp = ConditionComparisonFromString(pwzConditionComparison,
        pwzConditionName, ruleConditionInfo.m_ruleCondition, bNegative);

    return TRUE;
}

BOOL ConditionInfoToDetails(const RuleConditionInfo &ruleConditionInfo,
    std::wstring &wstrConditionName, std::wstring &wstrConditionComparison,
    std::wstring &wstrTestName, std::wstring &wstrTest, std::wstring &wstrValue,
    BOOL &bNegative)
{
    ConditionNameToString(ruleConditionInfo.m_ruleCondition,
        ruleConditionInfo.m_ruleConditionOp, wstrConditionName);
    ConditionComparisonToString(ruleConditionInfo.m_ruleConditionOp, wstrConditionComparison);

    wstrTest = ruleConditionInfo.m_wstrTest;
    wstrTestName = ruleConditionInfo.m_wstrTestName;
    wstrValue = ruleConditionInfo.m_wstrValue;
    bNegative = ruleConditionInfo.m_bNegative;
    switch (ruleConditionInfo.m_ruleCondition)
    {
    case From:
        wstrTest = L"From";
        break;

    case To:
        wstrTest = L"To";
        break;

    case Cc:
        wstrTest = L"Cc";
        break;

    case Subject:
    {
        wstrTest = L"Subject";
        break;
    }

    default:
        break;
    }
    return FALSE;
}

// CRuleProcessor class
CRuleProcessor::CRuleProcessor(Zimbra::MAPI::MAPISession* session, Zimbra::MAPI::MAPIStore* store, std::wstring account) : m_session(session), m_userStore(store), m_account(account) {}

CRuleProcessor::~CRuleProcessor() {}

CString CRuleProcessor::MakeFolderPath(LPMAPIFOLDER pFolder, std::vector<CString> &vFolders)
    
{
    LPSPropValue lpParentEID = NULL;
    HRESULT hr = HrGetOneProp(pFolder, PR_PARENT_ENTRYID, &lpParentEID);

    if (FAILED(hr))
        return vFolders[0];

    ULONG ulObjType = 0;
    LPMAPIFOLDER pParentFolder;

    hr = m_userStore->OpenEntry(lpParentEID->Value.bin.cb,         // m_userStore->OpenEntry may not be right
        (LPENTRYID)lpParentEID->Value.bin.lpb, NULL, 0, &ulObjType,
        (LPUNKNOWN *)&pParentFolder);
    if (FAILED(hr))
        return vFolders[0];

    /*
     * Note -- could do something like this instead of comparing entry ids:
     *
     * LPSPropValue lpParentDisplayName = NULL ;
     * hr = HrGetOneProp( pParentFolder, PR_DISPLAY_NAME, &lpParentDisplayName );
     * LPWSTR pwszDisplayName = lpParentDisplayName->Value.lpszW;
     * if (wcscmp(pwszDisplayName, L"Top of Information Store") == 0) {
     *
     * Then we wouldn't have to pass the pTargetStore around.  But checking the display name
     * "Top of Information Store" is dangerous, so we suck it up and get the IPM subtree from
     * // the target store and compare entry ids.  Much safer.
     */

    Zimbra::Util::ScopedBuffer<SPropValue> spIPMSubtreeEntryId;

    hr = HrGetOneProp(m_userStore->GetInternalMAPIStore(), PR_IPM_SUBTREE_ENTRYID,
        spIPMSubtreeEntryId.getptr());
    if (FAILED(hr))
        return vFolders[0];

    ULONG ulResult = 0;

    m_userStore->CompareEntryIDs(&lpParentEID->Value.bin, &spIPMSubtreeEntryId->Value.bin, ulResult);

    if (ulResult)
    {
        size_t numFolders = vFolders.size();

        if (numFolders == 1)
        {
            pParentFolder->Release();
            return vFolders[0];
        }
        else
        {
            CString strRetval;
            ::std::reverse(vFolders.begin(), vFolders.end());

            for (size_t i = 0; i < numFolders; i++)
            {
                strRetval += vFolders[i];
                if (i < (numFolders - 1))
                    strRetval += L"/";
            }
            pParentFolder->Release();
            return strRetval;
        }
    }
    else
    {
        LPSPropValue lpParentDisplayName = NULL;

        hr = HrGetOneProp(pParentFolder, PR_DISPLAY_NAME, &lpParentDisplayName);

        LPWSTR pwszDisplayName = lpParentDisplayName->Value.lpszW;

        vFolders.push_back(pwszDisplayName);
        return MakeFolderPath(pParentFolder, vFolders);
    }
}

bool CRuleProcessor::ProcessRestrictions(CRule &rule, LPSRestriction pRestriction, bool
    bNegative, ULONG ulCallingType)
{
    bool bRetval = true;
    WCHAR wTestName[20] = L"";
    WCHAR wName[20] = L"";
    WCHAR wComparison[10] = L"";
    WCHAR wTest[256] = L"";                     // FBS bug 59042 -- 4/15/11 -- bump up from [40]
    WCHAR wValue[256] = L"";                    // FBS bug 59042 -- 4/15/11 -- bump up from [40]
    WCHAR wMode[10] = L"";
    ULONG i = 0;
    RuleConditionInfo ruleConditionInfo;
    const int FULLSTRING = 0;
    //const int SUBSTRING = 1;
    //const int LT = 0;
    //const int LE = 1;
    const int GT = 2;
    const int GE = 3;

    if (pRestriction == NULL)
    {
        //TRACE(_T("NULL Restriction"));
        return false;
    }
    switch (pRestriction->rt)
    {
    case RES_CONTENT:
    {
        ULONG ulFuzzyLevel = pRestriction->res.resContent.ulFuzzyLevel;
        ULONG ulFuzzyLevelLow = ulFuzzyLevel & 0x0000FFFF;

        if (ulFuzzyLevelLow == FULLSTRING)
            wcscpy(wComparison, L"matches");
        else
            wcscpy(wComparison, L"contains");

        LPSPropValue pProp = pRestriction->res.resContent.lpProp;

        switch (PROP_ID(pProp->ulPropTag))
        {
        case PROP_ID(PR_SUBJECT):
        {
            wcscpy(wTestName, L"headerTest");
            wcscpy(wName, L"header");
            wcscpy(wTest, L"Subject");

            std::wstring val = CA2W(pProp->Value.lpszA);

            wcscpy(wValue, val.c_str());
            ConditionDetailsToInfo(wTestName, wName, wComparison, wTest, wValue, bNegative,
                ruleConditionInfo);
            rule.AddCondition(ruleConditionInfo);
            break;
        }

        case PROP_ID(PR_BODY):
        {
            wcscpy(wTestName, L"bodyTest");
            wcscpy(wName, L"body");
            wcscpy(wTest, L"value");

            std::wstring val = CA2W(pProp->Value.lpszA);

            wcscpy(wValue, val.c_str());
            ConditionDetailsToInfo(wTestName, wName, wComparison, wTest, wValue, bNegative,
                ruleConditionInfo);
            rule.AddCondition(ruleConditionInfo);
            break;
        }

        case PROP_ID(PR_SENDER_SEARCH_KEY):
        {
            wcscpy(wTestName, L"headerTest");
            wcscpy(wName, L"header");
            wcscpy(wComparison, L"contains");
            wcscpy(wTest, L"From");

            // take the blob and make it a string
            ULONG cb = pProp->Value.bin.cb;
            LPBYTE pTemp = new BYTE[cb + 1];

            memcpy(pTemp, pProp->Value.bin.lpb, cb);
            pTemp[cb] = '\0';

            std::wstring val = CA2W((LPSTR)pTemp);

            wcscpy(wValue, val.c_str());

            ConditionDetailsToInfo(wTestName, wName, wComparison, wTest, wValue, bNegative,
                ruleConditionInfo);
            rule.AddCondition(ruleConditionInfo);
            delete[] pTemp;
            break;
        }

        case PROP_ID(PR_MESSAGE_CLASS):
        {
            if (strcmp(pProp->Value.lpszA, "IPM.Schedule.Meeting.Request") == 0)
            {
                wcscpy(wTestName, L"inviteTest");
                wcscpy(wName, L"invite");
                wcscpy(wTest, L"anyrequest");
                wcscpy(wComparison, L"");
                ConditionDetailsToInfo(wTestName, wName, wComparison, wTest, wValue, bNegative,
                    ruleConditionInfo);
                rule.AddCondition(ruleConditionInfo);
            }
            else if (strcmp(pProp->Value.lpszA, "IPM.Note.Rules.OofTemplate.Microsoft") == 0)
            {
                //TRACE(_T("'which is an automatic reply' condition not supported"));
                rule.SetActive(0);
            }
            else if (strncmp(pProp->Value.lpszA, "IPM.Note.", 9) == 0)
            {
                //TRACE(_T("'uses the form' condition not supported"));
                rule.SetActive(0);
            }
            break;
        }

        case PROP_ID(PR_TRANSPORT_MESSAGE_HEADERS):
        {
            wcscpy(wTestName, L"headerExistsTest");
            wcscpy(wName, L"exists");

            std::wstring val = CA2W(pProp->Value.lpszA);

            wcscpy(wTest, val.c_str());
            ConditionDetailsToInfo(wTestName, wName, wComparison, wTest, wValue, bNegative,
                ruleConditionInfo);
            rule.AddCondition(ruleConditionInfo);
            break;
        }

        default:
            //TRACE(_T("Restriction content property %0x not supported"), pProp->ulPropTag);
            rule.SetActive(0);
        }
        break;
    }

    case RES_PROPERTY:
    {
        if (ulCallingType != RES_NOT)
            wcscpy(wMode, L"condition");
        else
            wcscpy(wMode, L"exception");

        ULONG ulRelop = pRestriction->res.resProperty.relop;
        LPSPropValue pProp = pRestriction->res.resProperty.lpProp;

        switch (PROP_ID(pProp->ulPropTag))
        {
        case PROP_ID(PR_MESSAGE_SIZE):
        {
            WCHAR wSizval[16];

            wcscpy(wTestName, L"sizeTest");
            wcscpy(wName, L"size");
            wcscpy(wTest, L"s");
            if ((ulRelop == GT) || (ulRelop == GE))     // should never be 3 [GE] but let's provide for it
                wcscpy(wComparison, L"over");
            else
                wcscpy(wComparison, L"under");          // same deal with 1 [LE]

            int iSize = pProp->Value.l;

            iSize /= 1024;                      // Exchange just uses K
            _itow(iSize, wSizval, 10);
            wcscat(wSizval, L"K");
            ConditionDetailsToInfo(wTestName, wName, wComparison, wTest, wSizval, bNegative,
                ruleConditionInfo);
            rule.AddCondition(ruleConditionInfo);
            break;
        }

        case PROP_ID(PR_MESSAGE_DELIVERY_TIME):
        {
            __int64 unixTime;
            WCHAR wDateval[64] = { 0 };

            wcscpy(wTestName, L"dateTest");
            wcscpy(wName, L"date");
            wcscpy(wTest, L"d");
            if ((ulRelop == GT) || (ulRelop == GE))     // should never be 3 [GE] but let's provide for it
                wcscpy(wComparison, L"after");
            else
                wcscpy(wComparison, L"before");         // same deal with 1 [LE]

            // server wants UnixTime64 format
            __int64 ux = pProp->Value.ft.dwLowDateTime |
                (static_cast<__int64>(pProp->Value.ft.dwHighDateTime) << 32);

            ux -= 116444736000000000;
            ux /= 10000000;
            unixTime = ux;
            // ///////////////////////////////

            _i64tow(unixTime, wDateval, 10);
            ConditionDetailsToInfo(wTestName, wName, wComparison, wTest, wDateval, bNegative,
                ruleConditionInfo);
            rule.AddCondition(ruleConditionInfo);
            break;
        }

        case PROP_ID(PR_MESSAGE_RECIP_ME):
        case PROP_ID(PR_MESSAGE_TO_ME):
        {
            wcscpy(wTestName, L"headerTest");
            wcscpy(wComparison, L"contains");
            if (PROP_ID(pProp->ulPropTag) == PROP_ID(PR_MESSAGE_RECIP_ME))
            {
                wcscpy(wTest, L"to,cc");
                rule.SetToOrCc();
            }
            else
            {
                wcscpy(wTest, L"to");
            }

            wcscpy(wValue, m_account.c_str());
            ConditionDetailsToInfo(wTestName, wName, wComparison, wTest, wValue, bNegative,
                ruleConditionInfo);
            rule.AddCondition(ruleConditionInfo);
            break;
        }

        case PROP_ID(PR_MESSAGE_CC_ME):
        {
            wcscpy(wTestName, L"headerTest");
            wcscpy(wComparison, L"contains");
            if (ulCallingType == RES_AND)       // just in case
            {
                wcscpy(wTest, L"cc");
                wcscpy(wValue, m_account.c_str());
                ConditionDetailsToInfo(wTestName, wName, wComparison, wTest, wValue, bNegative,
                    ruleConditionInfo);
                rule.AddCondition(ruleConditionInfo);
            }
            break;
        }

        case PROP_ID(PR_IMPORTANCE):
        {
            wcscpy(wTestName, L"headerTest");
            wcscpy(wComparison, L"is");
            wcscpy(wName, L"header");
            wcscpy(wTest, L"Importance");
            wcscpy(wValue, L"Normal");
            switch (pProp->Value.l)
            {
            case 0:
                wcscpy(wValue, L"Low");
                break;
            case 2:
                wcscpy(wValue, L"High");
                break;
            default:
                ;
            }
            ConditionDetailsToInfo(wTestName, wName, wComparison, wTest, wValue, bNegative,
                ruleConditionInfo);
            rule.AddCondition(ruleConditionInfo);
            break;
        }

        case PROP_ID(PR_SENSITIVITY):
        {
            wcscpy(wTestName, L"headerTest");
            wcscpy(wComparison, L"is");
            wcscpy(wName, L"header");
            wcscpy(wTest, L"Sensitivity");
            wcscpy(wValue, L"Normal");
            switch (pProp->Value.l)
            {
            case 1:
                wcscpy(wValue, L"Personal");
                break;
            case 2:
                wcscpy(wValue, L"Private");
                break;
            case 3:
                wcscpy(wValue, L"Company-Confidential");
                break;
            default:
                ;
            }
            ConditionDetailsToInfo(wTestName, wName, wComparison, wTest, wValue, bNegative,
                ruleConditionInfo);
            rule.AddCondition(ruleConditionInfo);
            break;
        }

        case PROP_ID(PR_FLAG_STATUS):
        {
            //TRACE(_T("'flagged for action' %s not supported"), wMode);
            rule.SetActive(0);
            break;
        }

        case PROP_ID(PR_MESSAGE_CLASS):
        {
            //TRACE(_T("'RSS feed %s not supported"), wMode);
            rule.SetActive(0);
            break;
        }

        case PROP_ID(PR_DISPLAY_CC):
        {
            //TRACE(_T("'sent only to me' %s not supported"), wMode);
            rule.SetActive(0);
            break;
        }

        default:
            //TRACE(_T("Restriction property %0x not supported"), pProp->ulPropTag);
            rule.SetActive(0);
        }
        break;
    }

    case RES_BITMASK:
    {
        ULONG ulPropTag = pRestriction->res.resBitMask.ulPropTag;

        switch (ulPropTag)
        {
        case PR_MESSAGE_FLAGS:
        {
            if (pRestriction->res.resBitMask.ulMask == MSGFLAG_HASATTACH)
            {
                wcscpy(wTestName, L"attachmentTest");
                wcscpy(wName, L"attachment");
                ConditionDetailsToInfo(wTestName, wName, NULL, NULL, NULL, bNegative,
                    ruleConditionInfo);
                rule.AddCondition(ruleConditionInfo);
            }
            break;
        }

        default:
            ;
        }
        break;
    }

    case RES_AND:
    {
        ULONG ulCount = pRestriction->res.resAnd.cRes;

        for (i = 0; i < ulCount; i++)
        {
            if (!ProcessRestrictions(rule, &pRestriction->res.resAnd.lpRes[i], bNegative,
                RES_AND))
                bRetval = false;                // if any one fails, we want to log them all, but not continue
        }
        break;
    }

    case RES_OR:
    {
        rule.SetOrProcessed();
        if (ulCallingType == RES_NOT)           // FBS bug 60691 6/8/11
            rule.SetNotOr();

        ULONG ulCount = pRestriction->res.resAnd.cRes;

        for (i = 0; i < ulCount; i++)
        {
            if (!ProcessRestrictions(rule, &pRestriction->res.resOr.lpRes[i], bNegative,
                RES_OR))
                bRetval = false;                // if any one fails, we want to log them all, but not continue
        }
        break;
    }

    case RES_NOT:
    {
        if (rule.GetOrProcessed())
        {
            //TRACE(_T("mixed OR and AND conditions not supported"));
            bRetval = false;
        }
        ProcessRestrictions(rule, pRestriction->res.resNot.lpRes, TRUE, RES_NOT);
        break;
    }

    case RES_SUBRESTRICTION:
    {
        ULONG ulSubObject = pRestriction->res.resSub.ulSubObject;

        if (ulSubObject == PR_MESSAGE_RECIPIENTS)
            ProcessRestrictions(rule, pRestriction->res.resSub.lpRes, bNegative,
                RES_SUBRESTRICTION);
        break;
    }

    case RES_COMMENT:                           // weird -- the From rule uses a Comment restriction
    {
        ULONG ulPropCount = pRestriction->res.resComment.cValues;
        LPSPropValue pProp = NULL;
        std::wstring val = L"";

        for (i = 0; i < ulPropCount; i++)
        {
            pProp = &pRestriction->res.resComment.lpProp[i];
            if (pProp->ulPropTag == PR_RESPROP_DISPLAYNAME_A)
            {
                val = CA2W(pProp->Value.lpszA);
                break;
            }
            else if (pProp->ulPropTag == PR_RESPROP_DISPLAYNAME_W)
            {
                val = pProp->Value.lpszW;
                break;
            }
        }
        // figure out wTest
        wcscpy(wTest, L"From");                 // assume this to start

        LPSRestriction pResInRes = pRestriction->res.resComment.lpRes;

        if (pResInRes->rt == RES_PROPERTY)
        {
            if (pResInRes->res.resProperty.ulPropTag == PR_SEARCH_KEY)
                wcscpy(wTest, L"To");
        }
        wcscpy(wComparison, L"contains");       // may not be cool
        wcscpy(wTestName, L"headerTest");
        wcscpy(wName, L"header");

        wcscpy(wValue, val.c_str());
        ConditionDetailsToInfo(wTestName, wName, wComparison, wTest, wValue, bNegative,
            ruleConditionInfo);
        rule.AddCondition(ruleConditionInfo);
        break;
    }

    case RES_EXIST:
    {
        if (ulCallingType != RES_NOT)
            wcscpy(wMode, L"condition");
        else
            wcscpy(wMode, L"exception");

        ULONG ulPropTag = pRestriction->res.resExist.ulPropTag;

        if ((PROP_ID(ulPropTag)) == (PROP_ID(PR_MESSAGE_CLASS)))
        {
            /*
            TRACE(_T("Client-only %s not yet supported.  Possible conditions causing this:"),
                wMode);
            TRACE(_T("   sender is in specified address book"));
            TRACE(_T("   through specified account"));
            TRACE(_T("   assigned to category"));
            TRACE(_T("   with selected properties of documents of forms"));
            TRACE(_T("   with specific words in the recipient's address"));
            TRACE(_T("   no conditions specified"));
            */
            rule.SetActive(0);
        }
        else if (PROP_ID(ulPropTag) == 0x8008)
        {
            //TRACE(_T("'assigned to any category' %s not supported"), wMode);
            rule.SetActive(0);
        }
        break;
    }

    default:
        //TRACE(_T("Restriction type %d not supported"), pRestriction->rt);
        rule.SetActive(0);
    }
    return bRetval;
}

// Warning numbers in the range 4700-4999 (code generation warnings) require the
// #pragma to be outside the function, because for these, the current compiler
// warning state remains in effect for the whole function
#pragma warning(push)
#pragma warning(disable: 4706)
bool CRuleProcessor::ProcessActions(CRule &rule, LPACTIONS pActions)    
{
    bool bRetval = true;
    ULONG i = 0;
    HRESULT hr;
    LPMAPIFOLDER pFolder = NULL;
    ULONG objtype = 0;

    if (pActions == NULL)
    {
        //TRACE(_T("NULL Actions"));
        return false;
    }
    for (i = 0; i < pActions->cActions; i++)
    {
        RuleActionInfo ruleAction;
        ACTION action = pActions->lpAction[i];

        switch (action.acttype)
        {
        case OP_MOVE:
        {
            ULONG cbFldEntryId = action.actMoveCopy.cbFldEntryId;
            LPENTRYID lpFldEntryId = action.actMoveCopy.lpFldEntryId;

            hr = m_session->OpenEntry(cbFldEntryId, lpFldEntryId, NULL, MAPI_BEST_ACCESS,
                &objtype, (LPUNKNOWN *)&pFolder);
            if (hr == S_OK)
            {
                LPSPropValue lpProp = NULL;
                /*HRESULT hr = */ HrGetOneProp(pFolder, PR_DISPLAY_NAME, &lpProp);

                ruleAction.m_ruleAction = RuleActionFromString(L"fileInto");
                if (wcscmp(lpProp->Value.lpszW, L"Deleted Items") == 0)
                {
                    ruleAction.m_wstrArg = L"Trash";
                }
                else if (wcscmp(lpProp->Value.lpszW, L"Junk E-Mail") == 0)
                {
                    ruleAction.m_wstrArg = L"Junk";
                }
                else
                {
                    std::vector<CString> vFolders;

                    vFolders.push_back(lpProp->Value.lpszW);
                    ruleAction.m_wstrArg = MakeFolderPath(pFolder, vFolders);
                }
                rule.AddAction(ruleAction);
                pFolder->Release();             // FBS bug 58954 -- 4/13/11 -- move here from 5 lines below
            }
            else
            {
               // TRACE(_T("Unable to determine folder path"));
                rule.SetActive(0);
            }
            break;
        }

        case OP_COPY:
        {
            //TRACE(_T("'move a copy to the specified folder' action is not supported"));
            rule.SetActive(0);
            break;
        }

        case OP_FORWARD:
        {
            ULONG j;
            //LPADRLIST lpAdrlist = action.lpadrlist;

            if (action.ulActionFlavor == 3)
            {
                //TRACE(_T("'redirect' action is not supported"));
                rule.SetActive(0);
            }
            else if (action.ulActionFlavor == 4)
            {
                //TRACE(_T("'forward as an attachment' action is not supported"));
                rule.SetActive(0);
            }
            else
            {
                for (j = 0; j < action.lpadrlist->cEntries; j++)
                {
                    Zimbra::Util::ScopedBuffer<SPropValue> pPropVals;

                    SizedSPropTagArray(2, propTags) = {
                        2, { PR_DISPLAY_NAME_W, PR_EMAIL_ADDRESS_W }
                    };

                    ULONG cVals = 0;
                    ADRENTRY lpEntry = action.lpadrlist->aEntries[j];
                    ULONG cbEID = lpEntry.rgPropVals->Value.bin.cb;
                    LPENTRYID lpEID = (LPENTRYID)lpEntry.rgPropVals->Value.bin.lpb;
                    Zimbra::Util::ScopedInterface<IMAPIProp> spUnk;

                    hr = m_session->OpenEntry(cbEID, lpEID, NULL, MAPI_BEST_ACCESS,
                        &objtype, (LPUNKNOWN *)spUnk.getptr());
                    if (hr == S_OK)
                    {
                        ruleAction.m_ruleAction = RuleActionFromString(L"redirect");
                        ruleAction.m_wstrArg = L"";
                        hr = spUnk->GetProps((LPSPropTagArray) & propTags, fMapiUnicode, &cVals,
                            pPropVals.getptr());
                        if (pPropVals[0].ulPropTag == PR_DISPLAY_NAME_W)
                            ruleAction.m_wstrArg = pPropVals[0].Value.lpszW;
                        else if (pPropVals[1].ulPropTag == PR_EMAIL_ADDRESS_W)
                            ruleAction.m_wstrArg = pPropVals[1].Value.lpszW;
                        rule.AddAction(ruleAction);
                    }
                    else
                    {
                        //TRACE(_T("Unable to determine address"));
                        rule.SetActive(0);
                    }
                }
            }
            break;
        }

        case OP_REPLY:
        {
            //TRACE(_T("'have server reply using a specific message' action is not supported"));
            rule.SetActive(0);
            break;
        }

        case OP_TAG:
        {
            //TRACE(_T("'clear message\'s categories' action is not supported"));
            rule.SetActive(0);
            break;
        }

        case OP_DEFER_ACTION:
        {
            BYTE AssignToCategory[14] = {
                0x01, 0x80, 0x33, 0x01, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00
            };
            BYTE PermDelete[10] = { 0x01, 0x80, 0x4a, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
                                    0x00 };
            BYTE MarkAsRead[10] = { 0x01, 0x80, 0x4C, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
                                    0x00 };
            BYTE ClearMFlag[10] = { 0x01, 0x80, 0x32, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
                                    0x00 };
            BYTE PlayASound[6] = { 0x01, 0x80, 0xef, 0x00, 0x00, 0x00 };
            bool bRecognize = false;
            ULONG cbData = action.actDeferAction.cbData;
            LPBYTE lpData = action.actDeferAction.pbData;

            if (cbData > 0)
            {
                LPBYTE dataPtr = NULL;

                if (dataPtr = memmem(lpData, cbData, L"Follow up", 9))
                {
                    bRecognize = true;
                    dataPtr -= 17;
                    if (dataPtr[0] == 0x51)     // make sure it's really a Follow up
                    {
                        dataPtr += 12;
                        if (dataPtr[0] == 0x01) // Today
                        {
                            ruleAction.m_ruleAction = RuleActionFromString(L"flag");
                            ruleAction.m_wstrArg = L"flagged";
                            rule.AddAction(ruleAction);
                            //TRACE(_T(
                            //    "Deferred action 'flag message'.  If there are other non-deferred actions on this rule, you must re-add them."));
                        }
                        else
                        {
                            //TRACE(_T("Follow up actions other than today are not supported"));
                        }
                    }
                }
                if (dataPtr = memmem(lpData, cbData, AssignToCategory, 14))     // hokey
                {                               // right after the memory block we check for, the length of the category name is stored, followed by the name.
                                                                                // Increment the pointer to get to the length, get memory of one more than that length, zero it out, increment
                                                                                // the pointer again to get to the data, and copy it in.  Horrible.  Make sure to delete the memory.
                    bRecognize = true;
                    dataPtr += 14;

                    ULONG len = dataPtr[0];
                    LPWSTR pWTagname = new WCHAR[len + 1];

                    ZeroMemory(pWTagname, (len + 1) * sizeof (WCHAR));
                    dataPtr++;
                    memcpy(pWTagname, dataPtr, len * 2);
                    ruleAction.m_ruleAction = RuleActionFromString(L"tag");
                    ruleAction.m_wstrArg = pWTagname;
                    rule.AddAction(ruleAction);
                    delete[] pWTagname;
                    //TRACE(_T(
                    //    "Deferred action 'assign to category'.  If there are other non-deferred actions on this rule, you must re-add them."));
                }

                if (memmem(lpData, cbData, PermDelete, 10))     // hokey
                {
                    bRecognize = true;
                    ruleAction.m_ruleAction = RuleActionFromString(L"discard");
                    rule.AddAction(ruleAction);
                    //TRACE(_T(
                    //    "Deferred action 'permanently delete it'.  If there are other non-deferred actions on this rule, you must re-add them."));
                }
                if (memmem(lpData, cbData, MarkAsRead, 10))     // hokey
                {
                    bRecognize = true;
                    ruleAction.m_ruleAction = RuleActionFromString(L"flag");
                    ruleAction.m_wstrArg = L"read";
                    rule.AddAction(ruleAction);
                    //TRACE(_T(
                    //    "Deferred action 'mark as read'.  If there are other non-deferred actions on this rule, you must re-add them."));
                }
                if (memmem(lpData, cbData, ClearMFlag, 10))     // hokey
                {
                    bRecognize = true;
                    //TRACE(_T("clear message flag action not supported"));
                    rule.SetActive(0);
                }
                if (memmem(lpData, cbData, PlayASound, 6))      // hokey
                {
                    bRecognize = true;
                    //TRACE(_T("play a sound action not supported"));
                    rule.SetActive(0);
                }
                if (!bRecognize)
                {
                    //TRACE(_T("This client-only rule is not supported"));
                    bRetval = false;
                }
            }
            else
            {
                //TRACE(_T(
                //    "This client-only rule is not supported -- internal processing error"));
                bRetval = false;
            }
            break;
        }

        default:
            //TRACE(_T("Action type not supported"));
            bRetval = false;
        }
    }
    // if not processing additional rules, add the stop action
    if (!rule.GetProcessAdditionalRules())
    {
        RuleActionInfo stopAction;

        stopAction.m_ruleAction = RuleActionFromString(L"stop");
        rule.AddAction(stopAction);
    }
    return bRetval;
}
#pragma warning(pop)

LPBYTE CRuleProcessor::memmem(const void *buf, size_t buf_len, const void *byte_sequence, size_t
    byte_sequence_len)
{
    BYTE *bf = (BYTE *)buf;
    BYTE *bs = (BYTE *)byte_sequence;
    BYTE *p = bf;

    while (byte_sequence_len <= (buf_len - (p - bf)))
    {
        UINT b = *bs & 0xFF;

        if ((p = (BYTE *)memchr(p, b, buf_len - (p - bf))) != NULL)
        {
            if ((memcmp(p, byte_sequence, byte_sequence_len)) == 0)
                return p;
            else
                p++;
        }
        else
        {
            break;
        }
    }
    return NULL;
}

// CRuleMap class
CRuleMap::CRuleMap() {}

CRuleMap::~CRuleMap() {}

void CRuleMap::WriteFilterRule(CRule &rule, LPWSTR &filterRule)
{
    std::wstring wstrRuleName;
    std::wstring wstrRuleActive;

    filterRule[0] = L'\0';
    rule.GetName(wstrRuleName);
    long lActive = rule.GetActive();
    wstrRuleActive = (lActive == 0) ? L"0" : L"1";
    lstrcpy(filterRule, L"name,");
    lstrcat(filterRule, wstrRuleName.c_str());
    lstrcat(filterRule, L",active,");
    lstrcat(filterRule, wstrRuleActive.c_str());
}

void CRuleMap::WriteFilterTests(CRule &rule, LPWSTR &filterTests)
{
    filterTests[0] = L'\0';
    if ((rule.GetOrProcessed()) && (!rule.GetToOrCc()) && (!rule.GetNotOr()))
    {
        lstrcpy(filterTests, L"anyof:");
    }
    else
    {
        lstrcpy(filterTests, L"allof:");
    }

    CListRuleConditions listRuleConditions;
    rule.GetConditions(listRuleConditions);

    CListRuleConditions::iterator conditionIndex;
    size_t numConditions = listRuleConditions.size();
    size_t iIndex = 0;
    WCHAR pwszTemp[5];

    for (conditionIndex = listRuleConditions.begin();
        conditionIndex != listRuleConditions.end();
        conditionIndex++)
    {
        const RuleConditionInfo &ruleConditionInfo = *conditionIndex;
        std::wstring wstrConditionName;
        std::wstring wstrOpName;
        std::wstring wstrTestName;
        std::wstring wstrTest;
        std::wstring wstrValue;
        BOOL bNegative = FALSE;

        ConditionInfoToDetails(ruleConditionInfo, wstrConditionName, wstrOpName,
            wstrTestName, wstrTest, wstrValue, bNegative);
        // FBS bug 29159 -- 6/30/08 -- special case for header existence or non-existence
        if (ruleConditionInfo.m_ruleCondition == HeaderNamed)
        {
            if (ruleConditionInfo.m_ruleConditionOp == Exists)
                wstrConditionName = L"exists";
            else if (ruleConditionInfo.m_ruleConditionOp == DoesNotExist)
                wstrConditionName = L"not exists";
        }
        _itow((int)iIndex, pwszTemp, 10);
        if (0 == lstrcmpiW((LPCWSTR)wstrTestName.c_str(), L"headerTest"))
        {
            lstrcat(filterTests, L"headerTest`~index`~");
            lstrcat(filterTests, pwszTemp);
            lstrcat(filterTests, L"`~stringComparison`~");
            lstrcat(filterTests, wstrOpName.c_str());
            lstrcat(filterTests, L"`~header`~");
            lstrcat(filterTests, wstrTest.c_str());
            lstrcat(filterTests, L"`~value`~");
            lstrcat(filterTests, wstrValue.c_str());
            if (bNegative)
            {
                lstrcat(filterTests, L"`~negative`~1");
            }
        }
        if (0 == lstrcmpiW((LPCWSTR)wstrTestName.c_str(), L"headerExistsTest"))
        {
            lstrcat(filterTests, L"headerExistsTest`~index`~");
            lstrcat(filterTests, pwszTemp);
            lstrcat(filterTests, L"`~header`~");
            lstrcat(filterTests, wstrTest.c_str());
            if (bNegative)
            {
                lstrcat(filterTests, L"`~negative`~1");
            }
        }
        if (0 == lstrcmpiW((LPCWSTR)wstrTestName.c_str(), L"sizeTest"))
        {
            lstrcat(filterTests, L"sizeTest`~index`~");
            lstrcat(filterTests, pwszTemp);
            lstrcat(filterTests, L"`~numberComparison`~");
            lstrcat(filterTests, wstrOpName.c_str());
            lstrcat(filterTests, L"`~s`~");
            lstrcat(filterTests, wstrValue.c_str());
            if (bNegative)
            {
                lstrcat(filterTests, L"`~negative`~1");
            }
        }
        if (0 == lstrcmpiW((LPCWSTR)wstrTestName.c_str(), L"dateTest"))
        {
            lstrcat(filterTests, L"dateTest`~index`~");
            lstrcat(filterTests, pwszTemp);
            lstrcat(filterTests, L"`~dateComparison`~");
            lstrcat(filterTests, wstrOpName.c_str());
            lstrcat(filterTests, L"`~d`~");
            lstrcat(filterTests, wstrValue.c_str());
            if (bNegative)
            {
                lstrcat(filterTests, L"`~negative`~1");
            }
        }
        if (0 == lstrcmpiW((LPCWSTR)wstrTestName.c_str(), L"bodyTest"))
        {
            lstrcat(filterTests, L"bodyTest`~index`~");
            lstrcat(filterTests, pwszTemp);
            lstrcat(filterTests, L"`~value`~");
            lstrcat(filterTests, wstrValue.c_str());
            if (bNegative)
            {
                lstrcat(filterTests, L"`~negative`~1");
            }
        }
        if (0 == lstrcmpiW((LPCWSTR)wstrTestName.c_str(), L"attachmentTest"))
        {
            lstrcat(filterTests, L"attachmentTest`~index`~");
            lstrcat(filterTests, pwszTemp);
            if (bNegative)
            {
                lstrcat(filterTests, L"`~negative`~1");
            }
        }
        if (0 == lstrcmpiW((LPCWSTR)wstrTestName.c_str(), L"addressBookTest"))
        {
            lstrcat(filterTests, L"bodyTest`~index`~");
            lstrcat(filterTests, pwszTemp);
            lstrcat(filterTests, L"`~in`~");
            lstrcat(filterTests, wstrOpName.c_str());
            
            // FBS bug 51123, 51567 -- set the address book test to the correct English case-sensitive string
            std::wstring wstrHdrTest = L"From";
            if (0 == lstrcmpiW((LPCWSTR)wstrTest.c_str(), L"To"))
                wstrHdrTest = L"to";
            else if (0 == lstrcmpiW((LPCWSTR)wstrTest.c_str(), L"Cc"))
                wstrHdrTest = L"cc";
            else if (0 == lstrcmpiW((LPCWSTR)wstrTest.c_str(), L"Bcc"))
                wstrHdrTest = L"bcc";
            lstrcat(filterTests, L"`~header`~");
            lstrcat(filterTests, wstrHdrTest.c_str());
            lstrcat(filterTests, L"`~folderPath`~");
            lstrcat(filterTests, wstrValue.c_str());
            if (bNegative)
            {
                lstrcat(filterTests, L"`~negative`~1");
            }
        }
        if (0 == lstrcmpiW((LPCWSTR)wstrTestName.c_str(), L"inviteTest"))
        {
            lstrcat(filterTests, L"inviteTest`~index`~");
            lstrcat(filterTests, pwszTemp);
            lstrcat(filterTests, L"`~method`~");
            lstrcat(filterTests, wstrTest.c_str());
            if (bNegative)
            {
                lstrcat(filterTests, L"`~negative`~1");
            }
        }
        if (0 == lstrcmpiW((LPCWSTR)wstrTestName.c_str(), L"mimeHeaderTest"))
        {
            lstrcat(filterTests, L"mimeHeaderTest`~index`~");
            lstrcat(filterTests, pwszTemp);
            lstrcat(filterTests, L"`~stringComparison`~");
            lstrcat(filterTests, wstrOpName.c_str());
            lstrcat(filterTests, L"`~header`~");
            lstrcat(filterTests, wstrTest.c_str());
            lstrcat(filterTests, L"`~value`~");
            lstrcat(filterTests, wstrValue.c_str());
            if (bNegative)
            {
                lstrcat(filterTests, L"`~negative`~1");
            }
        }
        iIndex++;
        if (numConditions > iIndex)
        {
            lstrcat(filterTests, L"^^^");
        }
    }
}

void CRuleMap::WriteFilterActions(CRule &rule, LPWSTR &filterActions)
{
    filterActions[0] = L'\0';

    CListRuleActions listRuleActions;
    rule.GetActions(listRuleActions);

    CListRuleActions::iterator actionIndex;

    size_t numActions = listRuleActions.size();
    size_t iCount = 0;
    for (actionIndex = listRuleActions.begin(); actionIndex != listRuleActions.end();
        actionIndex++)
    {
        RuleActionInfo &actionInfo = *actionIndex;
        std::wstring wstrActionName;

        RuleActionToString(actionInfo.m_ruleAction, wstrActionName);
        ATLASSERT(wstrActionName.length());

        std::wstring wstrActionArg = L"";

        wstrActionArg = actionInfo.m_wstrArg;
        if (0 == lstrcmpiW((LPCWSTR)wstrActionName.c_str(), L"fileInto"))
        {
            lstrcat(filterActions, L"actionFileInto`~folderPath`~");
            lstrcat(filterActions, wstrActionArg.c_str());
        }
        if (0 == lstrcmpiW((LPCWSTR)wstrActionName.c_str(), L"redirect"))
        {
            lstrcat(filterActions, L"actionRedirect`~a`~");
            lstrcat(filterActions, wstrActionArg.c_str());
        }
        if (0 == lstrcmpiW((LPCWSTR)wstrActionName.c_str(), L"discard"))
        {
            lstrcat(filterActions, L"actionDiscard");
        }
        if (0 == lstrcmpiW((LPCWSTR)wstrActionName.c_str(), L"stop"))
        {
            lstrcat(filterActions, L"actionStop");
        }
        //else etc == do the rest of the actions

        iCount++;
        if (numActions > iCount)
        {
            lstrcat(filterActions, L"^^^");
        }
    }
}
