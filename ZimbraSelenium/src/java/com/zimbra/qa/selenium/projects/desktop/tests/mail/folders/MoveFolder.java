package com.zimbra.qa.selenium.projects.desktop.tests.mail.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.DialogMove;


public class MoveFolder extends AjaxCommonTest {

   public MoveFolder() {
		logger.info("New "+ MoveFolder.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;

	}

	@Test(	description = "Move a folder - Right click, Move",
			groups = { "smoke" })
	public void MoveFolder_01() throws HarnessException {

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

		// Click on Get Mail to refresh the folder list
      app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

      FolderItem subfolder1 = FolderItem.importFromSOAP(
            app.zGetActiveAccount(),
            name1,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            app.zGetActiveAccount().EmailAddress);
		ZAssert.assertNotNull(subfolder1, "Verify the first subfolder is available");
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ name2 +"' l='"+ inbox.getId() +"'/>" +
                "</CreateFolderRequest>");

		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		FolderItem subfolder2 = FolderItem.importFromSOAP(
		      app.zGetActiveAccount(),
		      name2,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            app.zGetActiveAccount().EmailAddress);

		ZAssert.assertNotNull(subfolder2, "Verify the second subfolder is available");

		// Move the folder using context menu
		DialogMove dialog = (DialogMove)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_MOVE, subfolder1);
		dialog.zClickTreeFolder(subfolder2);
		dialog.zClickButton(Button.B_OK);
		
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

      // Verify the folder is now in the other subfolder on ZD client
		subfolder1 = FolderItem.importFromSOAP(
		      app.zGetActiveAccount(),
		      name1,
		      SOAP_DESTINATION_HOST_TYPE.CLIENT,
		      app.zGetActiveAccount().EmailAddress);

		ZAssert.assertNotNull(subfolder1, "Verify the subfolder is again available on ZD Client");
		ZAssert.assertEquals(subfolder2.getId(), subfolder1.getParentId(),
		      "Verify the subfolder's parent is now the other subfolder on ZD Client");

      // Verify the folder is now in the other subfolder on ZCS server		
		FolderItem subFolder1Zcs = FolderItem.importFromSOAP(
            app.zGetActiveAccount(),
            name1);

	    FolderItem subFolder2Zcs = FolderItem.importFromSOAP(
	            app.zGetActiveAccount(),
	            name2);
	    ZAssert.assertNotNull(subFolder1Zcs, "Verify the subfolder is again available on ZCS Server");
	    ZAssert.assertEquals(subFolder2Zcs.getId(), subFolder1Zcs.getParentId(),
	            "Verify the subfolder's parent is now the other subfolder on ZCS Server");
	}

	


}
