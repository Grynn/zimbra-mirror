/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.social;


import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;



/**
 * Represents a "Rename Folder" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogSocialZimletWelcome extends AbsDialog {

	public static class Locators {
		public static final String zDialogLocatorCSS = "css=div[id='SocialZimlet_WelcomeDlg']";
	}
	
	
	public DialogSocialZimletWelcome(AbsApplication application, AbsTab tab) {
		super(application, tab);
	}
	
	
	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		String locator = null;
		
		if ( button == Button.B_OK ) {
			
			locator = Locators.zDialogLocatorCSS + " td[id^='OK_'] td[id$='_title']";
			
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
		
		zWaitForBusyOverlay();
		
		return (null);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		
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

		String locator = Locators.zDialogLocatorCSS;
		
		boolean present = this.sIsElementPresent(locator);
		if ( !present ) {
			return (false); // Not even present
		}
		
		boolean visible = this.zIsVisiblePerPosition(locator, 0, 0);
		if ( !visible ) {
			return (false);	// Not visible per position
		}
	
		// Yes, visible
		logger.info(myPageName() + " zIsVisible() = true");
		return (true);
	}



}
