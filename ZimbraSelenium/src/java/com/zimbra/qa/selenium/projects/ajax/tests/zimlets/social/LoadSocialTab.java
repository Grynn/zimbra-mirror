package com.zimbra.qa.selenium.projects.ajax.tests.zimlets.social;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class LoadSocialTab extends AjaxCommonTest {

	
	public LoadSocialTab() {
		logger.info("New "+ LoadSocialTab.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageSocial;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;


	}
	

	/**
	 * @throws HarnessException
	 */
	@Bugs(ids = "50123")
	@Test(	description = "Basic test case: Load the Social tab",
			groups = { "deprecated" })
	public void LoadSocialTab_01() throws HarnessException {
		
		ZAssert.assertTrue(app.zPageSocial.zIsActive(), "Verify the social page is active");

		
	}


}
