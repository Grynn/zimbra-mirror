/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.spi;

import org.jivesoftware.wildfire.*;
import org.jivesoftware.wildfire.container.BasicModule;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

/**
 * An uber router that can handle any packet type.<p>
 *
 * The interface is provided primarily as a convenience for services
 * that must route all packet types (e.g. s2s routing, e2e encryption, etc).
 *
 * @author Iain Shigeoka
 */
public class PacketRouterImpl extends BasicModule implements PacketRouter {

    private IQRouter iqRouter;
    private PresenceRouter presenceRouter;
    private MessageRouter messageRouter;

    /**
     * Constructs a packet router.
     */
    public PacketRouterImpl() {
        super("XMPP Packet Router");
    }

    /**
     * Routes the given packet based on packet recipient and sender. The
     * router defers actual routing decisions to other classes.
     * <h2>Warning</h2>
     * Be careful to enforce concurrency DbC of concurrent by synchronizing
     * any accesses to class resources.
     *
     * @param packet The packet to route
     */
    public void route(Packet packet) {
        if (packet instanceof Message) {
            route((Message)packet);
        }
        else if (packet instanceof Presence) {
            route((Presence)packet);
        }
        else if (packet instanceof IQ) {
            route((IQ)packet);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    public void route(IQ packet) {
        iqRouter.route(packet);
    }

    public void route(Message packet) {
        messageRouter.route(packet);
    }

    public void route(Presence packet) {
        presenceRouter.route(packet);
    }

    public void initialize(XMPPServer server) {
        super.initialize(server);
        iqRouter = server.getIQRouter();
        messageRouter = server.getMessageRouter();
        presenceRouter = server.getPresenceRouter();
    }
}