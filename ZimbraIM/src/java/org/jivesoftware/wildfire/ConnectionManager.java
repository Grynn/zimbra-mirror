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
package org.jivesoftware.wildfire;

import org.apache.mina.common.IoSession;
import org.jivesoftware.wildfire.net.SocketReader;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;

/**
 * Coordinates connections (accept, read, termination) on the server.
 *
 * @author Iain Shigeoka
 */
public interface ConnectionManager {

    /**
     * Returns an array of the ports managed by this connection manager.
     *
     * @return an iterator of the ports managed by this connection manager
     *      (can be an empty but never null).
     */
    public Iterator<ServerPort> getPorts();

    /**
     * Creates a new blocking-mode socket reader for the new accepted socket to be managed
     * by the connection manager.
     *
     * @param socket the new accepted socket by this manager.
     * @param serverPort holds information about the port on which the server is listening for
     *        connections.
     * @param useBlockingMode true means that the server will use a thread per connection.
     */
    public SocketReader createSocketReader(Socket socket, boolean isSecure, ServerPort serverPort)
    throws IOException;
    
    /**
     * Creates a new nio-mode socket reader for the new accepted socket to be managed
     * by the connection manager.
     *
     * @param nioSocket the newly accepted IoSession object
     * @param serverPort holds information about the port on which the server is listening for
     *        connections.
     * @param useBlockingMode true means that the server will use a thread per connection.
     */
    public SocketReader createSocketReader(IoSession nioSocket, boolean isSecure, 
                ServerPort serverPort) throws IOException;
    
    /**
     * Sets if the port listener for unsecured clients will be available or not. When disabled
     * there won't be a port listener active. Therefore, new clients won't be able to connect to
     * the server.
     *
     * @param enabled true if new unsecured clients will be able to connect to the server.
     */
//    public void enableClientListener(boolean enabled);

    /**
     * Returns true if the port listener for unsecured clients is available. When disabled
     * there won't be a port listener active. Therefore, new clients won't be able to connect to
     * the server.
     *
     * @return true if the port listener for unsecured clients is available.
     */
//    public boolean isClientListenerEnabled();

    /**
     * Sets if the port listener for secured clients will be available or not. When disabled
     * there won't be a port listener active. Therefore, new secured clients won't be able to
     * connect to the server.
     *
     * @param enabled true if new secured clients will be able to connect to the server.
     */
//    public void enableClientSSLListener(boolean enabled);

    /**
     * Returns true if the port listener for secured clients is available. When disabled
     * there won't be a port listener active. Therefore, new secured clients won't be able to
     * connect to the server.
     *
     * @return true if the port listener for unsecured clients is available.
     */
//    public boolean isClientSSLListenerEnabled();

    /**
     * Sets if the port listener for external components will be available or not. When disabled
     * there won't be a port listener active. Therefore, new external components won't be able to
     * connect to the server.
     *
     * @param enabled true if new external components will be able to connect to the server.
     */
//    public void enableComponentListener(boolean enabled);

    /**
     * Returns true if the port listener for external components is available. When disabled
     * there won't be a port listener active. Therefore, new external components won't be able to
     * connect to the server.
     *
     * @return true if the port listener for external components is available.
     */
//    public boolean isComponentListenerEnabled();

    /**
     * Sets if the port listener for remote servers will be available or not. When disabled
     * there won't be a port listener active. Therefore, new remote servers won't be able to
     * connect to the server.
     *
     * @param enabled true if new remote servers will be able to connect to the server.
     */
//    public void enableServerListener(boolean enabled);

    /**
     * Returns true if the port listener for remote servers is available. When disabled
     * there won't be a port listener active. Therefore, new remote servers won't be able to
     * connect to the server.
     *
     * @return true if the port listener for remote servers is available.
     */
//    public boolean isServerListenerEnabled();

//    /**
//     * Sets if the port listener for connection managers will be available or not. When disabled
//     * there won't be a port listener active. Therefore, clients will need to connect directly
//     * to the server.
//     *
//     * @param enabled true if new connection managers will be able to connect to the server.
//     */
//    public void enableConnectionManagerListener(boolean enabled);

    /**
     * Returns true if the port listener for connection managers is available. When disabled
     * there won't be a port listener active. Therefore, clients will need to connect directly
     * to the server.
     *
     * @return true if the port listener for connection managers is available.
     */
//    public boolean isConnectionManagerListenerEnabled();

    /**
     * Sets the port to use for unsecured clients. Default port: 5222.
     *
     * @param port the port to use for unsecured clients.
     */
//    public void setClientListenerPort(int port);

    /**
     * Returns the port to use for unsecured clients. Default port: 5222.
     *
     * @return the port to use for unsecured clients.
     */
//    public int getClientListenerPort();

    /**
     * Sets the port to use for secured clients. Default port: 5223.
     *
     * @param port the port to use for secured clients.
     */
//    public void setClientSSLListenerPort(int port);

    /**
     * Returns the port to use for secured clients. Default port: 5223.
     *
     * @return the port to use for secured clients.
     */
//    public int getClientSSLListenerPort();

    /**
     * Sets the port to use for external components.
     *
     * @param port the port to use for external components.
     */
//    public void setComponentListenerPort(int port);

    /**
     * Returns the port to use for external components.
     *
     * @return the port to use for external components.
     */
//    public int getComponentListenerPort();

    /**
     * Sets the port to use for remote servers. This port is used for remote servers to connect
     * to this server. Default port: 5269.
     *
     * @param port the port to use for remote servers.
     */
//    public void setServerListenerPort(int port);

    /**
     * Returns the port to use for remote servers. This port is used for remote servers to connect
     * to this server. Default port: 5269.
     *
     * @return the port to use for remote servers.
     */
//    public int getServerListenerPort();

    /**
     * Sets the port to use for connection managers. This port is used for connection managers
     * to connect to this server. Default port: 5262.
     *
     * @param port the port to use for connection managers.
     */
//    public void setConnectionManagerListenerPort(int port);

    /**
     * Returns the port to use for remote servers. This port is used for connection managers
     * to connect to this server. Default port: 5262.
     *
     * @return the port to use for connection managers.
     */
//    public int getConnectionManagerListenerPort();
}
