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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.ContactGroupItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.DisplayContactGroup;


public class ViewContactGroup extends AjaxCommonTest  {
	
	public ViewContactGroup() {
		logger.info("New "+ ViewContactGroup.class.getCanonicalName());

		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;		

	}


	@Test(
			description = "View a contact group", 
			groups = { "smoke" }
			)
	public void DisplayContactGroupInfo() throws HarnessException {

		//-- Data
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		// Refresh
		app.zPageAddressbook.zRefresh();		
		
		// Select the contact group
		DisplayContactGroup groupView = (DisplayContactGroup) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

		// verify groupname
		//
		// The GUI view shows the group name surrounded in quotes, so do a 'contains'
		//
		ZAssert.assertStringContains(
				groupView.zGetContactProperty(DisplayContactGroup.Field.Company),
				group.getName(),
				"Verify contact group email (" + group.getName() + ") displayed");	

		// verify group members
		for (ContactGroupItem.MemberItem m : group.getMemberList()) {
			
			String email = m.getValue();
			String locator = "css=div.ZmContactSplitView span[id$='']:contains('"+ email +"')";

			boolean present = app.zPageAddressbook.sIsElementPresent(locator);
			ZAssert.assertTrue(present, "Verify the member "+ email +" is present");

			boolean visible = app.zPageAddressbook.zIsVisiblePerPosition(locator, 0, 0);
			ZAssert.assertTrue(visible, "Verify the member "+ email +" is visible");
			
		}
		
	}
}

