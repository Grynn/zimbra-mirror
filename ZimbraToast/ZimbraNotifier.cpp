#include "StdAfx.h"
#include "ZimbraNotifier.h"
#include "NotifParams.h"
#include "TrayIcon.h"
#include "NotifierWnd.h"

//n millis to first timer event
const int CZimbraNotifier::TIMER_DUE_TIME = 1 * 1000;

//millis between timer events
const int CZimbraNotifier::TIMER_PERIOD = 1 * 60 * 1000;

//static single instance of this class
CZimbraNotifier* CZimbraNotifier::m_pNotifier = NULL;


ULONG_PTR REQUEST_EXIT = 1001;
ULONG_PTR REQUEST_UPDATE = 1009;

/**
 *
 *
 *
 **/
CZimbraNotifier* CZimbraNotifier::GetInstance()
{
	if( m_pNotifier == NULL )
	{
		m_pNotifier = new CZimbraNotifier();
	}
	return m_pNotifier;
}


/**
 *
 *
 *
 **/
CZimbraNotifier::CZimbraNotifier(void)
{
	m_hTimerQueue = INVALID_HANDLE_VALUE;
	m_hTimer = INVALID_HANDLE_VALUE;
}


/**
 *
 *
 *
 **/
CZimbraNotifier::~CZimbraNotifier(void)
{
}




/**
 *
 *
 *
 **/
BOOL CZimbraNotifier::Start()
{
	DWORD tid;

	m_hThreadInit = CreateEvent(NULL, TRUE, FALSE, NULL);
	m_hCompletionPort = CreateIoCompletionPort( INVALID_HANDLE_VALUE, NULL, 12, 1 );

	//start the worker thread
	CreateThread( NULL, 0, CZimbraNotifier::WorkerThread, this, 0, &tid );

	//wait for it to be initialized
	WaitForSingleObject( m_hThreadInit, INFINITE );

	//done with this... for now
	CloseHandle(m_hThreadInit);

	//get its response
	if( m_bThreadInit )
	{
			m_hTimerQueue = CreateTimerQueue();
			CreateTimerQueueTimer(  &m_hTimer, 
									m_hTimerQueue, 
									CZimbraNotifier::OnTimer, 
									this, 
									TIMER_DUE_TIME,
									TIMER_PERIOD,
									0 );
			return TRUE;
	}
	
	return FALSE;
}



/**
 *
 *
 *
 **/
BOOL CZimbraNotifier::Stop()
{
	if( m_hTimer != INVALID_HANDLE_VALUE )
		DeleteTimerQueueTimer( m_hTimerQueue, m_hTimer, INVALID_HANDLE_VALUE );
	if( m_hTimerQueue != INVALID_HANDLE_VALUE )
		DeleteTimerQueueEx( m_hTimerQueue, INVALID_HANDLE_VALUE );

	m_hTimerQueue = INVALID_HANDLE_VALUE;
	m_hTimer = INVALID_HANDLE_VALUE;

	m_hThreadExit = CreateEvent( NULL, TRUE, FALSE, NULL );

	//post a completion event to the thread
	PostQueuedCompletionStatus( m_hCompletionPort, 0, REQUEST_EXIT, NULL );	

	//wait for the thread to die
	WaitForSingleObject(m_hThreadExit, INFINITE);
	
	CloseHandle( m_hThreadExit );
	CloseHandle( m_hCompletionPort );

	return TRUE;
}



/**
 *
 *
 *
 **/
void CZimbraNotifier::OnTimer( PVOID pParam, BOOLEAN bTimeOrWait )
{
	
	CZimbraNotifier* pLN = (CZimbraNotifier*)pParam;
	pLN->OnTimer();
}



/**
 *
 *
 *
 **/
void CZimbraNotifier::OnTimer()
{
	//post a completion event to the worker thread
	PostQueuedCompletionStatus( m_hCompletionPort, 0, REQUEST_UPDATE, NULL );	
}


DWORD CZimbraNotifier::WorkerThread( LPVOID pParam )
{
	CZimbraNotifier* pLN = (CZimbraNotifier*)pParam;
	pLN->WorkerThread();
	return 0;
}

void CZimbraNotifier::WorkerThread()
{
	CoInitialize(NULL);

	//initialization stuff
	BOOL bFirst = TRUE;

	LPTSTR pServer = CNotifParams::GetInstance()->Server();
	UINT nPort = CNotifParams::GetInstance()->Port();

	//auth to zimbra
	Zimbra::Mail::Mailbox mbx( pServer, nPort );

	LocalFree(pServer);

	BOOL done = FALSE;
	while( !done )
	{		
		LPTSTR pUser = CNotifParams::GetInstance()->Username();
		if( pUser == NULL )
		{
			//set the return val
			m_bThreadInit = FALSE;
			SetEvent(m_hThreadInit);
			return;
		}

		LPTSTR pPass = CNotifParams::GetInstance()->Password();
		try
		{
			mbx.Logon( pUser, pPass );
			LocalFree(pUser);
			LocalFree(pPass);
			done = TRUE;
		}
		catch( Zimbra::ZimbraException& le )
		{
			if( !CNotifParams::GetInstance()->PrompForCreds() )
			{
				LocalFree(pUser);
				LocalFree(pPass);
				m_bThreadInit = FALSE;
				SetEvent(m_hThreadInit);
				return;
			}
		}		
	}

	m_bThreadInit = TRUE;
	SetEvent(m_hThreadInit);

	//wait for completion events...
	while( true )
	{
		DWORD dwBytes;
		ULONG_PTR key;
		LPOVERLAPPED pOv;
		
		GetQueuedCompletionStatus( m_hCompletionPort, &dwBytes, &key, &pOv, INFINITE );
		if( key == REQUEST_EXIT )
		{
			break;
		}

		
		if( bFirst )
		{
			bFirst = FALSE;
			ProcessFirstRequest(mbx);
		}
		else
		{
			UpdateMessage(mbx);				
		}
	}

	SetEvent(m_hThreadExit);
}


void CZimbraNotifier::ProcessFirstRequest(Zimbra::Mail::Mailbox& mbx)
{
	IXMLDOMDocument2* pResponseDoc = NULL;
	try
	{
		Zimbra::Rpc::PingRequest request;
		mbx.ProcessRequest( request, FALSE, pResponseDoc );

		WCHAR pMsg[128];
		LPWSTR pVal = NULL;
		Zimbra::Util::XmlUtil::FindNodeAttributeValue( pResponseDoc, L"/soap:Envelope/soap:Header/l:context/l:refresh/l:folder/l:folder[@id=2]", _T("u"), pVal );
		if( pVal == NULL )
		{
			wcscpy( pMsg, L"0 unread messages" );
			CTrayIcon::GetInstance()->SetNoMailIcon();
		}
		else
		{
			CTrayIcon::GetInstance()->SetNewMailIcon();
			swprintf( pMsg, L"%s unread messages", pVal );
			delete [] pVal;
		}
		CTrayIcon::GetInstance()->SetToolTip( pMsg );
		SafeRelease(pResponseDoc);
	}catch(...)
	{
		CTrayIcon::GetInstance()->SetToolTip( L"Error communicating with zimbra" );
		CTrayIcon::GetInstance()->SetErrorIcon();
		SafeRelease(pResponseDoc);		
	}

	
}

LPWSTR pNoSender = L"<no sender>";

LPWSTR MakeFrom( LPWSTR pszAddress, LPWSTR pszDisplay, LPWSTR pszPersonal )
{
	size_t len = 0;
	LPWSTR pszRet = NULL;

	if( pszPersonal != NULL && (len = wcslen(pszPersonal)) > 0 )
	{
		pszRet = new WCHAR[ len+1 ];
		wcscpy( pszRet, pszPersonal);
	}
	else if( pszAddress != NULL && (len = wcslen(pszAddress)) > 0 )
	{
		pszRet = new WCHAR[len+1];
		wcscpy( pszRet, pszAddress );
	}
	else
	{
		len = wcslen( pNoSender );
		pszRet = new WCHAR[ len+1 ];
		wcscpy( pszRet, pNoSender );
	}
	return pszRet;	
}


LPWSTR pNoSubject = L"<no subject>";

LPWSTR MakeMsg( LPWSTR pszSubject, LPWSTR pszFragment )
{
	size_t len = 0;
	if( pszSubject != NULL )
		len += wcslen(pszSubject);
	else
		len += wcslen(pNoSubject);

	if( pszFragment != NULL )
		len += wcslen(pszFragment) + 3;

	LPWSTR pRet = new WCHAR[ len + 1 ];
	
	if( pszSubject != NULL )
		wcscpy( pRet, pszSubject );
	else 
		wcscpy( pRet, pNoSubject );

	if( pszFragment )
	{
		wcscat( pRet, L" - " );
		wcscat( pRet, pszFragment );
	}
	return pRet;
}



void CZimbraNotifier::UpdateMessage(Zimbra::Mail::Mailbox& mbx)
{
	IXMLDOMDocument2* pResponseDoc = NULL;
	try
	{
		Zimbra::Rpc::PingRequest request;
		mbx.ProcessRequest( request, pResponseDoc );
		
		LPWSTR pszCount = NULL;

		//how many new messages?
		Zimbra::Util::XmlUtil::FindNodeAttributeValue( pResponseDoc, L"/soap:Envelope/soap:Header/l:context/l:notify/l:modified/l:folder", L"u", pszCount );

		if( pszCount != NULL )
		{
			int count = _wtoi( pszCount );
			if( count == 0 )
				CTrayIcon::GetInstance()->SetNoMailIcon();
			else
				CTrayIcon::GetInstance()->SetNewMailIcon();

			WCHAR pMsg[128];
			swprintf( pMsg, L"%s unread messages", pszCount );
			delete [] pszCount;
			CTrayIcon::GetInstance()->SetToolTip( pMsg );
		}
		
		//what was created
		IXMLDOMNodeList* pResultList = NULL;
		pResponseDoc->selectNodes( _bstr_t(L"/soap:Envelope/soap:Header/l:context/l:notify/l:created/l:m"), &pResultList );
		SafeRelease(pResponseDoc);

		if( pResultList == NULL )
		{
			return;
		}

		long nMessages = 0;
		pResultList->get_length(&nMessages);

		if( nMessages <= 0 )
		{
			return;
		}

		NOTIFYSTRUCT* pNotifs = new NOTIFYSTRUCT[ nMessages ];
		NOTIFYSTRUCT* pCurr = pNotifs;
		int nNotifs = 0;

		for( long i = 0; i < nMessages; i++ )
		{
			IXMLDOMNode* pNode = NULL;
			pResultList->get_item( i, &pNode );

			//if the node has flag with d or s, ignore it
			LPWSTR pFlags = NULL;
			Zimbra::Util::XmlUtil::GetSingleAttribute( pNode, L"f", pFlags );
			BOOL bSkip = FALSE;
			if( pFlags != NULL )
			{
				for( unsigned int i = 0; i < wcslen(pFlags); i++ )
				{
					if( pFlags[i] == L'd' || pFlags[i] == L'D' ||
						pFlags[i] == L's' || pFlags[i] == L'S' )
					{
						bSkip = TRUE;
						break;
					}
				}
			}

			if( bSkip )
			{
				SafeRelease( pNode );
				continue;
			}


			
			LPWSTR pszId = NULL;
			LPWSTR pszAddress = NULL;
			LPWSTR pszDisplay = NULL;
			LPWSTR pszPersonal = NULL;
			LPWSTR pszSubject = NULL;
			LPWSTR pszFragment = NULL;

			//message id
			Zimbra::Util::XmlUtil::GetSingleAttribute( pNode, L"id", pszId );

			//from address
			Zimbra::Util::XmlUtil::FindNodeAttributeValue( pNode, L"./l:e[@t=\"f\"]", L"a", pszAddress );

			//from display
			Zimbra::Util::XmlUtil::FindNodeAttributeValue( pNode, L"./l:e[@t=\"f\"]", L"d", pszDisplay );

			//from personal
			Zimbra::Util::XmlUtil::FindNodeAttributeValue( pNode, L"./l:e[@t=\"f\"]", L"p", pszPersonal );

			//subject
			Zimbra::Util::XmlUtil::FindNodeValue( pNode, L"./l:su", pszSubject );

			//fragment
			Zimbra::Util::XmlUtil::FindNodeValue( pNode, L"./l:f", pszFragment );

			//create and append the correct notify struct
			pCurr->bHasAttach = 0;
			pCurr->nMid = 0;
			pCurr->pszFrom = MakeFrom( pszAddress, pszDisplay, pszPersonal );
			pCurr->pszMsg = MakeMsg( pszSubject, pszFragment );

			Zimbra::Util::SafeDelete(pszId);
			Zimbra::Util::SafeDelete(pszAddress);
			Zimbra::Util::SafeDelete(pszDisplay);
			Zimbra::Util::SafeDelete(pszPersonal);
			Zimbra::Util::SafeDelete(pszSubject);
			Zimbra::Util::SafeDelete(pszFragment);

			SafeRelease(pNode);
			pCurr++;
			nNotifs++;
		}

		SafeRelease(pResultList);
		if( nNotifs != 0 )
		{
			CNotifierWindow::GetInstance()->DisplayNotification( pNotifs, nNotifs );
		}
		else
		{
			delete [] pNotifs;
		}
		
	}
	catch(...)
	{
		CTrayIcon::GetInstance()->SetToolTip( L"Error communicating with zimbra" );
		CTrayIcon::GetInstance()->SetErrorIcon();
		SafeRelease(pResponseDoc);
	}
}
