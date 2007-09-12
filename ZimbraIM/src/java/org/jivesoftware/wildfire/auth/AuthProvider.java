/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.auth;

import org.jivesoftware.wildfire.user.UserNotFoundException;

/**
 * Provider interface for authentication. Users that wish to integrate with
 * their own authentication system must implement this class and then register
 * the implementation with Wildfire in the <tt>wildfire.xml</tt>
 * file. An entry in that file would look like the following:
 *
 * <pre>
 *   &lt;provider&gt;
 *     &lt;auth&gt;
 *       &lt;className&gt;com.foo.auth.CustomAuthProvider&lt;/className&gt;
 *     &lt;/auth&gt;
 *   &lt;/provider&gt;</pre>
 *
 * @author Matt Tucker
 */
public interface AuthProvider {

    /**
     * Returns true if this AuthProvider supports authentication using plain-text
     * passwords according to JEP--0078. Plain text authentication is not secure
     * and should generally only be used for a TLS/SSL connection.
     *
     * @return true if plain text password authentication is supported by
     *      this AuthProvider.
     */
    boolean isPlainSupported();

    /**
     * Returns true if this AuthProvider supports digest authentication
     * according to JEP-0078.
     *
     * @return true if digest authentication is supported by this
     *      AuthProvider.
     */
    boolean isDigestSupported();

    /**
     * Returns if the username and password are valid; otherwise this
     * method throws an UnauthorizedException.<p>
     *
     * If {@link #isPlainSupported()} returns false, this method should
     * throw an UnsupportedOperationException.
     *
     * @param username the username.
     * @param password the passwordl
     * @throws UnauthorizedException if the username and password do
     *      not match any existing user.
     */
    void authenticate(String username, String password) throws UnauthorizedException;

    /**
     * Returns if the username, token, and digest are valid; otherwise this
     * method throws an UnauthorizedException.<p>
     *
     * If {@link #isDigestSupported()} returns false, this method should
     * throw an UnsupportedOperationException.
     *
     * @param username the username.
     * @param token the token that was used with plain-text password to
     *      generate the digest.
     * @param digest the digest generated from plain-text password and unique token.
     * @throws UnauthorizedException if the username and password
     *      do not match any existing user.
     */
    void authenticate(String username, String token, String digest)
            throws UnauthorizedException;

    /**
     * Returns the user's password. This method should throw an UnsupportedOperationException
     * if this operation is not supported by the backend user store.
     *
     * @param username the username of the user.
     * @return the user's password.
     * @throws UserNotFoundException if the given user's password could not be loaded.
     * @throws UnsupportedOperationException if the provider does not
     *      support the operation (this is an optional operation).
     */
    public String getPassword(String username) throws UserNotFoundException,
            UnsupportedOperationException;

    /**
     * Sets the users's password. This method should throw an UnsupportedOperationException
     * if this operation is not supported by the backend user store.
     *
     * @param username the username of the user.
     * @param password the new plaintext password for the user.
     * @throws UserNotFoundException if the given user could not be loaded.
     * @throws UnsupportedOperationException if the provider does not
     *      support the operation (this is an optional operation).
     */
    public void setPassword(String username, String password)
            throws UserNotFoundException, UnsupportedOperationException;

    /**
     * Returns true if this UserProvider is able to retrieve user passwords from
     * the backend user store. If this operation is not supported then {@link #getPassword(String)}
     * will throw an {@link UnsupportedOperationException} if invoked.
     *
     * @return true if this UserProvider is able to retrieve user passwords from the
     *         backend user store.
     */
    public boolean supportsPasswordRetrieval();
}