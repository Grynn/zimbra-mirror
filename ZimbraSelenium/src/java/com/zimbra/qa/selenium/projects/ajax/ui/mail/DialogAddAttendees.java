/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;

public class DialogAddAttendees extends AbsDialog {

	public static class Locators {
		public static final String DialogDivLocatorCSS = "css=div[id='YesNoMsgDialog']";

		// Buttons
		public static final String zYesButton = "css=div[id='YesNoMsgDialog_buttons'] td[id^='Yes_'] td[id$='_title']";
		public static final String zNoButton = "css=div[id='YesNoMsgDialog_buttons'] td[id^='No_'] td[id$='_title']";
	}
	
	
	public DialogAddAttendees(AbsApplication application, AbsTab tab) {
		super(application, tab);
		logger.info("new "+ DialogAddAttendees.class.getCanonicalName());
	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		String locator = Locators.DialogDivLocatorCSS;
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false); // Not even present
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);
		}
	
		logger.info(myPageName() + " zIsActive() = true");
		return (true);
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		tracer.trace("Click dialog button "+ button);

		AbsPage page = null;
		String locator = null;
		
		if ( button == Button.B_YES ) {

			locator = Locators.zYesButton;

		} else if ( button == Button.B_NO ) {

			locator = Locators.zNoButton;

		} else {
			throw new HarnessException("Button "+ button +" not implemented");
		}

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
		SleepUtil.sleepMedium();

		return (page);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		logger.info(myPageName() + " zGetDisplayedText("+ locator +")");
		
		if ( locator == null )
			throw new HarnessException("locator was null");
		
		return (this.sGetText(locator));
	}

}
