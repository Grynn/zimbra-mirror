package com.zimbra.qa.selenium.projects.desktop.tests.mail.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.ContextMenuItem.CONTEXT_MENU_ITEM_NAME;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.projects.desktop.core.DesktopCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.ContextMenu;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DialogCreateFolder;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.TreeMail;

public class CreateFolder extends DesktopCommonTest{

   public CreateFolder() throws HarnessException {
      logger.info("New " + CreateFolder.class.getCanonicalName());

      super.startingPage = app.zPageMail;
      super.startingAccountPreferences = null;
   }

   @Test(description = "Create a new folder",
         groups = {"sanity"})
   public void CreateFolder_01() throws HarnessException {
      String name = "folder" + ZimbraSeleniumProperties.getUniqueString();
      String locator = "css=td[id*='" + startingAccount.EmailAddress + "'][id*='2_textCell']";

      logger.debug("ID is: " + locator);
      ZimbraSeleniumProperties.waitForElementPresent(app.zPageMail, locator);

      String treeItemLocator = TreeMail.Locators.zTreeItems.replace(TreeMail.stringToReplace, _defaultAccountName);
      ContextMenu contextMenu = (ContextMenu)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, treeItemLocator);

      DialogCreateFolder createFolderDialog = (DialogCreateFolder)contextMenu.zSelect(CONTEXT_MENU_ITEM_NAME.NEW_FOLDER);
      createFolderDialog.zEnterFolderName(name);
      createFolderDialog.zClick(DialogCreateFolder.Locators.zOkButton);

      Object[] params = {app.zGetActiveAccount(), name};

      // Make sure the folder was created on the server
      FolderItem folder = (FolderItem)GeneralUtility.waitFor("com.zimbra.qa.selenium.framework.items.FolderItem",
            null, true, "importFromSOAP", params, WAIT_FOR_OPERAND.NEQ, null, 30000, 1000);
      ZAssert.assertNotNull(folder, "Verify the new form opened");

      ZAssert.assertEquals(folder.getName(), name, "Verify the server and client folder names match");
   }
}
