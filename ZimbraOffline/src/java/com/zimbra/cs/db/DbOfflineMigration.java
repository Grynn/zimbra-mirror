/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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

import org.apache.derby.iapi.error.StandardException;

import com.zimbra.cs.db.DbPool.Connection;

public class DbOfflineMigration {

    public void testRun() throws Exception {
        runInternal(true, null);
    }

    public void run(Connection conn) throws Exception {
        runInternal(false, conn);
    }

    public void runInternal(boolean isTestRun, Connection dbConn) throws Exception {
        Connection conn = dbConn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int newOfflineDbVersion = OfflineVersions.OFFLINE_DB_VERSION;
        int oldOfflineDbVersion = 1; // default to 1 if missing

        try {
            if (conn == null)
                conn = DbPool.getConnection();
            
            stmt = conn.prepareStatement("SELECT value FROM config WHERE name = 'db.version'");
            rs = stmt.executeQuery();
            rs.next();
            int oldDbVersion = Integer.parseInt(rs.getString(1));
            rs.close();
            stmt.close();

            int newDbVersion = Integer.parseInt(Versions.DB_VERSION);
            System.out.println("oldDbVersion=" + oldDbVersion + " newDbVersion=" + newDbVersion);

            if (oldDbVersion != newDbVersion) {
                switch (oldDbVersion) {
                case 63:
                    migrateFromVersion63(conn, isTestRun);
                case 64:
                    migrateFromVersion64(conn, isTestRun);
                    //if there are more versions, let it fall through
                    break;
                default:
                    throw new DbUnsupportedVersionException();
                }
            }

            // now do offline specific db migration
            stmt = conn.prepareStatement("SELECT value FROM config WHERE name = 'offline.db.version'");
            rs = stmt.executeQuery();
            if (rs.next())
                oldOfflineDbVersion = Integer.parseInt(rs.getString(1));
            rs.close();
            stmt.close();

            System.out.println("oldOfflineDbVersion=" + oldOfflineDbVersion +
                " newOfflineDbVersion=" + newOfflineDbVersion);

            if (oldOfflineDbVersion != newOfflineDbVersion) {
                switch (oldOfflineDbVersion) {
                case 1:
                    // migrateFromOfflineVersionX(conn, isTestRun);
                    break;
                default:
                    throw new DbUnsupportedVersionException();
                }
            }
        } catch (Exception x) {
            x.printStackTrace(System.err);
            if (x.getCause() instanceof SQLException) {
                SQLException sqlException = (SQLException)x.getCause();
                if (sqlException.getSQLState().equalsIgnoreCase("XJ040")) {
                    throw new DbExclusiveAccessException();
                } else if (sqlException.getSQLState().equalsIgnoreCase("XJ004")) {
                    throw new DbNotFoundException();
                } else {
                    throw new DbDataCorruptedException();
                }
            } else if (x.getCause() instanceof StandardException) {
                throw new DbUpdateException();
            } else {
                throw x;
            }
        } finally {
            DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
            if (dbConn == null)
                DbPool.quietClose(conn);
        }
    }
    
    private void migrateFromVersion63(Connection conn, boolean isTestRun) throws Exception {
        PreparedStatement stmt = null;
        boolean isSuccess = false;
        try {
            stmt = conn.prepareStatement("ALTER TABLE mobile_devices ADD COLUMN policy_values VARCHAR(512);");
            stmt.executeUpdate();
            stmt.close();
            
            stmt = conn.prepareStatement("UPDATE config set value='64' where name='db.version'");
            stmt.executeUpdate();
            stmt.close();
            
            isSuccess = true;
        } finally {
            DbPool.closeStatement(stmt);
            if (isTestRun || !isSuccess)
                conn.rollback();
            else
                conn.commit();
        }
    }
    
    private void migrateFromVersion64(Connection conn, boolean isTestRun) throws Exception {
        PreparedStatement stmt = null;
        boolean isSuccess = false;
        try {
            // only update db.version without actually creating dumpster tables
            stmt = conn.prepareStatement("UPDATE config set value='65' where name='db.version'");
            stmt.executeUpdate();
            stmt.close();
            
            isSuccess = true;
        } finally {
            DbPool.closeStatement(stmt);
            if (isTestRun || !isSuccess)
                conn.rollback();
            else
                conn.commit();
        }
    }
    
    // derby does not support "drop table if exists...", so have to do this
    // programmatically
    public void dropTableIfExists(Connection conn, String table)
        throws Exception {
        try {
            executeUpdateStatement(conn, "DROP TABLE " + table);
        } catch (SQLException e) {
            if (!e.getSQLState().equals("42Y55")) // derby error - table does not exist
                throw e;
        }
    }

    private void executeUpdateStatement(Connection conn, String sql)
        throws Exception {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
            stmt.close();
        } finally {
            DbPool.closeStatement(stmt);
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("zimbra.config",
            "/Users/jjzhuang/Library/Zimbra Desktop/conf/localconfig.xml");

        new DbOfflineMigration().testRun();
        new DbOfflineMigration().testRun();
    }
}
