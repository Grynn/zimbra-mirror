/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.DialogEditFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder.FolderColor;


public class EditProperties extends AjaxCommonTest {

	public EditProperties() {
		logger.info("New "+ EditProperties.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;

	}


	@Test(
			description = "Edit a folder, change the color (Context menu -> Edit)", 
			groups = { "functional" }
			)
	public void ChangeColorOfTopLevelFolder() throws HarnessException {

		//-- Data

		FolderItem userRoot= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);

		// Create a folder
		String name = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name + "' view='contact' l='"+ userRoot.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);


		//-- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Change the folder's color using context menu
		DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folderItem);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");

		dialog.zSetNewColor(FolderColor.Blue);
		dialog.zClickButton(Button.B_OK);



		//-- Verification

		// Get the folder again
		FolderItem actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);

		ZAssert.assertEquals(actual.getColor(), "1", "Verify the color of the folder is set to blue (1)");

	}

	@Test(
			description = "Edit a folder, change the color (Context menu -> Edit)",
			groups = { "functional" }
			)
	public void ChangeColorOfSystemFolders() throws HarnessException {

		//-- Data

		FolderItem contacts= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);



		//-- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Change the folder's color using context menu
		DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, contacts);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");

		dialog.zSetNewColor(FolderColor.Green);
		dialog.zClickButton(Button.B_OK);



		//-- Verification

		// Get the folder again
		FolderItem actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), contacts.getName());

		ZAssert.assertEquals(actual.getColor(), "3", "Verify the color of the folder is set to green (3)");

	}

	@Test(
			description = "Edit a folder, change the color (Context menu -> Edit)", 
			groups = { "functional" })
	public void ChangeColorOfSubFolder() throws HarnessException {

		//-- Data

		FolderItem contacts= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);

		// Create a folder
		String name = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name + "' view='contact' l='"+ contacts.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);


		//-- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Change the folder's color using context menu
		DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folderItem);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");

		dialog.zSetNewColor(FolderColor.Red);
		dialog.zClickButton(Button.B_OK);



		//-- Verification

		// Get the folder again
		FolderItem actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);

		ZAssert.assertEquals(actual.getColor(), "5", "Verify the color of the folder is set to red (5)");
	}


	@Test(
			description = "Edit a folder, change name(Context menu -> Edit)", 
			groups = { "smoke" }
			)
	public void ChangeNameOfTopLevelFolder() throws HarnessException {

		//-- Data

		FolderItem userRoot= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);

		// Create a folder
		String name = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		String newname = "ab"+ ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name + "' view='contact' l='"+ userRoot.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);


		//-- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Change the folder's color using context menu
		DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folderItem);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");

		dialog.zSetNewName(newname);
		dialog.zClickButton(Button.B_OK);



		//-- Verification

		// Get the folder again
		FolderItem actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNull(actual, "Verify the old addressbook does not exist");

		actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), newname);
		ZAssert.assertNotNull(actual, "Verify the new addressbook exists");


	}


	@Test(
			description = "Edit a folder, change name(Context menu -> Edit)", 
			groups = { "smoke" }
			)
	public void ChangeNameOfSubFolder() throws HarnessException {


		//-- Data

		FolderItem contacts= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);

		// Create a folder
		String name = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		String newname = "ab"+ ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name + "' view='contact' l='"+ contacts.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);


		//-- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Change the folder's color using context menu
		DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folderItem);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");

		dialog.zSetNewName(newname);
		dialog.zClickButton(Button.B_OK);



		//-- Verification

		// Get the folder again
		FolderItem actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNull(actual, "Verify the old addressbook does not exist");

		actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), newname);
		ZAssert.assertNotNull(actual, "Verify the new addressbook exists");

	}





	@Test(
			description = "Edit a top level folder, change name and color Context menu -> Edit)", 
			groups = { "functional" })
	public void ChangeNameColorOfTopLevelFolder() throws HarnessException {


		//-- Data

		FolderItem userRoot = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);

		// Create a folder
		String name = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		String newname = "ab"+ ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name + "' view='contact' l='"+ userRoot.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);


		//-- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Change the folder's color using context menu
		DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folderItem);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");

		dialog.zSetNewColor(FolderColor.Yellow);
		dialog.zSetNewName(newname);
		dialog.zClickButton(Button.B_OK);



		//-- Verification

		// Get the folder again
		FolderItem actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNull(actual, "Verify the old addressbook does not exist");

		actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), newname);
		ZAssert.assertNotNull(actual, "Verify the new addressbook exists");
		ZAssert.assertEquals(actual.getColor(), "6", "Verify the color of the folder is set to yellow (6)");

	}


	@Test(
			description = "Edit a subfolder, change name and color Context menu -> Edit)", 
			groups = { "functional" })
	public void ChangeNameColorOfSubFolder() throws HarnessException {


		//-- Data

		FolderItem contacts= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);

		// Create a folder
		String name = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		String newname = "ab"+ ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name + "' view='contact' l='"+ contacts.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);


		//-- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Change the folder's color using context menu
		DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folderItem);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");

		dialog.zSetNewColor(FolderColor.Orange);
		dialog.zSetNewName(newname);
		dialog.zClickButton(Button.B_OK);



		//-- Verification

		// Get the folder again
		FolderItem actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNull(actual, "Verify the old addressbook does not exist");

		actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), newname);
		ZAssert.assertNotNull(actual, "Verify the new addressbook exists");
		ZAssert.assertEquals(actual.getColor(), "9", "Verify the color of the folder is set to orange (9)");

	}

}
