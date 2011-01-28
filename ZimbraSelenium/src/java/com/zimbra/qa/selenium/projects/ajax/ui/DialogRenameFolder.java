/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;


/**
 * Represents a "Rename Folder" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogRenameFolder extends AbsDialog {

	public static class Locators {
	

	}
	
	
	public DialogRenameFolder(AbsApplication application) {
		super(application);
	}
	
	public void zSetNewName(String name) throws HarnessException {
		logger.info(myPageName() + " zSetNewName("+ name +")");

		String locator = "implement me";
		
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
			
			locator =  "implement me";
			
		} else if ( button == Button.B_CANCEL ) {
			
			locator =  "implement me";

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
		String locator = "implement me";
		return ( this.sIsElementPresent(locator) );
	}



}
