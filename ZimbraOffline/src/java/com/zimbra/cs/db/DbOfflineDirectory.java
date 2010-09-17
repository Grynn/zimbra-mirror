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
package com.zimbra.cs.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning.EntryType;
import com.zimbra.cs.db.DbPool.Connection;

public class DbOfflineDirectory {
    static final Object lock = new Object();

    private static int getLastId(Connection conn, String table) throws ServiceException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = conn.prepareStatement("SELECT MAX(entry_id) FROM " + table);
            rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            else
                throw ServiceException.FAILURE("getting last ID for " + table, null);
        } catch (SQLException e) {
            throw ServiceException.FAILURE("getting last ID for " + table, e);
        } finally {
            OfflineDbPool.getInstance().closeResults(rs);
            OfflineDbPool.getInstance().closeStatement(stmt);
        }
    }

    public static void createDirectoryEntry(EntryType etype, String name, Map<String,Object> attrs,
        boolean markChanged) throws ServiceException {
        String zimbraId = (String) attrs.get(Provisioning.A_zimbraId);

        Connection conn = null;
        PreparedStatement stmt = null;
        int entryId;

        try {
            conn = OfflineDbPool.getInstance().getConnection();

            stmt = conn.prepareStatement("INSERT INTO directory (entry_type, entry_name, zimbra_id, modified)" +
                    " VALUES (?, ?, ?, ?)");
            stmt.setString(1, etype.toString());
            stmt.setString(2, name);
            stmt.setString(3, zimbraId);
            stmt.setBoolean(4, markChanged);
            synchronized (lock) {
                stmt.executeUpdate();
                stmt.close();
                entryId = getLastId(conn, "directory");

                for (Map.Entry<String,Object> attr : attrs.entrySet()) {
                    String key = attr.getKey();
                    Object vobject = attr.getValue();
                    if (vobject == null)
                        continue;
                    for (String value : (vobject instanceof String[] ? (String[]) vobject : new String[] { (String) vobject })) {
                        if (value != null)
                            insertAttribute(conn, etype, entryId, key, value);
                    }
                }
                conn.commit();
            }
        } catch (SQLException e) {
            if (Db.errorMatches(e, Db.Error.DUPLICATE_ROW))
                throw AccountServiceException.ACCOUNT_EXISTS(zimbraId);
            else
                throw ServiceException.FAILURE("inserting new " + etype + ": " + zimbraId, e);
        } finally {
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }

    public static void createDirectoryLeaf(EntryType etype, NamedEntry parent, String name, String id, Map<String,Object> attrs, boolean markChanged)
    throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        int entryId;

        try {
            conn = OfflineDbPool.getInstance().getConnection();

            int parentId = getIdForParent(conn, parent);

            stmt = conn.prepareStatement("INSERT INTO directory_leaf (parent_id, entry_type, entry_name, zimbra_id)" +
                    " VALUES (?, ?, ?, ?)");
            stmt.setInt(1, parentId);
            stmt.setString(2, etype.toString());
            stmt.setString(3, name);
            stmt.setString(4, id);
            synchronized (lock) {
                stmt.executeUpdate();
                stmt.close();
                entryId = getLastId(conn, "directory_leaf");

                for (Map.Entry<String,Object> attr : attrs.entrySet()) {
                    String key = attr.getKey();
                    Object vobject = attr.getValue();
                    if (vobject == null)
                        continue;
                    for (String value : (vobject instanceof String[] ? (String[]) vobject : new String[] { (String) vobject })) {
                        if (value != null)
                            insertAttribute(conn, etype, entryId, key, value);
                    }
                }
    
                if (markChanged)
                    markEntryDirty(conn, parentId);
    
                conn.commit();
            }
        } catch (SQLException e) {
            if (Db.errorMatches(e, Db.Error.DUPLICATE_ROW)) {
                if (etype == EntryType.IDENTITY)
                    throw AccountServiceException.IDENTITY_EXISTS(name);
                else if (etype == EntryType.DATASOURCE)
                    throw AccountServiceException.DATA_SOURCE_EXISTS(name);
                else if (etype == EntryType.SIGNATURE)
                	throw AccountServiceException.SIGNATURE_EXISTS(name);
            } else {
                throw ServiceException.FAILURE("inserting new " + etype + ": " + parent.getName() + '/' + name, e);
            }
        } finally {
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }

    private static void insertAttribute(Connection conn, EntryType etype, int entryId, String key, String value)
    throws ServiceException, SQLException {
    	value = OfflineProvisioning.getSanitizedValue(key, value);
        PreparedStatement stmt = null;
        try {
            String table = (etype.isLeafEntry() ? "directory_leaf_attrs" : "directory_attrs");
            stmt = conn.prepareStatement("INSERT INTO " + table + " (entry_id, name, value) VALUES (?, ?, ?)");
            stmt.setInt(1, entryId);
            stmt.setString(2, key);
            stmt.setString(3, value);
            stmt.executeUpdate();
        } finally {
            OfflineDbPool.getInstance().closeStatement(stmt);
        }
    }

    private static void deleteAttribute(Connection conn, EntryType etype, int entryId, String key, String value)
    throws ServiceException, SQLException {
        boolean allValues = (value == null);
        if (value != null) {
        	value = OfflineProvisioning.getSanitizedValue(key, value);
        }
        PreparedStatement stmt = null;
        try {
            String table = (etype.isLeafEntry() ? "directory_leaf_attrs" : "directory_attrs");
            stmt = conn.prepareStatement("DELETE FROM " + table +
                    " WHERE entry_id = ? AND " + Db.equalsSTRING("name") +
                    (allValues ? "" : " AND " + Db.equalsSTRING("value")));
            stmt.setInt(1, entryId);
            stmt.setString(2, key.toUpperCase());
            if (!allValues)
                stmt.setString(3, value.toUpperCase());
            stmt.executeUpdate();
        } finally {
            OfflineDbPool.getInstance().closeStatement(stmt);
        }
    }

    private static void markEntryDirty(Connection conn, int entryId) throws SQLException, ServiceException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE directory SET modified = 1 WHERE entry_id = ?");
            stmt.setInt(1, entryId);
            stmt.executeUpdate();
        } finally {
            OfflineDbPool.getInstance().closeStatement(stmt);
        }
    }

    public static void markEntryClean(EntryType etype, NamedEntry entry) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();

            int entryId = getIdForEntry(conn, etype, Provisioning.A_zimbraId, entry.getId());
            if (entryId <= 0)
                return;

            // clear the "dirty bit" on the entry
            stmt = conn.prepareStatement("UPDATE directory SET modified = 0 WHERE entry_id = ?");
            stmt.setInt(1, entryId);
            stmt.executeUpdate();
            stmt.close();

            // clear the attributes that track changes to the entry
            Map<String, Object> attrs = new HashMap<String, Object>(3);
            attrs.put(OfflineProvisioning.A_offlineModifiedAttrs, null);
            attrs.put(OfflineProvisioning.A_offlineDeletedDataSource, null);
            attrs.put(OfflineProvisioning.A_offlineDeletedIdentity, null);
            attrs.put(OfflineProvisioning.A_offlineDeletedSignature, null);
            modifyDirectoryEntry(conn, etype, entryId, attrs);

            conn.commit();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("marking entry " + entry.getName() + " as clean", e);
        } finally {
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }

    public static List<String> listAllDirtyEntries(EntryType etype) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();

            stmt = conn.prepareStatement("SELECT zimbra_id FROM directory WHERE entry_type = ? AND modified > 0");
            stmt.setString(1, etype.toString());
            rs = stmt.executeQuery();
            List<String> ids = new ArrayList<String>();
            while (rs.next())
                ids.add(rs.getString(1));
            return ids;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("listing dirty entries of type " + etype, e);
        } finally {
            OfflineDbPool.getInstance().closeResults(rs);
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }

    public static List<String> listAllDirectoryEntries(EntryType etype) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();

            stmt = conn.prepareStatement("SELECT zimbra_id FROM directory WHERE entry_type = ?");
            stmt.setString(1, etype.toString());
            rs = stmt.executeQuery();
            List<String> ids = new ArrayList<String>();
            while (rs.next())
                ids.add(rs.getString(1));
            return ids;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("listing all entries of type " + etype, e);
        } finally {
            OfflineDbPool.getInstance().closeResults(rs);
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }

    public static List<String> listAllDirectoryLeaves(EntryType etype, NamedEntry parent) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();

            stmt = conn.prepareStatement("SELECT entry_name FROM directory_leaf WHERE parent_id = ? AND entry_type = ?");
            stmt.setInt(1, getIdForParent(conn, parent));
            stmt.setString(2, etype.toString());
            rs = stmt.executeQuery();
            List<String> ids = new ArrayList<String>();
            while (rs.next())
                ids.add(rs.getString(1));
            return ids;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("listing all entries of type " + etype, e);
        } finally {
            OfflineDbPool.getInstance().closeResults(rs);
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }

    public static List<String> searchDirectoryEntries(EntryType etype, String lookupKey, String lookupPattern) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();

            int pos = 1;
            if (lookupKey.equalsIgnoreCase(Provisioning.A_zimbraId)) {
                stmt = conn.prepareStatement("SELECT zimbra_id FROM directory" +
                        " WHERE " + Db.likeSTRING("zimbra_id") + " AND entry_type = ?");
            } else if (lookupKey.equalsIgnoreCase(OfflineProvisioning.A_offlineDn)) {
                stmt = conn.prepareStatement("SELECT zimbra_id FROM directory" +
                        " WHERE " + Db.likeSTRING("entry_name") + " AND entry_type = ?");
            } else {
                stmt = conn.prepareStatement("SELECT zimbra_id FROM directory d, directory_attrs da" +
                        " WHERE " + Db.equalsSTRING("name") + " AND " + Db.likeSTRING("value") +
                        " AND d.entry_id = da.entry_id AND entry_type = ?");
                stmt.setString(pos++, lookupKey.toUpperCase());
            }
            stmt.setString(pos++, lookupPattern.toUpperCase());
            stmt.setString(pos++, etype.toString());
            rs = stmt.executeQuery();
            List<String> ids = new ArrayList<String>();
            while (rs.next())
                ids.add(rs.getString(1));
            return ids;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("searching all entries of type " + etype, e);
        } finally {
            OfflineDbPool.getInstance().closeResults(rs);
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }
    
    public static Map<String,Object> readDirectoryEntry(EntryType etype, String lookupKey, String lookupValue) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();

            int entryId = getIdForEntry(conn, etype, lookupKey, lookupValue);
            if (entryId <= 0)
                return null;

            stmt = conn.prepareStatement("SELECT name, value FROM directory_attrs WHERE entry_id = ?");
            stmt.setInt(1, entryId);
            rs = stmt.executeQuery();
            Map<String,Object> attrs = new HashMap<String,Object>();
            while (rs.next())
                OfflineProvisioning.addToMap(attrs, rs.getString(1), rs.getString(2));
            if (attrs.isEmpty()) {
                deleteDirectoryEntry(entryId); // remove dangling directory entry
                return null;
            }
            return attrs;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("fetching " + etype + " (" + lookupKey + "=" + lookupValue + ")", e);
        } finally {
            OfflineDbPool.getInstance().closeResults(rs);
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }

    public static Map<String,Object> readDirectoryLeaf(EntryType etype, NamedEntry parent, String name) throws ServiceException {
        return readDirectoryLeaf(etype, parent, OfflineProvisioning.A_offlineDn, name);
    }

    public static Map<String,Object> readDirectoryLeaf(EntryType etype, NamedEntry parent, String lookupKey, String lookupValue)
    throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();

            int entryId = getIdForLeaf(conn, etype, parent, lookupKey, lookupValue);
            if (entryId <= 0)
                return null;

            stmt = conn.prepareStatement("SELECT name, value FROM directory_leaf_attrs WHERE entry_id = ?");
            stmt.setInt(1, entryId);
            rs = stmt.executeQuery();
            Map<String,Object> attrs = new HashMap<String,Object>();
            while (rs.next())
                OfflineProvisioning.addToMap(attrs, rs.getString(1), rs.getString(2));
            if (attrs.isEmpty()) {
                deleteDirectoryLeaf(parent, entryId); // remove dangling directory leaf entry
                return null;
            }
            return attrs;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("fetching " + etype + ": " + parent.getName() + '/' + lookupValue, e);
        } finally {
            OfflineDbPool.getInstance().closeResults(rs);
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }

    public static void modifyDirectoryEntry(EntryType etype, String lookupKey, String lookupValue, Map<String,? extends Object> attrs, boolean markChanged)
    throws ServiceException {
        Connection conn = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();

            int entryId = getIdForEntry(conn, etype, lookupKey, lookupValue);
            if (entryId <= 0)
                return;

            modifyDirectoryEntry(conn, etype, entryId, attrs);

            if (markChanged)
                markEntryDirty(conn, entryId);

            conn.commit();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("modifying " + etype + " (" + lookupKey + "=" + lookupValue + ")", e);
        } finally {
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }

    public static void modifyDirectoryLeaf(EntryType etype, NamedEntry parent, String lookupKey, String lookupValue,
                                           Map<String,? extends Object> attrs, boolean markChanged, String newName)
    throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();

            int entryId = getIdForLeaf(conn, etype, parent, lookupKey, lookupValue);
            if (entryId <= 0)
                return;

            modifyDirectoryEntry(conn, etype, entryId, attrs);

            if (newName != null) {
                stmt = conn.prepareStatement("UPDATE directory_leaf SET entry_name = ? WHERE entry_id = ?");
                stmt.setString(1, newName);
                stmt.setInt(2, entryId);
                stmt.executeUpdate();
                stmt.close();
            }

            if (markChanged)
                markEntryDirty(conn, getIdForParent(conn, parent));

            conn.commit();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("modifying " + etype + ": " + parent.getName() + '/' + lookupValue, e);
        } finally {
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }

    private static void modifyDirectoryEntry(Connection conn, EntryType etype, int entryId, Map<String,? extends Object> attrs)
    throws ServiceException, SQLException {
        for (Map.Entry<String,? extends Object> attr : attrs.entrySet()) {
            Object vobject = attr.getValue();
            if (vobject instanceof Collection) {
                Collection c = (Collection) vobject;
                if (c.isEmpty()) {
                    vobject = null;
                } else {
                    vobject = new String[c.size()];
                    int i = 0;
                    for (Object o : c)
                        ((String[]) vobject)[i++] = (String) o;
                }
            }

            String key = attr.getKey();
            boolean doAdd = key.charAt(0) == '+', doRemove = key.charAt(0) == '-';
            if (doAdd || doRemove) {
                // make sure there aren't other conflicting changes without +/- going on at the same time 
                key = key.substring(1);
                if (attrs.containsKey(key)) 
                    throw ServiceException.INVALID_REQUEST("can't mix +attrName/-attrName with attrName", null);
            }

            if (doAdd || doRemove) {
                if (vobject != null) {
                    for (String value : (vobject instanceof String[] ? (String[]) vobject : new String[] { (String) vobject })) {
                        deleteAttribute(conn, etype, entryId, key, value);
                        // this is hacky, but we're doing redneck duplicate elimination by killing any existing entry/key/value pair first
                        if (doAdd)
                            insertAttribute(conn, etype, entryId, key, value);
                    }
                }
            } else {
                // get rid of any existing values for the key
                deleteAttribute(conn, etype, entryId, key, null);
                // and insert any new values
                if (vobject != null) {
                    for (String value : (vobject instanceof String[] ? (String[]) vobject : new String[] { (String) vobject }))
                        insertAttribute(conn, etype, entryId, key, value);
                }
            }
        }
    }

    private static int getIdForEntry(Connection conn, EntryType etype, String lookupKey, String lookupValue) throws ServiceException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            if (lookupKey.equalsIgnoreCase(Provisioning.A_zimbraId)) {
                stmt = conn.prepareStatement("SELECT entry_id FROM directory" +
                        " WHERE " + Db.equalsSTRING("zimbra_id") + " AND entry_type = ?");
                stmt.setString(1, lookupValue.toUpperCase());
                stmt.setString(2, etype.toString());
            } else if (lookupKey.equalsIgnoreCase(OfflineProvisioning.A_offlineDn)) {
                stmt = conn.prepareStatement("SELECT entry_id FROM directory" +
                        " WHERE " + Db.equalsSTRING("entry_name") + " AND entry_type = ?");
                stmt.setString(1, lookupValue.toUpperCase());
                stmt.setString(2, etype.toString());
            } else {
                stmt = conn.prepareStatement("SELECT da.entry_id FROM directory_attrs da, directory d" +
                        " WHERE " + Db.equalsSTRING("da.name") + " AND " + Db.equalsSTRING("da.value") +
                        " AND da.entry_id = d.entry_id AND d.entry_type = ?" +
                        " GROUP BY da.entry_id");
                stmt.setString(1, lookupKey.toUpperCase());
                stmt.setString(2, lookupValue.toUpperCase());
                stmt.setString(3, etype.toString());
            }
            rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            return -1;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("fetching id for " + etype + " (" + lookupKey + "=" + lookupValue + ")", e);
        } finally {
            OfflineDbPool.getInstance().closeResults(rs);
            OfflineDbPool.getInstance().closeStatement(stmt);
        }
    }

    private static int getIdForParent(Connection conn, NamedEntry parent) throws ServiceException {
        int parentId = getIdForEntry(conn, EntryType.typeForEntry(parent), Provisioning.A_zimbraId, parent.getAttr(Provisioning.A_zimbraId));
        if (parentId <= 0)
            throw AccountServiceException.NO_SUCH_ACCOUNT(parent.getName());
        return parentId;
    }

    private static int getIdForLeaf(Connection conn, EntryType etype, NamedEntry parent, String lookupKey, String lookupValue) throws ServiceException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            if (lookupKey.equals(Provisioning.A_zimbraId)) {
                stmt = conn.prepareStatement("SELECT entry_id FROM directory_leaf" +
                        " WHERE parent_id = ? AND entry_type = ? AND " + Db.equalsSTRING("zimbra_id"));
                stmt.setInt(1, getIdForParent(conn, parent));
                stmt.setString(2, etype.toString());
                stmt.setString(3, lookupValue.toUpperCase());
            } else if (lookupKey.equals(OfflineProvisioning.A_offlineDn)) {
                stmt = conn.prepareStatement("SELECT entry_id FROM directory_leaf" +
                        " WHERE parent_id = ? AND entry_type = ? AND " + Db.equalsSTRING("entry_name"));
                stmt.setInt(1, getIdForParent(conn, parent));
                stmt.setString(2, etype.toString());
                stmt.setString(3, lookupValue.toUpperCase());
            } else {
                stmt = conn.prepareStatement("SELECT da.entry_id FROM directory_leaf_attrs da, directory_leaf d" +
                        " WHERE d.parent_id = ? AND d.entry_type = ? AND da.entry_id = d.entry_id" +
                        " AND " + Db.equalsSTRING("da.name") + " AND " + Db.equalsSTRING("da.value") +
                        " GROUP BY da.entry_id");
                stmt.setInt(1, getIdForParent(conn, parent));
                stmt.setString(2, etype.toString());
                stmt.setString(3, lookupKey.toUpperCase());
                stmt.setString(4, lookupValue.toUpperCase());
            }
            rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            return -1;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("fetching id for " + etype + ": " + parent.getName() + '/' + lookupValue, e);
        } finally {
            OfflineDbPool.getInstance().closeResults(rs);
            OfflineDbPool.getInstance().closeStatement(stmt);
        }
    }

    public static void deleteDirectoryEntry(EntryType etype, String zimbraId) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();

            stmt = conn.prepareStatement("DELETE FROM directory" +
                    " WHERE entry_type = ? AND " + Db.equalsSTRING("zimbra_id"));
            stmt.setString(1, etype.toString());
            stmt.setString(2, zimbraId.toUpperCase());
            synchronized(lock) {
                stmt.executeUpdate();
                conn.commit();
            }
        } catch (SQLException e) {
            throw ServiceException.FAILURE("deleting " + etype + ": " + zimbraId, e);
        } finally {
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }

    public static void deleteDirectoryEntry(int entryId) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();
            stmt = conn.prepareStatement("DELETE FROM directory WHERE entry_id = ?");
            stmt.setInt(1, entryId);
            synchronized(lock) {
                stmt.executeUpdate();
                conn.commit();
            }
        } catch (SQLException e) {
            throw ServiceException.FAILURE("deleting entry " + Integer.toString(entryId), e);
        } finally {
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }
        
    public static void deleteDirectoryLeaf(EntryType etype, NamedEntry parent, String id, boolean markChanged) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();

            int parentId = getIdForParent(conn, parent);

            stmt = conn.prepareStatement("DELETE FROM directory_leaf" +
                    " WHERE parent_id = ? AND entry_type = ? AND " + Db.equalsSTRING("zimbra_id"));
            stmt.setInt(1, parentId);
            stmt.setString(2, etype.toString());
            stmt.setString(3, id.toUpperCase());
            int count;
            
            synchronized(lock) {
                count = stmt.executeUpdate();
                stmt.close();

                if (markChanged && count > 0) {
                    markEntryDirty(conn, parentId);
    
                    Map<String, Object> record = new HashMap<String, Object>(1);
                    if (etype == EntryType.IDENTITY)
                        record.put('+' + OfflineProvisioning.A_offlineDeletedIdentity, id);
                    else if (etype == EntryType.DATASOURCE)
                        record.put('+' + OfflineProvisioning.A_offlineDeletedDataSource, id);
                    else if (etype == EntryType.SIGNATURE)
                    	record.put('+' + OfflineProvisioning.A_offlineDeletedSignature, id);
                    
                    modifyDirectoryEntry(conn, EntryType.ACCOUNT, parentId, record);
                }
                conn.commit();
            }
        } catch (SQLException e) {
            throw ServiceException.FAILURE("deleting " + etype + ": " + parent.getName() + '/' + id, e);
        } finally {
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }
    
    public static void deleteDirectoryLeaf(NamedEntry parent, int entryId) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();
            int parentId = getIdForParent(conn, parent);
            stmt = conn.prepareStatement("DELETE FROM directory_leaf WHERE parent_id = ? AND entry_id = ?");
            stmt.setInt(1, parentId);
            stmt.setInt(2, entryId);
            synchronized(lock) {
                stmt.executeUpdate();
                conn.commit();
            }
        } catch (SQLException e) {
            throw ServiceException.FAILURE("deleting entry " + parent.getName() + '/' + Integer.toString(entryId), e);
        } finally {
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }
    
    public static class GranterEntry {
        public String name;
        public String id;
        public String granteeId;
        
        public GranterEntry(String n, String i, String gi) {
            name = n;
            id = i;
            granteeId = gi;
        }
    }
    
    public static void createGranterEntry(String name, String id, String granteeId) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();
            stmt = conn.prepareStatement("INSERT INTO directory_granter (granter_name, granter_id, grantee_id) VALUES (?, ?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, id);
            stmt.setString(3, granteeId);
            stmt.executeUpdate();
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("creating granter entry: " + name + ", " + id + ", " + granteeId, e);
        } finally {
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }
    }
    
    public static GranterEntry readGranter(String name, String granteeId) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();
            stmt = conn.prepareStatement("SELECT granter_name, granter_id, grantee_id FROM directory_granter" +
                " WHERE " + Db.equalsSTRING("granter_name") + " AND grantee_id = ?");
            stmt.setString(1, name.toUpperCase());
            stmt.setString(2, granteeId);
            
            rs = stmt.executeQuery();
            return rs.next() ? new GranterEntry(rs.getString(1), rs.getString(2), rs.getString(3)) : null;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("reading granter: " + name + ", " + granteeId, e);
        } finally {
            OfflineDbPool.getInstance().closeResults(rs);
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }         
    }
    
    public static List<GranterEntry> searchGranter(String by, String pattern) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String column = by.equalsIgnoreCase("id") ? "granter_id" : "granter_name";
        try {
            conn = OfflineDbPool.getInstance().getConnection();
            stmt = conn.prepareStatement("SELECT granter_name, granter_id, grantee_id FROM directory_granter" +
                " WHERE " + Db.likeSTRING(column));
            stmt.setString(1, pattern.toUpperCase());
            
            rs = stmt.executeQuery();            
            List<GranterEntry> ents = new ArrayList<GranterEntry>();
            while (rs.next())
                ents.add(new GranterEntry(rs.getString(1), rs.getString(2), rs.getString(3)));
            return ents;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("searching granters: " + by + " like " + pattern, e);
        } finally {
            OfflineDbPool.getInstance().closeResults(rs);
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }        
    }
    
    public static void deleteGranterByGrantee(String granteeId) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = OfflineDbPool.getInstance().getConnection();
            stmt = conn.prepareStatement("DELETE FROM directory_granter WHERE grantee_id = ?");
            stmt.setString(1, granteeId);
            
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("deleting granter by grantee " + granteeId + ": " + e.getMessage(), e);
        } finally {
            OfflineDbPool.getInstance().closeStatement(stmt);
            OfflineDbPool.getInstance().quietClose(conn);
        }        
    }
}
