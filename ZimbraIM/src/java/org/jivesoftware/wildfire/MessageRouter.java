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

import org.jivesoftware.wildfire.auth.UnauthorizedException;
import org.jivesoftware.wildfire.container.BasicModule;
import org.jivesoftware.util.IMConfig;
import org.jivesoftware.util.Log;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketError;

import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <p>Route message packets throughout the server.</p>
 * <p>Routing is based on the recipient and sender addresses. The typical
 * packet will often be routed twice, once from the sender to some internal
 * server component for handling or processing, and then back to the router
 * to be delivered to it's final destination.</p>
 *
 * @author Iain Shigeoka
 */
public class MessageRouter extends BasicModule {

    private OfflineMessageStrategy messageStrategy;
    private RoutingTable routingTable;
    private SessionManager sessionManager;
    private MulticastRouter multicastRouter;

    /**
     * Constructs a message router.
     */
    public MessageRouter() {
        super("XMPP Message Router");
    }
    
    private Collection<String> getServerNames() {
        return XMPPServer.getInstance().getLocalDomains();
    }

    /**
     * <p>Performs the actual packet routing.</p>
     * <p>You routing is considered 'quick' and implementations may not take
     * excessive amounts of time to complete the routing. If routing will take
     * a long amount of time, the actual routing should be done in another thread
     * so this method returns quickly.</p>
     * <h2>Warning</h2>
     * <p>Be careful to enforce concurrency DbC of concurrent by synchronizing
     * any accesses to class resources.</p>
     *
     * @param packet The packet to route
     * @throws NullPointerException If the packet is null
     */
    public void route(Message packet) {
        if (packet == null) {
            throw new NullPointerException();
        }
        Session session = sessionManager.getSession(packet.getFrom());
        if (session == null
                || session.getStatus() == Session.STATUS_AUTHENTICATED)
        {
            JID recipientJID = packet.getTo();

            // Check if the message was sent to the server hostname
            if (recipientJID != null && recipientJID.toBareJID() == null &&
                    recipientJID.getResource() == null &&
                    getServerNames().contains(recipientJID.getDomain())) {
                if (packet.getElement().element("addresses") != null) {
                    // Message includes multicast processing instructions. Ask the multicastRouter
                    // to route this packet
                    multicastRouter.route(packet);
                }
                else {
                    // Message was sent to the server hostname so forward it to a configurable
                    // set of JID's (probably admin users)
                    sendMessageToAdmins(packet);
                }
                return;
            }

            try {
//                ChannelHandler route = routingTable.getBestRoute(recipientJID);
//                route.process(packet);
                // Check if message was sent to a bare JID of a local user
                if (recipientJID != null && recipientJID.getResource() == null &&
                                XMPPServer.getInstance().isLocalDomain(recipientJID.getDomain())) {
                    routeToBareJID(recipientJID, packet);
                }
                else {
                    // Deliver stanza to best route
                    routingTable.getBestRoute(recipientJID).process(packet);
                }
            }
            catch (Exception e) {
                try {
                    messageStrategy.storeOffline(packet);
                }
                catch (Exception e1) {
                    Log.error(e1);
                }
            }

        }
        else {
            packet.setTo(session.getAddress());
            packet.setFrom((JID)null);
            packet.setError(PacketError.Condition.not_authorized);
            try {
                session.process(packet);
            }
            catch (UnauthorizedException ue) {
                Log.error(ue);
            }
        }
    }

    /**
     * Deliver the message sent to the bare JID of a local user to the best connected resource. If the
     * target user is not online then messages will be stored offline according to the offline strategy.
     * However, if the user is connected from only one resource then the message will be delivered to
     * that resource. In the case that the user is connected from many resources the logic will be the
     * following:
     * <ol>
     *  <li>Select resources with highest priority</li>
     *  <li>Select resources with highest show value (chat, available, away, xa, dnd)</li>
     *  <li>Select resource with most recent activity</li>
     * </ol>
     *
     * @param recipientJID the bare JID of the target local user.
     * @param packet the message to send.
     */
    private void routeToBareJID(JID recipientJID, Message packet) {
        List<ClientSession> sessions = sessionManager.getHighestPrioritySessions(recipientJID.getNode());
        if (sessions.isEmpty()) {
            try {
                // Just fall back to the routing table...(cloud routing)
                routingTable.getBestRoute(recipientJID).process(packet);
            } catch (Exception e) {
                try {
                    messageStrategy.storeOffline(packet);
                }
                catch (Exception e1) {
                    Log.error(e1);
                }
            }
        }
        else if (sessions.size() == 1) {
            // Found only one session so deliver message
            sessions.get(0).process(packet);
        } 
        else {
            // Deliver stanza to all connected resources with highest priority
            for (ClientSession session : sessions) {
                session.process(packet);
            }
        }
    }
    
    
    /**
     * Forwards the received message to the list of users defined in the property
     * <b>XMPP_FORWARD_ADMINS</b>. The property may include bare JIDs or just usernames separated
     * by commas or white spaces. When using bare JIDs the target user may belong to a remote
     * server.<p>
     *
     * If the property <b>XMPP_FORWARD_ADMINS</b> was not defined then the message will be sent
     * to all the users allowed to enter the admin console.
     *
     * @param packet the message to forward.
     */
    private void sendMessageToAdmins(Message packet) {
        String jids = IMConfig.XMPP_FORWARD_ADMINS.getString();
        if (jids != null && jids.trim().length() > 0) {
            // Forward the message to the users specified in the "XMPP_FORWARD_ADMINS" property
            StringTokenizer tokenizer = new StringTokenizer(jids, ", ");
            while (tokenizer.hasMoreTokens()) {
                String username = tokenizer.nextToken();
                Message forward = packet.createCopy();
                if (username.contains("@")) {
                    // Use the specified bare JID address as the target address
                    forward.setTo(username);
                } else {
                    Log.error("Could not forward packet to admin '"+username+
                    "' because bare JIDs are not allowed in XMPP_FORWARD_ADMINS");
                }
                route(forward);
            }
        }
        else {
            // Forward the message to the users allowed to log into the admin console
            for (JID jid : XMPPServer.getInstance().getAdmins()) {
                Message forward = packet.createCopy();
                forward.setTo(jid);
                route(forward);
            }
        }
    }

    public void initialize(XMPPServer server) {
        super.initialize(server);
        messageStrategy = server.getOfflineMessageStrategy();
        routingTable = server.getRoutingTable();
        sessionManager = server.getSessionManager();
        multicastRouter = server.getMulticastRouter();
    }
}
