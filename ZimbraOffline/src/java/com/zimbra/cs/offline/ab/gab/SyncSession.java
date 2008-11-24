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
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.ab.LocalData;
import com.zimbra.cs.offline.ab.SyncState;
import com.zimbra.cs.offline.ab.Change;
import com.zimbra.cs.offline.ab.ContactGroup;
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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.io.IOException;

public class SyncSession {
    private final LocalData localData;
    private final GabService service;
    private Map<String, ContactGroup> contactGroups;

    private static class Stats {
        int added, updated, deleted;

        public String toString() {
            return String.format(
                "%d added, %d updated, and %d deleted", added, updated, deleted);
        }
    }

    private static final Log LOG = OfflineLog.gab;

    private static final boolean FORCE_TRACE = true;
    private static final boolean HTTP_DEBUG = false;

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
        localData = new LocalData((OfflineDataSource) ds);
        service = new GabService(ds.getUsername(), ds.getDecryptedPassword());
    }

    public void sync() throws ServiceException {
        try {
            syncData();
        } catch (Exception e) {
            throw ServiceException.FAILURE("Google contact sync error", e);
        }
    }

    private void syncData() throws IOException, ServiceException {
        Mailbox mbox = localData.getMailbox();
        SyncState state = localData.loadState();
        // Get remote changes since last sync
        DateTime currentTime = DateTime.now();
        String rev = state.getLastRevision();
        DateTime lastSyncTime = rev != null ? DateTime.parseDateTime(rev) : null;
        ContactFeed contacts = service.getContacts(lastSyncTime, currentTime);
        ContactGroupFeed groups = service.getGroups(lastSyncTime, currentTime);
        state.setLastRevision(currentTime.toString());
        List<SyncRequest> contactRequests;
        int seq = state.getLastModSequence();
        contactGroups = new HashMap<String, ContactGroup>();
        synchronized (mbox) {
            // Get local changes since last sync
            Map<Integer, Change> contactChanges = localData.getContactChanges(seq);
            // Process remote group and contact changes
            processGroupChanges(groups.getEntries());
            processContactChanges(contacts.getEntries(), contactChanges);
            // Process local changes and determine changes to push
            contactRequests = processContactChanges(contactChanges.values());
            state.setLastModSequence(mbox.getLastChangeID());
        }
        // Push local changes to remote
        pushContactChanges(contactRequests);
        int errors = contactRequests.size();
        if (errors > 0) {
            LOG.debug("Contact sync had %d error(s)", errors);
        }
        localData.saveState(state);
        localData.saveContactGroups(contactGroups.values());
    }

    private void processGroupChanges(List<ContactGroupEntry> entries)
        throws ServiceException, IOException {
        LOG.debug("Processing %d remote contact group changes", entries.size());
        Stats stats = new Stats();
        for (ContactGroupEntry entry : entries) {
            DataSourceItem dsi = localData.getReverseMapping(entry.getId());
            try {
                processGroupChange(entry, dsi, stats);
            } catch (ServiceException e) {
                localData.syncContactFailed(e, dsi.itemId, service.pp(entry));
            }
        }
        LOG.debug("Processed remote contact group changes: " + stats);
    }

    private void processGroupChange(ContactGroupEntry entry,
                                    DataSourceItem dsi,
                                    Stats stats) throws ServiceException {
        if (isTraceEnabled()) {
            LOG.debug("Processing remote group entry:\n%s", service.pp(entry));
        }
        int itemId = dsi.itemId;
        boolean deleted = entry.getDeleted() != null;
        String remoteId = entry.getId();
        if (itemId > 0) {
            // Existing ontact group updated or deleted
            ContactGroup group = getContactGroup(remoteId, itemId);
            if (deleted) {
                // Don't delete tag since we can't be sure that it is not
                // also used by other non-contact items
                localData.deleteMapping(itemId);
                localData.deleteContactGroup(itemId);
                contactGroups.remove(remoteId);
                stats.deleted++;
                LOG.debug("Deleted contact group: " + group);
            } else {
                String newName = getName(entry);
                if (!newName.equals(group.getName())) {
                    group.setName(newName);
                    group.modify();
                    stats.updated++;
                    LOG.debug("Updated contact group: " + group);
                }
                updateEntry(itemId, entry);
            }
        } else if (!deleted) {
            // Contact group was added
            ContactGroup group = localData.createContactGroup(
                Mailbox.ID_FOLDER_CONTACTS, getName(entry));
            contactGroups.put(remoteId, group);
            stats.added++;
            LOG.debug("Contact group added: " + group);
        }
    }

    private static String getName(BaseEntry entry) {
        return entry.getTitle().getPlainText();
    }
    
    private void processContactChanges(List<ContactEntry> entries,
                                       Map<Integer, Change> changes)
        throws ServiceException, IOException {
        LOG.debug("Processing %d remote contact changes", entries.size());
        Stats stats = new Stats();
        for (ContactEntry entry : entries) {
            DataSourceItem dsi = localData.getReverseMapping(entry.getId());
            try {
                processContactChange(entry, dsi, changes, stats);
            } catch (ServiceException e) {
                localData.syncContactFailed(e, dsi.itemId, service.pp(entry));
            }
        }
        LOG.debug("Processed remote contact changes: " + stats);
    }

    private void processContactChange(ContactEntry entry,
                                      DataSourceItem dsi,
                                      Map<Integer, Change> changes,
                                      Stats stats) throws ServiceException {
        if (isTraceEnabled()) {
            LOG.debug("Processing remote contact entry:\n%s", service.pp(entry));
        }
        int itemId = dsi.itemId;
        boolean deleted = entry.getDeleted() != null;
        if (itemId > 0) {
            // Contact updated or deleted
            if (deleted) {
                localData.deleteContact(itemId);
                localData.deleteMapping(itemId);
                changes.remove(itemId);
                stats.deleted++;
            } else if (!changes.containsKey(itemId)) {
                // Remote contact was updated with no local change
                String url = getEditUrl(getEntry(dsi, ContactEntry.class));
                if (!getEditUrl(entry).equals(url)) {
                    // Only update local entry if edit url has changed
                    // (avoids modifying contacts which we just pushed)
                    ContactData cd = new ContactData(entry);
                    localData.modifyContact(itemId, cd.getParsedContact());
                    updateEntry(itemId, entry);
                    stats.updated++;
                }
            }
        } else if (!deleted) {
            // New contact added
            ContactData cd = new ContactData(entry);
            Contact contact = localData.createContact(cd.getParsedContact());
            updateEntry(contact.getId(), entry);
            stats.added++;
        }
    }

    private static String getEditUrl(BaseEntry entry) {
        return entry.getEditLink().getHref();
    }

    private List<SyncRequest> processContactChanges(Collection<Change> changes)
        throws ServiceException {
        List<SyncRequest> reqs = new ArrayList<SyncRequest>();
        for (Change change : changes) {
            reqs.add(getContactSyncRequest(change));
        }
        return reqs;
    }
    
    private SyncRequest getContactSyncRequest(Change change)
        throws ServiceException {
        // For ADD and UPDATE, group membership info will be set later after
        // we've pushed contact group changes, since until then we will not
        // know the entry id for a newly added group.
        int id = change.getItemId();
        if (change.isAdd()) {
            ContactEntry entry = getContactData(id).newContactEntry();
            return SyncRequest.insert(this, id, entry);
        } else if (change.isUpdate()) {
            ContactEntry entry = getEntry(id, ContactEntry.class);
            getContactData(id).updateContactEntry(entry);
            return SyncRequest.update(this, id, entry);
        } else if (change.isDelete()) {
            ContactEntry entry = getEntry(id, ContactEntry.class);
            return SyncRequest.delete(this, id, entry);
        }
        return null;
    }

    private void pushContactChanges(List<SyncRequest> reqs)
        throws ServiceException, IOException {
        Stats stats = new Stats();
        Iterator<SyncRequest> it = reqs.iterator();
        while (it.hasNext()) {
            SyncRequest req = it.next();
            if (pushChange(req, stats)) {
                it.remove();
            }
        }
        LOG.debug("Contact changes pushed: " + stats);
    }
    
    private boolean pushChange(SyncRequest req, Stats stats)
        throws ServiceException, IOException {
        int itemId = req.getItemId();
        try {
            req.execute();
            if (req.isInsert()) {
                updateEntry(itemId, req.getEntry());
                stats.added++;
            } else if (req.isUpdate()) {
                updateEntry(itemId, req.getEntry());
                stats.updated++;
            } else if (req.isDelete()) {
                localData.deleteMapping(itemId);
                stats.deleted++;
            }
            return true;
        } catch (ServiceException e) {
            localData.syncContactFailed(e, itemId, service.pp(req.getEntry()));
            return false;
        }
    }

    private ContactData getContactData(int itemId) throws ServiceException {
        return new ContactData(localData.getContact(itemId));
    }

    private <T extends BaseEntry> T getEntry(int itemId, Class<T> entryClass)
        throws ServiceException {
        return getEntry(localData.getMapping(itemId), entryClass);
    }

    private <T extends BaseEntry> T getEntry(DataSourceItem dsi, Class<T> entryClass)
        throws ServiceException {
        // LOG.debug("Loading contact data for item id = %d", dsi.itemId);
        String xml = localData.getData(dsi);
        return xml != null ? service.parseEntry(xml, entryClass) : null;
    }

    private void updateEntry(int itemId, BaseEntry entry) throws ServiceException {
        localData.updateMapping(itemId, entry.getId(), service.toXml(entry));
        if (entry.getClass() == ContactEntry.class) {
            updateContactGroups(itemId, (ContactEntry) entry);
        }
    }

    private void updateContactGroups(int itemId, ContactEntry entry) throws ServiceException {
        for (GroupMembershipInfo info : entry.getGroupMembershipInfos()) {
            ContactGroup group = getContactGroup(info.getHref());
            if (group != null) {
                boolean deleted = Boolean.TRUE.equals(info.getDeleted());
                if (deleted) {
                    if (group.removeContact(itemId)) {
                        LOG.debug("Removed contact id %d from group %s", itemId, group);
                    }
                } else if (group.addContact(itemId)) {
                    LOG.debug("Added contact id %d to group %s", itemId, group);
                }
            }
        }
    }

    private ContactGroup getContactGroup(String remoteId)
        throws ServiceException {
        ContactGroup group = contactGroups.get(remoteId);
        if (group == null) {
            DataSourceItem dsi = localData.getReverseMapping(remoteId);
            if (dsi.itemId > 0) {
                group = getContactGroup(remoteId, dsi.itemId);
            }
        }
        return group;
    }

    private ContactGroup getContactGroup(String remoteId, int itemId)
        throws ServiceException {
        ContactGroup group = contactGroups.get(remoteId);
        if (group == null) {
            try {
                group = localData.getContactGroup(itemId);
            } catch (MailServiceException.NoSuchItemException e) {
                return null;
            }
            contactGroups.put(remoteId, group);
        }
        return group;
    }

    public boolean isTraceEnabled() {
        return LOG.isDebugEnabled() &&
               (FORCE_TRACE || localData.getDataSource().isDebugTraceEnabled());
    }
    
    public GabService getGabService() { return service; }
    public LocalData getLocalData() { return localData; }
}
