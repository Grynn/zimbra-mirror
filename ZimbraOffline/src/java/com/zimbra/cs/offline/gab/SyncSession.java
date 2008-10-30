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
package com.zimbra.cs.offline.gab;

import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.Tag;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.SyncExceptionHandler;
import com.zimbra.cs.mailbox.DesktopMailbox;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.yab.SyncException;
import com.zimbra.cs.db.DbDataSource;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;
import com.zimbra.common.util.Log;
import com.zimbra.common.service.ServiceException;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.client.Query;
import com.google.gdata.client.http.HttpGDataRequest;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.common.xml.XmlWriter;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.ParseSource;
import com.google.gdata.data.DateTime;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumSet;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class SyncSession {
    private final DataSource ds;
    private final Mailbox mbox;
    private final ContactsService service;
    private final SyncState state;
    private final URL contactsFeedUrl;
    private final URL groupsFeedUrl;

    private enum ChangeType { ADD, DELETE, UPDATE }
    
    private static final Log LOG = OfflineLog.gab;

    private static final String KEY_GAB_CONTACT = "GAB_CONTACT";
    private static final String BASE_URL = OfflineLC.zdesktop_gab_base_url.value();

    private static final boolean DEBUG_TRACE = true;
    private static final boolean HTTP_DEBUG = false;
    
    private static final String APP_NAME = String.format("Zimbra-%s-%s",
        OfflineLC.zdesktop_name.value(), OfflineLC.zdesktop_version.value());

    private static final Mailbox.OperationContext CONTEXT =
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
        service = new ContactsService(APP_NAME);
        state = new SyncState(this);
        String user = ds.getUsername();
        contactsFeedUrl = toUrl(BASE_URL + "/contacts/" + user + "/full");
        groupsFeedUrl = toUrl(BASE_URL + "/groups/" + user + "/full");
        try {
            service.setUserCredentials(user, ds.getDecryptedPassword());
        } catch (AuthenticationException e) {
            throw ServiceException.FAILURE("Google address book authentication failed", e);
        }
    }

    public void sync() throws ServiceException {
        try {
            syncContacts();
        } catch (Exception e) {
            throw ServiceException.FAILURE("Google contact sync error", e);
        }
    }

    private void syncContacts() throws IOException, ServiceException {
        state.load();
        // Get remote changes since last sync
        ContactFeed feed = getContactFeed(state.getLastUpdateTime());
        state.setLastUpdateTime(feed.getUpdated());
        List<SyncRequest> requests;
        synchronized (mbox) {
            // Get local changes sync last sync
            Map<Integer, ChangeType> changes = getLocalChanges(state.getLastModSequence());
            // Process remote changes
            processRemoteChanges(feed, changes);
            // Process local changes and get changes to push
            requests = processLocalChanges(changes);
            state.setLastModSequence(mbox.getLastChangeID());
        }
        // Push changes to remote
        pushChanges(requests);
        if (requests.size() > 0) {
            LOG.debug("Contact sync had %d errors", requests.size());
        }
        state.save();
    }

    /*
     * Process remote changes from contact feed and update local contacts.
     * Also reconcile remote changes with any pending local changes which may
     * need to be pushed.
     */
    private void processRemoteChanges(ContactFeed feed,
                                      Map<Integer, ChangeType> changes)
        throws ServiceException, IOException {
        List<ContactEntry> entries = feed.getEntries();
        LOG.debug("Processing %d remote contact changes for feed %s",
                  entries.size(), contactsFeedUrl);
        int added = 0;
        int deleted = 0;
        int updated = 0;
        for (ContactEntry entry : entries) {
            String entryId = entry.getId();
            int itemId = state.getItemId(entryId);
            if (isTraceEnabled()) {
                LOG.debug("Processing remote contact entry:\n%s", pp(entry));
            }
            try {
                if (itemId != -1) {
                    if (entry.getDeleted() != null) {
                        // Remote contact was deleted
                        mbox.delete(CONTEXT, itemId, MailItem.TYPE_CONTACT);
                        deleteContactEntry(itemId);
                        changes.remove(itemId);
                        deleted++;
                    } else if (!changes.containsKey(itemId)) {
                        // Remote contact was updated with no local change
                        String url = getContactEntry(itemId).getEditLink().getHref();
                        if (!entry.getEditLink().getHref().equals(url)) {
                            // Only update local entry if edit url has changed
                            // (avoids modifying contacts which we just pushed)
                            ContactData cd = new ContactData(entry);
                            mbox.modifyContact(CONTEXT, itemId, cd.getParsedContact());
                            updateContactEntry(itemId, entry);
                            updated++;
                        }
                    }
                } else if (entry.getDeleted() == null) {
                    // Remote contact was added
                    ContactData cd = new ContactData(entry);
                    Contact contact = mbox.createContact(
                        CONTEXT, cd.getParsedContact(), Mailbox.ID_FOLDER_CONTACTS, null);
                    updateContactEntry(contact.getId(), entry);
                    added++;
                }
            } catch (ServiceException e) {
                syncContactFailed(e, itemId, entry);
            }
        }
        LOG.debug("Processed remote changes: %d local contacts added, " +
                  "%d updated, and %d deleted", added, updated, deleted);
    }

    private List<SyncRequest> processLocalChanges(Map<Integer, ChangeType> changes)
        throws ServiceException {
        List<SyncRequest> reqs = new ArrayList<SyncRequest>();
        for (Map.Entry<Integer, ChangeType> entry : changes.entrySet()) {
            reqs.add(getSyncRequest(entry.getKey(), entry.getValue()));
        }
        return reqs;
    }

    private SyncRequest getSyncRequest(int itemId, ChangeType type)
        throws ServiceException {
        ContactEntry entry;
        switch (type) {
        case ADD:
            entry = getContactData(itemId).newContactEntry();
            return new SyncRequest(this, itemId, RequestType.INSERT, entry);
        case UPDATE:
            entry = getContactEntry(itemId);
            getContactData(itemId).updateContactEntry(entry);
            return new SyncRequest(this, itemId, RequestType.UPDATE, entry);
        case DELETE:
            entry = getContactEntry(itemId);
            return new SyncRequest(this, itemId, RequestType.DELETE, entry);
        default:
            throw new AssertionError();
        }
    }

    private ContactFeed getContactFeed(DateTime lastUpdate)
        throws IOException, ServiceException {
        try {
            Query query = new Query(contactsFeedUrl);
            query.setMaxResults(9999999);
            if (lastUpdate != null) {
                query.setUpdatedMin(lastUpdate);
                query.setStringCustomParameter("showdeleted", "true");
            }
            return service.getFeed(query, ContactFeed.class, lastUpdate);
        } catch (com.google.gdata.util.ServiceException e) {
            throw ServiceException.FAILURE(
                "Unable to retrieve contact feed " + contactsFeedUrl, e);
        }
    }

    private ContactGroupFeed getGroupsFeed(DateTime lastUpdate)
        throws IOException, ServiceException {
        try {
            Query query = new Query(groupsFeedUrl);
            query.setMaxResults(9999999);
            if (lastUpdate != null) {
                query.setUpdatedMin(lastUpdate);
                query.setStringCustomParameter("showdeleted", "true");
            }
            return service.getFeed(query, ContactGroupFeed.class, lastUpdate);
        } catch (com.google.gdata.util.ServiceException e) {
            throw ServiceException.FAILURE(
                "Unable to retrieve contact groups feed " + groupsFeedUrl, e);
        }
    }

    private void pushChanges(List<SyncRequest> reqs)
        throws ServiceException, IOException {
        int added = 0;
        int updated = 0;
        int deleted = 0;
        Iterator<SyncRequest> it = reqs.iterator();
        while (it.hasNext()) {
            SyncRequest req = it.next();
            int itemId = req.getItemId();
            try {
                req.execute();
                switch (req.getType()) {
                case DELETE:
                    deleteContactEntry(itemId);
                    deleted++;
                    break;
                case UPDATE:
                    updateContactEntry(itemId, req.getEntry());
                    updated++;
                    break;
                case INSERT:
                    updateContactEntry(itemId, req.getEntry());
                    added++;
                    break;
                }
                it.remove();
            } catch (ServiceException e) {
                syncContactFailed(e, itemId, req.getEntry());
            }
        }
        LOG.debug("pushChanged finished: %d contacts added, %d updated, " +
                  "and %d deleted", added, updated, deleted);
    }
    
    private void syncContactFailed(ServiceException e, int itemId,
                                   ContactEntry entry) throws ServiceException {
        SyncExceptionHandler.checkRecoverableException("Contact sync failed", e);
        DesktopMailbox dmbx = (DesktopMailbox) mbox;
        try {
            SyncExceptionHandler.syncContactFailed(dmbx, itemId, pp(entry), e);
        } catch (IOException ioe) {
            SyncExceptionHandler.syncContactFailed(dmbx, itemId, e);
        }
    }
    
    private Map<Integer, ChangeType> getLocalChanges(int seq)
        throws ServiceException {
        Map<Integer, ChangeType> changes = new HashMap<Integer, ChangeType>();
        List<Integer> tombstones =
            mbox.getTombstones(seq).getIds(MailItem.TYPE_CONTACT, MailItem.TYPE_TAG);
        if (tombstones != null) {
            for (int id : tombstones) {
                if (state.hasItem(id)) {
                    changes.put(id, ChangeType.DELETE);
                }
            }
        }
        List<Integer> modified = getModifiedItems(seq, MailItem.TYPE_CONTACT);
        for (int id : modified) {
            changes.put(id, state.hasItem(id) ? ChangeType.UPDATE : ChangeType.ADD);
        }
        return changes;
    }

    private Set<Integer> getContactTags(Collection<Integer> itemIds)
        throws ServiceException {
        Set<Integer> tags = new HashSet<Integer>();
        for (int itemId : itemIds) {
            MailItem item = mbox.getItemById(CONTEXT, itemId, MailItem.TYPE_CONTACT);
            for (Tag tag : item.getTagList()) {
                tags.add(tag.getId());
            }
        }
        return tags;
    }
    
    private List<Integer> getModifiedItems(int seq, byte type) throws ServiceException {
        return mbox.getModifiedItems(CONTEXT, seq, type, CONTACT_FOLDERS).getFirst();
    }
    
    private ContactData getContactData(int itemId) throws ServiceException {
        return new ContactData((Contact)
            mbox.getItemById(CONTEXT, itemId, MailItem.TYPE_CONTACT));
    }

    private ContactEntry getContactEntry(int itemId) throws ServiceException {
        LOG.debug("Loading contact data for itemid = %d", itemId);
        DataSourceItem dsi = DbDataSource.getMapping(ds, itemId);
        String data = dsi.md.get(KEY_GAB_CONTACT);
        return data != null ? parseContactEntry(data) : null;
    }

    public ContactEntry parseContactEntry(String s) throws ServiceException {
        try {
            return BaseEntry.readEntry(new ParseSource(new StringReader(s)),
                                       ContactEntry.class,
                                       service.getExtensionProfile());
        } catch (Exception e) {
            throw ServiceException.FAILURE("Unable to parse contact data", e);
        }
    }

    private void updateContactEntry(int itemId, ContactEntry entry)
        throws ServiceException {
        LOG.debug("Saving contact entry for itemid = %d", itemId);
        state.addEntry(itemId, entry.getId());
        StringWriter sw = new StringWriter();
        try {
            entry.generateAtom(new XmlWriter(sw), service.getExtensionProfile());
        } catch (IOException e) {
            throw ServiceException.FAILURE(
                "Unable to generate XML for contact entry: itemid = " + itemId, e);
        }
        Metadata md = new Metadata();
        md.put(KEY_GAB_CONTACT, sw.toString());
        DataSourceItem dsi = new DataSourceItem(itemId, entry.getId(), md);
        if (DbDataSource.hasMapping(ds, itemId)) {
            DbDataSource.updateMapping(ds, dsi);
        } else {
            DbDataSource.addMapping(ds, dsi);
        }
    }

    private void deleteContactEntry(int itemId) throws ServiceException {
        LOG.debug("Deleting contact entry for item id: %d", itemId);
        state.removeEntry(itemId);
        DbDataSource.deleteMappings(ds, Arrays.asList(itemId));
    }

    private Tag createTag(String name) throws ServiceException {
        String normalized = MailItem.normalizeItemName(name);
        if (!name.equals(normalized)) {
            LOG.warn("Normalizing GAB category name '%s' to '%s' since it " +
                     "contains invalid tag characters", name, normalized);
            name = normalized;
        }
        try {
            return mbox.getTagByName(name);
        } catch (MailServiceException.NoSuchItemException e) {
            return mbox.createTag(CONTEXT, name, Tag.DEFAULT_COLOR);
        }
    }
    
    private long getTagBitmask(ContactEntry entry)
        throws SyncException, ServiceException {
        long mask = 0;
        for (GroupMembershipInfo info : entry.getGroupMembershipInfos()) {
            int itemId = state.getItemId(info.getHref());
            if (itemId == -1) {
                throw ServiceException.FAILURE(
                    "Missing item id for contact group url = " + info.getHref(), null);
            }
            Tag tag = mbox.getTagById(CONTEXT, itemId);
            mask |= tag.getBitmask();
        }
        return mask;
    }

    public String pp(BaseEntry entry) throws IOException {
        StringWriter sw = new StringWriter();
        XmlWriter xw = new XmlWriter(sw,
            EnumSet.of(XmlWriter.WriterFlags.PRETTY_PRINT), null);
        entry.generateAtom(xw, service.getExtensionProfile());
        return sw.toString();
    }
    
    public boolean isTraceEnabled() {
        return LOG.isDebugEnabled() && (DEBUG_TRACE || ds.isDebugTraceEnabled());
    }

    private static URL toUrl(String url) throws ServiceException {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw ServiceException.FAILURE("Bad URL format: " + url, null);
        }
    }

    public Mailbox getMailbox() { return mbox; }
    public ContactsService getContactsService() { return service; }
    public Mailbox.OperationContext getContext() { return CONTEXT; }
    public DataSource getDataSource() { return ds; }
    public URL getContactsFeedUrl() { return contactsFeedUrl; }
    public URL getGroupsFeedUrl() { return groupsFeedUrl; }
}
