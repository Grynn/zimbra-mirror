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
package com.zimbra.cs.offline.ab.gab;

import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.SyncExceptionHandler;
import com.zimbra.cs.mailbox.DesktopMailbox;
import com.zimbra.cs.mailbox.Tag;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.db.DbDataSource;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;
import com.zimbra.common.util.Log;
import com.zimbra.common.service.ServiceException;
import com.google.gdata.client.http.HttpGDataRequest;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.io.IOException;

public class SyncSession {
    private final DataSource ds;
    private final Mailbox mbox;
    private final GabService service;

    private enum ChangeType {
        ADD, UPDATE, DELETE
    }

    private static class Stats {
        int added, updated, deleted;

        public String toString() {
            return String.format(
                "%d added, %d updated, and %d deleted", added, updated, deleted);
        }
    }

    private static final Log LOG = OfflineLog.gab;

    private static final String KEY_GAB_ENTRY = "GAB_CONTACT";

    private static final boolean DEBUG_TRACE = true;
    private static final boolean HTTP_DEBUG = false;

    public static final Mailbox.OperationContext CONTEXT =
        new OfflineMailbox.OfflineContext();

    private static final Set<Integer> CONTACT_FOLDERS =
        new HashSet<Integer>(Arrays.asList(Mailbox.ID_FOLDER_CONTACTS));
    
    static {
        if (HTTP_DEBUG) {
            Logger httpLogger = Logger.getLogger(HttpGDataRequest.class.getName());
            httpLogger.setLevel(Level.ALL);
            //Logger xmlLogger = Logger.getLogger(XmlParser.class.getName());
            // Create a log handler which prints all log events to the console.
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(Level.ALL);
            httpLogger.addHandler(handler);
            //xmlLogger.addHandler(handler);
        }
    }

    public SyncSession(DataSource ds) throws ServiceException {
        this.ds = ds;
        mbox = ds.getMailbox();
        service = new GabService(ds.getUsername(), ds.getDecryptedPassword());
    }

    public void sync() throws ServiceException {
        try {
            syncContacts();
        } catch (Exception e) {
            throw ServiceException.FAILURE("Google contact sync error", e);
        }
    }

    private void syncContacts() throws IOException, ServiceException {
        SyncState state = new SyncState(mbox);
        // Get remote changes since last sync
        DateTime currentTime = DateTime.now();
        DateTime lastSyncTime = state.getLastSyncTime();
        ContactFeed contacts = service.getContacts(lastSyncTime, currentTime);
        ContactGroupFeed groups = service.getGroups(lastSyncTime, currentTime);
        state.setLastSyncTime(currentTime);
        List<SyncRequest> groupRequests;
        List<SyncRequest> contactRequests;
        int seq = state.getLastModSequence();
        synchronized (mbox) {
            // Get local changes sync last sync
            Map<Integer, ChangeType> groupChanges = getLocalGroupChanges(seq);
            Map<Integer, ChangeType> contactChanges = getLocalContactChanges(seq);
            // Process remote changes
            processGroupChanges(groups.getEntries(), groupChanges);
            processContactChanges(contacts.getEntries(), contactChanges);
            // Process local changes and determine changes to push
            groupRequests = processLocalGroupChanges(groupChanges);
            contactRequests = processLocalContactChanges(contactChanges);
            state.setLastModSequence(mbox.getLastChangeID());
        }
        // Push local changes to remote
        pushGroupChanges(groupRequests);
        pushContactChanges(contactRequests);
        int errors = groupRequests.size() + contactRequests.size();
        if (errors > 0) {
            LOG.debug("Contact sync had %d errors", errors);
        }
        state.save(mbox);
    }

    private void processGroupChanges(List<ContactGroupEntry> entries,
                                     Map<Integer, ChangeType> changes)
        throws ServiceException, IOException {
        LOG.debug("Processing %d remote contact group changes", entries.size());
        Stats stats = new Stats();
        for (ContactGroupEntry entry : entries) {
            DataSourceItem dsi = DbDataSource.getReverseMapping(ds, entry.getId());
            try {
                processGroupChange(entry, dsi, changes, stats);
            } catch (ServiceException e) {
                syncContactFailed(e, dsi.itemId, entry);
            }
        }
        LOG.debug("Processed remote contact group changes: " + stats);
    }

    private void processGroupChange(ContactGroupEntry entry,
                                    DataSourceItem dsi,
                                    Map<Integer, ChangeType> changes,
                                    Stats stats) throws ServiceException {
        if (isTraceEnabled()) {
            LOG.debug("Processing remote group entry:\n%s", service.pp(entry));
        }
        int itemId = dsi.itemId;
        boolean deleted = entry.getDeleted() != null;
        if (itemId > 0) {
            // Contact group updated or deleted
            if (deleted) {
                // Don't delete tag since we can't be sure that it is not
                // also used by other non-contact items
                deleteEntry(itemId);
                changes.remove(itemId);
                stats.deleted++;
            } else if (!changes.containsKey(itemId)) {
                String newName = getName(entry);
                String oldName = getName(getEntry(dsi, ContactGroupEntry.class));
                if (!newName.equals(oldName)) {
                    // Contact group was renamed...
                    mbox.rename(CONTEXT, itemId, MailItem.TYPE_TAG, newName,
                                Mailbox.ID_FOLDER_TAGS);
                    stats.updated++;
                }
                updateEntry(itemId, entry);
            }
        } else if (!deleted) {
            // Contact group was added
            Tag tag = createTag(getName(entry));
            updateEntry(tag.getId(), entry);
            stats.added++;
        }
    }

    private Tag createTag(String name) throws ServiceException {
        String normalized = MailItem.normalizeItemName(name);
        if (!name.equals(normalized)) {
            LOG.warn("Normalizing tag name '%s' to '%s' since it contains" +
                     "invalid tag characters", name, normalized);
            name = normalized;
        }
        try {
            return mbox.getTagByName(name);
        } catch (MailServiceException.NoSuchItemException e) {
            return mbox.createTag(CONTEXT, name, Tag.DEFAULT_COLOR);
        }
    }
    
    private static String getName(BaseEntry entry) {
        return entry.getTitle().getPlainText();
    }
    
    private void processContactChanges(List<ContactEntry> entries,
                                       Map<Integer, ChangeType> changes)
        throws ServiceException, IOException {
        LOG.debug("Processing %d remote contact changes", entries.size());
        Stats stats = new Stats();
        for (ContactEntry entry : entries) {
            DataSourceItem dsi = DbDataSource.getReverseMapping(ds, entry.getId());
            try {
                processContactChange(entry, dsi, changes, stats);
            } catch (ServiceException e) {
                syncContactFailed(e, dsi.itemId, entry);
            }
        }
        LOG.debug("Processed remote contact changes: " + stats);
    }

    private void processContactChange(ContactEntry entry,
                                      DataSourceItem dsi,
                                      Map<Integer, ChangeType> changes,
                                      Stats stats) throws ServiceException {
        if (isTraceEnabled()) {
            LOG.debug("Processing remote contact entry:\n%s", service.pp(entry));
        }
        int itemId = dsi.itemId;
        boolean deleted = entry.getDeleted() != null;
        if (itemId > 0) {
            // Contact updated or deleted
            if (deleted) {
                mbox.delete(CONTEXT, itemId, MailItem.TYPE_CONTACT);
                deleteEntry(itemId);
                changes.remove(itemId);
                stats.deleted++;
            } else if (!changes.containsKey(itemId)) {
                // Remote contact was updated with no local change
                String url = getEditUrl(getEntry(dsi, ContactEntry.class));
                if (!getEditUrl(entry).equals(url)) {
                    // Only update local entry if edit url has changed
                    // (avoids modifying contacts which we just pushed)
                    ContactData cd = new ContactData(entry);
                    mbox.modifyContact(CONTEXT, itemId, cd.getParsedContact());
                    setTags(itemId, entry);
                    updateEntry(itemId, entry);
                    stats.updated++;
                }
            }
        } else if (!deleted) {
            // New contact added
            ContactData cd = new ContactData(entry);
            Contact contact = mbox.createContact(
                CONTEXT, cd.getParsedContact(), Mailbox.ID_FOLDER_CONTACTS, null);
            setTags(contact.getId(), entry);
            updateEntry(contact.getId(), entry);
            stats.added++;
        }
    }

    private void setTags(int itemId, ContactEntry entry) throws ServiceException {
        long mask = getTagBitmask(entry);
        LOG.debug("Setting tags for item id %d: %x", itemId, mask);
        mbox.setTags(CONTEXT, itemId, MailItem.TYPE_CONTACT,
                     MailItem.FLAG_UNCHANGED, mask);
    }

    private static String getEditUrl(BaseEntry entry) {
        return entry.getEditLink().getHref();
    }

    private List<SyncRequest> processLocalGroupChanges(Map<Integer, ChangeType> changes)
        throws ServiceException {
        List<SyncRequest> reqs = new ArrayList<SyncRequest>();
        for (Map.Entry<Integer, ChangeType> entry : changes.entrySet()) {
            reqs.add(getGroupSyncRequest(entry.getKey(), entry.getValue()));
        }
        return reqs;
    }

    private List<SyncRequest> processLocalContactChanges(Map<Integer, ChangeType> changes)
        throws ServiceException {
        List<SyncRequest> reqs = new ArrayList<SyncRequest>();
        for (Map.Entry<Integer, ChangeType> entry : changes.entrySet()) {
            reqs.add(getContactSyncRequest(entry.getKey(), entry.getValue()));
        }
        return reqs;
    }
    
    private SyncRequest getContactSyncRequest(int itemId, ChangeType type)
        throws ServiceException {
        ContactEntry entry;
        switch (type) {
        case ADD:
            entry = getContactData(itemId).newContactEntry();
            // setGroupInfo(entry, itemId);
            return SyncRequest.insert(this, itemId, entry);
        case UPDATE:
            entry = getEntry(itemId, ContactEntry.class);
            getContactData(itemId).updateContactEntry(entry);
            // setGroupInfo(entry, itemId);
            return SyncRequest.update(this, itemId, entry);
        case DELETE:
            entry = getEntry(itemId, ContactEntry.class);
            return SyncRequest.delete(this, itemId, entry);
        default:
            throw new AssertionError();
        }
    }

    private SyncRequest getGroupSyncRequest(int itemId, ChangeType type)
        throws ServiceException {
        ContactGroupEntry entry;
        switch (type) {
        case ADD:
            entry = new ContactGroupEntry();
            entry.setTitle(new PlainTextConstruct(
                mbox.getTagById(CONTEXT, itemId).getName()));
            return SyncRequest.insert(this, itemId, entry);
        case UPDATE:
            entry = getEntry(itemId, ContactGroupEntry.class);
            entry.setTitle(new PlainTextConstruct(
                mbox.getTagById(CONTEXT, itemId).getName()));
            return SyncRequest.update(this, itemId, entry);
        case DELETE:
            entry = getEntry(itemId, ContactGroupEntry.class);
            return SyncRequest.delete(this, itemId, entry);
        default:
            throw new AssertionError();
        }
    }

    private void pushGroupChanges(List<SyncRequest> reqs)
        throws ServiceException, IOException {
        LOG.debug("Pushing contact group changes");
        Stats stats = new Stats();
        Iterator<SyncRequest> it = reqs.iterator();
        while (it.hasNext()) {
            SyncRequest req = it.next();
            if (pushChange(req, stats)) {
                it.remove();
            }
        }
        LOG.debug("Pushed contact group changes: ", stats);
    }
    
    private void pushContactChanges(List<SyncRequest> reqs)
        throws ServiceException, IOException {
        Stats stats = new Stats();
        Iterator<SyncRequest> it = reqs.iterator();
        while (it.hasNext()) {
            SyncRequest req = it.next();
            switch (req.getType()) {
            case INSERT: case UPDATE:
                setGroupInfo((ContactEntry) req.getEntry(), req.getItemId());
            }
            if (pushChange(req, stats)) {
                it.remove();
            }
        }
        LOG.debug("Contact changes pushed: " + stats);
    }
    
    private void setGroupInfo(ContactEntry entry, int itemId)
        throws ServiceException {
        List<GroupMembershipInfo> groups = entry.getGroupMembershipInfos();
        groups.clear();
        for (Tag tag : getTagList(itemId)) {
            ContactGroupEntry ge = getEntry(tag.getId(), ContactGroupEntry.class);
            if (ge != null) {
                groups.add(new GroupMembershipInfo(false, ge.getId()));
            }
        }
    }

    private List<Tag> getTagList(int itemId) throws ServiceException {
        return mbox.getItemById(CONTEXT, itemId, MailItem.TYPE_CONTACT).getTagList();
    }

    private boolean pushChange(SyncRequest req, Stats stats)
        throws ServiceException, IOException {
        int itemId = req.getItemId();
        try {
            req.execute();
            switch (req.getType()) {
            case DELETE:
                deleteEntry(itemId);
                stats.deleted++;
                break;
            case UPDATE:
                updateEntry(itemId, req.getEntry());
                stats.updated++;
                break;
            case INSERT:
                updateEntry(itemId, req.getEntry());
                stats.added++;
                break;
            }
            return true;
        } catch (ServiceException e) {
            syncContactFailed(e, itemId, req.getEntry());
            return false;
        }
    }

    private void syncContactFailed(ServiceException e, int itemId,
                                   BaseEntry entry) throws ServiceException {
        SyncExceptionHandler.checkRecoverableException("Contact sync failed", e);
        DesktopMailbox dmbx = (DesktopMailbox) mbox;
        SyncExceptionHandler.syncContactFailed(dmbx, itemId, service.pp(entry), e);
    }

    private Map<Integer, ChangeType> getLocalContactChanges(int seq)
        throws ServiceException {
        Map<Integer, ChangeType> changes = new HashMap<Integer, ChangeType>();
        // Get modified and deleted contacts
        for (int id : getModifiedItems(seq, MailItem.TYPE_CONTACT)) {
            changes.put(id, DbDataSource.hasMapping(ds, id) ?
                        ChangeType.UPDATE : ChangeType.ADD);
        }
        for (int id : getTombstones(seq, MailItem.TYPE_CONTACT)) {
            if (DbDataSource.hasMapping(ds, id)) {
                changes.put(id, ChangeType.DELETE);
            }
        }
        return changes;
    }

    private Map<Integer, ChangeType> getLocalGroupChanges(int seq)
        throws ServiceException {
        Map<Integer, ChangeType> changes = new HashMap<Integer, ChangeType>();
        // Get modified tags (but only those associated with modified contacts)
        Set<Integer> tags = getContactTags(
            getModifiedItems(seq, MailItem.TYPE_CONTACT));
        for (Tag tag : mbox.getModifiedTags(CONTEXT, seq)) {
            int id = tag.getId();
            if (tags.contains(id)) {
                changes.put(id, DbDataSource.hasMapping(ds, id) ?
                            ChangeType.UPDATE : ChangeType.ADD);
            }
        }
        // Get deleted tags
        for (int id : getTombstones(seq, MailItem.TYPE_TAG)) {
            if (DbDataSource.hasMapping(ds, id)) {
                changes.put(id, ChangeType.DELETE);
            }
        }
        return changes;
    }

    public Set<Integer> getContactTags(Collection<Integer> itemIds)
        throws ServiceException {
        Set<Integer> tags = new HashSet<Integer>();
        for (int itemId : itemIds) {
            for (Tag tag : getTagList(itemId)) {
                tags.add(tag.getId());
            }
        }
        return tags;
    }

    private List<Integer> getTombstones(int seq, byte type) throws ServiceException {
        List<Integer> ids = mbox.getTombstones(seq).getIds(type);
        return ids != null ? ids : Collections.<Integer>emptyList();
    }

    private List<Integer> getModifiedItems(int seq, byte type) throws ServiceException {
        return mbox.getModifiedItems(CONTEXT, seq, type, CONTACT_FOLDERS).getFirst();
    }

    private ContactData getContactData(int itemId) throws ServiceException {
        return new ContactData((Contact)
            mbox.getItemById(CONTEXT, itemId, MailItem.TYPE_CONTACT));
    }

    private <T extends BaseEntry> T getEntry(int itemId, Class<T> entryClass)
        throws ServiceException {
        return getEntry(DbDataSource.getMapping(ds, itemId), entryClass);
    }

    private <T extends BaseEntry> T getEntry(DataSourceItem dsi, Class<T> entryClass)
        throws ServiceException {
        // LOG.debug("Loading contact data for item id = %d", dsi.itemId);
        String xml = dsi.md.get(KEY_GAB_ENTRY);
        return xml != null ? service.parseEntry(xml, entryClass) : null;
    }

    private void updateEntry(int itemId, BaseEntry entry) throws ServiceException {
        LOG.debug("Saving entry for item id = %d, entry id = %s", itemId, entry.getId());
        Metadata md = new Metadata();
        md.put(KEY_GAB_ENTRY, service.toXml(entry));
        DataSourceItem dsi = new DataSourceItem(itemId, entry.getId(), md);
        if (DbDataSource.hasMapping(ds, itemId)) {
            DbDataSource.updateMapping(ds, dsi);
        } else {
            DbDataSource.addMapping(ds, dsi);
        }
    }

    private void deleteEntry(int itemId) throws ServiceException {
        LOG.debug("Deleting entry for item id: %d", itemId);
        DbDataSource.deleteMappings(ds, Arrays.asList(itemId));
    }

    private long getTagBitmask(ContactEntry entry) throws ServiceException {
        long mask = 0;
        for (GroupMembershipInfo info : entry.getGroupMembershipInfos()) {
            Boolean deleted = info.getDeleted();
            if (deleted == null || !deleted) {
                String id = info.getHref();
                DataSourceItem dsi = DbDataSource.getReverseMapping(ds, id);
                if (dsi.itemId == -1) {
                    throw ServiceException.FAILURE(
                        "Missing item id for contact group: " + id, null);
                }
                Tag tag = mbox.getTagById(CONTEXT, dsi.itemId);
                mask |= tag.getBitmask();
            }
        }
        return mask;
    }

    public boolean isTraceEnabled() {
        return LOG.isDebugEnabled() && (DEBUG_TRACE || ds.isDebugTraceEnabled());
    }

    public GabService getGabService() { return service; }
}
