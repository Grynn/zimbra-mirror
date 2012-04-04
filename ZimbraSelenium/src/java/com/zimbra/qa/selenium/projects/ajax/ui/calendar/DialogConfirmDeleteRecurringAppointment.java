package com.zimbra.qa.selenium.projects.ajax.ui.calendar;
/**
 * 
 */


import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

/**
 * Represents a "Delete Recurring Item(s)" dialog box,
 * for an appointment without attendees.
 * 
 * Two new options
 * - Delete This Instance
 * - Delete The Series
 * 
 * No new buttons on this dialog, just OK and Cancel
 * <p>
 */
public class DialogConfirmDeleteRecurringAppointment extends DialogWarning {

	// The ID for the main Dialog DIV
	public static final String LocatorDivID = "CAL_ITEM_TYPE_DIALOG";

	public static class Locators {

		public static final String DeleteThisInstanceRadioButton = "css=td input[id*='_defaultRadio']";
		public static final String DeleteTheSeriesRadioButton = "css=td input[id$='_openSeries']";

	}

	public DialogConfirmDeleteRecurringAppointment(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(LocatorDivID), application, page);

		logger.info("new " + DialogConfirmDeleteRecurringAppointment.class.getCanonicalName());
	}



	public AbsPage zCheckRadioButton(Button button) throws HarnessException {

		if (button == null)
			throw new HarnessException("Radio button cannot be null!");

		logger.info(myPageName() + " zCheckRadioButton(" + button + ")");

		tracer.trace("Check the radio " + button + " button");

		// Default behavior variables
		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)

		if (button == Button.B_DELETE_THIS_INSTANCE) {

			locator = Locators.DeleteThisInstanceRadioButton;
			page = null;

		} else if (button == Button.B_DELETE_THE_SERIES) {

			locator = Locators.DeleteTheSeriesRadioButton;
			page = null;

		} else {
			throw new HarnessException("no logic defined for radio button " + button);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for radio button " + button);
		}

		// Default behavior, process the locator by clicking on it
		sClick(locator);

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		// If page was specified, make sure it is active
		if (page != null) {

			// This function (default) throws an exception if never active
			page.zWaitForActive();

		}

		return (page);
	}
	
	public AbsPage zClickButton(Button button) throws HarnessException {
		AbsPage page = null;
		
		// Default behavior is ok for the actions
		page = super.zClickButton(button);
		
		// After clicking OK, another dialog will happen
		if ( button == Button.B_OK ) {
			
			page = new DialogConfirmDeleteSeries(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if ( page.zIsActive() ) {
				return (page);
			}

		}
		
		if ( page != null ) {
			page.zWaitForActive();
		}
		
		return (page);
	}

}

