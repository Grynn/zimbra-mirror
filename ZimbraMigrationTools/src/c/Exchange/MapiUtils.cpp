#include "common.h"
#include "Exchange.h"


/** special folder stuff **/
inline void SFAddEidBin( UINT propIdx, LPSPropValue lpProps, UINT folderId, SBinaryArray* pEntryIds )
{
	if( PROP_TYPE(lpProps[ propIdx ].ulPropTag) == PT_ERROR )
	{
		pEntryIds->lpbin[ folderId ].cb  = 0;
		pEntryIds->lpbin[ folderId ].lpb = NULL;
	}
	else
	{
		ULONG size  = lpProps[ propIdx ].Value.bin.cb;
		pEntryIds->lpbin[ folderId ].cb = size;
		
		MAPIAllocateBuffer( size,  (LPVOID*)&(pEntryIds->lpbin[ folderId ].lpb) );
		memcpy( pEntryIds->lpbin[ folderId ].lpb, lpProps[ propIdx ].Value.bin.lpb, size );
	}
}


inline void SFAddEidMVBin( UINT mvIdx, UINT propIdx, LPSPropValue lpProps, UINT folderId, SBinaryArray* pEntryIds )
{
	if( PROP_TYPE(lpProps[ propIdx ].ulPropTag) == PT_ERROR )
	{
		pEntryIds->lpbin[ folderId ].cb  = 0;
		pEntryIds->lpbin[ folderId ].lpb = NULL;
	}
	else if( lpProps[ propIdx ].Value.MVbin.cValues > mvIdx )
	{
		ULONG size  = lpProps[ propIdx ].Value.MVbin.lpbin[mvIdx].cb;
		pEntryIds->lpbin[ folderId ].cb = size;
		
		MAPIAllocateBuffer( size,  (LPVOID*)&(pEntryIds->lpbin[ folderId ].lpb) );
		memcpy( pEntryIds->lpbin[ folderId ].lpb, (lpProps[ propIdx ].Value.MVbin.lpbin[mvIdx].lpb), size );
	}
	else
	{
		pEntryIds->lpbin[ folderId ].cb  = 0;
		pEntryIds->lpbin[ folderId ].lpb = NULL;
	}
}

HRESULT Zimbra::MAPI::Util::HrMAPIFindDefaultMsgStore(LPMAPISESSION lplhSession,
    SBinary &bin) {
    HRESULT hr = S_OK;
    ULONG cRows = 0;
    ULONG i = 0;

    bin.lpb = NULL;
    bin.cb = 0;
    Zimbra::Util::ScopedInterface<IMAPITable> lpTable;
    Zimbra::Util::ScopedRowSet lpRows(NULL);

    SizedSPropTagArray(2, rgPropTagArray) = { 2, { PR_DEFAULT_STORE, PR_ENTRYID }
    };
    // Get the list of available message stores from MAPI
    if (FAILED(hr = lplhSession->GetMsgStoresTable(0, lpTable.getptr())))
        throw MapiUtilsException(
            hr, L"Util:: HrMAPIFindDefaultMsgStore(): GetMsgStoresTable Failed.", __LINE__,
            __FILE__);
    // Get the row count for the message recipient table
    if (FAILED(hr = lpTable->GetRowCount(0, &cRows)))
        throw MapiUtilsException(hr, L"Util:: HrMAPIFindDefaultMsgStore(): GetRowCount Failed.",
            __LINE__,
            __FILE__);
    // Set the columns to return
    if (FAILED(hr = lpTable->SetColumns((LPSPropTagArray) & rgPropTagArray, 0)))
        throw MapiUtilsException(hr, L"Util:: HrMAPIFindDefaultMsgStore(): SetColumns Failed.",
            __LINE__,
            __FILE__);
    // Go to the beginning of the recipient table for the envelope
    if (FAILED(hr = lpTable->SeekRow(BOOKMARK_BEGINNING, 0, NULL)))
        throw MapiUtilsException(hr, L"Util:: HrMAPIFindDefaultMsgStore(): SeekRow Failed.",
            __LINE__,
            __FILE__);
    // Read all the rows of the table
    if (FAILED(hr = lpTable->QueryRows(cRows, 0, lpRows.getptr())))
        throw MapiUtilsException(hr, L"Util:: HrMAPIFindDefaultMsgStore(): QueryRows Failed.",
            __LINE__,
            __FILE__);
    if (lpRows->cRows == 0)
        return MAPI_E_NOT_FOUND;
    for (i = 0; i < cRows; i++) {
        if (lpRows->aRow[i].lpProps[0].Value.b == TRUE) {
            bin.cb = lpRows->aRow[i].lpProps[1].Value.bin.cb;
            if (FAILED(MAPIAllocateBuffer(bin.cb, (void **)&bin.lpb)))
                throw MapiUtilsException(
                    hr, L"Util:: HrMAPIFindDefaultMsgStore(): MAPIAllocateBuffer Failed.",
                    __LINE__,
                    __FILE__);
            // Copy entry ID of message store
            CopyMemory(bin.lpb, lpRows->aRow[i].lpProps[1].Value.bin.lpb, bin.cb);
            break;
        }
    }
    if (bin.lpb == NULL)
        return MAPI_E_NOT_FOUND;
    return hr;
}

HRESULT Zimbra::MAPI::Util::MailboxLogon(LPMAPISESSION pSession, LPMDB pMdb, LPWSTR pStoreDn,
    LPWSTR pMailboxDn,
    LPMDB *ppMdb) {
    SBinary storeEID;

    storeEID.cb = 0;
    storeEID.lpb = NULL;
    Zimbra::Util::ScopedBuffer<BYTE> pB(storeEID.lpb);
    LPEXCHANGEMANAGESTORE pXManageStore = NULL;
    HRESULT hr = S_OK;

    // convert the dn's to ascii
    LPSTR pStoreDnA = NULL;
    LPSTR pMailboxDnA = NULL;

    WtoA(pStoreDn, pStoreDnA);
    WtoA(pMailboxDn, pMailboxDnA);

    hr = pMdb->QueryInterface(IID_IExchangeManageStore, (LPVOID *)&pXManageStore);
    if (FAILED(hr)) {
        SafeDelete(pStoreDnA);
        SafeDelete(pMailboxDnA);
        throw MapiUtilsException(hr, L"Util:: MailboxLogon(): QueryInterface Failed.", __LINE__,
            __FILE__);
    }
    hr = pXManageStore->CreateStoreEntryID(pStoreDnA, pMailboxDnA,
            OPENSTORE_HOME_LOGON | OPENSTORE_USE_ADMIN_PRIVILEGE | OPENSTORE_TAKE_OWNERSHIP,
            &storeEID.cb, (LPENTRYID *)&storeEID.lpb);
    SafeDelete(pStoreDnA);
    SafeDelete(pMailboxDnA);
    if (pXManageStore != NULL)
        pXManageStore->Release();
    if (FAILED(hr))
        throw MapiUtilsException(hr, L"Util:: MailboxLogon(): CreateStoreEntryID Failed.",
            __LINE__,
            __FILE__);
    hr = pSession->OpenMsgStore(0, storeEID.cb, (LPENTRYID)storeEID.lpb, NULL,
            MDB_ONLINE | MAPI_BEST_ACCESS | MDB_NO_MAIL | MDB_TEMPORARY | MDB_NO_DIALOG, ppMdb);
    if (hr == MAPI_E_FAILONEPROVIDER) {
        hr = pSession->OpenMsgStore(NULL, storeEID.cb, (LPENTRYID)storeEID.lpb, NULL,
                MDB_ONLINE | MAPI_BEST_ACCESS, ppMdb);
    } else if (hr == MAPI_E_UNKNOWN_FLAGS) {
        hr = pSession->OpenMsgStore(0, storeEID.cb, (LPENTRYID)storeEID.lpb, NULL,
                MAPI_BEST_ACCESS | MDB_NO_MAIL | MDB_TEMPORARY | MDB_NO_DIALOG, ppMdb);
    }
    if (FAILED(hr))
        throw MapiUtilsException(hr, L"Util:: MailboxLogon(): OpenMsgStore Failed.", __LINE__,
            __FILE__);
    return hr;
}

HRESULT Zimbra::MAPI::Util::GetUserDNAndLegacyName(LPCWSTR lpszServer, LPCWSTR lpszUser, LPCWSTR lpszPwd,
    wstring &wstruserdn,wstring &wstrlegacyname) {
    wstruserdn = L"";

    // Get IDirectorySearch Object
    CComPtr<IDirectorySearch> pDirSearch;
    wstring strADServer = L"LDAP://";
    strADServer += lpszServer;
    HRESULT hr = ADsOpenObject(
            strADServer.c_str(), lpszUser/*NULL*/,lpszPwd /*NULL*/, ADS_SECURE_AUTHENTICATION, IID_IDirectorySearch,
            (void **)&pDirSearch);
    if (((FAILED(hr))))
        throw MapiUtilsException(hr, L"Util::GetUserDNAndLegacyName(): ADsOpenObject Failed.", __LINE__,
            __FILE__);
    wstring strFilter = _T("(&(objectClass=organizationalPerson)(cn=");
    strFilter += lpszUser;
    strFilter += L"))";

    // Set Search Preferences
    ADS_SEARCH_HANDLE hSearch;
    ADS_SEARCHPREF_INFO searchPrefs[2];
    searchPrefs[0].dwSearchPref = ADS_SEARCHPREF_SEARCH_SCOPE;
    searchPrefs[0].vValue.dwType = ADSTYPE_INTEGER;
    searchPrefs[0].vValue.Integer = ADS_SCOPE_SUBTREE;

    // Ask for only one object that satisfies the criteria
    searchPrefs[1].dwSearchPref = ADS_SEARCHPREF_SIZE_LIMIT;
    searchPrefs[1].vValue.dwType = ADSTYPE_INTEGER;
    searchPrefs[1].vValue.Integer = 1;

    pDirSearch->SetSearchPreference(searchPrefs, 2);

    // Retrieve the "distinguishedName" attribute for the specified dn
	LPWSTR pAttributes[] = {L"distinguishedName",L"legacyExchangeDN"};
    hr = pDirSearch->ExecuteSearch((LPWSTR)strFilter.c_str(), pAttributes, 2, &hSearch);
    if (FAILED(hr))
        throw MapiUtilsException(hr, L"Util:: GetUserDNAndLegacyName(): ExecuteSearch() Failed.", __LINE__,
            __FILE__);
    ADS_SEARCH_COLUMN dnCol;
    while (SUCCEEDED(hr = pDirSearch->GetNextRow(hSearch))) {
        if (S_OK == hr) {
			//distinguishedName
            hr = pDirSearch->GetColumn(hSearch, pAttributes[0], &dnCol);
            if (FAILED(hr))
                break;
            wstruserdn = dnCol.pADsValues->CaseIgnoreString;
			//legacyExchangeDN
			hr = pDirSearch->GetColumn(hSearch, pAttributes[1], &dnCol);
            if (FAILED(hr))
                break;
            wstrlegacyname = dnCol.pADsValues->CaseIgnoreString;

            pDirSearch->CloseSearchHandle(hSearch);
            return S_OK;
        } else if (S_ADS_NOMORE_ROWS == hr) {
            // Call ADsGetLastError to see if the search is waiting for a response.
            DWORD dwError = ERROR_SUCCESS;
            WCHAR szError[512];
            WCHAR szProvider[512];

            ADsGetLastError(&dwError, szError, 512, szProvider, 512);
            if (ERROR_MORE_DATA != dwError)
                break;
        } else {
            break;
        }
    }
    pDirSearch->CloseSearchHandle(hSearch);
	if (wstruserdn.empty() || wstrlegacyname.empty())
        throw MapiUtilsException(hr, L"Util::GetUserDNAndLegacyName(): S_ADS_NOMORE_ROWS.", __LINE__,
            __FILE__);
    return S_OK;
}

HRESULT Zimbra::MAPI::Util::GetUserDnAndServerDnFromProfile(LPMAPISESSION pSession,
    LPSTR &pExchangeServerDn,
    LPSTR &pExchangeUserDn) {
    HRESULT hr = S_OK;
    ULONG nVals = 0;

    Zimbra::Util::ScopedInterface<IMsgServiceAdmin> pServiceAdmin;
    Zimbra::Util::ScopedInterface<IProfSect> pProfileSection;
    Zimbra::Util::ScopedBuffer<SPropValue> pPropValues;

    SizedSPropTagArray(2, profileProps) = { 2, { PR_PROFILE_HOME_SERVER_DN, PR_PROFILE_USER }
    };
    if (FAILED(hr = pSession->AdminServices(0, pServiceAdmin.getptr())))
        throw MapiUtilsException(hr, L"Util::GetUserDnAndServerDnFromProfile(): AdminServices.",
            __LINE__,
            __FILE__);
    if (FAILED(hr =
                pServiceAdmin->OpenProfileSection((LPMAPIUID)GLOBAL_PROFILE_SECTION_GUID, NULL,
                    0,
                    pProfileSection.getptr())))
        throw MapiUtilsException(
            hr, L"Util::GetUserDnAndServerDnFromProfile(): OpenProfileSection.", __LINE__,
            __FILE__);
    if (FAILED(hr =
                pProfileSection->GetProps((LPSPropTagArray) & profileProps, 0, &nVals,
                    pPropValues.getptr())))
        throw MapiUtilsException(hr, L"Util::GetUserDnAndServerDnFromProfile(): GetProps.",
            __LINE__,
            __FILE__);
    if (nVals != 2)
        throw MapiUtilsException(hr, L"Util::GetUserDnAndServerDnFromProfile(): nVals not 2.",
            __LINE__,
            __FILE__);
    if ((pPropValues[0].ulPropTag != PR_PROFILE_HOME_SERVER_DN) &&
        (pPropValues[1].ulPropTag != PR_PROFILE_USER))
        throw MapiUtilsException(hr,
            L"Util::GetUserDnAndServerDnFromProfile(): ulPropTag error.", __LINE__,
            __FILE__);
    size_t len = strlen(pPropValues[0].Value.lpszA);
    pExchangeServerDn = new CHAR[len + 1];
    strcpy(pExchangeServerDn, pPropValues[0].Value.lpszA);

    len = strlen(pPropValues[1].Value.lpszA);
    pExchangeUserDn = new CHAR[len + 1];
    strcpy(pExchangeUserDn, pPropValues[1].Value.lpszA);

    return S_OK;
}

HRESULT Zimbra::MAPI::Util::HrMAPIFindIPMSubtree(LPMDB lpMdb, SBinary &bin) {
    Zimbra::Util::ScopedBuffer<SPropValue> lpEID;
    HRESULT hr = S_OK;
    if (FAILED(hr = HrGetOneProp(lpMdb, PR_IPM_SUBTREE_ENTRYID, lpEID.getptr())))
        throw MapiUtilsException(hr, L"Util::HrMAPIFindIPMSubtree(): HrGetOneProp Failed.",
            __LINE__,
            __FILE__);
    bin.cb = lpEID->Value.bin.cb;
    if (FAILED(MAPIAllocateBuffer(lpEID->Value.bin.cb, (void **)&bin.lpb)))
        throw MapiUtilsException(
            hr, L"Util:: HrMAPIFindDefaultMsgStore(): MAPIAllocateBuffer Failed.", __LINE__,
            __FILE__);
    // Copy entry ID of message store
    CopyMemory(bin.lpb, lpEID->Value.bin.lpb, lpEID->Value.bin.cb);

    return S_OK;
}

ULONG Zimbra::MAPI::Util::IMAPHeaderInfoPropTag(LPMAPIPROP lpMapiProp) {
    HRESULT hRes = S_OK;
    LPSPropTagArray lpNamedPropTag = NULL;
    MAPINAMEID NamedID = { 0 };
    LPMAPINAMEID lpNamedID = NULL;

    ULONG ulIMAPHeaderPropTag = PR_NULL;

    NamedID.lpguid = (LPGUID)&PSETID_COMMON;
    NamedID.ulKind = MNID_ID;
    NamedID.Kind.lID = DISPID_HEADER_ITEM;
    lpNamedID = &NamedID;

    hRes = lpMapiProp->GetIDsFromNames(1, &lpNamedID, NULL, &lpNamedPropTag);
    if (SUCCEEDED(hRes) && (PROP_TYPE(lpNamedPropTag->aulPropTag[0]) != PT_ERROR)) {
        lpNamedPropTag->aulPropTag[0] = CHANGE_PROP_TYPE(lpNamedPropTag->aulPropTag[0], PT_LONG);
        ulIMAPHeaderPropTag = lpNamedPropTag->aulPropTag[0];
    }
    MAPIFreeBuffer(lpNamedPropTag);

    return ulIMAPHeaderPropTag;
}

HRESULT Zimbra::MAPI::Util::CopyEntryID(SBinary &src, SBinary &dest)
{
	HRESULT hr=S_OK;
	dest.cb = src.cb;
    hr=MAPIAllocateBuffer(src.cb, (LPVOID *)&(dest.lpb));
    memcpy(dest.lpb, src.lpb, dest.cb);
	return hr;
}

wstring Zimbra::MAPI::Util::ReverseDelimitedString(wstring wstrString, WCHAR* delimiter)
{
	wstring wstrresult=L"";
	//get last pos
	wstring::size_type lastPos = wstrString.length();
	//get first last delimiter
	wstring::size_type pos = wstrString.rfind(delimiter, lastPos);

	//till npos
    while (wstring::npos != pos && wstring::npos != lastPos)
    {
        wstrresult= wstrresult+wstrString.substr(pos+1, lastPos-pos)+delimiter;
		lastPos = pos-1;
        pos = wstrString.rfind(delimiter, lastPos);
    }
	//add till last pos
	wstrresult= wstrresult+wstrString.substr(pos+1, lastPos-pos);
	return wstrresult;
}

HRESULT Zimbra::MAPI::Util::GetMdbSpecialFolders( IN LPMDB lpMdb, IN OUT SBinaryArray* pEntryIds )
{
	HRESULT hr = S_OK;
	ULONG cValues = 0;
	LPSPropValue lpProps = 0;
	enum{FSUB, FOUT, FSENT, FTRASH, FNPROPS };
	SizedSPropTagArray( FNPROPS, rgSFProps ) = 
	{ FNPROPS, { 
		PR_IPM_SUBTREE_ENTRYID,
		PR_IPM_OUTBOX_ENTRYID,
		PR_IPM_SENTMAIL_ENTRYID,
		PR_IPM_WASTEBASKET_ENTRYID,
	}};

	hr = lpMdb->GetProps( (LPSPropTagArray)&rgSFProps, 0, &cValues, &lpProps );
	if( FAILED(hr) )
	{
		goto cleanup;
	}

	SFAddEidBin( FSUB,   lpProps, IPM_SUBTREE, pEntryIds );
	SFAddEidBin( FOUT,   lpProps, OUTBOX,      pEntryIds );
	SFAddEidBin( FSENT,  lpProps, SENTMAIL,    pEntryIds );
	SFAddEidBin( FTRASH, lpProps, TRASH,       pEntryIds );

cleanup:
	MAPIFreeBuffer(lpProps);
	return hr;
}

HRESULT Zimbra::MAPI::Util::GetInboxSpecialFolders( LPMAPIFOLDER pInbox, SBinaryArray* pEntryIds )
{
	HRESULT hr = S_OK;
	ULONG cValues = 0;
	LPSPropValue lpProps = 0;
	enum{FCAL, FCONTACT, FDRAFTS, FJOURNAL, FNOTE, FTASK, FOTHER, FNPROPS };
	SizedSPropTagArray( FNPROPS, rgSFProps ) = 
	{ FNPROPS, { 
		PR_IPM_APPOINTMENT_ENTRYID,
		PR_IPM_CONTACT_ENTRYID,
		PR_IPM_DRAFTS_ENTRYID,
		PR_IPM_JOURNAL_ENTRYID,
		PR_IPM_NOTE_ENTRYID,
		PR_IPM_TASK_ENTRYID,
		PR_IPM_OTHERSPECIALFOLDERS_ENTRYID
	}};

	hr = pInbox->GetProps( (LPSPropTagArray)&rgSFProps, 0, &cValues, &lpProps );
	if( FAILED(hr) )
	{
		goto cleanup;
	}

	SFAddEidBin( FCAL,     lpProps, CALENDAR, pEntryIds );
	SFAddEidBin( FCONTACT, lpProps, CONTACTS, pEntryIds );
	SFAddEidBin( FDRAFTS,  lpProps, DRAFTS,   pEntryIds );
	SFAddEidBin( FJOURNAL, lpProps, JOURNAL,  pEntryIds );
	SFAddEidBin( FNOTE,    lpProps, NOTE,     pEntryIds );
	SFAddEidBin( FTASK,    lpProps, TASK,     pEntryIds );

	if( PROP_TYPE(lpProps[ FOTHER ].ulPropTag != PT_ERROR ) )
	{
		SFAddEidMVBin( 0, FOTHER, lpProps, SYNC_CONFLICTS, pEntryIds );
		SFAddEidMVBin( 1, FOTHER, lpProps, SYNC_ISSUES, pEntryIds );
		SFAddEidMVBin( 2, FOTHER, lpProps, SYNC_LOCAL_FAILURES, pEntryIds );
		SFAddEidMVBin( 3, FOTHER, lpProps, SYNC_SERVER_FAILURES, pEntryIds );
		SFAddEidMVBin( 4, FOTHER, lpProps, JUNK_MAIL, pEntryIds );	
	}
cleanup:
	MAPIFreeBuffer(lpProps);
	return hr;
}

HRESULT Zimbra::MAPI::Util::GetAllSpecialFolders( IN LPMDB lpMdb, IN OUT SBinaryArray* pEntryIds )
{
	HRESULT hr = S_OK;
	LPMAPIFOLDER pInbox = NULL;
	ULONG obj = 0;
	pEntryIds->cValues = 0;
	pEntryIds->cValues = NULL;

	//create the memory for the pointers to the entry ids...
	hr = MAPIAllocateBuffer( TOTAL_NUM_SPECIAL_FOLDERS * sizeof(SBinary), (LPVOID*)&(pEntryIds->lpbin) );
	memset( pEntryIds->lpbin, 0, TOTAL_NUM_SPECIAL_FOLDERS * sizeof(SBinary) );
	if( FAILED(hr) )
	{
		goto cleanup;
	}
	pEntryIds->cValues = TOTAL_NUM_SPECIAL_FOLDERS;

	//get the props on the mdb
	hr = GetMdbSpecialFolders( lpMdb, pEntryIds );
	if( FAILED(hr) )
	{
		goto cleanup;
	}
    	
	//get the inbox folder entry id's
	hr = lpMdb->GetReceiveFolder( NULL, 0, &(pEntryIds->lpbin[INBOX].cb), (LPENTRYID*)&(pEntryIds->lpbin[INBOX].lpb), NULL );
	if( FAILED(hr) )
	{
		pEntryIds->lpbin[INBOX].cb = 0;
		pEntryIds->lpbin[INBOX].lpb = NULL;
		goto cleanup;
	}

	hr = lpMdb->OpenEntry(pEntryIds->lpbin[INBOX].cb, (LPENTRYID)(pEntryIds->lpbin[INBOX].lpb), NULL, MAPI_DEFERRED_ERRORS, &obj, (LPUNKNOWN*)&pInbox );
	if( FAILED(hr) )
	{
		goto cleanup;
	}

	//get the props on the inbox folder
	hr = GetInboxSpecialFolders( pInbox, pEntryIds );
	if( FAILED(hr) )
	{
		goto cleanup;
	}
cleanup:
	UlRelease( pInbox );
	return hr;
}

HRESULT Zimbra::MAPI::Util::FreeAllSpecialFolders( IN SBinaryArray* lpSFIds )
{
	HRESULT hr = S_OK;
	if( lpSFIds == NULL )
		return hr;

	SBinary* pBin = lpSFIds->lpbin;
	for( ULONG i = 0; i < lpSFIds->cValues; i++, pBin++ )
	{
		if( pBin->cb > 0 && pBin->lpb != NULL )
			hr = MAPIFreeBuffer( pBin->lpb );
	}
	hr = MAPIFreeBuffer( lpSFIds->lpbin );
	return hr;
}

ExchangeSpecialFolderId Zimbra::MAPI::Util::GetExchangeSpecialFolderId(IN LPMAPISESSION lpSession, 
	IN ULONG cbEntryId, IN LPENTRYID pFolderEntryId, SBinaryArray* pEntryIds)
{
	SBinary* pCurr = pEntryIds->lpbin;
	for( ULONG i = 0; i < pEntryIds->cValues; i++, pCurr++ )
	{
		ULONG bResult = 0;
		lpSession->CompareEntryIDs( cbEntryId, pFolderEntryId, pCurr->cb, (LPENTRYID)pCurr->lpb, 0, &bResult );
		if( bResult )
			return (ExchangeSpecialFolderId)i;
	}
	return SPECIAL_FOLDER_ID_NONE;
}
