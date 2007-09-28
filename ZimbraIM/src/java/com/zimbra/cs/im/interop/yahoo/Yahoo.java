/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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
package com.zimbra.cs.im.interop.yahoo;

import java.net.InetSocketAddress;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

/**
 * 
 */
public class Yahoo {
    //private static final String HOSTNAME = "localhost";
    private static final String HOSTNAME = "scs.msg.yahoo.com";
//  private static final String HOSTNAME = "216.155.193.162";
    private static final int PORT = 5050;
    

    /**
     * This is the primary interface used to connect to the yahoo service.
     * 
     * @param listener
     * @param authProvider
     * @param username
     * @param password
     * @return
     */
    public static YahooSession connect(YahooEventListener listener, YMSGAuthProvider authProvider,
        String username, String password) {

        SocketConnector connector = new SocketConnector();

        // Change the worker timeout to 1 second to make the I/O thread quit soon
        // when there's no connection to manage.
        connector.setWorkerTimeout( 10 );
        
        // Configure the service.
        SocketConnectorConfig cfg = new SocketConnectorConfig();
        cfg.setConnectTimeout(10);
        
        cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new YMSGProtocolCodecFactory()));

        // class com.zimbra.cs.im.interop.yahoo.YMSGSession
        //cfg.getFilterChain().addLast( "logger", new LoggingFilter() );
        
        YMSGSession ymsg = new YMSGSession(authProvider, listener, username, password);
        ConnectFuture future = connector.connect(new InetSocketAddress( HOSTNAME, PORT ), ymsg, cfg);
        future.addListener(ymsg);
        return ymsg;
    }
}
