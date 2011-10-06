package com.zimbra.qa.selenium.projects.ajax.tests.preferences.gui;

import java.util.HashMap;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class NavigateAway extends AjaxCommonTest {

	public NavigateAway() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String , String>() {
			private static final long serialVersionUID = 8043572657728539313L;
		{
		    put("zimbraFeatureMobileSyncEnabled", "TRUE");
		}};

	}

	/**
	 * Navigate to NavigateAwayDestinationTab, check for "Save Changes?" dialog
	 * @throws HarnessException
	 */
	protected boolean navigateAwayCheckWarning() throws HarnessException {
		logger.info("navigateAwayCheckWarning: start");
		
		boolean isVisible = true;
		
		// Navigate to mail
		//
		// This may cause problems if mail is not enabled for this account, but
		// let's assume that configuration is very rare.
		//
		app.zPagePreferences.zClick(PageMain.Locators.zAppbarMail);

		app.zPagePreferences.zWaitForBusyOverlay();
		
		// Check for the warning dialog
		DialogWarning warning = app.zPageMain.zGetWarningDialog(DialogWarning.DialogWarningID.PreferencesSaveChanges);
		ZAssert.assertNotNull(warning, "Verify the warning dialog object is created");
		isVisible = warning.zIsActive();
		
		if ( isVisible ) {
			
			// If the warning dialog is visible, discard
			// changes and throw exception
			warning.zClickButton(Button.B_NO);
			
		}
		
		logger.info("navigateAwayCheckWarning: finish");

		return (isVisible);
		
	}

	/**
	 * A table of preferences tree locators
	 * @return
	 */
	@DataProvider(name = "DataProviderPreferencePageToLocator")
	public Object[][] DataProviderPreferencePageToLocator() {
		return new Object[][] {
				new Object[] { TreeItem.General,				"css=td[id='CHANGE_PASSWORD_title']" },
				new Object[] { TreeItem.Mail,					null },
				new Object[] { TreeItem.MailAccounts,			null },
				new Object[] { TreeItem.MailFilters,			null },
				new Object[] { TreeItem.MailSignatures,			null },
				new Object[] { TreeItem.MailOutOfOffice,		null },
				new Object[] { TreeItem.MailTrustedAddresses,	null },
				new Object[] { TreeItem.AddressBook,			null },
				new Object[] { TreeItem.Calendar,				null },
				new Object[] { TreeItem.Sharing,				null },
				new Object[] { TreeItem.MobileDevices,			null },
				new Object[] { TreeItem.Notifications,			null },
				new Object[] { TreeItem.ImportExport,			null },
				new Object[] { TreeItem.Shortcuts,				null },
				new Object[] { TreeItem.QuickCommands,			null },
				new Object[] { TreeItem.Zimlets,				null },
		};
	}


	
	@Test(
			description = "If no changes made, verify that navigating away from preferences pages do not prompt 'Save Changes?'",
			groups = { "functional" },
			dataProvider = "DataProviderPreferencePageToLocator")
	public void NavigateAway_01(TreeItem treeItemLocator, String verificationLocator) throws HarnessException {

		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, treeItemLocator);

		
		if ( verificationLocator == null ) {
			
			// No locator defined.  Just sleep a bit
			SleepUtil.sleep(1000);
			
		} else {
			
			// Locator specified.  Make sure it is present and visible
			ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(verificationLocator), "Verify the page is present");
			ZAssert.assertTrue(app.zPagePreferences.zIsVisiblePerPosition(verificationLocator, 0, 0), "Verify the page is visible");
			
		}
		
		// There seems to be a bug that once the preferences are dirty, the client doesn't
		// clean it up.  So, all subsequent pages checks also fail.  Throw a harness
		// exception so that Account is cleaned up and started new.
		//
		// Original Code that should work:
		// ZAssert.assertFalse(navigateAwayCheckWarning(), "Verify the Save Changes? dialog was not visible");

		
		// Navigate to "mail" and check for the "Save Changes?" dialog
		boolean isVisible = navigateAwayCheckWarning();
		if ( isVisible ) {
			throw new HarnessException("Dialog 'Save Changes?' was present!");
		}
		
	}
}

		
