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

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Provider for authorization using the default storage database. Checks
 * if the authenticated principal is in the user's list of authorized
 * principals.
 *
 * @author Jay Kline
 */
public class DefaultAuthorizationProvider extends AbstractAuthorizationProvider
        implements AuthorizationProvider {

    private static final String MATCH_AUTHORIZED =
            "SELECT username FROM jiveSASLAuthorized WHERE username=? AND authorized=?";
    private static final String GET_AUTHORIZED =
            "SELECT authorized FROM jiveSASLAuthorized WHERE username=?";
    private static final String INSERT_AUTHORIZED =
            "INSERT into jiveSASLAuthorized (username,authorized) VALUES (?,?)";
    private static final String DELETE_AUTHORIZED =
            "DELETE FROM jiveSASLAuthorized WHERE username=? AND authorized=?";
    private static final String DELETE_USER = "DELETE FROM jiveSASLAuthorized WHERE username=?";

    /**
     * Returns true if the principal is explicity authorized to the JID
     *
     * @param username The username requested.
     * @param principal The principal requesting the username.
     * @return true is the user is authorized to be principal
     */
    public boolean authorize(String username, String principal) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(MATCH_AUTHORIZED);
            pstmt.setString(1, username);
            pstmt.setString(2, principal);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
        catch (Exception e) {
            return false;
        }
        finally {
            try { if (pstmt != null) { pstmt.close(); } }
            catch (Exception e) { Log.error(e); }
            try { if (con != null) { con.close(); } }
            catch (Exception e) { Log.error(e); }
        }
        // not reachable
        //return false;
    }

    /**
    * Returns a String Collection of principals that are authorized to use
    * the named user.
     *
     * @param username The username.
     * @return A String Collection of principals that are authorized.
     */
    public Collection<String> getAuthorized(String username) {
        Connection con = null;
        PreparedStatement pstmt = null;
        Collection<String> authorized = new ArrayList<String>();
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(GET_AUTHORIZED);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                authorized.add(rs.getString("authorized"));
            }
            return authorized;
        } catch (Exception e) {
            return new ArrayList<String>();
        }
        finally {
            try { if (pstmt != null) { pstmt.close(); } }
            catch (Exception e) { Log.error(e); }
            try { if (con != null) { con.close(); } }
            catch (Exception e) { Log.error(e); }
        }
    }



    /**
     * Returns true.
     *
     * @return true
     */
    public boolean isWritable() {
        return true;
    }

    /**
     * Add a single authorized principal to use the named user.
     *
     * @param username The username.
     * @param principal The principal authorized to use the named user.
     * @throws UnsupportedOperationException If this AuthorizationProvider cannot be updated.
     */
    public void addAuthorized(String username, String principal)
            throws UnsupportedOperationException {

        if (authorize(username, principal)) {
            // Already exists
            return;
        }
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(INSERT_AUTHORIZED);
            pstmt.setString(1, username);
            pstmt.setString(2, principal);
            pstmt.execute();
        }
        catch (Exception e) {
            Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
        } finally {
            try { if (pstmt != null) { pstmt.close(); } }
            catch (Exception e) { Log.error(e); }
            try { if (con != null) { con.close(); } }
            catch (Exception e) { Log.error(e); }
        }
    }

    /**
    * Add a Collection of users authorized to use the named user.
     *
     * @param username The username.
     * @param principals The Collection of principals authorized to use the named user.
     * @throws UnsupportedOperationException If this AuthorizationProvider cannot be updated.
     */
    public void addAuthorized(String username, Collection<String> principals)
            throws UnsupportedOperationException {
        for (String principal : principals) {
            addAuthorized(username, principal);
        }
    }

    /**
     * Set the users authorized to use the named user. All existing principals listed
     * will be removed.
     *
     * @param username The username.
     * @param principals The Collection of principals authorized to use the named user.
     * @throws UnsupportedOperationException If this AuthorizationProvider cannot be updated.
     */
    public void setAuthorized(String username, Collection<String> principals)
            throws UnsupportedOperationException {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(DELETE_USER);
            pstmt.setString(1, username);
            pstmt.execute();
            addAuthorized(username, principals);
        }
        catch (Exception e) {
            Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
        }
        finally {
            try { if (pstmt != null) { pstmt.close(); } }
            catch (Exception e) { Log.error(e); }
            try { if (con != null) { con.close(); } }
            catch (Exception e) { Log.error(e); }
        }
    }

   /**
     * Returns the short name of the Policy
     *
     * @return The short name of the Policy
     */
    public String name() {
        return "Default Provider";
    }

    /**
     * Returns a description of the Policy
     *
     * @return The description of the Policy.
     */
    public String description() {
        return "Provider for authorization using the default storage database.";
    }

}