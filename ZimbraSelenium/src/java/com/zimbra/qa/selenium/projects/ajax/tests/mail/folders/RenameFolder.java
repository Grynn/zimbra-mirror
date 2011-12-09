package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders;

import org.testng.annotations.*;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraCharsets.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogError;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogRenameFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogError.DialogErrorID;


public class RenameFolder extends PrefGroupMailByMessageTest {

	public RenameFolder() {
		logger.info("New "+ RenameFolder.class.getCanonicalName());
		
		
		
		
		
	}
	
	@Test(	description = "Rename a folder - Context menu -> Rename",
			groups = { "smoke" })
	public void RenameFolder_01() throws HarnessException {
		
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		ZAssert.assertNotNull(inbox, "Verify the inbox is available");
				
		// Create the subfolder
		String name1 = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ name1 +"' l='"+ inbox.getId() +"'/>" +
                "</CreateFolderRequest>");

		FolderItem subfolder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(subfolder1, "Verify the subfolder is available");
		
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Rename the folder using context menu
		DialogRenameFolder dialog = (DialogRenameFolder)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_RENAME, subfolder1);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");
		
		// Set the name, click OK
		String name2 = "folder" + ZimbraSeleniumProperties.getUniqueString();
		dialog.zSetNewName(name2);
		dialog.zClickButton(Button.B_OK);

		
		// Get all the folders and verify the new name appears and the old name disappears
		app.zGetActiveAccount().soapSend("<GetFolderRequest xmlns = 'urn:zimbraMail'/>");
		
		Element[] eFolder1 = app.zGetActiveAccount().soapSelectNodes("//mail:folder[@name='"+ name1 +"']");
		ZAssert.assertEquals(eFolder1.length, 0, "Verify the old folder name no longer exists");
		
		Element[] eFolder2 = app.zGetActiveAccount().soapSelectNodes("//mail:folder[@name='"+ name2 +"']");
		ZAssert.assertEquals(eFolder2.length, 1, "Verify the new folder name exists");
		
		
	}

	
	@Test(	description = "Rename a folder - set to an invalid name with ':'",
			groups = { "functional" })
	public void RenameFolder_02() throws HarnessException {
		
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		ZAssert.assertNotNull(inbox, "Verify the inbox is available");
				
		// Create the subfolder
		String name1 = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ name1 +"' l='"+ inbox.getId() +"'/>" +
                "</CreateFolderRequest>");

		FolderItem subfolder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(subfolder1, "Verify the subfolder is available");
		
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Rename the folder using context menu
		DialogRenameFolder dialog = (DialogRenameFolder)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_RENAME, subfolder1);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");
		
		// Set the name, click OK
		String name2 = "folder:folder" + ZimbraSeleniumProperties.getUniqueString();
		dialog.zSetNewName(name2);
		dialog.zClickButton(Button.B_OK);

		DialogError error = app.zPageMain.zGetErrorDialog(DialogErrorID.InvalidFolderName);
		ZAssert.assertTrue(error.zIsActive(), "Verify the error dialog appears");
		
		error.zClickButton(Button.B_OK);
		
		
	}

	@DataProvider(name = "DataProviderFilenames")
	public Object[][] DataProviderDeleteKeys() throws HarnessException {
		return (ZimbraCharsets.getInstance().getSampleTable());
	}

	@Test(
			description = "Get a folder with non-ASCII special characters", 
			groups = { "functional", "matt" },
			dataProvider = "DataProviderFilenames")
	public void RenameFolder_03(ZCharset charset, String foldername) throws HarnessException {
		
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		ZAssert.assertNotNull(inbox, "Verify the inbox is available");
				
		// Create the subfolder
		String name1 = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ name1 +"' l='"+ inbox.getId() +"'/>" +
                "</CreateFolderRequest>");

		FolderItem subfolder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(subfolder1, "Verify the subfolder is available");
		
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Rename the folder using context menu
		DialogRenameFolder dialog = (DialogRenameFolder)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_RENAME, subfolder1);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");
		
		// Set the name, click OK
		dialog.zSetNewName(foldername);
		dialog.zClickButton(Button.B_OK);

		
		// Get all the folders and verify the new name appears and the old name disappears
		app.zGetActiveAccount().soapSend("<GetFolderRequest xmlns = 'urn:zimbraMail'/>");
		
		Element[] eFolder1 = app.zGetActiveAccount().soapSelectNodes("//mail:folder[@name='"+ name1 +"']");
		ZAssert.assertEquals(eFolder1.length, 0, "Verify the old folder name no longer exists");
		
		Element[] eFolder2 = app.zGetActiveAccount().soapSelectNodes("//mail:folder[@name='"+ foldername +"']");
		ZAssert.assertEquals(eFolder2.length, 1, "Verify the new folder name exists");
		
		
	}


}
