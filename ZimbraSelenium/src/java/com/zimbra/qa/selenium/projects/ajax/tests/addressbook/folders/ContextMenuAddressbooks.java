package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;




public class ContextMenuAddressbooks extends AjaxCommonTest {

	public ContextMenuAddressbooks() {
		logger.info("New "+ ContextMenuAddressbooks.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		
		// Enable user preference checkboxes
		super.startingAccountPreferences = null;
		
	}

	
	
	

				
	@Test(
			description = "Cannot delete an addressbook system folder- Right click, Delete",
			groups = { "functional" },
			dataProvider = "DataProviderSystemFolders"
			)
	public void SystemFoldersDeleteButtonDisabledFromContextmenu(String name, SystemFolder systemFolder) throws HarnessException {
		
		FolderItem folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), systemFolder);
		ZAssert.assertNotNull(folder, "Verify can get the folder: "+ name);	

		// Right click on Folder 
		app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, folder);
		
		
		// Get the context menu
		String divLocator = "css=div#ZmActionMenu_contacts_ADDRBOOK";
		ZAssert.assertTrue(app.zTreeContacts.zIsVisiblePerPosition(divLocator, 0, 0), "Verify the context menu is visible");
		
		
		// Determine if the Edit option is enabled
		String editLocator = divLocator + " div#DELETE_WITHOUT_SHORTCUT.ZDisabled";
		ZAssert.assertTrue(app.zTreeContacts.sIsElementPresent(editLocator), "Verify the Delete Folder option is disabled");

	}	

	// These folders can change color or share only (i.e. has an edit dialog)
	@DataProvider(name = "DataProviderSystemFolders")
	public Object[][] DataProviderSystemFolders() {
	  return new Object[][] {
	    new Object[] { "Contacts", SystemFolder.Contacts },
	    new Object[] { "Emailed Contacts", SystemFolder.EmailedContacts },
//	    new Object[] { "Distribution Lists", SystemFolder.DistributionLists },
	    new Object[] { "Trash", SystemFolder.Trash },
	  };
	}
	
	@Test(
			description = "Verify 'Rename folder' dialog is not present from right click context menu",
			groups = { "functional" },
			dataProvider = "DataProviderSystemFolders"
			)
	public void CannotRenameSystemFolders(String name, SystemFolder systemFolder) throws HarnessException {

		FolderItem folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), systemFolder);
		ZAssert.assertNotNull(folder, "Verify can get the folder: "+ name);	

		// Right click on Folder 
		app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, folder);
		
		
		// Get the context menu
		String divLocator = "css=div#ZmActionMenu_contacts_ADDRBOOK";
		ZAssert.assertTrue(app.zTreeContacts.zIsVisiblePerPosition(divLocator, 0, 0), "Verify the context menu is visible");
		
		
		// Determine if the Edit option is enabled
		String editLocator = divLocator + " div#RENAME_FOLDER.ZDisabled";
		ZAssert.assertTrue(app.zTreeContacts.sIsElementPresent(editLocator), "Verify the Rename Folder option is disabled");

		
	}	



}
