package com.zimbra.qa.selenium.projects.admin.tests.mobilesyncstatistics;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageMobileSyncStatistics;

public class NavigateMobileSyncStatistics extends AdminCommonTest {
	
	public NavigateMobileSyncStatistics() {
		logger.info("New "+ NavigateMobileSyncStatistics.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageMobileSyncStatistics;
	}
	
	/**
	 * Testcase : Navigate to Client Upload page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Monitor --> MobileSync Statistics"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to MobileSync Statistics",
			groups = { "sanity" })
			public void NavigateMobileSync_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Tools and Migraton --> Client Upload"
		 */
		ZAssert.assertTrue(app.zPageManageMobileSyncStatistics.zVerifyHeader(PageManageMobileSyncStatistics.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageMobileSyncStatistics.zVerifyHeader(PageManageMobileSyncStatistics.Locators.MONITOR), "Verfiy the \"Monitor\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageMobileSyncStatistics.zVerifyHeader(PageManageMobileSyncStatistics.Locators.MOBILESYNC_STATISTICS), "Verfiy the \"MobileSync Statistics\" text exists in navigation path");
		
	}

}
