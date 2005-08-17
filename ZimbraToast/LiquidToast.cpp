#include "stdafx.h"
#include "LiquidToast.h"
#include "NotifierWnd.h"
#include "TrayIcon.h"
#include "NotifParams.h"
#include "OptionsDlg.h"
#include "LiquidNotifier.h"

int APIENTRY _tWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPTSTR lpCmdLine, int nCmdShow)
{

	CNotifParams* pNotifParams = CNotifParams::GetInstance();

	size_t len = _tcslen(lpCmdLine);
	
	//see if we are mailto-ing
	if( len > 8 && (_tcsnicmp( lpCmdLine, _T("-mailto "), 8 )==0) )
	{
		LPTSTR pMailToUrl = lpCmdLine + 8;
		LPTSTR pServer = pNotifParams->Server();
		UINT   nPort   = pNotifParams->Port();

		size_t urlLen = 31 + (len-8) + _tcslen(pServer) + 32 + 20;
		LPTSTR pszUrl = new TCHAR[urlLen];

		//TODO: URLEscape

		_stprintf( pszUrl, _T("http://%s:%d/liquid/compose?mailto=%s"), pServer, nPort, pMailToUrl+7 );

		ShellExecute( NULL, _T("open"), pszUrl, NULL, NULL, SW_SHOWNORMAL );

		delete [] pszUrl;
		LocalFree( (HLOCAL)pServer );

		return 0;
	}


	//allow only 1 instance of the application to execute
	HANDLE hMux = CreateMutex( NULL, TRUE, _T("LiQ_n0t1f_MuTEx") );
	if( hMux != NULL && GetLastError() == ERROR_ALREADY_EXISTS )
	{
		CloseHandle(hMux);
		return 0;
	}

	CoInitialize(NULL);

	InitCommonControls();

	HACCEL hAccelTable = LoadAccelerators(hInstance, (LPCTSTR)IDC_WINDOWTEST);

	//initialize the notifier window
	if( !CNotifierWindow::GetInstance()->Create(hInstance) )
		return FALSE;

	//initialize the options dialog
	if( !COptionsDlg::GetInstance()->Create(hInstance) )
		return FALSE;

	//initialize the tray icon
	if( !CTrayIcon::GetInstance()->Create(hInstance) )
		return FALSE;

	//start the poller
	if( !CLiquidNotifier::GetInstance()->Start() )
	{
		CTrayIcon::GetInstance()->SetErrorIcon();
	}
	else
	{
		CTrayIcon::GetInstance()->SetNoMailIcon();
	}


	MSG msg;
	while (GetMessage(&msg, NULL, 0, 0)) 
	{
		if (!TranslateAccelerator(msg.hwnd, hAccelTable, &msg)) 
		{
			TranslateMessage(&msg);
			DispatchMessage(&msg);
		}
	}

	//stop the notifier
	CLiquidNotifier::GetInstance()->Stop();

	//remove any icons from the tray
	CTrayIcon::GetInstance()->Destroy();

	//let another instance run if it wants
	CloseHandle(hMux);
	CoUninitialize();
	return (int) msg.wParam;
}
