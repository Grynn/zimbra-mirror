package com.zimbra.qa.selenium.projects.octopus.ui;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class DisplayFilePreview extends AbsDisplay {

	public static class Locators {
		public static final Locators zFilePreview = new Locators(
				"css=div[id=my-files-preview]");
		public static final Locators zFileWatchIcon = new Locators(
				"css=div[id=my-files-preview-toolbar] span[class=file-info-view-watch-icon]");
		public static final Locators zFileImageIcon = new Locators(
				"css=div[id=my-files-preview-toolbar] span[class=file-info-view-file-icon]>span[class^=Img]");
		public static final Locators zHistory = new Locators(
				"css=div[id=my-files-preview] div[id=my-files-preview-toolbar] button[id=show-activitystream-button]");
		public static final Locators zComments = new Locators(
				"css=div[id=my-files-preview] div[id=my-files-preview-toolbar] button[id=my-files-preview-show-comments-button]");

		public final String locator;

		private Locators(String locator) {
			this.locator = locator;
		}
	}

	/**
	 * The various displayed fields in preview panel
	 */
	public static enum Field {
		Name, Version, Size, Body
	}

	public DisplayFilePreview(AbsApplication application) {
		super(application);
		logger.info("new " + DisplayFilePreview.class.getCanonicalName());
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
			throw new HarnessException("Button cannot be null!");

		// Default behavior variables
		String buttonLocator = null;
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		if (button == Button.B_WATCH) {
			buttonLocator = Locators.zFileWatchIcon.locator
					+ " span[class^=unwatched-icon]";
		} else if (button == Button.B_UNWATCH) {
			buttonLocator = Locators.zFileWatchIcon.locator
					+ " span[class^=watched-icon]";
		} else if (button == Button.B_HISTORY) {
			buttonLocator = Locators.zHistory.locator;

			page = new DialogFileHistory(MyApplication,
					((AppOctopusClient) MyApplication).zPageOctopus);
		} else if (button == Button.B_COMMENTS) {
			buttonLocator = Locators.zComments.locator;

			page = new DisplayFileComments(MyApplication);
		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (!this.sIsElementPresent(buttonLocator))
			throw new HarnessException("Button is not present: "
					+ buttonLocator);

		// Default behavior, process the locator by clicking on it

		// Click it
		zClick(buttonLocator);

		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

		if (page != null)
			page.zWaitForActive();

		return page;
	}

	/**
	 * Get the string value of the specified field
	 * 
	 * @return the displayed string value
	 * @throws HarnessException
	 */
	public String zGetFileProperty(Field field) throws HarnessException {
		logger.info("DocumentPreview.zGetDocumentProperty(" + field + ")");
		String locator = null;

		if (field == Field.Name) {
			throw new HarnessException("implement me!");
		} else if (field == Field.Body) {
			/*
			 * To get the body contents, need to switch iframes
			 */
			try {
				this.sSelectFrame("//iframe[contains(@class, 'PreviewFrame')]");
				String bodyLocator = "css=body";
				// Make sure the body is present
				if (!this.sIsElementPresent(bodyLocator))
					throw new HarnessException("Unable to preview body!");

				// Get the body value
				// String body = this.sGetText(bodyLocator).trim();
				String html = this.zGetHtml(bodyLocator);

				logger.info("DocumentPreview GetBody(" + bodyLocator + ") = "
						+ html);
				return (html);
			} finally {
				// Make sure to go back to the original iframe
				this.sSelectFrame("relative=top");
			}
		} else if (field == Field.Version) {
			locator = "css=";
			this.sGetText(locator);
			throw new HarnessException("implement me!");
		} else if (field == Field.Size) {
			locator = "css=";
			this.sGetText(locator);
			throw new HarnessException("implement me!");
		} else {
			throw new HarnessException(" no such field " + field);
		}
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		if (zWaitForElementPresent(Locators.zFilePreview.locator, "3000"))
			return true;
		else
			return false;
	}
}
