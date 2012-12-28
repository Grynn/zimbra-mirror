package com.zimbra.qa.selenium.projects.admin.tests.serverstatus;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageServerStatus;

public class NavigateServerStatus extends AdminCommonTest {
	
	public NavigateServerStatus() {
		logger.info("New "+ NavigateServerStatus.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageServerStatus;
	}
	
	/**
	 * Testcase : Navigate to Client Upload page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Monitor --> Server Status"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Server Status",
			groups = { "sanity" })
			public void NavigateServerStatus_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Tools and Migraton --> Client Upload"
		 */
		ZAssert.assertTrue(app.zPageManageServerStatus.zVerifyHeader(PageManageServerStatus.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageServerStatus.zVerifyHeader(PageManageServerStatus.Locators.MONITOR), "Verfiy the \"Monitor\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageServerStatus.zVerifyHeader(PageManageServerStatus.Locators.SERVER_STATUS), "Verfiy the \"Server Status\" text exists in navigation path");
		
	}

}
