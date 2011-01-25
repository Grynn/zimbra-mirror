package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.ContextMenu;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;


public class DeleteFolder extends AjaxCommonTest {

	public DeleteFolder() {
		logger.info("New "+ DeleteFolder.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Delete a folder - Right click, Delete",
			groups = { "smoke" })
	public void DeleteFolder_01() throws HarnessException {
		
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		ZAssert.assertNotNull(inbox, "Verify the inbox is available");
		
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		ZAssert.assertNotNull(trash, "Verify the trash is available");
		

		
		// Create the subfolder
		String name = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ name +"' l='"+ inbox.getId() +"'/>" +
                "</CreateFolderRequest>");

		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(subfolder, "Verify the subfolder is available");
		
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Click on the folder
		ContextMenu contextMenu = (ContextMenu) app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, subfolder);
		ZAssert.assertNotNull(contextMenu, "Verify the context menu opened");
		
		
		// Select Delete
		contextMenu.zSelect(ContextMenuItem.C_CONTACT_DELETE);
		
		
		// Verify the folder is now in the trash
		subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(subfolder, "Verify the subfolder is again available");
		ZAssert.assertEquals(trash.getId(), subfolder.getParentId(), "Verify the subfolder's parent is now the trash folder ID");

		
	}

	


}
