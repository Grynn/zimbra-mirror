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
