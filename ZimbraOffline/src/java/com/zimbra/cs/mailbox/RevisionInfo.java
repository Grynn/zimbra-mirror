/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.mailbox;

public class RevisionInfo {
    private int version;
    private long timestamp;
    private int folderId;
    public RevisionInfo(int version, long timestamp, int folderId) {
        super();
        this.version = version;
        this.timestamp = timestamp;
        this.folderId = folderId;
    }
    public int getVersion() {
        return version;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public int getFolderId() {
        return folderId;
    }
}
