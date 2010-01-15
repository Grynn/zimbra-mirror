/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.util.yab;

public class YabException extends Exception {
    private final ErrorResult error;

    public YabException(ErrorResult error) {
        super(error.toString());
        this.error = error;
    }

    public YabException(String msg) {
        super(msg);
        error = null;
    }

    public ErrorResult getErrorResult() {
        return error;
    }
}
