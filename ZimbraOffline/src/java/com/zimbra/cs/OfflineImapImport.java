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
package com.zimbra.cs;

import com.zimbra.cs.datasource.ImapSync;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.common.service.ServiceException;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class OfflineImapImport extends ImapSync {
    private static final int SYNC_INTERVAL =
        OfflineLC.zdesktop_imap_fullsync_interval.intValue() * 60000;

    private static final Map<String, Long> lastFullSyncTime =
         new LinkedHashMap<String, Long>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry e) {
                return size() > 256;
            }
        };

    public OfflineImapImport(DataSource ds) throws ServiceException {
        super(ds);
    }

    @Override
    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
        super.importData(folderIds, checkSyncInterval(fullSync));
    }

    // Force full sync if more than SYNC_INTERVAL minutes have elapsed since
    // the last full sync.
    private boolean checkSyncInterval(boolean fullSync) {
        OfflineLog.offline.debug("TEST");
        long currentTime = System.currentTimeMillis();
        synchronized (lastFullSyncTime) {
            Long time = lastFullSyncTime.get(dataSource.getId());
            if (time != null && currentTime - time > SYNC_INTERVAL) {
                OfflineLog.offline.debug(
                    "Forcing full IMAP sync of account %s since more than %d minutes have elapsed since last full sync",
                    dataSource.getEmailAddress(), SYNC_INTERVAL / 60000);
                fullSync = true;
            }
            if (time == null || fullSync) {
                // Update last full sync time
                lastFullSyncTime.put(dataSource.getId(), currentTime);
            }
        }
        return fullSync;
    }
}
