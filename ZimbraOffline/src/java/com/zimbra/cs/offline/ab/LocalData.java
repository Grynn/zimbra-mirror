/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.ab;

import com.zimbra.cs.mailbox.ChangeTrackingMailbox;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.DesktopMailbox;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.SyncExceptionHandler;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.db.DbDataSource;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public final class LocalData {
    private final OfflineDataSource ds;
    private final DesktopMailbox mbox;
    private final String key;
    private final Log log;

    private static final String KEY_GAB = "GAB";
    private static final String KEY_YAB = "YAB";

    public static final OperationContext CONTEXT =
        new ChangeTrackingMailbox.TracelessContext();

    public LocalData(OfflineDataSource ds) throws ServiceException {
        this.ds = ds;
        this.mbox = (DesktopMailbox) DataSourceManager.getInstance().getMailbox(ds);
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

    public boolean hasLocalChanges() throws ServiceException {
        SyncState ss = loadState();
        if (ss == null) return true;
        int seq = ss.getLastModSequence();
        return seq <= 0 || getModifiedContacts(seq).size() > 0 ||
            getTombstones(seq, MailItem.TYPE_CONTACT).size() > 0;
    }
    
    public Map<Integer, Change> getContactChanges(int seq)
        throws ServiceException {
        Map<Integer, Change> changes = new HashMap<Integer, Change>();
        // Get modified and deleted contacts
        for (int id : getModifiedContacts(seq)) {
            int folderId = getContact(id).getFolderId();
            if (hasMapping(id)) {
                if (isContactsFolder(folderId)) {
                    changes.put(id, Change.update(id));
                } else {
                    // Delete contact moved outside of contacts folder
                    changes.put(id, Change.delete(id));
                }
            } else if (isContactsFolder(folderId)) {
                changes.put(id, Change.add(id));
            }
        }
        for (int id : getTombstones(seq, MailItem.TYPE_CONTACT)) {
            if (hasMapping(id)) {
                changes.put(id, Change.delete(id));
            }
        }
        return changes;
    }

    private boolean isContactsFolder(int id) {
        return id == Mailbox.ID_FOLDER_CONTACTS ||
               id == Mailbox.ID_FOLDER_AUTO_CONTACTS && syncAutoContacts();
    }
    
    private boolean syncAutoContacts() {
        return ds.isGmail();
    }

    private boolean hasMapping(int id) throws ServiceException {
        return DbDataSource.hasMapping(ds, id);
    }

    private List<Integer> getTombstones(int seq, byte type)
        throws ServiceException {
        List<Integer> ids = mbox.getTombstones(seq).getIds(type);
        return ids != null ? ids : Collections.<Integer>emptyList();
    }

    private List<Integer> getModifiedContacts(int seq) throws ServiceException {
        return mbox.getModifiedItems(CONTEXT, seq, MailItem.TYPE_CONTACT).getFirst();
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
        Metadata md = null;
        if (data != null) {
            md = new Metadata();
            md.put(key, data);
        }
        DataSourceItem dsi = new DataSourceItem(-1, itemId, remoteId, md);
        if (DbDataSource.hasMapping(ds, itemId)) {
            DbDataSource.updateMapping(ds, dsi);
        } else {
            DbDataSource.addMapping(ds, dsi);
        }
    }

    public Collection<DataSourceItem> getAllContactMappings()
        throws ServiceException {
        // Excludes auto-contacts which have no groups
        return DbDataSource.getAllMappingsInFolder(ds, Mailbox.ID_FOLDER_CONTACTS);
    }

    public Contact getContact(int id) throws ServiceException {
        return mbox.getContactById(CONTEXT, id);
    }

    public Contact createContact(ParsedContact pc) throws ServiceException {
        return createContact(pc, Mailbox.ID_FOLDER_CONTACTS);
    }
    
    public Contact createContact(ParsedContact pc, int folderId) throws ServiceException {
        Contact contact = mbox.createContact(CONTEXT, pc, folderId, null);
        log.debug("Created new contact: id = %d", contact.getId());
        return contact;
    }

    public void modifyContact(int id, ParsedContact pc) throws ServiceException {
        log.debug("Updating contact: id = %d", id);
        mbox.modifyContact(CONTEXT, id, pc);
    }

    public void deleteContact(int id) throws ServiceException {
        try {
            Contact contact = getContact(id);
            // Don't delete contacts moved outside contact folder
            if (isContactsFolder(contact.getFolderId())) {
                mbox.delete(CONTEXT, id, MailItem.TYPE_CONTACT);
                log.debug("Deleted contact: id = %d", id);
            }
        } catch (MailServiceException.NoSuchItemException e) {
        }
    }

    public void moveContact(int id, int targetFolderId) throws ServiceException {
        log.debug("Moving contact id %d -> folder %d", id, targetFolderId);
        mbox.move(null, id, MailItem.TYPE_CONTACT, targetFolderId);
    }
    
    public void deleteMissingContacts(Set<String> remoteIds) throws ServiceException {
        for (DataSourceItem dsi : getAllContactMappings()) {
            if (!remoteIds.contains(dsi.remoteId)) {
                deleteMapping(dsi.itemId);
                deleteContact(dsi.itemId);
            }
        }
    }
    
    public ContactGroup getContactGroup(int id) throws ServiceException {
        try {
            Contact contact = getContact(id);
            if (ContactGroup.isContactGroup(contact)) {
                return new ContactGroup(contact);
            }
        } catch (MailServiceException.NoSuchItemException e) {
        }
        return null;
    }

    public void modifyContactGroup(int id, ContactGroup group)
        throws ServiceException {
        log.debug("Updating contact group: id = " + id);
        mbox.modifyContact(CONTEXT, id, group.getParsedContact());
    }
    
    public int createContactGroup(String remoteId, ContactGroup group)
        throws ServiceException {
        Contact contact = createContact(group.getParsedContact());
        updateMapping(contact.getId(), remoteId, null);
        log.debug("Created new contact group: item id = %d, remote id = %s, name = %s",
                  contact.getId(), remoteId, group.getName());
        return contact.getId();
    }

    public void deleteContactGroup(int id) throws ServiceException {
        log.debug("Deleting contact group: id = " + id);
        mbox.delete(CONTEXT, id, MailItem.TYPE_CONTACT);
        deleteMapping(id);
    }

    public SyncState loadState() throws ServiceException {
        Metadata md = mbox.getConfig(CONTEXT, key);
        if (md == null) {
            log.debug("Created initial sync state for data source '%s'", ds.getName());
            return new SyncState();
        }
        if (SyncState.isCompatibleVersion(md)) {
            SyncState ss = new SyncState();
            ss.load(md);
            log.debug("Loaded sync state for data source '%s': %s", ds.getName(), ss);
            return ss;
        }
        return null;
    }

    public void saveState(SyncState ss) throws ServiceException {
        log.debug("Saving sync state for data source '%s': %s", ds.getName(), ss);
        mbox.setConfig(CONTEXT, key, ss.getMetadata());
    }

    public void resetData() throws ServiceException {
        log.info("Resetting address book data for data source: %s", ds.getName());
        synchronized (mbox) {
            mbox.emptyFolder(CONTEXT, Mailbox.ID_FOLDER_CONTACTS, true);
            if (syncAutoContacts()) {
                mbox.emptyFolder(CONTEXT, Mailbox.ID_FOLDER_AUTO_CONTACTS, true);
            }
            mbox.setConfig(CONTEXT, key, null);
            DbDataSource.deleteAllMappingsInFolder(ds, Mailbox.ID_FOLDER_CONTACTS);
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

