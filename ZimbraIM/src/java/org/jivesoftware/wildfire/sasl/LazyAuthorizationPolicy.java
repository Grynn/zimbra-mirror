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
package org.jivesoftware.wildfire.sasl;

/**
 * This policy will authorize any principal who's username matches exactly 
 * the username of the JID. This means when cross realm authentication is 
 * allowed, user@REALM_A.COM and user@REALM_B.COM could both authorize as
 * user@servername, so there is some risk here. But if usernames across the
 *
 * @author Jay Kline
 */
public class LazyAuthorizationPolicy extends AbstractAuthorizationPolicy implements AuthorizationProvider {

    /**
     * Returns true if the principal is explicity authorized to the JID
     *
     * @param username The username requested.
     * @param principal The principal requesting the username.
     * @return true is the user is authorized to be principal
     */
    public boolean authorize(String username, String principal) {
        return (principal.startsWith(username+"@"));
    }

    /**
     * Returns the short name of the Policy
     *
     * @return The short name of the Policy
     */
    public String name() {
        return "Lazy";
    }

    /**
     * Returns a description of the Policy
     *
     * @return The description of the Policy.
     */
    public String description() {
        return "This policy will authorize any principal who's username matches exactly the username of the JID. This means when cross realm authentication is allowed, user@REALM_A.COM and user@REALM_B.COM could both authorize as user@servername, so there is some risk here. But if usernames across the realms are unique, this can be very helpful.";
    }
}