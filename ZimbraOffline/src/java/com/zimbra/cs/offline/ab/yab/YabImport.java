/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
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
package com.zimbra.cs.offline.ab.yab;

import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.offline.util.yab.Session;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.mailbox.SyncExceptionHandler;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.common.service.ServiceException;

import java.util.List;

public class YabImport implements DataSource.DataImport {
    private final OfflineDataSource ds;
    private final SyncSession session;

    private static final String ERROR = "Yahoo address book synchronization failed";
    
    public YabImport(DataSource ds) throws ServiceException {
        this.ds = (OfflineDataSource) ds;
        session = newSyncSession();
    }
    
    public void test() throws ServiceException {}

    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
        ds.getMailbox().beginTrackingSync();
        try {
            session.sync();
        } catch (Exception e) {
            SyncExceptionHandler.checkRecoverableException(ERROR, e);
            ds.reportError(Mailbox.ID_FOLDER_CONTACTS, ERROR + " - contact synchronization disabled.", e);
            ds.setContactSyncEnabled(false);
        }
    }

    private SyncSession newSyncSession() throws ServiceException {
        Session session = new Session(OfflineYAuth.newAuthenticator(ds));
        session.setTrace(ds.isDebugTraceEnabled());
        return new SyncSession(ds, session);
    }
}
