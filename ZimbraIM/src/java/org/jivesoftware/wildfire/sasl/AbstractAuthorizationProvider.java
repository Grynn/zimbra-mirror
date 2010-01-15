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

import java.util.Collection;

/**
 * Provider for authorization. Unlike the AbstractAuthorizationPolicy
 * class, this is intended for classes that need a more "heavyweight" 
 * solution, often that requires consulting some storage or external
 * entity about each specific case.  This class allows individual mappings
 * between authenticated principals and usernames, and if the storage
 * mechanism allows it, management of those mappings.
 *
 * Users that wish to integrate with their own authorization 
 * system must extend this class and implement the 
 * AuthorizationProvider interface then register the class
 * with Wildfire in the <tt>wildfire.xml</tt> file. An entry 
 * in that file would look like the following:
 *
 * <pre>
 *   &lt;provider&gt;
 *     &lt;authorizationpolicy&gt;
 *       &lt;classlist&gt;com.foo.auth.CustomPolicyProvider&lt;/classlist&gt;
 *     &lt;/authorizationpolicy&gt;
 *   &lt;/provider&gt;</pre>
 *
 * @author Jay Kline
 */
public abstract class AbstractAuthorizationProvider implements AuthorizationProvider {

    /**
     * Returns true if the principal is explicity authorized to the JID
     *
     * @param username The username requested.
     * @param principal The principal requesting the username.
     * @return true is the user is authorized to be principal
     */
    public abstract boolean authorize(String username, String principal);
    
    /**
     * Returns a String Collection of principals that are authorized to use
     * the named user.
     *
     * @param username The username.
     * @return A String Collection of principals that are authorized.
     */
    public abstract Collection<String> getAuthorized(String username);
    
    /**
     * Returns true if this AuthorizationProvider supports changing the
     * list of authorized principals for users.
     *
     * @return true if updating the list of authorized principals is 
     *      supported by this AuthorizationProvider.
     */
    public abstract boolean isWritable();
    
    /**
     * Add a single authorized principal to use the named user.
     *
     * @param username The username.
     * @param principal The principal authorized to use the named user.
     * @throws UnsupportedOperationException If this AuthorizationProvider cannot be updated.
     */
    public abstract void addAuthorized(String username, String principal) throws UnsupportedOperationException;
    
    /**
     * Add a Collection of users authorized to use the named user.
     *
     * @param username The username.
     * @param principals The Collection of principals authorized to use the named user.
     * @throws UnsupportedOperationException If this AuthorizationProvider cannot be updated.
     */
    public abstract void addAuthorized(String username, Collection<String> principals) throws UnsupportedOperationException;
    
    /**
     * Set the users authorized to use the named user. All existing principals listed
     * will be removed.
     *
     * @param username The username.
     * @param principals The Collection of principals authorized to use the named user.
     * @throws UnsupportedOperationException If this AuthorizationProvider cannot be updated.
     */
    public abstract void setAuthorized(String username, Collection<String> principals) throws UnsupportedOperationException;

    /**
     * Returns the short name of the Policy
     *
     * @return The short name of the Policy
     */
    public abstract String name();
    
    /**
     * Returns a description of the Policy
     *
     * @return The description of the Policy.
     */
    public abstract String description();
}