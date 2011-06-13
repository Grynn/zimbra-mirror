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
import com.zimbra.cs.offline.util.yc.ContactChanges;
import com.zimbra.cs.offline.util.yc.ContactSync;
import com.zimbra.cs.offline.util.yc.Fields;
import com.zimbra.cs.offline.util.yc.PutRequest;
import com.zimbra.cs.offline.util.yc.PutResponse;
import com.zimbra.cs.offline.util.yc.SyncRequest;
import com.zimbra.cs.offline.util.yc.SyncResponse;
import com.zimbra.cs.offline.util.yc.SyncResult;
import com.zimbra.cs.offline.util.yc.YContactException;
import com.zimbra.cs.offline.util.yc.YContactSyncResult;
import com.zimbra.cs.offline.util.yc.oauth.OAuthManager;

public class YContactSync {

    private static DocumentBuilder builder = Xml.newDocumentBuilder();
    private static boolean isMigrated = false;
    private final LocalData localData;
    private Set<String> contactsToRemove = new HashSet<String>();
    private List<ParsedContact> parsedContacts = new ArrayList<ParsedContact>();
    private Map<String, Integer> localContactIdMap = new HashMap<String, Integer>();

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
            clientRev = Integer.parseInt(ss.getLastRevision());
        }
        int seq = ss.getLastModSequence();
        YContactSyncResult ysyncResult = null;
        Map<Integer, Change> localChanges = this.localData.getContactChanges(seq);
        if (clientRev == 0 || localChanges.isEmpty()) {
            ysyncResult = syncFromServer(mbox, clientRev);
        } else {
            ysyncResult = pushToServerAndSync(mbox, clientRev, localChanges);
        }

        updateDb(ss, mbox, ysyncResult);
    }

    private YContactSyncResult pushToServerAndSync(DataSourceMailbox mbox, int clientRev,
            Map<Integer, Change> localChanges) throws Exception {

        String reqBody = constructLocalChangeXml(mbox, localChanges, clientRev);
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

        for (String id : syncResult.getDiff().keySet()) {
            Contact contact = syncResult.getDiff().get(id);
            switch (contact.getOp()) {
            case ADD:
            case UPDATE:
                ContactData cd = new ContactData(contact);
                this.parsedContacts.add(cd.getParsedContact());
                break;
            case REMOVE:
                this.contactsToRemove.add(contact.getId());
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

        OfflineLog.yab.debug("sync from yahoo: [clinet rev: %s, server rev: %s]\n", contactSync.getClientRev(),
                contactSync.getYahooRev());

        for (Contact contact : contactSync.getContacts()) {
            switch (contact.getOp()) {
            case ADD:
            case UPDATE:
                ContactData cd = new ContactData(contact);
                ParsedContact pc = cd.getParsedContact();
                this.parsedContacts.add(pc);
                break;
            case REMOVE:
                this.contactsToRemove.add(contact.getId());
                break;
            }
        }

        return contactSync;
    }

    private void updateDb(SyncState ss, DataSourceMailbox mbox, YContactSyncResult ySyncResult)
            throws YContactException, ServiceException {
        if (ySyncResult.getContacts().isEmpty() && this.contactsToRemove.isEmpty()) {
            return;
        }
        boolean success = false;
        int addCount = 0;
        try {
            mbox.beginTransaction("yahoo-contacts-sync", null);
            int index = 0;
            for (Contact contact : ySyncResult.getContacts()) {
                switch (contact.getOp()) {
                case ADD:
                    if (ySyncResult.isPushResult()) {
                        SyncResult result = (SyncResult) ySyncResult;
                        if (!(result.containsContactId(contact.getId()))) {
                            this.saveContact(this.parsedContacts.get(index++), contact);
                            addCount++;
                        } else {
                            int itemId = this.localContactIdMap.get(result.getRefIdByContactId(contact.getId()));
                            this.updateContactAndAddMapping(itemId, this.parsedContacts.get(index++), contact);
                        }
                    } else {
                        this.saveContact(this.parsedContacts.get(index++), contact);
                        addCount++;
                    }
                    break;
                case UPDATE:
                    this.updateContact(this.parsedContacts.get(index++), contact);
                    break;
                case REMOVE:
                    break;
                }
            }

            for (String id : this.contactsToRemove) {
                this.deleteContact(id);
            }

            ss.setLastRevision(String.valueOf(ySyncResult.getYahooRev()));
            ss.setLastModSequence(mbox.getLastChangeID());
            this.localData.saveState(ss);

            OfflineLog.yab.debug("SyncState after sync (persisted): %s", ss);
            OfflineLog.yab.info("Yahoo contacts synced, %d added, %d updated, %d deleted", addCount,
                    this.parsedContacts.size() - addCount, this.contactsToRemove.size());

            success = true;
        } catch (Exception e) {
            throw new YContactException("exception raised when syncing contacts to db", "", false, e, null);
        } finally {
            mbox.endTransaction(success);
        }
    }

    private void deleteContact(String id) throws NumberFormatException, ServiceException {
        DataSourceItem dsi = this.localData.getReverseMapping(RemoteId.contactId(Integer.parseInt(id)).toString());
        this.localData.deleteContact(dsi.itemId);
        this.localData.deleteMapping(dsi.itemId, true);
    }

    private void updateContact(ParsedContact pc, Contact ycContact) throws NumberFormatException, ServiceException {
        DataSourceItem dsi = this.localData.getReverseMapping(RemoteId.contactId(Integer.parseInt(ycContact.getId()))
                .toString());
        this.localData.modifyContact(dsi.itemId, pc);
        this.localData.updateMapping(dsi.itemId, dsi.remoteId, Xml.toString(ycContact.toXml(getContactSyncDocument())),
                true);
    }

    private void saveContact(ParsedContact pc, Contact ycContact) throws ServiceException {
        int itemId = this.localData.createContact(pc).getId();
        RemoteId rid = RemoteId.contactId(Integer.parseInt(ycContact.getId()));
        this.localData.updateMapping(itemId, rid.toString(), Xml.toString(ycContact.toXml(getContactSyncDocument())),
                true);
    }

    private void updateContactAndAddMapping(int itemId, ParsedContact pc, Contact ycContact) throws ServiceException {
        this.localData.modifyContact(itemId, pc);
        RemoteId rid = RemoteId.contactId(Integer.parseInt(ycContact.getId()));
        this.localData.updateMapping(itemId, rid.toString(), Xml.toString(ycContact.toXml(getContactSyncDocument())),
                true);
    }

    private static Document getContactSyncDocument() {
        Document doc = builder.newDocument();
        Element root = doc.createElement("contactsync");
        return root.getOwnerDocument();
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
        Collection<DataSourceItem> mappings = DbDataSource.getAllMappingsInFolder(ds, -1);  //YAB hard coded -1 as folder id
        Mailbox mbox = ds.getMailbox();
        boolean success = false;
        try {
            mbox.beginTransaction("yahoo-contacts-migrate", null);
            for (DataSourceItem item : mappings) {
                mbox.delete(null, item.itemId, MailItem.Type.CONTACT);
            }
            DbDataSource.deleteAllMappings(ds);
        } catch (Exception e) {
            throw new YContactException("exception raised when migrating existing yahoo contacts", "", false, e, null);
        } finally {
            mbox.endTransaction(success);
        }
        isMigrated = true;
    }
}
