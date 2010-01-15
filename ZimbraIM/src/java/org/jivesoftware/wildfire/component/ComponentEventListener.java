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
package org.jivesoftware.wildfire.component;

import org.xmpp.component.Component;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

/**
 * Interface to listen for component events. Use the
 * {@link InternalComponentManager#addListener(ComponentEventListener)}
 * method to register for events.
 *
 * @author Gaston Dombiak
 */
public interface ComponentEventListener {

    /**
     * A component was registered with the Component Manager. At this point the
     * component has been intialized and started. XMPP entities can exchange packets
     * with the component. However, the component is still not listed as a disco#items
     * of the server since the component has not answered the disco#info request sent
     * by the server.
     *
     * @param component the newly added component.
     * @param componentJID address where the component can be located (e.g. search.myserver.com)
     */
    public void componentRegistered(Component component, JID componentJID);

    /**
     * A component was removed.
     *
     * @param component the removed component.
     * @param componentJID address where the component was located (e.g. search.myserver.com)
     */
    public void componentUnregistered(Component component, JID componentJID);

    /**
     * The server has received a disco#info response from the component. Once a component
     * is registered with the server, the server will send a disco#info request to the
     * component to discover if service discover is supported by the component. This event
     * is triggered when the server received the response of the component.
     *
     * @param component the component that answered the disco#info request.
     * @param iq the IQ packet with the disco#info sent by the component.
     */
    public void componentInfoReceived(Component component, IQ iq);
}
