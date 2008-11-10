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
            "[lastModSequence=%d, lastRevision=%s]", lastModSequence, lastRevision);
    }
}
