#pragma once

namespace Zimbra {
namespace MAPI {

struct _Folder_Data{
	wstring name;
	SBinary sbin;
};

typedef _Folder_Data Folder_Data;

class MAPIAccessAPI
{
private:
	std::wstring m_strAdminProfileName;
	std::wstring m_strUserName;
	std::wstring m_strExchangeHostName;
	Zimbra::MAPI::MAPISession *m_zmmapisession;
	Zimbra::MAPI::MAPIStore *m_defaultStore;
	Zimbra::MAPI::MAPIStore *m_userStore;
	Zimbra::MAPI::MAPIFolder *m_rootFolder;

	HRESULT OpenSessionAndStore();
	bool iterate_folders(Zimbra::MAPI::MAPIFolder &folder, tree<Folder_Data> &tr, tree<Folder_Data>::iterator tritr);
	void travrese_folder(Zimbra::MAPI::MAPIFolder &folder);
	HRESULT MAPIAccessAPI::GetFolderHierarchy(Zimbra::MAPI::MAPIFolder rootFolder,tree<Folder_Data> &tr);
public:
	MAPIAccessAPI(wstring strExchangeHostName,wstring strAdminProfileName, wstring strUserName);
	~MAPIAccessAPI();
	HRESULT Initialize();
	HRESULT GetRootFolderHierarchy(tree<Folder_Data> &tr);
	HRESULT IterateTree(tree<Folder_Data> &tr);
};


}
}