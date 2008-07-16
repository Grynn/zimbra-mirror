/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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

import org.jivesoftware.util.IMConfig;
import org.jivesoftware.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Configuration of Wildfire's SSL settings.
 *
 * @author Iain Shigeoka
 */
public class SSLConfig {

    private static SSLJiveServerSocketFactory sslServerSocketFactory;
    private static SSLSocketFactory sslSocketFactory;
    private static KeyStore keyStore;
    private static String keypass;
    private static KeyStore trustStore;
    private static String trustpass;
    private static String keyStoreLocation;
    private static String trustStoreLocation;

    private SSLConfig() {
    }
    
    // Create a trust manager that does not validate certificate chains
    static TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
            }
        }
    };    

    static {
        String algorithm = IMConfig.XMPP_SOCKET_SSL_ALGORITHM.getString();
        String storeType = IMConfig.XMPP_SOCKET_SSL_STORETYPE.getString();

        // Get the keystore location. The default location is security/keystore
        keyStoreLocation = IMConfig.XMPP_SOCKET_SSL_KEYSTORE.getString();

        // Get the keystore password. The default password is "changeit".
        keypass = IMConfig.XMPP_SOCKET_SSL_KEYPASS.getString();
        keypass = keypass.trim();

        // Get the truststore location; default at security/truststore
        trustStoreLocation = IMConfig.XMPP_SOCKET_SSL_TRUSTSTORE.getString();

        // Get the truststore passwprd; default is "changeit".
        trustpass = IMConfig.XMPP_SOCKET_SSL_TRUSTPASS.getString();
        trustpass = trustpass.trim();

        try {
            keyStore = KeyStore.getInstance(storeType);
            keyStore.load(new FileInputStream(keyStoreLocation), keypass.toCharArray());

            trustStore = KeyStore.getInstance(storeType);
            trustStore.load(new FileInputStream(trustStoreLocation), trustpass.toCharArray());

            {
                SSLContext sslcontext = SSLContext.getInstance(algorithm);
                KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyFactory.init(keyStore, SSLConfig.getKeyPassword().toCharArray());
                TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustFactory.init(trustStore);
                sslcontext.init(keyFactory.getKeyManagers(),
                                trustFactory.getTrustManagers(),
                                new java.security.SecureRandom());
                
                sslServerSocketFactory = (SSLJiveServerSocketFactory)SSLJiveServerSocketFactory.getInstance(algorithm, keyStore, trustStore);
            }
            {
                SSLContext sslcontext = SSLContext.getInstance(algorithm);
                KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyFactory.init(keyStore, SSLConfig.getKeyPassword().toCharArray());
                TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustFactory.init(trustStore);
                if (IMConfig.XMPP_SOCKET_SSL_ALLOW_UNTRUSTED_CERTS.getBoolean()) {
                    sslcontext.init(keyFactory.getKeyManagers(),
                                    trustAllCerts,
                                    new java.security.SecureRandom());
                } else {
                    sslcontext.init(keyFactory.getKeyManagers(),
                                    trustFactory.getTrustManagers(),
                                    new java.security.SecureRandom());
                }
                
                sslSocketFactory = sslcontext.getSocketFactory();
            }
        }
        catch (Exception e) {
            Log.error("SSLConfig startup problem.\n" +
                    "  storeType: [" + storeType + "]\n" +
                    "  keyStoreLocation: [" + keyStoreLocation + "]\n" +
                    "  keypass: [" + keypass + "]\n" +
                    "  trustStoreLocation: [" + trustStoreLocation+ "]\n" +
                    "  trustpass: [" + trustpass + "]", e);
            keyStore = null;
            trustStore = null;
            sslServerSocketFactory = null;
        }
    }

    public static String getKeyPassword() {
        return keypass;
    }

    public static String getTrustPassword() {
        return trustpass;
    }

    public static String[] getDefaultCipherSuites() {
        String[] suites;
        if (sslServerSocketFactory == null) {
            suites = new String[]{};
        }
        else {
            suites = sslServerSocketFactory.getDefaultCipherSuites();
        }
        return suites;
    }

    public static String[] getSpportedCipherSuites() {
        String[] suites;
        if (sslServerSocketFactory == null) {
            suites = new String[]{};
        }
        else {
            suites = sslServerSocketFactory.getSupportedCipherSuites();
        }
        return suites;
    }

    public static KeyStore getKeyStore() throws IOException {
        if (keyStore == null) {
            throw new IOException();
        }
        return keyStore;
    }

    public static KeyStore getTrustStore() throws IOException {
        if (trustStore == null) {
            throw new IOException();
        }
        return trustStore;
    }

    public static void saveStores() throws IOException {
        try {
            keyStore.store(new FileOutputStream(keyStoreLocation), keypass.toCharArray());
            trustStore.store(new FileOutputStream(trustStoreLocation), trustpass.toCharArray());
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public static ServerSocket createServerSocket(int port, InetAddress ifAddress) throws
            IOException {
        if (sslServerSocketFactory == null) {
            throw new IOException();
        }
        else {
            return sslServerSocketFactory.createServerSocket(port, -1, ifAddress);
        }
    }
    
    public static Socket createSSLSocket(int port, InetAddress ifaddress) throws IOException {
        if (sslSocketFactory == null) {
            throw new IOException("sslSocketFactory is null");
        }
        return sslSocketFactory.createSocket(ifaddress, port);
    }
    
    public static Socket createSSLSocket() throws IOException {
        if (sslSocketFactory == null) {
            throw new IOException("sslSocketFactory is null");
        }
        return sslSocketFactory.createSocket();
    }
    
}