package com.zimbra.qa.selenium.projects.desktop.tests.accounts;

import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.DesktopAccountItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.PageLogin;
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

      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddZimbraAccountThruUI();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account's email address is greater than 0.");
   }

   @Test(description="Add Yahoo account to ZD client", groups = { "sanity" })
   public void addYahooAccount() throws HarnessException {

      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddYahooAccountThruUI();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(new ZimbraAccount(desktopAccountItem.emailAddress,
            desktopAccountItem.password));
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account's email address is greater than 0.");
   }

   @Test(description="Add Gmail account to ZD client", groups = { "sanity" })
   public void addGmailAccount() throws HarnessException {

      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddGmailAccountThruUI();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(new ZimbraAccount(desktopAccountItem.emailAddress,
            desktopAccountItem.password));
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account's email address is greater than 0.");
   }

   @Test(description="Wrong email address when creating Zimbra Account", groups = { "functional" } )
   public void wrongEmailAddressZimbraAccount() throws HarnessException {

      String wrongEmailAddress = ZimbraSeleniumProperties.getUniqueString() + "@testdomain.com";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopZimbraAccountItem(
            wrongEmailAddress,
            ZimbraAccount.AccountZWC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.port", "80"),
            false);

      FormAddZimbraAccount accountForm = (FormAddZimbraAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.ZIMBRA);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Account authentication failed. Please check username and password.",
            "Verify error message of wrong email address");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Wrong password when creating Zimbra Account", groups = { "functional" } )
   public void wrongPasswordZimbraAccount() throws HarnessException {

      String wrongPassword = ZimbraSeleniumProperties.getUniqueString();
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopZimbraAccountItem(
            ZimbraAccount.AccountZWC().EmailAddress,
            wrongPassword,
            ZimbraSeleniumProperties.getStringProperty("server.port", "80"),
            false);

      FormAddZimbraAccount accountForm = (FormAddZimbraAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.ZIMBRA);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Account authentication failed. Please check username and password.",
            "Verify error message of wrong password");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Wrong server when creating Zimbra Account", groups = { "functional" } )
   public void wrongServerZimbraAccount() throws HarnessException {

      String wrongServer = "1.1.1.1";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopZimbraAccountItem(
            ZimbraAccount.AccountZWC().EmailAddress,
            ZimbraAccount.AccountZWC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.port", "80"),
            wrongServer,
            false);

      FormAddZimbraAccount accountForm = (FormAddZimbraAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.ZIMBRA);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Timeout when connecting to \"http://" + wrongServer + "/service/soap/\"." +
            		" Please check host/port and network connectivity.",
            "Verify error message of wrong incoming server address");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Wrong Non-SSL port when creating Zimbra Account", groups = { "functional" })
   public void wrongNonSSLPortZimbraAccount() throws HarnessException {
      String nonSSLPort = "111";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopZimbraAccountItem(
            ZimbraAccount.AccountZWC().EmailAddress,
            ZimbraAccount.AccountZWC().Password,
            nonSSLPort,
            false);

      FormAddZimbraAccount accountForm = (FormAddZimbraAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.ZIMBRA);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "service.FAILURE: system failure: error while proxying request to target server:" +
            " HTTP/1.0 403 Forbidden Display error details",
            "Verify error message of wrong incoming server address");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Wrong SSL port when creating Zimbra Account", groups = { "functional" })
   public void wrongSSLPortZimbraAccount() throws HarnessException {
      String sslPort = "111";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopZimbraAccountItem(
            ZimbraAccount.AccountZWC().EmailAddress,
            ZimbraAccount.AccountZWC().Password,
            sslPort,
            true);

      FormAddZimbraAccount accountForm = (FormAddZimbraAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.ZIMBRA);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "service.FAILURE: system failure: error while proxying request to target server:" +
            " HTTP/1.0 403 Forbidden Display error details",
            "Verify error message of wrong incoming server address");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Wrong email address when creating Gmail Account", groups = { "functional" })
   public void wrongEmailAddressGmailAccount() throws HarnessException {

      String wrongEmailAddress = ZimbraSeleniumProperties.getUniqueString() + "@gmail.com";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopGmailAccountItem(
            wrongEmailAddress, AjaxCommonTest.gmailPassword);

      FormAddGmailAccount accountForm = (FormAddGmailAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.GMAIL);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "User account authentication failed. Please check username and password.",
            "Verify error message of wrong password");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Wrong password when creating Gmail Account", groups = { "functional" })
   public void wrongPasswordGmailAccount() throws HarnessException {

      String wrongPassword = ZimbraSeleniumProperties.getUniqueString();
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopGmailAccountItem(
            AjaxCommonTest.gmailUserName, wrongPassword);

      FormAddGmailAccount accountForm = (FormAddGmailAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.GMAIL);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "User account authentication failed. Please check username and password.",
            "Verify error message of wrong password");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Wrong email address when creating Yahoo Account", groups = { "functional2" })
   public void wrongEmailAddressYahooAccount() throws HarnessException {

      String wrongEmailAddress = ZimbraSeleniumProperties.getUniqueString() + "@yahoo.com";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopYahooAccountItem(
            wrongEmailAddress, AjaxCommonTest.yahooPassword);

      FormAddYahooAccount accountForm = (FormAddYahooAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.YAHOO);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "The requested user account does not exist. Please check the spelling.",
            "Verify error message of wrong password");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Wrong password when creating Yahoo Account", groups = { "functional2" })
   public void wrongPasswordYahooAccount() throws HarnessException {

      String wrongPassword = ZimbraSeleniumProperties.getUniqueString();
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopYahooAccountItem(
            AjaxCommonTest.yahooUserName, wrongPassword);

      FormAddYahooAccount accountForm = (FormAddYahooAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.YAHOO);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Invalid password.",
            "Verify error message of wrong password");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @AfterMethod(alwaysRun=true)
   public void cleanUp() throws HarnessException {
      ZimbraAccount.ResetAccountZWC();
      app.zPageLogin.zNavigateTo();
   }
}
