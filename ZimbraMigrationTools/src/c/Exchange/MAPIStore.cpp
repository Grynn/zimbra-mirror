/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
#include "common.h"
#include "Exchange.h"
#include "MAPIStore.h"
#include "edk/edkmapi.h"

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// Exception class
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIStoreException::MAPIStoreException(HRESULT hrErrCode, LPCWSTR
    lpszDescription): GenericException(hrErrCode, lpszDescription)
{
    //
}

MAPIStoreException::MAPIStoreException(HRESULT hrErrCode, LPCWSTR lpszDescription, LPCWSTR lpszShortDescription, 
	int nLine, LPCSTR strFile): GenericException(hrErrCode, lpszDescription, lpszShortDescription, nLine, strFile)
{
    //
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MAPIStore
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIStore::MAPIStore(): m_Store(NULL), m_mapiSession(NULL)
{
	m_specialFolderIds.cValues = 0;
	m_specialFolderIds.lpbin = NULL;
}

MAPIStore::~MAPIStore()
{
    Zimbra::Util::AutoCriticalSection autocriticalsection(cs_store);
    ULONG flags = LOGOFF_ORDERLY;
	if ((m_specialFolderIds.cValues != 0) && (m_specialFolderIds.lpbin != NULL))
	{
		Zimbra::MAPI::Util::FreeAllSpecialFolders(&m_specialFolderIds);
		m_specialFolderIds.cValues = 0;
		m_specialFolderIds.lpbin = NULL;
	}

    if (m_Store)
    {
        m_Store->StoreLogoff(&flags);
        m_Store->Release();
    }
    m_Store = NULL;
}

void MAPIStore::Initialize(LPMAPISESSION mapisession, LPMDB pMdb)
{
    Zimbra::Util::AutoCriticalSection autocriticalsection(cs_store);

    m_Store = pMdb;
    m_mapiSession = mapisession;
    g_ulIMAPHeaderInfoPropTag = Zimbra::MAPI::Util::IMAPHeaderInfoPropTag(m_Store);

    Zimbra::MAPI::Util::GetAllSpecialFolders(m_Store, &m_specialFolderIds);
}

HRESULT MAPIStore::CompareEntryIDs(SBinary *pBin1, SBinary *pBin2, ULONG &lpulResult)
{
    Zimbra::Util::AutoCriticalSection autocriticalsection(cs_store);
    HRESULT hr = S_OK;

    hr = m_Store->CompareEntryIDs(pBin1->cb, (LPENTRYID)(pBin1->lpb), pBin2->cb,
        (LPENTRYID)(pBin2->lpb), 0, &lpulResult);
    return hr;
}

HRESULT MAPIStore::GetRootFolder(MAPIFolder &rootFolder)
{
    Zimbra::Util::AutoCriticalSection autocriticalsection(cs_store);
    HRESULT hr = S_OK;
    SBinary bin;
    ULONG objtype = 0;
	bin.cb = 0;
	bin.lpb = NULL;
    LPMAPIFOLDER pFolder = NULL;
	
	if (FAILED(hr = Zimbra::MAPI::Util::HrMAPIFindIPMSubtree(m_Store, bin)))
		throw MAPIStoreException(hr, L"GetRootFolder(): HrMAPIFindIPMSubtree Failed.", 
		ERR_ROOT_FOLDER, __LINE__, __FILE__);
	if (FAILED(hr = m_Store->OpenEntry(bin.cb, (LPENTRYID)bin.lpb, NULL, MAPI_BEST_ACCESS,
			&objtype, (LPUNKNOWN *)&pFolder)))
		throw MAPIStoreException(hr, L"GetRootFolder(): OpenEntry Failed.", 
		ERR_ROOT_FOLDER, __LINE__, __FILE__);
	
    // Init root folder object
    rootFolder.Initialize(pFolder, _TEXT("/"), &bin);
    return hr;
}

HRESULT MAPIStore::OpenEntry(ULONG cbEntryID, LPENTRYID lpEntryID, LPCIID lpInterface, ULONG
    ulFlags, ULONG FAR *lpulObjType, LPUNKNOWN FAR *lppUnk)
{
    Zimbra::Util::AutoCriticalSection autocriticalsection(cs_store);

    return m_Store->OpenEntry(cbEntryID, lpEntryID, lpInterface, ulFlags, lpulObjType, lppUnk);
}

// Use these guids:
// pbExchangeProviderPrimaryUserGuid
// pbExchangeProviderDelegateGuid
// pbExchangeProviderPublicGuid
// pbExchangeProviderXportGuid

HRESULT MAPIStore::OpenMessageStoreGUID(_In_ LPMAPISESSION lpMAPISession,
							 _In_z_ LPCSTR lpGUID,
							 _Deref_out_opt_ LPMDB* lppMDB)
{
	LPMAPITABLE	pStoresTbl = NULL;
	LPSRowSet	pRow		= NULL;
	ULONG		ulRowNum;
	HRESULT		hRes = S_OK;

	enum
	{
		EID,
		STORETYPE,
		NUM_COLS
	};
	static const SizedSPropTagArray(NUM_COLS,sptCols) =
	{
		NUM_COLS,
		PR_ENTRYID,
		PR_MDB_PROVIDER
	};

	*lppMDB = NULL;
	if (!lpMAPISession) return MAPI_E_INVALID_PARAMETER;

	hRes=lpMAPISession->GetMsgStoresTable(0, &pStoresTbl);

	if (pStoresTbl)
	{
		hRes=HrQueryAllRows(
			pStoresTbl,					// table to query
			(LPSPropTagArray) &sptCols,	// columns to get
			NULL,						// restriction to use
			NULL,						// sort order
			0,							// max number of rows
			&pRow);
		if (pRow)
		{
			if (!FAILED(hRes)) for (ulRowNum=0; ulRowNum<pRow->cRows; ulRowNum++)
			{
				hRes = S_OK;
				// check to see if we have a folder with a matching GUID
				if (pRow->aRow[ulRowNum].lpProps[STORETYPE].ulPropTag == PR_MDB_PROVIDER &&
					pRow->aRow[ulRowNum].lpProps[EID].ulPropTag == PR_ENTRYID &&
					IsEqualMAPIUID(pRow->aRow[ulRowNum].lpProps[STORETYPE].Value.bin.lpb,lpGUID))
				{
					CallOpenMsgStore(
						lpMAPISession,
						NULL,
						&pRow->aRow[ulRowNum].lpProps[EID].Value.bin,
						MDB_WRITE,
						lppMDB);
					break;
				}
			}
		}
	}
	if (!*lppMDB) hRes = MAPI_E_NOT_FOUND;

	if (pRow) FreeProws(pRow);
	if (pStoresTbl) pStoresTbl->Release();
	return hRes;
} // OpenMessageStoreGUID


HRESULT MAPIStore::CallOpenMsgStore(
						 _In_ LPMAPISESSION	lpSession,
						 _In_ ULONG_PTR		ulUIParam,
						 _In_ LPSBinary		lpEID,
						 ULONG			ulFlags,
						 _Deref_out_ LPMDB*			lpMDB)
{
	if (!lpSession || !lpMDB || !lpEID) return MAPI_E_INVALID_PARAMETER;

	HRESULT hRes = S_OK;
	ulFlags |= MDB_ONLINE;
	hRes=lpSession->OpenMsgStore(
		ulUIParam,
		lpEID->cb,
		(LPENTRYID) lpEID->lpb,
		NULL,
		ulFlags,
		(LPMDB*) lpMDB);
	if (MAPI_E_UNKNOWN_FLAGS == hRes && (ulFlags & MDB_ONLINE))
	{
		hRes = S_OK;
		// perhaps this store doesn't know the MDB_ONLINE flag - remove and retry
		ulFlags = ulFlags & ~MDB_ONLINE;
		hRes=lpSession->OpenMsgStore(
			ulUIParam,
			lpEID->cb,
			(LPENTRYID) lpEID->lpb,
			NULL,
			ulFlags,
			(LPMDB*) lpMDB);
	}
	return hRes;
} // CallOpenMsgStore


bool MAPIStore::StoreSupportsManageStore(_In_ LPMDB lpMDB)
{
	HRESULT					hRes = S_OK;
	LPEXCHANGEMANAGESTORE	lpIManageStore = NULL;

	if (!lpMDB) return false;

	hRes=lpMDB->QueryInterface(
		IID_IExchangeManageStore,
		(void **) &lpIManageStore);

	if ((hRes==S_OK)&&(lpIManageStore))
	{
		lpIManageStore->Release();
		return true;
	}
	return false;
} // StoreSupportsManageStore


HRESULT MAPIStore::OpenPublicMessageStore(
	_In_ LPMAPISESSION lpMAPISession,
	ULONG ulFlags, // Flags for CreateStoreEntryID
	_Deref_out_opt_ LPMDB* lppPublicMDB)
{
	HRESULT			hRes = S_OK;

	LPMDB			lpPublicMDBNonAdmin	= NULL;
	LPSPropValue	lpServerName	= NULL;

	if (!lpMAPISession || !lppPublicMDB) return MAPI_E_INVALID_PARAMETER;

	hRes=OpenMessageStoreGUID(
		lpMAPISession,
		pbExchangeProviderPublicGuid,
		&lpPublicMDBNonAdmin);

	if(hRes!=S_OK)
		return hRes;
	// If we don't have flags we're done
	if (!ulFlags)
	{
		*lppPublicMDB = lpPublicMDBNonAdmin;
		return hRes;
	}

	if (lpPublicMDBNonAdmin && StoreSupportsManageStore(lpPublicMDBNonAdmin))
	{
		hRes=HrGetOneProp(
			lpPublicMDBNonAdmin,
			PR_HIERARCHY_SERVER,
			&lpServerName);

		if (Zimbra::MAPI::Util::CheckStringProp(lpServerName,PT_TSTRING))
		{
			LPTSTR	szServerDN = NULL;

			hRes=BuildServerDN(
				lpServerName->Value.LPSZ,
				_T("/cn=Microsoft Public MDB"), // STRING_OK
				&szServerDN);

			if (szServerDN)
			{
				hRes=HrMailboxLogon(
					lpMAPISession,
					lpPublicMDBNonAdmin,
					szServerDN,
					NULL,
					ulFlags,
					lppPublicMDB);
				MAPIFreeBuffer(szServerDN);
			}
		}
	}

	MAPIFreeBuffer(lpServerName);
	if (lpPublicMDBNonAdmin) lpPublicMDBNonAdmin->Release();
	return hRes;
} // OpenPublicMessageStore


HRESULT MAPIStore::HrMailboxLogon(
									  _In_ LPMAPISESSION		lpMAPISession,	// MAPI session handle
									  _In_ LPMDB				lpMDB,			// open message store
									  _In_z_ LPCTSTR			lpszMsgStoreDN,	// desired message store DN
									  _In_opt_z_ LPCTSTR		lpszMailboxDN,	// desired mailbox DN or NULL
									  ULONG						ulFlags,		// desired flags for CreateStoreEntryID
									  _Deref_out_opt_ LPMDB*	lppMailboxMDB)	// ptr to mailbox message store ptr
{
	HRESULT					hRes			= S_OK;
	LPEXCHANGEMANAGESTORE	lpXManageStore  = NULL;
	LPMDB					lpMailboxMDB	= NULL;
	SBinary					sbEID			= {0};

	*lppMailboxMDB = NULL;

	if (!lpMAPISession || !lpMDB || !lpszMsgStoreDN || !lppMailboxMDB || !StoreSupportsManageStore(lpMDB))
	{
		return MAPI_E_INVALID_PARAMETER;
	}

	// Use a NULL MailboxDN to open the public store
	if (lpszMailboxDN == NULL || !*lpszMailboxDN)
	{
		ulFlags |= OPENSTORE_PUBLIC;
	}

	hRes=lpMDB->QueryInterface(
		IID_IExchangeManageStore,
		(LPVOID*) &lpXManageStore);

	if ((SUCCEEDED(hRes))&&(lpXManageStore))
	{
		//DebugPrint(DBGGeneric,_T("HrMailboxLogon: Creating EntryID. StoreDN = \"%s\", MailboxDN = \"%s\"\n"),lpszMsgStoreDN,lpszMailboxDN);

#ifdef UNICODE
		{
			char *szAnsiMsgStoreDN = NULL;
			char *szAnsiMailboxDN = NULL;
			WtoA((LPWSTR)lpszMsgStoreDN,szAnsiMsgStoreDN);

			if (lpszMailboxDN) (WtoA((LPWSTR)lpszMailboxDN,szAnsiMailboxDN));

			hRes=lpXManageStore->CreateStoreEntryID(
				szAnsiMsgStoreDN,
				szAnsiMailboxDN,
				ulFlags,
				&sbEID.cb,
				(LPENTRYID*) &sbEID.lpb);
			delete[] szAnsiMsgStoreDN;
			delete[] szAnsiMailboxDN;
			if(hRes!=S_OK)
				return hRes;
		}
#else
		hRes=(lpXManageStore->CreateStoreEntryID(
			(LPSTR) lpszMsgStoreDN,
			(LPSTR) lpszMailboxDN,
			ulFlags,
			&sbEID.cb,
			(LPENTRYID*) &sbEID.lpb));
#endif
		
		hRes=CallOpenMsgStore(
			lpMAPISession,
			NULL,
			&sbEID,
			MDB_NO_DIALOG |
			MDB_NO_MAIL |     // spooler not notified of our presence
			MDB_TEMPORARY |   // message store not added to MAPI profile
			MAPI_BEST_ACCESS, // normally WRITE, but allow access to RO store
			&lpMailboxMDB);

		*lppMailboxMDB = lpMailboxMDB;
	}

	MAPIFreeBuffer(sbEID.lpb);
	if (lpXManageStore) lpXManageStore->Release();
	return hRes;
} // HrMailboxLogon

HRESULT MAPIStore::BuildServerDN(
					  _In_z_ LPCTSTR szServerName,
					  _In_z_ LPCTSTR szPost,
					  _Deref_out_z_ LPTSTR* lpszServerDN)
{
	HRESULT hRes = S_OK;
	if (!lpszServerDN) return MAPI_E_INVALID_PARAMETER;

	static LPCTSTR szPre = _T("/cn=Configuration/cn=Servers/cn="); // STRING_OK
	size_t cbPreLen = 0;
	size_t cbServerLen = 0;
	size_t cbPostLen = 0;
	size_t cbServerDN = 0;

	StringCbLength(szPre,STRSAFE_MAX_CCH * sizeof(TCHAR),&cbPreLen);
	StringCbLength(szServerName,STRSAFE_MAX_CCH * sizeof(TCHAR),&cbServerLen);
	StringCbLength(szPost,STRSAFE_MAX_CCH * sizeof(TCHAR),&cbPostLen);

	cbServerDN = cbPreLen + cbServerLen + cbPostLen + sizeof(TCHAR);

	hRes=(MAPIAllocateBuffer((ULONG) cbServerDN,
		(LPVOID*)lpszServerDN));
		
	if(hRes!=S_OK)
		return hRes;

	StringCbPrintf(
		*lpszServerDN,
		cbServerDN,
		_T("%s%s%s"), // STRING_OK
		szPre,
		szServerName,
		szPost);
	return hRes;
} // BuildServerDN

HRESULT MAPIStore::GetPublicFolderTable1(
							  _In_ LPMDB lpMDB,
							  _In_z_ LPCTSTR szServerDN,
							  ULONG ulFlags,
							  _Deref_out_opt_ LPMAPITABLE* lpPFTable)
{
	if (!lpMDB || !lpPFTable || !szServerDN) return MAPI_E_INVALID_PARAMETER;
	*lpPFTable = NULL;

	HRESULT	hRes = S_OK;
	LPEXCHANGEMANAGESTORE lpManageStore1 = NULL;

	hRes=lpMDB->QueryInterface(
		IID_IExchangeManageStore,
		(void **) &lpManageStore1);
	
	if (!SUCCEEDED(hRes))
		return hRes;

	if (lpManageStore1)
	{
		hRes=lpManageStore1->GetPublicFolderTable(
			(LPSTR) szServerDN,
			lpPFTable,
			ulFlags);

		lpManageStore1->Release();
	}

	return hRes;
} // GetPublicFolderTable1

HRESULT MAPIStore::GetPublicFolderTable4(
							  _In_ LPMDB lpMDB,
							  _In_z_ LPCTSTR szServerDN,
							  ULONG ulOffset,
							  ULONG ulFlags,
							  _Deref_out_opt_ LPMAPITABLE* lpPFTable)
{
	if (!lpMDB || !lpPFTable || !szServerDN) return MAPI_E_INVALID_PARAMETER;
	*lpPFTable = NULL;

	HRESULT	hRes = S_OK;
	LPEXCHANGEMANAGESTORE4 lpManageStore4 = NULL;

	hRes=lpMDB->QueryInterface(
		IID_IExchangeManageStore4,
		(void **) &lpManageStore4);
	if (!SUCCEEDED(hRes))
		return hRes;

	if (lpManageStore4)
	{
		hRes=lpManageStore4->GetPublicFolderTableOffset(
			(LPSTR) szServerDN,
			lpPFTable,
			ulFlags,
			ulOffset);
		lpManageStore4->Release();
	}

	return hRes;
} // GetPublicFolderTable4

HRESULT MAPIStore::GetPublicFolderTable5(
							  _In_ LPMDB lpMDB,
							  _In_z_ LPCTSTR szServerDN,
							  ULONG ulOffset,
							  ULONG ulFlags,
							  _In_opt_ LPGUID lpGuidMDB,
							  _Deref_out_opt_ LPMAPITABLE* lpPFTable)
{
	if (!lpMDB || !lpPFTable || !szServerDN) return MAPI_E_INVALID_PARAMETER;
	*lpPFTable = NULL;

	HRESULT	hRes = S_OK;
	LPEXCHANGEMANAGESTORE5 lpManageStore5 = NULL;

	hRes=(lpMDB->QueryInterface(
		IID_IExchangeManageStore5,
		(void **) &lpManageStore5));

	if (!SUCCEEDED(hRes))
		return hRes;

	if (lpManageStore5)
	{
		hRes=lpManageStore5->GetPublicFolderTableEx(
			(LPSTR) szServerDN,
			lpGuidMDB,
			lpPFTable,
			ulFlags,
			ulOffset);

		lpManageStore5->Release();
	}

	return hRes;
} // GetPublicFolderTable5

HRESULT MAPIStore::GetServerName(_In_ LPMAPISESSION lpSession, _Deref_out_opt_z_ LPTSTR* szServerName)
{
	HRESULT			hRes = S_OK;
	LPSERVICEADMIN	pSvcAdmin = NULL;
	LPPROFSECT		pGlobalProfSect = NULL;
	LPSPropValue	lpServerName	= NULL;

	if (!lpSession) return MAPI_E_INVALID_PARAMETER;

	*szServerName = NULL;

	hRes=lpSession->AdminServices(
		0,
		&pSvcAdmin);
	if(hRes!=S_OK)
		return hRes;

	hRes=pSvcAdmin->OpenProfileSection(
		(LPMAPIUID)pbGlobalProfileSectionGuid,
		NULL,
		0,
		&pGlobalProfSect);
	if(hRes!=S_OK)
		return hRes;

	hRes=HrGetOneProp(pGlobalProfSect,
		PR_PROFILE_HOME_SERVER,
		&lpServerName);
	if(hRes!=S_OK)
		return hRes;

	if (Zimbra::MAPI::Util::CheckStringProp(lpServerName,PT_STRING8)) // profiles are ASCII only
	{
#ifdef UNICODE
		LPWSTR	szWideServer = NULL;
		AtoW(lpServerName->Value.lpszA,
			szWideServer);
		CopyString((LPWSTR)*szServerName,szWideServer);
		delete[] szWideServer;
#else
		EC_H(CopyStringA(szServerName,lpServerName->Value.lpszA,NULL));
#endif
	}

	MAPIFreeBuffer(lpServerName);
	if (pGlobalProfSect) pGlobalProfSect->Release();
	if (pSvcAdmin) pSvcAdmin->Release();
	return hRes;
} // GetServerName


HRESULT MAPIStore::GetPublicFolderTable(LPMAPITABLE *lpMapiTable)
{
	HRESULT hRes=S_OK;
	LPMDB lpMDB=m_Store;
	LPMDB lpPrivateMDB = NULL;

	// try the 'current' MDB first
	if (!StoreSupportsManageStore(lpMDB))
	{
		// if that MDB doesn't support manage store, try to get one that does
		hRes=OpenMessageStoreGUID(m_mapiSession,pbExchangeProviderPrimaryUserGuid,&lpPrivateMDB);
		lpMDB = lpPrivateMDB;
	}

	if (lpMDB && StoreSupportsManageStore(lpMDB))
	{
		LPMAPITABLE	lpPFTable = NULL;
		LPTSTR		szServerName = NULL;
		hRes=GetServerName(m_mapiSession, &szServerName);

		LPTSTR	szServerDN = NULL;

		hRes=BuildServerDN(szServerName,
			_T(""),
			&szServerDN);
		if (szServerDN)
		{
			LPMDB lpOldMDB = NULL;

			// if we got a new MDB, set it in lpMapiObjects
			if (lpPrivateMDB)
			{
				lpOldMDB = m_Store; // do not release
				if (lpOldMDB) lpOldMDB->AddRef(); // hold on to this so that...
				// If we don't do this, we crash when destroying the Mailbox Table Window
				m_Store= lpMDB;
			}
		
			hRes=GetPublicFolderTable1(
							lpMDB,
							szServerDN,
							fMapiUnicode,
							&lpPFTable);

			*lpMapiTable= lpPFTable;

			if (lpOldMDB)
			{
				m_Store=lpOldMDB; 
				if (lpOldMDB) lpOldMDB->Release();
			}
		}
		MAPIFreeBuffer(szServerDN);
		MAPIFreeBuffer(szServerName);
	}
	if (lpPrivateMDB) lpPrivateMDB->Release();
	return hRes;
}