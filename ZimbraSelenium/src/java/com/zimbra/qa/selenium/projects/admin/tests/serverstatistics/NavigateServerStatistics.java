package com.zimbra.qa.selenium.projects.admin.tests.serverstatistics;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageServerStatistics;

public class NavigateServerStatistics extends AdminCommonTest {
	
	public NavigateServerStatistics() {
		logger.info("New "+ NavigateServerStatistics.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageServerStatistics;
	}
	
	/**
	 * Testcase : Navigate to Client Upload page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Monitor --> Server Statistics"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Server Statistics",
			groups = { "sanity" })
			public void NavigateServerStatistics_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Tools and Migraton --> Client Upload"
		 */
		ZAssert.assertTrue(app.zPageManageServerStatistics.zVerifyHeader(PageManageServerStatistics.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageServerStatistics.zVerifyHeader(PageManageServerStatistics.Locators.MONITOR), "Verfiy the \"Monitor\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageServerStatistics.zVerifyHeader(PageManageServerStatistics.Locators.SERVER_STATISTICS), "Verfiy the \"Server Statistics\" text exists in navigation path");
		
	}

}
