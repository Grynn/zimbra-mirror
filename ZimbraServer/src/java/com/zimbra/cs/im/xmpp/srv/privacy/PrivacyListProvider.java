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
 * Part of the Zimbra Collaboration Suite Server.
 *
 * The Original Code is Copyright (C) Jive Software. Used with permission
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.im.xmpp.srv.privacy;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.zimbra.cs.im.xmpp.database.DbConnectionManager;
import com.zimbra.cs.im.xmpp.util.Log;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Provider for the privacy lists system. Privacy lists are read and written
 * from the <tt>jivePrivacyList</tt> database table.
 *
 * @author Gaston Dombiak
 */
public class PrivacyListProvider {

    private static final String LOAD_LIST_NAMES =
            "SELECT name, isDefault FROM jivePrivacyList WHERE username=?";
    private static final String LOAD_PRIVACY_LIST =
            "SELECT isDefault, list FROM jivePrivacyList WHERE username=? AND name=?";
    private static final String LOAD_DEFAULT_PRIVACY_LIST =
            "SELECT name, list FROM jivePrivacyList WHERE username=? AND isDefault=1";
    private static final String DELETE_PRIVACY_LIST =
            "DELETE FROM jivePrivacyList WHERE username=? AND name=?";
    private static final String DELETE_PRIVACY_LISTS =
            "DELETE FROM jivePrivacyList WHERE username=?";
    private static final String UPDATE_PRIVACY_LIST =
            "UPDATE jivePrivacyList SET isDefault=?, list=? WHERE username=? AND name=?";
    private static final String INSERT_PRIVACY_LIST =
            "INSERT INTO jivePrivacyList (username, name, isDefault, list) VALUES (?, ?, ?, ?)";

    /**
     * Pool of SAX Readers. SAXReader is not thread safe so we need to have a pool of readers.
     */
    private BlockingQueue<SAXReader> xmlReaders = new LinkedBlockingQueue<SAXReader>();

    public PrivacyListProvider() {
        super();
        // Initialize the pool of sax readers
        for (int i=0; i<50; i++) {
            xmlReaders.add(new SAXReader());
        }
    }

    /**
     * Returns the names of the existing privacy lists indicating which one is the
     * default privacy list associated to a user.
     *
     * @param username the username of the user to get his privacy lists names.
     * @return the names of the existing privacy lists with a default flag.
     */
    public Map<String, Boolean> getPrivacyLists(String username) {
        Map<String, Boolean> names = new HashMap<String, Boolean>();
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOAD_LIST_NAMES);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                names.put(rs.getString(1), rs.getInt(2) == 1);
            }
        }
        catch (Exception e) {
            Log.error("Error loading names of privacy lists for username: " + username, e);
        }
        finally {
            try { if (pstmt != null) { pstmt.close(); } }
            catch (Exception e) { Log.error(e); }
            try { if (con != null) { con.close(); } }
            catch (Exception e) { Log.error(e); }
        }
        return names;
    }

    /**
     * Loads the requested privacy list from the database. Returns <tt>null</tt> if a list
     * with the specified name does not exist.
     *
     * @param username the username of the user to get his privacy list.
     * @param listName name of the list to load.
     * @return the privacy list with the specified name or <tt>null</tt> if a list
     *         with the specified name does not exist.
     */
    public PrivacyList loadPrivacyList(String username, String listName) {
        PrivacyList privacyList = null;
        java.sql.Connection con = null;
        PreparedStatement pstmt = null;
        SAXReader xmlReader = null;
        try {
            // Get a sax reader from the pool
            xmlReader = xmlReaders.take();
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOAD_PRIVACY_LIST);
            pstmt.setString(1, username);
            pstmt.setString(2, listName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                boolean isDefault = rs.getInt(1) == 1;
                Element listElement =
                        xmlReader.read(new StringReader(rs.getString(2))).getRootElement();
                privacyList = new PrivacyList(username, listName, isDefault, listElement);
            }
        }
        catch (Exception e) {
            Log.error("Error loading privacy list: " + listName + " of username: " + username, e);
        }
        finally {
            // Return the sax reader to the pool
            if (xmlReader != null) {
                xmlReaders.add(xmlReader);
            }
            try { if (pstmt != null) { pstmt.close(); } }
            catch (Exception e) { Log.error(e); }
            try { if (con != null) { con.close(); } }
            catch (Exception e) { Log.error(e); }
        }
        return privacyList;
    }

    /**
     * Loads the default privacy list of a given user from the database. Returns <tt>null</tt>
     * if no list was found.
     *
     * @param username the username of the user to get his default privacy list.
     * @return the default privacy list or <tt>null</tt> if no list was found.
     */
    public PrivacyList loadDefaultPrivacyList(String username) {
        PrivacyList privacyList = null;
        java.sql.Connection con = null;
        PreparedStatement pstmt = null;
        SAXReader xmlReader = null;
        try {
            // Get a sax reader from the pool
            xmlReader = xmlReaders.take();
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOAD_DEFAULT_PRIVACY_LIST);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String listName= rs.getString(1);
                Element listElement =
                        xmlReader.read(new StringReader(rs.getString(2))).getRootElement();
                privacyList = new PrivacyList(username, listName, true, listElement);
            }
        }
        catch (Exception e) {
            Log.error("Error loading default privacy list of username: " + username, e);
        }
        finally {
            // Return the sax reader to the pool
            if (xmlReader != null) {
                xmlReaders.add(xmlReader);
            }
            try { if (pstmt != null) { pstmt.close(); } }
            catch (Exception e) { Log.error(e); }
            try { if (con != null) { con.close(); } }
            catch (Exception e) { Log.error(e); }
        }
        return privacyList;
    }

    /**
     * Creates and saves the new privacy list to the database.
     *
     * @param username the username of the user that created a new privacy list.
     * @param list the PrivacyList to save.
     */
    public void createPrivacyList(String username, PrivacyList list) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(INSERT_PRIVACY_LIST);
            pstmt.setString(1, username);
            pstmt.setString(2, list.getName());
            pstmt.setInt(3, (list.isDefault() ? 1 : 0));
            pstmt.setString(4, list.asElement().asXML());
            pstmt.executeUpdate();
        }
        catch (Exception e) {
            Log.error("Error adding privacy list: " + list.getName() + " of username: " + username,
                    e);
        }
        finally {
            try { if (pstmt != null) { pstmt.close(); } }
            catch (Exception e) { Log.error(e); }
            try { if (con != null) { con.close(); } }
            catch (Exception e) { Log.error(e); }
        }
    }

    /**
     * Updated the existing privacy list in the database.
     *
     * @param username the username of the user that updated a privacy list.
     * @param list the PrivacyList to update in the database.
     */
    public void updatePrivacyList(String username, PrivacyList list) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(UPDATE_PRIVACY_LIST);
            pstmt.setInt(1, (list.isDefault() ? 1 : 0));
            pstmt.setString(2, list.asElement().asXML());
            pstmt.setString(3, username);
            pstmt.setString(4, list.getName());
            pstmt.executeUpdate();
        }
        catch (Exception e) {
            Log.error(
                    "Error updating privacy list: " + list.getName() + " of username: " + username,
                    e);
        }
        finally {
            try { if (pstmt != null) { pstmt.close(); } }
            catch (Exception e) { Log.error(e); }
            try { if (con != null) { con.close(); } }
            catch (Exception e) { Log.error(e); }
        }
    }

    /**
     * Deletes an existing privacy list from the database.
     *
     * @param username the username of the user that deleted a privacy list.
     * @param listName the name of the PrivacyList to delete.
     */
    public void deletePrivacyList(String username, String listName) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(DELETE_PRIVACY_LIST);
            pstmt.setString(1, username);
            pstmt.setString(2, listName);
            pstmt.executeUpdate();
        }
        catch (Exception e) {
            Log.error("Error deleting privacy list: " + listName + " of username: " + username, e);
        }
        finally {
            try { if (pstmt != null) { pstmt.close(); } }
            catch (Exception e) { Log.error(e); }
            try { if (con != null) { con.close(); } }
            catch (Exception e) { Log.error(e); }
        }
    }

    /**
     * Deletes all existing privacy list from the database for the given user.
     *
     * @param username the username of the user whose privacy lists are going to be deleted.
     */
    public void deletePrivacyLists(String username) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(DELETE_PRIVACY_LISTS);
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
        catch (Exception e) {
            Log.error("Error deleting privacy lists of username: " + username, e);
        }
        finally {
            try { if (pstmt != null) { pstmt.close(); } }
            catch (Exception e) { Log.error(e); }
            try { if (con != null) { con.close(); } }
            catch (Exception e) { Log.error(e); }
        }
    }
}
