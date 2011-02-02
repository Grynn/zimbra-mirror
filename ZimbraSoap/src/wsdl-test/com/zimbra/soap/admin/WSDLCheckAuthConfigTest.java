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

import javax.xml.ws.soap.SOAPFaultException;

import com.sun.xml.ws.developer.WSBindingProvider;

import com.zimbra.soap.admin.wsimport.generated.*;

import com.zimbra.soap.Utility;

import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLCheckAuthConfigTest {

    // The AdminService interface is the Java type bound to
    // the portType section of the WSDL document.
    private final static String testAcctDomain = "wsdl.acct.domain.example.test";
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

    // TODO: Add a test with a valid LDAP URL.
    @Test
    public void checkAuthConfigBadAuthURLTest() throws Exception {
        Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        CheckAuthConfigRequest req = new CheckAuthConfigRequest();
        req.setName(testAcct);
        req.setPassword("test123");
        Attr attr;
        attr = new Attr(); attr.setN("zimbraAuthMech");
        attr.setValue("ldap");
        req.getA().add(attr);
        attr = new Attr(); attr.setN("zimbraAuthLdapURL");
        attr.setValue("ldaps://localhost:3269");
        req.getA().add(attr);
        attr = new Attr(); attr.setN("zimbraAuthLdapBindDn");
        attr.setValue("%u@example.test");
        req.getA().add(attr);
        try {
            eif.checkAuthConfigRequest(req);
        } catch (SOAPFaultException sfe) {
            Assert.assertTrue("Soap fault message [" +
                    sfe.getMessage() + "] should start with 'javax.naming.CommunicationException'",
                    sfe.getMessage().startsWith("javax.naming.CommunicationException"));
        }
        Utility.deleteAccountIfExists(testAcct);
    }

}
