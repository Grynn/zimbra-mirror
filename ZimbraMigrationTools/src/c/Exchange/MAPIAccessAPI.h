#pragma once
#include "logger.h"

namespace Zimbra
{
namespace MAPI
{
typedef struct _Folder_Data
{
    wstring name;
    SBinary sbin;
    wstring folderpath;
    long zimbraid;
    unsigned long itemcount;
} Folder_Data;

typedef struct _Item_Data
{
    SBinary sbMessageID;
    long lItemType;
    __int64 MessageDate;

    // parent folder
} Item_Data;

// base item data
typedef struct _BaseItemData
{
    __int64 MessageDate;
} BaseItemData;

// folders to skip
enum
{
    TS_JOURNAL = 0, TS_OUTBOX, TS_SYNC_CONFLICTS,
    TS_SYNC_ISSUES, TS_SYNC_LOCAL_FAILURES, TS_SYNC_SERVER_FAILURES,
    TS_JUNK_MAIL, TS_FOLDERS_MAX
};

// contact item data
typedef struct _ContactItemData: BaseItemData
{
    wstring CallbackPhone;
    wstring CarPhone;
    wstring Company;
    wstring Email1;
    wstring Email2;
    wstring Email3;
    wstring FileAs;
    wstring FirstName;
    wstring HomeCity;
    wstring HomeCountry;
    wstring HomeFax;
    wstring HomePhone;
    wstring HomePhone2;
    wstring HomePostalCode;
    wstring HomeState;
    wstring HomeStreet;
    wstring HomeURL;
    wstring JobTitle;
    wstring LastName;
    wstring MiddleName;
    wstring MobilePhone;
    wstring NamePrefix;
    wstring NameSuffix;
    wstring Notes;
    wstring OtherCity;
    wstring OtherCountry;
    wstring OtherFax;
    wstring OtherPhone;
    wstring OtherPostalCode;
    wstring OtherState;
    wstring OtherStreet;
    wstring OtherURL;
    wstring Pager;
    wstring WorkCity;
    wstring WorkCountry;
    wstring WorkFax;
    wstring WorkPhone;
    wstring WorkPostalCode;
    wstring WorkState;
    wstring WorkStreet;
    wstring WorkURL;
    wstring Birthday;
    wstring UserField1;
    wstring UserField2;
    wstring UserField3;
    wstring UserField4;
    wstring NickName;
    wstring pDList;
    wstring Type;
    wstring PictureID;
    wstring IMAddress1;
} ContactItemData;

typedef struct
{
    LPTSTR buffer;
    unsigned long size;
} data_buffer;

typedef struct _MessageItemData: BaseItemData
{
    wstring Subject;
    bool IsFlagged;
    wstring Urlname;
    bool IsDraft;
    bool IsFromMe;
    bool IsUnread;
    bool IsForwared;
    bool RepliedTo;
    bool HasAttachments;
    bool IsUnsent;
    bool HasHtml;
    bool HasText;
    __int64 deliveryDate;
    wstring DeliveryDateString;
    __int64 Date;
    wstring DateString;
    data_buffer textbody;
    data_buffer htmlbody;
	wstring MimeFile;
} MessageItemData;
class MAPIAccessAPI
{
private:
    static std::wstring m_strAdminProfileName;
    static std::wstring m_strExchangeHostName;
    static Zimbra::MAPI::MAPISession *m_zmmapisession;
    static Zimbra::MAPI::MAPIStore *m_defaultStore;

    std::wstring m_strUserName;
    Zimbra::MAPI::MAPIStore *m_userStore;
    Zimbra::MAPI::MAPIFolder *m_rootFolder;
    ExchangeSpecialFolderId FolderToSkip[TS_FOLDERS_MAX];

    void InitFoldersToSkip();
    bool SkipFolder(ExchangeSpecialFolderId exfid);
    LPCWSTR OpenUserStore();
    HRESULT Iterate_folders(Zimbra::MAPI::MAPIFolder &folder, vector<Folder_Data> &fd);
    void travrese_folder(Zimbra::MAPI::MAPIFolder &folder);
    HRESULT GetInternalFolder(SBinary sbFolderEID, MAPIFolder &folder);

public:
    // static methods to be used by all mailboxes
    static LPCWSTR InitGlobalSessionAndStore(LPCWSTR lpcwstrAdminProfile);
    static void UnInitGlobalSessionAndStore();

    // Per mailbox methods.
    MAPIAccessAPI(wstring strUserName);
    ~MAPIAccessAPI();
    LPCWSTR InitializeUser();
    LPCWSTR GetRootFolderHierarchy(vector<Folder_Data> &vfolderlist);
    LPCWSTR GetFolderItemsList(SBinary sbFolderEID, vector<Item_Data> &ItemList);
    LPCWSTR GetItem(SBinary sbItemEID, BaseItemData &itemData);
};
}
}
