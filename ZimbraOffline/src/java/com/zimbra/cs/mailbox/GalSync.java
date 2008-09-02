/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008 Zimbra, Inc.
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
package com.zimbra.cs.mailbox;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.Provisioning.SearchGalResult;
import com.zimbra.cs.index.MailboxIndex.SortBy;
import com.zimbra.cs.index.ZimbraQueryResults;
import com.zimbra.cs.index.queryparser.ParseException;

public class GalSync {
    
    public static void sync(OfflineMailbox ombx, OfflineSyncManager syncMan, boolean isOnRequest) throws ServiceException {
        String user = ombx.getRemoteUser();
        String target = user + OfflineConstants.GAL_ACCOUNT_SUFFIX;
        if (!isOnRequest) {
            if (!syncMan.retryOK(target))
                return;
            long interval = OfflineLC.zdesktop_gal_sync_interval_secs.longValue();
            long last = syncMan.getLastSyncTime(target);
            if (last > 0 && (System.currentTimeMillis() - last) / 1000 < interval)
                return;
        }
                
        syncMan.syncStart(target);
        
        OfflineAccount account = (OfflineAccount)ombx.getAccount();
        if (!account.getBooleanAttr(Provisioning.A_zimbraFeatureGalEnabled , false) ||
            !account.getBooleanAttr(Provisioning.A_zimbraFeatureGalSyncEnabled , false)) {
            OfflineLog.offline.debug("Offline GAL sync is disable: " + user);
            syncMan.syncComplete(target);
            ensureGalAccountNotExists(account);
            return;
        }
        
        boolean success = true;
        try {
            OfflineLog.offline.info("Offline GAL sync started: " + user);            
            syncGal(ombx, account, isOnRequest);                            
        } catch (Exception e) {
            syncMan.processSyncException(target, "", e);
            success = false;
        } finally {
            syncMan.syncComplete(target);
            if (success)
                OfflineLog.offline.info("Offline GAL sync completed successfully: " + user);
            else
                OfflineLog.offline.info("Offline GAL sync failed: " + user);
        }
    }

    private static void ensureGalAccountNotExists(OfflineAccount account) throws ServiceException {       
        OfflineProvisioning prov = (OfflineProvisioning)Provisioning.getInstance();
        prov.deleteGalAccount(account);
    }
    
    private static OfflineAccount ensureGalAccountExists(OfflineAccount account) throws ServiceException {
        String galAcctId = account.getAttr(OfflineConstants.A_offlineGalAccountId, false);
        OfflineProvisioning prov = (OfflineProvisioning)Provisioning.getInstance();
        OfflineAccount galAcct;
        if (galAcctId != null && galAcctId.length() > 0 && (galAcct = (OfflineAccount)prov.get(AccountBy.id, galAcctId)) != null)
            return galAcct;
            
        galAcct = prov.createGalAccount(account);
        OfflineLog.offline.info("Offline GAL mailbox created: " + galAcct.getName());
        return galAcct;
    }
    
    private static void syncGal(OfflineMailbox mbox, OfflineAccount account, boolean isOnRequest)
        throws ServiceException, IOException, ParseException {        
        OfflineAccount galAccount = ensureGalAccountExists(account);

        long lastFullSync = galAccount.getLongAttr(OfflineConstants.A_offlineGalAccountLastFullSync, 0);
        if (isOnRequest && lastFullSync > 0)
            return;
        
        String syncToken = galAccount.getAttr(OfflineConstants.A_offlineGalAccountSyncToken);        
        long interval = OfflineLC.zdesktop_gal_refresh_interval_days.longValue();        
        if (lastFullSync > 0 && (System.currentTimeMillis() - lastFullSync) / Constants.MILLIS_PER_DAY >= interval)
            syncToken = "";
        boolean fullSync = (syncToken == null || syncToken.length() == 0);
        
        XMLElement req = new XMLElement(AccountConstants.SYNC_GAL_REQUEST);
        if (!fullSync)
            req.addAttribute(AdminConstants.A_TOKEN, syncToken);
        Element resp = mbox.sendRequest(req, true, true, OfflineLC.zdesktop_galsync_request_timeout.intValue());

        LocalMailbox galMbox = (LocalMailbox)MailboxManager.getInstance().getMailboxByAccountId(galAccount.getId(), false);
        Mailbox.OperationContext context = new Mailbox.OperationContext(galMbox);
        if (fullSync) {
            OfflineLog.offline.debug("Offline GAL full sync requested. Clearing current GAL folder: " + galAccount.getName());
            galMbox.emptyFolder(context, Mailbox.ID_FOLDER_CONTACTS, true);            
        }
                
        SearchGalResult syncResult = new SearchGalResult();
        syncResult.token = resp.getAttribute(AdminConstants.A_TOKEN);
        for (Element e: resp.listElements(AdminConstants.E_CN)) {
            Map<String, String> map = new HashMap<String, String>();
            String dn = e.getAttribute(AdminConstants.A_ID);
            map.put(OfflineConstants.GAL_LDAP_DN, dn);         
            for (Element a : e.listElements(AdminConstants.E_A)) {
                map.put(a.getAttribute(AdminConstants.A_N), a.getText());
            }
            
            String fname, lname;
            if (map.get(Contact.A_fullName) == null && (fname = map.get(Contact.A_firstName)) != null && 
                (lname = map.get(Contact.A_lastName)) != null)
                map.put(Contact.A_fullName, fname + " " + lname);
            
            ParsedContact contact = new ParsedContact(map);
            
            String ctime, mtime;
            if (fullSync || ((ctime = map.get("createTimeStamp")) != null &&
                (mtime = map.get("modifyTimeStamp")) != null && mtime.equals(ctime))) {
                galMbox.createContact(context, contact, Mailbox.ID_FOLDER_CONTACTS, null);
                OfflineLog.offline.debug("Offline GAL contact crearted: dn=" + dn);
            } else {
                byte[] types = new byte[1];
                types[0] = MailItem.TYPE_CONTACT;
                ZimbraQueryResults zqr = galMbox.search(context, dn, types, SortBy.NONE, 1);
                if (zqr.hasNext()) {
                    galMbox.modifyContact(context, zqr.getNext().getItemId(), contact);
                    OfflineLog.offline.debug("Offline GAL contact modified: dn=" + dn);
                } else {
                    galMbox.createContact(context, contact, Mailbox.ID_FOLDER_CONTACTS, null);
                    OfflineLog.offline.debug("Offline GAL contact crearted: dn=" + dn);
                }
            }
        }
        
        OfflineProvisioning prov = (OfflineProvisioning)Provisioning.getInstance();
        prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountSyncToken, syncResult.token);
        if (fullSync) {
            prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountLastFullSync,
                Long.toString(System.currentTimeMillis()));
        }
    }    
}
