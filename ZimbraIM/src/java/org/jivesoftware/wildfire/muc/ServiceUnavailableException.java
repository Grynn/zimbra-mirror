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
 * Exception used for representing that the MultiUserChat service is not available at the moment.
 * There are many reasons why a ServiceUnavailableException could occur such as: a user is trying
 * to join a room that has reached the max number of occupants.
 *
 * @author Gaston Dombiak
 */
public class ServiceUnavailableException extends Exception {

    private static final long serialVersionUID = 1L;

    private Throwable nestedThrowable = null;

    public ServiceUnavailableException() {
        super();
    }

    public ServiceUnavailableException(String msg) {
        super(msg);
    }

    public ServiceUnavailableException(Throwable nestedThrowable) {
        this.nestedThrowable = nestedThrowable;
    }

    public ServiceUnavailableException(String msg, Throwable nestedThrowable) {
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
