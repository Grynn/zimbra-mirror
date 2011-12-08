package com.zimbra.qa.selenium.projects.desktop.tests.login;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.PageLogin;


public class LoginScreen extends AjaxCommonTest {

	public LoginScreen() {
		logger.info("New "+ LoginScreen.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageLogin;
		super.startingAccountPreferences = null;

	}

	@Test(	description = "Verify the label text on the Desktop client login screen with the account set",
			groups = { "smoke" })
	public void ZD_LoginScreen01() throws HarnessException {
	   String accountName = app.zPageLogin.sGetText(PageLogin.Locators.zAccountLabel);
	   String emailAddress = app.zPageLogin.sGetText(PageLogin.Locators.zEmailLabel);

	   ZAssert.assertEquals(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zBtnLoginDesktop),
	         true, "Desktop login button is present.");
	   ZAssert.assertEquals(app.zPageLogin.sIsVisible(PageLogin.Locators.zBtnLoginDesktop),
            true, "Desktop login button is visible.");

	   ZAssert.assertEquals(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zAddNewAccountButton),
            true, "Desktop Add Account Tab is present.");
      ZAssert.assertEquals(app.zPageLogin.sIsVisible(PageLogin.Locators.zAddNewAccountButton),
            true, "Desktop Add Account Tab is visible.");

		ZAssert.assertEquals(accountName, defaultAccountName,
		      "Verify the displayed label 'Account Name'");
      ZAssert.assertEquals(emailAddress, ZimbraAccount.AccountZDC().EmailAddress,
            "Verify the displayed label 'Email Address'");

	}

   @Test(   description = "Verify the label text on the Desktop client login screen without the account set",
         groups = { "smoke" })
   public void ZD_LoginScreen02() throws HarnessException {
      app.zPageLogin.zRemoveAccount();

      ZAssert.assertEquals(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zBtnLoginDesktop),
            false, "Desktop login button is present.");

      ZAssert.assertEquals(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zAddNewAccountButton),
            true, "Desktop Add Account Tab is present.");
      ZAssert.assertEquals(app.zPageLogin.sIsVisible(PageLogin.Locators.zAddNewAccountButton),
            true, "Desktop Add Account Tab is visible.");

      ZAssert.assertEquals(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zAccountLabel), false,
            "Verify the displayed label 'Account Name' is present");
      ZAssert.assertEquals(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zEmailLabel), false,
            "Verify the displayed label 'Email Address' is present");

   }

	@Test(	description = "Verify the copyright on the login screen contains the current year",
			groups = { "functional" })
	public void LoginScreen02() throws HarnessException {
		
		Calendar calendar = new GregorianCalendar();
		String thisYear = "" + calendar.get(Calendar.YEAR);
		
		String copyright = app.zPageLogin.sGetText(PageLogin.Locators.zDisplayedcopyright);
		
		String message = String.format("Verify the copyright (%s) on the login screen contains the current year (%s)", copyright, thisYear);
		ZAssert.assertStringContains(copyright, thisYear, message);
		

	}

	@Test(	description = "Verify initial focus on the login screen should be in username",
			groups = { "skip-functional" })
	public void LoginScreen03() throws HarnessException {
		
		// Get to the login screen
		// TODO: probably need to watch out for previously typed text
		// TODO: probably need to watch out for browser cache
		// TODO: maybe it is better just to reload the URL?
		app.zPageLogin.zNavigateTo();
		
		// Type a unique string into the browser
		String value = "foo" + ZimbraSeleniumProperties.getUniqueString();
		app.zPageLogin.zKeyboardTypeString(value);
		
		// Get the value of the username field
		String actual = app.zPageLogin.sGetValue(PageLogin.Locators.zInputUsername);
		
		// Verify typed text and the actual text match
		ZAssert.assertEquals(actual, value, "Verify the username has initial focus");
		
	}

	@Test(	description = "Verify tab order in the login screen (username, password, enter)",
			groups = { "unstable" })
	public void LoginScreen04() throws HarnessException {
		
		throw new HarnessException("implement me!");
		
	}


}
