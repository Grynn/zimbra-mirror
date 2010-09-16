package projects.admin.tests.login;

import org.testng.annotations.Test;

import projects.admin.tests.CommonTest;
import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraAdminAccount;
import framework.util.ZimbraSeleniumProperties;

public class BasicLogin extends CommonTest {
	
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
		ZAssert.assertTrue(app.zPageMain.isActive(), "Verify that the account is logged in");
		
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
		ZAssert.assertTrue(app.zPageMain.isActive(), "Verify that the account is logged in");
		
	}


}
