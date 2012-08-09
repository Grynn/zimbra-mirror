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

ExchangeAdminException::ExchangeAdminException(HRESULT hrErrCode, LPCWSTR lpszDescription, LPCWSTR lpszShortDescription, 
	int nLine, LPCSTR strFile): GenericException(hrErrCode, lpszDescription, lpszShortDescription, nLine, strFile)
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
        dloge("ExchangeAdmin::ExchangeAdmin exception: %S", exc.Description().c_str());
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
        throw ExchangeAdminException(hr, L"Init(): MAPIInitialize Failed.", 
		ERR_GEN_EXCHANGEADMIN, __LINE__, __FILE__);

    if (FAILED(hr = MAPIAdminProfiles(0, &m_pProfAdmin)))
        throw ExchangeAdminException(hr, L"Init(): MAPIAdminProfiles Failed.", 
		ERR_GEN_EXCHANGEADMIN, __LINE__, __FILE__);
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
        throw ExchangeAdminException(hr, L"CreateProfile(): CreateProfile Failed.", 
			ERR_CREATE_EXCHPROFILE, __LINE__, __FILE__);
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
        int trials = 2;
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
        throw ExchangeAdminException(hr, errDescrption, ERR_CREATE_EXCHPROFILE, __LINE__, __FILE__);
    }
	else
	{
		//Create supporting OL profile entries else crash may happen!
		if(!Zimbra::MAPI::Util::SetOLProfileRegistryEntries(strProfileName.c_str()))
		{
			throw ExchangeAdminException(hr, L"ExchangeAdmin::CreateProfile()::SetOLProfileRegistryEntries Failed.",
				ERR_CREATE_EXCHPROFILE, __LINE__, __FILE__);
		}
/*		Zimbra::Util::ScopedBuffer<char> strProfName;
		WtoA((LPWSTR)strProfileName.c_str(), strProfName.getref());
		hr=m_pProfAdmin->SetDefaultProfile((LPTSTR)strProfName.get(),NULL);
*/
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
        throw ExchangeAdminException(hr, L"DeleteProfile(): DeleteProfile Failed.",
		ERR_DELETE_PROFILE, __LINE__, __FILE__);
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
                        throw ExchangeAdminException(hr,L"GetAllProfiles(): AdminServices Failed.",
						ERR_GETALL_PROFILE, __LINE__, __FILE__);
                    // get message service table
                    hr = spServiceAdmin->GetMsgServiceTable(0, spServiceTable.getptr());
                    if (FAILED(hr))
                    {
                        throw ExchangeAdminException(hr,L"GetAllProfiles(): GetMsgServiceTable Failed.",
							ERR_GETALL_PROFILE, __LINE__, __FILE__);
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
                            L"GetAllProfiles(): GetRowCount Failed.",
							ERR_GETALL_PROFILE, __LINE__, __FILE__);
                    else if (!dwCount)
                        return hr;

                    Zimbra::Util::ScopedRowSet pRows;

                    hr = spServiceTable->QueryRows(dwCount, 0, pRows.getptr());
                    if (FAILED(hr))
                        throw ExchangeAdminException(hr, L"GetAllProfiles(): QueryRows Failed.",
                            ERR_GETALL_PROFILE, __LINE__, __FILE__);
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
            ERR_SET_DEFPROFILE, __LINE__, __FILE__);
    return hr;
}

void ThrowSetInfoException(HRESULT hr, LPWSTR wstrmsg)
{
    dloge("SetInfoException : %S",wstrmsg);
    throw ExchangeAdminException(hr,wstrmsg, ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
}
BOOL PutBinaryIntoVariant(CComVariant * ovData, BYTE * pBuf,unsigned long cBufLen)
{
	BOOL fRetVal = FALSE;
	VARIANT var;
	VariantInit(&var); //Initialize our variant
	//Set the type to an array of unsigned chars (OLE SAFEARRAY)
	var.vt = VT_ARRAY | VT_UI1;
	//Set up the bounds structure
	SAFEARRAYBOUND rgsabound[1];
	rgsabound[0].cElements = cBufLen;
	rgsabound[0].lLbound = 0;
	//Create an OLE SAFEARRAY
	var.parray = SafeArrayCreate(VT_UI1,1,rgsabound);
	if(var.parray != NULL)
	{
		void * pArrayData = NULL;
		//Get a safe pointer to the array
		SafeArrayAccessData(var.parray,&pArrayData);
		//Copy data to it
		memcpy(pArrayData, pBuf, cBufLen);
		//Unlock the variant data
		SafeArrayUnaccessData(var.parray);
		*ovData = var;
		// Create a COleVariant based on our variant
		VariantClear(&var);
		fRetVal = TRUE;
	}
	return fRetVal;
}

HRESULT ExchangeAdmin::CreateExchangeMailBox(LPCWSTR lpwstrNewUser, LPCWSTR lpwstrNewUserPwd,
    LPCWSTR lpwstrlogonuser, LPCWSTR lpwstrLogonUsrPwd)
{
    HRESULT hr = S_OK;

    // Get Logon user DN
    wstring LogonUserDN;
    wstring legacyName;
	wstring msExchHomeSvrName;
    Zimbra::MAPI::Util::GetUserDNAndLegacyName(m_strServer.c_str(), lpwstrlogonuser,
        lpwstrLogonUsrPwd, LogonUserDN, legacyName);
	Zimbra::MAPI::Util::GetmsExchHomeServerName(m_strServer.c_str(), lpwstrlogonuser,
        lpwstrLogonUsrPwd, msExchHomeSvrName);
    Zimbra::Util::ScopedInterface<IDirectoryObject> pLogonContainer;
	Zimbra::Util::ScopedInterface<IADsUser> pIAdUser;
	Zimbra::Util::ScopedInterface<IADs> pIAds;
    wstring strContainer = L"LDAP://";

    strContainer += LogonUserDN.c_str();

    dloge("strContainer %S  msExchHomeSvrName: %S", strContainer.c_str(), msExchHomeSvrName.c_str());
    // Get loggedin user container
    hr = ADsOpenObject(strContainer.c_str(), NULL, NULL, ADS_SECURE_AUTHENTICATION,
        IID_IDirectoryObject, (void **)pLogonContainer.getptr());
    if (FAILED(hr))
    {
        if (hr == 0x8007052e)                   // credentials are not valid
        {
            hr = ADsOpenObject((LPTSTR)strContainer.c_str(), lpwstrlogonuser, lpwstrLogonUsrPwd,
				ADS_SECURE_AUTHENTICATION, IID_IDirectoryObject, (void **)pLogonContainer.getptr());
			if (FAILED(hr)||(pLogonContainer.get()==NULL))
                throw ExchangeAdminException(hr,L"CreateExchangeMailBox(): ADsOpenObject Failed.",
				ERR_ADOBJECT_OPEN, __LINE__, __FILE__);
        }
        else
        {
            throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): ADsOpenObject Failed.",
                ERR_ADOBJECT_OPEN, __LINE__, __FILE__);
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
        throw ExchangeAdminException(hr,L"CreateExchangeMailBox(): GetObjectAttributes Failed.", 
		ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
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
	ADSVALUE controlValue;
    ADS_ATTR_INFO attrInfo[] = {
        { L"objectClass", ADS_ATTR_UPDATE, ADSTYPE_CASE_IGNORE_STRING, &classValue, 1 },
        { L"cn", ADS_ATTR_UPDATE, ADSTYPE_CASE_IGNORE_STRING, &cnValue, 1 },
        { L"sAMAccountName", ADS_ATTR_UPDATE, ADSTYPE_CASE_IGNORE_STRING, &sAMValue, 1 },
        { L"userPrincipalName", ADS_ATTR_UPDATE, ADSTYPE_CASE_IGNORE_STRING, &uPNValue, 1 },
		{L"userAccountControl", ADS_ATTR_UPDATE, ADSTYPE_INTEGER,&controlValue, 1},
    };
    DWORD dwAttrs = sizeof (attrInfo) / sizeof (ADS_ATTR_INFO);

    classValue.dwType = ADSTYPE_CASE_IGNORE_STRING;
    classValue.CaseIgnoreString = L"user";

	//int UF_ACCOUNTDISABLE = 0x0002;
	int UF_PASSWD_NOTREQD = 0x0020;
	//int UF_PASSWD_CANT_CHANGE = 0x0040;
	int UF_NORMAL_ACCOUNT = 0x0200;
	int UF_DONT_EXPIRE_PASSWD = 0x10000;
	//int UF_PASSWORD_EXPIRED = 0x800000;

	controlValue.dwType = ADSTYPE_INTEGER;
	controlValue.Integer=UF_NORMAL_ACCOUNT | UF_PASSWD_NOTREQD |UF_DONT_EXPIRE_PASSWD;

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
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);

    wstring wstrUserCN = L"CN=";

    wstrUserCN += lpwstrNewUser;
    dloge("CreateDSObject: %S",wstrUserCN.c_str());
    if (FAILED(hr = pDirContainer->CreateDSObject((LPWSTR)wstrUserCN.c_str(), attrInfo, dwAttrs,
            pDisp.getptr())))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): CreateDSObject Failed.",
            ERR_CREATE_EXCHMBX,__LINE__, __FILE__);
    if (FAILED(hr = pDisp->QueryInterface(IID_IADsUser, (void **)pIADNewUser.getptr())))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): QueryInterface Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);

    CComVariant varProp;
    varProp.Clear();

    // set samAccount
    varProp = lpwstrNewUser;
    if (FAILED(hr = pIADNewUser->Put(CComBSTR(L"sAMAccountName"), varProp)))
        throw ExchangeAdminException(hr,L"CreateExchangeMailBox(): Put(sAMAccountName) Failed.",
		ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    if(FAILED(hr = pIADNewUser->SetInfo()))
        throw ExchangeAdminException(hr,L"CreateExchangeMailBox(): Put(sAMAccountName) Failed.", 
		ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    // set userAccountControl
    varProp.Clear();
    hr = pIADNewUser->Get(CComBSTR(L"userAccountControl"), &varProp);
    varProp = varProp.lVal & ~(ADS_UF_ACCOUNTDISABLE);
    if (FAILED(hr = pIADNewUser->Put(CComBSTR(L"userAccountControl"), varProp)))
        throw ExchangeAdminException(hr,L"CreateExchangeMailBox(): Put(userAccountControl) Failed.",
		ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    if(FAILED(hr = pIADNewUser->SetInfo()))
        ThrowSetInfoException(hr, L"SetInfo - Put(userAccountControl) Failed.");
    // set Account enabled
    if (FAILED(hr = pIADNewUser->put_AccountDisabled(VARIANT_FALSE)))
    {
        throw
            ExchangeAdminException(hr, L"CreateExchangeMailBox(): put_AccountDisabled Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    }
    if(FAILED(hr = pIADNewUser->SetInfo()))
        ThrowSetInfoException(hr, L"SetInfo - put_AccountDisabled Failed.");
    // set password
    if (FAILED(hr = pIADNewUser->SetPassword(CComBSTR(lpwstrNewUserPwd))))
    {
        throw
            ExchangeAdminException(hr, L"CreateExchangeMailBox(): SetPassword Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    }
    if(FAILED(hr = pIADNewUser->SetInfo()))
        ThrowSetInfoException(hr, L"SetInfo - SetPassword Failed.");
    // user account password does not expire
    varProp.Clear();

    VARIANT var;

    VariantInit(&var);
    if (!FAILED(hr = pIADNewUser->Get(CComBSTR(L"userAccountControl"), &var)))
    {
        V_I4(&var) |= ADS_UF_DONT_EXPIRE_PASSWD;
        if (FAILED(hr = pIADNewUser->Put(CComBSTR(L"userAccountControl"), var)))
        {
            throw ExchangeAdminException(hr,L"CreateExchangeMailBox(): Put(userAccountControl) Failed.", 
				ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
        }
    }
    if(FAILED(hr = pIADNewUser->SetInfo()))
        ThrowSetInfoException(hr, L"SetInfo - userAccountControl Failed.");
    varProp.Clear();
    // set the homeMDB;
    if (!strLogonHomeMDB.empty())
    {
        varProp = strLogonHomeMDB.c_str();
        if (FAILED(hr = pIADNewUser->Put(CComBSTR("homeMDB"), varProp)))
            throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put(homeMDB) Failed.",
                ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    }
    if(FAILED(hr = pIADNewUser->SetInfo()))
        ThrowSetInfoException(hr, L"SetInfo - Put(homeMDB) Failed.");

	varProp.Clear();
    if (!strLogonHomeMTA.empty())
    {
        varProp = strLogonHomeMTA.c_str();
        if (FAILED(hr = pIADNewUser->Put(CComBSTR("homeMTA"), varProp)))
            throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put(homeMTA) Failed.",
                ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    }
    if(FAILED(hr = pIADNewUser->SetInfo()))
        ThrowSetInfoException(hr, L"SetInfo - Put(homeMTA) Failed.");

	varProp.Clear();
	if (!msExchHomeSvrName.empty())
    {
        varProp = msExchHomeSvrName.c_str();
        if (FAILED(hr = pIADNewUser->Put(CComBSTR("msExchHomeServerName"), varProp)))
            throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put(msExchHomeServerName) Failed.",
                ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    }
    if(FAILED(hr = pIADNewUser->SetInfo()))
        ThrowSetInfoException(hr, L"SetInfo - Put(msExchHomeServerName) Failed.");
	varProp.Clear();

	varProp.Clear();
	wstring newUsrLegacyName=legacyName;
	size_t nwpos=newUsrLegacyName.rfind(L"cn=");
	if(nwpos !=wstring::npos)
	{
		newUsrLegacyName = newUsrLegacyName.substr(0,nwpos);
		newUsrLegacyName += L"cn=";
		newUsrLegacyName += lpwstrNewUser;
	}
	if (!newUsrLegacyName.empty())
    {
        varProp = newUsrLegacyName.c_str();
        if (FAILED(hr = pIADNewUser->Put(CComBSTR("legacyExchangeDN"), varProp)))
            throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put(legacyExchangeDN) Failed.",
                ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    }
    if(FAILED(hr = pIADNewUser->SetInfo()))
        ThrowSetInfoException(hr, L"SetInfo - Put(legacyExchangeDN) Failed.");

    // set nickname
    varProp.Clear();
    varProp = lpwstrNewUser;
    if (FAILED(hr = pIADNewUser->Put(CComBSTR("mailNickname"), varProp)))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put(mailNickname) Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    if(FAILED(hr = pIADNewUser->SetInfo()))
        ThrowSetInfoException(hr, L"SetInfo - Put(mailNickname) Failed.");

    // set the displayName
    varProp.Clear();
    varProp = lpwstrNewUser;
    if (FAILED(hr = pIADNewUser->Put(CComBSTR("displayName"), varProp)))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put(displayName) Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    if(FAILED(hr = pIADNewUser->SetInfo()))
        ThrowSetInfoException(hr, L"SetInfo - Put(displayName) Failed.");
    // set the mail atrribute
    varProp.Clear();
    varProp = wstrMail.c_str();
    if (FAILED(hr = pIADNewUser->Put(CComBSTR("mail"), varProp)))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put(mail) Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    if(FAILED(hr = pIADNewUser->SetInfo()))
        ThrowSetInfoException(hr, L"SetInfo - Put(mail) Failed.");
    // set email
    if (FAILED(hr = pIADNewUser->put_EmailAddress(CComBSTR(wstrMail.c_str()))))
    {
        throw
            ExchangeAdminException(hr, L"CreateExchangeMailBox(): put_EmailAddress Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    }
    if(FAILED(hr = pIADNewUser->SetInfo()))
        ThrowSetInfoException(hr, L"SetInfo - put_EmailAddress Failed.");

	varProp.Clear();
	wstrMail=L"SMTP:"+wstrMail;
	varProp = wstrMail.c_str();
	if (FAILED(hr = pIADNewUser->Put(CComBSTR("proxyAddresses"),varProp)))
	{
        throw
            ExchangeAdminException(hr, L"CreateExchangeMailBox(): proxyAddressess Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    }
    if(FAILED(hr = pIADNewUser->SetInfo()))
        ThrowSetInfoException(hr, L"SetInfo - proxyAddressess Failed.");

    // add to Domain Admins group
    BSTR bstrADSPath;

    if (FAILED(hr = pIADNewUser->get_ADsPath(&bstrADSPath)))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): get_ADsPath Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);

    wstring wstrGroup = _T("LDAP://CN=Domain Admins,CN=Users,") + wstrServerDN;
    Zimbra::Util::ScopedInterface<IADsGroup> pGroup;

    if (FAILED(hr = ADsGetObject(wstrGroup.c_str(), IID_IADsGroup, (void **)pGroup.getptr())))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): ADsGetObject Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    if (FAILED(hr = ADsOpenObject(wstrGroup.c_str(), wstrLoggedUserName.c_str(),
            lpwstrLogonUsrPwd, ADS_SECURE_AUTHENTICATION, IID_IADsGroup,
            (void **)pGroup.getptr())))
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): ADsOpenObject Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    if (SUCCEEDED(hr = pGroup->Add(bstrADSPath)))
    {
        if (FAILED(hr = pGroup->SetInfo()))
            throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): pGroup SetInfo Failed.",
                ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    }
    else
    {
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): pGroup Add Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    }

	GUID guid;
    if(FAILED(hr = CoCreateGuid(&guid)))
    {
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): CoCreateGuid Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    }

    BYTE *str;
    hr = UuidToString((UUID *)&guid, (RPC_WSTR *)&str);
    if (hr != RPC_S_OK)
    {
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): UuidToString Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    }

	varProp.Clear();
	//BYTE bytArr[]="3429bb3084703348b8023e94fabf16ea";
	PutBinaryIntoVariant(&varProp,str,16);
	RpcStringFree((RPC_WSTR *)&str);
	if (FAILED(hr = pIADNewUser->Put(CComBSTR("msExchMailboxGuid"), varProp)))
	{
		throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put msExchMailboxGuid Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
	}
	if(FAILED(hr = pIADNewUser->SetInfo()))
            ThrowSetInfoException(hr, L"SetInfo - msExchMailboxGuid Failed.");

	if (FAILED(hr = ADsOpenObject(strContainer.c_str(), NULL, NULL, ADS_SECURE_AUTHENTICATION,
        IID_IDirectoryObject, (void **)pIAdUser.getptr())))
	{
		throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): ADsOpenObject2 Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
	}	
	if (FAILED(hr = pIAdUser->QueryInterface(IID_IADs, (void**) pIAds.getptr())))
	{
		throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): pIAdUser->QueryInterface Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
	}

	varProp.Clear();
	if( FAILED(hr= pIAds->Get(CComBSTR("msExchMailboxSecurityDescriptor"),&varProp)))
	{
        throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Get msExchMailboxSecurityDescriptor Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
    }
	if (FAILED(hr = pIADNewUser->Put(CComBSTR("msExchMailboxSecurityDescriptor"), varProp)))
	{
		throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put msExchMailboxSecurityDescriptor Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
	}
	if(FAILED(hr = pIADNewUser->SetInfo()))
            ThrowSetInfoException(hr, L"SetInfo - msExchMailboxSecurityDescriptor Failed.");

	varProp.Clear();
	if( FAILED(hr=pIAds->Get(CComBSTR("msExchPoliciesIncluded"),&varProp)))
	{
		throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Get msExchPoliciesIncluded Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);        
    }
	if (FAILED(hr = pIADNewUser->Put(CComBSTR("msExchPoliciesIncluded"), varProp)))
	{
		throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put msExchPoliciesIncluded Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);     
	}
	if(FAILED(hr = pIADNewUser->SetInfo()))
            ThrowSetInfoException(hr, L"SetInfo - msExchPoliciesIncluded Failed.");

	varProp.Clear();
	if( FAILED(hr= pIAds->Get(CComBSTR("msExchUserAccountControl"),&varProp)))
	{
		throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Get msExchUserAccountControl Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);        
    }
	if (FAILED(hr = pIADNewUser->Put(CComBSTR("msExchUserAccountControl"), varProp)))
	{
		throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put msExchUserAccountControl Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);    
	}
	if(FAILED(hr = pIADNewUser->SetInfo()))
            ThrowSetInfoException(hr, L"SetInfo - msExchUserAccountControl Failed.");

	varProp.Clear();
	if(FAILED(hr = pIAds->GetEx(CComBSTR("showInAddressBook"), &varProp )))
	{
		throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Get showInAddressBook Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);		
	}
	if(FAILED(hr = pIADNewUser->Put(CComBSTR("showInAddressBook"), varProp)))
	{
		throw ExchangeAdminException(hr, L"CreateExchangeMailBox(): Put showInAddressBook Failed.",
            ERR_CREATE_EXCHMBX, __LINE__, __FILE__);
	}
	if(FAILED(hr = pIADNewUser->SetInfo()))
            ThrowSetInfoException(hr, L"SetInfo - showInAddressBook Failed.");
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
        dloge("ExchangeAdmin::DeleteExchangeMailBox ExchangeAdminException exception: %S", ex.Description().c_str());
        throw;
    }
    catch (Zimbra::MAPI::Util::MapiUtilsException &ex)
    {
        dloge("ExchangeAdmin::DeleteExchangeMailBox MapiUtilsException exception: %S", ex.Description().c_str());
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
            ERR_DELETE_MBOX, __LINE__, __FILE__);

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
	try
	{
		Clean();
	}
	catch (Zimbra::MAPI::ExchangeAdminException &ex)
    {
		dloge("Setup: Clean exception: %S",ex.Description().c_str());
	}
	catch(...)
	{
		dloge("Setup: Unknown Clean exception");
	}

    try
    {
        dloge("Going for CreateExchangeMailbox...");
        m_exchAdmin->CreateExchangeMailBox(DEFAULT_ADMIN_MAILBOX_NAME, m_ExchangeAdminPwd.c_str(),
            m_ExchangeAdminName.c_str(), m_ExchangeAdminPwd.c_str());
		dloge("CreateExchangeMailbox success.");
    }
    catch (Zimbra::MAPI::ExchangeAdminException &ex)
    {
        dloge("ExchangeMigrationSetup::Setup::CreateExchangeMailBox ExchangeAdminException exception: %S", ex.Description().c_str());
        throw;
    }
    catch (Zimbra::MAPI::Util::MapiUtilsException &ex)
    {
        dloge("ExchangeMigrationSetup::Setup::CreateExchangeMailBox MapiUtilsException exception: %S", ex.Description().c_str());
        throw;
    }
	
    try
    {
        m_exchAdmin->CreateProfile(DEFAULT_ADMIN_PROFILE_NAME, DEFAULT_ADMIN_MAILBOX_NAME,
            m_ExchangeAdminPwd.c_str());
    }
    catch (Zimbra::MAPI::ExchangeAdminException &ex)
    {
        dloge("ExchangeMigrationSetup::Setup::CreateProfile ExchangeAdminException exception: %S", ex.Description().c_str());
        throw;
    }
	catch(...)
	{
		dloge("ExchangeMigrationSetup::Setup::CreateProfile Unknown exception");
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
		dloge("DeleteProfile exception: %S", ex.Description().c_str());
        throw;
    }
    try
    {
        m_exchAdmin->DeleteExchangeMailBox(DEFAULT_ADMIN_MAILBOX_NAME,
            m_ExchangeAdminName.c_str(), m_ExchangeAdminPwd.c_str());
    }
    catch (Zimbra::MAPI::ExchangeAdminException &ex)
    {
        dloge("DeleteExchangeMailBox exception: %S", ex.Description().c_str());
        throw;
    }
    catch (Zimbra::MAPI::Util::MapiUtilsException &ex)
    {
        dloge("DeleteExchangeMailBox(MAPIUtils) exception: %S", ex.Description().c_str());
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
Zimbra::Util::MiniDumpGenerator *ExchangeOps::m_EOminidmpgntr = NULL;

void  ExchangeOps::internalEOInit()
{
	//Get App dir
	wstring appdir= Zimbra::Util::GetAppDir();
	//instantiate dump generator
	LPWSTR pwszTempPath = new WCHAR[MAX_PATH];
	wcscpy(pwszTempPath,appdir.c_str());
	Zimbra::Util::AppendString(pwszTempPath,L"dbghelp.dll");
    m_EOminidmpgntr = new Zimbra::Util::MiniDumpGenerator(pwszTempPath);
}

LPCWSTR ExchangeOps::GlobalInit(LPCWSTR lpMAPITarget, LPCWSTR lpAdminUsername, LPCWSTR
    lpAdminPassword)
{
	LPWSTR exceptionmsg=NULL;
	internalEOInit();	
	__try
	{
		return _GlobalInit(lpMAPITarget, lpAdminUsername, lpAdminPassword);
	}
	__except(m_EOminidmpgntr->GenerateCoreDump(GetExceptionInformation(),exceptionmsg))
	{
		dloge(exceptionmsg);
	}
	return exceptionmsg;
}

LPCWSTR ExchangeOps::_GlobalInit(LPCWSTR lpMAPITarget, LPCWSTR lpAdminUsername, LPCWSTR
    lpAdminPassword)
{
    LPWSTR lpwstrStatus = NULL;
	LPWSTR lpwstrRetVal=NULL;

    // if lpAdminUsername is NULL then we assume that Outlook admin profile exists and we should use it
    // else create a Admin mailbox and create corresponding profile on local machine
    dloge("Check AdminUserName");
    if (lstrlen(lpAdminUsername) > 0)
    {
        dloge("Check Initialized:%d",Initialized);
        if (Initialized == EXCH_UNINITIALIZED)
        {
            dloge("Do ExchangeMigrationSetup");
            m_exchmigsetup = new ExchangeMigrationSetup(lpMAPITarget, lpAdminUsername,
                lpAdminPassword);
            Initialized = EXCH_INITIALIZED_PROFCREATE;			
        }
        try
        {
            dloge("Going for Exchange mig setup");
            m_exchmigsetup->Setup();
			lpMAPITarget = DEFAULT_ADMIN_PROFILE_NAME;
        }
        catch (Zimbra::MAPI::ExchangeAdminException &ex)
        {
            lpwstrStatus = FormatExceptionInfo(ex.ErrCode(), (LPWSTR)ex.Description().c_str(),
                (LPSTR)ex.SrcFile().c_str(), ex.SrcLine());
			dloge(lpwstrStatus);
			Zimbra::Util::CopyString(lpwstrRetVal, ex.ShortDescription().c_str());
        }
        catch (Zimbra::MAPI::Util::MapiUtilsException &ex)
        {
            lpwstrStatus = FormatExceptionInfo(ex.ErrCode(), (LPWSTR)ex.Description().c_str(),
                (LPSTR)ex.SrcFile().c_str(), ex.SrcLine());
			dloge(lpwstrStatus);
			Zimbra::Util::CopyString(lpwstrRetVal, ex.ShortDescription().c_str());
        }
    }
	//check for any exception or error
	if(lpwstrStatus)
	{
		Zimbra::Util::FreeString(lpwstrStatus);
		return lpwstrRetVal;
	}

    // Create Session and Open admin store with profile
	lpwstrStatus = (LPWSTR)MAPIAccessAPI::InitGlobalSessionAndStore(lpMAPITarget);

	if (!lpwstrStatus)
        Initialized = EXCH_INITIALIZED_PROFEXIST;
    return lpwstrStatus;
}

LPCWSTR ExchangeOps::GlobalUninit()
{
    LPWSTR lpwstrStatus = NULL;
	LPWSTR lpwstrRetVal=NULL;
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
			Zimbra::Util::CopyString(lpwstrRetVal, ex.ShortDescription().c_str());
        }
        catch (Zimbra::MAPI::Util::MapiUtilsException &ex)
        {
            lpwstrStatus = FormatExceptionInfo(ex.ErrCode(), (LPWSTR)ex.Description().c_str(),
                (LPSTR)ex.SrcFile().c_str(), ex.SrcLine());
			Zimbra::Util::CopyString(lpwstrRetVal, ex.ShortDescription().c_str());
        }
    }
    else
	//if (Initialized != EXCH_UNINITIALIZED)
    {
        MAPIAccessAPI::UnInitGlobalSessionAndStore();
    }
    if (m_zmmapisession)
    {
        delete m_zmmapisession;
        m_zmmapisession = NULL;
    }
	//clean minidump ptr
	if(m_EOminidmpgntr)
		delete m_EOminidmpgntr;
	m_EOminidmpgntr=NULL;

    Initialized = EXCH_UNINITIALIZED;
	if(lpwstrStatus)
		Zimbra::Util::FreeString(lpwstrStatus);
    return lpwstrRetVal;
}

LPCWSTR ExchangeOps::SelectExchangeUsers(vector<ObjectPickerData> &vUserList)
{
    LPWSTR lpwstrStatus = NULL;
	try
    {
        Zimbra::MAPI::Util::GetExchangeUsersUsingObjectPicker(vUserList);
    }
    catch (Zimbra::MAPI::Util::MapiUtilsException &ex)
    {
        lpwstrStatus = FormatExceptionInfo(ex.ErrCode(), (LPWSTR)ex.Description().c_str(),
            (LPSTR)ex.SrcFile().c_str(), ex.SrcLine());
		dloge(lpwstrStatus);
		Zimbra::Util::FreeString(lpwstrStatus);
		Zimbra::Util::CopyString(lpwstrStatus, ex.ShortDescription().c_str());
    }
    return lpwstrStatus;
}
