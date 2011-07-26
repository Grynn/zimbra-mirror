#include "stdafx.h"
#include "..\Exchange\ExchangeAdmin.h"
#include "..\Exchange\MAPISession.h"
#include "..\Exchange\MAPIStore.h"
#include "..\Exchange\MAPIObjects.h"
#include "..\Exchange\MapiUtils.h"

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

bool iterate_folders(Zimbra::MAPI::MAPIFolder &folder)
{
	Zimbra::MAPI::FolderIterator folderIter;
	folder.GetFolderIterator( folderIter );

	Zimbra::MAPI::MAPIFolder childFolder;
	BOOL bMore = TRUE;
	while( bMore)
	{
		bMore = folderIter.GetNext( childFolder );
		if(bMore)
		{
			ULONG itemCount=0;
			childFolder.GetItemCount(itemCount);
			printf("FolderName: %S ----- %d\n",childFolder.Name().c_str(),itemCount );
		}
		if( bMore )
		{
			iterate_folders(childFolder);
		}		
	}
	return true;
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
/*
	Zimbra::MAPI::ExchangeAdmin *exchadmin= new Zimbra::MAPI::ExchangeAdmin("10.117.82.161");
	try
	{
		try
		{
			exchadmin->DeleteProfile("new_zm12@exch");
		}
		catch(Zimbra::MAPI::ExchangeAdminException &ex)
		{
			UNREFERENCED_PARAMETER(ex);
		}

		try
		{
			exchadmin->DeleteExchangeMailBox(L"new_zm12",L"Administrator",L"z1mbr4Migration");
		}
		catch(Zimbra::MAPI::ExchangeAdminException &ex)
		{
			UNREFERENCED_PARAMETER(ex);
		}
		exchadmin->CreateExchangeMailBox(L"new_zm12",L"z1mbr4Migration",L"Administrator",L"z1mbr4Migration");
		exchadmin->CreateProfile("new_zm12@exch","new_zm12","z1mbr4Migration");
		exchadmin->SetDefaultProfile("new_zm12@exch");
	}
	catch(Zimbra::MAPI::ExchangeAdminException &ex)
	{
		UNREFERENCED_PARAMETER(ex);
	}

*/	
/*
	vector<string> vProfileList;
	exchadmin->GetAllProfiles(vProfileList);
	vector<string>::iterator itr= vProfileList.begin();

	exchadmin->DeleteProfile("test_profile@exch");
	delete exchadmin;
	

	vector<string> vProfileList;
	exchadmin->GetAllProfiles(vProfileList);
	vector<string>::iterator itr= vProfileList.begin();
*/

	Zimbra::MAPI::MAPISession *p_zmmapisession = new Zimbra::MAPI::MAPISession();
	p_zmmapisession->Logon();
	Zimbra::MAPI::MAPIStore store;
	p_zmmapisession->OpenDefaultStore(store);

	Zimbra::MAPI::MAPIFolder rootFolder;
	store.GetRootFolder(rootFolder);
	
	iterate_folders(rootFolder);
	delete p_zmmapisession;


/*	wstring userDN;
	Zimbra::MAPI::Util::GetUserDN(L"10.117.82.161",L"Administrator",userDN);
*/
	return 0;
	//exchadmin->CreateExchangeMailBox(L"new_zm12",L"z1mbr4Migration",L"CN=Administrator,CN=Users,DC=zmexch,DC=in,DC=zimbra,DC=com",L"z1mbr4Migration");

}

