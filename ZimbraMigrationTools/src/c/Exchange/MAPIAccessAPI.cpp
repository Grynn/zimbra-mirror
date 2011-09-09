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
LPCWSTR MAPIAccessAPI::OpenSessionAndStore() {
	LPCWSTR lpwstrStatus = NULL;
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
	}catch(MAPISessionException &msse){
		lpwstrStatus = FromatExceptionInfo(msse.ErrCode(), (LPWSTR)msse.Description().c_str(),
                    (LPSTR)msse.SrcFile().c_str(), msse.SrcLine());
	}
	catch(MAPIStoreException &mste){
		lpwstrStatus = FromatExceptionInfo(mste.ErrCode(), (LPWSTR)mste.Description().c_str(),
                    (LPSTR)mste.SrcFile().c_str(), mste.SrcLine());
	}
	catch(Util::MapiUtilsException &muex)
	{
		lpwstrStatus = FromatExceptionInfo(muex.ErrCode(), (LPWSTR)muex.Description().c_str(),
                    (LPSTR)muex.SrcFile().c_str(), muex.SrcLine());
	}
CLEAN_UP:
    SafeDelete(ExchangeServerDN);
    SafeDelete(ExchangeUserDN);
    SafeDelete(pwstrExchangeServerDN);
	if(hr!=S_OK)
		lpwstrStatus = FromatExceptionInfo(hr, L"MAPIAccessAPI::OpenSessionAndStore() Failed",
                    __FILE__, __LINE__);
    return lpwstrStatus;
}

// Get root folders
LPCWSTR MAPIAccessAPI::Initialize() {
	LPCWSTR lpwstrStatus = NULL;
    HRESULT hr = S_OK;
	try {
		lpwstrStatus = OpenSessionAndStore();
	    // Get root folder from user store
		m_rootFolder = new Zimbra::MAPI::MAPIFolder(*m_zmmapisession, *m_defaultStore);
		if(FAILED(hr = m_userStore->GetRootFolder(*m_rootFolder)))
			lpwstrStatus = FromatExceptionInfo(hr, L"MAPIAccessAPI::Initialize() Failed",
                    __FILE__, __LINE__);
	}catch(GenericException &ge) {
		lpwstrStatus = FromatExceptionInfo(ge.ErrCode(), (LPWSTR)ge.Description().c_str(),
                    (LPSTR)ge.SrcFile().c_str(), ge.SrcLine());
	}
    return lpwstrStatus;
}


LPCWSTR MAPIAccessAPI::GetRootFolderHierarchy(vector<Folder_Data> &vfolderlist)
{
	LPCWSTR lpwstrStatus = NULL;
	HRESULT hr=S_OK;
	try{
		hr= Iterate_folders(*m_rootFolder, vfolderlist);
	}catch(GenericException &ge) {
		lpwstrStatus = FromatExceptionInfo(ge.ErrCode(), (LPWSTR)ge.Description().c_str(),
                    (LPSTR)ge.SrcFile().c_str(), ge.SrcLine());
	}
	return lpwstrStatus;
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
			
            // store foldername
            Folder_Data flderdata;
            flderdata.name = childFolder->Name();
			
			//folder item count
			flderdata.itemcount = itemCount;

            // store Folder EntryID
			SBinary sbin = childFolder->EntryID();
			CopyEntryID(sbin,flderdata.sbin);
            
            //folder path
			flderdata.folderpath = childFolder->GetFolderPath();

			//ExchangeFolderID
			flderdata.zimbraid = (long)childFolder->GetZimbraFolderId();

            //append
			fd.push_back(flderdata);
		}
		if (bMore) {
            Iterate_folders(*childFolder, fd);
			delete childFolder;
            childFolder = NULL; 
        } else {
            delete childFolder;
            childFolder = NULL;
        }
	}
	delete folderIter;
	folderIter = NULL;
    return S_OK;
}


HRESULT MAPIAccessAPI::GetInternalFolder(SBinary sbFolderEID, MAPIFolder &folder)
{
	LPMAPIFOLDER pFolder = NULL;
    HRESULT hr = S_OK;
    ULONG objtype;
    if ((hr =
             m_zmmapisession->OpenEntry(sbFolderEID.cb, (LPENTRYID)sbFolderEID.lpb, NULL, MAPI_BEST_ACCESS, &objtype,
                 (LPUNKNOWN *)&pFolder)) != S_OK)
        throw GenericException(hr, L"GetFolderItems OpenEntry Failed.", __LINE__,
            __FILE__);
	Zimbra::Util::ScopedBuffer<SPropValue> pPropValues;
    // Get PR_DISPLAY_NAME
    if(FAILED(hr= HrGetOneProp(pFolder,PR_DISPLAY_NAME,pPropValues.getptr())))
		throw GenericException(hr, L"GetFolderItems HrGetOneProp() Failed.", __LINE__,
            __FILE__);

    folder.Initialize(pFolder, pPropValues->Value.LPSZ,&sbFolderEID);
	return hr;
}

LPCWSTR MAPIAccessAPI::GetFolderItemsList(SBinary sbFolderEID, vector<Item_Data> &ItemList)
{
	LPCWSTR lpwstrStatus = NULL;
	HRESULT hr=S_OK;
	MAPIFolder folder;
	try
	{
		if (FAILED(hr=GetInternalFolder(sbFolderEID, folder)!=S_OK))
		{
			lpwstrStatus = FromatExceptionInfo(hr, L"MAPIAccessAPI::GetFolderItemsList() Failed",
                    __FILE__, __LINE__);
			goto ZM_EXIT;
		}
		Zimbra::MAPI::MessageIterator *msgIter = new Zimbra::MAPI::MessageIterator();
		folder.GetMessageIterator(*msgIter);
		BOOL bContinue = true;
		while (bContinue) {
			Zimbra::MAPI::MAPIMessage *msg = new Zimbra::MAPI::MAPIMessage();
			bContinue = msgIter->GetNext(*msg);
			if (bContinue) {
				Item_Data itemdata;
				//
				itemdata.lItemType = msg->ItemType();
				SBinary sbin = msg->EntryID();
				CopyEntryID(sbin,itemdata.sbMessageID);
				itemdata.MessageDate = msg->Date();
				ItemList.push_back(itemdata);
			}
			delete msg;
		}
		delete msgIter;
	}
	catch(MAPISessionException &mssex)
	{
		lpwstrStatus = FromatExceptionInfo(mssex.ErrCode(), (LPWSTR)mssex.Description().c_str(),
                    (LPSTR)mssex.SrcFile().c_str(), mssex.SrcLine());
	}
	catch(MAPIFolderException &mfex)
	{
		lpwstrStatus = FromatExceptionInfo(mfex.ErrCode(), (LPWSTR)mfex.Description().c_str(),
                    (LPSTR)mfex.SrcFile().c_str(), mfex.SrcLine());
	}
	catch(MAPIMessageException &msgex)
	{
		lpwstrStatus = FromatExceptionInfo(msgex.ErrCode(), (LPWSTR)msgex.Description().c_str(),
                    (LPSTR)msgex.SrcFile().c_str(), msgex.SrcLine());
	}
	catch(GenericException &genex)
	{
		lpwstrStatus = FromatExceptionInfo(genex.ErrCode(), (LPWSTR)genex.Description().c_str(),
                    (LPSTR)genex.SrcFile().c_str(), genex.SrcLine());
	}
ZM_EXIT:
	return lpwstrStatus;
}

LPCWSTR MAPIAccessAPI::GetItem(SBinary sbItemEID, BaseItemData &itemData)
{
	LPWSTR lpwstrStatus=NULL;
	HRESULT hr=S_OK;
	LPMESSAGE pMessage = NULL;
    ULONG objtype;
	if(FAILED(hr=m_zmmapisession->OpenEntry(sbItemEID.cb,(LPENTRYID)sbItemEID.lpb,
		NULL, MAPI_BEST_ACCESS, &objtype, (LPUNKNOWN*)&pMessage)))
	{
		lpwstrStatus = FromatExceptionInfo(hr, L"MAPIAccessAPI::GetItem() Failed",
                    __FILE__, __LINE__);
			goto ZM_EXIT;
	}

	try {
		MAPIMessage msg;
		msg.Initialize(pMessage);
		
		if(msg.ItemType() == ZT_MAIL)
		{
			printf("ITEM TYPE: ZT_MAIL \n");
		}
		else if (msg.ItemType() == ZT_CONTACTS)
		{
			printf("ITEM TYPE: ZT_CONTACTS \n");
			MAPIContact mapicontact(*m_zmmapisession,msg);
			ContactItemData *cd = (ContactItemData*)&itemData;
			cd->Birthday= mapicontact.Birthday();
			cd->CallbackPhone = mapicontact.CallbackPhone();
			cd->CarPhone = mapicontact.CarPhone();
			cd->Company = mapicontact.Company();
			cd->Email1 = mapicontact.Email();
			cd->Email2 = mapicontact.Email2();
			cd->Email3 = mapicontact.Email3();
			cd->FileAs = mapicontact.FileAs();
			cd->FirstName = mapicontact.FirstName();
			cd->HomeCity = mapicontact.HomeCity();
			cd->HomeCountry = mapicontact.HomeCountry();
			cd->HomeFax = mapicontact.HomeFax();
			cd->HomePhone = mapicontact.HomePhone();
			cd->HomePhone2 = mapicontact.HomePhone2();
			cd->HomePostalCode = mapicontact.HomePostalCode();
			cd->HomeState = mapicontact.HomeState();
			cd->HomeStreet = mapicontact.HomeStreet();
			cd->HomeURL = mapicontact.HomeURL();
			cd->IMAddress1 = mapicontact.IMAddress1();
			cd->JobTitle = mapicontact.JobTitle();
			cd->LastName = mapicontact.LastName();
			cd->MiddleName = mapicontact.MiddleName();
			cd->MobilePhone = mapicontact.MobilePhone();
			cd->NamePrefix = mapicontact.NamePrefix();
			cd->NameSuffix = mapicontact.NameSuffix();
			cd->NickName = mapicontact.NickName();
			cd->Notes = mapicontact.Notes();
			cd->OtherCity = mapicontact.OtherCity();
			cd->OtherCountry = mapicontact.OtherCountry();
			cd->OtherFax = mapicontact.OtherFax();
			cd->OtherPhone = mapicontact.OtherPhone();
			cd->OtherPostalCode = mapicontact.OtherPostalCode();
			cd->OtherState = mapicontact.OtherState();
			cd->OtherStreet = mapicontact.OtherStreet();
			cd->OtherURL = mapicontact.OtherURL();
			cd->Pager = mapicontact.Pager();
			cd->pDList = L"";//mapicontact.P
			cd->PictureID = mapicontact.Picture();
			cd->Type = mapicontact.Type();
			cd->UserField1 = mapicontact.UserField1();
			cd->UserField2 = mapicontact.UserField2();
			cd->UserField3 = mapicontact.UserField3();
			cd->UserField4 = mapicontact.UserField4();
			cd->WorkCity = mapicontact.WorkCity();
			cd->WorkCountry = mapicontact.WorkCountry();
			cd->WorkFax = mapicontact.WorkFax();
			cd->WorkPhone = mapicontact.WorkPhone();
			cd->WorkPostalCode = mapicontact.WorkPostalCode();
			cd->WorkState = mapicontact.WorkState();
			cd->WorkStreet = mapicontact.WorkStreet();
			cd->WorkURL = mapicontact.WorkURL();

		}
		else if (msg.ItemType() == ZT_APPOINTMENTS)
		{
			printf("ITEM TYPE: ZT_APPOINTMENTS \n");
		}
		else if (msg.ItemType() == ZT_TASKS)
		{
			printf("ITEM TYPE: ZT_TASKS \n");
		}
		else if(msg.ItemType() == ZT_MEETREQ_RESP)
		{
			printf("ITEM TYPE: ZT_MEETREQ_RESP \n");
		}
	} 
	catch(MAPIMessageException &mex)
	{
		lpwstrStatus = FromatExceptionInfo(mex.ErrCode(), (LPWSTR)mex.Description().c_str(),
                    (LPSTR)mex.SrcFile().c_str(), mex.SrcLine());
	}
	catch(MAPIContactException &cex)
	{
		lpwstrStatus = FromatExceptionInfo(cex.ErrCode(), (LPWSTR)cex.Description().c_str(),
                    (LPSTR)cex.SrcFile().c_str(), cex.SrcLine());
	}
ZM_EXIT:
	return lpwstrStatus;
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
