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

import org.apache.mina.common.IoSession;
import org.dom4j.Element;
import org.jivesoftware.util.IMConfig;
import org.jivesoftware.wildfire.ClientSession;
import org.jivesoftware.wildfire.PacketRouter;
import org.jivesoftware.wildfire.RoutingTable;
import org.jivesoftware.wildfire.auth.UnauthorizedException;
import org.xmlpull.v1.XmlPullParserException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Presence;

import java.io.IOException;
import java.net.Socket;

/**
 * A SocketReader specialized for client connections. This reader will be used when the open
 * stream contains a jabber:client namespace. Received packet will have their FROM attribute
 * overriden to avoid spoofing.<p>
 *
 * By default the hostname specified in the stream header sent by clients will not be validated.
 * When validated the TO attribute of the stream header has to match the server name or a valid
 * subdomain. If the value of the 'to' attribute is not valid then a host-unknown error
 * will be returned. To enable the validation set the system property
 * <b>XMPP_CLIENT_VALIDATE_HOST</b> to true.
 *
 * @author Gaston Dombiak
 */
public class ClientSocketReader extends SocketReader {

    public ClientSocketReader(PacketRouter router, RoutingTable routingTable, 
                Socket socket, SocketConnection connection) {
        super(router, routingTable, socket, connection);
    }

    public ClientSocketReader(PacketRouter router, RoutingTable routingTable, 
                IoSession nioSocket, SocketConnection connection) {
        super(router, routingTable, nioSocket, connection);
    }

    protected void processIQ(IQ packet) throws UnauthorizedException {
        // Overwrite the FROM attribute to avoid spoofing
        packet.setFrom(session.getAddress());
        super.processIQ(packet);
    }

    protected void processPresence(Presence packet) throws UnauthorizedException {
        // Overwrite the FROM attribute to avoid spoofing
        packet.setFrom(session.getAddress());
        super.processPresence(packet);
    }

    protected void processMessage(Message packet) throws UnauthorizedException {
        // Overwrite the FROM attribute to avoid spoofing
        packet.setFrom(session.getAddress());
        super.processMessage(packet);
    }

    /**
     * Only packets of type Message, Presence and IQ can be processed by this class. Any other
     * type of packet is unknown and thus rejected generating the connection to be closed.
     *
     * @param doc the unknown DOM element that was received
     * @return always false.
     */
    protected boolean processUnknowPacket(Element doc) {
        return false;
    }

    boolean createSession(String namespace, String host, Element streamElt) throws UnauthorizedException, XmlPullParserException, IOException {
        if ("jabber:client".equals(namespace)) {
            // The connected client is a regular client so create a ClientSession
            session = ClientSession.createSession(host, connection, streamElt);
            return true;
        }
        return false;
    }

    String getNamespace() {
        return "jabber:client";
    }

    String getName() {
        return "Client SR - " + hashCode();
    }

    boolean validateHost() {
        return IMConfig.XMPP_CLIENT_VALIDATE_HOST.getBoolean();
    }
}
