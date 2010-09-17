/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
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

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.db.Db.Capability;
import com.zimbra.cs.db.DbPool.Connection;
import com.zimbra.cs.db.DbPool.PoolConfig;
import com.zimbra.cs.db.DbPool.ZimbraConnectionFactory;

/**
 * Connection pool for Offline DB operations. Assumes DbPool has been started prior to first access. Application must call shutdown() to exit cleanly.
 *
 */
public class OfflineDbPool {

    private static OfflineDbPool instance;
    
    public static synchronized OfflineDbPool getInstance() {
        if (instance == null) {
            instance = new OfflineDbPool();
        }
        return instance;
    }
    
    private PoolingDataSource mDataSource;
    private GenericObjectPool mConnectionPool;

    private OfflineDbPool() {
        initPool();
    }
    
    public Connection getConnection() throws ServiceException {
        java.sql.Connection dbconn = null;
        Connection conn = null;
        try {
            dbconn = mDataSource.getConnection();
            if (dbconn.getAutoCommit() != false) {
                dbconn.setAutoCommit(false);
            }
            conn = new Connection(dbconn);
            Db.getInstance().postOpen(conn);
        } catch (SQLException e) {
            try {
                if (dbconn != null && !dbconn.isClosed()) {
                    dbconn.close();
                }
            } catch (SQLException e2) {
                ZimbraLog.sqltrace.warn("DB connection close caught exception", e);
            }
            throw ServiceException.FAILURE("getting database connection", e);
        }
        return conn;
    }
    
    private void initPool() {
        PoolConfig pconfig = Db.getInstance().getPoolConfig();
        int poolSize = Db.supports(Capability.ROW_LEVEL_LOCKING) ? pconfig.mPoolSize : 1; 
        //if no row lock, we need serial access to the underlying db file
        //still need external synchronization for now since other connections to zimbra.db can occur through DbPool
        mConnectionPool = new GenericObjectPool(null, poolSize, GenericObjectPool.WHEN_EXHAUSTED_BLOCK, -1, poolSize);
        ConnectionFactory cfac = new ZimbraConnectionFactory(pconfig.mConnectionUrl, pconfig.mDatabaseProperties);
        boolean defAutoCommit = false, defReadOnly = false;
        new PoolableConnectionFactory(cfac, mConnectionPool, null, null, defReadOnly, defAutoCommit);
        try {
            PoolingDataSource pds = new PoolingDataSource(mConnectionPool);
            pds.setAccessToUnderlyingConnectionAllowed(true);
            Db.getInstance().startup(pds, pconfig.mPoolSize);
            mDataSource = pds;
        } catch (SQLException e) {
            ZimbraLog.system.error("failed to initialize offline db pool", e);
        }
    }

    public void closeResults(ResultSet rs) throws ServiceException {
        DbPool.closeResults(rs);
    }

    public void closeStatement(PreparedStatement stmt) throws ServiceException {
        DbPool.closeStatement(stmt);
    }
    
    public void quietClose(Connection conn) {
        DbPool.quietClose(conn);
    }
    
    public void close() throws Exception {
        if (mConnectionPool != null) {
            mConnectionPool.close();
            mConnectionPool = null;
        }
        mDataSource = null;
    }

    public static synchronized void shutdown() throws Exception {
        getInstance().close();
    }
}
