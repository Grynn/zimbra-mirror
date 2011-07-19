#pragma once

#include "Common.h"
#include "Zimbra/Zimbra.h"
#include "Zimbra/Rpc.h"

#include "..\Util\ZimbraUtil.h"
#include "..\Util\ZimbraLogging.h"

#include<CGuid.h>
#include <atlbase.h>

#include<string>
#include<vector>

#include <MAPIX.h>

#include <Iads.h>
#include <adshlp.h>
#include <AdsErr.h>

#include "GenericException.h"

// use this flag on OpenMsgStore to force cached mode connections
// to read remote data and not local data
#define MDB_ONLINE ((ULONG) 0x00000100)
#define GLOBAL_PROFILE_SECTION_GUID      "\x13\xDB\xB0\xC8\xAA\x05\x10\x1A\x9B\xB0\x00\xAA\x00\x2F\xC4\x5A"

