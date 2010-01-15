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
package org.jivesoftware.wildfire.auth;

import org.jivesoftware.wildfire.user.UserManager;

/**
 * A token that proves that a user has successfully authenticated.
 *
 * @author Matt Tucker
 * @see AuthFactory
 */
public class AuthToken {

    private static final long serialVersionUID = 01L;
    private String username;

    /**
     * Constucts a new AuthToken with the specified username.
     *
     * @param username the username to create an authToken token with.
     */
    public AuthToken(String username) {
        assert(username.indexOf('@') > 0);
        this.username = username;
    }

    /**
     * Returns the username associated with this AuthToken.
     *
     * @return the username associated with this AuthToken.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns true if this AuthToken is the Anonymous auth token.
     *
     * @return true if this token is the anonymous AuthToken.
     */
    public boolean isAnonymous() {
        return username == null || !UserManager.getInstance().isRegisteredUser(username);
    }
}