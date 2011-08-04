#pragma once

// use this flag on OpenMsgStore to force cached mode connections
// to read remote data and not local data
#define MDB_ONLINE ((ULONG)0x00000100)

#define GLOBAL_PROFILE_SECTION_GUID \
    "\x13\xDB\xB0\xC8\xAA\x05\x10\x1A\x9B\xB0\x00\xAA\x00\x2F\xC4\x5A"
DEFINE_OLEGUID(PSETID_COMMON, MAKELONG(0x2000 + (8), 0x0006), 0, 0);

// A named property which specifies whether the mail is
// completely downloaded or in header only form in case of IMAP
#define DISPID_HEADER_ITEM 0x8578

#define PR_URL_NAME PROP_TAG(PT_TSTRING, 0x6707)

// Item type consts
#define ZCM_NONE                0x00
#define ZCM_MAIL                0x01
#define ZCM_CONTACTS            0x02
#define ZCM_APPOINTMENTS        0x04
#define ZCM_TASKS               0x08
#define ZCM_ALL                 0xFF

#include "MAPITableIterator.h"

namespace Zimbra {
namespace MAPI {
class MAPIFolderException: public GenericException {
public:
    MAPIFolderException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    MAPIFolderException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine, LPCSTR strFile);
    virtual ~MAPIFolderException() {}
};

class MAPIMessageException: public GenericException {
public:
    MAPIMessageException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    MAPIMessageException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine, LPCSTR strFile);
    virtual ~MAPIMessageException() {}
};

class MAPIFolder;

// Folder Iterator class
class FolderIterator: public MAPITableIterator {
private:
    typedef enum _FolderIterPropTagIdx {
        FI_DISPLAY_NAME, FI_ENTRYID,
        FI_PR_LONGTERM_ENTRYID_FROM_TABLE, FI_FLAGS,
        NFOLDERPROPS
    } FolerIterPropTagIdx;
    typedef struct _FolderIterPropTags {
        ULONG cValues;
        ULONG aulPropTags[NFOLDERPROPS];
    } FolderIterPropTags;

protected:
    static FolderIterPropTags m_props;

public:
    FolderIterator();
    ~FolderIterator();
    virtual LPSPropTagArray GetProps();

    virtual LPSSortOrderSet GetSortOrder() { return NULL; }
    virtual LPSRestriction GetRestriction(ULONG TypeMask, FILETIME startDate) {
        UNREFERENCED_PARAMETER(TypeMask);
        UNREFERENCED_PARAMETER(startDate);
        return NULL;
    }

    BOOL GetNext(MAPIFolder &folder);
};

class MAPIMessage;

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

// MAPIMessage Clasc
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

    static MessagePropTags m_messagePropTags;
    static RecipientPropTags m_recipientPropTags;
    static ReplyToPropTags m_replyToPropTags;

public:
    MAPIMessage();
    ~MAPIMessage();
    void Initialize(LPMESSAGE pMessage);
    void InternalFree();
    bool Subject(LPTSTR *ppSubject);
};

// MapiFolder class
class MAPIFolder {
private:
    LPMAPIFOLDER m_folder;
    wstring m_displayname;
    SBinary m_EntryID;

public:
    MAPIFolder();
    ~MAPIFolder();
    MAPIFolder(const MAPIFolder &folder);
    void Initialize(LPMAPIFOLDER pFolder, LPTSTR displayName, LPSBinary pEntryId);
    HRESULT GetItemCount(ULONG &ulCount);
    HRESULT GetMessageIterator(MessageIterator &msgIterator);
    HRESULT GetFolderIterator(FolderIterator &folderIter);

    wstring Name() { return m_displayname; }
};

// global declaration
static ULONG g_ulIMAPHeaderInfoPropTag = PR_NULL;
}                                       // namespace MAPI
}                                       // namespace Zimbra
