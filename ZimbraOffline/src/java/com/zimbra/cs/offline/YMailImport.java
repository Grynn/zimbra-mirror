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
import com.zimbra.cs.offline.ab.yab.YabImport;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.util.yauth.XYMEAuthenticator;
import com.zimbra.cs.util.yauth.Authenticator;
import com.zimbra.cs.OfflineImapImport;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.ZimbraLog;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.io.IOException;

public class YMailImport extends OfflineImapImport {
    private final YabImport yabImport;
    private final OfflineCalDavDataImport calDavImport;
    
    private static final Log LOG = ZimbraLog.datasource;
    
    public YMailImport(OfflineDataSource ds) throws ServiceException {
        super(ds);
        yabImport = ds.isContactSyncEnabled() ? new YabImport(ds) : null;
        calDavImport = ds.isCalendarSyncEnabled() ?
            new OfflineCalDavDataImport(ds) : null;
    }

    @Override
    public void test() throws ServiceException {
        if (yabImport != null) {
            yabImport.test();
        }
        super.test();
    }

    @Override
    protected void connect() throws ServiceException {
        Authenticator auth = OfflineYAuth.newAuthenticator(dataSource);
        initAuth(auth);
        try {
            super.connect();
        } catch (ServiceException e) {
            if (!isAuthError(e)) throw e;
            LOG.debug("Invalidating possibly expired cookie and retrying auth");
            // Invalidate possibly expired cookie so that we will regenerate
            // and try again
            auth.invalidate();
            initAuth(auth);
            super.connect();
        }
    }

    private boolean isAuthError(ServiceException e) {
        Throwable cause = e.getCause();
        return cause == null || !(cause instanceof LoginException);
    }

    private void initAuth(Authenticator auth) throws ServiceException {
        try {
            setAuthenticator(new XYMEAuthenticator(
                auth.authenticate(), OfflineConstants.YMAIL_PARTNER_NAME));
        } catch (IOException e) {
            throw ServiceException.FAILURE("I/O error during authentication", e);
        }
    }
    
    @Override
    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
        super.importData(folderIds, fullSync);

        String dsName = dataSource.getName();
        if (yabImport != null) {
            yabImport.importData(folderIds, fullSync);
        }                
        if (calDavImport != null) {
            LOG.info("Importing calendar for YMail account '%s'", dsName);
            calDavImport.importData("yahoo.com", null, fullSync);
            LOG.info("Finished importing calendar for YMail account '%s'", dsName);
        }
    }
}
