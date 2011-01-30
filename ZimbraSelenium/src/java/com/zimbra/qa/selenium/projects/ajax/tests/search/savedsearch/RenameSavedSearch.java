package com.zimbra.qa.selenium.projects.ajax.tests.search.savedsearch;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.SavedSearchFolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogRenameFolder;


//TODO: add more in ContactItem.java

public class RenameSavedSearch extends AjaxCommonTest  {

	public RenameSavedSearch() {
		logger.info("New "+ RenameSavedSearch.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		
		
	}
	
	
	@Test(	description = "Rename a saved search",
			groups = { "smoke" })
	public void RenameSavedSearch_01() throws HarnessException {				
				
			
		// Create the message data to be sent
		String name1 = "search" + ZimbraSeleniumProperties.getUniqueString();
		String name2 = "search" + ZimbraSeleniumProperties.getUniqueString();
		String query = "subject:(" + ZimbraSeleniumProperties.getUniqueString() + ")";
		

		app.zGetActiveAccount().soapSend(
				"<CreateSearchFolderRequest xmlns='urn:zimbraMail'>" +
					"<search name='"+ name1 +"' query='"+ query +"' l='1'/>" +
				"</CreateSearchFolderRequest>");
		
		// Get the item
		SavedSearchFolderItem item = SavedSearchFolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		
		// Refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Right click on the search, select delete
		// TODO: can the folder rename dialog be reused?  Or, do we need DialogRenameSavedSearchFolder class?
		DialogRenameFolder dialog = (DialogRenameFolder) app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_RENAME, item);
		
		// Rename the search
		dialog.zSetNewName(name2);
		dialog.zClickButton(Button.B_OK);


		// Verify the saved search exists in the folder tree
		List<SavedSearchFolderItem> searches = app.zTreeMail.zListGetSavedSearches();
		ZAssert.assertNotNull(searches, "Verify the saved search list exists");

		// Make sure the old search no longer appears
		// Make sure the new search does appear
		SavedSearchFolderItem found1 = null;
		SavedSearchFolderItem found2 = null;
		for (SavedSearchFolderItem s : searches) {
			logger.info("Subject: looking for "+ name1 +" found: "+ s.getName());
			if ( name1.equals(s.getName()) ) {
				found1 = s;
				break;
			}
			logger.info("Subject: looking for "+ name2 +" found: "+ s.getName());
			if ( name2.equals(s.getName()) ) {
				found2 = s;
				break;
			}
		}
		ZAssert.assertNull(found1, "Verify the old saved search is not in the folder tree");
		ZAssert.assertNotNull(found2, "Verify the new saved search is in the folder tree");

	}
}
