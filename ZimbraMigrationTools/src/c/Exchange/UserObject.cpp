// UserObject.cpp : Implementation of CUserObject

#include "common.h"
#include "Logger.h"
#include "UserObject.h"


// CUserObject

STDMETHODIMP CUserObject::InterfaceSupportsErrorInfo(REFIID riid)
{
	static const IID* const arr[] = 
	{
		&IID_IUserObject
	};

	for (int i=0; i < sizeof(arr) / sizeof(arr[0]); i++)
	{
		if (InlineIsEqualGUID(*arr[i],riid))
			return S_OK;
	}
	return S_FALSE;
}

STDMETHODIMP CUserObject::Init(BSTR host, BSTR location, BSTR account, BSTR *pErrorText)
{
    HRESULT hr = S_OK;

    dlogd(L"Initialize", Log::KV<BSTR>(L"host", host), Log::KV<BSTR>(L"location", location),
        Log::KV<BSTR>(L"account", account));
    MailType = L"MAPI";
    UserID = location;
    if (host && *host)
    {
        hr = mapiObj->UserInit(location, account, pErrorText);
    }
    else
    {
        LPCWSTR err = MAPIAccessAPI::InitGlobalSessionAndStore(location);

        if (err)
            *pErrorText = CComBSTR(err);
        else
            hr = mapiObj->UserInit(L"", account, pErrorText);
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

STDMETHODIMP CUserObject::GetItemsForFolder(IFolderObject *folderObj, VARIANT creationDate,
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

STDMETHODIMP CUserObject::GetOOO(BSTR *pOOO)
{
    return mapiObj->GetOOOInfo(pOOO);
}

STDMETHODIMP CUserObject::GetRules(VARIANT *vRules)
{
    VariantInit(vRules);
    return mapiObj->GetRuleList(vRules);
}
