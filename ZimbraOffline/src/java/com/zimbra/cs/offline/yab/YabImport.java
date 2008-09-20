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
package com.zimbra.cs.offline.yab;

import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.offline.util.yab.Session;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.util.yauth.RawAuthManager;
import com.zimbra.cs.util.yauth.Authenticator;
import com.zimbra.common.service.ServiceException;

import java.util.List;

public class YabImport implements DataSource.DataImport {
    private final OfflineDataSource ds;
    private SyncSession session;

    public YabImport(DataSource ds) throws ServiceException {
        this.ds = (OfflineDataSource) ds;
        session = newSyncSession();
    }
    
    public String test() throws ServiceException {
        return null;
    }

    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
        session.sync();
    }

    private SyncSession newSyncSession() throws ServiceException {
        Session session = new Session(OfflineYAuth.newAuthenticator(ds));
        return new SyncSession(ds.getMailbox(), session);
    }
}
