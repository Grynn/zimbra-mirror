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
package com.zimbra.soap.mail;

import com.sun.xml.ws.developer.WSBindingProvider;
import com.zimbra.soap.Utility;
import com.zimbra.soap.mail.wsimport.generated.ImportContactsRequest;
import com.zimbra.soap.mail.wsimport.generated.ImportContactsResponse;
import com.zimbra.soap.mail.wsimport.generated.Content;
import com.zimbra.soap.mail.wsimport.generated.ImportContact;
import com.zimbra.soap.mail.wsimport.generated.MailService;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLImportContactsTest {

    private static MailService mailSvcEIF;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @BeforeClass
    public static void init() throws Exception {
        mailSvcEIF = Utility.getMailSvcEIF();
    }

    @Test
    public void withDefaults() throws Exception {
       ImportContactsRequest req = new ImportContactsRequest();
       req.setCt("csv");
       Content theContent = new Content();
       theContent.setValue("\"email\",\"firstName\",\"lastName\"\n\"fred@example.org\",\"Fred\",\"Flintstone\"");
       req.setContent(theContent);
       Utility.addSoapAuthHeader((WSBindingProvider)mailSvcEIF);
       ImportContactsResponse resp = mailSvcEIF.importContactsRequest(req);
       Assert.assertNotNull("ImportContactsResponse object", resp);
       ImportContact contact = resp.getCn();
       Assert.assertNotNull("<cn> contents", contact);
       Assert.assertEquals("Number of contacts imported", 1, contact.getN());
       String ids = contact.getIds();
       Assert.assertNotNull("Contact ids", ids);
       Assert.assertTrue("Contact ids sting length >= 2", (ids.length() > 1));
    }

    @Test
    public void winLiveGerman() throws Exception {
       ImportContactsRequest req = new ImportContactsRequest();
       req.setCt("csv");
       req.setCsvfmt("windows-live-mail-csv");
       req.setCsvlocale("de");
       req.setCsvsep(";");
       // req.setL(""); // TODO: Specify a valid item ID
       Content theContent = new Content();
       theContent.setValue("\"E-Mail-Adresse\";\"Vorname\";\"Nachname\"\n\"ae@example.org\";\"Albert\";\"Einstein\"");
       req.setContent(theContent);
       Utility.addSoapAuthHeader((WSBindingProvider)mailSvcEIF);
       ImportContactsResponse resp = mailSvcEIF.importContactsRequest(req);
       Assert.assertNotNull("ImportContactsResponse object", resp);
       ImportContact contact = resp.getCn();
       Assert.assertNotNull("<cn> contents", contact);
       Assert.assertEquals("Number of contacts imported", 1, contact.getN());
       String ids = contact.getIds();
       Assert.assertNotNull("Contact ids", ids);
       Assert.assertTrue("Contact ids sting length >= 2", (ids.length() > 1));
    }
}
