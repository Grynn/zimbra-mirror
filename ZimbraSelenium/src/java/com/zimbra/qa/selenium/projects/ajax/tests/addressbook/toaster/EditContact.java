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
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactNew.Field;

public class EditContact extends AjaxCommonTest {
	public EditContact() {
		logger.info("New " + EditContact.class.getCanonicalName());

		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;

	}

	@Test(description = "Edit a contact item, click Edit on toolbar and verify toast msg", groups = { "functional" })
	public void EditContactToastMsg_01() throws HarnessException {

		// Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());

		// The new first name
		String firstname = "new" + ZimbraSeleniumProperties.getUniqueString();

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.getName());

		// Click "Edit" from the toolbar
		FormContactNew form = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);

		// Change the first name
		form.zFillField(Field.FirstName, firstname);
		form.zToolbarPressButton(Button.B_SAVE);

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "Contact Saved","Verify toast message: Contact Saved");

	}

	@Test(description = "Edit a contact item, Right click then click Edit and verify toast msg", groups = { "functional" })
	public void EditContactToastMsg_02() throws HarnessException {

		// Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());

		// The new first name
		String firstname = "new" + ZimbraSeleniumProperties.getUniqueString();

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Rigth Click -> "Edit"
		FormContactNew form = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, contact.getName());

		// Change the first name
		form.zFillField(Field.FirstName, firstname);
		form.zToolbarPressButton(Button.B_SAVE);

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "Contact Saved","Verify toast message: Contact Saved");

	}

	@Test(description = "Edit a contact item, double click the contact and verify toast msg", groups = { "functional" })
	public void EditContactToastMsg_03() throws HarnessException {

		// Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());

		// The new first name
		String firstname = "new" + ZimbraSeleniumProperties.getUniqueString();

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Double click contact
		FormContactNew form = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_DOUBLECLICK, contact.getName());

		// Change the first name
		form.zFillField(Field.FirstName, firstname);
		form.zToolbarPressButton(Button.B_SAVE);

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "Contact Saved","Verify toast message: Contact Saved");

	}
}
