/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.projects.admin.tests.adminextensions;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageAdminExtensions;

public class NavigateAdminExtensions extends AdminCommonTest {
	public NavigateAdminExtensions() {
		logger.info("New "+ NavigateAdminExtensions.class.getCanonicalName());

		// All tests start at the "Cos" page
		super.startingPage = app.zPageManageAdminExtensions;
	}
	
	/**
	 * Testcase : Navigate to Admin Extensions page
	 * Steps :
	 * 1. Go to Accounts
	 * 2. Verify navigation path -- "Home --> Configure --> Admin Extensions"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Admin Extensions",
			groups = { "sanity" })
			public void NavigateAdminExtensions_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Configure --> Admin Extensions"
		 */
		ZAssert.assertTrue(app.zPageManageAdminExtensions.zVerifyHeader(PageManageAdminExtensions.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAdminExtensions.zVerifyHeader(PageManageAdminExtensions.Locators.CONFIGURE), "Verfiy the \"Configure\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAdminExtensions.zVerifyHeader(PageManageAdminExtensions.Locators.ADMIN_EXTENSIONS), "Verfiy the \"Admin Extensions\" text exists in navigation path");
		
	}

}
