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

public class SyncFailure {
    private int itemId;
    private Exception exception;
    private String message;
    public SyncFailure(int itemId, Exception exception, String message) {
        super();
        this.itemId = itemId;
        this.exception = exception;
        this.message = message;
    }
    public int getItemId() {
        return itemId;
    }
    public Exception getException() {
        return exception;
    }
    public String getMessage() {
        return message;
    }
}
