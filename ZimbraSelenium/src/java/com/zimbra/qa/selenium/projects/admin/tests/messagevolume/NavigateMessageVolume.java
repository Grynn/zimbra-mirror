package com.zimbra.qa.selenium.projects.admin.tests.messagevolume;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageMessageVolume;

public class NavigateMessageVolume extends AdminCommonTest {
	
	public NavigateMessageVolume() {
		logger.info("New "+ NavigateMessageVolume.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageMessageVolume;
	}
	
	/**
	 * Testcase : Navigate to Client Upload page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Monitor --> Message Volume"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Message Volume",
			groups = { "sanity" })
			public void NavigateMessageVolume_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Tools and Migraton --> Client Upload"
		 */
		ZAssert.assertTrue(app.zPageManageMessageVolume.zVerifyHeader(PageManageMessageVolume.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageMessageVolume.zVerifyHeader(PageManageMessageVolume.Locators.MONITOR), "Verfiy the \"Monitor\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageMessageVolume.zVerifyHeader(PageManageMessageVolume.Locators.MESSAGE_VOLUME), "Verfiy the \"Message Volume\" text exists in navigation path");
		
	}

}
