package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.cs.account.Provisioning.ZimletBy;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
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
		}
		
		
		// Logout
		if ( ((AppAjaxClient)MyApplication).zPageMain.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageMain.zLogout();
		}
		
		zWaitForActive();
		
	}


	
	/**
	 * Login as the specified account
	 * @param account
	 * @throws HarnessException
	 */
	public void zLogin(ZimbraAccount account) throws HarnessException {
	   logger.debug("login(ZimbraAccount account)" + account.EmailAddress);

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
	public AbsPage zKeyboardShortcut(Shortcut shortcut) throws HarnessException {
		throw new HarnessException("No shortcuts supported in the login page");
	}



}
