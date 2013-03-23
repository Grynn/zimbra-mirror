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
package com.zimbra.qa.selenium.projects.admin.tests.serverstatus;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageServerStatus;

public class NavigateServerStatus extends AdminCommonTest {
	
	public NavigateServerStatus() {
		logger.info("New "+ NavigateServerStatus.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageServerStatus;
	}
	
	/**
	 * Testcase : Navigate to Client Upload page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Monitor --> Server Status"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Server Status",
			groups = { "sanity" })
			public void NavigateServerStatus_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Tools and Migraton --> Client Upload"
		 */
		ZAssert.assertTrue(app.zPageManageServerStatus.zVerifyHeader(PageManageServerStatus.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageServerStatus.zVerifyHeader(PageManageServerStatus.Locators.MONITOR), "Verfiy the \"Monitor\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageServerStatus.zVerifyHeader(PageManageServerStatus.Locators.SERVER_STATUS), "Verfiy the \"Server Status\" text exists in navigation path");
		
	}

}
