package com.zimbra.qa.selenium.projects.desktop.tests.mail.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DialogEditFolder;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DialogEditFolder.FolderColor;

public class ChangeColorFolder extends AjaxCommonTest {

   public ChangeColorFolder() {
      logger.info("New " + ChangeColorFolder.class.getCanonicalName());

      // All tests start at the login page
      super.startingPage = app.zPageMail;
      super.startingAccountPreferences = null;

   }

   @Test(description = "Edit a folder, change the color (Context menu -> Edit)", groups = { "functional" })
   public void ChangeColorFolder_01() throws HarnessException {

      FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(),
            SystemFolder.Inbox);
      ZAssert.assertNotNull(inbox, "Verify the inbox is available");

      // Create the subfolder
      String name1 = "folder" + ZimbraSeleniumProperties.getUniqueString();

      app.zGetActiveAccount().soapSend(
               "<CreateFolderRequest xmlns='urn:zimbraMail'>"
            +     "<folder name='" + name1 + "' l='" + inbox.getId() + "'/>"
            +  "</CreateFolderRequest>");

      // Click on Get Mail to refresh the folder list
      app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

      FolderItem subfolder1 = FolderItem.importFromSOAP(
            app.zGetActiveAccount(), name1);

      FolderItem subfolderZD1 = FolderItem.importFromSOAP(
            app.zGetActiveAccount(),
            name1,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            app.zGetActiveAccount().EmailAddress);

      ZAssert.assertNotNull(subfolder1, "Verify the subfolder is available");

      // Rename the folder using context menu
      DialogEditFolder dialog = (DialogEditFolder) app.zTreeMail.zTreeItem(
            Action.A_RIGHTCLICK, Button.B_TREE_EDIT, subfolderZD1);

      ZAssert.assertNotNull(dialog, "Verify the dialog opened");

      // Change the color, click OK
      dialog.zSetNewColor(FolderColor.Gray);
      dialog.zClickButton(Button.B_OK);

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

      // Check the color
      app.zGetActiveAccount().soapSend(
            "<GetFolderRequest xmlns='urn:zimbraMail'>"
         +     "<folder id='" + subfolder1.getId() + "'/>"
         +  "</GetFolderRequest>");

      String color = app.zGetActiveAccount().soapSelectValue("//mail:folder[@name='" + subfolder1.getName() + "']", "color");
      ZAssert.assertEquals(color, "8", "Verify the color of the folder is set to gray (8)");
   }

}
