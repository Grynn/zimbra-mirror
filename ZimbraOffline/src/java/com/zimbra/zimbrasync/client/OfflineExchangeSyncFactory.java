package com.zimbra.zimbrasync.client;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.mailbox.ChangeTrackingMailbox.TracelessContext;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;

public class OfflineExchangeSyncFactory extends ExchangeSyncFactory {

    private static final OperationContext sContext = new TracelessContext();
    
    @Override
    String getUserAgent() {
        return "Zimbra Desktop";
    }
    
    @Override
    protected OperationContext getContext(Mailbox mbox, boolean markChanges) throws ServiceException {
        if (markChanges)
            return new OperationContext(mbox);
        else
            return sContext;
    }
    
    @Override
    protected ChangeTracker getClientChanges(DataSource ds, Map<Integer, ExchangeFolderMapping> folderMappingsByClientId) throws ServiceException {
        return new OfflineChangeTracker(ds, folderMappingsByClientId);
    }
}
