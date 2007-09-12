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
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.database;

import org.jivesoftware.database.AbstractConnection;
import org.jivesoftware.database.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * An implementation of the Connection interface that wraps an underlying
 * Connection object. It releases the connection back to a connection pool
 * when Connection.close() is called.
 *
 * @author Jive Software
 */
public class ConnectionWrapper extends AbstractConnection {

    public ConnectionPool pool;
    public boolean checkedout = false;
    public long createTime;
    public long lockTime;
    public long checkinTime;
    public Exception exception;
    public boolean hasLoggedException = false;

    public ConnectionWrapper(Connection connection, ConnectionPool pool) {
        super(connection);

        this.pool = pool;
        createTime = System.currentTimeMillis();
        lockTime = createTime;
        checkinTime = lockTime;
    }

    public void setConnection(Connection connection) {
        super.connection = connection;
    }

    /**
     * Instead of closing the underlying connection, we simply release
     * it back into the pool.
     */
    public void close() throws SQLException {
        synchronized (this) {
            checkedout = false;
            checkinTime = System.currentTimeMillis();
        }

        pool.freeConnection();

        // Release object references. Any further method calls on the connection will fail.
        // super.connection = null;
    }

    public String toString() {
        if (connection != null) {
            return connection.toString();
        }
        else {
            return "Jive Software Connection Wrapper";
        }
    }

    public synchronized boolean isCheckedOut() {
        return checkedout;
    }
}
