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
import java.util.UUID;

import com.zimbra.common.localconfig.LC;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.offline.OfflineLC;

class OfflineLocalServer extends Server {
    private OfflineLocalServer(OfflineConfig oconfig, Map<String, Object> attrs) {
        super((String) attrs.get(Provisioning.A_cn), (String) attrs.get(Provisioning.A_zimbraId), attrs, oconfig.getServerDefaults());
    }

    static OfflineLocalServer instantiate(OfflineConfig oconfig) {
        Map<String, Object> attrs = new HashMap<String, Object>(12);
        attrs.put(Provisioning.A_objectClass, "zimbraServer");
        attrs.put(Provisioning.A_cn, "localhost");
        attrs.put(Provisioning.A_zimbraServiceHostname, "localhost");
        attrs.put(Provisioning.A_zimbraSmtpHostname, "localhost");
        attrs.put(Provisioning.A_zimbraId, UUID.randomUUID().toString());
        attrs.put("zimbraServiceEnabled", "mailbox");
        attrs.put("zimbraServiceInstalled", "mailbox");
        attrs.put(Provisioning.A_zimbraMailPort, "7633");
        attrs.put(Provisioning.A_zimbraAdminPort, LC.zimbra_admin_service_port.value());
        attrs.put(Provisioning.A_zimbraMailMode, "http");
        attrs.put(Provisioning.A_zimbraLmtpNumThreads, "1");
        attrs.put(Provisioning.A_zimbraLmtpBindPort, "7635");
        attrs.put(Provisioning.A_zimbraFileUploadMaxSize, OfflineLC.zdesktop_upload_size_limit.value());
        return new OfflineLocalServer(oconfig, attrs);
    }
}
