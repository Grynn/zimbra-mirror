package com.zimbra.qa.selenium.projects.octopus.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDisplay;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class DisplayFileComments extends AbsDisplay {

	public static class Locators {
		public static final Locators zFileCommentsView = new Locators(
				"css=div[id=comments-stream-view]");
		public static final Locators zFileCommentsViewCloseBtn = new Locators(
				"css=div[id=comments-stream-view] img[class=icon Cancel]");

		public final String locator;

		private Locators(String locator) {
			this.locator = locator;
		}
	}

	public DisplayFileComments(AbsApplication application) {
		super(application);
		logger.info("new " + DisplayFileComments.class.getCanonicalName());
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zPressButton(" + button + ")");

		tracer.trace("Click button " + button);

		if (button == null)
			throw new HarnessException("button cannot be null");

		// Default behavior variables
		String locator = null;
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		if (button == Button.B_CLOSE) {
			locator = Locators.zFileCommentsViewCloseBtn.locator;
		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (!this.sIsElementPresent(locator))
			throw new HarnessException("Button is not present: " + locator);

		// Default behavior, process the locator by clicking on it

		// Click it
		zClickAt(locator, "0,0");

		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

		if (page != null)
			page.zWaitForActive();

		return (page);
	}

	@Override
	public boolean zIsActive() throws HarnessException {

		if (!this.sIsElementPresent(Locators.zFileCommentsView.locator))
			return (false);

		if (!this.zIsVisiblePerPosition(Locators.zFileCommentsView.locator, 0,
				0))
			return (false);

		return (true);
	}

}
