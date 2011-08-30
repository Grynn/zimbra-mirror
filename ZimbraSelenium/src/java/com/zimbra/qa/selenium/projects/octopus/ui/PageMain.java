/**
 * 
 */
package com.zimbra.qa.selenium.projects.octopus.ui;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogError;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogError.DialogErrorID;

public class PageMain extends AbsTab {

	public static class Locators {

		public static final String zLogoffButton = "css=td[id=skin_container_logoff] a";
		public static final String zAppbarBriefcase = "css=td[id='zb__App__Briefcase_left_icon']";
		public static final String zTabMyFiles = "css=div.octopus-tab-label:contains(My Files)";
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

	@Override
	public boolean zIsActive() throws HarnessException {
		// Look for the My Files tab
		boolean present = sIsElementPresent(Locators.zTabMyFiles);
		sIsElementPresent("css=div.octopus-tab-label:contains(My Files)");

		if (!present) {
			logger.debug("isActive() present = " + present);
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

		if (zIsActive()) {
			// This page is already active
			return;
		}

		// 1. Logout
		// 2. Login as the default account
		if (!((AppOctopusClient) MyApplication).zPageLogin.zIsActive()) {
			((AppOctopusClient) MyApplication).zPageLogin.zNavigateTo();
		}
		((AppOctopusClient) MyApplication).zPageLogin.zLogin(ZimbraAccount
				.AccountZWC());
		zWaitForActive();

	}

	/**
	 * Click the logout button
	 * 
	 * @throws HarnessException
	 */
	public void zLogout() throws HarnessException {
		logger.debug("logout()");

		tracer.trace("Logout of the " + MyApplication.myApplicationName());

		zNavigateTo();

		if (!sIsElementPresent(Locators.zLogoffButton)) {
			throw new HarnessException("The logoff button is not present "
					+ Locators.zLogoffButton);
		}

		// Click on logout
		sClick(Locators.zLogoffButton);

		sWaitForPageToLoad();
		((AppOctopusClient) MyApplication).zPageLogin.zWaitForActive();

		((AppOctopusClient) MyApplication).zSetActiveAcount(null);

	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {

		// Q. Should the tabs or help or logout be processed here?
		// A. I don't think those are considered "toolbars", so don't handle
		// here for now (Matt)
		throw new HarnessException("Main page does not have a Toolbar");

	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		throw new HarnessException("Main page does not have a Toolbar");
	}

	@Override
	public AbsPage zListItem(Action action, String item)
			throws HarnessException {
		throw new HarnessException("Main page does not have lists");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item)
			throws HarnessException {
		throw new HarnessException("Main page does not have lists");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption,
			String item) throws HarnessException {
		throw new HarnessException("Main page does not have lists");
	}

	/**
	 * Close any extra compose tabs
	 */
	public void zCloseComposeTabs() throws HarnessException {

		String locator = "css=td[id^='ztb_appChooser_item_'] div[id^='zb__App__tab_COMPOSE']";
		if (sIsElementPresent(locator)) {
			logger.debug("Found compose tabs");

			int count = this.sGetCssCount(locator);
			for (int i = 1; i <= count; i++) {
				final String composeLocator = locator + ":nth-child(" + i
						+ ") td[id$='_left_icon']";
				if (!sIsElementPresent(composeLocator))
					throw new HarnessException(
							"Unable to find compose tab close icon "
									+ composeLocator);
				this.zClick(composeLocator);
				this.zWaitForBusyOverlay();
			}
		}
	}

}
