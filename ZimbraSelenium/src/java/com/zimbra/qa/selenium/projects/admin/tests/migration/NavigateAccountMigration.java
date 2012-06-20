package com.zimbra.qa.selenium.projects.admin.tests.migration;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageAccountMigration;

public class NavigateAccountMigration extends AdminCommonTest {
	
	public NavigateAccountMigration() {
		logger.info("New "+ NavigateAccountMigration.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageAccountMigration;
	}
	
	/**
	 * Testcase : Navigate to Accounts Migration page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Tools and Migraton --> Account Migration"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Account Migration",
			groups = { "sanity" })
			public void NavigateAccountMigration_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Tools and Migraton --> Account Migration"
		 */
		ZAssert.assertTrue(app.zPageManageAccountMigration.zVerifyHeader(PageManageAccountMigration.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAccountMigration.zVerifyHeader(PageManageAccountMigration.Locators.TOOLS_AND_MIGRATION), "Verfiy the \"Tools and Migration\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAccountMigration.zVerifyHeader(PageManageAccountMigration.Locators.ACCOUNT_MIGRATION), "Verfiy the \"Account Migration\" text exists in navigation path");
		
	}

}
