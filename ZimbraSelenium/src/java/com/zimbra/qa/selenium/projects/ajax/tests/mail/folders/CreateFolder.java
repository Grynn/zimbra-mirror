package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;

public class CreateFolder extends AjaxCommonTest {

   private boolean _folderIsCreated = false;
   private String _folderName = null;
   private String _rootFolderName = null;
   private SOAP_DESTINATION_HOST_TYPE _soapDestination = null;

   public CreateFolder() {
		logger.info("New "+ CreateFolder.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
	}

   @BeforeMethod(alwaysRun=true)
   public void setParameters() {
      _rootFolderName = ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP ?
            defaultAccountName : "Folders";
      _soapDestination = ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP ?
            SOAP_DESTINATION_HOST_TYPE.CLIENT: SOAP_DESTINATION_HOST_TYPE.SERVER;
   }

	@Test(	description = "Create a new folder by clicking 'new folder' on folder tree",
			groups = { "sanity" })
	public void CreateFolder_01() throws HarnessException {
	   _folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

	   FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.UserRoot,
	         _soapDestination, app.zGetActiveAccount().EmailAddress);
      DialogCreateFolder createFolderDialog = null;

      // TODO: For now, on desktop test, create the folder through context menu, until a way to identify desktop/ajax specific
      // test is decided.
      if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
         createFolderDialog = (DialogCreateFolder)app.zPageMail.zListItem(Action.A_RIGHTCLICK,
               Button.B_TREE_NEWFOLDER, folderItem);
      } else {
         createFolderDialog = (DialogCreateFolder)app.zPageMail.zListItem(Action.A_LEFTCLICK,
               Button.B_TREE_NEWFOLDER, folderItem);
      }

      createFolderDialog.zEnterFolderName(_folderName);
      createFolderDialog.zClickButton(Button.B_OK);

      _folderIsCreated = true;

      if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
         // Force-sync
         GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

         // Make sure the folder was created on the Desktop Server
         FolderItem desktopFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), _folderName,
               SOAP_DESTINATION_HOST_TYPE.CLIENT, app.zGetActiveAccount().EmailAddress);

         ZAssert.assertNotNull(desktopFolder, "Verify the new form opened");
         ZAssert.assertEquals(desktopFolder.getName(), _folderName,
               "Verify the server and client folder names match");
      }

      // Make sure the folder was created on the ZCS server
      FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), _folderName);
      ZAssert.assertNotNull(folder, "Verify the new form opened");
      ZAssert.assertEquals(folder.getName(), _folderName, "Verify the server and client folder names match");
	}

	@Test(	description = "Create a new folder using keyboard shortcuts",
			groups = { "functional" })
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

   @Test(	description = "Create a new folder using context menu from root folder",
      groups = { "functional" })
   public void CreateFolder_03() throws HarnessException {
      _folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

      FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.UserRoot,
            _soapDestination, app.zGetActiveAccount().EmailAddress);
      DialogCreateFolder createFolderDialog = (DialogCreateFolder)app.zPageMail.zListItem(Action.A_RIGHTCLICK,
            Button.B_TREE_NEWFOLDER, folderItem);

      createFolderDialog.zEnterFolderName(_folderName);
      createFolderDialog.zClickButton(Button.B_OK);

      _folderIsCreated = true;

      if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
         // Force-sync
         GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

         // Make sure the folder was created on the Desktop Server
         FolderItem desktopFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), _folderName,
               SOAP_DESTINATION_HOST_TYPE.CLIENT, app.zGetActiveAccount().EmailAddress);

         ZAssert.assertNotNull(desktopFolder, "Verify the new form opened");
         ZAssert.assertEquals(desktopFolder.getName(), _folderName,
               "Verify the server and client folder names match");
      }

      // Make sure the folder was created on the ZCS server
      FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), _folderName);
      ZAssert.assertNotNull(folder, "Verify the new form opened");
      ZAssert.assertEquals(folder.getName(), _folderName, "Verify the server and client folder names match");
   }

	@Test(	description = "Create a new folder using mail app New -> New Folder",
			groups = { "functional" })
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
