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
