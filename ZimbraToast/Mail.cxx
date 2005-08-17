#include <stdafx.h>
#include "Mail.h"


using namespace Liquid;
using namespace Liquid::Mail;
using namespace Liquid::Util;




/*********************************************************************************************************************
 *********************************************************************************************************************
	BatchResponse Object
 *********************************************************************************************************************
 *********************************************************************************************************************/
BatchResponse::BatchResponse( IXMLDOMDocument2* pResponseDoc )
{
	_pBatchResponse = pResponseDoc;
	if( _pBatchResponse != NULL )
	{
		_pBatchResponse->AddRef();
	}
}

BatchResponse::BatchResponse( const BatchResponse& in )
{
	_pBatchResponse = in._pBatchResponse;
	if( _pBatchResponse != NULL )
	{
		_pBatchResponse->AddRef();
	}
}

BatchResponse::~BatchResponse()
{
	if( _pBatchResponse != NULL )
	{
		_pBatchResponse->Release();
	}
}


void BatchResponse::operator=( const BatchResponse& in )
{
	if( _pBatchResponse != NULL )
	{
		_pBatchResponse->Release();
	}

	_pBatchResponse = in._pBatchResponse;
	if( _pBatchResponse != NULL )
	{
		_pBatchResponse->AddRef();
	}
}


ContactId BatchResponse::GetCreateContactResponse( __int64 id )
{
	//select cn element child of the CreateContactResponse element that the given id.
	WCHAR pXPathQuery[ 256 ];
	WCHAR strBatchId[64];

	_i64tow( id, strBatchId,10 );
	wsprintf( pXPathQuery, L"//mail:CreateContactResponse[@id=\"%s\"]//mail:cn", strBatchId );

	LPWSTR pContactId = NULL;
	XmlUtil::FindNodeAttributeValue( _pBatchResponse, pXPathQuery, L"id", pContactId );
	if( pContactId == NULL )
	{
		LPWSTR pText = NULL;
		LPWSTR pCode = NULL;
		wsprintf( pXPathQuery, L"//soap:Fault[@id=\"%s\"]//soap:Text", strBatchId );
		XmlUtil::FindNodeValue( _pBatchResponse, pXPathQuery, pText );

		wsprintf( pXPathQuery, L"//soap:Fault[@id=\"%s\"]//l:Code", strBatchId );
		XmlUtil::FindNodeValue( _pBatchResponse, pXPathQuery, pCode );

		if( pText != NULL && pCode != NULL )
		{
			throw LiquidException( pCode, pText );
		}
		else
		{
			SafeDelete(pText);
			SafeDelete(pCode);
			throw LiquidException( L"contact_id_not_returned", L"no contact id returned" );
		}
	}
	ContactId contactId = _wtoi64( pContactId );
	delete [] pContactId;
	return contactId;
}


MessageId BatchResponse::GetAddMessageResponse( __int64 id )
{
	WCHAR pXPathQuery[ 256 ];
	WCHAR strBatchId[64];

	_i64tow( id, strBatchId, 10 );
	wsprintf( pXPathQuery, L"//mail:AddMsgResponse[@id=\"%s\"]//mail:m", strBatchId );

	LPWSTR pMessageId = NULL;
	XmlUtil::FindNodeAttributeValue( _pBatchResponse, pXPathQuery, L"id", pMessageId );
	if( pMessageId == NULL )
	{
		LPWSTR pText = NULL;
		LPWSTR pCode = NULL;
		wsprintf( pXPathQuery, L"//soap:Fault[@id=\"%s\"]//soap:Text", strBatchId );
		XmlUtil::FindNodeValue( _pBatchResponse, pXPathQuery, pText );

		wsprintf( pXPathQuery, L"//soap:Fault[@id=\"%s\"]//l:Code", strBatchId );
		XmlUtil::FindNodeValue( _pBatchResponse, pXPathQuery, pCode );

		if( pText != NULL && pCode != NULL )
		{
			throw LiquidException( pCode, pText );
		}
		else
		{
			SafeDelete(pText);
			SafeDelete(pCode);
			throw LiquidException( L"message not added", L"no message id returned" );
		}
		
	}
	MessageId messageId = _wtoi64( pMessageId );
	delete [] pMessageId;
	return messageId;
}




/*********************************************************************************************************************
 *********************************************************************************************************************
	Mailbox Object
 *********************************************************************************************************************
 *********************************************************************************************************************/



/**
 *
 * A proxy object for a mailbox on a Liquid mail server
 *
 * @param pServer	The server the mailbox lives on
 * @param nPort		The port to contact the server on
 *
 */
Mailbox::Mailbox(LPWSTR pServer, UINT nPort) : _connection(pServer, nPort), _bBatching(FALSE), _pBatchRequest(NULL)
{}



/**
 *
 *
 *
 */
Mailbox::~Mailbox()
{
	if( _pBatchRequest != NULL )
	{
		delete _pBatchRequest;
		_pBatchRequest = NULL;
	}
}



/**
 *
 *  Logon to the mailbox specified
 *
 * @param pAccount		The account to logon to
 * @param pPassword		The password belonging to the account
 * @return				TRUE if the logon succeeded
 */
BOOL Mailbox::Logon( LPCWSTR pAccount, LPCWSTR pPassword )
{
	IXMLDOMDocument2* pResponseDoc = NULL;

	try
	{
		//create the request
		Liquid::Rpc::AuthRequest authRequest( pAccount, pPassword );	

		//process it (an exception will be thrown if an error was returned)
		ProcessRequest( authRequest, FALSE, pResponseDoc );

		//yank out the auth token
		_session.Initialize( pResponseDoc );

		SafeRelease(pResponseDoc);

		//if we were successful, we have an auth token
		return (_session.AuthToken() != NULL);
	}
	catch( LiquidException& le )
	{
		SafeRelease(pResponseDoc);
		throw LiquidLogonException(le);
	}
	catch(...)
	{
		SafeRelease(pResponseDoc);
		throw;
	}
}



/**
 *
 *  When we start batching, all subsequent requests go into the batch request
 *  we issue the request when ExecuteBatchRequest is called
 *
 **/
void Mailbox::StartBatching()
{
	_bBatching = true;
	if( _pBatchRequest != NULL )
	{
		delete _pBatchRequest;
		_pBatchRequest = NULL;
	}

	_pBatchRequest = new Liquid::Rpc::BatchRequest();
	_batchIdx = 0;
}



BatchResponse Mailbox::ExecuteBatchRequest( LPWSTR pTargetAccount )
{
	if( _pBatchRequest == NULL )
	{
		_bBatching = FALSE;
		throw LiquidException( L"ExecuteBatchRequest failed", L"no batch request exists" );
	}
	IXMLDOMDocument2* pResponseDoc = NULL;

	try
	{
		try
		{
			ProcessRequest( *_pBatchRequest, pResponseDoc, pTargetAccount );
		}
		catch( LiquidException& le )
		{
			//swallow this...
		}
		BatchResponse response( pResponseDoc );
		SafeRelease(pResponseDoc);
		_bBatching = FALSE;
		delete _pBatchRequest;
		_pBatchRequest = NULL;
		return response;
		
	}
	catch(...)
	{
		SafeRelease(pResponseDoc);
		_bBatching = FALSE;
		delete _pBatchRequest;
		_pBatchRequest = NULL;
		throw;
	}

}




/**
 *  Retrieve contacts from the server
 *
 * @param pContactIds		The list of contact id's to retrieve.  If nContactIds is 0, all contacts are retrieved
 * @param nContactIds		The number of entries pointed to by pContactIds
 * @param pAttrList			The list of attributes to retrieve for each contact.  If nAttrs is 0, all attributes ar retrieved
 * @param nAttrs			The number of entries pointed to by pAttrList
 * @return					The list of contacts.  Caller must delete the ContactList.
 */
ContactList* Mailbox::GetContacts(ContactId* pContactIds, UINT nContactIds, LPWSTR* pAttrList, UINT nAttrs, LPWSTR pTargetAccount )
{
	IXMLDOMDocument2* pResponseDoc = NULL;

	try
	{
		//create the request
		Liquid::Rpc::GetContactsRequest request(pContactIds, nContactIds, pAttrList, nAttrs );

		//ship it
		ProcessRequest( request, pResponseDoc, pTargetAccount );

		//create the list of contacts from the soap doc
		ContactList* pRetVal = Contact::CreateContactList( pResponseDoc );

		SafeRelease(pResponseDoc);

		return pRetVal; 
	}
	catch(...)
	{
		SafeRelease(pResponseDoc);
		throw;
	}

}

void Mailbox::Ping(BOOL bUseSid)
{
	IXMLDOMDocument2* pResponseDoc = NULL;
	try
	{
		Liquid::Rpc::PingRequest request;
		ProcessRequest( request, bUseSid, pResponseDoc );
		SafeRelease(pResponseDoc);
	}
	catch(...)
	{
		SafeRelease(pResponseDoc);
		throw;
	}
}


/**
 *  Returns the folder hierarchy
 *
 *  @return		A folder object.  Caller must delete the object.
 */
Liquid::Mail::Folder* Mailbox::GetFolder(LPWSTR pTargetAccount)
{
	IXMLDOMDocument2* pResponseDoc = NULL;
	try
	{
		Liquid::Rpc::GetFolderRequest request;

		ProcessRequest( request, pResponseDoc, pTargetAccount );

		Folder* pFolder =  new Liquid::Mail::Folder(pResponseDoc);

		SafeRelease( pResponseDoc );

		return pFolder;
	}
	catch(...)
	{
		SafeRelease(pResponseDoc);
		throw;
	}
}



/**
 *  Returns all the tags
 *
 *  @return		A list of tags.  Caller must free the list.
 *
 **/
TagList* Mailbox::GetTag(LPWSTR pTargetAccount)
{
	IXMLDOMDocument2* pResponseDoc = NULL;

	try
	{
		Liquid::Rpc::GetTagRequest request;

		ProcessRequest( request, pResponseDoc, pTargetAccount );

		TagList* pList = Tag::CreateTagList(pResponseDoc);

		SafeRelease(pResponseDoc);

		return pList;
	}
	catch(...)
	{
		SafeRelease(pResponseDoc);
		throw;
	}
}



/**
 *  Creates a contact
 *
 * @param contactAttributes		The contact's attributes
 * @param parentFolderId		The folder to create the contact in
 * @return						The id of the new contact
 */
ContactId Mailbox::CreateContact(AttributeMap& contactAttributes, FolderId parentFolderId, TagIdList& tags, LPWSTR pTargetAccount )
{

	IXMLDOMDocument2* pResponseDoc = NULL;

	try
	{
		Liquid::Rpc::CreateContactRequest request(contactAttributes, tags, parentFolderId);
		if( _bBatching )
		{
			_pBatchRequest->AppendRequest( request, _batchIdx );
			return _batchIdx++;
		}

		ProcessRequest( request, pResponseDoc, pTargetAccount );

		LPWSTR pContactId = NULL;
		XmlUtil::FindNodeAttributeValue( pResponseDoc, L"//mail:cn", L"id", pContactId );
		if( pContactId == NULL )
			throw LiquidException( L"contact_id_not_returned", L"No contact id was returned." );

		ContactId contactId = _wtoi64( pContactId );
		delete [] pContactId;

		SafeRelease(pResponseDoc);

		return contactId;
	}
	catch(...)
	{
		SafeRelease(pResponseDoc);
		throw;
	}
}




/**
 *  Creates a folder on the server
 *
 * @param pFolderName		The name to give the folder
 * @param parentFolderId	The folders parent folder
 * @return					The id of the new folder
 */
FolderId Mailbox::CreateFolder(LPWSTR pFolderName, FolderId parentFolderId, LPWSTR pTargetAccount)
{
	IXMLDOMDocument2* pResponseDoc = NULL;

	try
	{
		Liquid::Rpc::CreateFolderRequest request(pFolderName, parentFolderId);

		ProcessRequest( request, pResponseDoc, pTargetAccount );

		LPWSTR pFolderId = NULL;
		XmlUtil::FindNodeAttributeValue( pResponseDoc, L"//mail:folder", L"id", pFolderId );
		if( pFolderId == NULL )
			throw LiquidException( L"folder not created", L"no folder id returned" );

		FolderId folderId = _wtoi64( pFolderId );
		delete [] pFolderId;

		SafeRelease(pResponseDoc);

		return folderId;
	}
	catch(...)
	{
		SafeRelease(pResponseDoc);
		throw;
	}
}




/**
 *  Creates a tag
 *
 * @param pTagName		The name to give the tag
 * @param tagColor		The color of the tag
 * @return				The id of the new tag
 */
TagId Mailbox::CreateTag(LPWSTR pTagName, Tag::Color tagColor, LPWSTR pTargetAccount )
{
	IXMLDOMDocument2* pResponseDoc = NULL;

	try
	{
		Liquid::Rpc::CreateTagRequest request(pTagName, tagColor );

		ProcessRequest( request, pResponseDoc, pTargetAccount );

		LPWSTR pTagId = NULL;
		XmlUtil::FindNodeAttributeValue( pResponseDoc, L"//mail:tag", L"id", pTagId );
		if( pTagId == NULL )
			throw LiquidException( L"tag not created", L"no tag id returned" );

		TagId tagId = _wtoi64( pTagId );
		delete [] pTagId;		

		SafeRelease(pResponseDoc);

		return tagId;
	}
	catch(...)
	{
		SafeRelease(pResponseDoc);
		throw;
	}
}

/**
 * Add a message to the mailbox
 *
 * @param	pMimeMsg		Buffer containing the mime message to add
 * @param	parentFolderId	The id of the folder the message should be deposited into
 * @param	flags			The flags on the message
 * @param	tags			The list of tags on the message
 *
 * @return					The id of the message that was created
 **/
MessageId Mailbox::AddMessage( LPCWSTR pMimeMsg, FolderId parentFolderId, MessageFlags& flags, TagIdList& tags, LPWSTR pTargetAccount )
{
	IXMLDOMDocument2* pResponseDoc = NULL;
	try
	{
		Liquid::Rpc::AddMessageRequest request(pMimeMsg, parentFolderId, (LPWSTR)flags, tags);
		if( _bBatching )
		{
			_pBatchRequest->AppendRequest( request, _batchIdx );
			return _batchIdx++;
		}

		ProcessRequest( request, pResponseDoc, pTargetAccount );

		LPWSTR pMbxId = NULL;
		XmlUtil::FindNodeAttributeValue( pResponseDoc, L"//mail:m", L"id", pMbxId );
		if( pMbxId == NULL )
			throw LiquidException( L"message not added", L"no message id returned" );

		MessageId mbxId = _wtoi64(pMbxId);
		delete [] pMbxId;

		SafeRelease(pResponseDoc);
		return mbxId;
	}
	catch( ... )
	{
		SafeRelease( pResponseDoc );
		throw;
	}
}




/**
 *  Process the request - set the auth token and session id of the request
 *
 * @param request			- The request to send
 * @param pResponseDoc		- The return value
 */
void Mailbox::ProcessRequest( Liquid::Rpc::Request& request, IXMLDOMDocument2*& pResponseDoc, LPWSTR pTargetAccount )
{
	ProcessRequest( request, TRUE, pResponseDoc, pTargetAccount ); 
}





/**
 *  Process the request
 *
 * @param request			- The request to send
 * @param bSetSnA			- If true, the auth token and session id will be set.
 * @param pResponseDoc		- The return value
 */
void Mailbox::ProcessRequest( Liquid::Rpc::Request& request, BOOL bSetSnA, IXMLDOMDocument2*& pResponseDoc, LPWSTR pTargetAccount )
{
	request.SetAuthToken( _session.AuthToken() );

	if( bSetSnA )
	{
		request.SetSessionId( _session.SessionId() );			
	}

	if( pTargetAccount != NULL )
		request.SetTargetAccount( pTargetAccount );

	//ship it
	_connection.SendRequest( request, pResponseDoc );

	
	pResponseDoc->setProperty(L"SelectionLanguage",  _variant_t(L"XPath"));
	pResponseDoc->setProperty( L"SelectionNamespaces", 
		_variant_t("xmlns:mail='urn:liquidMail' xmlns:l='urn:liquid' xmlns:account='urn:liquidAccount' xmlns:soap='http://www.w3.org/2003/05/soap-envelope'") );

	//did it barf?
	ExceptionManager::tryThrowException( pResponseDoc );

	//any updates to the session id?
	_session.Update(pResponseDoc);
}











/*********************************************************************************************************************
 *********************************************************************************************************************
	Folder Object
 *********************************************************************************************************************
 *********************************************************************************************************************/


/**
 *
 * @param pDoc 
 */
void Liquid::Mail::Folder::init(IXMLDOMDocument2* pDoc)
{
	//read the values out of the dom and into the member variables
	pDoc->selectSingleNode( (LPWSTR)(L"//mail:folder"), &_pFolderNode );
	init(_pFolderNode);
}


/**
 *
 * @param pNode 
 */
void Liquid::Mail::Folder::init(IXMLDOMNode* pNode )
{
	_pFolderNode = pNode;

	//get the id attribute
	LPWSTR pIdStr = NULL;
	XmlUtil::GetSingleAttribute( _pFolderNode, L"id", pIdStr );

	if( pIdStr == NULL )
		return;

	_folderId = _wtoi( pIdStr );
	delete [] pIdStr;

	if( _folderId == SpecialFolderId::ROOT )
	{
		_parentFolderId = SpecialFolderId::ROOT;
		_pFolderName = NULL;
		return;
	}

	//get the parent folder id
	XmlUtil::GetSingleAttribute( _pFolderNode, L"l", pIdStr );
	if( pIdStr == NULL )
	{
		_parentFolderId = -1;
	}
	else
	{
		_parentFolderId = _wtoi(pIdStr);
		delete [] pIdStr;
	}

	//get the name of the folder
	XmlUtil::GetSingleAttribute( _pFolderNode, L"name", _pFolderName );
}



/**
 *
 * @param pFolderName 
 * @return 
 */
Liquid::Mail::Folder* Liquid::Mail::Folder::GetFolderByName( FolderId parentFolderId, LPWSTR pFolderName )
{
	IXMLDOMNode* pFolderNode = NULL;

	//allocate a buffer large enough for both queries
	LPWSTR pQuery = new WCHAR[ 64 + wcslen(pFolderName) + 36 ];

	//find all folder nodes with the given folder id
	swprintf( pQuery, L"//mail:folder[@l=\"%d\" and @name=\"%s\"]", (int)parentFolderId, pFolderName);
	_pFolderNode->selectSingleNode( pQuery, &pFolderNode );

	delete [] pQuery;

	//didn't find the parent, the child can't exist
	if( pFolderNode == NULL )
	{
		return NULL;
	}

	//we found the bastard!
	return new Liquid::Mail::Folder(pFolderNode);
}




/**
 *
 * @return 
 */
BOOL Liquid::Mail::Folder::hasChildren()
{
	VARIANT_BOOL hasChildren = 0;
	_pFolderNode->hasChildNodes(&hasChildren);
	return hasChildren;
}



/**
 *
 * @return 
 */
Liquid::Mail::FolderList* Liquid::Mail::Folder::getChildren()
{
	FolderList* pFolderList = new FolderList();

	IXMLDOMNodeList* pChildNodes = NULL;
	_pFolderNode->get_childNodes(&pChildNodes);

	if( pChildNodes == NULL )
		return pFolderList;

	long nChildren = 0;
	pChildNodes->get_length(&nChildren);

	for( int i = 0; i < nChildren; i++ )
	{
		IXMLDOMNode* pChild = NULL;
		pChildNodes->get_item( i, &pChild );
		if( pChild == NULL )
			continue;

		Folder* pFolder = new Liquid::Mail::Folder(pChild);
		pFolderList->push_back(pFolder);
	}

	pChildNodes->Release();
	return pFolderList;
}



/*********************************************************************************************************************
 *********************************************************************************************************************
	Tag Object
 *********************************************************************************************************************
 *********************************************************************************************************************/


Tag::Tag(LPWSTR pTagName, TagId tagId, Tag::Color tagColor ) : _tagId(tagId), _tagColor(tagColor)
{
	CopyString( _pTagName, pTagName );
}

/**
 *
 * @param pTagNode 
 * @return 
 */
Tag::Tag(IXMLDOMNode* pTagNode )
{
	LPWSTR pTagId = NULL;
	XmlUtil::GetSingleAttribute( pTagNode, L"name", _pTagName );
	XmlUtil::GetSingleAttribute( pTagNode, L"id", pTagId );

	if( pTagId != NULL )
	{
		_tagId = _wtoi( pTagId );
		delete [] pTagId;
	}
	else
	{
		_tagId = -1;
	}
}



/**
 *
 * @return 
 */
Tag::~Tag()
{
	SafeDelete(_pTagName);
}



/**
 *
 * @param pDoc 
 * @return 
 */
TagList* Tag::CreateTagList( IXMLDOMDocument2* pDoc )
{
	IXMLDOMNodeList* pTagNodes = NULL;
	pDoc->selectNodes( (LPWSTR)(L"//mail:tag"), &pTagNodes );

	long nTags = 0;
	pTagNodes->get_length(&nTags);

	TagList* pTagList = new TagList();

	for( int i = 0; i < nTags; i++ )
	{
		IXMLDOMNode* pTagNode;
		pTagNodes->get_item(i, &pTagNode);
		Tag* pTag = new Tag(pTagNode);
		pTagList->push_back(pTag);
		pTagNode->Release();
	}

	return pTagList;
}




/*********************************************************************************************************************
 *********************************************************************************************************************
	Contact Object
 *********************************************************************************************************************
 *********************************************************************************************************************/



LPCWSTR Liquid::Mail::Contact::A_callbackPhone = L"callbackPhone";
LPCWSTR Liquid::Mail::Contact::A_carPhone = L"carPhone";
LPCWSTR Liquid::Mail::Contact::A_company = L"company";
LPCWSTR Liquid::Mail::Contact::A_companyPhone = L"companyPhone";
LPCWSTR Liquid::Mail::Contact::A_email = L"email";
LPCWSTR Liquid::Mail::Contact::A_email2 = L"email2";
LPCWSTR Liquid::Mail::Contact::A_email3 = L"email3";
LPCWSTR Liquid::Mail::Contact::A_fileAs = L"fileAs";
LPCWSTR Liquid::Mail::Contact::A_firstName = L"firstName";
LPCWSTR Liquid::Mail::Contact::A_homeCity = L"homeCity";
LPCWSTR Liquid::Mail::Contact::A_homeCountry = L"homeCountry";
LPCWSTR Liquid::Mail::Contact::A_homeFax = L"homeFax";
LPCWSTR Liquid::Mail::Contact::A_homePhone = L"homePhone";
LPCWSTR Liquid::Mail::Contact::A_homePhone2 = L"homePhone2";
LPCWSTR Liquid::Mail::Contact::A_homePostalCode = L"homePostalCode";
LPCWSTR Liquid::Mail::Contact::A_homeState = L"homeState";
LPCWSTR Liquid::Mail::Contact::A_homeStreet = L"homeStreet";
LPCWSTR Liquid::Mail::Contact::A_homeURL = L"homeURL";
LPCWSTR Liquid::Mail::Contact::A_jobTitle = L"jobTitle";
LPCWSTR Liquid::Mail::Contact::A_lastName = L"lastName";
LPCWSTR Liquid::Mail::Contact::A_middleName = L"middleName";
LPCWSTR Liquid::Mail::Contact::A_mobilePhone = L"mobilePhone";
LPCWSTR Liquid::Mail::Contact::A_namePrefix = L"namePrefix";
LPCWSTR Liquid::Mail::Contact::A_nameSuffix = L"nameSuffix";
LPCWSTR Liquid::Mail::Contact::A_notes = L"notes";
LPCWSTR Liquid::Mail::Contact::A_otherCity = L"otherCity";
LPCWSTR Liquid::Mail::Contact::A_otherCountry = L"otherCountry";
LPCWSTR Liquid::Mail::Contact::A_otherFax = L"otherFax";
LPCWSTR Liquid::Mail::Contact::A_otherPhone = L"otherPhone";
LPCWSTR Liquid::Mail::Contact::A_otherPostalCode = L"otherPostalCode";
LPCWSTR Liquid::Mail::Contact::A_otherState = L"otherState";
LPCWSTR Liquid::Mail::Contact::A_otherStreet = L"otherStreet";
LPCWSTR Liquid::Mail::Contact::A_otherURL = L"otherURL";
LPCWSTR Liquid::Mail::Contact::A_pager = L"pager";
LPCWSTR Liquid::Mail::Contact::A_workCity = L"workCity";
LPCWSTR Liquid::Mail::Contact::A_workCountry = L"workCountry";
LPCWSTR Liquid::Mail::Contact::A_workFax = L"workFax";
LPCWSTR Liquid::Mail::Contact::A_workPhone = L"workPhone";
LPCWSTR Liquid::Mail::Contact::A_workPhone2 = L"workPhone2";
LPCWSTR Liquid::Mail::Contact::A_workPostalCode = L"workPostalCode";
LPCWSTR Liquid::Mail::Contact::A_workState = L"workState";
LPCWSTR Liquid::Mail::Contact::A_workStreet = L"workStreet";
LPCWSTR Liquid::Mail::Contact::A_workURL = L"workURL";


/**
 *
 * @param pContactNode 
 * @return 
 */
Contact::Contact(IXMLDOMNode* pContactNode)
{
	//get the id attribute
	LPWSTR pId = NULL;
	XmlUtil::GetSingleAttribute(pContactNode, L"id", pId );
	if( pId != NULL )
	{
		_id = _wtoi(pId);
		delete [] pId;
		pId = NULL;
	}

	//get the parentFolderId attribute
	XmlUtil::GetSingleAttribute(pContactNode, L"l", pId );
	if( pId != NULL )
	{
		_parentFolderId = _wtoi(pId);
		delete [] pId;
		pId = NULL;
	}

	//TODO: this might not be MSXML40 friendly
	//get all the "a" children and add each attribute to the hash table
	IXMLDOMNodeList* pANodes = NULL;
	pContactNode->selectNodes( L"mail:a", &pANodes );

	if( pANodes == NULL )
		return;

	long nAttrs = 0;
	pANodes->get_length(&nAttrs);

	for( long i = 0; i < nAttrs; i++ )
	{
		IXMLDOMNode* pANode = NULL;
		pANodes->get_item( i, &pANode );
		if( pANode == NULL )
			continue;

		LPWSTR pAttrName = NULL;
		LPWSTR pAttrVal  = NULL;
		XmlUtil::GetSingleAttribute( pANode, L"n", pAttrName );
		XmlUtil::GetNodeValue( pANode, pAttrVal );

		_attributes[pAttrName] = pAttrVal;
		pANode->Release();
		pANode = NULL;
	}

	pANodes->Release();
}




/**
 *
 * @return 
 */
Contact::~Contact()
{
	AttributeMapIterator iter = _attributes.begin();
	AttributeMapIterator end  = _attributes.end();

	while( iter != end )
	{
		delete iter->first;
		delete iter->second;
		iter++;
	}

	_attributes.clear();
}




/**
 *
 * @param pDoc 
 * @return 
 */
ContactList* Contact::CreateContactList( IXMLDOMDocument2* pDoc )
{
	ContactList* pContactList = new ContactList();
	IXMLDOMNodeList* pContactNodes = NULL;
	pDoc->selectNodes( L"//mail:cn", &pContactNodes );

	if( pContactNodes == NULL )
		return pContactList;

	long nContacts = 0;
	pContactNodes->get_length(&nContacts);

	for( long i = 0; i < nContacts; i++ )
	{
		IXMLDOMNode* pContactNode = NULL;
		pContactNodes->get_item( i, &pContactNode );
		if( pContactNode == NULL )
			continue;

		pContactList->push_back( new Contact(pContactNode) );
		pContactNode->Release();
	}

	pContactNodes->Release();
	return pContactList;
}





///////////////////////////////////////////////////////////////////////////////


void ExceptionManager::tryThrowException(IXMLDOMDocument2* pDoc)
{
	if( pDoc == NULL )
	{
		//todo: throw something
	}

	LPWSTR pErrorMessage	= NULL;
	LPWSTR pErrorCode		= NULL;

	XmlUtil::FindNodeValue( pDoc, L"//soap:Text", pErrorMessage );
	XmlUtil::FindNodeValue( pDoc, L"//l:Code", pErrorCode );

	if( pErrorCode == NULL )
		return;

	throw LiquidException(pErrorCode, pErrorMessage );
}


