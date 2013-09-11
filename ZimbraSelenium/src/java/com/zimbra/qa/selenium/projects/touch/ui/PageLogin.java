/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.touch.ui;

import java.util.Date;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.*;


public class PageLogin extends AbsTab {

	public static class Locators {

		// Buttons
		public static final String zBtnLogin = "css=input[class*=LoginButton]";


		// Desktop-specific
		public static final String zAddNewAccountButton = "css=td div[class*='ZPanel'][onclick*='OnAdd()']";
		public static final String zMyAccountsTab = "css=div[class$='ctive ZPanelFirstTab']";
		public static final String zBtnLoginDesktop = "css=div[id*='loginButton']";
		public static final String zDeleteButton = "css=div[class*='ZPanelInfoInner'] a[href*='OnDelete']";

		// Text Input
		public static final String zInputUsername = "css=input[id='username']";
		public static final String zInputPassword = "css=input[id='password']";
		public static final String zInputRemember = "css=input[id='remember']";

		// Displayed text
		public static final String zDisplayedusername = "css=form[name='loginForm'] label[for='username']";
		public static final String zDisplayedcopyright = "css=div[class='copyright']";
		
		// Toolbar links
		public static final String zLogoutLink = "css=[id='skin_container_logoff']>a";

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
		case TOUCH:
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
		if ( ((AppTouchClient)MyApplication).zPageMain.zIsActive() ) {
			((AppTouchClient)MyApplication).zPageMain.zLogout();
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

		Date start = new Date();
		
		try {
			
			zSetLoginName(account.EmailAddress);
			zSetLoginPassword(account.Password);

			// Click the Login button
			sClick(Locators.zBtnLogin);

			// Wait for the app to load
			/* TODO: ... debugging to be removed */
			//sWaitForPageToLoad();
			
			((AppTouchClient)MyApplication).zPageMain.zWaitForActive(180000);

			((AppTouchClient)MyApplication).zSetActiveAcount(account);


		} finally {
			
			SleepMetrics.RecordProcessing((new Throwable()).getStackTrace(), start, new Date());

		}
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
		if (ZimbraSeleniumProperties.isWebDriver()){
		    clearField(locator);
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
		if (ZimbraSeleniumProperties.isWebDriver()){
		    clearField(locator);
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
