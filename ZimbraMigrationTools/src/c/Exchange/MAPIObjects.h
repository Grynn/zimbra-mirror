#pragma once
#include "ExchangeCommon.h"

#define PR_PROFILE_UNRESOLVED_NAME 0x6607001e
#define PR_PROFILE_UNRESOLVED_SERVER 0x6608001e

using namespace std;
namespace Zimbra {namespace MAPI {

class ExchangeAdminException:public GenericException
{
public:
	ExchangeAdminException(HRESULT hrErrCode, LPCWSTR lpszDescription);
	ExchangeAdminException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile);
	virtual ~ExchangeAdminException(){};
};

class MAPISessionException:public GenericException
{
public:
	MAPISessionException(HRESULT hrErrCode, LPCWSTR lpszDescription);
	MAPISessionException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile);
	virtual ~MAPISessionException(){};
};

class MAPIStoreException:public GenericException
{
public:
	MAPIStoreException(HRESULT hrErrCode, LPCWSTR lpszDescription);
	MAPIStoreException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile);
	virtual ~MAPIStoreException(){};
};

class MAPIFolderException:public GenericException
{
public:
	MAPIFolderException(HRESULT hrErrCode, LPCWSTR lpszDescription);
	MAPIFolderException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile);
	virtual ~MAPIFolderException(){};
};

class ExchangeAdmin
{
private:
	LPPROFADMIN m_pProfAdmin;
	string m_strServer;
private:
	HRESULT Init();
public:
	ExchangeAdmin(string strExchangeServer);
	~ExchangeAdmin();
	HRESULT CreateProfile(string strProfileName,string strMailboxName,string strPassword);
	HRESULT DeleteProfile(string strProfile);
	HRESULT GetAllProfiles(vector<string> &vProfileList);
	HRESULT SetDefaultProfile(string strProfile);
	HRESULT CreateExchangeMailBox(LPWSTR lpwstrNewUser, LPWSTR lpwstrNewUserPwd, LPWSTR lpwstrlogonuser, LPWSTR lpwstrLogonUsrPwd);
	HRESULT DeleteExchangeMailBox(LPWSTR lpwstrMailBox,LPWSTR lpwstrlogonuser, LPWSTR lpwstrLogonUsrPwd);
};

class MAPIStore;
class MAPIFolder;
class MessageIterator;
class FolderIterator;
//MAPI session class
class MAPISession
{
private:
	IMAPISession *m_Session;
	HRESULT _mapiLogon(LPWSTR strProfile, DWORD dwFlags, LPMAPISESSION &session);
public:
	MAPISession();
	~MAPISession();
	HRESULT Logon(LPWSTR strProfile);
	HRESULT Logon(bool bDefaultProfile=true);
	LPMAPISESSION GetMAPISessionObject(){return m_Session;};
	HRESULT OpenDefaultStore(MAPIStore &Store);
	HRESULT OpenOtherStore(LPMDB OpenedStore,LPWSTR pServerDn, LPWSTR pUserDn,MAPIStore &OtherStore);
	HRESULT OpenAddressBook(LPADRBOOK* ppAddrBook);
	
};

//Mapi Store class
class MAPIStore
{
private:
	LPMDB m_Store;
	LPMAPISESSION m_mapiSession;
public:
	MAPIStore();
	~MAPIStore();
	void Initialize(LPMAPISESSION mapisession, LPMDB pMdb);
	HRESULT CompareEntryIDs( SBinary* pBin1, SBinary* pBin2, ULONG &lpulResult);
	HRESULT GetRootFolder(MAPIFolder &rootFolder);
};

//MapiFolder class
class MAPIFolder
{
private:
	LPMAPIFOLDER m_folder;
	wstring m_displayname;
	SBinary m_EntryID;
public:
	MAPIFolder();
	~MAPIFolder();
	MAPIFolder(const MAPIFolder& folder);
	void Initialize(LPMAPIFOLDER pFolder, LPTSTR displayName, LPSBinary pEntryId);
	HRESULT GetItemCount(ULONG &ulCount);
	HRESULT GetMessageIterator(MessageIterator &msgIterator);
	HRESULT GetFolderIterator( FolderIterator& folderIter );
	wstring Name() { return m_displayname;}
};

//Base Iterator class
class MAPITableIterator
{
protected:
	LPMAPIFOLDER m_pParentFolder;
	LPMAPITABLE m_pTable;
	LPSRowSet m_pRows;	
	ULONG m_currRow;		
	ULONG m_batchSize;	
	ULONG m_rowsVisited; 
	ULONG m_totalRows;	
public:
	MAPITableIterator();
	virtual ~MAPITableIterator();
	virtual void Initialize( LPMAPITABLE pTable, LPMAPIFOLDER pFolder );
	virtual LPSPropTagArray GetProps() = 0;
	virtual LPSSortOrderSet GetSortOrder() = 0;
	virtual LPSRestriction GetRestriction(int isContact = 0) = 0;
	SRow* GetNext();

};

//Folder Iterator class
class FolderIterator:public MAPITableIterator
{
private:
	typedef enum _FolderIterPropTagIdx{ FI_DISPLAY_NAME, FI_ENTRYID, FI_PR_LONGTERM_ENTRYID_FROM_TABLE, FI_FLAGS, NFOLDERPROPS } FolerIterPropTagIdx;
	typedef struct _FolderIterPropTags
	{
		ULONG cValues;
		ULONG aulPropTags[NFOLDERPROPS];
	} FolderIterPropTags ;
protected:
	static FolderIterPropTags m_props;
public:
	FolderIterator();
	~FolderIterator();
	virtual LPSPropTagArray GetProps();
	virtual LPSSortOrderSet GetSortOrder(){return NULL;}
	virtual LPSRestriction GetRestriction(int isContact = 0){UNREFERENCED_PARAMETER(isContact); return NULL;}
	BOOL GetNext( MAPIFolder& folder );
};

//Message Iterator class
class MessageIterator:public MAPITableIterator
{

};


} //namespace MAPI

}//namespace Zimbra