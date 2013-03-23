/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2011, 2012, 2013 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
#include <Iads.h>
#include <adshlp.h>
#include <AdsErr.h>
#include "commonMAPI.h"
#include "edkmdb.h"
#include "GenericException.h"

#include "resource.h"
#include "Zimbra/Zimbra.h"
#include "Exchange_i.h"

#include "MAPISession.h"
#include "MAPIStore.h"
#include "MAPITableIterator.h"
#include "MAPIMessage.h"
#include "MAPIFolder.h"
#include "MapiUtils.h"
#include "MAPIContacts.h"
#include "MAPIRfc2445.h"
#include "MAPIAppointment.h"
#include "MAPITask.h"

#include <objsel.h>
#include <cmnquery.h>
#include <dsquery.h>
#include <Shlobj.h>
#include <shlguid.h>
#include <dsclient.h>
#include <adshlp.h>
#include <IADS.h>
#include <sddl.h>

#include "Zimbra/mso.tlh"
#include "Zimbra/msoutl.tlh"

using namespace Zimbra::Util;
using namespace Zimbra::MAPI;

class CExchangeModule: public CAtlDllModuleT<CExchangeModule>
{
public:
    DECLARE_LIBID(LIBID_Exchange) DECLARE_REGISTRY_APPID_RESOURCEID(IDR_EXCHANGE,
        "{0A8DF15B-275B-408A-84EA-8AEF784C81C3}")
};

extern class CExchangeModule _AtlModule;

/* Flags for MAPIINIT_0 structure ulFlags value passed to MAPIInitialize()  */
#define MAPI_NO_COINIT			0x00000008