#pragma once


typedef struct _ns
{
	BOOL	bHasAttach;
	__int64 nMid;
	LPTSTR pszFrom;
	LPTSTR pszMsg;

	_ns()
	{
		bHasAttach = FALSE;
		nMid = 0;
		pszFrom = NULL;
		pszMsg = NULL;
	}

	_ns( LPTSTR pszFrom, LPTSTR pszMsg, BOOL bCopy )
	{
		bHasAttach = FALSE;
		nMid = 0;
		if( bCopy )
		{
			this->pszFrom = new TCHAR[ _tcslen( pszFrom ) + 1 ];
			_tcscpy( this->pszFrom, pszFrom );

			this->pszMsg = new TCHAR[ _tcslen( pszMsg ) + 1 ];
			_tcscpy( this->pszMsg, pszMsg );
		}
		else
		{
			this->pszFrom = pszFrom;
			this->pszMsg = pszMsg;
		}
	}

	_ns( const _ns& in )
	{
		bHasAttach = in.bHasAttach;
		nMid = in.nMid;
		pszFrom = NULL;
		pszMsg = NULL;
		if( pszFrom != NULL )
		{
			pszFrom = new TCHAR[ wcslen(in.pszFrom) + 1 ];
			_tcscpy( pszFrom, in.pszFrom );
		}
		if( pszMsg != NULL )
		{
			pszMsg = new TCHAR[ wcslen(in.pszMsg) + 1 ];
			_tcscpy( pszMsg, in.pszMsg );
		}
	}

	~_ns()
	{
		if( pszFrom != NULL )
			delete [] pszFrom;
		if( pszMsg != NULL )
			delete [] pszMsg;
	}
}NOTIFYSTRUCT, *PNOTIFYSTRUCT;




class CNotifierWindow
{
	public:
		static CNotifierWindow* GetInstance();
		~CNotifierWindow();

		BOOL Create(HINSTANCE hInstance);

		void SetFrom   ( LPCTSTR pszSender  );
		void SetSummary( LPCTSTR pszSummary );

		void DisplayNotification( PNOTIFYSTRUCT pNS, int nNS );

		static LRESULT CALLBACK WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam);

	private:
		CNotifierWindow();
		void OnTimer();
		void SetPosition();

		enum STATE { FADEIN, SHOWNEXT, FADEOUT };

	private:
		static CNotifierWindow* m_gpWnd;
		HINSTANCE	m_hInstance;
		HWND		m_hWnd;
		HBRUSH		m_bkBrush;

		//child windows
		HWND		m_hWndFrom;
		HWND		m_hWndSummary;
		//HWND		m_hWndLiquidImage;
		//HWND		m_hWndFlagImage;		
		//HWND		m_hWndDeleteImage;
		//HWND		m_hWndDropDown;
		//HWND		m_hWndClose;
		

		INT_PTR		m_timerId;
		STATE		m_nState;
		WORD		m_alpha;

		PNOTIFYSTRUCT	m_pNS;
		int				m_nNS;
		int				m_nCurrNS;



};
