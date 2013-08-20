/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.zimlets.social;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class LoadSocialTab extends AjaxCommonTest {

	
	public LoadSocialTab() {
		logger.info("New "+ LoadSocialTab.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageSocial;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;


	}
	

	/**
	 * @throws HarnessException
	 */
	@Bugs(ids = "50123")
	@Test(	description = "Basic test case: Load the Social tab",
			groups = { "deprecated" })
	public void LoadSocialTab_01() throws HarnessException {
		
		ZAssert.assertTrue(app.zPageSocial.zIsActive(), "Verify the social page is active");

		
	}


}
