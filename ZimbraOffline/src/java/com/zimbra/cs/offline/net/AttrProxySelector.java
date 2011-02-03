/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.offline.net;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.zimbra.common.net.AuthProxy;
import com.zimbra.common.net.ProxyAuthenticator;
import com.zimbra.common.net.UsernamePassword;
import com.zimbra.common.net.ProxySelectors.CustomProxySelector;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.OfflineLog;

/**
 * ProxySelector implementation which reads configuration from offline attributes
 */
public class AttrProxySelector extends CustomProxySelector {

    public AttrProxySelector() {
        super(null);
    }

    protected enum Mode {MANUAL, NONE, SYSTEM};
    
    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        ZimbraLog.net.error("Unable to connect to proxy %s",uri,ioe);
    }
    
    private void addProxy(Proxy.Type type, String proxyHost, int proxyPort, String proxyUsername, String proxyPassword, List<Proxy> proxies, ProxyAuthenticator proxyAuth) {
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
            AuthProxy proxy = new AuthProxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            if (proxyUsername != null && proxyUsername.length() > 0 && proxyPassword != null && proxyPassword.length() > 0) {
                proxy.setUsername(proxyUsername); 
                proxy.setPassword(proxyPassword); 
            }
            proxies.add(proxy);
            proxyAuth.addCredentials(Proxy.Type.HTTP, new UsernamePassword(proxyUsername,proxyPassword));
        }
    }

    @Override
    public List<Proxy> select(URI uri) {
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        List<Proxy> proxies = new ArrayList<Proxy>();
        try {
            String mode = prov.getLocalAccount().getAttr(OfflineProvisioning.A_zimbraPrefOfflineAttrProxyMode);
            if (Mode.MANUAL.toString().equals(mode)) {
                if (uri.getHost().indexOf("localhost") < 0 && uri.getHost().indexOf("127.0.0.1") < 0) {
                    Account localAcct = prov.getLocalAccount();
                    ProxyAuthenticator proxyAuth = new ProxyAuthenticator();
                    //HTTP
                    if (uri.getScheme().indexOf("http") == 0) {
                        String proxyHost = localAcct.getAttr(OfflineProvisioning.A_zimbraPrefOfflineHttpProxyHost);
                        int proxyPort = localAcct.getIntAttr(OfflineProvisioning.A_zimbraPrefOfflineHttpProxyPort, -1);
                        String proxyUsername = localAcct.getAttr(OfflineProvisioning.A_zimbraPrefOfflineHttpProxyUsername);
                        String proxyPassword = localAcct.getAttr(OfflineProvisioning.A_zimbraPrefOfflineHttpProxyPassword);
                        addProxy(Proxy.Type.HTTP, proxyHost, proxyPort, proxyUsername, proxyPassword, proxies, proxyAuth);
                    }
                    //SOCKS
                    String proxyHost = localAcct.getAttr(OfflineProvisioning.A_zimbraPrefOfflineSocksProxyHost);
                    int proxyPort = localAcct.getIntAttr(OfflineProvisioning.A_zimbraPrefOfflineSocksProxyPort, -1);
                    String proxyUsername = localAcct.getAttr(OfflineProvisioning.A_zimbraPrefOfflineSocksProxyUsername);
                    String proxyPassword = localAcct.getAttr(OfflineProvisioning.A_zimbraPrefOfflineSocksProxyPassword);
                    addProxy(Proxy.Type.SOCKS, proxyHost, proxyPort, proxyUsername, proxyPassword, proxies, proxyAuth);
                    //configure authentication for 3rd party libraries (like gdata) that use HttpUrlConnection
                    Authenticator.setDefault(proxyAuth);
                }
            } else if (mode == null || Mode.SYSTEM.toString().equals(mode)) {
                //if not set yet use old default behavior; system proxy settings
                return ps.select(uri);
            }
        } catch (ServiceException e) {
            OfflineLog.offline.error("ServiceException configuring manual proxy; assuming disabled",e);
        }
        if (proxies.isEmpty()) {
            proxies.add(Proxy.NO_PROXY);
        }
        return proxies;
    }
}
