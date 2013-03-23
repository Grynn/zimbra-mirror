/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
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
