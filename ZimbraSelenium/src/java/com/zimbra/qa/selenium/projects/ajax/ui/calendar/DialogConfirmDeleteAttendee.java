package com.zimbra.qa.selenium.projects.ajax.ui.calendar;
/**
 * 
 */


import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;

/**
 * Represents a "Delete Meeting Request" dialog box,
 * from the viewpoint of the attendee.
 * 
 * Adds two buttons for processing:
 * - Notify organizer
 * - Don't notify organizer
 * <p>
 */
public class DialogConfirmDeleteAttendee extends DialogWarning {

	// The ID for the main Dialog DIV
	public static final String LocatorDivID = "CONFIRM_DELETE_APPT_DIALOG";

	
	
	public DialogConfirmDeleteAttendee(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(LocatorDivID), application, page);
				
		logger.info("new " + DialogConfirmDeleteAttendee.class.getCanonicalName());
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");

		tracer.trace("Click dialog button " + button);
		if ( button == null )
			throw new HarnessException("button cannot be null");
	
		String locator = null;
		AbsPage page = null;
		boolean waitForPostfix = false;

		if (button == Button.B_NOTIFY_ORGANIZER) {
			
			// Weird ID string for send cancellation
			// td[id='No_DWT392'] == Send Cancellation
			//
			locator = "css=div[id='"+ this.MyDivId +"'] td[id^='TODO'] td[id$='_title']";
			page = null;
			waitForPostfix = true;
			
		} else if (button == Button.B_DONT_NOTIFY_ORGANIZER) {
			
			locator = "css=div[id='"+ this.MyDivId +"'] td[id^='TODO'] td[id$='_title']";
			page = null;
			waitForPostfix = false;

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
		
		// If page was specified, make sure it is active
		if ( page != null ) {
			
			// This function (default) throws an exception if never active
			page.zWaitForActive();
			
		}

		// This dialog could send messages, so wait for the queue
		if ( waitForPostfix ) {
			Stafpostqueue sp = new Stafpostqueue();
			sp.waitForPostqueue();
		}


		return (page);
	}

}

