/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.admin.tests.topmenu.links;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;


public class LoggedInUsername extends AdminCommonTest {
	
	public LoggedInUsername() {
		logger.info("New "+ LoggedInUsername.class.getCanonicalName());
		
		// Use default starting page and starting account
	}
	
	@Test(	description = "Verify the Top Menu displays the correct Admin username",
			groups = { "smoke" })
	public void TopMenu_LoggedInUsername_01() throws HarnessException {
		
		// The displayed name is part of the full email address.  
		// For Example: 
		// Displayed:    globaladmin1284592683
		// Actual Email: globaladmin12845926837811@qa62.lab.zimbra.com
		//

		// Check that the displayed name is contained in the email
		String displayed = app.zPageMain.sGetText(PageMain.Locators.zSkinContainerUsername);
		ZAssert.assertStringContains("globaladmi...", displayed.split("@")[0], "Verify the correct account display name is shown");
		
	}


}
