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
#pragma once
#include "Util.h"

namespace Zimbra
{
namespace MAPI
{
#define EXCH_UNINITIALIZED              0
#define EXCH_INITIALIZED_PROFCREATE     1
#define EXCH_INITIALIZED_PROFEXIST      2

class ExchangeAdminException: public GenericException
{
public:
    ExchangeAdminException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    ExchangeAdminException(HRESULT hrErrCode, LPCWSTR lpszDescription, LPCWSTR lpszShortDescription, int nLine, LPCSTR
        strFile);
    virtual ~ExchangeAdminException() {}
};

class ExchangeAdmin
{
private:
    LPPROFADMIN m_pProfAdmin;
    wstring m_strServer;

private:
    HRESULT Init();

public:
    ExchangeAdmin(wstring strExchangeServer);
    ~ExchangeAdmin();
    HRESULT CreateProfile(wstring strProfileName, wstring strMailboxName, wstring strPassword);
    HRESULT DeleteProfile(wstring strProfile);
    HRESULT GetAllProfiles(vector<string> &vProfileList);
    HRESULT SetDefaultProfile(wstring strProfile);
    HRESULT CreateExchangeMailBox(LPCWSTR lpwstrNewUser, LPCWSTR lpwstrNewUserPwd, LPCWSTR
        lpwstrlogonuser, LPCWSTR lpwstrLogonUsrPwd);
    HRESULT DeleteExchangeMailBox(LPCWSTR lpwstrMailBox, LPCWSTR lpwstrlogonuser, LPCWSTR
        lpwstrLogonUsrPwd);
};

class ExchangeMigrationSetup
{
private:
    ExchangeAdmin *m_exchAdmin;
    wstring m_strServer;
    wstring m_ExchangeAdminName;
    wstring m_ExchangeAdminPwd;

public:
    ExchangeMigrationSetup(LPCWSTR strExhangeHost, LPCWSTR ExchangeAdminName, LPCWSTR
        ExchangeAdminPwd);
    ~ExchangeMigrationSetup();
    HRESULT Setup();
    HRESULT Clean();
    HRESULT GetAllProfiles(vector<string> &vProfileList);
};

class ExchangeOps
{
private:
    static ExchangeMigrationSetup *m_exchmigsetup;
    static int Initialized;
    static MAPISession *m_zmmapisession;
	static void internalEOInit();
	static LPCWSTR _GlobalInit(LPCWSTR lpMAPITarget, LPCWSTR lpAdminUsername = NULL, LPCWSTR
        lpAdminPassword = NULL);
public:
    static LPCWSTR GlobalInit(LPCWSTR lpMAPITarget, LPCWSTR lpAdminUsername = NULL, LPCWSTR
        lpAdminPassword = NULL);
    static LPCWSTR GlobalUninit();
    static LPCWSTR SelectExchangeUsers(vector<ObjectPickerData> &vUserList);
    static BOOL AvoidInternalErrors(LPCWSTR lpToCmp);
};

const LPCWSTR DEFAULT_ADMIN_PROFILE_NAME = L"zmprof";
const LPCWSTR DEFAULT_ADMIN_MAILBOX_NAME = L"zmmbox";
}
}
