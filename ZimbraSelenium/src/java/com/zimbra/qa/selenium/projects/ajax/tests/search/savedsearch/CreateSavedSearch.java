/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
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

		// Remember to close the search window after saving
		try {

			// Search for the message
			app.zPageSearch.zAddSearchQuery(query);
			app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
			DialogSaveSearch dialog = (DialogSaveSearch)app.zPageSearch.zToolbarPressButton(Button.B_SAVE);
			
			// Save the search
			dialog.zEnterFolderName(name);
			dialog.zClickButton(Button.B_OK);
		
		} finally {
			
			// Remember to close the search window after saving
			app.zPageSearch.zClose();
			
		}

		
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
