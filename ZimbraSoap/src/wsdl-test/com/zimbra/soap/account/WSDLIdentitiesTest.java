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
package com.zimbra.soap.account;
import java.util.List;

import com.sun.xml.ws.developer.WSBindingProvider;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zimbra.soap.Utility;
import generated.zcsclient.account.testAttr;
import generated.zcsclient.account.testCreateIdentityRequest;
import generated.zcsclient.account.testCreateIdentityResponse;
import generated.zcsclient.account.testDeleteIdentityRequest;
import generated.zcsclient.account.testDeleteIdentityResponse;
import generated.zcsclient.account.testGetIdentitiesRequest;
import generated.zcsclient.account.testGetIdentitiesResponse;
import generated.zcsclient.account.testIdentity;
import generated.zcsclient.account.testModifyIdentityRequest;
import generated.zcsclient.account.testModifyIdentityResponse;
import generated.zcsclient.account.testNameId;
import generated.zcsclient.ws.service.ZcsPortType;

public class WSDLIdentitiesTest {

    private static ZcsPortType acctSvcEIF;

    private final static String testAcctDomain = "wsdl.acct.domain.example.test";
    private final static String testAcct = "wsdl1@" + testAcctDomain;
    private final static String altId = "alternateId";
    private final static String testAcctAltEmail = "altwsdl1@" + testAcctDomain;

    @BeforeClass
    public static void init() throws Exception {
        Utility.setUpToAcceptAllHttpsServerCerts();
        acctSvcEIF = Utility.getZcsSvcEIF();
        oneTimeTearDown();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
        try {
            Utility.deleteAccountIfExists(testAcct);
            Utility.deleteAccountIfExists(testAcctAltEmail);
            Utility.deleteDomainIfExists(testAcctDomain);
        } catch (Exception ex) {
            System.err.println("Exception " + ex.toString() + 
            " thrown inside oneTimeTearDown");
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private void checkIdentity(testIdentity ident, String tag, String name) {
        Assert.assertNotNull(tag + " id", ident.getId());
        if (name == null)
            Assert.assertNotNull(tag + " name", ident.getName());
        else
            Assert.assertEquals(tag + " name", "DEFAULT", ident.getName());
        List<testAttr> attrs = ident.getA();
        Assert.assertNotNull(tag + " attrs", attrs);
        Assert.assertTrue(tag + " Number of attrs=" + attrs.size() +
                " > 4", attrs.size() > 4);
        int aNum = 0;
        for (testAttr attr : attrs) {
            aNum++;
            String aTag = tag + " attr " + aNum;
            Assert.assertNotNull(aTag + " name", attr.getName());
            Assert.assertNotNull(aTag + " value", attr.getValue());
        }
    }

    private void checkIdentities(String tag, int numExpected)
    throws Exception {
        testGetIdentitiesRequest req = new testGetIdentitiesRequest();
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)acctSvcEIF,
                testAcct);
        testGetIdentitiesResponse resp = acctSvcEIF.getIdentitiesRequest(req);
        Assert.assertNotNull(tag + ":GetIdentitiesResponse", resp);
        List<testIdentity> identities = resp.getIdentity();
        Assert.assertNotNull(tag + ":identities", identities);
        Assert.assertEquals(tag + ":Number of identities",
                numExpected, identities.size());
        int num = 0;
        for (testIdentity ident : identities) {
            num++;
            String identTag = tag + ":identity " + num;
            checkIdentity(ident, identTag, null);
        }
    }

    @Test
    public void getIdentitiesTest() throws Exception {
        Utility.ensureAccountExists(testAcct);
        testGetIdentitiesRequest req = new testGetIdentitiesRequest();
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)acctSvcEIF,
                testAcct);
        testGetIdentitiesResponse resp = acctSvcEIF.getIdentitiesRequest(req);
        Assert.assertNotNull("GetIdentitiesResponse", resp);
        List<testIdentity> identities = resp.getIdentity();
        Assert.assertNotNull("identities", identities);
        Assert.assertEquals("Number of identities", 1, identities.size());
        int num = 0;
        for (testIdentity ident : identities) {
            num++;
            String tag = "identity " + num;
            checkIdentity(ident, tag, "DEFAULT");
        }
    }

    @Test
    public void identityTest() throws Exception {
        Utility.ensureAccountExists(testAcct);
        testCreateIdentityRequest req = new testCreateIdentityRequest();
        testIdentity newIdentity = new testIdentity();
        newIdentity.setName(altId);
        testAttr prefFromAddr = new testAttr();
        prefFromAddr.setName("zimbraPrefFromAddress");
        prefFromAddr.setValue(testAcctAltEmail);
        newIdentity.getA().add(prefFromAddr);
        req.setIdentity(newIdentity);
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)acctSvcEIF,
                testAcct);
        testCreateIdentityResponse resp = acctSvcEIF.createIdentityRequest(req);
        Assert.assertNotNull("CreateIdentityResponse", resp);
        testIdentity ident = resp.getIdentity();
        checkIdentity(ident, "identity", null);
        checkIdentities("After Create", 2);
        testModifyIdentityRequest modReq = new testModifyIdentityRequest();
        testIdentity modIdentity = new testIdentity();
        modIdentity.setName(altId);
        testAttr prefAttr = new testAttr();
        prefAttr.setName("zimbraPrefSaveToSent");
        prefAttr.setValue("FALSE");
        modIdentity.getA().add(prefAttr);
        modReq.setIdentity(modIdentity);
        testModifyIdentityResponse modResp = acctSvcEIF.modifyIdentityRequest(modReq);
        Assert.assertNotNull("ModifyIdentityResponse", modResp);
        checkIdentities("After Modify", 2);
        testDeleteIdentityRequest delReq = new testDeleteIdentityRequest();
        testNameId delNameId = new testNameId();
        delNameId.setId(ident.getId());
        delReq.setIdentity(delNameId);
        testDeleteIdentityResponse delResp = acctSvcEIF.deleteIdentityRequest(delReq);
        Assert.assertNotNull("DeleteIdentityResponse", delResp);
        checkIdentities("After Delete", 1);
    }
}
