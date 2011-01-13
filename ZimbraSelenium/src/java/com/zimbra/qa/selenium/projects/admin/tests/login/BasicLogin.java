package com.zimbra.qa.selenium.projects.admin.tests.login;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;


public class BasicLogin extends AdminCommonTest {
	
	public BasicLogin() {
		logger.info("New "+ BasicLogin.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageLogin;
		super.startingAccount = null;
		
	}
	
	@Test(	description = "Login to the Admin Console",
			groups = { "sanity" })
	public void BasicLogin01() throws HarnessException {
		
		// Login
		app.zPageLogin.login(gAdmin);
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageMain.zIsActive(), "Verify that the account is logged in");
		
	}

	@Test(	description = "Login to the Admin Console as a different Admin Account",
			groups = { "smoke" })
	public void BasicLogin02() throws HarnessException {
		
		// Create a new AdminAccount
		ZimbraAdminAccount account = new ZimbraAdminAccount("admin"+ ZimbraSeleniumProperties.getUniqueString() + "@" + ZimbraSeleniumProperties.getStringProperty("testdomain"));
		account.provision();
		account.authenticate();
		
		// Login
		app.zPageLogin.login(account);
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageMain.zIsActive(), "Verify that the account is logged in");
		
	}


}
