package projects.mobile.tests.login;

import org.testng.annotations.Test;

import projects.mobile.tests.CommonTest;
import framework.util.HarnessException;
import framework.util.ZAssert;

public class BasicLogout extends CommonTest {
	
	public BasicLogout() {
		logger.info("New "+ BasicLogout.class.getCanonicalName());
	}
	
	@Test(	description = "Logout of the Mobile Client",
			groups = { "sanity" })
	public void BasicLogout01() throws HarnessException {
		
		// Login
		app.zPageMain.logout();
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageLogin.isActive(), "Verify that the account is logged out");
		
	}


}
