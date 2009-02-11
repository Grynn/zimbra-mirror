package com.zimbra.cs.offline;

import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.DataSource.DataImport;
import com.zimbra.common.service.ServiceException;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

public class OfflineImport implements DataImport {
    private final OfflineDataSource ds;
    private final DataImport di;
    private final long interval;

    public static final int IMAP_INTERVAL =
        OfflineLC.zdesktop_imap_fullsync_interval.intValue();

    public static final int CONTACTS_INTERVAL =
        OfflineLC.zdesktop_contacts_fullsync_interval.intValue();

    public static final int CALENDAR_INTERVAL =
        OfflineLC.zdesktop_calendar_fullsync_interval.intValue();

    private static final Map<String, Long> lastFullSyncTime =
         new LinkedHashMap<String, Long>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry e) {
                return size() > 256;
            }
        };

    public OfflineImport(OfflineDataSource ds, DataImport di, int intervalMins) {
        this.ds = ds;
        this.di = di;
        interval = intervalMins * 60000;
    }

    public void test() throws ServiceException {
        di.test();
    }

    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
        di.importData(folderIds, checkSyncInterval(fullSync));
    }

    // Force full sync if more than interval ms have elapsed since the last
    // full sync.
    private boolean checkSyncInterval(boolean fullSync) {
        long currentTime = System.currentTimeMillis();
        synchronized (lastFullSyncTime) {
            Long time = lastFullSyncTime.get(ds.getId());
            if (time != null && currentTime - time > interval) {
                OfflineLog.offline.debug(
                    "Forcing full sync of data source %s since more than %d minutes have elapsed since last full sync",
                    ds.getName(), interval / 60000);
                fullSync = true;
            }
            if (time == null || fullSync) {
                // Update last full sync time
                lastFullSyncTime.put(ds.getId(), currentTime);
            }
        }
        return fullSync;
    }
}
