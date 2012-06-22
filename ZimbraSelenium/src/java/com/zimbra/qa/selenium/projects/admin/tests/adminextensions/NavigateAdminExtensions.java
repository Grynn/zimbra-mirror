package com.zimbra.qa.selenium.projects.admin.tests.adminextensions;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageAdminExtensions;

public class NavigateAdminExtensions extends AdminCommonTest {
	public NavigateAdminExtensions() {
		logger.info("New "+ NavigateAdminExtensions.class.getCanonicalName());

		// All tests start at the "Cos" page
		super.startingPage = app.zPageManageAdminExtensions;
	}
	
	/**
	 * Testcase : Navigate to Admin Extensions page
	 * Steps :
	 * 1. Go to Accounts
	 * 2. Verify navigation path -- "Home --> Configure --> Admin Extensions"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Admin Extensions",
			groups = { "sanity" })
			public void NavigateAdminExtensions_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Configure --> Admin Extensions"
		 */
		ZAssert.assertTrue(app.zPageManageAdminExtensions.zVerifyHeader(PageManageAdminExtensions.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAdminExtensions.zVerifyHeader(PageManageAdminExtensions.Locators.CONFIGURE), "Verfiy the \"Configure\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAdminExtensions.zVerifyHeader(PageManageAdminExtensions.Locators.ADMIN_EXTENSIONS), "Verfiy the \"Admin Extensions\" text exists in navigation path");
		
	}

}
