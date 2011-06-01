// dllmain.h : Declaration of module class.

class CMapiExchangeModule : public ATL::CAtlDllModuleT< CMapiExchangeModule >
{
public :
	DECLARE_LIBID(LIBID_MapiExchangeLib)
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_MAPIEXCHANGE, "{AFAD609A-F1C4-4010-9FED-9B96089ACA0F}")
};

extern class CMapiExchangeModule _AtlModule;
