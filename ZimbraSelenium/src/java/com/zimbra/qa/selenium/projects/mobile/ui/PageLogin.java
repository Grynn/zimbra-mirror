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
package com.zimbra.qa.selenium.projects.mobile.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;


public class PageLogin extends AbsTab {

	public static class Locators {
		
		// TODO: Should this just extend a single class for all (Ajax, HTML, Mobile) login pages?
		
		// Buttons
		public static final String zBtnLogin = "css=input.ZLoginButton";
		
		// Text Input
		public static final String zInputUsername = "css=input#username";
		public static final String zInputPassword = "css=input#password";
		public static final String zInputRemember = "css=input#remember";
		
		// Displayed text
		public static final String zDisplayedZLoginAppName = "css=[id='ZLoginAppName']";
		public static final String zDisplayedusername = "css=form[name='loginForm'] label[for='username']";
		public static final String zDisplayedpassword = "css=td.zLoginLabelContainer label[for='password']";
		public static final String zDisplayedremember = "css=td.zLoginCheckboxLabelContainer label[for='remember']";
		public static final String zDisplayedwhatsthis = "css=[id='ZLoginWhatsThisAnchor']";
		public static final String zDisplayedcopyright = "css=div.copyright";
		
	}
		
	public ZimbraAccount DefaultLoginAccount = null;
	
	public PageLogin(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageLogin.class.getCanonicalName());

	}

	@Override
	public boolean zIsActive() throws HarnessException {
		
		// Make sure the application is loaded first
		if ( !MyApplication.zIsLoaded() )
			throw new HarnessException("Admin Console application is not active!");


		// Look for the login button. 
		boolean present = sIsElementPresent(Locators.zBtnLogin);
		if ( !present ) {
			logger.debug("isActive() present = "+ present);
			return (false);
		}
		
		boolean visible = zIsVisiblePerPosition(Locators.zBtnLogin, 0 , 0);
		if ( !visible ) {
			logger.debug("isActive() visible = "+ visible);
			return (false);
		}
		
		logger.debug("isActive() = "+ true);
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
		if ( ((AppMobileClient)MyApplication).zPageMain.zIsActive() ) {
			((AppMobileClient)MyApplication).zPageMain.zLogout();
		}
		
		zWaitForActive();
		
	}

	/**
	 * Login as DefaultLoginAccount
	 * @throws HarnessException
	 */
	public void zLogin() throws HarnessException {
		logger.debug("login()");

		zLogin(DefaultLoginAccount);
	}

	
	/**
	 * Login as the specified account
	 * @param account
	 * @throws HarnessException
	 */
	public void zLogin(ZimbraAccount account) throws HarnessException {
		logger.debug("login(ZimbraAccount account)" + account.EmailAddress);

		zNavigateTo();
		
		// Fill out the form
		zSetLoginName(account.EmailAddress);
		zSetLoginPassword(account.Password);
		
		// Click the Login button
		sClick(Locators.zBtnLogin);

		// Wait for the app to load
		((AppMobileClient)MyApplication).zPageMain.zWaitForActive();
		
		((AppMobileClient)MyApplication).zSetActiveAcount(account);
		
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
	public AbsPage zListItem(Action action, String item)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item) throws HarnessException {
		throw new HarnessException("Login page does not have lists");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption ,String item)
			throws HarnessException {
		throw new HarnessException("Mobile page does not have context menu");
	}	
	
	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		throw new HarnessException("Login page does not have lists");
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		throw new HarnessException("Login page does not have lists");
	}
	


}
