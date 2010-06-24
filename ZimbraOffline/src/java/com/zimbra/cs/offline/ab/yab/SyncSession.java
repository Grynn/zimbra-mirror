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
package com.zimbra.cs.offline.ab.yab;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.offline.util.yab.Session;
import com.zimbra.cs.offline.util.yab.Contact;
import com.zimbra.cs.offline.util.yab.SyncResponse;
import com.zimbra.cs.offline.util.yab.SyncResponseEvent;
import com.zimbra.cs.offline.util.yab.SyncRequest;
import com.zimbra.cs.offline.util.yab.SyncRequestEvent;
import com.zimbra.cs.offline.util.yab.Result;
import com.zimbra.cs.offline.util.yab.ErrorResult;
import com.zimbra.cs.offline.util.yab.SuccessResult;
import com.zimbra.cs.offline.util.yab.Category;
import com.zimbra.cs.offline.util.yab.Entity;
import com.zimbra.cs.offline.util.yab.ContactChange;
import com.zimbra.cs.offline.util.yab.Field;
import com.zimbra.cs.offline.util.yab.SimpleField;
import com.zimbra.cs.offline.util.yab.YabException;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.ab.LocalData;
import com.zimbra.cs.offline.ab.SyncState;
import com.zimbra.cs.offline.ab.Change;
import com.zimbra.cs.offline.ab.SyncException;
import com.zimbra.cs.offline.ab.ContactGroup;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;

import java.util.Map;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;

import org.w3c.dom.Document;

public class SyncSession {
    private final LocalData localData;
    private final Session session;
    private Map<Integer, Change> contactChanges;
    private Map<Integer, Contact> pushedContacts;
    private List<Integer> eventItemIds;

    private static class Stats {
        int added, updated, deleted;

        int total() { return added + updated + deleted; }
        
        public String toString() {
            return String.format("%d added, %d updated, and %d deleted",
                                 added, updated, deleted);
        }
    }

    private static final Log LOG = OfflineLog.yab;

    public SyncSession(DataSource ds, Session session) throws ServiceException {
        localData = new LocalData((OfflineDataSource) ds);
        this.session = session;
    }

    public void sync() throws IOException, YabException, ServiceException {
        SyncState ss = localData.loadState();
        if (ss == null) {
            LOG.info("Sync state version change - resetting contact data");
            reset();
            return;
        }
        Mailbox mbox = localData.getMailbox();
        SyncRequest req;
        synchronized (mbox) {
            getLocalChanges(ss);
            ss.setLastModSequence(mbox.getLastChangeID());
            req = getSyncRequest(getLastRevision(ss));
        }
        while (req != null) {
            SyncResponse res = (SyncResponse) req.send();
            synchronized (mbox) {
                getLocalChanges(ss);
                ss.setLastRevision(String.valueOf(res.getRevision()));
                processContactResults(req.getEvents());
                List<SyncResponseEvent> events = res.getEvents();
                if (!events.isEmpty()) {
                    // Categories are not returned unless there are contact changes
                    processEvents(events);
                    processCategories(res.getCategories());
                }
                ss.setLastModSequence(mbox.getLastChangeID());
                localData.saveState(ss);
                req = hasLocalChanges() ? getSyncRequest(res.getRevision()) : null;
            }
        }
    }

    /*
     * Reset address book data. Replace local with remote contact data, but
     * preserve non-Yahoo related contact fields. Also, remove all local
     * contacts which have been deleted remotely, and reset sync state.
     *
     * Reset is done after a metadata version change or irrecoverable sync
     * error. Reset does not depend on metadata format since the data is
     * completely replaced.
     */
    private void reset() throws IOException, YabException, ServiceException {
        // Fetch all remote contacts and merge with local data
        Mailbox mbox = localData.getMailbox();
        contactChanges = Collections.emptyMap();
        SyncRequest req = getSyncRequest(0);
        SyncResponse res = (SyncResponse) req.send();
        synchronized (mbox) {
            processContactResults(req.getEvents());
            List<SyncResponseEvent> events = res.getEvents();
            if (!events.isEmpty()) {
                // Categories are not returned unless there are contact changes
                processEvents(events);
                processCategories(res.getCategories());
            }
            // Remove contacts which no longer exist remotely
            localData.deleteMissingContacts(getRemoteIds(events));
            SyncState ss = new SyncState();
            // Save new sync state 
            ss.setLastRevision(String.valueOf(res.getRevision()));
            ss.setLastModSequence(mbox.getLastChangeID());
            localData.saveState(ss);
        }
    }

    private static Set<String> getRemoteIds(List<SyncResponseEvent> events) {
        Set<String> ids = new HashSet<String>(events.size());
        for (SyncResponseEvent event : events) {
            if (event.isAddContact() || event.isUpdateContact()) {
                ids.add(RemoteId.contactId(event.getContactId()).toString());
            }
        }
        return ids;
    }
    
    private void getLocalChanges(SyncState ss) throws ServiceException {
        contactChanges = localData.getContactChanges(ss.getLastModSequence());
        LOG.debug("Found %d local contact changes", contactChanges.size());
    }

    private boolean hasLocalChanges() {
        return !contactChanges.isEmpty();
    }
    
    private static int getLastRevision(SyncState ss) {
        String s = ss.getLastRevision();
        return s != null ? Integer.parseInt(s) : 0;
    }

    private SyncRequest getSyncRequest(int revision) throws ServiceException {
        SyncRequest req = session.createSyncRequest(revision);
        eventItemIds = new ArrayList<Integer>();
        pushedContacts = new HashMap<Integer, Contact>();
        for (Change change : contactChanges.values()) {
            SyncRequestEvent event = getContactEvent(change);
            if (event != null) {
                req.addEvent(event);
                eventItemIds.add(change.getItemId());
            }
        }
        return req;
    }

    private SyncRequestEvent getContactEvent(Change change) throws ServiceException {
        int itemId = change.getItemId();
        if (change.isAdd() || change.isUpdate()) {
            com.zimbra.cs.mailbox.Contact zcontact = localData.getContact(itemId);
            if (ContactGroup.isContactGroup(zcontact)) {
                // Delete mapping so contact group will no longer be sync'd
                localData.deleteMapping(itemId);
            } else {
                ContactData cd = new ContactData(zcontact);
                if (change.isAdd()) {
                    Contact contact = cd.getContact();
                    pushedContacts.put(itemId, contact);
                    return SyncRequestEvent.addContact(contact);
                } else {
                    //LOG.debug("count = " + cd.getCategories().size());
                    Contact newContact = cd.getContact();
                    Contact oldContact = getContact(localData.getMapping(itemId));
                    //LOG.debug("oldContact: " + oldContact);
                    //LOG.debug("newContact: " + newContact);
                    newContact.setId(oldContact.getId());
                    // Add categories so we can track updates
                    for (Category category : oldContact.getCategories()) {
                        newContact.addCategory(category);
                    }
                    pushedContacts.put(itemId, newContact);
                    ContactChange cc = cd.getContactChange(oldContact);
                    return cc.isEmpty() ? null : SyncRequestEvent.updateContact(cc);
                }
            }
        } else if (change.isDelete()) {
            DataSourceItem dsi = localData.getMapping(itemId);
            RemoteId rid = RemoteId.parse(dsi.remoteId);
            if (rid.isContact()) {
                pushedContacts.put(itemId, new Contact(rid.getId()));
                return SyncRequestEvent.removeContact(rid.getId());
            } else {
                // Remove mapping for deleted contact group
                localData.deleteMapping(rid.getId());
            }
        }
        return null;
    }

    private void processContactResults(List<SyncRequestEvent> events)
        throws ServiceException {
        Stats stats = new Stats();
        int errors = 0;
        for (int i = 0; i < events.size(); i++) {
            SyncRequestEvent event = events.get(i);
            if (event.isContact()) {
                int itemId = eventItemIds.get(i);
                if (checkErrorResult(event, itemId)) {
                    processContactResult(event, itemId, stats);
                } else {
                    errors++;
                }
            }
        }
        LOG.debug("Pushed %d contact changes: %s", stats.total(), stats);
        if (errors > 0) {
            LOG.debug("%d contact changes failed due to errors", errors);
        }
    }

    private boolean checkErrorResult(SyncRequestEvent event, int itemId)
        throws ServiceException {
        Result result = event.getResult();
        if (result.isError()) {
            ErrorResult error = (ErrorResult) result;
            if (!ignoreError(error, event)) {
                String msg = String.format(
                    "Error code: %s\nError description: %s\nRequest event:\n%s",
                    error.getCode(), error.getUserMessage(), event);
                localData.syncContactFailed(
                    new SyncException("Sync request event failed"), itemId, msg);
                return false;
            }
        }
        return true;
    }

    private boolean ignoreError(ErrorResult error, SyncRequestEvent event) {
        if ((event.isRemoveContact() || event.isUpdateContact()) && error.getCode() == ErrorResult.CODE_CONTACT_DOES_NOT_EXIST)
            return true;
        return false;
    }

    private void processContactResult(SyncRequestEvent event, int itemId, Stats stats)
        throws ServiceException {
        Contact contact = pushedContacts.get(itemId);
        if (event.isAddContact()) {
            SuccessResult result = (SuccessResult) event.getResult();
            contact.setId(result.getContactId());
            updateContactMapping(itemId, contact);
            stats.added++;
        } else if (event.isUpdateContact()) {
            updateContactMapping(itemId, contact);
            stats.updated++;
        } else if (event.isRemoveContact()) {
            deleteContact(itemId, stats);
        }
    }

    private void processCategories(List<Category> categories)
        throws ServiceException {
        Stats stats = new Stats();
        // Get all existing contact groups
        Map<Integer, ContactGroup> groups = new HashMap<Integer, ContactGroup>();
        for (Category cat : categories) {
            groups.put(cat.getId(), new ContactGroup(cat.getName()));
        }
        // Get all contact mappings and update contact group dlist information
        Collection<DataSourceItem> mappings = localData.getAllContactMappings();
        for (DataSourceItem dsi : mappings) {
            if (dsi.remoteId != null) {
                RemoteId rid = RemoteId.parse(dsi.remoteId);
                if (rid.isContact()) {
                    Contact contact = getContact(dsi);
                    if (contact != null) {
                        List<Category> cats = contact.getCategories();
                        if (!cats.isEmpty()) {
                            List<String> emails = getEmailAddresses(contact);
                            if (!emails.isEmpty()) {
                                for (Category cat : cats) {
                                    ContactGroup group = groups.get(cat.getId());
                                    if (group != null) {
                                        group.addEmail(emails.get(0));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Remove contact groups deleted remotely
        for (DataSourceItem dsi : mappings) {
            if (dsi.remoteId != null) {
                RemoteId rid = RemoteId.parse(dsi.remoteId);
                if (rid.isCategory() && !groups.containsKey(rid.getId())) {
                    localData.deleteContactGroup(dsi.itemId);
                    groups.remove(rid.getId());
                    stats.deleted++;
                }
            }
        }
        // Create new or update existring contact groups
        for (Map.Entry<Integer, ContactGroup> me : groups.entrySet()) {
            RemoteId rid = RemoteId.categoryId(me.getKey());
            updateGroup(rid.toString(), me.getValue(), stats);
        }
        LOG.debug("Processed remote category changes: %s", stats);
    }

    private void updateGroup(String remoteId, ContactGroup group, Stats stats)
        throws ServiceException {
        DataSourceItem dsi = localData.getReverseMapping(remoteId);
        if (dsi.itemId > 0) {
            if (group.isEmpty()) {
                localData.deleteContactGroup(dsi.itemId);
                stats.deleted++;
            } else {
                ContactGroup oldGroup = localData.getContactGroup(dsi.itemId);
                if (!group.equals(oldGroup)) {
                    localData.modifyContactGroup(dsi.itemId, group);
                    stats.updated++;
                }
            }
        } else if (!group.isEmpty()) {
            localData.createContactGroup(remoteId, group);
            stats.added++;
        }
    }
    
    private List<String> getEmailAddresses(Contact contact) {
        List<String> emails = new ArrayList<String>();
        for (Field field : contact.getFields()) {
            if (field.isSimple()) {
                SimpleField simple = (SimpleField) field;
                if (simple.isEmail()) {
                    emails.add(simple.getValue());
                }
            }
        }
        return emails;
    }
    
    private void processEvents(List<SyncResponseEvent> events)
        throws ServiceException {
        Stats stats = new Stats();
        int errors = 0;
        for (SyncResponseEvent event : events) {
            if (!event.isAddressBookReset()) {
                try {
                    processEvent(event, stats);
                } catch (ServiceException e) {
                    String msg = String.format("Contact event:\n%s", event);
                    localData.syncContactFailed(e, -1, msg);
                    errors++;
                }
            }
        }
        LOG.debug("Processed %d remote contact changes: %s", stats.total(), stats);
        if (errors > 0) {
            LOG.debug("%d contact changes could not be processed due to errors", errors);
        }
    }

    private void processEvent(SyncResponseEvent event, Stats stats)
        throws ServiceException {
        Contact contact = event.getContact();
        RemoteId rid = RemoteId.contactId(contact.getId());
        DataSourceItem dsi = localData.getReverseMapping(rid.toString());
        if (event.isAddContact() || event.isUpdateContact()) {
            if (dsi.itemId > 0) {
                // Don't update contact if it has been modified locally since
                // sync request was sent. Wait until we send the new changes
                // before updating the local contact.
                if (!contactChanges.containsKey(dsi.itemId)) {
                    updateContact(contact, dsi, stats);
                }
            } else {
                addContact(contact, stats);
            }
        } else if (event.isRemoveContact()) {
            if (dsi.itemId > 0) {
                deleteContact(dsi.itemId, stats);
            }
        }
    }

    private void addContact(Contact contact, Stats stats) throws ServiceException {
        int cid = contact.getId();
        ContactData cd = new ContactData(contact);
        if (!cd.isEmpty()) {
            ParsedContact pc = cd.getParsedContact();
            int itemId = localData.createContact(pc).getId();
            updateContactMapping(itemId, contact);
            stats.added++;
            LOG.debug("Created new local contact: itemId=%d, cid=%d", itemId, cid);
        } else {
            LOG.debug("Not adding contact with cid %d since it would " +
                      "result in an empty contact", cid);
        }
    }

    private void updateContact(Contact contact, DataSourceItem dsi, Stats stats)
        throws ServiceException {
        int cid = contact.getId();
        ContactData cd = new ContactData(contact);
        if (!cd.isEmpty()) {
            ParsedContact pc = new ParsedContact(localData.getContact(dsi.itemId));
            cd.modifyParsedContact(pc);
            localData.modifyContact(dsi.itemId, pc);
            updateContactMapping(dsi.itemId, contact);
            stats.updated++;
            LOG.debug("Modified local contact: itemId=%d, cid=%d", dsi.itemId, cid);
        } else {
            LOG.debug("Removing contact with cid %d since changes would " +
                      "result in an empty contact", cid);
            deleteContact(dsi.itemId, stats);
        }
    }

    private void deleteContact(int itemId, Stats stats)
        throws ServiceException {
        localData.deleteContact(itemId);
        localData.deleteMapping(itemId);
        stats.deleted++;
        LOG.debug("Deleted contact with item id %d", itemId);
    }

    private void updateContactMapping(int itemId, Contact contact)
        throws ServiceException {
        RemoteId rid = RemoteId.contactId(contact.getId());
        localData.updateMapping(itemId, rid.toString(), toXml(contact));
    }

    private String toXml(Entity entity) {
        return session.toString(entity.toXml(session.createDocument()));
    }

    private Contact getContact(DataSourceItem dsi) throws ServiceException {
        return Contact.fromXml(getDocument(dsi).getDocumentElement());
    }

    private Document getDocument(DataSourceItem dsi) throws ServiceException {
        String xml = localData.getData(dsi);
        if (xml != null) {
            try {
                return session.parseDocument(xml);
            } catch (IOException e) {
                throw new SyncException(
                    "Unable to parse entry data for item id: " + dsi.itemId, null);
            }
        }
        throw new SyncException("Missing data source item for id: " + dsi.itemId);
    }
}
