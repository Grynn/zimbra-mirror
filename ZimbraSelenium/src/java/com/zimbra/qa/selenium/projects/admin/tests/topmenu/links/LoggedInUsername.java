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
