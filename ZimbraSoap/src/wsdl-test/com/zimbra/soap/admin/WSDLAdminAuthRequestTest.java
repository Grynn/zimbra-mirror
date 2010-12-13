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

import com.zimbra.soap.admin.wsimport.generated.AdminService;
import com.zimbra.soap.admin.wsimport.generated.Account;
import com.zimbra.soap.admin.wsimport.generated.Attr;
import com.zimbra.soap.admin.wsimport.generated.AuthRequest;
import com.zimbra.soap.admin.wsimport.generated.AuthResponse;
import com.zimbra.soap.admin.wsimport.generated.By;

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
       Account acct = new Account();
       acct.setBy(By.NAME);
       acct.setValue("admin");
       authReq.setAccount(acct);
       authReq.setPassword("test123");
       authReq.setAuthToken(null);
       // Invoke the methods.
       AuthResponse authResponse = eif.authRequest(authReq);
       Assert.assertNotNull(authResponse);
       String authToken = authResponse.getAuthToken();
       Assert.assertTrue(authToken != null);
       Assert.assertTrue(authToken.length() > 10);
       long lifetime = authResponse.getLifetime();
       Assert.assertTrue(lifetime > 0);
       Attr attr = authResponse.getA();
       System.out.println("[" + attr.getValue() + "]");
       System.out.println("[" + attr.getN() + "]");
       // assertEquals(java.lang.String message, java.lang.Object expected, java.lang.Object actual) 
       Assert.assertEquals("value of <a> -", "false", attr.getValue());
       Assert.assertEquals("'n' attribute of <a> -", "zimbraIsDomainAdminAccount", attr.getN());
       
    }

    @Test
    public void badPasswd() throws Exception {
       AuthRequest authReq = new AuthRequest();
       Account acct = new Account();
       acct.setBy(By.NAME);
       acct.setValue("admin");
       authReq.setAccount(acct);
       authReq.setPassword("BAD-ONE");
       authReq.setAuthToken(null);
       // Invoke the methods.
       try {
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
       Account acct = new Account();
       acct.setBy(By.NAME);
       acct.setValue("user1");
       authReq.setAccount(acct);
       authReq.setPassword("test123");
       authReq.setAuthToken(null);
       // Invoke the methods.
       try {
           AuthResponse authResponse = eif.authRequest(authReq);
           Assert.fail("Should have had a fault resulting in an exception being thrown");
       } catch (SOAPFaultException sfe) {
           Assert.assertEquals("SOAP fault message - ",
                   "permission denied: not an admin account",
                   sfe.getMessage());
       }
    }
}
