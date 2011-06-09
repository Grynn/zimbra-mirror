/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.offline.ab.yc;

import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.mailbox.YContactSync;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.ab.LocalData;


public class YContactImport implements DataSource.DataImport {

    private final OfflineDataSource ds;
    private YContactSync session;
    
    public YContactImport(OfflineDataSource ds) {
        this.ds = ds;
    }
    @Override
    public void test() throws ServiceException {
    }

    @Override
    public void importData(List<Integer> folderIds, boolean fullSync) throws ServiceException {
        if (!fullSync && !new LocalData(ds).hasLocalChanges()) {
            return;
        }
        OfflineLog.yab.info("Importing Yahoo contacts for account '%s'", ds.getName());
        if (session == null) {
            session = new YContactSync(ds);
        }
        try {
            session.sync();
        } catch (Exception e) {
            e.printStackTrace();
            OfflineLog.yab.info("Failed to import Yahoo contacts for account '%s'", ds.getName());
            return;
        }
        OfflineLog.yab.info("Finished importing Yahoo contacts for account '%s'", ds.getName());
    }
}
