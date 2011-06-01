// ExchangeMigObject.h : Declaration of the CExchangeMigObject

#pragma once
#include "resource.h"       // main symbols



#include "MapiExchange_i.h"

#include"MapiMigration.h"


#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Single-threaded COM objects are not properly supported on Windows CE platform, such as the Windows Mobile platforms that do not include full DCOM support. Define _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA to force ATL to support creating single-thread COM object's and allow use of it's single-threaded COM object implementations. The threading model in your rgs file was set to 'Free' as that is the only threading model supported in non DCOM Windows CE platforms."
#endif

using namespace ATL;


// CExchangeMigObject

class ATL_NO_VTABLE CExchangeMigObject :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CExchangeMigObject, &CLSID_ExchangeMigObject>,
	public ISupportErrorInfo,
	public IDispatchImpl<IExchangeMigObject, &IID_IExchangeMigObject, &LIBID_MapiExchangeLib, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
	CExchangeMigObject()
	{
		
	}

DECLARE_REGISTRY_RESOURCEID(IDR_EXCHANGEMIGOBJECT)


BEGIN_COM_MAP(CExchangeMigObject)
	COM_INTERFACE_ENTRY(IExchangeMigObject)
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



	STDMETHOD(InitializeMigration)(BSTR ConfigXMLFileName, BSTR UserMapFileName);
	STDMETHOD(ConnectToserver)(void);
	STDMETHOD(ImportMailoptions)(void);

	CMigration *MigrationObj;
};

OBJECT_ENTRY_AUTO(__uuidof(ExchangeMigObject), CExchangeMigObject)
