package com.zimbra.qa.selenium.projects.desktop.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.FormMailNew;

public class PageMail extends AbsTab {
   public PageMail(AbsApplication application) {
      super(application);
      logger.info("New " + PageMail.class.getCanonicalName());
   }

   public static class Locators {
      // Buttons
      public static final String zNewIconButton = "css=td#zb__CLV__NEW_MENU_left_icon"; 
      public static final String zSendReceiveButton = "css=td#zb__CLV__CHECK_MAIL_left_icon";
   }

   @Override
   public AbsPage zListItem(Action action, String item) throws HarnessException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public AbsPage zListItem(Action action, Action option, String item)
         throws HarnessException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void zNavigateTo() throws HarnessException {
      ((AppDesktopClient)MyApplication).zPageMain.zNavigateTo();
      ZimbraSeleniumProperties.waitForElementPresent(this, PageMain.Locators.zMailTabs);
      sClick(PageMain.Locators.zMailTabs);
      ZimbraSeleniumProperties.waitForElementPresent(this, Locators.zNewIconButton);
   }

   @Override
   public AbsPage zToolbarPressButton(Button button) throws HarnessException {
      logger.info(myPageName() + " zToolbarPressButton("+ button +")");
      
      if ( button == null )
         throw new HarnessException("Button cannot be null!");
      
            
      // Default behavior variables
      //
      String locator = null;        // If set, this will be clicked
      AbsPage page = null; // If set, this page will be returned
      
      // Based on the button specified, take the appropriate action(s)
      //
      
      if ( button == Button.B_NEW ) {
         
         locator = "css=div[id^='ztb__'] td[id$='__NEW_MENU_title']";
         
         // Make sure the button exists
         if ( !this.sIsElementPresent(locator) )
            throw new HarnessException("Button is not present locator="+ locator +" button="+ button);
         
         // Click it
         this.zClick(locator);

         // Create the page
         page = new FormMailNew(this.MyApplication);
         
         // FALL THROUGH
         
      } // TODO
      /**else if ( button == Button.B_GETMAIL ) {
         
         if ( zGetPropMailView() == PageMailView.BY_MESSAGE ) {
            locator = "id="+ Locators.zGetMailIconBtnTVID;
         } else {
            locator = "id="+ Locators.zGetMailIconBtnCLVID;
         }

         
      } else if ( button == Button.B_DELETE ) {
         String id;
         if ( zGetPropMailView() == PageMailView.BY_MESSAGE ) {
            id = "zb__TV__DELETE_left_icon";
         } else {
            id = "zb__CLV__DELETE_left_icon";
         }
         
         // Check if the button is enabled
         String attrs = sGetAttribute("xpath=(//td[@id='"+ id +"']/div)@class");
         if ( attrs.contains("ZDisabledImage") ) {
            throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
         }

         locator = "id="+ id;
            
         
      } else if ( button == Button.B_MOVE ) {
         
         // Check if the button is enabled
         String attrs = sGetAttribute("xpath=(//td[contains(@id, '__MOVE_left_icon')]/div)@class");
         if ( attrs.contains("ZDisabledImage") ) {
            throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
         }

         locator = "css=td[id$='__MOVE_left_icon']";
         
         // Click it
         this.zClick(locator);

         page = new DialogMove(MyApplication);

         // FALL THROUGH
         
      } else if ( button == Button.B_PRINT ) {
         
         // Check if the button is enabled
         String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zPrintIconBtnID +"']/div)@class");
         if ( attrs.contains("ZDisabledImage") ) {
            throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
         }

         locator = "id='"+ Locators.zPrintIconBtnID;
         page = null;   // TODO
         throw new HarnessException("implement Print dialog");
         
      } else if ( button == Button.B_REPLY ) {
         
         page = new FormMailNew(this.MyApplication);;
         locator = "css=div[id$='__REPLY']";
         
         if ( !this.sIsElementPresent(locator) ) {
            throw new HarnessException("Reply icon not present "+ button);
         }
         
         // Check if the button is enabled
         String attrs = sGetAttribute("xpath=(//div[contains(@id,'__REPLY')])@class");
         if ( attrs.contains("ZDisabled") ) {
            throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
         }
         
      } else if ( button == Button.B_REPLYALL ) {
         
         page = new FormMailNew(this.MyApplication);;
         locator = "css=div[id$='__REPLY_ALL']";
         
         if ( !this.sIsElementPresent(locator) ) {
            throw new HarnessException("Reply All icon not present "+ button);
         }
         
         // Check if the button is enabled
         String attrs = sGetAttribute("xpath=(//div[contains(@id,'__REPLY_ALL')])@class");
         if ( attrs.contains("ZDisabled") ) {
            throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
         }
         
      } else if ( button == Button.B_FORWARD ) {
         
         page = new FormMailNew(this.MyApplication);;
         locator = "css=div[id$='__FORWARD']";
         
         if ( !this.sIsElementPresent(locator) ) {
            throw new HarnessException("Forward icon not present "+ button);
         }
         
         // Check if the button is enabled
         String attrs = sGetAttribute("xpath=(//div[contains(@id,'__FORWARD')])@class");
         if ( attrs.contains("ZDisabled") ) {
            throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
         }
         
      } else if ( (button == Button.B_RESPORTSPAM) || (button == Button.B_RESPORTNOTSPAM) ) {
         
         page = null;
         locator = "css=div[id$='__SPAM']";
         if ( !this.sIsElementPresent(locator) ) {
            throw new HarnessException("Spam icon not present "+ button);
         }
         
         // Check if the button is enabled
         String attrs = sGetAttribute("xpath=(//div[contains(@id,'__SPAM')])@class");
         if ( attrs.contains("ZDisabled") ) {
            throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
         }
         
      } else if ( button == Button.B_TAG ) {
         
         // For "TAG" without a specified pulldown option, just click on the pulldown
         // To use "TAG" with a pulldown option, see  zToolbarPressPulldown(Button, Button)
         //
         
         // Check if the button is enabled
         String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zTagMenuDropdownBtnID +"']/div)@class");
         if ( attrs.contains("ZDisabledImage") ) {
            throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
         }

         locator = "id='"+ Locators.zTagMenuDropdownBtnID +"'";
         
      } else if ( button == Button.B_NEWWINDOW ) {
         
         // Check if the button is enabled
         String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zDetachIconBtnID +"']/div)@class");
         if ( attrs.contains("ZDisabledImage") ) {
            throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
         }

         locator = "id='"+ Locators.zDetachIconBtnID;
         page = null;   // TODO
         throw new HarnessException("implement new window page ... probably just DisplayMail object?");
         
         
      } else if ( button == Button.B_LISTVIEW ) {
         
         // For "TAG" without a specified pulldown option, just click on the pulldown
         // To use "TAG" with a pulldown option, see  zToolbarPressPulldown(Button, Button)
         //
         
         // Check if the button is enabled
         String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zViewMenuDropdownBtnID +"']/div)@class");
         if ( attrs.contains("ZDisabledImage") ) {
            throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
         }

         locator = "id='"+ Locators.zViewMenuDropdownBtnID +"'";
         
      }*/ else {
         throw new HarnessException("no logic defined for button "+ button);
      }

      if ( locator == null ) {
         throw new HarnessException("locator was null for button "+ button);
      }
      
      // Default behavior, process the locator by clicking on it
      //
      
      // Make sure the button exists
      if ( !this.sIsElementPresent(locator) )
         throw new HarnessException("Button is not present locator="+ locator +" button="+ button);
      
      // Click it
      this.zClick(locator);
      
      // If page was specified, make sure it is active
      if ( page != null ) {
         
         // This function (default) throws an exception if never active
         page.zWaitForActive();
         
      }

      return (page);
   }

   @Override
   public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
         throws HarnessException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String myPageName() {
      return (this.getClass().getName());
   }

   @Override
   public boolean zIsActive() throws HarnessException {
      if (!sIsElementPresent(Locators.zNewIconButton)) {
         logger.debug(Locators.zNewIconButton + " is not present.");
         return false;
      }
      if (!zIsVisiblePerPosition(Locators.zNewIconButton, 0, 0)) {
         logger.debug(Locators.zNewIconButton + " is not visible.");
         return false;
      }
      logger.debug(Locators.zNewIconButton + " is present and visible.");
      return true;
   }
}
