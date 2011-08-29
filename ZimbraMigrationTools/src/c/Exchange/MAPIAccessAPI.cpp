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
    wstring wstruserdn; wstring legacyName;
    LPSTR ExchangeServerDN = NULL;
    LPSTR ExchangeUserDN = NULL;
    LPWSTR pwstrExchangeServerDN = NULL;

	try{
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
		Zimbra::MAPI::Util::GetUserDNAndLegacyName(m_strExchangeHostName.c_str(),
			m_strUserName.c_str(),NULL, wstruserdn,legacyName);
		hr = m_zmmapisession->OpenOtherStore(
				m_defaultStore->GetInternalMAPIStore(), pwstrExchangeServerDN,
				(LPWSTR)legacyName.c_str(), *m_userStore);
		if (hr != S_OK)
			goto CLEAN_UP;
	}catch(GenericException &ge){
		hr= ge.ErrCode();
	}
CLEAN_UP:
    SafeDelete(ExchangeServerDN);
    SafeDelete(ExchangeUserDN);
    SafeDelete(pwstrExchangeServerDN);

    return hr;
}

// Get root folders
HRESULT MAPIAccessAPI::Initialize() {
    HRESULT hr = S_OK;
	try {
		hr = OpenSessionAndStore();
	    if (hr != S_OK)
			return hr;
		// Get root folder from user store
		m_rootFolder = new Zimbra::MAPI::MAPIFolder(*m_zmmapisession, *m_defaultStore);
		hr = m_userStore->GetRootFolder(*m_rootFolder);
	}catch(GenericException &ge) {
		hr= ge.ErrCode();
	}
    return hr;
}


HRESULT MAPIAccessAPI::GetRootFolderHierarchy(vector<Folder_Data> &vfolderlist)
{
	HRESULT hr=S_OK;
	try{
		hr= Iterate_folders(*m_rootFolder, vfolderlist);
	}catch(GenericException &ge) {
		hr= ge.ErrCode();
	}
	return hr;
}

HRESULT MAPIAccessAPI::Iterate_folders(Zimbra::MAPI::MAPIFolder &folder, vector<Folder_Data> &fd)
{
	Zimbra::MAPI::FolderIterator *folderIter = new Zimbra::MAPI::FolderIterator;

    folder.GetFolderIterator(*folderIter);

    BOOL bMore = TRUE;
    while (bMore) {
		ULONG itemCount = 0;
		// delete them while clearing the tree nodes
        Zimbra::MAPI::MAPIFolder *childFolder = new Zimbra::MAPI::MAPIFolder(*m_zmmapisession,*m_defaultStore);
        bMore = folderIter->GetNext(*childFolder);
        if (bMore) {
            childFolder->GetItemCount(itemCount);
			printf("FolderPath: %S\n",childFolder->GetFolderPath().c_str());
			printf("FolderName: %S ----- %d\n", childFolder->Name().c_str(), itemCount);

            // store foldername
            Folder_Data flderdata;
            flderdata.name = childFolder->Name();

            // store Folder EntryID
            SBinary sbin = childFolder->EntryID();
            flderdata.sbin.cb = sbin.cb;
            MAPIAllocateBuffer(sbin.cb, (LPVOID *)&(flderdata.sbin.lpb));
            memcpy(flderdata.sbin.lpb, sbin.lpb, sbin.cb);

			//folder path
			flderdata.folderpath = childFolder->GetFolderPath();

			//ExchangeFolderID
			flderdata.zimbraid = (long)childFolder->GetZimbraFolderId();

            // Store mapi folder reference
			flderdata.mapifolder = childFolder;
			//append
			fd.push_back(flderdata);
		}
		if (bMore) {
            Iterate_folders(*childFolder, fd);
        } else {
            delete childFolder;
            childFolder = NULL;
        }
	}
	delete folderIter;
	folderIter = NULL;
    return true;
}

HRESULT MAPIAccessAPI::IterateVectorList(vector<Folder_Data> &vFolderList)
{
	vector<Folder_Data>::iterator it;
	for(it=vFolderList.begin();it!=vFolderList.end();it++)
	{
		printf("==================    FolderName:%S    ============\n", (*it).mapifolder->Name().c_str());
		travrese_folder(*(*it).mapifolder);
		// cleanup
        MAPIFreeBuffer((*it).sbin.lpb);
		delete (*it).mapifolder;
	}
	return S_OK;
}

//>>>>>>>>>>>>>>TREE Impl>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// Get Folder hierarchy
HRESULT MAPIAccessAPI::GetFolderHierarchy_tr(Zimbra::MAPI::MAPIFolder rootFolder,
    tree<Folder_Data> &tr) {
    tree<Folder_Data>::iterator top = tr.begin();
    Folder_Data rootFolderData;
    rootFolderData.name = L"root";
    tree<Folder_Data>::iterator root = tr.insert(top, rootFolderData);
    iterate_folders_tr(rootFolder, tr, root);
    return S_OK;
}

// Get root folder hierarchy
HRESULT MAPIAccessAPI::GetRootFolderHierarchy_tr(tree<Folder_Data> &tr) {
    return GetFolderHierarchy_tr(*m_rootFolder, tr);
}

// iterate given folder hierarchy
bool MAPIAccessAPI::iterate_folders_tr(Zimbra::MAPI::MAPIFolder &folder, tree<Folder_Data> &tr,
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
            iterate_folders_tr(*childFolder, tr, itrMore);
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
            Zimbra::Util::ScopedBuffer<WCHAR> subject ;
			if (msg->Subject(subject.getptr()))
			{
				printf("\tsubject--%S\n", subject.get());
			}
        }
        delete msg;
    }
    delete msgIter;
}

//
HRESULT MAPIAccessAPI::IterateTree_tr(tree<Folder_Data> &tr) {
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
//<<<<<<<<<<<<<<<<<<,TREE Impl<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<