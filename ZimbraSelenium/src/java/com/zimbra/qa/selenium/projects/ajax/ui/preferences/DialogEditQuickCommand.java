/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.preferences;

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
public class DialogEditQuickCommand extends AbsDialog {

	public static class Locators {

		public static final String MainDivID = "DWT301";
		
	}
	

	// It is difficult to determine if the first criteria is already
	// filled out.  If not, then user needs to click "+" to add a
	// new one.
	//
	// Use this boolean to keep track.
	protected boolean IsFirstCriteria = true;
	protected boolean isFirstAction = true;
	
	
	public DialogEditQuickCommand(AbsApplication application, AbsTab tab) {
		super(application, tab);
	}
	
	
	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		String locator = null;
		AbsPage page = null;
		
		if ( button == Button.B_OK ) {
			
			locator = "css=div[id='"+ Locators.MainDivID +"'] div[id$='_buttons'] td[id^='OK_'] td[id$='_title']";

		} else if ( button == Button.B_CANCEL ) {
				
			locator = "css=div[id='"+ Locators.MainDivID +"'] div[id$='_buttons'] td[id^='Cancel_'] td[id$='_title']";

		} else {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Default behavior, click the locator
		//
		
		// Make sure the locator was set
		if ( locator == null ) {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		zClick(locator);
		zWaitForBusyOverlay();
		
		if ( page != null ) {
			page.zWaitForActive();
		}
		
		return (page);
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

		String locator = "css=div[id='"+ Locators.MainDivID +"']";

		boolean present = this.sIsElementPresent(locator);
		if ( !present ) {
			logger.info("Locator was not present: " + locator);
			return (false);
		}
		
		boolean visible = this.zIsVisiblePerPosition(locator, 0, 0);
		if ( !visible ) {
			logger.info("Locator was not visible: " + locator);
			return (false);
		}

		return (true);
		
	}

	
	public void zSetQuickCommandName(String name) throws HarnessException {
		logger.info(myPageName() + " zSetQuickCommandName("+ name +")");
		String locator = "css=div[id='"+ Locators.MainDivID +"'] div[id$='_content'] input[id$='_name']";
		this.sType(locator, name);
		this.zWaitForBusyOverlay();
	}

	public void zSetQuickCommandDescription(String description) throws HarnessException {
		logger.info(myPageName() + " zSetQuickCommandName("+ description +")");
		String locator = "css=div[id='"+ Locators.MainDivID +"'] div[id$='_content'] input[id$='_description']";
		this.sType(locator, description);
		this.zWaitForBusyOverlay();
	}


}
