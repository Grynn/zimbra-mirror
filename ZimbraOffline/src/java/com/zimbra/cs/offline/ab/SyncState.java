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

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Metadata;

public class SyncState {
    private int lastModSequence;
    private String lastRevision;

    private static final String VERSION = "2";

    private static final String KEY_VERSION = "VERSION";
    private static final String KEY_SEQUENCE = "SEQUENCE";
    private static final String KEY_REVISION = "REVISION";

    static boolean isCompatibleVersion(Metadata md) throws ServiceException {
        return md == null || VERSION.equals(md.get(KEY_VERSION));
    }
    
    void load(Metadata md) throws ServiceException {
        if (md == null) return;
        if (!isCompatibleVersion(md)) {
            throw new IllegalStateException("Incompatible sync version");
        }
        lastModSequence = (int) md.getLong(KEY_SEQUENCE);
        lastRevision = md.get(KEY_REVISION);
    }

    Metadata getMetadata() {
        Metadata md = new Metadata();
        md.put(KEY_VERSION, VERSION);
        md.put(KEY_SEQUENCE, lastModSequence);
        md.put(KEY_REVISION, lastRevision);
        return md;
    }

    public String getLastRevision() { return lastRevision; }
    public int getLastModSequence() { return lastModSequence; }

    public void setLastRevision(String lastRevision) {
        this.lastRevision = lastRevision;
    }

    public void setLastModSequence(int lastModSequence) {
        this.lastModSequence = lastModSequence;
    }

    @Override
    public String toString() {
        return String.format(
            "{lastModSequence=%d, lastRevision=%s}", lastModSequence, lastRevision);
    }
}
