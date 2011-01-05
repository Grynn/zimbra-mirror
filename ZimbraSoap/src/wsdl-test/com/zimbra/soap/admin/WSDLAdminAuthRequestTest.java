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
package com.zimbra.soap.admin;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.xml.ws.developer.WSBindingProvider;

import com.zimbra.soap.admin.wsimport.generated.AccountBy;
import com.zimbra.soap.admin.wsimport.generated.AdminService;
import com.zimbra.soap.admin.wsimport.generated.AccountSelector;
import com.zimbra.soap.admin.wsimport.generated.Attr;
import com.zimbra.soap.admin.wsimport.generated.AuthRequest;
import com.zimbra.soap.admin.wsimport.generated.AuthResponse;
import com.zimbra.soap.admin.wsimport.generated.DelegateAuthRequest;
import com.zimbra.soap.admin.wsimport.generated.DelegateAuthResponse;

import com.zimbra.soap.Utility;

public class WSDLAdminAuthRequestTest {

    // The AdminService interface is the Java type bound to
    // the portType section of the WSDL document.
    private static AdminService eif;

    @BeforeClass
    public static void init() throws Exception {
        Utility.setUpToAcceptAllHttpsServerCerts();
        eif =Utility.getAdminSvcEIF();
    }

    @Test
    public void simple() throws Exception {
        AuthRequest authReq = new AuthRequest();
        AccountSelector acct = new AccountSelector();
        acct.setBy(AccountBy.NAME);
        acct.setValue("admin");
        authReq.setAccount(acct);
        authReq.setPassword("test123");
        authReq.setAuthToken(null);
        AuthResponse authResponse = eif.authRequest(authReq);
        Assert.assertNotNull(authResponse);
        String authToken = authResponse.getAuthToken();
        Assert.assertNotNull(authToken);
        int len = authToken.length();
        Assert.assertTrue("authToken length should be at least 10 actual value=" + len, len >= 10);
        long lifetime = authResponse.getLifetime();
        Assert.assertTrue("lifetime value should be +ve", lifetime > 0);
        Attr attr = authResponse.getA();
        Assert.assertEquals("value of <a> -", "false", attr.getValue());
        Assert.assertEquals("'n' attribute of <a> -", "zimbraIsDomainAdminAccount", attr.getN());
    }

    @Test
    public void badPasswd() throws Exception {
        AuthRequest authReq = new AuthRequest();
        AccountSelector acct = new AccountSelector();
        acct.setBy(AccountBy.NAME);
        acct.setValue("admin");
        authReq.setAccount(acct);
        authReq.setPassword("BAD-ONE");
        authReq.setAuthToken(null);
        // Invoke the methods.
        try {
            @SuppressWarnings("unused")
            AuthResponse authResponse = eif.authRequest(authReq);
            Assert.fail("Should have had a fault resulting in an exception being thrown");
        } catch (SOAPFaultException sfe) {
            Assert.assertTrue(
                    sfe.getMessage() + "should start with <authentication failed for >",
                    sfe.getMessage().startsWith("authentication failed for "));
        }
    }

    @Test
    public void nonAdminUser() throws Exception {
        AuthRequest authReq = new AuthRequest();
        AccountSelector acct = new AccountSelector();
        acct.setBy(AccountBy.NAME);
        acct.setValue("user1");
        authReq.setAccount(acct);
        authReq.setPassword("test123");
        authReq.setAuthToken(null);
        // Invoke the methods.
        try {
            @SuppressWarnings("unused")
            AuthResponse authResponse = eif.authRequest(authReq);
            Assert.fail("Should have had a fault resulting in an exception being thrown");
        } catch (SOAPFaultException sfe) {
            Assert.assertEquals("SOAP fault message - ",
                    "permission denied: not an admin account",
                    sfe.getMessage());
        }
    }

    @Test
    public void delegateAuth() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider) eif);
        DelegateAuthRequest delegateAuthReq = new DelegateAuthRequest();
        AccountSelector delegateAcct = new AccountSelector();
        delegateAcct.setBy(AccountBy.NAME);
        delegateAcct.setValue("admin");
        delegateAuthReq.setAccount(delegateAcct);
        DelegateAuthResponse delegateAuthResp = eif.delegateAuthRequest(delegateAuthReq);
        Assert.assertNotNull(delegateAuthResp);
        String authToken = delegateAuthResp.getAuthToken();
        Assert.assertNotNull(authToken);
        int len = authToken.length();
        Assert.assertTrue("authToken length should be at least 10 actual value=" + len, len >= 10);
        long lifetime = delegateAuthResp.getLifetime();
        Assert.assertEquals("lifetime value", 0, lifetime);
    }
}
