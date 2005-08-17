#include "StdAfx.h"
#include ".\optionsdlg.h"
#include "resource.h"
#include "NotifParams.h"
#include "LiquidNotifier.h"
#include "TrayIcon.h"
COptionsDlg* COptionsDlg::m_pDlg = NULL;


COptionsDlg::COptionsDlg(void)
{
}


COptionsDlg::~COptionsDlg(void)
{
}

COptionsDlg* COptionsDlg::GetInstance()
{
	if( m_pDlg == NULL )
	{
		m_pDlg = new COptionsDlg();
	}
	return m_pDlg;
}

BOOL COptionsDlg::Create( HINSTANCE hInst )
{
	m_hInstance = hInst;
	return TRUE;
}

LRESULT CALLBACK COptionsDlg::WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
	CNotifParams* pParams = CNotifParams::GetInstance();
	HWND h;
	LPTSTR pStr;
	UINT ui;
	TCHAR pTmp[256];
	int nTmp = 255;

	switch (message)
	{
		case WM_INITDIALOG:
			h = GetDlgItem( hWnd, (IDC_EDIT_SERVER) );
			pStr = pParams->Server();
			SetWindowText( h, pStr );
			LocalFree(pStr);

			h = GetDlgItem( hWnd, (IDC_EDIT_PORT) );
			ui = pParams->Port();
			pStr = (LPTSTR)LocalAlloc( LPTR, 32 * sizeof(TCHAR) );
			_itot( ui, pStr, 10 );
			SetWindowText( h, pStr );
			LocalFree(pStr);

			if( pParams->MailtoClient() )
			{
				h = GetDlgItem( hWnd, (IDC_CHECK_MAILTO) );
				SendMessage( h, BM_SETCHECK, BST_CHECKED, 0 );
			}
			return TRUE;

		case WM_COMMAND:
			if (LOWORD(wParam) == IDC_BUTTON_RESET_PWD )
			{
				pParams->DeleteCredentials();
				CLiquidNotifier::GetInstance()->Stop();
				if(!CLiquidNotifier::GetInstance()->Start() )
				{
					CTrayIcon::GetInstance()->SetErrorIcon();
				}
				else
				{
					CTrayIcon::GetInstance()->SetNewMailIcon();
				}
			}
			else if (LOWORD(wParam) == IDOK )
			{
				//write out the options
				h = GetDlgItem( hWnd, (IDC_EDIT_SERVER) );
				GetWindowText( h, pTmp, nTmp );
				pParams->Server( pTmp );

				h = GetDlgItem( hWnd, (IDC_EDIT_PORT) );
				GetWindowText( h, pTmp, nTmp );
				pParams->Port( _ttoi( pTmp ) );
				EndDialog(hWnd, LOWORD(wParam));

				h = GetDlgItem( hWnd, (IDC_CHECK_MAILTO) );
				if( SendMessage( h, BM_GETCHECK, 0, 0 ) == BST_CHECKED )
				{
					pParams->SetMailtoClient();
				}

				CLiquidNotifier::GetInstance()->Stop();
				CLiquidNotifier::GetInstance()->Start();

				return TRUE;
			}
			else if( LOWORD(wParam) == IDCANCEL) 
			{
				EndDialog(hWnd, LOWORD(wParam));
				return TRUE;
			}
			break;
	}
	return FALSE;
}

void COptionsDlg::Show()
{
	DialogBox( m_hInstance, MAKEINTRESOURCE(IDD_DIALOG_OPTIONS), NULL, (DLGPROC)COptionsDlg::WndProc );
}