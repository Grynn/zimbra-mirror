package projects.mobile.tests.login;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.testng.annotations.Test;

import projects.mobile.tests.CommonTest;
import projects.mobile.ui.PageLogin;
import framework.util.HarnessException;
import framework.util.ZAssert;

public class LoginScreen extends CommonTest {

	public LoginScreen() {
		logger.info("New "+ LoginScreen.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageLogin;
		super.startingAccount = null;

	}

	@Test(	description = "Verify the label text on the mobile client login screen",
			groups = { "smoke" })
	public void LoginScreen01() throws HarnessException {
		
		String username = app.zPageLogin.getText(PageLogin.displayedusername);
		ZAssert.assertEquals(username, app.getLocaleString("usernameLabel"), "Verify the displayed label 'username'");
		

	}
	
	@Test(	description = "Verify the copyright on the login screen contains the current year",
			groups = { "smoke" })
	public void LoginScreen02() {
		
		Calendar calendar = new GregorianCalendar();
		String thisYear = "" + calendar.get(Calendar.YEAR);
		
		String copyright = app.zPageLogin.getText(PageLogin.displayedcopyright);
		ZAssert.assertContains(thisYear, copyright, "Verify the copyright on the login screen contains the current year");
		

	}

}
