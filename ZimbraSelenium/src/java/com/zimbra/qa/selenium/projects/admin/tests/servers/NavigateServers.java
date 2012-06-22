package com.zimbra.qa.selenium.projects.admin.tests.servers;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageServers;

public class NavigateServers extends AdminCommonTest {
	public NavigateServers() {
		logger.info("New "+ NavigateServers.class.getCanonicalName());

		// All tests start at the "Cos" page
		super.startingPage = app.zPageManageServers;
	}
	
	/**
	 * Testcase : Navigate to Servers page
	 * Steps :
	 * 1. Go to Accounts
	 * 2. Verify navigation path -- "Home --> Configure --> Servers"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Servers",
			groups = { "sanity" })
			public void NavigateServers_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Configure --> Servers"
		 */
		ZAssert.assertTrue(app.zPageManageServers.zVerifyHeader(PageManageServers.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageServers.zVerifyHeader(PageManageServers.Locators.CONFIGURE), "Verfiy the \"Configure\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageServers.zVerifyHeader(PageManageServers.Locators.SERVERS), "Verfiy the \"Servers\" text exists in navigation path");
		
	}

}
