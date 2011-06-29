package com.zimbra.qa.selenium.projects.desktop.tests.accounts;

import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.DesktopAccountItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.OperatingSystem;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.OperatingSystem.OsType;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.FormAddGmailAccount;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.FormAddYahooAccount;
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

   @Test(description="Add Yahoo account to ZD client", groups = { "sanity" })
   public void addYahooAccount() throws HarnessException {

      // TODO: Please remove this once issue in Mac is fixed.
      if (OperatingSystem.getOSType() == OsType.MAC) {
         throw new HarnessException(
               "Fail due to bug 61517, also refers to helpzilla ticket #811085");
      }
      String userName = ZimbraSeleniumProperties.getStringProperty("desktop.yahoo.login");
      String password = ZimbraSeleniumProperties.getStringProperty("desktop.yahoo.password");

      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopYahooAccountItem(
            userName, password);

      FormAddYahooAccount accountForm = (FormAddYahooAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.YAHOO);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(new ZimbraAccount(userName, password));
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account's email address is greater than 0.");
   }

   @Test(description="Add Gmail account to ZD client", groups = { "sanity" })
   public void addGmailAccount() throws HarnessException {

      // TODO: Please remove this once issue in Mac is fixed.
      if (OperatingSystem.getOSType() == OsType.MAC) {
         throw new HarnessException(
               "Fail due to bug 61517, also refers to helpzilla ticket #811085");
      }
      String userName = ZimbraSeleniumProperties.getStringProperty("desktop.gmail.login");
      String password = ZimbraSeleniumProperties.getStringProperty("desktop.gmail.password");

      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopGmailAccountItem(
            userName, password);

      FormAddGmailAccount accountForm = (FormAddGmailAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.GMAIL);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(new ZimbraAccount(userName, password));
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account's email address is greater than 0.");
   }

   @AfterMethod(alwaysRun=true)
   public void cleanUp() throws HarnessException {
      ZimbraAccount.ResetAccountZWC();
      app.zPageLogin.zNavigateTo();
   }
}
