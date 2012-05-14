package com.zimbra.qa.selenium.projects.octopus.tests.login;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageLogin;
import com.zimbra.qa.selenium.projects.octopus.ui.PageLogin.Locators;

public class LoginPolicy extends OctopusCommonTest
{

	public LoginPolicy()
	{
		super.startingPage= app.zPageLogin;
		//	super.startingAccountPreferences=null;
	}

	@Test(description="Check if user is forced to change password if required preferances are set", groups={"smoke"})
	public void VerifyForceChangePassword() throws HarnessException
	{
		//Create new account
		ZimbraAccount acct = new ZimbraAccount();

		//Set preferences for password must change policy
		acct.setPref("zimbraPasswordMustChange", "TRUE");
		acct.provision();

		//Enter login details and click login button
		app.zPageLogin.zSetLoginName(acct.EmailAddress);
		app.zPageLogin.zSetLoginPassword(acct.Password);
		app.zPageLogin.sClick(Locators.zBtnLogin);

		//Verify if new password and confirm password fields are present.
		ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zInputNewPassword),"Verify if input field for new password is present");
		ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zInputConfirmPassword),"Verify if input field for Confirm Password password is present");

	}

	@Test(description="Verify if error is displayed to user when account status is Not active.", groups={"smoke1"})
	public void verifyErrorDisplayedForInactiveAccounts()throws HarnessException
	{
		//Create array list of various account status.
		String [] accountStatus ={"closed","locked","pending","maintenance"};
		
		for (String status : accountStatus) {


			//Create new account
			ZimbraAccount acct = new ZimbraAccount();

			//Set account status
			acct.setPref("zimbraAccountStatus", status);
			acct.provision();

			//Enter login details and click login button
			app.zPageLogin.zSetLoginName(acct.EmailAddress);
			app.zPageLogin.zSetLoginPassword(acct.Password);
			app.zPageLogin.sClick(Locators.zBtnLogin);
			
			app.zPageOctopus.zWaitForElementPresent(Locators.zLoginErrorPanel);

			boolean isErrorPresent =app.zPageLogin.sIsElementPresent(Locators.zLoginErrorPanel);
          // Verify the required condition.  
			ZAssert.assertTrue(isErrorPresent, "Verify if error message is displayed for " + status+"account");

		}
	}

}
