package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogMove;


public class MoveFolder extends AjaxCommonTest {

	public MoveFolder() {
		logger.info("New "+ MoveFolder.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Drag one folder from top level and Drop into sub folder", groups = { "smoke" })
	public void DnDFromTopLevelToSubFolder() throws HarnessException {

		FolderItem userRoot= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		ZAssert.assertNotNull(userRoot, "Verify can get the userRoot ");
	
		FolderItem folderItemSrc = CreateFolder.createNewFolderViaSoap(userRoot,app);
	
		
		FolderItem contact= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
		ZAssert.assertNotNull(contact, "Verify can get the contact ");

	
		FolderItem folderItemDest = CreateFolder.createNewFolderViaSoap(contact,app);
        
	
		// Expand parent node to show up sub folder
		app.zTreeContacts.zExpand(contact);
	
		
		app.zPageAddressbook.zDragAndDrop(
				"css=div#zov__main_Contacts td#zti__main_Contacts__" + folderItemSrc.getId() + "_textCell:contains("+ folderItemSrc.getName() + ")",
				"css=div#zov__main_Contacts td#zti__main_Contacts__" + folderItemDest.getId() + "_textCell:contains("+ folderItemDest.getName() + ")");
			

		// Verify the folder is now in the other subfolder
		folderItemSrc = FolderItem.importFromSOAP(app.zGetActiveAccount(), folderItemSrc.getName());
		ZAssert.assertNotNull(folderItemSrc, "Verify the subfolder is again available");
		ZAssert.assertEquals(folderItemDest.getId(), folderItemSrc.getParentId(), "Verify the subfolder's parent is now the other subfolder");


	}




}
