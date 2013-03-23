/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.search.search;


import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class SearchContact extends AjaxCommonTest {
	
	
	public SearchContact() {
		logger.info("New "+ SearchContact.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;

	}
	

	@Test(	description = "Search for a contact",
			groups = { "functional" })
	public void SearchContact_01() throws HarnessException {
		
		//-- Data
		
		// Create a contact via soap 
		ContactItem contactItem = ContactItem.createContactItem(app.zGetActiveAccount());
 
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		
		// Remember to close the search view
		try {

			// search for firstname
			app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_CONTACTS);	 		
			app.zPageSearch.zAddSearchQuery(contactItem.firstName);
			app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);

			List<ContactItem> contacts = app.zPageSearch.zListGetContacts();
			ZAssert.assertNotNull(contacts, "Verify the message list exists");

			ZAssert.assertEquals(contacts.size(), 1, "Verify only the one message was returned");
			ZAssert.assertStringContains(contacts.get(0).getAttribute("fileAs", ""), contactItem.firstName, "Verify the contact is shown in the results");
			
		} finally {
			// Remember to close the search view
			app.zPageSearch.zClose();
		}
				
	}
	
	@Test(	description = "Search for a non-existing contact",
			groups = { "functional" })
	public void SearchContact_02() throws HarnessException {
		
		//-- Data
		
		String doesnotexist = "contact" + ZimbraSeleniumProperties.getUniqueString();
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		
		// Remember to close the search view
		try {

			// search for firstname
			app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_CONTACTS);	 		
			app.zPageSearch.zAddSearchQuery(doesnotexist);
			app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);

			List<ContactItem> contacts = app.zPageSearch.zListGetContacts();
			ZAssert.assertNotNull(contacts, "Verify the message list exists");

			ZAssert.assertEquals(contacts.size(), 0, "Verify no results");
			
		} finally {
			// Remember to close the search view
			app.zPageSearch.zClose();
		}
			
	}


}
