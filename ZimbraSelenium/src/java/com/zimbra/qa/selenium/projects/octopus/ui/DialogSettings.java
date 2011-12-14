package com.zimbra.qa.selenium.projects.octopus.ui;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class DialogSettings extends AbsDialog {
	public static class Locators {
		public static final Locators zSettingsPage = new Locators(
				"css=div[class*=SettingsPage]");
		public static final Locators zUserName = new Locators(
				"css=div[class=settings-pane] div[class=userName]");
		public static final Locators zChangePictureBtn = new Locators(
				"css=div[class=settings-pane] button:contains(Change Picture)");
		public static final Locators zChangeNameBtn = new Locators(
				"css=div[class=settings-pane] button:contains(Change Name)");
		public static final Locators zQuotaUsage = new Locators(
				"css=div[class=settings-pane] span[class=settings-quota-usage]");
		public static final Locators zChangePasswordBtn = new Locators(
				"css=div[class=settings-pane] button:contains(Change Password)");
		public static final Locators zCloseBtn = new Locators(
				"css=div[class*=SettingsDialogContent] span[id=settings-close-button] span:contains(x)");
		public static final Locators zDoneBtn = new Locators(
				"css=div[class*=SettingsDialogContent] div[class=settings-footer-buttons] button:contains(Done)");
		public static final Locators zDevicesListView = new Locators(
				"css=div[id=devices-list-view]");
		public static final Locators zDevicesListContainer = new Locators(
				"css=div[id=devices-list-view] div[class=devices-list-container]");
		public static final Locators zDevicesListItem = new Locators(
				"css=div[id=devices-list-view] div[class=devices-list-container] span[class=devices-list-item]");
		public static final Locators zUnlinkAndWipeBtn = new Locators(
				"css=div[id=devices-list-view] div[class=devices-list-container] span[class=devices-wipe-button] button:contains(Unlink & Wipe)");

		public final String locator;

		private Locators(String locator) {
			this.locator = locator;
		}
	}

	public DialogSettings(AbsApplication application, AbsTab page) {
		super(application, page);

		logger.info("new " + DialogMove.class.getCanonicalName());
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");

		AbsPage page = null;
		String locator = null;

		if (button == Button.B_CLOSE) {

			locator = Locators.zCloseBtn.locator;
		} else if (button == Button.B_DONE) {

			locator = Locators.zDoneBtn.locator;
		} else {
			throw new HarnessException("Button " + button + " not implemented");
		}

		// Default behavior, click the locator
		// Make sure the locator exists
		if (!this.sIsElementPresent(locator))
			throw new HarnessException("locator is not present: " + locator
					+ " button=" + button);

		this.zClick(locator);

		this.zWaitForBusyOverlay();

		if (page != null)
			page.zWaitForActive();

		return (page);
	}

	public AbsPage zListItem(Action action, Button button, String itemName)
			throws HarnessException {
		logger.info(myPageName() + " zListItem(" + action + ", " + button
				+ ", " + itemName + ")");

		tracer.trace("Click button " + button);

		// Validate the arguments
		if (action == null) {
			throw new HarnessException("Action cannot be null");
		}
		if (button == null)
			throw new HarnessException("Button cannot be null!");

		if (itemName == null)
			throw new HarnessException("Item name cannot be null!");

		// Default behavior variables
		String locator = null; // If set, this will be expanded
		AbsPage page = null; // If set, this page will be returned

		// Based on action and the button specified, take the appropriate action(s)
		if (action == Action.A_LEFTCLICK) {

			if (button == Button.B_UNLINK_AND_WIPE) {

				locator = Locators.zDevicesListContainer.locator + ":contains(" + itemName + ") button:contains(Unlink & Wipe)";
			} else {
				logger.info("no logic defined for " + button);
			}
			
			// Make sure the locator exists
			if (!this.sIsElementPresent(locator))
				throw new HarnessException("locator is not present: " + locator
						+ " button=" + button);
					
			this.zClick(locator);

			this.zWaitForBusyOverlay();
			
		} else {
			logger.info("no logic defined for " + action);
		}

		if (page != null)
			page.zWaitForActive();

		return page;
	}

	public boolean zIsDeviceDisabled(String deviceName) throws HarnessException {
		logger.info(myPageName() + " zIsDEviceDisabled() " + deviceName);
		
		String locator = Locators.zDevicesListContainer.locator;
		
		return this.zWaitForElementPresent(locator
				+ ":contains("
				+ deviceName
				+ ") span:contains(Disabled)","3000");	
	}
	
	@Override
	public boolean zIsActive() throws HarnessException {

		logger.info(myPageName() + " zIsActive()");

		String locator = Locators.zSettingsPage.locator;

		if (!this.sIsElementPresent(locator)) {
			return (false); // Not even present
		}

		if (!this.zIsVisiblePerPosition(locator, 0, 0)) {
			return (false); // Not visible per position
		}

		// Yes, visible
		logger.info(myPageName() + " zIsActive() = true");
		return (true);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}
}
