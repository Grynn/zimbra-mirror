package com.zimbra.qa.selenium.projects.desktop.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class PageAccounts extends AbsTab {


   // Inner class that store all the locators relevant to the page
   public static class Locators {

      // Buttons
      public static final String zAddNewAccountButton = "css=td div[class*='ZPanelButton'][onclick*='OnAdd()']";
      public static final String zLoginButton = "css=div[id*='loginButton']";
      public static final String zDeleteButton = "css=a[href*='OnDelete']";
   }

   public PageAccounts(AbsApplication application) {
      super(application);
      
      logger.info("new " + PageAccounts.class.getCanonicalName());
   }

   @Override
   public String myPageName() {
      return this.getClass().getName();
   }

   @Override
   public boolean zIsActive() throws HarnessException {
      // Check if the Add New Account Button is present
      boolean present = sIsElementPresent(Locators.zAddNewAccountButton);
      if ( !present ) {
         logger.debug("isActive() present = "+ present);
         return (false);
      }

      // Check if the Search People textfield is visible per position 
      boolean visible = zIsVisiblePerPosition(Locators.zAddNewAccountButton,
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
	public AbsPage zListItem(Action action, Action option, String item)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void zNavigateTo() throws HarnessException {
		// TODO Auto-generated method stub
		
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
