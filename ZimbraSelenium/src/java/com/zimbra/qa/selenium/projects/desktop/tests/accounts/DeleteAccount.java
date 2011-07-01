package com.zimbra.qa.selenium.projects.desktop.tests.accounts;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.DesktopAccountItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.OperatingSystem;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.OperatingSystem.OsType;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.PageLogin;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.FormAddGmailAccount;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.FormAddYahooAccount;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.FormAddZimbraAccount;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.PageAddNewAccount.DROP_DOWN_OPTION;

public class DeleteAccount extends AjaxCommonTest {
   public DeleteAccount() {
      logger.info("New " + DeleteAccount.class.getCanonicalName());

      super.startingPage = app.zPageAddNewAccount;
      super.startingAccountPreferences = null;
   }

   public enum ACCOUNT_TYPE {
      YAHOO,
      GMAIL,
      ZIMBRA,
      IMAP,
      MS_IMAP
   }

   @Test(description="Delete the Yahoo! account from ZD Client through clicking Delete Button", groups = { "smoke" })
   public void deleteYahooAccountThruClick() throws HarnessException {
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

      String confirmationMessage = app.zPageLogin.zRemoveAccountThroughClick();

      ZAssert.assertEquals(confirmationMessage,
            "Account settings and downloaded data will be deleted. Data on the server will not be affected. OK to proceed?",
            "Verifying delete confirmation message");

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();

      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Delete the Gmail account from ZD Client through clicking Delete Button", groups = { "smoke" })
   public void deleteGmailAccountThruClick() throws HarnessException {
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

      String confirmationMessage = app.zPageLogin.zRemoveAccountThroughClick();

      ZAssert.assertEquals(confirmationMessage,
            "Account settings and downloaded data will be deleted. Data on the server will not be affected. OK to proceed?",
            "Verifying delete confirmation message");

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();

      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Delete the Zimbra account from ZD Client through clicking Delete Button", groups = { "smoke" })
   public void deleteZimbraAccountThruClick() throws HarnessException {
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopZimbraAccountItem(
            ZimbraAccount.AccountZWC().EmailAddress,
            ZimbraAccount.AccountZWC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.port", "80"),
            false);

      FormAddZimbraAccount accountForm = (FormAddZimbraAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.ZIMBRA);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      String confirmationMessage = app.zPageLogin.zRemoveAccountThroughClick();

      ZAssert.assertEquals(confirmationMessage,
            "Account settings and downloaded data will be deleted. Data on the server will not be affected. OK to proceed?",
            "Verifying delete confirmation message");

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();

      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Delete the Yahoo! account from ZD Client through HTTP Post", groups = { "functional" })
   public void deleteYahooAccountThruHttpPost() throws HarnessException {
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

      app.zPageLogin.zLogin(new ZimbraAccount(userName, password));
      app.zPageLogin.zNavigateTo();
      
      app.zPageLogin.zRemoveAccount();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();

      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Delete the Gmail account from ZD Client through HTTP Post", groups = { "functional" })
   public void deleteGmailAccountThruHttpPost() throws HarnessException {
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

      app.zPageLogin.zLogin(new ZimbraAccount(userName, password));
      app.zPageLogin.zNavigateTo();

      app.zPageLogin.zRemoveAccount();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();

      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Delete the Zimbra account from ZD Client through HTTP Post", groups = { "functional" })
   public void deleteZimbraAccountThruHttpPost() throws HarnessException {
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopZimbraAccountItem(
            ZimbraAccount.AccountZWC().EmailAddress,
            ZimbraAccount.AccountZWC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.port", "80"),
            false);

      FormAddZimbraAccount accountForm = (FormAddZimbraAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.ZIMBRA);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());
      app.zPageLogin.zNavigateTo();

      app.zPageLogin.zRemoveAccount();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();

      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }
}
