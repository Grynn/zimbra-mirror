#include <Iads.h>
#include <adshlp.h>
#include <AdsErr.h>
#include "commonMAPI.h"
#include "edkmdb.h"
#include "GenericException.h"
#include "MapiObjects.h"
#include "MapiUtils.h"
#include "resource.h"
#include "Zimbra/Zimbra.h"
#include "Exchange_i.h"

using namespace Zimbra::MAPI;

class CExchangeModule: public CAtlDllModuleT<CExchangeModule>{
public:
    DECLARE_LIBID(LIBID_Exchange)
    DECLARE_REGISTRY_APPID_RESOURCEID(IDR_EXCHANGE, "{0A8DF15B-275B-408A-84EA-8AEF784C81C3}")
};

extern class CExchangeModule _AtlModule;
