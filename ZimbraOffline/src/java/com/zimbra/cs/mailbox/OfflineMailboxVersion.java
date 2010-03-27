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
package com.zimbra.cs.mailbox;

import com.zimbra.common.service.ServiceException;

public class OfflineMailboxVersion {

    private static final short CURRENT_VERSION = 3;

    private short version;

    static OfflineMailboxVersion CURRENT() {
        return new OfflineMailboxVersion();
    }
                    
    OfflineMailboxVersion() {
        version = CURRENT_VERSION;
    }
    
    OfflineMailboxVersion(short version) {
        this.version = version;
    }
    
    OfflineMailboxVersion(OfflineMailboxVersion other) {
        version = other.version;
    }

    static OfflineMailboxVersion fromMetadata(Metadata md) throws ServiceException {
        short ver = 1; // unknown version are set to 1
        if (md != null)
            ver = (short) md.getLong("ver", 1);
        return new OfflineMailboxVersion(ver);
    }

    void writeToMetadata(Metadata md) {
        md.put("ver", version);
    }
    
    public boolean atLeast(int version) {
        return this.version >= version;
    }

    public boolean atLeast(OfflineMailboxVersion b) {
        return atLeast(b.version);
    }

    public boolean isLatest() {
        return version == CURRENT_VERSION;
    }

    public boolean tooHigh() {
        return version >CURRENT_VERSION;
    }

    public String toString() {
        return Short.toString(version);
    }
}
