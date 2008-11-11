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
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Zimlet;
import com.zimbra.cs.account.offline.OfflineProvisioning.EntryType;
import com.zimbra.cs.db.DbOfflineDirectory;

class OfflineZimlet extends Zimlet {
    OfflineZimlet(String name, String id, Map<String, Object> attrs, Provisioning prov) {
        super(name, id, attrs, prov);
    }

    static Map<String,Zimlet> instantiateAll(Provisioning prov) {
        Map<String,Zimlet> zmap = new HashMap<String,Zimlet>();
        try {
            List<String> ids = DbOfflineDirectory.listAllDirectoryEntries(EntryType.ZIMLET);
            for (String id : ids) {
                Map<String, Object> attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.ZIMLET, Provisioning.A_zimbraId, id);
                if (attrs == null)
                    continue;
                String name = (String) attrs.get(Provisioning.A_cn);
                if (name != null)
                    zmap.put(name.toLowerCase(), new OfflineZimlet(name, id, attrs, prov));
            }
            return zmap;
        } catch (ServiceException e) {
            // throw RuntimeException because we're being called at startup...
            throw new RuntimeException("failure instantiating zimlets", e);
        }
    }
}