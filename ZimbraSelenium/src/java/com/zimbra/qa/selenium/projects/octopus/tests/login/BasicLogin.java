package com.zimbra.qa.selenium.projects.octopus.tests.login;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;

public class BasicLogin extends OctopusCommonTest {

	public BasicLogin() {
		logger.info("New " + BasicLogin.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageLogin;
		super.startingAccount = null;

	}

	@Test(description = "Login to the Octopus client", groups = { "nity" })
	public void BasicLogin01() throws HarnessException {
		// Login
		app.zPageLogin.zLogin(gAdmin);

		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageMain.zIsActive(),
				"Verify that the account is logged in");
	}
}
