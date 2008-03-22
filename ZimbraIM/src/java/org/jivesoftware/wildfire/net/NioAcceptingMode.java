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
package org.jivesoftware.wildfire.net;

import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.ConnectionManager;
import org.jivesoftware.wildfire.ServerPort;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.TransportType;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;

class NioAcceptingMode extends SocketAcceptingMode {

    private static final String HANDLER = NioAcceptingMode.class.getName() + ".h";
    
    class NioIoHandlerAdapter extends IoHandlerAdapter  {
        
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            Log.debug("XMPP Exception caught for session: " + session.toString() + " Caused by: " +cause.toString());
            cause.printStackTrace();
        }
        
        public void messageSent(IoSession session, Object message) throws Exception {
//            if (Log.isDebugEnabled()) { Log.debug("XMPP Message send for session: "+session.toString()); }
        }
        
        public void sessionClosed(IoSession session) throws Exception {
            Log.info("XMPP Session closed: "+session.toString());
            
            NioCompletionHandler handler = (NioCompletionHandler)(session.getAttribute(HANDLER));
            handler.nioClosed();
            super.sessionClosed(session);
        }
        
        public void sessionCreated(IoSession session) throws Exception {
            Log.info("XMPP Session created: " + session.toString());
            
            try {
                if( session.getTransportType() == TransportType.SOCKET )
                {
                    ( ( SocketSessionConfig ) session.getConfig() ).setReceiveBufferSize( 128 );
                    session.setIdleTime( IdleStatus.BOTH_IDLE,  60 * 30); // 30 minute idle                    
                }

                SocketReader reader = connManager.createSocketReader(session, false, serverPort);
                NioCompletionHandler handler = reader.getNioCompletionHandler();
                session.setAttribute(HANDLER, handler);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            super.sessionCreated(session);
        }
        
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            Log.debug("XMPP Session idle: "+session.toString() + " status "+status.toString());
        }

        public void sessionOpened(IoSession session) throws Exception {
            Log.debug("XMPP Session opened: " + session.toString());
            super.sessionOpened(session);
        }

        public void messageReceived( IoSession session, Object buf ) {
            if( !( buf instanceof ByteBuffer ) ) { // check your imports: should be org.apache.mina.common.ByteBuffer, not java.nio!
                return;
            }
            
            NioCompletionHandler handler = (NioCompletionHandler)(session.getAttribute(HANDLER));
            handler.nioReadCompleted((ByteBuffer)buf);
        }

    }
    
    private NioIoHandlerAdapter mIoAdapter = null;
    private IoAcceptor mAcceptor = null;
    
    /**
     * @param connManager
     * @param serverPort
     * @param bindInterface
     * @throws IOException
     */
    NioAcceptingMode(ConnectionManager connManager, ServerPort serverPort) throws IOException {
        super(connManager, serverPort);
        
        mIoAdapter = new NioIoHandlerAdapter();
        
        try {
            InetAddress addr = serverPort.getBindAddress();
            Log.debug("NioAcceptor starting for serverPort "+serverPort.toString()+" with bind address "+(addr != null ? addr.toString() : "0.0.0.0"));
        } catch (IOException e) {
            throw new IOException("Unable to bind to requested listener port: "+serverPort.toString()+" exception="+e.toString());
        }
    }

    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.net.SocketAcceptingMode#shutdown()
     */
    public void shutdown() {
        mAcceptor.unbindAll();
        super.shutdown();
    }


    /* 
     * NIO Accept Mainloop
     */
    public void run() {
        mAcceptor = new SocketAcceptor();
        DefaultIoFilterChainBuilder chain = mAcceptor.getFilterChain();
        System.out.println(chain);

//        InetSocketAddress addr = new InetSocketAddress( serverPort.getPort() );
        try {
            InetSocketAddress addr = new InetSocketAddress(serverPort.getBindAddress(), serverPort.getPort());
            
            try {
                mAcceptor.bind(addr, mIoAdapter);
                
            } catch (IOException ie) {
                if (notTerminated) {
                    Log.error(LocaleUtils.getLocalizedString("admin.error.accept"),
                        ie);
                }
            }
            System.out.println( "Listening on port " + addr );
        } catch (IOException e) {
            Log.error("Unable to start requested listener port: "+serverPort.toString()+" exception="+e.toString());
        }
    }        
}
