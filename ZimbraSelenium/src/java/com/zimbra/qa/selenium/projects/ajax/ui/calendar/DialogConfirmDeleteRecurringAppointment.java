package com.zimbra.qa.selenium.projects.ajax.ui.calendar;
/**
 * 
 */


import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
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

	public static class Locators {

		// The ID for the main Dialog DIV
		public static final String LocatorDivID = "CAL_ITEM_TYPE_DIALOG";
		public static final String LocatorDivCSS = "css=div#CAL_ITEM_TYPE_DIALOG";

		public static final String DeleteThisInstanceRadioButton = LocatorDivCSS +" input[id*='_defaultRadio']";
		public static final String DeleteTheSeriesRadioButton = LocatorDivCSS +" input[id$='_openSeries']";

	}

	public DialogConfirmDeleteRecurringAppointment(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(Locators.LocatorDivID), application, page);

		logger.info("new " + DialogConfirmDeleteRecurringAppointment.class.getCanonicalName());
	}



	
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");
		
		
		AbsPage page = null;
		String locator = null;
		
		// Default behavior is ok for the actions
		if (button == Button.B_DELETE_THIS_INSTANCE) {

			locator = Locators.DeleteThisInstanceRadioButton;
			page = null;

			sClick(locator);
			this.zWaitForBusyOverlay();
			
			return (page);

		} else if (button == Button.B_DELETE_THE_SERIES) {

			locator = Locators.DeleteTheSeriesRadioButton;
			page = null;

			sClick(locator);
			this.zWaitForBusyOverlay();
			
			return (page);

		} else if ( button == Button.B_OK ) {
			
			// Execute the super steps
			super.zClickButton(button);
			
			// Since we won't use zWaitForActive(), pause
			// for a while to make sure the dialog pops up.
			SleepUtil.sleepMedium();
			
			page = new DialogConfirmDeleteSeries(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if (page.zIsActive()) {
				return (page);
			}
			
			page = new DialogConfirmDeleteAppointment(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if (page.zIsActive()) {
				return (page);
			}

			page = new DialogConfirmDeleteRecurringAppointment(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if (page.zIsActive()) {
				return (page);
			}

			return (null);

		}

		return ( super.zClickButton(button) );

		
	}

}

