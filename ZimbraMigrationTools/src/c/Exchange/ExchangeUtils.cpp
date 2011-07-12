#include "ExchangeUtils.h"

using namespace Zimbra::ExchangeUtils;

ExchangeAdmin::ExchangeAdmin(string strExchangeServer)
{	
	m_pProfAdmin=NULL;
	m_strServer=strExchangeServer;
	Init();
}

ExchangeAdmin::~ExchangeAdmin()
{
	m_pProfAdmin->Release();
	MAPIUninitialize();
}

HRESULT ExchangeAdmin::Init()
{
	HRESULT hr= S_OK;
	if (FAILED(hr = MAPIInitialize(NULL)))
    {
		//log error into the log file
		return hr;
	}
	if (FAILED(hr = MAPIAdminProfiles(0, &m_pProfAdmin))) 
    {
		//log error into the log file
		return hr;
    }
	return hr;
}


HRESULT ExchangeAdmin::CreateProfile(string strProfileName,string strMailboxName,string strPassword)
{
	HRESULT hr=S_OK;
	 Zimbra::Util::ScopedInterface<IMsgServiceAdmin> pSvcAdmin;
    Zimbra::Util::ScopedInterface<IMAPITable> pMsgSvcTable;
	Zimbra::Util::ScopedRowSet pSvcRows;
	SPropValue rgval[2] = {0};
	SPropValue sProps = {0};
    SRestriction sres;

	// Columns to get from HrQueryAllRows.
    enum {iSvcName, iSvcUID, cptaSvc};
    SizedSPropTagArray(cptaSvc,sptCols) = { cptaSvc, PR_SERVICE_NAME, PR_SERVICE_UID };

	//create new profile
	if (FAILED(hr = m_pProfAdmin->CreateProfile((LPTSTR)strProfileName.c_str(), (LPTSTR)strPassword.c_str(),NULL,0)))//pWProfileStr,pWPasswordStr,NULL,0)))
	{
		//log error into the log file
		return hr;
	}

	// Get an IMsgServiceAdmin interface off of the IProfAdmin interface.
	if (FAILED(hr = m_pProfAdmin->AdminServices((LPTSTR)strProfileName.c_str(), (LPTSTR)strPassword.c_str(), NULL, 0, pSvcAdmin.getptr()))) 
    {
		//log error into the log file
		goto CRT_PROFILE_EXIT;
	}

	// Create the new message service for Exchange.
	if (FAILED(hr = pSvcAdmin->CreateMsgService((LPTSTR)"MSEMS", L"MSEMS", NULL, NULL)))      
    {
		//log error into the log file
		goto CRT_PROFILE_EXIT;
    }

	// Need to obtain the entry id for the new service. This can be done by getting the message service table
    // and getting the entry that corresponds to the new service.
	if (FAILED(hr = pSvcAdmin->GetMsgServiceTable(0, pMsgSvcTable.getptr())))
    {
		//log error into the log file
		goto CRT_PROFILE_EXIT;
    }

	sres.rt = RES_CONTENT;
    sres.res.resContent.ulFuzzyLevel = FL_FULLSTRING;
    sres.res.resContent.ulPropTag = PR_SERVICE_NAME;
    sres.res.resContent.lpProp = &sProps;

    sProps.ulPropTag = PR_SERVICE_NAME;
    sProps.Value.lpszA = "MSEMS";

    // Query the table to obtain the entry for the newly created message service.
	if (FAILED(hr = HrQueryAllRows(pMsgSvcTable.get(), (LPSPropTagArray)&sptCols, NULL, NULL, 0, pSvcRows.getptr())))
    {
		//log error into the log file
		goto CRT_PROFILE_EXIT;
	}

	// Set up a SPropValue array for the properties that you have to configure.
	if(pSvcRows->cRows > 0)
	{
		// First, the exchange server name.
		ZeroMemory(&rgval[0], sizeof(SPropValue) );
		rgval[0].ulPropTag = PR_PROFILE_UNRESOLVED_SERVER;
		rgval[0].Value.lpszA = (LPSTR)m_strServer.c_str();

		// Next, the user's AD name.
		ZeroMemory(&rgval[1], sizeof(SPropValue) );
		rgval[1].ulPropTag = PR_PROFILE_UNRESOLVED_NAME; 
		rgval[1].Value.lpszA = (LPSTR)strMailboxName.c_str();
        
		// Configure the message service by using the previous properties.
		if (FAILED(hr = pSvcAdmin->ConfigureMsgService( (LPMAPIUID)pSvcRows->aRow->lpProps[iSvcUID].Value.bin.lpb,
														NULL, 0, 2, rgval)))
		{
			//log error into the log file
			goto CRT_PROFILE_EXIT;
		}
	}
CRT_PROFILE_EXIT:
	if(hr!=S_OK)
	{
		DeleteProfile(strProfileName);
	}
	
	return hr;
}

HRESULT ExchangeAdmin::DeleteProfile(string strProfile)
{
	HRESULT hr=S_OK;
	//delete profile
	if (FAILED(hr = m_pProfAdmin->DeleteProfile((LPTSTR)strProfile.c_str(),0)))
	{
		//log error into the log file
	}	
	return hr;
}

HRESULT ExchangeAdmin::GetAllProfiles(vector<string> &vProfileList)
{
	HRESULT hr=S_OK;
	Zimbra::Util::ScopedInterface<IMAPITable> pProftable;
	
	//get profile table
	if((hr = m_pProfAdmin->GetProfileTable(0, pProftable.getptr()))==S_OK)
  	{ 
		SizedSPropTagArray(3, proftablecols) = { 3, {PR_DISPLAY_NAME_A,PR_DEFAULT_PROFILE,PR_SERVICE_NAME} };
		Zimbra::Util::ScopedRowSet profrows;
		//get all profile rows
		if((hr = HrQueryAllRows(pProftable.get(),(SPropTagArray*)&proftablecols,NULL,NULL,0,profrows.getptr()))==S_OK)
		{ 
			for (unsigned int i=0; i<profrows->cRows; i++)
			{
				if (profrows->aRow[i].lpProps[0].ulPropTag == PR_DISPLAY_NAME_A) 
				{
						Zimbra::Util::ScopedInterface<IMsgServiceAdmin> spServiceAdmin ;
						Zimbra::Util::ScopedInterface<IMAPITable> spServiceTable ;
						string strpname= profrows->aRow[i].lpProps[0].Value.lpszA;
						//get profile's admin service
						hr = m_pProfAdmin->AdminServices((LPTSTR)strpname.c_str(), NULL, NULL, 0, spServiceAdmin.getptr()) ; 
						if(FAILED(hr))
						{
							//log error into the log file
							return hr ;
						}
						//get message service table
						hr = spServiceAdmin->GetMsgServiceTable(0, spServiceTable.getptr()) ;
						if(FAILED(hr))
						{
							//log error into the log file
							return hr ;
						}
						
						//lets get the service name and the service uid for the primary service
						SizedSPropTagArray( 2, tags ) = { 2, { PR_SERVICE_NAME, PR_SERVICE_UID} } ;
						spServiceTable->SetColumns( (LPSPropTagArray)&tags, 0 ) ;
						DWORD dwCount = 0 ;
						hr = spServiceTable->GetRowCount(0, &dwCount) ;
						if(FAILED(hr))
						{
							//log error into the log file
							return hr ;
						}
						Zimbra::Util::ScopedRowSet pRows ;
						hr = spServiceTable->QueryRows( dwCount, 0, pRows.getptr() );
						if(FAILED(hr))
						{
							//log error into the log file
							return hr ;
						}

						//is it Zimbra profile?
						ULONG j;
						bool bFoundZimbra=false;
						for(j = 0; j < pRows->cRows ; j++)
						{
							if(PR_SERVICE_NAME == pRows->aRow[j].lpProps[0].ulPropTag) 
							{
								if(0 == lstrcmpiW(pRows->aRow[j].lpProps[0].Value.LPSZ, L"LSMS"))
								{
									//its a Zimbra Profile
									bFoundZimbra = true;
									break ;
								}
							}
						}
						//use non-zimbra profiles only
						if(!bFoundZimbra)
						{
							if (profrows->aRow[i].lpProps[0].ulPropTag == PR_DISPLAY_NAME_A) 
								vProfileList.push_back(profrows->aRow[i].lpProps[0].Value.lpszA);
						}						
				}
			}
		}
	}
	return hr;
}

HRESULT ExchangeAdmin::SetDefaultProfile(string strProfile)
{
	HRESULT hr=S_OK;
	if((hr = m_pProfAdmin->SetDefaultProfile((LPTSTR)strProfile.c_str(), 0))==S_OK)
  	{ 
		//log error into the log file
	}
	return hr;
}


HRESULT ExchangeAdmin::CreateExchangeMailBox(LPWSTR lpwstrNewUser, LPWSTR lpwstrNewUserPwd, LPWSTR lpwstrlogonuserDN, LPWSTR lpwstrLogonUsrPwd)
{
	HRESULT hr=S_OK;
	Zimbra::Util::ScopedInterface<IDirectoryObject> pLogonContainer;

	wstring strContainer = L"LDAP://";
	strContainer += lpwstrlogonuserDN;
	//Get loggedin user container
	hr = ADsOpenObject(strContainer.c_str(), NULL, NULL, ADS_SECURE_AUTHENTICATION, IID_IDirectoryObject, (void**)pLogonContainer.getptr());
	if(FAILED(hr))
	{
		if(hr==0x8007052e)//credentials are not valid
		{
			hr = ADsOpenObject((LPTSTR)strContainer.c_str(), lpwstrLogonUsrPwd, NULL, ADS_SECURE_AUTHENTICATION, IID_IDirectoryObject, (void**)&pLogonContainer);
			if(FAILED(hr))
			{
				//log error into the log file
				return hr;			
			}
		}
		else
		{
			return hr;
		}
	}

	ADS_ATTR_INFO *pAttrInfo = NULL;
	DWORD dwReturn;
	LPWSTR pAttrNames[] = { L"mail", L"homeMDB", L"homeMTA" };
	DWORD dwNumAttr = sizeof(pAttrNames)/sizeof(LPWSTR);

	wstring strLogonHomeMDB;
	wstring strLogonHomeMTA;
	wstring strLogonMail;

	// Get attribute values requested. Its not necessary the order is same as requested.
	if(FAILED(hr = pLogonContainer->GetObjectAttributes( pAttrNames, dwNumAttr, &pAttrInfo, &dwReturn )))
	{
		return hr;
	}
	
	for(DWORD idx = 0; idx < dwReturn; idx++ )
	{
		if ( _wcsicmp(pAttrInfo[idx].pszAttrName,L"mail") == 0 )
		{
			strLogonMail = pAttrInfo[idx].pADsValues->Email.Address;
		}
		else if ( _wcsicmp(pAttrInfo[idx].pszAttrName, L"homeMTA") == 0 )
		{
			strLogonHomeMTA = pAttrInfo[idx].pADsValues->DNString;
		}
		else if ( _wcsicmp(pAttrInfo[idx].pszAttrName, L"homeMDB") == 0  )
		{
			strLogonHomeMDB = pAttrInfo[idx].pADsValues->DNString;
		}
	}

	// Use FreeADsMem for all memory obtained from the ADSI call. 
	FreeADsMem( pAttrInfo );
	
	
	wstring twtsrlogonuserDN=lpwstrlogonuserDN;
	int nPos = twtsrlogonuserDN.find(_T("DC="), 0);
	wstring wstrServerDN = twtsrlogonuserDN.substr(nPos);
	wstring wstrADSPath = _T("LDAP://CN=Users,") + wstrServerDN;
	
	ADSVALUE   cnValue;
	ADSVALUE   classValue;
	ADS_ATTR_INFO  attrInfo[] = {  
	{L"objectClass", ADS_ATTR_UPDATE, ADSTYPE_CASE_IGNORE_STRING, &classValue, 1 },
	{L"cn", ADS_ATTR_UPDATE, ADSTYPE_CASE_IGNORE_STRING, &cnValue, 1},
	};

	DWORD dwAttrs = sizeof(attrInfo)/sizeof(ADS_ATTR_INFO); 
		
	classValue.dwType = ADSTYPE_CASE_IGNORE_STRING;
	classValue.CaseIgnoreString = L"user";
		
	cnValue.dwType=ADSTYPE_CASE_IGNORE_STRING;
	cnValue.CaseIgnoreString = lpwstrNewUser;

	UNREFERENCED_PARAMETER(lpwstrNewUserPwd);
	Zimbra::Util::ScopedInterface<IDirectoryObject> pDirContainer;
	Zimbra::Util::ScopedInterface<IDispatch> pDisp;
	Zimbra::Util::ScopedInterface<IADsUser> pIADNewUser;
	wstring wstrLoggedUserName(lpwstrlogonuserDN);
	unsigned int snPos = 0;
	unsigned int enPos = 0;
	if((snPos=wstrLoggedUserName.find(L"CN="))!=wstring::npos)
	{
		if((enPos = wstrLoggedUserName.find(L",",snPos))!=wstring::npos)
		{
			wstrLoggedUserName = wstrLoggedUserName.substr(snPos+3,(enPos-(snPos+3)));
		}
	}

	//get dir container
	if (FAILED(hr = ADsOpenObject( wstrADSPath.c_str(), wstrLoggedUserName.c_str(), lpwstrLogonUsrPwd, ADS_SECURE_AUTHENTICATION, IID_IDirectoryObject, (void**)pDirContainer.getptr())))
	{
		return hr;
	}

	wstring wstrUserCN= L"CN=";
	wstrUserCN +=lpwstrNewUser;
	if(FAILED(hr = pDirContainer->CreateDSObject( (LPWSTR)wstrUserCN.c_str(),  attrInfo, dwAttrs, pDisp.getptr()) ))
	{
		return hr;
	}

	if(FAILED(hr = pDisp->QueryInterface(IID_IADsUser, (void**)pIADNewUser.getptr())))
	{
		return hr;
	}
	

	CComVariant varProp;
	varProp.Clear();
	//set samAccount
	varProp=lpwstrNewUser;
    if(FAILED(hr = pIADNewUser->Put(CComBSTR(L"sAMAccountName"), varProp)))
	{
		return hr;
	}

	//set userAccountControl
	varProp.Clear();
	hr = pIADNewUser->Get(CComBSTR(L"userAccountControl"), &varProp); 
	varProp = varProp.lVal & ~(ADS_UF_ACCOUNTDISABLE);
	if(FAILED(hr = pIADNewUser->Put(CComBSTR(L"userAccountControl"), varProp)))
	{
		return hr;
	}
	//set Account enabled
	if(FAILED(hr = pIADNewUser->put_AccountDisabled(VARIANT_FALSE)))
	{
		return hr;
	}
	//set password
    if(FAILED(hr=pIADNewUser->SetPassword(CComBSTR(lpwstrNewUserPwd))))
	{
		return hr;
	}

	//user account password does not expire
	varProp.Clear();
	VARIANT var;
	VariantInit(&var);
	if(!FAILED(hr = pIADNewUser->Get(CComBSTR(L"userAccountControl"), &var)))
	{
		V_I4(&var)|=ADS_UF_DONT_EXPIRE_PASSWD;
		if(FAILED(hr = pIADNewUser->Put(CComBSTR(L"userAccountControl"), var)))
		{
			return hr;
		}		
	}

	//set the homeMDB;
	if(!strLogonHomeMDB.empty())
	{
		varProp = strLogonHomeMDB.c_str();
		if(FAILED(hr = pIADNewUser->Put(CComBSTR("homeMDB"), varProp)))
		{
			return hr;
		}
	}
	if(!strLogonHomeMTA.empty())
	{
		varProp = strLogonHomeMTA.c_str();
		if(FAILED(hr = pIADNewUser->Put(CComBSTR("homeMTA"), varProp)))
		{
			return hr;
		}
	}
	
	//set nickname
	varProp.Clear();
    varProp = lpwstrNewUser;
	if(FAILED(hr = pIADNewUser->Put(CComBSTR("mailNickname"), varProp)))
	{
		return hr;
	}
	
	//set the displayName
	varProp.Clear();
	varProp =lpwstrNewUser;
	if(FAILED(hr = pIADNewUser->Put(CComBSTR("displayName"), varProp)))
	{
		return hr;
	}

	//set the mail atrribute
	wstring wstrMail;
	int nPosMail = strLogonMail.find(_T("@"), 0);
	wstrMail = strLogonMail.substr(nPosMail);
	wstrMail = lpwstrNewUser + wstrMail;
	varProp.Clear();
	varProp = wstrMail.c_str();
 	if(FAILED(hr = pIADNewUser->Put(CComBSTR( "mail"), varProp)))
	{
		return hr;
	}

	//set email
	if(FAILED(hr = pIADNewUser->put_EmailAddress(CComBSTR(wstrMail.c_str()))))
	{
		return hr;
	}
	
	//add to Domain Admins group
	BSTR bstrADSPath;
	if(FAILED(hr = pIADNewUser->get_ADsPath(&bstrADSPath)))
	{
		return hr;
	}
	wstring wstrGroup = _T("LDAP://CN=Domain Admins,CN=Users,") + wstrServerDN;
	Zimbra::Util::ScopedInterface<IADsGroup> pGroup;
	if(FAILED(hr = ADsGetObject(wstrGroup.c_str(), IID_IADsGroup, (void**)pGroup.getptr())))
	{
		return hr;
	}
	if(FAILED(hr = ADsOpenObject(wstrGroup.c_str(), wstrLoggedUserName.c_str(), lpwstrLogonUsrPwd, ADS_SECURE_AUTHENTICATION, IID_IADsGroup, (void**)pGroup.getptr())))
	{
		return hr;
	}
	
	if(SUCCEEDED(hr = pGroup->Add(bstrADSPath)))
	{
		if(FAILED(hr = pGroup->SetInfo()))
		{
			return hr;
		}
	}
	// Commit the change to the directory.
    hr = pIADNewUser->SetInfo();
	return hr;
}