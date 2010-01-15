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
package org.jivesoftware.wildfire.handler;

import org.dom4j.Element;
import org.jivesoftware.wildfire.IQHandlerInfo;
import org.jivesoftware.wildfire.XMPPServer;
import org.jivesoftware.wildfire.auth.UnauthorizedException;
import org.jivesoftware.wildfire.group.Group;
import org.jivesoftware.wildfire.roster.RosterManager;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

import java.util.Collection;

/**
 * Handler of IQ packets whose child element is "sharedgroup" with namespace
 * "http://www.jivesoftware.org/protocol/sharedgroup". This handler will return the list of
 * shared groups where the user sending the request belongs.
 *
 * @author Gaston Dombiak
 */
public class IQSharedGroupHandler extends IQHandler {

    private IQHandlerInfo info;
    private RosterManager rosterManager;

    public IQSharedGroupHandler() {
        super("Shared Groups Handler");
        info = new IQHandlerInfo("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup");
    }

    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        IQ result = IQ.createResultIQ(packet);
        String username = packet.getFrom().toBareJID();
        if (!XMPPServer.getInstance().isLocalDomain(packet.getFrom().getDomain()) || username == null) {
            // Users of remote servers are not allowed to get their "shared groups". Users of
            // remote servers cannot have shared groups in this server.
            // Besides, anonymous users do not belong to shared groups so answer an error
            result.setChildElement(packet.getChildElement().createCopy());
            result.setError(PacketError.Condition.not_allowed);
            return result;
        }

        Collection<Group> groups = rosterManager.getSharedGroups(username);
        Element sharedGroups = result.setChildElement("sharedgroup",
                "http://www.jivesoftware.org/protocol/sharedgroup");
        for (Group sharedGroup : groups) {
            String displayName = sharedGroup.getProperties().get("sharedRoster.displayName");
            if (displayName != null) {
                sharedGroups.addElement("group").setText(displayName);
            }
        }
        return result;
    }

    public IQHandlerInfo getInfo() {
        return info;
    }

    public void initialize(XMPPServer server) {
        super.initialize(server);
        rosterManager = server.getRosterManager();
    }
}
