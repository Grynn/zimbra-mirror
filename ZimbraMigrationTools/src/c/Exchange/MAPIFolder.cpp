#include "common.h"
#include "Exchange.h"
#include "MAPIFolder.h"
#include "MAPIMessage.h"

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// Exception class
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIFolderException::MAPIFolderException(HRESULT hrErrCode, LPCWSTR
    lpszDescription): GenericException(hrErrCode, lpszDescription)
{
    //
}

MAPIFolderException::MAPIFolderException(HRESULT hrErrCode, LPCWSTR lpszDescription, LPCWSTR lpszShortDescription,
	int nLine, LPCSTR strFile): GenericException(hrErrCode, lpszDescription, lpszShortDescription, nLine, strFile)
{
    //
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// FolderIterator
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
FolderIterator::FolderIterator()
{
    m_pParentFolder = NULL;
}

FolderIterator::~FolderIterator() {}

FolderIterator::FolderIterPropTags FolderIterator::m_props = {
    NFOLDERPROPS, { PR_DISPLAY_NAME, PR_ENTRYID, PR_LONGTERM_ENTRYID_FROM_TABLE,
                    PR_FOLDER_FLAGS }
};
LPSPropTagArray FolderIterator::GetProps()
{
    return (LPSPropTagArray) & m_props;
}

BOOL FolderIterator::GetNext(MAPIFolder &folder)
{
    SRow *pRow;

    do
    {
        pRow = MAPITableIterator::GetNext();
        if (pRow == NULL)
            return FALSE;
    }
    while ((pRow->lpProps[FI_FLAGS].Value.l & MDB_FOLDER_NORMAL) == 0);

    LPMAPIFOLDER pFolder = NULL;
    HRESULT hr = S_OK;
    ULONG objtype;
    ULONG cb = pRow->lpProps[FI_ENTRYID].Value.bin.cb;
    LPENTRYID peid = (LPENTRYID)(pRow->lpProps[FI_ENTRYID].Value.bin.lpb);

    if ((hr = m_pParentFolder->OpenEntry(cb, peid, NULL, MAPI_BEST_ACCESS, &objtype,
            (LPUNKNOWN *)&pFolder)) != S_OK)
        throw GenericException(hr, L"FolderIterator::GetNext():OpenEntry Failed.", 
		ERR_GET_NEXT, __LINE__, __FILE__);
    folder.Initialize(pFolder, pRow->lpProps[FI_DISPLAY_NAME].Value.LPSZ,
        &(pRow->lpProps[FI_ENTRYID].Value.bin));

    return TRUE;
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MAPIFolder
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIFolder::MAPIFolder(): m_folder(NULL), m_session(NULL), m_store(NULL)
{
    m_EntryID.cb = 0;
    m_EntryID.lpb = NULL;
	m_pContentsTable = NULL;
	m_pHierarchyTable = NULL;
}

MAPIFolder::MAPIFolder(MAPISession &session, MAPIStore &store): m_folder(NULL)
{
    m_EntryID.cb = 0;
    m_EntryID.lpb = NULL;
	m_pContentsTable = NULL;
	m_pHierarchyTable=NULL;
    m_session = &session;
    m_store = &store;
}

MAPIFolder::~MAPIFolder()
{
    if (m_folder != NULL)
        UlRelease(m_folder);
    if (m_EntryID.lpb != NULL)
        MAPIFreeBuffer(m_EntryID.lpb);
	if(m_pContentsTable != NULL)
		UlRelease(m_pContentsTable);
	if(m_pHierarchyTable!=NULL)
		UlRelease(m_pHierarchyTable);
	m_pContentsTable = NULL;
    m_folder = NULL;
    m_EntryID.lpb = NULL;
    m_EntryID.cb = 0;
}

MAPIFolder::MAPIFolder(const MAPIFolder &folder)
{
    m_folder = folder.m_folder;
    m_displayname = folder.m_displayname;
	m_pContentsTable = folder.m_pContentsTable;
	m_pHierarchyTable= folder.m_pHierarchyTable;
	CopyEntryID((SBinary &)folder.m_EntryID, m_EntryID);
}

void MAPIFolder::Initialize(LPMAPIFOLDER pFolder, LPTSTR displayName, LPSBinary pEntryId)
{
	HRESULT hr=S_OK;
    if (m_folder != NULL)
        UlRelease(m_folder);
    if (m_EntryID.lpb != NULL)
        FreeEntryID(m_EntryID);
	if(m_pContentsTable != NULL)
		UlRelease(m_pContentsTable);
	if(m_pHierarchyTable!=NULL)
		UlRelease(m_pHierarchyTable);

    m_folder = pFolder;
    m_displayname = displayName;    
    
    //replace later by "/" allow "/"
	/*
    size_t npos= m_displayname.find(L"/");
    if ((npos != std::wstring::npos) && (npos>0))
    {
        m_displayname.replace(npos,1,CONST_FORWDSLASH);
    }
	*/
	//parse whole string for forward slash not just one occurence
	std::wstring::size_type i = m_displayname.find(L"/");
	while (i != std::wstring::npos && i < m_displayname.size())
    {
        m_displayname.replace(i, wcslen(L"/"),CONST_FORWDSLASH);
        i +=  wcslen(L"/");

        i = m_displayname.find( L"/", i);
    }

    CopyEntryID(*pEntryId, m_EntryID);
	
	//Get folder hierarchy table
	if (FAILED(hr = m_folder->GetHierarchyTable(fMapiUnicode, &m_pHierarchyTable)))
    {
        throw MAPIFolderException(E_FAIL, L"GetFolderIterator(): GetHierarchyTable Failed.",
            ERR_MAPI_FOLDER, __LINE__, __FILE__);
    }
	
	//get folders content tabel
	if (FAILED(hr = m_folder->GetContentsTable(fMapiUnicode, &m_pContentsTable)))
	{
		throw MAPIFolderException(hr, L"Initialize(): GetContentsTable Failed.",
            ERR_MAPI_FOLDER, __LINE__, __FILE__);
	}    

	ULONG ulItemMask =ZCM_ALL;
	//disable restriction for only mails on IPF.Note folders.
	//Lets migrate everything in it for now.
/*
	//find container class	
    wstring wstrCntrClass = L"";
    if(S_OK==ContainerClass(wstrCntrClass))
    {
        if (_tcsicmp(wstrCntrClass.c_str(), _TEXT("IPF.NOTE")) == 0)
            ulItemMask = ZCM_MAIL;
    }    
*/
	//Apply restrictions.
	Zimbra::MAPI::MIRestriction restriction;
	FILETIME tmpTime = { 0, 0 };
	if (FAILED(hr = m_pContentsTable->Restrict(restriction.GetRestriction(ulItemMask, tmpTime), 0)))
    {
        throw MAPIFolderException(hr, L"MAPIFolder::Initialize():Restrict Failed.",
            ERR_MAPI_FOLDER, __LINE__, __FILE__);
    }

    if (m_session)
    {
        wstring wstrFolderPath = FindFolderPath();
        m_folderpath = wstrFolderPath;
    }
}


ExchangeSpecialFolderId MAPIFolder::GetExchangeFolderId()
{
	ExchangeSpecialFolderId efid= SPECIAL_FOLDER_ID_NONE;
    if (m_store && m_session)
    {
        SBinaryArray specialFolderIds = m_store->GetSpecialFolderIds();

        efid= Zimbra::MAPI::Util::GetExchangeSpecialFolderId(m_store->GetInternalMAPIStore(),
            m_EntryID.cb, (LPENTRYID)(m_EntryID.lpb), &specialFolderIds);
		//it is possible that if its a pst migration then PR_IPM entries are not
		//available so use english inbox contained IPM folder names to compare 
		//current folder name
		if(efid==SPECIAL_FOLDER_ID_NONE)
		{
			for (int i=0;i<g_MAX_STR_IPM_FOLDERS;i++)
			{
				if(m_displayname.compare(g_strIPM_FOLDERS[i].c_str())==0)
				{
					efid= (i == 6) ? JUNK_MAIL : (ExchangeSpecialFolderId)(i+2);
					break;
				}
			}
		}
		return efid;
    }
    return efid;
}

ZimbraSpecialFolderId MAPIFolder::GetZimbraFolderId()
{
    ZimbraSpecialFolderId ZimbraSpecialFolderIdArray[TOTAL_NUM_SPECIAL_FOLDERS] = {
        ZM_INBOX, ZM_ROOT, ZM_CALENDAR, ZM_CONTACTS, ZM_DRAFTS, ZM_SFID_NONE /*JOURNAL*/,
        ZM_SFID_NONE /*NOTES*/, ZM_TASKS, ZM_SFID_NONE /*OUTBOX*/, ZM_SENT_MAIL, ZM_TRASH,
        ZM_SFID_NONE /*SYNC_CONFLICTS*/, ZM_SFID_NONE /*SYNC_ISSUES*/,
        ZM_SFID_NONE /*SYNC_LOCAL_FAILURES*/, ZM_SFID_NONE /*SYNC_SERVER_FAILURES*/, ZM_SPAM
    };

    if (m_store && m_session)
    {
        int idx = GetExchangeFolderId();

        if (idx < ZM_SFID_MAX)
            return ZimbraSpecialFolderIdArray[idx];
        else
            return ZM_SFID_NONE;
    }
    return ZM_SFID_NONE;
}

bool MAPIFolder::HiddenFolder()
{
    if (!m_folder)
        return false;

    HRESULT hr = S_OK;
    bool bRet = false;
    Zimbra::Util::ScopedBuffer<SPropValue> pPropValues;

    if (SUCCEEDED(hr = HrGetOneProp(m_folder, PR_ATTR_HIDDEN, pPropValues.getptr())))
        bRet = (pPropValues->Value.b != 0);
    return bRet;
}

HRESULT MAPIFolder::ContainerClass(wstring &wstrContainerClass)
{
    if (!m_folder)
        return E_FAIL;

    HRESULT hr = S_OK;
    Zimbra::Util::ScopedBuffer<SPropValue> pPropValues;

    wstrContainerClass = L"";
    if (SUCCEEDED(hr = HrGetOneProp(m_folder, PR_CONTAINER_CLASS, pPropValues.getptr())))
        wstrContainerClass = pPropValues->Value.LPSZ;
    return hr;
}

wstring MAPIFolder::FindFolderPath()
{
    // return if no session object to compare ids
    if (!m_session)
        return L"";

    HRESULT hr = S_OK;
    ULONG ulResult = FALSE;
    wstring wstrPath = m_displayname;
    LPSPropValue pPropVal = NULL;
    ULONG ulType = 0;
    LPMAPIFOLDER lpMAPIFolder = NULL;
    SBinary prevEntryID = { 0 };

    // Make copy of prev EntryID
    CopyEntryID(m_EntryID, prevEntryID);
    // Get parent ENTRYID
    if (SUCCEEDED(hr = HrGetOneProp(m_folder, PR_PARENT_ENTRYID, &pPropVal)))
    {
        while (!ulResult)
        {
            // compare entryID with previous
            m_session->CompareEntryIDs(&prevEntryID, &pPropVal->Value.bin, ulResult);

            // Free PrevEntryID
            FreeEntryID(prevEntryID);
            if (ulResult)
            {
                if (pPropVal)
                    MAPIFreeBuffer(pPropVal);
                continue;
            }
            // Get Parent MAPI Folder
            if (SUCCEEDED(hr = m_session->OpenEntry(pPropVal->Value.bin.cb,
                    (LPENTRYID)pPropVal->Value.bin.lpb, NULL, 0, &ulType,
                    (LPUNKNOWN *)&lpMAPIFolder)))
            {
                // Get parent folder name
                LPSPropValue pDisplayPropVal = NULL;

                if (SUCCEEDED(hr = HrGetOneProp(lpMAPIFolder, PR_DISPLAY_NAME,
                        &pDisplayPropVal)))
                {
					std::wstring tempath = pDisplayPropVal->Value.lpszW;
					 // need to parse the parent folder names for forward slash
					std::wstring::size_type i = tempath.find(L"/");
					while (i != std::wstring::npos && i < tempath.size())
					{
						tempath.replace(i, wcslen(L"/"),CONST_FORWDSLASH);
						i +=  wcslen(L"/");

						i = tempath.find( L"/", i);
					}
                    //wstrPath = wstrPath + L"/" + pDisplayPropVal->Value.lpszW;
					wstrPath = wstrPath + L"/" + tempath;
                    MAPIFreeBuffer(pDisplayPropVal);
                    pDisplayPropVal = NULL;
                }
                // Make copy of prev EntryID
                CopyEntryID(pPropVal->Value.bin, prevEntryID);

                // free parent folder entryID
                MAPIFreeBuffer(pPropVal);
                pPropVal = NULL;
                // Get parent's parent entry ID
                if (!SUCCEEDED(hr = HrGetOneProp(lpMAPIFolder, PR_PARENT_ENTRYID, &pPropVal)))
                    ulResult = TRUE;
                // free parent folder
                lpMAPIFolder->Release();
                lpMAPIFolder = NULL;
            }
            else
            {
                ulResult = TRUE;
            }
        }
    }
    wstrPath = Zimbra::MAPI::Util::ReverseDelimitedString(wstrPath, L"/");
    if (wstrPath.length() > 2)
    {
        size_t npos = wstrPath.find('/', 1);
        if (npos != std::string::npos)
        {
            wstrPath.replace(1, (npos-1), L"MAPIRoot");
        }
    }
    //check for any earlier masking of "/" and restore it
	
	/*
		size_t cnst_pos = wstrPath.find(CONST_FORWDSLASH);
    if(std::wstring::npos != cnst_pos)
    {
        wstrPath.replace(cnst_pos,wcslen(CONST_FORWDSLASH),L"/");
    }
	
	*/
	std::wstring::size_type i = wstrPath.find(CONST_FORWDSLASH);
    while (i != std::wstring::npos && i < wstrPath.size())
    {
        wstrPath.replace(i, wcslen(CONST_FORWDSLASH), L"/");
        i +=  wcslen(L"/");

       
        i = wstrPath.find( CONST_FORWDSLASH, i);
    }
    return wstrPath;
}

HRESULT MAPIFolder::GetItemCount(ULONG &ulCount)
{
    ulCount = 0;
    if (m_folder == NULL)
        throw MAPIFolderException(E_FAIL, L"GetItemCount(): Folder Object is NULL.", 
		ERR_MAPI_FOLDER, __LINE__, __FILE__);

    HRESULT hr = S_OK;
    Zimbra::Util::ScopedBuffer<SPropValue> pPropValues;

	if (FAILED(hr = m_pContentsTable->GetRowCount(0, &ulCount)))
    {
        throw MAPIFolderException(E_FAIL, L"GetItemCount(): GetRowCount() Failed.",
           ERR_MAPI_FOLDER, __LINE__, __FILE__);
    }
    return hr;
}

HRESULT MAPIFolder::GetFolderIterator(FolderIterator &folderIter)
{
    if (m_folder == NULL)
        return MAPI_E_NOT_FOUND;

    folderIter.Initialize(m_pHierarchyTable, m_folder, *m_session);
    return S_OK;
}

HRESULT MAPIFolder::GetMessageIterator(MessageIterator &msgIterator)
{
    if (m_folder == NULL)
    {
        throw MAPIFolderException(E_FAIL, L"GetMessageIterator(): Folder Object is NULL.",
            ERR_MAPI_FOLDER, __LINE__, __FILE__);
    }

    msgIterator.Initialize(m_pContentsTable, m_folder, *m_session);
	return S_OK;
}
