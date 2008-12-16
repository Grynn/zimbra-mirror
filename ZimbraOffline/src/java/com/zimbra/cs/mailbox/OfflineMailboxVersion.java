package com.zimbra.cs.mailbox;

import com.zimbra.common.service.ServiceException;

public class OfflineMailboxVersion {

    private static final short CURRENT_VERSION = 2;

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
