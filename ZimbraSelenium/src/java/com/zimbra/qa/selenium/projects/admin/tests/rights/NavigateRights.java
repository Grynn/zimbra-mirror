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
package com.zimbra.qa.selenium.projects.admin.tests.rights;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageRights;

public class NavigateRights extends AdminCommonTest {
	public NavigateRights() {
		logger.info("New "+ NavigateRights.class.getCanonicalName());

		// All tests start at the "Cos" page
		super.startingPage = app.zPageManageRights;
	}
	
	/**
	 * Testcase : Navigate to Rights page
	 * Steps :
	 * 1. Go to Accounts
	 * 2. Verify navigation path -- "Home --> Configure --> Rights"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Rights",
			groups = { "sanity" })
			public void NavigateRights_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Configure --> Rights"
		 */
		ZAssert.assertTrue(app.zPageManageRights.zVerifyHeader(PageManageRights.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageRights.zVerifyHeader(PageManageRights.Locators.CONFIGURE), "Verfiy the \"Configure\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageRights.zVerifyHeader(PageManageRights.Locators.RIGHTS), "Verfiy the \"Rights\" text exists in navigation path");
		
	}

}
