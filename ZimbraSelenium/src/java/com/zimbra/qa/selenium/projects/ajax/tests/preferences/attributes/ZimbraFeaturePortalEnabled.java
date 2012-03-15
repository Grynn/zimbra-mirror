/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.attributes;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;



public class ZimbraFeaturePortalEnabled extends AjaxCommonTest {
	
	public ZimbraFeaturePortalEnabled() {
		logger.info("New "+ ZimbraFeaturePortalEnabled.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageLogin;
		super.startingAccountPreferences = new HashMap<String , String>() {
			private static final long serialVersionUID = -3123410183252792255L;
		{
		    put("zimbraFeaturePortalEnabled", "TRUE");
		    put("zimbraPortalName", "example");
		}};
		
	}
	
	@Bugs(ids = "67462")
	@Test(	description = "Login to the Ajax Client with the 'example' portal enabled",
			groups = { "deprecated" })
	public void BasicLogin01() throws HarnessException {
		
		// Login
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageMain.zIsActive(), "Verify that the account is logged in");
		
		
		ZAssert.assertTrue(app.zPageMain.sIsElementPresent("css=td#zb__App__Portal_title"), "Verify the 'Home' tab is present");
		ZAssert.assertTrue(app.zPageMain.zIsVisiblePerPosition("css=td#zb__App__Portal_title", 0, 0), "Verify the 'Home' tab is present");
		
	}


}
