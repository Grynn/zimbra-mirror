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
package org.jivesoftware.wildfire.net;

import org.jivesoftware.wildfire.ConnectionManager;
import org.jivesoftware.wildfire.ServerPort;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Abstract class for {@link BlockingAcceptingMode} and {@link NonBlockingAcceptingMode}.
 *
 * @author Gaston Dombiak
 */
abstract class SocketAcceptingMode {

    /**
     * True while this thread should continue running.
     */
    protected boolean notTerminated = true;

    /**
     * Holds information about the port on which the server will listen for connections.
     */
    protected ServerPort serverPort;

    /**
     * socket that listens for connections.
     */
    protected ServerSocket serverSocket;

    protected ConnectionManager connManager;

    protected SocketAcceptingMode(ConnectionManager connManager, ServerPort serverPort) {
        this.connManager = connManager;
        this.serverPort = serverPort;
    }

    public abstract void run();

    public void shutdown() {
        notTerminated = false;
        try {
            ServerSocket sSock = serverSocket;
            serverSocket = null;
            if (sSock != null) {
                sSock.close();
            }
        }
        catch (IOException e) {
            // we don't care, no matter what, the socket should be dead
        }
    }
}
