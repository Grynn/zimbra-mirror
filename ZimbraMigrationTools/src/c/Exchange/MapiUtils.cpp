#include "common.h"
#include "Exchange.h"
#include <strsafe.h>
#pragma comment(lib, "netapi32.lib")
#include <lm.h>


enum AttachPropIdx
{
    ATTACH_METHOD = 0, ATTACH_CONTENT_ID,
    ATTACH_LONG_FILENAME, WATTACH_LONG_FILENAME,
    ATTACH_FILENAME, ATTACH_MIME_TAG,
    ATTACH_DISPLAY_NAME, WATTACH_DISPLAY_NAME,
    ATTACH_CONTENT_LOCATION, ATTACH_DISPOSITION,
    ATTACH_EXTENSION, ATTACH_ENCODING,
    NATTACH_PROPS
};

SizedSPropTagArray(NATTACH_PROPS, attachProps) = {
    NATTACH_PROPS,
    {
        PR_ATTACH_METHOD, PR_ATTACH_CONTENT_ID_A,
        PR_ATTACH_LONG_FILENAME_A, PR_ATTACH_LONG_FILENAME_W,
        PR_ATTACH_FILENAME_A, PR_ATTACH_MIME_TAG_A,
        PR_DISPLAY_NAME_A, PR_DISPLAY_NAME_W,
        PR_ATTACH_CONTENT_LOCATION_A, PR_ATTACH_DISPOSITION_A,
        PR_ATTACH_EXTENSION_A, PR_ATTACH_ENCODING
    }
};

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
    HRESULT hr = ADsOpenObject(strADServer.c_str(),
		/*lpszUser*/ NULL, lpszPwd /*NULL*/, ADS_SECURE_AUTHENTICATION,
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
    strcpy_s(pExchangeServerDn, len + 1, pPropValues[0].Value.lpszA);

    len = strlen(pPropValues[1].Value.lpszA);
    pExchangeUserDn = new CHAR[len + 1];
    strcpy_s(pExchangeUserDn, len + 1, pPropValues[1].Value.lpszA);

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
								wcscat_s(pwszZimbraAlias, (nAlias + nDomain + 1),L"@");
                                wcscat_s(pwszZimbraAlias, (nAlias + nDomain + 1), pDomain);

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

BOOL Zimbra::MAPI::Util::CompareRecipients(MAPISession &session, RECIP_INFO &r1, RECIP_INFO &r2)
{
    SBinary eid1, eid2;

    eid1.cb = r1.cbEid;
    eid2.cb = r2.cbEid;
    eid1.lpb = (LPBYTE)r1.pEid;
    eid2.lpb = (LPBYTE)r2.pEid;
    if ((eid1.lpb == NULL) || (eid2.lpb == NULL))
        return FALSE;
    ULONG ulResult = FALSE;
    session.CompareEntryIDs(&eid1, &eid2, ulResult);
    return ulResult;
}

bool Zimbra::MAPI::Util::NeedsEncoding(LPSTR pStr)
{
    for (unsigned int i = 0; i < strlen(pStr); i++)
    {
        unsigned char cVal = (unsigned char)(pStr[i]);
        if (((cVal > 0) && (cVal < 32)) ||
            ((cVal > 127) && (cVal < 256)))
            return true;
    }                                           // for( int i = 0; i < strlen(pStr); i++ )
    return false;
}

void Zimbra::MAPI::Util::CreateMimeSubject(IN LPTSTR pSubject, IN UINT codepage,
    IN OUT LPSTR *ppMimeSubject)
{
    LPSTR pMBSubject = NULL;

#if UNICODE
    // convert the wide string to the appropriate multi-byte string
    // get the length required
    int nLen = WideCharToMultiByte(codepage, 0, pSubject, (int)wcslen(
            pSubject), NULL, 0, NULL, NULL);

    // don't forget to free this buffer...
    pMBSubject = new CHAR[nLen + 1];
    ZeroMemory(pMBSubject, nLen + 1);

    // do the conversion
    WideCharToMultiByte(codepage, 0, pSubject, (int)wcslen(
            pSubject), pMBSubject, nLen + 1, NULL, NULL);
#else
    // no conversion required
    pMBSubject = pSubject;
#endif
    // do we need to do RFC2047 encoding on this string?
    if (NeedsEncoding(pMBSubject))
    {
        // get the string version of the codepage
        LPSTR pCharset = NULL;
        CharsetUtil::CharsetStringFromCodePageId(codepage, &pCharset);

        // use mimepp to encode the string
        mimepp::EncodedWord subj;
        subj.setDecodedText(pMBSubject);
        subj.setCharset(pCharset);
        subj.setEncodingType('q');
        subj.assemble();

        *ppMimeSubject = new CHAR[subj.getString().length() + 1];
        strcpy_s(*ppMimeSubject, subj.getString().length() + 1, subj.getString().c_str());
        if (pCharset != NULL)
            delete[] pCharset;
    }
    else
    {
        *ppMimeSubject = new CHAR[strlen(pMBSubject) + 1];
        strcpy_s(*ppMimeSubject, strlen(pMBSubject) + 1, pMBSubject);
    }
#if UNICODE
    delete[] pMBSubject;
#endif
}

void ReplaceLFWithCRLF(LPSTR pszMimeMsg, UINT mimeLength, LPSTR *ppszNewMsg, size_t *pNewLength)
{
    LPSTR pNewMsg = new CHAR[mimeLength * 2 + 2];
    size_t nNewLen = 0;

    *ppszNewMsg = pNewMsg;
    bool bLastCr = false;
    for (UINT i = 0; i < mimeLength; i++, pszMimeMsg++, pNewMsg++, nNewLen++)
    {
        if (!bLastCr && (*pszMimeMsg == '\n'))
        {
            *pNewMsg = '\r';
            pNewMsg++;
            nNewLen++;
        }
        else
        {
            bLastCr = (*pszMimeMsg == '\r');
        }
        *pNewMsg = *pszMimeMsg;
    }
    *pNewLength = nNewLen;
    // DumpBuffer( *ppszNewMsg, *pNewLength );
}

void Zimbra::MAPI::Util::AddBodyToPart(mimepp::BodyPart *pPart, LPSTR pStr, size_t length,
    BOOL bConvertLFToCRLF)
{
    if (!pStr)
    {
        // TRACE( _T("Zimbra::MAPI::Util::AddBodyToPart: No body pStr found"));
        return;
    }
    LPSTR pBuf = NULL;
    if (bConvertLFToCRLF)
        ReplaceLFWithCRLF(pStr, (UINT)length, &pBuf, &length);
    else
        pBuf = pStr;
    mimepp::String encodedBodyStr;
    mimepp::String bodyStr(pBuf, length);
    MIME_ENCODING me = CharsetUtil::FindBestEncoding(pBuf, (int)length);
    switch (me)
    {
    case ME_QUOTED_PRINTABLE:
    {
        // TODO: scream at hunny soft.
        try
        {
            mimepp::QuotedPrintableEncoder encoder;
            encodedBodyStr = encoder.encode(bodyStr);
            pPart->headers().contentTransferEncoding().setString("Quoted-printable");
        }
        catch (...)                             // if it barfs, re-encode it as base64
        {
            mimepp::Base64Encoder encoder;
            encodedBodyStr = encoder.encode(bodyStr);
            pPart->headers().contentTransferEncoding().setString("Base64");
        }
    }
    break;

    case ME_BASE64:
    {
        pPart->headers().contentTransferEncoding().setString("Base64");
        mimepp::Base64Encoder encoder;
        encodedBodyStr = encoder.encode(bodyStr);
    }
    break;

    case ME_7BIT:
    {
        pPart->headers().contentTransferEncoding().setString("7bit");
        encodedBodyStr = bodyStr;
    }
    break;
    }
    pPart->body().setString(encodedBodyStr);
    if (bConvertLFToCRLF)
        delete[] pBuf;
}

void GetContentTypeFromExtension(LPSTR pExt, LPSTR &pContentType)
{
    pContentType = NULL;
    if (pExt == NULL)
        return;
    HKEY hExtKey;
    if (RegOpenKeyA(HKEY_CLASSES_ROOT, pExt, &hExtKey) != ERROR_SUCCESS)
        return;
    DWORD type;
    DWORD nBytes;
    if ((RegQueryValueExA(hExtKey, "Content Type", NULL, &type, NULL,
                &nBytes) != ERROR_SUCCESS) || (type != REG_SZ))
    {
        RegCloseKey(hExtKey);
        return;
    }
    pContentType = new CHAR[nBytes];
    if (RegQueryValueExA(hExtKey, "Content Type", NULL, &type, (LPBYTE)pContentType,
            &nBytes) != ERROR_SUCCESS)
    {
        RegCloseKey(hExtKey);
        delete[] pContentType;
        pContentType = NULL;
        return;
    }
    RegCloseKey(hExtKey);
}

BYTE OID_MAC_BINARY[] = { 0x2A, 0x86, 0x48, 0x86, 0xf7, 0x14, 0x03, 0x0B, 0x01 };
mimepp::BodyPart *Zimbra::MAPI::Util::AttachPartFromIAttach(MAPISession &session,
    LPATTACH pAttach, LPSTR pCharset,
    LONG codepage)
{
    // text/plain attachments may start with a byte order mark
    enum BOM { BOM_UTF32BE, BOM_UTF32LE, BOM_UTF16BE, BOM_UTF16LE, BOM_UTF8, NBOMS, BOM_NONE };

    int nboms[] = { 4, 4, 2, 2, 3 };
    BYTE boms[][4] = {
        { 0x00, 0x00, 0xFE, 0xFF },
        { 0xFF, 0xFF, 0x00, 0x00 },
        { 0xFE, 0xFF },
        { 0xFF, 0xFE },
        { 0xEF, 0xBB, 0xBF }
    };
    LPSTR pBomCharsets[] = { "UTF-32BE", "UTF-32LE", "UTF-16BE", "UTF-16LE", "UTF-8" };

    BOM bom = BOM_NONE;

    BOOL bMacEncoded = FALSE;

    mimepp::BodyPart *pAttachPart = NULL;
    if (pAttach == NULL)
        return NULL;
    LPSTR pAttachFilename = NULL;
    HRESULT hr;
    LPSPropValue pProps = NULL;
    ULONG cProps = 0;
    hr = pAttach->GetProps((LPSPropTagArray) & attachProps, 0, &cProps, &pProps);
    mimepp::String strContentType("");
    if (FAILED(hr))
    {
        throw Zimbra::MAPI::Util::MapiUtilsException(
            hr, L"Util::AttachPartFromIAttach(): GetProps.",
            __LINE__,
            __FILE__);
    }
    if (pProps[ATTACH_METHOD].ulPropTag != PR_ATTACH_METHOD)
    {
        MAPIFreeBuffer(pProps);
        return NULL;
    }
    pAttachPart = new mimepp::BodyPart;
    if ((pProps[ATTACH_ENCODING].ulPropTag == PR_ATTACH_ENCODING) &&
        (pProps[ATTACH_ENCODING].Value.bin.cb == sizeof (OID_MAC_BINARY)))
    {
        bMacEncoded = (memcmp(OID_MAC_BINARY,
                pProps[ATTACH_ENCODING].Value.bin.lpb,
                pProps[ATTACH_ENCODING].Value.bin.cb) ==
            0);
    }
    if (pProps[ATTACH_CONTENT_ID].ulPropTag == PR_ATTACH_CONTENT_ID_A)
    {
        mimepp::String contentId("<");
        contentId += pProps[ATTACH_CONTENT_ID].Value.lpszA;
        contentId += ">";
        pAttachPart->headers().contentId().setString(contentId);
    }
    if (pProps[ATTACH_CONTENT_LOCATION].ulPropTag == PR_ATTACH_CONTENT_LOCATION_A)
    {
        // add a custom header for content location to support rfc2557
        pAttachPart->headers().fieldBody("Content-Location").setText(pProps[
                ATTACH_CONTENT_LOCATION].Value.lpszA);
    }
    if (pProps[ATTACH_DISPOSITION].ulPropTag == PR_ATTACH_DISPOSITION_A)
        pAttachPart->headers().contentDisposition().setType(
            pProps[ATTACH_DISPOSITION].Value.lpszA);
    else
        pAttachPart->headers().contentDisposition().setType("attachment");
    if (PROP_TYPE(pProps[WATTACH_LONG_FILENAME].ulPropTag) != PT_ERROR)
    {
        LPSTR pMimeAttName = NULL;
        int nLen = (int)_tcslen(pProps[WATTACH_LONG_FILENAME].Value.lpszW);
        UNREFERENCED_PARAMETER(nLen);
        LPTSTR pAttname = pProps[WATTACH_LONG_FILENAME].Value.lpszW;
        CreateMimeSubject(pAttname, codepage, &pMimeAttName);
        mimepp::String AttStr(pMimeAttName);

        pAttachPart->headers().contentDisposition().setFilename(AttStr);
        pAttachPart->headers().contentDisposition().assemble();
        if (pMimeAttName != NULL)
            delete[] pMimeAttName;
    }
    else if (pProps[ATTACH_LONG_FILENAME].ulPropTag == PR_ATTACH_LONG_FILENAME_A)
    {
        pAttachFilename = pProps[ATTACH_LONG_FILENAME].Value.lpszA;
        pAttachPart->headers().contentDisposition().setFilename(pAttachFilename);
        pAttachPart->headers().contentDisposition().assemble();
    }
    else if (pProps[ATTACH_FILENAME].ulPropTag == PR_ATTACH_FILENAME_A)
    {
        pAttachFilename = pProps[ATTACH_FILENAME].Value.lpszA;
        pAttachPart->headers().contentDisposition().setFilename(pAttachFilename);
        pAttachPart->headers().contentDisposition().assemble();
    }
    else if (pProps[ATTACH_DISPLAY_NAME].ulPropTag == PR_DISPLAY_NAME_A)
    {
        pAttachFilename = pProps[ATTACH_DISPLAY_NAME].Value.lpszA;
        pAttachPart->headers().contentDisposition().setFilename(pAttachFilename);
        pAttachPart->headers().contentDisposition().assemble();
    }
    // add the attachment data - embedded or regular?
    switch (pProps[0].Value.l)
    {
    case ATTACH_BY_VALUE:
        // pr_attach_data_bin
    {
        LPVOID pAttachData = NULL;
        LPVOID inBuffer = NULL;                 // temp buffer
        size_t attachDataLen = 0;

        LPSPropValue pProp = NULL;
        hr = HrGetOneProp(pAttach, PR_ATTACH_DATA_BIN, &pProp);
        if (hr == E_OUTOFMEMORY)
        {
            // need to IStream it out
            IStream *pIStream;
            hr =
                    pAttach->OpenProperty(PR_ATTACH_DATA_BIN, &IID_IStream, STGM_READ, 0,
                    (LPUNKNOWN FAR *)&pIStream);
            if (!SUCCEEDED(hr))
            {
                delete pAttachPart;
                pAttachPart = NULL;
                return NULL;
            }
            // discover the size of the incoming body
            STATSTG statstg;
            hr = pIStream->Stat(&statstg, STATFLAG_NONAME);
            if (!SUCCEEDED(hr))
            {
                delete pAttachPart;
                pAttachPart = NULL;
                return NULL;
            }
            attachDataLen = statstg.cbSize.LowPart;

            // allocate buffer for incoming body data
            hr = MAPIAllocateBuffer((ULONG)attachDataLen, (LPVOID FAR *)&inBuffer);
            if (!SUCCEEDED(hr))
            {
                delete pAttachPart;
                pAttachPart = NULL;
                return NULL;
            }
            ZeroMemory(inBuffer, (int)attachDataLen);

            // download the text
            ULONG cb;
            hr = pIStream->Read(inBuffer, statstg.cbSize.LowPart, &cb);

            // close the stream
            pIStream->Release();
            pAttachData = inBuffer;
        }
        else if (FAILED(hr))
        {
            MAPIFreeBuffer(pProp);
            delete pAttachPart;
            pAttachPart = NULL;
            // throw HRException(hr, __FILE__, __LINE__);
            return NULL;
        }
        else
        {
            pAttachData = pProp->Value.bin.lpb;
            attachDataLen = pProp->Value.bin.cb;
        }
        int i;
        for (i = 0; i < NBOMS; i++)
        {
            if (attachDataLen > (size_t)nboms[i])
            {
                if (memcmp(pAttachData, boms[i], nboms[i]) == 0)
                {
                    bom = (BOM)i;
                    break;
                }
            }
        }
        if (bMacEncoded)
            AddBodyToPart(pAttachPart, (LPSTR)pAttachData + 128, attachDataLen - 128, FALSE);
        else if (bom == BOM_NONE)
            AddBodyToPart(pAttachPart, (LPSTR)pAttachData, attachDataLen, FALSE);
        else
            AddBodyToPart(pAttachPart, (LPSTR)(((BYTE *)pAttachData) + nboms[i]),
                    (attachDataLen - nboms[i]), FALSE);
        if (inBuffer != NULL)
            MAPIFreeBuffer(inBuffer);
        if (pProp != NULL)
            MAPIFreeBuffer(pProp);
    }
    break;
    case ATTACH_EMBEDDED_MSG:
    {
        LPMESSAGE pMessage = NULL;
        hr = pAttach->OpenProperty(PR_ATTACH_DATA_OBJ, &IID_IMessage, 0, 0,
                (LPUNKNOWN *)&pMessage);
        if (FAILED(hr))
        {
            // TODO: error handling
        }
        // create a Message object
        mimepp::Message mimeMsg;
        MAPIMessage m;
        m.Initialize(pMessage, session);
        m.ToMimePPMessage(mimeMsg);
        mimepp::String ct("message/rfc822; charset=utf-7;");
        pAttachPart->headers().contentType().setString(ct);
        pAttachPart->headers().contentType().parse();
        pAttachPart->headers().contentTransferEncoding().setString("7bit");
        pAttachPart->headers().contentTransferEncoding().assemble();
        pAttachPart->headers().contentDisposition().setType("attachment");
        if (pProps[ATTACH_DISPLAY_NAME].ulPropTag == PR_DISPLAY_NAME_A)
        {
            LPSTR pFilename = pProps[ATTACH_DISPLAY_NAME].Value.lpszA;
            pAttachPart->headers().contentDisposition().setFilename(pFilename);
        }
        pAttachPart->headers().contentDisposition().assemble();
        pAttachPart->body().setString(mimeMsg.getString());
    }
        MAPIFreeBuffer(pProps);
        return pAttachPart;
    }
    if ((pProps[ATTACH_MIME_TAG].ulPropTag == PR_ATTACH_MIME_TAG_A) &&
        stricmp(pProps[ATTACH_MIME_TAG].Value.lpszA, "multipart/appledouble"))
    {
        // set the content-type
        strContentType += pProps[ATTACH_MIME_TAG].Value.lpszA;
        strContentType += "; charset=";
        if (bom == BOM_NONE)
            strContentType += pCharset;
        else
            strContentType += pBomCharsets[bom];
        strContentType += ";";
        pAttachPart->headers().contentType().setString(strContentType);
        pAttachPart->headers().contentType().parse();
    }
    else if (pAttachFilename != NULL)
    {
        if (pProps[ATTACH_EXTENSION].ulPropTag == PR_ATTACH_EXTENSION_A)
        {
            LPSTR pContentType = NULL;
            GetContentTypeFromExtension(pProps[ATTACH_EXTENSION].Value.lpszA, pContentType);
            if (pContentType == NULL)
            {
                strContentType += "application/octet-stream";
            }
            else
            {
                strContentType += pContentType;
                delete[] pContentType;
            }
        }
        else
        {
            strContentType += "application/octet-stream";
        }
        strContentType += "; charset=";
        if (bom == BOM_NONE)
            strContentType += pCharset;
        else
            strContentType += pBomCharsets[bom];
        strContentType += ";";
        pAttachPart->headers().contentType().setString(strContentType);
        pAttachPart->headers().contentType().parse();
    }
    MAPIFreeBuffer(pProps);
    return pAttachPart;
}

mimepp::BodyPart *Zimbra::MAPI::Util::AttachTooLargeAttachPart(ULONG attachSize,
    LPATTACH pAttach,
    LPSTR)
{
    mimepp::BodyPart *pAttachPart = NULL;

    if (pAttach == NULL)
        return NULL;
    HRESULT hr;
    LPSPropValue pProps = NULL;
    ULONG cProps = 0;
    hr = pAttach->GetProps((LPSPropTagArray) & attachProps, 0, &cProps, &pProps);

    mimepp::String textBody(
        "Attachment excluded during import of message.  The maximum attachment size has been exceeded.  Attachment information follows.\r\n\r\n\r\n");

    LPSTR pTmp = new CHAR[64];
    ultoa(attachSize, pTmp, 10);

    textBody += "Attachment Size: ";
    textBody += pTmp;
    textBody += "\r\n";

    delete[] pTmp;

    mimepp::String strContentType("");
    if (FAILED(hr))
    {
        throw Zimbra::MAPI::Util::MapiUtilsException(
            hr, L"Util::AttachTooLargeAttachPart(): GetProps.",
            __LINE__,
            __FILE__);
    }
    if (pProps[ATTACH_METHOD].ulPropTag != PR_ATTACH_METHOD)
    {
        MAPIFreeBuffer(pProps);
        return NULL;
    }
    pAttachPart = new mimepp::BodyPart;
    if (pProps[ATTACH_CONTENT_ID].ulPropTag == PR_ATTACH_CONTENT_ID_A)
    {
        textBody += "Content-Id: ";
        textBody += pProps[ATTACH_CONTENT_ID].Value.lpszA;
        textBody += "\r\n";
    }
    if (pProps[ATTACH_CONTENT_LOCATION].ulPropTag == PR_ATTACH_CONTENT_LOCATION_A)
    {
        // add a custom header for content location to support rfc2557
        textBody += "Content-Location: ";
        textBody += pProps[ATTACH_CONTENT_LOCATION].Value.lpszA;
        textBody += "\r\n";
    }
    if (pProps[ATTACH_LONG_FILENAME].ulPropTag == PR_ATTACH_LONG_FILENAME_A)
    {
        textBody += "Filename: ";
        textBody += pProps[ATTACH_LONG_FILENAME].Value.lpszA;
        textBody += "\r\n";
    }
    else if (pProps[ATTACH_FILENAME].ulPropTag == PR_ATTACH_FILENAME_A)
    {
        textBody += "Filename: ";
        textBody += pProps[ATTACH_FILENAME].Value.lpszA;
        textBody += "\r\n";
    }
    if (pProps[ATTACH_DISPLAY_NAME].ulPropTag == PR_DISPLAY_NAME_A)
    {
        textBody += "Content-Description: ";
        textBody += pProps[ATTACH_DISPLAY_NAME].Value.lpszA;
        textBody += "\r\n";
    }
    strContentType += "text/plain; charset=us-ascii;";
    pAttachPart->headers().contentType().setString(strContentType);
    pAttachPart->headers().contentType().parse();
    pAttachPart->headers().contentDisposition().setType("attachment");
    pAttachPart->headers().contentDisposition().setFilename("ImportAttachError.txt");
    pAttachPart->headers().contentDisposition().assemble();

    Zimbra::MAPI::Util::AddBodyToPart(pAttachPart, (LPSTR)textBody.c_str(), textBody.length());
    MAPIFreeBuffer(pProps);
    return pAttachPart;
}

void Zimbra::MAPI::Util::CharsetUtil::CharsetStringFromCodePageId(UINT codePageId,
    LPSTR *ppCharset)
{
    LPSTR pCS = NULL;

    switch (codePageId)
    {
    case 708: pCS = "ASMO-708"; break;
    case 720: pCS = "DOS-720"; break;
    case 28596: pCS = "iso-8859-6"; break;
    case 10004: pCS = "x-max-arabic"; break;
    case 775: pCS = "ibm775"; break;
    case 28594: pCS = "iso-8859-4"; break;
    case 852: pCS = "ibm852"; break;
    case 28592: pCS = "iso-8859-2"; break;
    case 10029: pCS = "x-mac-ce"; break;
    case 51936: pCS = "EUC-CN"; break;
    case 936: pCS = "gb2312"; break;
    case 52936: pCS = "hz-gb-2312"; break;
    case 10008: pCS = "x-mac-chinesesimp"; break;
    case 950: pCS = "big5"; break;
    case 20000: pCS = "x-Chinese-CNS"; break;
    case 20002: pCS = "x-Chinese-Eten"; break;
    case 10002: pCS = "x-mac-chinesetrad"; break;
    case 866: pCS = "cp866"; break;
    case 28595: pCS = "iso-8859-5"; break;
    case 20866: pCS = "koi8-r"; break;
    case 21866: pCS = "koi8-u"; break;
    case 10007: pCS = "x-mac-cyrillic"; break;
    case 29001: pCS = "x-Europa"; break;
    case 20106: pCS = "x-IA5-German"; break;
    case 737: pCS = "ibm737"; break;
    case 28597: pCS = "iso-8859-7"; break;
    case 10006: pCS = "x-mac-greek"; break;
    case 869: pCS = "ibm869"; break;
    case 862: pCS = "DOS-862"; break;
    case 38598: pCS = "iso-8859-8-i"; break;
    case 28598: pCS = "iso-8859-8"; break;
    case 10005: pCS = "x-mac-hebrew"; break;
    case 20420: pCS = "x-EBCDIC-Arabic"; break;
    case 20880: pCS = "x-EBCDIC-CyrillicRussian"; break;
    case 21025: pCS = "x-EBCDIC-CyrillicSerbianBulgarian"; break;
    case 20277: pCS = "x-EBCDIC-DenmarkNorway"; break;
    case 1142: pCS = "x-ebcdic-denmarknorway-euro"; break;
    case 20278: pCS = "x-EBCDIC-FinlandSweden"; break;
    case 1143: pCS = "x-ebcdic-finlandsweden-euro"; break;
    case 1147: pCS = "x-ebcdic-france-euro"; break;
    case 20273: pCS = "x-EBCDIC-Germany"; break;
    case 1141: pCS = "x-ebcdic-germany-euro"; break;
    case 875: pCS = "x-EBCDIC-GreekModern"; break;
    case 20423: pCS = "x-EBCDIC-Greek"; break;
    case 20424: pCS = "x-EBCDIC-Hebrew"; break;
    case 20871: pCS = "x-EBCDIC-Icelandic"; break;
    case 1149: pCS = "x-ebcdic-icelandic-euro"; break;
    case 1148: pCS = "x-ebcdic-international-euro"; break;
    case 20280: pCS = "x-EBCDIC-Italy"; break;
    case 1144: pCS = "x-ebcdic-italy-euro"; break;
    case 50930: pCS = "x-EBCDIC-JapaneseAndKana"; break;
    case 50939: pCS = "x-EBCDIC-JapaneseAndJapaneseLatin"; break;
    case 50931: pCS = "x-EBCDIC-JapaneseAndUSCanada"; break;
    case 20290: pCS = "x-EBCDIC-JapaneseKatakana"; break;
    case 50933: pCS = "x-EBCDIC-KoreanAndKoreanExtended"; break;
    case 20833: pCS = "x-EBCDIC-KoreanExtended"; break;
    case 870: pCS = "CP870"; break;
    case 50935: pCS = "x-EBCDIC-SimplifiedChinese"; break;
    case 20284: pCS = "X-EBCDIC-Spain"; break;
    case 1145: pCS = "x-ebcdic-spain-euro"; break;
    case 20838: pCS = "x-EBCDIC-Thai"; break;
    case 50937: pCS = "x-EBCDIC-TraditionalChinese"; break;
    case 1026: pCS = "CP1026"; break;
    case 20905: pCS = "x-EBCDIC-Turkish"; break;
    case 20285: pCS = "x-EBCDIC-UK"; break;
    case 1146: pCS = "x-ebcdic-uk-euro"; break;
    case 37: pCS = "ebcdic-cp-us"; break;
    case 1140: pCS = "x-ebcdic-cp-us-euro"; break;
    case 861: pCS = "ibm861"; break;
    case 10079: pCS = "x-mac-icelandic"; break;
    case 57006: pCS = "x-iscii-as"; break;
    case 57003: pCS = "x-iscii-be"; break;
    case 57002: pCS = "x-iscii-de"; break;
    case 57010: pCS = "x-iscii-gu"; break;
    case 57008: pCS = "x-iscii-ka"; break;
    case 57009: pCS = "x-iscii-ma"; break;
    case 57007: pCS = "x-iscii-or"; break;
    case 57011: pCS = "x-iscii-pa"; break;
    case 57004: pCS = "x-iscii-ta"; break;
    case 57005: pCS = "x-iscii-te"; break;
    case 51932: pCS = "euc-jp"; break;
    case 50220: pCS = "iso-2022-jp"; break;
    case 50222: pCS = "iso-2022-jp"; break;
    case 50221: pCS = "csISO2022JP"; break;
    case 10001: pCS = "x-mac-japanese"; break;
    case 932: pCS = "shift_jis"; break;
    case 949: pCS = "ks_c_5601-1987"; break;
    case 51949: pCS = "euc-kr"; break;
    case 50225: pCS = "iso-2022-kr"; break;
    case 1361: pCS = "Johab"; break;
    case 10003: pCS = "x-mac-korean"; break;
    case 28593: pCS = "iso-8859-3"; break;
    case 28605: pCS = "iso-8859-15"; break;
    case 20108: pCS = "x-IA5-Norwegian"; break;
    case 437: pCS = "IBM437"; break;
    case 20107: pCS = "x-IA5-Swedish"; break;
    case 857: pCS = "ibm857"; break;
    case 28599: pCS = "iso-8859-9"; break;
    case 10081: pCS = "x-mac-turkish"; break;
    case 1200: pCS = "unicode"; break;
    case 1201: pCS = "unicodeFFFE"; break;
    case 65000: pCS = "utf-7"; break;
    case 65001: pCS = "utf-8"; break;
    case 20127: pCS = "us-ascii"; break;
    case 850: pCS = "ibm850"; break;
    case 20105: pCS = "x-IA5"; break;
    case 28591: pCS = "iso-8859-1"; break;
    case 10000: pCS = "macintosh"; break;
    default:
        break;
    }                                           // switch( codePageId )
    if (pCS != NULL)
    {
        *ppCharset = new char[(int)strlen(pCS) + 1];
        strcpy_s(*ppCharset, (int)strlen(pCS) + 1, pCS);
    }
    else
    {
        *ppCharset = new char[25];
        sprintf_s(*ppCharset, 25, "windows-%d", codePageId);
    }
}

Zimbra::MAPI::Util::MIME_ENCODING Zimbra::MAPI::Util::CharsetUtil::FindBestEncoding(
    LPSTR pBuffer, int nBuffer)
{
    MIME_ENCODING result = ME_7BIT;
    int lineLength = 0;
    int nNonAscii = 0;
    int nNonAsciiThreshold = MulDiv(nBuffer, 17, 100);

    for (int i = 0; i < nBuffer; i++)
    {
        unsigned char cVal = (unsigned char)pBuffer[i];
        if ((cVal == 13) || (cVal == 10))
        {
            lineLength = 0;
        }
        else if (((cVal >= 0) && (cVal < 32)) || (cVal >= 127))
        {
            // Set the encoding to be base64 for a PDF file (Bug 9832)
            // First four characters of a PDF file are %PDF
            if (!strncmp(pBuffer, "%PDF", 4))
            {
                result = ME_BASE64;
                break;
            }
            nNonAscii++;
            result = ME_QUOTED_PRINTABLE;
        }
        lineLength++;
        if (lineLength > 76)
            result = ME_QUOTED_PRINTABLE;
        if (nNonAscii > nNonAsciiThreshold)
        {
            result = ME_BASE64;
            break;
        }
    }
    return result;
}

Zimbra::MAPI::Util::StoreUtils *Zimbra::MAPI::Util::StoreUtils::stUtilsInst = NULL;
// HrGetRegMultiSZValueA
// Get a REG_MULTI_SZ registry value - allocating memory using new to hold it.
void Zimbra::MAPI::Util::StoreUtils::HrGetRegMultiSZValueA(IN HKEY hKey,        // the key.
    IN LPCSTR lpszValue,                        // value name in key.
    OUT LPVOID *lppData)                        // where to put the data.
{
    *lppData = NULL;
    DWORD dwKeyType = NULL;
    DWORD cb = NULL;
    LONG lRet = 0;

    // Get its size
    lRet = RegQueryValueExA(
        hKey,
        lpszValue,
        NULL,
        &dwKeyType,
        NULL,
        &cb);
    if ((ERROR_SUCCESS == lRet) && cb && (REG_MULTI_SZ == dwKeyType))
    {
        *lppData = new BYTE[cb];
        if (*lppData)
        {
            // Get the current value
            lRet = RegQueryValueExA(
                hKey,
                lpszValue,
                NULL,
                &dwKeyType,
                (unsigned char *)*lppData,
                &cb);
            if (ERROR_SUCCESS != lRet)
            {
                delete[] *lppData;
                *lppData = NULL;
            }
        }
    }
}

// /////////////////////////////////////////////////////////////////////////////
// Function name   : GetMAPIDLLPath
// Description       : This will get the correct path to the MAPIDIR with MAPI32.DLL file.
// Return type      : void
// Argument         : LPSTR szMAPIDir - Buffer to hold the path to the MAPI32DLL file.
// ULONG cchMAPIDir - size of the buffer
void Zimbra::MAPI::Util::StoreUtils::GetMAPIDLLPath(LPSTR szMAPIDir, ULONG cchMAPIDir)
{
    HRESULT hRes = S_OK;
    UINT uiRet = 0;
    LONG lRet = 0;
    BOOL bRet = true;

    szMAPIDir[0] = '\0';                        // Terminate String at pos 0 (safer if we fail below)

    CHAR szSystemDir[MAX_PATH + 1] = { 0 };

    // Get the system directory path
    // (mapistub.dll and mapi32.dll reside here)
    uiRet = GetSystemDirectoryA(szSystemDir, MAX_PATH);
    if (uiRet > 0)
    {
        CHAR szDLLPath[MAX_PATH + 1] = { 0 };

        hRes = StringCchPrintfA(szDLLPath, MAX_PATH + 1, "%s\\%s",
            szSystemDir, "mapistub.dll");
        if (SUCCEEDED(hRes))
        {
            LPFGETCOMPONENTPATH pfnFGetComponentPath = NULL;

            HINSTANCE hmodStub = 0;
            HINSTANCE hmodMapi32 = 0;

            // Load mapistub.dll
            hmodStub = LoadLibraryA(szDLLPath);
            if (hmodStub)
            {
                // Get the address of FGetComponentPath from the mapistub
                pfnFGetComponentPath = (LPFGETCOMPONENTPATH)GetProcAddress(
                    hmodStub, "FGetComponentPath");
            }
            // If we didn't get the address of FGetComponentPath
            // try mapi32.dll
            if (!pfnFGetComponentPath)
            {
                hRes = StringCchPrintfA(szDLLPath, MAX_PATH + 1, "%s\\%s",
                    szSystemDir, "mapi32.dll");
                if (SUCCEEDED(hRes))
                {
                    // Load mapi32.dll
                    hmodMapi32 = LoadLibraryA(szDLLPath);
                    if (hmodMapi32)
                    {
                        // Get the address of FGetComponentPath from mapi32
                        pfnFGetComponentPath = (LPFGETCOMPONENTPATH)GetProcAddress(
                            hmodMapi32, "FGetComponentPath");
                    }
                }
            }
            if (pfnFGetComponentPath)
            {
                LPSTR szAppLCID = NULL;
                LPSTR szOfficeLCID = NULL;
                HKEY hMicrosoftOutlook = NULL;

                lRet = RegOpenKeyEx(
                    HKEY_LOCAL_MACHINE,
                    _T("Software\\Clients\\Mail\\Microsoft Outlook"),
                    NULL,
                    KEY_READ,
                    &hMicrosoftOutlook);
                if ((ERROR_SUCCESS == lRet) && hMicrosoftOutlook)
                {
                    HrGetRegMultiSZValueA(hMicrosoftOutlook, "MSIApplicationLCID",
                        (LPVOID *)&szAppLCID);
                    HrGetRegMultiSZValueA(hMicrosoftOutlook, "MSIOfficeLCID",
                        (LPVOID *)&szOfficeLCID);
                }
                if (szAppLCID)
                {
                    bRet = pfnFGetComponentPath(
                        "{FF1D0740-D227-11D1-A4B0-006008AF820E}", szAppLCID, szMAPIDir,
                        cchMAPIDir, true);
                }
                if ((!bRet || (szMAPIDir[0] == _T('\0'))) && szOfficeLCID)
                {
                    bRet = pfnFGetComponentPath(
                        "{FF1D0740-D227-11D1-A4B0-006008AF820E}", szOfficeLCID, szMAPIDir,
                        cchMAPIDir, true);
                }
                if (!bRet || (szMAPIDir[0] == _T('\0')))
                {
                    bRet = pfnFGetComponentPath(
                        "{FF1D0740-D227-11D1-A4B0-006008AF820E}", NULL, szMAPIDir, cchMAPIDir,
                        true);
                }
                // We got the path to msmapi32.dll - need to strip it
                if (bRet && (szMAPIDir[0] != _T('\0')))
                {
                    LPSTR lpszSlash = NULL;
                    LPSTR lpszCur = szMAPIDir;
                    for (lpszSlash = lpszCur; *lpszCur; lpszCur = lpszCur++)
                        if (*lpszCur == _T('\\'))
                            lpszSlash = lpszCur;
                    *lpszSlash = _T('\0');
                }
                delete[] szOfficeLCID;
                delete[] szAppLCID;
                if (hMicrosoftOutlook)
                    RegCloseKey(hMicrosoftOutlook);
            }
            // If FGetComponentPath returns FALSE or if
            // it returned nothing, or if we never found an
            // address of FGetComponentPath, then
            // just default to the system directory
            if (!bRet || (szMAPIDir[0] == '\0'))
            {
                hRes = StringCchPrintfA(
                    szMAPIDir, cchMAPIDir, "%s", szSystemDir);
            }
            if (szMAPIDir[0] != _T('\0'))
            {
                hRes = StringCchPrintfA(
                    szMAPIDir, cchMAPIDir, "%s\\%s", szMAPIDir, "msmapi32.dll");
            }
            if (hmodMapi32)
                FreeLibrary(hmodMapi32);
            if (hmodStub)
                FreeLibrary(hmodStub);
        }
    }
}

bool Zimbra::MAPI::Util::StoreUtils::isUnicodeStore(LPMESSAGE pMsg)
{
        #define STORE_UNICODE_OK ((ULONG)0x00040000)
    bool retval = false;
    LPSPropValue pPropSuppMask = NULL;
    HRESULT hr = HrGetOneProp(pMsg, PR_STORE_SUPPORT_MASK, &pPropSuppMask);
    if (hr == S_OK)
        retval = (pPropSuppMask->Value.l & STORE_UNICODE_OK) != 0;
    if (pPropSuppMask)
        MAPIFreeBuffer(pPropSuppMask);
    return retval;
}

bool Zimbra::MAPI::Util::StoreUtils::Init()
{
    if (_hinstLib == NULL)
    {
        char szMAPIDir[MAX_PATH];
        ULONG cchMAPIDir = MAX_PATH;
        GetMAPIDLLPath(szMAPIDir, cchMAPIDir);
        if (szMAPIDir[0] == _T('\0'))
        {
            // TRACE(_T("GetMAPIDLLPath cannot be found."));
            return false;
        }
        LPWSTR pszMAPIDir = NULL;
        int cbuf = MultiByteToWideChar(NULL, 0, szMAPIDir, cchMAPIDir, NULL, 0);
        HRESULT hr = MAPIAllocateBuffer((sizeof (WCHAR) * cbuf) + 10, (LPVOID FAR *)&pszMAPIDir);
        ZeroMemory(pszMAPIDir, (sizeof (WCHAR) * cbuf) + 10);
        int rbuf = MultiByteToWideChar(NULL, 0, szMAPIDir, cchMAPIDir, pszMAPIDir, cbuf);
        UNREFERENCED_PARAMETER(rbuf);
        UNREFERENCED_PARAMETER(hr);
        _hinstLib = LoadLibrary(pszMAPIDir);
        MAPIFreeBuffer(pszMAPIDir);
        if (_hinstLib != NULL)
        {
            pWrapCompressedRTFEx = (WRAPCOMPRESSEDRTFSTREAMEX)GetProcAddress(
                _hinstLib, "WrapCompressedRTFStreamEx");
            if (pWrapCompressedRTFEx == NULL)
                return false;
        }
        else
        {
            return false;
        }
    }
    return true;
}

bool Zimbra::MAPI::Util::StoreUtils::GetAnsiStoreMsgNativeType(LPMESSAGE pMsg, ULONG *nBody)
{
    bool retval = false;

    *nBody = 0;
    if (isUnicodeStore(pMsg))                   // if unicode store, return from here
        return false;
    if (NULL != pWrapCompressedRTFEx)
    {
        HRESULT hRes = S_OK;
        LPSTREAM lpCompressed = NULL;
        LPSTREAM lpUncompressed = NULL;
        RTF_WCSINFO wcsinfo = { 0 };
        RTF_WCSRETINFO retinfo = { 0 };
        LPSPropValue lpPropCPID = NULL;

        retinfo.size = sizeof (RTF_WCSRETINFO);

        wcsinfo.size = sizeof (RTF_WCSINFO);
        wcsinfo.ulFlags = MAPI_NATIVE_BODY;
        wcsinfo.ulOutCodePage = 0;
        // Retrieve the value of the Internet code page.
        // Pass this value to the WrapCompressedRTFStreamEx function.
        // If the property is not found, the default is 0.
        if (SUCCEEDED(hRes = HrGetOneProp(pMsg, PR_INTERNET_CPID, &lpPropCPID)))
            wcsinfo.ulInCodePage = lpPropCPID->Value.l;
        // Open the compressed RTF stream.
        if (SUCCEEDED(hRes = pMsg->OpenProperty(PR_RTF_COMPRESSED,
                    &IID_IStream,
                    STGM_READ | STGM_DIRECT,
                    0,
                    (LPUNKNOWN *)&lpCompressed)))
        {
            // Notice that the WrapCompressedRTFStreamEx function has been loaded
            // by using the GetProcAddress function into pfnWrapEx.
            // Call the WrapCompressedRTFStreamEx function.
            if (SUCCEEDED(hRes = pWrapCompressedRTFEx(lpCompressed,
                        &wcsinfo,
                        &lpUncompressed,
                        &retinfo)))
            {
                *nBody = retinfo.ulStreamFlags;
                // Check what the native body type is.
                switch (retinfo.ulStreamFlags)
                {
                case MAPI_NATIVE_BODY_TYPE_RTF:
                    break;
                case MAPI_NATIVE_BODY_TYPE_HTML:
                    break;
                case MAPI_NATIVE_BODY_TYPE_PLAINTEXT:
                    break;
                }
                retval = true;
            }
        }
        MAPIFreeBuffer(lpPropCPID);
        if (lpUncompressed)
            lpUncompressed->Release();
        if (lpCompressed)
            lpCompressed->Release();
    }
    return retval;
}

void Zimbra::MAPI::Util::StoreUtils::UnInit()
{
    if (_hinstLib != NULL)
    {
        BOOL fFreeResult = FreeLibrary(_hinstLib);
        UNREFERENCED_PARAMETER(fFreeResult);
    }
    _hinstLib = NULL;
}


BOOL Zimbra::MAPI::Util::CreateAppTemporaryDirectory()
{
	BOOL bRet = FALSE;
	wstring wstrTempDirPath;
	if(!GetAppTemporaryDirectory(wstrTempDirPath))
		return bRet;

    SECURITY_ATTRIBUTES secAttr;
    secAttr.bInheritHandle = FALSE;
    secAttr.lpSecurityDescriptor = NULL;
    secAttr.nLength = sizeof(SECURITY_ATTRIBUTES);

	bRet=CreateDirectory(wstrTempDirPath.c_str(), &secAttr);

    return bRet;
}

BOOL Zimbra::MAPI::Util::GetAppTemporaryDirectory(wstring &wstrTempAppDirPath)
{
	WCHAR pwszTempDir[MAX_PATH];
    if(!GetTempPath(MAX_PATH, pwszTempDir))
		return FALSE;

    wstrTempAppDirPath = pwszTempDir;
	wstring wstrAppName;
	if(!GetAppName(wstrAppName))
		return FALSE;
    wstrTempAppDirPath += wstrAppName;
	return TRUE;	
}

BOOL Zimbra::MAPI::Util::GetAppName(wstring &wstrAppName)
{
	WCHAR szAppPath[MAX_PATH] = L"";
	if(!GetModuleFileName(0, szAppPath, MAX_PATH))
		return FALSE;
	// Extract name
	wstrAppName = szAppPath;
	wstrAppName = wstrAppName.substr(wstrAppName.rfind(L"\\") + 1);
	wstrAppName = wstrAppName.substr(0,wstrAppName.find(L"."));
	return TRUE;
}


wstring Zimbra::MAPI::Util::GetUniqueName()
{
	GUID guid;
	HRESULT hr=CoCreateGuid(&guid);
	if(hr!=S_OK)
	{
		return L"";
	}
	BYTE * str;
	hr=UuidToString((UUID*)&guid, (RPC_WSTR*)&str);
	if(hr!=RPC_S_OK)
	{
		return L"";
	}
	wstring unique= (LPTSTR)str;
	RpcStringFree((RPC_WSTR*)&str);	
	replace( unique.begin(), unique.end(), '-', '_'); 
	unique += L"_migwiz";
	return unique;
}

wstring Zimbra::MAPI::Util::GetDomainName()
{
	wstring wDomain=L"";
	DWORD dwLevel = 102;
	LPWKSTA_INFO_102 pBuf = NULL;
	NET_API_STATUS nStatus;
	LPWSTR pszServerName = NULL;

	nStatus = NetWkstaGetInfo(pszServerName,
								dwLevel,
								(LPBYTE *)&pBuf);
	if (nStatus == NERR_Success)
	{
		wDomain= pBuf->wki102_langroup;
		//printf("\n\tPlatform: %d\n", pBuf->wki102_platform_id);
		//wprintf(L"\tName:     %s\n", pBuf->wki102_computername);
		//printf("\tVersion:  %d.%d\n", pBuf->wki102_ver_major,
		//							pBuf->wki102_ver_minor);
		//wprintf(L"\tLan Root: %s\n", pBuf->wki102_lanroot);
		//wprintf(L"\t# Logged On Users: %d\n", pBuf->wki102_logged_on_users);
	}
	// Free the allocated memory.
	if (pBuf != NULL)
		NetApiBufferFree(pBuf);

	return wDomain;
}
