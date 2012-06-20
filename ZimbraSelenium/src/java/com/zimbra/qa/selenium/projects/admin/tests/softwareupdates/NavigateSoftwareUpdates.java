package com.zimbra.qa.selenium.projects.admin.tests.softwareupdates;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageSoftwareUpdates;

public class NavigateSoftwareUpdates extends AdminCommonTest {
	
	public NavigateSoftwareUpdates() {
		logger.info("New "+ NavigateSoftwareUpdates.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageSoftwareUpdates;
	}
	
	/**
	 * Testcase : Navigate to Software Updates page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Tools and Migraton --> Software Updates"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Software Updates",
			groups = { "sanity" })
			public void NavigateAccountMigration_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Tools and Migraton --> Software Updates"
		 */
		ZAssert.assertTrue(app.zPageManageSoftwareUpdates.zVerifyHeader(PageManageSoftwareUpdates.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSoftwareUpdates.zVerifyHeader(PageManageSoftwareUpdates.Locators.TOOLS_AND_MIGRATION), "Verfiy the \"Tools and Migration\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSoftwareUpdates.zVerifyHeader(PageManageSoftwareUpdates.Locators.SOFTWARE_UPDATES), "Verfiy the \"Software Updates\" text exists in navigation path");
		
	}

}
