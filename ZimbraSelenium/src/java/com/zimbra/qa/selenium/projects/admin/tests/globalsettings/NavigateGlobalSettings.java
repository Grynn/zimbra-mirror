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
package com.zimbra.qa.selenium.projects.admin.tests.globalsettings;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageGlobalSettings;

public class NavigateGlobalSettings extends AdminCommonTest {
	public NavigateGlobalSettings() {
		logger.info("New "+ NavigateGlobalSettings.class.getCanonicalName());

		// All tests start at the "Cos" page
		super.startingPage = app.zPageManageGlobalSettings;
	}
	
	/**
	 * Testcase : Navigate to Global Settings page
	 * Steps :
	 * 1. Go to Accounts
	 * 2. Verify navigation path -- "Home --> Configure --> Global Settings"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Global Settings",
			groups = { "sanity" })
			public void NavigateGlobalSettings_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Configure --> Global Settings"
		 */
		ZAssert.assertTrue(app.zPageManageGlobalSettings.zVerifyHeader(PageManageGlobalSettings.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageGlobalSettings.zVerifyHeader(PageManageGlobalSettings.Locators.CONFIGURE), "Verfiy the \"Configure\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageGlobalSettings.zVerifyHeader(PageManageGlobalSettings.Locators.GLOBAL_SETTINGS), "Verfiy the \"Global Settings\" text exists in navigation path");
		
	}

}
