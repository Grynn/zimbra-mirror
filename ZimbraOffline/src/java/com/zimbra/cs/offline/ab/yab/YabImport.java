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
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.mailbox.SyncExceptionHandler;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.IOException;

public class YabImport implements DataSource.DataImport {
    private final OfflineDataSource ds;
    private SyncSession session;

    private static final Log LOG = OfflineLog.yab;
    
    private static final long SYNC_INTERVAL =
        OfflineLC.zdesktop_yab_sync_interval.intValue() * 60 * 1000;
    
    private static final String ERROR = "Yahoo address book synchronization failed";

    private static final Map<String, Long> lastSyncTime =
        new LinkedHashMap<String, Long>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry e) {
                return size() > 256;
            }
        };

    public YabImport(DataSource ds) {
        this.ds = (OfflineDataSource) ds;
    }
    
    public void test() throws ServiceException {
        try {
            OfflineYAuth.newAuthenticator(ds).authenticate();
        } catch (IOException e) {
            throw ServiceException.FAILURE(
                "Authentication failed due to I/O error", e);
        }
    }

    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
        if (skipSync(fullSync)) return;
        LOG.info("Importing contacts for account '%s'", ds.getName());
        ds.getMailbox().beginTrackingSync();
        if (session == null) {
            session = newSyncSession();
        }
        try {
            session.sync();
        } catch (Exception e) {
            SyncExceptionHandler.checkRecoverableException(ERROR, e);
            ds.reportError(Mailbox.ID_FOLDER_CONTACTS, ERROR, e);
        }
        LOG.info("Finished importing contacts for account '%s'", ds.getName());
    }

    private boolean skipSync(boolean fullSync) {
        long currentTime = System.currentTimeMillis();
        synchronized (lastSyncTime) {
            if (!fullSync) {
                Long time = lastSyncTime.get(ds.getId());
                if (time != null && currentTime - time < SYNC_INTERVAL) {
                    return true;
                }
            }
            lastSyncTime.put(ds.getId(), currentTime);
            return false;
        }
    }
    
    private SyncSession newSyncSession() throws ServiceException {
        Session session = new Session(OfflineYAuth.newAuthenticator(ds));
        session.setTrace(ds.isDebugTraceEnabled());
        return new SyncSession(ds, session);
    }
}
