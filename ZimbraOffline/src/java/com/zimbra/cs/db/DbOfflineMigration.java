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

import java.util.ArrayList;
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
        try {
            DbPool.startup();
            conn = DbPool.getConnection();
            stmt = conn.prepareStatement("SELECT value FROM zimbra.config WHERE name = 'db.version'");
            rs = stmt.executeQuery();
            rs.next();
            int oldDbVersion = Integer.parseInt(rs.getString(1));
            rs.close();
            stmt.close();
            
            int newDbVersion = Integer.parseInt(Versions.DB_VERSION);
            System.out.println("oldDbVersion=" + oldDbVersion + " newDbVersion=" + newDbVersion);
            
            if (oldDbVersion != newDbVersion) {
            switch (oldDbVersion) {
            case 51:
            	migrateFromVersion51(conn, isTestRun);
                //fall-through
            case 52:
            	migrateFromVersion52(conn, isTestRun);
                //fall-through
            case 53:
                migrateFromVersion53(conn, isTestRun);
            case 61:
                migrateFromVersion61(conn, isTestRun);
            case 62:
                migrateFromVersion62(conn, isTestRun);
                break;
            default:
            	throw new DbUnsupportedVersionException();
            }
            }
                        
            //now do offline specific db migration
            stmt = conn.prepareStatement("SELECT value FROM zimbra.config WHERE name = 'offline.db.version'");
            rs = stmt.executeQuery();
            int oldOfflineDbVersion = 1; //default to 1 if missing
            if (rs.next())
            	oldOfflineDbVersion = Integer.parseInt(rs.getString(1));
            rs.close();
            stmt.close();
            
            int newOfflineDbVersion = OfflineVersions.OFFLINE_DB_VERSION;
            System.out.println("oldOfflineDbVersion=" + oldOfflineDbVersion + " newOfflineDbVersion=" + newOfflineDbVersion);
            
            if (oldOfflineDbVersion != newOfflineDbVersion) {
	            switch (oldOfflineDbVersion) {
	            case 1:
	            	migrateFromOfflineVersion1(conn, isTestRun);
	            case 2:
	                migrateFromOfflineVersion2(conn, isTestRun);
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
            DbPool.close();
        }
	}
	
	private void migrateFromVersion51(Connection conn, boolean isTestRun) throws Exception {
        PreparedStatement stmt = null;
        boolean isSuccess = false;
        try {
            stmt = conn.prepareStatement("ALTER TABLE zimbra.mailbox ADD COLUMN idx_deferred_count INTEGER NOT NULL DEFAULT 0");
            stmt.executeUpdate();
            stmt.close();
           
            stmt = conn.prepareStatement("DELETE FROM zimbra.directory_attrs WHERE name='offlineModifiedAttrs'");
            stmt.executeUpdate();
            stmt.close();
            
            stmt = conn.prepareStatement("UPDATE zimbra.config set value='52' where name='db.version'");
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
	
    private static final String sql53to60_createMucRoom =
        "CREATE TABLE zimbra.mucRoom (\n"+
        "   service               VARCHAR(255)    NOT NULL,\n"+
        "   roomID                BIGINT          NOT NULL,\n"+
        "   creationDate          CHAR(15)        NOT NULL,\n"+
        "   modificationDate      CHAR(15)        NOT NULL,\n"+
        "   name                  VARCHAR(50)     NOT NULL,\n"+
        "   naturalName           VARCHAR(255)    NOT NULL,\n"+
        "   description           VARCHAR(255),\n"+
        "   lockedDate            CHAR(15)        NOT NULL,\n"+
        "   emptyDate             CHAR(15),\n"+
        "   canChangeSubject      SMALLINT        NOT NULL,\n"+
        "   maxUsers              INTEGER         NOT NULL,\n"+
        "   publicRoom            SMALLINT        NOT NULL,\n"+
        "   moderated             SMALLINT        NOT NULL,\n"+
        "   membersOnly           SMALLINT        NOT NULL,\n"+
        "   canInvite             SMALLINT        NOT NULL,\n"+
        "   password              VARCHAR(50),\n"+
        "   canDiscoverJID        SMALLINT        NOT NULL,\n"+
        "   logEnabled            SMALLINT        NOT NULL,\n"+
        "   subject               VARCHAR(100),\n"+
        "   rolesToBroadcast      SMALLINT        NOT NULL,\n"+
        "   useReservedNick       SMALLINT        NOT NULL,\n"+
        "   canChangeNick         SMALLINT        NOT NULL,\n"+
        "   canRegister           SMALLINT        NOT NULL,\n"+
        "\n"+
        "   CONSTRAINT pk_mucRoom PRIMARY KEY (service,name)\n"+
        ")";
    
    private static final String sql53to60_createMucRoomIdx = 
        "CREATE INDEX mucRoom_roomid_idx ON zimbra.mucRoom(service,roomID)";
    
    private static final String sql53to60_createMucRoomProp =
        "CREATE TABLE zimbra.mucRoomProp (\n"+
        "   service               VARCHAR(255)    NOT NULL,\n"+
        "   roomID                BIGINT          NOT NULL,\n"+
        "   name                  VARCHAR(100)    NOT NULL,\n"+
        "   propValue             CLOB            NOT NULL,\n"+
        "\n"+
        "   CONSTRAINT pk_mucRoomProp PRIMARY KEY (service,roomID, name)\n"+
        ")";
    
    private static final String sql53to60_createMucAffiliation =
        "CREATE TABLE zimbra.mucAffiliation (\n"+
        "   service               VARCHAR(255)    NOT NULL,\n"+
        "   roomID                BIGINT          NOT NULL,\n"+
        "   jid                   VARCHAR(32672)  NOT NULL,\n"+
        "   affiliation           SMALLINT        NOT NULL,\n"+
        "\n"+
        "   CONSTRAINT pk_mucAffiliation PRIMARY KEY (service,roomID, jid)\n"+
        ")";
    
    private static final String sql53to60_createMucMember =
        "CREATE TABLE zimbra.mucMember (\n"+
        "   service               VARCHAR(255)    NOT NULL,\n"+
        "   roomID                BIGINT          NOT NULL,\n"+
        "   jid                   VARCHAR(32672)  NOT NULL,\n"+
        "   nickname              VARCHAR(255),\n"+
        "   firstName             VARCHAR(100),\n"+
        "   lastName              VARCHAR(100),\n"+
        "   url                   VARCHAR(100),\n"+
        "   email                 VARCHAR(100),\n"+
        "   faqentry              VARCHAR(100),\n"+
        "\n"+
        "   CONSTRAINT pk_mucMember PRIMARY KEY (service,roomID, jid)\n"+
        ")";
    
    private static final String sql53to60_createMucConversationLog =
        "CREATE TABLE zimbra.mucConversationLog (\n"+
        "   service               VARCHAR(255)    NOT NULL,\n"+
        "   roomID                BIGINT          NOT NULL,\n"+
        "   sender                CLOB            NOT NULL,\n"+
        "   nickname              VARCHAR(255),\n"+
        "   time                  CHAR(15)        NOT NULL,\n"+
        "   subject               VARCHAR(255),\n"+
        "   body                  CLOB\n"+
        ")";
    
    private static final String sql53to60_createMucLogIdx =
        "CREATE INDEX mucLog_time_idx ON zimbra.mucConversationLog(time)";        
    
	private void migrateFromVersion52(Connection conn, boolean isTestRun) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean isSuccess = false;
        ArrayList<String> mboxgroups = new ArrayList<String>();
        try {
            stmt = conn.prepareStatement("SELECT schemaname FROM SYS.SYSSCHEMAS");
            rs = stmt.executeQuery();
            while (rs.next()) {
            	String name = rs.getString(1);
            	if (name.toLowerCase().startsWith("mboxgroup"))
            		mboxgroups.add(name);
            }
            rs.close();
            stmt.close();

            for (String mboxgroup : mboxgroups) {
                String stmtStr = 
                	"CREATE TABLE " + mboxgroup + ".data_source_item (" +
            		   "mailbox_id     INTEGER NOT NULL," +
            		   "data_source_id CHAR(36) NOT NULL," +
            		   "item_id        INTEGER NOT NULL," +
            		   "remote_id      VARCHAR(255) NOT NULL," +
            		   "metadata       CLOB," +
            		   "PRIMARY KEY (mailbox_id, item_id)," +
            		   "CONSTRAINT fk_data_source_item_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id)" +
            		")";
                stmt = conn.prepareStatement(stmtStr);
                stmt.executeUpdate();
                stmt.close();
                stmtStr = 
            		"CREATE UNIQUE INDEX i_remote_id ON " +
            		mboxgroup + ".data_source_item (mailbox_id, data_source_id, remote_id)";
                stmt = conn.prepareStatement(stmtStr);
                stmt.executeUpdate();
                stmt.close();
                
                //Rename "Sync Failures" to "Error Reports"
                stmt = conn.prepareStatement("SELECT name FROM " + mboxgroup + ".mail_item WHERE id=252");
                rs = stmt.executeQuery();
                String failureFolderName = null;
                boolean folderExists = false;
                if (rs.next()) {
                	folderExists = true;
                	failureFolderName = rs.getString(1);
                }
                rs.close();
                stmt.close();
                
                String newName = "Error Reports";
                if (folderExists && !newName.equals(failureFolderName)) {
                	stmt = conn.prepareStatement("UPDATE " + mboxgroup + ".mail_item SET name='" + newName + "' , subject='" + newName + "' WHERE id=252");
                	stmt.executeUpdate();
                	stmt.close();
                }
            }
            
            stmt = conn.prepareStatement("UPDATE zimbra.config set value='53' where name='db.version'");
            stmt.executeUpdate();
            stmt.close();
            
            isSuccess = true;
        } finally {
        	DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
            if (isTestRun || !isSuccess)
            	conn.rollback();
            else
            	conn.commit();
        }
	}
	
	private void migrateFromVersion53(Connection conn, boolean isTestRun) throws Exception {        	    
        boolean isSuccess = false;
        try {
            dropTableIfExists(conn, "zimbra.jiveVersion");
            dropTableIfExists(conn, "zimbra.mucRoom");
            dropTableIfExists(conn, "zimbra.mucRoomProp");
            dropTableIfExists(conn, "zimbra.mucAffiliation");
            dropTableIfExists(conn, "zimbra.mucMember");
            dropTableIfExists(conn, "zimbra.mucConversationLog");
            
            executeUpdateStatement(conn, sql53to60_createMucRoom);
            executeUpdateStatement(conn, sql53to60_createMucRoomIdx);
            executeUpdateStatement(conn, sql53to60_createMucRoomProp);
            executeUpdateStatement(conn, sql53to60_createMucAffiliation);
            executeUpdateStatement(conn, sql53to60_createMucMember);
            executeUpdateStatement(conn, sql53to60_createMucConversationLog);
            executeUpdateStatement(conn, sql53to60_createMucLogIdx);
            
            executeUpdateStatement(conn, "UPDATE zimbra.config set value='60' where name='db.version'");
            
            isSuccess = true;
        } finally {
            if (isTestRun || !isSuccess)
                conn.rollback();
            else
                conn.commit();
        }
	}
	
	private void migrateFromOfflineVersion1(Connection conn, boolean isTestRun) throws Exception {
        PreparedStatement stmt = null;
        boolean isSuccess = false;
        try {
            stmt = conn.prepareStatement("ALTER TABLE zimbra.directory_attrs ALTER value SET DATA TYPE VARCHAR(32672)");
            stmt.executeUpdate();
            stmt.close();
           
            stmt = conn.prepareStatement("ALTER TABLE zimbra.directory_leaf_attrs ALTER value SET DATA TYPE VARCHAR(32672)");
            stmt.executeUpdate();
            stmt.close();
            
            //if it's from version 1 the offline.db.version row is missing
            stmt = conn.prepareStatement("INSERT INTO zimbra.config(name, value, description) VALUES('offline.db.version', '2', 'offline db schema version')");
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
	
	private void migrateFromOfflineVersion2(Connection conn, boolean isTestRun) throws Exception {
        PreparedStatement stmt = null;
        PreparedStatement stmt2 = null;
        ResultSet rs = null;
        boolean isSuccess = false;        
        try {
            stmt = conn.prepareStatement("CREATE TABLE zimbra.directory_granter (granter_name VARCHAR(128) NOT NULL, " + 
                "granter_id CHAR(36) NOT NULL, grantee_id CHAR(36) NOT NULL, CONSTRAINT pk_dgranter PRIMARY KEY(granter_name, grantee_id))");
            stmt.executeUpdate();
            stmt.close();
            stmt = conn.prepareStatement("CREATE INDEX i_dgranter_gter_name ON zimbra.directory_granter(granter_name)");
            stmt.executeUpdate();
            stmt.close();
            stmt = conn.prepareStatement("CREATE INDEX i_dgranter_gter_id ON zimbra.directory_granter(granter_id)");
            stmt.executeUpdate();
            stmt.close();
            stmt = conn.prepareStatement("CREATE INDEX i_dgranter_gtee_id ON zimbra.directory_granter(grantee_id)");
            stmt.executeUpdate();            
            stmt.close();
            
            stmt = conn.prepareStatement("SELECT zimbra.directory.entry_name, zimbra.directory.zimbra_id, zimbra.directory_attrs.value, " + 
                "zimbra.directory.entry_id FROM zimbra.directory, zimbra.directory_attrs WHERE " + 
                "UPPER(zimbra.directory_attrs.name)='OFFLINEMOUNTPOINTPROXYACCOUNTID' AND zimbra.directory.entry_id=zimbra.directory_attrs.entry_id");
            rs = stmt.executeQuery();
            while(rs.next()) {
                stmt2 = conn.prepareStatement("INSERT INTO zimbra.directory_granter(granter_name, granter_id, grantee_id) VALUES(?, ?, ?)");
                stmt2.setString(1, rs.getString(1));
                stmt2.setString(2, rs.getString(2));
                stmt2.setString(3, rs.getString(3));
                stmt2.executeUpdate();
                stmt2.close();
            
                stmt2 = conn.prepareStatement("DELETE FROM zimbra.directory where entry_id = ?");
                stmt2.setInt(1, rs.getInt(4));
                stmt2.executeUpdate();
                stmt2.close();
            }
            
            stmt = conn.prepareStatement("UPDATE zimbra.config SET value='3' WHERE name='offline.db.version'");
            stmt.executeUpdate();            
            stmt.close();
            
            isSuccess = true;
        } finally {
            DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
            DbPool.closeStatement(stmt2);
            if (isTestRun || !isSuccess)
                conn.rollback();
            else
                conn.commit();
        }        
	}
	
	private void migrateFromVersion61(Connection conn, boolean isTestRun) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean isSuccess = false;
        ArrayList<String> mboxgroups = new ArrayList<String>();
        try {
            stmt = conn.prepareStatement("SELECT schemaname FROM SYS.SYSSCHEMAS");
            rs = stmt.executeQuery();
            while (rs.next()) {
            	String name = rs.getString(1);
            	if (name.toLowerCase().startsWith("mboxgroup"))
            		mboxgroups.add(name);
            }
            rs.close();
            stmt.close();

            for (String mboxgroup : mboxgroups) {
            	String dsitem = mboxgroup + ".data_source_item";
            	String mitem = mboxgroup + ".mail_item";
            	String dsitemid = dsitem + ".item_id";
            	String mitemid = mitem + ".id";
            	
                String stmtStr = 
                	"ALTER TABLE " + dsitem +
            		   " ADD COLUMN folder_id INTEGER NOT NULL DEFAULT 0";
                stmt = conn.prepareStatement(stmtStr);
                stmt.executeUpdate();
                stmt.close();
                stmtStr = 
                	"UPDATE " + dsitem +
                	  " SET folder_id = " +
                	  " (SELECT folder_id FROM " + mitem + " WHERE " + dsitemid + " = " + mitemid + ")";
                stmt = conn.prepareStatement(stmtStr);
                stmt.executeUpdate();
                stmt.close();
            }
            
            stmt = conn.prepareStatement("UPDATE zimbra.config set value='62' where name='db.version'");
            stmt.executeUpdate();
            stmt.close();
            
            isSuccess = true;
        } finally {
        	DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
            if (isTestRun || !isSuccess)
            	conn.rollback();
            else
            	conn.commit();
        }
	}
	
    private void migrateFromVersion62(Connection conn, boolean isTestRun) throws Exception {
        PreparedStatement stmt = null;
        boolean isSuccess = false;
        try {
            stmt = conn.prepareStatement("ALTER TABLE zimbra.mailbox ADD COLUMN highest_indexed VARCHAR(21)");
            stmt.executeUpdate();
            stmt.close();
           
            stmt = conn.prepareStatement("UPDATE zimbra.config set value='63' where name='db.version'");
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
    
    // derby does not support "drop table if exists...", so have to do this programmatically
	private void dropTableIfExists(Connection conn, String table) throws Exception {
	    try {
	        executeUpdateStatement(conn, "DROP TABLE " + table);
	    } catch (SQLException e) {
	        if (!e.getSQLState().equals("42Y55")) // derby error - table does not exist
	            throw e;
	    }
	}
	
	private void executeUpdateStatement(Connection conn, String sql) throws Exception {
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
		System.setProperty("zimbra.config", "/Users/jjzhuang/zimbra/zdesktop/conf/localconfig.xml");
		
		new DbOfflineMigration().testRun();
		new DbOfflineMigration().testRun();
	}
}
