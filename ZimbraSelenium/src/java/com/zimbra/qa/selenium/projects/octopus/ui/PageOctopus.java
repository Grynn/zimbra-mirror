/**
 * 
 */
package com.zimbra.qa.selenium.projects.octopus.ui;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.HttpStatus;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogError;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogError.DialogErrorID;

public class PageOctopus extends AbsTab {

	public static class Locators {
		public static final Locators zSignOutButton = new Locators(
				"css=div.header-links>a.(headerLink signOutLink):contains(sign out)");
		public static final Locators zTabMyFiles = new Locators(
				"css=div.octopus-tab-label:contains(My Files)");
		public static final Locators zTabSharing = new Locators(
		"css=div.octopus-tab-label:contains(Sharing)");
		
		public final String locator;

		private Locators(String locator) {
			this.locator = locator;
		}
	}

	public PageOctopus(AbsApplication application) {
		super(application);

		logger.info("new " + PageOctopus.class.getCanonicalName());

	}

	public Toaster zGetToaster() throws HarnessException {
		return (new Toaster(this.MyApplication));
	}

	public DialogError zGetErrorDialog(DialogErrorID zimbra) {
		return (new DialogError(zimbra, this.MyApplication, this));
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// Look for the Sign Out button
		boolean present = sIsElementPresent(Locators.zSignOutButton.locator);

		if (!present) {
			logger.debug("zIsActive(): PageOctopus sign out button present = " + present);
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

		// Login as the default account
		if (!((AppOctopusClient) MyApplication).zPageLogin.zIsActive()) {
			((AppOctopusClient) MyApplication).zPageLogin.zNavigateTo();
		}
		((AppOctopusClient) MyApplication).zPageLogin.zLogin(ZimbraAccount
				.AccountZWC());
		zWaitForActive();
	}

	/**
	 * Open url with logout attribute
	 * 
	 * @throws HarnessException
	 */
	public void zLogout() throws HarnessException {
		logger.debug("PageOctopus logout()");

		tracer.trace("Logout of the " + MyApplication.myApplicationName());

		zNavigateTo();

		// logout
		String url = this.getLocation();

		// Open url through RestUtil
		Map<String, String> map = new HashMap<String, String>();

		if (url.contains("?") && !url.endsWith("?")) {
			String query = url.split("\\?")[1];

			for (String p : query.split("&")) {
				if (p.contains("=")) {
					map.put(p.split("=")[0], p.split("=")[1].substring(0, 1));
				}
			}
		}

		map.put("loginOp", "logout");

		this.openUrl("", map);

		sWaitForPageToLoad();
		((AppOctopusClient) MyApplication).zPageLogin.zWaitForActive();

		((AppOctopusClient) MyApplication).zSetActiveAcount(null);

	}

	public String getLocation() {
		return ClientSessionFactory.session().selenium().getLocation();
	}

	public String openUrl(String url) throws HarnessException {

		this.sOpen(url);

		return url;
	}

	public String openUrl(String path, Map<String, String> params)
			throws HarnessException {
		ZimbraAccount account = ((AppOctopusClient) MyApplication)
				.zGetActiveAccount();
		if (null == account)
			account = ZimbraAccount.AccountZWC();

		RestUtil util = new RestUtil();

		util.setAuthentication(account);

		if (null != path && !path.isEmpty())
			util.setPath("/" + path + "/");
		else
			util.setPath("/");

		if (null != params && !params.isEmpty()) {
			for (Map.Entry<String, String> query : params.entrySet()) {
				util.setQueryParameter(query.getKey(), query.getValue());
			}
		}

		if (util.doGet() != HttpStatus.SC_OK)
			throw new HarnessException("Unable to open " + util.getLastURI());

		String url = util.getLastURI().toString();

		if (url.endsWith("?"))
			url = url.substring(0, url.length() - 1);

		this.sOpen(url);

		return url;
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton(" + button + ")");

		tracer.trace("Press the " + button + " button");

		if (button == null)
			throw new HarnessException("Button cannot be null!");

		// Default behavior variables
		//
		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if (button == Button.B_TAB_MY_FILES) {
			// Check if the button is disabled
			locator = Locators.zTabMyFiles.locator;
			page = new PageMyFiles(MyApplication);
		} else if (button == Button.B_TAB_SHARING) {
			// Check if the button is disabled
			locator = Locators.zTabSharing.locator;
			page = new PageSharing(MyApplication);
		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for button " + button);
		}

		// Default behavior, process the locator by clicking on it
		//

		// Make sure the button exists
		if (!this.sIsElementPresent(locator))
			throw new HarnessException("Button is not present locator="
					+ locator + " button=" + button);

		// Click it
		this.zClick(locator);

		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

		return (page);
	}

	
	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		throw new HarnessException("Implement me");
	}

	@Override
	public AbsPage zListItem(Action action, String item)
			throws HarnessException {
		throw new HarnessException("Implement me");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item)
			throws HarnessException {
		throw new HarnessException("Implement me");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption,
			String item) throws HarnessException {
		throw new HarnessException("Implement me");
	}
}
