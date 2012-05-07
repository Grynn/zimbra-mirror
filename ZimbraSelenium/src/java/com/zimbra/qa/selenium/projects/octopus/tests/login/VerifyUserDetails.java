package com.zimbra.qa.selenium.projects.octopus.tests.login;

import junit.framework.Assert;

import mx4j.tools.config.DefaultConfigurationBuilder.New;

import org.testng.annotations.Test;

import com.zimbra.qa.QA.Bug;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageOctopus.Locators;

public class VerifyUserDetails extends OctopusCommonTest{
	
	public VerifyUserDetails()
	{
logger.info("New " + VerifyUserDetails.class.getCanonicalName());
		
		// All tests start at the Octopus page
		super.startingPage = app.zPageLogin;
	}
	@Bugs(ids="70100")
	@Test(description="Verify user display name when account display name is not set", groups={"functional"})
	public void verifyUserNameDisplayed() throws HarnessException
	{
		//create new account
		ZimbraAccount account = new ZimbraAccount();
		//Clear Display Name
		account.clearPref("displayName");
		
		account.provision();
		
		account.authenticate();		
		//Login to account
		app.zPageLogin.zLogin(account);
		
		//Assert if Display Name field contains email address if display name is not set.
		
	    ZAssert.assertTrue(app.zPageOctopus.sIsElementPresent(Locators.zUserDisplayName.locator+":contains("+account.EmailAddress+")"),
	    		"Check if user email address is displayed in user display name field if no display name is set");
	  	    
	}

}
