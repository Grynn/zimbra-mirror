package projects.ajax.tests.login;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;

import framework.util.HarnessException;
import framework.util.ZAssert;

public class BasicLogout extends AjaxCommonTest {
	
	public BasicLogout() {
		logger.info("New "+ BasicLogout.class.getCanonicalName());
	}
	
	@Test(	description = "Logout of the Mobile Client",
			groups = { "sanity" })
	public void BasicLogout01() throws HarnessException {
		
		// Login
		app.zPageMain.zLogout();
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageLogin.zIsActive(), "Verify that the account is logged out");
		
	}


}
