package projects.admin.tests.topmenu.links;

import org.testng.annotations.Test;

import projects.admin.tests.CommonTest;
import framework.util.HarnessException;
import framework.util.ZAssert;

public class LoggedInUsername extends CommonTest {
	
	public LoggedInUsername() {
		logger.info("New "+ LoggedInUsername.class.getCanonicalName());
	}
	
	@Test(	description = "Verify the Top Menu displays the correct Admin username",
			groups = { "smoke" })
	public void TopMenu_LoggedInUsername_01() throws HarnessException {

		// Logout if required
		if (app.zMainPage.isActive())
			app.zMainPage.logout();
		
		// Login
		app.zLoginPage.login(admin);
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zMainPage.isActive(), "Verify that the account is logged in");
		ZAssert.assertEquals(app.zMainPage.getContainerUsername(), admin.EmailAddress, "Verify the correct account is logged in");
		
	}


}
