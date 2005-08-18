#include "stdafx.h"
#include "Rpc.h"





/*********************************************************************************************************************
 *********************************************************************************************************************
	Constants
 *********************************************************************************************************************
 *********************************************************************************************************************/


LPCWSTR Zimbra::Rpc::TAGS::Context				= L"context";
LPCWSTR Zimbra::Rpc::TAGS::AuthToken			= L"authToken";
LPCWSTR Zimbra::Rpc::TAGS::SessionId			= L"sessionId";

LPCWSTR Zimbra::Rpc::TAGS::AuthRequest			= L"AuthRequest";
LPCWSTR Zimbra::Rpc::TAGS::Account				= L"account";
LPCWSTR Zimbra::Rpc::TAGS::Password				= L"password";

LPCWSTR Zimbra::Rpc::TAGS::BatchRequest			= L"BatchRequest";
LPCWSTR Zimbra::Rpc::TAGS::GetFolderRequest		= L"GetFolderRequest";
LPCWSTR Zimbra::Rpc::TAGS::GetTagRequest		= L"GetTagRequest";
LPCWSTR Zimbra::Rpc::TAGS::CreateFolderRequest	= L"CreateFolderRequest";
LPCWSTR Zimbra::Rpc::TAGS::CreateTagRequest		= L"CreateTagRequest";
LPCWSTR Zimbra::Rpc::TAGS::GetContactsRequest	= L"GetContactsRequest";
LPCWSTR Zimbra::Rpc::TAGS::CreateContactRequest	= L"CreateContactRequest";
LPCWSTR Zimbra::Rpc::TAGS::AddMessageRequest	= L"AddMsgRequest";
LPCWSTR Zimbra::Rpc::TAGS::PingRequest			= L"NoOpRequest"; //authenticated nothing!

LPCWSTR Zimbra::Rpc::TAGS::Folder				= L"folder";
LPCWSTR Zimbra::Rpc::TAGS::Tag					= L"tag";
LPCWSTR Zimbra::Rpc::TAGS::Attribute			= L"a";
LPCWSTR Zimbra::Rpc::TAGS::Contact				= L"cn";
LPCWSTR Zimbra::Rpc::TAGS::Message				= L"m";
LPCWSTR Zimbra::Rpc::TAGS::Content				= L"content";

//the bstr version

_bstr_t Zimbra::Rpc::TAGS::bContext				( Zimbra::Rpc::TAGS::Context				);
_bstr_t Zimbra::Rpc::TAGS::bAuthToken			( Zimbra::Rpc::TAGS::AuthToken				);
_bstr_t Zimbra::Rpc::TAGS::bSessionId			( Zimbra::Rpc::TAGS::SessionId				);

_bstr_t Zimbra::Rpc::TAGS::bAuthRequest			( Zimbra::Rpc::TAGS::AuthRequest			);
_bstr_t Zimbra::Rpc::TAGS::bAccount				( Zimbra::Rpc::TAGS::Account				);
_bstr_t Zimbra::Rpc::TAGS::bPassword			( Zimbra::Rpc::TAGS::Password				);

_bstr_t Zimbra::Rpc::TAGS::bBatchRequest		( Zimbra::Rpc::TAGS::BatchRequest			);
_bstr_t Zimbra::Rpc::TAGS::bGetFolderRequest	( Zimbra::Rpc::TAGS::GetFolderRequest		);
_bstr_t Zimbra::Rpc::TAGS::bGetTagRequest		( Zimbra::Rpc::TAGS::GetTagRequest			);
_bstr_t Zimbra::Rpc::TAGS::bCreateFolderRequest	( Zimbra::Rpc::TAGS::CreateFolderRequest	);
_bstr_t Zimbra::Rpc::TAGS::bCreateTagRequest	( Zimbra::Rpc::TAGS::CreateTagRequest		);
_bstr_t Zimbra::Rpc::TAGS::bGetContactsRequest	( Zimbra::Rpc::TAGS::GetContactsRequest		);
_bstr_t Zimbra::Rpc::TAGS::bCreateContactRequest( Zimbra::Rpc::TAGS::CreateContactRequest	);
_bstr_t Zimbra::Rpc::TAGS::bAddMessageRequest	( Zimbra::Rpc::TAGS::AddMessageRequest		);
_bstr_t Zimbra::Rpc::TAGS::bPingRequest			( Zimbra::Rpc::TAGS::PingRequest			);

_bstr_t Zimbra::Rpc::TAGS::bFolder				( Zimbra::Rpc::TAGS::Folder					);
_bstr_t Zimbra::Rpc::TAGS::bTag					( Zimbra::Rpc::TAGS::Tag					);
_bstr_t Zimbra::Rpc::TAGS::bAttribute			( Zimbra::Rpc::TAGS::Attribute				);
_bstr_t Zimbra::Rpc::TAGS::bContact				( Zimbra::Rpc::TAGS::Contact				);
_bstr_t Zimbra::Rpc::TAGS::bMessage				( Zimbra::Rpc::TAGS::Message				);
_bstr_t Zimbra::Rpc::TAGS::bContent				( Zimbra::Rpc::TAGS::Content				);


LPCWSTR Zimbra::Rpc::ATTRIBUTES::XmlNamespace	= L"xmlns";
LPCWSTR Zimbra::Rpc::ATTRIBUTES::By				= L"by";
LPCWSTR Zimbra::Rpc::ATTRIBUTES::ParentFolderId	= L"l";
LPCWSTR Zimbra::Rpc::ATTRIBUTES::Name			= L"name";
LPCWSTR Zimbra::Rpc::ATTRIBUTES::AttributeName   = L"n";
LPCWSTR Zimbra::Rpc::ATTRIBUTES::Color			= L"color";
LPCWSTR Zimbra::Rpc::ATTRIBUTES::Id				= L"id";
LPCWSTR Zimbra::Rpc::ATTRIBUTES::TagIds			= L"t";
LPCWSTR Zimbra::Rpc::ATTRIBUTES::Flags			= L"f";

_bstr_t Zimbra::Rpc::ATTRIBUTES::bXmlNamespace		( Zimbra::Rpc::ATTRIBUTES::XmlNamespace		);
_bstr_t Zimbra::Rpc::ATTRIBUTES::bBy				( Zimbra::Rpc::ATTRIBUTES::By				);
_bstr_t Zimbra::Rpc::ATTRIBUTES::bParentFolderId	( Zimbra::Rpc::ATTRIBUTES::ParentFolderId	);
_bstr_t Zimbra::Rpc::ATTRIBUTES::bName				( Zimbra::Rpc::ATTRIBUTES::Name				);
_bstr_t Zimbra::Rpc::ATTRIBUTES::bAttributeName		( Zimbra::Rpc::ATTRIBUTES::AttributeName	);
_bstr_t Zimbra::Rpc::ATTRIBUTES::bColor				( Zimbra::Rpc::ATTRIBUTES::Color			);
_bstr_t Zimbra::Rpc::ATTRIBUTES::bId				( Zimbra::Rpc::ATTRIBUTES::Id				);
_bstr_t Zimbra::Rpc::ATTRIBUTES::bTagIds			( Zimbra::Rpc::ATTRIBUTES::TagIds			);
_bstr_t Zimbra::Rpc::ATTRIBUTES::bFlags				( Zimbra::Rpc::ATTRIBUTES::Flags			);


LPCWSTR Zimbra::Rpc::URNS::Zimbra				= L"urn:zimbra";
LPCWSTR Zimbra::Rpc::URNS::ZimbraAccount		= L"urn:zimbraAccount";
LPCWSTR Zimbra::Rpc::URNS::ZimbraMail			= L"urn:zimbraMail";

_variant_t Zimbra::Rpc::URNS::vZimbra				( Zimbra::Rpc::URNS::Zimbra			);
_variant_t Zimbra::Rpc::URNS::vZimbraAccount		( Zimbra::Rpc::URNS::ZimbraAccount	);
_variant_t Zimbra::Rpc::URNS::vZimbraMail			( Zimbra::Rpc::URNS::ZimbraMail		);



/*********************************************************************************************************************
 *********************************************************************************************************************
	Connection
 *********************************************************************************************************************
 *********************************************************************************************************************/
using namespace Zimbra::Rpc;


void CALLBACK connectionMonitor( HINTERNET hInternet, DWORD_PTR , DWORD dwInternetStatus, LPVOID , DWORD  )
{
	switch( dwInternetStatus )
	{
		case WINHTTP_CALLBACK_STATUS_RESOLVING_NAME:
			TRACE( _T("(CM): (0x%X) resolving name"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_NAME_RESOLVED:
			TRACE( _T("(CM): (0x%X) name resolved"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_CONNECTING_TO_SERVER:
			TRACE( _T("(CM): (0x%X) connecting to server"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_CONNECTED_TO_SERVER:
			TRACE( _T("(CM): (0x%X) connected to server"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_SENDING_REQUEST:
			TRACE( _T("(CM): (0x%X) sending request"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_REQUEST_SENT:
			TRACE( _T("(CM): (0x%X) request sent"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_RECEIVING_RESPONSE:
			TRACE( _T("(CM): (0x%X) receiving response"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_RESPONSE_RECEIVED:
			TRACE( _T("(CM): (0x%X) response received"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_CLOSING_CONNECTION:
			TRACE( _T("(CM): (0x%X) closing connection"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_CONNECTION_CLOSED:
			TRACE( _T("(CM): (0x%X) connection closed"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_DETECTING_PROXY:
			TRACE( _T("(CM): (0x%X) detecting proxy"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_REDIRECT:
			TRACE( _T("(CM): (0x%X) redirecting"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_INTERMEDIATE_RESPONSE:
			TRACE( _T("(CM): (0x%X) intermediate response"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_DATA_AVAILABLE:
			TRACE( _T("(CM): (0x%X) data available"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_READ_COMPLETE:
			TRACE( _T("(CM): (0x%X) read complete"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_WRITE_COMPLETE:
			TRACE( _T("(CM): (0x%X) write complete"), hInternet );
			break;
		case WINHTTP_CALLBACK_STATUS_SENDREQUEST_COMPLETE:
			TRACE( _T("(CM): (0x%X) send request complete"), hInternet );
			break;
	}
}

/**
 *
 *  Constrcutor initializes instance.  Proxy connects to server only when necessary.
 *
 **/
Connection::Connection( LPWSTR pServer, unsigned int nPort )
{
	//save a copy of the server name
	_pServer = new WCHAR[ wcslen(pServer) + 1 ];
	wcscpy( _pServer, pServer );

	//save the port
	_nPort = nPort;

	_hWHSession =	WinHttpOpen( L"Zimbra Systems Notifier", 
								WINHTTP_ACCESS_TYPE_DEFAULT_PROXY, 
								WINHTTP_NO_PROXY_NAME, 
								WINHTTP_NO_PROXY_BYPASS, 
								0 );

	DWORD dwTimeout = 1000 * 60 * 5; //5 minute timeout
	WinHttpSetTimeouts( _hWHSession, 0, dwTimeout, dwTimeout, dwTimeout );
	//WinHttpSetStatusCallback( _hWHSession, connectionMonitor, WINHTTP_CALLBACK_FLAG_ALL_NOTIFICATIONS, NULL );

	_hConnection = 0;
	_hRequest = 0;
}


/**
 *
 *  Destructor frees all resources in use by this object
 *
 **/
Connection::~Connection()
{
	if( _pServer != NULL )
	{
		delete [] _pServer;
		_pServer = NULL;
	}

	WinHttpCloseHandle(_hRequest);
	WinHttpCloseHandle(_hConnection);
	WinHttpCloseHandle(_hWHSession);
	_hWHSession = 0;

	_nPort = 0;
}






/**
 *
 *  POSTS the request in pRequest to the Zimbra Soap Server and returns the response
 *  as a utf-8 string (assumes server responds in utf-8)
 *
 **/
void Connection::SendRequest( LPCWSTR pRequest, LPSTR& pResponseStr )
{
	DWORD nResponse = 0;

	//add the content-type header to the reqeust
	static WCHAR pAdditionalHeaders[] = { L"Content-Type: application/soap+xml; charset=utf-8" };

	//make sure we are connected
	Connect();

	//how many bytes in the UTF-8 version?
	int nPostData = WideCharToMultiByte( CP_UTF8, 0, pRequest, -1, NULL, 0, NULL, NULL );
	
	//create and initialize the utf-8 string
	LPSTR pData = new CHAR[nPostData];

	//get the utf-8 string
	nPostData = WideCharToMultiByte( CP_UTF8, 0, pRequest, -1, pData, nPostData, NULL, NULL );

	//send the utf-8 version of the request (don't send the null character)
	if( !WinHttpSendRequest( _hRequest, pAdditionalHeaders, (DWORD)-1, (LPVOID)pData, (DWORD)(nPostData-1), (DWORD)(nPostData-1), NULL ) )
	{
		DWORD error = GetLastError();
		delete [] pData;
		pData = NULL;
		throw RpcException( error );
	}

	//get the response
	WinHttpReceiveResponse( _hRequest, NULL );

	//how many bytes for the response? (who gives a s**t?)
	WinHttpQueryDataAvailable( _hRequest, &nResponse );

	//get the content length of the response
	WCHAR pContentLength[1024] = {0};
	DWORD nCLBytes = 1024 * sizeof(WCHAR);
	WinHttpQueryHeaders( _hRequest,	WINHTTP_QUERY_CONTENT_LENGTH, WINHTTP_HEADER_NAME_BY_INDEX, (LPVOID)pContentLength, &nCLBytes, WINHTTP_NO_HEADER_INDEX );
	
	int nTotalContentLength = nResponse; //if no content-length header, we may be screwed.

	if( pContentLength != NULL )
		nTotalContentLength = _wtoi(pContentLength)	;

	//create the response buffer (I will add 1 zero for you)
	pResponseStr = new CHAR[nTotalContentLength + 1];
	ZeroMemory(pResponseStr, nTotalContentLength+1);

	//read in the response
	DWORD nBytesRead = 0;
	WinHttpReadData( _hRequest, (LPVOID)pResponseStr, nTotalContentLength, &nBytesRead );

	delete [] pData;
}



/**
 *
 *  Send the request to the server
 *
 **/
void Connection::SendRequest( Request& req, LPSTR& pResponseStr )
{
	BSTR requestStr;
	req.GetDocument()->get_xml(&requestStr);
	__try
	{
		SendRequest( requestStr, pResponseStr );
	}
	__finally
	{
		SysFreeString(requestStr);
	}
}


/**
 *
 *  Send the request to the server
 *
 **/
void Connection::SendRequest( Request& req, IXMLDOMDocument2*& pResponseXml )
{
	//the result string
	LPSTR pResponseStr;

	//make the auth request
	SendRequest( req, pResponseStr );

	//create the response doc
	CoCreateInstance(CLSID_DOMDocument2, NULL, CLSCTX_INPROC_SERVER, IID_IXMLDOMDocument2, (void**)&pResponseXml );

	//load the string into the xml document
	VARIANT_BOOL bResult;

	int len = (int)strlen(pResponseStr);

	//convert from utf 8 response to utf 16
	int nResponseLen = MultiByteToWideChar( CP_UTF8, 0, pResponseStr, len, NULL, 0 );
	LPWSTR pWResponseStr = new WCHAR[ nResponseLen + 1 ];
	ZeroMemory( pWResponseStr, (nResponseLen + 1) * sizeof(WCHAR) );
	MultiByteToWideChar( CP_UTF8, 0, pResponseStr, len, pWResponseStr, nResponseLen );

	pResponseXml->loadXML( ( pWResponseStr ), &bResult );
	
	delete [] pWResponseStr;

	//trash the response buffer
	delete [] pResponseStr;	
}




/**
 *
 *  If doesn't exist, create a WinHttpConnection and WinHttpRequest 
 *  to the Zimbra soap server
 *
 **/
void Connection::Connect()
{
	//close the last request

	WinHttpCloseHandle( _hRequest );
	
	//make sure we have a connection
	WinHttpCloseHandle( _hConnection );

	WinHttpCloseHandle( _hWHSession );

	_hWHSession =	WinHttpOpen( L"Zimbra Systems Notifier", 
								WINHTTP_ACCESS_TYPE_DEFAULT_PROXY, 
								WINHTTP_NO_PROXY_NAME, 
								WINHTTP_NO_PROXY_BYPASS, 
								0 );
	_hConnection = WinHttpConnect( _hWHSession, _pServer, (INTERNET_PORT)_nPort, 0 );

	//the new request
	_hRequest = WinHttpOpenRequest( 
						_hConnection, 
						L"POST", 
						L"/service/soap/", 
						NULL, 
						WINHTTP_NO_REFERER, 
						WINHTTP_DEFAULT_ACCEPT_TYPES,
						WINHTTP_FLAG_BYPASS_PROXY_CACHE | WINHTTP_FLAG_ESCAPE_DISABLE | WINHTTP_FLAG_ESCAPE_DISABLE_QUERY | WINHTTP_FLAG_REFRESH );
}



/*********************************************************************************************************************
 *********************************************************************************************************************
	Request 
 *********************************************************************************************************************
 *********************************************************************************************************************/

/** 
 *
 *  This template defines what every request must contian
 *
 **/
LPWSTR Request::_pRequestTemplate = 
L"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">"
	L"<soap:Header>"
		L"<context xmlns=\"urn:zimbra\">"
		L"</context>"
	L"</soap:Header>"
	L"<soap:Body>"
	L"</soap:Body>"
L"</soap:Envelope>";


/**
 *
 *  Request constructor
 *
 **/
Request::Request() : _pDom(NULL)
{
	init();
}



/**
 *
 *  Request destructor 
 *
 */
Request::~Request()
{

	if( _pDom )
	{
		_pDom->Release();
		_pDom = NULL;
	}
}



/**
 *
 *  Initialize a request
 *
 **/
void Request::init()
{
	HRESULT hr = S_OK;
	if( _pDom == NULL )
	{
		//create the DOM object
		hr = CoCreateInstance(CLSID_DOMDocument2, NULL, CLSCTX_INPROC_SERVER, IID_IXMLDOMDocument2, (void**)&_pDom );
		if( FAILED(hr) )
		{
			//sthg seriously wrong, like no MSXML installed on the machine
		}
	}
	
	
	VARIANT_BOOL b;
	
	hr = _pDom->put_async(VARIANT_FALSE);
	hr = _pDom->put_validateOnParse(VARIANT_FALSE);
	hr = _pDom->put_resolveExternals(VARIANT_FALSE);
	hr = _pDom->setProperty(L"SelectionLanguage",  _variant_t(L"XPath"));
	hr = _pDom->setProperty(L"SelectionNamespaces", 
		_variant_t("xmlns:mail='urn:zimbraMail' xmlns:l='urn:zimbra' xmlns:account='urn:zimbraAccount' xmlns:soap='http://www.w3.org/2003/05/soap-envelope'") );
	_pDom->loadXML( this->_pRequestTemplate, &b );
	//Assert( !FAILED(hr) && b != 0 );
}




/**
 *
 *  Appends an element to the soap:Body section of the document
 *
 * @param pElement		The element to append to the body 
 * @return				an HRESULT indicating success/failure
 */
HRESULT Request::AppendToSoapBody(IXMLDOMElement* pElement)
{
	HRESULT hr = S_OK;
	IXMLDOMNode* pSoapBodyElement = NULL;

	//find the soap body element
	hr = _pDom->selectSingleNode(L"//soap:Body", &pSoapBodyElement );
	if( FAILED(hr) )
	{
	}

	hr = pSoapBodyElement->appendChild( pElement, NULL );
	
	SafeRelease(pSoapBodyElement);
	return hr;
}





/**
 *
 *  Append an element to the soap:Header portion of the document
 *
 * @param pElement		The element to append to the header
 * @return				an HRESULT indicating success/failure
 */
HRESULT Request::AppendToSoapHeader(IXMLDOMElement* pElement)
{
	HRESULT hr = S_OK;
	IXMLDOMNode* pSoapHeaderElement = NULL;

	//find the soap header element
	hr = _pDom->selectSingleNode(L"//soap:Header", &pSoapHeaderElement );
	if( FAILED(hr) )
	{
	}

	hr = pSoapHeaderElement->appendChild( pElement, NULL );
	
	SafeRelease(pSoapHeaderElement);
	return hr;
}




/**
 *
 * Appends an element to the context element in the soap header
 *
 * @param pElement		The element to add
 * @return				an HRESULT indicating success/failure
 */
HRESULT Request::AppendToSoapHeaderContext( IXMLDOMNode* pElement )
{
	HRESULT hr = S_OK;
	IXMLDOMNode* pContextNode = NULL;

	//find the soap header context element
	hr = _pDom->selectSingleNode(L"//soap:Header/l:context", &pContextNode );
	if( !FAILED(hr) && pContextNode == NULL )
	{
		IXMLDOMElement* pContextElement = NULL;

		//create the soap header context element
		hr = _pDom->createElement( TAGS::bContext, &pContextElement );
		if( FAILED(hr) )
		{
		}

		hr = pContextElement->setAttribute( ATTRIBUTES::bXmlNamespace, URNS::vZimbra );
		if( FAILED(hr) )
		{
		}

		hr = AppendToSoapHeader(pContextElement);
		if( FAILED(hr) )
		{
		}

		hr = pContextElement->QueryInterface( IID_IXMLDOMNode, (LPVOID*)&pContextNode );
		if( FAILED(hr) )
		{
		}

		SafeRelease(pContextElement);
	}

	hr = pContextNode->appendChild( pElement, NULL );
	if( FAILED(hr) )
	{
	}

	SafeRelease(pContextNode);
	return hr;

}







/**
 *
 * Adds an authtoken to the soap header.
 *
 * Todo: if an auth token exists, modify it.
 *
 * @param pAuthTokenStr		The value of the auth token
 */
void Request::SetAuthToken(LPCWSTR pAuthTokenStr)
{
	HRESULT hr = S_OK;
	IXMLDOMNode* pAuthTokenNode = NULL;

	hr = _pDom->createNode( _variant_t(NODE_ELEMENT), (TAGS::bAuthToken), _bstr_t("urn:zimbra"), &pAuthTokenNode );
	if( FAILED(hr) )
	{
	}

	//create the child text node
	hr = pAuthTokenNode->put_text( (LPWSTR)(pAuthTokenStr) );
	if( FAILED(hr) )
	{
	}

	//add it to the soap header
	hr = AppendToSoapHeaderContext(pAuthTokenNode);
	if( FAILED(hr) )
	{
	}

	SafeRelease(pAuthTokenNode);
}




/**
 *
 * Adds a session id to the soap header
 *
 * Todo: if a session id exists, modify it.
 *
 * @param pSessionIdStr 
 *
 */
void Request::SetSessionId(LPCWSTR pSessionIdStr)
{
	HRESULT hr = S_OK;
	IXMLDOMNode* pSessionIdNode = NULL;	

	//create the SessionId node
	hr = _pDom->createNode( _variant_t(NODE_ELEMENT), TAGS::bSessionId, _bstr_t("urn:zimbra"), &pSessionIdNode );
	if( FAILED(hr) )
	{
	}

	//create the child text node
	hr = pSessionIdNode->put_text( (LPWSTR)(pSessionIdStr) );
	if( FAILED(hr) )
	{
	}

	//add it to the soap header
	AppendToSoapHeaderContext( pSessionIdNode );
	if( FAILED(hr) )
	{
	}

	SafeRelease(pSessionIdNode);
}


/**
 *
 * Adds a target account name to the soap header
 *
 * Todo: if a target account exists, modify it.
 *
 * @param pTargetAccountName - The name of the target account
 *
 */
void Request::SetTargetAccount(LPCWSTR pTargetAccountName)
{
	//uh uh uh
	if( pTargetAccountName == NULL )
		return;

	HRESULT hr = S_OK;
	IXMLDOMNode* pTargetAccountNode = NULL;

	//create the SessionId node
	hr = _pDom->createNode( _variant_t(NODE_ELEMENT), TAGS::bAccount, _bstr_t("urn:zimbra"), &pTargetAccountNode );
	if( FAILED(hr) )
	{
	}

	IXMLDOMElement* pTargetAccountElement = NULL;
	pTargetAccountNode->QueryInterface( IID_IXMLDOMElement, (LPVOID*)&pTargetAccountElement );
	
	//set the 'by' attribute
	hr = pTargetAccountElement->setAttribute( ATTRIBUTES::bBy, _variant_t( L"name" ) );
	if( FAILED(hr) )
	{
	}

	//create the child text node
	hr = pTargetAccountNode->put_text( (LPWSTR)(pTargetAccountName) );
	if( FAILED(hr) )
	{
	}

	//add it to the soap header
	AppendToSoapHeaderContext( pTargetAccountNode );
	if( FAILED(hr) )
	{
	}

	SafeRelease(pTargetAccountNode);
	SafeRelease(pTargetAccountElement);
}


/*********************************************************************************************************************
 *********************************************************************************************************************
	BatchRequest
	
	<BatchRequest xmlns=urn:zimbra>
		<BlaRequest></BlaRequest>+
	</AuthRequest>
 *********************************************************************************************************************
 *********************************************************************************************************************/
BatchRequest::BatchRequest()
{
	HRESULT hr = S_OK;

	//create the request element
	hr = _pDom->createElement( TAGS::bBatchRequest, &_pBatchRequestElement );
	if( FAILED(hr) )
	{
	}

	//set the namespace attribute
	hr = _pBatchRequestElement->setAttribute( ATTRIBUTES::bXmlNamespace, URNS::vZimbra );
	if( FAILED(hr) )
	{
	}

	hr = AppendToSoapBody( _pBatchRequestElement );
	if( FAILED(hr) )
	{
	}
}


BatchRequest::~BatchRequest()
{
	SafeRelease(_pBatchRequestElement);
}

void BatchRequest::AppendRequest( Request& r, __int64 batchIdx )
{
	HRESULT hr = S_OK;
	IXMLDOMNode* pRequestElement = NULL;

	//find the soap body element
	hr = r.GetDocument()->selectSingleNode(L"//soap:Body", &pRequestElement);
	if( FAILED(hr) )
	{
	}

	IXMLDOMNode* pChild = NULL;
	hr = pRequestElement->get_firstChild( &pChild );
	SafeRelease(pRequestElement);

	if( FAILED(hr) )
	{
	}

	WCHAR strBatchIdx[64];
	_i64tow( batchIdx, strBatchIdx, 10 );

	//give the request an id
	IXMLDOMElement* pChildElement = NULL;
	pChild->QueryInterface( IID_IXMLDOMElement, (LPVOID*) &pChildElement );


	pChildElement->setAttribute( ATTRIBUTES::bId, _variant_t( strBatchIdx ) );
	SafeRelease(pChildElement);

	//append the child of the soap body as the child of the batchrequestelement
	hr = _pBatchRequestElement->appendChild( pChild, NULL );
	if( FAILED(hr) )
	{
	}

	SafeRelease(pChild);
}

/*********************************************************************************************************************
 *********************************************************************************************************************
	AuthRequest 
	
	<AuthRequest xmlns=urn:zimbraAccount>
		<account by=name>...</account>
		<password>.../password>
	</AuthRequest>
 *********************************************************************************************************************
 *********************************************************************************************************************/



/**
 *
 * Construct an 'AuthRequest'
 *
 * @param pAccount		The account to authenticate
 * @param pPassword		The password associated with the account to authenticate
 */
AuthRequest::AuthRequest(LPCWSTR pAccount, LPCWSTR pPassword )
{
	HRESULT hr = S_OK;
	IXMLDOMElement* pAuthRequestElement		= NULL;
	IXMLDOMElement* pAccountElement			= NULL;
	IXMLDOMElement* pPasswordElement		= NULL;


	//create the request element
	hr = _pDom->createElement( TAGS::bAuthRequest, &pAuthRequestElement );
	if( FAILED(hr) )
	{
	}

	//set the namespace attribute
	hr = pAuthRequestElement->setAttribute( ATTRIBUTES::bXmlNamespace, URNS::vZimbraAccount );
	if( FAILED(hr) )
	{
	}

	//build the account node
	MakeAccountNode( pAccount, &pAccountElement );

	//build the password node
	MakePasswordNode( pPassword, &pPasswordElement );


	hr = pAuthRequestElement->appendChild( pAccountElement, NULL );
	if( FAILED(hr) )
	{
	}

	hr = pAuthRequestElement->appendChild( pPasswordElement, NULL );
	if( FAILED(hr) )
	{
	}

	hr = AppendToSoapBody( pAuthRequestElement );
	if( FAILED(hr) )
	{
	}

	SafeRelease(pAuthRequestElement);
	SafeRelease(pAccountElement);
	SafeRelease(pPasswordElement);	
}



/**
 *
 *  Destruct an 'AuthRequest'
 *
 */
AuthRequest::~AuthRequest()
{}



/**
 *
 * Helper to create the account node 
 *
 * @param pAccount			The text 'value' of the child node of the account node
 * @param ppAccountNode		The return value
 */
void AuthRequest::MakeAccountNode( LPCWSTR pAccount, IXMLDOMElement** ppAccountNode )
{
	//IXMLDOMText* pText = NULL;
	HRESULT hr = S_OK;

	//create the account element
	hr = _pDom->createElement( TAGS::bAccount, ppAccountNode );
	if( FAILED(hr) )
	{
	}

	//set the 'by' attribute
	hr = (*ppAccountNode)->setAttribute( ATTRIBUTES::bBy, _variant_t( L"name" ) );
	if( FAILED(hr) )
	{
	}

	hr = (*ppAccountNode)->put_text( (LPWSTR)pAccount );
	if( FAILED(hr) )
	{
	}
}



/**
 *
 *  Helper to create the password node
 *
 * @param pPassword			The text of the child node of the password node
 * @param ppPasswordNode	The return value
 */
void AuthRequest::MakePasswordNode( LPCWSTR pPassword, IXMLDOMElement** ppPasswordNode )
{
	//IXMLDOMText* pText = NULL;
	HRESULT hr = S_OK;

	//create the password element
	hr = _pDom->createElement( TAGS::bPassword, ppPasswordNode );
	if( FAILED(hr) )
	{
	}

	hr = (*ppPasswordNode)->put_text( (LPWSTR)pPassword );
	if( FAILED(hr) )
	{
	}
}





/*********************************************************************************************************************
 *********************************************************************************************************************
	PingRequest
	
	<PingRequest xmlns=urn:zimbraMail />
 *********************************************************************************************************************
 *********************************************************************************************************************/
PingRequest::PingRequest()
{
	HRESULT hr = S_OK;
	IXMLDOMElement* pRequestElement = NULL;

	hr = _pDom->createElement( TAGS::bPingRequest, &pRequestElement );
	if( FAILED(hr) )
	{
	}

	hr = pRequestElement->setAttribute( ATTRIBUTES::bXmlNamespace, URNS::vZimbraMail );
	if( FAILED(hr) )
	{
	}

	hr = AppendToSoapBody(pRequestElement);
	if( FAILED(hr) )
	{
	}
	
	SafeRelease(pRequestElement);
}


PingRequest::~PingRequest()
{
}


/*********************************************************************************************************************
 *********************************************************************************************************************
	GetFolderRequest
	
	<GetFolderRequest xmlns=urn:zimbraMail [l="base-folder-id"]/>
 *********************************************************************************************************************
 *********************************************************************************************************************/


/**
 *
 *  Construct a 'GetFolderRequest' 
 *
 */
GetFolderRequest::GetFolderRequest()
{
	HRESULT hr = S_OK;
	IXMLDOMElement* pRequestElement = NULL;

	hr = _pDom->createElement( TAGS::bGetFolderRequest, &pRequestElement );
	if( FAILED(hr) )
	{
	}

	hr = pRequestElement->setAttribute( ATTRIBUTES::bXmlNamespace, URNS::vZimbraMail );
	if( FAILED(hr) )
	{
	}

	hr = AppendToSoapBody(pRequestElement);
	if( FAILED(hr) )
	{
	}
	
	SafeRelease(pRequestElement);
}


/**
 *
 *  Destruct a 'GetFolderRequest'
 *
 **/
GetFolderRequest::~GetFolderRequest()
{}





/*********************************************************************************************************************
 *********************************************************************************************************************
	GetTagRequest
	
	<GetTagRequest xmlns=urn:zimbraMail />
 *********************************************************************************************************************
 *********************************************************************************************************************/


/**
 *
 *  Construct a 'GetTagRequest'
 *
 **/
GetTagRequest::GetTagRequest()
{
	HRESULT hr = S_OK;
	IXMLDOMElement* pRequestElement = NULL;

	hr = _pDom->createElement( TAGS::bGetTagRequest, &pRequestElement );
	if( FAILED(hr) )
	{
	}

	hr = pRequestElement->setAttribute( ATTRIBUTES::bXmlNamespace, URNS::vZimbraMail );
	if( FAILED(hr) )
	{
	}

	hr = AppendToSoapBody(pRequestElement);
	if( FAILED(hr) )
	{
	}
	
	SafeRelease(pRequestElement);
}



/**
 *
 *  Destruct a 'GetTagRequest'
 *
 **/
GetTagRequest::~GetTagRequest()
{}




/*********************************************************************************************************************
 *********************************************************************************************************************
	CreateFolderRequest
	
	<CreateFolderRequest xmlns=urn:zimbraMail >
		<folder name="..." l="{parent-folder}"/>
	</CreateFolderRequest>
 *********************************************************************************************************************
 *********************************************************************************************************************/


CreateFolderRequest::CreateFolderRequest( LPCWSTR pFolderName, __int64 parentFolderId )
{
	HRESULT hr = S_OK;
	IXMLDOMElement* pRequestElement = NULL;
	IXMLDOMElement* pFolderElement = NULL;

	WCHAR pParentFolderIdStr[64] = {0};
	_i64tow( parentFolderId, pParentFolderIdStr, 10 );

	//create the CreateFolderRequest element
	hr = _pDom->createElement( TAGS::bCreateFolderRequest, &pRequestElement );
	if( FAILED(hr) )
	{
	}

	//set its namespace
	hr = pRequestElement->setAttribute( ATTRIBUTES::bXmlNamespace, URNS::vZimbraMail );
	if( FAILED(hr) )
	{
	}

	//create the folder element
	hr = _pDom->createElement( TAGS::bFolder, &pFolderElement );
	if( FAILED(hr) )
	{
	}

	//set the name of the folder
	hr = pFolderElement->setAttribute( ATTRIBUTES::bName, _variant_t(pFolderName) );
	if( FAILED(hr) )
	{
	}

	//set the parent folder id
	hr = pFolderElement->setAttribute( ATTRIBUTES::bParentFolderId, _variant_t(pParentFolderIdStr) );
	if( FAILED(hr) )
	{
	}

	//make the folder a child of the request
	hr = pRequestElement->appendChild( pFolderElement, NULL );
	if( FAILED(hr) )
	{
	}

	//add the request to the soap body
	hr = AppendToSoapBody( pRequestElement );
	if( FAILED(hr) )
	{
	}

	SafeRelease(pRequestElement);
	SafeRelease(pFolderElement);
}


CreateFolderRequest::~CreateFolderRequest()
{}





/*********************************************************************************************************************
 *********************************************************************************************************************
	CreateTagRequest
	
	<CreateTagRequest xmlns=urn:zimbraMail >
		<tag name="..." color="..."/>
	</CreateTagRequest>
 *********************************************************************************************************************
 *********************************************************************************************************************/


CreateTagRequest::CreateTagRequest(LPCWSTR pTagName, UINT tagColor )
{
	HRESULT hr = S_OK;
	IXMLDOMElement* pRequestElement = NULL;
	IXMLDOMElement* pTagElement = NULL;

	WCHAR tagColorStr[64] = {0};
	_i64tow( tagColor, tagColorStr, 10 );

	//create the CreateFolderRequest element
	hr = _pDom->createElement( TAGS::bCreateTagRequest, &pRequestElement );
	if( FAILED(hr) )
	{
	}

	//set its namespace
	hr = pRequestElement->setAttribute( ATTRIBUTES::bXmlNamespace, URNS::vZimbraMail );
	if( FAILED(hr) )
	{
	}

	//create the folder element
	hr = _pDom->createElement( TAGS::bTag, &pTagElement );
	if( FAILED(hr) )
	{
	}

	//set the name of the folder
	hr = pTagElement->setAttribute( ATTRIBUTES::bName, _variant_t(pTagName) );
	if( FAILED(hr) )
	{
	}

	//set the parent folder id
	hr = pTagElement->setAttribute( ATTRIBUTES::bColor, _variant_t(tagColorStr) );
	if( FAILED(hr) )
	{
	}

	//make the folder a child of the request
	hr = pRequestElement->appendChild( pTagElement, NULL );
	if( FAILED(hr) )
	{
	}

	//add the request to the soap body
	hr = AppendToSoapBody( pRequestElement );
	if( FAILED(hr) )
	{
	}

	SafeRelease(pRequestElement);
	SafeRelease(pTagElement);
}


CreateTagRequest::~CreateTagRequest()
{}








/*********************************************************************************************************************
 *********************************************************************************************************************
	GetContactsRequest
	
	<GetContactsRequest xmlns=urn:zimbraMail >
		<a n="..."/>*
		<cn id="contact_id"" />*
	</GetContactsRequest>
 *********************************************************************************************************************
 *********************************************************************************************************************/


GetContactsRequest::GetContactsRequest(__int64* pContactIds, UINT nContactIds, LPWSTR* pAttrList, UINT nAttrs )
{
	HRESULT hr = S_OK;
	IXMLDOMElement* pRequestElement = NULL;

	hr = _pDom->createElement( TAGS::bGetContactsRequest, &pRequestElement );
	if( FAILED(hr) )
	{
	}

	hr = pRequestElement->setAttribute( ATTRIBUTES::bXmlNamespace, URNS::vZimbraMail );
	if( FAILED(hr) )
	{
	}

	//add each attribute
	LPWSTR* pCurr = pAttrList;
	for( UINT attrIdx = 0; attrIdx < nAttrs; attrIdx++, pCurr++ )
	{
		IXMLDOMElement* pAttrElement = NULL;
		hr = _pDom->createElement( TAGS::bAttribute, &pAttrElement );
		if( FAILED(hr) )
		{
		}

		hr = pAttrElement->setAttribute( ATTRIBUTES::bAttributeName, _variant_t(*pCurr) );
		if( FAILED(hr) )
		{
		}

		hr = pRequestElement->appendChild( pAttrElement, NULL );
		if( FAILED(hr) )
		{
		}

		SafeRelease(pAttrElement);
	}

	//add each contact requested
	__int64* pCurrC = pContactIds;
	for( UINT cidIdx = 0; cidIdx < nContactIds; cidIdx++, pCurrC++ )
	{
		WCHAR contactIdStr[64] = {0};
		_i64tow( *pCurrC, contactIdStr, 10 );

		IXMLDOMElement* pContactElement = NULL;
		hr = _pDom->createElement( TAGS::bContact, &pContactElement );
		if( FAILED(hr) )
		{
		}

		hr = pContactElement->setAttribute( ATTRIBUTES::bId, _variant_t(contactIdStr) );
		if( FAILED(hr) )
		{
		}

		hr = pRequestElement->appendChild( pContactElement, NULL );
		if( FAILED(hr) )
		{
		}

		SafeRelease(pContactElement);
	}

	hr = AppendToSoapBody(pRequestElement);
	if( FAILED(hr) )
	{
	}

	SafeRelease(pRequestElement);
}

GetContactsRequest::~GetContactsRequest()
{}







/*********************************************************************************************************************
 *********************************************************************************************************************
	CreateContactsRequest
	
	<CreateContactsRequest xmlns=urn:zimbraMail >
		<cn [folder="{folder-id}"]>
			<a n="...">...</a>+
		</cn>
	</CreateContactRequest>
 *********************************************************************************************************************
 *********************************************************************************************************************/

CreateContactRequest::CreateContactRequest(std::map<LPWSTR,LPWSTR>& attrs, std::list<__int64>& tagIds, __int64 parentFolderId )
{
	HRESULT hr = S_OK;
	IXMLDOMElement* pRequestElement = NULL;
	IXMLDOMElement* pContactElement = NULL;

	WCHAR parentFolderIdStr[64] = {0};
	_i64tow( parentFolderId, parentFolderIdStr, 10 );

	//build the list of tags
	LPWSTR tagIdStr = NULL;
	if( tagIds.size() > 0 )
	{
		int nLength = (64 * (int)tagIds.size()) + 1;
		tagIdStr = new WCHAR[ nLength ];
		ZeroMemory( tagIdStr, nLength );
	}

	WCHAR tempBuffer[64] = {0};
	for( std::list<__int64>::iterator i = tagIds.begin(); i != tagIds.end(); i++ )
	{
		if( i != tagIds.begin() )
			wcscat( tagIdStr, L",");

		__int64 val = *i;
		_i64tow( val, tempBuffer, 10 );
		wcscat( tagIdStr, tempBuffer );
	}


	//create the request tag
	hr = _pDom->createElement( TAGS::bCreateContactRequest, &pRequestElement );
	if( FAILED(hr) )
	{
	}

	//set the namespace attribute
	hr = pRequestElement->setAttribute( ATTRIBUTES::bXmlNamespace, URNS::vZimbraMail );
	if( FAILED(hr) )
	{
	}


	//create the contact element
	hr = _pDom->createElement( TAGS::bContact, &pContactElement );
	if( FAILED(hr) )
	{
	}

	//add the parent folder id attribute
	hr = pContactElement->setAttribute( ATTRIBUTES::bParentFolderId, _variant_t(parentFolderIdStr) );
	if( FAILED(hr) )
	{
	}

	//add the tags attribute if there are any tags
	if( tagIdStr != NULL )
	{
		hr = pContactElement->setAttribute( ATTRIBUTES::bTagIds, _variant_t(tagIdStr) );
		if( FAILED(hr) )
		{
		}
	}

	//add all the attributes
	for( std::map<LPWSTR,LPWSTR>::iterator i = attrs.begin(); i != attrs.end(); i++ )
	{
		IXMLDOMElement* pAttributeElement = NULL;

		hr = _pDom->createElement( TAGS::bAttribute, &pAttributeElement );
		if( FAILED(hr) )
		{
		}
		
		hr = pAttributeElement->setAttribute( ATTRIBUTES::bAttributeName, _variant_t(i->first) );
		if( FAILED(hr) )
		{
		}
		
		hr = pAttributeElement->put_text( i->second );
		if( FAILED(hr) )
		{
		}

		hr = pContactElement->appendChild( pAttributeElement, NULL );
		if( FAILED(hr) )
		{
		}

		SafeRelease( pAttributeElement );
	}
	
	hr = pRequestElement->appendChild( pContactElement, NULL );
	if( FAILED(hr) )
	{
	}


	hr = AppendToSoapBody(pRequestElement);
	if( FAILED(hr) )
	{
	}

	SafeRelease(pRequestElement);
	SafeRelease(pContactElement);

	if( tagIdStr != NULL )
		delete [] tagIdStr;

}

CreateContactRequest::~CreateContactRequest()
{}


/*********************************************************************************************************************
 *********************************************************************************************************************
	AddMessageRequest
	
	<AddMsgRequest xmlns=urn:zimbraMail >
		<m l="{parent-folder-id}" f="flags" t="tag-ids">
			<content>mime-data</content>
		</m>
	</AddMsgRequest>
 *********************************************************************************************************************
 *********************************************************************************************************************/

AddMessageRequest::AddMessageRequest(LPCWSTR pMimeMsg, __int64 parentFolderId, LPWSTR flags, std::list<__int64>& tagIds )
{
	HRESULT hr = S_OK;
	IXMLDOMElement* pRequestElement = NULL;
	IXMLDOMElement* pMessageElement = NULL;
	IXMLDOMElement* pContentElement = NULL;

	//build the parent folder id string
	WCHAR parentFolderIdStr[64] = {0};
	_i64tow( parentFolderId, parentFolderIdStr, 10 );

	//build the tag id string
	LPWSTR tagIdStr = NULL;
	if( tagIds.size() > 0 )
	{
		int nLength = (64 * (int)tagIds.size()) + 1;
		tagIdStr = new WCHAR[ nLength ];
		ZeroMemory( tagIdStr, nLength );
	}

	WCHAR tempBuffer[64] = {0};
	for( std::list<__int64>::iterator i = tagIds.begin(); i != tagIds.end(); i++ )
	{
		if( i != tagIds.begin() )
			wcscat( tagIdStr, L",");

		__int64 val = *i;
		_i64tow( val, tempBuffer, 10 );
		wcscat( tagIdStr, tempBuffer );
	}


	//create the request tag
	hr = _pDom->createElement( TAGS::bAddMessageRequest, &pRequestElement );
	if( FAILED(hr) )
	{
	}

	//set the namespace attribute
	hr = pRequestElement->setAttribute( ATTRIBUTES::bXmlNamespace, URNS::vZimbraMail );
	if( FAILED(hr) )
	{
	}

	//create the message element
	hr = _pDom->createElement( TAGS::bMessage, &pMessageElement );
	if( FAILED(hr) )
	{
	}

	//add the parent folder id attribute
	hr = pMessageElement->setAttribute( ATTRIBUTES::bParentFolderId, _variant_t(parentFolderIdStr) );
	if( FAILED(hr) )
	{
	}

	//add the message flags, if any
	if( flags != NULL && wcslen(flags) > 0 )
	{
		hr = pMessageElement->setAttribute( ATTRIBUTES::bFlags, _variant_t(flags) );
		if( FAILED(hr) )
		{
		}
	}

	//add the tags attribute if there are any tags
	if( tagIdStr != NULL )
	{
		hr = pMessageElement->setAttribute( ATTRIBUTES::bTagIds, _variant_t(tagIdStr) );
		if( FAILED(hr) )
		{
		}
	}

	//create the content element
	hr = _pDom->createElement( TAGS::bContent, &pContentElement );
	if( FAILED(hr) )
	{
	}

	hr = pContentElement->put_text( (LPWSTR)pMimeMsg );
	if( FAILED(hr) )
	{
	}

	
	pMessageElement->appendChild( pContentElement, NULL );
	pRequestElement->appendChild( pMessageElement, NULL );
	AppendToSoapBody( pRequestElement );
	
	SafeRelease( pContentElement );
	SafeRelease( pMessageElement );
	SafeRelease( pRequestElement );

	if( tagIdStr != NULL )
		delete [] tagIdStr;

}

AddMessageRequest::~AddMessageRequest()
{}







/////////// winhttp errors
LPCTSTR RpcException::pTERROR_WINHTTP_CANNOT_CONNECT			= _T("Unable to connect.");
LPCTSTR RpcException::pTERROR_WINHTTP_CONNECTION_ERROR			= _T("The connection with the server has been reset or terminated, or an incompatible SSL protocol was encountered.");
LPCTSTR RpcException::pTERROR_WINHTTP_INCORRECT_HANDLE_STATE	= _T("The requested operation cannot be carried out because the handle supplied is not in the correct state.");
LPCTSTR RpcException::pTERROR_WINHTTP_INCORRECT_HANDLE_TYPE		= _T("The type of handle supplied is incorrect for this operation.");
LPCTSTR RpcException::pTERROR_WINHTTP_INTERNAL_ERROR			= _T("An internal WinHttp error has occurred.");
LPCTSTR RpcException::pTERROR_WINHTTP_INVALID_URL				= _T("The URL is invalid.");
LPCTSTR RpcException::pTERROR_WINHTTP_LOGIN_FAILURE				= _T("The WinHttp login attempt failed.");
LPCTSTR RpcException::pTERROR_WINHTTP_NAME_NOT_RESOLVED			= _T("The server name could not be resolved.");
LPCTSTR RpcException::pTERROR_WINHTTP_OPERATION_CANCELLED		= _T("The WinHttp operation was canceled");
LPCTSTR RpcException::pTERROR_WINHTTP_RESPONSE_DRAIN_OVERFLOW	= _T("The response exceeds an internal WinHTTP size limit.");
LPCTSTR RpcException::pTERROR_WINHTTP_SECURE_FAILURE			= _T("One or more errors were found in the Secure Sockets Layer (SSL) certificate sent by the server.");
LPCTSTR RpcException::pTERROR_WINHTTP_SHUTDOWN					= _T("The WinHTTP function support is being shut down or unloaded.");
LPCTSTR RpcException::pTERROR_WINHTTP_TIMEOUT					= _T("The request has timed out.");
LPCTSTR RpcException::pTERROR_WINHTTP_UNRECOGNIZED_SCHEME		= _T("The URL specified a scheme other than \"http:\" or \"https:\".");

