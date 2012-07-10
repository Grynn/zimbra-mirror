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

import generated.zcsclient.mail.testContactGroupMember;
import generated.zcsclient.mail.testContactInfo;
import generated.zcsclient.mail.testContactSpec;
import generated.zcsclient.mail.testCreateContactRequest;
import generated.zcsclient.mail.testCreateContactResponse;
import generated.zcsclient.mail.testCreateTagRequest;
import generated.zcsclient.mail.testCreateTagResponse;
import generated.zcsclient.mail.testModifyContactAttr;
import generated.zcsclient.mail.testModifyContactGroupMember;
import generated.zcsclient.mail.testModifyContactRequest;
import generated.zcsclient.mail.testModifyContactResponse;
import generated.zcsclient.mail.testModifyContactSpec;
import generated.zcsclient.mail.testNewContactAttr;
import generated.zcsclient.mail.testNewContactGroupMember;
import generated.zcsclient.mail.testTagInfo;
import generated.zcsclient.mail.testTagSpec;
import generated.zcsclient.ws.service.ZcsPortType;
import generated.zcsclient.zm.testContactAttr;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLContactsTest {

    private static ZcsPortType mailSvcEIF = null;

    private final static String testAcctDomain = "wsdl.contacts.example.test";
    private final static String testAcct = "wsdl1@" + testAcctDomain;

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
    public void modifyContactSpecifyingTag() throws Exception {
        final String firstName = "bug67327";
        final String surName = "surname";
        final String tag1 = "tag67327-a";
        final String tag2 = "tag67327-b";
        final Byte tag1color = Byte.valueOf((byte)2);
        final Byte tag2color = Byte.valueOf((byte)3);
        Utility.ensureAccountExists(testAcct);
        // create a contact
        testCreateContactRequest req = new testCreateContactRequest();
        testContactSpec origSpec = new testContactSpec();
        origSpec.setL("7");  // Folder ID - would be nice if wasn't hard coded.
        testNewContactAttr nam = new testNewContactAttr();
        nam.setN("firstName");
        nam.setValue(firstName);
        origSpec.getA().add(nam);
        req.setCn(origSpec);
        req.setVerbose(true);
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)mailSvcEIF, testAcct);
        testCreateContactResponse resp = mailSvcEIF.createContactRequest(req);
        Assert.assertNotNull("CreateContactResponse object", resp);
        testContactInfo createdContact = resp.getCn();
        Assert.assertNotNull("CreateContactResponse/cn object", createdContact);
        String contactId = createdContact.getId();
        Assert.assertNotNull("CreateContactResponse/cn contactId", contactId);
        Assert.assertEquals("Created FileAs string", firstName, createdContact.getFileAsStr());
        Assert.assertEquals("Folder string", "7", createdContact.getL());
        Assert.assertTrue("Revision should be positive", createdContact.getRev() > 0);
        List<testContactAttr> attrs = createdContact.getA();
        Assert.assertEquals("Original number of attrs", 1, attrs.size());
        testContactAttr firstAttr = attrs.get(0);
        Assert.assertEquals("Created first attr value", firstName, firstAttr.getValue());
        // create a couple of tags to apply to the contact
        testCreateTagRequest crTagReq = new testCreateTagRequest();
        testTagSpec tagSpec = new testTagSpec();
        tagSpec.setName(tag1);
        tagSpec.setColor(tag1color);
        crTagReq.setTag(tagSpec);
        testCreateTagResponse crTagResp = mailSvcEIF.createTagRequest(crTagReq);
        Assert.assertNotNull("CreateTagResponse object 1", crTagResp);
        testTagInfo tagInfo = crTagResp.getTag();
        Assert.assertNotNull("CreateTagResponse object 1 tagInfo", tagInfo);
        Assert.assertNotNull("CreateTagResponse object 1 tagInfo id", tagInfo.getId());
        Assert.assertEquals("CreateTagResponse object 1 tagInfo name", tag1, tagInfo.getName());
        Assert.assertEquals("CreateTagResponse object 1 tagInfo color", tag1color, tagInfo.getColor());
        tagSpec.setName(tag2);
        tagSpec.setColor(tag2color);
        crTagResp = mailSvcEIF.createTagRequest(crTagReq);
        Assert.assertNotNull("CreateTagResponse object 2", crTagResp);
        tagInfo = crTagResp.getTag();
        Assert.assertNotNull("CreateTagResponse object 2 tagInfo", tagInfo);
        Assert.assertNotNull("CreateTagResponse object 2 tagInfo id", tagInfo.getId());
        Assert.assertEquals("CreateTagResponse object 2 tagInfo name", tag2, tagInfo.getName());
        Assert.assertEquals("CreateTagResponse object 2 tagInfo color", tag2color, tagInfo.getColor());
        // modify the contact with a real change as well as applying the 2 tags
        testModifyContactRequest modReq = new testModifyContactRequest();
        modReq.setReplace(false);
        modReq.setVerbose(true);
        testModifyContactSpec modSpec = new testModifyContactSpec();
        modSpec.setId(Integer.valueOf(contactId));
        modSpec.setTn(tag1 + "," + tag2);
        testModifyContactAttr lnam = new testModifyContactAttr();
        lnam.setN("lastName");
        lnam.setValue(surName);
        modSpec.getA().add(lnam);
        modReq.setCn(modSpec);
        testModifyContactResponse modResp = mailSvcEIF.modifyContactRequest(modReq);
        testContactInfo modCn = modResp.getCn();
        attrs = modCn.getA();
        Assert.assertEquals("ModifiedContact 1 number of attrs", 2, attrs.size());
        String tags = modCn.getTn();
        Assert.assertTrue("ModifiedContact 1 has tag1", tags.indexOf(tag1) >= 0);
        Assert.assertTrue("ModifiedContact 1 has tag2", tags.indexOf(tag2) >= 0);
        // Check that we can replace the set of tags with a new set
        modSpec.setTn(tag2);
        modResp = mailSvcEIF.modifyContactRequest(modReq);
        modCn = modResp.getCn();
        attrs = modCn.getA();
        Assert.assertEquals("ModifiedContact 2 number of attrs", 2, attrs.size());
        tags = modCn.getTn();
        Assert.assertTrue("ModifiedContact 2 does NOT have tag1", tags.indexOf(tag1) == -1);
        Assert.assertTrue("ModifiedContact 2 has tag2", tags.indexOf(tag2) >= 0);
        // Check that not specifying "tn" leaves the taglist alone
        modSpec.setTn(null);
        modResp = mailSvcEIF.modifyContactRequest(modReq);
        modCn = modResp.getCn();
        attrs = modCn.getA();
        Assert.assertEquals("ModifiedContact 3 number of attrs", 2, attrs.size());
        tags = modCn.getTn();
        Assert.assertTrue("ModifiedContact 3 has tag2", tags.indexOf(tag2) >= 0);
        // Check that can delete all tags
        modSpec.setTn("");
        modResp = mailSvcEIF.modifyContactRequest(modReq);
        modCn = modResp.getCn();
        attrs = modCn.getA();
        Assert.assertEquals("ModifiedContact 4 number of attrs", 2, attrs.size());
        tags = modCn.getTn();
        Assert.assertTrue("ModifiedContact 4 tags", tags == null);
    }

    @Test
    public void modifyContactGroup() throws Exception {
        final String fileAs = "bug75912";
        final String nickName = "contactGrp";
        final String qaTeam = "uid=qa-team,ou=people,dc=example,dc=com";
        final String terryp = "\"Terry Pratchett\" <terryp@example.test>";
        final String neilg = "\"Neil Gaiman\" <neilg@example.test>";
        final String jrrtolkien = "\"JRR Tolkien\" <jrrtolkien@example.test>";
        Utility.ensureAccountExists(testAcct);
        testCreateContactRequest req = new testCreateContactRequest();
        req.setVerbose(true);
        testContactSpec origSpec = new testContactSpec();
        req.setCn(origSpec);
        origSpec.setL("7");  // Folder ID - would be nice if wasn't hard coded.

        testNewContactAttr nam = new testNewContactAttr();
        nam.setN("fileAs");
        nam.setValue("8:" + fileAs);  // 8: means fileAs is freeform (i.e. not Company, or first/last etc)
        origSpec.getA().add(nam);
        nam = new testNewContactAttr();
        nam.setN("nickname");
        nam.setValue(nickName);
        origSpec.getA().add(nam);
        nam = new testNewContactAttr();
        nam.setN("type");
        nam.setValue("group");
        origSpec.getA().add(nam);

        testNewContactGroupMember member = new testNewContactGroupMember();
        member.setType("G");
        member.setValue(qaTeam);
        origSpec.getM().add(member);
        member = new testNewContactGroupMember();
        member.setType("I");
        member.setValue(terryp);
        origSpec.getM().add(member);

        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)mailSvcEIF, testAcct);
        testCreateContactResponse resp = mailSvcEIF.createContactRequest(req);
        Assert.assertNotNull("CreateContactResponse object", resp);
        testContactInfo createdContact = resp.getCn();
        Assert.assertNotNull("CreateContactResponse/cn object", createdContact);
        String contactId = createdContact.getId();
        Assert.assertNotNull("CreateContactResponse/cn contactId", contactId);
        Assert.assertEquals("Created FileAs string", fileAs, createdContact.getFileAsStr());
        Assert.assertEquals("Folder string", "7", createdContact.getL());
        Assert.assertTrue("Revision should be positive", createdContact.getRev() > 0);
        List<testContactAttr> attrs = createdContact.getA();
        Assert.assertEquals("Original number of attrs", 3, attrs.size());
        Assert.assertEquals("Original number of members", 2, createdContact.getM().size());

        // Modify to remove a member
        testModifyContactRequest modReq = new testModifyContactRequest();
        modReq.setReplace(false);
        modReq.setVerbose(true);
        testModifyContactSpec modSpec = new testModifyContactSpec();
        modSpec.setId(Integer.valueOf(contactId));
        modReq.setCn(modSpec);
        testModifyContactGroupMember modMember = new testModifyContactGroupMember();
        modMember.setOp("-");
        modMember.setType("I");
        modMember.setValue(terryp);
        modSpec.getM().add(modMember);

        testModifyContactResponse modResp = mailSvcEIF.modifyContactRequest(modReq);
        Assert.assertEquals("After 1st mod number of members", 1, modResp.getCn().getM().size());
        testContactGroupMember firstMember = modResp.getCn().getM().get(0);
        firstMember.getValue();
        Assert.assertEquals("After 1st mod first member value", qaTeam, firstMember.getValue());
 
        // Modify to replace all members with one new member
        modReq = new testModifyContactRequest();
        modReq.setReplace(false);
        modReq.setVerbose(true);
        modSpec = new testModifyContactSpec();
        modSpec.setId(Integer.valueOf(contactId));
        modReq.setCn(modSpec);
        modMember = new testModifyContactGroupMember();
        modMember.setOp("+");
        modMember.setType("I");
        modMember.setValue(neilg);
        modSpec.getM().add(modMember);

        modResp = mailSvcEIF.modifyContactRequest(modReq);
        Assert.assertEquals("After 2nd mod number of members", 2, modResp.getCn().getM().size());

        // Modify to add a member
        modReq = new testModifyContactRequest();
        modReq.setReplace(false);
        modReq.setVerbose(true);
        modSpec = new testModifyContactSpec();
        modSpec.setId(Integer.valueOf(contactId));
        modReq.setCn(modSpec);
        modMember = new testModifyContactGroupMember();
        modMember.setOp("reset");
        modSpec.getM().add(modMember);
        modMember = new testModifyContactGroupMember();
        modMember.setOp("+");
        modMember.setType("I");
        modMember.setValue(jrrtolkien);
        modSpec.getM().add(modMember);

        modResp = mailSvcEIF.modifyContactRequest(modReq);
        Assert.assertEquals("After 3rd mod number of members", 1, modResp.getCn().getM().size());
        firstMember = modResp.getCn().getM().get(0);
        firstMember.getValue();
        Assert.assertEquals("After 3rd mod first member value", jrrtolkien, firstMember.getValue());
    }
}
