/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.ab;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Metadata;

public class SyncState {
    private int lastModSequence;
    private String lastRevisionContact;
    private String lastRevisionGroup;
    private String lastContactURL;

    private static final String VERSION = "7";

    private static final String KEY_VERSION = "VERSION";
    private static final String KEY_SEQUENCE = "SEQUENCE";
    private static final String KEY_REVISION = "REVISION";
    private static final String KEY_GROUP_REVISION = "REVISIONGRP";
    private static final String KEY_CONTACTURL = "CONTACTURL";

    static boolean isCompatibleVersion(Metadata md) throws ServiceException {
        return md == null || VERSION.equals(md.get(KEY_VERSION));
    }

    void load(Metadata md) throws ServiceException {
        if (md == null) return;
        if (!isCompatibleVersion(md)) {
            throw new IllegalStateException("Incompatible sync version");
        }
        lastModSequence = (int) md.getLong(KEY_SEQUENCE);
        lastRevisionContact = md.get(KEY_REVISION, null);
        lastRevisionGroup = md.get(KEY_GROUP_REVISION, null);
        lastContactURL = md.get(KEY_CONTACTURL, null);
    }

    Metadata getMetadata() {
        Metadata md = new Metadata();
        md.put(KEY_VERSION, VERSION);
        md.put(KEY_SEQUENCE, lastModSequence);
        if (lastRevisionContact != null)
            md.put(KEY_REVISION, lastRevisionContact);
        if (lastRevisionGroup != null)
            md.put(KEY_GROUP_REVISION, lastRevisionGroup);
        if (lastContactURL != null)
            md.put(KEY_CONTACTURL, lastContactURL);
        return md;
    }

    public String getLastRevision() { return lastRevisionContact; }
    public String getlastRevisionGroup() { return lastRevisionGroup; }
    public String getlastContactURL() { return lastContactURL; }
    public int getLastModSequence() { return lastModSequence; }

    public void setLastRevision(String lastRevisionContact) {
        this.lastRevisionContact = lastRevisionContact;
    }

    public void setLastRevisionGroup(String lastRevisionGroup) {
        this.lastRevisionGroup = lastRevisionGroup;
    }

    public void setLastModSequence(int lastModSequence) {
        this.lastModSequence = lastModSequence;
    }

    public void setLastContactURL(String lastContactURL) {
        this.lastContactURL = lastContactURL;
    }

    @Override
    public String toString() {
        return String.format(
            "{lastModSequence=%d, lastRevisionContact=%s, lastRevisionGroup=%s, lastContactURL=%s}", lastModSequence,
            lastRevisionContact, lastRevisionGroup, lastContactURL);
    }
}
