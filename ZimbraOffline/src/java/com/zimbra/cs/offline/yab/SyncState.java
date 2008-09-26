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
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.mailbox.MetadataList;
import com.zimbra.cs.offline.util.yab.Category;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;

import java.util.Map;
import java.util.HashMap;

public class SyncState {
    private final SyncSession sync;
    private int revision;  // YAB revision number
    private int sequence;  // Zimbra mailbox last change id
    private final Map<Integer, Integer> contactIdByItemId;
    private final Map<Integer, Integer> itemIdByContactId;
    private final Map<Integer, Integer> itemIdByCategoryId;
    private final Map<Integer, Category> categoryByItemId;

    private static final Log LOG = OfflineLog.yab;

    private static final int VERSION = 1;

    private static final String KEY_YAB = "YAB";
    private static final String KEY_VERSION = "VERSION";
    private static final String KEY_REVISION = "REVISION";
    private static final String KEY_SEQUENCE = "SEQUENCE";
    private static final String KEY_CIDS = "CIDS";
    private static final String KEY_CATIDS = "CATIDS";
    private static final String KEY_CATEGORIES = "CATEGORIES";

    public SyncState(SyncSession sync) {
        this.sync = sync;
        contactIdByItemId = new HashMap<Integer, Integer>();
        itemIdByContactId = new HashMap<Integer, Integer>();
        itemIdByCategoryId = new HashMap<Integer, Integer>();
        categoryByItemId = new HashMap<Integer, Category>();
    }

    public void load() throws ServiceException {
        clear();
        Mailbox mbox = sync.getMailbox();
        Metadata md = mbox.getConfig(sync.getContext(), KEY_YAB);
        if (md == null) return;
        int version = (int) md.getLong(KEY_VERSION);
        if (version != VERSION) {
            throw new IllegalStateException("Incompatible sync state version");
        }
        revision = (int) md.getLong(KEY_REVISION);
        sequence = (int) md.getLong(KEY_SEQUENCE);
        MetadataList cids = md.getList(KEY_CIDS);
        for (int i = 0; i < cids.size(); i += 2) {
            int itemId = (int) cids.getLong(i);
            int cid = (int) cids.getLong(i + 1);
            contactIdByItemId.put(itemId, cid);
            itemIdByContactId.put(cid, itemId);
        }
        MetadataList catids = md.getList(KEY_CATIDS);
        MetadataList categories = md.getList(KEY_CATEGORIES);
        for (int i = 0; i < catids.size(); i += 2) {
            int itemId = (int) catids.getLong(i);
            int catid = (int) catids.getLong(i + 1);
            String name = categories.get(i >> 1);
            Category cat = new Category(name, catid);
            categoryByItemId.put(itemId, cat);
            itemIdByCategoryId.put(catid, itemId);
        }
        LOG.debug("Loaded sync state: %s", this);
    }

    public void save() throws ServiceException {
        Metadata md = new Metadata();
        md.put(KEY_VERSION, VERSION);
        md.put(KEY_REVISION, revision);
        md.put(KEY_SEQUENCE, sequence);
        MetadataList cids = new MetadataList();
        for (Map.Entry<Integer, Integer> e : contactIdByItemId.entrySet()) {
            int itemId = e.getKey();
            cids.add(itemId);
            cids.add(e.getValue());
        }
        md.put(KEY_CIDS, cids);
        MetadataList catids = new MetadataList();
        MetadataList categories = new MetadataList();
        for (Map.Entry<Integer, Category> e : categoryByItemId.entrySet()) {
            int itemId = e.getKey();
            Category cat = e.getValue();
            int catid = cat.getId();
            if (catid == -1) {
                throw new IllegalStateException("Missing category id for item id: " + itemId);
            }
            String name = cat.getName();
            if (name == null) {
                throw new IllegalStateException("Missing category name for item id: " + itemId);
            }
            catids.add(itemId);
            catids.add(catid);
            categories.add(name);
        }
        md.put(KEY_CATIDS, catids);
        md.put(KEY_CATEGORIES, categories);
        sync.getMailbox().setConfig(sync.getContext(), KEY_YAB, md);
        LOG.debug("Saved sync state: %s", this);
    }

    public void delete() throws ServiceException {
        sync.getMailbox().setConfig(sync.getContext(), KEY_YAB, null);
        clear();
    }

    public void clear() {
        revision = 0;
        sequence = 0;
        contactIdByItemId.clear();
        itemIdByContactId.clear();
        itemIdByCategoryId.clear();
        categoryByItemId.clear();
    }

    public int getRevision() { return revision; }
    public int getSequence() { return sequence; }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getContactId(int itemId) {
        return getInt(contactIdByItemId, itemId);
    }

    public int getContactItemId(int contactId) {
        return getInt(itemIdByContactId, contactId);
    }

    public void addCategory(int itemId, int categoryId) {
        if (getCategory(itemId) != null) {
            throw new IllegalArgumentException("Duplicate category id");
        }
        Category cat = new Category(categoryId);
        categoryByItemId.put(itemId, cat);
        itemIdByCategoryId.put(categoryId, itemId);
        LOG.debug("Added category for itemid=%d, catid=%d", itemId, categoryId);
    }

    public Map<Integer, Category> getCategories() {
        return new HashMap<Integer, Category>(categoryByItemId);
    }

    public Category getCategory(int itemId) {
        return categoryByItemId.get(itemId);
    }

    public int getCategoryItemId(int categoryId) {
        return getInt(itemIdByCategoryId, categoryId);
    }
    
    public void removeCategory(int itemId) {
        Category cat = categoryByItemId.remove(itemId);
        if (cat != null) {
            itemIdByCategoryId.remove(cat.getId());
            LOG.debug("Removed category for itemId=%d, catid=%d", itemId, cat.getId());
        }
    }

    public void updateCategoryName(int itemId, String name) {
        Category cat = getCategory(itemId);
        if (cat == null) {
            cat = new Category();
            categoryByItemId.put(itemId, cat);
        }
        cat.setName(name);
        LOG.debug("Updated category name for itemId=%d, newName=%s", itemId, name);
    }
    
    private static int getInt(Map<Integer, Integer> map, int key) {
        Integer value = map.get(key);
        return value != null ? value : -1;
    }

    public void addContact(int itemId, int contactId) {
        if (getContactId(itemId) != -1) {
            throw new IllegalArgumentException("Duplicate contact id");
        }
        contactIdByItemId.put(itemId, contactId);
        itemIdByContactId.put(contactId, itemId);
        LOG.debug("Added contact for itemId=%d, cid=%d", itemId, contactId);
    }


    public void removeContact(int itemId) {
        Integer cid = contactIdByItemId.remove(itemId);
        if (cid != null) {
            itemIdByContactId.remove(cid);
        }
        LOG.debug("Removed contact for itemId=%d, cid=%d", itemId, cid);
    }

    public String toString() {
        return String.format("[rev=%d,seq=%d,contacts=%d,categories=%d]",
            revision, sequence, contactIdByItemId.size(), categoryByItemId.size());
    }
}
