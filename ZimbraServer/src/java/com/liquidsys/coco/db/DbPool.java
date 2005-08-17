/*
 * Created on Apr 7, 2004
 */
package com.liquidsys.coco.db;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.liquidsys.coco.localconfig.LC;
import com.liquidsys.coco.service.ServiceException;
import com.liquidsys.coco.service.util.LiquidPerf;
import com.liquidsys.coco.util.LiquidLog;

/**
 * @author schemers
 */
public class DbPool {

	public static class Connection {
		private java.sql.Connection mConnection;

		private Connection(java.sql.Connection conn)  { mConnection = conn; }

		public java.sql.Connection getConnection()  { return mConnection; }

		public void setTransactionIsolation(int level) throws ServiceException {
            try {
            	mConnection.setTransactionIsolation(level);
            } catch (SQLException e) {
            	throw ServiceException.FAILURE("setting database connection isolation level", e);
            }
		}

        /**
         * Disable foreign key constraint checking for this Connection.  Used by the mailbox restore code
         * so that it can do a LOAD DATA INFILE without hitting foreign key constraint troubles.
         *   
         * @throws ServiceException
         */
        public void disableForeignKeyConstraints() throws ServiceException {
            PreparedStatement stmt = null;
            try {
                stmt = mConnection.prepareStatement("SET FOREIGN_KEY_CHECKS=0");                
                stmt.execute();
            } catch (SQLException e) {
                throw ServiceException.FAILURE("disabling foreign key constraints", e);
            } finally {
                DbPool.closeStatement(stmt);
            }
        }

		public PreparedStatement prepareStatement(String sql) throws ServiceException {
            LiquidPerf.incrementPrepareCount();
            try {
            	return mConnection.prepareStatement(sql);
            } catch (SQLException e) {
            	throw ServiceException.FAILURE("preparing database statement", e);
            }
		}
		public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws ServiceException {
            try {
            	return mConnection.prepareStatement(sql, autoGeneratedKeys);
            } catch (SQLException e) {
            	throw ServiceException.FAILURE("preparing database statement", e);
            }
		}

		public void close() throws ServiceException {
            try {
                mConnection.close();
            } catch (SQLException e) {
            	throw ServiceException.FAILURE("closing database connection", e);
            }
		}

		public void rollback() throws ServiceException {
            try {
                mConnection.rollback();
            } catch (SQLException e) {
            	throw ServiceException.FAILURE("rolling back database transaction", e);
            }
		}
		
		public void commit() throws ServiceException {
			try {
				mConnection.commit();
            } catch (SQLException e) {
            	throw ServiceException.FAILURE("committing database transaction", e);
            }
		}
	}

	private static PoolingDriver mPoolingDriver;
    private static Log mLog = LogFactory.getLog(DbPool.class);

    static {
        String drivers = System.getProperty("jdbc.drivers");
        if (drivers == null)
            System.setProperty("jdbc.drivers", "com.mysql.jdbc.Driver");
        
        String myAddress = LC.mysql_bind_address.value();
        String myPort = LC.mysql_port.value();
        String url = "jdbc:mysql://" + myAddress + ":" + myPort + "/liquid";

        Properties props = getLiquidDbProps();
        // TODO: need to tune these
        int poolSize = 100;
        ObjectPool cpool = new GenericObjectPool(null, poolSize, GenericObjectPool.WHEN_EXHAUSTED_BLOCK, -1, poolSize);
        ConnectionFactory cfac = new DriverManagerConnectionFactory(url, props);
        
        boolean defAutoCommit = false;
        boolean defReadOnly = false;
        
        // I don't think we need PreparedStatement pooling as it appears
        // the lastest mysql driver does it internally. Need to investigate.
        PoolableConnectionFactory poolCF = 
            new PoolableConnectionFactory(cfac, cpool, null, null, defReadOnly, defAutoCommit);
        
        try {
        	Class.forName("com.mysql.jdbc.Driver");
        	Class.forName("org.apache.commons.dbcp.PoolingDriver");
            mPoolingDriver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
            mPoolingDriver.registerPool("liquid", cpool);
        } catch (ClassNotFoundException e) {
            mLog.fatal("can't init Pool", e);
            System.exit(1);
        } catch (SQLException e) {
            mLog.fatal("can't init Pool", e);
            System.exit(1);
        }
    };

    private static Properties getLiquidDbProps() {
        Properties props = new Properties();
        
        props.put("cacheResultSetMetadata", "true");
        props.put("cachePrepStmts", "true");
        props.put("prepStmtCacheSize", "25");        
        props.put("autoReconnect", "true");
        props.put("useUnicode", "true");
        props.put("characterEncoding", "UTF-8");
        props.put("dumpQueriesOnException", "true");

        //props.put("characterEncoding", "UnicodeBig");

        //props.put("cacheCallableStmts", "true");

        //props.put("prepStmtCacheSqlLmiit", "256");

        //props.put("connectTimeout", "0"); // connect timeout in msecs
        //props.put("initialTimeout", "2"); // time to wait between re-connects
        //props.put("maxReconnects", "3""); // max number of reconnects to attempt

        // Set/override MySQL Connector/J connection properties from
        // localconfig.  Localconfig keys with "liquid_mysql_connector."
        // prefix are used.
        String prefix = "liquid_mysql_connector.";
        int prefixLen = prefix.length();
        String[] keys = LC.getAllKeys();
        for (int i = 0; i < keys.length; i++) {
        	String key = keys[i];
            if (key.startsWith(prefix)) {
            	String prop = key.substring(prefixLen);
                if (prop.length() > 0 && !prop.equalsIgnoreCase("logger")) {
                	String val = LC.get(key);
                    mLog.info("Setting mysql connector property: " + prop + "=" + val);
                    props.put(prop, val);
                }
            }
        }

        if (LiquidLog.sqltrace.isDebugEnabled() || LiquidLog.perf.isDebugEnabled()) {
            props.put("profileSQL", "true");
            props.put("logger", MySqlTraceLogger.class.getName());
        }
        if (LiquidLog.perf.isDebugEnabled()) {
            props.put("slowQueryThresholdMillis", "300");
            props.put("logSlowQueries", "true");
            // xxx bburtin: for some reason, using explainSlowQueries causes the subsequent
            // call to executeQuery() after an EXPLAIN to hang.  Filed MySQL bug# 12229
	    // for this issue.  See http://bugs.mysql.com/bug.php?id=12229 for more info.
            // props.put("explainSlowQueries", "true");
        }
        
        // These properties cannot be set with "liquid_mysql_connector." keys.
        props.put("user", LC.liquid_mysql_user.value());
        props.put("password", LC.liquid_mysql_password.value());

        return props;
    }
    
    /**
     * return a connection to use for the liquid database.
     * @param 
     * @return
     * @throws ServiceException
     */
    public static Connection getConnection() throws ServiceException {
        java.sql.Connection conn = null;

        try {
	        String url = "jdbc:apache:commons:dbcp:liquid";
	        conn = DriverManager.getConnection(url);
	        
	        if (conn.getAutoCommit() != false)
	            conn.setAutoCommit(false);
	
	        // We want READ COMMITTED transaction isolation level for duplicate
	        // handling code in BucketBlobStore.newBlobInfo().
	        conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_COMMITTED);
        } catch (SQLException e) {
        	throw ServiceException.FAILURE("getting database connection", e);
        }

        return new Connection(conn);
    }
    
    /**
     * closes the specified connection (if not null), and catches any
     * exceptions on close, and logs them.
     * @param conn
     */
    public static void quietClose(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (ServiceException e) {
                if (mLog.isWarnEnabled())
                    mLog.warn("quietClose caught exception", e);
            }
        }
    }
    
    /**
     * Does a rollback the specified connection (if not null), and catches any
     * exceptions and logs them.
     * @param conn
     */
    public static void quietRollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (ServiceException e) {
                if (mLog.isWarnEnabled())
                    mLog.warn("quietRollback caught exception", e);
            }
        }
    }

    /**
     * Closes a statement and wraps any resulting exception in a ServiceException.
     * @param stmt
     * @throws ServiceException
     */
    public static void closeStatement(Statement stmt) throws ServiceException {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                throw ServiceException.FAILURE("closing statement", e);
            }
        }
    }

    /**
     * Closes a ResultSet and wraps any resulting exception in a ServiceException.
     * @param rs
     * @throws ServiceException
     */
    public static void closeResults(ResultSet rs) throws ServiceException {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw ServiceException.FAILURE("closing statement", e);
            }
        }
    }
}
