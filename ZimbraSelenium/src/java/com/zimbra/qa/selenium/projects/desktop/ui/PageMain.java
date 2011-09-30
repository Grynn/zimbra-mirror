/**
 *
 */
package com.zimbra.qa.selenium.projects.desktop.ui;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.desktop.ui.DialogError.DialogErrorID;


/**
 * @author Matt Rhoades
 *
 */
public class PageMain extends AbsTab {

	public static class Locators {

		public static final String zLogoffButton		= "css=td[id=skin_container_logoff] a";

		public static final String zAppbarMail			= "id=zb__App__Mail_title";
		public static final String zAppbarContact		= "id=zb__App__Contacts_title";
		public static final String zAppbarCal			= "id=zb__App__Calendar_title";
		public static final String zAppbarTasks			= "id=zb__App__Tasks_title";
		public static final String zAppbarBriefcase		= "css=td[id='zb__App__Briefcase_left_icon']";
		public static final String zAppbarPreferences	= "id=zb__App__Options_title";
		public static final String rssFolders = "css=div[id$='main_Mail-parent-FOLDER'] div[class='DwtTreeItemLevel1ChildDiv'] div td:contains('<FOLDER_NAME>')";
		// For Social tab, see Zimlet classes

	}


	public PageMain(AbsApplication application) {
		super(application);

		logger.info("new " + PageMain.class.getCanonicalName());

	}

	public Toaster zGetToaster() throws HarnessException {
		return (new Toaster(this.MyApplication));
	}

	public DialogError zGetErrorDialog(DialogErrorID zimbra) {
		return (new DialogError(zimbra, this.MyApplication, this));
	}



	public boolean zIsZimletLoaded() throws HarnessException {
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
		boolean present = sIsElementPresent(Locators.zLogoffButton)
		               && zIsZimletLoaded()
		           //    && zIsMinicalLoaded()
		               ;


		if ( !present ) {
			logger.debug("isActive() present = "+ present);
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

		if ( !sIsElementPresent(Locators.zLogoffButton) ) {
			throw new HarnessException("The logoff button is not present " + Locators.zLogoffButton);
		}

		// Click on logout
		sClick(Locators.zLogoffButton);

		sWaitForPageToLoad();
		((AppAjaxClient)MyApplication).zPageLogin.zWaitForActive();

		((AppAjaxClient)MyApplication).zSetActiveAcount(null);

	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {

		// Q. Should the tabs or help or logout be processed here?
		// A. I don't think those are considered "toolbars", so don't handle here for now (Matt)
		throw new HarnessException("Main page does not have a Toolbar");

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

		String locator = "//td[contains(@id,'ztb_appChooser_item_')]//div[contains(@id,'zb__App__tab_COMPOSE')]";
		if ( sIsElementPresent(locator) ) {
			logger.debug("Found compose tabs");

			String xpath = "//td[contains(@id,'ztb_appChooser_item_')]//div[contains(@id,'zb__App__tab_COMPOSE')]";
			int count = this.sGetXpathCount(xpath);
			for (int i = 1; i <= count; i++) {
				locator = xpath + "//td[contains(@id,'_left_icon')]["+ i +"]";
				if ( !sIsElementPresent(locator) )
					throw new HarnessException("Unable to find compose tab close icon "+ locator);
				this.zClick(locator);
			}
		}
	}



}
