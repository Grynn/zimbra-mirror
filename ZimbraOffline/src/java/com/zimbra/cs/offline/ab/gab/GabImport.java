/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.offline.ab.gab;

import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.mailbox.SyncExceptionHandler;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.ab.LocalData;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;

import java.util.List;

public class GabImport implements DataSource.DataImport {
    private final OfflineDataSource ds;
    private SyncSession session;

    private static final Log LOG = OfflineLog.gab;

    private static final String ERROR = "Google address book synchronization failed";

    public GabImport(DataSource ds) {
        this.ds = (OfflineDataSource) ds;
    }

    public void test() throws ServiceException {
        session = new SyncSession(ds);
    }

    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
         // Only sync contacts if full sync or there are local contact changes
        if (!fullSync && !new LocalData(ds).hasLocalChanges()) {
            return;
        }
        LOG.info("Importing contacts for data source '%s'", ds.getName());
        DataSourceManager.getInstance().getMailbox(ds).beginTrackingSync();
        if (session == null) {
            session = new SyncSession(ds);
        }
        try {
            session.sync();
        } catch (Exception e) {
            SyncExceptionHandler.checkRecoverableException(ERROR, e);
            ds.reportError(Mailbox.ID_FOLDER_CONTACTS, ERROR, e);
        }
        LOG.info("Finished importing contacts for data source '%s'", ds.getName());
    }
}
