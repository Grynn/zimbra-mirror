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
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Tag;
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
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.io.IOException;

import org.w3c.dom.Document;

public class SyncSession {
    private final LocalData localData;
    private final Session session;
    private Map<Integer, Change> tagChanges;
    private Map<Integer, Change> contactChanges;
    private Map<Integer, Category> pushedCategories;
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

    static {
        if (LOG.isDebugEnabled()) {
            Yab.enableDebug();
        }
    }
    
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
                processCategoryResults(req.getEvents());
                processContactResults(req.getEvents());
                List<SyncResponseEvent> events = res.getEvents();
                if (!events.isEmpty()) {
                    // Categories are not returned unless there are contact changes 
                    processCategories(res.getCategories());
                    processContactEvents(events);
                }
                ss.setLastModSequence(mbox.getLastChangeID());
                localData.saveState(ss);
                req = hasLocalChanges() ? getSyncRequest(res.getRevision()) : null;
            }
        }
    }

    private void getLocalChanges(SyncState ss) throws ServiceException {
        int seq = ss.getLastModSequence();
        tagChanges = localData.getTagChanges(seq);
        contactChanges = localData.getContactChanges(seq);
        LOG.debug("found %d local tag changes", tagChanges.size());
        LOG.debug("found %d local contact changes", contactChanges.size());
    }

    private boolean hasLocalChanges() {
        return !tagChanges.isEmpty() || !contactChanges.isEmpty();
    }
    
    private static int getLastRevision(SyncState ss) {
        String s = ss.getLastRevision();
        return s != null ? Integer.parseInt(s) : 0;
    }

    private SyncRequest getSyncRequest(int revision) throws ServiceException {
        SyncRequest req = session.createSyncRequest(revision);
        eventItemIds = new ArrayList<Integer>();
        pushedCategories = new HashMap<Integer, Category>();
        pushedContacts = new HashMap<Integer, Contact>();
        for (Change change : tagChanges.values()) {
            SyncRequestEvent event = getCategoryEvent(change);
            if (event != null) {
                req.addEvent(getCategoryEvent(change));
                eventItemIds.add(change.getItemId());
            }
        }
        for (Change change : contactChanges.values()) {
            SyncRequestEvent event = getContactEvent(change);
            if (event != null) {
                req.addEvent(getContactEvent(change));
                eventItemIds.add(change.getItemId());
            }
        }
        return req;
    }

    private SyncRequestEvent getCategoryEvent(Change change) throws ServiceException {
        int itemId = change.getItemId();
        if (change.isAdd()) {
            String name = localData.getTagName(itemId);
            pushedCategories.put(itemId, new Category(name));
            return SyncRequestEvent.addCategory(name);
        }
        if (change.isUpdate()) {
            Category category = getCategory(localData.getMapping(itemId));
            String newName = localData.getTagName(itemId);
            if (!newName.equals(category.getName())) {
                category.setName(newName);
                pushedCategories.put(itemId, category);
                return SyncRequestEvent.renameCategory(category.getId(), newName);
            }
        } else if (change.isDelete()) {
            DataSourceItem dsi = localData.getMapping(itemId);
            int catid = RemoteId.parse(dsi.remoteId).getValue();
            Category category = new Category(catid);
            pushedCategories.put(itemId, category);
            return SyncRequestEvent.removeCategory(catid);
        }
        return null;
    }

    private SyncRequestEvent getContactEvent(Change change) throws ServiceException {
        int itemId = change.getItemId();
        if (change.isAdd()) {
            Contact contact = getContactData(itemId).getContact();
            pushedContacts.put(itemId, contact);
            return SyncRequestEvent.addContact(contact);
        } else if (change.isUpdate()) {
            ContactData cd = getContactData(itemId);
            LOG.debug("count = " + cd.getCategories().size());
            Contact newContact = cd.getContact();
            Contact oldContact = getContact(localData.getMapping(itemId));
            LOG.debug("oldContact: " + oldContact);
            LOG.debug("newContact: " + newContact);
            newContact.setId(oldContact.getId());
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
        com.zimbra.cs.mailbox.Contact contact = localData.getContact(itemId);
        return new ContactData(contact, getCategories(contact));
    }

    private List<Category> getCategories(MailItem contact)
        throws ServiceException {
        List<Tag> tags = contact.getTagList();
        LOG.debug("tags for %d: %s", contact.getId(), tags);
        List<Category> categories = new ArrayList<Category>(tags.size());
        for (Tag tag : tags) {
            DataSourceItem dsi = localData.getMapping(tag.getId());
            if (dsi.md != null) {
                categories.add(new Category(getCategory(dsi).getId()));
            } else {
                categories.add(new Category(tag.getName()));
            }
        }
        LOG.debug("categories: " + categories.size());
        return categories;
    }

    private void processCategoryResults(List<SyncRequestEvent> events)
        throws ServiceException {
        Stats stats = new Stats();
        int errors = 0;
        for (int i = 0; i < events.size(); i++) {
            SyncRequestEvent event = events.get(i);
            if (event.isCategory()) {
                int itemId = eventItemIds.get(i);
                if (checkErrorResult(event, itemId)) {
                    processCategoryResult(event, itemId, stats);
                } else {
                    errors++;
                }
            }
        }
        LOG.debug("Pushed %d category changes: %s", stats.total(), stats);
        if (errors > 0) {
            LOG.debug("%d category changes failed due to errors", errors);
        }
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
    
    private void processCategoryResult(SyncRequestEvent event, int itemId, Stats stats)
        throws ServiceException {
        SuccessResult result = (SuccessResult) event.getResult();
        Category category = pushedCategories.get(itemId);
        if (event.isAddCategory()) {
            category.setId(result.getCategoryId());
            updateCategoryMapping(itemId, category);
            stats.added++;
        } else if (event.isRenameCategory()) {
            updateCategoryMapping(itemId, category);
            stats.updated++;
        } else if (event.isRemoveCategory()) {
            localData.deleteMapping(itemId);
            stats.deleted++;
        }
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
            localData.deleteMapping(itemId);
            stats.deleted++;
        }
    }

    private void processCategories(List<Category> categories)
        throws ServiceException {
        Stats stats = new Stats();
        int errors = 0;
        Set<Integer> tags = getCategoryTags();
        for (Category category : categories) {
            RemoteId rid = RemoteId.categoryId(category.getId());
            DataSourceItem dsi = localData.getReverseMapping(rid.toString());
            try {
                Tag tag = processCategory(category, dsi, stats);
                tags.remove(tag.getId());
            } catch (ServiceException e) {
                localData.syncContactFailed(e, dsi.itemId, category.toString());
                errors++;
            }
        }
        // Remaining categories have been deleted. Do not delete the
        // associated tag since we can't be sure that it is not also 
        // used by other non-contact items
        for (int itemId : tags) {
            localData.deleteMapping(itemId);
            stats.deleted++;
        }
        LOG.debug("Processed %d remote category changes: %s", stats.total(), stats);
        if (errors > 0) {
            LOG.debug("%d category changes could not be processed due to errors", errors);
        }
    }

    private Tag processCategory(Category category, DataSourceItem dsi, Stats stats)
        throws ServiceException {
        String name = category.getName();
        // Check for new or updated category
        if (dsi.itemId > 0) {
            Tag tag = localData.getTag(dsi.itemId);
            if (!tag.getName().equals(name)) {
                localData.renameTag(dsi.itemId, name);
                updateCategoryMapping(dsi.itemId, category);
                stats.updated++;
            }
            return tag;
        } else {
            Tag tag = localData.createTag(name);
            updateCategoryMapping(tag.getId(), category);
            stats.added++;
            return tag;
        }
    }
    
    /*
     * Returns item id's for all tags associated with mapped categories.
     */
    private Set<Integer> getCategoryTags() throws ServiceException {
        Collection<DataSourceItem> mappings =
            localData.getAllMappingsInFolder(Mailbox.ID_FOLDER_TAGS);
        Set<Integer> tags = new HashSet<Integer>(mappings.size());
        for (DataSourceItem dsi : mappings) {
            tags.add(dsi.itemId);
        }
        return tags;
    }

    private void processContactEvents(List<SyncResponseEvent> events)
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
                deleteContact(contact.getId(), dsi, stats);
            }
        }
    }

    private void addContact(Contact contact, Stats stats)
        throws ServiceException {
        int cid = contact.getId();
        ContactData cd = new ContactData(contact);
        if (!cd.isEmpty()) {
            ParsedContact pc = cd.getParsedContact();
            long bits = getTagBitmask(cd);
            int itemId = localData.createContact(pc, bits).getId();
            updateContactMapping(itemId, contact);
            stats.added++;
            LOG.debug("Created new local contact: itemId=%d, cid=%d, tagBits=%x",
                      itemId, cid, bits);
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
            long bits = getTagBitmask(cd);
            localData.modifyContact(dsi.itemId, cd.getParsedContact(), bits);
            updateContactMapping(dsi.itemId, contact);
            stats.updated++;
            LOG.debug("Modified local contact: itemId=%d, cid=%d, tagBits=%x",
                      dsi.itemId, cid, bits);
        } else {
            LOG.debug("Removing contact with cid %d since changes would " +
                      "result in an empty contact", cid);
            deleteContact(cid, dsi, stats);
        }
    }

    private void deleteContact(int cid, DataSourceItem dsi, Stats stats)
        throws ServiceException {
        localData.deleteContact(dsi.itemId);
        localData.deleteMapping(dsi.itemId);
        stats.deleted++;
        LOG.debug("Deleted contact: itemId=%d, cid=%d", dsi.itemId, cid);
    }

    /*
     * Returns tag bits for specified contact data.
     */
    private long getTagBitmask(ContactData cd) throws ServiceException {
        long bits = 0;
        for (Category category : cd.getCategories()) {
            RemoteId rid = RemoteId.categoryId(category.getId());
            DataSourceItem dsi = localData.getReverseMapping(rid.toString());
            if (dsi != null) {
                bits |= localData.getTag(dsi.itemId).getBitmask();
            }
        }
        return bits;
    }

    private void updateContactMapping(int itemId, Contact contact)
        throws ServiceException {
        RemoteId rid = RemoteId.contactId(contact.getId());
        localData.updateMapping(itemId, rid.toString(), toXml(contact));
    }

    private void updateCategoryMapping(int itemId, Category category)
        throws ServiceException {
        RemoteId rid = RemoteId.categoryId(category.getId());
        localData.updateMapping(itemId, rid.toString(), toXml(category));
    }

    private String toXml(Entity entity) {
        return session.toString(entity.toXml(session.createDocument()));
    }

    private Contact getContact(DataSourceItem dsi) throws ServiceException {
        return Contact.fromXml(getDocument(dsi).getDocumentElement());
    }

    private Category getCategory(DataSourceItem dsi) throws ServiceException {
        return Category.fromXml(getDocument(dsi).getDocumentElement());
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