package com.zimbra.qa.selenium.projects.desktop.tests.mail.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.Toaster;

public class DragAndDropFolder extends AjaxCommonTest{

	public DragAndDropFolder(){
		logger.info("New "+ DragAndDropFolder.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;

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

		app.zPageMail.zDragAndDrop(
				"//td[contains(@id, 'zti__" + app.zGetActiveAccount().EmailAddress + ":main_Mail__') and contains(@id, ':" + subfolder1.getId() + "_textCell') and contains(text(), '"+ name1 + "')]",
				"//td[contains(@id, 'zti__" + app.zGetActiveAccount().EmailAddress + ":main_Mail__') and contains(@id, ':" + subfolder2.getId() + "_textCell') and contains(text(), '"+ name2 + "')]");

		Toaster toast = app.zPageMain.zGetToaster();
      String toastMsg = toast.zGetToastMessage();
      ZAssert.assertStringContains(toastMsg, "1 Folder moved to \"" + name2 + "\"",
            "Verify toast message" );

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

		// Verify the folder is now in the other subfolder
		subfolder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(subfolder1, "Verify the subfolder is again available");
		ZAssert.assertEquals(subfolder2.getId(), subfolder1.getParentId(), "Verify the subfolder's parent is now the other subfolder");


	}

}
