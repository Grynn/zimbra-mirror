package com.zimbra.qa.selenium.projects.desktop.tests.accounts;

import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.DesktopAccountItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.DesktopAccountItem.SECURITY_TYPE;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.staf.Stafzmtlsctl;
import com.zimbra.qa.selenium.framework.util.staf.Stafzmtlsctl.SERVER_ACCESS;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.PageLogin;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.FormAddGmailAccount;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.FormAddImapAccount;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.FormAddPopAccount;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.FormAddYahooAccount;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.FormAddZimbraAccount;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.PageAddNewAccount.DROP_DOWN_OPTION;

public class CreateAccount extends AjaxCommonTest {

   private boolean _sslIsModified = false;

   @BeforeMethod(alwaysRun=true)
   public void setup() {
      _sslIsModified = false;
   }

   public CreateAccount() {
      logger.info("New " + CreateAccount.class.getCanonicalName());

      super.startingPage = app.zPageAddNewAccount;
      super.startingAccountPreferences = null;
   }

   // ZIMBRA ACCOUNT
   @Test(description="Create New Single Account (Zimbra) - SSL", groups = { "sanity" })
   public void CreateSingleZimbraAccountSSL() throws HarnessException{
      Stafzmtlsctl stafzmtlsctl = new Stafzmtlsctl();
      stafzmtlsctl.setServerAccess(SERVER_ACCESS.BOTH);
      _sslIsModified = true;

      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddZimbraAccountThruUI(ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password, true, "443");

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(new ZimbraAccount(desktopAccountItem.emailAddress,
            desktopAccountItem.password));
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account's email address is greater than 0.");

   }

   @Test(description="Create New Single Account (Zimbra) - Non SSL", groups = { "sanity" })
   public void CreateSingleZimbraAccountNonSSL() throws HarnessException {

      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddZimbraAccountThruUI();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(new ZimbraAccount(desktopAccountItem.emailAddress,
            desktopAccountItem.password));
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account's email address is greater than 0.");
   }

   @Test(description="Create Multiple Accounts (Zimbra) - SSL", groups = { "functional" })
   public void CreateMultipleZimbraAccountSSL() throws HarnessException {
      Stafzmtlsctl stafzmtlsctl = new Stafzmtlsctl();
      stafzmtlsctl.setServerAccess(SERVER_ACCESS.BOTH);
      _sslIsModified = true;

      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddZimbraAccountThruUI(ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password, true, "443");

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      DesktopAccountItem desktopAccountItem2 = app.zPageAddNewAccount.zAddZimbraAccountThruUI(ZimbraAccount.AccountA().EmailAddress,
            ZimbraAccount.AccountA().Password, true, "443");

      message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem2.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(new ZimbraAccount(desktopAccountItem.emailAddress,
            desktopAccountItem.password));
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account1's email address is greater than 0.");

      app.zSetActiveAcount(new ZimbraAccount(desktopAccountItem2.emailAddress,
            desktopAccountItem2.password));
      folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account2's email address is greater than 0.");
   }

   @Test(description="Create Multiple Accounts (Zimbra) - Non SSL", groups = { "functional" })
   public void CreateMultipleZimbraAccountNonSSL() throws HarnessException {

      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddZimbraAccountThruUI(ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password, false, "80");

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      DesktopAccountItem desktopAccountItem2 = app.zPageAddNewAccount.zAddZimbraAccountThruUI(ZimbraAccount.AccountA().EmailAddress,
            ZimbraAccount.AccountA().Password, false, "80");

      message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem2.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(new ZimbraAccount(desktopAccountItem.emailAddress,
            desktopAccountItem.password));
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account1's email address is greater than 0.");

      app.zSetActiveAcount(new ZimbraAccount(desktopAccountItem2.emailAddress,
            desktopAccountItem2.password));
      folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account2's email address is greater than 0.");
   }

   @Test(description="Wrong email address format (alphabet characters only) when creating Zimbra Account", groups = { "functional" } )
   public void wrongEmailAddressFormatZimbraAccount1() throws HarnessException {

      String wrongEmailAddress = ZimbraSeleniumProperties.getUniqueString();
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopZimbraAccountItem(
            wrongEmailAddress,
            ZimbraAccount.AccountZDC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.port", "80"),
            false);

      FormAddZimbraAccount accountForm = (FormAddZimbraAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.ZIMBRA);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Please correct missing or invalid input.",
            "Verify error message of wrong email address format");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Wrong email address format (alphabet characters and '@') when creating Zimbra Account", groups = { "functional" } )
   public void wrongEmailAddressFormatZimbraAccount2() throws HarnessException {

      String wrongEmailAddress = ZimbraSeleniumProperties.getUniqueString() + "@";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopZimbraAccountItem(
            wrongEmailAddress,
            ZimbraAccount.AccountZDC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.port", "80"),
            false);

      FormAddZimbraAccount accountForm = (FormAddZimbraAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.ZIMBRA);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Please correct missing or invalid input.",
            "Verify error message of wrong email address format");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Wrong email address when creating Zimbra Account", groups = { "functional" } )
   public void wrongEmailAddressZimbraAccount() throws HarnessException {

      String wrongEmailAddress = ZimbraSeleniumProperties.getUniqueString() + "@testdomain.com";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopZimbraAccountItem(
            wrongEmailAddress,
            ZimbraAccount.AccountZDC().Password,
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
            ZimbraAccount.AccountZDC().EmailAddress,
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
            ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.port", "80"),
            wrongServer,
            false);

      FormAddZimbraAccount accountForm = (FormAddZimbraAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.ZIMBRA);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage(true);
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
            ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password,
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
            ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password,
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

   @Test(description="Failure in attempting to add duplicated Zimbra accounts", groups = { "functional" })
   public void addDuplicatedZimbraAccount() throws HarnessException {
      // Adding the Zimbra account
      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddZimbraAccountThruUI();

      // Trying to add the same Zimbra account
      app.zPageAddNewAccount.zNavigateTo();
      FormAddZimbraAccount accountForm = (FormAddZimbraAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.ZIMBRA);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      // Verifying error message
      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "account.ACCOUNT_EXISTS: email address already exists: ",
            "Verify error message of wrong password");

      app.zPageLogin.zNavigateTo();

      // Verifying in login page, the first added account is still there
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDeleteButton),
            "Delete account link exists");
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zBtnLoginDesktop),
            "Launch Zimbra Dekstop Button exists");
   }

   // YAHOO ACCOUNT
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

   @Test(description="Wrong email address when creating Yahoo Account", groups = { "functional" })
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

   @Test(description="Wrong password when creating Yahoo Account", groups = { "functional" })
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

   // TODO: Please uncomment this when bug 63341 is fixed
   // @Test(description="Failure in attempting to add duplicated Yahoo! accounts", groups = { "functional" })
   public void addDuplicatedYahooAccount() throws HarnessException {
      // Adding the Yahoo! account
      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddYahooAccountThruUI();

      // Trying to add the same Yahoo! account
      app.zPageAddNewAccount.zNavigateTo();
      FormAddYahooAccount accountForm = (FormAddYahooAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.YAHOO);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      // Verifying error message
      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "account.ACCOUNT_EXISTS: email address already exists: ",
            "Verify error message of wrong password");

      app.zPageLogin.zNavigateTo();

      // Verifying in login page, the first added account is still there
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDeleteButton),
            "Delete account link exists");
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zBtnLoginDesktop),
            "Launch Zimbra Dekstop Button exists");
   }

   // GMAIL ACCOUNT
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

   @Test(description="Failure in attempting to add duplicated gmail accounts", groups = { "functional" })
   public void addDuplicatedGmailAccount() throws HarnessException {
      // Adding the gmail account
      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddGmailAccountThruUI();

      // Trying to add the same gmail account
      app.zPageAddNewAccount.zNavigateTo();
      FormAddGmailAccount accountForm = (FormAddGmailAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.GMAIL);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      // Verifying error message
      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "account.ACCOUNT_EXISTS: email address already exists: ",
            "Verify error message of wrong password");

      app.zPageLogin.zNavigateTo();

      // Verifying in login page, the first added account is still there
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDeleteButton),
            "Delete account link exists");
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zBtnLoginDesktop),
            "Launch Zimbra Dekstop Button exists");
   }

   // IMAP ACCOUNT
   @Test(description="Add Gmail IMAP account to ZD client", groups = { "sanity" })
   public void addGmailImapAccount() throws HarnessException {

      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddGmailImapAccountThruUI();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(new ZimbraAccount(desktopAccountItem.emailAddress,
            desktopAccountItem.password));
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account's email address is greater than 0.");
   }

   @Test(description="Add Zimbra IMAP (SSL) account to ZD client with Sending Mail set to SSL", groups = { "smoke" })
   public void addZimbraImapSslSendingSslAccount() throws HarnessException {
      Stafzmtlsctl stafzmtlsctl = new Stafzmtlsctl();
      stafzmtlsctl.setServerAccess(SERVER_ACCESS.BOTH);
      _sslIsModified = true;

      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddZimbraImapAccountThruUI(ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.host", "localhost"),
            true,
            "465");

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(new ZimbraAccount(desktopAccountItem.emailAddress,
            desktopAccountItem.password));
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account's email address is greater than 0.");
   }

   @Test(description="Add Zimbra IMAP (SSL) account to ZD client with Sending Mail set to non SSL", groups = { "smoke-skip" })
   public void addZimbraImapSslSendingNonSslAccount() throws HarnessException {
      Stafzmtlsctl stafzmtlsctl = new Stafzmtlsctl();
      stafzmtlsctl.setServerAccess(SERVER_ACCESS.BOTH);
      _sslIsModified = true;

      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddZimbraImapAccountThruUI(ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.host", "localhost"),
            false,
            "25");

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(new ZimbraAccount(desktopAccountItem.emailAddress,
            desktopAccountItem.password));
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account's email address is greater than 0.");
   }

   @Test(description="Failure in adding Zimbra IMAP account with Receiving Mail security set to None",
         groups = { "functional" })
   public void addZimbraImapNonSslAccount() throws HarnessException {

      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.host", "localhost"),
            SECURITY_TYPE.NONE,
            null,
            ZimbraSeleniumProperties.getStringProperty("server.host", "localhost"),
            false,
            null,
            ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password);

      FormAddImapAccount accountForm = (FormAddImapAccount)app.
            zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopAccountItem);

      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "User account authentication failed. Please check username and password.",
            "Verify error message of disabled cleartext login");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Wrong email address format (alphabet characters) when creating IMAP Account", groups = { "functional" } )
   public void wrongEmailAddressFormatImapAccount1() throws HarnessException {
      String wrongEmailAddress = ZimbraSeleniumProperties.getUniqueString();
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            wrongEmailAddress,
            AjaxCommonTest.gmailUserName,
            AjaxCommonTest.gmailPassword,
            AjaxCommonTest.gmailImapReceivingServer,
            SECURITY_TYPE.SSL,
            "993",
            AjaxCommonTest.gmailImapSmtpServer,
            true,
            "465",
            AjaxCommonTest.gmailUserName,
            AjaxCommonTest.gmailPassword);

      FormAddImapAccount accountForm = (FormAddImapAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Please correct missing or invalid input.",
            "Verify error message of wrong email address format");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Wrong email address format (alphabet characters and '@') when creating IMAP Account", groups = { "functional" } )
   public void wrongEmailAddressFormatImapAccount2() throws HarnessException {
      String wrongEmailAddress = ZimbraSeleniumProperties.getUniqueString() + "@";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            wrongEmailAddress,
            AjaxCommonTest.gmailUserName,
            AjaxCommonTest.gmailPassword,
            AjaxCommonTest.gmailImapReceivingServer,
            SECURITY_TYPE.SSL,
            "993",
            AjaxCommonTest.gmailImapSmtpServer,
            true,
            "465",
            AjaxCommonTest.gmailUserName,
            AjaxCommonTest.gmailPassword);

      FormAddImapAccount accountForm = (FormAddImapAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Please correct missing or invalid input.",
            "Verify error message of wrong email address format");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Wrong None security receiving port when creating Gmail IMAP Account", groups = { "functional" })
   public void wrongNonePortGmailImapAccount() throws HarnessException {
      String wrongSslPort = "111";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            gmailUserName,
            gmailUserName,
            gmailPassword,
            gmailImapReceivingServer,
            SECURITY_TYPE.NONE,
            wrongSslPort,
            gmailImapSmtpServer,
            true,
            "465",
            gmailUserName,
            gmailPassword);

      FormAddImapAccount accountForm = (FormAddImapAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Cannot connect to \"" + gmailImapReceivingServer + ":" + wrongSslPort + "\". Please check host/port and network connectivity.",
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

   @Test(description="Wrong Ssl security receiving port when creating Gmail IMAP Account", groups = { "functional" })
   public void wrongSslPortGmailImapAccount() throws HarnessException {
      String wrongSslPort = "111";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            gmailUserName,
            gmailUserName,
            gmailPassword,
            gmailImapReceivingServer,
            SECURITY_TYPE.SSL,
            wrongSslPort,
            gmailImapSmtpServer,
            true,
            "465",
            gmailUserName,
            gmailPassword);

      FormAddImapAccount accountForm = (FormAddImapAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Cannot connect to \"" + gmailImapReceivingServer + ":" + wrongSslPort + "\". Please check host/port and network connectivity.",
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

   @Test(description="Wrong TLS security receiving port when creating Gmail IMAP Account", groups = { "functional" })
   public void wrongTlsPortGmailImapAccount() throws HarnessException {
      String wrongSslPort = "111";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            gmailUserName,
            gmailUserName,
            gmailPassword,
            gmailImapReceivingServer,
            SECURITY_TYPE.TLS,
            wrongSslPort,
            gmailImapSmtpServer,
            true,
            "465",
            gmailUserName,
            gmailPassword);

      FormAddImapAccount accountForm = (FormAddImapAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Cannot connect to \"" + gmailImapReceivingServer + ":" + wrongSslPort + "\". Please check host/port and network connectivity.",
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

   @Test(description="Wrong TLS If Available security receiving port when creating Gmail IMAP Account", groups = { "functional" })
   public void wrongTlsIfAvailPortGmailImapAccount() throws HarnessException {
      String wrongSslPort = "111";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            gmailUserName,
            gmailUserName,
            gmailPassword,
            gmailImapReceivingServer,
            SECURITY_TYPE.TLS_IF_AVAILABLE,
            wrongSslPort,
            gmailImapSmtpServer,
            true,
            "465",
            gmailUserName,
            gmailPassword);

      FormAddImapAccount accountForm = (FormAddImapAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Cannot connect to \"" + gmailImapReceivingServer + ":" + wrongSslPort + "\". Please check host/port and network connectivity.",
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

   @Test(description="Wrong receiving server when creating Gmail IMAP Account", groups = { "functional" })
   public void wrongReceivingServerGmailImapAccount() throws HarnessException {
      String wrongReceivingServer = ZimbraSeleniumProperties.getUniqueString();
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            gmailUserName,
            gmailUserName,
            gmailPassword,
            wrongReceivingServer,
            SECURITY_TYPE.SSL,
            "993",
            gmailImapSmtpServer,
            true,
            "465",
            gmailUserName,
            gmailPassword);

      FormAddImapAccount accountForm = (FormAddImapAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "\"" + wrongReceivingServer + ":993" + "\" host not found. Please check hostname and network connectivity.",
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

   @Test(description="Wrong Receiving username when creating Gmail IMAP Account", groups = { "functional" })
   public void wrongReceivingUsernameGmailIMAPAccount() throws HarnessException {

      String wrongUsername = ZimbraSeleniumProperties.getUniqueString();

      DesktopAccountItem desktopImapAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            gmailUserName,
            wrongUsername,
            gmailPassword,
            gmailImapReceivingServer,
            SECURITY_TYPE.SSL,
            "993",
            gmailImapSmtpServer,
            true,
            "465",
            gmailUserName,
            gmailPassword);

      // Trying to add the same Gmail IMAP account
      app.zPageAddNewAccount.zNavigateTo();
      FormAddImapAccount accountForm = (FormAddImapAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopImapAccountItem);
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

   @Test(description="Wrong Receiving password when creating Gmail IMAP Account", groups = { "functional" })
   public void wrongReceivingPasswordGmailIMAPAccount() throws HarnessException {

      String wrongPassword = ZimbraSeleniumProperties.getUniqueString();

      DesktopAccountItem desktopImapAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            gmailUserName,
            gmailUserName,
            wrongPassword,
            gmailImapReceivingServer,
            SECURITY_TYPE.SSL,
            "993",
            gmailImapSmtpServer,
            true,
            "465",
            gmailUserName,
            gmailPassword);

      // Trying to add the same Gmail IMAP account
      app.zPageAddNewAccount.zNavigateTo();
      FormAddImapAccount accountForm = (FormAddImapAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopImapAccountItem);
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

   @Test(description="Wrong Sending username when creating Gmail IMAP Account", groups = { "functional" })
   public void wrongSendingUsernameGmailIMAPAccount() throws HarnessException {

      String wrongUsername = ZimbraSeleniumProperties.getUniqueString();

      DesktopAccountItem desktopImapAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            gmailUserName,
            gmailUserName,
            gmailPassword,
            gmailImapReceivingServer,
            SECURITY_TYPE.SSL,
            "993",
            gmailImapSmtpServer,
            true,
            "465",
            wrongUsername,
            gmailPassword);

      // Trying to add the same Gmail IMAP account
      app.zPageAddNewAccount.zNavigateTo();
      FormAddImapAccount accountForm = (FormAddImapAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopImapAccountItem);
      accountForm.zSubmit();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "SMTP authentication failed. Please check SMTP username and password.",
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

   @Test(description="Wrong Sending password when creating Gmail IMAP Account", groups = { "functional" })
   public void wrongSendingPasswordGmailIMAPAccount() throws HarnessException {

      String wrongPassword = ZimbraSeleniumProperties.getUniqueString();

      DesktopAccountItem desktopImapAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            gmailUserName,
            gmailUserName,
            gmailPassword,
            gmailImapReceivingServer,
            SECURITY_TYPE.SSL,
            "993",
            gmailImapSmtpServer,
            true,
            "465",
            gmailUserName,
            wrongPassword);

      // Trying to add the same Gmail IMAP account
      app.zPageAddNewAccount.zNavigateTo();
      FormAddImapAccount accountForm = (FormAddImapAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopImapAccountItem);
      accountForm.zSubmit();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "SMTP authentication failed. Please check SMTP username and password.",
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

   @Test(description="Failure in attempting to add duplicated Zimbra IMAP accounts", groups = { "functional" })
   public void addDuplicatedZimbraImapAccount() throws HarnessException {
      // Adding the Zimbra IMAP account
      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddZimbraImapAccountThruUI(
            ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.host", "localhost"),
            true,
            "465");

      // Trying to add the same Zimbra IMAP account
      app.zPageAddNewAccount.zNavigateTo();
      FormAddImapAccount accountForm = (FormAddImapAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      // Verifying error message
      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "account.ACCOUNT_EXISTS: email address already exists: ",
            "Verify error message of wrong password");

      app.zPageLogin.zNavigateTo();

      // Verifying in login page, the first added account is still there
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDeleteButton),
            "Delete account link exists");
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zBtnLoginDesktop),
            "Launch Zimbra Dekstop Button exists");
   }

   @Test(description="Failure in attempting to add Zimbra IMAP account, where the same Zimbra account being used already exists",
         groups = { "functional" })
   public void addImapAccountWithPreExistingZimbraAccount() throws HarnessException {
      // Adding the Zimbra account
      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddZimbraAccountThruUI();

      DesktopAccountItem desktopImapAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            desktopAccountItem.emailAddress,
            desktopAccountItem.emailAddress,
            desktopAccountItem.password,
            desktopAccountItem.incomingServer,
            SECURITY_TYPE.SSL,
            null,
            desktopAccountItem.incomingServer,
            true,
            "465",
            desktopAccountItem.emailAddress,
            desktopAccountItem.password);

      // Trying to add the same Zimbra IMAP account
      app.zPageAddNewAccount.zNavigateTo();
      FormAddImapAccount accountForm = (FormAddImapAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopImapAccountItem);
      accountForm.zSubmit();

      // Verifying error message
      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "account.ACCOUNT_EXISTS: email address already exists: ",
            "Verify error message of wrong password");

      app.zPageLogin.zNavigateTo();

      // Verifying in login page, the first added account is still there
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDeleteButton),
            "Delete account link exists");
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zBtnLoginDesktop),
            "Launch Zimbra Dekstop Button exists");
   }

   @Test(description="Failure in attempting to add Gmail IMAP account, where the same Gmail account being used already exists",
         groups = { "functional" })
   public void addImapAccountWithPreExistingGmailAccount() throws HarnessException {
      // Adding the Gmail account
      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddGmailAccountThruUI();

      DesktopAccountItem desktopImapAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            desktopAccountItem.emailAddress,
            desktopAccountItem.emailAddress,
            desktopAccountItem.password,
            gmailImapReceivingServer,
            SECURITY_TYPE.SSL,
            "993",
            gmailImapSmtpServer,
            true,
            "465",
            desktopAccountItem.emailAddress,
            desktopAccountItem.password);

      // Trying to add the same Gmail IMAP account
      app.zPageAddNewAccount.zNavigateTo();
      FormAddImapAccount accountForm = (FormAddImapAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopImapAccountItem);
      accountForm.zSubmit();

      // Verifying error message
      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "account.ACCOUNT_EXISTS: email address already exists: ",
            "Verify error message of wrong password");

      app.zPageLogin.zNavigateTo();

      // Verifying in login page, the first added account is still there
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDeleteButton),
            "Delete account link exists");
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zBtnLoginDesktop),
            "Launch Zimbra Dekstop Button exists");
   }

   // POP ACCOUNT
   @Test(description="Add Zimbra POP (SSL) account to ZD client with Sending Mail set to SSL", groups = { "smoke" })
   public void addZimbraPopSslSendingSslAccount() throws HarnessException {
      Stafzmtlsctl stafzmtlsctl = new Stafzmtlsctl();
      stafzmtlsctl.setServerAccess(SERVER_ACCESS.BOTH);
      _sslIsModified = true;

      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddZimbraPopAccountThruUI(ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.host", "localhost"),
            true,
            "465");

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(new ZimbraAccount(desktopAccountItem.emailAddress,
            desktopAccountItem.password));
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account's email address is greater than 0.");
   }

   @Test(description="Add Zimbra POP (SSL) account to ZD client with Sending Mail set to non SSL", groups = { "smoke-skip" })
   public void addZimbraPopSslSendingNonSslAccount() throws HarnessException {
      Stafzmtlsctl stafzmtlsctl = new Stafzmtlsctl();
      stafzmtlsctl.setServerAccess(SERVER_ACCESS.BOTH);
      _sslIsModified = true;

      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddZimbraPopAccountThruUI(ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.host", "localhost"),
            false,
            "25");

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(new ZimbraAccount(desktopAccountItem.emailAddress,
            desktopAccountItem.password));
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account's email address is greater than 0.");
   }

   @Test(description="Failure in adding Zimbra POP account with Receiving Mail security set to None",
         groups = { "functional" })
   public void addZimbraPopNonSslAccount() throws HarnessException {

      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopPopAccountItem(
            ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.host", "localhost"),
            SECURITY_TYPE.NONE,
            null,
            ZimbraSeleniumProperties.getStringProperty("server.host", "localhost"),
            false,
            null,
            ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password);

      FormAddPopAccount accountForm = (FormAddPopAccount)app.
            zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.POP);
      accountForm.zFill(desktopAccountItem);

      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "User account authentication failed. Please check username and password.",
            "Verify error message of disabled cleartext login");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Add Hotmail POP account to ZD client", groups = { "private" })
   public void addHotmailPopAccount() throws HarnessException {

      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddPopAccountThruUI();

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message, "Account added: " + desktopAccountItem.accountName, "Verify Account added message");

      app.zPageLogin.zLogin(new ZimbraAccount(desktopAccountItem.emailAddress,
            desktopAccountItem.password));
      List<FolderItem> folders = app.zTreeMail.zListGetFolders();
      ZAssert.assertGreaterThan(folders.size(), 0, "Folder with the active account's email address is greater than 0.");
   }

   @Test(description="Wrong email address format (alphabet characters) when creating POP Account", groups = { "functional" } )
   public void wrongEmailAddressFormatPopAccount1() throws HarnessException {
      String wrongEmailAddress = ZimbraSeleniumProperties.getUniqueString();
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            wrongEmailAddress,
            AjaxCommonTest.hotmailUserName,
            AjaxCommonTest.hotmailPassword,
            AjaxCommonTest.hotmailPopReceivingServer,
            SECURITY_TYPE.SSL,
            "995",
            AjaxCommonTest.hotmailPopSmtpServer,
            false,
            "25",
            AjaxCommonTest.hotmailUserName,
            AjaxCommonTest.hotmailPassword);

      FormAddPopAccount accountForm = (FormAddPopAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.POP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Please correct missing or invalid input.",
            "Verify error message of wrong email address format");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Wrong email address format (alphabet characters and '@') when creating POP Account", groups = { "functional" } )
   public void wrongEmailAddressFormatPopAccount2() throws HarnessException {
      String wrongEmailAddress = ZimbraSeleniumProperties.getUniqueString() + "@";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            wrongEmailAddress,
            AjaxCommonTest.hotmailUserName,
            AjaxCommonTest.hotmailPassword,
            AjaxCommonTest.hotmailPopReceivingServer,
            SECURITY_TYPE.SSL,
            "995",
            AjaxCommonTest.hotmailPopSmtpServer,
            false,
            "25",
            AjaxCommonTest.hotmailUserName,
            AjaxCommonTest.hotmailPassword);

      FormAddPopAccount accountForm = (FormAddPopAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.POP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Please correct missing or invalid input.",
            "Verify error message of wrong email address format");

      app.zPageLogin.zNavigateTo();

      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }

   @Test(description="Failure in attempting to add duplicated Zimbra POP accounts", groups = { "functional" })
   public void addDuplicatedZimbraPopAccount() throws HarnessException {
      // Adding the Zimbra POP account
      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddZimbraPopAccountThruUI(
            ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.host", "localhost"),
            true,
            "465");

      // Trying to add the same Zimbra POP account
      app.zPageAddNewAccount.zNavigateTo();
      FormAddPopAccount accountForm = (FormAddPopAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.POP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      // Verifying error message
      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "account.ACCOUNT_EXISTS: email address already exists: ",
            "Verify error message of wrong password");

      app.zPageLogin.zNavigateTo();

      // Verifying in login page, the first added account is still there
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDeleteButton),
            "Delete account link exists");
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zBtnLoginDesktop),
            "Launch Zimbra Dekstop Button exists");
   }

   @Test(description="Failure in attempting to add Hotmail POP account, where the same Hotmail POP account already exists",
         groups = { "private" })
   public void addPopAccountWithPreExistingHotmailAccount() throws HarnessException {
      // Adding the Hotmail POP account
      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddPopAccountThruUI();

      DesktopAccountItem desktopPopAccountItem = DesktopAccountItem.generateDesktopPopAccountItem(
            desktopAccountItem.emailAddress,
            desktopAccountItem.emailAddress,
            desktopAccountItem.password,
            hotmailPopReceivingServer,
            SECURITY_TYPE.SSL,
            "993",
            hotmailPopSmtpServer,
            true,
            "465",
            desktopAccountItem.emailAddress,
            desktopAccountItem.password);

      // Trying to add the same Hotmail POP account
      app.zPageAddNewAccount.zNavigateTo();
      FormAddPopAccount accountForm = (FormAddPopAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.POP);
      accountForm.zFill(desktopPopAccountItem);
      accountForm.zSubmit();

      // Verifying error message
      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "account.ACCOUNT_EXISTS: email address already exists: ",
            "Verify error message of wrong password");

      app.zPageLogin.zNavigateTo();

      // Verifying in login page, the first added account is still there
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDeleteButton),
            "Delete account link exists");
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zBtnLoginDesktop),
            "Launch Zimbra Dekstop Button exists");
   }

   @Test(description="Failure in attempting to add Zimbra POP account, where the same Zimbra account being used already exists",
         groups = { "functional" })
   public void addPopAccountWithPreExistingZimbraAccount() throws HarnessException {
      // Adding the Zimbra account
      DesktopAccountItem desktopAccountItem = app.zPageAddNewAccount.zAddZimbraAccountThruUI();

      DesktopAccountItem desktopPopAccountItem = DesktopAccountItem.generateDesktopPopAccountItem(
            desktopAccountItem.emailAddress,
            desktopAccountItem.emailAddress,
            desktopAccountItem.password,
            desktopAccountItem.incomingServer,
            SECURITY_TYPE.SSL,
            null,
            desktopAccountItem.incomingServer,
            true,
            "465",
            desktopAccountItem.emailAddress,
            desktopAccountItem.password);

      // Trying to add the same Zimbra POP account
      app.zPageAddNewAccount.zNavigateTo();
      FormAddPopAccount accountForm = (FormAddPopAccount)app.zPageAddNewAccount.zDropDownListSelect(
            DROP_DOWN_OPTION.POP);
      accountForm.zFill(desktopPopAccountItem);
      accountForm.zSubmit();

      // Verifying error message
      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "account.ACCOUNT_EXISTS: email address already exists: ",
            "Verify error message of wrong password");

      app.zPageLogin.zNavigateTo();

      // Verifying in login page, the first added account is still there
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDeleteButton),
            "Delete account link exists");
      ZAssert.assertTrue(app.zPageLogin.sIsElementPresent(PageLogin.Locators.zBtnLoginDesktop),
            "Launch Zimbra Dekstop Button exists");
   }

   @Test(description="Wrong receiving server name when creating Hotmail POP Account", groups = { "functional" } )
   public void wrongReceivingServerHotmailPopAccount() throws HarnessException {
      String wrongReceivingServer = ZimbraSeleniumProperties.getUniqueString();
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            hotmailUserName,
            hotmailUserName,
            hotmailPassword,
            wrongReceivingServer,
            SECURITY_TYPE.SSL,
            "995",
            hotmailPopSmtpServer,
            false,
            "25",
            hotmailUserName,
            hotmailPassword);

      FormAddPopAccount accountForm = (FormAddPopAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.POP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "\"" + wrongReceivingServer + ":995" + "\" host not found. Please check hostname and network connectivity.",
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

   @Test(description="Wrong None security receiving port when creating Hotmail POP Account", groups = { "functional" })
   public void wrongNonePortHotmailPopAccount() throws HarnessException {
      String wrongSslPort = "111";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            hotmailUserName,
            hotmailUserName,
            hotmailPassword,
            hotmailPopReceivingServer,
            SECURITY_TYPE.NONE,
            wrongSslPort,
            hotmailPopSmtpServer,
            false,
            "25",
            hotmailUserName,
            hotmailPassword);

      FormAddPopAccount accountForm = (FormAddPopAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.POP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Cannot connect to \"" + hotmailPopReceivingServer + ":" + wrongSslPort + "\". Please check host/port and network connectivity.",
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

   @Test(description="Wrong Ssl security receiving port when creating Hotmail POP Account", groups = { "functional" })
   public void wrongSslPortHotmailPopAccount() throws HarnessException {
      String wrongSslPort = "111";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            hotmailUserName,
            hotmailUserName,
            hotmailPassword,
            hotmailPopReceivingServer,
            SECURITY_TYPE.SSL,
            wrongSslPort,
            hotmailPopSmtpServer,
            false,
            "25",
            hotmailUserName,
            hotmailPassword);

      FormAddPopAccount accountForm = (FormAddPopAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.POP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Cannot connect to \"" + hotmailPopReceivingServer + ":" + wrongSslPort + "\". Please check host/port and network connectivity.",
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

   @Test(description="Wrong TLS security receiving port when creating Hotmail POP Account", groups = { "functional" })
   public void wrongTlsPortHotmailPopAccount() throws HarnessException {
      String wrongSslPort = "111";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            hotmailUserName,
            hotmailUserName,
            hotmailPassword,
            hotmailPopReceivingServer,
            SECURITY_TYPE.TLS,
            wrongSslPort,
            hotmailPopSmtpServer,
            false,
            "25",
            hotmailUserName,
            hotmailPassword);

      FormAddPopAccount accountForm = (FormAddPopAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.POP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Cannot connect to \"" + hotmailPopReceivingServer + ":" + wrongSslPort + "\". Please check host/port and network connectivity.",
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

   @Test(description="Wrong TLS If Available security receiving port when creating Hotmail POP Account", groups = { "functional" })
   public void wrongTlsIfAvailPortHotmailPopAccount() throws HarnessException {
      String wrongSslPort = "111";
      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            hotmailUserName,
            hotmailUserName,
            hotmailPassword,
            hotmailPopReceivingServer,
            SECURITY_TYPE.TLS_IF_AVAILABLE,
            wrongSslPort,
            hotmailPopSmtpServer,
            false,
            "25",
            hotmailUserName,
            hotmailPassword);

      FormAddPopAccount accountForm = (FormAddPopAccount)app.zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.POP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zPressButton(Button.B_VALIDATE_AND_SAVE);

      String message = app.zPageLogin.zGetMessage();
      ZAssert.assertStringContains(message,
            "Cannot connect to \"" + hotmailPopReceivingServer + ":" + wrongSslPort + "\". Please check host/port and network connectivity.",
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

   @AfterMethod(alwaysRun=true)
   public void cleanUp() throws HarnessException {
      if (_sslIsModified) {
         Stafzmtlsctl stafzmtlsctl = new Stafzmtlsctl();
         stafzmtlsctl.setServerAccess(SERVER_ACCESS.BOTH);
      }

      ZimbraAccount.ResetAccountZDC();
      app.zPageLogin.zNavigateTo();
   }
}
