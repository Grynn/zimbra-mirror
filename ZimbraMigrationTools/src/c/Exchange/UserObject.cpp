// UserObject.cpp : Implementation of CUserObject

#include "common.h"
#include "Logger.h"
#include "UserObject.h"

STDMETHODIMP CUserObject::InterfaceSupportsErrorInfo(REFIID riid)
{
    static const IID *const arr[] = { &IID_IUserObject };

    for (int i = 0; i < sizeof (arr) / sizeof (arr[0]); i++)
    {
        if (InlineIsEqualGUID(*arr[i], riid))
            return S_OK;
    }
    return S_FALSE;
}

/*
 * long CUserObject::Init(BSTR Id)
 * {
 *  MailType = L"MAPI";
 *  UserID = Id;
 *  return 0;
 * }
 *
 * long CUserObject::GetFolders(VARIANT *folders)
 * {
 *  VariantInit(folders);
 *  return 0;
 * }
 *
 * long CUserObject::GetItems(VARIANT *Items)
 * {
 *  VariantInit(Items);
 *  return 0;
 * }
 *
 * void CUserObject::Uninit(void)
 * {
 * }
 */
STDMETHODIMP CUserObject::Init(BSTR host, BSTR location, BSTR account, BSTR *pErrorText)
{
    HRESULT hr = S_OK;

    dlogd(L"Initialize", Log::KV<BSTR>(L"host", host), Log::KV<BSTR>(L"location", location),
        Log::KV<BSTR>(L"account", account));
    MailType = L"MAPI";
    UserID = location;
    if (host && *host)
    {
        hr = mapiObj->UserInit(location, pErrorText);
    }
    else
    {
        LPCWSTR err = MAPIAccessAPI::InitGlobalSessionAndStore(location);

        if (err)
            *pErrorText = CComBSTR(err);
        else
            hr = mapiObj->UserInit(L"", pErrorText);
    }
    return hr;
}

STDMETHODIMP CUserObject::Uninit()
{
    mapiObj->UserUninit();
    return S_OK;
}

STDMETHODIMP CUserObject::GetFolders(VARIANT *vObjects)
{
    VariantInit(vObjects);
    return mapiObj->GetFolderList(vObjects);
}

STDMETHODIMP CUserObject::GetItemsForFolder(IfolderObject *folderObj, VARIANT creationDate,
    VARIANT *vItems)
{
    VariantInit(vItems);
    return mapiObj->GetItemsList(folderObj, creationDate, vItems);
}

STDMETHODIMP CUserObject::GetMapiAccessObject(BSTR userID, IMapiAccessWrap **pVal)
{
    (void)userID;
    (*pVal) = mapiObj;
    (*pVal)->AddRef();
    return S_OK;
}
