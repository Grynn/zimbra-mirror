package com.zimbra.qa.selenium.projects.desktop.ui.mail;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class DialogCreateFolder extends AbsDialog {

   public DialogCreateFolder(AbsApplication application, AbsTab tab) {
      super(application, tab);
      
      logger.debug("new " + DialogCreateFolder.class.getCanonicalName());
   }

   public static class Locators {
      public static final String zDialogId = "css=div[class*='DwtDialog WindowOuterContainer']";

      // Textfields
      public static final String zNameField = "css=input[id$='_name'][class*='Field']";

      // Buttons
      public static final String zOkButton = "css=tr>td>div[id*='button2']";
      public static final String zCancelButton = "css=tr>td>div[id*='button1']";
   }

   /**
    * Enter text into the move message dialog folder name field
    * @param folder
    */
   public void zEnterFolderName(String folder) throws HarnessException {
      logger.info(myPageName() + " zEnterFolderName("+ folder +")");
      
      if ( folder == null ) 
         throw new HarnessException("folder must not be null");
      
      String locator = Locators.zNameField;

      if ( !this.sIsElementPresent(locator) )
         throw new HarnessException("unable to find folder name field "+ locator);
      
      sType(locator, folder);      
   }

   @Override
   public String myPageName() {
      return (this.getClass().getName());
   }

   @Override
   public AbsPage zClickButton(Button button) throws HarnessException {
      logger.info(myPageName() + " zClickButton("+ button +")");

      String locator = null;
      
      if ( button == Button.B_OK ) {
         
         // TODO: L10N this      
         locator = Locators.zOkButton;

      } else if ( button == Button.B_CANCEL ) {
         
         // TODO: L10N this
         locator = Locators.zCancelButton;

      } else {
         throw new HarnessException("Button "+ button +" not implemented");
      }
      
      // Default behavior, click the locator
      //
      
      // Make sure the locator was set
      if ( locator == null ) {
         throw new HarnessException("Button "+ button +" not implemented");
      }
      
      // Make sure the locator exists
      if ( !this.sIsElementPresent(locator) ) {
         throw new HarnessException("Button "+ button +" locator "+ locator +" not present!");
      }
      
      this.zClick(locator);
      
      return (null);
   }

   @Override
   public String zGetDisplayedText(String locator) throws HarnessException {
      logger.info(myPageName() + " zGetDisplayedText("+ locator +")");
      
      if ( locator == null )
         throw new HarnessException("locator was null");
      
      return (this.sGetText(locator));
   }

   @Override
   public boolean zIsActive() throws HarnessException {
      logger.info(myPageName() + " zIsVisible()");
      String locator = Locators.zDialogId;

      if ( !this.sIsElementPresent(locator) ) {
         return (false); // Not even present
      }

      if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
         return (false);
      }

      // Yes, visible
      logger.info(myPageName() + " zIsVisible() = true");
      return (true);
   }
}
