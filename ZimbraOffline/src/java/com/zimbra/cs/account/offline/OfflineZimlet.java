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
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Zimlet;
import com.zimbra.cs.account.offline.OfflineProvisioning.EntryType;
import com.zimbra.cs.db.DbOfflineDirectory;
import com.zimbra.cs.object.ObjectType;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.zimlet.ZimletHandler;
import com.zimbra.cs.zimlet.ZimletUtil;

class OfflineZimlet extends Zimlet implements ObjectType {
    OfflineZimlet(String name, String id, Map<String, Object> attrs) {
        super(name, id, attrs);
    }

    static Map<String,Zimlet> instantiateAll() {
        Map<String,Zimlet> zmap = new HashMap<String,Zimlet>();
        try {
            List<String> ids = DbOfflineDirectory.listAllDirectoryEntries(EntryType.ZIMLET);
            for (String id : ids) {
                Map<String, Object> attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.ZIMLET, Provisioning.A_zimbraId, id);
                if (attrs == null)
                    continue;
                String name = (String) attrs.get(Provisioning.A_cn);
                if (name != null)
                    zmap.put(name.toLowerCase(), new OfflineZimlet(name, id, attrs));
            }
            return zmap;
        } catch (ServiceException e) {
            // throw RuntimeException because we're being called at startup...
            throw new RuntimeException("failure instantiating zimlets", e);
        }
    }

    @Override
    public String getAttr(String name, boolean applyDefaults) {
        OfflineLog.offline.debug("fetching zimlet attr: " + name);
        return super.getAttr(name, applyDefaults);
    }

    public String getType()              { return getAttr(Provisioning.A_cn); }
    public String getDescription()       { return getAttr(Provisioning.A_zimbraZimletDescription); }
    public boolean isIndexingEnabled()   { return getBooleanAttr(Provisioning.A_zimbraZimletIndexingEnabled, false); }
    public String getHandlerClassName()  { return getAttr(Provisioning.A_zimbraZimletHandlerClass); }
    public ZimletHandler getHandler()    { return ZimletUtil.getHandler(getName()); }
    public String getHandlerConfig()     { return getAttr(Provisioning.A_zimbraZimletHandlerConfig); }
    public String getServerIndexRegex()  { return getAttr(Provisioning.A_zimbraZimletServerIndexRegex); }
}