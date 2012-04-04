package com.zimbra.qa.selenium.projects.ajax.ui.calendar;
/**
 * 
 */


import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;

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
public class DialogConfirmDeleteSeries extends DialogWarning {

	// The ID for the main Dialog DIV
	public static final String LocatorDivID = "CONFIRM_DELETE_APPT_DIALOG";

	public static class Locators {

	}

	public DialogConfirmDeleteSeries(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(LocatorDivID), application, page);

		logger.info("new " + DialogConfirmDeleteSeries.class.getCanonicalName());
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

			locator = "implement me!";
			page = null;

		} else if (button == Button.B_DELETE_THE_SERIES) {

			locator = "implement me!";
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

}

