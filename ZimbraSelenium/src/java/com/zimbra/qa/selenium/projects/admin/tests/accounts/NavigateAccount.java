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
	 * 2. Verify navigation path -- "Home --> Manage Accounts --> Accounts"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Accounts",
			groups = { "sanity" })
			public void NavigateAccount_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Manage Accounts --> Accounts"
		 */
		ZAssert.assertTrue(app.zPageManageAccounts.zVerifyHeader(PageManageAccounts.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAccounts.zVerifyHeader(PageManageAccounts.Locators.MANAGE_ACCOUNTS), "Verfiy the \"Manage Accounts\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAccounts.zVerifyHeader(PageManageAccounts.Locators.ACCOUNT), "Verfiy the \"Accounts\" text exists in navigation path");
		
	}

}
