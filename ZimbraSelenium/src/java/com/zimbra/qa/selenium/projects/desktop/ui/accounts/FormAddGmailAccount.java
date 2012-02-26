package com.zimbra.qa.selenium.projects.desktop.ui.accounts;

import com.zimbra.qa.selenium.framework.items.DesktopAccountItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.desktop.ui.PageLogin;

public class FormAddGmailAccount extends AbsForm {

   public static class Locators {
      // Text fields
      public static final String zAccountNameField = "css=input[id='accountName']";
      public static final String zFullNameField = "css=input[id='fromDisplay']";
      public static final String zEmailAddressField = "css=input[id='email']";
      public static final String zPasswordField = "css=input[id='password']";

      // Buttons
      public static final String zValidateAndSaveButton = "css=div[id='saveButton']";
      public static final String zCancelButton = "css=div[id='cancelButton']";
   }

   public FormAddGmailAccount(AbsApplication application) {
      super(application);
      logger.info("new " + FormAddGmailAccount.class.getCanonicalName());
   }

   @Override
   public String myPageName() {
      return (this.getClass().getName());
   }

   @Override
   public void zFill(IItem item) throws HarnessException {
      logger.info(FormAddGmailAccount.class.getCanonicalName() +
            ".fill(IItem)");
      logger.info(item.prettyPrint());

      // Make sure the item is a DesktopAccountItem
      if ( !(item instanceof DesktopAccountItem) ) {
         throw new HarnessException("Invalid item type - must be DesktopAccountItem");
      }

      // Convert object to ContactGroupItem
      DesktopAccountItem desktopAccountItem  = (DesktopAccountItem) item;

      // Fill out the form
      if (desktopAccountItem.accountName != null && !desktopAccountItem.accountName.equals("")) {
         sType(Locators.zAccountNameField, desktopAccountItem.accountName);
      }

      if (desktopAccountItem.emailAddress != null && !desktopAccountItem.emailAddress.equals("")) {
         sType(Locators.zEmailAddressField, desktopAccountItem.emailAddress);
      }

      if (desktopAccountItem.fullName != null && !desktopAccountItem.fullName.equals("")) {
         sType(Locators.zFullNameField, desktopAccountItem.fullName);
      }

      if (desktopAccountItem.password != null && !desktopAccountItem.password.equals("")) {
         sType(Locators.zPasswordField, desktopAccountItem.password);
      }

   }

   @Override
   public void zSubmit() throws HarnessException {
      sClick(Locators.zValidateAndSaveButton);
      GeneralUtility.waitForElementPresent(this, PageLogin.Locators.zBtnLoginDesktop);

   }

   public void zCancel() throws HarnessException {
      GeneralUtility.waitForElementPresent(this, Locators.zCancelButton);
      sClick(Locators.zCancelButton);
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
}
