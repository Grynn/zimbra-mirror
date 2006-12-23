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

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning.EntryType;
import com.zimbra.cs.db.DbOfflineDirectory;
import com.zimbra.cs.offline.OfflineLog;

class OfflineConfig extends Config {
    private OfflineConfig(Map<String, Object> attrs) {
        super(attrs);
    }

    static OfflineConfig instantiate() {
        try {
            Map<String, Object> attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.CONFIG, OfflineProvisioning.A_offlineDn, "config");
            if (attrs == null) {
                attrs = new HashMap<String, Object>(3);
                attrs.put(Provisioning.A_cn, "config");
                attrs.put(Provisioning.A_objectClass, "zimbraGlobalConfig");
                attrs.put(Provisioning.A_zimbraInstalledSkin, new String[] { "bare", "froggy", "harvest", "lavender", "rose", "sand", "sky", "steel", "ttt", "vanilla" } );
                DbOfflineDirectory.createDirectoryEntry(EntryType.CONFIG, "config", attrs, false);
            }
            return new OfflineConfig(attrs);
        } catch (ServiceException e) {
            // throw RuntimeException because we're being called at startup...
            throw new RuntimeException("failure instantiating global Config", e);
        }
    }

    @Override
    public String getAttr(String name, boolean applyDefaults) {
        OfflineLog.offline.debug("fetching config attr: " + name);
        return super.getAttr(name, applyDefaults);
    }
}
