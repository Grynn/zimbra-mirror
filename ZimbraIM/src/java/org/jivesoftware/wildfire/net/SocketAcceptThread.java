/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.net;

import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.wildfire.ConnectionManager;
import org.jivesoftware.wildfire.ServerPort;

import java.io.IOException;

/**
 * Implements a network front end with a dedicated thread reading
 * each incoming socket. Blocking and non-blocking modes are supported.
 * By default blocking mode is used. Use the <i>xmpp.socket.blocking</i>
 * system property to change the blocking mode. Restart the server after making
 * changes to the system property.
 *
 * @author Gaston Dombiak
 */
public class SocketAcceptThread extends Thread {

    /**
     * The default XMPP port for clients.
     */
    public static final int DEFAULT_PORT = 5222;

    /**
     * The default XMPP port for external components.
     */
    public static final int DEFAULT_COMPONENT_PORT = 10015;

    /**
     * The default XMPP port for server2server communication.
     */
    public static final int DEFAULT_SERVER_PORT = 5269;

    /**
     * The default XMPP port for connection multiplex.
     */
    public static final int DEFAULT_MULTIPLEX_PORT = 5262;

    /**
     * Holds information about the port on which the server will listen for connections.
     */
    private ServerPort serverPort;

    private SocketAcceptingMode acceptingMode;

    public SocketAcceptThread(ConnectionManager connManager, ServerPort serverPort)
            throws IOException {
        super("Socket Listener at port " + serverPort.getPort());
//        // Listen on a specific network interface if it has been set.
//        String interfaceName = 
//        InetAddress bindInterface = null;
//        if (interfaceName != null) {
//            if (interfaceName.trim().length() > 0) {
//                bindInterface = InetAddress.getByName(interfaceName);
//                // Create the new server port based on the new bind address
//                serverPort = new ServerPort(serverPort.getPort(),
//                        serverPort.getDomainNames().get(0), interfaceName, serverPort.isSecure(),
//                        serverPort.getSecurityType(), serverPort.getType());
//            }
//        }
        this.serverPort = serverPort;
        // Set the blocking reading mode to use
        boolean useBlockingMode = JiveGlobals.getBooleanProperty("xmpp.socket.blocking", true);
        if (useBlockingMode) {
            acceptingMode = new BlockingAcceptingMode(connManager, serverPort);
        }
        else {
            acceptingMode = new NioAcceptingMode(connManager, serverPort);
        }
    }

    /**
     * Retrieve the port this server socket is bound to.
     *
     * @return the port the socket is bound to.
     */
    public int getPort() {
        return serverPort.getPort();
    }

    /**
     * Returns information about the port on which the server is listening for connections.
     *
     * @return information about the port on which the server is listening for connections.
     */
    public ServerPort getServerPort() {
        return serverPort;
    }

    /**
     * Unblock the thread and force it to terminate.
     */
    public void shutdown() {
        acceptingMode.shutdown();
    }

    /**
     * About as simple as it gets.  The thread spins around an accept
     * call getting sockets and handing them to the SocketManager.
     */
    public void run() {
        acceptingMode.run();
        // We stopped accepting new connections so close the listener
//        shutdown();
    }
}
