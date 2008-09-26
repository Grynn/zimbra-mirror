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
import com.zimbra.cs.mailbox.Tag;
import com.zimbra.cs.mailbox.MailServiceException;
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
import com.zimbra.cs.offline.util.yab.Category;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.io.IOException;

public class SyncSession {
    private final Mailbox mbox;
    private final Session session;
    private final Set<Integer> deletedContacts;
    private final Set<Integer> modifiedContacts;
    private final Set<Integer> deletedTags;
    private final Set<Integer> modifiedTags;
    private final Map<Integer, ContactData> pushedContacts;
    private final Map<Integer, Integer> unmappedItemIds;
    private final SyncState state;

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
        deletedContacts = new HashSet<Integer>();
        modifiedContacts = new HashSet<Integer>();
        deletedTags = new HashSet<Integer>();
        modifiedTags = new HashSet<Integer>();
        pushedContacts = new HashMap<Integer, ContactData>();
        unmappedItemIds = new HashMap<Integer, Integer>();
        state = new SyncState(this);
    }

    public void sync() throws ServiceException {
        int count = 0;
        do {
            try {
                syncData();
                return;
            } catch (Exception e) {
                LOG.error("Contact sync error", e);
                resetData();
            }
        } while (count++ < MAX_RETRIES);
        throw ServiceException.FAILURE(
            "Unrecoverable contact sync error after " + MAX_RETRIES + " retry attempts", null);
    }
    
    private void syncData() throws IOException, ServiceException {
        pushedContacts.clear();
        state.load();
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
                checkResults(req.getEvents());
                List<SyncResponseEvent> events = res.getEvents();
                if (!events.isEmpty()) {
                    processCategories(res.getCategories());
                    processEvents(res.getEvents());
                }
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
        deletedContacts.clear();
        modifiedContacts.clear();
        deletedTags.clear();
        modifiedTags.clear();
        if (seq > 0) {
            for (int itemId : getTombstones(seq, MailItem.TYPE_CONTACT)) {
                if (state.getContactId(itemId) != -1) {
                    deletedContacts.add(itemId);
                }
            }
            modifiedContacts.addAll(mbox.getModifiedItems(
                CONTEXT, seq, MailItem.TYPE_CONTACT).getFirst());
            for (int itemId : getTombstones(seq, MailItem.TYPE_TAG)) {
                if (state.getCategory(itemId) != null) {
                    deletedTags.add(itemId);
                }
            }
            // Only include modified tags which belong to changed contacts
            Set<Integer> tags = getContactTags(modifiedContacts);
            for (Tag tag : mbox.getModifiedTags(CONTEXT, seq)) {
                int itemId = tag.getId();
                if (tags.contains(itemId)) {
                    modifiedTags.add(itemId);
                }
            }
        }
        LOG.debug("Loaded local contact changes: seq=%d, deleted_contact=%d, " +
                  "modified_contacts=%d, deleted_tags=%d, modified_tags=%d",
                   seq, deletedContacts.size(), modifiedContacts.size(),
                   deletedTags.size(), modifiedTags.size());
    }

    private List<Integer> getTombstones(int seq, byte type)
        throws ServiceException {
        List<Integer> ids = mbox.getTombstones(seq).getIds(type);
        return ids != null ? ids : Collections.<Integer>emptyList();
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
    
    private boolean hasChanges() {
        return !deletedContacts.isEmpty() || !modifiedContacts.isEmpty() &&
               !deletedTags.isEmpty() || !modifiedTags.isEmpty();
    }

    private SyncRequest newSyncRequest() throws SyncException, ServiceException {
        SyncRequest req = session.createSyncRequest(state.getRevision());
        Map<Integer, Category> categories = state.getCategories();
        unmappedItemIds.clear();
        for (int itemId : deletedTags) {
            Category cat = state.getCategory(itemId);
            if (cat != null) {
                req.addEvent(SyncRequestEvent.removeCategory(cat.getId()));
                state.removeCategory(itemId);
            }
        }
        for (int itemId : modifiedTags) {
            Tag tag = mbox.getTagById(CONTEXT, itemId);
            String name = tag.getName();
            Category cat = state.getCategory(itemId);
            if (cat != null) {
                String oldName = cat.getName();
                if (oldName != null && !name.equals(oldName)) {
                    req.addEvent(SyncRequestEvent.renameCategory(oldName, name));
                }
                state.updateCategoryName(itemId, name);
            } else {
                unmappedItemIds.put(req.getEvents().size(), itemId);
                req.addEvent(SyncRequestEvent.addCategory(name));
                // Category id will be determined when we check sync results
                state.updateCategoryName(itemId, name);
            }
        }
        for (int itemId : deletedContacts) {
            int cid = state.getContactId(itemId);
            if (cid != -1) {
                req.addEvent(SyncRequestEvent.removeContact(cid));
            }
        }
        for (int itemId : modifiedContacts) {
            ContactData newData = getContactData(itemId, categories);
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
                    pushedContacts.put(itemId, newData);
                }
            } else {
                // Add new contact
                unmappedItemIds.put(req.getEvents().size(), itemId);
                req.addEvent(SyncRequestEvent.addContact(newData.getContact()));
                pushedContacts.put(itemId, newData);
            }
        }

        return req;
    }

    private ContactData getContactData(int itemId, Map<Integer, Category> categories)
        throws ServiceException {
        MailItem contact = mbox.getItemById(CONTEXT, itemId, MailItem.TYPE_CONTACT);
        return contact != null ?
            new ContactData((com.zimbra.cs.mailbox.Contact) contact, categories) : null;
    }

    private boolean isChanged(int cid) {
        return deletedContacts.contains(cid) || modifiedContacts.contains(cid);
    }

    private void processCategories(List<Category> categories) throws ServiceException {
        Set<Integer> catItemIds = state.getCategories().keySet();
        for (Category category : categories) {
            int catid = category.getId();
            String name = category.getName();
            int itemId = state.getCategoryItemId(catid);
            if (itemId == -1) {
                Tag tag = createTag(name);
                itemId = tag.getId();
                state.addCategory(itemId, catid);
                state.updateCategoryName(itemId, name);
            } else {
                catItemIds.remove(itemId);
                Tag tag = mbox.getTagById(CONTEXT, itemId);
                // Check if renamed
                if (!tag.getName().equals(name)) {
                    mbox.rename(CONTEXT, itemId, MailItem.TYPE_TAG, name, Mailbox.ID_FOLDER_TAGS);
                    state.updateCategoryName(itemId, name);
                }
            }
        }
        // Remaining categories have been removed. Do not remove local tag,
        // since we cannot be sure that is not in use by non-contact mail
        // items.
        for (int itemId : catItemIds) {
            state.removeCategory(itemId);
        }
    }

    private Tag createTag(String name) throws ServiceException {
        try {
            return mbox.getTagByName(name);
        } catch (MailServiceException.NoSuchItemException e) {
            return mbox.createTag(CONTEXT, name, Tag.DEFAULT_COLOR);
        }
    }
    
    private void processEvents(List<SyncResponseEvent> events)
        throws SyncException, ServiceException {
        for (SyncResponseEvent event : events) {
            Contact contact = event.getContact();
            switch (event.getType()) {
            case ADD_CONTACT: case UPDATE_CONTACT:
                updateContact(contact);
                break;
            case REMOVE_CONTACT:
                removeContact(contact.getId());
                break;
            case ADDRESS_BOOK_RESET:
                // TODO Can this ever be received after initial sync?
                break;
            }
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
                long mask = getTagBitmask(cd);
                mbox.setTags(CONTEXT, itemId, MailItem.TYPE_CONTACT, MailItem.FLAG_UNCHANGED, mask);
                state.updateContact(contact);
                LOG.debug("Modified local contact: itemId=%d, cid=%d, tag_bits=%x", itemId, cid, mask);
            }
        } else {
            // Add new contact
            ContactData cd = new ContactData(contact);
            MailItem item = mbox.createContact(
                CONTEXT, cd.getParsedContact(), Mailbox.ID_FOLDER_CONTACTS, null);
            itemId = item.getId();
            long mask = getTagBitmask(cd);
            mbox.setTags(CONTEXT, itemId, MailItem.TYPE_CONTACT, MailItem.FLAG_UNCHANGED, mask);
            state.addContact(itemId, cid);
            state.updateContact(contact);
            LOG.debug("Created new local contact: itemId=%d, cid=%d, tag_bits=%x", itemId, cid, mask);
        }
    }

    private long getTagBitmask(ContactData cd) throws SyncException, ServiceException {
        long mask = 0;
        for (Category category : cd.getCategories()) {
            int cid = category.getId();
            if (cid == -1) {
                throw syncError("Missing id in category response:\n", category);
            }
            int itemId = state.getCategoryItemId(cid);
            if (itemId != -1) {
                Tag tag = mbox.getTagById(CONTEXT, itemId);
                mask |= tag.getBitmask();
            }
        }
        return mask;
    }
    
    private void removeContact(int cid) throws SyncException, ServiceException {
        int itemId = state.getContactItemId(cid);
        if (itemId == -1) {
            throw syncError("Attempt to remove contact with unknown cid %d" + cid);
        }
        pushedContacts.remove(itemId);
        mbox.delete(CONTEXT, itemId, MailItem.TYPE_CONTACT);
        deletedContacts.remove(itemId);
        modifiedContacts.remove(itemId);
        state.removeContact(itemId);
        LOG.debug("Deleted local contact: itemId=%d, cid=%d", itemId, cid);
    }

    private void checkResults(List<SyncRequestEvent> events) throws SyncException {
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
                Integer itemId = unmappedItemIds.remove(i);
                if (itemId != null) {
                    int cid = success.getContactId();
                    if (cid != -1) {
                        state.addContact(itemId, success.getContactId());
                    } else {
                        state.addCategory(itemId, success.getCategoryId());
                    }
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