/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.undo;

import java.util.HashMap;
import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;

public class UndoDeleteContact extends AjaxCommonTest {

	public UndoDeleteContact() {
		logger.info("New " + UndoDeleteContact.class.getCanonicalName());
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;
		// Enable user preference checkboxes
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -8102550098554063084L;
			{
				put("zimbraPrefShowSelectionCheckbox", "TRUE");
			}
		};

	}

	@Test(description = "Undone deleted contact", groups = { "functional" })
	public void UndoDeleteContact_01() throws HarnessException {

		//-- Data
		
		// The contacts folder
		FolderItem contacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);

		// Create a contact item
		ContactItem contact = new ContactItem();
		contact.firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		contact.lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();
		contact.email = "email" + ZimbraSeleniumProperties.getUniqueString()+ "@domain.com";

		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" + "<cn >"
				+ "<a n='firstName'>" + contact.firstName + "</a>"
				+ "<a n='lastName'>" + contact.lastName + "</a>"
				+ "<a n='email'>" + contact.email + "</a>" + "</cn>"
				+ "</CreateContactRequest>");

		// -- GUI

		// Get a toaster object
		Toaster toast = app.zPageMain.zGetToaster();		

		// Refresh to get the contact into the client
		app.zPageAddressbook.zRefresh();

		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);

		// Wait for the toaster (if any) to close
		toast.zWaitForClose();
		
		// delete contact
		app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);		

		// Click undo from the toaster message
		toast.zWaitForActive();
		toast.zClickUndo();


		//Verify contact come back into Contacts folder
		ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
		ZAssert.assertNotNull(actual, "Verify the contact is not deleted from the addressbook");
		ZAssert.assertEquals(actual.getFolderId(), contacts.getId(), "Verify the contact is back in the contacts folder");

	}

	@Test(description = "Undone deleted a contact item selected with checkbox", groups = { "functional" })
	public void UndoDeleteContact_02() throws HarnessException {

		//-- Data
		
		// The contacts folder
		FolderItem contacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);

		// Create a contact item
		ContactItem contact = new ContactItem();
		contact.firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		contact.lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();
		contact.email = "email" + ZimbraSeleniumProperties.getUniqueString()+ "@domain.com";

		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" + "<cn >"
				+ "<a n='firstName'>" + contact.firstName + "</a>"
				+ "<a n='lastName'>" + contact.lastName + "</a>"
				+ "<a n='email'>" + contact.email + "</a>" + "</cn>"
				+ "</CreateContactRequest>");

		// -- GUI

		// Get a toaster object
		Toaster toast = app.zPageMain.zGetToaster();		

		// Refresh to get the contact into the client
		app.zPageAddressbook.zRefresh();

		// Select the contact's checkbox
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contact.firstName);

		// Wait for the toaster (if any) to close
		toast.zWaitForClose();
		
		// delete contact
		app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

		// Click undo from the toaster message
		toast.zWaitForActive();
		toast.zClickUndo();


		//Verify contact come back into Contacts folder
		ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
		ZAssert.assertNotNull(actual, "Verify the contact is not deleted from the addressbook");
		ZAssert.assertEquals(actual.getFolderId(), contacts.getId(), "Verify the contact is back in the contacts folder");

	}



	@Test(description = "Undone deleted multiple contact items", groups = { "functional" })
	public void UndoDeleteContact_03() throws HarnessException {


		//-- Data
		
		// The contacts folder
		FolderItem contacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);

		// Create a contact items
		ContactItem contact1 = new ContactItem();
		contact1.firstName = "First"+ ZimbraSeleniumProperties.getUniqueString();
		contact1.lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();
		contact1.email = "email" + ZimbraSeleniumProperties.getUniqueString()+ "@domain.com";
		contact1.fileAs = contact1.lastName + ", " + contact1.firstName;

		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" + "<cn >"
				+ "<a n='firstName'>" + contact1.firstName + "</a>"
				+ "<a n='lastName'>" + contact1.lastName + "</a>"
				+ "<a n='email'>" + contact1.email + "</a>" + "</cn>"
				+ "</CreateContactRequest>");

		ContactItem contact2 = new ContactItem();
		contact2.firstName = "First"+ ZimbraSeleniumProperties.getUniqueString();
		contact2.lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();
		contact2.email = "email" + ZimbraSeleniumProperties.getUniqueString()
		+ "@domain.com";
		contact2.fileAs = contact2.lastName + ", " + contact2.firstName;

		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" + "<cn >"
				+ "<a n='firstName'>" + contact2.firstName + "</a>"
				+ "<a n='lastName'>" + contact2.lastName + "</a>"
				+ "<a n='email'>" + contact2.email + "</a>" + "</cn>"
				+ "</CreateContactRequest>");

		ContactItem contact3 = new ContactItem();
		contact3.firstName = "First"
			+ ZimbraSeleniumProperties.getUniqueString();
		contact3.lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();
		contact3.email = "email" + ZimbraSeleniumProperties.getUniqueString()
		+ "@domain.com";
		contact3.fileAs = contact3.lastName + ", " + contact3.firstName;

		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" + "<cn >"
				+ "<a n='firstName'>" + contact3.firstName + "</a>"
				+ "<a n='lastName'>" + contact3.lastName + "</a>"
				+ "<a n='email'>" + contact3.email + "</a>" + "</cn>"
				+ "</CreateContactRequest>");

		// -- GUI

		// Get a toaster object
		Toaster toast = app.zPageMain.zGetToaster();		

		// Refresh to get the contact into the client
		app.zPageAddressbook.zRefresh();

		// Select the item
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contact1.fileAs);
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contact2.fileAs);
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contact3.fileAs);

		// Wait for the toaster (if any) to close
		toast.zWaitForClose();
		
		// delete 3 contacts
		app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

		// Click undo from the toaster message
		toast.zWaitForActive();
		toast.zClickUndo();



		//Verify all 3 contacts are come back into Contacts folder

		ContactItem actual1 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact1.firstName);
		ZAssert.assertNotNull(actual1, "Verify the contact1 is not deleted from the addressbook");
		ZAssert.assertEquals(actual1.getFolderId(), contacts.getId(), "Verify the contact is back in the contacts folder");

		ContactItem actual2 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact2.firstName);
		ZAssert.assertNotNull(actual2, "Verify the contact2  not deleted from the addressbook");
		ZAssert.assertEquals(actual2.getFolderId(), contacts.getId(), "Verify the contact is back in the contacts folder");

		ContactItem actual3 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact3.firstName);
		ZAssert.assertNotNull(actual3, "Verify the contact3 not deleted from the addressbook");
		ZAssert.assertEquals(actual3.getFolderId(), contacts.getId(), "Verify the contact is back in the contacts folder");

	}

}

