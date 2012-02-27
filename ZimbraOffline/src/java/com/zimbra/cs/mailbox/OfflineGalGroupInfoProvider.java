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
package com.zimbra.cs.mailbox;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.gal.GalGroup.GroupInfo;
import com.zimbra.cs.gal.GalGroupInfoProvider;
import com.zimbra.cs.offline.OfflineLog;

/**
 * Provide GroupInfo from entries in OfflineGal
 * 
 */
public class OfflineGalGroupInfoProvider extends GalGroupInfoProvider {

    @Override
    public GroupInfo getGroupInfo(String addr, boolean needCanExpand, Account requestedAcct, Account authedAcct) {
        OfflineAccount reqAccount = (OfflineAccount) requestedAcct;
        if (reqAccount.isZcsAccount() && reqAccount.isFeatureGalEnabled() && reqAccount.isFeatureGalSyncEnabled()) {
            try {
                Contact con = GalSyncUtil.getGalDlistContact(reqAccount, addr);
                if (con != null && con.isGroup()) {
                    return needCanExpand ? GroupInfo.CAN_EXPAND : GroupInfo.IS_GROUP;
                }
            } catch (ServiceException e) {
                OfflineLog.offline.error("Unable to find group %s addr due to exception", e, addr);
            }
        }
        return null;
    }

}
