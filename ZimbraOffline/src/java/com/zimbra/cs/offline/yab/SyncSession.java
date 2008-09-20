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
package com.zimbra.cs.offline.yab;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.offline.util.yab.Session;
import com.zimbra.cs.offline.util.yab.Contact;
import com.zimbra.cs.offline.util.yab.SyncResponse;
import com.zimbra.cs.offline.util.yab.SyncResponseEvent;
import com.zimbra.cs.offline.util.yab.SyncRequest;
import com.zimbra.cs.offline.util.yab.SyncRequestEvent;
import com.zimbra.cs.offline.util.yab.ContactChange;
import com.zimbra.cs.offline.util.yab.Result;
import com.zimbra.cs.offline.util.yab.ErrorResult;
import com.zimbra.cs.offline.util.yab.Yab;
import com.zimbra.cs.offline.util.yab.SuccessResult;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class SyncSession {
    private final Mailbox mbox;
    private final Session session;
    private final Set<Integer> deletedContactIds;
    private final Set<Integer> modifiedContactIds;
    private final Map<Integer, ContactData> pushedContacts;
    private final List<Integer> pushedItemIds;
    private SyncState state;

    private static final Log LOG = OfflineLog.yab;

    private static final Mailbox.OperationContext CONTEXT =
        new OfflineMailbox.OfflineContext();

    private static final int MAX_RETRIES = 3;

    static {
        LOG.setLevel(Log.Level.debug);
        Yab.enableDebug();
    }
    
    public SyncSession(Mailbox mbox, Session session) throws ServiceException {
        this.mbox = mbox;
        this.session = session;
        deletedContactIds = new HashSet<Integer>();
        modifiedContactIds = new HashSet<Integer>();
        pushedContacts = new HashMap<Integer, ContactData>();
        pushedItemIds = new ArrayList<Integer>();
    }

    public void sync() throws ServiceException {
        int count = 0;
        while (count++ < MAX_RETRIES) {
            try {
                syncData();
                return;
            } catch (Exception e) {
                LOG.error("Contact sync error", e);
                resetData();
            }
        }
        throw ServiceException.FAILURE(
            "Unrecoverable contact sync error after " + MAX_RETRIES + " retry attempts", null);
    }
    
    private void syncData() throws IOException, ServiceException {
        pushedContacts.clear();
        state = SyncState.load(this);
        SyncRequest req;
        synchronized (mbox) {
            loadChanges();
            state.setSequence(mbox.getLastChangeID());
            req = newSyncRequest();
        }
        while (req != null) {
            SyncResponse res = (SyncResponse) req.send();
            synchronized (mbox) {
                loadChanges();
                checkResults(req);
                processEvents(res);
                state.setSequence(mbox.getLastChangeID());
                state.setRevision(res.getRevision());
                req = hasChanges() ? newSyncRequest() : null;
            }
        }
        state.save();
    }

    private void resetData() throws ServiceException {
        // TODO Push any local changes that may be pending
        LOG.debug("Resetting local contacts");
        synchronized (mbox) {
            mbox.emptyFolder(CONTEXT, Mailbox.ID_FOLDER_CONTACTS, true);
            state.delete();
            pushedContacts.clear();
        }
    }
    
    private void loadChanges() throws ServiceException {
        int seq = state.getSequence();
        deletedContactIds.clear();
        modifiedContactIds.clear();
        if (seq > 0) {
            List<Integer> tombstones = mbox.getTombstones(seq).getIds(MailItem.TYPE_CONTACT);
            if (tombstones != null && !tombstones.isEmpty()) {
                deletedContactIds.addAll(tombstones);
            }
            modifiedContactIds.addAll(mbox.getModifiedItems(
                CONTEXT, seq, MailItem.TYPE_CONTACT).getFirst());
        }
        LOG.debug("Loaded contact changes: seq=%d, tombstones=%d, changes=%d",
                   seq, deletedContactIds.size(), modifiedContactIds.size());
    }

    private boolean hasChanges() {
        return !deletedContactIds.isEmpty() && !modifiedContactIds.isEmpty();
    }

    private SyncRequest newSyncRequest() throws SyncException, ServiceException {
        SyncRequest req = session.createSyncRequest(state.getRevision());
        pushedItemIds.clear();
        for (int itemId : deletedContactIds) {
            int cid = state.getContactId(itemId);
            if (cid != -1) {
                req.addEvent(SyncRequestEvent.removeContact(cid));
                pushedItemIds.add(0);
            }
        }
        for (int itemId : modifiedContactIds) {
            ContactData newData = getContactData(itemId);
            int cid = state.getContactId(itemId);
            if (cid != -1) {
                // Update existing contact
                ContactData oldData = pushedContacts.get(itemId);
                if (oldData == null) {
                    Contact oldContact = state.getContact(itemId);
                    if (oldContact == null) {
                        throw syncError("Missing original contact data for itemId=%d, cid=%d", itemId, cid);
                    }
                    oldData = new ContactData(oldContact);
                }
                ContactChange cc = newData.getContactChange(cid, oldData);
                if (!cc.isEmpty()) {
                    req.addEvent(SyncRequestEvent.updateContact(cc));
                    pushedItemIds.add(0);
                    pushedContacts.put(itemId, newData);
                }
            } else {
                // Add new contact
                req.addEvent(SyncRequestEvent.addContact(newData.getContact()));
                pushedItemIds.add(itemId);
                pushedContacts.put(itemId, newData);
            }
        }
        return req;
    }

    private ContactData getContactData(int id)
        throws ServiceException {
        MailItem contact = mbox.getItemById(CONTEXT, id, MailItem.TYPE_CONTACT);
        return contact != null ?
            new ContactData((com.zimbra.cs.mailbox.Contact) contact) : null;
    }

    private boolean isChanged(int cid) {
        return deletedContactIds.contains(cid) || modifiedContactIds.contains(cid);
    }

    private void processEvents(SyncResponse res) throws SyncException, ServiceException {
        int errors = 0;
        for (SyncResponseEvent event : res.getEvents()) {
            try {
                processEvent(event);
            } catch (Exception e) {
                LOG.error("Error processing event:\n%s", event, e);
                errors++;
            }
        }
        if (errors > 0) {
            throw syncError("%d sync response events could not be processed" +
                            " due to errors", errors);
        }
    }

    private void processEvent(SyncResponseEvent event)
        throws SyncException, ServiceException {
        Contact contact = event.getContact();
        switch (event.getType()) {
        case ADD_CONTACT: case UPDATE_CONTACT:
            updateContact(contact);
            break;
        case REMOVE_CONTACT:
            removeContact(contact.getId());
            break;
        case ADDRESS_BOOK_RESET:
            // TODO Check if this can ever be received after initial sync
            break;
        }
    }

    // YAB sometimes sends an "add-contact" event even for updated contacts,
    // so rely on presence of existing contact to determine if contact needs
    // to be updated or created.
    private void updateContact(Contact contact) throws SyncException, ServiceException {
        int cid = contact.getId();
        int itemId = state.getContactItemId(cid);
        if (itemId != -1) {
            // Update existing contact unless it has been modified since we
            // sent the sync request, in which case we delay updating the
            // contact until we've had a chance to push the new changes.
            if (!isChanged(cid)) {
                pushedContacts.remove(itemId);
                ContactData cd = new ContactData(contact);
                mbox.modifyContact(CONTEXT, itemId, cd.getParsedContact());
                state.updateContact(contact);
                LOG.debug("Modified local contact: itemId=%d, cid=%d", itemId, cid);
            }
        } else {
            // Add new contact
            ContactData cd = new ContactData(contact);
            MailItem item = mbox.createContact(
                CONTEXT, cd.getParsedContact(), Mailbox.ID_FOLDER_CONTACTS, null);
            state.addContact(item.getId(), cid);
            state.updateContact(contact);
            LOG.debug("Created new local contact: itemId=%d, cid=%d", item.getId(), cid);
        }
    }

    private void removeContact(int cid) throws SyncException, ServiceException {
        int itemId = state.getContactItemId(cid);
        if (itemId == -1) {
            throw syncError("Attempt to remove contact with unknown cid %d" + cid);
        }
        pushedContacts.remove(itemId);
        mbox.delete(CONTEXT, itemId, MailItem.TYPE_CONTACT);
        deletedContactIds.remove(itemId);
        modifiedContactIds.remove(itemId);
        state.removeContact(itemId);
        LOG.debug("Deleted local contact: itemId=%d, cid=%d", itemId, cid);
    }

    private void checkResults(SyncRequest req) throws SyncException {
        List<SyncRequestEvent> events = req.getEvents();
        for (int i = 0; i < events.size(); i++) {
            SyncRequestEvent event = events.get(i);
            Result result = event.getResult();
            if (result == null) {
                throw syncError("Missing YAB result for event:\n%s", event);
            } else if (result.isError()) {
                ErrorResult error = (ErrorResult) result;
                throw syncError("YAB SyncResponse error (code = %d): %s",
                                error.getCode(), error.getUserMessage());
            }
            SuccessResult success = (SuccessResult) result;
            if (success.getAddAction() != null) {
                int itemId = pushedItemIds.get(i);
                if (itemId > 0) {
                    state.addContact(itemId, success.getContactId());
                }
            }
        }
    }

    private SyncException syncError(String fmt, Object... args) {
        return new SyncException(String.format(fmt, args));
    }

    public Mailbox getMailbox() { return mbox; }
    public Session getSession() { return session; }
    public Mailbox.OperationContext getContext() { return CONTEXT; }
}