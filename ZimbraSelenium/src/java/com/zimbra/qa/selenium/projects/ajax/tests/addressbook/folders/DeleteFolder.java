package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import java.awt.event.KeyEvent;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;



public class DeleteFolder extends AjaxCommonTest {

	public DeleteFolder() {
		logger.info("New "+ DeleteFolder.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		
		// Enable user preference checkboxes
		super.startingAccountPreferences = null;
		
	}

	
	
	
	@Test(	description = "Delete a top level addressbook - Right click, Delete",
			groups = { "smoke" })
	public void DeleteTopLevelFolderFromContextmenu() throws HarnessException {
		
		//-- Data
		
		FolderItem userRoot = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);

		String name = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name + "' view='contact' l='"+ userRoot.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);

		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Delete the folder using context menu
		app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, folderItem);

		
		//-- Verification
		
		// Get the folder again
		FolderItem actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		
		// Verify the ab is moved to trash
		ZAssert.assertEquals(actual.getParentId(), trash.getId(), "Verify the ab is moved to trash");

	}	

				

	
	@Test(	description = "Delete a sub folder - Right click, Delete",
			groups = { "functional" })
	public void DeleteSubFolderFromContextmenu() throws HarnessException {
		
		//-- Data
		
		FolderItem contact = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);

		String name = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name + "' view='contact' l='"+ contact.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);

		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Delete the folder using context menu
		app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, folderItem);

		
		//-- Verification
		
		// Get the folder again
		FolderItem actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		
		// Verify the ab is moved to trash
		ZAssert.assertEquals(actual.getParentId(), trash.getId(), "Verify the ab is moved to trash");


			
	}
	
	@Test(	description = "Drag one sub folder to Trash folder", groups = { "functional" })
	public void DnDFromSubFolderToTrash() throws HarnessException {
		
		//-- Data
		
		FolderItem contact = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);

		String name = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name + "' view='contact' l='"+ contact.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);

		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Delete the folder DND
		app.zPageAddressbook.zDragAndDrop(
				"css=td#zti__main_Contacts__" + folderItem.getId() + "_textCell:contains("+ folderItem.getName() + ")",
				"css=td#zti__main_Contacts__" + trash.getId() + "_textCell:contains("+ trash.getName() + ")");

		
		//-- Verification
		
		// Get the folder again
		FolderItem actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		
		// Verify the ab is moved to trash
		ZAssert.assertEquals(actual.getParentId(), trash.getId(), "Verify the ab is moved to trash");


	}

	@Test(	description = "Delete an addressbook folder- Use shortcut Del",
			groups = { "deprecated" })
	public void UseShortcutDel() throws HarnessException {
		
		//-- Data
		
		FolderItem userRoot = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);

		String name = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name + "' view='contact' l='"+ userRoot.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);

		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the folder
		app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, folderItem);
		
 	    // Delete the folder using shortcut Del		
	    app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_DELETE);

		
		//-- Verification
		
		// Get the folder again
		FolderItem actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		
		// Verify the ab is moved to trash
		ZAssert.assertEquals(actual.getParentId(), trash.getId(), "Verify the ab is moved to trash");


	}	

}
