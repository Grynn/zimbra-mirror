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

import java.util.List;

import com.sun.xml.ws.developer.WSBindingProvider;

import com.zimbra.soap.admin.wsimport.generated.AdminService;
import com.zimbra.soap.admin.wsimport.generated.Account;
import com.zimbra.soap.admin.wsimport.generated.AccountInfo;
import com.zimbra.soap.admin.wsimport.generated.Attr;
import com.zimbra.soap.admin.wsimport.generated.By;
import com.zimbra.soap.admin.wsimport.generated.ReloadLocalConfigRequest;
import com.zimbra.soap.admin.wsimport.generated.ReloadLocalConfigResponse;
import com.zimbra.soap.admin.wsimport.generated.GetAccountRequest;
import com.zimbra.soap.admin.wsimport.generated.GetAccountResponse;

import com.zimbra.soap.Utility;

import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLAccountTest {

    // The AdminService interface is the Java type bound to
    // the portType section of the WSDL document.
    private static AdminService eif;
    private static String authToken;

    @BeforeClass
    public static void init() throws Exception {
        Utility.setUpToAcceptAllHttpsServerCerts();
        eif = Utility.getAdminSvcEIF();
        authToken = Utility.getAdminServiceAuthToken();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void reloadLocalConfigTest() throws Exception {
       Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
       ReloadLocalConfigRequest req = new ReloadLocalConfigRequest();
       ReloadLocalConfigResponse resp = eif.reloadLocalConfigRequest(req);
       Assert.assertNotNull(resp);
    }

    @Test
    public void simpleGetAccountTest() throws Exception {
       GetAccountRequest req = new GetAccountRequest();
       Account acct = new Account();
       acct.setBy(By.NAME);
       acct.setValue("user1");
       req.setAccount(acct);
       Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
       GetAccountResponse resp = eif.getAccountRequest(req);
       Assert.assertNotNull(resp);
       AccountInfo acctInfo = resp.getAccount();
       Assert.assertNotNull(acctInfo);
       // assertEquals(java.lang.String message, java.lang.Object expected, java.lang.Object actual) 
       Assert.assertTrue("value of <account> 'name' attribute should start with 'user1@'", acctInfo.getName().startsWith("user1@"));
       int len = acctInfo.getId().length();
       Assert.assertTrue("length of <account> 'id' attribute length is " + len + " - should be longer than 10", len > 10);
       len = acctInfo.getA().size();
       Assert.assertTrue("<account> has " + len + " <a> children - should have at least 12", len >= 12);
    }

    @Test
    public void foreignPrincGetAccountTest() throws Exception {
       GetAccountRequest req = new GetAccountRequest();
       Account acct = new Account();
       acct.setBy(By.NAME);
       acct.setValue("user1");
       req.setAccount(acct);
       req.setAttrs("zimbraForeignPrincipal");
       Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
       GetAccountResponse resp = eif.getAccountRequest(req);
       Assert.assertNotNull(resp);
       AccountInfo acctInfo = resp.getAccount();
       Assert.assertNotNull(acctInfo);
       // assertEquals(java.lang.String message, java.lang.Object expected, java.lang.Object actual) 
       Assert.assertTrue("value of <account> 'name' attribute should start with 'user1@'", acctInfo.getName().startsWith("user1@"));
       int len = acctInfo.getId().length();
       Assert.assertTrue("length of <account> 'id' attribute length is " + len + " - should be longer than 10", len > 10);
       List <Attr> attrs = acctInfo.getA();
       len = attrs.size();
       Assert.assertTrue("<account> has " + len + " <a> children - should have only 1", len == 1);
       Assert.assertEquals("'n' attribute of <a> -", "zimbraForeignPrincipal", attrs.get(0).getN());
    }
}
