/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.handler;

import org.jivesoftware.wildfire.auth.UnauthorizedException;

/**
 * Information for controlling the authentication options for the server.
 *
 * @author Iain Shigeoka
 */
public interface IQAuthInfo {

    /**
     * Returns true if anonymous authentication is allowed.
     *
     * @return true if anonymous logins are allowed
     */
    public boolean isAnonymousAllowed();

    /**
     * Changes the server's support for anonymous authentication.
     *
     * @param isAnonymous True if anonymous logins should be allowed.
     * @throws UnauthorizedException If you don't have permission to adjust this setting
     */
    public void setAllowAnonymous(boolean isAnonymous) throws UnauthorizedException;
}