package com.zimbra.qa.selenium.projects.admin.tests.advancedstatistics;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageAdvancedStatistics;

public class NavigateAdvancedStatistics extends AdminCommonTest {
	
	public NavigateAdvancedStatistics() {
		logger.info("New "+ NavigateAdvancedStatistics.class.getCanonicalName());

		// All tests start at the "Monitor" page
		super.startingPage = app.zPageManageAdvancedStatistics;
	}
	
	/**
	 * Testcase : Navigate to Advanced Statistics page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Monitor --> Advanced Statistics"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Advanced Statistics",
			groups = { "sanity" })
			public void NavigateAdvancedStatistics_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Monitor --> Advanced Statistics"
		 */
		ZAssert.assertTrue(app.zPageManageAdvancedStatistics.zVerifyHeader(PageManageAdvancedStatistics.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAdvancedStatistics.zVerifyHeader(PageManageAdvancedStatistics.Locators.MONITOR), "Verfiy the \"Monitor\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAdvancedStatistics.zVerifyHeader(PageManageAdvancedStatistics.Locators.ADVANCED_STATISTICS), "Verfiy the \"Advanced Statistics\" text exists in navigation path");
		
	}

}
