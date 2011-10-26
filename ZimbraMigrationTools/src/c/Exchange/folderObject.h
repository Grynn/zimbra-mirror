// folderObject.h : Declaration of the CfolderObject

#pragma once
#include "resource.h"                           // main symbols

#include "Exchange_i.h"
#include "BaseFolder.h"
#include "MAPIDefs.h"

/*
 *
 * #if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
 * #error "Single-threaded COM objects are not properly supported on Windows CE platform, such as the Windows Mobile platforms that do not include full DCOM support. Define _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA to force ATL to support creating single-thread COM object's and allow use of it's single-threaded COM object implementations. The threading model in your rgs file was set to 'Free' as that is the only threading model supported in non DCOM Windows CE platforms."
 * #endif
 *
 * using namespace ATL;
 *
 */
// CfolderObject

class ATL_NO_VTABLE CfolderObject: public CComObjectRootEx<CComSingleThreadModel>,
    public CComCoClass<CfolderObject,
    &CLSID_folderObject>, public BaseFolder,
    public IDispatchImpl<IfolderObject, &IID_IfolderObject, &LIBID_Exchange, /*wMajor =*/ 1,
    /*wMinor =*/ 0>
{
private:
    /* BSTR Strname;
     * LONG LngID;
     * BSTR parentPath;*/
    SBinary FolderId;
	LONG Itemcnt;

public:
    CfolderObject()
    {}
    DECLARE_REGISTRY_RESOURCEID(IDR_FOLDEROBJECT)

    BEGIN_COM_MAP(CfolderObject)
    COM_INTERFACE_ENTRY(IfolderObject)
    COM_INTERFACE_ENTRY(IDispatch)
    END_COM_MAP()

    STDMETHOD(InterfaceSupportsErrorInfo) (REFIID riid);

    DECLARE_PROTECT_FINAL_CONSTRUCT()

    HRESULT FinalConstruct()
    {
        return S_OK;
    }
    void FinalRelease()
    {}
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

OBJECT_ENTRY_AUTO(__uuidof(folderObject), CfolderObject)
