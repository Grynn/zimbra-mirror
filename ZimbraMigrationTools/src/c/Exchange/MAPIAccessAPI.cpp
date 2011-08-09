#include "common.h"
#include "Exchange.h"
#include "MAPIAccessAPI.h"

// Initialize with Exchange Sever hostname, Outlook Admin profile name, Exchange mailbox name to be migrated
MAPIAccessAPI::MAPIAccessAPI(wstring strExchangeHostName, wstring strAdminProfileName,
    wstring strUserName): m_zmmapisession(NULL), m_userStore(NULL), m_defaultStore(NULL),
    m_rootFolder(NULL) {
    m_strExchangeHostName = strExchangeHostName;
    m_strAdminProfileName = strAdminProfileName;
    m_strUserName = strUserName;
    MAPIInitialize(NULL);
    Zimbra::Mapi::Memory::SetMemAllocRoutines(NULL, MAPIAllocateBuffer, MAPIAllocateMore,
        MAPIFreeBuffer);
}

MAPIAccessAPI::~MAPIAccessAPI() {
    if (m_defaultStore)
        delete m_defaultStore;
    m_defaultStore = NULL;
    if (m_userStore)
        delete m_userStore;
    m_userStore = NULL;
    if (m_zmmapisession)
        delete m_zmmapisession;
    m_zmmapisession = NULL;
    MAPIUninitialize();
}

// Open MAPI sessiona and Open Stores
HRESULT MAPIAccessAPI::OpenSessionAndStore() {
    HRESULT hr = S_OK;
    wstring wstruserdn;
    LPSTR ExchangeServerDN = NULL;
    LPSTR ExchangeUserDN = NULL;
    LPWSTR pwstrExchangeServerDN = NULL;

    // Logon into Admin profile
    m_zmmapisession = new Zimbra::MAPI::MAPISession();
    hr = m_zmmapisession->Logon((LPWSTR)m_strAdminProfileName.c_str());
    if (hr != S_OK)
        goto CLEAN_UP;
    m_defaultStore = new Zimbra::MAPI::MAPIStore();
    m_userStore = new Zimbra::MAPI::MAPIStore();

    // Open Admin default store
    hr = m_zmmapisession->OpenDefaultStore(*m_defaultStore);
    if (hr != S_OK)
        goto CLEAN_UP;
    // Get Exchange Server DN
    hr = Zimbra::MAPI::Util::GetUserDnAndServerDnFromProfile(
            m_zmmapisession->GetMAPISessionObject(),
            ExchangeServerDN, ExchangeUserDN);
    if (hr != S_OK)
        goto CLEAN_UP;
    AtoW(ExchangeServerDN, pwstrExchangeServerDN);

    // Get DN of user to be migrated
    Zimbra::MAPI::Util::GetUserDN(m_strExchangeHostName.c_str(),
        m_strUserName.c_str(), wstruserdn);
    hr = m_zmmapisession->OpenOtherStore(
            m_defaultStore->GetInternalMAPIStore(), pwstrExchangeServerDN,
            (LPWSTR)wstruserdn.c_str(), *m_userStore);
    if (hr != S_OK)
        goto CLEAN_UP;
CLEAN_UP:
    SafeDelete(ExchangeServerDN);
    SafeDelete(ExchangeUserDN);
    SafeDelete(pwstrExchangeServerDN);

    return hr;
}

// Get root folders
HRESULT MAPIAccessAPI::Initialize() {
    HRESULT hr = S_OK;

    hr = OpenSessionAndStore();
    if (hr != S_OK)
        return hr;
    // Get root folder from user store
    m_rootFolder = new Zimbra::MAPI::MAPIFolder();
    hr = m_userStore->GetRootFolder(*m_rootFolder);
    if (hr != S_OK)
        return hr;
    return hr;
}

// Get Folder hierarchy
HRESULT MAPIAccessAPI::GetFolderHierarchy(Zimbra::MAPI::MAPIFolder rootFolder,
    tree<Folder_Data> &tr) {
    tree<Folder_Data>::iterator top = tr.begin();
    Folder_Data rootFolderData;
    rootFolderData.name = L"root";
    tree<Folder_Data>::iterator root = tr.insert(top, rootFolderData);
    iterate_folders(rootFolder, tr, root);
    return S_OK;
}

// Get root folder hierarchy
HRESULT MAPIAccessAPI::GetRootFolderHierarchy(tree<Folder_Data> &tr) {
    return GetFolderHierarchy(*m_rootFolder, tr);
}

// iterate given folder hierarchy
bool MAPIAccessAPI::iterate_folders(Zimbra::MAPI::MAPIFolder &folder, tree<Folder_Data> &tr,
    tree<Folder_Data>::iterator tritr) {
    Zimbra::MAPI::FolderIterator *folderIter = new Zimbra::MAPI::FolderIterator;

    folder.GetFolderIterator(*folderIter);

    BOOL bMore = TRUE;
    while (bMore) {
        tree<Folder_Data>::iterator itrMore;

        // delete them while clearing the tree nodes
        Zimbra::MAPI::MAPIFolder *childFolder = new Zimbra::MAPI::MAPIFolder();
        bMore = folderIter->GetNext(*childFolder);
        if (bMore) {
            ULONG itemCount = 0;
            childFolder->GetItemCount(itemCount);
            printf("FolderName: %S ----- %d\n", childFolder->Name().c_str(), itemCount);

            // store foldername
            Folder_Data flderdata;
            flderdata.name = childFolder->Name();

            // store Folder EntryID
            SBinary sbin = childFolder->EntryID();
            flderdata.sbin.cb = sbin.cb;
            MAPIAllocateBuffer(sbin.cb, (LPVOID *)&(flderdata.sbin.lpb));
            memcpy(flderdata.sbin.lpb, sbin.lpb, sbin.cb);

            // Store mapi folder reference
            flderdata.mapifolder = childFolder;
            itrMore = tr.append_child(tritr, flderdata);
        }
        if (bMore) {
            iterate_folders(*childFolder, tr, itrMore);
        } else {
            delete childFolder;
            childFolder = NULL;
        }
    }
    delete folderIter;
    folderIter = NULL;
    return true;
}

// Access MAPI folder items
void MAPIAccessAPI::travrese_folder(Zimbra::MAPI::MAPIFolder &folder) {
    Zimbra::MAPI::MessageIterator *msgIter = new Zimbra::MAPI::MessageIterator();

    folder.GetMessageIterator(*msgIter);
    BOOL bContinue = true;
    while (bContinue) {
        Zimbra::MAPI::MAPIMessage *msg = new Zimbra::MAPI::MAPIMessage();
        bContinue = msgIter->GetNext(*msg);
        if (bContinue) {
            LPTSTR subject = NULL;
            if (msg->Subject(&subject))
                printf("\tsubject--%S\n", subject);
        }
        delete msg;
    }
    delete msgIter;
}

//
HRESULT MAPIAccessAPI::IterateTree(tree<Folder_Data> &tr) {
    tree<Folder_Data>::iterator loc = tr.begin();
    wcout << (LPTSTR)(*loc).name.c_str() << endl;
    if (loc != tr.end()) {
        tree<Folder_Data>::iterator sib2 = tr.begin(loc);
        tree<Folder_Data>::iterator end2 = tr.end(loc);
        while (sib2 != end2) {
            for (int i = 0; i < tr.depth(sib2) - 2; ++i)
                cout << "   ";
            wcout << "FolderName: " << (LPTSTR)(*sib2).name.c_str() << " Depth:" << tr.depth(
                sib2);
            wcout << " ParentName: " << sib2.node->parent->data.name.c_str() << endl;

            // traverse folder items
            travrese_folder(*(*sib2).mapifolder);

            // cleanup
            MAPIFreeBuffer((*sib2).sbin.lpb);
            delete (*sib2).mapifolder;
            ++sib2;
        }
    }
    return S_OK;
}
