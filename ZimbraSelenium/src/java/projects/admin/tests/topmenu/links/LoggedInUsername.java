package projects.admin.tests.topmenu.links;

import org.testng.annotations.Test;

import projects.admin.tests.CommonTest;
import framework.util.HarnessException;
import framework.util.ZAssert;

public class LoggedInUsername extends CommonTest {
	
	public LoggedInUsername() {
		logger.info("New "+ LoggedInUsername.class.getCanonicalName());
		
		// Use default starting page and starting account
	}
	
	@Test(	description = "Verify the Top Menu displays the correct Admin username",
			groups = { "smoke" })
	public void TopMenu_LoggedInUsername_01() throws HarnessException {
		
		ZAssert.assertEquals(app.zMainPage.getContainerUsername(), gAdmin.EmailAddress, "Verify the correct account is logged in");
		
	}


}
