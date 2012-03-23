/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2011 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.clientuploader;

/**
 * <code>ZClientUploaderException</code> will be thrown if error occurs in this extension.
 *
 * @author Dongwei Feng
 * @since 2012.3.14
 */
public class ZClientUploaderException extends Exception {
    private ZClientUploaderRespCode code;

    public ZClientUploaderException(ZClientUploaderRespCode code) {
        super();
        this.code = code;
    }

    public ZClientUploaderException(ZClientUploaderRespCode code, Throwable t) {
        super(t);
        this.code = code;
    }

    public ZClientUploaderException(ZClientUploaderRespCode code, String msg) {
        super(msg);
        this.code = code;
    }

    public ZClientUploaderException(ZClientUploaderRespCode code, String msg, Throwable t) {
        super(msg, t);
        this.code = code;
    }

    public ZClientUploaderRespCode getRespCode() {
        return code;
    }
    
    @Override
    public String toString() {
        return code + ", " + super.toString();
    }
}
