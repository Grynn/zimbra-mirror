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
    wchar_t buf[1024];
    HRESULT hr = S_OK;

    GetTempPath(sizeof (buf) / sizeof (wchar_t), buf);
    wcscat(buf, account);
    wcscat(buf, L".log");
    dlog.open(buf);
   
    dlogd(L"Initialize", Log::KV<BSTR>(L"host", host), Log::KV<BSTR>(L"location", location),
        Log::KV<BSTR>(L"account", account));
    MailType = L"MAPI";
    UserID = location;
    if (host && *host)
    {
        dlog.info(L"UserInit",Log::KV<BSTR>(L"account", account));
        hr = mapiObj->UserInit(location, account, pErrorText);
        if(FAILED(hr))
        {
            CComBSTR str ="error in UserInit " ;
            str.AppendBSTR(*pErrorText);
           dlog.err(str);
           return hr;
        }
    }
    else
    {
        dlog.info(L"InitGlobalSessionAndStore",Log::KV<BSTR>(L"location", location));
        LPCWSTR err = MAPIAccessAPI::InitGlobalSessionAndStore(location);

        if (err)
            *pErrorText = CComBSTR(err);
        else
            {
                hr = mapiObj->UserInit(L"", account, pErrorText);
                 if(FAILED(hr))
                {
                   CComBSTR str ="error in UserInit " ;
                   str.AppendBSTR(*pErrorText);
                   dlog.err(str);
                   return hr;
                }
        }
    }
    return hr;
}

STDMETHODIMP CUserObject::Uninit()
{
   HRESULT hr = S_OK;
   dlog.debug(L" UnInit");
   hr= mapiObj->UserUninit();
    return hr;
}

STDMETHODIMP CUserObject::GetFolders(VARIANT *vObjects)
{
     HRESULT hr = S_OK;
     dlog.debug(L"GetFolders");
    VariantInit(vObjects);
    
    hr = mapiObj->GetFolderList(vObjects);
    
    if(FAILED(hr))
    {
         CComBSTR str ="error in GetFolders Hresult error value is" ;
          str += hr;
          dlog.err(str);
          return hr;
    }
    return hr;
}

STDMETHODIMP CUserObject::GetItemsForFolder(IFolderObject *folderObj, VARIANT creationDate,
    VARIANT *vItems)
{
    HRESULT hr = S_OK;
     dlog.debug(L"GetItemsForFolder");
    VariantInit(vItems);
    hr = mapiObj->GetItemsList(folderObj, creationDate, vItems);
    if(FAILED(hr))
    {
         CComBSTR str ="error in GetItemsForFolder Hresult error value is" ;
          str += hr;
          dlog.err(str);
          return hr;
    }
    return hr;
}

STDMETHODIMP CUserObject::GetMapiAccessObject(BSTR userID, IMapiAccessWrap **pVal)
{
    HRESULT hr = S_OK;
    (void)userID;
    (*pVal) = mapiObj;
   hr =  (*pVal)->AddRef();
   if(FAILED(hr))
    {
         CComBSTR str ="error in GetMapiAccessObject Hresult error value is" ;
          str += hr;
          dlog.err(str);
          return hr;
    }
    return hr;
}

STDMETHODIMP CUserObject::GetOOO(BSTR *pOOO)
{
    HRESULT hr = S_OK;
     dlog.debug(L"GetOOO");
    hr= mapiObj->GetOOOInfo(pOOO);
    if(FAILED(hr))
    {
         CComBSTR str ="error in GetOOO Hresult error value is" ;
          str += hr;
          dlog.err(str);
          return hr;
    }
    return hr;
}

STDMETHODIMP CUserObject::GetRules(VARIANT *vRules)
{
    HRESULT hr = S_OK;
     dlog.debug(L"GetRules");
    VariantInit(vRules);
    hr= mapiObj->GetRuleList(vRules);
    if(FAILED(hr))
    {
         CComBSTR str ="error in GetRuleList Hresult error value is" ;
          str += hr;
          dlog.err(str);
          return hr;
    }
    return hr;
}
