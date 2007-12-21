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

import java.io.IOException;
import java.net.Socket;

import org.apache.mina.common.IoSession;
import org.dom4j.Element;
import org.jivesoftware.wildfire.PacketRouter;
import org.jivesoftware.wildfire.RoutingTable;
import org.jivesoftware.wildfire.Session;
import org.jivesoftware.wildfire.auth.UnauthorizedException;
import org.xmlpull.v1.XmlPullParserException;

/**
 * 
 */
public class CloudRoutingSocketReader extends SocketReader {
    
    public static interface CloudRoutingSessionFactory {
        Session createSession(String hostname, CloudRoutingSocketReader reader, SocketConnection connection, Element streamElt); 
    }
    
    private static CloudRoutingSessionFactory sSessionFact = null;
    
    public static void setSessionFactory(CloudRoutingSessionFactory fact) {
        sSessionFact = fact;
    }

    /**
     * @param router
     * @param routingTable
     * @param nioSocket
     * @param connection
     */
    public CloudRoutingSocketReader(PacketRouter router, RoutingTable routingTable, IoSession nioSocket,
        SocketConnection connection) {
        super(router, routingTable, nioSocket, connection);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param router
     * @param routingTable
     * @param socket
     * @param connection
     */
    public CloudRoutingSocketReader(PacketRouter router, RoutingTable routingTable, Socket socket,
        SocketConnection connection) {
        super(router, routingTable, socket, connection);
        // TODO Auto-generated constructor stub
    }

    @Override
    boolean createSession(String namespace, String hostname, Element streamElt) throws UnauthorizedException,
        XmlPullParserException, IOException {
        if (getNamespace().equals(namespace)) {
            if (sSessionFact != null)
                session = sSessionFact.createSession(hostname, this, connection, streamElt);
            return session != null;
        }
        return false;
    }

    @Override
    String getName() {
        return "CloudRoutingSR - " + hashCode();
    }

    @Override
    String getNamespace() {
        return "jabber:cloudrouting";
    }

    @Override
    boolean processUnknowPacket(Element doc) {
        return false;
    }

    @Override
    boolean validateHost() {
        return false;
    }
    
    @Override
    protected boolean shouldInvokeInterceptor() { return false; }

}
