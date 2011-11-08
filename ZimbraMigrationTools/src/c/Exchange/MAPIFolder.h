#pragma once

// use this flag on OpenMsgStore to force cached mode connections
// to read remote data and not local data
#define MDB_ONLINE              ((ULONG)0x00000100)

#define GLOBAL_PROFILE_SECTION_GUID \
    "\x13\xDB\xB0\xC8\xAA\x05\x10\x1A\x9B\xB0\x00\xAA\x00\x2F\xC4\x5A"
DEFINE_OLEGUID(PSETID_COMMON, MAKELONG(0x2000 + (8), 0x0006), 0, 0);

// A named property which specifies whether the mail is
// completely downloaded or in header only form in case of IMAP
#define DISPID_HEADER_ITEM      0x8578

namespace Zimbra
{
namespace MAPI
{
class MAPIFolderException: public GenericException
{
public:
    MAPIFolderException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    MAPIFolderException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine, LPCSTR strFile);
    virtual ~MAPIFolderException() {}
};

class MAPIFolder;

// Folder Iterator class
class FolderIterator: public MAPITableIterator
{
private:
    typedef enum _FolderIterPropTagIdx
    {
        FI_DISPLAY_NAME, FI_ENTRYID, FI_PR_LONGTERM_ENTRYID_FROM_TABLE, FI_FLAGS, NFOLDERPROPS
    } FolerIterPropTagIdx;
    typedef struct _FolderIterPropTags
    {
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
    virtual LPSRestriction GetRestriction(ULONG TypeMask, FILETIME startDate)
    {
        UNREFERENCED_PARAMETER(TypeMask);
        UNREFERENCED_PARAMETER(startDate);
        return NULL;
    }

    BOOL GetNext(MAPIFolder &folder);
};

// Exchange System folder enumeration
typedef enum _ExchangeSpecialFolderId
{
    INBOX = 0, IPM_SUBTREE = 1, CALENDAR = 2, CONTACTS = 3, DRAFTS = 4, JOURNAL = 5, NOTE = 6,
    TASK = 7, OUTBOX = 8, SENTMAIL = 9, TRASH = 10, SYNC_CONFLICTS = 11,
    SYNC_ISSUES = 12, SYNC_LOCAL_FAILURES = 13, SYNC_SERVER_FAILURES = 14,
    JUNK_MAIL = 15, TOTAL_NUM_SPECIAL_FOLDERS = 16, SPECIAL_FOLDER_ID_NONE = 1000
} ExchangeSpecialFolderId;

// Zimbra system folder enumeration
typedef enum _ZimbraSpecialFolderId
{
    ZM_SFID_MIN = 0, ZM_SFID_NONE = 0, ZM_ROOT = 1, ZM_INBOX, ZM_TRASH, ZM_SPAM, ZM_SENT_MAIL,
    ZM_DRAFTS, ZM_CONTACTS, ZM_TAGS, ZM_CONVERSATIONS, ZM_CALENDAR, ZM_MAILBOX_ROOT,
    ZM_WIKI, ZM_EMAILEDCONTACTS, ZM_CHATS, ZM_TASKS, ZM_SFID_MAX
} ZimbraSpecialFolderId;

//IPM folders strings if pst doesn't have IPM EntryIDs in Inbox folder
const int g_MAX_STR_IPM_FOLDERS=6;
const wstring g_strIPM_FOLDERS[g_MAX_STR_IPM_FOLDERS] ={L"Calendar",L"Contacts",L"Drafts",L"Journal",L"Notes",L"Tasks"};
// MapiFolder class
class MAPIFolder
{
private:
    LPMAPIFOLDER m_folder;
    wstring m_displayname;
    SBinary m_EntryID;
    MAPISession *m_session;
    MAPIStore *m_store;
    wstring m_folderpath;

    wstring FindFolderPath();

public:
    MAPIFolder();
    MAPIFolder(MAPISession &session, MAPIStore &store);
    ~MAPIFolder();
    MAPIFolder(const MAPIFolder &folder);
    void Initialize(LPMAPIFOLDER pFolder, LPTSTR displayName, LPSBinary pEntryId);
    HRESULT GetItemCount(ULONG &ulCount);
    HRESULT GetMessageIterator(MessageIterator &msgIterator);
    HRESULT GetFolderIterator(FolderIterator &folderIter);

    wstring GetFolderPath() { return m_folderpath; }
    wstring Name() { return m_displayname; }
    SBinary EntryID() { return m_EntryID; }
    ExchangeSpecialFolderId GetExchangeFolderId();
    ZimbraSpecialFolderId GetZimbraFolderId();
    bool HiddenFolder();
    HRESULT ContainerClass(wstring &wstrContainerClass);
};

// global declaration
static ULONG g_ulIMAPHeaderInfoPropTag = PR_NULL;
}                                               // namespace MAPI
}                                               // namespace Zimbra
