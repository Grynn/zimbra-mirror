package com.zimbra.qa.selenium.projects.admin.tests.downloads;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageDownloads;

public class NavigateDownloads extends AdminCommonTest {
	public NavigateDownloads() {
		logger.info("New "+ NavigateDownloads.class.getCanonicalName());

		// All tests start at the "Domain" page
		super.startingPage = app.zPageDownloads;
	}
	
	/**
	 * Testcase : Navigate to Downloads page
	 * Steps :
	 * 1. Go to Downloads
	 * 2. Verify navigation path -- "Home --> Tools and Migration --> Downloads"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Downloads",
			groups = { "sanity" })
			public void NavigateDownloads_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Tools and Migration --> Downloads"
		 */
		ZAssert.assertTrue(app.zPageDownloads.zVerifyHeader(PageDownloads.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageDownloads.zVerifyHeader(PageDownloads.Locators.TOOLS_AND_MIGRATION), "Verfiy the \"Tools and Migration\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageDownloads.zVerifyHeader(PageDownloads.Locators.DOWNLOAD), "Verfiy the \"Downloads\" text exists in navigation path");
		
	}
}
