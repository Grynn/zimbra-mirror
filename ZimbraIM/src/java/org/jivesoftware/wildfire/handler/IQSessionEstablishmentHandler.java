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
package org.jivesoftware.wildfire.handler;

import org.jivesoftware.wildfire.IQHandlerInfo;
import org.jivesoftware.wildfire.auth.UnauthorizedException;
import org.xmpp.packet.IQ;

/**
 * Activate client sessions once resource binding has been done. Clients need to active their
 * sessions in order to engage in instant messaging and presence activities. The server may
 * deny sessions activations if the max number of sessions in the server has been reached or
 * if a user does not have permissions to create sessions.<p>
 *
 * Current implementation does not check any of the above conditions. However, future versions
 * may add support for those checkings.
 *
 * @author Gaston Dombiak
 */
public class IQSessionEstablishmentHandler extends IQHandler {

    private IQHandlerInfo info;

    public IQSessionEstablishmentHandler() {
        super("Session Establishment handler");
        info = new IQHandlerInfo("session", "urn:ietf:params:xml:ns:xmpp-session");
    }

    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        // Just answer that the session has been activated
        IQ reply = IQ.createResultIQ(packet);
        reply.setChildElement(packet.getChildElement().createCopy());
        return reply;
    }

    public IQHandlerInfo getInfo() {
        return info;
    }
}
