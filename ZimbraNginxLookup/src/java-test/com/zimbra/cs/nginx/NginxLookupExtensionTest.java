/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
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

package com.zimbra.cs.nginx;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.Provisioning.DomainBy;

/*
 * Note: restart server after each run, the lookup servlet caches things
 *       TODO: send a flush cache comand to the lookup servlet
 */
public class NginxLookupExtensionTest {
    
    private static final String USER = "user1";
    private static final String DEFAULT_DOMAIN = "phoebe.mbp";// TODO, REremove hardcode
    
    private static final String QUSER = USER + "@" +DEFAULT_DOMAIN;
    
    private static final String PASSWORD = "test123";
    
    private static final String LOCALHOST = "localhost";
    private static final String LOCALHOST_IP = "127.0.0.1";
    
    private static final String POP3_PORT     = "7110";
    private static final String POP3_SSL_PORT = "7995";
    private static final String IMAP_PORT     = "7143";
    private static final String IMAP_SSL_PORT = "7993";
    private static final String HTTP_PORT     = "7070";
    
    private enum AuthMethod {
        plain,
        other,
        zimbraId,
        gssapi
    }
    
    private enum AuthProtocol {
        pop3,
        pop3ssl,
        imap,
        imapssl,
        http
    }
    
    private static class LookupData {
        // required 
        AuthMethod mAuthMethod;
        String mAuthUser;
        String mAuthPass;
        AuthProtocol mAuthProtocol;
        
        LookupData(AuthMethod authMethod, String authUser, String authPass, AuthProtocol authProtocol) {
            setAuthMethod(authMethod);
            setAuthUser(authUser);
            setAuthPass(authPass);
            setAuthProtocol(authProtocol);
        }
        
        void setAuthMethod(AuthMethod authMethod) {
            mAuthMethod = authMethod;
        }
        
        void setAuthUser(String authUser) {
            mAuthUser = authUser;
        }
        
        void setAuthPass(String authPass) {
            mAuthPass = authPass;
        }
        
        void setAuthProtocol(AuthProtocol authProtocol) {
            mAuthProtocol = authProtocol;
        }
        
        /*
        String h_AUTH_LOGIN_ATTEMPT,
        String h_CLIENT_IP,
        String h_SERVER_IP,
        String h_SERVER_HOST,
        String h_AUTH_ID,
        String h_AUTH_ADMIN_USER,
        String h_AUTH_ADMIN_PASS,
        */
        
        void setRequestHeader(GetMethod method) {
            if (mAuthMethod != null)
                method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_METHOD, mAuthMethod.name());
            if (mAuthUser != null) 
                method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_USER, mAuthUser);
            if (mAuthPass != null) 
                method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_PASS, mAuthPass);
            if (mAuthProtocol != null) 
                method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_PROTOCOL, mAuthProtocol.name());
            /*
            if (h_AUTH_LOGIN_ATTEMPT != null) 
                method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_LOGIN_ATTEMPT, h_AUTH_LOGIN_ATTEMPT);
            if (h_CLIENT_IP != null) 
                method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.CLIENT_IP, h_CLIENT_IP);
            if (h_SERVER_IP != null) 
                method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.SERVER_IP, h_SERVER_IP);
            if (h_SERVER_HOST != null) 
                method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.SERVER_HOST, h_SERVER_HOST);
            if (h_AUTH_ID != null) 
                method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_ID, h_AUTH_ID);
            if (h_AUTH_ADMIN_USER != null) 
                method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_ADMIN_USER, h_AUTH_ADMIN_USER);
            if (h_AUTH_ADMIN_PASS != null) 
                method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_ADMIN_PASS, h_AUTH_ADMIN_PASS);
            */
        }
    }
    
    private static class RespHeaders {
        Map<String, String> mHeaders = new HashMap<String, String>();
        
        void add(Header header) {
            mHeaders.put(header.getName(), header.getValue());
        }
        
        void dump() {
            for (Map.Entry<String, String> header : mHeaders.entrySet())
                System.out.println(header.getKey() + ": " + header.getValue());
        }
        
        String authStatus() {
            return mHeaders.get(NginxLookupExtension.NginxLookupHandler.AUTH_STATUS);
        }
        
        String authUser() {
            return mHeaders.get(NginxLookupExtension.NginxLookupHandler.AUTH_USER);
        }
        
        String authServer() {
            return mHeaders.get(NginxLookupExtension.NginxLookupHandler.AUTH_SERVER);
        }

        String authPort() {
            return mHeaders.get(NginxLookupExtension.NginxLookupHandler.AUTH_PORT);
        }

        
        void assertAuthStatusOK() {
            Assert.assertEquals("OK", authStatus());
        }
                
        void assertAuthUser(String expected) {
            Assert.assertEquals(expected, authUser());
        }
        
        void assertAuthServer(String expected) {
            Assert.assertEquals(expected, authServer());
        }

        void assertAuthPort(String expected) {
            Assert.assertEquals(expected, authPort());
        }
        
        void assertBasic(String expectedUser, String expectedServer, String expectedPort) {
            assertAuthStatusOK();
            assertAuthUser(expectedUser);
            assertAuthServer(expectedServer);
            assertAuthPort(expectedPort);
        }
    }
    
    private RespHeaders senRequest(LookupData lookupData) throws IOException {
        String url = "http://localhost:7072/service/extension/nginx-lookup";
        
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        
        method.setRequestHeader("Host", "localhost");
        lookupData.setRequestHeader(method);
        
        RespHeaders respHdrs = new RespHeaders();
        try {
            int statusCode = client.executeMethod(method);
            
            for (Header header : method.getResponseHeaders()) 
                respHdrs.add(header);
        
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        
        return respHdrs; 
    }
    
    private Provisioning getProv() {
        return Provisioning.getInstance();
    }
    
    private Account getAccount(String name) throws ServiceException {
        return getProv().get(AccountBy.name, name);
    }
    
    private Account createAccount(String localpart, String domainName) throws ServiceException {
        Domain domain = getDomain(domainName);
        if (domain == null)
            createDomain(domainName);
        return getProv().createAccount(localpart+"@"+domainName, PASSWORD, new HashMap<String, Object>());
    }

    private void deleteAccount(Account acct) throws ServiceException {
        getProv().deleteAccount(acct.getId());
    }
    
    private Domain getDomain() throws ServiceException {
        return getProv().get(DomainBy.name, DEFAULT_DOMAIN);
    }
    
    private Domain getDomain(String name) throws ServiceException {
        return getProv().get(DomainBy.name, name);
    }
    
    private Domain createDomain(String domainName) throws ServiceException {
        return getProv().createDomain(domainName, new HashMap<String, Object>());
    }
    
    private void deleteDomain(Domain domain) throws ServiceException {
        getProv().deleteDomain(domain.getId());
    }
    
    private void setupExternalRoute(Account acct, Boolean useExternalRoute,
            String pop3Port, String pop3sslPort, String imapPort, String imapsslPort) throws Exception {
        Map<String,Object> attrs = new HashMap<String,Object>();
        if (useExternalRoute != null)
            acct.setReverseProxyUseExternalRoute(useExternalRoute, attrs);
        acct.setExternalPop3PortAsString(pop3Port, attrs);
        acct.setExternalPop3SSLPortAsString(pop3sslPort, attrs);
        acct.setExternalImapPortAsString(imapPort, attrs);
        acct.setExternalImapSSLPortAsString(imapsslPort, attrs);
        acct.setExternalPop3Hostname(LOCALHOST, attrs);
        acct.setExternalPop3SSLHostname(LOCALHOST, attrs);
        acct.setExternalImapHostname(LOCALHOST, attrs);
        acct.setExternalImapSSLHostname(LOCALHOST, attrs);
        getProv().modifyAttrs(acct, attrs);
    }
    
    private void unsetExternalRoute(Account acct) throws Exception {
        Map<String,Object> attrs = new HashMap<String,Object>();
        acct.unsetReverseProxyUseExternalRoute(attrs);
        acct.unsetExternalPop3Port(attrs);
        acct.unsetExternalPop3SSLPort(attrs);
        acct.unsetExternalImapPort(attrs);
        acct.unsetExternalImapSSLPort(attrs);
        acct.unsetExternalPop3Hostname(attrs);
        acct.unsetExternalPop3SSLHostname(attrs);
        acct.unsetExternalImapHostname(attrs);
        acct.unsetExternalImapSSLHostname(attrs);
        getProv().modifyAttrs(acct, attrs);
    }
    
    private void setupExternalRoute(Domain domain, Boolean useExternalRoute, Boolean useExternalRouteIfAccountNotExist,
            String pop3Port, String pop3sslPort, String imapPort, String imapsslPort) throws Exception {
        Map<String,Object> attrs = new HashMap<String,Object>();
        if (useExternalRoute != null)
            domain.setReverseProxyUseExternalRoute(useExternalRoute, attrs);
        if (useExternalRouteIfAccountNotExist != null)
            domain.setReverseProxyUseExternalRouteIfAccountNotExist(useExternalRouteIfAccountNotExist, attrs);
        domain.setExternalPop3PortAsString(pop3Port, attrs);
        domain.setExternalPop3SSLPortAsString(pop3sslPort, attrs);
        domain.setExternalImapPortAsString(imapPort, attrs);
        domain.setExternalImapSSLPortAsString(imapsslPort, attrs);
        domain.setExternalPop3Hostname(LOCALHOST, attrs);
        domain.setExternalPop3SSLHostname(LOCALHOST, attrs);
        domain.setExternalImapHostname(LOCALHOST, attrs);
        domain.setExternalImapSSLHostname(LOCALHOST, attrs);
        getProv().modifyAttrs(domain, attrs);
    }
    
    private void unsetExternalRoute(Domain domain) throws Exception {
        Map<String,Object> attrs = new HashMap<String,Object>();
        domain.unsetReverseProxyUseExternalRoute(attrs);
        domain.unsetExternalPop3Port(attrs);
        domain.unsetExternalPop3SSLPort(attrs);
        domain.unsetExternalImapPort(attrs);
        domain.unsetExternalImapSSLPort(attrs);
        domain.unsetExternalPop3Hostname(attrs);
        domain.unsetExternalPop3SSLHostname(attrs);
        domain.unsetExternalImapHostname(attrs);
        domain.unsetExternalImapSSLHostname(attrs);
        getProv().modifyAttrs(domain, attrs);
    }
    
    @Test
    public void imap() throws Exception {
        LookupData lookupData = new LookupData(AuthMethod.plain, USER, PASSWORD, AuthProtocol.imap);
        RespHeaders respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(QUSER, LOCALHOST_IP, IMAP_PORT);
    }

    @Test
    public void imapssl() throws Exception {
        LookupData lookupData = new LookupData(AuthMethod.plain, USER, PASSWORD, AuthProtocol.imapssl);
        RespHeaders respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(QUSER, LOCALHOST_IP, IMAP_SSL_PORT);
    }
    
    @Test
    public void pop3() throws Exception {
        LookupData lookupData = new LookupData(AuthMethod.plain, USER, PASSWORD, AuthProtocol.pop3);
        RespHeaders respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(QUSER, LOCALHOST_IP, POP3_PORT);
    }

    @Test
    public void pop3ssl() throws Exception {
        LookupData lookupData = new LookupData(AuthMethod.plain, USER, PASSWORD, AuthProtocol.pop3ssl);
        RespHeaders respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(QUSER, LOCALHOST_IP, POP3_SSL_PORT);
    }

    @Test
    public void http() throws Exception {
        LookupData lookupData = new LookupData(AuthMethod.plain, USER, PASSWORD, AuthProtocol.http);
        RespHeaders respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(QUSER, LOCALHOST_IP, HTTP_PORT);
    }
    
    @Test
    public void externalRouteOnAccountUseRouteOnAccount() throws Exception {
        String user = "user";
        String domainName = "account.account.externalroute";
        String quser = user + "@" + domainName;
        
        Account acct = createAccount(user, domainName);
        Domain domain = getDomain(domainName);
        
        setupExternalRoute(acct, true, "1", "2", "3", "4");
        
        LookupData lookupData;
        RespHeaders respHdrs;
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.pop3);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "1");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.pop3ssl);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "2");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.imap);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "3");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.imapssl);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "4");
        
        deleteAccount(acct);
        deleteDomain(domain);
    }
 
    @Test
    public void externalRouteOnAccountUseRouteOnAccountUseRouteOnDomain() throws Exception {
        String user = "user";
        String domainName = "account.domain.externalroute";
        String quser = user + "@" + domainName;
        
        Account acct = createAccount(user, domainName);
        setupExternalRoute(acct, true, "", "", "", "");
        
        Domain domain = getDomain(domainName);
        setupExternalRoute(domain, true, null, "5", "6", "7", "8");
        
        LookupData lookupData;
        RespHeaders respHdrs;
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.pop3);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "5");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.pop3ssl);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "6");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.imap);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "7");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.imapssl);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "8");
        
        deleteAccount(acct);
        deleteDomain(domain);
    }
    
    @Test
    public void externalRouteOnDomainUseRouteOnAccountUseRouteOnAccount() throws Exception {
        String user = "user";
        String domainName = "domain.account.externalroute";
        String quser = user + "@" + domainName;
        
        Account acct = createAccount(user, domainName);
        setupExternalRoute(acct, null, "1", "2", "3", "4");
        
        Domain domain = getDomain(domainName);
        setupExternalRoute(domain, true, null, "5", "6", "7", "8");
        
        LookupData lookupData;
        RespHeaders respHdrs;
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.pop3);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "1");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.pop3ssl);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "2");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.imap);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "3");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.imapssl);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "4");
        
        deleteAccount(acct);
        deleteDomain(domain);
    }
    
    @Test
    public void externalRouteOnDomainUseRouteOnAccountUseRouteOnDomain() throws Exception {
        String user = "user";
        String domainName = "domain.domain.externalroute";
        String quser = user + "@" + domainName;
        
        Account acct = createAccount(user, domainName);
        setupExternalRoute(acct, null, "", "", "", "");
        
        Domain domain = getDomain(domainName);
        setupExternalRoute(domain, true, null, "5", "6", "7", "8");
        
        LookupData lookupData;
        RespHeaders respHdrs;
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.pop3);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "5");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.pop3ssl);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "6");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.imap);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "7");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.imapssl);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "8");
        
        deleteAccount(acct);
        deleteDomain(domain);
    }
    
    @Test
    public void externalRouteOnDomainIfAccountNoExistUseRouteOnAccountUseRouteOnDomain() throws Exception {
        String user = "user";
        String domainName = "domain.domain.acountNotExist.externalroute";
        String quser = user + "@" + domainName;
        
        Domain domain = createDomain(domainName);
        setupExternalRoute(domain, true, true, "5", "6", "7", "8");
        
        LookupData lookupData;
        RespHeaders respHdrs;
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.pop3);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "5");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.pop3ssl);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "6");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.imap);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "7");
        
        lookupData = new LookupData(AuthMethod.plain, quser, PASSWORD, AuthProtocol.imapssl);
        respHdrs = senRequest(lookupData);
        respHdrs.assertBasic(quser, LOCALHOST_IP, "8");
        
        deleteDomain(domain);
    }
}
