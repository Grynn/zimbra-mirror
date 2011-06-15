package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;



public class DeleteFolder extends AjaxCommonTest {

	public DeleteFolder() {
		logger.info("New "+ DeleteFolder.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;
		
	}

	
	
	private void verifyExistInTrashFolder(FolderItem folderItem) throws HarnessException {
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		ZAssert.assertNotNull(trash, "Verify the trash is available");
				
		// Verify the folder is now in the trash
		folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), folderItem.getName());
		ZAssert.assertNotNull(folderItem, "Verify the folder Item is again available");
		ZAssert.assertEquals(trash.getId(), folderItem.getParentId(), "Verify the folder's parent is now the trash folder ID");
	
	}
	
	@Test(	description = "Delete a top level addressbook - Right click, Delete",
			groups = { "smoke" })
	public void DeleteTopLevelFolderFromContextmenu() throws HarnessException {
		
		FolderItem userRoot= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		ZAssert.assertNotNull(userRoot, "Verify can get the userRoot ");
	
 	    FolderItem folderItem = CreateFolder.createNewFolderViaSoap(userRoot,app);
										
		// Delete the folder using context menu
		AbsPage page= app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, folderItem);

		// Verify Delete option is enabled
		ZAssert.assertNotNull(page, "Verify Delete option is enabled");

		verifyExistInTrashFolder(folderItem);
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

	
	@Test(	description = "Delete a sub folder - Right click, Delete",
			groups = { "functional" })
	public void DeleteSubFolderFromContextmenu() throws HarnessException {
		
		FolderItem contact = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
		ZAssert.assertNotNull(contact, "Verify the folder contact is available");
	    
		FolderItem subFolder = CreateFolder.createNewFolderViaSoap(contact,app);
				  
		// Expand parent node to show up sub folder
		app.zTreeContacts.zExpand(contact);
						
		// Delete the folder using context menu
		AbsPage page= app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, subFolder);
				
		// Verify Delete option is enabled
		ZAssert.assertNotNull(page, "Verify Delete option is enabled");
		
		verifyExistInTrashFolder(subFolder);
			
	}
}
