#include "MAPISession.h"
#include "MAPIStore.h"
#include "MapiUtils.h"
using namespace Zimbra::MAPI;

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//Exception class
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPISessionException::MAPISessionException(HRESULT hrErrCode, LPCWSTR lpszDescription):
	GenericException(hrErrCode,lpszDescription)
{
	//
}

MAPISessionException::MAPISessionException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile):
	GenericException(hrErrCode,lpszDescription,nLine,strFile)
{
	//
}

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//MAPI Session Class
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

MAPISession::MAPISession():m_Session(NULL)
{

}

MAPISession::~MAPISession()
{
	if( m_Session != NULL )
	{
		m_Session->Logoff( NULL, 0, 0 );
		UlRelease(m_Session);
		m_Session = NULL;
	}
}

HRESULT MAPISession::_mapiLogon(LPWSTR strProfile, DWORD dwFlags, LPMAPISESSION &session)
{
	HRESULT hr= S_OK;
	if (FAILED(hr= MAPILogonEx( 0, strProfile, NULL, dwFlags, &session )))
	{
		throw MAPISessionException(hr,L"_mapiLogon(): MAPILogonEx Failed.",__LINE__,__FILE__);			
	}
	return hr;
}

HRESULT MAPISession::Logon(LPWSTR strProfile)
{
	DWORD dwFlags = MAPI_EXTENDED | MAPI_NEW_SESSION | MAPI_EXPLICIT_PROFILE | MAPI_NO_MAIL | fMapiUnicode;
	return _mapiLogon(strProfile,dwFlags,m_Session);
}

HRESULT MAPISession::Logon(bool bDefaultProfile)
{
	DWORD dwFlags = MAPI_EXTENDED | MAPI_NEW_SESSION | fMapiUnicode;
	if( bDefaultProfile )
		dwFlags |= MAPI_USE_DEFAULT;
	else
		dwFlags |= MAPI_LOGON_UI;

	return _mapiLogon(NULL,dwFlags,m_Session);
}

HRESULT MAPISession::OpenDefaultStore(MAPIStore &Store)
{
	HRESULT		hr			= E_FAIL;
	SBinary		defMsgStoreEID;
	LPMDB		pDefaultMDB = NULL;

	if( m_Session == NULL )
		throw MAPISessionException(hr,L"OpenDefaultStore(): m_mapiSession is NULL.",__LINE__,__FILE__);			
	
	if(FAILED(hr = Zimbra::MAPI::Util::HrMAPIFindDefaultMsgStore( m_Session, defMsgStoreEID)))
		throw MAPISessionException(hr,L"OpenDefaultStore(): HrMAPIFindDefaultMsgStore Failed.",__LINE__,__FILE__);
	Zimbra::Util::ScopedBuffer<BYTE> autoDeletePtr(defMsgStoreEID.lpb);

	hr = m_Session->OpenMsgStore( NULL, defMsgStoreEID.cb, (LPENTRYID)defMsgStoreEID.lpb, NULL,
		MDB_ONLINE | MAPI_BEST_ACCESS | MDB_NO_MAIL | MDB_TEMPORARY | MDB_NO_DIALOG, &pDefaultMDB );
	if( hr == MAPI_E_UNKNOWN_FLAGS )
	{
		if( FAILED(hr = m_Session->OpenMsgStore( NULL, defMsgStoreEID.cb, (LPENTRYID)defMsgStoreEID.lpb, NULL, MAPI_BEST_ACCESS | MDB_NO_MAIL | MDB_TEMPORARY | MDB_NO_DIALOG, &pDefaultMDB )))
			throw MAPISessionException(hr,L"OpenDefaultStore(): OpenMsgStore Failed.",__LINE__,__FILE__);
	}
	
	if( FAILED(hr) )
		throw MAPISessionException(hr,L"OpenDefaultStore(): OpenMsgStore Failed.",__LINE__,__FILE__);
	Store.Initialize(m_Session, pDefaultMDB);
	return S_OK;
}

HRESULT MAPISession::OpenOtherStore(LPMDB OpenedStore,LPWSTR pServerDn, LPWSTR pUserDn,MAPIStore &OtherStore)
{
	HRESULT hr = E_FAIL;
	if( m_Session == NULL )
		throw MAPISessionException(hr,L"OpenDefaultStore(): m_mapiSession is NULL.",__LINE__,__FILE__);			

	//build the dn of the store to open
	LPWSTR pszSuffix = L"/cn=Microsoft Private MDB";
	int iLen = wcslen( pServerDn ) +  wcslen( pszSuffix ) + 1 ;
	LPWSTR pszStoreDN = new WCHAR[ iLen ];
	
	swprintf( pszStoreDN,iLen, L"%s%s", pServerDn, pszSuffix );

	LPMDB pMdb = NULL;
	hr = Zimbra::MAPI::Util::MailboxLogon( m_Session,OpenedStore, pszStoreDN, pUserDn, &pMdb );
	delete [] pszStoreDN;

	if( FAILED(hr) )
		throw MAPISessionException(hr,L"OpenDefaultStore(): MailboxLogon Failed.",__LINE__,__FILE__);

	OtherStore.Initialize(m_Session, pMdb);

	return S_OK;
}

HRESULT MAPISession::OpenAddressBook(LPADRBOOK* ppAddrBook)
{
	HRESULT hr=E_FAIL;
	if(m_Session)
	{
		hr = m_Session->OpenAddressBook(NULL,NULL,AB_NO_DIALOG,ppAddrBook);
	}
	return hr;
}
