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
package com.zimbra.cs.offline.gab;

import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.mailbox.SyncExceptionHandler;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.common.service.ServiceException;

import java.util.List;
import java.io.IOException;

public class GabImport implements DataSource.DataImport {
    private final OfflineDataSource ds;
    private final SyncSession session;

    private static final String ERROR = "Google address book synchronization failed";

    public GabImport(DataSource ds) throws ServiceException {
        this.ds = (OfflineDataSource) ds;
        session = new SyncSession(ds);
    }

    public String test() throws ServiceException {
        return null;
    }

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
}
