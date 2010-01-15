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

import org.xmpp.packet.Packet;
import org.jivesoftware.wildfire.auth.UnauthorizedException;

/**
 * Interface to handle packets delivered by Channels.
 *
 * @author Matt Tucker
 */
public interface ChannelHandler<T extends Packet> {

    /**
     * Process an XMPP packet.
     *
     * @param packet a packet to process.
     * @throws UnauthorizedException if not allowed to process the packet.
     * @throws PacketException thrown if the packet is malformed (results in the sender's
     *      session being shutdown).
     */
    public abstract void process(T packet) throws UnauthorizedException, PacketException;
}