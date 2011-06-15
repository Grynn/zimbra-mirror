package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import java.util.List;

import org.testng.annotations.Test;


import com.zimbra.qa.selenium.framework.items.FolderItem;
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
	
	private void RenameAndVerify(FolderItem folderItem, DialogRenameFolder dialog)
	    throws HarnessException {
		// Set the name, click OK
		String oldName = folderItem.getName();
		String name    = "folder" + ZimbraSeleniumProperties.getUniqueString();
		dialog.zSetNewName(name);
		dialog.zClickButton(Button.B_OK);

		//Verify created folder listed on the left menu
		boolean isNewFolderDisplayed=false;
		boolean isOldFolderDisplayed=false;
		
		List<FolderItem> list= app.zPageAddressbook.zListGetFolders(app.zGetActiveAccount());
		for (FolderItem i: list) {
			if (i.getName().equals(name)) {
				isNewFolderDisplayed=true;			
			}
			else if (i.getName().equals(oldName)) {
				isOldFolderDisplayed=true;			
			}
		}
		
		ZAssert.assertTrue(isNewFolderDisplayed, "Verify new folder (" + name + ") displayed ");		
		ZAssert.assertFalse(isOldFolderDisplayed, "Verify old folder (" + oldName + ") not displayed ");		
				

	}
	
	@Test(	description = "Rename a folder - Context menu -> Rename",
			groups = { "smoke" })
	public void SelectFolderRenameOnContextMenu() throws HarnessException {
	
		FolderItem userRoot= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		ZAssert.assertNotNull(userRoot, "Verify can get the userRoot ");
	
		FolderItem folderItem = CreateFolder.createNewFolderViaSoap(userRoot,app);
			
		// Rename the folder using context menu
		DialogRenameFolder dialog = (DialogRenameFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_RENAME, folderItem);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");
		
	    RenameAndVerify(folderItem, dialog);		
	}

	
	@Test(	description = "Rename a sub folder - Context menu -> Rename",
			groups = { "functional" })
	public void SelectSubFolderRenameOnContextMenu() throws HarnessException {
	
		FolderItem contact= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
		ZAssert.assertNotNull(contact, "Verify can get the userRoot ");
	
		FolderItem folderItem = CreateFolder.createNewFolderViaSoap(contact,app);
			
		// Expand parent node to show up sub folder
		app.zTreeContacts.zExpand(contact);
	
		// Rename the folder using context menu
		DialogRenameFolder dialog = (DialogRenameFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_RENAME, folderItem);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");
		
	    RenameAndVerify(folderItem, dialog);		
	}
	
	
	@Test(	description = "Cannot rename an addressbook system folder- Right click, Rename option disabled",
					groups = { "functional" })
	public void SystemFoldersRenameButtonDisabledFromContextmenu() throws HarnessException {
		
		FolderItem folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
		ZAssert.assertNotNull(folder, "Verify can get the Contacts ");
											
		// Rename the folder using context menu
		AbsPage page= app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_RENAME, folder);

		// Verify Rename  option is disabled
		ZAssert.assertNull(page, "Verify Rename option is disabled");
		

        folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
		ZAssert.assertNotNull(folder, "Verify can get the EmailedContacts ");
											
		// Rename the folder using context menu
		page= app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_RENAME, folder);

		// Verify Rename  option is disabled
		ZAssert.assertNull(page, "Verify Rename option is disabled");

		folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		ZAssert.assertNotNull(folder, "Verify can get the Trash ");
											
		// Rename the folder using context menu
		page= app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_RENAME, folder);

		// Verify Rename option is disabled
		ZAssert.assertNull(page, "Verify Rename option is disabled");

	}	

	

}
