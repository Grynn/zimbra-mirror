/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public static void createDirectoryEntry(EntryType etype, String name, Map<String,Object> attrs) throws ServiceException {
        String zimbraId = (String) attrs.get(Provisioning.A_zimbraId);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DbPool.getConnection();

            stmt = conn.prepareStatement("INSERT INTO zimbra.directory (entry_type, entry_name, zimbra_id) VALUES (?, ?, ?)",
                                         Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, etype.toString());
            stmt.setString(2, name);
            stmt.setString(3, zimbraId);
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (!rs.next())
                throw ServiceException.FAILURE("no autoincrement returned from INSERT", null);
            int entryId = rs.getInt(1);
            rs.close();
            stmt.close();

            for (Map.Entry<String,Object> attr : attrs.entrySet()) {
                String key = attr.getKey();
                Object vobject = attr.getValue();
                for (String value : (vobject instanceof String[] ? (String[]) vobject : new String[] { (String) vobject })) {
                    if (value != null)
                        insertAttribute(conn, etype, entryId, key, value);
                }
            }
    
            conn.commit();
        } catch (SQLException e) {
            if (e.getErrorCode() == Db.Error.DUPLICATE_ROW)
                throw AccountServiceException.ACCOUNT_EXISTS(zimbraId);
            else
                throw ServiceException.FAILURE("inserting new " + etype + ": " + zimbraId, e);
        } finally {
            DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
            DbPool.quietClose(conn);
        }
    }

    public static void createDirectoryLeafEntry(EntryType etype, NamedEntry parent, String name, String id, Map<String,Object> attrs) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DbPool.getConnection();

            stmt = conn.prepareStatement("INSERT INTO zimbra.directory_leaf (parent_id, entry_type, entry_name, zimbra_id)" +
                    " VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, getIdForParent(conn, parent));
            stmt.setString(2, etype.toString());
            stmt.setString(3, name);
            stmt.setString(4, id);
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (!rs.next())
                throw ServiceException.FAILURE("no autoincrement returned from INSERT", null);
            int entryId = rs.getInt(1);
            rs.close();
            stmt.close();

            for (Map.Entry<String,Object> attr : attrs.entrySet()) {
                String key = attr.getKey();
                Object vobject = attr.getValue();
                for (String value : (vobject instanceof String[] ? (String[]) vobject : new String[] { (String) vobject })) {
                    if (value != null)
                        insertAttribute(conn, etype, entryId, key, value);
                }
            }
    
            conn.commit();
        } catch (SQLException e) {
            if (e.getErrorCode() == Db.Error.DUPLICATE_ROW) {
                if (etype == EntryType.IDENTITY)
                    throw AccountServiceException.IDENTITY_EXISTS(name);
            } else {
                throw ServiceException.FAILURE("inserting new " + etype + ": " + parent.getName() + '/' + name, e);
            }
        } finally {
            DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
            DbPool.quietClose(conn);
        }
    }

    private static void insertAttribute(Connection conn, EntryType etype, int entryId, String key, String value)
    throws ServiceException, SQLException {
        PreparedStatement stmt = null;
        try {
            String table = (etype == EntryType.IDENTITY || etype == EntryType.DATASOURCE ? "zimbra.directory_leaf_attrs" : "zimbra.directory_attrs");
            stmt = conn.prepareStatement("INSERT INTO " + table + " (entry_id, name, value) VALUES (?, ?, ?)");
            stmt.setInt(1, entryId);
            stmt.setString(2, key);
            stmt.setString(3, value);
            stmt.executeUpdate();
            stmt.close();
        } finally {
            DbPool.closeStatement(stmt);
        }
    }

    public static List<String> listAllDirectoryEntries(EntryType etype) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DbPool.getConnection();

            stmt = conn.prepareStatement("SELECT zimbra_id FROM zimbra.directory WHERE entry_type = ?");
            stmt.setString(1, etype.toString());
            rs = stmt.executeQuery();
            List<String> ids = new ArrayList<String>();
            while (rs.next())
                ids.add(rs.getString(1));
            return ids;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("listing all entries of type " + etype, e);
        } finally {
            DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
            DbPool.quietClose(conn);
        }
    }

    public static List<String> listAllDirectoryLeaves(EntryType etype, NamedEntry parent) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DbPool.getConnection();

            stmt = conn.prepareStatement("SELECT entry_name FROM zimbra.directory_leaf WHERE parent_id = ? AND entry_type = ?");
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
            DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
            DbPool.quietClose(conn);
        }
    }

    public static List<String> searchDirectoryEntries(EntryType etype, String lookupKey, String lookupPattern) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DbPool.getConnection();

            int pos = 1;
            if (lookupKey.equals(Provisioning.A_zimbraId)) {
                stmt = conn.prepareStatement("SELECT zimbra_id FROM zimbra.directory WHERE zimbra_id LIKE ? AND entry_type = ?");
            } else if (lookupKey.equals(OfflineProvisioning.A_offlineDn)) {
                stmt = conn.prepareStatement("SELECT zimbra_id FROM zimbra.directory WHERE entry_name LIKE ? AND entry_type = ?");
            } else {
                stmt = conn.prepareStatement("SELECT zimbra_id FROM zimbra.directory d, zimbra.directory_attrs da" +
                        " WHERE name = ? AND value LIKE ? AND d.entry_id = da.entry_id AND entry_type = ?");
                stmt.setString(pos++, lookupKey);
            }
            stmt.setString(pos++, lookupPattern);
            stmt.setString(pos++, etype.toString());
            rs = stmt.executeQuery();
            List<String> ids = new ArrayList<String>();
            while (rs.next())
                ids.add(rs.getString(1));
            return ids;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("listing all entries of type " + etype, e);
        } finally {
            DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
            DbPool.quietClose(conn);
        }
    }

    public static Map<String,Object> readDirectoryEntry(EntryType etype, String lookupKey, String lookupValue) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DbPool.getConnection();

            int entryId = getIdForEntry(conn, etype, lookupKey, lookupValue);
            if (entryId <= 0)
                return null;

            stmt = conn.prepareStatement("SELECT name, value FROM zimbra.directory_attrs WHERE entry_id = ?");
            stmt.setInt(1, entryId);
            rs = stmt.executeQuery();
            Map<String,Object> attrs = new HashMap<String,Object>();
            while (rs.next())
                OfflineProvisioning.addToMap(attrs, rs.getString(1), rs.getString(2));
            if (attrs.isEmpty())
                return null;
            return attrs;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("fetching " + etype + " (" + lookupKey + "=" + lookupValue + ")", e);
        } finally {
            DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
            DbPool.quietClose(conn);
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
            conn = DbPool.getConnection();

            int entryId = getIdForLeaf(conn, etype, parent, lookupKey, lookupValue);
            if (entryId <= 0)
                return null;

            stmt = conn.prepareStatement("SELECT name, value FROM zimbra.directory_leaf_attrs WHERE entry_id = ?");
            stmt.setInt(1, entryId);
            rs = stmt.executeQuery();
            Map<String,Object> attrs = new HashMap<String,Object>();
            while (rs.next())
                OfflineProvisioning.addToMap(attrs, rs.getString(1), rs.getString(2));
            if (attrs.isEmpty())
                return null;
            return attrs;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("fetching " + etype + ": " + parent.getName() + '/' + lookupValue, e);
        } finally {
            DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
            DbPool.quietClose(conn);
        }
    }

    public static void modifyDirectoryEntry(EntryType etype, String lookupKey, String lookupValue, Map<String,? extends Object> attrs) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DbPool.getConnection();

            int entryId = getIdForEntry(conn, etype, lookupKey, lookupValue);
            if (entryId <= 0)
                return;

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
                            stmt = conn.prepareStatement("DELETE FROM zimbra.directory_attrs WHERE entry_id = ? AND name = ? AND value = ?");
                            stmt.setInt(1, entryId);
                            stmt.setString(2, key);
                            stmt.setString(3, value);
                            stmt.executeUpdate();
                            stmt.close();

                            // this is hacky, but we're doing redneck duplicate elimination by killing any existing entry/key/value pair first
                            if (doAdd)
                                insertAttribute(conn, etype, entryId, key, value);
                        }
                    }
                } else {
                    // get rid of any existing values for the key
                    stmt = conn.prepareStatement("DELETE FROM zimbra.directory_attrs WHERE entry_id = ? AND name = ?");
                    stmt.setInt(1, entryId);
                    stmt.setString(2, key);
                    stmt.executeUpdate();
                    stmt.close();

                    if (vobject != null) {
                        // and insert any new values
                        for (String value : (vobject instanceof String[] ? (String[]) vobject : new String[] { (String) vobject }))
                            insertAttribute(conn, etype, entryId, key, value);
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("fetching " + etype + " (" + lookupKey + "=" + lookupValue + ")", e);
        } finally {
            DbPool.closeStatement(stmt);
            DbPool.quietClose(conn);
        }
    }

    private static int getIdForEntry(Connection conn, EntryType etype, String lookupKey, String lookupValue) throws ServiceException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            if (lookupKey.equals(Provisioning.A_zimbraId)) {
                stmt = conn.prepareStatement("SELECT entry_id FROM zimbra.directory WHERE zimbra_id = ? AND entry_type = ?");
                stmt.setString(1, lookupValue);
                stmt.setString(2, etype.toString());
            } else if (lookupKey.equals(OfflineProvisioning.A_offlineDn)) {
                stmt = conn.prepareStatement("SELECT entry_id FROM zimbra.directory WHERE entry_name = ? AND entry_type = ?");
                stmt.setString(1, lookupValue);
                stmt.setString(2, etype.toString());
            } else {
                stmt = conn.prepareStatement("SELECT da.entry_id FROM zimbra.directory_attrs da, zimbra.directory d" +
                        " WHERE da.name = ? AND da.value = ? and da.entry_id = d.entry_id AND d.entry_type = ?" +
                        " GROUP BY da.entry_id");
                stmt.setString(1, lookupKey);
                stmt.setString(2, lookupValue);
                stmt.setString(3, etype.toString());
            }
            rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            return -1;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("fetching id for " + etype + " (" + lookupKey + "=" + lookupValue + ")", e);
        } finally {
            DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
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
                stmt = conn.prepareStatement("SELECT entry_id FROM zimbra.directory_leaf" +
                        " WHERE parent_id = ? AND entry_type = ? and zimbra_id = ?");
                stmt.setInt(1, getIdForParent(conn, parent));
                stmt.setString(2, etype.toString());
                stmt.setString(3, lookupValue);
            } else if (lookupKey.equals(OfflineProvisioning.A_offlineDn)) {
                stmt = conn.prepareStatement("SELECT entry_id FROM zimbra.directory_leaf" +
                        " WHERE parent_id = ? AND entry_type = ? and entry_name = ?");
                stmt.setInt(1, getIdForParent(conn, parent));
                stmt.setString(2, etype.toString());
                stmt.setString(3, lookupValue);
            } else {
                stmt = conn.prepareStatement("SELECT da.entry_id FROM zimbra.directory_leaf_attrs da, zimbra.directory_leaf d" +
                        " WHERE d.parent_id = ? AND d.entry_type = ? AND da.name = ? AND da.value = ? and da.entry_id = d.entry_id" +
                        " GROUP BY da.entry_id");
                stmt.setInt(1, getIdForParent(conn, parent));
                stmt.setString(2, etype.toString());
                stmt.setString(3, lookupKey);
                stmt.setString(4, lookupValue);
            }
            rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            return -1;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("fetching id for " + etype + ": " + parent.getName() + '/' + lookupValue, e);
        } finally {
            DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
        }
    }

    public static void deleteDirectoryEntry(EntryType etype, String zimbraId) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DbPool.getConnection();

            stmt = conn.prepareStatement("DELETE FROM zimbra.directory WHERE entry_type = ? AND zimbra_id = ?");
            stmt.setString(1, etype.toString());
            stmt.setString(2, zimbraId);
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("deleting " + etype + ": " + zimbraId, e);
        } finally {
            DbPool.closeStatement(stmt);
            DbPool.quietClose(conn);
        }
    }

    public static void deleteDirectoryLeaf(EntryType etype, NamedEntry parent, String name) throws ServiceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DbPool.getConnection();

            stmt = conn.prepareStatement("DELETE FROM zimbra.directory_leaf WHERE parent_id = ? AND entry_type = ? AND entry_name = ?");
            stmt.setInt(1, getIdForParent(conn, parent));
            stmt.setString(2, etype.toString());
            stmt.setString(3, name);
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("deleting " + etype + ": " + parent.getName() + '/' + name, e);
        } finally {
            DbPool.closeStatement(stmt);
            DbPool.quietClose(conn);
        }
    }
}
