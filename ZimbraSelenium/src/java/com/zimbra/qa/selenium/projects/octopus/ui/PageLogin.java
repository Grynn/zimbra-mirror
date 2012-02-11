package com.zimbra.qa.selenium.projects.octopus.ui;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;

public class PageLogin extends AbsTab {

	public static class Locators {

		// Buttons
		public static final String zBtnLogin = "css=input[class='zLoginButton']";


		// Text Input
		public static final String zInputUsername = "css=input[id='username']";
		public static final String zInputPassword = "css=input[id='password']";
		public static final String zInputRemember = "css=input[id='remember']";

		// Displayed text
		public static final String zDisplayedusername = "css=form[name='loginForm'] label[for='username']";
		public static final String zDisplayedcopyright = "css=div[class='copyright']";

	}

	public PageLogin(AbsApplication application) {
		super(application);

		logger.info("new " + PageLogin.class.getCanonicalName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		String locator = null;

		locator = Locators.zBtnLogin;

		// Look for the login button. 
		boolean present = sIsElementPresent(locator);
		if ( !present ) {
			logger.debug("zIsActive(): "+ present);
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
		if ( ((AppOctopusClient)MyApplication).zPageOctopus.zIsActive() ) {
			((AppOctopusClient)MyApplication).zPageOctopus.zLogout();
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

		tracer.trace("Login to the "+ MyApplication.myApplicationName() +" using user/password "+ account.EmailAddress +"/"+ account.Password);

		zNavigateTo();
		
		if (CodeCoverage.getInstance().isEnabled()) {
							
			////
			// Workaround for
			// https://bugzilla.zimbra.com/show_bug.cgi?id=70060
			////
			this.sOpen(ZimbraSeleniumProperties.getBaseURL());
			this.sWaitForPageToLoad();
				
			
		}
			

		zSetLoginName(account.EmailAddress);
		zSetLoginPassword(account.Password);

		// Click the Login button
		sClick(Locators.zBtnLogin);

		// Wait for the app to load
		sWaitForPageToLoad();
		((AppOctopusClient)MyApplication).zPageOctopus.zWaitForActive();

		((AppOctopusClient)MyApplication).zSetActiveAcount(account);

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



}
