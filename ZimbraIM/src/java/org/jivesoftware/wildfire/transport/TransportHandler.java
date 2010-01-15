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
package org.jivesoftware.wildfire.transport;

import org.jivesoftware.wildfire.container.BasicModule;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.*;
import org.jivesoftware.wildfire.auth.UnauthorizedException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Routes packets to the appropriate transport gateway or drops the packet.
 *
 * @author Iain Shigeoka
 */
public class TransportHandler extends BasicModule implements ChannelHandler {

    private Map<String, Channel> transports = new ConcurrentHashMap<String, Channel>();

    private PacketDeliverer deliverer;

    public TransportHandler() {
        super("Transport handler");
    }

    public void addTransport(Channel transport) {
        transports.put(transport.getName(), transport);
    }

    public void process(Packet packet) throws UnauthorizedException, PacketException {
        boolean handled = false;
        String host = packet.getTo().getDomain();
        for (Channel channel : transports.values()) {
            if (channel.getName().equalsIgnoreCase(host)) {
                channel.add(packet);
                handled = true;
            }
        }
        if (!handled) {
            JID recipient = packet.getTo();
            JID sender = packet.getFrom();
            packet.setError(PacketError.Condition.remote_server_timeout);
            packet.setFrom(recipient);
            packet.setTo(sender);
            try {
                deliverer.deliver(packet);
            }
            catch (PacketException e) {
                Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
            }
        }
    }

    public void initialize(XMPPServer server) {
        super.initialize(server);
        deliverer = server.getPacketDeliverer();
    }
}