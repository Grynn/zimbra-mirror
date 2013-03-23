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
package com.zimbra.qa.selenium.projects.ajax.tests.login;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;



public class BasicLogout extends AjaxCommonTest {
	
	public BasicLogout() {
		logger.info("New "+ BasicLogout.class.getCanonicalName());
	}
	
	@Test(	description = "Logout of the Mobile Client",
			groups = { "sanity" })
	public void BasicLogout01() throws HarnessException {
		
		// Login
		app.zPageMain.zLogout();
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageLogin.zIsActive(), "Verify that the account is logged out");
		
	}


}
