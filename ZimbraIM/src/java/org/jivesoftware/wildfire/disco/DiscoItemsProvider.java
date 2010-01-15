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
package org.jivesoftware.wildfire.disco;

import org.dom4j.Element;
import org.xmpp.packet.JID;

import java.util.Iterator;

/**
 * A DiscoItemsProvider is responsible for providing the items associated with a JID's name and
 * node. For example, the room service could implement this interface in order to provide
 * the existing rooms as its items. In this case, the JID's name and node won't be used.<p>
 * <p/>
 * The items to provide must have a JID attribute specifying the JID of the item and may possess a
 * name attribute specifying a natural-language name for the item. The node attribute is optional
 * and must be used only for items that aren't addressable as a JID.
 *
 * @author Gaston Dombiak
 */
public interface DiscoItemsProvider {

    /**
     * Returns an Iterator (of Element) with the target entity's items or null if none. Each Element
     * must include a JID attribute and may include the name and node attributes of the entity. In
     * case that the sender of the disco request is not authorized to discover items an
     * UnauthorizedException will be thrown.
     *
     * @param name the recipient JID's name.
     * @param node the requested disco node.
     * @param senderJID the XMPPAddress of user that sent the disco items request.
     * @return an Iterator (of Element) with the target entity's items or null if none.
     */
    public abstract Iterator<Element> getItems(String name, String node, JID senderJID);

}
