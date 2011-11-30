#include <Iads.h>
#include <adshlp.h>
#include <AdsErr.h>
#include "commonMAPI.h"
#include "edkmdb.h"
#include "GenericException.h"
#include "MAPISession.h"
#include "MAPIStore.h"
#include "MAPITableIterator.h"
#include "MAPIMessage.h"
#include "MAPIFolder.h"
#include "MapiUtils.h"
#include "MAPIContacts.h"

#include "resource.h"
#include "Zimbra/Zimbra.h"
#include "Exchange_i.h"

#include <objsel.h>
#include <cmnquery.h>
#include <dsquery.h>
#include <Shlobj.h>
#include <shlguid.h>
#include <dsclient.h>
#include <adshlp.h>
#include <IADS.h>
#include <sddl.h>

using namespace Zimbra::Util;
using namespace Zimbra::MAPI;

class CExchangeModule: public CAtlDllModuleT<CExchangeModule>
{
public:
    DECLARE_LIBID(LIBID_Exchange) DECLARE_REGISTRY_APPID_RESOURCEID(IDR_EXCHANGE,
        "{0A8DF15B-275B-408A-84EA-8AEF784C81C3}")
};

extern class CExchangeModule _AtlModule;
