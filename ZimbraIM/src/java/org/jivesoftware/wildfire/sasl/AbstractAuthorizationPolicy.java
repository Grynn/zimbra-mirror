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
 * Provider for authorization policies. Policy decisions are
 * not based on any storage or specific options.  They are 
 * ment to be broad sweeping policies, and are often implemented
 * with a simple pattern matching algorithm. For a large 
 * majority of sites, a policy will be all that is required.
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
public abstract class AbstractAuthorizationPolicy implements AuthorizationProvider {

    /**
     * Returns true if the principal is explicity authorized to the JID
     *
     * @param username The username requested.
     * @param principal The principal requesting the username.
     * @return true is the user is authorized to be principal
     */
    public abstract boolean authorize(String username, String principal);

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