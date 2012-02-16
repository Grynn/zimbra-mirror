/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011, 2012 Zimbra, Inc.
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
import generated.zcsclient.mail.testImportContactsRequest;
import generated.zcsclient.mail.testImportContactsResponse;
import generated.zcsclient.mail.testContent;
import generated.zcsclient.mail.testImportContact;
import generated.zcsclient.ws.service.ZcsPortType;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLImportContactsTest {

    private static ZcsPortType mailSvcEIF = null;

    private final static String testAcctDomain = "wsdl.import.example.test";
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
    public void withDefaults() throws Exception {
        Utility.ensureAccountExists(testAcct);
        testImportContactsRequest req = new testImportContactsRequest();
        req.setCt("csv");
        testContent theContent = new testContent();
        StringBuilder contentB = new StringBuilder();
        contentB.append("\"email\",\"firstName\",\"lastName\"\n");
        contentB.append("\"fred@example.org\",\"Fred\",\"Flintstone\"");
        theContent.setValue(contentB.toString());
        req.setContent(theContent);
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)mailSvcEIF,
                testAcct);
        testImportContactsResponse resp = mailSvcEIF.importContactsRequest(req);
        Assert.assertNotNull("ImportContactsResponse object", resp);
        testImportContact contact = resp.getCn();
        Assert.assertNotNull("<cn> contents", contact);
        Assert.assertEquals("Number of contacts imported", 1, contact.getN());
        String ids = contact.getIds();
        Assert.assertNotNull("Contact ids", ids);
        Assert.assertTrue("Contact ids sting length >= 2", (ids.length() > 1));
    }

    @Test
    public void winLiveGerman() throws Exception {
        testImportContactsRequest req = new testImportContactsRequest();
        req.setCt("csv");
        req.setCsvfmt("windows-live-mail-csv");
        req.setCsvlocale("de");
        // req.setL(""); // TODO: Specify a valid item ID
        testContent theContent = new testContent();
        StringBuilder contentB = new StringBuilder();
        contentB.append("\"E-Mail-Adresse\";\"Vorname\";\"Nachname\"\n");
        contentB.append("\"ae@example.org\";\"Albert\";\"Einstein\"");
        theContent.setValue(contentB.toString());
        req.setContent(theContent);
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)mailSvcEIF,
                testAcct);
        testImportContactsResponse resp = mailSvcEIF.importContactsRequest(req);
        Assert.assertNotNull("ImportContactsResponse object", resp);
        testImportContact contact = resp.getCn();
        Assert.assertNotNull("<cn> contents", contact);
        Assert.assertEquals("Number of contacts imported", 1, contact.getN());
        String ids = contact.getIds();
        Assert.assertNotNull("Contact ids", ids);
        Assert.assertTrue("Contact ids sting length >= 2", (ids.length() > 1));
    }
}
