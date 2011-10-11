package com.zimbra.qa.selenium.projects.ajax.ui.tasks;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;


public class DialogCreateTaskFolder  extends AbsDialog{
	public static class Locators {

		public static final String zDialogId = "css=div[class*='DwtDialog WindowOuterContainer']";
		// Textfields
		public static final String zNameField = "css=input[id$='_name'][class*='Field']";
		// Buttons
		public static final String zOkButton = "css=div[id='CreateNewFolderDialog'] td[id^='OK_DWT']> div[id^='CreateNewFolderDialog_button']";
		public static final String zCancelButton = "css=div[id='CreateNewFolderDialog'] td[id^='Cancel_DWT']> div[id^='CreateNewFolderDialog_button']";
	}

	public DialogCreateTaskFolder(AbsApplication application, AbsTab page) {
		super(application, page);
		logger.info("new "+ DialogCreateFolder.class.getCanonicalName());
	}

	@Override
	public String myPageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		logger.info(myPageName() + " zGetDisplayedText("+ locator +")");
		
		if ( locator == null )
			throw new HarnessException("locator was null");
		
		return (this.sGetText(locator));
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		String locator = Locators.zNameField;
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false); // Not even present
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);	// Not visible per position
		}
	
		// Yes, visible
		logger.info(myPageName() + " zIsActive() = true");
		return (true);
		
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		tracer.trace("Click dialog button "+ button);

		AbsPage page = null;
		String locator = null;
		if ( button == Button.B_OK ) {

			locator = Locators.zOkButton;

			this.zClickAt(locator,"0,0");

			this.zWaitForBusyOverlay();

			// Wait for the spinner image ONLY for desktop
			((AppAjaxClient)MyApplication).zPageMail.zWaitForDesktopLoadingSpinner(5000);

			// Check the message queue
			Stafpostqueue sp = new Stafpostqueue();
			sp.waitForPostqueue();

			return (page);

		} else if ( button == Button.B_CANCEL ) {

			locator = Locators.zCancelButton;

		} else {
			throw new HarnessException("Button "+ button +" not implemented");
		}

		// Default behavior, click the locator
		//

		// Make sure the locator was set
		if ( locator == null ) {
			throw new HarnessException("Button "+ button +" not implemented");
		}

		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Button "+ button +" locator "+ locator +" not present!");
		}

		this.zClickAt(locator,"0,0");

		this.zWaitForBusyOverlay();

		return (page);
	}
	/**
	 * Enter text into the move message dialog folder name field
	 * @param folder
	 */
	public void zEnterFolderName(String folder) throws HarnessException {
		logger.info(myPageName() + " zEnterFolderName("+ folder +")");

		tracer.trace("Enter folder name in text box "+ folder);

		if ( folder == null ) 
			throw new HarnessException("folder must not be null");

		String locator = Locators.zNameField;

		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("unable to find folder name field "+ locator);
		sClickAt(locator, "0,0");
		sType(locator, folder);      
	}

	public enum FolderColor {
		None,
		Blue,
		Cyan,
		Green,
		Purple,
		Red,
		Yellow,
		Pink,
		Grey,
		Orange,
		MoreColors
	}
	
	/**
	 * Set the color pulldown
	 * @param folder
	 */
	public void zEnterFolderColor(FolderColor color) throws HarnessException {
		logger.info(myPageName() + " zEnterFolderColor("+ color +")");
		
		tracer.trace("Enter color "+ color);

		if ( color == null ) 
			throw new HarnessException("folder must not be null");
		
		if ( color == FolderColor.MoreColors )
			throw new HarnessException("'more colors' - implement me!");
		
		throw new HarnessException("implement me!");
		
	}

}
