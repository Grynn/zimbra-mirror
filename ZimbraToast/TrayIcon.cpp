#include "StdAfx.h"
#include "trayicon.h"
#include "resource.h"
#include "OptionsDlg.h"
#include "NotifParams.h"

#define TRAYICONID	1
#define WM_TRAY		(WM_USER+1)

CTrayIcon* CTrayIcon::m_pTrayIcon = NULL;

CTrayIcon::CTrayIcon(void)
{
	m_hInstance = 0;
	m_hWnd		= 0;
	m_hMailIcon	= 0;
	m_hMenu		= 0;
	m_hErrorIcon = 0;
}


CTrayIcon* CTrayIcon::GetInstance()
{
	if( m_pTrayIcon == NULL )
	{
		m_pTrayIcon = new CTrayIcon();
	}
	return m_pTrayIcon;
}


CTrayIcon::~CTrayIcon(void)
{
}


BOOL CTrayIcon::Create(HINSTANCE hInstance)
{
	m_hInstance = hInstance;
	m_hWnd = CreateDialog( m_hInstance, MAKEINTRESOURCE(IDD_TRAY_DLG), NULL, (DLGPROC)CTrayIcon::WndProc );
	if( m_hWnd == 0 )
		return FALSE;

	m_hErrorIcon = LoadIcon( m_hInstance, MAKEINTRESOURCE(IDI_ICON_MAIL_ERROR) );
	m_hMailIcon = LoadIcon( m_hInstance, MAKEINTRESOURCE(IDI_ICON_MAIL) );
	m_hNewMailIcon = LoadIcon( m_hInstance, MAKEINTRESOURCE(IDI_ICON_MAIL_NEW));
	m_hMenu = LoadMenu( m_hInstance, MAKEINTRESOURCE(IDR_MENU_POPUP) );
	m_hMenu = GetSubMenu( m_hMenu, 0 );

	NOTIFYICONDATA nid = {0};
	nid.hWnd = m_hWnd;
	nid.uID = TRAYICONID;
	nid.uFlags = NIF_ICON | NIF_MESSAGE;
	nid.hIcon = m_hMailIcon;
	nid.uCallbackMessage = WM_TRAY;

	Shell_NotifyIcon( NIM_ADD, &nid);
	return TRUE;
}


void CTrayIcon::SetNoMailIcon()
{
	NOTIFYICONDATA nid = {0};
	nid.hWnd = m_hWnd;
	nid.uID = TRAYICONID;
	nid.uFlags = NIF_ICON;
	nid.hIcon = m_hMailIcon;

	Shell_NotifyIcon( NIM_MODIFY, &nid);
}

void CTrayIcon::SetNewMailIcon()
{
	NOTIFYICONDATA nid = {0};
	nid.hWnd = m_hWnd;
	nid.uID = TRAYICONID;
	nid.uFlags = NIF_ICON;
	nid.hIcon = m_hNewMailIcon;

	Shell_NotifyIcon( NIM_MODIFY, &nid);
}

void CTrayIcon::SetErrorIcon()
{
	NOTIFYICONDATA nid = {0};
	nid.hWnd = m_hWnd;
	nid.uID = TRAYICONID;
	nid.uFlags = NIF_ICON;
	nid.hIcon = m_hErrorIcon;

	Shell_NotifyIcon( NIM_MODIFY, &nid);
}

void CTrayIcon::SetToolTip( LPCTSTR pStr )
{
	NOTIFYICONDATA nid = {0};
	nid.hWnd = m_hWnd;
	nid.uID = TRAYICONID;
	nid.uFlags = NIF_TIP;
	int len = (int)_tcslen(pStr);

	if( len >= 128 )
		len = 127;
	_tcsncpy( nid.szTip, pStr, len );
	nid.szTip[len] = _T('\0');

	Shell_NotifyIcon( NIM_MODIFY, &nid);
}


void CTrayIcon::Destroy()
{
	SendMessage( m_hWnd, WM_DESTROY, 0, 0 );
}


void openMailbox()
{
	CNotifParams* pP = CNotifParams::GetInstance();
	LPCTSTR pS = pP->Server();
	UINT port = pP->Port();

	LPTSTR pCmd = new TCHAR[ _tcslen(pS) + 128 ];
	_stprintf( pCmd, _T("http://%s:%d/zimbra"), pS, port );

	ShellExecute( NULL, _T("open"), pCmd, NULL, NULL, SW_SHOWNORMAL );
	delete [] pCmd;
	LocalFree((HLOCAL)pS);
}


LRESULT CALLBACK CTrayIcon::WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
	POINT mousePos = {0};
	NOTIFYICONDATA nid = {0};
	CTrayIcon* pTray = CTrayIcon::GetInstance();

	WORD code = HIWORD(wParam);
	WORD id = LOWORD(wParam);

	switch( message )
	{
		case WM_COMMAND:
			switch( id )
			{
				case ID_POPUP_OPTIONS:
					COptionsDlg::GetInstance()->Show();
					break;
				case ID_POPUP_VIEWMAILBOX:
					openMailbox();
					break;
				case ID_POPUP_QUIT:
					nid.hWnd = pTray->m_hWnd;
					nid.uID = TRAYICONID;
					Shell_NotifyIcon( NIM_DELETE, &nid );
					PostQuitMessage(0);
					break;
			}
			break;
		case WM_TRAY:
			switch( lParam )
			{
				case WM_LBUTTONDOWN:
					break;
				case WM_LBUTTONUP:
					break;
				case WM_RBUTTONDOWN:
					GetCursorPos(&mousePos);
					TrackPopupMenu( pTray->m_hMenu, 0, mousePos.x, mousePos.y, 0, pTray->m_hWnd, NULL );
					break;
				case WM_RBUTTONUP:
					break;
			}
			break;

	}
	return 0;
}