#include "StdAfx.h"
#include ".\notifparams.h"
#include "resource.h"

CNotifParams* CNotifParams::m_pParams = NULL;

CNotifParams::CNotifParams(void)
{
	LONG lResult = RegOpenKey( HKEY_CURRENT_USER, _T("Software\\Zimbra\\Notifier"), &m_hkNotif );
	if( lResult == ERROR_FILE_NOT_FOUND )
	{
		RegCreateKey( HKEY_CURRENT_USER, _T("Software\\Zimbra\\Notifier"), &m_hkNotif);
	}
}

CNotifParams::~CNotifParams(void)
{
	RegCloseKey( m_hkNotif );
}

CNotifParams* CNotifParams::GetInstance()
{
	if( m_pParams == NULL )
	{
		m_pParams = new CNotifParams();
	}
	return m_pParams;
}


void CNotifParams::Server( LPTSTR pSrvr )
{
	DWORD dwBytes = ((int)_tcslen(pSrvr)+1) * sizeof(TCHAR);
	RegSetValueEx( m_hkNotif, _T("Server"), NULL, REG_SZ, (LPBYTE)pSrvr, dwBytes );
}

//make sure to LocalFree the return value
LPTSTR CNotifParams::Server()
{
	DWORD dwType;
	DWORD dwBytes;
	LPTSTR pszServer = NULL;

	LONG lResult = RegQueryValueEx( m_hkNotif, _T("Server"), NULL, &dwType, NULL, &dwBytes );
	if( lResult == ERROR_FILE_NOT_FOUND )
	{
		Server( _T("dogfood.example.zimbra.com") );
		dwBytes = 22 * sizeof(TCHAR);
		dwType = REG_SZ;
	}
	
	pszServer = (LPTSTR)LocalAlloc( LPTR, dwBytes );	
	RegQueryValueEx( m_hkNotif, _T("Server"), NULL, &dwType, (LPBYTE)pszServer, &dwBytes );
	return pszServer;
}

void CNotifParams::SoundFile( LPTSTR pszSoundFile )
{
	DWORD dwBytes = ((int)_tcslen(pszSoundFile)+1) * sizeof(TCHAR);
	RegSetValueEx( m_hkNotif, _T("SoundFile"), NULL, REG_SZ, (LPBYTE)pszSoundFile, dwBytes);
}



LPTSTR CNotifParams::SoundFile()
{
	DWORD dwType;
	DWORD dwBytes;
	LPTSTR pszSoundFile = NULL;
	LONG lResult = RegQueryValueEx( m_hkNotif, _T("SoundFile"), NULL, &dwType, NULL, &dwBytes );
	if( lResult == ERROR_FILE_NOT_FOUND )
	{
		SoundFile( _T("new_mail.wav") );
		dwBytes = 13 * sizeof(TCHAR);
		dwType = REG_SZ;
	}

	pszSoundFile = (LPTSTR)LocalAlloc(LPTR, dwBytes );
	RegQueryValueEx( m_hkNotif, _T("SoundFile"), NULL, &dwType, (LPBYTE)pszSoundFile, &dwBytes );
	return pszSoundFile;
}



void CNotifParams::Port( UINT port )
{
	DWORD dwPort = port;
	RegSetValueEx( m_hkNotif, _T("Port"), NULL, REG_DWORD, (LPBYTE)&dwPort, sizeof(DWORD) );
}

UINT CNotifParams::Port()
{
	DWORD dwType;
	DWORD dwBytes = sizeof(DWORD);
	DWORD dwPort = 7070;

	LONG lResult = RegQueryValueEx( m_hkNotif, _T("Port"), NULL, &dwType, (LPBYTE)&dwPort, &dwBytes );
	if( lResult == ERROR_FILE_NOT_FOUND )
	{
		Port(dwPort);
	}
	return (UINT)dwPort;
}


BOOL CNotifParams::PrompForCreds()
{
	TCHAR pszUsername[CREDUI_MAX_USERNAME_LENGTH+1];
	TCHAR pszPassword[CREDUI_MAX_PASSWORD_LENGTH+1];

	pszUsername[0] = _T('\0');
	pszPassword[0] = _T('\0');

	BOOL fSave = TRUE;
	DWORD dwFlags = CREDUI_FLAGS_ALWAYS_SHOW_UI | CREDUI_FLAGS_EXCLUDE_CERTIFICATES | CREDUI_FLAGS_GENERIC_CREDENTIALS | CREDUI_FLAGS_PERSIST; // | CREDUI_FLAGS_DO_NOT_PERSIST;

	CREDUI_INFO credInfo = {0};
	credInfo.cbSize = sizeof(CREDUI_INFO);
	credInfo.hwndParent = NULL;
	credInfo.pszMessageText = _T("Enter your Zimbra account name and password");
	credInfo.pszCaptionText = _T("Zimbra Mailbox Credentials");
	credInfo.hbmBanner = LoadBitmap( GetModuleHandle(NULL), MAKEINTRESOURCE(IDB_BITMAP_BANNER) );

	DWORD dwRetVal = CredUIPromptForCredentials( 
						&credInfo, 
						_T("LiQ_n0t1f_Cr3dS"),
						NULL, 
						0, 
						pszUsername, CREDUI_MAX_USERNAME_LENGTH, 
						pszPassword, CREDUI_MAX_PASSWORD_LENGTH,
						&fSave,
						dwFlags );

	if( dwRetVal == NO_ERROR )
	{
		//save the username and password
		return TRUE;
	}

	return FALSE;
}


LPTSTR CNotifParams::Username()
{
	PCREDENTIAL pCreds = GetCredentials();
	if( pCreds == NULL )
		return NULL;

	if( pCreds->UserName == NULL )
	{
		DeleteCredentials();
		return NULL;
	}

	LPTSTR pszUsername = (LPTSTR)LocalAlloc( LPTR, (_tcslen(pCreds->UserName)+1) * sizeof(TCHAR) );
	_tcscpy( pszUsername, pCreds->UserName );
	CredFree( pCreds );
	return pszUsername;
}


LPTSTR CNotifParams::Password()
{
	PCREDENTIAL pCreds = GetCredentials();
	if( pCreds == NULL )
		return NULL;
	LPTSTR pszPassword = (LPTSTR)LocalAlloc( LPTR, pCreds->CredentialBlobSize + (1 * sizeof(TCHAR)) );
	memcpy( pszPassword, pCreds->CredentialBlob, pCreds->CredentialBlobSize );
	CredFree( pCreds );
	return pszPassword;
}

PCREDENTIAL CNotifParams::GetCredentials()
{
	PCREDENTIAL pCreds = NULL;
	BOOL bResult = CredRead( _T("LiQ_n0t1f_Cr3dS"), CRED_TYPE_GENERIC, NULL, &pCreds );
	if( !bResult )
	{
		PrompForCreds();
		CredRead( _T("LiQ_n0t1f_Cr3dS"), CRED_TYPE_GENERIC, NULL, &pCreds );
	}
	return pCreds;
}


void CNotifParams::DeleteCredentials()
{
	CredDelete( _T("LiQ_n0t1f_Cr3dS"), CRED_TYPE_GENERIC, 0 );
}


BOOL CNotifParams::MailtoClient()
{
	TCHAR pCmd[1024];
	LONG nCmd = 1023 * sizeof(TCHAR);
	RegQueryValue( HKEY_CLASSES_ROOT, _T("mailto\\shell\\open\\command"), pCmd, &nCmd );

	TCHAR pFilename[MAX_PATH];
	GetModuleFileName( NULL, pFilename, MAX_PATH );

	TCHAR pCmd1[MAX_PATH + 32];
	_stprintf( pCmd1, _T("\"%s\" -mailto %c1"), pFilename, _T('%') );


	return (_tcscmp( pCmd, pCmd1 ) == 0 );
}

//HKEY_CLASSES_ROOT\mailto\shell\open\command
void CNotifParams::SetMailtoClient()
{
	TCHAR pFilename[MAX_PATH];
	GetModuleFileName( NULL, pFilename, MAX_PATH );

	TCHAR pCmd[MAX_PATH + 32];
	_stprintf( pCmd, _T("\"%s\" -mailto %c1"), pFilename, _T('%') );

	int nCmd = ((int)_tcslen( pCmd )+1) * sizeof(TCHAR);
	RegSetValue( HKEY_CLASSES_ROOT, _T("mailto\\shell\\open\\command"), REG_SZ, pCmd, nCmd );
}