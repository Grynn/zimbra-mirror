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

    retval = Initialize(AccountID);
    // Logger = CSingleton::getInstance();

    dlogd("initialize", "AccountID:", AccountID, "AccountName:", AccountName);
    // Create Session and Open admin store.
    // Its a static function and store/session will be used commonly by all mailboxes.
    // MAPIAccessAPI::InitGlobalSessionAndStore(host,admin);
    // TODO for Karuna: Call MAPIAccessAPI::UnInitGlobalSessionAndStore() to realse global session and store.
    // Specify user.
  
	hr = MapiObj->Initializeuser(AccountID,pErrorText);

    
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
       		hr = MapiObj->Initializeuser(L"",pErrorText);
    }
    *pErrorText = (lpwstrStatus) ? CComBSTR(lpwstrStatus) : SysAllocString(L"");
    return hr;
}

STDMETHODIMP CUserObject::UMUnInitializeUser()
{
    dlogd("UMUnInitializeUser");
    MapiObj->UnInitializeuser();
    MAPIAccessAPI::UnInitGlobalSessionAndStore();
    return S_OK;
}

STDMETHODIMP CUserObject::SMUnInitializeUser()
{
    dlogd("SMUnInitializeUser");
    MapiObj->UnInitializeuser();
    return S_OK;
}

STDMETHODIMP CUserObject::GetFolderObjects( /*[out, retval]*/ VARIANT *vObjects)
{
    dlogi("Begin GetFolderObjects");

    HRESULT hr = S_OK;

    VariantInit(vObjects);
	hr = MapiObj->GetFolderList(vObjects);
    dlogi("End GetFolderObjects");

    return hr;
}

STDMETHODIMP CUserObject::GetItemsForFolderObjects(IfolderObject *FolderObj,
    VARIANT creattiondate, VARIANT *vItems)
{
    dlogi("Begin GetItemsForFolderObjects");

    HRESULT hr = S_OK;

    VariantInit(vItems);

	hr = MapiObj->GetItemsList(FolderObj,creattiondate,vItems);

    dlogi("End GetItemsForFolderObjects");

    return S_OK;
}

STDMETHODIMP CUserObject::GetMapiAccessObject(BSTR UserID,IMapiAccessWrap **pVal)
{
	HRESULT hr= S_OK;
	CComBSTR userID = UserID;
	(*pVal) = MapiObj;
	(*pVal)->AddRef();

	return hr;
}