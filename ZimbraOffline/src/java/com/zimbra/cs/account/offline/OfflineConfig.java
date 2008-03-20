/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
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
import com.zimbra.cs.offline.OfflineLC;

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
                DbOfflineDirectory.createDirectoryEntry(EntryType.CONFIG, "config", attrs, false);
            }
           	String[] skins = OfflineLC.zdesktop_skins.value().split("\\s*,\\s*");
            attrs.put(Provisioning.A_zimbraInstalledSkin, skins);
            
            attrs.put(Provisioning.A_zimbraRedoLogEnabled, OfflineLC.zdesktop_redolog_enabled.booleanValue() ? "TRUE" : "FALSE");
            
            return new OfflineConfig(attrs);
        } catch (ServiceException e) {
            // throw RuntimeException because we're being called at startup...
            throw new RuntimeException("failure instantiating global Config", e);
        }
    }
}
