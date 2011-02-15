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

public class WSDLCalResourceTest {

    // The AdminService interface is the Java type bound to
    // the portType section of the WSDL document.
    private final static String testCalResDomain = "wsdl.calr.domain.example.test";
    private final static String testCalRes = "wsdl1@" + testCalResDomain;
    private final static String testCalResDisplayName = "WSDL Test CalResource";
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
            Utility.deleteCalendarResourceIfExists(testCalRes);
            Utility.deleteCalendarResourceIfExists("foobar" + testCalRes);
            Utility.deleteDomainIfExists(testCalResDomain);
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
    public void createCalendarResourceTest() throws Exception {
        int len;
        Utility.deleteCalendarResourceIfExists(testCalRes);
        Utility.ensureDomainExists(testCalResDomain);
        CreateCalendarResourceRequest createReq =
                new CreateCalendarResourceRequest();
        createReq.setName(testCalRes);
        createReq.setPassword("test123");
        createReq.getA().add(Utility.mkAttr("displayName",
                "WSDL Test Cal Resource"));
        createReq.getA().add(Utility.mkAttr("zimbraCalResType", "Location"));
        createReq.getA().add(Utility.mkAttr(
                "zimbraCalResLocationDisplayName", "Harare"));
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        CreateCalendarResourceResponse resp =
                eif.createCalendarResourceRequest(createReq);
        Assert.assertNotNull("CreateCalendarResourceResponse object", resp);
        CalendarResourceInfo calResInfo = resp.getCalresource();
        Assert.assertNotNull("CalendarResourceInfo object", calResInfo);
        Assert.assertEquals(
                "createCalendarResourceResponse <calResource> 'name' attribute",
                testCalRes, calResInfo.getName());
        String testCalendarResourceId = calResInfo.getId();
        len = testCalendarResourceId.length();
        Assert.assertTrue(
                "length of <calResource> 'id' attribute length is " +
                len + " - should be longer than 10", len > 10);
        len = calResInfo.getA().size();
        Assert.assertTrue("CreateCalendarResourceResponse <calResource> has " +
                len + " <a> children - should have at least 12", len >= 12);
    }

    @Test
    public void getCalendarResourceTest() throws Exception {
        int len;
        String testCalendarResourceId = Utility.ensureCalendarResourceExists(
                testCalRes, testCalResDisplayName);
        GetCalendarResourceRequest getReq = new GetCalendarResourceRequest();
        CalendarResourceSelector calResSel = new CalendarResourceSelector();
        calResSel.setBy(CalendarResourceBy.ID);
        calResSel.setValue(testCalendarResourceId);
        getReq.setCalresource(calResSel);
        GetCalendarResourceResponse getResp = eif.getCalendarResourceRequest(getReq);
        Assert.assertNotNull("GetCalendarResourceResponse object", getResp);
        CalendarResourceInfo calResInfo = getResp.getCalresource();
        Assert.assertNotNull("CalendarResourceInfo object", calResInfo);
        Assert.assertEquals("getCalendarResourceResponse <calResource> 'name' attribute",
                testCalRes, calResInfo.getName());
        String respId = calResInfo.getId();
        Assert.assertEquals(
                "getCalendarResourceResponse <calResource> 'id' attribute",
                testCalendarResourceId, respId);
        len = calResInfo.getA().size();
        Assert.assertTrue("GetCalendarResourceResponse <calResource> has " + len +
                " <a> children - should have at least 12", len >= 12);
    }

    @Test
    public void modifyCalendarResourceTest() throws Exception {
        int len;
        String testCalendarResourceId = Utility.ensureCalendarResourceExists(
                testCalRes, testCalResDisplayName);
        String respId;
        ModifyCalendarResourceRequest modReq = new ModifyCalendarResourceRequest();
        modReq.setId(testCalendarResourceId);
        Attr modAttr = new Attr();
        modAttr.setN("displayName");
        modAttr.setValue("Modified Displayname");
        modReq.getA().add(modAttr);
        ModifyCalendarResourceResponse modResp = eif.modifyCalendarResourceRequest(modReq);
        Assert.assertNotNull("ModifyCalendarResourceResponse object", modResp);
        CalendarResourceInfo calResInfo = modResp.getCalresource();
        Assert.assertNotNull("CalendarResourceInfo object", calResInfo);
        Assert.assertEquals("modifyCalendarResourceResponse <calResource> 'name' attribute", 
                testCalRes, calResInfo.getName());
        respId = calResInfo.getId();
        Assert.assertEquals("modifyCalendarResourceResponse <calResource> 'id' attribute",
                testCalendarResourceId, respId);
        len = calResInfo.getA().size();
        Assert.assertTrue("modifyCalendarResourceResponse <calResource> has " + len +
                " <a> children - should have at least 50", len >= 50);
    }

    @Test
    public void renameCalendarResourceTest() throws Exception {
        int len;
        String testCalendarResourceId = Utility.ensureCalendarResourceExists(
                testCalRes, testCalResDisplayName);
        String respId;
        RenameCalendarResourceRequest renameCalendarResourceReq =
                new RenameCalendarResourceRequest();
        renameCalendarResourceReq.setId(testCalendarResourceId);
        renameCalendarResourceReq.setNewName("foobar" + testCalRes);
        RenameCalendarResourceResponse renameCalendarResourceResp =
                eif.renameCalendarResourceRequest(renameCalendarResourceReq);
        Assert.assertNotNull(renameCalendarResourceResp);
        CalendarResourceInfo calResInfo = renameCalendarResourceResp.getCalresource();
        Assert.assertNotNull(calResInfo);
        Assert.assertEquals("renameCalendarResourceResponse <calResource> 'name' attribute",
                "foobar" + testCalRes, calResInfo.getName());
        respId = calResInfo.getId();
        Assert.assertEquals("renameCalendarResourceResponse <calResource> 'id' attribute",
                testCalendarResourceId, respId);
        len = calResInfo.getA().size();
        Assert.assertTrue("renameCalendarResourceResponse <calResource> has " + len +
                " <a> children - should have at least 50", len >= 50);
        Utility.deleteCalendarResourceIfExists("foobar" + testCalRes);
    }

    @Test
    public void getAllCalendarResourcesTest() throws Exception {
        GetAllCalendarResourcesRequest req = new GetAllCalendarResourcesRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetAllCalendarResourcesResponse resp = eif.getAllCalendarResourcesRequest(req);
        Assert.assertNotNull("GetAllCalendarResourcesResponse object", resp);
        List <CalendarResourceInfo> calResources = resp.getCalresource();
        int len;
        Assert.assertNotNull(
                "GetAllCalendarResourcesResponse list of CalendarResources", calResources);
        len = calResources.size();
        Assert.assertTrue(
                "Number of GetAllCalendarResourcesResponse <calResource> children is " +
                len + " - should be at least 2", len >= 2);
    }

    @Test
    public void searchCalendarResourcesTest() throws Exception {
        String testCalendarResourceId = Utility.ensureCalendarResourceExists(
                testCalRes, testCalResDisplayName);
        SearchCalendarResourcesRequest req = new SearchCalendarResourcesRequest();
        req.setApplyCos(false);
        req.setAttrs("displayName,zimbraId");
        req.setDomain(testCalResDomain);
        req.setLimit(200);
        req.setSortAscending(true);
        EntrySearchFilterInfo filter = new EntrySearchFilterInfo();
        EntrySearchFilterSingleCond cond = new EntrySearchFilterSingleCond();
        cond.setNot(false);
        cond.setAttr("displayName");
        cond.setOp("startswith");
        cond.setValue(testCalResDisplayName);
        filter.setCond(cond);
        req.setSearchFilter(filter);
        SearchCalendarResourcesResponse resp =
            eif.searchCalendarResourcesRequest(req);
        Assert.assertNotNull(resp);
        Assert.assertEquals("Total found", 1, resp.getSearchTotal());
        Assert.assertEquals("is more", false, resp.isMore());
        List <CalendarResourceInfo> calResources = resp.getCalresource();
        int len;
        Assert.assertNotNull("list of CalendarResources", calResources);
        len = calResources.size();
        Assert.assertEquals("number of CalendarResources", 1, len);
        Assert.assertEquals("CalendarResource id", testCalendarResourceId,
                calResources.get(0).getId());
    }

    @Test
    public void deleteCalendarResourceTest() throws Exception {
        String testCalendarResourceId = Utility.ensureCalendarResourceExists(
                testCalRes, testCalResDisplayName);
        DeleteCalendarResourceRequest delReq = new DeleteCalendarResourceRequest();
        delReq.setId(testCalendarResourceId);
        DeleteCalendarResourceResponse delResp = eif.deleteCalendarResourceRequest(delReq);
        Assert.assertNotNull(delResp);
    }
}
