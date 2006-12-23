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

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Cos;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning.EntryType;
import com.zimbra.cs.db.DbOfflineDirectory;
import com.zimbra.cs.offline.OfflineLog;

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
            return new OfflineCos("default", (String) attrs.get(Provisioning.A_zimbraId), attrs);
        } catch (ServiceException e) {
            // throw RuntimeException because we're being called at startup...
            throw new RuntimeException("failure instantiating default cos", e);
        }
    }

    @Override
    public String getAttr(String name, boolean applyDefaults) {
        OfflineLog.offline.debug("fetching cos attr: " + name);
        return super.getAttr(name, applyDefaults);
    }
}
