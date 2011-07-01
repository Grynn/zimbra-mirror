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
package com.zimbra.soap.mail;

import java.util.List;

import com.sun.xml.ws.developer.WSBindingProvider;
import com.zimbra.soap.Utility;
import com.zimbra.soap.mail.wsimport.generated.CalEcho;
import com.zimbra.soap.mail.wsimport.generated.CalOrganizer;
import com.zimbra.soap.mail.wsimport.generated.CalendarAttendee;
import com.zimbra.soap.mail.wsimport.generated.CalendarItemMsg;
import com.zimbra.soap.mail.wsimport.generated.CreateAppointmentRequest;
import com.zimbra.soap.mail.wsimport.generated.CreateAppointmentResponse;
import com.zimbra.soap.mail.wsimport.generated.CreateTaskRequest;
import com.zimbra.soap.mail.wsimport.generated.CreateTaskResponse;
import com.zimbra.soap.mail.wsimport.generated.DtTimeInfo;
import com.zimbra.soap.mail.wsimport.generated.EmailAddrInfo;
import com.zimbra.soap.mail.wsimport.generated.InvitationInfo;
import com.zimbra.soap.mail.wsimport.generated.InviteAsMP;
import com.zimbra.soap.mail.wsimport.generated.InviteComponent;
import com.zimbra.soap.mail.wsimport.generated.MailService;
import com.zimbra.soap.mail.wsimport.generated.MimePartInfo;
import com.zimbra.soap.mail.wsimport.generated.MpInviteInfo;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLCalendaringTest {

    private static MailService mailSvcEIF = null;

    private final static String testAcctDomain = "wsdl.cal.example.test";
    private final static String testAcct = "wsdl1@" + testAcctDomain;
    private final static String testAcct2 = "wsdl2@" + testAcctDomain;

    @BeforeClass
    public static void init() throws Exception {
        Utility.setUpToAcceptAllHttpsServerCerts();
        mailSvcEIF = Utility.getMailSvcEIF();
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
    public void createAppointment() throws Exception {
        Utility.ensureAccountExists(testAcct);
        Utility.ensureAccountExists(testAcct2);
        CreateAppointmentRequest req = new CreateAppointmentRequest();
        CalendarItemMsg msg = new CalendarItemMsg();
        msg.setL("15");
        msg.setSu("WSDL Appointment 1");
        InvitationInfo invite = new InvitationInfo();
        InviteComponent inviteComp = new InviteComponent();
        inviteComp.setFb("B");
        inviteComp.setRsvp(true);
        inviteComp.setMethod("REQUEST");
        inviteComp.setLoc("Mars");
        // TODO inviteComp.setType("event");
        inviteComp.setName("WSDL Appointment 1");
        inviteComp.setAllDay(false);
        inviteComp.setTransp("O");
        CalendarAttendee attendee = new CalendarAttendee();
        attendee.setRsvp(true);
        attendee.setA(testAcct2);
        attendee.setRole("OPT");
        attendee.setPtst("NE");
        inviteComp.getAt().add(attendee);
        DtTimeInfo start = new DtTimeInfo();
        start.setD("20320627T075906");
        inviteComp.setS(start);
        DtTimeInfo end = new DtTimeInfo();
        end.setD("20320627T085959");
        inviteComp.setE(end);
        CalOrganizer org = new CalOrganizer();
        org.setA(testAcct);
        org.setD("wsdl1");
        inviteComp.setOr(org);
        invite.setComp(inviteComp);
        msg.setInv(invite);
        EmailAddrInfo emailAddr = new EmailAddrInfo();
        emailAddr.setT("t");
        emailAddr.setA(testAcct2);
        msg.getE().add(emailAddr);
        MimePartInfo mp = new MimePartInfo();
        mp.setCt("multipart/alternative");
        MimePartInfo mpPlain = new MimePartInfo();
        mpPlain.setCt("text/plain");
        mpPlain.setContent("Body of the Appointment");
        mp.getMp().add(mpPlain);
        MimePartInfo mpHtml = new MimePartInfo();
        mpHtml.setCt("text/html");
        mpHtml.setContent("<html><body><b>Body</b> of the Appointment</body></html>");
        mp.getMp().add(mpHtml);
        msg.setMp(mp);
        req.setM(msg);
        req.setEcho(true);
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)mailSvcEIF,
                testAcct);
        CreateAppointmentResponse resp = mailSvcEIF.createAppointmentRequest(req);
        Assert.assertNotNull("CreateAppointmentResponse object", resp);
        Assert.assertTrue("revision", resp.getRev() >= 0);
        Assert.assertTrue("ms", resp.getMs() >= 0);
        Assert.assertNotNull("CreateAppointmentResponse invId", resp.getInvId());
        Assert.assertNotNull("CreateAppointmentResponse calItemId",
                resp.getCalItemId());
        CalEcho echo = resp.getEcho();
        Assert.assertNotNull("CreateAppointmentResponse echo object", echo);
    }

    @Test
    public void createTask() throws Exception {
        Utility.ensureAccountExists(testAcct);
        CreateTaskRequest req = new CreateTaskRequest();
        CalendarItemMsg msg = new CalendarItemMsg();
        msg.setL("15");
        msg.setSu("WSDL Task 1");
        InvitationInfo invite = new InvitationInfo();
        InviteComponent inviteComp = new InviteComponent();
        inviteComp.setPercentComplete("0");
        inviteComp.setAllDay(true);
        inviteComp.setStatus("NEED");
        inviteComp.setPriority("5");
        inviteComp.setName("WSDL Task 1");
        inviteComp.setLoc("Mars");
        CalOrganizer org = new CalOrganizer();
        org.setA(testAcct);
        org.setD("wsdl1");
        inviteComp.setOr(org);
        invite.setComp(inviteComp);
        msg.setInv(invite);
        MimePartInfo mp = new MimePartInfo();
        mp.setCt("multipart/alternative");
        MimePartInfo mpPlain = new MimePartInfo();
        mpPlain.setCt("text/plain");
        mpPlain.setContent("Body of the Task");
        mp.getMp().add(mpPlain);
        MimePartInfo mpHtml = new MimePartInfo();
        mpHtml.setCt("text/html");
        mpHtml.setContent("<html><body><b>Body</b> of the Task</body></html>");
        mp.getMp().add(mpHtml);
        msg.setMp(mp);
        req.setM(msg);
        req.setEcho(true);
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)mailSvcEIF,
                testAcct);
        CreateTaskResponse resp = mailSvcEIF.createTaskRequest(req);
        Assert.assertNotNull("CreateTaskResponse object", resp);
        Assert.assertTrue("revision", resp.getRev() >= 0);
        Assert.assertTrue("ms", resp.getMs() >= 0);
        Assert.assertNotNull("CreateTaskResponse invId", resp.getInvId());
        Assert.assertNotNull("CreateTaskResponse calItemId",
                resp.getCalItemId());
        CalEcho echo = resp.getEcho();
        Assert.assertNotNull("CreateTaskResponse echo object", echo);
        InviteAsMP inviteMp = echo.getM();
        Assert.assertNotNull("CreateTaskResponse/echo/m object", inviteMp);
        Assert.assertNotNull("CreateTaskResponse/echo/m @id object",
                inviteMp.getId());
        Assert.assertNotNull("CreateTaskResponse/echo/m @f object",
                inviteMp.getF());
        Assert.assertNotNull("CreateTaskResponse/echo/m @rev object",
                inviteMp.getRev());
        Assert.assertNotNull("CreateTaskResponse/echo/m @d object",
                inviteMp.getD());
        Assert.assertNotNull("CreateTaskResponse/echo/m @t object",
                inviteMp.getT());
        Assert.assertNotNull("CreateTaskResponse/echo/m @s object",
                inviteMp.getS());
        Assert.assertNotNull("CreateTaskResponse/echo/m @md object",
                inviteMp.getMd());
        Assert.assertNotNull("CreateTaskResponse/echo/m @ms object",
                inviteMp.getMs());
        Assert.assertNotNull("CreateTaskResponse/echo/m @l object",
                inviteMp.getL());
        Assert.assertNotNull("CreateTaskResponse/echo/m/meta object",
                inviteMp.getMeta());
        MpInviteInfo info = inviteMp.getInv();
        Assert.assertNotNull("CreateTaskResponse/echo/m/inv object", info);
        Assert.assertEquals("invite type", "task", info.getType());
        List<InviteComponent> iComps = info.getComp();
        Assert.assertNotNull("CreateTaskResponse/echo/m/inv/comp list ",
                iComps);
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp list size", 1,
                iComps.size());
        InviteComponent iComp = iComps.get(0);
        Assert.assertNotNull("CreateTaskResponse/echo/m/inv/comp @uid",
                iComp.getUid());
        Assert.assertNotNull(
                "CreateTaskResponse/echo/m/inv/comp @percentComplete",
                iComp.getPercentComplete());
        Assert.assertNotNull("CreateTaskResponse/echo/m/inv/comp @d",
                iComp.getD());
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp @status",
                "NEED", iComp.getStatus());
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp @noBlob",
                true, iComp.isNoBlob());
        Assert.assertNotNull("CreateTaskResponse/echo/m/inv/comp @ciFolder",
                iComp.getCiFolder());
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp @isOrg",
                true, iComp.isIsOrg());
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp @class",
                "PUB", iComp.getClazz());
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp @loc",
                "Mars", iComp.getLoc());
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp @compNum",
                0, iComp.getCompNum());
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp @url",
                "", iComp.getUrl());
        Assert.assertNotNull("CreateTaskResponse/echo/m/inv/comp @calItemId",
                iComp.getCalItemId());
        Assert.assertNotNull("CreateTaskResponse/echo/m/inv/comp @x_uid",
                iComp.getXUid());
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp @priority",
                "5", iComp.getPriority());
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp @name",
                "WSDL Task 1", iComp.getName());
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp @rsvp",
                false, iComp.isRsvp());
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp @seq",
                new Integer(0), iComp.getSeq());
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp @method",
                "PUBLISH", iComp.getMethod());
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp/fr",
                "Body of the Task", iComp.getFr());
        Assert.assertEquals("CreateTaskResponse/echo/m/inv/comp/desc",
                "Body of the Task", iComp.getDesc());
        Assert.assertNotNull("CreateTaskResponse/echo/m/inv/comp/descHtml",
                iComp.getDescHtml());
        CalOrganizer echoO = iComp.getOr();
        Assert.assertNotNull("CreateTaskResponse/echo/m/inv/comp/or", echoO);
        Assert.assertNotNull("CreateTaskResponse/echo/m/inv/comp/or @url",
                echoO.getUrl());
    }
}
