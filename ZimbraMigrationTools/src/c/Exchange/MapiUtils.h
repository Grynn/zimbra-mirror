#pragma once
#include "MAPICommon.h"

#define pidExchangeXmitReservedMin		0x3FE0
#define pidExchangeNonXmitReservedMin	0x65E0
#define	pidProfileMin					0x6600
#define	pidStoreMin						0x6618
#define	pidFolderMin					0x6638
#define	pidMessageReadOnlyMin			0x6640
#define	pidMessageWriteableMin			0x6658
#define	pidAttachReadOnlyMin			0x666C
#define	pidSpecialMin					0x6670
#define	pidAdminMin						0x6690
#define pidSecureProfileMin				PROP_ID_SECURE_MIN
#define pidRenMsgFldMin					0x1080
#define	pidFolderMin					0x6638

#define	PR_RULES_TABLE					PROP_TAG( PT_OBJECT, pidExchangeXmitReservedMin+0x1)

DEFINE_GUID(IID_IExchangeModifyTable, 0x2d734cb0,0x53fd,0x101b,0xb1,0x9d,0x08,0x00,0x2b,0x30,0x56,0xe3);
DEFINE_GUID(IID_IExchangeManageStore, 0x559d10b0,0xa772,0x11cd,0x9b,0xc8,0x00,0xaa,0x00,0x2f,0xc4,0x5a);
DEFINE_GUID(CLSID_MailMessage,0x00020D0B,0x0000, 0x0000, 0xC0, 0x00, 0x0, 0x00, 0x0, 0x00, 0x00, 0x46);

/*------------------------------------------------------------------------
 *
 *	"IExchangeModifyTable" Interface Declaration
 *
 *	Used for get/set rules and access control on folders.
 *
 *-----------------------------------------------------------------------*/


/* ulRowFlags */
#define ROWLIST_REPLACE		((ULONG)1)

#define ROW_ADD				((ULONG)1)
#define ROW_MODIFY			((ULONG)2)
#define ROW_REMOVE			((ULONG)4)
#define ROW_EMPTY			(ROW_ADD|ROW_REMOVE)

typedef struct _ROWENTRY
{
	ULONG			ulRowFlags;
	ULONG			cValues;
	LPSPropValue	rgPropVals;
} ROWENTRY, FAR * LPROWENTRY;

typedef struct _ROWLIST
{
	ULONG			cEntries;
	ROWENTRY		aEntries[MAPI_DIM];
} ROWLIST, FAR * LPROWLIST;

#define EXCHANGE_IEXCHANGEMODIFYTABLE_METHODS(IPURE)					\
	MAPIMETHOD(GetLastError)											\
		(THIS_	HRESULT						hResult,					\
				ULONG						ulFlags,					\
				LPMAPIERROR FAR *			lppMAPIError) IPURE;		\
	MAPIMETHOD(GetTable)												\
		(THIS_	ULONG						ulFlags,					\
				LPMAPITABLE FAR *			lppTable) IPURE;			\
	MAPIMETHOD(ModifyTable)												\
		(THIS_	ULONG						ulFlags,					\
				LPROWLIST					lpMods) IPURE;

#undef		 INTERFACE
#define		 INTERFACE  IExchangeModifyTable
DECLARE_MAPI_INTERFACE_(IExchangeModifyTable, IUnknown)
{
	MAPI_IUNKNOWN_METHODS(PURE)
	EXCHANGE_IEXCHANGEMODIFYTABLE_METHODS(PURE)
};
#undef	IMPL
#define IMPL

DECLARE_MAPI_INTERFACE_PTR(IExchangeModifyTable,	LPEXCHANGEMODIFYTABLE);

/* 	Special flag bit for GetContentsTable, GetHierarchyTable and
	OpenEntry.
	Supported by > 5.x servers 
	If set in GetContentsTable and GetHierarchyTable
	we will show only items that are soft deleted, i.e deleted
	by user but not yet purged from the system. If set in OpenEntry
	we will open this item even if it is soft deleted */
/* Flag bits must not collide by existing definitions in Mapi */
/****** MAPI_UNICODE			((ULONG) 0x80000000) above */
/****** MAPI_DEFERRED_ERRORS	((ULONG) 0x00000008) below */
/****** MAPI_ASSOCIATED			((ULONG) 0x00000040) below */
/****** CONVENIENT_DEPTH		((ULONG) 0x00000001)	   */
#define SHOW_SOFT_DELETES		((ULONG) 0x00000002)

/* 	Special flag bit for DeleteFolder
	Supported by > 5.x servers 
	If set the server will hard delete the folder (i.e it will not be
	retained for later recovery) */
/* Flag bits must not collide by existing definitions in Mapi	*/
/*	DeleteFolder */
/*****	#define DEL_MESSAGES			((ULONG) 0x00000001)	*/
/*****	#define FOLDER_DIALOG			((ULONG) 0x00000002)	*/
/*****	#define DEL_FOLDERS				((ULONG) 0x00000004)	*/
/* EmptyFolder */
/*****	#define DEL_ASSOCIATED			((ULONG) 0x00000008)	*/

#define	DELETE_HARD_DELETE				((ULONG) 0x00000010)

/* Access Control Specifics */

//Properties
#define	PR_MEMBER_ID					PROP_TAG( PT_I8, pidSpecialMin+0x01)
#define	PR_MEMBER_NAME					PROP_TAG( PT_TSTRING, pidSpecialMin+0x02)
#define	PR_MEMBER_ENTRYID				PR_ENTRYID
#define	PR_MEMBER_RIGHTS				PROP_TAG( PT_LONG, pidSpecialMin+0x03)

//Security bits
typedef DWORD RIGHTS;
#define frightsReadAny			0x0000001L
#define	frightsCreate			0x0000002L
#define	frightsEditOwned		0x0000008L
#define	frightsDeleteOwned		0x0000010L
#define	frightsEditAny			0x0000020L
#define	frightsDeleteAny		0x0000040L
#define	frightsCreateSubfolder	0x0000080L
#define	frightsOwner			0x0000100L
#define	frightsContact			0x0000200L	// NOTE: not part of rightsAll
#define	frightsVisible			0x0000400L
#define	rightsNone				0x00000000
#define	rightsReadOnly			frightsReadAny
#define	rightsReadWrite			(frightsReadAny|frightsEditAny)
#define	rightsAll				0x00005FBL

/* Rules specifics */

//Property types
#define	PT_SRESTRICTION				((ULONG) 0x00FD)
#define	PT_ACTIONS					((ULONG) 0x00FE)

//Properties in rule table
#define	PR_RULE_ID						PROP_TAG( PT_I8, pidSpecialMin+0x04)
#define	PR_RULE_IDS						PROP_TAG( PT_BINARY, pidSpecialMin+0x05)
#define	PR_RULE_SEQUENCE				PROP_TAG( PT_LONG, pidSpecialMin+0x06)
#define	PR_RULE_STATE					PROP_TAG( PT_LONG, pidSpecialMin+0x07)
#define	PR_RULE_USER_FLAGS				PROP_TAG( PT_LONG, pidSpecialMin+0x08)
#define	PR_RULE_CONDITION				PROP_TAG( PT_SRESTRICTION, pidSpecialMin+0x09)
#define	PR_RULE_ACTIONS					PROP_TAG( PT_ACTIONS, pidSpecialMin+0x10)
#define	PR_RULE_PROVIDER				PROP_TAG( PT_STRING8, pidSpecialMin+0x11)
#define	PR_RULE_NAME					PROP_TAG( PT_TSTRING, pidSpecialMin+0x12)
#define	PR_RULE_LEVEL					PROP_TAG( PT_LONG, pidSpecialMin+0x13)
#define	PR_RULE_PROVIDER_DATA			PROP_TAG( PT_BINARY, pidSpecialMin+0x14)
// moved to ptag.h (scottno) - still needed for 2.27 upgrader
// #define	PR_RULE_VERSION				PROP_TAG( PT_I2, pidSpecialMin+0x1D)

//PR_STATE property values
#define ST_DISABLED			0x0000
#define ST_ENABLED			0x0001
#define ST_ERROR			0x0002
#define ST_ONLY_WHEN_OOF	0x0004
#define ST_KEEP_OOF_HIST	0x0008
#define ST_EXIT_LEVEL		0x0010

#define ST_CLEAR_OOF_HIST	0x80000000

//Empty restriction
#define NULL_RESTRICTION	0xff

// special RELOP for Member of DL
#define RELOP_MEMBER_OF_DL	100

//Action types
typedef enum
{
	OP_MOVE = 1,
	OP_COPY,
	OP_REPLY,
	OP_OOF_REPLY,
	OP_DEFER_ACTION,
	OP_BOUNCE,
	OP_FORWARD,
	OP_DELEGATE,
	OP_TAG,
	OP_DELETE,
	OP_MARK_AS_READ,

} ACTTYPE;

// provider name for moderator rules
#define szProviderModeratorRule		"MSFT:MR"
#define wszProviderModeratorRule	L"MSFT:MR"

// action flavors

// for OP_REPLY
#define	DO_NOT_SEND_TO_ORIGINATOR		1
#define STOCK_REPLY_TEMPLATE			2

// for OP_FORWARD
#define FWD_PRESERVE_SENDER				1
#define FWD_DO_NOT_MUNGE_MSG			2
#define FWD_AS_ATTACHMENT				4

//scBounceCode values
#define	BOUNCE_MESSAGE_SIZE_TOO_LARGE	(SCODE) MAPI_DIAG_LENGTH_CONSTRAINT_VIOLATD
#define BOUNCE_FORMS_MISMATCH			(SCODE) MAPI_DIAG_RENDITION_UNSUPPORTED
#define BOUNCE_ACCESS_DENIED			(SCODE) MAPI_DIAG_MAIL_REFUSED

//Message class prefix for Reply and OOF Reply templates
#define szReplyTemplateMsgClassPrefix	"IPM.Note.Rules.ReplyTemplate."
#define szOofTemplateMsgClassPrefix		"IPM.Note.Rules.OofTemplate."

//Action structure
typedef struct _action
{
	ACTTYPE		acttype;

	// to indicate which flavour of the action.
	ULONG		ulActionFlavor;

	// Action restriction
	// currently unsed and must be set to NULL
	LPSRestriction	lpRes;

	// currently unused, must be set to 0.
	LPSPropTagArray	lpPropTagArray;

	// User defined flags
	ULONG		ulFlags;

	// padding to align the union on 8 byte boundary
	ULONG		dwAlignPad;

	union
	{
		// used for OP_MOVE and OP_COPY actions
		struct
		{
			ULONG		cbStoreEntryId;
			LPENTRYID	lpStoreEntryId;
			ULONG		cbFldEntryId;
			LPENTRYID	lpFldEntryId;
		} actMoveCopy;

		// used for OP_REPLY and OP_OOF_REPLY actions
		struct
		{
			ULONG		cbEntryId;
			LPENTRYID	lpEntryId;
			GUID		guidReplyTemplate;
		} actReply;

		// used for OP_DEFER_ACTION action
		struct
		{
			ULONG		cbData;
			BYTE		*pbData;
		} actDeferAction;

		// Error code to set for OP_BOUNCE action
		SCODE			scBounceCode;

		// list of address for OP_FORWARD and OP_DELEGATE action
		LPADRLIST		lpadrlist;

		// prop value for OP_TAG action
		SPropValue		propTag;
	};
} ACTION, FAR * LPACTION;

// Rules version
#define EDK_RULES_VERSION		1

//Array of actions
typedef struct _actions
{
	ULONG		ulVersion;		// use the #define above
	UINT		cActions;
	LPACTION	lpAction;
} ACTIONS;

typedef ACTIONS FAR *   LPACTIONS;

// message class definitions for Deferred Action and Deffered Error messages
#define szDamMsgClass		"IPC.Microsoft Exchange 4.0.Deferred Action"
#define szDemMsgClass		"IPC.Microsoft Exchange 4.0.Deferred Error"

/*
 *	Rule error codes
 *	Values for PR_RULE_ERROR
 */
#define	RULE_ERR_UNKNOWN		1			//general catchall error
#define	RULE_ERR_LOAD			2			//unable to load folder rules
#define	RULE_ERR_DELIVERY		3			//unable to deliver message temporarily
#define	RULE_ERR_PARSING		4			//error while parsing
#define	RULE_ERR_CREATE_DAE		5			//error creating DAE message
#define	RULE_ERR_NO_FOLDER		6			//folder to move/copy doesn't exist
#define	RULE_ERR_NO_RIGHTS		7			//no rights to move/copy into folder
#define	RULE_ERR_CREATE_DAM		8			//error creating DAM
#define RULE_ERR_NO_SENDAS		9			//can not send as another user
#define RULE_ERR_NO_TEMPLATE	10			//reply template is missing
#define RULE_ERR_EXECUTION		11			//error in rule execution
#define RULE_ERR_QUOTA_EXCEEDED	12			//mailbox quota size exceeded
#define RULE_ERR_TOO_MANY_RECIPS	13			//number of recips exceded upper limit

#define RULE_ERR_FIRST		RULE_ERR_UNKNOWN
#define RULE_ERR_LAST		RULE_ERR_TOO_MANY_RECIPS

/*------------------------------------------------------------------------
 *
 *	"IExchangeRuleAction" Interface Declaration
 *
 *	Used for get actions from a Deferred Action Message.
 *
 *-----------------------------------------------------------------------*/

#define EXCHANGE_IEXCHANGERULEACTION_METHODS(IPURE)						\
	MAPIMETHOD(ActionCount)												\
		(THIS_	ULONG FAR *					lpcActions) IPURE;			\
	MAPIMETHOD(GetAction)												\
		(THIS_	ULONG						ulActionNumber,				\
				LARGE_INTEGER	*			lpruleid,					\
				LPACTION FAR *				lppAction) IPURE;

#undef		 INTERFACE
#define		 INTERFACE  IExchangeRuleAction
DECLARE_MAPI_INTERFACE_(IExchangeRuleAction, IUnknown)
{
	MAPI_IUNKNOWN_METHODS(PURE)
	EXCHANGE_IEXCHANGERULEACTION_METHODS(PURE)
};
#undef	IMPL
#define IMPL

DECLARE_MAPI_INTERFACE_PTR(IExchangeRuleAction,	LPEXCHANGERULEACTION);


/*------------------------------------------------------------------------
 *
 *	"IExchangeManageStore" Interface Declaration
 *
 *	Used for store management functions.
 *
 *-----------------------------------------------------------------------*/

#define EXCHANGE_IEXCHANGEMANAGESTORE_METHODS(IPURE)					\
	MAPIMETHOD(CreateStoreEntryID)										\
		(THIS_	LPSTR						lpszMsgStoreDN,				\
				LPSTR						lpszMailboxDN,				\
				ULONG						ulFlags,					\
				ULONG FAR *					lpcbEntryID,				\
				LPENTRYID FAR *				lppEntryID) IPURE;			\
	MAPIMETHOD(EntryIDFromSourceKey)									\
		(THIS_	ULONG						cFolderKeySize,				\
				BYTE FAR *					lpFolderSourceKey,			\
				ULONG						cMessageKeySize,			\
				BYTE FAR *					lpMessageSourceKey,			\
				ULONG FAR *					lpcbEntryID,				\
				LPENTRYID FAR *				lppEntryID) IPURE;			\
	MAPIMETHOD(GetRights)												\
		(THIS_	ULONG						cbUserEntryID,				\
				LPENTRYID					lpUserEntryID,				\
				ULONG						cbEntryID,					\
				LPENTRYID					lpEntryID,					\
				ULONG FAR *					lpulRights) IPURE;			\
	MAPIMETHOD(GetMailboxTable)											\
		(THIS_	LPSTR						lpszServerName,				\
				LPMAPITABLE FAR *			lppTable,					\
				ULONG						ulFlags) IPURE;				\
	MAPIMETHOD(GetPublicFolderTable)									\
		(THIS_	LPSTR						lpszServerName,				\
				LPMAPITABLE FAR *			lppTable,					\
				ULONG						ulFlags) IPURE;

#undef		 INTERFACE
#define		 INTERFACE  IExchangeManageStore
DECLARE_MAPI_INTERFACE_(IExchangeManageStore, IUnknown)
{
	MAPI_IUNKNOWN_METHODS(PURE)
	EXCHANGE_IEXCHANGEMANAGESTORE_METHODS(PURE)
};
#undef	IMPL
#define IMPL

DECLARE_MAPI_INTERFACE_PTR(IExchangeManageStore, LPEXCHANGEMANAGESTORE);

// Bit values for PR_PROFILE_OPEN_FLAGS

#define	OPENSTORE_USE_ADMIN_PRIVILEGE		((ULONG)1)
#define OPENSTORE_PUBLIC					((ULONG)2)
#define	OPENSTORE_HOME_LOGON				((ULONG)4)
#define OPENSTORE_TAKE_OWNERSHIP			((ULONG)8)
#define OPENSTORE_OVERRIDE_HOME_MDB			((ULONG)16)
#define OPENSTORE_TRANSPORT					((ULONG)32)
#define OPENSTORE_REMOTE_TRANSPORT			((ULONG)64)
#define	OPENSTORE_INTERNET_ANONYMOUS		((ULONG)128)
#define OPENSTORE_ALTERNATE_SERVER			((ULONG)256) /* reserved for internal use */
#define OPENSTORE_IGNORE_HOME_MDB			((ULONG)512) /* reserved for internal use */
#define OPENSTORE_NO_MAIL					((ULONG)1024)/* reserved for internal use */
#define OPENSTORE_OVERRIDE_LAST_MODIFIER	((ULONG)2048)

inline void HexStrFromBSTR( _bstr_t bstr, LPTSTR& szHex )
{
	int i = 0 ;
	int len = bstr.length();
	BSTR bstrChar = bstr.GetBSTR();

	szHex =  new TCHAR[ 2 * len * sizeof(TCHAR) + 1 ] ;
	ZeroMemory( szHex, 2 * len * sizeof(TCHAR) + 1 );

	LPTSTR szTemp = szHex;
	while( i++ < len )
	{	
		szTemp += wsprintf( szTemp, _T("%X"), *bstrChar++ );
	}
}
inline void WtoA( LPWSTR pStrW, LPSTR& pStrA )
{
	int nWChars = (int)wcslen( pStrW );
	int nAChars = WideCharToMultiByte( CP_ACP, 0, pStrW, nWChars, NULL, 0, NULL, NULL );

	pStrA = new CHAR[nAChars + 1];
	ZeroMemory( (void*)pStrA, nAChars + 1 );
	WideCharToMultiByte( CP_ACP, 0, pStrW, nWChars, pStrA, nAChars, NULL, NULL );
}


inline void AtoW( LPSTR pStrA, LPWSTR& pStrW )
{
	int AChars = (int)strlen(pStrA);
	int WChars = MultiByteToWideChar( CP_ACP, 0, pStrA, AChars, NULL, 0 );

	pStrW = new WCHAR[WChars + 1];
	ZeroMemory( (void*)pStrW, (WChars + 1) * sizeof(WCHAR) );
	MultiByteToWideChar( CP_ACP, 0, pStrA, AChars, pStrW, WChars );
}

inline void SafeDelete( LPWSTR& pStr )
{
	if( pStr != NULL )
	{
		delete [] pStr;
		pStr = NULL;
	}
}

inline void SafeDelete( LPCWSTR pStr )
{
	if( pStr != NULL )
	{
		delete [] pStr;
	}
}

inline void SafeDelete( LPSTR & pStr )
{
	if( pStr != NULL )
	{
		delete [] pStr;
		pStr = NULL;
	}
}

class MapiUtilsException:public GenericException
{
public:
	MapiUtilsException(HRESULT hrErrCode, LPCWSTR lpszDescription):GenericException(hrErrCode,lpszDescription){};
	MapiUtilsException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile):GenericException(hrErrCode,lpszDescription,nLine,strFile){};
	virtual ~MapiUtilsException(){};
};
#define PR_PROFILE_HOME_SERVER_DN						PROP_TAG( PT_STRING8,	0x6612 )
#define PR_PROFILE_USER									PROP_TAG( PT_STRING8,	0x6603 )

namespace Zimbra{ 
	namespace MAPI {
		namespace Util {

HRESULT HrMAPIFindDefaultMsgStore( LPMAPISESSION lplhSession, SBinary &bin);
HRESULT MailboxLogon( LPMAPISESSION pSession, LPMDB pMdb, LPWSTR pStoreDn, LPWSTR pMailboxDn, LPMDB* ppMdb );
HRESULT GetUserDN(LPCWSTR lpszServer, LPCWSTR lpszUser, wstring &wstruserdn);
HRESULT GetUserDnAndServerDnFromProfile( LPMAPISESSION pSession, LPSTR& pExchangeServerDn, LPSTR& pExchangeUserDn );
HRESULT HrMAPIFindIPMSubtree(LPMDB lpMdb, SBinary &bin);
		}
	}
}