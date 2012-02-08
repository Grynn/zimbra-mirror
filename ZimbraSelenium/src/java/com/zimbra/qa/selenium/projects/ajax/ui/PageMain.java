/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui;

import org.openqa.selenium.JavascriptExecutor;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogError.DialogErrorID;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning.DialogWarningID;


/**
 * @author Matt Rhoades
 *
 */
public class PageMain extends AbsTab {

	public static class Locators {
				
		public static final String zLogoffPulldown		= "css=td[id='skin_dropMenu'] td[id$='_dropdown']";
		public static final String zLogoffOption		= "css=tr[id='POPUP_logOff'] td[id$='_title']";
		
		public static final String zAppbarMail			= "id=zb__App__Mail_title";
		public static final String zAppbarContact		= "id=zb__App__Contacts_title";
		public static final String zAppbarCal			= "id=zb__App__Calendar_title";
		public static final String zAppbarTasks			= "id=zb__App__Tasks_title";
		public static final String zAppbarBriefcase		= "css=td[id=zb__App__Briefcase_title]";
		public static final String zAppbarPreferences	= "id=zb__App__Options_title";

		public static final String zAppbarSocialLocator	= 		"css=div[id^='zb__App__com_zimbra_social_'] td[id$='_title']";
		
		// 8.0 D1: public static final String ButtonRefreshLocatorCSS = "css=div[id='CHECK_MAIL'] td[id='CHECK_MAIL_left_icon'] div[class='ImgRefresh']";
		// 8.0 D2: public static final String ButtonRefreshLocatorCSS = "css=div[id='CHECK_MAIL'] td[id='CHECK_MAIL_left_icon'] div[class='ImgRefreshAll']";
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
	
	public DialogWarning zGetWarningDialog(DialogWarningID zimbra) {
		return (new DialogWarning(zimbra, this.MyApplication, this));
	}
	
	public DialogError zGetErrorDialog(DialogErrorID zimbra) {
		return (new DialogError(zimbra, this.MyApplication, this));
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
		boolean present = sIsElementPresent(Locators.zLogoffPulldown);
		if ( !present ) {
			logger.debug("Logoff button present = "+ present);
			return (false);
		}

		boolean loaded = zIsZimletLoaded();
		if ( !loaded) {
			logger.debug("zIsZimletLoaded() = "+ loaded);
			return (false);
		}
		
//		boolean minical = zIsMinicalLoaded();
//		if ( !minical ) {
//			logger.debug("zIsMinicalLoaded() = "+ minical);
//			return (false);
//		}
		
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
		if ( !((AppAjaxClient)MyApplication).zPageLogin.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageLogin.zNavigateTo();
		}
		((AppAjaxClient)MyApplication).zPageLogin.zLogin(ZimbraAccount.AccountZWC());
		zWaitForActive();
		
	}

	/**
	 * Click the logout button
	 * @throws HarnessException
	 */
	public void zLogout() throws HarnessException {
		logger.debug("logout()");

		tracer.trace("Logout of the "+ MyApplication.myApplicationName());

		zNavigateTo();

		if ( !sIsElementPresent(Locators.zLogoffPulldown) ) {
			throw new HarnessException("The logoff button is not present " + Locators.zLogoffPulldown);
		}

		// Click on logout pulldown
		zClickAt(Locators.zLogoffPulldown, "0,0");
		this.zWaitForBusyOverlay();
		
		if ( !sIsElementPresent(Locators.zLogoffOption) ) {
			throw new HarnessException("The logoff button is not present " + Locators.zLogoffOption);
		}

		// Click on logout pulldown
		zClick(Locators.zLogoffOption);
		this.zWaitForBusyOverlay();


		sWaitForPageToLoad();
		((AppAjaxClient)MyApplication).zPageLogin.zWaitForActive();

		((AppAjaxClient)MyApplication).zSetActiveAcount(null);

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
		throw new HarnessException("Main page does not have a Toolbar");
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

	

}
