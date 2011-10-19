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
package com.zimbra.soap.account;

import com.sun.xml.ws.developer.WSBindingProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Element;

import com.zimbra.soap.Utility;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import generated.zcsclient.account.testAccountZimletDesc;
import generated.zcsclient.account.testAccountZimletInfo;
import generated.zcsclient.account.testGetInfoRequest;
import generated.zcsclient.account.testGetInfoResponse;
import generated.zcsclient.account.testGetInfoResponse.Zimlets;
import generated.zcsclient.ws.service.ZcsPortType;

/**
 * Current assumption : user1 exists with password test123
 */
public class WSDLGetInfoTest {

    private static final Logger LOG = Logger.getLogger(WSDLGetInfoTest.class);

    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        LOG.setLevel(Level.INFO);
    }

    private static ZcsPortType acctSvcEIF;

    @BeforeClass
    public static void init() throws Exception {
        // if use validation - get issue processing handlerObject which should be caught by
        // an XmlAnyElement in AccountZimletDesc
        acctSvcEIF = Utility.getNonValidatingZcsSvcEIF();
        // acctSvcEIF = Utility.getZcsSvcEIF();
    }

    @Test
    public void simple() throws Exception {
       testGetInfoRequest req = new testGetInfoRequest();
       Utility.addSoapAcctAuthHeader((WSBindingProvider)acctSvcEIF);
       testGetInfoResponse response = acctSvcEIF.getInfoRequest(req);
       Assert.assertNotNull("response object", response);
       Assert.assertNotNull("public URL string", response.getPublicURL());
    }
 
    /**
     * This test doesn't actually fail as not sure zimlets are necessarily present on
     * test systems.  The logging demonstates that the XmlAnyElement causes unmarshaling
     * to create Element objects where appropriate for e.g. <handlerObject>
     */
    @Test
    public void zimlets() throws Exception {
       testGetInfoRequest req = new testGetInfoRequest();
       req.setSections("zimlets");
       Utility.addSoapAcctAuthHeader((WSBindingProvider)acctSvcEIF);
       testGetInfoResponse response = acctSvcEIF.getInfoRequest(req);
       Assert.assertNotNull("response object", response);
       Zimlets zimlets = response.getZimlets();
       if (zimlets != null) {
           for (testAccountZimletInfo zimletInfo : zimlets.getZimlet()) {
               testAccountZimletDesc zimletDesc = zimletInfo.getZimlet();
               if (zimletDesc != null) {
                   for (Object obj : zimletDesc.getServerExtensionOrIncludeOrIncludeCSS()) {
                       if (obj instanceof Element) {
                           Element elem = (Element) obj;
                           LOG.info("Found zimlet description sub-element with name " + elem.getLocalName());
                       }
                   }
               }
           }
       }
    }
}
