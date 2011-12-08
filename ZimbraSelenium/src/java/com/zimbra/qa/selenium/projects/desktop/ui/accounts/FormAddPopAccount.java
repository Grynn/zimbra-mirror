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

public class FormAddPopAccount extends AbsForm {

   public static class Locators {
      // Text fields
      public static final String zAccountNameField = "css=input[id='accountName']";
      public static final String zFullNameField = "css=input[id='fromDisplay']";
      public static final String zEmailAddressField = "css=input[id='email']";
      public static final String zPasswordField = "css=input[id='password']";
      public static final String zReceivingUserNameField = "css=input[id='username']";
      public static final String zReceivingPasswordField = "css=input[id='password']";
      public static final String zReceivingServerField = "css=input[id='host']";
      public static final String zReceivingPortField = "css=input[id='port']";
      public static final String zSendingServerField = "css=input[id='smtpHost']";
      public static final String zSendingPortField = "css=input[id='smtpPort']";
      public static final String zSendingUserNameField = "css=input[id='smtpUsername']";
      public static final String zSendingPasswordField = "css=input[id='smtpPassword']";
      public static final String zSendingReplyToNameField = "css=input[id='replyToDisplay']";
      public static final String zSendingReplyToEmailField = "css=input[id='replyTo']";

      // Links
      public static final String zEditReceivingPortLink = "css=td[id='portLink']";
      public static final String zEditSendingPortLink = "css=td[id='smtpPortLink']";

      // Radio Buttons
      public static final String zSecurityNoneRadioButton = "css=input[id='cleartext']";
      public static final String zSecuritySSLRadioButton = "css=input[id='ssl']";
      public static final String zSecurityTLSRadioButton = "css=input[id='tls']";
      public static final String zSecurityTLSIfAvailableRadioButton = "css=input[id='tls_if_available']";

      // Buttons
      public static final String zValidateAndSaveButton = "css=div[id='saveButton']";
      public static final String zCancelButton = "css=div[id='cancelButton']";

      // Checkboxes
      public static final String zSendingSSLCheckbox = "css=input[id='smtpSsl']";
      public static final String zUseAuthCheckbox = "css=input[id='smtpAuth']";
      public static final String zEnableDebugLoggingCheckbox = "css=input[id='debugTraceEnabled']";
   }

   public FormAddPopAccount(AbsApplication application) {
      super(application);
      logger.info("new " + FormAddPopAccount.class.getCanonicalName());
   }

   @Override
   public String myPageName() {
      return (this.getClass().getName());
   }

   @Override
   public void zFill(IItem item) throws HarnessException {
      logger.info(FormAddPopAccount.class.getCanonicalName() +
      ".fill(IItem)");
      logger.info(item.prettyPrint());
      
      // Make sure the item is a DesktopAccountItem
      if ( !(item instanceof DesktopAccountItem) ) {
         throw new HarnessException("Invalid item type - must be DesktopAccountItem");
      }
      
      // Convert object to ContactGroupItem
      DesktopAccountItem desktopAccountItem  = (DesktopAccountItem) item;
      
      // Fill out the form
      if (desktopAccountItem.accountName != null &&
            !desktopAccountItem.accountName.equals("")) {
         sType(Locators.zAccountNameField, desktopAccountItem.accountName);
      }
      
      if (desktopAccountItem.emailAddress != null &&
            !desktopAccountItem.emailAddress.equals("")) {
         sType(Locators.zEmailAddressField, desktopAccountItem.emailAddress);
      }
      
      if (desktopAccountItem.fullName != null &&
            !desktopAccountItem.fullName.equals("")) {
         sType(Locators.zFullNameField, desktopAccountItem.fullName);
      }
      
      if (desktopAccountItem.receivingUsernname != null &&
            !desktopAccountItem.receivingUsernname.equals("")) {
         sType(Locators.zReceivingUserNameField, desktopAccountItem.receivingUsernname);
      }
      
      if (desktopAccountItem.receivingPassword != null &&
            !desktopAccountItem.receivingPassword.equals("")) {
         sType(Locators.zReceivingPasswordField, desktopAccountItem.receivingPassword);
      }
      
      if (desktopAccountItem.receivingIncomingServer != null &&
            !desktopAccountItem.receivingIncomingServer.equals("")) {
         sType(Locators.zReceivingServerField, desktopAccountItem.receivingIncomingServer);
      }
      
      if (desktopAccountItem.receivingPassword != null &&
            !desktopAccountItem.receivingPassword.equals("")) {
         sType(Locators.zReceivingPasswordField, desktopAccountItem.receivingPassword);
      }
      
      if (desktopAccountItem.receivingSecurityType != null) {
         String radioButtonLocator = null;
         switch (desktopAccountItem.receivingSecurityType) {
         case NONE:
            radioButtonLocator = Locators.zSecurityNoneRadioButton;
            break;
         case SSL:
            radioButtonLocator = Locators.zSecuritySSLRadioButton;
            break;
         case TLS:
            radioButtonLocator = Locators.zSecurityTLSRadioButton;
            break;
         case TLS_IF_AVAILABLE:
            radioButtonLocator = Locators.zSecurityTLSIfAvailableRadioButton;
            break;
         default:
            throw new HarnessException("Unuspported receivingSecurityType: " +
                  desktopAccountItem.receivingSecurityType);
         }
      
         sClick(radioButtonLocator);
      }
      
      if (desktopAccountItem.receivingPort != null &&
            !desktopAccountItem.receivingPort.equals("")) {
      
         if (!sGetText(Locators.zReceivingPortField).equals(
               desktopAccountItem.receivingPort)) {
            zClick(Locators.zEditReceivingPortLink);
            sType(Locators.zReceivingPortField, desktopAccountItem.receivingPort);            
         } else {
            logger.debug("Receiving Port is already the same as the new one," +
            " thus no need to change it");
            // Fall through
         }
      
      }
      
      if (desktopAccountItem.sendingSmtpServer != null &&
            !desktopAccountItem.sendingSmtpServer.equals("")) {
         sType(Locators.zSendingServerField, desktopAccountItem.sendingSmtpServer);
      }
      
      if (desktopAccountItem.sendingPassword != null &&
            !desktopAccountItem.sendingPassword.equals("")) {
         sType(Locators.zSendingPasswordField, desktopAccountItem.sendingPassword);
      }
      
      if (desktopAccountItem.sendingThroughSsl) {
         sCheck(Locators.zSendingSSLCheckbox);
      } else {
         sUncheck(Locators.zSendingSSLCheckbox);
      }
      
      if (desktopAccountItem.sendingPort != null &&
            !desktopAccountItem.sendingPort.equals("")) {
      
         if (!sGetText(Locators.zSendingPortField).equals(
               desktopAccountItem.sendingPort)) {
            zClick(Locators.zEditSendingPortLink);
            sType(Locators.zSendingPortField, desktopAccountItem.sendingPort);            
         } else {
            logger.debug("Sending Port is already the same as the new one," +
            " thus no need to change it");
            // Fall through
         }
      
      }
      
      if (desktopAccountItem.sendingUserName != null ||
            desktopAccountItem.sendingPassword != null) {
         sCheck(Locators.zUseAuthCheckbox);
         GeneralUtility.waitForElementPresent(this, Locators.zSendingUserNameField);
         if (!sGetText(Locators.zSendingUserNameField).equals(
               desktopAccountItem.sendingUserName)) {
            sType(Locators.zSendingUserNameField, desktopAccountItem.sendingUserName);
         }
      
         if (!sGetText(Locators.zSendingPasswordField).equals(
               desktopAccountItem.sendingPassword)) {
            sType(Locators.zSendingPasswordField, desktopAccountItem.sendingPassword);
         }
      }
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


   public void zPressButton(Button button) throws HarnessException {
      String locator = null;
      if (button == Button.B_VALIDATE_AND_SAVE) {
         locator = Locators.zValidateAndSaveButton;
      }
      sClick(locator);
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

   public void zCancel() throws HarnessException {
      GeneralUtility.waitForElementPresent(this, Locators.zCancelButton);
      sClick(Locators.zCancelButton);
   }

}
