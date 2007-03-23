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
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.im.interop.yahoo;

import java.net.InetSocketAddress;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.RuntimeIOException;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

import com.zimbra.cs.im.interop.yahoo.YahooEventListener.StatusChangeType;

/**
 * 
 */
public class YMSGTester {

    private static final String HOSTNAME = "scs.msg.yahoo.com";
//    private static final String HOSTNAME = "216.155.193.162";
//    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5050;
    
    private static class Listener implements YahooEventListener {

        public void authFailed() {
            System.out.println("AuthFailed");
        }

        public void buddyStatusChanged(StatusChangeType type, YahooBuddy buddy) {
            System.out.println("BuddyStatusChanged: "+type.name()+" "+buddy.toString());
        }

        public void loggedOn() {
            System.out.println("LoggedOn");
        }

        public void receivedBuddyList() {
            System.out.println("ReceivedBuddyList");
        }

        public void receivedMessage(YahooMessage msg) {
            System.out.println("ReceivedMessage: "+msg.toString());
        }
    }
    
    private static class TestAuthProvider implements YMSGAuthProvider {
        public String[] calculateChallengeResponse(String username, String password, String challenge) {
            return new String[] { "testing", "1234" };
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        if (args.length < 2) {
            System.err.println("Requires USERNAME and PASSWORD arguments");
        }
        SocketConnector connector = new SocketConnector();

        // Change the worker timeout to 1 second to make the I/O thread quit soon
        // when there's no connection to manage.
        connector.setWorkerTimeout( 1 );
        
        // Configure the service.
        SocketConnectorConfig cfg = new SocketConnectorConfig();
        cfg.setConnectTimeout(10);
        
        cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new YMSGProtocolCodecFactory()));
        
        cfg.getFilterChain().addLast( "logger", new LoggingFilter() );
        
    
        boolean completed = false;
        IoSession session = null;
        while (!completed) {
            try {
                ConnectFuture future = connector.connect(
                    new InetSocketAddress( HOSTNAME, PORT ),
                    new SessionHandler(new TestAuthProvider(), new Listener(), 
                        args[0], args[1]), cfg );
                
                future.join();
                session = future.getSession();
                completed = true;
            } catch (RuntimeIOException e) {
                System.err.println( "Failed to connect." );
                e.printStackTrace();
            } catch (InterruptedException e) { }
        }
        
        try {
            session.getCloseFuture().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
