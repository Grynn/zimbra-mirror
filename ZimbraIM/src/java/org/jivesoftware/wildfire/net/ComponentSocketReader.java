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
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.PacketRouter;
import org.jivesoftware.wildfire.RoutingTable;
import org.jivesoftware.wildfire.XMPPServer;
import org.jivesoftware.wildfire.auth.UnauthorizedException;
import org.jivesoftware.wildfire.component.ComponentSession;
import org.jivesoftware.wildfire.component.InternalComponentManager;
import org.xmlpull.v1.XmlPullParserException;
import org.xmpp.component.ComponentException;
import org.xmpp.packet.PacketError;

import java.io.IOException;
import java.net.Socket;

/**
 * A SocketReader specialized for component connections. This reader will be used when the open
 * stream contains a jabber:component:accept namespace.
 *
 * @author Gaston Dombiak
 */
public class ComponentSocketReader extends SocketReader {

    public ComponentSocketReader(PacketRouter router, RoutingTable routingTable,
                Socket socket, SocketConnection connection) {
        super(router, routingTable, socket, connection);
    }
    
    public ComponentSocketReader(PacketRouter router, RoutingTable routingTable,
                IoSession nioSocket, SocketConnection connection) {
        super(router, routingTable, nioSocket, connection);
        }
    

    /**
     * Only <tt>bind<tt> packets will be processed by this class to bind more domains
     * to existing external components. Any other type of packet is unknown and thus
     * rejected generating the connection to be closed.
     *
     * @param doc the unknown DOM element that was received
     * @return false if packet is unknown otherwise true.
     */
    protected boolean processUnknowPacket(Element doc) {
        // Handle subsequent bind packets
        if ("bind".equals(doc.getName())) {
            ComponentSession componentSession = (ComponentSession) session;
            // Get the external component of this session
            ComponentSession.ExternalComponent component = componentSession.getExternalComponent();
            String initialDomain = component.getInitialSubdomain();
            String extraDomain = doc.attributeValue("name");
            if (extraDomain == null || "".equals(extraDomain)) {
                // No new bind domain was specified so return a bad_request error
                Element reply = doc.createCopy();
                reply.add(new PacketError(PacketError.Condition.bad_request).getElement());
                connection.deliverRawText(reply.asXML());
            }
            else if (extraDomain.equals(initialDomain)) {
                // Component is binding initial domain that is already registered
                // Send confirmation that the new domain has been registered
                connection.deliverRawText("<bind/>");
            }
            else if (extraDomain.endsWith(initialDomain)) {
                // Only accept subdomains under the initial registered domain
                if (component.getSubdomains().contains(extraDomain)) {
                    // Domain already in use so return a conflict error
                    Element reply = doc.createCopy();
                    reply.add(new PacketError(PacketError.Condition.conflict).getElement());
                    connection.deliverRawText(reply.asXML());
                }
                else {
                    try {
                        // Get the requested subdomain
                        String subdomain = extraDomain;
                        int index = -1;
                        for (String serverName : XMPPServer.getInstance().getLocalDomains()) {
                            index = extraDomain.indexOf(serverName);
                            if (index > -1)
                                break;
                        }
                        if (index > -1) {
                            subdomain = extraDomain.substring(0, index -1);
                        }
                        InternalComponentManager.getInstance().addComponent(subdomain, component);
                        // Send confirmation that the new domain has been registered
                        connection.deliverRawText("<bind/>");
                    }
                    catch (ComponentException e) {
                        Log.error("Error binding extra domain: " + extraDomain + " to component: " +
                                component, e);
                        // Return internal server error
                        Element reply = doc.createCopy();
                        reply.add(new PacketError(
                                PacketError.Condition.internal_server_error).getElement());
                        connection.deliverRawText(reply.asXML());
                    }
                }
            }
            else {
                // Return forbidden error since we only allow subdomains of the intial domain
                // to be used by the same external component
                Element reply = doc.createCopy();
                reply.add(new PacketError(PacketError.Condition.forbidden).getElement());
                connection.deliverRawText(reply.asXML());
            }
            return true;
        }
        // This is an unknown packet so return false (and close the connection)
        return false;
    }

    boolean createSession(String namespace, String host, Element streamElt) throws UnauthorizedException, XmlPullParserException,
            IOException {
        if ("jabber:component:accept".equals(namespace)) {
            // The connected client is a component so create a ComponentSession
            session = ComponentSession.createSession(host, connection, streamElt);
            return true;
        }
        return false;
    }

    String getNamespace() {
        return "jabber:component:accept";
    }

    String getName() {
        return "Component SR - " + hashCode();
    }

    boolean validateHost() {
        return false;
    }
}
