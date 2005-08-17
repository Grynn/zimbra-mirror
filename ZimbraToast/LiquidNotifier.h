#pragma once
#include "Mail.h"

class CLiquidNotifier
{
	public:
		BOOL Start();
		BOOL Stop();

		static CLiquidNotifier* GetInstance();

		~CLiquidNotifier();

	private:
		CLiquidNotifier();
		static void CALLBACK OnTimer( PVOID pParam, BOOLEAN bTimeOrWait );
		void OnTimer();

		static DWORD CALLBACK CLiquidNotifier::WorkerThread(LPVOID pParam);
		void CLiquidNotifier::WorkerThread();

		void ProcessFirstRequest(Liquid::Mail::Mailbox& mbx);
		void UpdateMessage(Liquid::Mail::Mailbox& mbx);

	private:
		static CLiquidNotifier* m_pNotifier;
		
		static const int TIMER_DUE_TIME;
		static const int TIMER_PERIOD;

		HANDLE m_hTimerQueue;
		HANDLE m_hTimer;
		HANDLE m_hCompletionPort;
		HANDLE m_hThreadExit;
		HANDLE m_hThreadInit;
		BOOL m_bThreadInit;

};
