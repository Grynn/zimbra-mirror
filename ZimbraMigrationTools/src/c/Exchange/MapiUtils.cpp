#include "common.h"
#include "Exchange.h"

/** special folder stuff **/
inline void SFAddEidBin(UINT propIdx, LPSPropValue lpProps, UINT folderId,
    SBinaryArray *pEntryIds)
{
    if (PROP_TYPE(lpProps[propIdx].ulPropTag) == PT_ERROR)
    {
        pEntryIds->lpbin[folderId].cb = 0;
        pEntryIds->lpbin[folderId].lpb = NULL;
    }
    else
    {
        ULONG size = lpProps[propIdx].Value.bin.cb;
        pEntryIds->lpbin[folderId].cb = size;

        MAPIAllocateBuffer(size, (LPVOID *)&(pEntryIds->lpbin[folderId].lpb));
        memcpy(pEntryIds->lpbin[folderId].lpb, lpProps[propIdx].Value.bin.lpb, size);
    }
}

inline void SFAddEidMVBin(UINT mvIdx, UINT propIdx, LPSPropValue lpProps, UINT folderId,
    SBinaryArray *pEntryIds)
{
    if (PROP_TYPE(lpProps[propIdx].ulPropTag) == PT_ERROR)
    {
        pEntryIds->lpbin[folderId].cb = 0;
        pEntryIds->lpbin[folderId].lpb = NULL;
    }
    else if (lpProps[propIdx].Value.MVbin.cValues > mvIdx)
    {
        ULONG size = lpProps[propIdx].Value.MVbin.lpbin[mvIdx].cb;
        pEntryIds->lpbin[folderId].cb = size;

        MAPIAllocateBuffer(size, (LPVOID *)&(pEntryIds->lpbin[folderId].lpb));
        memcpy(pEntryIds->lpbin[folderId].lpb, (lpProps[propIdx].Value.MVbin.lpbin[mvIdx].lpb),
            size);
    }
    else
    {
        pEntryIds->lpbin[folderId].cb = 0;
        pEntryIds->lpbin[folderId].lpb = NULL;
    }
}

HRESULT Zimbra::MAPI::Util::HrMAPIFindDefaultMsgStore(LPMAPISESSION lplhSession, SBinary &bin)
{
    HRESULT hr = S_OK;
    ULONG cRows = 0;
    ULONG i = 0;

    bin.lpb = NULL;
    bin.cb = 0;
    Zimbra::Util::ScopedInterface<IMAPITable> lpTable;
    Zimbra::Util::ScopedRowSet lpRows(NULL);

    SizedSPropTagArray(2, rgPropTagArray) = {
        2, { PR_DEFAULT_STORE, PR_ENTRYID }
    };
    // Get the list of available message stores from MAPI
    if (FAILED(hr = lplhSession->GetMsgStoresTable(0, lpTable.getptr())))
    {
        throw MapiUtilsException(
            hr, L"Util:: HrMAPIFindDefaultMsgStore(): GetMsgStoresTable Failed.", __LINE__,
            __FILE__);
    }
    // Get the row count for the message recipient table
    if (FAILED(hr = lpTable->GetRowCount(0, &cRows)))
    {
        throw MapiUtilsException(hr, L"Util:: HrMAPIFindDefaultMsgStore(): GetRowCount Failed.",
            __LINE__,
            __FILE__);
    }
    // Set the columns to return
    if (FAILED(hr = lpTable->SetColumns((LPSPropTagArray) & rgPropTagArray, 0)))
    {
        throw MapiUtilsException(hr, L"Util:: HrMAPIFindDefaultMsgStore(): SetColumns Failed.",
            __LINE__,
            __FILE__);
    }
    // Go to the beginning of the recipient table for the envelope
    if (FAILED(hr = lpTable->SeekRow(BOOKMARK_BEGINNING, 0, NULL)))
    {
        throw MapiUtilsException(hr, L"Util:: HrMAPIFindDefaultMsgStore(): SeekRow Failed.",
            __LINE__,
            __FILE__);
    }
    // Read all the rows of the table
    if (FAILED(hr = lpTable->QueryRows(cRows, 0, lpRows.getptr())))
    {
        throw MapiUtilsException(hr, L"Util:: HrMAPIFindDefaultMsgStore(): QueryRows Failed.",
            __LINE__,
            __FILE__);
    }
    if (lpRows->cRows == 0)
        return MAPI_E_NOT_FOUND;
    for (i = 0; i < cRows; i++)
    {
        if (lpRows->aRow[i].lpProps[0].Value.b == TRUE)
        {
            bin.cb = lpRows->aRow[i].lpProps[1].Value.bin.cb;
            if (FAILED(MAPIAllocateBuffer(bin.cb, (void **)&bin.lpb)))
            {
                throw MapiUtilsException(
                    hr, L"Util:: HrMAPIFindDefaultMsgStore(): MAPIAllocateBuffer Failed.",
                    __LINE__,
                    __FILE__);
            }
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
    LPMDB *ppMdb)
{
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
    if (FAILED(hr))
    {
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
    {
        throw MapiUtilsException(hr, L"Util:: MailboxLogon(): CreateStoreEntryID Failed.",
            __LINE__,
            __FILE__);
    }
    hr = pSession->OpenMsgStore(0, storeEID.cb, (LPENTRYID)storeEID.lpb, NULL,
        MDB_ONLINE | MAPI_BEST_ACCESS | MDB_NO_MAIL | MDB_TEMPORARY | MDB_NO_DIALOG, ppMdb);
    if (hr == MAPI_E_FAILONEPROVIDER)
    {
        hr = pSession->OpenMsgStore(NULL, storeEID.cb, (LPENTRYID)storeEID.lpb, NULL,
            MDB_ONLINE | MAPI_BEST_ACCESS, ppMdb);
    }
    else if (hr == MAPI_E_UNKNOWN_FLAGS)
    {
        hr = pSession->OpenMsgStore(0, storeEID.cb, (LPENTRYID)storeEID.lpb, NULL,
            MAPI_BEST_ACCESS | MDB_NO_MAIL | MDB_TEMPORARY | MDB_NO_DIALOG, ppMdb);
    }
    if (FAILED(hr))
        throw MapiUtilsException(hr, L"Util:: MailboxLogon(): OpenMsgStore Failed.", __LINE__,
            __FILE__);
    return hr;
}

HRESULT Zimbra::MAPI::Util::GetUserDNAndLegacyName(LPCWSTR lpszServer, LPCWSTR lpszUser,
    LPCWSTR lpszPwd, wstring &wstruserdn,
    wstring &wstrlegacyname)
{
    wstruserdn = L"";

    // Get IDirectorySearch Object
    CComPtr<IDirectorySearch> pDirSearch;
    wstring strADServer = L"LDAP://";
    strADServer += lpszServer;
    HRESULT hr = ADsOpenObject(
        strADServer.c_str(), /*lpszUser*/ NULL, lpszPwd /*NULL*/, ADS_SECURE_AUTHENTICATION,
        IID_IDirectorySearch,
        (void **)&pDirSearch);
    if (((FAILED(hr))))
    {
        throw MapiUtilsException(hr, L"Util::GetUserDNAndLegacyName(): ADsOpenObject Failed.",
            __LINE__,
            __FILE__);
    }
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
    LPWSTR pAttributes[] = { L"distinguishedName", L"legacyExchangeDN" };
    hr = pDirSearch->ExecuteSearch((LPWSTR)strFilter.c_str(), pAttributes, 2, &hSearch);
    if (FAILED(hr))
    {
        throw MapiUtilsException(hr,
            L"Util:: GetUserDNAndLegacyName(): ExecuteSearch() Failed.", __LINE__,
            __FILE__);
    }
    ADS_SEARCH_COLUMN dnCol;
    while (SUCCEEDED(hr = pDirSearch->GetNextRow(hSearch)))
    {
        if (S_OK == hr)
        {
            // distinguishedName
            hr = pDirSearch->GetColumn(hSearch, pAttributes[0], &dnCol);
            if (FAILED(hr))
                break;
            wstruserdn = dnCol.pADsValues->CaseIgnoreString;

            // legacyExchangeDN
            hr = pDirSearch->GetColumn(hSearch, pAttributes[1], &dnCol);
            if (FAILED(hr))
                break;
            wstrlegacyname = dnCol.pADsValues->CaseIgnoreString;

            pDirSearch->CloseSearchHandle(hSearch);
            return S_OK;
        }
        else if (S_ADS_NOMORE_ROWS == hr)
        {
            // Call ADsGetLastError to see if the search is waiting for a response.
            DWORD dwError = ERROR_SUCCESS;
            WCHAR szError[512];
            WCHAR szProvider[512];

            ADsGetLastError(&dwError, szError, 512, szProvider, 512);
            if (ERROR_MORE_DATA != dwError)
                break;
        }
        else
        {
            break;
        }
    }
    pDirSearch->CloseSearchHandle(hSearch);
    if (wstruserdn.empty() || wstrlegacyname.empty())
    {
        throw MapiUtilsException(hr, L"Util::GetUserDNAndLegacyName(): S_ADS_NOMORE_ROWS.",
            __LINE__,
            __FILE__);
    }
    return S_OK;
}

HRESULT Zimbra::MAPI::Util::GetUserDnAndServerDnFromProfile(LPMAPISESSION pSession,
    LPSTR &pExchangeServerDn,
    LPSTR &pExchangeUserDn)
{
    HRESULT hr = S_OK;
    ULONG nVals = 0;

    Zimbra::Util::ScopedInterface<IMsgServiceAdmin> pServiceAdmin;
    Zimbra::Util::ScopedInterface<IProfSect> pProfileSection;
    Zimbra::Util::ScopedBuffer<SPropValue> pPropValues;

    SizedSPropTagArray(2, profileProps) = {
        2, { PR_PROFILE_HOME_SERVER_DN, PR_PROFILE_USER }
    };
    if (FAILED(hr = pSession->AdminServices(0, pServiceAdmin.getptr())))
    {
        throw MapiUtilsException(hr, L"Util::GetUserDnAndServerDnFromProfile(): AdminServices.",
            __LINE__,
            __FILE__);
    }
    if (FAILED(hr =
                pServiceAdmin->OpenProfileSection((LPMAPIUID)GLOBAL_PROFILE_SECTION_GUID, NULL,
                0,
                pProfileSection.getptr())))
    {
        throw MapiUtilsException(
            hr, L"Util::GetUserDnAndServerDnFromProfile(): OpenProfileSection.", __LINE__,
            __FILE__);
    }
    if (FAILED(hr =
                pProfileSection->GetProps((LPSPropTagArray) & profileProps, 0, &nVals,
                pPropValues.getptr())))
    {
        throw MapiUtilsException(hr, L"Util::GetUserDnAndServerDnFromProfile(): GetProps.",
            __LINE__,
            __FILE__);
    }
    if (nVals != 2)
    {
        throw MapiUtilsException(hr, L"Util::GetUserDnAndServerDnFromProfile(): nVals not 2.",
            __LINE__,
            __FILE__);
    }
    if ((pPropValues[0].ulPropTag != PR_PROFILE_HOME_SERVER_DN) &&
        (pPropValues[1].ulPropTag != PR_PROFILE_USER))
    {
        throw MapiUtilsException(hr,
            L"Util::GetUserDnAndServerDnFromProfile(): ulPropTag error.", __LINE__,
            __FILE__);
    }
    size_t len = strlen(pPropValues[0].Value.lpszA);
    pExchangeServerDn = new CHAR[len + 1];
    strcpy(pExchangeServerDn, pPropValues[0].Value.lpszA);

    len = strlen(pPropValues[1].Value.lpszA);
    pExchangeUserDn = new CHAR[len + 1];
    strcpy(pExchangeUserDn, pPropValues[1].Value.lpszA);

    return S_OK;
}

HRESULT Zimbra::MAPI::Util::HrMAPIFindIPMSubtree(LPMDB lpMdb, SBinary &bin)
{
    Zimbra::Util::ScopedBuffer<SPropValue> lpEID;
    HRESULT hr = S_OK;
    if (FAILED(hr = HrGetOneProp(lpMdb, PR_IPM_SUBTREE_ENTRYID, lpEID.getptr())))
    {
        throw MapiUtilsException(hr, L"Util::HrMAPIFindIPMSubtree(): HrGetOneProp Failed.",
            __LINE__,
            __FILE__);
    }
    bin.cb = lpEID->Value.bin.cb;
    if (FAILED(MAPIAllocateBuffer(lpEID->Value.bin.cb, (void **)&bin.lpb)))
    {
        throw MapiUtilsException(
            hr, L"Util:: HrMAPIFindDefaultMsgStore(): MAPIAllocateBuffer Failed.", __LINE__,
            __FILE__);
    }
    // Copy entry ID of message store
    CopyMemory(bin.lpb, lpEID->Value.bin.lpb, lpEID->Value.bin.cb);

    return S_OK;
}

ULONG Zimbra::MAPI::Util::IMAPHeaderInfoPropTag(LPMAPIPROP lpMapiProp)
{
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
    if (SUCCEEDED(hRes) && (PROP_TYPE(lpNamedPropTag->aulPropTag[0]) != PT_ERROR))
    {
        lpNamedPropTag->aulPropTag[0] = CHANGE_PROP_TYPE(lpNamedPropTag->aulPropTag[0], PT_LONG);
        ulIMAPHeaderPropTag = lpNamedPropTag->aulPropTag[0];
    }
    MAPIFreeBuffer(lpNamedPropTag);

    return ulIMAPHeaderPropTag;
}

wstring Zimbra::MAPI::Util::ReverseDelimitedString(wstring wstrString, WCHAR *delimiter)
{
    wstring wstrresult = L"";

    // get last pos
    wstring::size_type lastPos = wstrString.length();

    // get first last delimiter
    wstring::size_type pos = wstrString.rfind(delimiter, lastPos);

    // till npos
    while (wstring::npos != pos && wstring::npos != lastPos)
    {
        wstrresult = wstrresult + wstrString.substr(pos + 1, lastPos - pos) + delimiter;
        lastPos = pos - 1;
        pos = wstrString.rfind(delimiter, lastPos);
    }
    // add till last pos
    wstrresult = wstrresult + wstrString.substr(pos + 1, lastPos - pos);
    return wstrresult;
}

HRESULT Zimbra::MAPI::Util::GetMdbSpecialFolders(IN LPMDB lpMdb, IN OUT SBinaryArray *pEntryIds)
{
    HRESULT hr = S_OK;
    ULONG cValues = 0;
    LPSPropValue lpProps = 0;

    enum { FSUB, FOUT, FSENT, FTRASH, FNPROPS };

    SizedSPropTagArray(FNPROPS, rgSFProps) = {
        FNPROPS, {
            PR_IPM_SUBTREE_ENTRYID,
            PR_IPM_OUTBOX_ENTRYID,
            PR_IPM_SENTMAIL_ENTRYID,
            PR_IPM_WASTEBASKET_ENTRYID,
        }
    };

    hr = lpMdb->GetProps((LPSPropTagArray) & rgSFProps, 0, &cValues, &lpProps);
    if (FAILED(hr))
        goto cleanup;
    SFAddEidBin(FSUB, lpProps, IPM_SUBTREE, pEntryIds);
    SFAddEidBin(FOUT, lpProps, OUTBOX, pEntryIds);
    SFAddEidBin(FSENT, lpProps, SENTMAIL, pEntryIds);
    SFAddEidBin(FTRASH, lpProps, TRASH, pEntryIds);

cleanup:
    MAPIFreeBuffer(lpProps);
    return hr;
}

HRESULT Zimbra::MAPI::Util::GetInboxSpecialFolders(LPMAPIFOLDER pInbox, SBinaryArray *pEntryIds)
{
    HRESULT hr = S_OK;
    ULONG cValues = 0;
    LPSPropValue lpProps = 0;

    enum { FCAL, FCONTACT, FDRAFTS, FJOURNAL, FNOTE, FTASK, FOTHER, FNPROPS };

    SizedSPropTagArray(FNPROPS, rgSFProps) = {
        FNPROPS, {
            PR_IPM_APPOINTMENT_ENTRYID,
            PR_IPM_CONTACT_ENTRYID,
            PR_IPM_DRAFTS_ENTRYID,
            PR_IPM_JOURNAL_ENTRYID,
            PR_IPM_NOTE_ENTRYID,
            PR_IPM_TASK_ENTRYID,
            PR_IPM_OTHERSPECIALFOLDERS_ENTRYID
        }
    };

    hr = pInbox->GetProps((LPSPropTagArray) & rgSFProps, 0, &cValues, &lpProps);
    if (FAILED(hr))
        goto cleanup;
    SFAddEidBin(FCAL, lpProps, CALENDAR, pEntryIds);
    SFAddEidBin(FCONTACT, lpProps, CONTACTS, pEntryIds);
    SFAddEidBin(FDRAFTS, lpProps, DRAFTS, pEntryIds);
    SFAddEidBin(FJOURNAL, lpProps, JOURNAL, pEntryIds);
    SFAddEidBin(FNOTE, lpProps, NOTE, pEntryIds);
    SFAddEidBin(FTASK, lpProps, TASK, pEntryIds);
    if (PROP_TYPE(lpProps[FOTHER].ulPropTag != PT_ERROR))
    {
        SFAddEidMVBin(0, FOTHER, lpProps, SYNC_CONFLICTS, pEntryIds);
        SFAddEidMVBin(1, FOTHER, lpProps, SYNC_ISSUES, pEntryIds);
        SFAddEidMVBin(2, FOTHER, lpProps, SYNC_LOCAL_FAILURES, pEntryIds);
        SFAddEidMVBin(3, FOTHER, lpProps, SYNC_SERVER_FAILURES, pEntryIds);
        SFAddEidMVBin(4, FOTHER, lpProps, JUNK_MAIL, pEntryIds);
    }
cleanup:
    MAPIFreeBuffer(lpProps);
    return hr;
}

HRESULT Zimbra::MAPI::Util::GetAllSpecialFolders(IN LPMDB lpMdb, IN OUT SBinaryArray *pEntryIds)
{
    HRESULT hr = S_OK;
    LPMAPIFOLDER pInbox = NULL;
    ULONG obj = 0;

    pEntryIds->cValues = 0;
    pEntryIds->cValues = NULL;

    // create the memory for the pointers to the entry ids...
    hr =
        MAPIAllocateBuffer(TOTAL_NUM_SPECIAL_FOLDERS * sizeof (SBinary),
        (LPVOID *)&(pEntryIds->lpbin));
    memset(pEntryIds->lpbin, 0, TOTAL_NUM_SPECIAL_FOLDERS * sizeof (SBinary));
    if (FAILED(hr))
        goto cleanup;
    pEntryIds->cValues = TOTAL_NUM_SPECIAL_FOLDERS;

    // get the props on the mdb
    hr = GetMdbSpecialFolders(lpMdb, pEntryIds);
    if (FAILED(hr))
        goto cleanup;
    // get the inbox folder entry id's
    hr =
        lpMdb->GetReceiveFolder(NULL, 0, &(pEntryIds->lpbin[INBOX].cb),
        (LPENTRYID *)&(pEntryIds->lpbin[INBOX].lpb), NULL);
    if (FAILED(hr))
    {
        pEntryIds->lpbin[INBOX].cb = 0;
        pEntryIds->lpbin[INBOX].lpb = NULL;
        goto cleanup;
    }
    hr =
        lpMdb->OpenEntry(pEntryIds->lpbin[INBOX].cb, (LPENTRYID)(pEntryIds->lpbin[INBOX].lpb),
        NULL,
        MAPI_DEFERRED_ERRORS, &obj,
        (LPUNKNOWN *)&pInbox);
    if (FAILED(hr))
        goto cleanup;
    // get the props on the inbox folder
    hr = GetInboxSpecialFolders(pInbox, pEntryIds);
    if (FAILED(hr))
        goto cleanup;
cleanup:
    UlRelease(pInbox);
    return hr;
}

HRESULT Zimbra::MAPI::Util::FreeAllSpecialFolders(IN SBinaryArray *lpSFIds)
{
    HRESULT hr = S_OK;

    if (lpSFIds == NULL)
        return hr;
    SBinary *pBin = lpSFIds->lpbin;
    for (ULONG i = 0; i < lpSFIds->cValues; i++, pBin++)
    {
        if ((pBin->cb > 0) && (pBin->lpb != NULL))
            hr = MAPIFreeBuffer(pBin->lpb);
    }
    hr = MAPIFreeBuffer(lpSFIds->lpbin);
    return hr;
}

ExchangeSpecialFolderId Zimbra::MAPI::Util::GetExchangeSpecialFolderId(
    IN LPMDB userStore, IN ULONG cbEntryId, IN LPENTRYID pFolderEntryId,
    SBinaryArray *pEntryIds)
{
    SBinary *pCurr = pEntryIds->lpbin;

    for (ULONG i = 0; i < pEntryIds->cValues; i++, pCurr++)
    {
        ULONG bResult = 0;
        userStore->CompareEntryIDs(cbEntryId, pFolderEntryId, pCurr->cb, (LPENTRYID)pCurr->lpb,
            0,
            &bResult);
        if (bResult)
            return (ExchangeSpecialFolderId)i;
    }
    return SPECIAL_FOLDER_ID_NONE;
}

HRESULT Zimbra::MAPI::Util::GetExchangeUsersUsingObjectPicker(
    vector<ObjectPickerData> &vUserList)
{
    HRESULT hr = S_OK;
    wstring wstrExchangeDomainAddress;

    MAPIInitialize(NULL);
    CComPtr<IDsObjectPicker> pDsObjectPicker = NULL;
    hr =
        CoCreateInstance(CLSID_DsObjectPicker, NULL, CLSCTX_INPROC_SERVER, IID_IDsObjectPicker,
        (LPVOID *)&pDsObjectPicker);
    if (FAILED(hr))
    {
        MAPIUninitialize();
        throw MapiUtilsException(
            hr, L"Util::GetExchangeUsersUsingObjectPicker(): CoCreateInstance Failed.",
            __LINE__, __FILE__);
    }
    DSOP_SCOPE_INIT_INFO aScopeInit[1];
    DSOP_INIT_INFO InitInfo;

    // Initialize the DSOP_SCOPE_INIT_INFO array.
    ZeroMemory(aScopeInit, sizeof (aScopeInit));

    // Combine multiple scope types in a single array entry.
    aScopeInit[0].cbSize = sizeof (DSOP_SCOPE_INIT_INFO);
    aScopeInit[0].flType = DSOP_SCOPE_TYPE_UPLEVEL_JOINED_DOMAIN |
        DSOP_SCOPE_TYPE_DOWNLEVEL_JOINED_DOMAIN;

    // Set up-level and down-level filters to include only computer objects.
    // Up-level filters apply to both mixed and native modes.
    // Be aware that the up-level and down-level flags are different.
    aScopeInit[0].FilterFlags.Uplevel.flBothModes = DSOP_FILTER_USERS | DSOP_FILTER_COMPUTERS |
        DSOP_FILTER_WELL_KNOWN_PRINCIPALS |
        DSOP_FILTER_DOMAIN_LOCAL_GROUPS_DL;
    aScopeInit[0].FilterFlags.flDownlevel = DSOP_DOWNLEVEL_FILTER_USERS;

    // Initialize the DSOP_INIT_INFO structure.
    ZeroMemory(&InitInfo, sizeof (InitInfo));

    InitInfo.cbSize = sizeof (InitInfo);
    InitInfo.pwzTargetComputer = NULL;          // Target is the local computer.
    InitInfo.cDsScopeInfos = sizeof (aScopeInit) / sizeof (DSOP_SCOPE_INIT_INFO);
    InitInfo.aDsScopeInfos = aScopeInit;
    InitInfo.flOptions = DSOP_FLAG_MULTISELECT;

    enum ATTRS
    {
        EX_SERVER, EX_STORE, PROXY_ADDRS, C, CO, COMPANY, DESCRIPTION,
        DISPLAYNAME, GIVENNAME, INITIALS, L, O, STREETADDRESS, POSTALCODE,
        SN, ST, PHONE, TITLE, OFFICE, USERPRINCIPALNAME, OBJECTSID,
        NATTRS
    };

    LPCWSTR pAttrs[NATTRS] = {
        L"msExchHomeServerName", L"legacyExchangeDN", L"proxyAddresses", L"c", L"co",
        L"company", L"description",
        L"displayName", L"givenName", L"initials", L"l", L"o", L"streetAddress",
        L"postalCode", L"sn", L"st", L"telephoneNumber", L"title",
        L"physicalDeliveryOfficeName", L"userPrincipalName",
        L"objectSID"
    };

    InitInfo.cAttributesToFetch = NATTRS;
    InitInfo.apwzAttributeNames = pAttrs;

    // Initialize can be called multiple times, but only the last call has effect.
    // Be aware that object picker makes its own copy of InitInfo.
    hr = pDsObjectPicker->Initialize(&InitInfo);
    if (FAILED(hr))
    {
        MAPIUninitialize();
        throw MapiUtilsException(
            hr,
            L"Util::GetExchangeUsersUsingObjectPicker(): pDsObjectPicker::Initialize Failed",
            __LINE__, __FILE__);
    }
    // Supply a window handle to the application.
    // HWND hwndParent = GetConsoleWindow();
    HWND hwndParent =
        CreateWindow(L"STATIC", NULL, 0, 0, 0, 0, 0, NULL, NULL, GetModuleHandle(NULL), NULL);
    CComPtr<IDataObject> pdo = NULL;
    hr = pDsObjectPicker->InvokeDialog(hwndParent, &pdo);
    if (hr == S_OK)
    {
        // process the result set
        STGMEDIUM stm;
        FORMATETC fe;

        // Get the global memory block that contain the user's selections.
        fe.cfFormat = (CLIPFORMAT)RegisterClipboardFormat(CFSTR_DSOP_DS_SELECTION_LIST);
        fe.ptd = NULL;
        fe.dwAspect = DVASPECT_CONTENT;
        fe.lindex = -1;
        fe.tymed = TYMED_HGLOBAL;

        hr = pdo->GetData(&fe, &stm);
        if (FAILED(hr))
        {
            // if (hwndParent != NULL) {
            // DestroyWindow(hwndParent);
            // }
            MAPIUninitialize();
            throw MapiUtilsException(
                hr, L"Util::GetExchangeUsersUsingObjectPicker(): pdo::GetData Failed",
                __LINE__, __FILE__);
        }
        else
        {
            PDS_SELECTION_LIST pDsSelList = NULL;

            // Retrieve a pointer to DS_SELECTION_LIST structure.
            pDsSelList = (PDS_SELECTION_LIST)GlobalLock(stm.hGlobal);
            if (NULL != pDsSelList)
            {
                if (NULL != pDsSelList->aDsSelection[0].pwzUPN)
                    wstrExchangeDomainAddress =
                        wcschr(pDsSelList->aDsSelection[0].pwzUPN, '@') + 1;
                // TO Do: //use Zimbra domain here
                CString pDomain = wstrExchangeDomainAddress.c_str();
                int nDomain = pDomain.GetLength();
                // Loop through DS_SELECTION array of selected objects.
                for (ULONG i = 0; i < pDsSelList->cItems; i++)
                {
                    ObjectPickerData opdData;
                    if (pDsSelList->aDsSelection[i].pvarFetchedAttributes->vt == VT_EMPTY)
                        continue;
                    CString exStore(
                        LPTSTR(pDsSelList->aDsSelection[i].pvarFetchedAttributes[1].bstrVal));
                    opdData.wstrExchangeStore = exStore;

                    // VT_ARRAY | VT_VARIANT
                    SAFEARRAY *pArray =
                        pDsSelList->aDsSelection[i].pvarFetchedAttributes[PROXY_ADDRS].parray;
                    // pArray will be empty if the account is just created and never accessed
                    if (!pArray)
                        continue;
                    // Get a pointer to the elements of the array.
                    VARIANT *pbstr;
                    SafeArrayAccessData(pArray, (void **)&pbstr);

                    CString alias;

                    // Use USERPRINCIPALNAME to provison account on Zimbra. bug: 34846
                    BSTR bstrAlias =
                        pDsSelList->aDsSelection[i].pvarFetchedAttributes[USERPRINCIPALNAME].
                        bstrVal;
                    alias = bstrAlias;
                    opdData.wstrUsername = alias;
                    if (!alias.IsEmpty())
                    {
                        if (alias.Find(L"@") != -1)
                        {
                            alias.Truncate(alias.Find(L"@") + 1);
                            alias += pDomain;
                            opdData.vAliases.push_back(bstrAlias);
                        }
                        else                    // make it empty. we will try it with SMTP: address next.
                        {
                            alias.Empty();
                        }
                    }
                    try
                    {
                        for (unsigned int ipa = 0; ipa < pArray->rgsabound->cElements; ipa++)
                        {
                            LPWSTR pwszAlias = pbstr[ipa].bstrVal;
                            if (!pwszAlias)
                            {
                                // Skipping alias with value NULL
                                continue;
                            }
                            // Use the primary SMTP address to create user's account at Zimbra
                            // if "alias" is not currently set and "pwszAlias" refers to primaty SMTP address.
                            if (!alias.GetLength() && (wcsncmp(pwszAlias, L"SMTP:", 5) == 0))
                            {
                                alias = pwszAlias + 5;

                                LPWSTR pwszEnd = wcschr(pwszAlias, L'@');
                                DWORD_PTR nAlias = pwszEnd - (pwszAlias + 5);
                                if (!pwszEnd)
                                    nAlias = wcslen(alias) + 1;
                                alias = alias.Left((int)nAlias);

                                CString aliasName = alias;
                                int nAliasName = aliasName.GetLength();
                                alias += _T("@");
                                alias += pDomain;
                                UNREFERENCED_PARAMETER(nAliasName);

                                opdData.vAliases.push_back(pwszAlias + 5);

                                // Start again right from the first entry as some proxyaddresses might have
                                // appeared before primary SMTP address and got skipped
                                ipa = (unsigned int)-1;
                            }
                            // If "alias" is set and "pwszAlias" does not refer to primary SMTP address
                            // add it to the list of aliases
                            else if (alias.GetLength() &&
                                (wcsncmp(pwszAlias, L"smtp:", 5) == 0))
                            {
                                LPWSTR pwszStart = pwszAlias + 5;
                                LPWSTR pwszEnd = wcschr(pwszAlias, L'@');
                                DWORD_PTR nAlias = pwszEnd - pwszStart + 1;
                                if (!pwszEnd)
                                    nAlias = wcslen(pwszStart) + 1;
                                LPWSTR pwszZimbraAlias = new WCHAR[nAlias + nDomain + 1];
                                wcsncpy(pwszZimbraAlias, pwszStart, nAlias - 1);
                                pwszZimbraAlias[nAlias - 1] = L'\0';
                                wcscat(pwszZimbraAlias, L"@");
                                wcscat(pwszZimbraAlias, pDomain);

                                wstring pwszNameTemp = L"zimbraMailAlias";

                                // Zimbra::Util::CopyString( pwszNameTemp, L"zimbraMailAlias" );

                                std::pair<wstring, wstring> p;
                                p.first = pwszNameTemp;
                                p.second = pwszZimbraAlias;
                                opdData.pAttributeList.push_back(p);

                                opdData.vAliases.push_back(pwszAlias + 5);
                            }
                        }
                        if (alias.IsEmpty())
                            continue;
                    }
                    catch (...)
                    {
                        if (alias.IsEmpty())
                        {
                            SafeArrayUnaccessData(pArray);

                            // Unknown exception while processing aliases")
                            // Moving on to next entry
                            continue;
                        }
                    }
                    SafeArrayUnaccessData(pArray);
                    for (int j = C; j < NATTRS; j++)
                    {
                        if (pDsSelList->aDsSelection[i].pvarFetchedAttributes[j].vt == VT_EMPTY)
                            continue;
                        wstring pAttrName;
                        wstring pAttrVal;
                        std::pair<wstring, wstring> p;
                        // Get the objectSID for zimbraForeignPrincipal
                        if (j == OBJECTSID)
                        {
                            void HUGEP *pArray;
                            ULONG dwSLBound;
                            ULONG dwSUBound;

                            VARIANT var = pDsSelList->aDsSelection[i].pvarFetchedAttributes[j];

                            hr = SafeArrayGetLBound(V_ARRAY(&var), 1, (long FAR *)&dwSLBound);
                            hr = SafeArrayGetUBound(V_ARRAY(&var), 1, (long FAR *)&dwSUBound);
                            hr = SafeArrayAccessData(V_ARRAY(&var), &pArray);
                            if (SUCCEEDED(hr))
                            {
                                // Convert binary SID into String Format i.e. S-1-... Format
                                LPTSTR StringSid = NULL;
                                ConvertSidToStringSid((PSID)pArray, &StringSid);

                                // Get the name of the domain
                                TCHAR acco_name[512] = { 0 };
                                TCHAR domain_name[512] = { 0 };
                                DWORD cbacco_name = 512, cbdomain_name = 512;
                                SID_NAME_USE sid_name_use;

                                BOOL bRet = LookupAccountSid(NULL, (PSID)pArray, acco_name,
                                    &cbacco_name, domain_name,
                                    &cbdomain_name, &sid_name_use);
                                if (!bRet)
                                {
                                    // LookupAccountSid Failed: %u ,GetLastError()
                                }
                                // Convert the SID as per zimbraForeignPrincipal format
                                CString strzimbraForeignPrincipal;
                                strzimbraForeignPrincipal = CString(L"ad:") + CString(acco_name);

                                // Free the buffer allocated by ConvertSidToStringSid
                                LocalFree(StringSid);

                                // Add the SID into the attribute list vector for zimbraForeignPrincipal
                                LPCTSTR lpZFP = strzimbraForeignPrincipal;

                                // Zimbra::Util::CopyString( pAttrName, L"zimbraForeignPrincipal" );
                                // Zimbra::Util::CopyString( pAttrVal, ( LPWSTR ) lpZFP );

                                p.first = L"zimbraForeignPrincipal";
                                p.second = (LPWSTR)lpZFP;
                                opdData.pAttributeList.push_back(p);

                                break;
                            }
                        }
                        BSTR b = pDsSelList->aDsSelection[i].pvarFetchedAttributes[j].bstrVal;
                        if (b != NULL)
                        {
                            // Zimbra doesnt know USERPRINCIPALNAME. Skip it.#39286
                            if (j == USERPRINCIPALNAME)
                                continue;
                            // Change the attribute name to "street" if its "streetAddress"
                            if (j == STREETADDRESS)
                            {
                                pAttrName = L"street";

                                // Zimbra::Util::CopyString( pAttrName, L"street" );
                            }
                            else
                            {
                                pAttrName = (LPWSTR)pAttrs[j];

                                // Zimbra::Util::CopyString( pAttrName, (LPWSTR)pAttrs[j] );
                            }
                            // Zimbra::Util::CopyString( pAttrVal,  (LPWSTR)b );
                            pAttrVal = (LPWSTR)b;
                            p.first = pAttrName;
                            p.second = pAttrVal;
                            opdData.pAttributeList.push_back(p);
                        }
                    }
                    vUserList.push_back(opdData);
                }
                GlobalUnlock(stm.hGlobal);
            }
            ReleaseStgMedium(&stm);
        }
    }
    // if (hwndParent != NULL) {
    // DestroyWindow(hwndParent);
    // }
    MAPIUninitialize();
    return hr;
}

HRESULT Zimbra::MAPI::Util::HrMAPIGetSMTPAddress(IN MAPISession &session,
    IN RECIP_INFO &recipInfo,
    OUT wstring &strSmtpAddress)
{
    LPMAILUSER pUser = NULL;
    ULONG objtype = 0;
    ULONG cVals = 0;
    HRESULT hr = S_OK;
    LPSPropValue pPropVal = NULL;

    SizedSPropTagArray(1, rgPropTags) = { 1, PR_EMS_AB_PROXY_ADDRESSES };
    if (_tcsicmp(recipInfo.pAddrType, _TEXT("SMTP")) == 0)
    {
        if (recipInfo.pEmailAddr == NULL)
            strSmtpAddress = _TEXT("");

        else
            strSmtpAddress = recipInfo.pEmailAddr;
    }
    else if (_tcsicmp(recipInfo.pAddrType, _TEXT("EX")) != 0)
    {
        // unsupported sender type
        hr = E_FAIL;
    }
    else
    {
        hr = session.OpenEntry(recipInfo.cbEid, recipInfo.pEid, NULL, 0, &objtype,
            (LPUNKNOWN *)&pUser);
        if (FAILED(hr))
            return hr;
        hr = pUser->GetProps((LPSPropTagArray) & rgPropTags, fMapiUnicode, &cVals, &pPropVal);
        if (FAILED(hr))
        {
            UlRelease(pUser);
            return hr;
        }
        // loop through the resulting array looking for the address of type SMTP
        int nVals = pPropVal->Value.MVSZ.cValues;

        hr = E_FAIL;
        for (int i = 0; i < nVals; i++)
        {
            LPTSTR pAdr = pPropVal->Value.MVSZ.LPPSZ[i];
            if (_tcsncmp(pAdr, _TEXT("SMTP:"), 5) == 0)
            {
                strSmtpAddress = (pAdr + 5);
                hr = S_OK;
                break;
            }
        }
    }
    if (pPropVal != NULL)
        MAPIFreeBuffer(pPropVal);
    if (pUser != NULL)
        UlRelease(pUser);
    return hr;
}
