#include "common.h"
#include "Exchange.h"
#include "MAPIFolder.h"

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// Exception class
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIFolderException::MAPIFolderException(HRESULT hrErrCode, LPCWSTR
    lpszDescription): GenericException(hrErrCode, lpszDescription)
{
    //
}

MAPIFolderException::MAPIFolderException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine,
    LPCSTR strFile): GenericException(hrErrCode, lpszDescription, nLine, strFile)
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
        throw GenericException(hr, L"FolderIterator::GetNext():OpenEntry Failed.", __LINE__,
            __FILE__);
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
}

MAPIFolder::MAPIFolder(MAPISession &session, MAPIStore &store): m_folder(NULL)
{
    m_EntryID.cb = 0;
    m_EntryID.lpb = NULL;
    m_session = &session;
    m_store = &store;
}

MAPIFolder::~MAPIFolder()
{
    if (m_folder != NULL)
        UlRelease(m_folder);
    if (m_EntryID.lpb != NULL)
        MAPIFreeBuffer(m_EntryID.lpb);
    m_folder = NULL;
    m_EntryID.lpb = NULL;
    m_EntryID.cb = 0;
}

MAPIFolder::MAPIFolder(const MAPIFolder &folder)
{
    m_folder = folder.m_folder;
    m_displayname = folder.m_displayname;
    CopyEntryID((SBinary &)folder.m_EntryID, m_EntryID);
}

void MAPIFolder::Initialize(LPMAPIFOLDER pFolder, LPTSTR displayName, LPSBinary pEntryId)
{
    if (m_folder != NULL)
        UlRelease(m_folder);
    if (m_EntryID.lpb != NULL)
        FreeEntryID(m_EntryID);
    m_folder = pFolder;
    m_displayname = displayName;
    CopyEntryID(*pEntryId, m_EntryID);
    if (m_session)
    {
        wstring wstrFolderPath = FindFolderPath();

        m_folderpath = wstrFolderPath;
    }
}

ExchangeSpecialFolderId MAPIFolder::GetExchangeFolderId()
{
    if (m_store && m_session)
    {
        SBinaryArray specialFolderIds = m_store->GetSpecialFolderIds();

        return Zimbra::MAPI::Util::GetExchangeSpecialFolderId(m_store->GetInternalMAPIStore(),
            m_EntryID.cb, (LPENTRYID)(m_EntryID.lpb), &specialFolderIds);
    }
    return SPECIAL_FOLDER_ID_NONE;
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
                    wstrPath = wstrPath + L"/" + pDisplayPropVal->Value.lpszW;
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
    return wstrPath;
}

HRESULT MAPIFolder::GetItemCount(ULONG &ulCount)
{
    ulCount = 0;
    if (m_folder == NULL)
        throw MAPIFolderException(E_FAIL, L"GetItemCount(): Folder Object is NULL.", __LINE__,
            __FILE__);

    HRESULT hr = S_OK;
    Zimbra::Util::ScopedBuffer<SPropValue> pPropValues;

    // Get PR_CONTENT_COUNT
    if (FAILED(hr = HrGetOneProp(m_folder, PR_CONTENT_COUNT, pPropValues.getptr())))
    {
        // if failed, try to read contents table and get count out of it.
        Zimbra::Util::ScopedInterface<IMAPITable> lpTable;

        if (FAILED(hr = m_folder->GetContentsTable(fMapiUnicode, lpTable.getptr())))
        {
            throw MAPIFolderException(E_FAIL, L"GetItemCount(): GetContentsTable() Failed.",
                __LINE__, __FILE__);
        }
        if (FAILED(hr = lpTable->GetRowCount(0, &ulCount)))
        {
            throw MAPIFolderException(E_FAIL, L"GetItemCount(): GetRowCount() Failed.",
                __LINE__, __FILE__);
        }
    }
    else
    {
        ulCount = pPropValues->Value.ul;
    }
    return hr;
}

HRESULT MAPIFolder::GetFolderIterator(FolderIterator &folderIter)
{
    HRESULT hr = S_OK;

    if (m_folder == NULL)
        return MAPI_E_NOT_FOUND;

    LPMAPITABLE pTable = NULL;

    if (FAILED(hr = m_folder->GetHierarchyTable(fMapiUnicode, &pTable)))
    {
        throw MAPIFolderException(E_FAIL, L"GetFolderIterator(): GetHierarchyTable Failed.",
            __LINE__, __FILE__);
    }

/*
 *      SPropTagArray arrPropTags;
 *      arrPropTags.cValues=1;
 *      arrPropTags.aulPropTag[0]=PR_CONTAINER_CLASS;
 *      if(FAILED(hr = pTable->SetColumns( (LPSPropTagArray)&arrPropTags, 0 )))
 *      {
 *
 *      }
 *      SPropValue spv;
 *      spv.dwAlignPad = 0;
 *      spv.ulPropTag = PR_CONTAINER_CLASS;
 *      spv.Value.LPSZ = L"ipm.note";
 *
 *      SRestriction sr[3];
 *      //sr[0].rt = RES_OR;
 *      //sr[0].res.resOr.cRes = 2;
 *      //sr[0].res.resOr.lpRes = &sr[1];
 *
 *              //sr[1].rt=RES_NOT;
 *              //sr[1].res.resNot.lpRes = &sr[4];
 *
 *                      //sr[1].rt= RES_EXIST;
 *                      //sr[1].res.resExist.ulPropTag=PR_CONTAINER_CLASS_W;
 * /*
 *      sr[2].rt= RES_AND;
 *      sr[2].res.resAnd.cRes = 2;
 *      sr[2].res.resAnd.lpRes = &sr[5];
 *              sr[5].rt = RES_EXIST;
 *              sr[5].res.resExist.ulPropTag=PR_CONTAINER_CLASS;
 *                      sr[6].rt = RES_PROPERTY ;
 *                      sr[6].res.resProperty.relop = RELOP_RE ;
 *                      sr[6].res.resContent.ulFuzzyLevel = FL_IGNORECASE ;
 *                      sr[6].res.resProperty.lpProp = &spv;
 *
 *      sr[0].rt= RES_AND;
 *      sr[0].res.resAnd.cRes = 2;
 *      sr[0].res.resAnd.lpRes = &sr[1];
 *              sr[1].rt = RES_EXIST;
 *              sr[1].res.resExist.ulPropTag=PR_CONTAINER_CLASS;
 *                      sr[2].rt = RES_PROPERTY ;
 *                      sr[2].res.resProperty.relop = RELOP_RE ;
 *                      sr[2].res.resContent.ulFuzzyLevel = FL_IGNORECASE ;
 *                      sr[2].res.resProperty.lpProp = &spv;
 *
 *      hr=pTable->Restrict(&sr[0],0);
 */
    folderIter.Initialize(pTable, m_folder, *m_session);
    return S_OK;
}

HRESULT MAPIFolder::GetMessageIterator(MessageIterator &msgIterator)
{
    if (m_folder == NULL)
    {
        throw MAPIFolderException(E_FAIL, L"GetMessageIterator(): Folder Object is NULL.",
            __LINE__, __FILE__);
    }

    HRESULT hr = S_OK;
    LPMAPITABLE pContentsTable = NULL;

    if (FAILED(hr = m_folder->GetContentsTable(fMapiUnicode, &pContentsTable)))
    {
        throw MAPIFolderException(E_FAIL, L"GetMessageIterator(): GetContentsTable Failed.",
            __LINE__, __FILE__);
    }
    // Init message iterator
    msgIterator.Initialize(pContentsTable, m_folder, *m_session);
    return S_OK;
}
