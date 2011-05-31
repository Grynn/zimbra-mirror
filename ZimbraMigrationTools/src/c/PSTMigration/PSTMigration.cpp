#include "common.h"
#include "commonMAPI.h"
#include "Zimbra/Zimbra.h"
#include "Zimbra/Rpc.h"

int main(int argc, TCHAR *argv[])
{
	UNREFERENCED_PARAMETER(argc);
	UNREFERENCED_PARAMETER(argv);
	
	Zimbra::Rpc::UserSession::CreateInstance(_T("dummy profile"),L"av1@zcs2.zmexch.in.zimbra.com",L"test123",L"10.117.82.163",7071,false, false , NULL);
	//Zimbra::Rpc::SendMsgRequest sendMsg( L"testtoken" );
	/*

 	// TODO: Place code here.
	MSG msg;
	HACCEL hAccelTable;

	// Initialize global strings
	LoadString(hInstance, IDS_APP_TITLE, szTitle, MAX_LOADSTRING);
	LoadString(hInstance, IDC_FOOBAZ, szWindowClass, MAX_LOADSTRING);
	MyRegisterClass(hInstance);

	// Perform application initialization:
	if (!InitInstance (hInstance, nCmdShow))
	{
		return FALSE;
	}

	hAccelTable = LoadAccelerators(hInstance, MAKEINTRESOURCE(IDC_FOOBAZ));

	// Main message loop:
	while (GetMessage(&msg, NULL, 0, 0))
	{
		if (!TranslateAccelerator(msg.hwnd, hAccelTable, &msg))
		{
			TranslateMessage(&msg);
			DispatchMessage(&msg);
		}
	}

	return (int) msg.wParam;
	*/

	return 1;
}

