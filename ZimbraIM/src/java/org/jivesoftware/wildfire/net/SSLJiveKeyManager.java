/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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

import com.sun.net.ssl.X509KeyManager;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * A skeleton placeholder for developers wishing to implement their own custom
 * key manager. In future revisions we may expand the skeleton code if customers
 * request assistance in creating custom key managers.
 * <p/>
 * The key manager is an essential part of server SSL support. Typically you
 * will implement a custom key manager to retrieve certificates from repositories
 * that are not of standard Java types (e.g. obtaining them from LDAP or a JDBC database).
 *
 * @author Iain Shigeoka
 */
public class SSLJiveKeyManager implements X509KeyManager {
    public String[] getClientAliases(String s, Principal[] principals) {
        return new String[0];
    }

    public String chooseClientAlias(String s, Principal[] principals) {
        return null;
    }

    public String chooseClientAlias(String[] strings, Principal[] principals, Socket socket) {
        return null;
    }

    public String[] getServerAliases(String s, Principal[] principals) {
        return new String[0];
    }

    public String chooseServerAlias(String s, Principal[] principals) {
        return null;
    }

    public String chooseServerAlias(String s, Principal[] principals, Socket socket) {
        return null;
    }

    public X509Certificate[] getCertificateChain(String s) {
        return new X509Certificate[0];
    }

    public PrivateKey getPrivateKey(String s) {
        return null;
    }
}
