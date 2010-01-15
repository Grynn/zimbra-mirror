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
package org.jivesoftware.wildfire.sasl;

import org.jivesoftware.wildfire.XMPPServer;

/**
 * This policy will authorize any principal that matches exactly the full
 * JID (REALM and server name must be the same if using GSSAPI) or any
 * principal that matches exactly the username (without REALM or server
 * name). This does exactly what users expect if not supplying a seperate
 * principal for authentication.
 *
 * @author Jay Kline
 */
public class DefaultAuthorizationPolicy extends AbstractAuthorizationPolicy
        implements AuthorizationProvider {

    public DefaultAuthorizationPolicy() {
    }

    /**
     * Returns true if the principal is explicity authorized to the JID
     *
     * @param username  The username requested.
     * @param principal The principal requesting the username.
     * @return true is the user is authorized to be principal
     */
    public boolean authorize(String username, String principal) {
        return (principal.equals(username) || 
                    principal.equals(username + "@" + XMPPServer.getInstance().getServerInfo().getDefaultName()));
    }

    /**
     * Returns the short name of the Policy
     *
     * @return The short name of the Policy
     */
    public String name() {
        return "Default Policy";
    }

    /**
     * Returns a description of the Policy
     *
     * @return The description of the Policy.
     */
    public String description() {
        return "This policy will authorize any principal that matches exactly the full " +
                "JID (REALM and server name must be the same if using GSSAPI) or any principal " +
                "that matches exactly the username (without REALM or server name). This does " +
                "exactly what users expect if not supplying a seperate principal for authentication.";
    }
}
    