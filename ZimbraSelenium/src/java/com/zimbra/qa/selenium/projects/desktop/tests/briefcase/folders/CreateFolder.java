package com.zimbra.qa.selenium.projects.desktop.tests.briefcase.folders;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.briefcase.DialogCreateBriefcaseFolder;

public class CreateFolder extends AjaxCommonTest {
	private boolean _folderIsCreated = false;
	private String _folderName = null;
	private String _accountName = null;

	public CreateFolder() {
		logger.info("New " + CreateFolder.class.getCanonicalName());

		// test starts at the briefcase tab
		super.startingPage = app.zPageBriefcase;
		super.startingAccountPreferences = null;
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
		DialogCreateBriefcaseFolder createFolderDialog = (DialogCreateBriefcaseFolder) app.zPageBriefcase
				.zKeyboardShortcut(shortcut);
		ZAssert.assertNotNull(createFolderDialog,"Verify the new dialog opened");

		// Fill out the form with the basic details
		createFolderDialog.zEnterFolderName(_folderName);
		createFolderDialog.zClickButton(Button.B_OK);
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageMain.zWaitForDesktopLoadingSpinner(5000);
		_folderIsCreated = true;

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseRootFolder,
				false);
		
		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(account, _folderName);
		ZAssert.assertNotNull(folder, "Verify the new folder was created");
		ZAssert.assertEquals(folder.getName(), _folderName,
				"Verify the server and client folder names match");
	}

	@Test(description = "Create a new folder using context menu from root folder", groups = { "Sanity" })
	public void CreateFolder_03() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();
		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,SystemFolder.Briefcase);

		// Set the new folder name
		_folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();
		DialogCreateBriefcaseFolder createFolderDialog = (DialogCreateBriefcaseFolder) app.zTreeBriefcase
				.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_NEWFOLDER,
						briefcaseRootFolder);
		createFolderDialog.zEnterFolderName(_folderName);
		createFolderDialog.zClickButton(Button.B_OK);
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageMain.zWaitForDesktopLoadingSpinner(5000);
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
		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,SystemFolder.Briefcase);

		// Set the new folder name
		_folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		// Create a new briefcase folder using right click context menu + New Briefcase
		DialogCreateBriefcaseFolder dialog = (DialogCreateBriefcaseFolder) app.zTreeBriefcase
				.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_NEWFOLDER,briefcaseRootFolder);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");

		// Fill out the form with the basic details
		dialog.zEnterFolderName(_folderName);
		dialog.zClickButton(Button.B_OK);
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageMain.zWaitForDesktopLoadingSpinner(5000);
		_folderIsCreated = true;
		
		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseRootFolder,
				false);
		
		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(account, _folderName);
		ZAssert.assertNotNull(folder, "Verify the new folder was created");
		ZAssert.assertEquals(folder.getName(), _folderName,
				"Verify the server and client folder names match");
	}

	@Test(description = "Create a new local briefcase folder through context menu", groups = { "smoke" })
	public void createLocalFolderThroughContextMenu() throws HarnessException {
	   _accountName = ZimbraAccount.clientAccountName;
		ZimbraAccount account = app.zGetActiveAccount();

	   _folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();
	   FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(
	         account,
	         SystemFolder.UserRoot,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

	   // Create a new briefcase folder using right click context menu + New Briefcase
      DialogCreateBriefcaseFolder dialog = (DialogCreateBriefcaseFolder) app.zTreeBriefcase
            .zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_NEWFOLDER, briefcaseRootFolder);
      ZAssert.assertNotNull(dialog, "Verify the new dialog opened");

      // Fill out the form with the basic details
      dialog.zEnterFolderName(_folderName);
      dialog.zClickButton(Button.B_OK);
      _folderIsCreated = true;

      // Make sure the folder was created on the server
      FolderItem folder = FolderItem.importFromSOAP(
            account,
            _folderName,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);
      FolderItem desktopFolderParent = folder.getParentFolder(
            app.zGetActiveAccount(),
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

      ZAssert.assertNotNull(folder,
            "Verify the new folder was created");
      ZAssert.assertEquals(folder.getName(), _folderName,
            "Verify the client folder name is as expected");
      ZAssert.assertEquals(desktopFolderParent.getName(),
            briefcaseRootFolder.getName(),
            "Verify the parent folder's name is correct");
	}

   @Test(description = "Create a new local briefcase subfolder through context menu", groups = { "smoke" })
   public void createLocalSubfolderThroughContextMenu() throws HarnessException {

      _accountName = ZimbraAccount.clientAccountName;

      ZimbraAccount account = app.zGetActiveAccount();

      _folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();
      FolderItem briefcaseFolder = FolderItem.importFromSOAP(
            account,
            SystemFolder.Briefcase,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

      // Create a new briefcase folder using right click context menu + New Briefcase
      DialogCreateBriefcaseFolder dialog = (DialogCreateBriefcaseFolder) app.zTreeBriefcase
            .zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_NEWFOLDER, briefcaseFolder);

      ZAssert.assertNotNull(dialog, "Verify the new dialog opened");

      // Fill out the form with the basic details
      dialog.zEnterFolderName(_folderName);
      dialog.zClickButton(Button.B_OK);

      _folderIsCreated = true;

      // refresh briefcase page
      app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder,
            false);

      // Make sure the folder was created on the server
      FolderItem folder = FolderItem.importFromSOAP(
            account,
            _folderName,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);
      FolderItem desktopFolderParent = folder.getParentFolder(
            app.zGetActiveAccount(),
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

      ZAssert.assertNotNull(folder,
            "Verify the new folder was created");

      ZAssert.assertEquals(folder.getName(), _folderName,
            "Verify the client folder name is as expected");

      ZAssert.assertEquals(desktopFolderParent.getName(),
            briefcaseFolder.getName(),
            "Verify the parent folder's name is correct");
   }

	@AfterMethod(groups = { "always" })
	public void createFolderTestCleanup() {
		if (_folderIsCreated) {
			try {
				app.zPageBriefcase.zNavigateTo();

				// Delete it using SOAP to the client
				if (_accountName == null) {
		         FolderItem.deleteUsingSOAP(
		               app.zGetActiveAccount(),
		               _folderName,
		               SOAP_DESTINATION_HOST_TYPE.CLIENT,
		               app.zGetActiveAccount().EmailAddress);
				} else {
				   FolderItem.deleteUsingSOAP(
                     app.zGetActiveAccount(),
                     _folderName,
                     SOAP_DESTINATION_HOST_TYPE.CLIENT,
                     ZimbraAccount.clientAccountName);
				}

			} catch (Exception e) {
				logger.warn("Failed while removing the folder.", e);
			} finally {
				_folderName = null;
				_folderIsCreated = false;
				_accountName = null;
			}
		}
	}
}
