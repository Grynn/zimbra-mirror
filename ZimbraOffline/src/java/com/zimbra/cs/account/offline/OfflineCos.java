/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.account.offline;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Cos;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning.EntryType;
import com.zimbra.cs.db.DbOfflineDirectory;
import com.zimbra.cs.offline.OfflineLC;

class OfflineCos extends Cos {
    OfflineCos(String name, String id, Map<String, Object> attrs, Provisioning prov) {
        super(name, id, attrs, prov);
    }

    static OfflineCos instantiate(Provisioning prov) {
        try {
            Map<String, Object> attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.COS, OfflineProvisioning.A_offlineDn, "default");
            if (attrs == null) {
                attrs = new HashMap<String, Object>(3);
                attrs.put(Provisioning.A_cn, "default");
                attrs.put(Provisioning.A_objectClass, "zimbraCOS");
                attrs.put(Provisioning.A_zimbraId, UUID.randomUUID().toString());
                DbOfflineDirectory.createDirectoryEntry(EntryType.COS, "default", attrs, false);
            }
            
            //make sure auth token doesn't expire too soon
            attrs.put(Provisioning.A_zimbraAuthTokenLifetime, OfflineLC.auth_token_lifetime.value());
            attrs.put(Provisioning.A_zimbraAdminAuthTokenLifetime, OfflineLC.auth_token_lifetime.value());
            
            //allow proxy to any domains
            attrs.put(Provisioning.A_zimbraProxyAllowedDomains, "*");
            
            return new OfflineCos("default", (String) attrs.get(Provisioning.A_zimbraId), attrs, prov);
        } catch (ServiceException e) {
            // throw RuntimeException because we're being called at startup...
            throw new RuntimeException("failure instantiating default cos", e);
        }
    }
}
