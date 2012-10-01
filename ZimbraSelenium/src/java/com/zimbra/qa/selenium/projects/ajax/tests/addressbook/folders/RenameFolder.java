package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogRenameFolder;


public class RenameFolder extends AjaxCommonTest {

	public RenameFolder() {
		logger.info("New "+ RenameFolder.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Rename a folder - Context menu -> Rename",
			groups = { "smoke" })
	public void SelectFolderRenameOnContextMenu() throws HarnessException {

		//-- Data
		
		// Root folder
		FolderItem userRoot= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		
		// Subfolders in root
		String name = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		String name2 = "ab" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name + "' view='contact' l='"+ userRoot.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);



		//-- GUI
		
		// refresh
		app.zPageAddressbook.zRefresh();
		
		// Rename the folder using context menu
		DialogRenameFolder dialog = (DialogRenameFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_RENAME, folderItem);
		
		dialog.zSetNewName(name2);
		dialog.zClickButton(Button.B_OK);

		
		//-- Verification
		FolderItem actual = FolderItem.importFromSOAP(app.zGetActiveAccount(),name);
		ZAssert.assertNull(actual, "Verify the old folder name no longer exists");
		
		actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), name2);
		ZAssert.assertNotNull(actual, "Verify the new folder name exists");
		

	}

	
	@Test(	description = "Rename a sub folder - Context menu -> Rename",
			groups = { "functional" })
	public void SelectSubFolderRenameOnContextMenu() throws HarnessException {
	

		//-- Data
		
		// Contacts folder
		FolderItem contacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);

		// Subfolders in root
		String name = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		String name2 = "ab" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name + "' view='contact' l='"+ contacts.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);



		//-- GUI
		
		// refresh
		app.zPageAddressbook.zRefresh();
		
		// Rename the folder using context menu
		DialogRenameFolder dialog = (DialogRenameFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_RENAME, folderItem);
		
		dialog.zSetNewName(name2);
		dialog.zClickButton(Button.B_OK);

		
		//-- Verification
		FolderItem actual = FolderItem.importFromSOAP(app.zGetActiveAccount(),name);
		ZAssert.assertNull(actual, "Verify the old folder name no longer exists");
		
		actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), name2);
		ZAssert.assertNotNull(actual, "Verify the new folder name exists");
		

	}
	
	
	@Test(	description = "Cannot rename an addressbook system folder- Right click, Rename option disabled",
					groups = { "functional" })
	public void SystemFoldersRenameButtonDisabledFromContextmenu() throws HarnessException {
		
		
		//-- Data
		boolean exists;
		String locator = "css=div[id^='ZmActionMenu_contacts_ADDRBOOK'] div[id^='RENAME_FOLDER'].ZDisabled";
		
		
		//-- GUI
		FolderItem contacts= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
											
		// Rename the folder using context menu
		app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, contacts);

		// Verify Rename  option is disabled
		exists = app.zTreeContacts.sIsElementPresent(locator);
		ZAssert.assertTrue(exists, "Verify Rename option is disabled");
		
		
		//-- GUI
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
											
		// Rename the folder using context menu
		app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, emailedContacts);

		// Verify Rename  option is disabled
		exists = app.zTreeContacts.sIsElementPresent(locator);
		ZAssert.assertTrue(exists, "Verify Rename option is disabled");

		
		//-- GUI
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
											
		// Rename the folder using context menu
		app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, trash);

		// Verify Rename option is disabled
		exists = app.zTreeContacts.sIsElementPresent(locator);
		ZAssert.assertTrue(exists, "Verify Rename option is disabled");

	}	

	

}
