package com.zimbra.qa.selenium.projects.ajax.ui.calendar;
/**
 * 
 */


import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;

/**
 * Represents a "Confirmation" dialog box
 * <p>
 */
public class DialogAddExternalCalendar extends AbsDialog {

	public static class Locators {
		public static final String DialogDivID = "DWT563";
		public static final String DialogDivCss = "css=div[id='DWT563']";
		
		public static final String RadioGoogleCss = "css=input[id$='_shareRadioGoogle']";
		public static final String RadioYahooCss = "css=input[id$='_shareRadioYahoo']";
		public static final String RadioOtherCss = "css=input[id$='_shareRadioOther']";
		
	}

	public DialogAddExternalCalendar(AbsApplication application, AbsTab page) {
		super(application, page);
				
		logger.info("new " + DialogAddExternalCalendar.class.getCanonicalName());
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

		if (button == Button.B_NEXT) {
			
			locator = Locators.DialogDivCss + " div[id$='_buttons'] td[id^='Next_'] td[id$='_title']";
			page = null;
			
		} else if (button == Button.B_CANCEL) {
			
			locator = Locators.DialogDivCss + " div[id$='_buttons'] td[id^='Cancel_'] td[id$='_title']";
			page = null; // Probably need to have a FormMailNew
			
		} else if (button == Button.B_BACK) {
			
			locator = Locators.DialogDivCss + " div[id$='_buttons'] td[id^='Back_'] td[id$='_title']";
			page = null; // Probably need to have a FormMailNew
			
		} else {
			
			throw new HarnessException("no logic defined for button "+ button);

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

		// See https://bugzilla.zimbra.com/show_bug.cgi?id=66576 
		String locator = Locators.DialogDivCss;

		if (!this.sIsElementPresent(locator)) {
			return (false); // Not even present
		}

		if (!this.zIsVisiblePerPosition(locator, 0, 0)) {
			return (false); // Not even present
		}

		logger.info(myPageName() + " zIsActive() = true");
		return (true);

	}
	
	public static enum SourceType {
		Google, Yahoo, Other
	}
	
	public void zSetSourceType(SourceType type) throws HarnessException {
		logger.info(myPageName() + " zSetSourceType("+ type +")");
		
		String locator = null;
		if ( type == SourceType.Google ) {
			
			locator = Locators.RadioGoogleCss;
			
		} else if ( type == SourceType.Yahoo ) {
			
			locator = Locators.RadioYahooCss;
			
		} else if ( type == SourceType.Other ) {
			
			locator = Locators.RadioOtherCss;
			
		} else {
			throw new HarnessException("unknown calendar type: "+ type);
		}
		
		if ( !sIsElementPresent(locator) ) {
			throw new HarnessException("locator not present: "+ locator);
		}
		
		this.sClick(locator);
		this.zWaitForBusyOverlay();
		
	}
	
	public static enum CalendarType {
		CalDav, iCal, Other
	}
	
	public void zSetCalendarType(CalendarType type) throws HarnessException {
		logger.info(myPageName() + " zSetCalendarType("+ type +")");
		
		String pulldownLocator = null;
		String optionLocator = null;
		

	}
	
}

