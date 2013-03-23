/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.admin.tests.accounts;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageAccounts;

public class NavigateAccount extends AdminCommonTest {
	
	public NavigateAccount() {
		logger.info("New "+ NavigateAccount.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageAccounts;
	}
	
	/**
	 * Testcase : Navigate to Accounts page
	 * Steps :
	 * 1. Go to Accounts
	 * 2. Verify navigation path -- "Home --> Manage --> Accounts"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Accounts",
			groups = { "sanity" })
			public void NavigateAccount_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Manage Accounts --> Accounts"
		 */
		ZAssert.assertTrue(app.zPageManageAccounts.zVerifyHeader(PageManageAccounts.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAccounts.zVerifyHeader(PageManageAccounts.Locators.MANAGE), "Verfiy the \"Manage Accounts\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAccounts.zVerifyHeader(PageManageAccounts.Locators.ACCOUNT), "Verfiy the \"Accounts\" text exists in navigation path");
		
	}

}
