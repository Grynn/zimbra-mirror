/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package org.jivesoftware.wildfire.net;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.ConnectionManager;
import org.jivesoftware.wildfire.ServerPort;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

class NioAcceptingMode extends SocketAcceptingMode {

    private static final String HANDLER = NioAcceptingMode.class.getName() + ".h";

    class NioIoHandlerAdapter extends IoHandlerAdapter {

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            Log.debug("XMPP Exception caught for session: " + session.toString() + " Caused by: " +cause.toString());
            cause.printStackTrace();
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
//            if (Log.isDebugEnabled()) { Log.debug("XMPP Message send for session: "+session.toString()); }
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            Log.info("XMPP Session closed: "+session.toString());

            NioCompletionHandler handler = (NioCompletionHandler)(session.getAttribute(HANDLER));
            handler.nioClosed();
            super.sessionClosed(session);
        }

        @Override
        public void sessionCreated(IoSession session) throws Exception {
            Log.info("XMPP Session created: " + session.toString());

            try {
                if (session.getConfig() instanceof SocketSessionConfig) {
                    ((SocketSessionConfig) session.getConfig()).setReceiveBufferSize(128);
                    session.getConfig().setBothIdleTime(60 * 30); // 30 minute idle
                }

                SocketReader reader = connManager.createSocketReader(session, false, serverPort);
                NioCompletionHandler handler = reader.getNioCompletionHandler();
                session.setAttribute(HANDLER, handler);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            super.sessionCreated(session);
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            Log.debug("XMPP Session idle: "+session.toString() + " status "+status.toString());
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            Log.debug("XMPP Session opened: " + session.toString());
            super.sessionOpened(session);
        }

        @Override
        public void messageReceived(IoSession session, Object buf) {
            if(!(buf instanceof IoBuffer)) {
                return;
            }

            NioCompletionHandler handler = (NioCompletionHandler) session.getAttribute(HANDLER);
            handler.nioReadCompleted((IoBuffer) buf);
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

    @Override
    public void shutdown() {
        mAcceptor.unbind();
        super.shutdown();
    }


    /*
     * NIO Accept Mainloop
     */
    @Override
    public void run() {
        mAcceptor = new NioSocketAcceptor();
        mAcceptor.setHandler(mIoAdapter);
        DefaultIoFilterChainBuilder chain = mAcceptor.getFilterChain();
        Log.debug("NioAcceptingMode.run: "+chain);

        try {
            InetSocketAddress addr = new InetSocketAddress(serverPort.getBindAddress(), serverPort.getPort());

            try {
                mAcceptor.bind(addr);
            } catch (IOException ie) {
                if (notTerminated) {
                    Log.error(LocaleUtils.getLocalizedString("admin.error.accept"),
                        ie);
                }
            }
            Log.debug("NioAcceptingMode Listening on port " + addr );
        } catch (IOException e) {
            Log.error("Unable to start requested listener port: "+serverPort.toString()+" exception="+e.toString());
        }
    }
}
