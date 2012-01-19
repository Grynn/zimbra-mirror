/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;

/**
 * Represents a "Send Confirmation" dialog box 
 * 
 * Currently implemented in the app as a "Page", but 
 * a "Dialog" makes more sense for the harness.
 * 
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogSendConfirmation extends AbsDialog {

	
	
	public static class Locators {
		
		// Main dialog locator
		// TODO: need to update this locator https://bugzilla.zimbra.com/show_bug.cgi?id=61935
		public static final String DialogLocatorID				= "ztb__MAILCONFIRM";
		public static final String DialogLocatorCSS				= "css=div#" + DialogLocatorID;

		// Buttons
		public static final String ButtonCloseLocatorCSS		= "css=div[id='ztb__MAILCONFIRM'] div[id='zb__MAILCONFIRM__CLOSE'] td[id$='_title']";
	}
	
	
	public DialogSendConfirmation(AbsApplication application, AbsTab tab) {
		super(application, tab);
		
		logger.info("new "+ DialogSendConfirmation.class.getCanonicalName());

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

		String locator = Locators.DialogLocatorCSS;
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false); // Not even present
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);	// Not visible per position
		}
	
		// Yes, visible
		logger.info(myPageName() + " zIsVisible() = true");
		return (true);
		
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		tracer.trace("Click dialog button "+ button);

		AbsPage page = null;
		String locator = null;
		
		
		if ( button == Button.B_CLOSE ) {

			locator = Locators.ButtonCloseLocatorCSS;

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

	public static class Field {
		
		public static final Field To = new Field("To");		
		
		private String field;
		private Field(String name) {
			field = name;
		}
		
		@Override
		public String toString() {
			return (field);
		}

	}
	


	public void zFillField(Field field, String email) throws HarnessException {
		tracer.trace("Set "+ field +" to "+ email);
		
		throw new HarnessException("implement me");
		
	}


}
