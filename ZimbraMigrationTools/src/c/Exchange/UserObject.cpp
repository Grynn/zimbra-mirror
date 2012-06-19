// UserObject.cpp : Implementation of CUserObject

#include "common.h"
#include "Logger.h"
#include "UserObject.h"

STDMETHODIMP CUserObject::InterfaceSupportsErrorInfo(REFIID riid)
{
    static const IID* const arr[] = 
    {
        &IID_IUserObject
    };

    for (int i = 0; i < sizeof (arr) / sizeof (arr[0]); i++)
    {
        if (InlineIsEqualGUID(*arr[i], riid))
            return S_OK;
    }
    return S_FALSE;
}

STDMETHODIMP CUserObject::Init(BSTR host, BSTR location, BSTR account, BSTR *pErrorText)
{
    wchar_t buf[1024];
    HRESULT hr = S_FALSE;

    GetTempPath(sizeof (buf) / sizeof (wchar_t), buf);
    wcscat(buf, account);
    wcscat(buf, L".log");
    dlog.open(buf);
    dlogi(L"Init", Log::KV<BSTR>(L"host", host), Log::KV<BSTR>(L"location", location),
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
    if (FAILED(hr))
    {
        CComBSTR str = "Init error ";

        str.AppendBSTR(*pErrorText);
        dlog.err(str);
    }
    return hr;
}

STDMETHODIMP CUserObject::Uninit()
{
    HRESULT hr = S_OK;

    dlog.trace(L"Begin UnInit");
    hr = mapiObj->UserUninit();
    dlog.trace(L"End UnInit");
    return hr;
}

STDMETHODIMP CUserObject::GetFolders(VARIANT *vObjects)
{
    HRESULT hr = S_OK;

dlog.dump(L"--------------", L"aaaaaaaa\r\n   bbbb\r\nccccccc");
    dlog.trace(L"Begin GetFolders");
    VariantInit(vObjects);
    hr = mapiObj->GetFolderList(vObjects);    
    if (FAILED(hr))
    {
          CComBSTR str = "End GetFolders " ;
          str += hr;
          dlog.err(str);
    }
    else
    {
        dlog.trace(L"End GetFolders");
    }
    return hr;
}

STDMETHODIMP CUserObject::GetItemsForFolder(IFolderObject *folderObj, VARIANT creationDate,
    VARIANT *vItems)
{
    HRESULT hr = S_OK;
    dlog.trace(L"Begin GetItemsForFolder");
    VariantInit(vItems);
    hr = mapiObj->GetItemsList(folderObj, creationDate, vItems);
    if (FAILED(hr))
    {
         CComBSTR str = "End GetItemsForFolder " ;
         str += hr;
         dlog.err(str);
    }
    else
    {
        dlog.trace(L"End GetItemsForFolder");
    }
    return hr;
}

STDMETHODIMP CUserObject::GetMapiAccessObject(BSTR userID, IMapiAccessWrap **pVal)
{
    HRESULT hr = S_OK;

    (void)userID;
    *pVal = mapiObj;
    hr = (*pVal)->AddRef();
    if (FAILED(hr))
    {
         CComBSTR str = "GetMapiAccessObject error ";

         str += hr;
         dlog.err(str);
    }
    return hr;
}

STDMETHODIMP CUserObject::GetOOO(BSTR *pOOO)
{
    HRESULT hr = S_OK;
    
    dlog.trace(L"Begin GetOOO");
    hr = mapiObj->GetOOOInfo(pOOO);
    if (FAILED(hr))
    {
         CComBSTR str = "End GetOOO ";
         
         str += hr;
         dlog.err(str);
    }
    else
    {
        dlog.trace(L"End GetOOO");
    }
    return hr;
}

STDMETHODIMP CUserObject::GetRules(VARIANT *vRules)
{
    HRESULT hr = S_OK;
    
    dlog.trace(L"Begin GetRules");
    VariantInit(vRules);
    hr = mapiObj->GetRuleList(vRules);
    if (FAILED(hr))
    {
         CComBSTR str = "End GetRules ";

         str += hr;
         dlog.err(str);
    }
    else
    {
        dlog.trace(L"End GetRules");
    }
    return hr;
}
