/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.briefcase;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;

public class DialogFindShares extends AbsDialog {

	public static class Locators {

      public static final String zDialogLocator			= "div[class='ZmShareSearchDialog DwtDialog']";
      public static final String zTitleId				= "div[id$=_title]";
      public static final String zDialogButtonsId		= "div[id$=_buttons]";


	}
		
	public DialogFindShares(AbsApplication application, AbsTab tab) {
		super(application, tab);
		
		logger.info("new " + DialogFindShares.class.getCanonicalName());
	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		String locator = "css=" + Locators.zDialogLocator;
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false); 
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);	// Not visible per position
		}
	
		logger.info(myPageName() + " zIsActive() = true");
		return (true);		
	}
	
	
	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		tracer.trace("Click dialog button "+ button);

		String locator = null;
		
		if ( button == Button.B_CANCEL ) {

         locator = "css=td[class=ZWidgetTitle]:contains(Cancel)";

      } else {
         throw new HarnessException("Button "+ button +" not implemented");
      }
		
		// Default behavior, click the locator
		//
		
		// Make sure the locator was set
		
		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Button "+ button +" locator "+ locator +" not present!");
		}
		
		this.zClickAt(locator,"0,0");
		
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
		if ( folder == null ){ 
			throw new HarnessException("zClickTreeFolder(FolderItem): folder must not be null");
		}
		
		logger.info(myPageName() + " zClickTreeFolder("+ folder +")");
		
		tracer.trace("Click on tree folder with name "+ folder.getName());

		String locator = Locators.zDialogLocator + " td[id$=" + folder.getId() + "_textCell']";
		
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("unable to find folder in tree "+ locator);
		
		this.zClick(locator);
		
		SleepUtil.sleepSmall();		
	}
}
