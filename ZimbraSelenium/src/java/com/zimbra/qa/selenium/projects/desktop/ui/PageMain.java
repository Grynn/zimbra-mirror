package com.zimbra.qa.selenium.projects.desktop.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class PageMain extends AbsTab{

   public static class Locators {

      // TextFields
      public static final String zPeopleSearchField =
         "css=td#ztb_people_search_inputField div input[class*='people_search_input']";

      // Images
      // This is the search icon (magnifying glass)
      public static final String zPeopleSearchImage = "css=div[class*='ImgSearch2']";
      
      // Buttons
      public static final String zSetupButton = "css=tbody td#skin_container_logoff a[onclick*='LogOff']";

      // Tabs
      public static final String zMailTabs = "css=td#zb__App__Mail_left_icon";
      public static final String zAddressBookTabs = "css=td#zb__App__Contacts_left_icon";
      public static final String zCalendarTabs = "css=td#zb__App__Calendar_left_icon";
   }

   public PageMain(AbsApplication application) {
      super(application);

      logger.info("new " + PageMain.class.getCanonicalName());
   }

   @Override
   public String myPageName() {
      // TODO Auto-generated method stub
      return this.getClass().getName();
   }

   @Override
   public boolean zIsActive() throws HarnessException {
      // Check if the Search People textfield is present 
      boolean present = sIsElementPresent(Locators.zPeopleSearchField);
      if ( !present ) {
         logger.debug("isActive() present = "+ present);
         return (false);
      }

      // Check if the Search People textfield is visible per position 
      boolean visible = zIsVisiblePerPosition(Locators.zPeopleSearchField,
            0, 0);
      if ( !visible ) {
         logger.debug("isActive() visible = "+ visible);
         return (false);
      }
      
      logger.debug("isActive() = "+ true);
      return (true);
   }

   @Override
   public AbsPage zListItem(Action action, String item) throws HarnessException {
	   // TODO Auto-generated method stub
	   return null;
   }

   @Override
	public AbsPage zListItem(Action action, Button option, String item)
		throws HarnessException {
	   // TODO Auto-generated method stub
	   return null;
   }

   @Override
	public AbsPage zListItem(Action action, Button option, Button subOption ,String item)
			throws HarnessException {
		tracer.trace(action +" then "+ option + "," + subOption + " on item = "+ item);

		throw new HarnessException("implement me!");
	}
	
	@Override
	public void zNavigateTo() throws HarnessException {
	   if (zIsActive()) {
         logger.debug("Main page has already been reached.");
      } else {
         if (sIsElementPresent(PageAccounts.Locators.zAddNewAccountButton)) {
            throw new HarnessException("No account hasn't been created, please check your environment");
         } else if (sIsElementPresent(PageAccounts.Locators.zLoginButton)) {
            logger.debug("Currently in Accounts page, now navigating to main page");
            sClick(PageAccounts.Locators.zLoginButton);
         } else if (sIsElementPresent(PageAccounts.Locators.zAccountTypeDropDownList)) {
            logger.debug("Currently in Add New Account Tab on Accounts page, now navigating to main page");
            sClick(PageAccounts.Locators.zMyAccountsTab);
            GeneralUtility.waitForElementPresent(this, PageAccounts.Locators.zLoginButton);
            sClick(PageAccounts.Locators.zLoginButton);
         }
         GeneralUtility.waitForElementPresent(this, Locators.zSetupButton);
      }
	}
	
	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}
}
