#pragma once

namespace Zimbra {
namespace MAPI {

typedef struct _Folder_Data {
    wstring name;	
    SBinary sbin;
	wstring folderpath;
	long zimbraid;
    Zimbra::MAPI::MAPIFolder *mapifolder;
}Folder_Data;

class MAPIAccessAPI {
private:
    std::wstring m_strAdminProfileName;
    std::wstring m_strUserName;
    std::wstring m_strExchangeHostName;
    Zimbra::MAPI::MAPISession *m_zmmapisession;
    Zimbra::MAPI::MAPIStore *m_defaultStore;
    Zimbra::MAPI::MAPIStore *m_userStore;
    Zimbra::MAPI::MAPIFolder *m_rootFolder;

    HRESULT OpenSessionAndStore();
    HRESULT Iterate_folders(Zimbra::MAPI::MAPIFolder &folder, vector<Folder_Data> &fd);
    void travrese_folder(Zimbra::MAPI::MAPIFolder &folder);
    

	bool iterate_folders_tr(Zimbra::MAPI::MAPIFolder &folder, tree<Folder_Data> &tr,
    tree<Folder_Data>::iterator tritr);
	HRESULT MAPIAccessAPI::GetFolderHierarchy_tr(Zimbra::MAPI::MAPIFolder rootFolder,
    tree<Folder_Data> &tr);

public:
    MAPIAccessAPI(wstring strExchangeHostName, wstring strAdminProfileName, wstring strUserName);
    ~MAPIAccessAPI();
    HRESULT Initialize();
    HRESULT GetRootFolderHierarchy(vector<Folder_Data> &vfolderlist);
	HRESULT IterateVectorList(vector<Folder_Data> &vFolderList);
    
	HRESULT GetRootFolderHierarchy_tr(tree<Folder_Data> &tr);
	HRESULT IterateTree_tr(tree<Folder_Data> &tr);
	
};
}
}
