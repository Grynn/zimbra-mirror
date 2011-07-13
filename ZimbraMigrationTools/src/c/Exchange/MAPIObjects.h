#pragma once
#include "ExchangeCommon.h"

#define PR_PROFILE_UNRESOLVED_NAME 0x6607001e
#define PR_PROFILE_UNRESOLVED_SERVER 0x6608001e

using namespace std;
namespace Zimbra {namespace MAPI {

class GenericException
{
private:
	HRESULT m_errcode;
	wstring m_strdescription;
	int m_srcLine;
	string m_srcFile;
public:
	GenericException(HRESULT hrErrCode, LPCWSTR lpszDescription);
	GenericException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile);
	virtual ~GenericException(){};
};


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
	HRESULT CreateExchangeMailBox(LPWSTR lpwstrNewUser, LPWSTR lpwstrNewUserPwd, LPWSTR lpwstrlogonuserDN, LPWSTR lpwstrLogonUsrPwd);

};

class MAPISession
{
private:
	IMAPISession *m_Session;
	HRESULT _mapiLogon(LPWSTR strProfile, DWORD dwFlags, LPMAPISESSION &session);
public:
	MAPISession();
	~MAPISession();
	HRESULT Logon(LPWSTR strProfile);
	HRESULT Logon(bool bDefaultProfile);
	LPMAPISESSION GetMAPISessionObject(){return m_Session;};
};

class MAPIStore
{
private:
	LPMDB m_Store;
	LPMAPISESSION m_mapiSession;
public:
	MAPIStore(LPMAPISESSION mapisession);//returns default store
	MAPIStore(LPMAPISESSION mapisession, LPWSTR pServerDn, LPWSTR pUserDn); // opens user's store
	~MAPIStore();
};


} //namespace MAPI

}//namespace Zimbra