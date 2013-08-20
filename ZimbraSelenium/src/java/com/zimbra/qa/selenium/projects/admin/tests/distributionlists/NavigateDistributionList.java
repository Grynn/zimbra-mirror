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
package com.zimbra.qa.selenium.projects.admin.tests.distributionlists;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageDistributionLists;

public class NavigateDistributionList extends AdminCommonTest {

	public NavigateDistributionList() {
		logger.info("New "+ NavigateDistributionList.class.getCanonicalName());

		// All tests start at the "DL" page
		super.startingPage = app.zPageManageDistributionList;
	}
	
	/**
	 * Testcase : Navigate to DL page
	 * Steps :
	 * 1. Go to DL
	 * 2. Verify navigation path -- "Home --> Manage --> Distribution Lists"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to DL",
			groups = { "sanity" })
			public void NavigateDistributionList_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Manage Accounts --> Distribution Lists"
		 */
		ZAssert.assertTrue(app.zPageManageDistributionList.zVerifyHeader(PageManageDistributionLists.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageDistributionList.zVerifyHeader(PageManageDistributionLists.Locators.MANAGE), "Verfiy the \"Manage Accounts\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageDistributionList.zVerifyHeader(PageManageDistributionLists.Locators.DISTRIBUTION_LIST), "Verfiy the \"Distribution Lists\" text exists in navigation path");
	}
}
