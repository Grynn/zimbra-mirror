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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.toaster;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactGroupNew;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactGroupNew.Field;

public class CreateContactGroup extends AjaxCommonTest {

	public CreateContactGroup() {
		logger.info("New " + CreateContactGroup.class.getCanonicalName());

		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;

	}

	@Test(description = "Create a basic contact group with 2 addresses.  New -> Contact Group and verify toast msg", groups = { "functional" })
	public void CreateContactGroupToastMsg_01() throws HarnessException {

		// -- Data

		String groupName = "group" + ZimbraSeleniumProperties.getUniqueString();
		String member1 = "m" + ZimbraSeleniumProperties.getUniqueString()+ "@example.com";
		String member2 = "m" + ZimbraSeleniumProperties.getUniqueString()+ "@example.com";

		// -- GUI
		// Refresh the addressbook
		app.zPageAddressbook.zRefresh();

		// open contact group form
		FormContactGroupNew form = (FormContactGroupNew) app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_CONTACTGROUP);

		// fill in group name and email addresses
		form.zFillField(Field.GroupName, groupName);
		form.zFillField(Field.FreeFormAddress, member1);
		form.zFillField(Field.FreeFormAddress, member2);
		form.zSubmit();

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "Group Created","Verify toast message: Group Created");

	}

	@Test(description = "Create a contact group with existing contacts and verify toast msg", groups = { "functional" })
	public void CreateContactGroupToastMsg_02() throws HarnessException {

		// The contact group name
		String groupName = "group" + ZimbraSeleniumProperties.getUniqueString();

		// Create two contacts
		ContactItem contact1 = ContactItem.createContactItem(app.zGetActiveAccount());
		ContactItem contact2 = ContactItem.createContactItem(app.zGetActiveAccount());

		// -- GUI
		// Refresh
		app.zPageAddressbook.zRefresh();

		// open contact group form
		FormContactGroupNew form = (FormContactGroupNew) app.zPageAddressbook
		.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_CONTACTGROUP);

		// fill in group name
		form.zFillField(Field.GroupName, groupName);

		// Select Contact search
		form.zToolbarPressPulldown(Button.B_CONTACTGROUP_SEARCH_TYPE,Button.O_CONTACTGROUP_SEARCH_CONTACTS);
		form.zFillField(Field.SearchField, contact1.email);
		form.zToolbarPressButton(Button.B_SEARCH);
		form.zToolbarPressButton(Button.B_CONTACTGROUP_ADD_SEARCH_RESULT);

		form.zToolbarPressPulldown(Button.B_CONTACTGROUP_SEARCH_TYPE,Button.O_CONTACTGROUP_SEARCH_CONTACTS);
		form.zFillField(Field.SearchField, contact2.email);
		form.zToolbarPressButton(Button.B_SEARCH);
		form.zToolbarPressButton(Button.B_CONTACTGROUP_ADD_SEARCH_RESULT);
		
		// click Save
		form.zSubmit();
		
		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "Group Created","Verify toast message: Group Created");
	}
}
