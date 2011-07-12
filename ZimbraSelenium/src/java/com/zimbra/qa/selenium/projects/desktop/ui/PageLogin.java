package com.zimbra.qa.selenium.projects.desktop.ui;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;


public class PageLogin extends AbsTab {

	public static class Locators {

		// Buttons
		public static final String zBtnLogin = "xpath=//input[@class='zLoginButton']";

      // Desktop-specific
      public static final String zAddNewAccountButton = "css=td div[class*='ZPanel'][onclick*='OnAdd()']";
      public static final String zMyAccountsTab = "css=div[class$='ctive ZPanelFirstTab']";
      public static final String zBtnLoginDesktop = "css=div[id*='loginButton']";
      public static final String zDeleteButton = "css=div[class*='ZPanelInfoInner'] a[href*='OnDelete']";
		public static final String zAccountLabel = "css=div[class='ZAccountName']";
		public static final String zEmailLabel = "css=div[class='ZAccountEmail']";

		// Text Input
		public static final String zInputUsername = "xpath=//*[@id='username']";
		public static final String zInputPassword = "xpath=//*[@id='password']";
		public static final String zInputRemember = "xpath=//*[@id='remember']";
		
		// Displayed text
		public static final String zDisplayedZLoginAppName = "xpath=//*[@id='ZLoginAppName']";
		public static final String zDisplayedusername = "xpath=//form[@name='loginForm']//label[@for='username']";
		public static final String zDisplayedpassword = "xpath=//td[@class='zLoginLabelContainer']//label[@for='password']";
		public static final String zDisplayedremember = "xpath=//td[@class='zLoginCheckboxLabelContainer']//label[@for='remember']";
		public static final String zDisplayedwhatsthis = "xpath=//*[@id='ZLoginWhatsThisAnchor']";
		public static final String zDisplayedcopyright = "xpath=//div[@class='copyright']";
		public static final String zDisplayedMessage = "css=div[id='message']";
		public static final String zWelcomeMessage = "css=p[class='ZWelcome']:nth-of-type(NUMBER)";

	}
	
	
	
	public PageLogin(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageLogin.class.getCanonicalName());

	}

	@Override
	public boolean zIsActive() throws HarnessException {
	   AppType appType = ZimbraSeleniumProperties.getAppType();
	   String locator = null;

	   switch (appType) {
	   case AJAX:
	      locator = Locators.zBtnLogin;
	      break;
	   case DESKTOP:
	      locator = Locators.zAddNewAccountButton;
	      break;
	   default:
	      throw new HarnessException("Please add a support for appType: " + appType);
		}

		// Look for the login button. 
		boolean present = sIsElementPresent(locator);
		if ( !present ) {
			logger.debug("isActive() present = "+ present);
			return (false);
		}

		boolean visible = zIsVisiblePerPosition(locator, 0 , 0);
		if ( !visible ) {
			logger.debug("isActive() visible = "+ visible);
			return (false);
		}
		
		logger.debug("isActive() = " + true);
		return (true);
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zNavigateTo() throws HarnessException {

		if ( zIsActive() ) {
			// This page is already active.
			return;

		} else {

   		// Logout
   		if ( ((AppAjaxClient)MyApplication).zPageMain.zIsActive() ) {
   			((AppAjaxClient)MyApplication).zPageMain.zLogout();
   		} 
   		// Add Account page
   		else if (sIsElementPresent(Locators.zMyAccountsTab)) {
   		   sClick(Locators.zMyAccountsTab);
   		}

   		zWaitForActive();

		}
	}
	
	/**
	 * Login as the specified account
	 * @param account
	 * @throws HarnessException
	 */
	public void zLogin(ZimbraAccount account) throws HarnessException {
	   logger.debug("login(ZimbraAccount account)" + account.EmailAddress);
	   
	   tracer.trace("Login to the "+ MyApplication.myApplicationName() +" using user/password "+ account.EmailAddress +"/"+ account.Password);

	   zNavigateTo();

	   AppType appType = ZimbraSeleniumProperties.getAppType();
	   switch (appType) {
	   case AJAX:
	      // Fill out the form
	      zSetLoginName(account.EmailAddress);
	      zSetLoginPassword(account.Password);
	      
	      // Click the Login button
	      sClick(Locators.zBtnLogin);
	      break;

	   case DESKTOP:
	      // Click the Login button
	      if (!this.sIsElementPresent(Locators.zBtnLoginDesktop) ||
	            !this.sIsVisible(Locators.zBtnLoginDesktop)) {
	         if (this.sIsElementPresent(Locators.zMyAccountsTab)) {
	            sClick(Locators.zMyAccountsTab);
	         } else {
	            throw new HarnessException("It looks like account hasn't been created," +
	            		" please check the logic.");
	         }
	      }

	      GeneralUtility.waitForElementPresent(this, Locators.zBtnLoginDesktop);
	      sClick(Locators.zBtnLoginDesktop);
	      break;

	   default:
	      throw new HarnessException("Please add a support for appType: " + appType);
	   }

	   // Wait for the app to load
	   sWaitForPageToLoad();
	   ((AppAjaxClient)MyApplication).zPageMain.zWaitForActive();
	   
	   ((AppAjaxClient)MyApplication).zSetActiveAcount(account);
		
	}

	/**
	 * Removing account from login page
	 * @throws HarnessException
	 */
	public void zRemoveAccount() throws HarnessException {

	   GeneralUtility.waitForElementPresent(this, Locators.zDeleteButton);
	   String attribute = sGetAttribute(Locators.zDeleteButton + "@href");
	   ((AppAjaxClient)MyApplication).zDeleteDesktopAccount(attribute.split("'")[3], attribute.split("'")[1],
	         attribute.split("'")[7], attribute.split("'")[7], true);

	   Object[] params = {Locators.zDeleteButton};
      GeneralUtility.waitFor(null, this, false, "sIsElementPresent",
            params, WAIT_FOR_OPERAND.NEQ, true, 30000, 1000);
	}

	/**
    * Removing account from login page
    * @throws HarnessException
    * @return The confirmation message content
    */
   public String zRemoveAccountThroughClick() throws HarnessException {
      sClick(Locators.zDeleteButton);

      String confirmationMessage = this.sGetConfirmation();
      logger.debug("Selenium Confirmation: " + confirmationMessage);

      Object[] params = {Locators.zDeleteButton};
      GeneralUtility.waitFor(null, this, false, "sIsElementPresent",
            params, WAIT_FOR_OPERAND.NEQ, true, 30000, 1000);

      return confirmationMessage;
   }

   /**
	 * Add the specified name to the login name field
	 * @param name
	 * @throws HarnessException
	 */
	public void zSetLoginName(String name) throws HarnessException {
		String locator = Locators.zInputUsername;
		if ( name == null ) {
			throw new HarnessException("Name is null");
		}
			
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Login field does not exist "+ locator);
		}
		sType(locator, name);
	}
	
	/**
	 * Add the specified password to the login password field
	 * @param name
	 * @throws HarnessException
	 */
	public void zSetLoginPassword(String password) throws HarnessException {
		String locator = Locators.zInputPassword;
		if ( password == null ) {
			throw new HarnessException("Password is null");
		}
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Password field does not exist "+ locator);
		}
		sType(locator, password);
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		throw new HarnessException("Login page does not have a Toolbar");
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		throw new HarnessException("Login page does not have a Toolbar");
	}

	@Override
	public AbsPage zListItem(Action action, String item) throws HarnessException {
		throw new HarnessException("Login page does not have lists");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item) throws HarnessException {
		throw new HarnessException("Login page does not have lists");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption ,String item)
			throws HarnessException {
		throw new HarnessException("Login page does not have lists");
	}

	@Override
	public AbsPage zKeyboardShortcut(Shortcut shortcut) throws HarnessException {
		throw new HarnessException("No shortcuts supported in the login page");
	}

	public String zGetMessage() throws HarnessException {
	   return zGetMessage(false);
	}

   public String zGetMessage(boolean negativeTest) throws HarnessException {
      if (negativeTest) {
         GeneralUtility.waitForElementPresent(this, Locators.zDisplayedMessage, 60000);
      } else {
         GeneralUtility.waitForElementPresent(this, Locators.zDisplayedMessage);
      }

      return sGetText(Locators.zDisplayedMessage);
	}

   /**
	 * Compiling the welcome message lines into a String
	 * @return welcome message in 1 String
	 * @throws HarnessException
	 */
	public String zGetWelcomeMessage() throws HarnessException {
	   int i = 1;
	   boolean end = false;
	   StringBuilder output = new StringBuilder();
	   while (!end) {
	      String locator = Locators.zWelcomeMessage.replaceAll("NUMBER", Integer.toString(i));
	      if (i == 1) {
	         GeneralUtility.waitForElementPresent(this, locator);
	      }

	      if (!sIsElementPresent(locator)) {
	         end = true;
	         break;
	      }

	      output.append(sGetText(locator));
	      i++;
	   }

	   return output.toString();
	}
}
