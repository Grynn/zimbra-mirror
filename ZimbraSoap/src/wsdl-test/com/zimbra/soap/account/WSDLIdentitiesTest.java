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
import com.zimbra.soap.account.wsimport.generated.AccountService;
import com.zimbra.soap.account.wsimport.generated.Attr;
import com.zimbra.soap.account.wsimport.generated.CreateIdentityRequest;
import com.zimbra.soap.account.wsimport.generated.CreateIdentityResponse;
import com.zimbra.soap.account.wsimport.generated.DeleteIdentityRequest;
import com.zimbra.soap.account.wsimport.generated.DeleteIdentityResponse;
import com.zimbra.soap.account.wsimport.generated.GetIdentitiesRequest;
import com.zimbra.soap.account.wsimport.generated.GetIdentitiesResponse;
import com.zimbra.soap.account.wsimport.generated.Identity;
import com.zimbra.soap.account.wsimport.generated.ModifyIdentityRequest;
import com.zimbra.soap.account.wsimport.generated.ModifyIdentityResponse;
import com.zimbra.soap.account.wsimport.generated.NameId;
import com.zimbra.soap.account.wsimport.generated.NewIdentity;
import com.zimbra.soap.admin.wsimport.generated.AdminService;

public class WSDLIdentitiesTest {

    private static AccountService acctSvcEIF;
    private static AdminService adminSvcEIF = null;

    private final static String testAcctDomain = "wsdl.acct.domain.example.test";
    private final static String testAcct = "wsdl1@" + testAcctDomain;
    private final static String altId = "alternateId";
    private final static String testAcctAltEmail = "altwsdl1@" + testAcctDomain;

    @BeforeClass
    public static void init() throws Exception {
        Utility.setUpToAcceptAllHttpsServerCerts();
        adminSvcEIF = Utility.getAdminSvcEIF();
        acctSvcEIF = Utility.getAcctSvcEIF();
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

    private void checkIdentity(Identity ident, String tag, String name) {
        Assert.assertNotNull(tag + " id", ident.getId());
        if (name == null)
            Assert.assertNotNull(tag + " name", ident.getName());
        else
            Assert.assertEquals(tag + " name", "DEFAULT", ident.getName());
        List<Attr> attrs = ident.getA();
        Assert.assertNotNull(tag + " attrs", attrs);
        Assert.assertTrue(tag + " Number of attrs=" + attrs.size() +
                " > 4", attrs.size() > 4);
        int aNum = 0;
        for (Attr attr : attrs) {
            aNum++;
            String aTag = tag + " attr " + aNum;
            Assert.assertNotNull(aTag + " name", attr.getName());
            Assert.assertNotNull(aTag + " value", attr.getValue());
        }
    }

    private void checkIdentities(String tag, int numExpected)
    throws Exception {
        GetIdentitiesRequest req = new GetIdentitiesRequest();
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)acctSvcEIF,
                testAcct);
        GetIdentitiesResponse resp = acctSvcEIF.getIdentitiesRequest(req);
        Assert.assertNotNull(tag + ":GetIdentitiesResponse", resp);
        List<Identity> identities = resp.getIdentity();
        Assert.assertNotNull(tag + ":identities", identities);
        Assert.assertEquals(tag + ":Number of identities",
                numExpected, identities.size());
        int num = 0;
        for (Identity ident : identities) {
            num++;
            String identTag = tag + ":identity " + num;
            checkIdentity(ident, identTag, null);
        }
    }

    @Test
    public void getIdentitiesTest() throws Exception {
        Utility.ensureAccountExists(testAcct);
        GetIdentitiesRequest req = new GetIdentitiesRequest();
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)acctSvcEIF,
                testAcct);
        GetIdentitiesResponse resp = acctSvcEIF.getIdentitiesRequest(req);
        Assert.assertNotNull("GetIdentitiesResponse", resp);
        List<Identity> identities = resp.getIdentity();
        Assert.assertNotNull("identities", identities);
        Assert.assertEquals("Number of identities", 1, identities.size());
        int num = 0;
        for (Identity ident : identities) {
            num++;
            String tag = "identity " + num;
            checkIdentity(ident, tag, "DEFAULT");
        }
    }

    @Test
    public void identityTest() throws Exception {
        Utility.ensureAccountExists(testAcct);
        CreateIdentityRequest req = new CreateIdentityRequest();
        NewIdentity newIdentity = new NewIdentity();
        newIdentity.setName(altId);
        Attr prefFromAddr = new Attr();
        prefFromAddr.setName("zimbraPrefFromAddress");
        prefFromAddr.setValue(testAcctAltEmail);
        newIdentity.getA().add(prefFromAddr);
        req.setIdentity(newIdentity);
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)acctSvcEIF,
                testAcct);
        CreateIdentityResponse resp = acctSvcEIF.createIdentityRequest(req);
        Assert.assertNotNull("CreateIdentityResponse", resp);
        Identity ident = resp.getIdentity();
        checkIdentity(ident, "identity", null);
        checkIdentities("After Create", 2);
        ModifyIdentityRequest modReq = new ModifyIdentityRequest();
        Identity modIdentity = new Identity();
        modIdentity.setName(altId);
        Attr prefAttr = new Attr();
        prefAttr.setName("zimbraPrefSaveToSent");
        prefAttr.setValue("FALSE");
        modIdentity.getA().add(prefAttr);
        modReq.setIdentity(modIdentity);
        ModifyIdentityResponse modResp = acctSvcEIF.modifyIdentityRequest(modReq);
        Assert.assertNotNull("ModifyIdentityResponse", modResp);
        checkIdentities("After Modify", 2);
        DeleteIdentityRequest delReq = new DeleteIdentityRequest();
        NameId delNameId = new NameId();
        delNameId.setId(ident.getId());
        delReq.setIdentity(delNameId);
        DeleteIdentityResponse delResp = acctSvcEIF.deleteIdentityRequest(delReq);
        Assert.assertNotNull("DeleteIdentityResponse", delResp);
        checkIdentities("After Delete", 1);
    }
}
