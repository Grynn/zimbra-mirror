package projects.ajax.tests.preferences.general.searches;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.TreePreferences.TreeItem;
import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;

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
		app.zTreePreferences.zClickTreeItem(TreeItem.General);
		
		// Determine the status of the checkbox
		boolean checked = app.zPagePreferences.zGetCheckboxStatus("zimbraPrefIncludeSpamInSearch");
		
		// Since zimbraPrefIncludeSpamInSearch is set to TRUE, the checkbox should be checked
		ZAssert.assertTrue(checked, "Verify if zimbraPrefIncludeSpamInSearch is TRUE, the preference box is checked" );
		
		
	}



}
