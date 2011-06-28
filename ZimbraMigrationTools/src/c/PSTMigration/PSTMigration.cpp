#include "Exchange/ExchangeUtils.h"

LPCWSTR lpProfileName=L"testprofile";
LPCWSTR lpServerAddress=L"10.117.82.163";
LPCWSTR lpAdminUser = L"admin@zcs2.zmexch.in.zimbra.com";
LPCWSTR lpAccountUser = L"av1@zcs2.zmexch.in.zimbra.com";
LPCWSTR lpAccountUserPwd=L"test123";
LPCWSTR lpAdminPwd =L"z1mbr4";
ULONG nAdminPort = 7071;
ULONG nPort = 80;


Zimbra::Rpc::Connection *m_pConnection=NULL;


void Init()
{
	Zimbra::Mapi::Memory::SetMemAllocRoutines( NULL, MAPIAllocateBuffer, MAPIAllocateMore, MAPIFreeBuffer );
}

void AdminAuth()
{		
	Zimbra::Rpc::AdminAuthRequest authRequest(lpAdminUser, lpAdminPwd,L"");

	Zimbra::Rpc::AdminConnection*	m_pAdminConnection ;
	Zimbra::Util::ScopedInterface<IXMLDOMDocument2> pResponseXml;
	m_pAdminConnection = new Zimbra::Rpc::AdminConnection (lpServerAddress, 
														nAdminPort, 
														TRUE, 
														0,
														L"") ;
	 m_pAdminConnection->SetCurrentUser((LPWSTR)lpAccountUser);
	 m_pAdminConnection->SendRequest( authRequest, pResponseXml.getref() );
}


void UserAuth()
{
	Zimbra::Util::ScopedInterface<IXMLDOMDocument2> pResponseXml;
	m_pConnection = new Zimbra::Rpc::Connection(lpServerAddress,nPort, 
					false, 0, L"");

	m_pConnection->SetCurrentUser((LPWSTR)lpAccountUser);
	Zimbra::Rpc::AuthRequest authRequest( lpAccountUser, lpAccountUserPwd,lpServerAddress );
	m_pConnection->SendRequest( authRequest,pResponseXml.getref() );

	Zimbra::Util::ScopedPtr<Zimbra::Rpc::Response> pResponse(
				Zimbra::Rpc::Response::Manager::NewResponse( pResponseXml.get() ));
	
}

BOOL FileUpload( Zimbra::Rpc::ZimbraConnection *z_connection, LPWSTR* ppwszToken )
{
	LOGFN_INTERNAL_NO;
	
	LPSTR pszTestFile = "E:\\temp\\aa.log";

	LPSTREAM  pStreamFile = NULL;

	HRESULT hr = OpenStreamOnFile (MAPIAllocateBuffer, MAPIFreeBuffer,
                       STGM_READ, (LPWSTR)pszTestFile, NULL, &pStreamFile);

	if( FAILED(hr) ) {
		LOG_ERROR(_T("failed to OpenStreamOnFile call: %x"), hr);
		return FALSE;
	}

	HANDLE hFile = CreateFile( L"E:\\temp\\aa.log", GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL );
	DWORD dwFileSize = GetFileSize( hFile, NULL );	
	CloseHandle( hFile );
	_tprintf( _T("  File Upload size=%d bytes\n"), dwFileSize );

	BOOL bResult = z_connection->DoFileUpload( pStreamFile, dwFileSize, L"/service/upload?fmt=raw", ppwszToken, NULL );

	pStreamFile->Release();

	return bResult;
}


void ZCFileUploadTest()
{
	Zimbra::Rpc::UserSession *session = Zimbra::Rpc::UserSession::CreateInstance(lpProfileName, lpAccountUser, lpAccountUserPwd, lpServerAddress, nPort, 0,0, L"",false);
	Zimbra::Rpc::UserSession::SetProfileName(lpProfileName);
	Zimbra::Rpc::ZimbraConnection* z_pConnection=new Zimbra::Rpc::ZimbraConnection( lpServerAddress, nPort, 
					false, 0, L"");
    z_pConnection->SetProfileName( lpProfileName );
	
	LPWSTR pwszToken=NULL;
	FileUpload(z_pConnection, &pwszToken);

	Zimbra::Rpc::SendMsgRequest request( pwszToken );

    // free the token right away
	z_pConnection->FreeBuffer( pwszToken );

	request.SetAuthToken( session->AuthToken() );
    if(session->SetNoSession()) {
    	request.SetNoSession() ;
    }	

    Zimbra::Rpc::ScopedRPCResponse pResponse;
	try {
		z_pConnection->SendRequest( request, pResponse.getref() );
	}
	catch (Zimbra::Rpc::SoapFaultResponse & fault) {
		LOG_ERROR(_T("Response is soap error exception (%s)."), fault.ErrorCode());
		return;
	}
}

int main(int argc, TCHAR *argv[])
{
	UNREFERENCED_PARAMETER(argc);
	UNREFERENCED_PARAMETER(argv);
	
	CoInitialize(NULL);
	MAPIInitialize(NULL);

	Init();
//	AdminAuth();
//	UserAuth();
//	ZCFileUploadTest();
	Zimbra::ExchangeUtils::ExchangeAdmin *exchadmin= new Zimbra::ExchangeUtils::ExchangeAdmin("10.117.82.161");
/*	
	exchadmin->CreateProfile("test_profile@exch","appt1","test123");
	exchadmin->SetDefaultProfile("test_profile@exch");
	vector<string> vProfileList;
	exchadmin->GetAllProfiles(vProfileList);
	vector<string>::iterator itr= vProfileList.begin();

	exchadmin->DeleteProfile("test_profile@exch");
	delete exchadmin;
*/
	vector<string> vProfileList;
	exchadmin->GetAllProfiles(vProfileList);
	vector<string>::iterator itr= vProfileList.begin();
	//exchadmin->CreateExchangeMailBox(L"new_zm12",L"z1mbr4Migration",L"CN=Administrator,CN=Users,DC=zmexch,DC=in,DC=zimbra,DC=com",L"z1mbr4Migration");

	return 1;
}

