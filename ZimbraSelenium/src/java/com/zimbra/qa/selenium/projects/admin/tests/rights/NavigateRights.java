package com.zimbra.qa.selenium.projects.admin.tests.rights;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageRights;

public class NavigateRights extends AdminCommonTest {
	public NavigateRights() {
		logger.info("New "+ NavigateRights.class.getCanonicalName());

		// All tests start at the "Cos" page
		super.startingPage = app.zPageManageRights;
	}
	
	/**
	 * Testcase : Navigate to Rights page
	 * Steps :
	 * 1. Go to Accounts
	 * 2. Verify navigation path -- "Home --> Configure --> Rights"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Rights",
			groups = { "sanity" })
			public void NavigateRights_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Configure --> Rights"
		 */
		ZAssert.assertTrue(app.zPageManageRights.zVerifyHeader(PageManageRights.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageRights.zVerifyHeader(PageManageRights.Locators.CONFIGURE), "Verfiy the \"Configure\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageRights.zVerifyHeader(PageManageRights.Locators.RIGHTS), "Verfiy the \"Rights\" text exists in navigation path");
		
	}

}
