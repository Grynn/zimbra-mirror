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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.CharsetEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;

import org.apache.mina.common.IoSession;
import org.apache.mina.filter.SSLFilter;

import org.jivesoftware.wildfire.PacketDeliverer;
import org.jivesoftware.wildfire.Session;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;

public class NioSocketConnection extends SocketConnection {
    
    private CharsetEncoder mCharsetEncoder;
    private IoSession mIoSession;
    
    public NioSocketConnection(PacketDeliverer backupDeliverer, IoSession session, boolean isSecure)
    throws IOException {
        super(backupDeliverer, isSecure);
        
        if (session == null) {
            throw new NullPointerException("Socket channel must be non-null");
        }
        
        mIoSession = session;
        mCharsetEncoder = sCharset.newEncoder();
        
        writer = new BufferedWriter(getNioWriter());
        xmlSerializer = new XMLSocketWriter(writer, this);
    }
    
    public Writer getNioWriter() {
        return new NioWriter(mIoSession, mCharsetEncoder);
    }
    
    public void startTLS(boolean clientMode, String remoteServer) throws IOException {
        // Create/initialize the SSLContext with key material
        try {
            
            SSLContext tlsContext = null;
            
            if (true) { // leave X509 for now at bottom for testing
                // First initialize the key and trust material.
                KeyStore ksKeys = SSLConfig.getKeyStore();
                String keypass = SSLConfig.getKeyPassword();
                
                KeyStore ksTrust = SSLConfig.getTrustStore();
                String trustpass = SSLConfig.getTrustPassword();
                
                // KeyManager's decide which key material to use.
                KeyManager[] km = SSLJiveKeyManagerFactory.getKeyManagers(ksKeys, keypass);
                
                // TrustManager's decide whether to allow connections.
                TrustManager[] tm = SSLJiveTrustManagerFactory.getTrustManagers(ksTrust, trustpass);
                if (clientMode ) {
                    // Check if we can trust certificates presented by the server
                    tm = new TrustManager[]{new ServerTrustManager(remoteServer, ksTrust)};
                }
                tlsContext = SSLContext.getInstance("TLS");
                tlsContext.init(km, tm, null);
            } else {
                // First initialize the key and trust material.
                KeyStore ksKeys = SSLConfig.getKeyStore();
                char[] keypass = SSLConfig.getKeyPassword().toCharArray();
                
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                kmf.init(ksKeys, keypass);
                
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                tmf.init(ksKeys);
                
                tlsContext = SSLContext.getInstance("TLS");
                tlsContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            }
            
            SSLFilter filter = new SSLFilter(tlsContext);
            filter.setUseClientMode(clientMode);
            
            mIoSession.getFilterChain().addAfter("org.apache.mina.common.ExecutorThreadModel", "tls", filter);
            
            mIoSession.setAttribute(SSLFilter.DISABLE_ENCRYPTION_ONCE, Boolean.TRUE);
            
            if (!clientMode) {
                deliverRawText("<proceed xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/>");
            }
        } catch (KeyStoreException e) {
            Log.error("TLSHandler startup problem.\n" + "  SSLContext initialization failed.", e);
        } catch (UnrecoverableKeyException e) {
            Log.error("TLSHandler startup problem.\n" + "  SSLContext initialization failed.", e);
        } catch (KeyManagementException e) {
            Log.error("TLSHandler startup problem.\n" + "  SSLContext initialization failed.", e);
        } catch (NoSuchAlgorithmException e) {
            Log.error("TLSHandler startup problem.\n" + "  The TLS protocol does not exist", e);
        } catch (IOException e) {
            Log.error("TLSHandler startup problem.\n"
                    + "  the KeyStore or TrustStore does not exist", e);
        }
    }
    
    public void startCompression() throws IOException {
    }

    protected void closeConnection() {
        try {
            release();
            mIoSession.close();
        } catch (Exception e) {
            Log.error(LocaleUtils.getLocalizedString("admin.error.close")
                    + "\n" + this.toString(), e);
        }
    }
    
    public InetAddress getInetAddress() {
        return ((InetSocketAddress)mIoSession.getRemoteAddress()).getAddress(); 
    }

    /**
     * Returns the port that the connection uses.
     *
     * @return the port that the connection uses.
     */
    public int getPort() {
        return ((InetSocketAddress)mIoSession.getRemoteAddress()).getPort();        
    }

    
    public boolean isClosed() {
        if (session == null) {
            return mIoSession.isClosing();
        }
        return session.getStatus() == Session.STATUS_CLOSED;
    }
    
    
}
