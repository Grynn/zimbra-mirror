package com.zimbra.qa.selenium.projects.octopus.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class DialogHistory extends AbsDialog {

	public static class Locators {
		public static final Locators zFileHistoryMenuBar = new Locators(
				"css=div[id=activity-stream-pane-menu-bar]");
		public static final Locators zFileHistoryCloseBtn = new Locators(
				"css=span[id=activity-stream-pane-close-button]");

		public final String locator;

		private Locators(String locator) {
			this.locator = locator;
		}
	}

	public DialogHistory(AbsApplication application, AbsTab page) {
		super(application, page);

		logger.info("new " + DialogHistory.class.getCanonicalName());

	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {

		if (button == null)
			throw new HarnessException("button cannot be null");

		String locator = null;
		AbsPage page = null; // Does this ever result in a page being returned?

		if (button == Button.B_CLOSE) {

			locator = Locators.zFileHistoryCloseBtn.locator;

		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		// Default behavior, process the locator by clicking on it
		//

		// Click it
		zClickAt(locator, "0,0");

		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

		return (page);
	}

	@Override
	public boolean zIsActive() throws HarnessException {

		if (!this.sIsElementPresent(Locators.zFileHistoryMenuBar.locator))
			return (false);

		if (!this.zIsVisiblePerPosition(Locators.zFileHistoryMenuBar.locator,
				0, 0))
			return (false);

		return (true);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}
}
