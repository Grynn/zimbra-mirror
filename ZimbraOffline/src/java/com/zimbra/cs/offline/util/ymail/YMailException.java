/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009 Zimbra, Inc.
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
package com.zimbra.cs.offline.util.ymail;

import java.io.IOException;

public class YMailException extends IOException {
    private YMailError error;

    public YMailException(String msg, Throwable cause) {
        super(msg);
        initCause(cause);
    }

    public YMailException(String msg) {
        super(msg);
    }

    public void setError(YMailError error) {
        this.error = error;
    }
    
    public YMailError getError() {
        return error;
    }

    public boolean isRetriable() {
        return error != null && error.isRetriable();
    }
}
