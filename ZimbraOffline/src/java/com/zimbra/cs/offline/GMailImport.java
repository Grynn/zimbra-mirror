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
package com.zimbra.cs.offline;

import com.zimbra.cs.offline.ab.gab.GabImport;
import com.zimbra.cs.datasource.ImapSync;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.ZimbraLog;

import java.util.List;

public class GMailImport extends ImapSync {
    private final GabImport gabImport;
    private final OfflineCalDavDataImport calDavImport;

    private static final Log LOG = ZimbraLog.datasource;
    
    public GMailImport(OfflineDataSource ds) throws ServiceException {
        super(ds);
        gabImport = ds.isContactSyncEnabled() ? new GabImport(ds) : null;
        calDavImport = ds.isCalendarSyncEnabled() ?
            new OfflineCalDavDataImport(dataSource) : null;
    }

    @Override
    public void test() throws ServiceException {
        if (gabImport != null) {
            gabImport.test();
        }
        super.test();
    }
    
    @Override
    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
        String dsName = dataSource.getName();
        if (gabImport != null) {
            LOG.info("Importing contacts for GMail account '%s'", dsName);
            gabImport.importData(null, fullSync);
            LOG.info("Finished importing contacts for GMail account '%s'", dsName);
        }
        if (calDavImport != null) {
            LOG.info("Importing calendar for GMail account '%s'", dsName);
            calDavImport.importData("gmail.com", null, fullSync);
            LOG.info("Finished importing calendar for GMail account '%s'", dsName);
        }
        super.importData(folderIds, fullSync);
    }
}

