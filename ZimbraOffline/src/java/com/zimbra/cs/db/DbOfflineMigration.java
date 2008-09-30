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
            conn = DbPool.getConnection();
            stmt = conn.prepareStatement("SELECT value FROM zimbra.config WHERE name = 'db.version'");
            rs = stmt.executeQuery();
            rs.next();
            int oldDbVersion = Integer.parseInt(rs.getString(1));
            rs.close();
            stmt.close();
            
            int newDbVersion = Integer.parseInt(Versions.DB_VERSION);
            System.out.println("oldDbVersion=" + oldDbVersion + " newDbVersion=" + newDbVersion);
            
            if (oldDbVersion == newDbVersion)
            	return;
            
            switch (oldDbVersion) {
            case 51:
            	migrateFromVersion51(conn, isTestRun);
                //fall-through
            case 52:
            	migrateFromVersion52(conn, isTestRun);
                //fall-through
            case 53:
                migrateFromVersion53(conn, isTestRun);
                break;
            default:
            	throw new DbUnsupportedVersionException();
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
	
    private static final String sql53to54 =
        "SET SCHEMA zimbra;\n;"+
        "DROP TABLE IF EXISTS zimbra.jiveVersion;\n"+
        "DROP TABLE IF EXISTS zimbra.mucRoom;\n"+
        "DROP TABLE IF EXISTS zimbra.mucRoomProp;\n"+
        "DROP TABLE IF EXISTS zimbra.mucAffiliation;\n"+
        "DROP TABLE IF EXISTS zimbra.mucMember;\n"+
        "DROP TABLE IF EXISTS zimbra.mucConversationLog;\n"+
        "CREATE TABLE mucRoom (\n"+
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
        ");\n"+
        "\n"+
        "CREATE INDEX mucRoom_roomid_idx ON mucRoom(service,roomID);\n"+
        "\n"+
        "CREATE TABLE mucRoomProp (\n"+
        "   service               VARCHAR(255)    NOT NULL,\n"+
        "   roomID                BIGINT          NOT NULL,\n"+
        "   name                  VARCHAR(100)    NOT NULL,\n"+
        "   propValue             CLOB            NOT NULL,\n"+
        "\n"+
        "   CONSTRAINT pk_mucRoomProp PRIMARY KEY (service,roomID, name)\n"+
        ");\n"+
        "\n"+
        "CREATE TABLE mucAffiliation (\n"+
        "   service               VARCHAR(255)    NOT NULL,\n"+
        "   roomID                BIGINT          NOT NULL,\n"+
        "   jid                   VARCHAR(32672)  NOT NULL,\n"+
        "   affiliation           SMALLINT        NOT NULL,\n"+
        "\n"+
        "   CONSTRAINT pk_mucAffiliation PRIMARY KEY (service,roomID, jid)\n"+
        ");\n"+
        "\n"+
        "CREATE TABLE mucMember (\n"+
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
        ");\n"+
        "\n"+
        "CREATE TABLE mucConversationLog (\n"+
        "   service               VARCHAR(255)    NOT NULL,\n"+
        "   roomID                BIGINT          NOT NULL,\n"+
        "   sender                CLOB            NOT NULL,\n"+
        "   nickname              VARCHAR(255),\n"+
        "   time                  CHAR(15)        NOT NULL,\n"+
        "   subject               VARCHAR(255),\n"+
        "   body                  CLOB\n"+
        ");\n"+
        "\n"+
        "CREATE INDEX mucLog_time_idx ON mucConversationLog(time);\n";
        
    
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
        PreparedStatement stmt = null;
        boolean isSuccess = false;
        try {
            stmt = conn.prepareStatement(sql53to54);
            stmt.executeUpdate();
            stmt.close();
            stmt = conn.prepareStatement("UPDATE zimbra.config set value='60' where name='db.version'");
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
	
	public static void main(String[] args) throws Exception {
		System.setProperty("zimbra.config", "/Users/jjzhuang/zimbra/zdesktop/conf/localconfig.xml");
		
		new DbOfflineMigration().testRun();
	}
}
