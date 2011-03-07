package com.zimbra.qa.selenium.projects.html.tests.login;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.html.core.HtmlCommonTest;



public class BasicLogin extends HtmlCommonTest {
	
	public BasicLogin() {
		logger.info("New "+ BasicLogin.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageLogin;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Login to the Ajax Client",
			groups = { "sanity" })
	public void BasicLogin01() throws HarnessException {
		
		// Login
		app.zPageLogin.zLogin(ZimbraAccount.AccountZMC());
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageMain.zIsActive(), "Verify that the account is logged in");
		
	}


}
