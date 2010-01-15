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
package org.jivesoftware.wildfire.filetransfer.proxy;

import org.jivesoftware.util.Cacheable;
import org.jivesoftware.wildfire.filetransfer.FileTransferProgress;

import java.io.IOException;

/**
 * Tracks the different connections related to a proxy file transfer. There are two connections, the
 * initiator and the target and when both connections are completed the transfer can begin.
 */
public interface ProxyTransfer extends Cacheable, FileTransferProgress {

    /**
     * Sets the transfer digest for a file transfer. The transfer digest uniquely identifies a file
     * transfer in the system.
     *
     * @param digest the digest which uniquely identifies this transfer.
     */
    public void setTransferDigest(String digest);

    /**
     * Returns the transfer digest uniquely identifies a file transfer in the system.
     *  
     * @return the transfer digest uniquely identifies a file transfer in the system.
     */
    public String getTransferDigest();

    /**
     * Returns true if the Bytestream is ready to be activated and the proxy transfer can begin.
     *
     * @return true if the Bytestream is ready to be activated.
     */
    public boolean isActivatable();

    /**
     * Transfers the file from the initiator to the target.
     */
    public void doTransfer() throws IOException;
}
