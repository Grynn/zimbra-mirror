package com.zimbra.qa.selenium.projects.desktop.tests.accounts;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.PageLogin;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.FormAddZimbraAccount;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.PageAddNewAccount.DROP_DOWN_OPTION;

public class CancelAccount extends AjaxCommonTest {
   public CancelAccount() {
      logger.info("New " + CancelAccount.class.getCanonicalName());
      super.startingPage = app.zPageAddNewAccount;
      super.startingAccountPreferences = null;
   }

   @Test(description="Cancel Zimbra Account Creation", groups = { "functional" })
   public void cancelZimbraAccountCreation() throws HarnessException {

      FormAddZimbraAccount accountForm =
         (FormAddZimbraAccount)app.zPageAddNewAccount.zDropDownListSelect(
               DROP_DOWN_OPTION.ZIMBRA);
      accountForm.zCancel();
 
      String welcomeMessage = app.zPageLogin.zGetWelcomeMessage();
      ZAssert.assertStringContains(welcomeMessage,
            "Zimbra Desktop allows you to access email while you are disconnected from the internet.",
            "Verify welcome message is displayed");

      ZAssert.assertEquals(false,
            app.zPageLogin.sIsElementPresent(PageLogin.Locators.zDisplayedMessage),
            "Added account message is displayed");
   }
}
