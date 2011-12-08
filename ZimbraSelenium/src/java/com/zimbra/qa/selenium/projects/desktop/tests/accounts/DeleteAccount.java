package com.zimbra.qa.selenium.projects.desktop.tests.accounts;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.PageLogin;

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
      app.zPageAddNewAccount.zAddYahooAccountThruUI();

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
      app.zPageAddNewAccount.zAddGmailAccountThruUI();

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
      app.zPageAddNewAccount.zAddZimbraAccountThruUI();

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
      app.zPageAddNewAccount.zAddYahooAccountThruUI();

      app.zPageLogin.zLogin(new ZimbraAccount(yahooUserName, yahooPassword));
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
      app.zPageAddNewAccount.zAddGmailAccountThruUI();

      app.zPageLogin.zLogin(new ZimbraAccount(gmailUserName, gmailPassword));
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
      app.zPageAddNewAccount.zAddZimbraAccountThruUI();

      app.zPageLogin.zLogin(ZimbraAccount.AccountZDC());
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
