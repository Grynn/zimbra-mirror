#include "resource.h"
#include "Exchange_i.h"

class CExchangeModule: public CAtlDllModuleT<CExchangeModule> {
public :
	DECLARE_LIBID(LIBID_Exchange)
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_EXCHANGE, "{0A8DF15B-275B-408A-84EA-8AEF784C81C3}")
};

extern class CExchangeModule _AtlModule;
