package com.zimbra.qa.selenium.projects.admin.tests.aliases;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageAliases;

public class NavigateAlias extends AdminCommonTest {

	public NavigateAlias() {
		logger.info("New "+ NavigateAlias.class.getCanonicalName());

		// All tests start at the "Aliases" page
		super.startingPage = app.zPageManageAliases;
	}
	
	/**
	 * Testcase : Navigate to Aliases page
	 * Steps :
	 * 1. Go to Accounts
	 * 2. Verify navigation path -- "Home --> Manage Accounts --> Aliases"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Aliases",
			groups = { "sanity" })
			public void NavigateAlias_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Manage Accounts --> Aliases"
		 */
		ZAssert.assertTrue(app.zPageManageAliases.zVerifyHeader(PageManageAliases.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAliases.zVerifyHeader(PageManageAliases.Locators.MANAGE_ACCOUNTS), "Verfiy the \"Manage Accounts\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAliases.zVerifyHeader(PageManageAliases.Locators.ALIAS), "Verfiy the \"Aliases\" text exists in navigation path");
		
	}

}