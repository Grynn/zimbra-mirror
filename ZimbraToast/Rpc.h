#pragma once
#include "Util.h"


namespace Liquid { namespace Rpc {

class RpcException
{
	public:
		RpcException( DWORD error ) : _error(error), _pErrorStr(NULL){}
		~RpcException()
		{
			if( _pErrorStr != NULL )
			{
				LocalFree( _pErrorStr );
				_pErrorStr = NULL;
			}
		}

		DWORD GetError(){ return _error; }

		LPCWSTR GetErrorString() 
		{ 
			switch( _error )
			{	
				case		 ERROR_WINHTTP_CANNOT_CONNECT:	
					return pTERROR_WINHTTP_CANNOT_CONNECT;
				case		 ERROR_WINHTTP_CONNECTION_ERROR:
					return pTERROR_WINHTTP_CONNECTION_ERROR;
				case		 ERROR_WINHTTP_INCORRECT_HANDLE_STATE:
					return pTERROR_WINHTTP_INCORRECT_HANDLE_STATE;
				case		 ERROR_WINHTTP_INCORRECT_HANDLE_TYPE:
					return pTERROR_WINHTTP_INCORRECT_HANDLE_TYPE;
				case		 ERROR_WINHTTP_INTERNAL_ERROR:
					return pTERROR_WINHTTP_INTERNAL_ERROR;
				case		 ERROR_WINHTTP_INVALID_URL:
					return pTERROR_WINHTTP_INVALID_URL;
				case		 ERROR_WINHTTP_LOGIN_FAILURE:
					return pTERROR_WINHTTP_LOGIN_FAILURE;
				case		 ERROR_WINHTTP_NAME_NOT_RESOLVED:
					return pTERROR_WINHTTP_NAME_NOT_RESOLVED;
				case		 ERROR_WINHTTP_OPERATION_CANCELLED:
					return pTERROR_WINHTTP_OPERATION_CANCELLED;
				case		 ERROR_WINHTTP_RESPONSE_DRAIN_OVERFLOW:
					return pTERROR_WINHTTP_RESPONSE_DRAIN_OVERFLOW;
				case		 ERROR_WINHTTP_SECURE_FAILURE:
					return pTERROR_WINHTTP_SECURE_FAILURE;
				case		 ERROR_WINHTTP_SHUTDOWN:
					return pTERROR_WINHTTP_SHUTDOWN;
				case		 ERROR_WINHTTP_TIMEOUT:
					return pTERROR_WINHTTP_TIMEOUT;
				case		 ERROR_WINHTTP_UNRECOGNIZED_SCHEME:
					return pTERROR_WINHTTP_UNRECOGNIZED_SCHEME;
			}

			if( _pErrorStr == NULL )
			{
				if (!FormatMessage( 
						FORMAT_MESSAGE_ALLOCATE_BUFFER | 
						FORMAT_MESSAGE_FROM_SYSTEM | 
						FORMAT_MESSAGE_IGNORE_INSERTS,
						NULL,
						_error,
						MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
						(LPTSTR) &_pErrorStr,
						0,
						NULL ) )
				{
					_pErrorStr = (LPTSTR)LocalAlloc( 0, sizeof(TCHAR) * 64 );
					_stprintf( _pErrorStr, _T("Unkown Error %x"), _error );
				}

			}
			return _pErrorStr;
		}

	private:
		RpcException(); //no body.

	private:
		DWORD _error;
		LPWSTR _pErrorStr;

		static LPCTSTR pTERROR_WINHTTP_CANNOT_CONNECT;
		static LPCTSTR pTERROR_WINHTTP_CONNECTION_ERROR;
		static LPCTSTR pTERROR_WINHTTP_INCORRECT_HANDLE_STATE;
		static LPCTSTR pTERROR_WINHTTP_INCORRECT_HANDLE_TYPE;
		static LPCTSTR pTERROR_WINHTTP_INTERNAL_ERROR;
		static LPCTSTR pTERROR_WINHTTP_INVALID_URL;
		static LPCTSTR pTERROR_WINHTTP_LOGIN_FAILURE;
		static LPCTSTR pTERROR_WINHTTP_NAME_NOT_RESOLVED;
		static LPCTSTR pTERROR_WINHTTP_OPERATION_CANCELLED;
		static LPCTSTR pTERROR_WINHTTP_RESPONSE_DRAIN_OVERFLOW;
		static LPCTSTR pTERROR_WINHTTP_SECURE_FAILURE;
		static LPCTSTR pTERROR_WINHTTP_SHUTDOWN;
		static LPCTSTR pTERROR_WINHTTP_TIMEOUT;
		static LPCTSTR pTERROR_WINHTTP_UNRECOGNIZED_SCHEME;
};

class Request;

/*****************************************************************************
 *
 *  Connection
 *
 *****************************************************************************/

class Connection
{
	public:
		Connection( LPWSTR pServer, unsigned int nPort );
		~Connection();

		void SendRequest( Request& req, IXMLDOMDocument2*& pResponseXml );

	private:
		//forces a winhttp connect/request
		void Connect();

		//sends a request
		void SendRequest( Request& req, LPSTR& pResponseStr );
		void SendRequest( LPCWSTR pRequest, LPSTR& pResponseStr );

	private:
		//connection parameters
		LPWSTR			_pServer;				//server name
		unsigned int	_nPort;					//port to sonnect to

		//winhttp handles
		HINTERNET		_hWHSession;			//winhttp session handle
		HINTERNET		_hConnection;			//winhttp connection handle
		HINTERNET		_hRequest;				//winhttp request handle

};



/*****************************************************************************
 *
 *  Requests...
 *
 *****************************************************************************/

class BatchRequest;
/*****************************************************************************
 *
 *  Base class for all SOAP requests made to the liquid server
 *
 *****************************************************************************/
class Request
{
	friend class Connection;
	friend class BatchRequest;

	public:
		Request();
		virtual ~Request();
		void SetAuthToken( LPCWSTR pAuthToken );
		void SetSessionId( LPCWSTR pSessionId );
		void SetTargetAccount(LPCWSTR pTargetAccountName);

	protected:
		HRESULT AppendToSoapHeader( IXMLDOMElement* pElement );
		HRESULT AppendToSoapHeaderContext( IXMLDOMNode* pElement );
		HRESULT AppendToSoapBody( IXMLDOMElement* pElement );

		IXMLDOMDocument2* GetDocument(){return _pDom;}

	private:
		void init();

	protected:
		IXMLDOMDocument2* _pDom;

	protected:
		static LPWSTR _pRequestTemplate;
};


/*****************************************************************************
 *
 *  An 'BatchRequest' to the server.
 *
 *****************************************************************************/
class BatchRequest : public Request
{
	public:
		BatchRequest();
		virtual ~BatchRequest();

		void AppendRequest( Request& r, __int64 batchIdx );

	private:
		IXMLDOMElement* _pBatchRequestElement;
};




/*****************************************************************************
 *
 *  An 'AuthRequest' to the server.
 *
 *****************************************************************************/
class AuthRequest : public Request
{
	public:
		AuthRequest( LPCWSTR pAccount, LPCWSTR pPassword );
		virtual ~AuthRequest();

	private:
		void MakeAccountNode(LPCWSTR pAccount, IXMLDOMElement** ppAccountNode);
		void MakePasswordNode(LPCWSTR pPassword, IXMLDOMElement** ppPasswordNode);
};



/*****************************************************************************
 *
 *  A 'PingRequest' to the server
 *
 *****************************************************************************/
class PingRequest : public Request
{
	public:
		PingRequest();
		virtual ~PingRequest();
};




/*****************************************************************************
 *
 *  A 'GetFolderRequest' to the server
 *
 *****************************************************************************/
class GetFolderRequest : public Request
{
	public:
		GetFolderRequest();
		virtual ~GetFolderRequest();
};



/*****************************************************************************
 *
 *  A 'CreateFolderRequest' to the server
 *
 *****************************************************************************/
class CreateFolderRequest : public Request
{
	public:
		CreateFolderRequest( LPCWSTR pFolderName, __int64 parentFolderId );
		virtual ~CreateFolderRequest();
};



/*****************************************************************************
 *
 *  A 'GetTagRequest' to the server
 *
 *****************************************************************************/
class GetTagRequest : public Request
{
	public:
		GetTagRequest();
		virtual ~GetTagRequest();
};



/*****************************************************************************
 *
 *  A 'CreateTagRequest' to the server
 *
 *****************************************************************************/
class CreateTagRequest : public Request
{
	public:
		CreateTagRequest( LPCWSTR pTagName, UINT tagColor );
		virtual ~CreateTagRequest();
};



/*****************************************************************************
 *
 *  GetContactsRequest
 *
 *****************************************************************************/
class GetContactsRequest : public Request
{
	public:
		GetContactsRequest(__int64* pContactIds, UINT nContactIds, LPWSTR* pAttrList, UINT nAttrs);
		virtual ~GetContactsRequest();
};



/*****************************************************************************
 *
 *  CreateContactRequest  
 *
 *****************************************************************************/
class CreateContactRequest : public Request
{
	public:
		CreateContactRequest(std::map<LPWSTR,LPWSTR>& attrs, std::list<__int64>& tagIds, __int64 parentFolderId);
		virtual ~CreateContactRequest();
};



/*****************************************************************************
 *
 *  AddMessageRequest
 *
 *****************************************************************************/
class AddMessageRequest : public Request
{
	public:
		AddMessageRequest(LPCWSTR pMimeMsg, __int64 parentFolderId, LPWSTR pFlags, std::list<__int64>& tagIds);
		virtual ~AddMessageRequest();
};





/*****************************************************************************
 *
 *  SessionData
 *
 *****************************************************************************/
class SessionData
{
	public:
		//constructor
		SessionData() : _pAuthToken(NULL), _pSessionId(NULL), _pLifeTime(NULL){};

		//destructor
		~SessionData()
		{
			Cleanup();
		}

		//set the internal values based on the XML soap response
		void Initialize( IXMLDOMDocument2* pDoc )
		{
			Cleanup();
			Liquid::Util::XmlUtil::FindNodeValue( pDoc, L"//account:authToken", _pAuthToken );
			Liquid::Util::XmlUtil::FindNodeValue( pDoc, L"//account:lifetime",  _pLifeTime  );
			Liquid::Util::XmlUtil::FindNodeValue( pDoc, L"//account:sessionId", _pSessionId );
		}

		void Update( IXMLDOMDocument2* pDoc )
		{
			LPWSTR pNewSessionId = NULL;
			Liquid::Util::XmlUtil::FindNodeValue( pDoc, L"//l:sessionId", pNewSessionId );
			if( pNewSessionId != NULL )
			{
				Liquid::Util::SafeDelete( _pSessionId ); //remove the old session id
				_pSessionId = pNewSessionId;
			}
		}

		//get a reference to the internal auth token string
		LPWSTR& AuthToken(){ return _pAuthToken; }

		//get a reference to the internal session id string
		LPWSTR& SessionId(){ return _pSessionId; }

		//get a reference to the internal lifetime as a string
		LPWSTR& LifeTime(){ return _pLifeTime; }

	private:
		//free internal resources
		void Cleanup()
		{
			Liquid::Util::SafeDelete( _pAuthToken );
			Liquid::Util::SafeDelete( _pSessionId );
			Liquid::Util::SafeDelete( _pLifeTime  );
		}

		LPWSTR _pAuthToken;
		LPWSTR _pSessionId;
		LPWSTR _pLifeTime;
};



/**
 *
 *  Tag names
 *
 **/
struct TAGS
{
	static LPCWSTR Context;
	static LPCWSTR AuthToken;
	static LPCWSTR SessionId;

	static LPCWSTR AuthRequest;
	static LPCWSTR Account;
	static LPCWSTR Password;

	static LPCWSTR BatchRequest;
	static LPCWSTR GetFolderRequest;
	static LPCWSTR GetTagRequest;
	static LPCWSTR CreateFolderRequest;
	static LPCWSTR CreateTagRequest;
	static LPCWSTR GetContactsRequest;
	static LPCWSTR CreateContactRequest;
	static LPCWSTR AddMessageRequest;
	static LPCWSTR PingRequest;

	static LPCWSTR Folder;
	static LPCWSTR Tag;
	static LPCWSTR Attribute;
	static LPCWSTR Contact;
	static LPCWSTR Message;
	static LPCWSTR Content;

	//the bstr version

	static _bstr_t bContext;
	static _bstr_t bAuthToken;
	static _bstr_t bSessionId;
	

	static _bstr_t bAuthRequest;
	static _bstr_t bAccount;
	static _bstr_t bPassword;

	static _bstr_t bBatchRequest;
	static _bstr_t bGetFolderRequest;
	static _bstr_t bGetTagRequest;
	static _bstr_t bCreateFolderRequest;
	static _bstr_t bCreateTagRequest;
	static _bstr_t bGetContactsRequest;
	static _bstr_t bCreateContactRequest;
	static _bstr_t bAddMessageRequest;
	static _bstr_t bPingRequest;

	static _bstr_t bFolder;
	static _bstr_t bTag;
	static _bstr_t bAttribute;
	static _bstr_t bContact;
	static _bstr_t bMessage;
	static _bstr_t bContent;
};


/**
 *
 *  Attribute names
 *
 **/
struct ATTRIBUTES
{
	static LPCWSTR XmlNamespace;
	static LPCWSTR By;
	static LPCWSTR ParentFolderId;
	static LPCWSTR Name;
	static LPCWSTR AttributeName;
	static LPCWSTR Color;
	static LPCWSTR Id;
	static LPCWSTR TagIds;
	static LPCWSTR Flags;

	//the bstr versions
	static _bstr_t bXmlNamespace;
	static _bstr_t bBy;
	static _bstr_t bParentFolderId;
	static _bstr_t bName;
	static _bstr_t bAttributeName;
	static _bstr_t bColor;
	static _bstr_t bId;
	static _bstr_t bTagIds;
	static _bstr_t bFlags;
};



/**
 *
 *  Namespace values
 *
 **/
struct URNS
{
	static LPCWSTR Liquid;
	static LPCWSTR LiquidAccount;
	static LPCWSTR LiquidMail;

	//variant versions
	static _variant_t vLiquid;
	static _variant_t vLiquidAccount;
	static _variant_t vLiquidMail;
};

};};
