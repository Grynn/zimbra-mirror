package com.zimbra.qa.selenium.projects.admin.tests.resources;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageResources;

public class NavigateResource extends AdminCommonTest {

	public NavigateResource() {
		logger.info("New "+ NavigateResource.class.getCanonicalName());

		// All tests start at the "Resource" page
		super.startingPage = app.zPageManageResources;
	}
	
	/**
	 * Testcase : Navigate to Resource page
	 * Steps :
	 * 1. Go to Resource
	 * 2. Verify navigation path -- "Home --> Manage Accounts --> Resources"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Resource",
			groups = { "sanity" })
			public void NavigateResource_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Manage Accounts --> Resources"
		 */
		ZAssert.assertTrue(app.zPageManageResources.zVerifyHeader(PageManageResources.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageResources.zVerifyHeader(PageManageResources.Locators.MANAGE_ACCOUNTS), "Verfiy the \"Manage Accounts\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageResources.zVerifyHeader(PageManageResources.Locators.RESOURCE), "Verfiy the \"Resources\" text exists in navigation path");
	}
}
