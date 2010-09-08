/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
import java.util.Map;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.DataSource.Type;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.account.offline.OfflineGal;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.db.DbDataSource;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.index.ZimbraQueryResults;
import com.zimbra.cs.offline.common.OfflineConstants.SyncStatus;
import com.zimbra.common.util.Pair;

import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;

public class GalSync {

    private static class SyncHandler implements ElementHandler {

        public static final String PATH_RESPONSE = "/Envelope/Body/SyncGalResponse";
        public static final String PATH_CN = PATH_RESPONSE + "/cn";

        private OfflineAccount galAccount;
        private boolean fullSync;
        private Mailbox galMbox;
        private OperationContext context;
        private Exception exception = null;
        private String token = null;
        private int syncFolder;
        private int dropFolder;
        private ArrayList<String> idGroups = null;
        private int grpSize = OfflineLC.zdesktop_gal_sync_group_size.intValue();
        private int idCount;
        private DataSource ds = null;

        public SyncHandler(OfflineAccount galAccount, boolean fullSync, boolean trace) {
            this.galAccount = galAccount;
            this.fullSync = fullSync;
        }

        public String getToken() { return token; }
        public OfflineAccount getGalAccount() { return galAccount; }
        public int getDropFolder() { return dropFolder; }
        public Exception getException() { return exception; }
        public int getGroupCount() { return idGroups == null ? 0 : idGroups.size(); }
        public String removeGroup() { return idGroups.remove(0); }

        @Override
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
                galMbox = MailboxManager.getInstance().getMailboxByAccountId(galAccount.getId(), false);
                context = new OperationContext(galMbox);
                Pair<Integer, Integer> pair = OfflineGal.getSyncFolders(galMbox, context);
                String dsId = galAccount.getAttr(OfflineConstants.A_offlineGalAccountDataSourceId, false);

                if (fullSync) {
                    OfflineLog.offline.debug("Offline GAL full sync requested: " + galAccount.getName());
                    syncFolder = pair.getSecond().intValue();
                    dropFolder = pair.getFirst().intValue();
                    galMbox.emptyFolder(context, syncFolder, false);

                    if (dsId == null) {
                        dsId = UUID.randomUUID().toString();
                        OfflineProvisioning.getOfflineInstance().setAccountAttribute(galAccount,
                            OfflineConstants.A_offlineGalAccountDataSourceId, dsId);
                    }
                } else {
                    syncFolder = pair.getFirst().intValue();
                }

                if (dsId != null) {
                    ds = new DataSource(galAccount, Type.gal, galAccount.getName(), dsId,
                        new HashMap<String, Object>(), OfflineProvisioning.getOfflineInstance());
                    if (fullSync)
                        DbDataSource.deleteAllMappings(ds);
                }
            } catch (ServiceException e) {
                exception = e;
                elPath.removeHandler(PATH_CN);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
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
            String id = row.attributeValue(AdminConstants.A_ID);
            if (id == null) {
                OfflineLog.offline.debug("Offline GAL parse error: cn has no id attribute");
            } else {
                Iterator itr = row.elementIterator();
                if (itr.hasNext()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(OfflineConstants.GAL_LDAP_DN, id);
                    while(itr.hasNext()) {
                        org.dom4j.Element child = (org.dom4j.Element) itr.next();
                        String key = child.attributeValue(AdminConstants.A_N);
                        if (!key.equals("objectClass"))
                            map.put(key, child.getText());
                    }

                    try {
                        saveContact(id, map, galMbox);
                    } catch (ServiceException e) {
                        exception = e;
                    } catch (IOException e) {
                        exception = e;
                    }

                    if (exception != null)
                        elPath.removeHandler(PATH_CN);
                } else { // Ids Only
                    if (idGroups == null)
                        idGroups = new ArrayList<String>();
                    if (idGroups.size() == 0 || idCount >= grpSize) {
                        idGroups.add(id);
                        idCount = 1;
                    } else {
                        int i = idGroups.size() - 1;
                        idGroups.set(i, idGroups.get(i) + "," + id);
                        idCount++;
                    }
                }
            }

            row.detach(); // done with this node - prune it off to save memory
        }

        private void createContact(ParsedContact contact, String id, String logstr) throws ServiceException {
            Contact c = galMbox.createContact(context, contact, syncFolder, null);
            if (ds != null)
                DbDataSource.addMapping(ds, new DataSourceItem(0, c.getId(), id, null));
            OfflineLog.offline.debug("Offline GAL contact created: " + logstr);
        }

        private int findContact(String id) throws ServiceException, IOException {
            if (ds == null) {
                byte[] types = new byte[1];
                types[0] = MailItem.TYPE_CONTACT;

                ZimbraQueryResults zqr = galMbox.search(context, "#" + OfflineConstants.GAL_LDAP_DN + ":\"" + id + "\"", types, SortBy.NONE, 1);
                try {
                    if (zqr.hasNext())
                        return zqr.getNext().getItemId();
                } finally {
                    zqr.doneWithSearchResults();
                }
            } else {
                DataSourceItem dsItem = DbDataSource.getReverseMapping(ds, id);
                if (dsItem.itemId > 0)
                    return dsItem.itemId;
            }
            return -1;
        }

        public void saveContact(String id, Map<String, String> map, Mailbox galMbox) throws ServiceException, IOException {
            String fullName = map.get(ContactConstants.A_fullName);
            if (fullName == null) {
                String fname = map.get(ContactConstants.A_firstName);
                String lname = map.get(ContactConstants.A_lastName);
                fullName = fname == null ? "" : fname;
                if (lname != null)
                    fullName = fullName + (fullName.length() > 0 ? " " : "") + lname;
                if (fullName.length() > 0)
                    map.put(ContactConstants.A_fullName, fullName);
            }
            map.put(ContactConstants.A_type, map.get(OfflineGal.A_zimbraCalResType) == null ? OfflineGal.CTYPE_ACCOUNT : OfflineGal.CTYPE_RESOURCE);
            String logstr = "id=" + id + " name=\"" + fullName + "\"";

            ParsedContact contact = new ParsedContact(map);
            if (fullSync) {
                createContact(contact, id, logstr);
            } else {
                int itemId = findContact(id);
                if (itemId > 0) {
                    try {
                        galMbox.modifyContact(context, itemId, contact);
                        OfflineLog.offline.debug("Offline GAL contact modified: " + logstr);
                    } catch (MailServiceException.NoSuchItemException e) {
                        OfflineLog.offline.warn("Offline GAL modify error - no such contact: " + logstr + " itemId=" + Integer.toString(itemId));
                    }
                } else {
                    createContact(contact, id, logstr);
                }
            }
        }
    }

    private static class SyncThread extends Thread {
        private ZcsMailbox ombx;
        private String user;
        private OfflineAccount galAccount;
        private long lastFullSync;
        private boolean traceOn;
        private Semaphore lock;

        public SyncThread(ZcsMailbox ombx, String user, OfflineAccount galAccount, long lastFullSync, boolean traceOn, Semaphore lock) {
            super("sync-gal-" + user);
            this.ombx = ombx;
            this.user = user;
            this.galAccount = galAccount;
            this.lastFullSync = lastFullSync;
            this.traceOn = traceOn;
            this.lock = lock;

            setPriority(MIN_PRIORITY);
        }

        @Override
        public void run() {
            OfflineSyncManager syncMan = OfflineSyncManager.getInstance();
            try {
                OfflineLog.offline.info("Offline GAL sync started for " + user);
                syncGal(ombx, galAccount, lastFullSync, traceOn);
                syncMan.syncComplete(galAccount);
                OfflineLog.offline.info("Offline GAL sync completed successfully for " + user);
            } catch (Exception e) {
                syncMan.processSyncException(galAccount, "", e, traceOn);
                OfflineLog.offline.info("Offline GAL sync failed for " + user +
                    ": " + e.getMessage());
            } finally {
                lock.release();
            }
        }
    };

    private static Semaphore galSyncLock = new Semaphore(1);

    public static void sync(ZcsMailbox ombx, boolean isOnRequest) throws ServiceException {
        OfflineAccount account = (OfflineAccount)ombx.getAccount();
        if (!account.getBooleanAttr(Provisioning.A_zimbraFeatureGalEnabled , false) ||
            !account.getBooleanAttr(Provisioning.A_zimbraFeatureGalSyncEnabled , false)) {
            OfflineLog.offline.debug("Offline GAL sync is disabled: " + ombx.getRemoteUser());
            ensureGalAccountNotExists(account);
            return;
        }
        OfflineAccount galAccount = ensureGalAccountExists(account);
        OfflineSyncManager syncMan = OfflineSyncManager.getInstance();
        if (syncMan.getSyncStatus(galAccount) == SyncStatus.running)
            return;

        if (!isOnRequest) {
            if (!syncMan.retryOK(galAccount))
                return;
            long interval = OfflineLC.zdesktop_gal_sync_interval_secs.longValue();
            long last = syncMan.getLastSyncTime(galAccount);
            if (last > 0 && (System.currentTimeMillis() - last) / 1000 < interval)
                return;
        }

        if (!OfflineLC.zdesktop_sync_gal.booleanValue()) {
            OfflineLog.offline.debug("Offline GAL sync is disabled in local config (zdesktop_sync_gal=false)");
            syncMan.resetLastSyncTime(galAccount);
            return;
        }

        // to reduce system resources used by gal sync, only one active gal sync op is allowed
        if (!galSyncLock.tryAcquire()) {
            OfflineLog.offline.debug("Another account is running Offline GAL sync. Will retry later.");
            return;
        }

        long lastFullSync = galAccount.getLongAttr(OfflineConstants.A_offlineGalAccountLastFullSync, 0);
        syncMan.syncStart(galAccount);

        /* TODO: allow graceful shutdown of this thread once offline improves its shutdown routines
         * currently if server shuts down during gal sync, we get a nasty redo log exception from this thread,
         * which is the same as what we would get during mailbox sync. */
        new SyncThread(ombx, ombx.getRemoteUser(), galAccount, lastFullSync, account.isDebugTraceEnabled(), galSyncLock).start();
    }

    private static void ensureGalAccountNotExists(OfflineAccount account) throws ServiceException {
        OfflineProvisioning prov = (OfflineProvisioning)Provisioning.getInstance();
        prov.deleteGalAccount(account);
    }

    private static OfflineAccount ensureGalAccountExists(OfflineAccount account) throws ServiceException {
        String galAcctId = account.getAttr(OfflineConstants.A_offlineGalAccountId, false);
        OfflineProvisioning prov = (OfflineProvisioning)Provisioning.getInstance();
        OfflineAccount galAcct;
        if (galAcctId == null || galAcctId.length() == 0 || (galAcct = (OfflineAccount)prov.get(AccountBy.id, galAcctId)) == null) {
            galAcct = prov.createGalAccount(account);
            OfflineLog.offline.info("Offline GAL mailbox created: " + galAcct.getName());
        }

        // ensure second alternating contact folder is created as well
        Mailbox galMbox = MailboxManager.getInstance().getMailboxByAccountId(galAcct.getId(), false);
        OperationContext octxt = new OperationContext(galMbox);
        try {
            galMbox.getFolderByPath(octxt, OfflineGal.SECOND_GAL_FOLDER);
        } catch (MailServiceException.NoSuchItemException e) {
            try {
                galMbox.createFolder(octxt, OfflineGal.SECOND_GAL_FOLDER, (byte)0, MailItem.TYPE_CONTACT);
            } catch (ServiceException se) {
                prov.deleteGalAccount(account);
                throw se;
            }
        }
        return galAcct;
    }

    private static void syncGal(ZcsMailbox mbox, OfflineAccount galAccount, long lastFullSync, boolean traceEnabled)
        throws ServiceException, IOException {
        String syncToken = galAccount.getAttr(OfflineConstants.A_offlineGalAccountSyncToken, false);
        long interval = OfflineLC.zdesktop_gal_refresh_interval_days.longValue();
        if (lastFullSync > 0 && (System.currentTimeMillis() - lastFullSync) / Constants.MILLIS_PER_DAY >= interval)
            syncToken = "";
        boolean fullSync = (syncToken == null || syncToken.length() == 0);

        XMLElement req = new XMLElement(AccountConstants.SYNC_GAL_REQUEST);
        req.addAttribute(AccountConstants.A_ID_ONLY, "true");
        if (!fullSync)
            req.addAttribute(AdminConstants.A_TOKEN, syncToken);

        SyncHandler handler =  new SyncHandler(galAccount, fullSync, traceEnabled);
        HashMap<String, ElementHandler> handlers = new HashMap<String, ElementHandler>();
        handlers.put(SyncHandler.PATH_RESPONSE, handler);
        handlers.put(SyncHandler.PATH_CN, handler);

        mbox.sendRequest(req, true, true, OfflineLC.zdesktop_gal_sync_request_timeout.intValue(), SoapProtocol.Soap12, handlers);

        Exception e = handler.getException();
        String token;
        if (e != null) {
            if (e instanceof ServiceException)
                throw (ServiceException) e;
            else
                throw (IOException) e;
        } else if ((token = handler.getToken()) == null) {
            throw ServiceException.FAILURE("unable to search GAL", null);
        }

        while (handler.getGroupCount() > 0) {
            fetchContacts(handler, mbox);
            if (handler.getGroupCount() > 0) {
                try {
                    Thread.sleep(OfflineLC.zdesktop_gal_sync_group_interval.longValue());
                } catch (InterruptedException ie) {}
            }
        }

        OfflineProvisioning prov = (OfflineProvisioning)Provisioning.getInstance();
        prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountSyncToken, token);
        if (fullSync) {
            Mailbox galMbox = MailboxManager.getInstance().getMailboxByAccountId(galAccount.getId(), false);
            OperationContext octxt = new OperationContext(galMbox);
            galMbox.emptyFolder(octxt, handler.getDropFolder(), false);
            prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountLastFullSync, Long.toString(System.currentTimeMillis()));
            galMbox.optimize(null, 0);
        }
    }

    private static void fetchContacts(SyncHandler handler, ZcsMailbox mbox) throws ServiceException, IOException {
        XMLElement req = new XMLElement(MailConstants.GET_CONTACTS_REQUEST);
        req.addElement(AdminConstants.E_CN).addAttribute(AccountConstants.A_ID, handler.removeGroup());
        Element response = mbox.sendRequest(req, true, true, OfflineLC.zdesktop_gal_sync_request_timeout.intValue(), SoapProtocol.Soap12);

        Mailbox galMbox = MailboxManager.getInstance().getMailboxByAccountId(handler.getGalAccount().getId(), false);
        List<Element> contacts = response.listElements(MailConstants.E_CONTACT);
        for(Element elt : contacts) {
            String id = elt.getAttribute(AccountConstants.A_ID);
            Map<String, String> fields = new HashMap<String, String>();
            fields.put(OfflineConstants.GAL_LDAP_DN, id);
            for (Element eField : elt.listElements()) {
                String name = eField.getAttribute(Element.XMLElement.A_ATTR_NAME);
                if (!name.equals("objectClass"))
                    fields.put(name, eField.getText());
            }
            handler.saveContact(id, fields, galMbox);
        }
    }
}
