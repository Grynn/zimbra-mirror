package com.zimbra.qa.selenium.projects.desktop.tests.login;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;

public class BasicLogin extends AjaxCommonTest {
	
	public BasicLogin() {	
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
