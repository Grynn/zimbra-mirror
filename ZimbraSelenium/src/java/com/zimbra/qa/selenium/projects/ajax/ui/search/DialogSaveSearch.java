/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.search;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;

/**
 * Represents a "Move Message" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogSaveSearch extends AbsDialog {

	public static class Locators {

      public static final String zDialogLocator			= "css=div#CreateNewFolderDialog";
      public static final String zTitleId				= "css=td#CreateNewFolderDialog_title";
      public static final String zDialogInputLocator	= "css=input#CreateNewFolderDialog_name";
      public static final String zDialogButtonsId		= "CreateNewFolderDialog_buttons";


	}
	
	
	public DialogSaveSearch(AbsApplication application, AbsTab tab) {
		super(application, tab);
		
		logger.info("new " + DialogSaveSearch.class.getCanonicalName());

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

		String locator = Locators.zDialogLocator;
		
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

		String locator = null;
		
		if ( button == Button.B_OK ) {

         locator = "css=div[id='" + Locators.zDialogButtonsId + "'] div[id='CreateNewFolderDialog_button2']";

      } else if ( button == Button.B_CANCEL ) {

         locator = "css=div[id='" + Locators.zDialogButtonsId + "'] div[id='CreateNewFolderDialog_button1']";

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
		
		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		return (null);
	}


	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		logger.info(myPageName() + " zGetDisplayedText("+ locator +")");
		
		if ( locator == null )
			throw new HarnessException("locator was null");
		
		return (this.sGetText(locator));
	}


	/**
	 * Click on the folder in the dialog tree
	 * @param folder
	 * @throws HarnessException
	 */
	public void zClickTreeFolder(FolderItem folder) throws HarnessException {
		logger.info(myPageName() + " zClickTreeFolder("+ folder +")");
		
		tracer.trace("Click on tree folder with name "+ folder.getName());

		if ( folder == null ) 
			throw new HarnessException("folder must not be null");
		
		String locator = Locators.zDialogLocator + " td[id='zti__ZmChooseFolderDialog_Mail__"+ folder.getId() +"_textCell']";
		
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("unable to find folder in tree "+ locator);
		
		// For some reason, the text doesn't get entered on the first try
		this.zClick(locator);
		
		// Is this sleep necessary?
		SleepUtil.sleepSmall();
		

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
		
		String locator = Locators.zDialogInputLocator;

		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("unable to find folder name field "+ locator);
		
		// For some reason, the text doesn't get entered on the first try
		this.sFocus(locator);
		this.zClick(locator);
		this.sType(locator, folder);
		// zKeyboard.zTypeCharacters(folder);

		// Is this sleep necessary?
		SleepUtil.sleepSmall();
		
	}


	


}
