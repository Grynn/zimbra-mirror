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
package com.zimbra.qa.selenium.projects.admin.tests.aliases;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageAliases;

public class NavigateAlias extends AdminCommonTest {

	public NavigateAlias() {
		logger.info("New "+ NavigateAlias.class.getCanonicalName());

		// All tests start at the "Aliases" page
		super.startingPage = app.zPageManageAliases;
	}
	
	/**
	 * Testcase : Navigate to Aliases page
	 * Steps :
	 * 1. Go to Accounts
	 * 2. Verify navigation path -- "Home --> Manage --> Aliases"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Aliases",
			groups = { "sanity" })
			public void NavigateAlias_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Manage Accounts --> Aliases"
		 */
		ZAssert.assertTrue(app.zPageManageAliases.zVerifyHeader(PageManageAliases.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAliases.zVerifyHeader(PageManageAliases.Locators.MANAGE), "Verfiy the \"Manage Accounts\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAliases.zVerifyHeader(PageManageAliases.Locators.ALIAS), "Verfiy the \"Aliases\" text exists in navigation path");
		
	}

}