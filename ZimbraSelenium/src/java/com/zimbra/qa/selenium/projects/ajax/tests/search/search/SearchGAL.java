/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.projects.ajax.tests.search.search;


import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class SearchGAL extends AjaxCommonTest {


	public SearchGAL() {
		logger.info("New "+ SearchGAL.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;

	}


	@Test(	description = "Search for a GAL contact",
			groups = { "functional" })
			public void SearchGAL_01() throws HarnessException {

		//-- Data

		// Create a GAL Account
		final String first = "first"+ ZimbraSeleniumProperties.getUniqueString();
		final String last = "last"+ ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount accountGAL = new ZimbraAccount();
		Map<String,String> attrs = new HashMap<String, String>() {
			private static final long serialVersionUID = -939087302049217526L;
			{
				put("givenName", first);
				put("sn", last);
				put("displayName", first + " " + last);
			}};
		accountGAL.setAccountPreferences(attrs);
		accountGAL.provision();
		accountGAL.authenticate();


		//-- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();


		// Remember to close the search view
		try {

			// search for firstname
			app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_GAL);	 		
			app.zPageSearch.zAddSearchQuery(first);
			app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);

			List<ContactItem> contacts = app.zPageSearch.zListGetContacts();
			ZAssert.assertNotNull(contacts, "Verify the message list exists");

			ZAssert.assertEquals(contacts.size(), 1, "Verify only the one message was returned");
			ZAssert.assertStringContains(contacts.get(0).getAttribute("fileAs", ""), first, "Verify the contact is shown in the results");

		} finally {
			// Remember to close the search view
			app.zPageSearch.zClose();
		}

	}

	@Test(	description = "Search for a non-existing GAL contact",
			groups = { "functional" })
			public void SearchGAL_02() throws HarnessException {

		//-- Data

		String doesnotexist = "contact" + ZimbraSeleniumProperties.getUniqueString();

		//-- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();


		// Remember to close the search view
		try {

			// search for firstname
			app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_GAL);	 		
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
