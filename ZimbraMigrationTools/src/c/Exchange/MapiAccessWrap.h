// MapiAccessWrap.h : Declaration of the CMapiAccessWrap

#pragma once
#include "resource.h"       // main symbols

#include "OaIdl.h"
#include "Wtypes.h"


#include "Exchange_i.h"
#include "MAPIDefs.h"
#include "Exchange.h"
#include "ExchangeAdmin.h"

#include "..\Exchange\MAPIAccessAPI.h"


/*
#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Single-threaded COM objects are not properly supported on Windows CE platform, such as the Windows Mobile platforms that do not include full DCOM support. Define _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA to force ATL to support creating single-thread COM object's and allow use of it's single-threaded COM object implementations. The threading model in your rgs file was set to 'Free' as that is the only threading model supported in non DCOM Windows CE platforms."
#endif

using namespace ATL;
*/

// CMapiAccessWrap

class ATL_NO_VTABLE CMapiAccessWrap :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CMapiAccessWrap, &CLSID_MapiAccessWrap>,
	public ISupportErrorInfo,
	public IDispatchImpl<IMapiAccessWrap, &IID_IMapiAccessWrap, &LIBID_Exchange, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
	CMapiAccessWrap()
	{
	}

DECLARE_REGISTRY_RESOURCEID(IDR_MAPIACCESSWRAP)


BEGIN_COM_MAP(CMapiAccessWrap)
	COM_INTERFACE_ENTRY(IMapiAccessWrap)
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

	Zimbra::MAPI::MAPIAccessAPI *maapi;
	STDMETHOD(Initializeuser)(BSTR UserName, BSTR* StatusMsg);
	STDMETHOD(GetFolderList)(VARIANT* folders);
	STDMETHOD(GetItemsList)(IfolderObject* FolderObj, VARIANT creattiondate,VARIANT* vItems);
	STDMETHOD(GetData) (BSTR UserId, VARIANT ItemId, FolderType type, VARIANT * pVal);
        STDMETHOD(UnInitializeuser)();


};

OBJECT_ENTRY_AUTO(__uuidof(MapiAccessWrap), CMapiAccessWrap)
