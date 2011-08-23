/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;

/**
 * Represents a "Specify Message Send Time" dialog box (Send Later)
 * 
 * See https://bugzilla.zimbra.com/show_bug.cgi?id=7524
 * See https://bugzilla.zimbra.com/show_bug.cgi?id=61935
 * 
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogSendLater extends AbsDialog {

	
	
	public static class Locators {
		
		// Main dialog locator
		// TODO: need to update this locator https://bugzilla.zimbra.com/show_bug.cgi?id=61935
		public static final String SendLaterDialogLocatorCSS	= "css=div[id^='ZmTimeDialog']";

		// Fields
		public static final String FieldDateLocator				= SendLaterDialogLocatorCSS + " input[id$='_date']";
		public static final String FieldTimeLocator				= SendLaterDialogLocatorCSS + " input[id$='_startTimeInput']";

		// Pulldowns
		public static final String PulldownTimezoneLocator		= SendLaterDialogLocatorCSS + " div[id$='_buttons'] td[id$='_dropdown'] div[class='ImgSelectPullDownArrow']";

		// Buttons
		public static final String ButtonOkButtonLocator		= SendLaterDialogLocatorCSS + " div[id$='_buttons'] td[id^='OK_'] td[id$='_title']";
		public static final String ButtonCancelButtonLocator	= SendLaterDialogLocatorCSS + " div[id$='_buttons'] td[id^='Cancel'] td[id$='_title']";
	}
	
	
	public DialogSendLater(AbsApplication application, AbsTab tab) {
		super(application, tab);
		
		logger.info("new "+ DialogSendLater.class.getCanonicalName());

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

		String locator = Locators.SendLaterDialogLocatorCSS;
		
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
		
		
		if ( button == Button.B_OK ) {

			locator = Locators.ButtonOkButtonLocator;

			this.zClick(locator);

			this.zWaitForBusyOverlay();

			// Check the message queue
			Stafpostqueue sp = new Stafpostqueue();
			sp.waitForPostqueue();

			return (page);

		} else if ( button == Button.B_CANCEL ) {

			locator = Locators.ButtonCancelButtonLocator;

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
		
		public static final Field Date = new Field("Date");
		public static final Field Time = new Field("Time");
		public static final Field Timezone = new Field("Timezone");
		
		
		private String field;
		private Field(String name) {
			field = name;
		}
		
		@Override
		public String toString() {
			return (field);
		}

	}
	

	public void zFill(Calendar calendar) throws HarnessException {
		logger.info(myPageName() + ".zFill("+ calendar +")");

		if ( calendar == null )
			throw new HarnessException("calendar cannot be null!");
		
		zFillField(Field.Date, calendar);
		zFillField(Field.Time, calendar);
		// TODO: zFillField(Field.Timezone, calendar);
		
	}


	public void zFillField(Field field, Calendar calendar) throws HarnessException {
		String value = null;
		
		if ( field == Field.Date ) {

			// TODO: need to INTL
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
			value = format.format(calendar.getTime());
			
		} else if (field == Field.Time ) {
			
			// TODO: need to INTL
			SimpleDateFormat format = new SimpleDateFormat("HH:mm a");
			value = format.format(calendar.getTime());

		} else if ( field == Field.Timezone ) {
			
			throw new HarnessException("TODO: need to handle timezone as a pulldown");

		} else {
			throw new HarnessException("Unsupported field: "+ field);
		}
		
		if ( value == null || value.trim().length() == 0 ){
			throw new HarnessException("value cannot be null or empty");
		}
	
		zFillField(field, value);
	}

	public void zFillField(Field field, String value) throws HarnessException {
		tracer.trace("Set "+ field +" to "+ value);

		String locator = null;
		
		if ( field == Field.Date ) {
			
			locator = Locators.FieldDateLocator;
			
			// FALL THROUGH
			
		} else if ( field == Field.Time ) {
			
			locator = Locators.FieldTimeLocator;
			
			// FALL THROUGH
			
		} else if ( field == Field.Timezone ) {
					
			throw new HarnessException("TODO: need to handle timezone as a pulldown");
			
		} else {
			throw new HarnessException("Unsupported field: "+ field);
		}
		
		if ( locator == null ) {
			throw new HarnessException("locator was null for field "+ field);
		}
		
		// Default behavior, enter value into locator field
		//
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Field is not present field="+ field +" locator="+ locator);
		
		// Seems that the client can't handle filling out the new mail form too quickly
		// Click in the "To" fields, etc, to make sure the client is ready
		this.sFocus(locator);
		this.zClick(locator);
		this.zWaitForBusyOverlay();

		// Enter text
		this.sType(locator, value);
		
		this.zWaitForBusyOverlay();
		
	}


}
