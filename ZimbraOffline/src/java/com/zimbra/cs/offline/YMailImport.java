package com.zimbra.cs.offline;

import com.zimbra.cs.datasource.ImapSync;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.OfflineCalDavDataImport;
import com.zimbra.cs.offline.yab.YabImport;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.util.yauth.XYMEAuthenticator;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.ZimbraLog;

import java.util.List;

public class YMailImport extends ImapSync {
    private final YabImport yabImport;
    private final OfflineCalDavDataImport calDavImport;
    
    private static final Log LOG = ZimbraLog.datasource;
    
    public YMailImport(DataSource ds) throws ServiceException {
        super(ds, new XYMEAuthenticator(OfflineYAuth.authenticate(ds),
                                        OfflineConstants.YMAIL_PARTNER_NAME));
        yabImport = isContactSyncEnabled() ? new YabImport(ds) : null;
        calDavImport = isCalendarSyncEnabled() ? new OfflineCalDavDataImport(ds) : null;
    }

    @Override
    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
        String dsName = dataSource.getName();
        
        if (yabImport != null) {
            LOG.info("Importing YAB contacts for account '%s'", dsName);
            yabImport.importData(folderIds, fullSync);
            LOG.info("Finished importing YAB contact for account '%s'", dsName);
        }
        
        super.importData(folderIds, fullSync);
        
        if (calDavImport != null) {
            LOG.info("Importing calendar for account '%s'", dsName);
            calDavImport.importData(null, fullSync);
            LOG.info("Finished importing calendar for account '%s'", dsName);
        }
    }

    private boolean isContactSyncEnabled() {
        return dataSource.getBooleanAttr(
            OfflineProvisioning.A_zimbraDataSourceContactSyncEnabled, false);
    }
    
    private boolean isCalendarSyncEnabled() {
        return dataSource.getBooleanAttr(
            OfflineProvisioning.A_zimbraDataSourceCalendarSyncEnabled, false);       
    }
}
