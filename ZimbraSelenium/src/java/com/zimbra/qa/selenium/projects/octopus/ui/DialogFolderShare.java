package com.zimbra.qa.selenium.projects.octopus.ui;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class DialogFolderShare extends AbsDialog {
	public static class Locators {
		public static final Locators zShareBtn = new Locators(
						"css=div[class=share-buttons]>button:contains(Share)");
		public static final Locators zCancelBtn = new Locators(
				"css=div[class=share-buttons]>button:contains(Cancel)");
		public static final Locators zLeaveThisSharedFolderBtn = new Locators(
				"css=div[class=share-buttons]>button:contains(Leave this shared folder)");
		public static final Locators zViewInput = new Locators(
				"css=div[class=octopus-share-item-view]>div[class=permission-input] input[id=DWT1]");
		public static final Locators zViewAndEditInput = new Locators(
				"css=div[class=octopus-share-item-view]>div[class=permission-input] input[id=DWT2]");
		public static final Locators zViewEditAndShareInput = new Locators(
				"css=div[class=octopus-share-item-view]>div[class=permission-input] input[id=DWT3]");
		public static final Locators zShowMessageLink = new Locators(
				"css=div[class=octopus-share-item-view]>div[class=permission-label info-message] span[class=customLink]>span:contains(Show message)");
		public static final Locators zMessageInput = new Locators(
				"css=div[class=octopus-share-item-view]>div[class=permission-input] textarea[class=field]");
		public static final Locators zShareInfoField = new Locators(
				"css=div[class=ShareInfo]");
		
		public final String locator;

		private Locators(String locator) {
			this.locator = locator;
		}
	}

	public DialogFolderShare(AbsApplication application, AbsTab page) {
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

		if (button == Button.B_SHARE) {

			locator = Locators.zShareBtn.locator;
			
		} else if (button == Button.B_CANCEL) {

			locator = Locators.zCancelBtn.locator;
		}  else if (button == Button.B_SHOW_MESSAGE) {

			locator = Locators.zShowMessageLink.locator;
		}  else if (button == Button.B_LEAVE_THIS_SHARED_FOLDER) {

			locator = Locators.zLeaveThisSharedFolderBtn.locator;
		} else {
			throw new HarnessException("Button " + button + " not implemented");
		}

		// Default behavior, click the locator
		//

		this.zClick(locator);

		this.zWaitForBusyOverlay();

		return (page);
	}

	public void zClick(Locators field) throws HarnessException {

		String locator = field.locator;
		
		// Check if the locator is present
		if (!sIsElementPresent(locator)) {
			logger.info("zClick(" + locator + ") element is not present");
			throw new HarnessException("zClick(" + locator
					+ ") element is not present");

		}

		this.sMouseDown(locator);
		this.sMouseUp(locator);

		logger.info("zClick(" + locator + ")");
	}
	
	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean zIsActive() throws HarnessException {

		logger.info(myPageName() + " zIsActive()");

		String locator = "css=div[id='activity-stream-pane-menu-bar']";

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

	/**
	 * Left-Click on a folder in the tree
	 * 
	 * @param folder
	 * @throws HarnessException
	 */
	public void zTypeInput(Locators field, String name)
			throws HarnessException {

		logger.info(myPageName() + " Click on " + field + ")");

		if (field == null)
			throw new HarnessException("folder must not be null");
		
		String locator = field.locator;

		if (this.zWaitForElementPresent(locator, "3000")) {
			sClickAt(locator, "");			
		} else {
			throw new HarnessException(locator + " not present");
		}

		sType(locator, name);		
		
		zKeyEvent(locator, "13", "keydown");
	}
}
