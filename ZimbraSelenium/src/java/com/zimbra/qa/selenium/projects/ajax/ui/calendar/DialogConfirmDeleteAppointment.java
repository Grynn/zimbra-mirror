package com.zimbra.qa.selenium.projects.ajax.ui.calendar;
/**
 * 
 */


import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;

/**
 * Represents a "Delete Appointment" dialog box,
 * for an appointment without attendees.
 * 
 * No new buttons on this dialog, just YES and NO
 * <p>
 */
public class DialogConfirmDeleteAppointment extends DialogWarning {

	// The ID for the main Dialog DIV
	public static final String LocatorDivID = "CNF_DEL_YESNO";

	
	
	public DialogConfirmDeleteAppointment(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(LocatorDivID), application, page);
				
		logger.info("new " + DialogConfirmDeleteAppointment.class.getCanonicalName());
	}


}

