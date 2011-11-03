#pragma once

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
    ExchangeAdminException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine, LPCSTR
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

public:
    static LPCWSTR GlobalInit(LPCWSTR lpMAPITarget, LPCWSTR lpAdminUsername = NULL, LPCWSTR
        lpAdminPassword = NULL);
    static LPCWSTR GlobalUninit();
    static LPCWSTR SelectExchangeUsers(vector<ObjectPickerData> &vUserList);
};

const LPCWSTR DEFAULT_ADMIN_PROFILE_NAME = L"zm_prof";
const LPCWSTR DEFAULT_ADMIN_MAILBOX_NAME = L"zm_mbox";
const LPCWSTR DEFAULT_ADMIN_PASSWORD = L"z1mbr4Migration";
}
}
