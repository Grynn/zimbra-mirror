package com.zimbra.qa.selenium.projects.ajax.tests.preferences.general.searches;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ZimbraPrefShowSearchString extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public ZimbraPrefShowSearchString() {
		logger.info("New "+ ZimbraPrefShowSearchString.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPagePreferences;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = new HashMap<String, String>() {{
				    put("zimbraPrefShowSearchString", "TRUE");
				}};
			
		
	}
	

	@Test(	description = "Verify zimbraPrefShowSearchString setting when set to TRUE",
			groups = { "functional" })
	public void PreferencesGeneralSearches_zimbraPrefShowSearchString_01() throws HarnessException {
		

		// Go to "General"
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.General);
		
		// Determine the status of the checkbox
		boolean checked = app.zPagePreferences.zGetCheckboxStatus("zimbraPrefShowSearchString");
		
		// Since zimbraPrefIncludeSpamInSearch is set to TRUE, the checkbox should be checked
		ZAssert.assertTrue(checked, "Verify if zimbraPrefShowSearchString is TRUE, the preference box is checked" );
		
		
	}


}
