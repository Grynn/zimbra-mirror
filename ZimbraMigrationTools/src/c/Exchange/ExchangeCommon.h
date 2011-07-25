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

#define MDB_FOLDER_IPM									0x00000001
#define MDB_FOLDER_SEARCH								0x00000002
#define MDB_FOLDER_NORMAL								0x00000004
#define MDB_FOLDER_RULES								0x00000008

#define PR_LONGTERM_ENTRYID_FROM_TABLE					PROP_TAG( PT_BINARY,	0x6670 )
#define PR_FOLDER_FLAGS									PROP_TAG( PT_LONG,		0x66A8 )
#define PR_INTERNET_CPID								PROP_TAG( PT_LONG,		0x3FDE )
#define PR_MESSAGE_CODEPAGE								PROP_TAG( PT_LONG,		0x3FFD )
#define PR_LAST_VERB_EXECUTED							PROP_TAG( PT_LONG,		0x1081 )
#define PR_FLAG_STATUS									PROP_TAG( PT_LONG,		0x1090 )

#define PR_ATTACH_CONTENT_ID							PROP_TAG( PT_TSTRING,	0x3712 )
#define PR_ATTACH_CONTENT_ID_W							PROP_TAG( PT_UNICODE,	0x3712 )
#define PR_ATTACH_CONTENT_ID_A							PROP_TAG( PT_STRING8,	0x3712 )

#define PR_ATTACH_CONTENT_LOCATION						PROP_TAG( PT_TSTRING,	0x3713 )
#define PR_ATTACH_CONTENT_LOCATION_W					PROP_TAG( PT_UNICODE,	0x3713 )
#define PR_ATTACH_CONTENT_LOCATION_A					PROP_TAG( PT_STRING8,	0x3713 )

#define PR_ATTACH_DISPOSITION							PROP_TAG( PT_TSTRING,	0x3716 )
#define PR_ATTACH_DISPOSITION_W							PROP_TAG( PT_UNICODE,	0x3716 )
#define PR_ATTACH_DISPOSITION_A							PROP_TAG( PT_STRING8,	0x3716 )



