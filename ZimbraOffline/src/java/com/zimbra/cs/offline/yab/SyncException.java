package com.zimbra.cs.offline.yab;

/**
 * Indicates a failed sync operation.
 */
public class SyncException extends Exception {
    public SyncException(String msg) {
        super(msg);
    }
}
