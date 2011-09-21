package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;

public class DeleteFolder extends PrefGroupMailByMessageTest {

	public DeleteFolder() {
		logger.info("New "+ DeleteFolder.class.getCanonicalName());

		
		
		

	}

	@Test(	description = "Delete a folder - Right click, Delete",
			groups = { "smoke" })
	public void DeleteFolder_01() throws HarnessException {

		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		ZAssert.assertNotNull(inbox, "Verify the inbox is available");

		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
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

		// Delete the folder using context menu
		app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, subfolder);

		// Verify the folder is now in the trash
		subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(subfolder, "Verify the subfolder is again available");
		ZAssert.assertEquals(trash.getId(), subfolder.getParentId(), "Verify the subfolder's parent is now the trash folder ID");

	}

	@Test(   description = "Deleting the folder where the Trash already has a folder with the same name",
	      groups = { "functional" })
	public void DeleteFolder_02() throws HarnessException {

	   FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
	   ZAssert.assertNotNull(inbox, "Verify the inbox is available");

	   FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
	   ZAssert.assertNotNull(trash, "Verify the trash is available");

	   // Create the subfolder
	   String name = "folder" + ZimbraSeleniumProperties.getUniqueString();

	   app.zGetActiveAccount().soapSend(
	         "<CreateFolderRequest xmlns='urn:zimbraMail'>" +
	         "<folder name='"+ name +"' l='"+ inbox.getId() +"'/>" +
	         "</CreateFolderRequest>");

	   FolderItem subfolder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
	   ZAssert.assertNotNull(subfolder1, "Verify the subfolder is available");

	   // Click on Get Mail to refresh the folder list
	   app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

	   // Delete the folder using context menu
	   app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, subfolder1);

	   subfolder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);

	   // Create the same subfolder again under the same parent
	   app.zGetActiveAccount().soapSend(
            "<CreateFolderRequest xmlns='urn:zimbraMail'>" +
            "<folder name='"+ name +"' l='"+ inbox.getId() +"'/>" +
            "</CreateFolderRequest>");

      FolderItem subfolder2 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
      ZAssert.assertNotNull(subfolder2, "Verify the subfolder is available");

      // Click on Get Mail to refresh the folder list
      app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

      // Delete the folder using context menu
      app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, subfolder2);

      subfolder2 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name + "_");

      // Verify the folder is now in the trash
	   ZAssert.assertNotNull(subfolder1, "Verify the subfolder1 is still available");
	   ZAssert.assertNotNull(subfolder2, "Verify the subfolder2 is still available");

	   ZAssert.assertEquals(trash.getId(), subfolder1.getParentId(), "Verify the subfolder1's parent is now the trash folder ID");
	   ZAssert.assertEquals(trash.getId(), subfolder2.getParentId(), "Verify the subfolder2's parent is now the trash folder ID");
	   ZAssert.assertEquals(name, subfolder1.getName(), "Verify the subfolder1's name");
	   ZAssert.assertEquals(name + "_", subfolder2.getName(), "Verify the subfolder2's name");
	}
}
