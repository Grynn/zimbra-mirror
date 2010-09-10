package projects.admin.tests.login;

import org.testng.annotations.Test;

import projects.admin.tests.CommonTest;
import framework.util.HarnessException;
import framework.util.ZAssert;

public class BasicLogin extends CommonTest {
	
	public BasicLogin() {
		logger.info("New "+ BasicLogin.class.getCanonicalName());
	}
	
	@Test(	description = "Login to the Admin Console",
			groups = { "sanity" })
	public void BasicLogin01() throws HarnessException {

		// Logout if required
		if (app.zMainPage.isActive())
			app.zMainPage.logout();
		
		// Login
		app.zLoginPage.login(admin);
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zMainPage.isActive(), "Verify that the account is logged in");
		
	}


}
