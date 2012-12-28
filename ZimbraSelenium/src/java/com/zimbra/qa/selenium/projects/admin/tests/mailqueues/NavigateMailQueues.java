package com.zimbra.qa.selenium.projects.admin.tests.mailqueues;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageMailQueues;

public class NavigateMailQueues extends AdminCommonTest {
	
	public NavigateMailQueues() {
		logger.info("New "+ NavigateMailQueues.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageMailQueues;
	}
	
	/**
	 * Testcase : Navigate to Client Upload page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Monitor --> Mail Queues"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Mail Queues",
			groups = { "sanity" })
			public void NavigateMailQueues_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Tools and Migraton --> Client Upload"
		 */
		ZAssert.assertTrue(app.zPageManageMailQueues.zVerifyHeader(PageManageMailQueues.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageMailQueues.zVerifyHeader(PageManageMailQueues.Locators.MONITOR), "Verfiy the \"Monitor\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageMailQueues.zVerifyHeader(PageManageMailQueues.Locators.MAIL_QUEUES_TEXT), "Verfiy the \"Mail Queues\" text exists in navigation path");
		
	}

}
