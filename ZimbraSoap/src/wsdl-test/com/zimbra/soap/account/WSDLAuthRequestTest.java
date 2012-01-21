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
package com.zimbra.soap.account;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zimbra.soap.Utility;
import generated.zcsclient.account.testAuthRequest;
import generated.zcsclient.account.testAuthResponse;
import generated.zcsclient.ws.service.ZcsPortType;
import generated.zcsclient.zm.testAccountBy;
import generated.zcsclient.zm.testAccountSelector;

public class WSDLAuthRequestTest {

    private static ZcsPortType eif;

    @BeforeClass
    public static void init() throws Exception {
        eif = Utility.getZcsSvcEIF();
    }

    /**
     * Current assumption : user1 exists with password test123
     */
    @Test
    public void simple() throws Exception {
        testAuthRequest authReq = new testAuthRequest();
        testAccountSelector acct = new testAccountSelector();
        acct.setBy(testAccountBy.NAME);
        acct.setValue("user1");
        authReq.setAccount(acct);
        authReq.setPassword("test123");
        authReq.setPreauth(null);
        authReq.setAuthToken(null);
        testAuthResponse authResponse = eif.authRequest(authReq);
        Assert.assertNotNull(authResponse);
        String authToken = authResponse.getAuthToken();
        Assert.assertTrue(authToken != null);
        Assert.assertTrue(authToken.length() > 10);
        long lifetime = authResponse.getLifetime();
        Assert.assertTrue(lifetime > 0);
        Assert.assertNull(authResponse.getRefer());
        Assert.assertEquals(authResponse.getSkin(), "serenity");  // If the default changes, this might change too?
    }

    /**
     * Current assumption : user1 exists with password test123
     */
    @Test
    public void badPasswd() throws Exception {
        try {
            Utility.getAccountServiceAuthToken("user1", "BAD-PASSWORD");
            Assert.fail("Should have had a fault resulting in an exception being thrown");
        } catch (SOAPFaultException sfe) {
            Assert.assertTrue(sfe.getMessage().startsWith("authentication failed for "));
        }
    }
}
