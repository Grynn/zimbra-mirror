/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 Zimbra, Inc.
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
package com.zimbra.soap.mail;

import java.util.List;

import com.sun.xml.ws.developer.WSBindingProvider;
import com.zimbra.soap.Utility;

import generated.zcsclient.mail.testActionGrantSelector;
import generated.zcsclient.mail.testCalEcho;
import generated.zcsclient.mail.testCalOrganizer;
import generated.zcsclient.mail.testCalendarAttendee;
import generated.zcsclient.mail.testCreateMountpointRequest;
import generated.zcsclient.mail.testCreateMountpointResponse;
import generated.zcsclient.mail.testFolderActionRequest;
import generated.zcsclient.mail.testFolderActionResponse;
import generated.zcsclient.mail.testFolderActionResult;
import generated.zcsclient.mail.testFolderActionSelector;
import generated.zcsclient.mail.testMountpoint;
import generated.zcsclient.mail.testMsg;
import generated.zcsclient.mail.testCreateAppointmentRequest;
import generated.zcsclient.mail.testCreateAppointmentResponse;
import generated.zcsclient.mail.testCreateTaskRequest;
import generated.zcsclient.mail.testCreateTaskResponse;
import generated.zcsclient.mail.testDtTimeInfo;
import generated.zcsclient.mail.testEmailAddrInfo;
import generated.zcsclient.mail.testInvitationInfo;
import generated.zcsclient.mail.testInviteAsMP;
import generated.zcsclient.mail.testInviteComponent;
import generated.zcsclient.mail.testMimePartInfo;
import generated.zcsclient.mail.testMpInviteInfo;
import generated.zcsclient.mail.testNewMountpointSpec;
import generated.zcsclient.ws.service.ZcsPortType;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLSharingTest {

    private static ZcsPortType mailSvcEIF = null;

    private final static String testAcctDomain = "wsdl.sharing.example.test";
    private final static String testAcct = "owner@" + testAcctDomain;
    private final static String testAcct2 = "accessor@" + testAcctDomain;

    @BeforeClass
    public static void init() throws Exception {
        Utility.setUpToAcceptAllHttpsServerCerts();
        mailSvcEIF = Utility.getZcsSvcEIF();
        oneTimeTearDown();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
        try {
            Utility.deleteAccountIfExists(testAcct);
            Utility.deleteAccountIfExists(testAcct2);
            Utility.deleteDomainIfExists(testAcctDomain);
        } catch (Exception ex) {
            System.err.println("Exception " + ex.toString() + " thrown inside oneTimeTearDown");
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void shareCalendarAndCreateMountpoint() throws Exception {
        Utility.ensureAccountExists(testAcct);
        Utility.ensureAccountExists(testAcct2);
        testFolderActionRequest faReq = new testFolderActionRequest();
        testFolderActionSelector action = new testFolderActionSelector();
        action.setId("10");
        action.setOp("grant");
        testActionGrantSelector grant = new testActionGrantSelector();
        grant.setD(testAcct2);
        grant.setGt("usr");
        grant.setPerm("r");
        action.setGrant(grant);
        faReq.setAction(action);
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)mailSvcEIF, testAcct);
        testFolderActionResponse faResp = mailSvcEIF.folderActionRequest(faReq);
        Assert.assertNotNull("FolderActionResponse object", faResp);
        testFolderActionResult faRespAction = faResp.getAction();
        Assert.assertNotNull("FolderActionResponse action object", faRespAction);
        Assert.assertNotNull("FolderActionResponse action id", faRespAction.getId());
        Assert.assertEquals("FolderActionResponse action d", testAcct2, faRespAction.getD());
        Assert.assertEquals("FolderActionResponse action op", "grant", faRespAction.getOp());
        Assert.assertNotNull("FolderActionResponse action zid", faRespAction.getZid());

        testCreateMountpointRequest crReq = new testCreateMountpointRequest();
        testNewMountpointSpec crLink = new testNewMountpointSpec();
        crLink.setF("#");
        crLink.setReminder(false);
        crLink.setName("SharedCalendar");
        crLink.setOwner(testAcct);
        crLink.setPath("/Calendar");
        crLink.setL("1");
        crLink.setView("appointment");
        crReq.setLink(crLink);
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)mailSvcEIF, testAcct2);
        testCreateMountpointResponse crResp = mailSvcEIF.createMountpointRequest(crReq);
        Assert.assertNotNull("CreateMountpointResponse object", crResp);
        testMountpoint crRespLink = crResp.getLink();
        Assert.assertNotNull("CreateMountpointResponse link object", crRespLink);
        Assert.assertEquals("CreateMountpointResponse link f", "#", crRespLink.getF());
        Assert.assertEquals("CreateMountpointResponse link reminder", false, crRespLink.isReminder());
        Assert.assertEquals("CreateMountpointResponse link oname", "Calendar", crRespLink.getOname());
        Assert.assertEquals("CreateMountpointResponse link ms", new Integer(2), crRespLink.getMs());
        Assert.assertEquals("CreateMountpointResponse link n", new Integer(0), crRespLink.getN());
        Assert.assertEquals("CreateMountpointResponse link activesyncdisabled", false, crRespLink.isActivesyncdisabled());
        Assert.assertEquals("CreateMountpointResponse link l", "1", crRespLink.getL());
        Assert.assertEquals("CreateMountpointResponse link perm", "r", crRespLink.getPerm());
        Assert.assertNotNull("CreateMountpointResponse link ruuid", crRespLink.getRuuid());
        Assert.assertNotNull("CreateMountpointResponse link id", crRespLink.getId());
        Assert.assertEquals("CreateMountpointResponse link s", new Long(0), crRespLink.getS());
        Assert.assertNotNull("CreateMountpointResponse link rid", crRespLink.getRid());
        Assert.assertNotNull("CreateMountpointResponse link zid", crRespLink.getZid());
        Assert.assertEquals("CreateMountpointResponse link name", "SharedCalendar", crRespLink.getName());
        Assert.assertEquals("CreateMountpointResponse link owner", testAcct, crRespLink.getOwner());
        Assert.assertEquals("CreateMountpointResponse link view", "appointment", crRespLink.getView());
        Assert.assertNotNull("CreateMountpointResponse link rest", crRespLink.getRest());
        Assert.assertNotNull("CreateMountpointResponse link uuid", crRespLink.getUuid());
        Assert.assertNotNull("CreateMountpointResponse link luuid", crRespLink.getLuuid());
    }
}
