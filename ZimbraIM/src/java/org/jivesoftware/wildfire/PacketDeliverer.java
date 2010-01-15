/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire;

import org.xmpp.packet.Packet;
import org.jivesoftware.wildfire.auth.UnauthorizedException;

/**
 * Delivers packets to locally connected streams. This is the opposite
 * of the packet transporter.
 *
 * @author Iain Shigeoka
 */
public interface PacketDeliverer {

    /**
     * Delivers the given packet based on packet recipient and sender. The
     * deliverer defers actual routing decisions to other classes.
     * <h2>Warning</h2>
     * Be careful to enforce concurrency DbC of concurrent by synchronizing
     * any accesses to class resources.
     *
     * @param packet the packet to route
     * @throws PacketException if the packet is null or the packet could not be routed.
     */
    public void deliver(Packet packet) throws UnauthorizedException, PacketException;
}
