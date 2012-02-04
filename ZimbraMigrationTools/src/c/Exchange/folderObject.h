// FolderObject.h : Declaration of the CFolderObject

#pragma once
#include "resource.h"                           // main symbols

#include "Exchange_i.h"
#include "BaseFolder.h"
#include "MAPIDefs.h"


// CFolderObject

class ATL_NO_VTABLE CFolderObject :
	public CComObjectRootEx<CComMultiThreadModel>,
	public CComCoClass<CFolderObject, &CLSID_FolderObject>,public BaseFolder,
	public ISupportErrorInfo,
	public IDispatchImpl<IFolderObject, &IID_IFolderObject, &LIBID_Exchange, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
    private:
    /* BSTR Strname;
     * LONG LngID;
     * BSTR parentPath;*/
    SBinary FolderId;
    LONG Itemcnt;

public:
	CFolderObject()
	{
	}

DECLARE_REGISTRY_RESOURCEID(IDR_FOLDEROBJECT)


BEGIN_COM_MAP(CFolderObject)
	COM_INTERFACE_ENTRY(IFolderObject)
	COM_INTERFACE_ENTRY(IDispatch)
	COM_INTERFACE_ENTRY(ISupportErrorInfo)
END_COM_MAP()

// ISupportsErrorInfo
	STDMETHOD(InterfaceSupportsErrorInfo)(REFIID riid);


	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
		return S_OK;
	}

	void FinalRelease()
	{
	}

public:
     STDMETHOD(get_Name) (BSTR *pVal);
    STDMETHOD(put_Name) (BSTR newVal);
    STDMETHOD(get_Id) (LONG *pVal);
    STDMETHOD(put_Id) (LONG newVal);
    STDMETHOD(get_FolderPath) (BSTR *pVal);
    STDMETHOD(put_FolderPath) (BSTR newVal);
    STDMETHOD(get_ContainerClass) (BSTR *pVal);
    STDMETHOD(put_ContainerClass) (BSTR newVal);

    STDMETHOD(put_FolderID) (VARIANT id);
    STDMETHOD(get_FolderID) (VARIANT * id);
    STDMETHOD(get_ItemCount) (LONG *pVal);
    STDMETHOD(put_ItemCount) (LONG newVal);


};

OBJECT_ENTRY_AUTO(__uuidof(FolderObject), CFolderObject)
