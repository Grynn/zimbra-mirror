package com.zimbra.qa.selenium.projects.octopus.ui;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class DialogMove extends AbsDialog {
	public static class Locators {
		public static final Locators zMoveItemPageListView = new Locators(
				"css=div[class=sc-view] div[id=move-item-page-list-view]>div[class^=sc-view sc-list-item-view]" +
				">div[class=sc-outline]>label");

		public final String locator;

		private Locators(String locator) {
			this.locator = locator;
		}
	}

	public DialogMove(AbsApplication application, AbsTab page) {
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

		if (button == Button.B_NEW) {

			locator = "css=div[id='ChooseFolderDialog_buttons'] td[id^='New_'] td[id$='_title']";

		} else if (button == Button.B_OK) {

			locator = "css=div[id='ChooseFolderDialog_buttons'] td[id^='OK_'] td[id$='_title']";

		} else if (button == Button.B_CANCEL) {

			locator = "css=div[id='ChooseFolderDialog_buttons'] td[id^='Cancel_'] td[id$='_title']";

		} else {
			throw new HarnessException("Button " + button + " not implemented");
		}

		// Default behavior, click the locator
		//

		this.zClick(locator);

		this.zWaitForBusyOverlay();

		return (page);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean zIsActive() throws HarnessException {

		logger.info(myPageName() + " zIsActive()");

		String locator = "css=div[id='" + "']";

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
	public void zDoubleClickTreeFolder(String folderName)
			throws HarnessException {

		logger.info(myPageName() + " zClickTreeFolder(" + folderName + ")");

		if (folderName == null)

			throw new HarnessException("folder must not be null");
		String locator = Locators.zMoveItemPageListView.locator +":contains(" + folderName + ")";
		
		if (this.zWaitForElementPresent(locator, "3000")) {
			sClickAt(locator,"");
			if(this.sIsElementPresent(locator))
			sClickAt(locator,"");
						
		} else {
			throw new HarnessException(locator + " not present");
		}

		this.zWaitForBusyOverlay(); // This method call seems to be missing from
		// the briefcase function

	}
}
