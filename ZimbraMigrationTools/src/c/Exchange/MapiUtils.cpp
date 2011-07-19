#include "MapiUtils.h"

HRESULT Zimbra::MAPI::Util::HrMAPIFindDefaultMsgStore( IN LPMAPISESSION lplhSession, OUT ULONG *lpcbeid, OUT LPENTRYID *lppeid)
{   
    HRESULT     hr      = NOERROR;
    HRESULT     hrT     = NOERROR;
    SCODE       sc      = 0;
    LPMAPITABLE lpTable = NULL;
    LPSRowSet   lpRows  = NULL;
    LPENTRYID   lpeid   = NULL;
    ULONG       cbeid   = 0;
    ULONG       cRows   = 0;
    ULONG       i       = 0;

    SizedSPropTagArray(2, rgPropTagArray) =
    {
        2,
        {
            PR_DEFAULT_STORE,
            PR_ENTRYID
        }
    };

    // Get the list of available message stores from MAPI
    hrT = lplhSession->GetMsgStoresTable(0, &lpTable);

    if(FAILED(hrT))
    {
        hr = E_FAIL;
        goto cleanup;
    }

    // Get the row count for the message recipient table
    hrT = lpTable->GetRowCount(0, &cRows);

    if(FAILED(hrT))
    {
        hr = (E_FAIL);
        goto cleanup;
    }

    // Set the columns to return
    hrT = lpTable->SetColumns((LPSPropTagArray)&rgPropTagArray, 0);

    if(FAILED(hrT))
    {
        hr = (E_FAIL);
        goto cleanup;
    }

    // Go to the beginning of the recipient table for the envelope
    hrT = lpTable->SeekRow(BOOKMARK_BEGINNING, 0, NULL);

    if(FAILED(hrT))
    {
        hr = (E_FAIL);
        goto cleanup;
    }

    // Read all the rows of the table
    hrT = lpTable->QueryRows(cRows, 0, &lpRows);

    if(SUCCEEDED(hrT) && (lpRows != NULL) && (lpRows->cRows == 0))
    {
        FreeProws(lpRows);

        hrT = MAPI_E_NOT_FOUND;
    }

    if(FAILED(hrT) || (lpRows == NULL))
    {
        if(hrT != MAPI_E_NOT_FOUND)
        {
            hr = (E_FAIL);
        }
        else
        {
            hr = (MAPI_E_NOT_FOUND);
        }

        goto cleanup;
    }


    for(i = 0; i < cRows; i++)
    {
        if(lpRows->aRow[i].lpProps[0].Value.b == TRUE)
        {
            cbeid = lpRows->aRow[i].lpProps[1].Value.bin.cb;

            sc = MAPIAllocateBuffer(cbeid, (void **)&lpeid);

            if(FAILED(sc))
            {
                cbeid = 0;
                lpeid = NULL;

                hr = (E_OUTOFMEMORY);
                goto cleanup;
            }

            // Copy entry ID of message store
            CopyMemory(
                lpeid,
                lpRows->aRow[i].lpProps[1].Value.bin.lpb,
                cbeid);

            break;
        }
    }

    if(lpeid == NULL)
    {
        hr = (E_FAIL);
    }

cleanup:

    if(lpRows != NULL)
    {
        FreeProws(lpRows);
    }

    UlRelease(lpTable);

    *lpcbeid = cbeid;
    *lppeid = lpeid;

    return(hr);
}


HRESULT Zimbra::MAPI::Util::MailboxLogon( LPMAPISESSION pSession, LPMDB pMdb, LPWSTR pStoreDn, LPWSTR pMailboxDn, LPMDB* ppMdb )
{
    ULONG       cbeid   = 0;      // count of bytes in entry ID
    LPENTRYID   lpeid   = NULL;   // Entry ID of default store
	LPEXCHANGEMANAGESTORE pXManageStore = NULL;
	HRESULT hr = S_OK;

	//convert the dn's to ascii
	LPSTR pStoreDnA = NULL;
	LPSTR pMailboxDnA = NULL;

	WtoA( pStoreDn, pStoreDnA );
	WtoA( pMailboxDn, pMailboxDnA );

    hr = pMdb->QueryInterface( IID_IExchangeManageStore, (LPVOID*) &pXManageStore);
	if( FAILED(hr) )
	{
		SafeDelete(pStoreDnA);
		SafeDelete(pMailboxDnA);
		throw MapiUtilsException(hr, L"",__LINE__,__FILE__);
	}

    hr = pXManageStore->CreateStoreEntryID(  pStoreDnA, pMailboxDnA, OPENSTORE_HOME_LOGON | OPENSTORE_USE_ADMIN_PRIVILEGE | OPENSTORE_TAKE_OWNERSHIP, &cbeid, &lpeid);
	SafeDelete(pStoreDnA);
	SafeDelete(pMailboxDnA);
	if( pXManageStore != NULL ) 
		pXManageStore->Release();

	if( FAILED(hr) )
	{
		throw MapiUtilsException(hr, L"",__LINE__,__FILE__);
	}

    hr = pSession->OpenMsgStore( 0, cbeid, lpeid, NULL, MDB_ONLINE | MAPI_BEST_ACCESS | MDB_NO_MAIL | MDB_TEMPORARY | MDB_NO_DIALOG , ppMdb);
	if( hr == MAPI_E_UNKNOWN_FLAGS )
	{
		hr = pSession->OpenMsgStore( 0, cbeid, lpeid, NULL, MAPI_BEST_ACCESS | MDB_NO_MAIL | MDB_TEMPORARY | MDB_NO_DIALOG, ppMdb);
	}
	MAPIFreeBuffer(lpeid);

	if( FAILED(hr) )
	{
		throw MapiUtilsException(hr, L"",__LINE__,__FILE__);
	}
	
    return( S_OK);
}


HRESULT Zimbra::MAPI::Util::GetUserDnAndServerDnFromProfile( LPMAPISESSION pSession, LPSTR& pExchangeServerDn, LPSTR& pExchangeUserDn )
{
	HRESULT hr = S_OK;
	LPSERVICEADMIN pServiceAdmin = NULL;
	LPPROFSECT pProfileSection = NULL;
	SizedSPropTagArray( 2, profileProps ) = {2, { PR_PROFILE_HOME_SERVER_DN, PR_PROFILE_USER }};
	ULONG nVals;
	LPSPropValue pPropValues = NULL;

	hr = pSession->AdminServices( 0, &pServiceAdmin );
	if( FAILED(hr) )
	{
		return hr;
	}


	hr = pServiceAdmin->OpenProfileSection( (LPMAPIUID) GLOBAL_PROFILE_SECTION_GUID, NULL, 0, &pProfileSection );
	pServiceAdmin->Release();

	if( FAILED(hr) )
	{
		return hr;
	}

	
    
	hr = pProfileSection->GetProps( (LPSPropTagArray)&profileProps, 0, &nVals, &pPropValues );
	pProfileSection->Release();

	if( FAILED(hr) )
	{
		if( pPropValues != NULL )
			MAPIFreeBuffer( pPropValues );
		return hr;
	}


	if( nVals != 2 )
	{
		if( pPropValues != NULL )
			MAPIFreeBuffer( pPropValues );
		return E_FAIL;
	}


	if( pPropValues[0].ulPropTag != PR_PROFILE_HOME_SERVER_DN &&
		pPropValues[1].ulPropTag != PR_PROFILE_USER )
	{
		if( pPropValues != NULL )
			MAPIFreeBuffer( pPropValues );
		return E_FAIL;
	}

	size_t len = strlen( pPropValues[0].Value.lpszA );
	pExchangeServerDn = new CHAR[len+1];
	strcpy( pExchangeServerDn, pPropValues[0].Value.lpszA );

	len = strlen( pPropValues[1].Value.lpszA );
	pExchangeUserDn = new CHAR[len+1];
	strcpy( pExchangeUserDn, pPropValues[1].Value.lpszA );

	if( pPropValues != NULL )
			MAPIFreeBuffer( pPropValues );
	return S_OK;
}

HRESULT Zimbra::MAPI::Util::GetUserDN(LPCWSTR lpszServer, LPCWSTR lpszUser, wstring &wstruserdn)
{
	wstruserdn =L"";
	//Get IDirectorySearch Object
	CComPtr<IDirectorySearch> pDirSearch;
	wstring strADServer= L"LDAP://";
	strADServer += lpszServer;
	HRESULT hr = ADsOpenObject( strADServer.c_str(), NULL, NULL, ADS_SECURE_AUTHENTICATION, IID_IDirectorySearch, (void**)&pDirSearch);
	if(((FAILED(hr))))
	{
		throw MapiUtilsException(hr, L"Util::GetUserDN(): ADsOpenObject Failed.",__LINE__,__FILE__);
	}
	wstring strFilter = _T("(&(objectClass=organizationalPerson)(cn=");
	strFilter+=lpszUser;
	strFilter+=L"))";
	//Set Search Preferences
	ADS_SEARCH_HANDLE hSearch;
	ADS_SEARCHPREF_INFO searchPrefs[2];
	searchPrefs[0].dwSearchPref = ADS_SEARCHPREF_SEARCH_SCOPE;
	searchPrefs[0].vValue.dwType = ADSTYPE_INTEGER;
	searchPrefs[0].vValue.Integer = ADS_SCOPE_SUBTREE;
			
	//Ask for only one object that satisfies the criteria
	searchPrefs[1].dwSearchPref = ADS_SEARCHPREF_SIZE_LIMIT;
	searchPrefs[1].vValue.dwType = ADSTYPE_INTEGER;
	searchPrefs[1].vValue.Integer = 1;

	pDirSearch->SetSearchPreference( searchPrefs, 2 );
	//Retrieve the "distinguishedName" attribute for the specified dn
	LPWSTR pAttributes =  L"distinguishedName";
	hr = pDirSearch->ExecuteSearch( (LPWSTR)strFilter.c_str(), &pAttributes, 1, &hSearch );

	if( FAILED(hr) )
	{
		throw MapiUtilsException(hr, L"Util:: GetUserDN(): ExecuteSearch() Failed.",__LINE__,__FILE__);
	}
	ADS_SEARCH_COLUMN dnCol ;
	while(SUCCEEDED(hr = pDirSearch->GetNextRow(hSearch)))
	{
		if(S_OK == hr)
		{
			hr = pDirSearch->GetColumn( hSearch, pAttributes, &dnCol );
			if( FAILED(hr) )
			{
				break ;
			}
			wstruserdn = dnCol.pADsValues->CaseIgnoreString ;
			pDirSearch->CloseSearchHandle( hSearch );
			return S_OK;
		}
		else if(S_ADS_NOMORE_ROWS == hr)
		{
			// Call ADsGetLastError to see if the search is waiting for a response.
			DWORD dwError = ERROR_SUCCESS;
			WCHAR szError[512];
			WCHAR szProvider[512];

			ADsGetLastError(&dwError, szError, 512, szProvider, 512);
			if(ERROR_MORE_DATA != dwError)
			{
				break;
			}
		}
		else
		{
			break;
		}
	}
	pDirSearch->CloseSearchHandle( hSearch );
	if(wstruserdn.empty())
		throw MapiUtilsException(hr, L"Util::GetUserDN(): ADsOpenObject Failed.",__LINE__,__FILE__);
	return S_OK;
}