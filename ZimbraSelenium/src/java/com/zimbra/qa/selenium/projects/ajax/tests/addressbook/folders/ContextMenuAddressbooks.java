package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.DialogEditFolder;



public class ContextMenuAddressbooks extends AjaxCommonTest {

	public ContextMenuAddressbooks() {
		logger.info("New "+ ContextMenuAddressbooks.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		
		// Enable user preference checkboxes
		super.startingAccountPreferences = null;
		
	}

	
	
	

				
	@Test(	description = "Cannot delete an addressbook system folder- Right click, Delete",
					groups = { "smoke" })
	public void SystemFoldersDeleteButtonDisabledFromContextmenu() throws HarnessException {
		
		FolderItem folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
		ZAssert.assertNotNull(folder, "Verify can get the Contacts ");
											
		// Delete the folder using context menu
		AbsPage page= app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, folder);

		// Verify Delete option is disabled
		ZAssert.assertNull(page, "Verify Delete option is disabled");
		

        folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
		ZAssert.assertNotNull(folder, "Verify can get the EmailedContacts ");
											
		// Delete the folder using context menu
		page= app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, folder);

		// Verify Delete option is disabled
		ZAssert.assertNull(page, "Verify Delete option is disabled");

		folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		ZAssert.assertNotNull(folder, "Verify can get the Trash ");
											
		// Delete the folder using context menu
		page= app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, folder);

		// Verify Delete option is disabled
		ZAssert.assertNull(page, "Verify Delete option is disabled");

	}	

	
	@Test(	
			description = "Cannot rename an addressbook system folder- Right Click - Edit Properties - verify input name not displayed",	
			groups = { "functional" }
			)
	public void CannotRenameSystemFolders() throws HarnessException {

		FolderItem folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
		ZAssert.assertNotNull(folder, "Verify can get the Contacts ");	

		// Rename the folder 
		DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folder);
		dialog.zClickButton(Button.B_OK);

		folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
		ZAssert.assertNotNull(folder, "Verify can get the EmailedContacts ");
		
		// Rename the folder 
		dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folder);
		dialog.zClickButton(Button.B_OK);

		folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		ZAssert.assertNotNull(folder, "Verify can get the Trash ");	

		// Rename the folder 
		dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folder);
		ZAssert.assertNull(dialog, "Verify Edit Properties not enabled for Trash folder ");		
	}	


}
