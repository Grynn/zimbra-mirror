#pragma once
#include "ExchangeCommon.h"

#define PR_PROFILE_UNRESOLVED_NAME 0x6607001e
#define PR_PROFILE_UNRESOLVED_SERVER 0x6608001e

using namespace std;
namespace Zimbra {namespace MAPI {

class ExchangeAdminException:public GenericException
{
public:
	ExchangeAdminException(HRESULT hrErrCode, LPCWSTR lpszDescription);
	ExchangeAdminException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile);
	virtual ~ExchangeAdminException(){};
};

class MAPISessionException:public GenericException
{
public:
	MAPISessionException(HRESULT hrErrCode, LPCWSTR lpszDescription);
	MAPISessionException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile);
	virtual ~MAPISessionException(){};
};

class MAPIStoreException:public GenericException
{
public:
	MAPIStoreException(HRESULT hrErrCode, LPCWSTR lpszDescription);
	MAPIStoreException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile);
	virtual ~MAPIStoreException(){};
};

class ExchangeAdmin
{
private:
	LPPROFADMIN m_pProfAdmin;
	string m_strServer;
private:
	HRESULT Init();
public:
	ExchangeAdmin(string strExchangeServer);
	~ExchangeAdmin();
	HRESULT CreateProfile(string strProfileName,string strMailboxName,string strPassword);
	HRESULT DeleteProfile(string strProfile);
	HRESULT GetAllProfiles(vector<string> &vProfileList);
	HRESULT SetDefaultProfile(string strProfile);
	HRESULT CreateExchangeMailBox(LPWSTR lpwstrNewUser, LPWSTR lpwstrNewUserPwd, LPWSTR lpwstrlogonuser, LPWSTR lpwstrLogonUsrPwd);
	HRESULT DeleteExchangeMailBox(LPWSTR lpwstrMailBox,LPWSTR lpwstrlogonuser, LPWSTR lpwstrLogonUsrPwd);
};

class MAPIStore;

class MAPISession
{
private:
	IMAPISession *m_Session;
	HRESULT _mapiLogon(LPWSTR strProfile, DWORD dwFlags, LPMAPISESSION &session);
public:
	MAPISession();
	~MAPISession();
	HRESULT Logon(LPWSTR strProfile);
	HRESULT Logon(bool bDefaultProfile=true);
	LPMAPISESSION GetMAPISessionObject(){return m_Session;};
	HRESULT OpenDefaultStore(MAPIStore &Store);
	HRESULT OpenOtherStore(LPMDB OpenedStore,LPWSTR pServerDn, LPWSTR pUserDn,MAPIStore &OtherStore);
	HRESULT OpenAddressBook(LPADRBOOK* ppAddrBook);
	
};

class MAPIStore
{
private:
	LPMDB m_Store;
	LPMAPISESSION m_mapiSession;
public:
	MAPIStore();
	~MAPIStore();
	void Initialize(LPMAPISESSION mapisession, LPMDB pMdb);
};


} //namespace MAPI

}//namespace Zimbra