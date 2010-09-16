package projects.mobile.tests.login;

import org.testng.annotations.Test;

import projects.mobile.tests.CommonTest;
import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;

public class BasicLogin extends CommonTest {
	
	public BasicLogin() {
		logger.info("New "+ BasicLogin.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageLogin;
		super.startingAccount = null;
		
	}
	
	@Test(	description = "Login to the Mobile Client",
			groups = { "sanity" })
	public void BasicLogin01() throws HarnessException {
		
		// Login
		app.zPageLogin.login(ZimbraAccount.AccountZMC());
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageMain.isActive(), "Verify that the account is logged in");
		
	}


}
