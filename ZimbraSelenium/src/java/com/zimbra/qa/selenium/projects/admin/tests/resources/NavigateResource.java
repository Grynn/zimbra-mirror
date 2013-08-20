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
package com.zimbra.qa.selenium.projects.admin.tests.resources;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageResources;

public class NavigateResource extends AdminCommonTest {

	public NavigateResource() {
		logger.info("New "+ NavigateResource.class.getCanonicalName());

		// All tests start at the "Resource" page
		super.startingPage = app.zPageManageResources;
	}
	
	/**
	 * Testcase : Navigate to Resource page
	 * Steps :
	 * 1. Go to Resource
	 * 2. Verify navigation path -- "Home --> Manage --> Resources"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Resource",
			groups = { "sanity" })
			public void NavigateResource_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Manage Accounts --> Resources"
		 */
		ZAssert.assertTrue(app.zPageManageResources.zVerifyHeader(PageManageResources.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageResources.zVerifyHeader(PageManageResources.Locators.MANAGE), "Verfiy the \"Manage Accounts\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageResources.zVerifyHeader(PageManageResources.Locators.RESOURCE), "Verfiy the \"Resources\" text exists in navigation path");
	}
}
