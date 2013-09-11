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

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.touch.ui.mail.*;


/**
 * @author Matt Rhoades
 *
 */
public class PageMain extends AbsTab {

	public static class Locators {
				
		public static final String zSettingsPulldown	= "css=span[class='x-button-icon x-shown settings']";
		public static final String zSignOutMenu			= "css=div[class^='x-innerhtml']:contains('Sign Out')";
		
		public static final String zMailTab			= "css=div[id='ext-tab-1']";
		public static final String zContactsTab		= "css=div[id='ext-tab-2']";
		public static final String zCalendarTab			= "css=div[id='ext-tab-3']";

		public static final String ButtonRefreshLocatorCSS = "css=div[id='CHECK_MAIL'] td[id='CHECK_MAIL_left_icon']>div";
	}
	
	
	public PageMain(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageMain.class.getCanonicalName());

	}
	
	public Toaster zGetToaster() throws HarnessException {
		this.zWaitForBusyOverlay();
		Toaster toaster = new Toaster(this.MyApplication);
		logger.info("toaster is active: "+ toaster.zIsActive());
		return (toaster);
	}
	
	



	public boolean zIsZimletLoaded() throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver())
			return ("true".equals(sGetEval("return top.appCtxt.getZimletMgr().loaded")));
		else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium())
			return ("true".equals(sGetEval("selenium.browserbot.getCurrentWindow().top.appCtxt.getZimletMgr().loaded")));
		else
			return ("true".equals(sGetEval("this.browserbot.getUserWindow().top.appCtxt.getZimletMgr().loaded")));
	}
	
	public boolean zIsMinicalLoaded() throws HarnessException {
		return ("true".equals(sGetEval("this.browserbot.getUserWindow().top.appCtxt.getAppViewMgr().getCurrentViewComponent(this.browserbot.getUserWindow().top.ZmAppViewMgr.C_TREE_FOOTER) != null")));
	}
	
	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {

		// Look for the Logout button 
		// check if zimlet + minical loaded
		boolean present = sIsElementPresent(Locators.zSettingsPulldown);
		if ( !present ) {
			logger.debug("Settings button present = "+ present);
			return (false);
		}

		logger.debug("isActive() = "+ true);
		return (true);

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#navigateTo()
	 */
	@Override
	public void zNavigateTo() throws HarnessException {


		if ( zIsActive() ) {
			// This page is already active
			return;
		}
			
		// 1. Logout
		// 2. Login as the default account
		if ( !((AppTouchClient)MyApplication).zPageLogin.zIsActive() ) {
			((AppTouchClient)MyApplication).zPageLogin.zNavigateTo();
		}
		((AppTouchClient)MyApplication).zPageLogin.zLogin(ZimbraAccount.AccountZWC());
		
		// June 28, 2013 (9.0.0 Beta)
		// For coverage, it seems to take longer than 60 seconds for the page to load
		// Update the value to 120 seconds, and then make sure the harness doesn't slow
		// down, or else that could mean a slowdown in the client.
		//
		// zWaitForActive();		// Default: 60 seconds
		zWaitForActive(120000);
		
	}

	/**
	 * Click the logout button
	 * @throws HarnessException
	 */
	public void zLogout() throws HarnessException {
		logger.debug("logout()");

		tracer.trace("Logout of the "+ MyApplication.myApplicationName());

		zNavigateTo();

		if (ZimbraSeleniumProperties.isWebDriver()) {
			// Click on logout pulldown
			getElement("css=div[class=DwtLinkButtonDropDownArrow]").click();			
		}else{
		
			if ( !sIsElementPresent(Locators.zSettingsPulldown) ) {
				throw new HarnessException("The logoff button is not present " + Locators.zSettingsPulldown);
			}

			// Click on logout pulldown
			zClickAt(Locators.zSettingsPulldown, "0,0");
		}
		
		this.zWaitForBusyOverlay();
		
		if (ZimbraSeleniumProperties.isWebDriver()) {
			// Click on logout pulldown
			getElement("css=tr[id=POPUP_logOff]>td[id=logOff_title]").click();			
		}else{
			if ( !sIsElementPresent(Locators.zSignOutMenu) ) {
				throw new HarnessException("The logoff button is not present " + Locators.zSignOutMenu);
			}

			// Click on logout pulldown
			zClick(Locators.zSignOutMenu);
		}
		
		this.zWaitForBusyOverlay();

		/* TODO: ... debugging to be removed */
		//sWaitForPageToLoad();
		((AppTouchClient)MyApplication).zPageLogin.zWaitForActive();

		((AppTouchClient)MyApplication).zSetActiveAcount(null);

	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton(" + button + ")");

		// Q. Should the tabs or help or logout be processed here?
		// A. I don't think those are considered "toolbars", so don't handle here for now (Matt)

		if (button == null)
			throw new HarnessException("Button cannot be null!");

		// Default behavior variables
		//
		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if (button == Button.B_REFRESH) {
			
			locator = Locators.ButtonRefreshLocatorCSS;
			page = null;
			
		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for button " + button);
		}

		// Default behavior, process the locator by clicking on it
		//
		this.zClick(locator);
		SleepUtil.sleepSmall();

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		// If page was specified, make sure it is active
		if (page != null) {

			// This function (default) throws an exception if never active
			page.zWaitForActive();

		}

		return (page);
		
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ option +")");
		
		tracer.trace("Click pulldown "+ pulldown +" then "+ option);
		
		
		
		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (option == null)
			throw new HarnessException("Option cannot be null!");

		
		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned
		

		if (pulldown == Button.B_ACCOUNT) {
			
			
			if (option == Button.O_PRODUCT_HELP) {

				pulldownLocator = "css=div#skin_outer td#skin_dropMenu div.DwtLinkButtonDropDownArrow";
				optionLocator = "css=div[id^='POPUP'] div[id='documentation'] td[id$='_title']";
				
				//SeparateWindow window = new SeparateWindow(this.MyApplication);
				//window.zInitializeWindowNames();
				
				this.zClickAt(pulldownLocator, "0,0");
				this.zWaitForBusyOverlay();

				this.zClickAt(optionLocator, "0,0");
				this.zWaitForBusyOverlay();

				//return (window);
				
			} else if (option == Button.O_ABOUT) {

					pulldownLocator = "css=div#skin_outer td#skin_dropMenu div.DwtLinkButtonDropDownArrow";
					optionLocator = "css=div[id^='POPUP'] div[id='about'] td[id$='_title']";
					//page = new DialogInformational(DialogInformational.DialogWarningID.InformationalDialog, this.MyApplication, this);

					// FALL THROUGH
					
			} else {
				
				throw new HarnessException("no logic defined for pulldown/option " + pulldown + "/" + option);
			}
			

		} else {
			throw new HarnessException("no logic defined for pulldown/option " + pulldown + "/" + option);
		}

		
		
		// Default behavior
		if (pulldownLocator != null) {

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown + " option " + option + " pulldownLocator " + pulldownLocator + " not present!");
			}

			this.zClickAt(pulldownLocator, "0,0");

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			if (optionLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("Button " + pulldown + " option " + option + " optionLocator " + optionLocator + " not present!");
				}

				this.zClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				zWaitForBusyOverlay();
				
			}

		}
		
		// If we click on pulldown/option and the page is specified, then
		// wait for the page to go active
		if (page != null) {
			
			page.zWaitForActive();
			
		}

		// Return the specified page, or null if not set
		return (page);

	}

	@Override
	public AbsPage zListItem(Action action, String item) throws HarnessException {
		throw new HarnessException("Main page does not have lists");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item) throws HarnessException {
		throw new HarnessException("Main page does not have lists");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption ,String item)
			throws HarnessException {
		throw new HarnessException("Main page does not have lists");
	}	
	/**
	 * Close any extra compose tabs
	 */
	public void zCloseComposeTabs() throws HarnessException {
		
		String locator = "css=td[id^='ztb_appChooser_item_'] div[id^='zb__App__tab_COMPOSE']";
		if ( sIsElementPresent(locator) ) {
			logger.debug("Found compose tabs");
			
			int count = this.sGetCssCount(locator);
			for (int i = 1; i <= count; i++) {
				final String composeLocator = locator + ":nth-child("+i+") td[id$='_left_icon']";
				if ( !sIsElementPresent(composeLocator) ) 
					throw new HarnessException("Unable to find compose tab close icon "+ composeLocator);
				this.zClick(composeLocator);
				this.zWaitForBusyOverlay();
			}
		}
	}

	/**
	 * Change the URL (and reload) to access deep-link pages
	 * @param uri The URL to access (e.g. ?to=foo@foo.com&body=MsgContent&subject=MsgSubject&view=compose)
	 * @return the page that opens
	 * @throws HarnessException 
	 */
	public AbsPage zOpenDeeplink(ZimbraURI uri) throws HarnessException {
		logger.info("PageMain.zOpenDeeplink("+ uri.toString() + ")");
		
		AbsPage page = null;
		
		
		if ( !uri.getQuery().containsKey("view") ) {
			throw new HarnessException("query attribute 'view' must be specified");
		}
		
		if ( uri.getQuery().get("view").equals("compose") ) {
			
			page = new FormMailNew(this.MyApplication);
			
			// FALL THROUGH
			
		} else if ( uri.getQuery().get("view").equals("msg") ) {
			
			// page = new DisplayMail(this.MyApplication);
			throw new HarnessException("implement me!");
			
			// FALL THROUGH
			
		} else {
			
			throw new HarnessException("query attribute 'view' must be specified");
			
		}
		
		// Re-open the URL
		this.sOpen(uri.getURL().toString());

		if ( page != null ) {
			page.zWaitForActive();
		}
		
		return (page);

	}

	

}
