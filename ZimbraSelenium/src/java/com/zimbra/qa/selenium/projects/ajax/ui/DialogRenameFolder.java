/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;



/**
 * Represents a "Rename Folder" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogRenameFolder extends AbsDialog {

	public static class Locators {

		public static final String zDialogRenameId = "RenameFolderDialog";
		public static final String zNewFolderNameFieldId = "RenameFolderDialog_name";
		public static final String zButtonsId = "RenameFolderDialog_buttons";
	}
	
	
	public DialogRenameFolder(AbsApplication application, AbsTab tab) {
		super(application, tab);
	}
	
	public void zSetNewName(String name) throws HarnessException {
		logger.info(myPageName() + " zSetNewName("+ name +")");

		String locator = "css=input[id='"+ Locators.zNewFolderNameFieldId +"']";
		
		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Rename locator "+ locator +" is not present");
		}
		
		this.sType(locator, name);
		
	}
	
	
	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		String locator = null;
		
		if ( button == Button.B_OK ) {
			
			locator = "css=div[id='RenameFolderDialog_buttons'] td[id^='OK_'] td[id$='_title']";
			
		} else if ( button == Button.B_CANCEL ) {
			
			locator = "css=div[id='RenameFolderDialog_buttons'] td[id^='Cancel_'] td[id$='_title']";

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
		
		this.zClick(locator);
		zWaitForBusyOverlay();
		
		return (null);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		
		// Need to implement for:
		
		// "Rename Folder: foldername"
		// "New name:"
		// OK
		// Cancel
		
		throw new HarnessException("implement me");
	}


	/* (non-Javadoc)
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		String locator = "css=div[id='"+ Locators.zDialogRenameId +"']";
		
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



}
