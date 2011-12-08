package com.zimbra.qa.selenium.projects.desktop.tests.mail.folders;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContextMenuItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.ContextMenuItem.CONTEXT_MENU_ITEM_NAME;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;

public class ShowContextMenu extends AjaxCommonTest{

   private SOAP_DESTINATION_HOST_TYPE _soapDestination = null;

   public ShowContextMenu() {
      logger.info("New "+ ShowContextMenu.class.getCanonicalName());
      super.startingPage = app.zPageMail;

   }

   @BeforeMethod(alwaysRun = true)
   public void setParameters() {
      _soapDestination = ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP ? SOAP_DESTINATION_HOST_TYPE.CLIENT
            : SOAP_DESTINATION_HOST_TYPE.SERVER;
   }

   @Test(description = "Right click Zcs root mail folder to show correct context menu",
         groups = { "smoke" })
   public void showZcsRootContextMenu() throws HarnessException {
      FolderItem folderItem = FolderItem.importFromSOAP(app
            .zGetActiveAccount(), FolderItem.SystemFolder.UserRoot,
            _soapDestination, app.zGetActiveAccount().EmailAddress);

      String folderLocator = app.zTreeMail.zGetTreeFolderLocator(folderItem);

      // Invoke the context menu
      app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, folderLocator);
      
      ContextMenuItem cmi_NewFolder = ContextMenuItem.getDesktopContextMenuItem(
            CONTEXT_MENU_ITEM_NAME.NEW_FOLDER);
      ZAssert.assertEquals(app.zPageMail.sIsElementPresent(cmi_NewFolder.locator),
            true,
            "Verifying the presence of New Folder context menu item");
      String imageAttribute = app.zPageMail.sGetAttribute(cmi_NewFolder.locator + " td[id$='icon']>div@class");
      ZAssert.assertFalse(imageAttribute.contains("ZDisabled"), "Verify if the new folder context menu item is disabled");
      
      ContextMenuItem cmi_SendReceive = ContextMenuItem.getDesktopContextMenuItem(
            CONTEXT_MENU_ITEM_NAME.SEND_RECEIVE);
      ZAssert.assertEquals(app.zPageMail.sIsElementPresent(cmi_SendReceive.locator),
            true,
            "Verifying the presence of Send/Receive context menu item");
      imageAttribute = app.zPageMail.sGetAttribute(cmi_SendReceive.locator + " td[id$='icon']>div@class");
      ZAssert.assertFalse(imageAttribute.contains("ZDisabled"), "Verify if the send/receive context menu item is disabled");
   }

   @Test(description = "Right click ZCS Inbox mail folder to show correct context menu",
         groups = { "smoke" })
   public void showZcsInboxContextMenu() throws HarnessException {
      // Brand new account, so erase and add brand new account
      ZimbraAccount.ResetAccountZDC();
      app.zPageLogin.zNavigateTo();
      app.zPageLogin.zRemoveAccount();

      addDefaultAccount();
      ZimbraAccount.AccountZDC().authenticate();
      ZimbraAccount.AccountZDC().authenticateToMailClientHost();
      super.startingPage.zNavigateTo();

      FolderItem folderItem = FolderItem.importFromSOAP(app
            .zGetActiveAccount(), FolderItem.SystemFolder.Inbox,
            _soapDestination, app.zGetActiveAccount().EmailAddress);

      String folderLocator = app.zTreeMail.zGetTreeFolderLocator(folderItem);

      // Invoke the context menu
      app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, folderLocator);

      // NEW FOLDER
      ContextMenuItem cmi_NewFolder = ContextMenuItem.getDesktopContextMenuItem(
            CONTEXT_MENU_ITEM_NAME.NEW_FOLDER);
      ZAssert.assertEquals(app.zPageMail.sIsElementPresent(cmi_NewFolder.locator),
            true,
            "Verifying the presence of New Folder context menu item");
      String imageAttribute = app.zPageMail.sGetAttribute(cmi_NewFolder.locator + " td[id$='icon']>div@class");
      ZAssert.assertFalse(imageAttribute.contains("ZDisabled"), "Verify if the new folder context menu item is disabled");

      // MARK ALL AS READ
      ContextMenuItem cmi_MarkAll = ContextMenuItem.getDesktopContextMenuItem(
            CONTEXT_MENU_ITEM_NAME.MARK_ALL_AS_READ);
      ZAssert.assertEquals(app.zPageMail.sIsElementPresent(cmi_MarkAll.locator),
            true,
            "Verifying the presence of Mark all as read context menu item");
      imageAttribute = app.zPageMail.sGetAttribute(cmi_MarkAll.locator + " td[id$='icon']>div@class");
      ZAssert.assertTrue(imageAttribute.contains("ZDisabled"), "Verify if the Mark All as Read context menu item is disabled");

      // DELETE
      ContextMenuItem cmi_Delete = ContextMenuItem.getDesktopContextMenuItem(
            CONTEXT_MENU_ITEM_NAME.DELETE);
      ZAssert.assertEquals(app.zPageMail.sIsElementPresent(cmi_Delete.locator),
            true,
            "Verifying the presence of Delete context menu item");
      imageAttribute = app.zPageMail.sGetAttribute(cmi_Delete.locator + " td[id$='icon']>div@class");
      ZAssert.assertTrue(imageAttribute.contains("ZDisabled"), "Verify if the Delete context menu item is disabled");

      // RENAME FOLDER
      ContextMenuItem cmi_RenameFolder = ContextMenuItem.getDesktopContextMenuItem(
            CONTEXT_MENU_ITEM_NAME.RENAME_FOLDER);
      ZAssert.assertEquals(app.zPageMail.sIsElementPresent(cmi_RenameFolder.locator),
            true,
            "Verifying the presence of Rename Folder context menu item");
      imageAttribute = app.zPageMail.sGetAttribute(cmi_RenameFolder.locator + " td[id$='icon']>div@class");
      ZAssert.assertTrue(imageAttribute.contains("ZDisabled"), "Verify if the Rename Folder context menu item is disabled");

      // MOVE
      ContextMenuItem cmi_Move = ContextMenuItem.getDesktopContextMenuItem(
            CONTEXT_MENU_ITEM_NAME.MOVE);
      ZAssert.assertEquals(app.zPageMail.sIsElementPresent(cmi_Move.locator),
            true,
            "Verifying the presence of Move context menu item");
      imageAttribute = app.zPageMail.sGetAttribute(cmi_Move.locator + " td[id$='icon']>div@class");
      ZAssert.assertTrue(imageAttribute.contains("ZDisabled"), "Verify if the Move context menu item is disabled");

      // SHARE FOLDER
      ContextMenuItem cmi_ShareFolder = ContextMenuItem.getDesktopContextMenuItem(
            CONTEXT_MENU_ITEM_NAME.SHARE_FOLDER);
      ZAssert.assertEquals(app.zPageMail.sIsElementPresent(cmi_ShareFolder.locator),
            true,
            "Verifying the presence of Share Folder context menu item");
      imageAttribute = app.zPageMail.sGetAttribute(cmi_ShareFolder.locator + " td[id$='icon']>div@class");
      ZAssert.assertFalse(imageAttribute.contains("ZDisabled"), "Verify if the Share Folder context menu item is disabled");

      // EDIT PROPERTIES
      ContextMenuItem cmi_EditProperties = ContextMenuItem.getDesktopContextMenuItem(
            CONTEXT_MENU_ITEM_NAME.EDIT_PROPERTIES);
      ZAssert.assertEquals(app.zPageMail.sIsElementPresent(cmi_EditProperties.locator),
            true,
            "Verifying the presence of Edit Properties context menu item");
      imageAttribute = app.zPageMail.sGetAttribute(cmi_EditProperties.locator + " td[id$='icon']>div@class");
      ZAssert.assertFalse(imageAttribute.contains("ZDisabled"), "Verify if the Edit Properties context menu item is disabled");

      // EXPAND ALL
      ContextMenuItem cmi_ExpandAll = ContextMenuItem.getDesktopContextMenuItem(
            CONTEXT_MENU_ITEM_NAME.EXPAND_ALL);
      ZAssert.assertEquals(app.zPageMail.sIsElementPresent(cmi_ExpandAll.locator),
            true,
            "Verifying the presence of Expand All context menu item");
      imageAttribute = app.zPageMail.sGetAttribute(cmi_ExpandAll.locator + " td[id$='icon']>div@class");
      ZAssert.assertTrue(imageAttribute.contains("ZDisabled"), "Verify if the Expand All context menu item is disabled");

      // EMPTY FOLDER
      ContextMenuItem cmi_EmptyFolder = ContextMenuItem.getDesktopContextMenuItem(
            CONTEXT_MENU_ITEM_NAME.EMPTY_FOLDER);
      ZAssert.assertEquals(app.zPageMail.sIsElementPresent(cmi_EmptyFolder.locator),
            true,
            "Verifying the presence of Empty Folder context menu item");
      imageAttribute = app.zPageMail.sGetAttribute(cmi_EmptyFolder.locator + " td[id$='icon']>div@class");
      ZAssert.assertTrue(imageAttribute.contains("ZDisabled"), "Verify if the Empty Folder context menu item is disabled");
   }
}
