/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
package com.zimbra.cs.offline;

import com.zimbra.cs.datasource.ImapSync;
import com.zimbra.cs.account.offline.OfflineDataSource;
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
    
    public YMailImport(OfflineDataSource ds) throws ServiceException {
        super(ds, new XYMEAuthenticator(OfflineYAuth.authenticate(ds),
                                        OfflineConstants.YMAIL_PARTNER_NAME));
        yabImport = ds.isContactSyncEnabled() ? new YabImport(ds) : null;
        calDavImport = ds.isCalendarSyncEnabled() ?
            new OfflineCalDavDataImport(ds) : null;
    }

    @Override
    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
        String dsName = dataSource.getName();
        if (yabImport != null) {
            LOG.info("Importing contacts for YMail account '%s'", dsName);
            yabImport.importData(folderIds, fullSync);
            LOG.info("Finished importing contacts for YMail account '%s'", dsName);
        }
        
        super.importData(folderIds, fullSync);
        
        if (calDavImport != null) {
            LOG.info("Importing calendar for YMail account '%s'", dsName);
            calDavImport.importData(null, fullSync);
            LOG.info("Finished importing calendar for YMail account '%s'", dsName);
        }
    }
}
