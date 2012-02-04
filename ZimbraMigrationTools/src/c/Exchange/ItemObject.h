// ItemObject.h : Declaration of the CItemObject

#pragma once
#include "resource.h"                           // main symbols

#include "Exchange_i.h"
#include "folderObject.h"
#include "BaseItem.h"
#include "Exchange.h"
#include "ExchangeAdmin.h"
#include "..\Exchange\MAPIAccessAPI.h"



// CItemObject

class ATL_NO_VTABLE CItemObject :
	public CComObjectRootEx<CComMultiThreadModel>,
	public CComCoClass<CItemObject, &CLSID_ItemObject>,public BaseItem,
	public ISupportErrorInfo,
	public IDispatchImpl<IItemObject, &IID_IItemObject, &LIBID_Exchange, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
private:
    /*BSTR ID;*/
    FolderType TYPE;
    SBinary ItemID;

    CComQIPtr<IFolderObject, &IID_IFolderObject> parentObj;

public:
	CItemObject()
	{
	}

DECLARE_REGISTRY_RESOURCEID(IDR_ITEMOBJECT)


BEGIN_COM_MAP(CItemObject)
	COM_INTERFACE_ENTRY(IItemObject)
	COM_INTERFACE_ENTRY(IDispatch)
	COM_INTERFACE_ENTRY(ISupportErrorInfo)
END_COM_MAP()

// ISupportsErrorInfo
	STDMETHOD(InterfaceSupportsErrorInfo)(REFIID riid);


	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
            CComObject<CFolderObject> *obj = NULL;
        CComObject<CFolderObject>::CreateInstance(&obj);
        // BSTR str1;
        obj->put_Name(L"test");
        obj->put_Id(12202);
        parentObj = obj;                        // This automatically AddRef's

		return S_OK;
	}

	void FinalRelease()
	{
	}

public:

    STDMETHOD(get_ID) (BSTR *pVal);
    STDMETHOD(put_ID) (BSTR newVal);
    STDMETHOD(get_Type) (FolderType * pVal);
    STDMETHOD(put_Type) (FolderType newVal);
    STDMETHOD(get_Parentfolder) (IFolderObject * *pVal);
    STDMETHOD(put_Parentfolder) (IFolderObject * newVal);
    STDMETHOD(get_CreationDate) (VARIANT * pVal);
    STDMETHOD(put_CreationDate) (VARIANT newVal);
    

    STDMETHOD(put_ItemID) (VARIANT id);
    STDMETHOD(get_ItemID) (VARIANT * id);
    STDMETHOD(GetDataForItemID) (IUserObject *Userobj,VARIANT ItemId, FolderType type, VARIANT * pVal);

};

OBJECT_ENTRY_AUTO(__uuidof(ItemObject), CItemObject)
