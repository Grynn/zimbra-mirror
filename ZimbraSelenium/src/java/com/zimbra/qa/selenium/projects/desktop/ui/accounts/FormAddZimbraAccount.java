package com.zimbra.qa.selenium.projects.desktop.ui.accounts;

import com.zimbra.qa.selenium.framework.items.DesktopAccountItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.projects.desktop.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.desktop.ui.PageLogin;

public class FormAddZimbraAccount extends AbsForm {

   public FormAddZimbraAccount(AbsApplication application) {
      super(application);
      logger.info("new " + FormAddZimbraAccount.class.getCanonicalName());
   }

   public static class Locators {
      // Text fields
      public static final String zAccountNameField = "css=input[id='accountName']";
      public static final String zEmailAddressField = "css=input[id='email']";
      public static final String zPasswordField = "css=input[id='password']";
      public static final String zIncomingServerField = "css=input[id='host']";
      public static final String zPortField = "css=input[id='port']";

      // Links
      public static final String zEditPortLink = "css=td[id='portLink']";

      // DropDown List
      public static final String zCheckMessagesFrequencyDropDown = "css=select[id='syncFreqSecs']";

      // Radio Buttons
      public static final String zSecurityNoneRadioButton = "css=input[id='cleartext']";
      public static final String zSecuritySSLRadioButton = "css=input[id='ssl']";

      // Buttons
      public static final String zValidateAndSaveButton = "css=div[id='saveButton']";
      public static final String zCancelButton = "css=div[id='cancelButton']";
   }

   @Override
   public String myPageName() {
      return (this.getClass().getName());
   }

   @Override
   public boolean zIsActive() throws HarnessException {
      if (!sIsElementPresent(Locators.zValidateAndSaveButton)) {
         logger.info("Validate and Save Button is not present");
         return false;
      } else {
         logger.info("Validate and Save Button is present");
      }

      if (!sIsElementPresent(Locators.zAccountNameField)) {
         logger.info("Account Name textfield is not present");
         return false;
      } else {
         logger.info("Account Name textfield is present");
      }

      if (!zIsVisiblePerPosition(Locators.zValidateAndSaveButton, 0, 0)) {
         logger.info("Validate and Save Button is not visible per position (0, 0)");
         return false;
      } else {
         logger.info("Validate and Save Button is visible per position (0, 0)");
      }

      if (!zIsVisiblePerPosition(Locators.zAccountNameField, 0, 0)) {
         logger.info("Account Name textfield is not visible per position (0, 0)");
         return false;
      } else {
         logger.info("Account Name textfield is visible per position (0, 0)");
      }

      return true;
   }

   @Override
   public void zFill(IItem item) throws HarnessException {
      logger.info(FormAddZimbraAccount.class.getCanonicalName() +
            ".fill(IItem)");
      logger.info(item.prettyPrint());

      // Make sure the item is a DesktopAccountItem
      if ( !(item instanceof DesktopAccountItem) ) {
         throw new HarnessException("Invalid item type - must be DesktopAccountItem");
      }

      // Convert object to ContactGroupItem
      DesktopAccountItem desktopAccountItem  = (DesktopAccountItem) item;

      // Fill out the form
      if (desktopAccountItem.accountName != null ||
            !desktopAccountItem.accountName.equals("")) {
         sType(Locators.zAccountNameField, desktopAccountItem.accountName);
      }

      if (desktopAccountItem.emailAddress != null ||
            !desktopAccountItem.emailAddress.equals("")) {
         sType(Locators.zEmailAddressField, desktopAccountItem.emailAddress);
      }

      if (desktopAccountItem.incomingServer != null ||
            !desktopAccountItem.incomingServer.equals("")) {
         sType(Locators.zIncomingServerField, desktopAccountItem.incomingServer);
      }

      if (desktopAccountItem.password != null ||
            !desktopAccountItem.password.equals("")) {
         sType(Locators.zPasswordField, desktopAccountItem.password);
      }

      if (desktopAccountItem.ssl) {
         sClick(Locators.zSecuritySSLRadioButton);
      } else {
         sClick(Locators.zSecurityNoneRadioButton);
      }

      if (desktopAccountItem.port != null ||
            !desktopAccountItem.port.equals("")) {
         if (!sGetText(Locators.zPortField).equals(desktopAccountItem.port)) {
            zClick(Locators.zEditPortLink);
            sType(Locators.zPortField, desktopAccountItem.port);
         } else {
            logger.debug("Port is already the same as the new one," +
            		" thus no need to change it");
            // Fall through
         }
      }
   }

   public void zPressButton(Button button) throws HarnessException {
      String locator = null;
      if (button == Button.B_VALIDATE_AND_SAVE) {
         locator = Locators.zValidateAndSaveButton;
      }
      sClick(locator);
   }

   @Override
   public void zSubmit() throws HarnessException {
      zSubmit(false);
   }

   public void zSubmit(boolean ssl) throws HarnessException {
      zPressButton(Button.B_VALIDATE_AND_SAVE);

      // Waiting for the login button or validate SSL button
      if (ssl) {
         int maxRetry = 30;
         int i = 0;
         while (i < maxRetry) {
            if (sIsElementPresent(PageLogin.Locators.zBtnLoginDesktop)) {
               logger.debug("This may be not the first time adding a SSL account to the ZCS server");
               break;
            } else if (((AppAjaxClient)MyApplication).zPageAddNewAccount.zMessageContains("Invalid or untrusted server SSL certificate")) {
               zPressButton(Button.B_VALIDATE_AND_SAVE);
               GeneralUtility.waitForElementPresent(this, PageLogin.Locators.zBtnLoginDesktop);
               break;
            } else {
               i++;
            }
         }

      } else {
         GeneralUtility.waitForElementPresent(this, PageLogin.Locators.zBtnLoginDesktop);
      }
   }

   public void zCancel() throws HarnessException {
      GeneralUtility.waitForElementPresent(this, Locators.zCancelButton);
      sClick(Locators.zCancelButton);
   }
}
