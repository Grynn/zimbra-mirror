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
import zimbra.generated.mailclient.mail.testAppointmentHitInfo;
import zimbra.generated.mailclient.mail.testCalOrganizer;
import zimbra.generated.mailclient.mail.testSearchRequest;
import zimbra.generated.mailclient.mail.testSearchResponse;
import zimbra.generated.mailclient.ws.service.MailService;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLSearchTest {

    private static MailService mailSvcEIF = null;

    // private final static String testAcctDomain = "wsdl.cal.example.test";
    // private final static String testAcct = "wsdl1@" + testAcctDomain;

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
    public void user1CalSearch() throws Exception {
        testSearchRequest req = new testSearchRequest();
        req.setSortBy("none");
        req.setLimit(500);
        req.setLocale("en_US");
        req.setCalExpandInstStart(1306710000000L);
        req.setCalExpandInstEnd(1307228400000L);
        req.setTypes("appointment");
        req.setOffset(0);
        req.setQuery("every (inid:\"10\")");
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)mailSvcEIF,
                "user1");
        testSearchResponse resp = mailSvcEIF.searchRequest(req);
        Assert.assertNotNull("SearchResponse object", resp);
        Assert.assertEquals("SearchResponse sortBy", "none", resp.getSortBy());
        Assert.assertEquals("SearchResponse offset", new Integer(0),
                        resp.getOffset());
        Assert.assertEquals("SearchResponse more", Boolean.FALSE, resp.isMore());
        Assert.assertNull("SearchResponse total", resp.getTotal());
        List <Object> hits = resp.getHitOrCOrM();
        Assert.assertEquals("SearchResponse number of hits", 1, hits.size());
        Object o = hits.get(0);
        if (o instanceof testAppointmentHitInfo) {
            testAppointmentHitInfo ahi = (testAppointmentHitInfo) o;
            testCalOrganizer org = ahi.getOr();
            Assert.assertNotNull("SearchResponse/appt/or object", org);
            Assert.assertEquals("SearchResponse/appt/or @a",
                    "tom@example.zimbra.com", org.getA());
            Assert.assertEquals("SearchResponse/appt/or @url",
                    "tom@example.zimbra.com", org.getUrl());
            Assert.assertEquals("SearchResponse/appt/or @d",
                    "Tom", org.getD());
        } else {
            Assert.fail("SearchResponse hit is NOT an AppointmentHitInfo");
        }
    }
}
