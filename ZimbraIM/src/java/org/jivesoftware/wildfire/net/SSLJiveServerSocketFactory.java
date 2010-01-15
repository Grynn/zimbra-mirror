/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

import org.jivesoftware.util.Log;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Securue socket factory wrapper allowing simple setup of all security
 * SSL related parameters.
 *
 * @author Iain Shigeoka
 */
public class SSLJiveServerSocketFactory extends SSLServerSocketFactory {
    

    public static SSLServerSocketFactory getInstance(String algorithm,
                                                     KeyStore keystore,
                                                     KeyStore truststore,
                                                     String[] excludedCipherSuites) 
    throws IOException {
        try {
            SSLContext sslcontext = SSLContext.getInstance(algorithm);
            SSLServerSocketFactory factory;
            KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyFactory.init(keystore, SSLConfig.getKeyPassword().toCharArray());
            TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustFactory.init(truststore);

            sslcontext.init(keyFactory.getKeyManagers(),
                            trustFactory.getTrustManagers(),
                    new java.security.SecureRandom());
            factory = sslcontext.getServerSocketFactory();
            
            // compute the enabled cypher suites from the list of disabled ones
            String[] enabledCipherSuites = null;
            {
                if (excludedCipherSuites != null && excludedCipherSuites.length > 0) {
                    List<String> excludedCiphers = Arrays.asList(excludedCipherSuites);
                    String[] defaultCipherSuites = factory.getDefaultCipherSuites();
                    List<String> enabledCiphers = new ArrayList<String>(Arrays.asList(defaultCipherSuites));
                    
                    for (String cipher : excludedCiphers) {
                        if (enabledCiphers.contains(cipher)) {
                            enabledCiphers.remove(cipher);
                        }
                    }
                    if (enabledCiphers.size() == 0)
                        throw new IOException("no enabled cipher suites after excluding cipher suites " + excludedCipherSuites);
                    enabledCipherSuites = enabledCiphers.toArray(new String[enabledCiphers.size()]);
                }
            }
            
            return new SSLJiveServerSocketFactory(factory, enabledCipherSuites);
        }
        catch (Exception e) {
            Log.error(e);
            throw new IOException(e.getMessage());
        }
    }
    
    private SSLServerSocketFactory sFactory;
    private String[] mEnabledCipherSuites;

    private SSLJiveServerSocketFactory(SSLServerSocketFactory factory, String[] enabledCipherSuites) {
        this.sFactory = factory;
        mEnabledCipherSuites = enabledCipherSuites;
    }

    public ServerSocket createServerSocket(int i) throws IOException {
        return initSocket(sFactory.createServerSocket(i));
    }
    
    private ServerSocket initSocket(ServerSocket ss) throws IOException {
        SSLServerSocket sslServerSocket = (SSLServerSocket)ss;
        if (mEnabledCipherSuites != null)
            sslServerSocket.setEnabledCipherSuites(mEnabledCipherSuites);
        return ss;
    }

    public ServerSocket createServerSocket(int i, int i1) throws IOException {
        return initSocket(sFactory.createServerSocket(i, i1));
    }

    public ServerSocket createServerSocket(int i, int i1, InetAddress inetAddress) throws IOException {
        return initSocket(sFactory.createServerSocket(i, i1, inetAddress));
    }

    public String[] getDefaultCipherSuites() {
        if (mEnabledCipherSuites == null)
        return sFactory.getDefaultCipherSuites();
    else
        return mEnabledCipherSuites;
    }

    public String[] getSupportedCipherSuites() {
        return sFactory.getSupportedCipherSuites();
    }
}
