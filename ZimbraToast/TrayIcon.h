#pragma once

class CTrayIcon
{
	public:
		static CTrayIcon* GetInstance();
		~CTrayIcon(void);

		 BOOL Create(HINSTANCE hInstance);
		 void Destroy();

		 void SetNoMailIcon();
		 void SetNewMailIcon();
		 void SetErrorIcon();

		 void SetToolTip( LPCTSTR pStr );

	private:
		CTrayIcon(void);
		static LRESULT CALLBACK WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam);

	private:
		HINSTANCE			m_hInstance;
		HWND				m_hWnd;
		HMENU				m_hMenu;
		HICON				m_hErrorIcon;
		HICON				m_hMailIcon;
		HICON				m_hNewMailIcon;

		static CTrayIcon*	m_pTrayIcon;
		
};
