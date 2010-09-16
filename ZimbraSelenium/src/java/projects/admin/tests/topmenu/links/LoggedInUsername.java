package projects.admin.tests.topmenu.links;

import org.testng.annotations.Test;

import projects.admin.tests.CommonTest;
import projects.admin.ui.PageMain;
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
		
		// The displayed name is part of the full email address.  
		// For Example: 
		// Displayed:    globaladmin1284592683
		// Actual Email: globaladmin12845926837811@qa62.lab.zimbra.com
		//

		// Check that the displayed name is contained in the email
		String displayed = app.zPageMain.getText(PageMain.Zskin_container_username);	
		ZAssert.assertContains(displayed, gAdmin.EmailAddress, "Verify the correct account display name is shown");
		
	}


}
