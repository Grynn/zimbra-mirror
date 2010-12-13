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
package com.zimbra.soap;

import java.util.ArrayList;
import java.util.List;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.developer.WSBindingProvider;
import com.sun.xml.bind.api.JAXBRIContext;

import com.zimbra.soap.account.wsimport.generated.AccountService_Service;
import com.zimbra.soap.account.wsimport.generated.AccountService;
import com.zimbra.soap.account.wsimport.generated.Account;
import com.zimbra.soap.account.wsimport.generated.AuthRequest;
import com.zimbra.soap.account.wsimport.generated.AuthResponse;
import com.zimbra.soap.account.wsimport.generated.By;
import com.zimbra.soap.admin.wsimport.generated.AdminService_Service;
import com.zimbra.soap.admin.wsimport.generated.AdminService;
import com.zimbra.soap.mail.wsimport.generated.MailService_Service;
import com.zimbra.soap.mail.wsimport.generated.MailService;

import org.junit.Assert;

/**
 * Current assumption : user1 exists with password test123
 */
public class Utility {
    private static AccountService acctSvcEIF = null;
    private static AdminService adminSvcEIF = null;
    private static MailService mailSvcEIF = null;
    private static String acctAuthToken = null;
    private static String adminAuthToken = null;

    public static void addSoapAcctAuthHeader(WSBindingProvider bp) throws Exception {
        Utility.getAccountServiceAuthToken();
        // Note that am not using JAXB generated HeaderContext here
        JAXBRIContext jaxb = (JAXBRIContext) JAXBRIContext.newInstance(com.zimbra.soap.header.HeaderContext.class);
        com.zimbra.soap.header.HeaderContext hdrCtx = new com.zimbra.soap.header.HeaderContext();
        hdrCtx.setAuthToken(acctAuthToken);
        Header soapHdr = Headers.create(jaxb,hdrCtx);
        List <Header> soapHdrs = new ArrayList <Header>();
        soapHdrs.add(soapHdr);
        // See http://metro.java.net/1.5/guide/SOAP_headers.html
        // WSBindingProvider bp = (WSBindingProvider)acctSvcEIF;
        bp.setOutboundHeaders(soapHdrs);
    }

    public static String getAccountServiceAuthToken() throws Exception {
        Utility.getAcctSvcEIF();
        if (acctAuthToken == null) {
            Utility.getAcctSvcEIF();
            AuthRequest authReq = new AuthRequest();
            Account acct = new Account();
            acct.setBy(By.NAME);
            acct.setValue("user1");
            authReq.setAccount(acct);
            authReq.setPassword("test123");
            authReq.setPreauth(null);
            authReq.setAuthToken(null);
            // Invoke the methods.
            AuthResponse authResponse = getAcctSvcEIF().authRequest(authReq);
            Assert.assertNotNull(authResponse);
            acctAuthToken = authResponse.getAuthToken();
        }
        return acctAuthToken;
    }

    private static void setAcctSvcEIF(AccountService acctSvcEIF) {
        Utility.acctSvcEIF = acctSvcEIF;
    }

    public static AccountService getAcctSvcEIF() throws Exception {
        if (acctSvcEIF == null) {
            // The AccountService_Service class is the Java type bound to
            // the service section of the WSDL document.
            AccountService_Service acctSvc = new AccountService_Service();
            setAcctSvcEIF(acctSvc.getAccountServicePort());
        }
        return acctSvcEIF;
    }

    public static void addSoapAdminAuthHeader(WSBindingProvider bp) throws Exception {
        Utility.getAdminServiceAuthToken();
        // Note that am not using JAXB generated HeaderContext here
        JAXBRIContext jaxb = (JAXBRIContext) JAXBRIContext.newInstance(com.zimbra.soap.header.HeaderContext.class);
        com.zimbra.soap.header.HeaderContext hdrCtx = new com.zimbra.soap.header.HeaderContext();
        hdrCtx.setAuthToken(adminAuthToken);
        Header soapHdr = Headers.create(jaxb,hdrCtx);
        List <Header> soapHdrs = new ArrayList <Header>();
        soapHdrs.add(soapHdr);
        // See http://metro.java.net/1.5/guide/SOAP_headers.html
        // WSBindingProvider bp = (WSBindingProvider)acctSvcEIF;
        bp.setOutboundHeaders(soapHdrs);
    }

    public static String getAdminServiceAuthToken() throws Exception {
        Utility.getAdminSvcEIF();
        if (adminAuthToken == null) {
            Utility.getAdminSvcEIF();
            com.zimbra.soap.admin.wsimport.generated.AuthRequest authReq =
                    new com.zimbra.soap.admin.wsimport.generated.AuthRequest();
            com.zimbra.soap.admin.wsimport.generated.Account acct =
                    new com.zimbra.soap.admin.wsimport.generated.Account();
            acct.setBy(com.zimbra.soap.admin.wsimport.generated.By.NAME);
            acct.setValue("admin");
            authReq.setAccount(acct);
            authReq.setPassword("test123");
            authReq.setAuthToken(null);
            com.zimbra.soap.admin.wsimport.generated.AuthResponse authResponse =
                    getAdminSvcEIF().authRequest(authReq);
            Assert.assertNotNull(authResponse);
            adminAuthToken = authResponse.getAuthToken();
            Assert.assertTrue(adminAuthToken != null);
            Assert.assertTrue(adminAuthToken.length() > 10);
        }
        return adminAuthToken;
    }

    private static void setAdminSvcEIF(AdminService adminSvcEIF) {
        Utility.adminSvcEIF = adminSvcEIF;
    }

    public static AdminService getAdminSvcEIF() throws Exception {
        if (adminSvcEIF == null) {
            // The AccountService_Service class is the Java type bound to
            // the service section of the WSDL document.
            AdminService_Service adminSvc = new AdminService_Service();
            setAdminSvcEIF(adminSvc.getAdminServicePort());
        }
        return adminSvcEIF;
    }

    private static void setMailSvcEIF(MailService mailSvcEIF) {
        Utility.mailSvcEIF = mailSvcEIF;
    }

    public static MailService getMailSvcEIF() {
        if (mailSvcEIF == null) {
            MailService_Service mailSvc = new MailService_Service();
            Utility.setMailSvcEIF(mailSvc.getMailServicePort());
        }
        return mailSvcEIF;
    }

    public static void setUpToAcceptAllHttpsServerCerts() {
        // Create a trust manager that does not validate certificate chains
        // without this, we need to import the server certificate into the trust store.
        // when using https as is required for Admin
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted( java.security.cert.X509Certificate[] certs, String authType) { }
                    }
                };
        // Install the all-trusting trust manager
        try {
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) { }
    }
}
