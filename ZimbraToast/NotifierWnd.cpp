#include "stdafx.h"
#include "resource.h"
#include "notifierwnd.h"
#include "NotifParams.h"

#define BKCOLOR		RGB( 0xCD, 0xDA, 0xF7)
#define MAX_ALPHA	220


CNotifierWindow* CNotifierWindow::m_gpWnd = NULL;


CNotifierWindow* CNotifierWindow::GetInstance()
{
	if( m_gpWnd == NULL )
	{
		m_gpWnd = new CNotifierWindow();
	}
	return m_gpWnd;
}

/**
 *
 *
 *
 **/
CNotifierWindow::CNotifierWindow()
{
	m_hInstance		= 0;
	m_hWnd			= 0;
	m_timerId		= 0;
	m_bkBrush = CreateSolidBrush( BKCOLOR );

	m_pNS = NULL;
	m_nNS = 0;

}


/**
 *
 *
 *
 **/
CNotifierWindow::~CNotifierWindow()
{
	DeleteObject( m_bkBrush );
}



/**
 *
 *
 *
 **/
void CNotifierWindow::SetFrom( LPCTSTR pszSender )
{
	SetWindowText( m_hWndFrom, pszSender );
}




/**
 *
 *
 *
 **/
void CNotifierWindow::SetSummary( LPCTSTR pszSummary )
{
	SetWindowText( m_hWndSummary, pszSummary );
}




/**
 *
 *
 *
 **/
BOOL CNotifierWindow::Create(HINSTANCE hInstance)
{
	m_hInstance = hInstance;
	
	DWORD dwExStyle = WS_EX_TOOLWINDOW | WS_EX_TOPMOST;
	DWORD dwStyle = WS_POPUP | WS_BORDER | WS_VISIBLE;

	m_hWnd = CreateDialog( m_hInstance, MAKEINTRESOURCE(IDC_ZIMBRA_NOTIFIER_WND), NULL, (DLGPROC)CNotifierWindow::WndProc );
	if( m_hWnd == 0 )
		return FALSE;
	{
		DWORD exStyle = GetWindowLongPtr( m_hWnd, GWL_EXSTYLE ) | WS_EX_LAYERED;
		SetWindowLongPtr( m_hWnd, GWL_EXSTYLE, exStyle );
		SetLayeredWindowAttributes( m_hWnd, RGB(0,0,0), 0, LWA_ALPHA );
	}

	m_hWndFrom = GetDlgItem( m_hWnd, IDC_FROM );
	if( m_hWndFrom == 0 )
		return FALSE;
	{
		LOGFONT lf = {0};
		lf.lfHeight = -MulDiv(8, GetDeviceCaps(GetDC(m_hWndFrom), LOGPIXELSY), 72);
		lf.lfWeight = FW_BOLD;
		lf.lfCharSet = DEFAULT_CHARSET;
		lf.lfWidth = OUT_DEFAULT_PRECIS;
		lf.lfClipPrecision = CLIP_DEFAULT_PRECIS;
		lf.lfQuality = CLEARTYPE_QUALITY;
		lf.lfPitchAndFamily = DEFAULT_PITCH | FF_DONTCARE;
		HFONT hFont = CreateFontIndirect( &lf );
		SendMessage( m_hWndFrom, WM_SETFONT, (WPARAM)hFont, TRUE );
	}
	
	m_hWndSummary = GetDlgItem( m_hWnd, IDC_SUMMARY );
	if( m_hWndSummary == 0 )
		return FALSE;
	{
	}

	SetFrom   ( _T("") );
	SetSummary( _T("") );

	return 1;
}





/**
 *
 *
 *
 **/
LRESULT CALLBACK CNotifierWindow::WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
	switch (message) 
	{
		case WM_CTLCOLORSTATIC:
			SetBkColor( (HDC)wParam, BKCOLOR );
		case WM_CTLCOLORDLG:
			return (INT_PTR)CNotifierWindow::GetInstance()->m_bkBrush;
		case WM_TIMER:
			((CNotifierWindow*)wParam)->OnTimer();
			break;
		default:
			return 0;//DefWindowProc(hWnd, message, wParam, lParam);
	}
	return 0;
}





/**
 *
 *
 *
 **/
void CNotifierWindow::OnTimer()
{
	if( m_nState == STATE::FADEIN )
	{
		if( m_alpha == 0 )
		{
			SetPosition();
			SetFrom( m_pNS->pszFrom );
			SetSummary( m_pNS->pszMsg );
			ShowWindow( m_hWnd, SW_SHOWNA );
		}
		m_alpha += 2;
		SetLayeredWindowAttributes( m_hWnd, RGB(0,0,0), (BYTE)m_alpha, LWA_ALPHA );
		RedrawWindow( m_hWnd, NULL,  NULL, 0 );
		if( m_alpha >= MAX_ALPHA )
		{
			KillTimer( m_hWnd, m_timerId );
			m_timerId = 0;
			m_nCurrNS++;
			DWORD timeout = 0;
			if( m_nCurrNS >= m_nNS )
			{
				m_nState = STATE::FADEOUT;
				timeout = 3 * 1000;
			}
			else
			{
				m_nState = STATE::SHOWNEXT;
				timeout = 3 * 1000;
			}
			m_timerId = SetTimer( m_hWnd, (WPARAM)this, timeout, NULL );
		}
	}
	else if( m_nState == STATE::SHOWNEXT )
	{
		SetFrom( m_pNS[m_nCurrNS].pszFrom );
		SetSummary( m_pNS[m_nCurrNS].pszMsg );
		m_nCurrNS++;
		if( m_nCurrNS >= m_nNS )
		{
			m_nState = STATE::FADEOUT;
		}
	}
	else if( m_nState == STATE::FADEOUT )
	{
		if( m_alpha == MAX_ALPHA )
		{
			KillTimer( m_hWnd, m_timerId );
			m_timerId = SetTimer( m_hWnd, (WPARAM)this, 10, NULL );
		}
		m_alpha -= 1;
		SetLayeredWindowAttributes( m_hWnd, RGB(0,0,0), (BYTE)m_alpha, LWA_ALPHA );
		RedrawWindow( m_hWnd, NULL,  NULL, 0);
		if( m_alpha <= 1 )
		{
			KillTimer( m_hWnd, m_timerId );
			m_timerId = 0;
			ShowWindow( m_hWnd, SW_HIDE );
			delete [] m_pNS;
			m_pNS = NULL;
			m_nNS = 0;
		}	
	}
}




/**
 *
 *
 *
 **/
void CNotifierWindow::SetPosition()
{
	APPBARDATA appBarData = {0};
	appBarData.cbSize = sizeof(APPBARDATA);
	SHAppBarMessage( ABM_GETTASKBARPOS, &appBarData );

	RECT rect;
	GetWindowRect( m_hWnd, &rect );
	int rW = rect.right - rect.left;
	int rH = rect.bottom - rect.top;
	
	RECT notifRect;
	if( appBarData.uEdge == ABE_LEFT )
	{
		notifRect.top  = appBarData.rc.bottom - rH - 1;
		notifRect.left = appBarData.rc.right + 1;
	}
	else if( appBarData.uEdge == ABE_TOP )
	{
		notifRect.top  = appBarData.rc.bottom + 1;
		notifRect.left = appBarData.rc.right - rW - 1;
	}
	else if( appBarData.uEdge == ABE_RIGHT )
	{
		notifRect.top  = appBarData.rc.bottom - rH - 1;
		notifRect.left = appBarData.rc.left - rW - 1;
	}
	else
	{
		notifRect.top  = appBarData.rc.top - rH - 1;
		notifRect.left = appBarData.rc.right - rW - 1;
	}

	MoveWindow( m_hWnd, notifRect.left, notifRect.top, rW, rH, FALSE );
}




/**
 *
 *
 *
 **/
void CNotifierWindow::DisplayNotification( PNOTIFYSTRUCT pNS, int nNS )
{

	//LPTSTR pszSoundFile = CNotifParams::GetInstance()->SoundFile();
	//PlaySound( pszSoundFile, NULL, SND_FILENAME | SND_ASYNC );
	//LocalFree(pszSoundFile);
	PlaySound( L"MailBeep", NULL, SND_ALIAS | SND_ASYNC );

	m_nCurrNS = 0;
	m_nNS = nNS;
	m_pNS = pNS;

	m_alpha = 0;
	m_nState = STATE::FADEIN;
	m_timerId = SetTimer( m_hWnd, (UINT_PTR)this, 10, NULL ); 
};
