// ItemObject.h : Declaration of the CItemObject

#pragma once
#include "resource.h"                           // main symbols

#include "Exchange_i.h"
#include "folderObject.h"
#include "BaseItem.h"
#include "Exchange.h"
#include "ExchangeAdmin.h"
#include "..\Exchange\MAPIAccessAPI.h"

/*
 *
 * #if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
 * #error "Single-threaded COM objects are not properly supported on Windows CE platform, such as the Windows Mobile platforms that do not include full DCOM support. Define _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA to force ATL to support creating single-thread COM object's and allow use of it's single-threaded COM object implementations. The threading model in your rgs file was set to 'Free' as that is the only threading model supported in non DCOM Windows CE platforms."
 * #endif
 *
 * using namespace ATL;
 */

// CItemObject
Zimbra::MAPI::MAPIAccessAPI *maapi;

class ATL_NO_VTABLE CItemObject: public CComObjectRootEx<CComSingleThreadModel>, public
    CComCoClass<CItemObject, &CLSID_ItemObject>, public ISupportErrorInfo, public BaseItem,
    public IDispatchImpl<IItemObject, &IID_IItemObject, &LIBID_Exchange, /*wMajor =*/ 1,
    /*wMinor =*/ 0>
{
private:
    /*BSTR ID;*/
    FolderType TYPE;
    SBinary ItemID;

    CComQIPtr<IfolderObject, &IID_IfolderObject> parentObj;

public:
    CItemObject() {}
    DECLARE_REGISTRY_RESOURCEID(IDR_ITEMOBJECT) BEGIN_COM_MAP(CItemObject) COM_INTERFACE_ENTRY(
        IItemObject) COM_INTERFACE_ENTRY(IDispatch) COM_INTERFACE_ENTRY(
        ISupportErrorInfo) END_COM_MAP()
// ISupportsErrorInfo
    STDMETHOD(InterfaceSupportsErrorInfo) (REFIID riid);

    DECLARE_PROTECT_FINAL_CONSTRUCT() HRESULT FinalConstruct()
    {
        CComObject<CfolderObject> *obj = NULL;
        CComObject<CfolderObject>::CreateInstance(&obj);
        // BSTR str1;
        obj->put_Name(L"test");
        obj->put_Id(12202);
        parentObj = obj;                        // This automatically AddRef's

        return S_OK;
    }

    void FinalRelease() {}

public:
    STDMETHOD(get_ID) (BSTR *pVal);
    STDMETHOD(put_ID) (BSTR newVal);
    STDMETHOD(get_Type) (FolderType * pVal);
    STDMETHOD(put_Type) (FolderType newVal);
    STDMETHOD(get_Parentfolder) (IfolderObject * *pVal);
    STDMETHOD(put_Parentfolder) (IfolderObject * newVal);
    STDMETHOD(get_CreationDate) (VARIANT * pVal);
    STDMETHOD(put_CreationDate) (VARIANT newVal);
    STDMETHOD(GetDataForItem) (VARIANT * pVal);

    STDMETHOD(put_ItemID) (VARIANT id);
    STDMETHOD(get_ItemID) (VARIANT * id);
    STDMETHOD(GetDataForItemID) (VARIANT ItemId, FolderType type, VARIANT * pVal);
};

OBJECT_ENTRY_AUTO(__uuidof(ItemObject), CItemObject)
