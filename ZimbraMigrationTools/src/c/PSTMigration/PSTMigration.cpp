#include "common.h"
#include "..\Exchange\Exchange.h"
#include "..\Exchange\ExchangeAdmin.h"
#include "Zimbra/RPC.h"
#include "..\Exchange\MAPIAccessAPI.h"

LPCWSTR lpProfileName=L"testprofile";
LPCWSTR lpServerAddress=L"10.117.82.161";
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
	m_pConnection = new Zimbra::Rpc::Connection(L"migration",lpServerAddress,nPort, 
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
	Zimbra::Rpc::ZimbraConnection* z_pConnection=new Zimbra::Rpc::ZimbraConnection(L"migration", lpServerAddress, nPort, 
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

void CreateExchangeMailBox()
{
	Zimbra::MAPI::ExchangeAdmin *exchadmin= new Zimbra::MAPI::ExchangeAdmin(L"10.117.82.161");
	try
	{
		try
		{
			exchadmin->DeleteProfile(Zimbra::MAPI::DEFAULT_ADMIN_PROFILE_NAME);
		}
		catch(Zimbra::MAPI::ExchangeAdminException &ex)
		{
			UNREFERENCED_PARAMETER(ex);
		}

		try
		{
			exchadmin->DeleteExchangeMailBox(Zimbra::MAPI::DEFAULT_ADMIN_MAILBOX_NAME,
				L"Administrator",L"z1mbr4Migration");
		}
		catch(Zimbra::MAPI::ExchangeAdminException &ex)
		{
			UNREFERENCED_PARAMETER(ex);
		}
		exchadmin->CreateExchangeMailBox(Zimbra::MAPI::DEFAULT_ADMIN_MAILBOX_NAME,Zimbra::MAPI::DEFAULT_ADMIN_PASSWORD,
			L"Administrator",L"z1mbr4Migration");
		exchadmin->CreateProfile(Zimbra::MAPI::DEFAULT_ADMIN_PROFILE_NAME,
			Zimbra::MAPI::DEFAULT_ADMIN_MAILBOX_NAME,Zimbra::MAPI::DEFAULT_ADMIN_PASSWORD);
		exchadmin->SetDefaultProfile(Zimbra::MAPI::DEFAULT_ADMIN_PROFILE_NAME);
	}
	catch(Zimbra::MAPI::ExchangeAdminException &ex)
	{
		UNREFERENCED_PARAMETER(ex);
	}

	delete exchadmin;
}

void GetAllProfiles()
{
	Zimbra::MAPI::ExchangeAdmin *exchadmin= new Zimbra::MAPI::ExchangeAdmin(L"10.117.82.161");
	vector<string> vProfileList;
	exchadmin->GetAllProfiles(vProfileList);
	vector<string>::iterator itr= vProfileList.begin();
	delete exchadmin;
}

void GetUserDN()
{
	wstring userDN;
	wstring lagcyName;
	Zimbra::MAPI::Util::GetUserDNAndLegacyName(L"10.117.82.161",L"Administrator",userDN,lagcyName);
}

void ExchangeMigrationSetupTest()
{
/*	ExchangeMigrationSetup *exchmigsetup = new ExchangeMigrationSetup(L"10.117.82.161",
		L"Administrator",L"z1mbr4Migration");
	exchmigsetup->Setup();
	
	vector<string> vProfileList;
	exchmigsetup->GetAllProfiles(vProfileList);
	vector<string>::iterator itr= vProfileList.begin();
	while(itr != vProfileList.end())
	{
		printf("%s\n" ,((string)*itr).c_str());
		itr++ ;
	}

	exchmigsetup->Clean();
	delete exchmigsetup;
	*/
	//If Profile exists, rest are Optional else rest 3 params must be there!!!
	//ExchangeOps::Init("Profile", ExchangeIP[optional], AdminName[Optional], AdminPwd[Optional]);
	LPCWSTR lpwstrStatus=ExchangeOps::GlobalInit(L"10.117.82.161",L"Administrator",L"z1mbr4Migration");
	delete[]lpwstrStatus; 

	lpwstrStatus=ExchangeOps::GlobalUninit();
	delete[]lpwstrStatus;	
}

void MAPIAccessAPITest()
{
	tree<Folder_Data> tr;
	//Create class instance with Exchange server hostname/IP, Outlook admin profile name, Exchange mailbox to be migrated
	Zimbra::MAPI::MAPIAccessAPI *maapi = new Zimbra::MAPI::MAPIAccessAPI(L"10.117.82.161",L"Outlook",L"appt1");

	//Init session and stores
	maapi->Initialize();

	//Get all folders
	maapi->GetRootFolderHierarchy(tr);

	//iterate over folders
	maapi->IterateTree(tr);

	//free it
	delete maapi;
}

int main(int argc, TCHAR *argv[])
{
	UNREFERENCED_PARAMETER(argc);
	UNREFERENCED_PARAMETER(argv);
	
//	AdminAuth();
//	UserAuth();
//	ZCFileUploadTest();
//	CreateExchangeMailBox();
//	GetAllProfiles();	
//	MAPIAccessAPITest();

	ExchangeMigrationSetupTest();
	//CreateExchangeMailBox();
	
	return 0;
}

