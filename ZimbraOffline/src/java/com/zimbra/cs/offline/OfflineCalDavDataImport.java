package com.zimbra.cs.offline;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.datasource.CalDavDataImport;

public class OfflineCalDavDataImport extends CalDavDataImport {
    private static final String CALDAV_TARGET_URL = "calDavTargetUrl";
    private static final String CALDAV_PRINCIPAL_PATH = "calDavPrincipalPath";
    
    public OfflineCalDavDataImport(DataSource ds) throws ServiceException {
        super(ds);
    }
    
    @Override
    protected String getTargetUrl() {
        OfflineDataSource.KnownService ks = ((OfflineDataSource)dataSource).getKnownService();
        return (ks != null && ks.attrs != null) ? ks.attrs.get(CALDAV_TARGET_URL) : null;
    }
    
    @Override
    protected String getPrincipalUrl() {
        OfflineDataSource.KnownService ks = ((OfflineDataSource)dataSource).getKnownService();        
        if (ks == null || ks.attrs == null)
            return null;               
        
        String path = ks.attrs.get(CALDAV_PRINCIPAL_PATH);
        if (path == null)
            return null;
        
        return path.replaceAll("@USERNAME@", dataSource.getUsername());
    }
}
