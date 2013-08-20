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
#pragma once

namespace Zimbra
{
namespace MAPI
{
class MAPIStoreException: public GenericException
{
public:
    MAPIStoreException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    MAPIStoreException(HRESULT hrErrCode, LPCWSTR lpszDescription, LPCWSTR lpszShortDescription, int nLine, LPCSTR strFile);
    virtual ~MAPIStoreException() {}
};

class MAPIFolder;

// Mapi Store class
class MAPIStore
{
private:
    LPMDB m_Store;
    LPMAPISESSION m_mapiSession;
    SBinaryArray m_specialFolderIds;
    Zimbra::Util::CriticalSection cs_store;
	bool StoreSupportsManageStore(_In_ LPMDB lpMDB);
	HRESULT BuildServerDN(
					  _In_z_ LPCTSTR szServerName,
					  _In_z_ LPCTSTR szPost,
					  _Deref_out_z_ LPTSTR* lpszServerDN);
	HRESULT CallOpenMsgStore(
						 _In_ LPMAPISESSION	lpSession,
						 _In_ ULONG_PTR		ulUIParam,
						 _In_ LPSBinary		lpEID,
						 ULONG			ulFlags,
						 _Deref_out_ LPMDB*			lpMDB);
	HRESULT GetPublicFolderTable1(
							  _In_ LPMDB lpMDB,
							  _In_z_ LPCTSTR szServerDN,
							  ULONG ulFlags,
							  _Deref_out_opt_ LPMAPITABLE* lpPFTable);
	HRESULT GetPublicFolderTable4(
							  _In_ LPMDB lpMDB,
							  _In_z_ LPCTSTR szServerDN,
							  ULONG ulOffset,
							  ULONG ulFlags,
							  _Deref_out_opt_ LPMAPITABLE* lpPFTable);
	HRESULT GetPublicFolderTable5(
							  _In_ LPMDB lpMDB,
							  _In_z_ LPCTSTR szServerDN,
							  ULONG ulOffset,
							  ULONG ulFlags,
							  _In_opt_ LPGUID lpGuidMDB,
							  _Deref_out_opt_ LPMAPITABLE* lpPFTable);
	HRESULT GetServerName(_In_ LPMAPISESSION lpSession, _Deref_out_opt_z_ LPTSTR* szServerName);
public:
    MAPIStore();
    ~MAPIStore();
    void Initialize(LPMAPISESSION mapisession, LPMDB pMdb);
    HRESULT CompareEntryIDs(SBinary *pBin1, SBinary *pBin2, ULONG &lpulResult);
    HRESULT GetRootFolder(MAPIFolder &rootFolder);

    LPMDB GetInternalMAPIStore() { return m_Store; }
    SBinaryArray GetSpecialFolderIds() { return m_specialFolderIds; }
    HRESULT OpenEntry(ULONG cbEntryID, LPENTRYID lpEntryID, LPCIID lpInterface, ULONG ulFlags,
        ULONG FAR *lpulObjType, LPUNKNOWN FAR *lppUnk);
	HRESULT OpenMessageStoreGUID(_In_ LPMAPISESSION lpMAPISession,
							 _In_z_ LPCSTR lpGUID,
							 _Deref_out_opt_ LPMDB* lppMDB);
	
	HRESULT OpenPublicMessageStore(
	_In_ LPMAPISESSION lpMAPISession,
	ULONG ulFlags, // Flags for CreateStoreEntryID
	_Deref_out_opt_ LPMDB* lppPublicMDB);
	HRESULT HrMailboxLogon(
									  _In_ LPMAPISESSION		lpMAPISession,	// MAPI session handle
									  _In_ LPMDB				lpMDB,			// open message store
									  _In_z_ LPCTSTR			lpszMsgStoreDN,	// desired message store DN
									  _In_opt_z_ LPCTSTR		lpszMailboxDN,	// desired mailbox DN or NULL
									  ULONG						ulFlags,		// desired flags for CreateStoreEntryID
									  _Deref_out_opt_ LPMDB*	lppMailboxMDB);	// ptr to mailbox message store ptr
	
	HRESULT GetPublicFolderTable(LPMAPITABLE *lpMapiTable);
};
}
}
