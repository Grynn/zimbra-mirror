package com.zimbra.cs.offline;

import com.zimbra.cs.offline.OfflineCalDavDataImport;
import com.zimbra.cs.datasource.ImapSync;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.ZimbraLog;

import java.util.List;

public class GMailImport extends ImapSync {
    private OfflineCalDavDataImport calDavImport;

    private static final Log LOG = ZimbraLog.datasource;
    
    public GMailImport(DataSource ds) throws ServiceException {
        super(ds);              
        initCalDavImport();
    }

    @Override
    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
        super.importData(folderIds, fullSync);
        
        if (calDavImport != null) {
            String dsName = dataSource.getName();
            LOG.info("Importing calendar for account '%s'", dsName);
            calDavImport.importData(folderIds, fullSync);
            LOG.info("Finished importing calendar for account '%s'", dsName);
        }
    }

    private void initCalDavImport() throws ServiceException {
        if (dataSource.getBooleanAttr(OfflineProvisioning.A_zimbraDataSourceCalendarSyncEnabled, false)) {
            calDavImport = new OfflineCalDavDataImport(dataSource);
        } else {
            calDavImport = null;
        }        
    }
}

