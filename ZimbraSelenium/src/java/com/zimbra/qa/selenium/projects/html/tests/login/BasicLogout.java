package com.zimbra.qa.selenium.projects.html.tests.login;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.html.core.HtmlCommonTest;



public class BasicLogout extends HtmlCommonTest {
	
	public BasicLogout() {
		logger.info("New "+ BasicLogout.class.getCanonicalName());
	}
	
	@Test(	description = "Logout of the Mobile Client",
			groups = { "sanity" })
	public void BasicLogout01() throws HarnessException {
		
		// Login
		app.zPageMain.zLogout();
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageLogin.zIsActive(), "Verify that the account is logged out");
		
	}


}
