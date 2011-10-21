/**
 * 
 */
package com.zimbra.qa.selenium.projects.octopus.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogError;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogError.DialogErrorID;

public class PageSharing extends AbsTab {

	public static class Locators {
		public static final Locators zTabSharing = new Locators(
				"css=div.octopus-tab-label:contains(Sharing)");
		public static final Locators zTabSharingSelected = new Locators(
				"css=div[class^=octopus-tab sc-collection-item sel]>div.octopus-tab-label:contains(Sharing)");
		public static final Locators zShareHeader = new Locators(
		"css=div[id=share-header-view]");
		public static final Locators zShareView = new Locators(
				"css=div[id=octopus-share-view]");
		public static final Locators zShareNotificationListView = new Locators(
		"css=ul[id=share-notification-pending-page-list-view]");
		public static final Locators zShareNotificationListItem =  new Locators(
				"css=div[class=share-notification-list-item]");
		public static final Locators zSharedItemsView =  new Locators(
		"css=div[id=shared-items-view]");
		public static final Locators zSharedItemRow =  new Locators(
		"css=div[class=shared-item-row]");
		public static final Locators zIgnoredItemsView =  new Locators(
		"css=div[id=ignored-items-view]");
		public static final Locators zIgnoredItemRow =  new Locators(
		"css=div[class=shared-item-row]");
		
		
		public final String locator;

		private Locators(String locator) {
			this.locator = locator;
		}
	}

	public PageSharing(AbsApplication application) {
		super(application);

		logger.info("new " + PageSharing.class.getCanonicalName());

	}

	public Toaster zGetToaster() throws HarnessException {
		return (new Toaster(this.MyApplication));
	}

	public DialogError zGetErrorDialog(DialogErrorID zimbra) {
		return (new DialogError(zimbra, this.MyApplication, this));
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// Look for the Sharing tab
		boolean selected = sIsElementPresent(Locators.zTabSharingSelected.locator);

		if (!selected) {
			logger.debug("zIsActive(): "
					+ selected);
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

		// Make sure PageOctopus page is active
		if (!((AppOctopusClient) MyApplication).zPageOctopus.zIsActive()) {
			((AppOctopusClient) MyApplication).zPageOctopus.zNavigateTo();
		}

		String locator = Locators.zTabSharing.locator;

		if(!zWaitForElementPresent(locator,"5000")){
			throw new HarnessException(locator + " Not Present!");
		}

		// Click on Sharing tab
		zClickAt(locator, "0,0");

		zWaitForBusyOverlay();

		zWaitForActive();
	}

	
	public AbsPage zToolbarPressButton(Button button, IItem item) throws HarnessException {
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

		if (button == Button.B_ADD_TO_MY_FILES) {
			// Check if the button is disabled
			locator = Locators.zShareNotificationListItem.locator + ":contains(" + item.getName() + ") button:contains(Add to My Files)";
			
		} else if (button == Button.B_IGNORE) {
			// Check if the button is disabled
			locator = Locators.zShareNotificationListItem.locator + ":contains(" + item.getName() + ") button:contains(Ignore)";

		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		/*
		if (locator == null) {
			throw new HarnessException("locator was null for button " + button);
		}
		*/

		// Default behavior, process the locator by clicking on it
		//

		// Make sure the button exists
		if (!this.sIsElementPresent(locator))
			throw new HarnessException("Button is not present locator="
					+ locator + " button=" + button);

		// Click it
		this.zClickAt(locator,"0,0");

		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

		return (page);
	}
	
	
	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		throw new HarnessException("Implement me");
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
