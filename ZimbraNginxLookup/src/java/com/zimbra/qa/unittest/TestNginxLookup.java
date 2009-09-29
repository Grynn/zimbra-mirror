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
package com.zimbra.qa.unittest;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.util.CliUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.Provisioning.DomainBy;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.nginx.NginxLookupExtension;
import com.zimbra.cs.servlet.ZimbraServlet;

import com.zimbra.cs.nginx.NginxLookupExtension;

public class TestNginxLookup extends TestCase {
    
    private static SoapProvisioning mSoapProv = null;
    
    private static final String URL = "http://localhost:7072/service/extension/nginx-lookup";
    private static final String ACCT_LOCALPART = "user1";
    private static final String ACCT2_LOCALPART = "user2";
    
    private static final String PASSWORD = "test123";
    
    // private static final String IMAP_HOST = "10.10.131.101";  
    // private static final String IMAP_HOST = "192.168.0.162";
    private static final String IMAP_HOST_IP = "127.0.0.1";
    private static final String IMAP_SSL_HOST_IP = "127.0.0.1";
    private static final String IMAP_PORT = "7143";
    private static final String IMAP_SSL_PORT = "7993";
    
    // private static final String POP3_HOST = "192.168.0.162";
    private static final String POP3_HOST_IP = "127.0.0.1";
    private static final String POP3_SSL_HOST_IP = "127.0.0.1";
    private static final String POP3_PORT = "7110";
    private static final String POP3_SSL_PORT = "7995";
    
    private static final String HTTP_HOST_IP = "127.0.0.1";
    private static final String HTTP_PORT    = "7070";
    
    private static final String TEST_HOST_DOGFOOD    = "dogfood.zimbra.com";
    private static final String TEST_HOST_IP_DOGFOOD = "207.126.229.140";
    private static final String TEST_HOST_CATFOOD    = "catfood.zimbra.com";
    private static final String TEST_HOST_IP_CATFOOD = "207.126.229.141";
    
    private static final String IMAP_EXTERNAL_HOST = TEST_HOST_DOGFOOD;
    private static final String IMAP_EXTERNAL_HOST_IP = TEST_HOST_IP_DOGFOOD;
    private static final String IMAP_EXTERNAL_PORT = "1111";
    private static final String IMAP_SSL_EXTERNAL_HOST = TEST_HOST_CATFOOD;
    private static final String IMAP_SSL_EXTERNAL_HOST_IP = TEST_HOST_IP_CATFOOD;
    private static final String IMAP_SSL_EXTERNAL_PORT = "2222";
    private static final String POP3_EXTERNAL_HOST = TEST_HOST_CATFOOD;
    private static final String POP3_EXTERNAL_HOST_IP = TEST_HOST_IP_CATFOOD;
    private static final String POP3_EXTERNAL_PORT = "3333";
    private static final String POP3_SSL_EXTERNAL_HOST = TEST_HOST_DOGFOOD;
    private static final String POP3_SSL_EXTERNAL_HOST_IP = TEST_HOST_IP_DOGFOOD;
    private static final String POP3_SSL_EXTERNAL_PORT = "4444";
    
    private static final String IMAP_EXTERNAL_HOST_ON_DOMAIN = TEST_HOST_CATFOOD;
    private static final String IMAP_EXTERNAL_HOST_IP_ON_DOMAIN = TEST_HOST_IP_CATFOOD;
    private static final String IMAP_EXTERNAL_PORT_ON_DOMAIN = "5555";
    private static final String IMAP_SSL_EXTERNAL_HOST_ON_DOMAIN = TEST_HOST_DOGFOOD;
    private static final String IMAP_SSL_EXTERNAL_HOST_IP_ON_DOMAIN = TEST_HOST_IP_DOGFOOD;
    private static final String IMAP_SSL_EXTERNAL_PORT_ON_DOMAIN = "6666";
    private static final String POP3_EXTERNAL_HOST_ON_DOMAIN = TEST_HOST_DOGFOOD;
    private static final String POP3_EXTERNAL_HOST_IP_ON_DOMAIN = TEST_HOST_IP_DOGFOOD;
    private static final String POP3_EXTERNAL_PORT_ON_DOMAIN = "7777";
    private static final String POP3_SSL_EXTERNAL_HOST_ON_DOMAIN = TEST_HOST_CATFOOD;
    private static final String POP3_SSL_EXTERNAL_HOST_IP_ON_DOMAIN = TEST_HOST_IP_CATFOOD;
    private static final String POP3_SSL_EXTERNAL_PORT_ON_DOMAIN = "8888";
    
    private static final String STATUS_OK = "OK";
    private static final String STATUS_LOGIN_FAILED = NginxLookupExtension.NginxLookupHandler.ERRMSG;
    private static final String AUTH_WAIT = "10";
    private static final String FOREIGN_ID = "t12345";
    
    /*
     * There are three test accounts:
     * ACCT: test account in the test domain, with foreign principal ACCT_FOREIGN_PRINCIPAL
     * ACCT1: account in the default domain with the same local part as ACCT, no foreign principal
     * ACCT2: account in the default domain with different local part as ACCT, 
     *        with foreign principal that has the same "FOREIGN_ID" as that for ACCT,
     *        the foreign principal is FOREIGN_ID+"@"SYSTEM_DEFAULT_DOMAIN
     */
    // to be initialized in testInit
    private static String ACCT_EMAIL;
    private static String ACCT1_EMAIL;
    private static String ACCT2_EMAIL;
    private static String DOMAIN;
    private static String DOMAIN_VIRTUAL_IP;
    private static String SYSTEM_DEFAULT_DOMAIN;
    private static String ACCT_FOREIGN_PRINCIPAL;
    private static String ACCT2_FOREIGN_PRINCIPAL;
    
    public static class Result {
        public Result(String status, String server, String port, String user, String wait) {
            mStatus = status;
            mServer = server;
            mPort = port;
            mUser = user;
            mWait = wait;
        }
        
        void verify(String status, String server, String port, String user, String wait) {
            assertEquals(status, mStatus);
            assertEquals(server, mServer);
            assertEquals(port, mPort);
            
            /*
             * Strange, user should only be returned when the actual user name(email) is different 
             * than the email in the lookup request.  But it seems this behavior has been changed 
             * and the Auth-User is always returned.  Not sure if this is intended, leave it since 
             * it doesn't seem to break anything.
             */
            if (user != null)
                assertEquals(user, mUser);
            assertEquals(wait, mWait);
        }
        
        String mStatus;
        String mServer;
        String mPort;
        String mUser;
        String mWait;
    }
    
    private void modifyConfig(Map<String, Object> attrs) throws Exception {
        Config config = mSoapProv.getConfig();
        mSoapProv.modifyAttrs(config, attrs);
    }
    
    public void testInit() throws Exception {
        
        CliUtil.toolSetup();
        mSoapProv = new SoapProvisioning();
        mSoapProv.soapSetURI("https://localhost:7071" + AdminConstants.ADMIN_SERVICE_URI);
        mSoapProv.soapZimbraAdminAuthenticate();
        
        String TEST_ID = TestProvisioningUtil.genTestId();
        String TEST_NAME = "test-nginxlookup";
        
        DOMAIN = TestProvisioningUtil.baseDomainName(TEST_NAME, TEST_ID);
        
        // revert reverse proxy config to defaults
        unsetLookupByForeignPrincipal();
        
        SYSTEM_DEFAULT_DOMAIN = mSoapProv.getConfig().getAttr(Provisioning.A_zimbraDefaultDomainName);
        ACCT_EMAIL = ACCT_LOCALPART + "@" + DOMAIN;
        ACCT1_EMAIL = ACCT_LOCALPART + "@" + SYSTEM_DEFAULT_DOMAIN;
        ACCT2_EMAIL = ACCT2_LOCALPART + "@" + SYSTEM_DEFAULT_DOMAIN;
        
        // create the domain
        Map<String, Object> domainAttrs = new HashMap<String, Object>();
        // generate a unique IP for this test run so we won't get multiple from search
        SimpleDateFormat fmt =  new SimpleDateFormat("10.HH:mm:ss");
        DOMAIN_VIRTUAL_IP = fmt.format(new Date());
        domainAttrs.put(Provisioning.A_zimbraVirtualIPAddress, DOMAIN_VIRTUAL_IP);
        Domain domain = mSoapProv.createDomain(DOMAIN, domainAttrs);
        assertNotNull(domain);
        
        // create the test account
        ACCT_FOREIGN_PRINCIPAL = FOREIGN_ID + "@" + DOMAIN;
        Map<String, Object> acctAttrs = new HashMap<String, Object>();
        acctAttrs.put(Provisioning.A_zimbraForeignPrincipal, ACCT_FOREIGN_PRINCIPAL);
        Account acct = mSoapProv.createAccount(ACCT_EMAIL, PASSWORD, acctAttrs);
        assertNotNull(acct);
        
        // setup external route fort he acccout
        setupAccountExternalRoute(acct);
        
        // set foreign id for an account in the system default domain
        acctAttrs.clear();
        ACCT2_FOREIGN_PRINCIPAL = FOREIGN_ID + "@" +SYSTEM_DEFAULT_DOMAIN;
        acctAttrs.put(Provisioning.A_zimbraForeignPrincipal, ACCT2_FOREIGN_PRINCIPAL);
        Account acct2 = mSoapProv.get(AccountBy.name, ACCT2_EMAIL);
        assertNotNull(acct2);
        mSoapProv.modifyAttrs(acct2, acctAttrs);
    }
    
    // revert reverse proxy config to defaults
    private void unsetLookupByForeignPrincipal() throws Exception {
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(Provisioning.A_zimbraReverseProxyUserNameAttribute, "");
        attrs.put(Provisioning.A_zimbraReverseProxyMailHostQuery, "(|(zimbraMailDeliveryAddress=${USER})(zimbraMailAlias=${USER})(zimbraId=${USER}))");
        modifyConfig(attrs);
    }
    
    private void setupLookupByForeignPrincipal() throws Exception {
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(Provisioning.A_zimbraReverseProxyUserNameAttribute, "zimbraMailDeliveryAddress");
        attrs.put(Provisioning.A_zimbraReverseProxyMailHostQuery, "(zimbraForeignPrincipal=${USER})"); 
        modifyConfig(attrs);
    }
    
    private void unsetAccountExternalRouteFlag(String acctEmail) throws Exception {
        Account acct = mSoapProv.get(AccountBy.name, acctEmail);
        assertNotNull(acct);
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(Provisioning.A_zimbraReverseProxyUseExternalRoute, "");
        mSoapProv.modifyAttrs(acct, attrs);
    }
    
    private void setAccountExternalRouteFlag(String acctEmail) throws Exception {
        Account acct = mSoapProv.get(AccountBy.name, acctEmail);
        assertNotNull(acct);
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(Provisioning.A_zimbraReverseProxyUseExternalRoute, "TRUE");
        mSoapProv.modifyAttrs(acct, attrs);
    }
    
    private void setupAccountExternalRoute(Account acct) throws Exception {
        assertNotNull(acct);
        
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(Provisioning.A_zimbraExternalPop3Port, POP3_EXTERNAL_PORT);
        attrs.put(Provisioning.A_zimbraExternalPop3SSLPort, POP3_SSL_EXTERNAL_PORT);
        attrs.put(Provisioning.A_zimbraExternalImapPort, IMAP_EXTERNAL_PORT);
        attrs.put(Provisioning.A_zimbraExternalImapSSLPort, IMAP_SSL_EXTERNAL_PORT);
        attrs.put(Provisioning.A_zimbraExternalPop3Hostname, POP3_EXTERNAL_HOST);
        attrs.put(Provisioning.A_zimbraExternalPop3SSLHostname, POP3_SSL_EXTERNAL_HOST);
        attrs.put(Provisioning.A_zimbraExternalImapHostname, IMAP_EXTERNAL_HOST);
        attrs.put(Provisioning.A_zimbraExternalImapSSLHostname, IMAP_SSL_EXTERNAL_HOST);
        
        mSoapProv.modifyAttrs(acct, attrs);
    }
    
    public void testFullEmail() throws Exception {
        // unset the external route flag
        unsetAccountExternalRouteFlag(ACCT_EMAIL);
        doTest(ACCT_EMAIL, PASSWORD, null, "imap").verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, null, null);
        doTest(ACCT_EMAIL, PASSWORD, null, "pop3").verify(STATUS_OK, POP3_HOST_IP, POP3_PORT, null, null);
        
        // set the external route flag
        setAccountExternalRouteFlag(ACCT_EMAIL);
        doTest(ACCT_EMAIL, PASSWORD, null, "imap").verify(STATUS_OK, IMAP_EXTERNAL_HOST_IP, IMAP_EXTERNAL_PORT, null, null);
        doTest(ACCT_EMAIL, PASSWORD, null, "pop3").verify(STATUS_OK, POP3_EXTERNAL_HOST_IP, POP3_EXTERNAL_PORT, null, null);
    }
    
    /*
     * should find the account by domain virtual IP
     */
    public void testVirtualDomainByProxyIP() throws Exception {
        unsetAccountExternalRouteFlag(ACCT_EMAIL);
        doTest(ACCT_LOCALPART, PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, ACCT_EMAIL, null);
        doTest(ACCT_LOCALPART, PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_OK, POP3_HOST_IP, POP3_PORT, ACCT_EMAIL, null);
        
        setAccountExternalRouteFlag(ACCT_EMAIL);
        doTest(ACCT_LOCALPART, PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_OK, IMAP_EXTERNAL_HOST_IP, IMAP_EXTERNAL_PORT, ACCT_EMAIL, null);
        doTest(ACCT_LOCALPART, PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_OK, POP3_EXTERNAL_HOST_IP, POP3_EXTERNAL_PORT, ACCT_EMAIL, null);
    }
    
    /*
     * should find the account in the default domain
     */
    public void testVirtualDomainWrongProxyIP() throws Exception {
        doTest(ACCT_LOCALPART, PASSWORD, "127.0.0.2", "imap").verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, ACCT1_EMAIL, null);
        doTest(ACCT_LOCALPART, PASSWORD, "127.0.0.2", "pop3").verify(STATUS_OK, POP3_HOST_IP, POP3_PORT, ACCT1_EMAIL, null);
    }
    
    /*
     * should find the account in the default domain
     */
    public void testVirtualDomainNoProxyIP() throws Exception {
        doTest(ACCT_LOCALPART, PASSWORD, null, "imap").verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, ACCT1_EMAIL, null);
        doTest(ACCT_LOCALPART, PASSWORD, null, "pop3").verify(STATUS_OK, POP3_HOST_IP, POP3_PORT, ACCT1_EMAIL, null);
    }
    
    
    private Domain setupExternalRouteDomain(String domainName) throws Exception {
        Domain domain = mSoapProv.get(DomainBy.name, domainName);
        
        if (domain == null) {
            Map<String, Object> attrs = new HashMap<String, Object>();
            attrs.put(Provisioning.A_zimbraExternalPop3Port, POP3_EXTERNAL_PORT_ON_DOMAIN);
            attrs.put(Provisioning.A_zimbraExternalPop3SSLPort, POP3_SSL_EXTERNAL_PORT_ON_DOMAIN);
            attrs.put(Provisioning.A_zimbraExternalImapPort, IMAP_EXTERNAL_PORT_ON_DOMAIN);
            attrs.put(Provisioning.A_zimbraExternalImapSSLPort, IMAP_SSL_EXTERNAL_PORT_ON_DOMAIN);
            attrs.put(Provisioning.A_zimbraExternalPop3Hostname, POP3_EXTERNAL_HOST_ON_DOMAIN);
            attrs.put(Provisioning.A_zimbraExternalPop3SSLHostname, POP3_SSL_EXTERNAL_HOST_ON_DOMAIN);
            attrs.put(Provisioning.A_zimbraExternalImapHostname, IMAP_EXTERNAL_HOST_ON_DOMAIN);
            attrs.put(Provisioning.A_zimbraExternalImapSSLHostname, IMAP_SSL_EXTERNAL_HOST_ON_DOMAIN);
            
            domain = mSoapProv.createDomain(domainName, attrs);
        }
        assertNotNull(domain);
        return domain;
    }
    
    public void testExternalRouteOnAccount() throws Exception {
        String domainName = "external-route." + DOMAIN;
        setupExternalRouteDomain(domainName);
        
        String acctEmail = "user1@" + domainName;
        Account acct = mSoapProv.createAccount(acctEmail, PASSWORD, null);
        
        setAccountExternalRouteFlag(acctEmail);
        setupAccountExternalRoute(acct);
        
        doTest(acctEmail, PASSWORD, null, "imap").verify(STATUS_OK, IMAP_EXTERNAL_HOST_IP, IMAP_EXTERNAL_PORT, null, null);
        doTest(acctEmail, PASSWORD, null, "pop3").verify(STATUS_OK, POP3_EXTERNAL_HOST_IP, POP3_EXTERNAL_PORT, null, null);
        doTest(acctEmail, PASSWORD, null, "imapssl").verify(STATUS_OK, IMAP_SSL_EXTERNAL_HOST_IP, IMAP_SSL_EXTERNAL_PORT, null, null);
        doTest(acctEmail, PASSWORD, null, "pop3ssl").verify(STATUS_OK, POP3_SSL_EXTERNAL_HOST_IP, POP3_SSL_EXTERNAL_PORT, null, null);

        // external route only applies to pop3/imap, not http
        doTest(acctEmail, PASSWORD, null, "http").verify(STATUS_OK, HTTP_HOST_IP, HTTP_PORT, null, null);

    }
    
    public void testExternalRouteOnDomain() throws Exception {
        String domainName = "external-route." + DOMAIN;
        setupExternalRouteDomain(domainName);
        
        String acctEmail = "user2@" + domainName;
        Account acct = mSoapProv.createAccount(acctEmail, PASSWORD, null);
        
        setAccountExternalRouteFlag(acctEmail);
        
        doTest(acctEmail, PASSWORD, null, "imap").verify(STATUS_OK, IMAP_EXTERNAL_HOST_IP_ON_DOMAIN, IMAP_EXTERNAL_PORT_ON_DOMAIN, null, null);
        doTest(acctEmail, PASSWORD, null, "pop3").verify(STATUS_OK, POP3_EXTERNAL_HOST_IP_ON_DOMAIN, POP3_EXTERNAL_PORT_ON_DOMAIN, null, null);
        doTest(acctEmail, PASSWORD, null, "imapssl").verify(STATUS_OK, IMAP_SSL_EXTERNAL_HOST_IP_ON_DOMAIN, IMAP_SSL_EXTERNAL_PORT_ON_DOMAIN, null, null);
        doTest(acctEmail, PASSWORD, null, "pop3ssl").verify(STATUS_OK, POP3_SSL_EXTERNAL_HOST_IP_ON_DOMAIN, POP3_SSL_EXTERNAL_PORT_ON_DOMAIN, null, null);

        // external route only applies to pop3/imap, not http
        doTest(acctEmail, PASSWORD, null, "http").verify(STATUS_OK, HTTP_HOST_IP, HTTP_PORT, null, null);

        // set partial external route on account, should still fallback to the domain external route
        // settings, because the setting on account is not complete.
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(Provisioning.A_zimbraExternalPop3Hostname, POP3_EXTERNAL_HOST_ON_DOMAIN);
        attrs.put(Provisioning.A_zimbraExternalPop3SSLHostname, POP3_SSL_EXTERNAL_HOST_ON_DOMAIN);
        attrs.put(Provisioning.A_zimbraExternalImapHostname, IMAP_EXTERNAL_HOST_ON_DOMAIN);
        attrs.put(Provisioning.A_zimbraExternalImapSSLHostname, IMAP_SSL_EXTERNAL_HOST_ON_DOMAIN);
        mSoapProv.modifyAttrs(acct, attrs);
        
        doTest(acctEmail, PASSWORD, null, "imap").verify(STATUS_OK, IMAP_EXTERNAL_HOST_IP_ON_DOMAIN, IMAP_EXTERNAL_PORT_ON_DOMAIN, null, null);
        doTest(acctEmail, PASSWORD, null, "pop3").verify(STATUS_OK, POP3_EXTERNAL_HOST_IP_ON_DOMAIN, POP3_EXTERNAL_PORT_ON_DOMAIN, null, null);
        doTest(acctEmail, PASSWORD, null, "imapssl").verify(STATUS_OK, IMAP_SSL_EXTERNAL_HOST_IP_ON_DOMAIN, IMAP_SSL_EXTERNAL_PORT_ON_DOMAIN, null, null);
        doTest(acctEmail, PASSWORD, null, "pop3ssl").verify(STATUS_OK, POP3_SSL_EXTERNAL_HOST_IP_ON_DOMAIN, POP3_SSL_EXTERNAL_PORT_ON_DOMAIN, null, null);
    }
    
    public void testExternalRouteMissingExternalRouteInfo() throws Exception {
        String domainName = "external-route-missing-info." + DOMAIN;
        Domain domain = mSoapProv.createDomain(domainName, null);  // create the domain with no external route info
        
        String acctEmail = "user@" + domainName;
        Account acct = mSoapProv.createAccount(acctEmail, PASSWORD, null);  // create the account with no external route info
        
        setAccountExternalRouteFlag(acctEmail);  // turn on the use external route flag on account 
        
        // should all fallback to internal route
        doTest(acctEmail, PASSWORD, null, "imap").verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, null, null);
        doTest(acctEmail, PASSWORD, null, "pop3").verify(STATUS_OK, POP3_HOST_IP, POP3_PORT, null, null);
        doTest(acctEmail, PASSWORD, null, "imapssl").verify(STATUS_OK, IMAP_SSL_HOST_IP, IMAP_SSL_PORT, null, null);
        doTest(acctEmail, PASSWORD, null, "pop3ssl").verify(STATUS_OK, POP3_SSL_HOST_IP, POP3_SSL_PORT, null, null);
    }
    
    /*
        NGINX will no longer pass any suffixes(/tb|/wm|/ni) to the lookup servlet, see p4 change 107019.
        
        Skip all the Extension tests.  orig bug: 20542
    */
    public void skip_testSupportedExtensionFullEmail() throws Exception {
        doTest(ACCT_EMAIL+"/tb", PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, null, null);
        doTest(ACCT_EMAIL+"/tb", PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_OK, POP3_HOST_IP, POP3_PORT, null, null);
    }
    
    public void skip_testSupportedExtensionVirtualDomain() throws Exception {
        doTest(ACCT_LOCALPART+"/tb", PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, ACCT_EMAIL+"/tb", null);
        doTest(ACCT_LOCALPART+"/tb", PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_OK, POP3_HOST_IP, POP3_PORT, ACCT_EMAIL+"/tb", null);
    }
    
    public void skip_testUnsupportedExtension() throws Exception {
        doTest(ACCT_LOCALPART + "/zz", PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_LOGIN_FAILED, null, null, null, AUTH_WAIT);
        doTest(ACCT_LOCALPART + "/zz", PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_LOGIN_FAILED, null, null, null, AUTH_WAIT);
    }
    
    public void testLookupByForeignPrincipalFullEmail()  throws Exception {
        setupLookupByForeignPrincipal();
        unsetAccountExternalRouteFlag(ACCT_EMAIL);
        
        // full email
        doTest(ACCT_FOREIGN_PRINCIPAL, PASSWORD, null, "imap").verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, ACCT_EMAIL, null);
        doTest(ACCT_FOREIGN_PRINCIPAL, PASSWORD, null, "pop3").verify(STATUS_OK, POP3_HOST_IP, POP3_PORT, ACCT_EMAIL, null);
        
        setAccountExternalRouteFlag(ACCT_EMAIL);
        doTest(ACCT_FOREIGN_PRINCIPAL, PASSWORD, null, "imap").verify(STATUS_OK, IMAP_EXTERNAL_HOST_IP, IMAP_EXTERNAL_PORT, ACCT_EMAIL, null);
        doTest(ACCT_FOREIGN_PRINCIPAL, PASSWORD, null, "pop3").verify(STATUS_OK, POP3_EXTERNAL_HOST_IP, POP3_EXTERNAL_PORT, ACCT_EMAIL, null);
        
        /*
        // full email with supported extension
        doTest(ACCT_FOREIGN_PRINCIPAL+"/tb", PASSWORD, null, "imap").verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, ACCT_EMAIL+"/tb", null);
        doTest(ACCT_FOREIGN_PRINCIPAL+"/tb", PASSWORD, null, "pop3").verify(STATUS_OK, POP3_HOST_IP, POP3_PORT, ACCT_EMAIL+"/tb", null);
        
        // full email with unsupported extension
        doTest(ACCT_FOREIGN_PRINCIPAL+"/zz", PASSWORD, null, "imap").verify(STATUS_LOGIN_FAILED, null, null, null, AUTH_WAIT);
        doTest(ACCT_FOREIGN_PRINCIPAL+"/zz", PASSWORD, null, "pop3").verify(STATUS_LOGIN_FAILED, null, null, null, AUTH_WAIT);
        */
        
        unsetLookupByForeignPrincipal();
    }
    
    public void testLookupByForeignPrincipalVirtualDomain() throws Exception {
        setupLookupByForeignPrincipal();
        
        unsetAccountExternalRouteFlag(ACCT_EMAIL);
        
        // virtual domain by proxy IP
        doTest(FOREIGN_ID, PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, ACCT_EMAIL, null);
        doTest(FOREIGN_ID, PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_OK, POP3_HOST_IP, POP3_PORT, ACCT_EMAIL, null);
    
        setAccountExternalRouteFlag(ACCT_EMAIL);
        doTest(FOREIGN_ID, PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_OK, IMAP_EXTERNAL_HOST_IP, IMAP_EXTERNAL_PORT, ACCT_EMAIL, null);
        doTest(FOREIGN_ID, PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_OK, POP3_EXTERNAL_HOST_IP, POP3_EXTERNAL_PORT, ACCT_EMAIL, null);
        
        // virtual domain wrong proxy IP, the foreign id + default domain exists
        doTest(FOREIGN_ID, PASSWORD, "127.0.0.2", "imap").verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, ACCT2_EMAIL, null);
        doTest(FOREIGN_ID, PASSWORD, "127.0.0.2", "pop3").verify(STATUS_OK, POP3_HOST_IP, POP3_PORT, ACCT2_EMAIL, null);

        // virtual domain wrong proxy IP, the foreign id + default domain does not exist
        doTest(FOREIGN_ID+"wrong", PASSWORD, "127.0.0.2", "imap").verify(STATUS_LOGIN_FAILED, null, null, null, AUTH_WAIT);
        doTest(FOREIGN_ID+"wrong", PASSWORD, "127.0.0.2", "pop3").verify(STATUS_LOGIN_FAILED, null, null, null, AUTH_WAIT);

        // virtual domain no proxy IP
        doTest(FOREIGN_ID, PASSWORD, null, "imap").verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, ACCT2_EMAIL, null);
        doTest(FOREIGN_ID, PASSWORD, null, "pop3").verify(STATUS_OK, POP3_HOST_IP, POP3_PORT, ACCT2_EMAIL, null);

        /*
        // virtual domain with supported extension
        doTest(FOREIGN_ID+"/tb", PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, ACCT_EMAIL+"/tb", null);
        doTest(FOREIGN_ID+"/tb", PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_OK, POP3_HOST_IP, POP3_PORT, ACCT_EMAIL+"/tb", null);
        
        // virtual domain with unsupported extension
        doTest(FOREIGN_ID+"/zz", PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_LOGIN_FAILED, null, null, null, AUTH_WAIT);
        doTest(FOREIGN_ID+"/zz", PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_LOGIN_FAILED, null, null, null, AUTH_WAIT);
        */
        
        unsetLookupByForeignPrincipal();
    }
    
    public void testGssApi() throws Exception {
        
        /*
        zmprov md phoebe.mac zimbraAuthKerberos5Realm ZIMBRA.COM zimbraVirtualIPAddress 13.12.11.10
        zmprov mcf zimbraReverseProxyAdminIPAddress 13.12.11.10 
        */
        
        // domain
        String domainName = DOMAIN;
        Domain domain = mSoapProv.get(DomainBy.name, domainName);
        
        // test account names
        String acctLocalPart = "gssapi-test";
        String acctEmail = acctLocalPart + "@" + domainName;
        String otherAcctLocalPart = "gssapi-test-other";
        String otherAcctEmail = otherAcctLocalPart + "@" + domainName;
        
        // krb5 realm/principal
        // generate a unique realm for this test
        SimpleDateFormat fmtRealm =  new SimpleDateFormat("HH-mm-ss-");
        String krb5Realm = fmtRealm.format(new Date()) + "ZIMBRA.COM";
        String acctKrb5Principal = acctLocalPart + "@" + krb5Realm;
        
        // admin account
        String adminAcct = "zmnginx";
        String adminPassword = "zmnginx";  // from ZimbraServer/conf/ldap/zimbra.ldif
        
        // simulating nginx server IP and remote client IP.
        // generate a unique IP for this test
        SimpleDateFormat fmtIp =  new SimpleDateFormat("10.HH:mm:ss");
        String nginxServerIp = fmtIp.format(new Date()); // "13.12.11.10";
        String clientIp = "11.22.33.44"; // doesn't really matter
        
        // create test accounts
        mSoapProv.createAccount(acctEmail, PASSWORD, null);
        mSoapProv.createAccount(otherAcctEmail, PASSWORD, null);
        
        // setup domain
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(Provisioning.A_zimbraAuthKerberos5Realm, krb5Realm);
        attrs.put(Provisioning.A_zimbraVirtualIPAddress, nginxServerIp);
        mSoapProv.modifyAttrs(domain, attrs);
        
        // setup global config
        Config config = mSoapProv.getConfig();
        attrs.clear();
        attrs.put(Provisioning.A_zimbraReverseProxyAdminIPAddress, nginxServerIp);
        mSoapProv.modifyAttrs(config, attrs);
        
        // family mailbox
        String childLocalPart = "child";
        String childEmail = childLocalPart +  "@" + domainName;
        Account child = mSoapProv.createAccount(childEmail, PASSWORD, null);
        
        String parentLocalPart = "parent";
        String parentEmail = parentLocalPart + "@" + domainName;
        String parentKrb5Principal = parentLocalPart + "@" + krb5Realm;
        attrs.clear();
        attrs.put(Provisioning.A_zimbraChildAccount, child.getId());
        Account parent = mSoapProv.createAccount(parentEmail, PASSWORD, attrs);
        
        
        doTest("gssapi",  acctLocalPart,      null,  "imap",  "1",  clientIp,  nginxServerIp,  null,  acctKrb5Principal,  adminAcct,  adminPassword).verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, acctEmail, null);
        doTest("gssapi",  acctEmail,          null,  "imap",  "1",  clientIp,  nginxServerIp,  null,  acctKrb5Principal,  adminAcct,  adminPassword).verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, acctEmail, null);
        doTest("gssapi",  acctKrb5Principal,  null,  "imap",  "1",  clientIp,  nginxServerIp,  null,  acctKrb5Principal,  adminAcct,  adminPassword).verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, acctEmail, null);
        doTest("gssapi",  otherAcctLocalPart, null,  "imap",  "1",  clientIp,  nginxServerIp,  null,  acctKrb5Principal,  adminAcct,  adminPassword).verify(STATUS_LOGIN_FAILED, null, null, null, AUTH_WAIT);
        doTest("gssapi",  acctLocalPart,      null,  "imap",  "1",  clientIp,  nginxServerIp,  null,  acctKrb5Principal,  "zmnginxbogus",  adminPassword).verify(STATUS_LOGIN_FAILED, null, null, null, AUTH_WAIT);
        doTest("gssapi",  childLocalPart,     null,  "imap",  "1",  clientIp,  nginxServerIp,  null,  parentKrb5Principal,  adminAcct,  adminPassword).verify(STATUS_OK, IMAP_HOST_IP, IMAP_PORT, childEmail, null);
    }
    
    private static Result doLookupReq(HttpClient client, GetMethod method) {
        try {
            int statusCode = client.executeMethod(method);
        
            Header authStatus = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_STATUS);
            Header authServer = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_SERVER);
            Header authPort = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_PORT);
            Header authUser = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_USER);
            Header authWait = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_WAIT);
            
            return new Result(authStatus==null?null:authStatus.getValue(),
                              authServer==null?null:authServer.getValue(),
                              authPort==null?null:authPort.getValue(),
                              authUser==null?null:authUser.getValue(),
                              authWait==null?null:authWait.getValue());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private static Result doTest(String user, String pass, String serverIp, String protocol) {
        return doTest("plain", user, pass, protocol, null, null, serverIp, null, null, null, null);
    }
    
    private static Result doTest(String h_AUTH_METHOD,
                                 String h_AUTH_USER,
                                 String h_AUTH_PASS,
                                 String h_AUTH_PROTOCOL,
                                 String h_AUTH_LOGIN_ATTEMPT,
                                 String h_CLIENT_IP,
                                 String h_SERVER_IP,
                                 String h_SERVER_HOST,
                                 String h_AUTH_ID,
                                 String h_AUTH_ADMIN_USER,
                                 String h_AUTH_ADMIN_PASS) {
        
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(URL);
        
        method.setRequestHeader("Host", "localhost");
        if (h_AUTH_METHOD != null)
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_METHOD, h_AUTH_METHOD);
        if (h_AUTH_USER != null) 
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_USER, h_AUTH_USER);
        if (h_AUTH_PASS != null) 
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_PASS, h_AUTH_PASS);
        if (h_AUTH_PROTOCOL != null) 
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_PROTOCOL, h_AUTH_PROTOCOL);
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
        
        return doLookupReq(client, method);
    }

    public static void main(String args[]) {

        TestNginxLookup t = new TestNginxLookup();
        t.run();
    
    }
}
