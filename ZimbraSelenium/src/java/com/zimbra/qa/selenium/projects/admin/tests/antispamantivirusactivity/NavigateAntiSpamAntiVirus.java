package com.zimbra.qa.selenium.projects.admin.tests.antispamantivirusactivity;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageAntiSpamAnitVirusActivity;

public class NavigateAntiSpamAntiVirus extends AdminCommonTest {
	
	public NavigateAntiSpamAntiVirus() {
		logger.info("New "+ NavigateAntiSpamAntiVirus.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageAntispamAntiVirusActivity;
	}
	
	/**
	 * Testcase : Navigate to Client Upload page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Monitor --> Anti-Spam/Anti-Virus Activity"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Anti-Spam/Anti-Virus Activity",
			groups = { "sanity" })
			public void NavigateAnitSpamAntiVirus_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Tools and Migraton --> Client Upload"
		 */
		ZAssert.assertTrue(app.zPageManageAntispamAntiVirusActivity.zVerifyHeader(PageManageAntiSpamAnitVirusActivity.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAntispamAntiVirusActivity.zVerifyHeader(PageManageAntiSpamAnitVirusActivity.Locators.MONITOR), "Verfiy the \"Monitor\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageAntispamAntiVirusActivity.zVerifyHeader(PageManageAntiSpamAnitVirusActivity.Locators.ANTISPAM_ANTIVIRUS_ACTIVITY), "Verfiy the \"Anti-Spam/Anti-Virus Activity\" text exists in navigation path");
		
	}

}
