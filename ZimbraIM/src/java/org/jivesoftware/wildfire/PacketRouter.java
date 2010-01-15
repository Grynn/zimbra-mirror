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
package org.jivesoftware.wildfire;

import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

/**
 * A router that handles incoming packets. Packets will be routed to their
 * corresponding handler. A router is much like a forwarded with some logic
 * to figute out who is the target for each packet.
 *
 * @author Gaston Dombiak
 */
public interface PacketRouter {

    /**
     * Routes the given packet based on its type.
     *
     * @param packet The packet to route.
     */
    void route(Packet packet);

    /**
     * Routes the given IQ packet.
     *
     * @param packet The packet to route.
     */
    void route(IQ packet);

    /**
     * Routes the given Message packet.
     *
     * @param packet The packet to route.
     */
    void route(Message packet);

    /**
     * Routes the given Presence packet.
     *
     * @param packet The packet to route.
     */
    void route(Presence packet);
}
