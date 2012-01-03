package com.zimbra.qa.selenium.projects.admin.tests.domains;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageDomains;

public class NavigateDomain extends AdminCommonTest {
	public NavigateDomain() {
		logger.info("New "+ NavigateDomain.class.getCanonicalName());

		// All tests start at the "Domain" page
		super.startingPage = app.zPageManageDomains;
	}
	
	/**
	 * Testcase : Navigate to Domain page
	 * Steps :
	 * 1. Go to Domains
	 * 2. Verify navigation path -- "Home --> Configure --> Domains"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Domain",
			groups = { "sanity" })
			public void NavigateDomain_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Configure --> Domains"
		 */
		ZAssert.assertTrue(app.zPageManageDomains.zVerifyHeader(PageManageDomains.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageDomains.zVerifyHeader(PageManageDomains.Locators.CONFIGURE), "Verfiy the \"Configure\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageDomains.zVerifyHeader(PageManageDomains.Locators.DOMAIN), "Verfiy the \"Domain\" text exists in navigation path");
		
	}
}
