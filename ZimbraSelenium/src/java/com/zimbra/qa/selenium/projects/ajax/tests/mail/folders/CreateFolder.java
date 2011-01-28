package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;


public class CreateFolder extends AjaxCommonTest {

	public CreateFolder() {
		logger.info("New "+ CreateFolder.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Create a new folder",
			groups = { "sanity" })
	public void CreateFolder_01() throws HarnessException {
		
		
		// Create the message data to be sent
		String name = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		DialogCreateFolder dialog = (DialogCreateFolder)app.zTreeMail.zPressButton(Button.B_TREE_NEWFOLDER);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
		
		// Fill out the form with the basic details
		// TODO: does a folder in the tree need to be selected?
		dialog.zEnterFolderName(name);
		dialog.zClickButton(Button.B_OK);
		
		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(folder, "Verify the new form opened");
		
		ZAssert.assertEquals(folder.getName(), name, "Verify the server and client folder names match");
		
	}

	
	
	@Test(	description = "Create a new folder using keyboard shortcuts",
			groups = { "smoke" })
	public void CreateFolder_02() throws HarnessException {
		
		Shortcut shortcut = Shortcut.S_NEWFOLDER;
		
		
		
		// Create the message data to be sent
		String name = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		DialogCreateFolder dialog = (DialogCreateFolder)app.zPageMail.zKeyboardShortcut(shortcut);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
		
		// Fill out the form with the basic details
		// TODO: does a folder in the tree need to be selected?
		dialog.zEnterFolderName(name);
		dialog.zClickButton(Button.B_OK);
		
		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(folder, "Verify the new form opened");
		
		ZAssert.assertEquals(folder.getName(), name, "Verify the server and client folder names match");
		
		
	}

	@Test(	description = "Create a new folder using context menu",
			groups = { "smoke" })
	public void CreateFolder_03() throws HarnessException {
		
		
		// Create the message data to be sent
		String name = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		// Get the Inbox folder
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		
		// Create a new folder in the inbox
		// using the context menu + New Folder
		DialogCreateFolder dialog = (DialogCreateFolder)app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, Button.B_NEW, inbox);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
		
		// Fill out the form with the basic details
		// TODO: does a folder in the tree need to be selected?
		dialog.zEnterFolderName(name);
		dialog.zClickButton(Button.B_OK);
		
		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(folder, "Verify the new form opened");
		
		ZAssert.assertEquals(folder.getName(), name, "Verify the server and client folder names match");
		
	}


}
