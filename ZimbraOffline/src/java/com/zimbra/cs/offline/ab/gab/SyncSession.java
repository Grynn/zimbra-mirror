/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.ab.gab;

import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.Contact.Attachment;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.ab.LocalData;
import com.zimbra.cs.offline.ab.SyncState;
import com.zimbra.cs.offline.ab.Change;
import com.zimbra.cs.offline.ab.ContactGroup;
import com.zimbra.cs.offline.ab.Ab;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.common.util.Log;
import com.zimbra.common.service.ServiceException;
import com.google.gdata.client.http.HttpGDataRequest;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.SystemGroup;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.extensions.Email;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.io.IOException;

public class SyncSession {
    private final LocalData localData;
    private final GabService service;
    private String myContactsUrl;
    Map<Integer, Change> localChanges;
    Map<Integer, ContactEntry> remoteContacts;
    Map<String, Attachment> photos;
    private boolean reset;

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
        if (state == null) {
            LOG.info("Sync state version change - resetting contact data");
            state = new SyncState();
            reset = true;
        }
        // Get remote contact and group changes
        String rev = state.getLastRevision();
        DateTime lastRev = rev != null ? DateTime.parseDateTime(rev) : null;
        List<ContactEntry> contacts = service.getContactFeed(lastRev, null).getEntries();
        LOG.debug("Found %d remote contact change(s)", contacts.size());
        if (!contacts.isEmpty()) {
            photos = getContactPhotos(contacts);
            lastRev = new DateTime(getLastUpdated(contacts) + 1);
            state.setLastRevision(lastRev.toString());
        }
        List<ContactGroupEntry> groups = service.getGroupFeed(null, lastRev).getEntries();
        // Get the system group for all new contacts
        myContactsUrl = getSystemGroupUrl(groups, "Contacts");
        LOG.debug("System group 'My Contacts' url = " + myContactsUrl);
        int seq = state.getLastModSequence();
        List<SyncRequest> contactRequests;
        synchronized (mbox) {
            // Get local changes since last sync (none if resetting)
            localChanges = reset ?
                new HashMap<Integer, Change>() : localData.getContactChanges(seq);
            remoteContacts = new HashMap<Integer, ContactEntry>(contacts.size());
            // Process any remote contact changes
            if (!contacts.isEmpty()) {     
                processRemoteContacts(contacts);
            }
            // Process local changes and determine changes to push
            contactRequests = processLocalContactChanges(localChanges.values());
            // Process remote group changes
            if (!contacts.isEmpty()) {
                processGroups(groups);
            }
            // If resetting, then remove local contacts deleted remotely
            if (reset) {
                localData.deleteMissingContacts(getRemoteIds(contacts));
            }
            state.setLastModSequence(mbox.getLastChangeID());
        }
        // Push local changes to remote
        pushContactChanges(contactRequests);
        int errors = contactRequests.size();
        if (errors > 0) {
            LOG.debug("Contact sync had %d error(s)", errors);
        }
        localData.saveState(state);
    }

    private static long getLastUpdated(List<ContactEntry> contacts) {
        long updated = 0;
        for (ContactEntry contact : contacts) {
            long time = contact .getUpdated().getValue();
            if (time > updated) {
                updated = time;
            }
        }
        return updated;
    }

    private static String getSystemGroupUrl(List<ContactGroupEntry> groups, String name) {
        for (ContactGroupEntry group : groups) {
            SystemGroup sg = group.getSystemGroup();
            if (sg != null && sg.getId().equals(name)) {
                return group.getId();
            }
        }
        return null;
    }

    private Set<String> getRemoteIds(List<ContactEntry> entries) {
        Set<String> ids = new HashSet<String>(entries.size());
        for (ContactEntry entry : entries) {
            ids.add(entry.getId());
        }
        return ids;
    }

    private void processGroups(List<ContactGroupEntry> entries)
        throws ServiceException, IOException {
        LOG.debug("Found %d remote contact group(s)", entries.size());
        Stats stats = new Stats();
        // Get all existing contact groups
        Map<String, ContactGroup> groups = new HashMap<String, ContactGroup>();
        for (ContactGroupEntry entry : entries) {
            // Ignore system groups...
            if (entry.getSystemGroup() == null) {
                ContactGroup group = new ContactGroup(getName(entry));
                groups.put(entry.getId(), group);
            }
        }
        // Get all contact mappings and update contact group dlist information
        Collection<DataSourceItem> mappings = localData.getAllContactMappings();
        for (DataSourceItem dsi : mappings) {
            if (Gab.isContactId(dsi.remoteId)) {
                ContactEntry contact = getEntry(dsi, ContactEntry.class);
                if (contact.hasGroupMembershipInfos() && contact.hasEmailAddresses()) {
                    Email email = getPrimaryEmail(contact);
                    for (GroupMembershipInfo gmi : contact.getGroupMembershipInfos()) {
                        if (!isDeleted(gmi)) {
                            ContactGroup group = groups.get(gmi.getHref());
                            if (group != null) {
                                group.addEmail(email.getAddress());
                            }
                        }
                    }
                }
            }
        }
        // Remove contact groups deleted remotely
        for (DataSourceItem dsi : mappings) {
            if (Gab.isGroupId(dsi.remoteId) && !groups.containsKey(dsi.remoteId)) {
                localData.deleteContactGroup(dsi.itemId);
                groups.remove(dsi.remoteId);
                stats.deleted++;
            }
        }
        // Create new or update existing contact groups
        for (Map.Entry<String, ContactGroup> me : groups.entrySet()) {
            updateGroup(me.getKey(), me.getValue(), stats);
        }
        LOG.debug("Processed remote contact group changes: " + stats);
    }

    private static boolean isDeleted(GroupMembershipInfo gmi) {
        return Boolean.TRUE.equals(gmi.getDeleted());
    }
    
    private Email getPrimaryEmail(ContactEntry contact) {
        if (contact.hasEmailAddresses()) {
            for (Email email : contact.getEmailAddresses()) {
                if (email.getPrimary()) {
                    return email;
                }
            }
            // If primary not found, then use first available
            return contact.getEmailAddresses().get(0);
        }
        return null;
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

    private static String getName(BaseEntry entry) {
        return entry.getTitle().getPlainText();
    }

    private Map<String, Attachment> getContactPhotos(List<ContactEntry> entries)
        throws ServiceException, IOException {
        Map<String, Attachment> photos = new HashMap<String, Attachment>();
        for (ContactEntry entry : entries) {
            try {
                Attachment photo = getContactPhoto(entry);
                if (photo != null) {
                    LOG.debug("Found contact photo for entry: " + entry.getId());
                    photos.put(getEditUrl(entry), photo);
                }
            } catch (com.google.gdata.util.ServiceException e) {
                LOG.info("Unable to retrieve contact photo for entry id: " + entry.getId());

            }
        }
        LOG.debug("Found %d contact photo changes", photos.size());
        return photos;
    }

    private Attachment getContactPhoto(ContactEntry entry)
        throws ServiceException, IOException, com.google.gdata.util.ServiceException {
        boolean deleted = entry.getDeleted() != null;
        if (deleted) return null;
        DataSourceItem dsi = localData.getReverseMapping(entry.getId());
        if (dsi.itemId > 0) {
            // If existing contact then only retrieve photo if edit url has
            // changed (otherwise contact was just pushed).
            String url = getEditUrl(getEntry(dsi, ContactEntry.class));
            if (getEditUrl(entry).equals(url)) return null;
        }
        return service.getPhoto(entry);
    }
    
    private void processRemoteContacts(List<ContactEntry> entries)
        throws ServiceException, IOException {
        LOG.debug("Found %d remote contact change(s)", entries.size());
        remoteContacts = new HashMap<Integer, ContactEntry>(entries.size());
        Stats stats = new Stats();
        for (ContactEntry entry : entries) {
            DataSourceItem dsi = localData.getReverseMapping(entry.getId());
            try {
                processRemoteContact(entry, dsi, stats);
            } catch (ServiceException e) {
                localData.syncContactFailed(e, dsi.itemId, service.pp(entry));
            }
        }
        LOG.debug("Processed remote contact changes: " + stats);
    }

    private void processRemoteContact(ContactEntry entry, DataSourceItem dsi, Stats stats)
        throws IOException, ServiceException {
        if (isTraceEnabled()) {
            LOG.debug("Processing remote contact entry:\n%s", service.pp(entry));
        }
        int itemId = dsi.itemId;
        String editUrl = getEditUrl(entry);
        boolean deleted = entry.getDeleted() != null;
        if (itemId > 0) {
            // Contact updated or deleted
            if (deleted) {
                localData.deleteContact(itemId);
                localData.deleteMapping(itemId);
                localChanges.remove(itemId);
                stats.deleted++;
            } else if (localChanges.containsKey(itemId)) {
                // If there is a conflict then local change wins
                LOG.debug("Not importing update for contact %s since local change exists", entry.getId());
            } else if (reset || isChanged(dsi, entry)) {
                // Only import contact change if remote contact is different
                // from what we last pushed or we are resetting local data.
                Contact contact = localData.getContact(itemId);
                ParsedContact pc = new ParsedContact(contact);
                ContactData cd = new ContactData(entry);
                cd.updateParsedContact(pc, photos.get(editUrl));
                localData.modifyContact(itemId, pc);
                updateEntry(itemId, entry);
                int newFolderId = getLocalFolderId(entry);
                if (contact.getFolderId() != newFolderId) {
                    localData.moveContact(itemId, newFolderId);
                }
                stats.updated++;
            }
            remoteContacts.put(itemId, entry);
        } else if (!deleted) {
            ContactData cd = new ContactData(entry);
            ParsedContact pc = cd.newParsedContact(photos.get(editUrl));
            Contact contact = localData.createContact(pc, getLocalFolderId(entry));
            updateEntry(contact.getId(), entry);
            remoteContacts.put(contact.getId(), entry);
            stats.added++;
        }
    }

    private boolean isChanged(DataSourceItem dsi, ContactEntry entry)
        throws ServiceException {
        ContactEntry oldEntry = getEntry(dsi, ContactEntry.class);
        return oldEntry == null || !oldEntry.getUpdated().equals(entry.getUpdated());
    }
    
    private int getLocalFolderId(ContactEntry entry) {
        if (entry.hasGroupMembershipInfos()) {
            for (GroupMembershipInfo gmi : entry.getGroupMembershipInfos()) {
                if (!Boolean.TRUE.equals(gmi.getDeleted()) && myContactsUrl.equals(gmi.getHref())) {
                    return Mailbox.ID_FOLDER_CONTACTS;
                }
            }
        }
        return Mailbox.ID_FOLDER_AUTO_CONTACTS;
    }

    private static String getEditUrl(BaseEntry entry) {
        return entry.getEditLink().getHref();
    }

    private List<SyncRequest> processLocalContactChanges(Collection<Change> changes)
        throws ServiceException {
        LOG.debug("Found %d local contact changes", changes.size());
        List<SyncRequest> reqs = new ArrayList<SyncRequest>();
        for (Change change : changes) {
            SyncRequest req = processLocalContactChange(change);
            if (req != null) {
                reqs.add(req);
            }
        }
        return reqs;
    }
    
    private SyncRequest processLocalContactChange(Change change)
        throws ServiceException {
        int id = change.getItemId();
        if (change.isAdd() || change.isUpdate()) {
            Contact contact = localData.getContact(id);
            if (ContactGroup.isContactGroup(contact)) {
                // Delete mapping so contact group will no longer be synced
                localData.deleteMapping(id);
            } else {
                ContactData cd = new ContactData(contact);
                SyncRequest req;
                if (change.isAdd()) {
                    ContactEntry entry = cd.newContactEntry();
                    updateGroupMembershipInfos(entry, contact);
                    req = SyncRequest.insert(this, id, entry);
                } else {
                    ContactEntry entry = getContactEntry(id);
                    cd.updateContactEntry(entry);
                    updateGroupMembershipInfos(entry, contact);
                    req = SyncRequest.update(this, id, entry);
                }
                Attachment photo = Ab.getPhoto(contact);
                if (photo != null) {                                 
                    LOG.debug("Photo added for contact id " + contact.getId());
                    req.setPhoto(Ab.getContent(contact, photo), photo.getContentType());
                }
                return req;
            }
        } else if (change.isDelete()) {
            DataSourceItem dsi = localData.getMapping(id);
            if (dsi.remoteId != null && Gab.isGroupId(dsi.remoteId)) {
                // Remove mapping for deleted contact group 
                localData.deleteMapping(id);
            } else {
                ContactEntry entry = getEntry(dsi, ContactEntry.class);
                if (entry != null) {
                    return SyncRequest.delete(this, id, entry);
                }
            }
        }
        return null;
    }

    private void updateGroupMembershipInfos(ContactEntry entry, Contact contact) {
        if (entry.hasGroupMembershipInfos()) {
            for (GroupMembershipInfo gmi : entry.getGroupMembershipInfos()) {
                if (myContactsUrl.equals(gmi.getHref())) {
                    gmi.setDeleted(isAutoContact(contact));
                    return;
                }
            }
        }
        if (!isAutoContact(contact)) {
            GroupMembershipInfo gmi = new GroupMembershipInfo();
            gmi.setHref(myContactsUrl);
            entry.addGroupMembershipInfo(gmi);
        }
    }

    private static boolean isAutoContact(Contact contact) {
        return contact.getFolderId() == Mailbox.ID_FOLDER_AUTO_CONTACTS;
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

    // Get latest contact entry from retrieved contacts or restore from metadata
    private ContactEntry getContactEntry(int itemId) throws ServiceException {
        ContactEntry entry = remoteContacts.get(itemId);
        return entry != null ?
            entry : getEntry(localData.getMapping(itemId), ContactEntry.class);
    }
    
    private <T extends BaseEntry> T getEntry(DataSourceItem dsi, Class<T> entryClass)
        throws ServiceException {
        // LOG.debug("Loading contact data for item id = %d", dsi.itemId);
        String xml = localData.getData(dsi);
        return xml != null ? service.parseEntry(xml, entryClass) : null;
    }

    private void updateEntry(int itemId, BaseEntry entry) throws ServiceException {
        localData.updateMapping(itemId, entry.getId(), service.toXml(entry));
    }

    public boolean isTraceEnabled() {
        return LOG.isDebugEnabled() &&
               (FORCE_TRACE || localData.getDataSource().isDebugTraceEnabled());
    }
    
    public GabService getGabService() { return service; }
    public LocalData getLocalData() { return localData; }
}
