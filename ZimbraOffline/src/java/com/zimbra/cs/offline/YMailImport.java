package com.zimbra.cs.offline;

import com.zimbra.cs.datasource.ImapSync;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineProvisioning;
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

    private static final Log LOG = ZimbraLog.datasource;
    
    public YMailImport(DataSource ds) throws ServiceException {
        super(ds, new XYMEAuthenticator(OfflineYAuth.authenticate(ds),
                                        OfflineConstants.YMAIL_PARTNER_NAME));
        yabImport = isContactSyncEnabled() ? new YabImport(ds) : null;
    }

    @Override
    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
        if (yabImport != null) {
            String acctId = getDataSource().getAccountId();
            LOG.info("Importing YAB contacts for account '%s'", acctId);
            yabImport.importData(folderIds, fullSync);
            LOG.info("Finished importing YAB contact");
        }
        super.importData(folderIds, fullSync);
    }

    private boolean isContactSyncEnabled() {
        return dataSource.getBooleanAttr(
            OfflineProvisioning.A_zimbraDataSourceContactSyncEnabled, false);
    }
}
