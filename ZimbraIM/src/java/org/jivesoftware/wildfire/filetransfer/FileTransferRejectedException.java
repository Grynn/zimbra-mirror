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
package org.jivesoftware.wildfire.filetransfer;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Thrown by a FileTransferInterceptor when a file transfer is rejected my the Interceptor. The file
 * transfer is aborted and the participating parties are notified.
 *
 * @author Alexander Wenckus
 */
public class FileTransferRejectedException extends Exception {

    private static final long serialVersionUID = 1L;

    private Throwable nestedThrowable = null;

    /**
     * Text to include in a message that will be sent to the sender of the packet that got
     * rejected. If no text is specified then no message will be sent to the user.
     */
    private String rejectionMessage;

    public FileTransferRejectedException() {
        super();
    }

    public FileTransferRejectedException(String msg) {
        super(msg);
    }

    public FileTransferRejectedException(Throwable nestedThrowable) {
        this.nestedThrowable = nestedThrowable;
    }

    public FileTransferRejectedException(String msg, Throwable nestedThrowable) {
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

    /**
     * Retuns the text to include in a message that will be sent to the intitiator and target
     * of the file transfer that got rejected or <tt>null</tt> if none was defined. If no text was
     * specified then no message will be sent to the parties of the rejected file transfer.
     *
     * @return the text to include in a message that will be sent to the parties of the file
     * transfer that got rejected or <tt>null</tt> if none was defined.
     */
    public String getRejectionMessage() {
        return rejectionMessage;
    }

    /**
     * Sets the text to include in a message that will be sent to the intiator and target of the
     * file transfer that got rejected or <tt>null</tt> if no message will be sent to the parties
     * of the rejected file transfer. Bt default, no message will be sent.
     *
     * @param rejectionMessage the text to include in the notification message for the rejection.
     */
    public void setRejectionMessage(String rejectionMessage) {
        this.rejectionMessage = rejectionMessage;
    }
}
