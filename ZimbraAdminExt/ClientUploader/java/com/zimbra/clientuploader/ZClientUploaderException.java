/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
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
