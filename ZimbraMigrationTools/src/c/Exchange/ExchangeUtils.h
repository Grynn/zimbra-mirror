#pragma once
#include "ExchangeCommon.h"


#define PR_PROFILE_UNRESOLVED_NAME 0x6607001e
#define PR_PROFILE_UNRESOLVED_SERVER 0x6608001e

using namespace std;
namespace Zimbra {namespace ExchangeUtils {

	
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

};//class ExchangeAdmin



} //namespace ExchangeUtils

}//namespace Zimbra