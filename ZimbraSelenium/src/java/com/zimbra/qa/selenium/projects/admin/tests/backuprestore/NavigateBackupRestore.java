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
