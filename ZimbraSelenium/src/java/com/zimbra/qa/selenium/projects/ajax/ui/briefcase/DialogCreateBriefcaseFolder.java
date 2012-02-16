/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.briefcase;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;

/**
 * Represents a "Create New Briefcase Folder" dialog box
 * 
 */
public class DialogCreateBriefcaseFolder extends AbsDialog {

	public static class Locators {

		public static final String zDialogId = "ChooseFolderDialog";

		public static final String zTitleId = "ChooseFolderDialog_title";

		public static final String zDialogContentId = "ChooseFolderDialog_content";

		public static final String zTitle = "css=td[id^=CreateNewFolderDialog__]:contains(Create New Briefcase Folder)";
		
		// Textfields
		public static final String zNameField = "css=div[id^=CreateNewFolderDialog]:contains(Create New Briefcase Folder) td>input.Field";
		// Buttons
		public static final String zDialogButtonsId = "ChooseFolderDialog_buttons";
		public static final String zOkButton = "css=td[id^=OK] td[id^=CreateNewFolderDialog]:contains(OK)";									
		public static final String zCancelButton = "css=td[id^=Cancel] td[id^=CreateNewFolderDialog]:contains(Cancel)";

	}

	public DialogCreateBriefcaseFolder(AbsApplication application, AbsTab tab) {
		super(application, tab);

		logger.info("new "
				+ DialogCreateBriefcaseFolder.class.getCanonicalName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		String locator = Locators.zNameField;
	
		if (!this.zWaitForElementPresent(locator,"3000")) {
			throw new HarnessException("locator not even present");			
		}

		if (!this.zIsVisiblePerPosition(locator, 0, 0)) {
			return (false); // Not visible per position
		}

		// Yes, visible
		logger.info(myPageName() + " zIsActive() = true");
		return (true);

	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");

		tracer.trace("Click dialog button " + button);

		AbsPage page = null;
		String locator = null;

		if (button == Button.B_OK) {

			locator = Locators.zOkButton;

		} else if (button == Button.B_CANCEL) {

			locator = Locators.zCancelButton;

		} else {
			throw new HarnessException("Button " + button + " not implemented");
		}

		// Default behavior, click the locator
		//

		// Make sure the locator exists
		if (!this.sIsElementPresent(locator)) {
			throw new HarnessException("Button " + button + " locator "
					+ locator + " not present!");
		}

		this.zClickAt(locator, "0,0");
		
		this.zWaitForBusyOverlay();

		//Check the message queue
		//Stafpostqueue sp = new Stafpostqueue();
		//sp.waitForPostqueue();
		
		return (page);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		logger.info(myPageName() + " zGetDisplayedText(" + locator + ")");

		if (locator == null)
			throw new HarnessException("locator was null");

		return (this.sGetText(locator));
	}

	/**
	 * Click on the folder in the dialog tree
	 * 
	 * @param folder
	 * @throws HarnessException
	 */
	public void zClickTreeFolder(FolderItem folder) throws HarnessException {
		if (folder == null){
			throw new HarnessException("zClickTreeFolder(FolderItem): folder must not be null");
		}
		
		logger.info(myPageName() + " zClickTreeFolder(" + folder + ")");

		tracer.trace("Click on tree briefcase with name " + folder.getName());

		String locator = "css=div[id='" + Locators.zDialogId
				+ "'] td[id='zti__ZmChooseFolderDialog_Briefcase__" + folder.getId()
				+ "_textCell']";

		if (!this.sIsElementPresent(locator))
			throw new HarnessException("unable to find folder in tree "
					+ locator);

		this.zClickAt(locator, "0,0");

		this.zWaitForBusyOverlay();

	}

	/**
	 * Enter text into the move message dialog folder name field
	 * 
	 * @param folder
	 */
	public void zEnterFolderName(String folder) throws HarnessException {
		logger.info(myPageName() + " zEnterFolderName(" + folder + ")");

		tracer.trace("Enter briefcase name in text box " + folder);

		if (folder == null)
			throw new HarnessException("folder must not be null");

		String locator = Locators.zNameField;
		
		if (!this.sIsElementPresent(locator))
			throw new HarnessException("unable to find folder name field "
					+ locator);

		// For some reason, the text doesn't get entered on the first try
		this.sFocus(locator);
		this.zClickAt(locator, "0,0");
		this.sType(locator, folder);

		this.zWaitForBusyOverlay();
	}

	public enum FolderColor {
		None, Blue, Cyan, Green, Purple, Red, Yellow, Pink, Grey, Orange, MoreColors
	}

	/**
	 * Set the color pulldown
	 * 
	 * @param folder
	 */
	public void zEnterFolderColor(FolderColor color) throws HarnessException {
		logger.info(myPageName() + " zEnterFolderColor(" + color + ")");

		tracer.trace("Enter color " + color);

		if (color == null)
			throw new HarnessException("folder must not be null");

		if (color == FolderColor.MoreColors)
			throw new HarnessException("'more colors' - implement me!");

		throw new HarnessException("implement me!");

	}
}
