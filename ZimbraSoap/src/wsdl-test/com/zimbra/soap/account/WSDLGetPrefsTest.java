/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011 Zimbra, Inc.
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
import java.util.List;

import com.sun.xml.ws.developer.WSBindingProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zimbra.soap.Utility;
import zimbra.generated.accountclient.account.testGetPrefsRequest;
import zimbra.generated.accountclient.account.testGetPrefsResponse;
import zimbra.generated.accountclient.account.testPref;
import zimbra.generated.accountclient.ws.service.AccountService;

/**
 * Current assumption : user1 exists with password test123
 */
public class WSDLGetPrefsTest {

    private static AccountService acctSvcEIF;

    @BeforeClass
    public static void init() throws Exception {
        acctSvcEIF = Utility.getAcctSvcEIF();
    }

    @Test
    public void simple() throws Exception {
       testGetPrefsRequest req = new testGetPrefsRequest();
       Utility.addSoapAcctAuthHeader((WSBindingProvider)acctSvcEIF);
       testGetPrefsResponse response = acctSvcEIF.getPrefsRequest(req);
       Assert.assertNotNull(response);
       List <testPref> respPrefs = response.getPref();
       Assert.assertTrue("Number of preferences in response=" + respPrefs.size() + " which is less than expected", respPrefs.size() > 100);
    }

    @Test
    public void getTwo() throws Exception {
       testGetPrefsRequest req = new testGetPrefsRequest();
       Utility.addSoapAcctAuthHeader((WSBindingProvider)acctSvcEIF);
       testPref calUserQAddPref = new testPref();
       calUserQAddPref.setName("zimbraPrefCalendarUseQuickAdd");
       testPref zimbraPrefShowSearchString = new testPref();
       zimbraPrefShowSearchString.setName("zimbraPrefShowSearchString");

       req.getPref().add(calUserQAddPref);
       req.getPref().add(zimbraPrefShowSearchString);
       testGetPrefsResponse response = acctSvcEIF.getPrefsRequest(req);
       Assert.assertNotNull(response);
       List <testPref> respPrefs = response.getPref();
       Assert.assertEquals("Number of <pref> in response wrong.", 2, respPrefs.size());
    }
}
