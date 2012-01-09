// UserObject.cpp : Implementation of CUserObject

#include "common.h"
#include "Logger.h"
#include "UserObject.h"

STDMETHODIMP CUserObject::InterfaceSupportsErrorInfo(REFIID riid)
{
    static const IID *const arr[] = {
        &IID_IUserObject
    };

    for (int i = 0; i < sizeof (arr) / sizeof (arr[0]); i++)
    {
        if (InlineIsEqualGUID(*arr[i], riid))
            return S_OK;
    }
    return S_FALSE;
}

long CUserObject::Initialize(BSTR Id)
{
    wstring path(dlog.file());
    wstring::size_type pos = path.rfind('\\');

    path.erase(pos + 1);
    path += Id;
    path += L".log";
    dlog.open(path.c_str());
    UserID = Id;
    MailType = L"MAPI";
    return 0;
}

long CUserObject::GetFolders(VARIANT *folders)
{
    VariantInit(folders);
    return 0;
}

long CUserObject::GetItems(VARIANT *Items)
{
    VariantInit(Items);
    return 0;
}

long CUserObject::UnInitialize()
{
    return 0;
}

STDMETHODIMP CUserObject::InitializeUser(BSTR host, BSTR admin, BSTR AccountID,
    BSTR AccountName, BSTR *pErrorText)
{
    HRESULT hr = S_OK;
    long retval = 0;
    BSTR temp;

    temp = host;
    temp = admin;
    // ////////////

    retval = Initialize(AccountName);
    // Logger = CSingleton::getInstance();

    dlogd("initialize", AccountID, L" ", AccountName);
    // Create Session and Open admin store.
    // Its a static function and store/session will be used commonly by all mailboxes.
    // MAPIAccessAPI::InitGlobalSessionAndStore(host,admin);
    // TODO for Karuna: Call MAPIAccessAPI::UnInitGlobalSessionAndStore() to realse global session and store.
    // Specify user.
    maapi = new Zimbra::MAPI::MAPIAccessAPI(AccountID);

    // Init session and stores
    LPCWSTR lpStatus = maapi->InitializeUser();

    *pErrorText = (lpStatus) ? CComBSTR(lpStatus) : SysAllocString(L"");
    return hr;
}

STDMETHODIMP CUserObject::UMInitializeUser(BSTR ProfileName, BSTR AccountName,
    BSTR *pErrorText)
{
    HRESULT hr = S_OK;
    long retval = 0;

    retval = Initialize(AccountName);
    // Initialize the Mapi API..

    dlogd("UMInitializeUser ", ProfileName, L" ", AccountName);

    LPCWSTR lpwstrStatus = MAPIAccessAPI::InitGlobalSessionAndStore(ProfileName);

    if (!lpwstrStatus)
    {
        maapi = new Zimbra::MAPI::MAPIAccessAPI(L"");
        lpwstrStatus = maapi->InitializeUser();
    }
    *pErrorText = (lpwstrStatus) ? CComBSTR(lpwstrStatus) : SysAllocString(L"");
    return hr;
}

STDMETHODIMP CUserObject::UMUnInitializeUser()
{
    dlogd("UnInitializeUser");
    MAPIAccessAPI::UnInitGlobalSessionAndStore();
    return S_OK;
}

STDMETHODIMP CUserObject::GetFolderObjects( /*[out, retval]*/ VARIANT *vObjects)
{
    dlogi("Begin GetFolderObjects");

    HRESULT hr = S_OK;

    VariantInit(vObjects);
    vObjects->vt = VT_ARRAY | VT_DISPATCH;

    SAFEARRAY *psa;

    USES_CONVERSION;
    vector<Folder_Data> vfolderlist;

    maapi->GetRootFolderHierarchy(vfolderlist);

    std::vector<Folder_Data>::iterator it;
    size_t size = vfolderlist.size();

    it = vfolderlist.begin();

    SAFEARRAYBOUND bounds = { (ULONG)size, 0 };

    psa = SafeArrayCreate(VT_DISPATCH, 1, &bounds);

    IfolderObject **pfolders;

    SafeArrayAccessData(psa, (void **)&pfolders);
    for (size_t i = 0; i < size; i++, it++)
    {
        CComPtr<IfolderObject> pIFolderObject;

        hr = CoCreateInstance(CLSID_folderObject, NULL, CLSCTX_ALL, IID_IfolderObject,
            reinterpret_cast<void **>(&pIFolderObject));
        if (SUCCEEDED(hr))
        {
            CComBSTR temp((*it).name.c_str());

            pIFolderObject->put_Name(SysAllocString(temp));
            pIFolderObject->put_Id((*it).zimbraid);

            CComBSTR tempS((*it).folderpath.c_str());

            pIFolderObject->put_FolderPath(SysAllocString(tempS));

            CComBSTR temp3((*it).containerclass.c_str());

            pIFolderObject->put_ContainerClass(SysAllocString(temp3));

            pIFolderObject->put_ItemCount((*it).itemcount);

            // /////////////////////////////////////
            VARIANT var;
            SBinary Folderid = (*it).sbin;

            VariantInit(&var);                  // Initialize our variant
            // Set the type to an array of unsigned chars (OLE SAFEARRAY)
            var.vt = VT_ARRAY | VT_UI1;

            // Set up the bounds structure
            SAFEARRAYBOUND rgsabound[1];

            rgsabound[0].cElements = Folderid.cb;
            rgsabound[0].lLbound = 0;
            // Create an OLE SAFEARRAY
            var.parray = SafeArrayCreate(VT_UI1, 1, rgsabound);
            if (var.parray != NULL)
            {
                void *pArrayData = NULL;

                // Get a safe pointer to the array
                SafeArrayAccessData(var.parray, &pArrayData);
                // Copy data to it
                memcpy(pArrayData, Folderid.lpb, Folderid.cb);
                // Unlock the variant data
                SafeArrayUnaccessData(var.parray);
            }
            pIFolderObject->put_FolderID(var);
            // /////////////////////////////////////////////
        }
        if (FAILED(hr))
            return S_FALSE;
        pIFolderObject.CopyTo(&pfolders[i]);
    }
    SafeArrayUnaccessData(psa);
    vObjects->parray = psa;

    dlogi("End GetFolderObjects");

    return hr;
}

STDMETHODIMP CUserObject::GetItemsForFolderObjects(IfolderObject *FolderObj,
    VARIANT creattiondate, VARIANT *vItems)
{
    dlogi("Begin GetItemsForFolderObjects");

    HRESULT hr = S_OK;

    VariantInit(vItems);
    vItems->vt = VT_ARRAY | VT_DISPATCH;

    SAFEARRAY *psa;

    vector<Item_Data> vItemDataList;
    vector<Item_Data>::iterator it;

    SBinary folderEntryid;
    folderEntryid.cb = 0;
    folderEntryid.lpb = NULL;

    USES_CONVERSION;

    VARIANT vararg;

    VariantInit(&vararg);
    vararg.vt = (VT_ARRAY | VT_UI1);

    FolderObj->get_FolderID(&vararg);
    if (vararg.vt == (VT_ARRAY | VT_UI1))       // (OLE SAFEARRAY)
    {
        // Retrieve size of array
        folderEntryid.cb = vararg.parray->rgsabound[0].cElements;

        ULONG size = folderEntryid.cb;

        folderEntryid.lpb = new BYTE[size];     // Allocate a buffer to store the data
        if (folderEntryid.lpb != NULL)
        {
            void *pArrayData;

            // Obtain safe pointer to the array
            SafeArrayAccessData(vararg.parray, &pArrayData);
            // Copy the bitmap into our buffer
            memcpy(folderEntryid.lpb, pArrayData, size);        // Unlock the variant data
            SafeArrayUnaccessData(vararg.parray);

            maapi->GetFolderItemsList(folderEntryid, vItemDataList);
        }
    }

    size_t size = vItemDataList.size();

    it = vItemDataList.begin();

    SAFEARRAYBOUND bounds = { (ULONG)size, 0 };

    psa = SafeArrayCreate(VT_DISPATCH, 1, &bounds);

    IItemObject **pItems;

    SafeArrayAccessData(psa, (void **)&pItems);
    for (size_t i = 0; i < size; i++, it++)
    {
        CComPtr<IItemObject> pIItemObject;
        // Isampleobj* pIStatistics;
        hr = CoCreateInstance(CLSID_ItemObject, NULL, CLSCTX_ALL, IID_IItemObject,
            reinterpret_cast<void **>(&pIItemObject));
        if (SUCCEEDED(hr))
        {
            /*pIFolderObject->put_Name(L"testoing"); // so far so good
                * pIFolderObject->put_Id(12222);
                * pIFolderObject->put_ParentPath(L"\\Inbox\\personal\\mine");*/

            pIItemObject->put_Type((FolderType)((*it).lItemType));
            // pIItemObject->put_ID((*it).sbMessageID))
            pIItemObject->put_Parentfolder(FolderObj);
            creattiondate.vt = VT_DATE;
            creattiondate.date = (long)(*it).MessageDate;

// /////////////////////////////////////////////

            VARIANT var;
            SBinary Itemid = (*it).sbMessageID;

            VariantInit(&var);              // Initialize our variant
            // Set the type to an array of unsigned chars (OLE SAFEARRAY)
            var.vt = VT_ARRAY | VT_UI1;

            // Set up the bounds structure
            SAFEARRAYBOUND rgsabound[1];

            rgsabound[0].cElements = Itemid.cb;
            rgsabound[0].lLbound = 0;
            // Create an OLE SAFEARRAY
            var.parray = SafeArrayCreate(VT_UI1, 1, rgsabound);
            if (var.parray != NULL)
            {
                void *pArrayData = NULL;

                // Get a safe pointer to the array
                SafeArrayAccessData(var.parray, &pArrayData);
                // Copy data to it
                memcpy(pArrayData, Itemid.lpb, Itemid.cb);
                // Unlock the variant data
                SafeArrayUnaccessData(var.parray);
            }
            pIItemObject->put_ItemID(var);
        }
        if (FAILED(hr))
            return S_FALSE;
        // if

        {
            pIItemObject.CopyTo(&pItems[i]);
        }
    }
    SafeArrayUnaccessData(psa);
    vItems->parray = psa;

    if (folderEntryid.lpb != NULL)
    {
        delete folderEntryid.lpb;
    }

    dlogi("End GetItemsForFolderObjects");

    return S_OK;
}

STDMETHODIMP CUserObject::GetDataForItem(VARIANT ItemId, VARIANT *pVal)
{
    ContactItemData cd;
    SBinary ItemID;

    if (ItemId.vt == (VT_ARRAY | VT_UI1))       // (OLE SAFEARRAY)
    {
        // Retrieve size of array
        ItemID.cb = ItemId.parray->rgsabound[0].cElements;
        ItemID.lpb = new BYTE[ItemID.cb];       // Allocate a buffer to store the data
        if (ItemID.lpb != NULL)
        {
            void *pArrayData;

            // Obtain safe pointer to the array
            SafeArrayAccessData(ItemId.parray, &pArrayData);
            // Copy the bitmap into our buffer
            memcpy(ItemID.lpb, pArrayData, ItemID.cb);  // Unlock the variant data
            SafeArrayUnaccessData(ItemId.parray);

            maapi->GetItem(ItemID, cd);
	    delete ItemID.lpb;
        }
    }

    HRESULT hr = S_OK;
    std::map<BSTR, BSTR> pIt;
    std::map<BSTR, BSTR>::iterator it;

    pIt[L"BirthDay"] = SysAllocString((cd.Birthday).c_str());
    pIt[L"FirstName"] = SysAllocString((cd.FirstName).c_str());
    pIt[L"JobTitle"] = SysAllocString((cd.JobTitle).c_str());
    pIt[L"CallbackPhone"] = SysAllocString((cd.CallbackPhone).c_str());
    pIt[L"Email1"] = SysAllocString((cd.Email1).c_str());

    VariantInit(pVal);

    // Create SafeArray of VARIANT BSTRs
    SAFEARRAY *pSA = NULL;
    SAFEARRAYBOUND aDim[2];                     // two dimensional array

    aDim[0].lLbound = 0;
    aDim[0].cElements = 5;
    aDim[1].lLbound = 0;
    aDim[1].cElements = 5;                      // rectangular array
    pSA = SafeArrayCreate(VT_BSTR, 2, aDim);    // again, 2 dimensions

    long aLong[2];

    if (pSA != NULL)
    {
        BSTR temp;

        for (long x = aDim[0].lLbound; x < 2 /*(aDim[0].cElements + aDim[0].lLbound)*/; x++)
        {
            aLong[0] = x;                       // set x index
            it = pIt.begin();
            for (long y = aDim[1].lLbound; y < (long)(aDim[1].cElements + aDim[1].lLbound); y++)
            {
                aLong[1] = y;                   // set y index
                if (aLong[0] > 0)
                    temp = SysAllocString((*it).second);
                else
                    temp = SysAllocString((*it).first);
                hr = SafeArrayPutElement(pSA, aLong, temp);

                it++;
            }
        }
    }
    pVal->vt = VT_ARRAY | VT_BSTR;
    pVal->parray = pSA;

    return hr;
}
