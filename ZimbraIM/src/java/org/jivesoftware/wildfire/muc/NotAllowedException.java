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
 * Exception used for representing that the user is not allowed to perform the requested operation 
 * in the MUCRoom. There are many reasons why a not-allowed error could occur such as: a user tries 
 * to join a room that has reached its limit of max number of occupants. A 405 error code is 
 * returned to the user that requested the invalid operation.
 *
 * @author Gaston Dombiak
 */
public class NotAllowedException extends Exception {

    private static final long serialVersionUID = 1L;

    private Throwable nestedThrowable = null;

    public NotAllowedException() {
        super();
    }

    public NotAllowedException(String msg) {
        super(msg);
    }

    public NotAllowedException(Throwable nestedThrowable) {
        this.nestedThrowable = nestedThrowable;
    }

    public NotAllowedException(String msg, Throwable nestedThrowable) {
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
