/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2011, 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
#include "common.h"
#include "Exchange.h"
#include "MAPIStore.h"

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
