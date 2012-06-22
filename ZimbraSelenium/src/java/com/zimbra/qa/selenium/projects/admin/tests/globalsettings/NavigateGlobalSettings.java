package com.zimbra.qa.selenium.projects.admin.tests.globalsettings;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageGlobalSettings;

public class NavigateGlobalSettings extends AdminCommonTest {
	public NavigateGlobalSettings() {
		logger.info("New "+ NavigateGlobalSettings.class.getCanonicalName());

		// All tests start at the "Cos" page
		super.startingPage = app.zPageManageGlobalSettings;
	}
	
	/**
	 * Testcase : Navigate to Global Settings page
	 * Steps :
	 * 1. Go to Accounts
	 * 2. Verify navigation path -- "Home --> Configure --> Global Settings"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Global Settings",
			groups = { "sanity" })
			public void NavigateGlobalSettings_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Configure --> Global Settings"
		 */
		ZAssert.assertTrue(app.zPageManageGlobalSettings.zVerifyHeader(PageManageGlobalSettings.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageGlobalSettings.zVerifyHeader(PageManageGlobalSettings.Locators.CONFIGURE), "Verfiy the \"Configure\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageGlobalSettings.zVerifyHeader(PageManageGlobalSettings.Locators.GLOBAL_SETTINGS), "Verfiy the \"Global Settings\" text exists in navigation path");
		
	}

}
