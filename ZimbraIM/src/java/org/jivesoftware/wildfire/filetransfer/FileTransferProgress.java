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

import java.util.concurrent.Future;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An interface to track the progress of a file transfer through the server. This interface is used
 * by {@link FileTransfer} to make this information available if it is in the system.
 *
 * @author Alexander Wenckus
 */
public interface FileTransferProgress {
    public long getAmountTransfered() throws UnsupportedOperationException;

    /**
     * Returns the fully qualified JID of the initiator of the file transfer.
     *
     * @return the fully qualified JID of the initiator of the file transfer.
     */
    public String getInitiator();

    public void setInitiator(String initiator);

    /**
     * Returns the full qualified JID of the target of the file transfer.
     *
     * @return the fully qualified JID of the target
     */
    public String getTarget();

    public void setTarget(String target);

    /**
     * Returns the unique session id that correlates to the file transfer.
     *
     * @return Returns the unique session id that correlates to the file transfer.
     */
    public String getSessionID();

    public void setSessionID(String streamID);

    /**
     * When the file transfer is being caried out by another thread this will set the Future
     * relating to the thread that is carrying out the transfer.
     *
     * @param future the furute that is carrying out the transfer
     */
    public void setTransferFuture(Future<?> future);

    public void setInputStream(InputStream initiatorInputStream);

    public InputStream getInputStream();

    public void setOutputStream(OutputStream targetOutputStream);

    public OutputStream getOutputStream();
}
