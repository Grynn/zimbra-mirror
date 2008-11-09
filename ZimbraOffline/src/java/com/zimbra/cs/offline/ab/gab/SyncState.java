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

import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.common.util.Log;
import com.zimbra.common.service.ServiceException;
import com.google.gdata.data.DateTime;

public class SyncState {
    private DateTime lastSyncTime;
    private int lastModSequence;

    private static final Log LOG = OfflineLog.gab;

    private static final int VERSION = 1;
    
    private static final String KEY_GAB = "GAB";
    private static final String KEY_VERSION = "VERSION";
    private static final String KEY_TIMESTAMP = "TIMESTAMP";
    private static final String KEY_SEQUENCE = "SEQUENCE";

    public SyncState(Mailbox mbox) throws ServiceException {
        load(mbox);
    }
    
    private void load(Mailbox mbox) throws ServiceException {
        Metadata md = mbox.getConfig(SyncSession.CONTEXT, KEY_GAB);
        if (md == null) return;
        int version = (int) md.getLong(KEY_VERSION);
        if (version != VERSION) {
            throw new IllegalStateException("Incompatible sync state version");
        }
        String ts = md.get(KEY_TIMESTAMP);
        lastSyncTime = ts != null ? DateTime.parseDateTime(ts) : null;
        lastModSequence = (int) md.getLong(KEY_SEQUENCE);
        LOG.debug("Loaded sync state: %s", this);
    }

    public void save(Mailbox mbox) throws ServiceException {
        Metadata md = new Metadata();
        md.put(KEY_VERSION, VERSION);
        md.put(KEY_TIMESTAMP, lastSyncTime.toString());
        md.put(KEY_SEQUENCE, lastModSequence);
        mbox.setConfig(SyncSession.CONTEXT, KEY_GAB, md);
        LOG.debug("Saved sync state: " + this);
    }

    public DateTime getLastSyncTime() { return lastSyncTime; }
    
    public int getLastModSequence() { return lastModSequence; }

    public void setLastSyncTime(DateTime lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public void setLastModSequence(int lastModSequence) {
        this.lastModSequence = lastModSequence;
    }

    public String toString() {
        return String.format("[lastUpdateTime=%s, lastModSequence=%d]",
            lastSyncTime, lastModSequence);
    }
}
