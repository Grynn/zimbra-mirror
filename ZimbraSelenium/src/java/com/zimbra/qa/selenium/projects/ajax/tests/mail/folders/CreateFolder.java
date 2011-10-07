package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;

public class CreateFolder extends PrefGroupMailByMessageTest {

	private String _folderName = null;

	public CreateFolder() {
		logger.info("New " + CreateFolder.class.getCanonicalName());

		
		
		
	}


	@Test(
			description = "Create a new folder by clicking 'new folder' on folder tree", 
			groups = { "sanity" })
	public void CreateFolder_01() 
	throws HarnessException 
	{
		_folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		
		DialogCreateFolder createFolderDialog = (DialogCreateFolder) app.zTreeMail.zPressButton(Button.B_TREE_NEWFOLDER);
		
		createFolderDialog.zEnterFolderName(_folderName);
		createFolderDialog.zClickButton(Button.B_OK);


		// Make sure the folder was created on the ZCS server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),_folderName);
		ZAssert.assertNotNull(folder, "Verify the new folder was created");
		ZAssert.assertEquals(folder.getName(), _folderName,"Verify the server and client folder names match");
		
	}

	@Test(
			description = "Create a new folder using keyboard shortcuts", 
			groups = { "functional" })
	public void CreateFolder_02() 
	throws HarnessException 
	{

		Shortcut shortcut = Shortcut.S_NEWFOLDER;

		// Set the new folder name
		String name = "folder" + ZimbraSeleniumProperties.getUniqueString();

		DialogCreateFolder dialog = (DialogCreateFolder) app.zPageMail.zKeyboardShortcut(shortcut);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");

		// Fill out the form with the basic details
		// TODO: does a folder in the tree need to be selected?
		dialog.zEnterFolderName(name);
		dialog.zClickButton(Button.B_OK);

		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),name);
		ZAssert.assertNotNull(folder, "Verify the new folder was created");
		ZAssert.assertEquals(folder.getName(), name, "Verify the server and client folder names match");
		
	}

	@Test(
			description = "Create a new folder using context menu from root folder", 
			groups = { "functional" })
	public void CreateFolder_03() 
	throws HarnessException 
	{
		_folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		// get the root folder to create a subfolder in
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		
		// Right click on the "Folders" header
		DialogCreateFolder createFolderDialog = 
			(DialogCreateFolder) app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.O_NEW_FOLDER, root);

		createFolderDialog.zEnterFolderName(_folderName);
		createFolderDialog.zClickButton(Button.B_OK);


		// Make sure the folder was created on the ZCS server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), _folderName);
		ZAssert.assertNotNull(folder, "Verify the new form opened");
		ZAssert.assertEquals(folder.getName(), _folderName, "Verify the server and client folder names match");
		
	}

	@Test(
			description = "Create a new folder using mail app New -> New Folder", 
			groups = { "functional" })
	public void CreateFolder_04() 
	throws HarnessException 
	{

		// Set the new folder name
		String name = "folder" + ZimbraSeleniumProperties.getUniqueString();

		// Create a new folder in the inbox
		// using the context menu + New Folder
		DialogCreateFolder dialog = (DialogCreateFolder) app.zPageMail.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_FOLDER);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");

		// Fill out the form with the basic details
		// TODO: does a folder in the tree need to be selected?
		dialog.zEnterFolderName(name);
		dialog.zClickButton(Button.B_OK);

		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),name);
		ZAssert.assertNotNull(folder, "Verify the new folder was created");

		ZAssert.assertEquals(folder.getName(), name,
				"Verify the server and client folder names match");

	}


}
