package com.zimbra.qa.selenium.projects.desktop.ui.accounts;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.FormAddGmailAccount.Locators;

public class FormAddYahooAccount extends AbsForm {
   public static class Locators {
      // Text fields
      public static final String zAccountNameField = "css=input[id='accountName']";
      // Buttons
      public static final String zValidateAndSaveButton = "css=div[id='saveButton']";
      public static final String zCancelButton = "css=div[id='cancelButton']";
   }
   public FormAddYahooAccount(AbsApplication application) {
      super(application);
      logger.info("new " + FormAddZimbraAccount.class.getCanonicalName());
   }

   @Override
   public String myPageName() {
      return (this.getClass().getName());
   }

   @Override
   public void zFill(IItem item) throws HarnessException {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void zSubmit() throws HarnessException {
      // TODO Auto-generated method stub
      
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
