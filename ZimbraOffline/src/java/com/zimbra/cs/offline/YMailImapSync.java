/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.datasource.imap.ImapSync;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.util.yauth.Authenticator;
import com.zimbra.cs.util.yauth.XYMEAuthenticator;

import javax.security.auth.login.LoginException;
import java.io.IOException;

/*
 * Supports Yahoo XYMEAuthentication mechanism for faster login.
 */
class YMailImapSync extends ImapSync {
    YMailImapSync(DataSource ds) throws ServiceException {
        super(ds);
        setReuseConnections(false);
    }

    @Override
    protected void connect() throws ServiceException {
        Authenticator auth = OfflineYAuth.newAuthenticator(getDataSource());
        setAuthenticator(newXYMEAuthenticator(auth));
        try {
            super.connect();
        } catch (ServiceException e) {
            if (!isAuthError(e)) throw e;
            ZimbraLog.datasource.debug("Invalidating possibly expired cookie and retrying auth");
            auth.invalidate();
            setAuthenticator(newXYMEAuthenticator(auth));
            super.connect();
        }
    }

    private XYMEAuthenticator newXYMEAuthenticator(Authenticator auth)
        throws ServiceException {
        try {
            return new XYMEAuthenticator(auth.authenticate(), OfflineConstants.YMAIL_PARTNER_NAME);
        } catch (IOException e) {
            throw ServiceException.FAILURE("Authentication error", e);
        }
    }

    private static boolean isAuthError(ServiceException e) {
        Throwable cause = e.getCause();
        return cause == null || cause instanceof LoginException;
    }
}
