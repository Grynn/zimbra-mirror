package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;

public class DragAndDropFolder extends PrefGroupMailByMessageTest{

	public DragAndDropFolder(){
		logger.info("New "+ DragAndDropFolder.class.getCanonicalName());

		
		
		

	}

	@Test(	description = "Drag one folder and Drop into other",
			groups = { "smoke" })
	public void DragDropFolder_01() throws HarnessException {

		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		ZAssert.assertNotNull(inbox, "Verify the inbox is available");


		// Create two subfolders in the inbox
		// One folder to Drag
		// Another folder to drop into
		String name1 = "folder" + ZimbraSeleniumProperties.getUniqueString();
		String name2 = "folder" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='"+ name1 +"' l='"+ inbox.getId() +"'/>"
				+	"</CreateFolderRequest>");


		FolderItem subfolder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(subfolder1, "Verify the first subfolder is available");

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='"+ name2 +"' l='"+ inbox.getId() +"'/>"
				+	"</CreateFolderRequest>");

		FolderItem subfolder2 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name2);
		ZAssert.assertNotNull(subfolder2, "Verify the second subfolder is available");


		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Bug 65234
		// Sometimes the folder tree is rendered slowly.  sleep a bit
		SleepUtil.sleepVerySmall();
		
		app.zPageMail.zDragAndDrop(
				"css=td[id='zti__main_Mail__" + subfolder1.getId() + "_textCell']",
				"css=td[id='zti__main_Mail__" + subfolder2.getId() + "_textCell']");
				


		// Verify the folder is now in the other subfolder
		subfolder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(subfolder1, "Verify the subfolder is again available");
		ZAssert.assertEquals(subfolder2.getId(), subfolder1.getParentId(), "Verify the subfolder's parent is now the other subfolder");


	}

}
