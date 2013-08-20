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
class MAPISessionException: public GenericException
{
public:
    MAPISessionException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    MAPISessionException(HRESULT hrErrCode, LPCWSTR lpszDescription, LPCWSTR lpszShortDescription, int nLine, LPCSTR strFile);
    virtual ~MAPISessionException() {}
};

// Item type consts
#define ZCM_NONE                0x00
#define ZCM_MAIL                0x01
#define ZCM_CONTACTS            0x02
#define ZCM_APPOINTMENTS        0x04
#define ZCM_TASKS               0x08
#define ZCM_MEETRQRS            0x10
#define ZCM_ALL                 0xFF
class MAPIStore;

// MAPI session class
class MAPISession
{
private:
    IMAPISession *m_Session;

    HRESULT _mapiLogon(LPWSTR strProfile, DWORD dwFlags, LPMAPISESSION &session);

    Zimbra::Util::CriticalSection cs;

public:
    MAPISession();
    ~MAPISession();
    HRESULT Logon(LPWSTR strProfile);
    HRESULT Logon(bool bDefaultProfile = true);

    LPMAPISESSION GetMAPISessionObject() { return m_Session; }
    HRESULT OpenDefaultStore(MAPIStore &Store);
    HRESULT OpenOtherStore(LPMDB OpenedStore, LPWSTR pServerDn, LPWSTR pUserDn,
        MAPIStore &OtherStore);
	HRESULT OpenPublicStore(MAPIStore &Store);
    HRESULT OpenAddressBook(LPADRBOOK *ppAddrBook);
    HRESULT OpenEntry(ULONG cbEntryID, LPENTRYID lpEntryID, LPCIID lpInterface, ULONG ulFlags,
        ULONG FAR *lpulObjType, LPUNKNOWN FAR *lppUnk);
    HRESULT CompareEntryIDs(SBinary *pBin1, SBinary *pBin2, ULONG &lpulResult);
};
}
}
