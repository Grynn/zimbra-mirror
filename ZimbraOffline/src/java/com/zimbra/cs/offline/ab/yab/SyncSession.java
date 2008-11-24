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
import com.zimbra.cs.offline.util.yab.Yab;
import com.zimbra.cs.offline.util.yab.SuccessResult;
import com.zimbra.cs.offline.util.yab.Category;
import com.zimbra.cs.offline.util.yab.Entity;
import com.zimbra.cs.offline.util.yab.ContactChange;
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
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.io.IOException;

import org.w3c.dom.Document;

public class SyncSession {
    private final LocalData localData;
    private final Session session;
    private Map<Integer, Change> contactChanges;
    private Map<Integer, Contact> pushedContacts;
    private List<Integer> eventItemIds;
    private Map<RemoteId, ContactGroup> contactGroups;

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

    public void sync() throws ServiceException {
        try {
            syncData();
        } catch (Exception e) {
            throw ServiceException.FAILURE("Contact sync error", e);
        }
    }
    
    private void syncData() throws IOException, ServiceException {
        Mailbox mbox = localData.getMailbox();
        SyncState ss = localData.loadState();
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
                    contactGroups = processCategories(res.getCategories());
                    processEvents(events);
                    localData.saveContactGroups(contactGroups.values());
                }
                ss.setLastModSequence(mbox.getLastChangeID());
                localData.saveState(ss);
                req = hasLocalChanges() ? getSyncRequest(res.getRevision()) : null;
            }
        }
    }

    private void getLocalChanges(SyncState ss) throws ServiceException {
        contactChanges = localData.getContactChanges(ss.getLastModSequence());
        LOG.debug("found %d local contact changes", contactChanges.size());
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
                req.addEvent(getContactEvent(change));
                eventItemIds.add(change.getItemId());
            }
        }
        return req;
    }

    private SyncRequestEvent getContactEvent(Change change) throws ServiceException {
        int itemId = change.getItemId();
        if (change.isAdd()) {
            Contact contact = getContactData(itemId).getContact();
            pushedContacts.put(itemId, contact);
            return SyncRequestEvent.addContact(contact);
        } else if (change.isUpdate()) {
            ContactData cd = getContactData(itemId);
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
            if (!cc.isEmpty()) {
                return SyncRequestEvent.updateContact(cc);
            }
        } else if (change.isDelete()) {
            DataSourceItem dsi = localData.getMapping(itemId);
            int cid = RemoteId.parse(dsi.remoteId).getValue();
            pushedContacts.put(itemId, new Contact(cid));
            return SyncRequestEvent.removeContact(cid);
        }
        return null;
    }

    private ContactData getContactData(int itemId) throws ServiceException {
        return new ContactData(localData.getContact(itemId));
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
            String msg = String.format(
                "Error code: %s\nError description: %s\nRequest event:\n%s",
                error.getCode(), error.getUserMessage(), event);
            localData.syncContactFailed(
                new SyncException("Sync request event failed"), itemId, msg);
            return false;
        }
        return true;
    }
    
    private void processContactResult(SyncRequestEvent event, int itemId, Stats stats)
        throws ServiceException {
        SuccessResult result = (SuccessResult) event.getResult();
        Contact contact = pushedContacts.get(itemId);
        if (event.isAddContact()) {
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

    private Map<RemoteId, ContactGroup> processCategories(List<Category> categories)
        throws ServiceException {
        Stats stats = new Stats();
        int errors = 0;
        Map<RemoteId, ContactGroup> groups = getContactGroups();
        Set<Integer> received = new HashSet<Integer>();
        for (Category category : categories) {
            RemoteId rid = RemoteId.categoryId(category.getId());
            String name = category.getName();
            ContactGroup group = groups.get(rid);
            try {
                if (group != null) {
                    received.add(group.getId());
                    if (!name.equals(group.getName())) {
                        group.setName(name);
                        group.modify();
                        stats.updated++;
                        LOG.debug("Updated contact group: %s", group);
                    }
                } else {
                    group = localData.createContactGroup(
                        Mailbox.ID_FOLDER_CONTACTS, name);
                    received.add(group.getId());
                    localData.updateMapping(group.getId(), rid.toString(), null);
                    groups.put(rid, group);
                    stats.added++;
                    LOG.debug("Added new contact group: %s", group);
                }
            } catch (ServiceException e) {
                localData.syncContactFailed(e, -1, category.toString());
                errors++;
            }
        }
        // Unseen categories / groups have been deleted
        Iterator<ContactGroup> it = groups.values().iterator();
        while (it.hasNext()) {
            ContactGroup group = it.next();
            int id = group.getId();
            if (!received.contains(id)) {
                // Delete group
                try {
                    localData.deleteContactGroup(id);
                    localData.deleteMapping(id);
                } catch (ServiceException e) {
                    localData.syncContactFailed(e, id, null);
                    errors++;
                }
                it.remove();
                stats.deleted++;
            }
        }
        LOG.debug("Processed %d remote category changes: %s", stats.total(), stats);
        if (errors > 0) {
            LOG.debug("%d category changes could not be processed due to errors", errors);
        }
        return groups;
    }

    private Map<RemoteId, ContactGroup> getContactGroups() throws ServiceException {
        Map<RemoteId, ContactGroup> groups = new HashMap<RemoteId, ContactGroup>();
        for (DataSourceItem dsi : getGroupMappings()) {
            RemoteId rid = RemoteId.parse(dsi.remoteId);
            assert rid.isCategory();
            groups.put(rid, localData.getContactGroup(dsi.itemId));
        }
        return groups;
    }

    private Collection<DataSourceItem> getGroupMappings() throws ServiceException {
        return localData.getMappingsForRemoteIdPrefix(
            Mailbox.ID_FOLDER_CONTACTS, RemoteId.CATEGORY_PREFIX);
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
            updateContactGroups(itemId, contact, null);
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
            localData.modifyContact(dsi.itemId, cd.getParsedContact());
            updateContactMapping(dsi.itemId, contact);
            updateContactGroups(dsi.itemId, contact, getContact(dsi));
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
        // Remove contact from any contact groups
        for (ContactGroup group : contactGroups.values()) {
            group.removeContact(itemId);
        }
        stats.deleted++;
        LOG.debug("Deleted contact with item id %d", itemId);
    }

    private void updateContactMapping(int itemId, Contact contact)
        throws ServiceException {
        RemoteId rid = RemoteId.contactId(contact.getId());
        localData.updateMapping(itemId, rid.toString(), toXml(contact));
    }

    private void updateContactGroups(int itemId, Contact newContact,
                                                 Contact oldContact)
        throws SyncException {
        Set<RemoteId> newIds = getCategoryIds(newContact);
        Set<RemoteId> oldIds = getCategoryIds(oldContact);
        for (RemoteId id : newIds) {
            if (!oldIds.contains(id)) {
                // Contact added to category / group
                ContactGroup group = contactGroups.get(id);
                if (group != null) {
                    LOG.debug("Added contact id %d to group '%s' id %d",
                              itemId, group.getName(), group.getId());
                    group.addContact(itemId);
                } else {
                    LOG.warn("Contact group with rid %d does not exist", id.getValue());
                }
            }
        }
        for (RemoteId id : oldIds) {
            if (!newIds.contains(id)) {
                // Contact removed from (possibly deleted) category / group
                ContactGroup group = contactGroups.get(id);
                if (group != null) {
                    group.removeContact(itemId);
                    LOG.debug("Removed contact id %d from group '%s' id %d",
                              itemId, group.getName(), group.getId());
                }
            }
        }
    }

    private Set<RemoteId> getCategoryIds(Contact contact) {
        Set<RemoteId> ids = new HashSet<RemoteId>();
        if (contact != null) {
            for (Category category : contact.getCategories()) {
                ids.add(RemoteId.categoryId(category.getId()));
            }
        }
        return ids;
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