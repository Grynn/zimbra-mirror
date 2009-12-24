/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009 Zimbra, Inc.
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
package com.zimbra.cs.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.derby.iapi.error.StandardException;

import com.zimbra.cs.db.DbPool.Connection;

public class DbOfflineMigration {

    public void testRun() throws Exception {
        runInternal(true);
    }

    public void run() throws Exception {
        runInternal(false);
    }

    public void runInternal(boolean isTestRun) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int newOfflineDbVersion = OfflineVersions.OFFLINE_DB_VERSION;
        int oldOfflineDbVersion = 1; // default to 1 if missing

        try {
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
                case 0:
                    // migrateFromVersionXX(conn, isTestRun);
                    // fall-through
                case 1:
                    // migrateFromVersionYY(conn, isTestRun);
                    // fall-through
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
            DbPool.quietClose(conn);
            if (oldOfflineDbVersion != newOfflineDbVersion) {
                DbPool.close();
                DbPool.startup();
            }
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
            "/Users/jjzhuang/zimbra/zdesktop/conf/localconfig.xml");

        new DbOfflineMigration().testRun();
        new DbOfflineMigration().testRun();
    }
}
