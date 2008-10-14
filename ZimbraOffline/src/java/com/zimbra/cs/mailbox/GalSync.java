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
import java.util.Iterator;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.account.offline.OfflineGal;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.index.MailboxIndex.SortBy;
import com.zimbra.cs.index.ZimbraQueryResults;
import com.zimbra.cs.index.queryparser.ParseException;

import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;

public class GalSync {
    
    private class SaxHandler implements ElementHandler {
        
        public static final String PATH_RESPONSE = "/Envelope/Body/SyncGalResponse";
        public static final String PATH_CN = PATH_RESPONSE + "/cn";
        
        private OfflineAccount galAccount;
        private boolean fullSync;
        private boolean trace;        
        private LocalMailbox galMbox;
        private Mailbox.OperationContext context;
        private Exception exception = null;       
        private String token = null;
        
        public SaxHandler(OfflineAccount galAccount, boolean fullSync, boolean trace) {
            this.galAccount = galAccount;
            this.fullSync = fullSync;
            this.trace = trace && OfflineLC.zdesktop_gal_sync_trace_enabled.booleanValue();
        }
        
        public String getToken() {
            return token;
        }
        
        public Exception getException() {
            return exception;
        }
        
        public void onStart(ElementPath elPath) { //TODO: add trace logging; 
            String path = elPath.getPath();
            if (!path.equals(PATH_RESPONSE))
                return;
            
            org.dom4j.Element row = elPath.getCurrent();
            token = row.attributeValue(AdminConstants.A_TOKEN);
            if (token == null) {
                OfflineLog.offline.debug("Offline GAL parse error: SyncGalResponse has no token attribute");
                elPath.removeHandler(PATH_CN);
                return;
            }

            try {
                galMbox = (LocalMailbox) MailboxManager.getInstance().getMailboxByAccountId(galAccount.getId(), false);
                context = new Mailbox.OperationContext(galMbox);
                if (fullSync) {
                    OfflineLog.offline.debug("Offline GAL full sync requested. Clearing current GAL folder: " + galAccount.getName());
                    galMbox.emptyFolder(context, Mailbox.ID_FOLDER_CONTACTS, true);
                }
            } catch (ServiceException e) {
                exception = e;
                elPath.removeHandler(PATH_CN);
            }                                        
        }
                
        public void onEnd(ElementPath elPath) { //TODO: add trace logging;
            String path = elPath.getPath();
            if (!path.equals(PATH_CN))
                return;
            
            if (token == null) {
                OfflineLog.offline.debug("Offline GAL parse error: missing SyncGalResponse tag");
                elPath.removeHandler(PATH_CN);
                return;
            }
                                                        
            org.dom4j.Element row = elPath.getCurrent();
            String dn = row.attributeValue(AdminConstants.A_ID);           
            if (dn == null) {
                OfflineLog.offline.debug("Offline GAL parse error: cn has no id attribute");
            } else {
                Map<String, String> map = new HashMap<String, String>();
                map.put(OfflineConstants.GAL_LDAP_DN, dn); 
                
                Iterator itr = row.elementIterator();
                while(itr.hasNext()) {
                    org.dom4j.Element child = (org.dom4j.Element) itr.next();
                    String key = child.attributeValue(AdminConstants.A_N);
                    
                    if (!key.equals("objectClass"))  
                        map.put(key, child.getText());
                }
                        
                String fname, lname;
                if (map.get(Contact.A_fullName) == null && (fname = map.get(Contact.A_firstName)) != null && 
                    (lname = map.get(Contact.A_lastName)) != null)
                    map.put(Contact.A_fullName, fname + " " + lname);            
                map.put(Contact.A_type, map.get(OfflineGal.A_zimbraCalResType) == null ?
                    OfflineGal.CTYPE_ACCOUNT : OfflineGal.CTYPE_RESOURCE);
                        
                try {
                    ParsedContact contact = new ParsedContact(map);
                        
                    String ctime, mtime;
                    if (fullSync || ((ctime = map.get("createTimeStamp")) != null &&
                        (mtime = map.get("modifyTimeStamp")) != null && mtime.equals(ctime))) {
                        galMbox.createContact(context, contact, Mailbox.ID_FOLDER_CONTACTS, null);
                        OfflineLog.offline.debug("Offline GAL contact created: dn=" + dn);
                    } else {
                        byte[] types = new byte[1];
                        types[0] = MailItem.TYPE_CONTACT;
                        
                        ZimbraQueryResults zqr = galMbox.search(context, dn, types, SortBy.NONE, 1);
                        try {                         
                            if (zqr.hasNext()) {
                                galMbox.modifyContact(context, zqr.getNext().getItemId(), contact);
                                OfflineLog.offline.debug("Offline GAL contact modified: dn=" + dn);
                            } else {
                                galMbox.createContact(context, contact, Mailbox.ID_FOLDER_CONTACTS, null);
                                OfflineLog.offline.debug("Offline GAL contact created: dn=" + dn);
                            }
                        } finally {
                            zqr.doneWithSearchResults();
                        }
                    }
                } catch (ServiceException e) {
                    exception = e;
                } catch (ParseException e) {
                    exception = e;
                } catch (IOException e) {
                    exception = e;
                }
                
                if (exception != null)
                    elPath.removeHandler(PATH_CN);
            }
            
            row.detach(); // done with this node - prune it off to save memory
        }
    }
    
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
            OfflineLog.offline.debug("Offline GAL sync is disabled: " + user);
            syncMan.syncComplete(target);
            ensureGalAccountNotExists(account);
            return;
        }
        
        OfflineAccount galAccount = ensureGalAccountExists(account);
        long lastFullSync = galAccount.getLongAttr(OfflineConstants.A_offlineGalAccountLastFullSync, 0);
        if (isOnRequest && lastFullSync > 0)
            return;
        
        boolean success = true;
        try {            
            OfflineLog.offline.info("Offline GAL sync started: " + user);            
            syncGal(ombx, galAccount, lastFullSync, account.isDebugTraceEnabled());                            
        } catch (Exception e) {
            syncMan.processSyncException(target, "", e, account.isDebugTraceEnabled());
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
    
    private static void syncGal(OfflineMailbox mbox, OfflineAccount galAccount, long lastFullSync, boolean traceEnabled)
        throws ServiceException, IOException, ParseException {                
        String syncToken = galAccount.getAttr(OfflineConstants.A_offlineGalAccountSyncToken);
        long interval = OfflineLC.zdesktop_gal_refresh_interval_days.longValue();        
        if (lastFullSync > 0 && (System.currentTimeMillis() - lastFullSync) / Constants.MILLIS_PER_DAY >= interval)
            syncToken = "";
        boolean fullSync = (syncToken == null || syncToken.length() == 0);
        
        XMLElement req = new XMLElement(AccountConstants.SYNC_GAL_REQUEST);
        if (!fullSync)
            req.addAttribute(AdminConstants.A_TOKEN, syncToken);
               
        SaxHandler handler =  (new GalSync()).new SaxHandler(galAccount, fullSync, traceEnabled); 
        HashMap<String, ElementHandler> handlers = new HashMap<String, ElementHandler>();
        handlers.put(SaxHandler.PATH_RESPONSE, handler);
        handlers.put(SaxHandler.PATH_CN, handler);
        
        mbox.sendRequest(req, true, true, OfflineLC.zdesktop_galsync_request_timeout.intValue(), SoapProtocol.Soap12, handlers);

        Exception e = handler.getException();
        if (e != null) {
            if (e instanceof ServiceException)
                throw (ServiceException) e;
            else if (e instanceof ParseException)
                throw (ParseException) e;
            else
                throw (IOException) e;
        }
        
        OfflineProvisioning prov = (OfflineProvisioning)Provisioning.getInstance();
        prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountSyncToken, handler.getToken());
        if (fullSync) {
            prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountLastFullSync,
                Long.toString(System.currentTimeMillis()));
        }
    }    
}
