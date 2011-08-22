/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.offline.ab.yc;

import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.mailbox.YContactSync;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.ab.LocalData;
import com.zimbra.cs.offline.util.yc.YContactException;
import com.zimbra.cs.offline.util.yc.oauth.OAuthManager;


public class YContactImport implements DataSource.DataImport {

    private final OfflineDataSource ds;
    private YContactSync session;
    
    public YContactImport(OfflineDataSource ds) throws YContactException {
        this.ds = ds;
        //TODO it could be a good place to load oauth token, not a good place for persist it as it's 
        //before OfflineProvisiong's createAccount in code path
    }
    @Override
    public void test() throws ServiceException {
    }

    @Override
    public void importData(List<Integer> folderIds, boolean fullSync) throws ServiceException {
        if (!fullSync && !new LocalData(ds).hasLocalChanges()) {
            return;
        }

        try {
            OfflineLog.yab.info("Start importing Yahoo contacts for account '%s'", ds.getName());
            if (!OAuthManager.hasOAuthToken(ds.getAccountId())) {
                OAuthManager.persistCredential(ds.getAccountId(), (String) ds.getAttr(OfflineProvisioning.A_offlineYContactToken),
                                (String) ds.getAttr(OfflineProvisioning.A_offlineYContactTokenSecret),
                                (String) ds.getAttr(OfflineProvisioning.A_offlineYContactTokenSessionHandle),
                                (String) ds.getAttr(OfflineProvisioning.A_offlineYContactGuid),
                                (String) ds.getAttr(OfflineProvisioning.A_offlineYContactTokenTimestamp),
                                (String) ds.getAttr(OfflineProvisioning.A_offlineYContactVerifier));
            }
            if (session == null) {
                session = new YContactSync(ds);
            }
            session.sync();
        } catch (Exception e) {
            OfflineLog.yab.error("Failed to import Yahoo contacts for account '%s'", ds.getName(), e);
            throw OfflineServiceException.YCONTACT_NEED_VERIFY();
        }
        OfflineLog.yab.info("Finished importing Yahoo contacts for account '%s'", ds.getName());
    }
}