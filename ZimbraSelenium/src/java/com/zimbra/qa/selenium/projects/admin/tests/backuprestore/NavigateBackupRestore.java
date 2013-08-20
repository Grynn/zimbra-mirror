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
package com.zimbra.qa.selenium.projects.admin.tests.backuprestore;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageBackups;

public class NavigateBackupRestore extends AdminCommonTest {
	
	public NavigateBackupRestore() {
		logger.info("New "+ NavigateBackupRestore.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageBackups;
	}
	
	/**
	 * Testcase : Navigate to Backups page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Tools and Migraton --> Backups"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Backups",
			groups = { "sanity" })
			public void NavigateMigration_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Tools and Migraton --> Backups"
		 */
		ZAssert.assertTrue(app.zPageManageBackups.zVerifyHeader(PageManageBackups.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageBackups.zVerifyHeader(PageManageBackups.Locators.TOOLS_AND_MIGRATION), "Verfiy the \"Tools and Migration\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageBackups.zVerifyHeader(PageManageBackups.Locators.BACKUPS), "Verfiy the \"Backups\" text exists in navigation path");
		
	}

}
