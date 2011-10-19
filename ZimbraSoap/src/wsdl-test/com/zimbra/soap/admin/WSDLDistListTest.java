/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011 Zimbra, Inc.
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

import generated.zcsclient.admin.*;
import generated.zcsclient.ws.service.ZcsAdminPortType;

import com.zimbra.soap.Utility;

import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLDistListTest {

    private final static String testDlDomain = "wsdl.dl.domain.example.test";
    private final static String testAcct = "wsdl_acct1@" + testDlDomain;
    private final static String testDl = "wsdl_dl1@" + testDlDomain;
    private final static String parentDl = "wsdl_dlparent@" + testDlDomain;
    private static ZcsAdminPortType eif = null;

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
            Utility.deleteDistributionListIfExists(testDl);
            Utility.deleteDistributionListIfExists(parentDl);
            Utility.deleteDistributionListIfExists("foobar" + testDl);
            Utility.deleteAccountIfExists(testAcct);
            Utility.deleteDomainIfExists(testDlDomain);
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
    public void dlAliasTest() throws Exception {
        String testDistributionListId = Utility.ensureDistributionListExists(testDl);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testAddDistributionListAliasRequest req = new testAddDistributionListAliasRequest();
        req.setId(testDistributionListId);
        req.setAlias("alias1@" + testDlDomain);
        testAddDistributionListAliasResponse resp = eif.addDistributionListAliasRequest(req);
        Assert.assertNotNull("AddDistributionListAliasResponse object", resp);
        testRemoveDistributionListAliasRequest removeReq =
            new testRemoveDistributionListAliasRequest();
        removeReq.setId(testDistributionListId);
        removeReq.setAlias("alias1@" + testDlDomain);
        testRemoveDistributionListAliasResponse removeResp =
            eif.removeDistributionListAliasRequest(removeReq);
        Assert.assertNotNull("RemoveDistributionListAliasResponse object",
                removeResp);
    }

    @Test
    public void dlMemberTest() throws Exception {
        String testDistributionListId = Utility.ensureDistributionListExists(testDl);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testAddDistributionListMemberRequest req = new testAddDistributionListMemberRequest();
        req.setId(testDistributionListId);
        req.getDlm().add("fun@example.com");
        testAddDistributionListMemberResponse resp = eif.addDistributionListMemberRequest(req);
        Assert.assertNotNull("AddDistributionListMemberResponse object", resp);
        testRemoveDistributionListMemberRequest removeReq =
            new testRemoveDistributionListMemberRequest();
        removeReq.setId(testDistributionListId);
        removeReq.getDlm().add("fun@example.com");
        testRemoveDistributionListMemberResponse removeResp =
            eif.removeDistributionListMemberRequest(removeReq);
        Assert.assertNotNull("RemoveDistributionListMemberResponse object",
                removeResp);
    }

    @Test
    public void getDistributionListByIdTest() throws Exception {
        String testDistributionListId = Utility.ensureDistributionListExists(testDl);
        testGetDistributionListRequest req = new testGetDistributionListRequest();
        testDistributionListSelector dl = new testDistributionListSelector();
        dl.setBy(testDistributionListBy.ID);
        dl.setValue(testDistributionListId);
        req.setDl(dl);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetDistributionListResponse resp = eif.getDistributionListRequest(req);
        Assert.assertNotNull("GetDistributionListResponse object", resp);
        testDistributionListInfo dlInfo = resp.getDl();
        Assert.assertNotNull("DistributionListInfo object", dlInfo);
        Assert.assertEquals("DL name", testDl, dlInfo.getName());
        Assert.assertEquals("DL id", testDistributionListId, dlInfo.getId());
        int len = dlInfo.getA().size();
        Assert.assertTrue("<dl> has " + len +
                " <a> children - should have at least 8", len >= 8);
    }

    @Test
    public void getDistributionListByNameTest() throws Exception {
        String testDistributionListId = Utility.ensureDistributionListExists(testDl);
        testGetDistributionListRequest req = new testGetDistributionListRequest();
        testDistributionListSelector dl = new testDistributionListSelector();
        dl.setBy(testDistributionListBy.NAME);
        dl.setValue(testDl);
        req.setDl(dl);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetDistributionListResponse resp = eif.getDistributionListRequest(req);
        Assert.assertNotNull("GetDistributionListResponse object", resp);
        testDistributionListInfo dlInfo = resp.getDl();
        Assert.assertNotNull("DistributionListInfo object", dlInfo);
        Assert.assertEquals("DL name", testDl, dlInfo.getName());
        Assert.assertEquals("DL id", testDistributionListId, dlInfo.getId());
    }

    @Test
    public void createDistributionListTest() throws Exception {
        int len;
        Utility.deleteDistributionListIfExists(testDl);
        Utility.ensureDomainExists(testDlDomain);
        testCreateDistributionListRequest createAcctReq =
            new testCreateDistributionListRequest();
        createAcctReq.setName(testDl);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testCreateDistributionListResponse resp =
            eif.createDistributionListRequest(createAcctReq);
        Assert.assertNotNull("CreateDistributionListResponse object", resp);
        testDistributionListInfo dlInfo = resp.getDl();
        Assert.assertNotNull("DistributionListInfo object", dlInfo);
        Assert.assertEquals("createDistributionListResponse <dl> 'name' attribute",
                testDl, dlInfo.getName());
        String testDistributionListId = dlInfo.getId();
        len = testDistributionListId.length();
        Assert.assertTrue(
                "length of Response <dl> 'id' attribute length is " +
                len + " - should be longer than 10", len > 10);
        len = dlInfo.getA().size();
        Assert.assertTrue("CreateDistributionListResponse <dl> has " +
                len + " <a> children - should have at least 8", len >= 8);
    }

    @Test
    public void modifyDistributionListTest() throws Exception {
        int len;
        String testDistributionListId = Utility.ensureDistributionListExists(testDl);
        String respId;
        testModifyDistributionListRequest modReq = new testModifyDistributionListRequest();
        modReq.setId(testDistributionListId);
        testAttr modAttr = new testAttr();
        modAttr.setN("displayName");
        modAttr.setValue("Modified Displayname");
        modReq.getA().add(modAttr);
        testModifyDistributionListResponse modResp = eif.modifyDistributionListRequest(modReq);
        Assert.assertNotNull("ModifyDistributionListResponse object", modResp);
        testDistributionListInfo dlInfo = modResp.getDl();
        Assert.assertNotNull("DistributionListInfo object", dlInfo);
        Assert.assertEquals("modifyDistributionListResponse <dl> 'name' attribute", 
                testDl, dlInfo.getName());
        respId = dlInfo.getId();
        Assert.assertEquals("modifyDistributionListResponse <dl> 'id' attribute",
                testDistributionListId, respId);
        len = dlInfo.getA().size();
        Assert.assertTrue("modifyDistributionListResponse <dl> has " + len +
                " <a> children - should have at least 10", len >= 10);
    }

    @Test
    public void renameDistributionListTest() throws Exception {
        int len;
        String testDistributionListId = Utility.ensureDistributionListExists(testDl);
        String respId;
        testRenameDistributionListRequest renameDistributionListReq = new testRenameDistributionListRequest();
        renameDistributionListReq.setId(testDistributionListId);
        renameDistributionListReq.setNewName("foobar" + testDl);
        testRenameDistributionListResponse renameDistributionListResp = eif.renameDistributionListRequest(renameDistributionListReq);
        Assert.assertNotNull(renameDistributionListResp);
        testDistributionListInfo dlInfo = renameDistributionListResp.getDl();
        Assert.assertNotNull(dlInfo);
        Assert.assertEquals("renameDistributionListResponse <dl> 'name' attribute",
                "foobar" + testDl, dlInfo.getName());
        respId = dlInfo.getId();
        Assert.assertEquals("renameDistributionListResponse <dl> 'id' attribute",
                testDistributionListId, respId);
        len = dlInfo.getA().size();
        Assert.assertTrue("renameDistributionListResponse <dl> has " + len +
                " <a> children - should have at least 10", len >= 10);
        Utility.deleteDistributionListIfExists("foobar" + testDl);
    }

    @Test
    public void getDlMembershipTest() throws Exception {
        // For a useful test, we need a DL which is a member of another DL
        int len;
        Utility.ensureDistributionListExists(testDl);
        String parentDlId = Utility.ensureDistributionListExists(parentDl);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testAddDistributionListMemberRequest req =
            new testAddDistributionListMemberRequest();
        req.setId(parentDlId);
        req.getDlm().add(testDl);
        testAddDistributionListMemberResponse resp =
            eif.addDistributionListMemberRequest(req);
        Assert.assertNotNull("AddDistributionListMemberResponse object", resp);
        testGetDistributionListMembershipRequest membershipReq =
            new testGetDistributionListMembershipRequest();
        testDistributionListSelector adminAcct = new testDistributionListSelector();
        adminAcct.setBy(testDistributionListBy.NAME);
        adminAcct.setValue(testDl);
        membershipReq.setDl(adminAcct);
        testGetDistributionListMembershipResponse dlMembershipResponse =
            eif.getDistributionListMembershipRequest(membershipReq);
        Assert.assertNotNull("GetDistributionListMembershipResponse object",
                dlMembershipResponse);
        len = dlMembershipResponse.getDl().size();
        Assert.assertEquals("Number of dls this dl is a member of", 1, len);
        int pNum = 0;
        for (testDistributionListMembershipInfo parentInfo :
                    dlMembershipResponse.getDl()) {
            pNum++;
            String pTag = "containing DL" + pNum;
            Assert.assertEquals(pTag + " id", parentDlId, parentInfo.getId());
            Assert.assertEquals(pTag + " name", parentDl, parentInfo.getName());
            Assert.assertNull(pTag + " via", parentInfo.getVia());
        }
    }

    @Test
    public void getAllDistributionListsTest() throws Exception {
        testGetAllDistributionListsRequest req = new testGetAllDistributionListsRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetAllDistributionListsResponse resp = eif.getAllDistributionListsRequest(req);
        Assert.assertNotNull("GetAllDistributionListsResponse object", resp);
        List <testDistributionListInfo> dlInfoList = resp.getDl();
        int len;
        Assert.assertNotNull("GetAllDistributionListsResponse list of DistributionLists", dlInfoList);
        len = dlInfoList.size();
        Assert.assertTrue("Number of GetAllDistributionListsResponse <dl> children is " +
                len + " - should be at least 2", len >= 2);
    }

    @Test
    public void deleteDistributionListTest() throws Exception {
        String testDistributionListId = Utility.ensureDistributionListExists(testDl);
        testDeleteDistributionListRequest delReq = new testDeleteDistributionListRequest();
        delReq.setId(testDistributionListId);
        testDeleteDistributionListResponse delResp = eif.deleteDistributionListRequest(delReq);
        Assert.assertNotNull(delResp);
    }
}
