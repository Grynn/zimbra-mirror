/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;


/**
 * Represents a "Create New Folder" dialog box
 * 
 * Lots of methods not yet implemented.  See https://bugzilla.zimbra.com/show_bug.cgi?id=55923
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogEditFolder extends AbsDialog {

	public static class Locators {
	

	}
	
	
	public DialogEditFolder(AbsApplication application, AbsTab tab) {
		super(application, tab);
		logger.info("new "+ DialogEditFolder.class.getCanonicalName());

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
		logger.info(myPageName() + " zIsVisible()");

		throw new HarnessException("Implement me!");
	}
	
	
	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		AbsPage page = null;
		String locator = null;
		
		if ( button == Button.B_OK ) {
			
			locator = "implement me";

		} else if ( button == Button.B_CANCEL ) {
			
			locator = "implement me";

		} else {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Default behavior, click the locator
		//
		
		// Make sure the locator was set
		if ( locator == null ) {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		this.zClick(locator);
		
		this.zWaitForBusyOverlay();

		return (page);
	}


	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		logger.info(myPageName() + " zGetDisplayedText("+ locator +")");
		
		if ( locator == null )
			throw new HarnessException("locator was null");
		
		return (this.sGetText(locator));
	}



	/**
	 * Set the new folder name
	 * @param folder
	 */
	public void zSetNewName(String folder) throws HarnessException {
		logger.info(myPageName() + " zEnterFolderName("+ folder +")");
		
		if ( folder == null ) 
			throw new HarnessException("folder must not be null");
		
		String locator = "implement me";

		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("unable to find folder name field "+ locator);
		
		// For some reason, the text doesn't get entered on the first try
		this.sFocus(locator);
		this.zClick(locator);
		zKeyboard.zTypeCharacters(folder);

		this.zWaitForBusyOverlay();
		
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
	public void zSetNewColor(FolderColor color) throws HarnessException {
		logger.info(myPageName() + " zEnterFolderColor("+ color +")");
		
		if ( color == null ) 
			throw new HarnessException("folder must not be null");
		
		if ( color == FolderColor.MoreColors )
			throw new HarnessException("'more colors' - implement me!");
		
		throw new HarnessException("implement me!");
		
	}


	


}
