/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.account.offline;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Cos;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning.EntryType;
import com.zimbra.cs.db.DbOfflineDirectory;

class OfflineCos extends Cos {
    OfflineCos(String name, String id, Map<String, Object> attrs) {
        super(name, id, attrs);
    }

    static OfflineCos instantiate() {
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
            long authTokenTtl = 365 * 24 * 3600;
            try {
            	String lifetime = LC.get("auth_token_lifetime");
            	if (lifetime != null) {
            		long ttl = Long.parseLong(lifetime);
            		if (ttl > 0) {
            			authTokenTtl = ttl;
            		}
            	}
            } catch (NumberFormatException x) {}
            attrs.put(Provisioning.A_zimbraAuthTokenLifetime, Long.toString(authTokenTtl));
            attrs.put(Provisioning.A_zimbraAdminAuthTokenLifetime, Long.toString(authTokenTtl));
            
            return new OfflineCos("default", (String) attrs.get(Provisioning.A_zimbraId), attrs);
        } catch (ServiceException e) {
            // throw RuntimeException because we're being called at startup...
            throw new RuntimeException("failure instantiating default cos", e);
        }
    }
}
