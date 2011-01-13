package com.zimbra.qa.selenium.projects.ajax.tests.preferences.general.searches;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ZimbraPrefIncludeSpamInSearch extends AjaxCommonTest {

	public ZimbraPrefIncludeSpamInSearch() {
		logger.info("New "+ ZimbraPrefIncludeSpamInSearch.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPagePreferences;

		// Make sure we are using an account with conversation view
		ZimbraAccount account = new ZimbraAccount();
		account.provision();
		account.authenticate();
		account.modifyPreference("zimbraPrefIncludeSpamInSearch", "TRUE");
			
		super.startingAccount = account;		
		
	}
	
	@Test(	description = "Verify zimbraPrefIncludeSpamInSearch setting when set to TRUE",
			groups = { "functional" })
	public void PreferencesGeneralSearches_zimbraPrefIncludeSpamInSearch_01() throws HarnessException {
		

		// Go to "General"
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.General);
		
		// Determine the status of the checkbox
		boolean checked = app.zPagePreferences.zGetCheckboxStatus("zimbraPrefIncludeSpamInSearch");
		
		// Since zimbraPrefIncludeSpamInSearch is set to TRUE, the checkbox should be checked
		ZAssert.assertTrue(checked, "Verify if zimbraPrefIncludeSpamInSearch is TRUE, the preference box is checked" );
		
		
	}



}
