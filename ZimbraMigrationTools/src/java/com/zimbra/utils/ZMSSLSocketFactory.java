/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
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
package com.zimbra.utils;

 import com.sun.net.ssl.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import com.sun.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;


public class ZMSSLSocketFactory extends SSLSocketFactory
{
    private SSLSocketFactory factory;
    public ZMSSLSocketFactory()
    {
        System.out.println( "ZMSSLSocketFactory instantiated");
        try 
        {
            SSLContext sslcontext = SSLContext.getInstance( "SSL");
            sslcontext.init( null, // No KeyManager required
                new TrustManager[] { new ZMTrustManager()},
                new java.security.SecureRandom());
            factory = ( SSLSocketFactory) sslcontext.getSocketFactory();
        } 
        catch( Exception ex) 
        {
            ex.printStackTrace();
        }
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
            new javax.net.ssl.HostnameVerifier() 
            {
                public boolean verify(String urlHostname, String certHostname) { return true; }

                public boolean verify(String urlHostname, javax.net.ssl.SSLSession session) { return true; }
            }
        ); 
    }
    
    public static SocketFactory getDefault() 
    {
        return new ZMSSLSocketFactory();
    }
    
    public Socket createSocket( Socket socket, String s, int i, boolean 
                                flag) throws IOException 
    {
        return factory.createSocket( socket, s, i, flag);
    }
    
    public Socket createSocket( InetAddress inaddr, int i,
    InetAddress inaddr1, int j) throws IOException 
    {
      return factory.createSocket( inaddr, i, inaddr1, j);
    }
    public Socket createSocket( InetAddress inaddr, int i) throws 
                                        IOException 
    {
        return factory.createSocket( inaddr, i);
    }
    
    public Socket createSocket( String s, int i, InetAddress inaddr, int j)
        throws IOException 
    {
        return factory.createSocket( s, i, inaddr, j);
    }
    
    public Socket createSocket( String s, int i) throws IOException 
    {
        return factory.createSocket( s, i);
    }
    
    public String[] getDefaultCipherSuites() 
    {
        return factory.getSupportedCipherSuites();
    }
    
    public String[] getSupportedCipherSuites() 
    {
        return factory.getSupportedCipherSuites();
    }
}

class ZMTrustManager implements X509TrustManager
{
    
    public boolean isClientTrusted( X509Certificate[] cert) 
    {
        return true;
    }
    public boolean isServerTrusted( X509Certificate[] cert) 
    {
        return true;
    }
    public X509Certificate[] getAcceptedIssuers() 
    {
        return new X509Certificate[ 0];
    }
}

