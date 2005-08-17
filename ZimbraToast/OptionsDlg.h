#pragma once

class COptionsDlg
{
	public:
		static COptionsDlg* GetInstance();
		~COptionsDlg(void);

		BOOL Create( HINSTANCE hInst );
		void Show();

	private:
		COptionsDlg(void);
		static LRESULT CALLBACK WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam);

	private:
		static COptionsDlg*	m_pDlg;
		HWND			m_hWnd;
		HINSTANCE		m_hInstance;
};
