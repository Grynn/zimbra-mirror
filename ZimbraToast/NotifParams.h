#pragma once

class CNotifParams
{
	public:
		static CNotifParams* GetInstance();
		~CNotifParams(void);

		LPTSTR  SoundFile();
		void    SoundFile( LPTSTR pszSoundFile );

		LPTSTR	Server();
		void    Server( LPTSTR pSrvr );

		UINT	Port();
		void    Port( UINT port );

		LPTSTR	Username();
		LPTSTR	Password();

		BOOL MailtoClient();
		void SetMailtoClient();

		BOOL PrompForCreds();
		void DeleteCredentials();

	private:
		CNotifParams(void);
		PCREDENTIAL GetCredentials();

	private:
		HKEY m_hkNotif;
		static CNotifParams* m_pParams;

};
