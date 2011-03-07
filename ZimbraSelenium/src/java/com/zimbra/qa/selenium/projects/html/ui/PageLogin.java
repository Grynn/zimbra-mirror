package com.zimbra.qa.selenium.projects.html.ui;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.*;


public class PageLogin extends AbsTab {

	public static class Locators {
		public static final String zBtnLogin = "xpath=//input[@class='zLoginButton']";
		public static final String zInputUsername = "xpath=//*[@id='username']";
		public static final String zInputPassword = "xpath=//*[@id='password']";
		public static final String zInputRemember = "xpath=//*[@id='remember']";

	}



	public PageLogin(AbsApplication application) {
		super(application);

		logger.info("new " + PageLogin.class.getCanonicalName());

	}

	@Override
	public boolean zIsActive() throws HarnessException {
		String locator = Locators.zBtnLogin;


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
		if ( ((AppHtmlClient)MyApplication).zPageMain.zIsActive() ) {
			((AppHtmlClient)MyApplication).zPageMain.zLogout();
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

		zSetLoginName(account.EmailAddress);
		zSetLoginPassword(account.Password);

		// Click the Login button
		sClick(Locators.zBtnLogin);

		// Wait for the app to load
		sWaitForPageToLoad();
		((AppHtmlClient)MyApplication).zPageMain.zWaitForActive();

		((AppHtmlClient)MyApplication).zSetActiveAcount(account);

		// TODO: maybe handle this in the pulldown options?
		// Force the html client to load
		ClientSessionFactory.session().selenium().open(ZimbraSeleniumProperties.getBaseURL());
		

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
