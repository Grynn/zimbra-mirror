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

import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.mailbox.MetadataList;
import com.zimbra.common.util.Log;
import com.zimbra.common.service.ServiceException;
import com.google.gdata.data.DateTime;

import java.util.Map;
import java.util.HashMap;

public class SyncState {
    private final SyncSession sync;             
    private DateTime lastUpdateTime;
    private int lastModSequence;
    private final Map<Integer, String> entryIdByItemId;
    private final Map<String, Integer> itemIdByEntryId;

    private static final Log LOG = OfflineLog.yab;

    private static final int VERSION = 1;
    
    private static final String KEY_GAB = "GAB";
    private static final String KEY_VERSION = "VERSION";
    private static final String KEY_TIMESTAMP = "TIMESTAMP";
    private static final String KEY_SEQUENCE = "SEQUENCE";
    private static final String KEY_IDS = "IDS";

    public SyncState(SyncSession sync) {
        this.sync = sync;
        entryIdByItemId = new HashMap<Integer, String>();
        itemIdByEntryId = new HashMap<String, Integer>();
    }

    public void load() throws ServiceException {
        clear();
        Mailbox mbox = sync.getMailbox();
        Metadata md = mbox.getConfig(sync.getContext(), KEY_GAB);
        if (md == null) return;
        int version = (int) md.getLong(KEY_VERSION);
        if (version != VERSION) {
            throw new IllegalStateException("Incompatible sync state version");
        }
        String ts = md.get(KEY_TIMESTAMP);
        lastUpdateTime = ts != null ? DateTime.parseDate(ts) : null;
        lastModSequence = (int) md.getLong(KEY_SEQUENCE);
        MetadataList ids = md.getList(KEY_IDS);
        for (int i = 0; i < ids.size(); i += 2) {
            int itemId = (int) ids.getLong(i);
            String entryId = ids.get(i + 1);
            entryIdByItemId.put(itemId, entryId);
            itemIdByEntryId.put(entryId, itemId);
        }
        LOG.debug("Loaded sync state: %s", this);
    }

    public void save() throws ServiceException {
        Metadata md = new Metadata();
        md.put(KEY_VERSION, VERSION);
        md.put(KEY_TIMESTAMP, lastUpdateTime.toString());
        md.put(KEY_SEQUENCE, lastModSequence);
        MetadataList ids = new MetadataList();
        for (Map.Entry<Integer, String> e : entryIdByItemId.entrySet()) {
            ids.add(e.getKey());
            ids.add(e.getValue());
        }
        md.put(KEY_IDS, ids);
        sync.getMailbox().setConfig(sync.getContext(), KEY_GAB, md);
        LOG.debug("Saved sync state: %s", this);
    }

    public void clear() {
        lastUpdateTime = null;
        lastModSequence = 0;
        entryIdByItemId.clear();
        itemIdByEntryId.clear();
    }

    public DateTime getLastUpdateTime() { return lastUpdateTime; }

    public int getLastModSequence() { return lastModSequence; }

    public void setLastUpdateTime(DateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setLastModSequence(int lastModSequence) {
        this.lastModSequence = lastModSequence;
    }

    public String getEntryId(int itemId) {
        return entryIdByItemId.get(itemId);
    }

    public int getItemId(String entryId) {
        Integer id = itemIdByEntryId.get(entryId);
        return id != null ? id : -1;
    }

    public boolean hasItem(int itemId) {
        return getEntryId(itemId) != null;
    }
    
    public void addEntry(int itemId, String entryId) {
        String oldEntryId = getEntryId(itemId);
        if (oldEntryId == null) {
            entryIdByItemId.put(itemId, entryId);
            itemIdByEntryId.put(entryId, itemId);
            LOG.debug("Added new entry for itemId=%d, entryId=%s", itemId, entryId);
        } else if (!entryId.equals(oldEntryId)) {
            throw new IllegalStateException("Inconsistent entry id: itemId=" +
                itemId + ", oldEntryId=" + oldEntryId + ", newEntryId=" + entryId);
        }
    }

    public void removeEntry(int itemId) {
        String entryId = entryIdByItemId.remove(itemId);
        if (entryId != null) {
            itemIdByEntryId.remove(entryId);
            LOG.debug("Removed entry for itemId=%d, entryId=%s", itemId, entryId);
        }
    }


    public String toString() {
        return String.format("[ts=%s, seq=%d, entries=%d",
            lastUpdateTime, lastModSequence, entryIdByItemId.size());
    }
}
