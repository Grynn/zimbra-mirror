#pragma once
#include "Mail.h"

class CZimbraNotifier
{
	public:
		BOOL Start();
		BOOL Stop();

		static CZimbraNotifier* GetInstance();

		~CZimbraNotifier();

	private:
		CZimbraNotifier();
		static void CALLBACK OnTimer( PVOID pParam, BOOLEAN bTimeOrWait );
		void OnTimer();

		static DWORD CALLBACK CZimbraNotifier::WorkerThread(LPVOID pParam);
		void CZimbraNotifier::WorkerThread();

		void ProcessFirstRequest(Zimbra::Mail::Mailbox& mbx);
		void UpdateMessage(Zimbra::Mail::Mailbox& mbx);

	private:
		static CZimbraNotifier* m_pNotifier;
		
		static const int TIMER_DUE_TIME;
		static const int TIMER_PERIOD;

		HANDLE m_hTimerQueue;
		HANDLE m_hTimer;
		HANDLE m_hCompletionPort;
		HANDLE m_hThreadExit;
		HANDLE m_hThreadInit;
		BOOL m_bThreadInit;

};
