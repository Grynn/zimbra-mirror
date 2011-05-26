package com.zimbra.qa.selenium.projects.desktop.tests.accounts;

import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.DesktopAccountItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.FormAddZimbraAccount;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.PageAddNewAccount.DROP_DOWN_OPTION;

public class CreateAccount extends AjaxCommonTest {

   public CreateAccount() {
      logger.info("New " + CreateAccount.class.getCanonicalName());

      super.startingPage = app.zPageAddNewAccount;
      super.startingAccountPreferences = null;
   }

   @Test(description="Create New Single Account (Zimbra) - Non SSL", groups = { "sanity" })
   public void CreateSingleZimbraAccountNonSSL() throws HarnessException {
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopZimbraAccountItem(
            ZimbraAccount.AccountZWC().EmailAddress,
            ZimbraAccount.AccountZWC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.port", "80"),
            false);

      FormAddZimbraAccount accountForm = (FormAddZimbraAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.ZIMBRA);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account's email address is greater than 0.");
   }

   @AfterMethod(alwaysRun=true)
   public void cleanUp() {
      ZimbraAccount.ResetAccountZWC();
   }
}
