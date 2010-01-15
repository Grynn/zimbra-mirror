/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
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

import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSession;

import org.jivesoftware.wildfire.PacketDeliverer;
import org.jivesoftware.wildfire.Session;
import org.jivesoftware.wildfire.server.IncomingServerSession;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;

public class StdSocketConnection extends SocketConnection {
    private TLSStreamHandler tlsStreamHandler;
    private Socket mSocket; 

    public StdSocketConnection(PacketDeliverer backupDeliverer, Socket socket, boolean isSecure)
    throws IOException {
        super(backupDeliverer, isSecure);
        
        if (socket == null) {
            throw new NullPointerException("Socket channel must be non-null");
        }

        mSocket = socket;
        
        writer = new BufferedWriter(new OutputStreamWriter(
                    ServerTrafficCounter.wrapOutputStream(socket.getOutputStream()), CHARSET));
        
        xmlSerializer = new XMLSocketWriter(writer, this);
    }
    
    public InetAddress getInetAddress() {
        return mSocket.getInetAddress();
    }

    /**
     * Returns the port that the connection uses.
     *
     * @return the port that the connection uses.
     */
    public int getPort() {
        return mSocket.getPort();
    }

    /**
     * Returns the stream handler responsible for securing the plain connection and providing
     * the corresponding input and output streams.
     *
     * @return the stream handler responsible for securing the plain connection and providing
     *         the corresponding input and output streams.
     */
    public TLSStreamHandler getTLSStreamHandler() {
        return tlsStreamHandler;
    }

    /**
     * Secures the plain connection by negotiating TLS with the client. When connecting
     * to a remote server then <tt>clientMode</tt> will be <code>true</code> and
     * <tt>remoteServer</tt> is the server name of the remote server. Otherwise <tt>clientMode</tt>
     * will be <code>false</code> and  <tt>remoteServer</tt> null.
     *
     * @param clientMode boolean indicating if this entity is a client or a server.
     * @param remoteServer server name of the remote server we are connecting to or <tt>null</tt>
     *        when not in client mode.
     * @throws IOException if an error occured while securing the connection.
     */
    public void startTLS(boolean clientMode, String remoteServer) throws IOException {
        if (!secure) {
            secure = true;
            // Prepare for TLS
            tlsStreamHandler = new TLSStreamHandler(mSocket, clientMode, remoteServer, 
                        session instanceof IncomingServerSession);
            if (!clientMode) {
                // Indicate the client that the server is ready to negotiate TLS
                deliverRawText("<proceed xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/>");
            }
            // Start handshake
            tlsStreamHandler.start();
            // Use new wrapped writers
            writer = new BufferedWriter(new OutputStreamWriter(tlsStreamHandler.getOutputStream(), CHARSET));
            xmlSerializer = new XMLSocketWriter(writer, this);
        }
    }

    /**
     * Start using compression for this connection. Compression will only be available after TLS
     * has been negotiated. This means that a connection can never be using compression before
     * TLS. However, it is possible to use compression without TLS.
     *
     * @throws IOException if an error occured while starting compression.
     */
    public void startCompression() throws IOException {
        compressed = true;

        if (tlsStreamHandler == null) {
            ZOutputStream out = new ZOutputStream(
                    ServerTrafficCounter.wrapOutputStream(mSocket.getOutputStream()),
                    JZlib.Z_BEST_COMPRESSION);
            out.setFlushMode(JZlib.Z_PARTIAL_FLUSH);
            writer = new BufferedWriter(new OutputStreamWriter(out, CHARSET));
            xmlSerializer = new XMLSocketWriter(writer, this);
        }
        else {
            ZOutputStream out = new ZOutputStream(tlsStreamHandler.getOutputStream(), JZlib.Z_BEST_COMPRESSION);
            out.setFlushMode(JZlib.Z_PARTIAL_FLUSH);
            writer = new BufferedWriter(new OutputStreamWriter(out, CHARSET));
            xmlSerializer = new XMLSocketWriter(writer, this);
        }
    }
    
    public SSLSession getSSLSession() {
        if (tlsStreamHandler != null) {
            return tlsStreamHandler.getSSLSession();
        }
        return null;
    }
    
    public boolean isClosed() {
        if (session == null) {
            return mSocket.isClosed();
        }
        return session.getStatus() == Session.STATUS_CLOSED;
    }
    
    protected void closeConnection() {
        release();
        try {
            if (tlsStreamHandler == null) {
                mSocket.close();
            }
            else {
                // Close the channels since we are using TLS (i.e. NIO). If the channels implement
                // the InterruptibleChannel interface then any other thread that was blocked in
                // an I/O operation will be interrupted and an exception thrown
                tlsStreamHandler.close();
            }
        }
        catch (Exception e) {
            Log.error(LocaleUtils.getLocalizedString("admin.error.close")
                    + "\n" + this.toString(), e);
        }
    }

    public String toString() {
        return super.toString() + " socket: " + mSocket + " session: " + session;
    }
    
}
