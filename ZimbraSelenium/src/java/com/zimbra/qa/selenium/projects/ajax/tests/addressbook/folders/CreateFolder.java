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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.DialogCreateFolder;

public class CreateFolder extends AjaxCommonTest {

	

	public CreateFolder() {
		logger.info("New " + CreateFolder.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;
	}

	
	@Test(
			description = "Create a new folder by clicking 'new folder' on folder tree",
			groups = { "sanity" }
			)
	public void ClickNewFolderOnFolderTree() throws HarnessException {
	
		//-- Data
		
		// Folder name 
		String folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		
		//-- GUI
		
		// Refresh addressbook
	   	app.zPageAddressbook.zRefresh();

	   	// New Addressbook button
		DialogCreateFolder createFolderDialog = (DialogCreateFolder) app.zTreeContacts.zPressButton(Button.B_TREE_NEWADDRESSBOOK);	
		createFolderDialog.zEnterFolderName(folderName);
		createFolderDialog.zClickButton(Button.B_OK);
  
		
		//-- Verification
		
		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),folderName);
		ZAssert.assertNotNull(folder, "Verify the folder created on the server");
		ZAssert.assertEquals(folder.getName(), folderName,"Verify folder name found on server");
		
	}
 
	

	@Test(
			description = "Create a new folder using context menu from root folder", 
			groups = { "sanity" }
			)
	public void ClickContextMenuNewAddressbook() throws HarnessException {
		
		//-- Data
		
		// The root folder
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.UserRoot);

		// Folder name 
		String folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		
		//-- GUI
		
		// Refresh addressbook
	   	app.zPageAddressbook.zRefresh();

		// Right click on root -> New Addressbook
		DialogCreateFolder createFolderDialog = (DialogCreateFolder) 
				app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_NEWFOLDER,folderItem);
		createFolderDialog.zEnterFolderName(folderName);
		createFolderDialog.zClickButton(Button.B_OK);
  
		
		//-- Verification
		
		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),folderName);
		ZAssert.assertNotNull(folder, "Verify the folder created on the server");
		ZAssert.assertEquals(folder.getName(), folderName,"Verify folder name found on server");
		
	}

	@Test(
			description = "Create a new folder using context menu from root folder",
			groups = { "smoke" }
			)
	public void CreateSubFolderUnderContactsClickContextMenuNewAddressbook() throws HarnessException {	
		
		//-- Data
		
		// The root folder
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Contacts);

		// Folder name 
		String folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		
		//-- GUI
		
		// Refresh addressbook
	   	app.zPageAddressbook.zRefresh();

		// Right click on Contacts -> New Addressbook
		DialogCreateFolder createFolderDialog = (DialogCreateFolder) 
				app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_NEWFOLDER,folderItem);
		createFolderDialog.zEnterFolderName(folderName);
		createFolderDialog.zClickButton(Button.B_OK);
  
		
		//-- Verification
		
		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),folderName);
		ZAssert.assertNotNull(folder, "Verify the folder created on the server");
		ZAssert.assertEquals(folder.getName(), folderName,"Verify folder name found on server");
		
	}

	@Test(
			description = "Create a new folder using   New -> New Addressbook", 
			groups = { "functional" }
			)
	public void ClickMenuNewNewAddressbook() throws HarnessException {
		
		//-- Data
		
		// Folder name 
		String folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		
		//-- GUI
		
		// Refresh addressbook
	   	app.zPageAddressbook.zRefresh();

		// New -> Addressbook
		DialogCreateFolder createFolderDialog = (DialogCreateFolder) app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_ADDRESSBOOK);
		createFolderDialog.zEnterFolderName(folderName);
		createFolderDialog.zClickButton(Button.B_OK);
  
		
		//-- Verification
		
		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),folderName);
		ZAssert.assertNotNull(folder, "Verify the folder created on the server");
		ZAssert.assertEquals(folder.getName(), folderName,"Verify folder name found on server");
		

	}


}


