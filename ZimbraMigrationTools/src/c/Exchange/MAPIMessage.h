#pragma once

namespace Zimbra {
namespace MAPI {
#define PR_URL_NAME PROP_TAG(PT_TSTRING, 0x6707)

#define EXCHIVERB_OPEN              0
#define EXCHIVERB_RESERVED_COMPOSE  100
#define EXCHIVERB_RESERVED_OPEN     101
#define EXCHIVERB_REPLYTOSENDER     102
#define EXCHIVERB_REPLYTOALL        103
#define EXCHIVERB_FORWARD           104
#define EXCHIVERB_PRINT             105
#define EXCHIVERB_SAVEAS            106
#define EXCHIVERB_RESERVED_DELIVERY 107
#define EXCHIVERB_REPLYTOFOLDER     108

typedef enum _ZM_ITEM_TYPE {
	ZT_NONE=0, ZT_MAIL, ZT_CONTACTS,
	ZT_APPOINTMENTS,ZT_TASKS, ZT_MEETREQ_RESP,ZTMAX
} ZM_ITEM_TYPE;

//MAPIMessageException class
class MAPIMessageException: public GenericException {
public:
    MAPIMessageException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    MAPIMessageException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine, LPCSTR strFile);
    virtual ~MAPIMessageException() {}
};

// MAPIMessage Class
class MAPIMessage {
private:
    // order of the message properties in _pMessagePropVals
    typedef enum _MessagePropIndex {
        MESSAGE_CLASS, MESSAGE_FLAGS, MESSAGE_DATE,
        SENDER_ADDRTYPE, SENDER_EMAIL_ADDR, SENDER_NAME,
        SENDER_ENTRYID, SUBJECT, TEXT_BODY,
        HTML_BODY, INTERNET_CPID, MESSAGE_CODEPAGE,
        LAST_VERB_EXECUTED, FLAG_STATUS, ENTRYID,
        SENT_ADDRTYPE, SENT_ENTRYID, SENT_EMAIL_ADDR,
        SENT_NAME, REPLY_NAMES, REPLY_ENTRIES,
        MIME_HEADERS, IMPORTANCE, INTERNET_MESSAGE_ID,
        DELIVERY_DATE, URL_NAME, MESSAGE_SIZE,
        STORE_SUPPORT_MASK, RTF_IN_SYNC, NMSGPROPS
    } MessagePropIndex;

    // defined so a static variable can hold the message props to retrieve
    typedef struct _MessagePropTags {
        ULONG cValues;
        ULONG aulPropTags[NMSGPROPS];
    } MessagePropTags;

    // order of the recipient properties in each row of _pRecipRows
    typedef enum _RecipientPropIndex {
        RDISPLAY_NAME, RENTRYID, RADDRTYPE, REMAIL_ADDRESS, RRECIPIENT_TYPE, RNPROPS
    } RecipientPropIndex;

    // defined so a static variable can hold the recipient properties to retrieve
    typedef struct _RecipientPropTags {
        ULONG cValues;
        ULONG aulPropTags[RNPROPS];
    } RecipientPropTags;

    // order of the recipient properties in each row of _pRecipRows
    typedef enum _ReplyToPropIndex {
        REPLYTO_DISPLAY_NAME, REPLYTO_ENTRYID, REPLYTO_ADDRTYPE, REPLYTO_EMAIL_ADDRESS,
        NREPLYTOPROPS
    } ReplyToPropIndex;

    // defined so a static variable can hold the recipient properties to retrieve
    typedef struct _ReplyToPropTags {
        ULONG cValues;
        ULONG aulPropTags[NREPLYTOPROPS];
    } ReplyToPropTags;

    LPMESSAGE m_pMessage;
    LPSPropValue m_pMessagePropVals;
    LPSRowSet m_pRecipientRows;
	SBinary m_EntryID;
	CHAR m_pDateTimeStr[32];
	CHAR m_pDeliveryDateTimeStr[32];

    static MessagePropTags m_messagePropTags;
    static RecipientPropTags m_recipientPropTags;
    static ReplyToPropTags m_replyToPropTags;

public:
    MAPIMessage();
    ~MAPIMessage();
    void Initialize(LPMESSAGE pMessage);
    void InternalFree();
    bool Subject(LPTSTR *ppSubject);
	ZM_ITEM_TYPE ItemType();
	BOOL IsFlagged();
	LPTSTR GetURLName();
	bool IsDraft();
	BOOL IsFromMe();
	BOOL IsUnread();
	BOOL Forwarded();
	BOOL RepliedTo();
	bool HasAttach();
	BOOL IsUnsent();
	bool HasHtmlPart();
	bool HasTextPart();
	SBinary& UniqueId();
	__int64 DeliveryDate();
	LPSTR DateString();
	__int64 Date();
	DWORD Size();
	LPSTR DeliveryDateString();
	SBinary EntryID(){return m_EntryID;}
};

// Message Iterator class
class MessageIterator: public MAPITableIterator {
private:
    class MIRestriction;
    typedef enum _MessageIterPropTagIdx {
        MI_ENTRYID, MI_LONGTERM_ENTRYID_FROM_TABLE, MI_DATE,
        MI_MESSAGE_CLASS, NMSGPROPS
    } MessageIterPropTagIdx;
    typedef struct _MessageIterPropTags {
        ULONG cValues;
        ULONG aulPropTags[NMSGPROPS];
    } MessageIterPropTags;

    typedef struct _MessageIterSort {
        ULONG cSorts;
        ULONG cCategories;
        ULONG cExpanded;
        SSortOrder aSort[1];
    } MessageIterSortOrder;

public:
    MessageIterator();
    virtual ~MessageIterator();
    virtual LPSPropTagArray GetProps();
    virtual LPSSortOrderSet GetSortOrder();
    virtual LPSRestriction GetRestriction(ULONG TypeMask, FILETIME startDate);
    BOOL GetNext(MAPIMessage &msg);
    BOOL GetNext(__int64 &date, SBinary &bin);

protected:
    static MessageIterPropTags m_props;
    static MessageIterSortOrder m_sortOrder;
    static MessageIterator::MIRestriction m_restriction;
};

// Restriction class
class MessageIterator::MIRestriction {
public:
    MIRestriction();
    ~MIRestriction();
    LPSRestriction GetRestriction(ULONG TypeMask, FILETIME startDate);

private:
    SRestriction pR[25];
    SPropValue _propValCont;
    SPropValue _propValMail;
    SPropValue _propValCTime;
    SPropValue _propValSTime;
    SPropValue _propValCanbeMail;
    SPropValue _propValCanbeMailPost;

    SPropValue _propValAppt;
    LPWSTR _pApptClass;

    SPropValue _propValTask;
    LPWSTR _pTaskClass;

    SPropValue _propValReqAndRes;
    LPWSTR _pReqAndResClass;

    SPropValue _propValDistList;
    LPWSTR _pDistListClass;

    LPWSTR _pContactClass;
    LPWSTR _pMailClass;

    SPropValue _propValIMAPHeaderOnly;
};



}
}