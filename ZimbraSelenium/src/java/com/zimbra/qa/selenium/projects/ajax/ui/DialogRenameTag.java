/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;


/**
 * Represents a "Rename Tag" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogRenameTag extends AbsDialog {

	public static class Locators {
		//see https://bugzilla.zimbra.com/show_bug.cgi?id=57458
		public static final String zRenameTagDialogId	= "RenameTagDialog";
		public static final String zNewTagNameFieldId	= "RenameTagDialog_name";
		public static final String zButtonsId 		= "RenameTagDialog_buttons";
	}
	
	
	public DialogRenameTag(AbsApplication application, AbsTab tab) {
		super(application, tab);
		logger.info("new " + DialogRenameTag.class.getCanonicalName());

	}
	
	public void zSetNewName(String name) throws HarnessException {
		logger.info(myPageName() + " zSetNewName("+ name +")");

		String locator = "css=input#"+ Locators.zNewTagNameFieldId +"";
		
		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Rename locator "+ locator +" is not present");
		}		
		if (ZimbraSeleniumProperties.isWebDriver()){		
			clearField(locator);
		}
		sType(locator, name);
	}
	
	
	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		String locator = null;
		
		if ( button == Button.B_OK ) {
			locator="css=div#RenameTagDialog_button2";
					
		} else if ( button == Button.B_CANCEL ) {
			locator="css=div#RenameTagDialog_button1";

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
		
		zClickAt(locator,"0,0");
		
		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		return (null);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		
		// Need to implement for:
		
		// "Rename Tag: tagname"
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

		String locator = "id="+ Locators.zRenameTagDialogId;
		
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
