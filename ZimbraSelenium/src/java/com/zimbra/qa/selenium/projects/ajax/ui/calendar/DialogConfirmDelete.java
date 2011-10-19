package com.zimbra.qa.selenium.projects.ajax.ui.calendar;
/**
 * 
 */


import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogConfirm;

/**
 * Represents a "Confirmation" dialog box
 * <p>
 */
public class DialogConfirmDelete extends DialogConfirm {

	public static class Locators {
		public static final String DialogDivID = "252";
		public static final String DialogDivCss = "css=div[id='252']";
	}

	public DialogConfirmDelete(AbsApplication application, AbsTab page) {
		super(DialogConfirm.Confirmation.DELETE, application, page);
				
		logger.info("new " + DialogConfirmDelete.class.getCanonicalName());
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");

		tracer.trace("Click dialog button " + button);
		if ( button == null )
			throw new HarnessException("button cannot be null");
	
		String locator = null;
		AbsPage page = null; 

		if (button == Button.B_SEND_CANCELLATION) {
			
			locator = Locators.DialogDivCss + " div[id$='_buttons'] td[id^='No_'] td[id$='_title']";
			page = null;
			
		} else if (button == Button.B_EDIT_CANCELLATION) {
			
			locator = Locators.DialogDivCss + " div[id$='_buttons'] td[id^='Yes_'] td[id$='_title']";
			page = null; // Probably need to have a FormMailNew
			
		} else {
			
			return ( super.zClickButton(button) );

		}

		// Make sure the locator was set
		if (locator == null) {
			throw new HarnessException("Button " + button + " not implemented");
		}

		// Make sure the locator exists
		if (!this.sIsElementPresent(locator)) {
			throw new HarnessException("Button " + button + " locator "
					+ locator + " not present!");
		}

		this.zClickAt(locator,"0,0");

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();
		
		
		// This dialog could send messages, so wait for the queue
		Stafpostqueue sp = new Stafpostqueue();
		sp.waitForPostqueue();

		
		// If page was specified, make sure it is active
		if ( page != null ) {
			
			// This function (default) throws an exception if never active
			page.zWaitForActive();
			
		}

		return (page);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		logger.info(myPageName() + " zGetDisplayedText(" + locator + ")");

		if (locator == null)
			throw new HarnessException("locator was null");

		return (this.sGetText(locator));
	}
	
	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		String locator = Locators.DialogDivCss;

		if (!this.sIsElementPresent(locator)) {
			return ( super.zIsActive() ); // Not even present
		}

		if (!this.zIsVisiblePerPosition(locator, 0, 0)) {
			return ( super.zIsActive() ); // Not visible per position
		}

		logger.info(myPageName() + " zIsActive() = true");
		return (true);

	}
}

