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
package com.zimbra.soap.account;
import com.sun.xml.ws.developer.WSBindingProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zimbra.soap.Utility;
import generated.zcsclient.account.testEndSessionRequest;
import generated.zcsclient.account.testEndSessionResponse;
import generated.zcsclient.ws.service.ZcsPortType;

/**
 * Current assumption : user1 exists with password test123
 */
public class WSDLEndSessionTest {

    private static ZcsPortType acctSvcEIF;

    @BeforeClass
    public static void init() throws Exception {
        acctSvcEIF = Utility.getZcsSvcEIF();
    }

    @Test
    public void simple() throws Exception {
       testEndSessionRequest req = new testEndSessionRequest();
       Utility.addSoapAcctAuthHeader((WSBindingProvider)acctSvcEIF);
       testEndSessionResponse response = acctSvcEIF.endSessionRequest(req);
       Assert.assertNotNull(response);
    }
}
