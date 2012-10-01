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


