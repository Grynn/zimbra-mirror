// dllmain.h : Declaration of module class.

class CExchangeMigrationModule : public ATL::CAtlDllModuleT< CExchangeMigrationModule >
{
public :
	DECLARE_LIBID(LIBID_ExchangeMigrationLib)
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_EXCHANGEMIGRATION, "{0A8DF15B-275B-408A-84EA-8AEF784C81C3}")
};

extern class CExchangeMigrationModule _AtlModule;
