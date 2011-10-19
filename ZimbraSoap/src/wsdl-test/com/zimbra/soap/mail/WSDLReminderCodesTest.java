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

import javax.xml.ws.soap.SOAPFaultException;

import com.sun.xml.ws.developer.WSBindingProvider;
import com.zimbra.soap.Utility;

import generated.zcsclient.mail.testInvalidateReminderDeviceRequest;
import generated.zcsclient.mail.testInvalidateReminderDeviceResponse;
import generated.zcsclient.mail.testSendVerificationCodeRequest;
import generated.zcsclient.mail.testSendVerificationCodeResponse;
import generated.zcsclient.mail.testVerifyCodeRequest;
import generated.zcsclient.mail.testVerifyCodeResponse;
import generated.zcsclient.ws.service.ZcsPortType;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLReminderCodesTest {

    private static ZcsPortType mailSvcEIF = null;

    private final static String testAcctDomain = "wsdl.example.test";
    private final static String testAcct = "remind@" + testAcctDomain;

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
    public void reminderVerificationCodes() throws Exception {
        Utility.ensureAccountExists(testAcct);
        testSendVerificationCodeRequest req = new testSendVerificationCodeRequest();
        req.setA(testAcct);
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)mailSvcEIF,
                testAcct);
        testSendVerificationCodeResponse resp =
            mailSvcEIF.sendVerificationCodeRequest(req);
        Assert.assertNotNull("SendVerificationCodeResponse object", resp);

        testVerifyCodeRequest vcreq = new testVerifyCodeRequest();
        vcreq.setA(testAcct);
        // Would be nice to look at message sent to inbox above to get value.
        vcreq.setCode("duff");
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)mailSvcEIF,
                testAcct);
        testVerifyCodeResponse vcresp = mailSvcEIF.verifyCodeRequest(vcreq);
        Assert.assertNotNull("VerifyCodeResponse object", vcresp);
        Assert.assertFalse("Success state", vcresp.isSuccess());

        testInvalidateReminderDeviceRequest irdreq =
                new testInvalidateReminderDeviceRequest();
        irdreq.setA(testAcct);
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)mailSvcEIF,
                testAcct);
        try {
            // I think this only works if we provided the correct code in
            // VerifyCodeRequest.  As this testing is really just exercising
            // the parsing, not too worried.
            testInvalidateReminderDeviceResponse irdresp =
                mailSvcEIF.invalidateReminderDeviceRequest(irdreq);
            Assert.assertNotNull("InvalidateReminderDeviceResponse object",
                    irdresp);
        } catch (SOAPFaultException ex) {
            String exMsg = ex.getMessage();
            int ndx = exMsg.indexOf(
                    "is not same as the zimbraCalendarReminderDeviceEmail");
            Assert.assertTrue("Expected SOAP fault not thrown", ndx > 0 );
        }
    }
}
