// MapiAccessWrap.h : Declaration of the CMapiAccessWrap

#pragma once
#include "resource.h"
#include "OaIdl.h"
#include "Wtypes.h"
#include "Exchange_i.h"
#include "MAPIDefs.h"
#include "Exchange.h"
#include "ExchangeAdmin.h"
#include "MAPIAccessAPI.h"

class ATL_NO_VTABLE CMapiAccessWrap: public CComObjectRootEx<CComSingleThreadModel>, public
    CComCoClass<CMapiAccessWrap, &CLSID_MapiAccessWrap>, public ISupportErrorInfo, public
    IDispatchImpl<IMapiAccessWrap, &IID_IMapiAccessWrap, &LIBID_Exchange,
    /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
    CMapiAccessWrap() {}

    DECLARE_REGISTRY_RESOURCEID(IDR_MAPIACCESSWRAP) BEGIN_COM_MAP(
        CMapiAccessWrap) COM_INTERFACE_ENTRY(IMapiAccessWrap) COM_INTERFACE_ENTRY(
        IDispatch) COM_INTERFACE_ENTRY(ISupportErrorInfo) END_COM_MAP() STDMETHOD(
        InterfaceSupportsErrorInfo) (REFIID riid);

    DECLARE_PROTECT_FINAL_CONSTRUCT() HRESULT FinalConstruct() { return S_OK; }
    void FinalRelease() {}

public:
    Zimbra::MAPI::MAPIAccessAPI *maapi;

    STDMETHOD(UserInit) (BSTR userName, BSTR *statusMsg);
    STDMETHOD(GetFolderList) (VARIANT * folders);
    STDMETHOD(GetItemsList) (IfolderObject * folderObj, VARIANT creationDate, VARIANT * vItems);
    STDMETHOD(GetData) (BSTR userId, VARIANT itemId, FolderType type, VARIANT * pVal);
    STDMETHOD(UserUninit) ();
};

OBJECT_ENTRY_AUTO(__uuidof(MapiAccessWrap), CMapiAccessWrap)
