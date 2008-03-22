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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents a port on which the server will listen for connections.
 * Used to aggregate information that the rest of the system needs
 * regarding the port while hiding implementation details.
 *
 * @author Iain Shigeoka
 */
public class ServerPort {

    private int port;
    private List<String> names = new ArrayList<String>(1);
    private String address;
    private boolean secure;
    private String algorithm;
    private Type type;

//    public ServerPort(int port, String name, String address,
//                      boolean isSecure, String algorithm, Type type)
//    {
//        this.port = port;
//        this.names.add(name);
//        this.address = address;
//        this.secure = isSecure;
//        this.algorithm = algorithm;
//        this.type = type;
//    }
    
    public ServerPort(int port, Collection<String> names, String address,
                boolean isSecure, String algorithm, Type type)
    {
        this.port = port;
        this.names.addAll(names);
        this.address = address;
        this.secure = isSecure;
        this.algorithm = algorithm;
        this.type = type;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder("ServerPort("+(address!=null?address:"0.0.0.0")+":"+port+(secure?",secure":""));
        if (algorithm != null)
            sb.append(",algorithm:"+algorithm);
        sb.append(", type=").append(type.name());
        return sb.toString();
    }
    

    /**
     * Returns the port number that is being used.
     *
     * @return the port number this server port is listening on.
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the logical domains for this server port. As multiple
     * domains may point to the same server, this helps to define what
     * the server considers "local".
     *
     * @return the server domain name(s) as Strings.
     */
    public List<String> getDomainNames() {
        return Collections.unmodifiableList(names);
    }

    /**
     * Returns the dot separated IP address for the server.
     *
     * @return The dot separated IP address for the server
     */
    public InetAddress getBindAddress() throws IOException {
        if (address == null)
            return null;
        try {
            return InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            throw new IOException("Unable to bind to requested address "+address+" exception: "+e.toString());
        }
    }

    /**
     * Determines if the connection is secure.
     *
     * @return True if the connection is secure
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * Returns the basic protocol/algorithm being used to secure
     * the port connections. An example would be "SSL" or "TLS".
     *
     * @return The protocol used or null if this is not a secure server port
     */
    public String getSecurityType() {
        return algorithm;
    }

    /**
     * Returns true if other servers can connect to this port for s2s communication.
     *
     * @return true if other servers can connect to this port for s2s communication.
     */
    public boolean isServerPort() {
        return type == Type.server;
    }

    /**
     * Returns true if clients can connect to this port.
     *
     * @return true if clients can connect to this port.
     */
    public boolean isClientPort() {
        return type == Type.client;
    }

    /**
     * Returns true if external components can connect to this port.
     *
     * @return true if external components can connect to this port.
     */
    public boolean isComponentPort() {
        return type == Type.component;
    }
    
    public boolean isCloudRouter() {
        return type == Type.cloudRouter;
    }

    /**
     * Returns true if connection managers can connect to this port.
     *
     * @return true if connection managers can connect to this port.
     */
    public boolean isConnectionManagerPort() {
        return type == Type.connectionManager;
    }

    public Type getType() {
        return type;
    }

    public static enum Type {
        client,

        server,

        component,

        connectionManager,
        
        cloudRouter,
    }
}
