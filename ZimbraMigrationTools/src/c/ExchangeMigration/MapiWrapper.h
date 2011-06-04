// MapiWrapper.h : Declaration of the CMapiWrapper

#pragma once
#include "resource.h"       // main symbols



#include "ExchangeMigration_i.h"


#include "MapiMigration.h"



#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Single-threaded COM objects are not properly supported on Windows CE platform, such as the Windows Mobile platforms that do not include full DCOM support. Define _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA to force ATL to support creating single-thread COM object's and allow use of it's single-threaded COM object implementations. The threading model in your rgs file was set to 'Free' as that is the only threading model supported in non DCOM Windows CE platforms."
#endif

using namespace ATL;


// CMapiWrapper

class ATL_NO_VTABLE CMapiWrapper :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CMapiWrapper, &CLSID_MapiWrapper>,
	public ISupportErrorInfo,
	public IDispatchImpl<IMapiWrapper, &IID_IMapiWrapper, &LIBID_ExchangeMigrationLib, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
	CMapiWrapper()
	{
		baseMigrationObj = new MapiMigration();

	
	}

DECLARE_REGISTRY_RESOURCEID(IDR_MAPIWRAPPER)


BEGIN_COM_MAP(CMapiWrapper)
	COM_INTERFACE_ENTRY(IMapiWrapper)
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


	CMigration *baseMigrationObj;
	STDMETHOD(ConnectToServer)(BSTR ServerHostName, BSTR Port, BSTR AdminID);
	STDMETHOD(ConnecttoXchgServer)(BSTR HostName, BSTR ProfileName, BSTR Password);
	STDMETHOD(ImportMailOptions)(BSTR OptionsTag);
};

OBJECT_ENTRY_AUTO(__uuidof(MapiWrapper), CMapiWrapper)
