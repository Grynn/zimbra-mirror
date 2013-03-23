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
package com.zimbra.qa.selenium.projects.admin.tests.login;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;


public class BasicLogin extends AdminCommonTest {
	
	public BasicLogin() {
		logger.info("New "+ BasicLogin.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageLogin;
		super.startingAccount = null;
		
	}
	
	@Test(	description = "Login to the Admin Console",
			groups = { "sanity" })
	public void BasicLogin01() throws HarnessException {
		
		// Login
		app.zPageLogin.login(gAdmin);
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageMain.zIsActive(), "Verify that the account is logged in");
		
	}

	@Test(	description = "Login to the Admin Console as a different Admin Account",
			groups = { "smoke" })
	public void BasicLogin02() throws HarnessException {
		
		// Create a new AdminAccount
		ZimbraAdminAccount account = new ZimbraAdminAccount("admin"+ ZimbraSeleniumProperties.getUniqueString() + "@" + ZimbraSeleniumProperties.getStringProperty("testdomain"));
		account.provision();
		account.authenticate();
		
		// Login
		app.zPageLogin.login(account);
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageMain.zIsActive(), "Verify that the account is logged in");
		
	}


}
