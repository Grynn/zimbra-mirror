#include "common.h"
#include "Exchange.h"
#include "ExchangeAdmin.h"
#include "MAPISession.h"
#include "MAPIAccessAPI.h"
#include "Logger.h"

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// Exception class
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

ExchangeAdminException::ExchangeAdminException(HRESULT hrErrCode, LPCWSTR
    lpszDescription): GenericException(hrErrCode, lpszDescription)
{
    //
}

ExchangeAdminException::ExchangeAdminException(HRESULT hrErrCode, LPCWSTR lpszDescription, int
    nLine, LPCSTR strFile): GenericException(hrErrCode, lpszDescription, nLine, strFile)
{
    //
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// Exchange Admin class
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
ExchangeAdmin::ExchangeAdmin(wstring strExchangeServer)
{
    m_pProfAdmin = NULL;
    m_strServer = strExchangeServer;
    try
    {
        Init();
    }
    catch (ExchangeAdminException &exc)
    {
        UNREFERENCED_PARAMETER(exc);
    }
}

ExchangeAdmin::~ExchangeAdmin()
{
    m_pProfAdmin->Release();
    MAPIUninitialize();
}

HRESULT ExchangeAdmin::Init()
{
    HRESULT hr = S_OK;
    Zimbra::Mapi::Memory::SetMemAllocRoutines(NULL, MAPIAllocateBuffer, MAPIAllocateMore,
        MAPIFreeBuffer);

    if (FAILED(hr = MAPIInitialize(NULL)))
        throw ExchangeAdminException(hr, L"Init(): MAPIInitialize Failed.", __LINE__, __FILE__);

    if (FAILED(hr = MAPIAdminProfiles(0, &m_pProfAdmin)))
        throw ExchangeAdminException(hr, L"Init(): MAPIAdminProfiles Failed.", __LINE__,
            __FILE__);
    return hr;
}

HRESULT ExchangeAdmin::CreateProfile(wstring strProfileName, wstring strMailboxName, wstring
    strPassword)
{
    HRESULT hr = S_OK;
    Zimbra::Util::ScopedBuffer<char> strServer;
    Zimbra::Util::ScopedBuffer<char> strMBName;
    Zimbra::Util::ScopedBuffer<char> strProfName;
    Zimbra::Util::ScopedBuffer<char> strProfPwd;
    Zimbra::Util::ScopedInterface<IMsgServiceAdmin> pSvcAdmin;
    Zimbra::Util::ScopedInterface<IMAPITable> pMsgSvcTable;
    Zimbra::Util::ScopedRowSet pSvcRows;
    SPropValue rgval[2] = { 0 };
    SPropValue sProps = { 0 };
    SRestriction sres;
    WCHAR errDescrption[256] = {};

    // Columns to get from HrQueryAllRows.
    enum { iSvcName, iSvcUID, cptaSvc };

    SizedSPropTagArray(cptaSvc, sptCols) = { cptaSvc, PR_SERVICE_NAME, PR_SERVICE_UID };
    WtoA((LPWSTR)strProfileName.c_str(), strProfName.getref());
    WtoA((LPWSTR)strPassword.c_str(), strProfPwd.getref());
    // create new profile
    if (FAILED(hr = m_pProfAdmin->CreateProfile((LPTSTR)strProfName.get(),
            (LPTSTR)strProfPwd.get(), NULL, 0)))
    {
        throw ExchangeAdminException(hr, L"CreateProfile(): CreateProfile Failed.", __LINE__,
            __FILE__);
    }
    // Get an IMsgServiceAdmin interface off of the IProfAdmin interface.
    if (FAILED(hr = m_pProfAdmin->AdminServices((LPTSTR)strProfName.get(),
            (LPTSTR)strProfPwd.get(), NULL, 0, pSvcAdmin.getptr())))
    {
        wcscpy(errDescrption, L"CreateProfile(): AdminServices Failed.");
        goto CRT_PROFILE_EXIT;
    }
    // Create the new message service for Exchange.
    if (FAILED(hr = pSvcAdmin->CreateMsgService((LPTSTR)"MSEMS", (LPTSTR)"MSEMS", NULL, NULL)))
    {
        wcscpy(errDescrption, L"CreateProfile(): CreateMsgService Failed.");
        goto CRT_PROFILE_EXIT;
    }
    // Need to obtain the entry id for the new service. This can be done by getting the message service table
    // and getting the entry that corresponds to the new service.
    if (FAILED(hr = pSvcAdmin->GetMsgServiceTable(0, pMsgSvcTable.getptr())))
    {
        wcscpy(errDescrption, L"CreateProfile(): GetMsgServiceTable Failed.");
        goto CRT_PROFILE_EXIT;
    }
    sres.rt = RES_CONTENT;
    sres.res.resContent.ulFuzzyLevel = FL_FULLSTRING;
    sres.res.resContent.ulPropTag = PR_SERVICE_NAME;
    sres.res.resContent.lpProp = &sProps;

    sProps.ulPropTag = PR_SERVICE_NAME;
    sProps.Value.lpszA = "MSEMS";
    // Query the table to obtain the entry for the newly created message service.
    if (FAILED(hr = HrQueryAllRows(pMsgSvcTable.get(), (LPSPropTagArray) & sptCols, NULL, NULL,
            0, pSvcRows.getptr())))
    {
        wcscpy(errDescrption, L"CreateProfile(): HrQueryAllRows Failed.");
        goto CRT_PROFILE_EXIT;
    }
    // Set up a SPropValue array for the properties that you have to configure.
    if (pSvcRows->cRows > 0)
    {
        // First, the exchange server name.
        ZeroMemory(&rgval[0], sizeof (SPropValue));
        rgval[0].ulPropTag = PR_PROFILE_UNRESOLVED_SERVER;
        WtoA((LPWSTR)m_strServer.c_str(), strServer.getref());
        rgval[0].Value.lpszA = (LPSTR)strServer.get();

        // Next, the user's AD name.
        ZeroMemory(&rgval[1], sizeof (SPropValue));
        rgval[1].ulPropTag = PR_PROFILE_UNRESOLVED_NAME;
        WtoA((LPWSTR)strMailboxName.c_str(), strMBName.getref());
        rgval[1].Value.lpszA = (LPSTR)strMBName.get();

        // Configure the message service by using the previous properties.
        // int trials = 10;
        int trials = 7;
        int itrTrials = 0;

        hr = 0x81002746;                        // WSAECONNRESET
        while ((hr == 0x81002746) && (itrTrials < trials))
        {
            hr = pSvcAdmin->ConfigureMsgService(
                (LPMAPIUID)pSvcRows->aRow->lpProps[iSvcUID].Value.bin.lpb, NULL, 0, 2, rgval);
            //if (hr == 0x81002746)
                // Sleep(30000);
                //Sleep(10000);
            itrTrials++;
        }
        if (FAILED(hr))
        {
            /* =
             * pSvcAdmin->ConfigureMsgService((LPMAPIUID)pSvcRows->aRow->lpProps[iSvcUID].
             *   Value.bin.lpb,NULL, 0, 2, rgval)))*/
            wcscpy(errDescrption, L"CreateProfile(): ConfigureMsgService Failed.");
            goto CRT_PROFILE_EXIT;
        }
    }
CRT_PROFILE_EXIT: 
	if (hr != S_OK)
    {
        DeleteProfile(strProfileName);
        throw ExchangeAdminException(hr, errDescrption, __LINE__, __FILE__);
    }
	else
	{
		//Create supporting OL profile entries else crash may happen!
		if(!Zimbra::MAPI::Util::SetOLProfileRegistryEntries(strProfileName.c_str()))
		{
			throw ExchangeAdminException(hr, L"ExchangeAdmin::CreateProfile()::SetOLProfileRegistryEntries Failed.",
				__LINE__, __FILE__);
		}
	}
    return hr;
}

HRESULT ExchangeAdmin::DeleteProfile(wstring strProfile)
{
    HRESULT hr = S_OK;
    Zimbra::Util::ScopedBuffer<char> strProfName;

    WtoA((LPWSTR)strProfile.c_str(), strProfName.getref());
    // delete profile
    if (FAILED(hr = m_pProfAdmin->DeleteProfile((LPTSTR)strProfName.get(), 0)) && (hr !=
        MAPI_E_NOT_FOUND))
        throw ExchangeAdminException(hr, L"DeleteProfile(): DeleteProfile Failed.", __LINE__,
            __FILE__);
    return hr;
}

HRESULT ExchangeAdmin::GetAllProfiles(vector<string> &vProfileList)
{
    HRESULT hr = S_OK;
    Zimbra::Util::ScopedInterface<IMAPITable> pProftable;

    // get profile table
    if ((hr = m_pProfAdmin->GetProfileTable(0, pProftable.getptr())) == S_OK)
    {
        SizedSPropTagArray(3, proftablecols) = {
            3, { PR_DISPLAY_NAME_A, PR_DEFAULT_PROFILE, PR_SERVICE_NAME }
        };

        Zimbra::Util::ScopedRowSet profrows;

        // get all profile rows
        if ((hr = HrQueryAllRows(pProftable.get(), (SPropTagArray *)&proftablecols, NULL, NULL,
                0, profrows.getptr())) == S_OK)
        {
            for (unsigned int i = 0; i < profrows->cRows; i++)
            {
                if (profrows->aRow[i].lpProps[0].ulPropTag == PR_DISPLAY_NAME_A)
                {
                    Zimbra::Util::ScopedInterface<IMsgServiceAdmin> spServiceAdmin;
                    Zimbra::Util::ScopedInterface<IMAPITable> spServiceTable;
                    string strpname = profrows->aRow[i].lpProps[0].Value.lpszA;

                    // get profile's admin service
                    hr = m_pProfAdmin->AdminServices((LPTSTR)strpname.c_str(), NULL, NULL, 0,
                        spServiceAdmin.getptr());
                    if (FAILED(hr))
                        throw ExchangeAdminException(hr,
                            L"GetAllProfiles(): AdminServices Failed.", __LINE__, __FILE__);
                    // get message service table
                    hr = spServiceAdmin->GetMsgServiceTable(0, spServiceTable.getptr());
                    if (FAILED(hr))
                    {
                        throw ExchangeAdminException(hr,
                            L"GetAllProfiles(): GetMsgServiceTable Failed.", __LINE__,
                            __FILE__);
                    }
                    // lets get the service name and the service uid for the primary service
                    SizedSPropTagArray(2, tags) = {
                        2, { PR_SERVICE_NAME, PR_SERVICE_UID }
                    };
                    spServiceTable->SetColumns((LPSPropTagArray) & tags, 0);

                    DWORD dwCount = 0;

                    hr = spServiceTable->GetRowCount(0, &dwCount);
                    if (FAILED(hr))
                        throw ExchangeAdminException(hr,
                            L"GetAllProfiles(): GetRowCount Failed.", __LINE__, __FILE__);
                    else if (!dwCount)
                        return hr;

                    Zimbra::Util::ScopedRowSet pRows;

                    hr = spServiceTable->QueryRows(dwCount, 0, pRows.getptr());
                    if (FAILED(hr))
                        throw ExchangeAdminException(hr, L"GetAllProfiles(): QueryRows Failed.",
                            __LINE__, __FILE__);
                    for (ULONG j = 0; j < pRows->cRows; j++)
                    {
                        if (PR_SERVICE_NAME == pRows->aRow[j].lpProps[0].ulPropTag)
                        {
                            // if MSExchange service
                            if (0 == lstrcmpiW(pRows->aRow[j].lpProps[0].Value.LPSZ, L"MSEMS"))
                            {
                                if (profrows->aRow[i].lpProps[0].ulPropTag == PR_DISPLAY_NAME_A)
                                    vProfileList.push_back(
                                        profrows->aRow[i].lpProps[0].Value.lpszA);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    return hr;
}

HRESULT ExchangeAdmin::SetDefaultProfile(wstring strProfile)
{
    HRESULT hr = S_OK;

    if ((hr = m_pProfAdmin->SetDefaultProfile((LPTSTR)strProfile.c_str(), 0)) != S_OK)
        throw ExchangeAdminException(hr, L"SetDefaultProfile(): SetDefaultProfile Failed.",
            __LINE__, __FILE__);
    return hr;
}

HRESULT ExchangeAdmin::CreateExchangeMailBox(LPCWSTR lpwstrNewUser, LPCWSTR lpwstrNewUserPwd,
    LPCWSTR lpwstrlogonuser, LPCWSTR lpwstrLogonUsrPwd)
{
    HRESULT hr = S_OK;

    // Get Logon user DN
    wstring LogonUserDN;
    wstring legacyName;
    Zimbra::MAPI::Util::GetUserDNAndLegacyName(m_strServer.c_str(), lpwstrlogonuser,
        lpwstrLogonUsrPwd, LogonUserDN, legacyName);
    Zimbra::Util::ScopedInterface<IDirectoryObject> pLogonContainer;
    wstring strContainer = L"LDAP://";

    strContainer += LogonUserDN.c_str();

    // Get loggedin user container
    hr = ADsOpenObject(strContainer.c_str(), NULL, NULL, ADS_SECURE_AUTHENTICATION,
        IID_IDirectoryObject, (void **)pLogonContainer.getptr());
    if (FAILED(hr))
    {
        if (hr == 0x8007052e)                   // credentials are not valid
        {
            hr = ADsOpenObject((LPTSTR)strContainer.c_str(), lpwstrlogonuser, lpwstrLogonUsrPwd,
                ADS_SECURE_AUTHENTICATION, IID_IDirectoryObject, (void **)&pLogonContainer);
            if (FAILED(hr))
                throw ExchangeAdminException(hr,
                    L"CreateExchangeMailBox(): ADsOpenObject Failed.", __LINE__, __FILE__);
        }
        else
        {
            throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): ADsOpenObject Failed.",
                __LINE__, __FILE__);
        }
    }

    ADS_ATTR_INFO *pAttrInfo = NULL;
    DWORD dwReturn;
    LPWSTR pAttrNames[] = { L"mail", L"homeMDB", L"homeMTA" };
    DWORD dwNumAttr = sizeof (pAttrNames) / sizeof (LPWSTR);
    wstring strLogonHomeMDB;
    wstring strLogonHomeMTA;
    wstring strLogonMail;

    // Get attribute values requested. Its not necessary the order is same as requested.
    if (FAILED(hr = pLogonContainer->GetObjectAttributes(pAttrNames, dwNumAttr, &pAttrInfo,
            &dwReturn)))
        throw ExchangeAdminException(hr,
            L"CreateExchangeMailBox(): GetObjectAttributes Failed.", __LINE__, __FILE__);
    for (DWORD idx = 0; idx < dwReturn; idx++)
    {
        if (_wcsicmp(pAttrInfo[idx].pszAttrName, L"mail") == 0)
            strLogonMail = pAttrInfo[idx].pADsValues->Email.Address;

        else if (_wcsicmp(pAttrInfo[idx].pszAttrName, L"homeMTA") == 0)
            strLogonHomeMTA = pAttrInfo[idx].pADsValues->DNString;

        else if (_wcsicmp(pAttrInfo[idx].pszAttrName, L"homeMDB") == 0)
            strLogonHomeMDB = pAttrInfo[idx].pADsValues->DNString;
    }
    // Use FreeADsMem for all memory obtained from the ADSI call.
    FreeADsMem(pAttrInfo);

    wstring twtsrlogonuserDN = LogonUserDN;
    size_t nPos = twtsrlogonuserDN.find(_T("DC="), 0);
    wstring wstrServerDN = twtsrlogonuserDN.substr(nPos);
    wstring wstrADSPath = _T("LDAP://CN=Users,") + wstrServerDN;
    ADSVALUE cnValue;
    ADSVALUE classValue;
    ADSVALUE sAMValue;
    ADSVALUE uPNValue;
    ADS_ATTR_INFO attrInfo[] = {
        { L"objectClass", ADS_ATTR_UPDATE, ADSTYPE_CASE_IGNORE_STRING, &classValue, 1 },
        { L"cn", ADS_ATTR_UPDATE, ADSTYPE_CASE_IGNORE_STRING, &cnValue, 1 },
        { L"sAMAccountName", ADS_ATTR_UPDATE, ADSTYPE_CASE_IGNORE_STRING, &sAMValue, 1 },
        { L"userPrincipalName", ADS_ATTR_UPDATE, ADSTYPE_CASE_IGNORE_STRING, &uPNValue, 1 },
    };
    DWORD dwAttrs = sizeof (attrInfo) / sizeof (ADS_ATTR_INFO);

    classValue.dwType = ADSTYPE_CASE_IGNORE_STRING;
    classValue.CaseIgnoreString = L"user";

    cnValue.dwType = ADSTYPE_CASE_IGNORE_STRING;
    cnValue.CaseIgnoreString = (LPWSTR)lpwstrNewUser;

    sAMValue.dwType = ADSTYPE_CASE_IGNORE_STRING;
    sAMValue.CaseIgnoreString = (LPWSTR)lpwstrNewUser;

    wstring wstrMail;
    size_t nPosMail = strLogonMail.find(_T("@"), 0);

    wstrMail = strLogonMail.substr(nPosMail);
    wstrMail = lpwstrNewUser + wstrMail;

    LPWSTR upnval = (LPWSTR)wstrMail.c_str();

    uPNValue.dwType = ADSTYPE_CASE_IGNORE_STRING;
    uPNValue.CaseIgnoreString = upnval;

    Zimbra::Util::ScopedInterface<IDirectoryObject> pDirContainer;
    Zimbra::Util::ScopedInterface<IDispatch> pDisp;
    Zimbra::Util::ScopedInterface<IADsUser> pIADNewUser;
    wstring wstrLoggedUserName(LogonUserDN);
    size_t snPos = 0;
    size_t enPos = 0;

    if ((snPos = wstrLoggedUserName.find(L"CN=")) != wstring::npos)
    {
        if ((enPos = wstrLoggedUserName.find(L",", snPos)) != wstring::npos)
            wstrLoggedUserName = wstrLoggedUserName.substr(snPos + 3, (enPos - (snPos + 3)));
    }
    // get dir container
    if (FAILED(hr = ADsOpenObject(wstrADSPath.c_str(), wstrLoggedUserName.c_str(),
            lpwstrLogonUsrPwd, ADS_SECURE_AUTHENTICATION, IID_IDirectoryObject,
            (void **)pDirContainer.getptr())))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): ADsOpenObject Failed.",
            __LINE__, __FILE__);

    wstring wstrUserCN = L"CN=";

    wstrUserCN += lpwstrNewUser;
    if (FAILED(hr = pDirContainer->CreateDSObject((LPWSTR)wstrUserCN.c_str(), attrInfo, dwAttrs,
            pDisp.getptr())))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): CreateDSObject Failed.",
            __LINE__, __FILE__);
    if (FAILED(hr = pDisp->QueryInterface(IID_IADsUser, (void **)pIADNewUser.getptr())))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): QueryInterface Failed.",
            __LINE__, __FILE__);

    CComVariant varProp;

    varProp.Clear();

    // set samAccount
    varProp = lpwstrNewUser;
    if (FAILED(hr = pIADNewUser->Put(CComBSTR(L"sAMAccountName"), varProp)))
        throw ExchangeAdminException(hr,
            L"CreateExchangeMailBox(): Put(sAMAccountName) Failed.", __LINE__, __FILE__);
    // set userAccountControl
    varProp.Clear();
    hr = pIADNewUser->Get(CComBSTR(L"userAccountControl"), &varProp);
    varProp = varProp.lVal & ~(ADS_UF_ACCOUNTDISABLE);
    if (FAILED(hr = pIADNewUser->Put(CComBSTR(L"userAccountControl"), varProp)))
        throw ExchangeAdminException(hr,
            L"CreateExchangeMailBox(): Put(userAccountControl) Failed.", __LINE__, __FILE__);
    // set Account enabled
    if (FAILED(hr = pIADNewUser->put_AccountDisabled(VARIANT_FALSE)))
    {
        throw
            ExchangeAdminException(hr, L"CreateExchangeMailBox(): put_AccountDisabled Failed.",
            __LINE__, __FILE__);
    }
    // set password
    if (FAILED(hr = pIADNewUser->SetPassword(CComBSTR(lpwstrNewUserPwd))))
    {
        throw
            ExchangeAdminException(hr, L"CreateExchangeMailBox(): SetPassword Failed.",
            __LINE__, __FILE__);
    }
    // user account password does not expire
    varProp.Clear();

    VARIANT var;

    VariantInit(&var);
    if (!FAILED(hr = pIADNewUser->Get(CComBSTR(L"userAccountControl"), &var)))
    {
        V_I4(&var) |= ADS_UF_DONT_EXPIRE_PASSWD;
        if (FAILED(hr = pIADNewUser->Put(CComBSTR(L"userAccountControl"), var)))
        {
            throw ExchangeAdminException(hr,
                L"CreateExchangeMailBox(): Put(userAccountControl) Failed.", __LINE__,
                __FILE__);
        }
    }
    // set the homeMDB;
    if (!strLogonHomeMDB.empty())
    {
        varProp = strLogonHomeMDB.c_str();
        if (FAILED(hr = pIADNewUser->Put(CComBSTR("homeMDB"), varProp)))
            throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put(homeMDB) Failed.",
                __LINE__, __FILE__);
    }
    if (!strLogonHomeMTA.empty())
    {
        varProp = strLogonHomeMTA.c_str();
        if (FAILED(hr = pIADNewUser->Put(CComBSTR("homeMTA"), varProp)))
            throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put(homeMTA) Failed.",
                __LINE__, __FILE__);
    }
    // set nickname
    varProp.Clear();
    varProp = lpwstrNewUser;
    if (FAILED(hr = pIADNewUser->Put(CComBSTR("mailNickname"), varProp)))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put(mailNickname) Failed.",
            __LINE__, __FILE__);
    // set the displayName
    varProp.Clear();
    varProp = lpwstrNewUser;
    if (FAILED(hr = pIADNewUser->Put(CComBSTR("displayName"), varProp)))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put(displayName) Failed.",
            __LINE__, __FILE__);
    // set the mail atrribute
    varProp.Clear();
    varProp = wstrMail.c_str();
    if (FAILED(hr = pIADNewUser->Put(CComBSTR("mail"), varProp)))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put(mail) Failed.",
            __LINE__, __FILE__);
    // set email
    if (FAILED(hr = pIADNewUser->put_EmailAddress(CComBSTR(wstrMail.c_str()))))
    {
        throw
            ExchangeAdminException(hr, L"CreateExchangeMailBox(): put_EmailAddress Failed.",
            __LINE__, __FILE__);
    }

    // add to Domain Admins group
    BSTR bstrADSPath;

    if (FAILED(hr = pIADNewUser->get_ADsPath(&bstrADSPath)))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): get_ADsPath Failed.",
            __LINE__, __FILE__);

    wstring wstrGroup = _T("LDAP://CN=Domain Admins,CN=Users,") + wstrServerDN;
    Zimbra::Util::ScopedInterface<IADsGroup> pGroup;

    if (FAILED(hr = ADsGetObject(wstrGroup.c_str(), IID_IADsGroup, (void **)pGroup.getptr())))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): ADsGetObject Failed.",
            __LINE__, __FILE__);
    if (FAILED(hr = ADsOpenObject(wstrGroup.c_str(), wstrLoggedUserName.c_str(),
            lpwstrLogonUsrPwd, ADS_SECURE_AUTHENTICATION, IID_IADsGroup,
            (void **)pGroup.getptr())))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): ADsOpenObject Failed.",
            __LINE__, __FILE__);
    if (SUCCEEDED(hr = pGroup->Add(bstrADSPath)))
    {
        if (FAILED(hr = pGroup->SetInfo()))
            throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): pGroup SetInfo Failed.",
                __LINE__, __FILE__);
    }
    else
    {
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): pGroup Add Failed.",
            __LINE__, __FILE__);
    }
    // Commit the change to the directory.
    if (FAILED(hr = pIADNewUser->SetInfo()))
        throw ExchangeAdminException(hr,
            L"CreateExchangeMailBox(): pIADNewUser SetInfo Failed.", __LINE__, __FILE__);
    return hr;
}

HRESULT ExchangeAdmin::DeleteExchangeMailBox(LPCWSTR lpwstrMailBox, LPCWSTR lpwstrlogonuser,
    LPCWSTR lpwstrLogonUsrPwd)
{
    HRESULT hr;
    wstring UserDN;
    wstring LegacyName;
    Zimbra::Util::ScopedInterface<IDirectoryObject> pDirContainer;

    try
    {
        Zimbra::MAPI::Util::GetUserDNAndLegacyName(m_strServer.c_str(), lpwstrlogonuser,
            lpwstrLogonUsrPwd, UserDN, LegacyName);
    }
    catch (Zimbra::MAPI::ExchangeAdminException &ex)
    {
        // To do .. logs entry
        UNREFERENCED_PARAMETER(ex);
        throw;
    }
    catch (Zimbra::MAPI::Util::MapiUtilsException &ex)
    {
        UNREFERENCED_PARAMETER(ex);
        // To do .. logs entry
        throw;
    }
    wstring twtsrlogonuserDN = UserDN;
    size_t nPos = twtsrlogonuserDN.find(_T("DC="), 0);
    wstring wstrServerDN = twtsrlogonuserDN.substr(nPos);
    wstring wstrADSPath = _T("LDAP://CN=Users,") + wstrServerDN;

    // get dir container
    if (FAILED(hr = ADsOpenObject(wstrADSPath.c_str(), lpwstrlogonuser, lpwstrLogonUsrPwd,
            ADS_SECURE_AUTHENTICATION, IID_IDirectoryObject, (void **)pDirContainer.getptr())))
        throw ExchangeAdminException(hr, L"DeleteExchangeMailBox(): ADsOpenObject Failed.",
            __LINE__, __FILE__);

    wstring mailboxcn = L"CN=";

    mailboxcn += lpwstrMailBox;
    hr = pDirContainer->DeleteDSObject((LPWSTR)mailboxcn.c_str());

    return hr;
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// ExchangeMigrationSetup
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
ExchangeMigrationSetup::ExchangeMigrationSetup(LPCWSTR strExhangeHost, LPCWSTR
    ExchangeAdminName, LPCWSTR ExchangeAdminPwd)
{
    m_strServer = strExhangeHost;
    m_ExchangeAdminName = ExchangeAdminName;
    m_ExchangeAdminPwd = ExchangeAdminPwd;
    m_exchAdmin = new ExchangeAdmin(m_strServer);
}

ExchangeMigrationSetup::~ExchangeMigrationSetup()
{
    delete m_exchAdmin;
}

HRESULT ExchangeMigrationSetup::Setup()
{
    Clean();

    try
    {
        m_exchAdmin->CreateExchangeMailBox(DEFAULT_ADMIN_MAILBOX_NAME, DEFAULT_ADMIN_PASSWORD,
            m_ExchangeAdminName.c_str(), m_ExchangeAdminPwd.c_str());
    }
    catch (Zimbra::MAPI::ExchangeAdminException &ex)
    {
        // To do .. logs entry
        UNREFERENCED_PARAMETER(ex);
        throw;
    }
    catch (Zimbra::MAPI::Util::MapiUtilsException &ex)
    {
        // To do .. logs entry
        UNREFERENCED_PARAMETER(ex);
        throw;
    }
    try
    {
        m_exchAdmin->CreateProfile(DEFAULT_ADMIN_PROFILE_NAME, DEFAULT_ADMIN_MAILBOX_NAME,
            DEFAULT_ADMIN_PASSWORD);
    }
    catch (Zimbra::MAPI::ExchangeAdminException &ex)
    {
        // To do .. logs entry
        UNREFERENCED_PARAMETER(ex);
        throw;
    }
    return S_OK;
}

HRESULT ExchangeMigrationSetup::Clean()
{
    try
    {
        m_exchAdmin->DeleteProfile(DEFAULT_ADMIN_PROFILE_NAME);
    }
    catch (Zimbra::MAPI::ExchangeAdminException &ex)
    {
        // To do.. log entry
        UNREFERENCED_PARAMETER(ex);
        throw;
    }
    try
    {
        m_exchAdmin->DeleteExchangeMailBox(DEFAULT_ADMIN_MAILBOX_NAME,
            m_ExchangeAdminName.c_str(), m_ExchangeAdminPwd.c_str());
    }
    catch (Zimbra::MAPI::ExchangeAdminException &ex)
    {
        // To do .. logs entry
        UNREFERENCED_PARAMETER(ex);
        throw;
    }
    catch (Zimbra::MAPI::Util::MapiUtilsException &ex)
    {
        // To do .. logs entry
        UNREFERENCED_PARAMETER(ex);
        throw;
    }
    return S_OK;
}

HRESULT ExchangeMigrationSetup::GetAllProfiles(vector<string> &vProfileList)
{
    return m_exchAdmin->GetAllProfiles(vProfileList);
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// ExchangeOps
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
int ExchangeOps::Initialized = EXCH_UNINITIALIZED;
ExchangeMigrationSetup *ExchangeOps::m_exchmigsetup = NULL;
MAPISession *ExchangeOps::m_zmmapisession = NULL;
LPCWSTR ExchangeOps::GlobalInit(LPCWSTR lpMAPITarget, LPCWSTR lpAdminUsername, LPCWSTR
    lpAdminPassword)
{
    LPCWSTR lpwstrStatus = NULL;

    // if lpAdminUsername is NULL then we assume that Outlook admin profile exists and we should use it
    // else create a Admin mailbox and create corresponding profile on local machine
    if (lstrlen(lpAdminUsername) > 0)
    {
        if (Initialized == EXCH_UNINITIALIZED)
        {
            m_exchmigsetup = new ExchangeMigrationSetup(lpMAPITarget, lpAdminUsername,
                lpAdminPassword);
            Initialized = EXCH_INITIALIZED_PROFCREATE;
        }
        try
        {
            m_exchmigsetup->Setup();
        }
        catch (Zimbra::MAPI::ExchangeAdminException &ex)
        {
            lpwstrStatus = FormatExceptionInfo(ex.ErrCode(), (LPWSTR)ex.Description().c_str(),
                (LPSTR)ex.SrcFile().c_str(), ex.SrcLine());
        }
        catch (Zimbra::MAPI::Util::MapiUtilsException &ex)
        {
            lpwstrStatus = FormatExceptionInfo(ex.ErrCode(), (LPWSTR)ex.Description().c_str(),
                (LPSTR)ex.SrcFile().c_str(), ex.SrcLine());
        }
    }
    // Create Session and Open admin store with profile
    try
    {
        lpwstrStatus = MAPIAccessAPI::InitGlobalSessionAndStore(lpMAPITarget);
    }
    catch (...)
    {
        lpwstrStatus = L"Unexpected Initialization Exception";
		return lpwstrStatus;
    }
    if (!lpwstrStatus)
        Initialized = EXCH_INITIALIZED_PROFEXIST;
    return lpwstrStatus;
}

LPCWSTR ExchangeOps::GlobalUninit()
{
    LPCWSTR lpwstrStatus = NULL;

    if (Initialized == EXCH_INITIALIZED_PROFCREATE)
    {
        try
        {
            m_exchmigsetup->Clean();
            delete m_exchmigsetup;
        }
        catch (Zimbra::MAPI::ExchangeAdminException &ex)
        {
            lpwstrStatus = FormatExceptionInfo(ex.ErrCode(), (LPWSTR)ex.Description().c_str(),
                (LPSTR)ex.SrcFile().c_str(), ex.SrcLine());
        }
        catch (Zimbra::MAPI::Util::MapiUtilsException &ex)
        {
            lpwstrStatus = FormatExceptionInfo(ex.ErrCode(), (LPWSTR)ex.Description().c_str(),
                (LPSTR)ex.SrcFile().c_str(), ex.SrcLine());
        }
    }
    else
	if (Initialized != EXCH_UNINITIALIZED)
    {
        MAPIAccessAPI::UnInitGlobalSessionAndStore();
    }
    if (m_zmmapisession)
    {
        delete m_zmmapisession;
        m_zmmapisession = NULL;
    }
    Initialized = EXCH_UNINITIALIZED;

    return lpwstrStatus;
}

LPCWSTR ExchangeOps::SelectExchangeUsers(vector<ObjectPickerData> &vUserList)
{
    LPCWSTR lpwstrStatus = NULL;

    try
    {
        Zimbra::MAPI::Util::GetExchangeUsersUsingObjectPicker(vUserList);
    }
    catch (Zimbra::MAPI::Util::MapiUtilsException &ex)
    {
        lpwstrStatus = FormatExceptionInfo(ex.ErrCode(), (LPWSTR)ex.Description().c_str(),
            (LPSTR)ex.SrcFile().c_str(), ex.SrcLine());
    }
    return lpwstrStatus;
}
