#pragma once
#include <list>
#include <map>
#include "Rpc.h"

namespace Liquid { namespace Mail {



typedef __int64 TagId;
typedef __int64 ContactId;
typedef __int64 FolderId;
typedef __int64 MessageId;


/******************************************************************************
 *  A list that owns its members.  The members must be pointers and are 
 *  deleted when the list is destroyed.  This is used for lists returned 
 *  by the Mailbox class.
 ******************************************************************************/
template<class T>
class InternalFreeList : public std::list<T>
{
	public:
		virtual ~InternalFreeList()
		{
			for( std::list<T>::iterator i = this->begin(); i != this->end(); i++ )
			{
				T t = *i;
				delete t;
			}
			this->clear();
		}
};


class Folder;
typedef InternalFreeList<Folder*>		FolderList;
typedef std::list<Folder*>::iterator	FolderListIterator;

class Tag;
typedef InternalFreeList<Tag*>			TagList;
typedef std::list<Tag*>::iterator		TagListIterator;

typedef std::list<TagId>				TagIdList;
typedef std::list<TagId>::iterator		TagIdListIterator;

class Contact;
typedef InternalFreeList<Contact*>		ContactList;
typedef std::list<Contact*>::iterator	ContactListIterator;


typedef std::map<LPWSTR,LPWSTR>				AttributeMap;
typedef std::map<LPWSTR,LPWSTR>::iterator	AttributeMapIterator;



/******************************************************************************
 *  
 *  A Tag
 *  
 ******************************************************************************/
class Tag
{
	friend class Mailbox;
	public:
		enum Color { MinColor=0, Orange=0, Blue, Cyan, Green, Purple, Red, Yellow, NColors };

	public:
		Tag( LPWSTR pTagName, TagId tagId, Tag::Color tagColor );
		~Tag();
		LPWSTR& Name(){ return _pTagName; }
		TagId    Id(){ return _tagId; }

		
		
	private:
		Tag( IXMLDOMNode* _pTagNode );
		static TagList* CreateTagList( IXMLDOMDocument2* pDOc );

	private:
		LPWSTR _pTagName;
		TagId _tagId;
		Tag::Color _tagColor;

};


/******************************************************************************
 *  
 *  MessageFlags - flags a message can contain
 *  
 ******************************************************************************/
class MessageFlags
{
	public:
		MessageFlags() : _pFlags(NULL)
		{
			_pFlags = new WCHAR[6];
			_pFlags[0] = '\0';
		}

		MessageFlags( MessageFlags& mf )
		{
			_pFlags = new WCHAR[6];
			wcscpy( _pFlags, mf._pFlags );
		}

		void operator=(const MessageFlags& mf)
		{
			wcscpy( _pFlags, mf._pFlags);
		}

		~MessageFlags(){ Liquid::Util::SafeDelete(_pFlags); }

		void SetUnread()   { wcscat(_pFlags, L"u"); }
		void SetFlagged()  { wcscat(_pFlags, L"f"); }
		void SetReplied()  { wcscat(_pFlags, L"r"); }
		void SetSentByMe() { wcscat(_pFlags, L"s"); }
		void SetForwarded(){ wcscat(_pFlags, L"w"); }

		operator LPWSTR(){ return _pFlags; }
	private:
		LPWSTR _pFlags;
};

/******************************************************************************
 *  
 *  Batch Response - What is returned from Mailbox::ExecuteBatchRequest
 *  
 ******************************************************************************/
class BatchResponse
{
	public:
		BatchResponse( IXMLDOMDocument2* pResponseDoc );
		BatchResponse( const BatchResponse& );
		~BatchResponse();
		
		void operator=(const BatchResponse& mf);

		ContactId GetCreateContactResponse( __int64 id );
		MessageId GetAddMessageResponse( __int64 id );

	private:
		BatchResponse(); //don't do iT.

	private:
		IXMLDOMDocument2* _pBatchResponse;
};


/******************************************************************************
 *  
 *  A Mailbox - the main class
 *  
 ******************************************************************************/
class Mailbox
{
	friend class LiquidServer;

	public:
		Mailbox( LPWSTR pServer, UINT nPort );
		~Mailbox();

		//authenticate
		BOOL		Logon( LPCWSTR pAccount, LPCWSTR pPassword );

		//batching 
		void			StartBatching();
		BatchResponse	ExecuteBatchRequest( LPWSTR pTargetAccount = NULL );

		////read operations
		TagList*		GetTag( LPWSTR pTargetAccount = NULL );
		Folder*			GetFolder( LPWSTR pTargetAccount = NULL );
		ContactList*	GetContacts( LPWSTR pTargetAccount = NULL ){ return GetContacts(NULL, 0, NULL, 0, pTargetAccount ); }
		ContactList*	GetContacts( ContactId* pContactIds, UINT nContactIds, LPWSTR* pAttrList, UINT nAttrs, LPWSTR pTargetAccount = NULL  );

		//////write operations
		TagId			CreateTag( LPWSTR pTagName, Tag::Color tagColor, LPWSTR pTargetAccount = NULL  );
		FolderId		CreateFolder( LPWSTR pFolderName, FolderId parentFolderId, LPWSTR pTargetAccount = NULL  );
		ContactId		CreateContact( AttributeMap& contactAttributes, FolderId parentFolderId, TagIdList& tags, LPWSTR pTargetAccount = NULL  );
		MessageId		AddMessage( LPCWSTR pMimeMsg, FolderId parentFolderId, MessageFlags& flags, TagIdList& tags, LPWSTR pTargetAccount = NULL  );
		
		/////is it alive?
		void			Ping(BOOL bUseSid);

	
		void ProcessRequest( Liquid::Rpc::Request& request, IXMLDOMDocument2*& pResponseDoc, LPWSTR pTargetAccount = NULL );
		void ProcessRequest( Liquid::Rpc::Request& request, BOOL bSetAuth, IXMLDOMDocument2*& pResponseDoc, LPWSTR pTargetAccount = NULL );

	private:
		Liquid::Rpc::Connection		_connection;
		Liquid::Rpc::SessionData	_session;
		Liquid::Rpc::BatchRequest*  _pBatchRequest;
		BOOL						_bBatching;
		__int64						_batchIdx;
};




/******************************************************************************
 *  
 *  A Folder
 *  
 ******************************************************************************/
class Folder
{
	friend class Mailbox;

	public:
		enum SpecialFolderId { SFID_MIN = 0, SFID_NONE=0, ROOT=1, INBOX, TRASH, SPAM, SENT_MAIL, DRAFTS, CONTACTS,  TAGS, CONVERSATIONS, CALENDAR, SFID_MAX};

	public:
		~Folder()
		{
			Liquid::Util::SafeDelete(_pFolderName);
			SafeRelease(_pFolderNode);
		}

		BOOL isRoot    (){ return isSpecialFolder(SpecialFolderId::ROOT); }
		BOOL isInbox   (){ return isSpecialFolder(SpecialFolderId::INBOX); }
		BOOL isTrash   (){ return isSpecialFolder(SpecialFolderId::TRASH); }
		BOOL isSpam    (){ return isSpecialFolder(SpecialFolderId::SPAM); }
		BOOL isSentMail(){ return isSpecialFolder(SpecialFolderId::SENT_MAIL); }
		BOOL isDrafts  (){ return isSpecialFolder(SpecialFolderId::DRAFTS); }
		BOOL isContacts(){ return isSpecialFolder(SpecialFolderId::CONTACTS); }

		BOOL isSpecialFolder(){ return (_folderId > SFID_MIN && _folderId < SFID_MAX); }
		BOOL hasChildren();

		FolderList* getChildren();
		Folder*		GetFolderByName( FolderId parentFolderId, LPWSTR pFolderName ); //find a folder

		LPWSTR&		Name(){ return _pFolderName; }
		FolderId    Id()  { return _folderId; }
		FolderId	ParentFolderId(){ return _parentFolderId; }

	private:
		BOOL isSpecialFolder( SpecialFolderId sfid ){ return _folderId == sfid; }
		void init(IXMLDOMDocument2*);
		void init(IXMLDOMNode*);

	protected:
		Folder( IXMLDOMDocument2* pDoc )
		{
			_pFolderName = NULL;
			_folderId = -1;
			_parentFolderId = -1;
			init(pDoc);
		}

		Folder( IXMLDOMNode* pNode )
		{
			_pFolderName = NULL;
			_folderId = -1;
			_parentFolderId = -1;
			init(pNode);
		}

	private:
		IXMLDOMNode* _pFolderNode;
		LPWSTR _pFolderName;
		FolderId _folderId;
		FolderId _parentFolderId;
};




/******************************************************************************
 *  
 *  A Contact
 *  
 ******************************************************************************/
class Contact
{
	friend class Mailbox;
	
	public:
		~Contact();
		int Id(){ return _id; }
		int ParentFolderId(){ return _parentFolderId; }
		AttributeMap& Attributes(){ return _attributes; }

	protected:
		static ContactList* CreateContactList( IXMLDOMDocument2* pDoc );
		Contact( IXMLDOMNode* pContactNode );

	
	//member variables
	private:
		AttributeMap _attributes;
		int _id;
		int _parentFolderId;

		Contact();

	
	//predefined contact attributes
	public:
		static LPCWSTR A_callbackPhone;
		static LPCWSTR A_carPhone;
		static LPCWSTR A_company;
		static LPCWSTR A_companyPhone;
		static LPCWSTR A_email;
		static LPCWSTR A_email2;
		static LPCWSTR A_email3;
		static LPCWSTR A_fileAs;
		static LPCWSTR A_firstName;
		static LPCWSTR A_homeCity;
		static LPCWSTR A_homeCountry;
		static LPCWSTR A_homeFax;
		static LPCWSTR A_homePhone;
		static LPCWSTR A_homePhone2;
		static LPCWSTR A_homePostalCode;
		static LPCWSTR A_homeState;
		static LPCWSTR A_homeStreet;
		static LPCWSTR A_homeURL;
		static LPCWSTR A_jobTitle;
		static LPCWSTR A_lastName;
		static LPCWSTR A_middleName;
		static LPCWSTR A_mobilePhone;
		static LPCWSTR A_namePrefix;
		static LPCWSTR A_nameSuffix;
		static LPCWSTR A_notes;
		static LPCWSTR A_otherCity;
		static LPCWSTR A_otherCountry;
		static LPCWSTR A_otherFax;
		static LPCWSTR A_otherPhone;
		static LPCWSTR A_otherPostalCode;
		static LPCWSTR A_otherState;
		static LPCWSTR A_otherStreet;
		static LPCWSTR A_otherURL;
		static LPCWSTR A_pager;
		static LPCWSTR A_workCity;
		static LPCWSTR A_workCountry;
		static LPCWSTR A_workFax;
		static LPCWSTR A_workPhone;
		static LPCWSTR A_workPhone2;
		static LPCWSTR A_workPostalCode;
		static LPCWSTR A_workState;
		static LPCWSTR A_workStreet;
		static LPCWSTR A_workURL;

};

} //namespace Mailbox

/******************************************************************************
 *  
 *  Exception manager handles throwing the right kind of exception
 *  
 ******************************************************************************/
class ExceptionManager
{
	public:
		static void tryThrowException( IXMLDOMDocument2* pDoc );
};



/******************************************************************************
 *  
 *  LiquidException - the only kind of exception, for now.
 *  
 ******************************************************************************/
class LiquidException
{
	public:
		LiquidException( LPWSTR pErrorCode, LPWSTR pMessage ) : _pErrorCode(pErrorCode), _pMessage(pMessage){}
		LiquidException( LPCWSTR pErrorCode, LPCWSTR pMessage )
		{
			if( pErrorCode != NULL )
			{
				_pErrorCode = new WCHAR[ wcslen(pErrorCode ) + 1 ];
				wcscpy(_pErrorCode, pErrorCode );
			}

			if( pMessage != NULL )
			{
				_pMessage = new WCHAR[ wcslen(pMessage) + 1 ];
				wcscpy( _pMessage, pMessage );
			}
		}


		LiquidException( const LiquidException& le )
		{
			_pErrorCode = NULL;
			_pMessage = NULL;

			if( le._pErrorCode != NULL )
			{
				_pErrorCode = new WCHAR[ wcslen(le._pErrorCode ) + 1 ];
				wcscpy(_pErrorCode, le._pErrorCode );
			}

			if( le._pMessage != NULL )
			{
				_pMessage = new WCHAR[ wcslen(le._pMessage) + 1 ];
				wcscpy( _pMessage, le._pMessage );
			}
		}

		virtual ~LiquidException()
		{
			Liquid::Util::SafeDelete(_pMessage);
			Liquid::Util::SafeDelete(_pErrorCode);
		};

		virtual LPCWSTR GetError(){ return _pErrorCode; }
		virtual LPCWSTR GetMessage(){ return _pMessage; }

	private:
		LiquidException(){}

	private:
		LPWSTR _pErrorCode;
		LPWSTR _pMessage;
};


class LiquidLogonException : public LiquidException
{
	public:
		LiquidLogonException( LiquidException& le ) : LiquidException(le) {}
		virtual ~LiquidLogonException(){}
};


}
 