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
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineLC;
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
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.ParseSource;
import com.google.gdata.data.ExtensionProfile;
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
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
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
    private static final boolean TRACE = true;
    
    private static final String APP_NAME = String.format("Zimbra-%s-%s",
        OfflineLC.zdesktop_name.value(), OfflineLC.zdesktop_version.value());

    private static final Mailbox.OperationContext CONTEXT =
        new OfflineMailbox.OfflineContext();

    static {
        Logger httpLogger = Logger.getLogger(HttpGDataRequest.class.getName());
        httpLogger.setLevel(Level.ALL);
        //Logger xmlLogger = Logger.getLogger(XmlParser.class.getName());
        // Create a log handler which prints all log events to the console.
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        httpLogger.addHandler(handler);
        //xmlLogger.addHandler(handler);
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
            state.setLastModSequence(mbox.getLastChangeID());
            // Process remote changes
            processRemoteChanges(feed, changes);
            // Process local changes and get changes to push
            requests = processLocalChanges(changes);
        }
        // Push changes to remote
        pushChanges(requests);
        if (requests.size() > 0) {
            throw ServiceException.FAILURE("Contact sync failed due to errors", null);
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
            if (itemId != -1) {
                if (entry.getDeleted() != null) {
                    // Remote contact was deleted
                    mbox.delete(CONTEXT, itemId, MailItem.TYPE_CONTACT);
                    deleteContactEntry(itemId);
                    changes.remove(itemId);
                    deleted++;
                } else if (!changes.containsKey(itemId)) {
                    // Remote contact was updated with no local change
                    ContactData cd = new ContactData(entry);
                    mbox.modifyContact(CONTEXT, itemId, cd.getParsedContact());
                    updateContactEntry(itemId, entry);
                    updated++;
                }
            } else {
                // Remote contact was added
                ContactData cd = new ContactData(entry);
                Contact contact = mbox.createContact(
                    CONTEXT, cd.getParsedContact(), Mailbox.ID_FOLDER_CONTACTS, null);
                updateContactEntry(contact.getId(), entry);
                added++;
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
            if (lastUpdate != null) {
                Query query = new Query(contactsFeedUrl);
                query.setUpdatedMin(lastUpdate);
                return service.getFeed(query, ContactFeed.class, lastUpdate);
            } else {
                return service.getFeed(contactsFeedUrl, ContactFeed.class);
            }
        } catch (com.google.gdata.util.ServiceException e) {
            throw ServiceException.FAILURE(
                "Unable to retrieve contact feed " + contactsFeedUrl, e);
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
            if (req.execute()) {
                switch (req.getType()) {
                case DELETE:
                    deleteContactEntry(req.getItemId());
                    deleted++;
                    break;
                case UPDATE:
                    updateContactEntry(req.getItemId(), req.getCurrentEntry());
                    updated++;
                    break;
                case INSERT:
                    updateContactEntry(req.getItemId(), req.getCurrentEntry());
                    added++;
                    break;
                }
                it.remove();
            }
        }
        LOG.debug("pushChanged finished: %d contacts added, %d updated, " +
                  "and %d deleted", added, updated, deleted);
    }
    
    private Map<Integer, ChangeType> getLocalChanges(int seq)
        throws ServiceException {
        Map<Integer, ChangeType> changes = new HashMap<Integer, ChangeType>();
        List<Integer> tombstones = mbox.getTombstones(seq).getIds(
            MailItem.TYPE_CONTACT);
        if (tombstones != null) {
            for (int id : tombstones) {
                changes.put(id, ChangeType.DELETE);
            }
        }
        List<Integer> modified = mbox.getModifiedItems(
            CONTEXT, seq, MailItem.TYPE_CONTACT).getFirst();
        for (int itemId : modified) {
            if (state.hasItem(itemId)) {
                changes.put(itemId, ChangeType.UPDATE);
            } else {
                changes.put(itemId, ChangeType.ADD);
            }
        }
        return changes;
    }

    private ContactData getContactData(int itemId) throws ServiceException {
        return new ContactData((Contact)
            mbox.getItemById(CONTEXT, itemId, MailItem.TYPE_CONTACT));
    }

    private ContactEntry getContactEntry(int itemId) throws ServiceException {
        LOG.debug("Loading contact data for itemid = %d", itemId);
        DataSourceItem dsi = DbDataSource.getMapping(ds, itemId);
        String data = dsi.md.get(KEY_GAB_CONTACT);
        if (data == null) return null;
        ParseSource ps = new ParseSource(new StringReader(data));
        ExtensionProfile ep = service.getExtensionProfile();
        try {
            return BaseEntry.readEntry(ps, ContactEntry.class, ep);
        } catch (Exception e) {
            throw ServiceException.FAILURE(
                "Unable to parse contact data for itemid = " + itemId, e);
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

    public String pp(BaseEntry entry) throws IOException {
        StringWriter sw = new StringWriter();
        XmlWriter xw = new XmlWriter(sw,
            EnumSet.of(XmlWriter.WriterFlags.PRETTY_PRINT), null);
        entry.generateAtom(xw, service.getExtensionProfile());
        return sw.toString();
    }
    
    public boolean isTraceEnabled() {
        return LOG.isDebugEnabled() && (TRACE || ds.isDebugTraceEnabled());
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
