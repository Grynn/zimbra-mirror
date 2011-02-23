package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.ContextMenuItem.CONTEXT_MENU_ITEM_NAME;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.ContextMenu;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.TreeMail;

public class CreateFolder extends AjaxCommonTest {

   private boolean _folderIsCreated = false;
   private String _folderName = null;

   public CreateFolder() {
		logger.info("New "+ CreateFolder.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
	}

	@Test(	description = "Create a new folder by clicking 'new folder' on folder tree",
			groups = { "sanity" })
	public void CreateFolder_01() throws HarnessException {
	   _folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

      ContextMenu contextMenu = null;
      String treeItemLocator = null;
      if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
         treeItemLocator = TreeMail.Locators.zTreeItems.replace(TreeMail.stringToReplace, defaultAccountName);
      } else {
         treeItemLocator = TreeMail.Locators.ztih_main_Mail__FOLDER_ITEM_ID.replace(TreeMail.stringToReplace, "FOLDER");
      }

      GeneralUtility.waitForElementPresent(app.zPageMail, treeItemLocator);
      contextMenu = (ContextMenu)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, treeItemLocator);

      DialogCreateFolder createFolderDialog = (DialogCreateFolder)contextMenu.zSelect(CONTEXT_MENU_ITEM_NAME.NEW_FOLDER);
      createFolderDialog.zEnterFolderName(_folderName);
      createFolderDialog.zClick(DialogCreateFolder.Locators.zOkButton);
      _folderIsCreated = true;

      Object[] params = null;
      if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
         // Make sure the folder was created on the Desktop Server
         params = new Object[]{app.zGetActiveAccount(), _folderName, SOAP_DESTINATION_HOST_TYPE.CLIENT, app.zGetActiveAccount().EmailAddress};
         FolderItem desktopFolder = (FolderItem)GeneralUtility.waitFor("com.zimbra.qa.selenium.framework.items.FolderItem",
               null, true, "importFromSOAP", params, WAIT_FOR_OPERAND.NEQ, null, 30000, 1000);
         ZAssert.assertNotNull(desktopFolder, "Verify the new form opened");
         ZAssert.assertEquals(desktopFolder.getName(), _folderName, "Verify the server and client folder names match");

         // Force-sync
         GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      }

      // Make sure the folder was created on the ZCS server
      params = new Object[] {app.zGetActiveAccount(), _folderName};

      // Anticipate for slow performance client to ZCS server connection, thus putting 30 seconds
      FolderItem folder = (FolderItem)GeneralUtility.waitFor("com.zimbra.qa.selenium.framework.items.FolderItem",
            null, true, "importFromSOAP", params, WAIT_FOR_OPERAND.NEQ, null, 30000, 1000);
      ZAssert.assertNotNull(folder, "Verify the new form opened");
      ZAssert.assertEquals(folder.getName(), _folderName, "Verify the server and client folder names match");
	}

	@Test(	description = "Create a new folder using keyboard shortcuts",
			groups = { "smoke" })
	public void CreateFolder_02() throws HarnessException {
		
		Shortcut shortcut = Shortcut.S_NEWFOLDER;
		
		
		
		// Set the new folder name
		String name = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		DialogCreateFolder dialog = (DialogCreateFolder)app.zPageMail.zKeyboardShortcut(shortcut);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
		
		// Fill out the form with the basic details
		// TODO: does a folder in the tree need to be selected?
		dialog.zEnterFolderName(name);
		dialog.zClickButton(Button.B_OK);
		
		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(folder, "Verify the new folder was created");
		
		ZAssert.assertEquals(folder.getName(), name, "Verify the server and client folder names match");
		
		
	}

	@Test(	description = "Create a new folder using context menu from folder",
			groups = { "smoke" })
	public void CreateFolder_03() throws HarnessException {
		
		
		// Set the new folder name
		String name = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		// Get the Inbox folder
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		
		// Create a new folder in the inbox
		// using the context menu + New Folder
		DialogCreateFolder dialog = (DialogCreateFolder)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_NEW, inbox);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
		
		// Fill out the form with the basic details
		// TODO: does a folder in the tree need to be selected?
		dialog.zEnterFolderName(name);
		dialog.zClickButton(Button.B_OK);
		
		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(folder, "Verify the new folder was created");
		
		ZAssert.assertEquals(folder.getName(), name, "Verify the server and client folder names match");
		
	}

	@Test(	description = "Create a new folder using mail app New -> New Folder",
			groups = { "smoke" })
	public void CreateFolder_04() throws HarnessException {
		
		
		// Set the new folder name
		String name = "folder" + ZimbraSeleniumProperties.getUniqueString();
				
		// Create a new folder in the inbox
		// using the context menu + New Folder
		DialogCreateFolder dialog = (DialogCreateFolder)app.zPageMail.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_FOLDER);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
		
		// Fill out the form with the basic details
		// TODO: does a folder in the tree need to be selected?
		dialog.zEnterFolderName(name);
		dialog.zClickButton(Button.B_OK);
		
		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(folder, "Verify the new folder was created");
		
		ZAssert.assertEquals(folder.getName(), name, "Verify the server and client folder names match");
		
	}

   @AfterMethod(groups = {"always"})
   public void createFolderTestCleanup() {
      if (_folderIsCreated) {
         try {
            app.zPageMail.zNavigateTo();
            // Delete it from Email Server
            FolderItem.deleteUsingSOAP(app.zGetActiveAccount(), _folderName);
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
