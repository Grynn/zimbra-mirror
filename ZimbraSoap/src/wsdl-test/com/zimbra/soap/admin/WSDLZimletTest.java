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

import com.zimbra.soap.admin.wsimport.generated.*;

import com.zimbra.soap.Utility;

import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLZimletTest {

    // The AdminService interface is the Java type bound to
    // the portType section of the WSDL document.
    private final static String testAcctDomain = "wsdl.zimlets.example.test";
    private final static String testAcct = "wsdl1@" + testAcctDomain;
    private static AdminService eif = null;

    @BeforeClass
    public static void init() throws Exception {
        Utility.setUpToAcceptAllHttpsServerCerts();
        eif = Utility.getAdminSvcEIF();
        oneTimeTearDown();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
        try {
            // Utility.deleteAccountIfExists(testAcct);
            // Utility.deleteDomainIfExists(testAcctDomain);
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

    @Test
    public void getAllZimletsTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetAllZimletsRequest req = new GetAllZimletsRequest();
        req.setExclude("None");
        GetAllZimletsResponse resp = eif.getAllZimletsRequest(req);
        Assert.assertNotNull("GetAllZimletsResponse object", resp);
        List<ZimletInfo> zimlets = resp.getZimlet();
        Assert.assertNotNull("zimlets list object", zimlets);
        int zNum = zimlets.size();
        Assert.assertTrue("Number of zimlets=" + zNum +
                "is at least 4", zNum >= 4);
        int cnt = 0;
        for (ZimletInfo zimlet : zimlets) {
            cnt++;
            String tag = "zimlet " + cnt;
            Assert.assertNotNull(tag + " id", zimlet.getId());
            Assert.assertNotNull(tag + " name", zimlet.getName());
            zimlet.getHasKeyword();  // Not required
            int aCnt = 0;
            for (Attr attr : zimlet.getA()) {
                aCnt++;
                String aTag = tag + " attr " + aCnt;
                Assert.assertNotNull(aTag + " name", attr.getN());
                Assert.assertNotNull(aTag + " value", attr.getValue());
            }
        }
    }

    @Test
    public void getZimletTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetZimletRequest req = new GetZimletRequest();
        NamedElement ne = new NamedElement();
        ne.setName("com_zimbra_url");
        req.setZimlet(ne);
        GetZimletResponse resp = eif.getZimletRequest(req);
        Assert.assertNotNull("GetZimletResponse object", resp);
        ZimletInfo zimlet = resp.getZimlet();
        String tag = "zimlet";
        Assert.assertNotNull(tag + " id", zimlet.getId());
        Assert.assertNotNull(tag + " name", zimlet.getName());
        zimlet.getHasKeyword();  // Not required
        int aCnt = 0;
        for (Attr attr : zimlet.getA()) {
            aCnt++;
            String aTag = tag + " attr " + aCnt;
            Assert.assertNotNull(aTag + " name", attr.getN());
            Assert.assertNotNull(aTag + " value", attr.getValue());
        }
    }

    @Test
    public void getZimletStatusTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetZimletStatusRequest req = new GetZimletStatusRequest();
        GetZimletStatusResponse resp = eif.getZimletStatusRequest(req);
        Assert.assertNotNull("GetZimletStatusResponse object", resp);
        ZimletStatusParent parent = resp.getZimlets();
        int zNum = parent.getZimlet().size();
        Assert.assertTrue("Number of zimlets=" + zNum +
                "is at least 4", zNum >= 4);
        int zCnt = 0;
        for (ZimletStatus zimlet : parent.getZimlet()) {
            zCnt++;
            String zTag = "zimlet " + zCnt;
            Assert.assertNotNull(zTag + " name", zimlet.getName());
            Assert.assertTrue(zTag + " priority >= 0",
                    zimlet.getPriority() >= 0);
            Assert.assertEquals(zTag + " status", ZimletStatusSetting.ENABLED,
                    zimlet.getStatus());
            Assert.assertFalse(zTag + " extension setting",
                    zimlet.isExtension());
        }
        List<ZimletStatusCos> coses = resp.getCos();
        zNum = coses.size();
        Assert.assertTrue("Number of zimlets=" + zNum +
                "is at least 1", zNum >= 1);
        int cCnt = 0;
        for (ZimletStatusCos cos : coses) {
            cCnt++;
            String cTag = "cos " + cCnt;
            Assert.assertNotNull(cTag + " name", cos.getName());
            zCnt = 0;
            for (ZimletStatus zimlet : cos.getZimlet()) {
                zCnt++;
                String zTag = cTag + " zimlet " + zCnt;
                Assert.assertNotNull(zTag + " name", zimlet.getName());
                zimlet.getPriority();  // probably null
                Assert.assertEquals(zTag + " status",
                        ZimletStatusSetting.ENABLED, zimlet.getStatus());
                Assert.assertFalse(zTag + " extension setting",
                        zimlet.isExtension());
            }
        }
    }

    // TODO: Not yet implemented the Jaxb classes for this
    // @Test
    public void getAdminExtensionZimletsTestNOT() throws Exception {
    }
}
