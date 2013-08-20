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
package com.zimbra.qa.selenium.projects.admin.tests.clientupload;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageClientUpload;

public class NavigateClientUpload extends AdminCommonTest {
	
	public NavigateClientUpload() {
		logger.info("New "+ NavigateClientUpload.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageClientUpload;
	}
	
	/**
	 * Testcase : Navigate to Client Upload page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Tools and Migraton --> Client Upload"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Client Upload",
			groups = { "sanity" })
			public void NavigateClientUpload_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Tools and Migraton --> Client Upload"
		 */
		ZAssert.assertTrue(app.zPageManageClientUpload.zVerifyHeader(PageManageClientUpload.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageClientUpload.zVerifyHeader(PageManageClientUpload.Locators.TOOLS_AND_MIGRATION), "Verfiy the \"Tools and Migration\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageClientUpload.zVerifyHeader(PageManageClientUpload.Locators.CLIENT_UPLOAD), "Verfiy the \"Client Upload\" text exists in navigation path");
		
	}

}
