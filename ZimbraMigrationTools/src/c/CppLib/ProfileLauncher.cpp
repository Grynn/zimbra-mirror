#include "stdafx.h"
#include "common.h"
#include "ProfileLauncher.h"

#define PR_REPLICA_VERSION PROP_TAG(PT_I8,0x664b)
#define PR_PROFILE_HOME_SERVER_DN	PROP_TAG( PT_STRING8,	0x6612 )
#define MDB_ONLINE ((ULONG) 0x00000100)
#define pbGlobalProfileSectionGuid	"\x13\xDB\xB0\xC8\xAA\x05\x10\x1A\x9B\xB0\x00\xAA\x00\x2F\xC4\x5A"

ProfileLauncher::ProfileLauncher(void)
{
}


ProfileLauncher::~ProfileLauncher(void)
{
}

__declspec(dllexport) int ProfileLauncher::DisplayProfiles(char* pszBuffer)
{
	HRESULT hr = S_OK;
	LPMAPISESSION pSession = NULL;
	//TCHAR tracemsg [512];
	CString m_strProfileName;

	//_stprintf (tracemsg, L"NooooooooError: %0x", hr);

	MAPIInitialize(NULL);
	//Save the default profile name
	IProfAdmin *pProfAdmin = NULL ;
	//ZimbraProfileHandler helper ;
	
	//Get IProfAdmin interface pointer
	hr = MAPIAdminProfiles(0 ,&pProfAdmin);
	if( S_OK == hr )
	{
		//Get the current default profile information
		LPMAPITABLE lpProfTable = NULL ;
		hr = pProfAdmin->GetProfileTable( 0, &lpProfTable );
		if( S_OK == hr )
		{
			//We need only profile name
			SPropTagArray arrPropTag = { 1, { PR_DISPLAY_NAME_A } } ;
			hr = lpProfTable->SetColumns( &arrPropTag, 0 );
			if( S_OK ==hr )
			{
				//Set the restriction such that only default profile gets returned
				SRestriction sRes ;
				sRes.rt = RES_PROPERTY ;

				SPropertyRestriction sPropRes ;
				sPropRes.relop = RELOP_EQ ;
				sPropRes.ulPropTag = PR_DEFAULT_PROFILE ;

				SPropValue sPropVal; 
				sPropVal.ulPropTag = PR_DEFAULT_PROFILE ;
				sPropVal.Value.b = TRUE ;

				sPropRes.lpProp = &sPropVal ;
				sRes.res.resProperty = sPropRes ;

				hr = lpProfTable->Restrict( &sRes, 0 ) ;
				if( S_OK ==hr )
				{
					LPSRowSet lpRowSet = NULL ;
					hr = lpProfTable->QueryRows( 1, 0, &lpRowSet ) ;

					if( S_OK == hr && 1 == lpRowSet->cRows )
					{
						//Save this profile name so as to restore it back later
						//CImportParams::GetImportParams()->DefaultProfileName( lpRowSet->aRow[0].lpProps->Value.lpszA );
						FreeProws( lpRowSet ) ;
					}
				}
				else
				{
					//_stprintf (tracemsg, L"Profile Table Restrict Failed: %0x", hr);
					goto ERR;
				}
				lpProfTable->Release() ;
			}
			else
			{
				//_stprintf (tracemsg, L"lpProfTable->SetColumns Failed: %0x", hr);
				goto ERR;
			}

		}
		pProfAdmin->Release() ;
	}
	else {
		CString msg;
		msg.Format((LPCWSTR)L"MAPIAdminProfilesFailed: Error: %0x", hr);
		MessageBox(NULL, msg, (LPCWSTR)L"Error", MB_ICONERROR );
	}

	//We dont want to display Zimbra profiles into the profile selection list
	//ZimbraProfileHandler::DeleteZimbraProfiles deleting their entries from the registry
	
	//helper.DeleteZimbraProfiles() ;
	
	//hr = MAPILogonEx( (ULONG_PTR)(this->m_hWnd), NULL, NULL, MAPI_LOGON_UI | MAPI_NEW_SESSION | MAPI_TIMEOUT_SHORT, &pSession );
	hr = MAPILogonEx( 0, NULL, NULL, MAPI_LOGON_UI | MAPI_NEW_SESSION | MAPI_TIMEOUT_SHORT, &pSession );

	if( hr == S_OK )
	{
		
		/****************************Get the Exchange server version*********************/
		//Use the PR_REPLICA_VERSION to find the exchange server version

		SizedSPropTagArray(1,tag) = { 1,{PR_REPLICA_VERSION}};
		LPSPropValue pPropVal = NULL;
		DWORD cVal;

		LPMAPITABLE msgStoreTable;
		SRestriction rt = {0};
		SPropValue prop = {0};
	
		LPSRowSet tmpRow;
		LPENTRYID tmpEntryID;
		LPMDB defaultStore;
		ULONG tmpEntryIDSize;
		LPMAPIFOLDER tmpFolder;
		ULONG objType = 0;

		hr = pSession->GetMsgStoresTable(0, &msgStoreTable);
		if( S_OK !=hr )
		{
			pSession->Logoff( 0, 0, 0 );
			pSession->Release();
			//_stprintf (tracemsg, L"GetMsgStoresTable Failed: %0x", hr);
			goto ERR;
		}
		// Open the default message store
		rt.rt = RES_PROPERTY;
		rt.res.resProperty.relop = RELOP_EQ;
		rt.res.resProperty.ulPropTag = PR_DEFAULT_STORE;
		rt.res.resProperty.lpProp = &prop;

		prop.ulPropTag = PR_DEFAULT_STORE;
		prop.Value.b   = TRUE;
		
		SPropTagArray tmpTags[2];
		memset(tmpTags,0, CbNewSPropTagArray(2) );
		tmpTags->cValues = 2;
		tmpTags->aulPropTag[0] = PR_ENTRYID;
		tmpTags->aulPropTag[1] = PR_DEFAULT_STORE;


		hr = HrQueryAllRows(msgStoreTable, tmpTags, &rt, NULL, 0, &tmpRow);
		
		if(	SUCCEEDED(hr) )
		{
			msgStoreTable->Release();
		}
		else
		{
			//_stprintf (tracemsg, L"HrQueryAllRows Failed: %0x", hr);
			goto ERR;
		}
		
		try
		{
			tmpEntryID  = (LPENTRYID)(tmpRow->aRow[0].lpProps[0].Value.bin.lpb);
			tmpEntryIDSize = tmpRow->aRow[0].lpProps[0].Value.bin.cb;
		}
		catch(...)
		{
			//_stprintf (tracemsg, L"EntryID/EntryIDSize Exception");
			goto ERR;
		}

		try
		{
			hr = pSession->OpenMsgStore(NULL,tmpEntryIDSize, tmpEntryID, NULL, MDB_ONLINE | MDB_NO_MAIL,
			&defaultStore);
		}
		catch( ... )
		{
			//TRACE( _T("Could not open default message store. hr[0x%x]"), hr);
			//MessageBox( _T("Could not open the default message store."),
			//					_T("Message Store Error"), MB_ICONEXCLAMATION );
			return strlen(pszBuffer);
		}
		
		if( hr == MAPI_E_UNKNOWN_FLAGS )
		{
            hr = pSession->OpenMsgStore(NULL,tmpEntryIDSize, tmpEntryID, NULL,
									MDB_NO_MAIL | MDB_TEMPORARY | MDB_NO_DIALOG,
									&defaultStore);
		}

		//Free the Rowset
		FreeProws( tmpRow ) ;

		if (S_OK != hr)
		{
			//TRACE( _T("Could not open default message store. hr[0x%x]."), hr);
			//MessageBox( _T("Could not open the default message store."),
			//					_T("Message Store Error"), MB_ICONEXCLAMATION );
			return strlen(pszBuffer);
		}
		
        //Open the root container
		hr = defaultStore->OpenEntry( NULL, 
									  NULL, 
									  NULL, 
									  MAPI_BEST_ACCESS, 
									  &objType, 
									  (LPUNKNOWN*)&tmpFolder);

		if(	SUCCEEDED(hr) )
		{
            hr = tmpFolder->GetProps( (LPSPropTagArray)&tag, fMapiUnicode, &cVal, &pPropVal );
			tmpFolder->Release();
			/*GetProp may fail if the profile is not an exchange profile*/
			if(hr != S_OK)
			{
				//MessageBox( _T("Invalid Profile\nThe profile may not be exchange profile."), _T("Invalid Profile"), MB_ICONEXCLAMATION );
				if(SUCCEEDED(hr))
					MAPIFreeBuffer(pPropVal);
				return strlen(pszBuffer);
			}
            //CImportParams::GetImportParams()->IsExchange55(pPropVal->Value.li.HighPart == 0x00050005);		
            if(SUCCEEDED(hr))
                MAPIFreeBuffer(pPropVal);
        }
        else
        {
            hr = defaultStore->GetProps( (LPSPropTagArray)&tag, fMapiUnicode, &cVal, &pPropVal );
            if(	SUCCEEDED(hr) )
            {
                //CImportParams::GetImportParams()->IsExchange55(pPropVal->Value.li.HighPart == 0x00050005);		
                MAPIFreeBuffer(pPropVal);
            }
            else
            {
                //MessageBox( _T("Unable to get Exchange Server Version Information.Try Re-configuring the profile."), _T("Invalid Profile"), MB_ICONEXCLAMATION );
                return strlen(pszBuffer);
            }
        }

		LPPROFSECT pProfSect = NULL;
		hr = pSession->OpenProfileSection( (LPMAPIUID)pbGlobalProfileSectionGuid, NULL, 0, &pProfSect );

		if( SUCCEEDED(hr) )
		{
			//for pre outlook 2003, we must ask for strings as ASCII when getting profile props
			SizedSPropTagArray( 2, props ) = { 2, { PR_PROFILE_NAME_A,PR_PROFILE_HOME_SERVER_DN } };
			LPSPropValue pPropVals = NULL;
			DWORD cVals;
			hr = pProfSect->GetProps( (LPSPropTagArray)&props, fMapiUnicode, &cVals, &pPropVals );
			if( SUCCEEDED(hr) )
			{
				m_strProfileName = pPropVals[0].Value.lpszA;
				strcpy_s(pszBuffer, 40, pPropVals[0].Value.lpszA);

				//Get PR_PROFILE_HOME_SERVER_DN and convert it into unicode string
				LPWSTR lpout =	new WCHAR[strlen(pPropVals[1].Value.lpszA)+1];
				MultiByteToWideChar(CP_ACP,MB_ERR_INVALID_CHARS,pPropVals[1].Value.lpszA,
									strlen(pPropVals[1].Value.lpszA)+1,lpout,
					 				strlen(pPropVals[1].Value.lpszA)+1);
				//CString serverDN = lpout;
				//CImportParams::GetImportParams()->ServerDN(serverDN);
				//delete []lpout;
				//this->UpdateData(FALSE);
				MAPIFreeBuffer(pPropVals);
			}
			pProfSect->Release();
		}
		else
		{
			//_stprintf (tracemsg, L"OpenProfileSection Failed: %0x",hr);
			goto ERR;
		}
		pSession->Logoff( 0, 0, 0 );
		pSession->Release();
		
		IProfAdmin *iprofadmin = NULL ;
		//Get IProfAdmin interface pointer
		HRESULT hr = MAPIAdminProfiles(0 ,&iprofadmin);
		if( S_OK == hr )
		{
			LPSTR strProfName = NULL ;

			int nWChars = (int)wcslen( m_strProfileName );
			int nAChars = WideCharToMultiByte( CP_ACP, 0, m_strProfileName, nWChars, NULL, 0, NULL, NULL );

			strProfName = new CHAR[nAChars + 1];
			ZeroMemory( (void*)strProfName, nAChars + 1 );
			WideCharToMultiByte( CP_ACP, 0, m_strProfileName, nWChars, strProfName, nAChars, NULL, NULL );

			//Set the selected profile as default profile of outlook
			hr = iprofadmin->SetDefaultProfile( (LPTSTR)strProfName, 0 ) ;

			delete[] strProfName ;
			iprofadmin->Release() ; 
		}
	}
	else
	{
		goto ERR;
	}

	//Lets restore back the Zimbra profiles
	//helper.RestoreZimbraProfiles() ; 
ERR:
	MAPIUninitialize();
	// FBS bug 57781 -- 3/10/11 -- we should rewrite all of this not delete from the reg at all
	if (hr == MAPI_E_USER_CANCEL) {
		//_stprintf (tracemsg, L"Operation canceled by user");
		//helper.RestoreZimbraProfiles();
	}
	else {
		//_stprintf (tracemsg, L"MAPILogonEx Failed: %0x", hr);
	}
	///////

	//if (_tcsstr(tracemsg,L"NooooooooError:")==NULL)
	//{
	//	TRACE(_T("%s"),tracemsg);
	//	MessageBox(tracemsg,_T("Error"), MB_ICONEXCLAMATION );
	//}
	return strlen(pszBuffer);
}
