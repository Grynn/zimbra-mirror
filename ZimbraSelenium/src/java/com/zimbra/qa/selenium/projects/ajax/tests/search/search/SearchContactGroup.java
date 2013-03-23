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

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactGroupItem.MemberItem;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class SearchContactGroup extends AjaxCommonTest {
	
	
	public SearchContactGroup() {
		logger.info("New "+ SearchContactGroup.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;

	}
	
	
	@Test(	description = "Search for an existing contact group",
			groups = { "functional" })
	public void SearchContactGroup_01() throws HarnessException {
		
		//-- Data
		
		// Create a contact group via Soap 
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
	  
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Remember to close the search view
		try {

			// search for group name
			app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_CONTACTS);	 		
			app.zPageSearch.zAddSearchQuery(group.groupName);
			app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
					
		    List<ContactItem> contacts = app.zPageSearch.zListGetContacts(); 
	        	
	        ZAssert.assertTrue(contacts.size()==1, "Verify only 1 contact group displayed");		
	        ZAssert.assertEquals(contacts.get(0).fileAs, group.groupName, "Verify contact group (" + group.groupName + ") is displayed");
	
		} finally {
			// Remember to close the search view
			app.zPageSearch.zClose();
		}
				

	}

	@Bugs(ids = "77950")
	@Test(	description = "Search for an existing contact group, by member",
			groups = { "deprecated" })
	public void SearchContactGroup_02() throws HarnessException {
		
		//-- Data
		
		// Create a contact group via Soap 
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		MemberItem member = group.getMemberList().get(0);
		
	  
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Remember to close the search view
		try {

			// search for group name
			app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_CONTACTS);	 		
			app.zPageSearch.zAddSearchQuery(member.getValue());
			app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
					
		    List<ContactItem> contacts = app.zPageSearch.zListGetContacts(); 
	        	
	        ZAssert.assertTrue(contacts.size()==1, "Verify only 1 contact group displayed");		
	        ZAssert.assertEquals(contacts.get(0).fileAs, group.groupName, "Verify contact group (" + group.groupName + ") is displayed");
	
		} finally {
			// Remember to close the search view
			app.zPageSearch.zClose();
		}
				

	}


	
}
