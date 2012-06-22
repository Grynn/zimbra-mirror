package com.zimbra.qa.selenium.projects.admin.tests.voicechatservice;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageVoiceChatService;

public class NavigateVoiceChatServices extends AdminCommonTest {
	public NavigateVoiceChatServices() {
		logger.info("New "+ NavigateVoiceChatServices.class.getCanonicalName());

		// All tests start at the "Cos" page
		super.startingPage = app.zPageManageVoiceChatService;
	}
	
	/**
	 * Testcase : Navigate to Voice/Chat Services page
	 * Steps :
	 * 1. Go to Accounts
	 * 2. Verify navigation path -- "Home --> Configure --> Voice/Chat Services"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Voice/Chat Services",
			groups = { "sanity" })
			public void NavigateVoiceChatServices_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Configure --> Voice/Chat Services"
		 */
		ZAssert.assertTrue(app.zPageManageVoiceChatService.zVerifyHeader(PageManageVoiceChatService.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageVoiceChatService.zVerifyHeader(PageManageVoiceChatService.Locators.CONFIGURE), "Verfiy the \"Configure\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageVoiceChatService.zVerifyHeader(PageManageVoiceChatService.Locators.VOICE_CHAT_SERVICE), "Verfiy the \"Voice/Chat Services\" text exists in navigation path");
		
	}

}
