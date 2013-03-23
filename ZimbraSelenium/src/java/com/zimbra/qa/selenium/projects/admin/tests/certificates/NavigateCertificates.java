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
package com.zimbra.qa.selenium.projects.admin.tests.certificates;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageCertificates;

public class NavigateCertificates extends AdminCommonTest {
	public NavigateCertificates() {
		logger.info("New "+ NavigateCertificates.class.getCanonicalName());

		// All tests start at the "Cos" page
		super.startingPage = app.zPageManageCertificates;
	}
	
	/**
	 * Testcase : Navigate to Certificates page
	 * Steps :
	 * 1. Go to Accounts
	 * 2. Verify navigation path -- "Home --> Configure --> Certificates"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Certificates",
			groups = { "sanity" })
			public void NavigateCertificates_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Configure --> Certificates"
		 */
		ZAssert.assertTrue(app.zPageManageCertificates.zVerifyHeader(PageManageCertificates.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageCertificates.zVerifyHeader(PageManageCertificates.Locators.CONFIGURE), "Verfiy the \"Configure\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageCertificates.zVerifyHeader(PageManageCertificates.Locators.CERTIFICATES), "Verfiy the \"Certificates\" text exists in navigation path");
		
	}

}
