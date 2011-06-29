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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.db.DbDataSource;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.ab.Change;
import com.zimbra.cs.offline.ab.Change.Type;
import com.zimbra.cs.offline.ab.LocalData;
import com.zimbra.cs.offline.ab.SyncState;
import com.zimbra.cs.offline.ab.yc.ContactData;
import com.zimbra.cs.offline.ab.yc.RemoteId;
import com.zimbra.cs.offline.util.Xml;
import com.zimbra.cs.offline.util.yc.Action;
import com.zimbra.cs.offline.util.yc.Contact;
import com.zimbra.cs.offline.util.yc.ContactAddOrUpdate;
import com.zimbra.cs.offline.util.yc.ContactChanges;
import com.zimbra.cs.offline.util.yc.ContactOperation;
import com.zimbra.cs.offline.util.yc.ContactRemove;
import com.zimbra.cs.offline.util.yc.ContactSync;
import com.zimbra.cs.offline.util.yc.Fields;
import com.zimbra.cs.offline.util.yc.PutRequest;
import com.zimbra.cs.offline.util.yc.PutResponse;
import com.zimbra.cs.offline.util.yc.SyncRequest;
import com.zimbra.cs.offline.util.yc.SyncResponse;
import com.zimbra.cs.offline.util.yc.SyncResult;
import com.zimbra.cs.offline.util.yc.YContactException;
import com.zimbra.cs.offline.util.yc.YContactSyncResult;
import com.zimbra.cs.offline.util.yc.oauth.OAuthException;
import com.zimbra.cs.offline.util.yc.oauth.OAuthManager;

public class YContactSync {

    private static DocumentBuilder builder = Xml.newDocumentBuilder();
    private static boolean isMigrated = false;
    private final LocalData localData;
    private static int YCONTACT_FOLDER_ID = -2;
    public static int YAB_FOLDER_ID = -1;
    private List<ContactOperation> contactOperations = new ArrayList<ContactOperation>();
    private Map<String, Integer> localContactIdMap = new HashMap<String, Integer>();
    private static String MIGRATE_FLAG = "offline-ycontact-migration";

    public YContactSync(DataSource ds) throws ServiceException {
        this.localData = new LocalData((OfflineDataSource) ds);
    }

    /**
     * SyncRequest is used to sync all contacts, it happens when client rev is 0
     * (first time sync) or there is no client side update. otherwise, we need
     * to use PutRequest to push client data to server and sync from server
     * using the corresponding resp
     * 
     * @throws Exception
     */
    public void sync() throws Exception {
        SyncState ss = this.localData.loadState();
        OfflineLog.yab.debug("SyncState before sync yahoo contacts: %s", ss);
        if (ss == null) {
            OfflineLog.yab.info("Sync state version change - resetting contact data");
            ss = new SyncState();
        }
        DataSourceMailbox mbox = (DataSourceMailbox) localData.getMailbox();
        int clientRev = 0;
        String lastRev = ss.getLastRevision();
        if (!StringUtil.isNullOrEmpty(lastRev)) {
            try {
                clientRev = Integer.parseInt(ss.getLastRevision());
            } catch (NumberFormatException e) {
            }
        }
        int seq = ss.getLastModSequence();
        YContactSyncResult ysyncResult = null;
        Map<Integer, Change> localChanges = this.localData.getContactChanges(seq);
        OfflineLog.yab.debug("yahoo contacts local changes size: %d, map: %s", localChanges.size(),
                localChanges.toString());
        if (clientRev == 0 || localChanges.isEmpty()) {
            ysyncResult = syncFromServer(mbox, clientRev);
        } else {
            try {
                ysyncResult = pushToServerAndSync(mbox, clientRev, localChanges);
            } catch (Exception e) {
                if (e instanceof OAuthException) {
                    String errCode = ((OAuthException) e).getCode();
                    if (OAuthException.BAD_REQUEST.equals(errCode)) {
                        OfflineLog.yab.debug("Brute force sync from server");
                        ysyncResult = syncFromServer(mbox, 0);
                        /**
                         * TODO need to make choice here, either delete local contacts that don't exist on server or add them to server.
                         * It all depends on whether the most recent action is done on server or client, which we couldn't tell.
                         */
                    }
                }
            }
        }

        updateDb(ss, mbox, ysyncResult);
    }

    private YContactSyncResult pushToServerAndSync(DataSourceMailbox mbox, int clientRev,
            Map<Integer, Change> localChanges) throws Exception {

        String reqBody = constructLocalChangeXml(mbox, localChanges, clientRev);
        OfflineLog.yab.debug("push req to yahoo: %s", reqBody);
        PutRequest req;
        PutResponse resp;
        try {
            req = new PutRequest(OAuthManager.getOAuthToken(mbox.getAccountId()), reqBody);
            resp = req.send();
        } catch (Exception e) {
            req = new PutRequest(OAuthManager.getRetryToken(mbox.getAccountId()), reqBody);
            resp = req.send();
        }
        SyncResult syncResult = new SyncResult();
        resp.extract(syncResult);
        OfflineLog.yab.debug("push sync result, result items: %d, diff items: %d", syncResult.getResults().size(),
                syncResult.getDiff().size());

        for (String id : syncResult.getDiff().keySet()) {
            Contact contact = syncResult.getDiff().get(id);
            switch (contact.getOp()) {
            case ADD:
            case UPDATE:
                ContactData cd = new ContactData(contact);
                try {
                    // TODO, need combine contact in zd and diff part from yahoo, we are good for now as yahoo
                    // actually returns the whole contact in diff part
                    this.contactOperations.add(new ContactAddOrUpdate(cd.getParsedContact(), contact, true));
                } catch (ServiceException e) {
                    SyncExceptionHandler.syncContactFailed(mbox, -1,
                            Xml.toString(contact.toXml(getContactSyncDocument())), e);
                    OfflineLog.yab.error("yahoo contact parsing error, %s",
                            Xml.toString(contact.toXml(getContactSyncDocument())));
                }
                break;
            case REMOVE:
                this.contactOperations.add(new ContactRemove(contact.getId()));
                break;
            }
        }

        OfflineLog.yab.debug("put to and sync from yahoo: [clinet rev: %s, server rev: %s]\n",
                syncResult.getClientRev(), syncResult.getYahooRev());
        return syncResult;
    }

    private String constructLocalChangeXml(DataSourceMailbox mbox, Map<Integer, Change> localChanges, int clientRev)
            throws ServiceException {
        ContactChanges changes = new ContactChanges();

        for (Change change : localChanges.values()) {
            int itemId = change.getItemId();
            Type type = change.getType();
            switch (type) {
            case ADD:
                com.zimbra.cs.mailbox.Contact mboxContact = this.localData.getContact(itemId);
                Contact contact = ContactData.getYContactFromZcsContact(mboxContact, Action.ADD);
                contact.setOp(Action.ADD);
                String refId = UUID.randomUUID().toString();
                contact.setRefid(refId);
                this.localContactIdMap.put(refId, itemId);
                changes.addContactForAdd(contact);
                break;
            case UPDATE:
                mboxContact = this.localData.getContact(itemId);
                DataSourceItem mapping = this.localData.getMapping(itemId);
                int realRemoteId = RemoteId.parse(mapping.remoteId).getId();
                String xml = this.localData.getData(mapping);
                contact = Contact.extractFromXml(xml);
                contact.setOp(Action.UPDATE);
                List<Fields> delta = ContactData.delta(mboxContact, contact);
                changes.addUpdateFields(String.valueOf(realRemoteId), delta);
                break;
            case DELETE:
                DataSourceItem dsi = localData.getMapping(itemId);
                int remoteId = RemoteId.parse(dsi.remoteId).getId();
                changes.addContactdIdToRemove(String.valueOf(remoteId));
                break;
            }
        }
        return changes.toXml(clientRev);
    }

    private YContactSyncResult syncFromServer(DataSourceMailbox mbox, int clientRev) throws Exception {
        SyncRequest req;
        SyncResponse resp;
        try {
            req = new SyncRequest(OAuthManager.getOAuthToken(mbox.getAccountId()), clientRev);
            resp = req.send();
        } catch (Exception e) {
            req = new SyncRequest(OAuthManager.getRetryToken(mbox.getAccountId()), clientRev);
            resp = req.send();
        }
        ContactSync contactSync = new ContactSync();
        contactSync.setClientRev(clientRev); // need to have it so we know if
                                             // server updated
        boolean isServerUpdated = resp.extract(contactSync);
        if (!isServerUpdated) {
            OfflineLog.yab.debug("Yahoo contacts are up to date.");
            return contactSync;
        }

        OfflineLog.yab.debug("sync response from yahoo: [clinet rev: %s, server rev: %s]\n",
                contactSync.getClientRev(), contactSync.getYahooRev());
        Set<String> removeSet = new HashSet<String>();
        for (Contact contact : contactSync.getContacts()) {
            switch (contact.getOp()) {
            case ADD:
            case UPDATE:
                ContactData cd = null;
                try {
                    cd = new ContactData(contact);
                    this.contactOperations.add(new ContactAddOrUpdate(cd.getParsedContact(), contact, false));
                } catch (ServiceException e) {
                    SyncExceptionHandler.syncContactFailed(mbox, -1,
                            Xml.toString(contact.toXml(getContactSyncDocument())), e);
                    OfflineLog.yab.error("yahoo contact parsing error, %s",
                            Xml.toString(contact.toXml(getContactSyncDocument())));
                    if (cd != null)
                        OfflineLog.yab.error("error creating contact: " + cd.toString());
                }
                break;
            case REMOVE:
                String removeId = contact.getId();
                if (removeSet.add(removeId)) {
                    this.contactOperations.add(new ContactRemove(removeId));
                }
                break;
            }
        }
        removeSet = null;

        return contactSync;
    }

    private void updateDb(SyncState ss, DataSourceMailbox mbox, YContactSyncResult ySyncResult) throws ServiceException {
        if (this.contactOperations.isEmpty()) {
            return;
        }
        int addCount = 0;
        int updateCount = 0;

        // sync from server could also return "update" (contact has itemid), we
        // need sorting for lucene indexing
        for (ContactOperation contactOp : this.contactOperations) {
            if (contactOp.getOp() != Action.ADD) {
                String remoteId = RemoteId.contactId(Integer.parseInt(contactOp.getRemoteId())).toString();
                int itemId = this.localData.getReverseMapping(remoteId).itemId;
                contactOp.setItemId(itemId);
            }
        }
        Collections.sort(this.contactOperations);

        boolean success = false;
        try {
            mbox.beginTransaction("yahoo-contacts-dbupdate", null);
            for (ContactOperation contactOp : this.contactOperations) {
                switch (contactOp.getOp()) {
                case ADD:
                    if (contactOp.isPushOperation()) {
                        SyncResult result = (SyncResult) ySyncResult;
                        String refId = result.getRefIdByContactId(contactOp.getRemoteId());
                        if (refId != null) {
                            int itemId = this.localContactIdMap.get(refId);
                            this.updateContactAndAddMapping(itemId, contactOp.getParsedContact(),
                                    contactOp.getYContact());
                        } else {
                            OfflineLog.yab.debug("** refId is null, remoteId (%s)", contactOp.getRemoteId());
                        }
                    } else {
                        this.saveContact(contactOp.getParsedContact(), contactOp.getYContact());
                    }
                    addCount++;
                    break;
                case UPDATE:
                    this.updateContact(contactOp.getParsedContact(), contactOp.getYContact());
                    updateCount++;
                    break;
                case REMOVE:
                    this.deleteContact(contactOp.getItemId());
                    break;
                default:
                    break;
                }
            }

            ss.setLastRevision(String.valueOf(ySyncResult.getYahooRev()));
            ss.setLastModSequence(mbox.getLastChangeID());
            this.localData.saveState(ss);

            OfflineLog.yab.debug("SyncState after sync (persisted): %s", ss);
            OfflineLog.yab.info("Yahoo contacts synced, %d added, %d updated, %d deleted", addCount, updateCount,
                    this.contactOperations.size() - addCount - updateCount);
            success = true;
        } catch (Exception e) {
            throw new YContactException("exception raised when syncing contacts to db", "", false, e, null);
        } finally {
            mbox.endTransaction(success);
        }
    }

    private void deleteContact(int itemId) throws NumberFormatException, ServiceException {
        DataSourceItem dsi = this.localData.getMapping(itemId);
        if (dsi.folderId != 0) {
            this.localData.deleteContact(dsi.itemId);
            this.localData.deleteMapping(dsi.itemId, true);
        } else {
            OfflineLog.yab.debug("Yahoo contact itemId (%d) has already been deleted", itemId);
        }
    }

    private void updateContact(ParsedContact pc, Contact ycContact) throws NumberFormatException, ServiceException {
        String remoteId = RemoteId.contactId(Integer.parseInt(ycContact.getId())).toString();
        DataSourceItem dsi = this.localData.getReverseMapping(remoteId);
        if (dsi.itemId != 0) {
            this.localData.modifyContact(dsi.itemId, pc);
            this.localData.updateMapping(YCONTACT_FOLDER_ID, dsi.itemId, dsi.remoteId,
                    Xml.toString(ycContact.toXml(getContactSyncDocument())), true);
        } else {
            OfflineLog.yab.error("couldn't find update contact %s",
                    Xml.toString(ycContact.toXml(getContactSyncDocument())));
        }
    }

    private void saveContact(ParsedContact pc, Contact ycContact) throws ServiceException {
        String remoteId = RemoteId.contactId(Integer.parseInt(ycContact.getId())).toString();
        DataSourceItem sdi = this.localData.getReverseMapping(remoteId);
        if (sdi.itemId != 0) {
            OfflineLog.yab.debug("update existing contact: itemId(%d), remoteId(%s)", sdi.itemId, sdi.remoteId);
            this.localData.modifyContact(sdi.itemId, pc);
            this.localData.updateMapping(YCONTACT_FOLDER_ID, sdi.itemId, sdi.remoteId,
                    Xml.toString(ycContact.toXml(getContactSyncDocument())), true);
        } else {
            int itemId = this.localData.createContact(pc).getId();
            this.localData.updateMapping(YCONTACT_FOLDER_ID, itemId, remoteId,
                    Xml.toString(ycContact.toXml(getContactSyncDocument())), true);
        }
    }

    private void updateContactAndAddMapping(int itemId, ParsedContact pc, Contact ycContact) throws ServiceException {
        this.localData.modifyContact(itemId, pc); // should not need this if
                                                  // field mapping works perfect
        RemoteId rid = RemoteId.contactId(Integer.parseInt(ycContact.getId()));
        this.localData.updateMapping(YCONTACT_FOLDER_ID, itemId, rid.toString(),
                Xml.toString(ycContact.toXml(getContactSyncDocument())), true);
    }

    private static Document getContactSyncDocument() {
        Document doc = builder.newDocument();
        Element root = doc.createElement("contactsync");
        return root.getOwnerDocument();
    }

    public static void setMigrated() {
        isMigrated = true;
    }

    /**
     * delete existing yahoo contacts so they could be synced from server again
     * 
     * @param ds
     *            data source
     * @param mbox
     *            mail box
     * @throws ServiceException
     */
    public static void migrateExistingContacts(OfflineDataSource ds) throws ServiceException {
        if (isMigrated) {
            return;
        }
        Mailbox mbox = ds.getMailbox();
        Metadata md = mbox.getConfig(null, MIGRATE_FLAG);
        if (md != null) {
            isMigrated = true;
            return;
        }
        LocalData localData = new LocalData((OfflineDataSource) ds);
        SyncState ss = localData.loadState();
        OfflineLog.yab.debug("SyncState before migration: %s", ss);
        if (ss == null) {
            ss = new SyncState();
        }
        ss.setLastRevision("0");
        Collection<DataSourceItem> mappings = DbDataSource.getAllMappingsInFolder(ds, YAB_FOLDER_ID);
        List<Integer> delItemIds = new ArrayList<Integer>();
        for (DataSourceItem item : mappings) {
            delItemIds.add(item.itemId);
        }
        Collections.sort(delItemIds);
        boolean success = false;
        try {
            mbox.beginTransaction("yahoo-contacts-migrate", null);
            for (Integer itemId : delItemIds) {
                mbox.delete(null, itemId, MailItem.Type.CONTACT);
            }
            DbDataSource.deleteAllMappingsInFolder(ds, YAB_FOLDER_ID, true);
            md = new Metadata();
            md.put("migrated", "yes");
            mbox.setConfig(null, MIGRATE_FLAG, md);
            localData.saveState(ss);
            success = true;
        } catch (Exception e) {
            throw new YContactException("exception raised when migrating existing yahoo contacts", "", false, e, null);
        } finally {
            mbox.endTransaction(success);
        }
        isMigrated = true;
    }

    public static void skipMigration(Mailbox mbox) throws ServiceException {
        isMigrated = true;
        Metadata md = new Metadata();
        md.put("migrated", "no-need");
        mbox.setConfig(null, MIGRATE_FLAG, md);
    }
}
