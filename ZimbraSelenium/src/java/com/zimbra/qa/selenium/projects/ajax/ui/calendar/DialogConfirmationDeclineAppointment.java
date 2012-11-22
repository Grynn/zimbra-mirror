package com.zimbra.qa.selenium.projects.ajax.ui.calendar;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

public class DialogConfirmationDeclineAppointment extends DialogWarning {

	public static class Locators {

		public static final String LocatorDivID = "CONFIRM_DELETE_APPT_DIALOG";
		public static final String LocatorDivCSS = "css=div#CONFIRM_DELETE_APPT_DIALOG";

		public static final String DontNotifyOrganizerRadioButton = "css=div[id='CONFIRM_DELETE_APPT_DIALOG_content'] td label:contains('Don't notify organizer')";
		public static final String NotifyOrganizerRadioButton = "css=div[id='CONFIRM_DELETE_APPT_DIALOG_content'] td label:contains('Notify organizer')";

	}

	public DialogConfirmationDeclineAppointment(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(Locators.LocatorDivID), application, page);

		logger.info("new " + DialogConfirmationDeclineAppointment.class.getCanonicalName());
	}

	
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");
		
		
		AbsPage page = null;
		String locator = null;
		
		if (button == Button.B_DONT_NOTIFY_ORGANIZER) {

			locator = Locators.DontNotifyOrganizerRadioButton;

			sClick(locator);
			this.zWaitForBusyOverlay();
			
			return (page);

		} else if (button == Button.B_NOTIFY_ORGANIZER) {

			locator = Locators.NotifyOrganizerRadioButton;

			sClick(locator);
			this.zWaitForBusyOverlay();
			
			return (page);
		
		}
		
		return ( super.zClickButton(button) );
		
	}

}

