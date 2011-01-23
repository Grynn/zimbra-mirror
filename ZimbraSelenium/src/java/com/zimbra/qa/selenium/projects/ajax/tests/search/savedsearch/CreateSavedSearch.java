package com.zimbra.qa.selenium.projects.ajax.tests.search.savedsearch;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.SavedSearchFolderItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.search.DialogSaveSearch;


//TODO: add more in ContactItem.java

public class CreateSavedSearch extends AjaxCommonTest  {

	public CreateSavedSearch() {
		logger.info("New "+ CreateSavedSearch.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		
		
	}
	
	
	@Test(	description = "Create a basic saved search",
			groups = { "sanity" })
	public void CreateSavedSearch_01() throws HarnessException {				
				
			
		// Create the message data to be sent
		String name = "search" + ZimbraSeleniumProperties.getUniqueString();
		String query = "subject:(" + ZimbraSeleniumProperties.getUniqueString() + ")";
		

		// Search for the message
		app.zPageSearch.zAddSearchQuery(query);
		DialogSaveSearch dialog = (DialogSaveSearch)app.zPageSearch.zToolbarPressButton(Button.B_SEARCHSAVE);
		
		// Save the search
		dialog.zEnterFolderName(name);
		dialog.zClickButton(Button.B_OK);

		//Verify the saved search exists in the server
		SavedSearchFolderItem item = SavedSearchFolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(item, "Verify the saved search was created correctly");
		
		// Verify the saved search exists in the folder tree
		List<SavedSearchFolderItem> searches = app.zTreeMail.zListGetSavedSearches();
		ZAssert.assertNotNull(searches, "Verify the saved search list exists");

		// Make sure the message appears in the list
		SavedSearchFolderItem found = null;
		for (SavedSearchFolderItem s : searches) {
			logger.info("Subject: looking for "+ name +" found: "+ s.getName());
			if ( name.equals(s.getName()) ) {
				found = s;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the saved search is in the folder tree");

	}
}
