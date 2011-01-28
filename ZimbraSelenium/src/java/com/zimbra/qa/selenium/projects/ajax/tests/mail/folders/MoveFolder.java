package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogMove;


public class MoveFolder extends AjaxCommonTest {

	public MoveFolder() {
		logger.info("New "+ MoveFolder.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Move a folder - Right click, Move",
			groups = { "smoke" })
	public void DeleteFolder_01() throws HarnessException {
		
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		ZAssert.assertNotNull(inbox, "Verify the inbox is available");
		
		
		// Create two subfolders in the inbox
		// One folder to move
		// Another folder to move into
		String name1 = "folder" + ZimbraSeleniumProperties.getUniqueString();
		String name2 = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ name1 +"' l='"+ inbox.getId() +"'/>" +
                "</CreateFolderRequest>");

		FolderItem subfolder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(subfolder1, "Verify the first subfolder is available");
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ name2 +"' l='"+ inbox.getId() +"'/>" +
                "</CreateFolderRequest>");

		FolderItem subfolder2 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name2);
		ZAssert.assertNotNull(subfolder1, "Verify the second subfolder is available");
		
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Move the folder using context menu
		DialogMove dialog = (DialogMove)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, subfolder1);
		dialog.zEnterFolderName(name2);
		dialog.zClickButton(Button.B_OK);
		
		// Verify the folder is now in the other subfolder
		subfolder2 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name2);
		ZAssert.assertNotNull(subfolder2, "Verify the subfolder is again available");
		ZAssert.assertEquals(subfolder1.getId(), subfolder2.getParentId(), "Verify the subfolder's parent is now the other subfolder");

		
	}

	


}
