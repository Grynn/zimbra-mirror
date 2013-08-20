/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.taglib.memcached;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.spy.memcached.HashAlgorithm;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.memcached.ZimbraMemcachedClient;

public class MemcachedConnector {

    private static ZimbraMemcachedClient sTheClient = new ZimbraMemcachedClient();

    /**
     * Returns the one and only memcached client object.
     * @return
     */
    public static ZimbraMemcachedClient getClient() {
        return sTheClient;
    }

    /**
     * Startup the memcached connection.  Establish the memcached connection(s) if configured.
     * @throws ServiceException
     */
    public static void startup() throws ServiceException {
        reloadConfig();
    }

    /**
     * Are we currently connected to the memcached servers?
     * @return
     */
    public static boolean isConnected() {
        return sTheClient.isConnected();
    }

    /**
     * Reload the memcached client configuration.  Connect to the servers if configured with a
     * non-empty server list.  Any old connections are flushed and disconnected.
     * @throws ServiceException
     */
    public static void reloadConfig() throws ServiceException {
        String[] serverList = null;
        boolean useBinaryProtocol = false;
        String hashAlgorithm =  HashAlgorithm.KETAMA_HASH.toString();
        int expirySeconds = 86400;
        long timeoutMillis = 10000;
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");

            serverList = ((String) envCtx.lookup("memcachedServers")).split("\\s+");
            useBinaryProtocol = (Boolean) envCtx.lookup("memcachedClientBinaryProtocolEnabled");
            hashAlgorithm = (String) envCtx.lookup("memcachedClientHashAlgorithm");
            expirySeconds = (Integer) envCtx.lookup("memcachedClientExpiry");
            timeoutMillis = (Integer) envCtx.lookup("memcachedClientTimeout");
        } catch (NamingException ne) {
            //REDO to throw the error or print stack trace??
            ne.printStackTrace();
        } catch (Exception e) {
            //REDO to throw the error or print stack trace??
            e.printStackTrace();
        }
        sTheClient.connect(serverList, useBinaryProtocol, hashAlgorithm, expirySeconds, timeoutMillis);
    }

    /**
     * Shutdown the memcached connection.
     * @throws ServiceException
     */
    public static void shutdown() throws ServiceException {
        sTheClient.disconnect(30000);
        sTheClient = null;
    }
}
