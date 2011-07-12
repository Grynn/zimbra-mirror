// MapiWrapper.h : Declaration of the CMapiWrapper

#pragma once
#include "resource.h"
#include "Exchange_i.h"
#include "MapiMigration.h"
#include "ExchangeUtils.h"

class ATL_NO_VTABLE CMapiWrapper:
    public CComObjectRootEx<CComSingleThreadModel>,
    public CComCoClass<CMapiWrapper, &CLSID_MapiWrapper>,
    public ISupportErrorInfo,
    public IDispatchImpl<IMapiWrapper, &IID_IMapiWrapper, &LIBID_Exchange,
    /*wMajor =*/ 1, /*wMinor =*/ 0> {
public:
    CMapiWrapper() {
	baseMigrationObj = new MapiMigration();
	exchadmin= new Zimbra::ExchangeUtils::ExchangeAdmin("10.117.82.161");
    }

    DECLARE_REGISTRY_RESOURCEID(IDR_MAPIWRAPPER)

    BEGIN_COM_MAP(CMapiWrapper)
	COM_INTERFACE_ENTRY(IMapiWrapper)
	COM_INTERFACE_ENTRY(IDispatch)
	COM_INTERFACE_ENTRY(ISupportErrorInfo)
    END_COM_MAP()

    STDMETHOD(InterfaceSupportsErrorInfo)(REFIID riid);

    DECLARE_PROTECT_FINAL_CONSTRUCT()

    HRESULT FinalConstruct() { return S_OK; }

    void FinalRelease() {}

    CMigration *baseMigrationObj;
	Zimbra::ExchangeUtils::ExchangeAdmin *exchadmin;

    STDMETHOD(ConnectToServer)(BSTR ServerHostName, BSTR Port, BSTR AdminID);
    STDMETHOD(ConnecttoXchgServer)(BSTR HostName, BSTR ProfileName, BSTR Password);
    STDMETHOD(ImportMailOptions)(BSTR OptionsTag);
	STDMETHOD(GetProfilelist)(VARIANT* Profiles);
	std::vector<CComBSTR>m_vecColors;

	std::wstring str_to_wstr( const std::string& str );


};

OBJECT_ENTRY_AUTO(__uuidof(MapiWrapper), CMapiWrapper)
