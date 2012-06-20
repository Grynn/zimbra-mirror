package com.zimbra.qa.selenium.projects.admin.tests.searchmail;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageSearchMail;

public class NavigateSearchMail extends AdminCommonTest {
	
	public NavigateSearchMail() {
		logger.info("New "+ NavigateSearchMail.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageSearchMail;
	}
	
	/**
	 * Testcase : Navigate to Search Mail page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Tools and Migraton --> Search Mail"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Search Mail",
			groups = { "sanity" })
			public void NavigateAccountSearchMail_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Tools and Migraton --> Search Mail"
		 */
		ZAssert.assertTrue(app.zPageManageSearchMail.zVerifyHeader(PageManageSearchMail.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearchMail.zVerifyHeader(PageManageSearchMail.Locators.TOOLS_AND_MIGRATION), "Verfiy the \"Tools and Migration\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearchMail.zVerifyHeader(PageManageSearchMail.Locators.SEARCH_MAIL), "Verfiy the \"Search Mail\" text exists in navigation path");
		
	}

}
