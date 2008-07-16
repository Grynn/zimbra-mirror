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
package org.jivesoftware.wildfire.sasl;

import org.jivesoftware.util.IMConfig;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Provider for authorization. Checks if the authenticated principal is in
 * the user's .k5login file. A traditional Unix Kerberos methodology. The
 * location of this file can be configured in the <tt>wildfire.xml</tt>
 * file. An entry in that file would look like the following:
 *
 * <pre>
 *   &lt;unix&gt;
 *     &lt;k5login&gt; /home/{0}/.k5login &lt;/k5login&gt;
 *   &lt;/unix&gt;</pre>
 *
 * The string <tt>{0}</tt> will be replaced with the username.
 *
 * @author Jay Kline
 */
public class UnixK5LoginProvider extends AbstractAuthorizationProvider implements AuthorizationProvider {

    /**
     * Returns true if the principal is explicity authorized to the JID
     *
     * @param username The username requested.
     * @param principal The principal requesting the username.
     * @return true is the user is authorized to be principal
     */
    public boolean authorize(String username, String principal) {
        return getAuthorized(username).contains(principal);
    }
    
    /**
     * Returns a String Collection of principals that are authorized to use
     * the named user.
     *
     * @param username The username.
     * @return A String Collection of principals that are authorized.
     */
    public Collection<String> getAuthorized(String username) {
        Collection<String> authorized = new ArrayList<String>();
        try {
            String filename = IMConfig.XMPP_AUTHPROVIDER_K5LOGIN_FILENAME.getString();
            filename = filename.replace("{0}",username);
            File k5login = new File(filename);
            FileInputStream fis = new FileInputStream(k5login);
            DataInputStream dis = new DataInputStream(fis);

            String line;
            while ( (line = dis.readLine() ) != null) {
                authorized.add(line);
            }
        } catch (IOException e) {
            //??
        }
    return authorized;
    }
    
    /**
    * Returns false, this implementation is not writeable.
     *
     * @return False.
     */
    public boolean isWritable() {
        return false;
    }
    
    /**
     * Always throws UnsupportedOperationException.
     *
     * @param username The username.
     * @param principal The principal authorized to use the named user.
     * @throws UnsupportedOperationException If this AuthorizationProvider cannot be updated.
     */
    public void addAuthorized(String username, String principal) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Always throws UnsupportedOperationException.
     *
     * @param username The username.
     * @param principals The Collection of principals authorized to use the named user.
     */
    public void addAuthorized(String username, Collection<String> principals) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Always throws UnsupportedOperationException.
     *
     * @param username The username.
     * @param principals The Collection of principals authorized to use the named user.
     * @throws UnsupportedOperationException If this AuthorizationProvider cannot be updated.
     */
    public void setAuthorized(String username, Collection<String> principals) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns the short name of the Policy
     *
     * @return The short name of the Policy
     */
    public String name() {
        return "Unix .k5login";
    }
    
    /**
     * Returns a description of the Policy
     *
     * @return The description of the Policy.
     */
    public String description() {
        return "Checks if the authenticated principal is in the user's .k5login file. A traditional Unix Kerberos methodology.";
    }
}