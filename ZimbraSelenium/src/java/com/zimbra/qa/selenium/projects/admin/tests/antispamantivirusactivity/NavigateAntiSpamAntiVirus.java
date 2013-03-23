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
package com.zimbra.qa.selenium.projects.admin.tests.antispamantivirusactivity;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageAntiSpamAnitVirusActivity;

public class NavigateAntiSpamAntiVirus extends AdminCommonTest {
	
	public NavigateAntiSpamAntiVirus() {
		logger.info("New "+ NavigateAntiSpamAntiVirus.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageAntispamAntiVirusActivity;
	}
	
	/**
	 * Testcase : Navigate to Client Upload page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Monitor --> Anti-Spam/Anti-Virus Activity"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Anti-Spam/Anti-Virus Activity",
			groups = { "sanity" })
			public void NavigateAnitSpamAntiVirus_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Tools and Migraton --> Client Upload"
		 */
		ZAssert.assertTrue(app.zPageManageAntispamAntiVirusActivity.zVerifyHeader(PageManageAntiSpamAnitVirusActivity.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAntispamAntiVirusActivity.zVerifyHeader(PageManageAntiSpamAnitVirusActivity.Locators.MONITOR), "Verfiy the \"Monitor\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAntispamAntiVirusActivity.zVerifyHeader(PageManageAntiSpamAnitVirusActivity.Locators.ANTISPAM_ANTIVIRUS_ACTIVITY), "Verfiy the \"Anti-Spam/Anti-Virus Activity\" text exists in navigation path");
		
	}

}
