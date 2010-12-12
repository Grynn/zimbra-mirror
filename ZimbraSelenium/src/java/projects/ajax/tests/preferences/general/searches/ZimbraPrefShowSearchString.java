package projects.ajax.tests.preferences.general.searches;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.preferences.TreePreferences.TreeItem;
import framework.ui.Action;
import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;

public class ZimbraPrefShowSearchString extends AjaxCommonTest {

	public ZimbraPrefShowSearchString() {
		logger.info("New "+ ZimbraPrefShowSearchString.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPagePreferences;

		// Make sure we are using an account with conversation view
		ZimbraAccount account = new ZimbraAccount();
		account.provision();
		account.authenticate();
		account.modifyPreference("zimbraPrefShowSearchString", "TRUE");

			
		super.startingAccount = account;		
		
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
