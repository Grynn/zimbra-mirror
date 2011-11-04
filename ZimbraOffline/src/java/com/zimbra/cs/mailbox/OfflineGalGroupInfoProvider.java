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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
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

    @Override
    public void encodeAddrsWithGroupInfo(Provisioning prov, Element eParent, String emailElem, Account requestedAcct,
            Account authedAcct) {
        OfflineAccount reqAccount = (OfflineAccount) requestedAcct;
        if (reqAccount.isZcsAccount() && reqAccount.isFeatureGalEnabled() && reqAccount.isFeatureGalSyncEnabled()) {
            Map<String, Element> emailElems = new HashMap<String, Element>();
            for (Element eEmail : eParent.listElements(emailElem)) {
                String addr = eEmail.getAttribute(MailConstants.A_ADDRESS, null);
                if (addr != null) {
                    // shortcut the check if the email address is the authed or requested account - it cannot be a group
                    if (addr.equalsIgnoreCase(requestedAcct.getName()) || addr.equalsIgnoreCase(authedAcct.getName()))
                        continue;
                    emailElems.put(addr, eEmail);
                }

                if (emailElems.size() >= 100) { // 100 at a time seems reasonable; more will overflow SQLite param limit
                    encodeGroups(emailElems, reqAccount);
                    emailElems.clear();
                }
            }
            encodeGroups(emailElems, reqAccount); // last chunk
        }
    }

    private void encodeGroups(Map<String, Element> emailElems, OfflineAccount requestedAcct) {
        try {
            if (emailElems == null || emailElems.size() <= 0) {
                return;
            }
            List<String> groups = GalSyncUtil.getGroupNames(requestedAcct, emailElems.keySet());
            for (String group : groups) {
                Element groupEmailElem = emailElems.get(group);
                if (groupEmailElem != null) {
                    groupEmailElem.addAttribute(MailConstants.A_IS_GROUP, true);
                    groupEmailElem.addAttribute(MailConstants.A_EXP, true);
                }
            }
        } catch (ServiceException e) {
            OfflineLog.offline.error("Unable to find groups due to exception", e);
        }
    }
}
