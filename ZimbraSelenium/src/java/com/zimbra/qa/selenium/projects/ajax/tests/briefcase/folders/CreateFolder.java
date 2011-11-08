package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.folders;

import java.util.HashMap;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DialogCreateBriefcaseFolder;

public class CreateFolder extends AjaxCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;

	public CreateFolder() {
		logger.info("New " + CreateFolder.class.getCanonicalName());

		// test starts at the briefcase tab
		super.startingPage = app.zPageBriefcase;
		
		super.startingAccountPreferences = new HashMap<String, String>() {
			
			private static final long serialVersionUID = 1L;
			
			{
				put("zimbraPrefBriefcaseReadingPaneLocation", "bottom");
			}
		};
	}

	@Test(description = "Create a new folder by clicking 'Create a new briefcase' on folders tree", groups = { "sanity" })
	public void CreateFolder_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Set the new folder name
		_folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		DialogCreateBriefcaseFolder createFolderDialog = (DialogCreateBriefcaseFolder) app.zTreeBriefcase
				.zPressPulldown(Button.B_TREE_FOLDERS_OPTIONS, Button.B_TREE_NEWFOLDER);

		createFolderDialog.zEnterFolderName(_folderName);
		createFolderDialog.zClickButton(Button.B_OK);

		_folderIsCreated = true;

		SleepUtil.sleepVerySmall();

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseRootFolder,
				false);

		// Make sure the folder was created on the ZCS server
		FolderItem folder = FolderItem.importFromSOAP(account, _folderName);
		ZAssert.assertNotNull(folder, "Verify the new form opened");

		ZAssert.assertEquals(folder.getName(), _folderName,
				"Verify the server and client folder names match");
	}

	@Test(description = "Create a new folder using 'nf' keyboard shortcut", groups = { "functional" })
	public void CreateFolder_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		Shortcut shortcut = Shortcut.S_NEWFOLDER;

		// Set the new folder name
		_folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		// "NEW Folder" shortcut opens "Create New Folder" dialog
		// due to the bug #63029 it opens dialog with Mail tree view
		DialogCreateBriefcaseFolder createFolderDialog = (DialogCreateBriefcaseFolder) app.zPageBriefcase
				.zKeyboardShortcut(shortcut);

		ZAssert.assertNotNull(createFolderDialog,
				"Verify the new dialog opened");

		// Fill out the form with the basic details
		createFolderDialog.zEnterFolderName(_folderName);
		createFolderDialog.zClickButton(Button.B_OK);

		_folderIsCreated = true;

		SleepUtil.sleepVerySmall();

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseRootFolder,
				false);

		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(account, _folderName);
		ZAssert.assertNotNull(folder, "Verify the new folder was created");

		ZAssert.assertEquals(folder.getName(), _folderName,
				"Verify the server and client folder names match");
	}

	@Test(description = "Create a new folder using context menu from root folder", groups = { "functional" })
	public void CreateFolder_03() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Set the new folder name
		_folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		DialogCreateBriefcaseFolder createFolderDialog = (DialogCreateBriefcaseFolder) app.zTreeBriefcase
				.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_NEWFOLDER,
						briefcaseRootFolder);

		createFolderDialog.zEnterFolderName(_folderName);
		createFolderDialog.zClickButton(Button.B_OK);

		_folderIsCreated = true;

		SleepUtil.sleepVerySmall();

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseRootFolder,
				false);

		// Make sure the folder was created on the ZCS server
		FolderItem folder = FolderItem.importFromSOAP(account, _folderName);
		ZAssert.assertNotNull(folder, "Verify the new form opened");

		ZAssert.assertEquals(folder.getName(), _folderName,
				"Verify the server and client folder names match");
	}

	@Test(description = "Create a new Briefcase folder using Briefcase app toolbar pulldown: New -> New Briefcase", groups = { "functional" })
	public void CreateFolder_04() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Set the new folder name
		_folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		// Create a new briefcase folder using right click context menu + New Briefcase
		DialogCreateBriefcaseFolder dialog = (DialogCreateBriefcaseFolder) app.zPageBriefcase
				.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_BRIEFCASE, null);

		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");

		// Fill out the form with the basic details
		dialog.zEnterFolderName(_folderName);
		dialog.zClickButton(Button.B_OK);

		_folderIsCreated = true;
		
		SleepUtil.sleepVerySmall();

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseRootFolder,
				false);
		
		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(account, _folderName);
		ZAssert.assertNotNull(folder, "Verify the new folder was created");

		ZAssert.assertEquals(folder.getName(), _folderName,
				"Verify the server and client folder names match");
	}

	@AfterMethod(groups = { "always" })
	public void createFolderTestCleanup() {
		if (_folderIsCreated) {
			try {
				app.zPageBriefcase.zNavigateTo();
				// Delete it from Server
				FolderItem
						.deleteUsingSOAP(app.zGetActiveAccount(), _folderName);
			} catch (Exception e) {
				logger.info("Failed while removing the folder.");
				e.printStackTrace();
			} finally {
				_folderName = null;
				_folderIsCreated = false;
			}
		}
	}
}
