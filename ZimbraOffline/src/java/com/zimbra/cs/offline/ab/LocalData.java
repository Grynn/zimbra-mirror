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
package com.zimbra.cs.offline.ab;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Tag;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.DesktopMailbox;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.mailbox.SyncExceptionHandler;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.db.DbDataSource;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;

import java.util.List;
import java.util.Collections;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import static com.zimbra.cs.mailbox.Mailbox.*;
import static com.zimbra.cs.mailbox.MailItem.*;

public final class LocalData {
    private final OfflineDataSource ds;
    private final DesktopMailbox mbox;
    private final String key;
    private final Log log;

    private static final String KEY_GAB = "GAB";
    private static final String KEY_YAB = "YAB";

    public static final Mailbox.OperationContext CONTEXT =
        new OfflineMailbox.OfflineContext();

    private static final Set<Integer> CONTACT_FOLDERS =
        new HashSet<Integer>(Arrays.asList(ID_FOLDER_CONTACTS));
    
    public LocalData(OfflineDataSource ds) throws ServiceException {
        this.ds = ds;
        this.mbox = (DesktopMailbox) ds.getMailbox();
        key = getKey(ds);
        log = getLog(ds);
    }

    private static String getKey(OfflineDataSource ds) {
        if (ds.isYahoo()) return KEY_YAB;
        if (ds.isGmail()) return KEY_GAB;
        throw new IllegalArgumentException(
            "Address book sync not supported for specified data source");
    }
    
    private static Log getLog(OfflineDataSource ds) {
        if (ds.isYahoo()) return OfflineLog.yab;
        if (ds.isGmail()) return OfflineLog.gab;
        return OfflineLog.offline;
    }
    
    public Map<Integer, Change> getContactChanges(int seq)
        throws ServiceException {
        Map<Integer, Change> changes = new HashMap<Integer, Change>();
        // Get modified and deleted contacts
        for (int id : getModifiedContacts(seq)) {
            changes.put(id, hasMapping(id) ? Change.update(id) : Change.add(id));
        }
        for (int id : getTombstones(seq, MailItem.TYPE_CONTACT)) {
            if (hasMapping(id)) {
                changes.put(id, Change.delete(id));
            }
        }
        return changes;
    }
    
    public Map<Integer, Change> getTagChanges(int seq)
        throws ServiceException {
        // Get modified and deleted tags
        Map<Integer, Change> changes = new HashMap<Integer, Change>();
        for (Tag tag : getModifiedTags(seq)) {
            int id = tag.getId();
            changes.put(id, hasMapping(id) ? Change.update(id) : Change.add(id));
        }
        for (int id : getTombstones(seq, MailItem.TYPE_TAG)) {
            if (hasMapping(id)) {
                changes.put(id, Change.delete(id));
            }
        }
        return changes;
    }

    private boolean hasMapping(int id) throws ServiceException {
        return DbDataSource.hasMapping(ds, id);
    }

    private List<Integer> getTombstones(int seq, byte type)
        throws ServiceException {
        List<Integer> ids = mbox.getTombstones(seq).getIds(type);
        return ids != null ? ids : Collections.<Integer>emptyList();
    }

    private List<Tag> getModifiedTags(int seq) throws ServiceException {
        List<Tag> tags = mbox.getModifiedTags(CONTEXT, seq);
        Set<Integer> ctags = getContactTags(getModifiedContacts(seq));
        // Ignore tag changes not associated with modified contacts
        for (Iterator<Tag> it = tags.iterator(); it.hasNext(); ) {
            Tag tag = it.next();
            if (!ctags.contains(tag.getId())) {
                it.remove();
            }
        }
        return tags;
    }

    private List<Integer> getModifiedContacts(int seq) throws ServiceException {
        return mbox.getModifiedItems(
            CONTEXT, seq, TYPE_CONTACT, CONTACT_FOLDERS).getFirst();
    }
    
    private Set<Integer> getContactTags(Collection<Integer> contactIds)
        throws ServiceException {
        Set<Integer> tags = new HashSet<Integer>();
        for (int id : contactIds) {
            Contact contact = getContact(id);
            for (Tag tag : contact.getTagList()) {
                tags.add(tag.getId());
            }
        }
        return tags;
    }

    public String getData(DataSourceItem dsi) throws ServiceException {
        return dsi.md != null ? dsi.md.get(key) : null;
    }

    public DataSourceItem getReverseMapping(String remoteId)
        throws ServiceException {
        return DbDataSource.getReverseMapping(ds, remoteId);
    }

    public DataSourceItem getMapping(int itemId) throws ServiceException {
        return DbDataSource.getMapping(ds, itemId);
    }
    
    public void deleteMapping(int itemId) throws ServiceException {
        log.debug("Deleting entry for item: id = %d", itemId);
        DbDataSource.deleteMappings(ds, Arrays.asList(itemId));
    }

    public void updateMapping(int itemId, String remoteId, String data)
        throws ServiceException {
        log.debug("Updating entry for item: item id = %d, remote id = %s",
                  itemId, remoteId);
        Metadata md = new Metadata();
        md.put(key, data);
        DataSourceItem dsi = new DataSourceItem(itemId, remoteId, md);
        if (DbDataSource.hasMapping(ds, itemId)) {
            DbDataSource.updateMapping(ds, dsi);
        } else {
            DbDataSource.addMapping(ds, dsi);
        }
    }

    public Collection<DataSourceItem> getAllMappingsInFolder(int folderId)
        throws ServiceException {
        return DbDataSource.getAllMappingsInFolder(ds, folderId);
    }
    
    public Contact getContact(int id) throws ServiceException {
        return mbox.getContactById(CONTEXT, id);
    }

    public Tag createTag(String name) throws ServiceException {
        log.debug("Creating tag: name = %s", name);
        String normalized = MailItem.normalizeItemName(name);
        if (!name.equals(normalized)) {
            log.warn("Normalizing tag name '%s' to '%s' since it contains " +
                     "invalid characters", name, normalized);
            name = normalized;
        }
        try {
            return mbox.getTagByName(name);
        } catch (MailServiceException.NoSuchItemException e) {
            return mbox.createTag(CONTEXT, name, Tag.DEFAULT_COLOR);
        }
    }

    public Tag getTag(int id) throws ServiceException {
        return mbox.getTagById(CONTEXT, id);
    }

    public String getTagName(int id) throws ServiceException {
        return getTag(id).getName();
    }

    public Contact createContact(ParsedContact pc, long tags)
        throws ServiceException {
        Contact contact = mbox.createContact(CONTEXT, pc, ID_FOLDER_CONTACTS, null);
        setContactTags(contact.getId(), tags);
        log.debug("Created new contact: id = %d, tags = %d", contact.getId(), tags);
        return contact;
    }

    public void modifyContact(int id, ParsedContact pc, long tags)
        throws ServiceException {
        log.debug("Modifying contact: id = %d, tags = %d", id, tags);
        mbox.modifyContact(CONTEXT, id, pc);
        setContactTags(id, tags);
    }

    public void deleteContact(int id) throws ServiceException {
        log.debug("Deleting contact: id = %d", id);
        mbox.delete(CONTEXT, id, TYPE_CONTACT);
    }

    public void setContactTags(int id, long bitmask) throws ServiceException {
        mbox.setTags(CONTEXT, id, TYPE_CONTACT, FLAG_UNCHANGED, bitmask);
    }
    
    public void renameTag(int id, String name) throws ServiceException {
        log.debug("Renaming tag: id = %d, new name = %s", id, name);
        mbox.rename(CONTEXT, id, TYPE_TAG, name, ID_FOLDER_TAGS);
    }

    public SyncState loadState() throws ServiceException {
        Metadata md = mbox.getConfig(CONTEXT, key);
        if (md != null && !SyncState.isCompatibleVersion(md)) {
            log.info("Sync state version change - resetting address book data");
            resetData();
            md = null;
        }
        SyncState ss = new SyncState();
        ss.load(md);
        log.debug("Loaded sync state for data source '%s': %s", ds.getName(), ss);
        return ss;
    }

    public void saveState(SyncState ss) throws ServiceException {
        log.debug("Saving sync state for data source '%s': %s", ds.getName(), ss);
        mbox.setConfig(CONTEXT, key, ss.getMetadata());
    }

    public void resetData() throws ServiceException {
        log.info("Resetting address book data for data source: %s", ds.getName());
        synchronized (mbox) {
            mbox.emptyFolder(CONTEXT, Mailbox.ID_FOLDER_CONTACTS, true);
            mbox.setConfig(CONTEXT, key, null);
            DbDataSource.deleteAllMappings(ds);
        }
    }

    public void syncContactFailed(Exception e, int itemId, String data)
        throws ServiceException {
        SyncExceptionHandler.checkRecoverableException("Contact sync failed", e);
        SyncExceptionHandler.syncContactFailed(mbox, itemId, data, e);
    }

    
    public OfflineDataSource getDataSource() { return ds; }
    public DesktopMailbox getMailbox() { return mbox; }
    public Log getLog() { return log; }
}

