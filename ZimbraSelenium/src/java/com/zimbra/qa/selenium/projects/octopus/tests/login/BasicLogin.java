package com.zimbra.qa.selenium.projects.octopus.tests.login;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageOctopus.Locators;

public class BasicLogin extends OctopusCommonTest {

	public BasicLogin() {
		logger.info("New " + BasicLogin.class.getCanonicalName());

		// All tests start at the Octopus page
		super.startingPage = app.zPageOctopus;
		super.startingAccountPreferences = null;
	}

	@Test(description = "Login to the Octopus client", groups = { "functional" })
	public void BasicLogin01() throws HarnessException {
		// Login
		if (!startingPage.zIsActive())
			app.zPageLogin.zLogin(ZimbraAccount.AccountZMC());

		app.zPageOctopus.zWaitForActive();

		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageOctopus.zIsActive(),
				"Verify that the account is logged in");
	}

	@Test(description = "Upon Login to the Octopus client verify tabs are present", groups = { "sanity" })
	public void BasicLogin02() throws HarnessException {
		// Login
		if (!startingPage.zIsActive())
			app.zPageLogin.zLogin(ZimbraAccount.AccountZMC());

		app.zPageOctopus.zWaitForActive();

		// Verify My Files tab is present
		ZAssert.assertTrue(app.zPageOctopus
				.sIsElementPresent(Locators.zTabMyFiles.locator),
				"Verify My Files tab is present");
		
		// Verify Sharing tab is present
		ZAssert.assertTrue(app.zPageOctopus
				.sIsElementPresent(Locators.zTabSharing.locator),
				"Verify Sharing tab is present");
		
		// Verify Favorites tab is present
		ZAssert.assertTrue(app.zPageOctopus
				.sIsElementPresent(Locators.zTabFavorites.locator),
				"Verify Favorites tab is present");
		
		// Verify History tab is present
		ZAssert.assertTrue(app.zPageOctopus
				.sIsElementPresent(Locators.zTabHistory.locator),
				"Verify History tab is present");
		
		// Verify Trash tab is present
		ZAssert.assertTrue(app.zPageOctopus
				.sIsElementPresent(Locators.zTabTrash.locator),
				"Verify Trash tab is present");
		
		// Verify Search tab is present
		ZAssert.assertTrue(app.zPageOctopus
				.sIsElementPresent(Locators.zTabSearch.locator),
				"Verify Search tab is present");

	}
}
