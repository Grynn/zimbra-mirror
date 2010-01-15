/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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
package org.jivesoftware.wildfire.muc;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Exception used for representing a conflict error in a MUCRoom. There are many reasons why a 
 * conflict error could occur such as: owner tries to remove all the owners of a room. A 409 error
 * code is returned to the user that requested the invalid operation.
 *
 * @author Gaston Dombiak
 */
public class ConflictException extends Exception {

    private static final long serialVersionUID = 1L;

    private Throwable nestedThrowable = null;

    public ConflictException() {
        super();
    }

    public ConflictException(String msg) {
        super(msg);
    }

    public ConflictException(Throwable nestedThrowable) {
        this.nestedThrowable = nestedThrowable;
    }

    public ConflictException(String msg, Throwable nestedThrowable) {
        super(msg);
        this.nestedThrowable = nestedThrowable;
    }

    public void printStackTrace() {
        super.printStackTrace();
        if (nestedThrowable != null) {
            nestedThrowable.printStackTrace();
        }
    }

    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
        if (nestedThrowable != null) {
            nestedThrowable.printStackTrace(ps);
        }
    }

    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        if (nestedThrowable != null) {
            nestedThrowable.printStackTrace(pw);
        }
    }
}
