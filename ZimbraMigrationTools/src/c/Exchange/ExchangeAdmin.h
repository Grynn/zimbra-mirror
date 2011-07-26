#pragma once
#include "MAPICommon.h"


using namespace std;
namespace Zimbra {namespace MAPI {

class ExchangeAdminException:public GenericException
{
public:
	ExchangeAdminException(HRESULT hrErrCode, LPCWSTR lpszDescription);
	ExchangeAdminException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile);
	virtual ~ExchangeAdminException(){};
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
}
}