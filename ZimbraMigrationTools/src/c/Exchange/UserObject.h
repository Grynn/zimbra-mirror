// UserObject.h : Declaration of the CUserObject

#pragma once
#include "resource.h"
#include "Exchange_i.h"
#include "Logger.h"
#include "Exchange.h"
#include "ExchangeAdmin.h"
#include "..\Exchange\MAPIAccessAPI.h"
#include "BaseUser.h"

class ATL_NO_VTABLE CUserObject: public CComObjectRootEx<CComSingleThreadModel>, public
    CComCoClass<CUserObject, &CLSID_UserObject>, public BaseUser, public ISupportErrorInfo,
    public IDispatchImpl<IUserObject, &IID_IUserObject, &LIBID_Exchange, /*wMajor =*/ 1,
    /*wMinor =*/ 0>
{
public:
    CUserObject() {}
    DECLARE_REGISTRY_RESOURCEID(IDR_USEROBJECT) BEGIN_COM_MAP(CUserObject) COM_INTERFACE_ENTRY(
        IUserObject) COM_INTERFACE_ENTRY(IDispatch) COM_INTERFACE_ENTRY(
        ISupportErrorInfo) END_COM_MAP()
    // ISupportsErrorInfo
    STDMETHOD(InterfaceSupportsErrorInfo) (REFIID riid);

    DECLARE_PROTECT_FINAL_CONSTRUCT() HRESULT FinalConstruct()
    {
        return S_OK;
    }
    void FinalRelease() {}

public:
    STDMETHOD(InitializeUser) (BSTR host, BSTR admin, BSTR AccountID, BSTR AccountName,
        BSTR *pErrorText);
    STDMETHOD(GetFolderObjects) ( /*[out, retval]*/ VARIANT * vObjects);
    STDMETHOD(GetItemsForFolderObjects) (IfolderObject * FolderObj, VARIANT creattiondate,
        VARIANT * vItems);
    STDMETHOD(GetDataForItem) (VARIANT ItemId, VARIANT * pVal);
    STDMETHOD(UMInitializeUser) (BSTR ProfileName, BSTR AccountName, BSTR *pErrorText);
    STDMETHOD(UMUnInitializeUser) ();

    virtual long Initialize(BSTR Id);
    virtual long GetFolders(VARIANT *folders);
    virtual long GetItems(VARIANT *Items);
    virtual long UnInitialize();

    Zimbra::MAPI::MAPIAccessAPI *maapi;
};

OBJECT_ENTRY_AUTO(__uuidof(UserObject), CUserObject)
