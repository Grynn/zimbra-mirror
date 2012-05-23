package com.zimbra.qa.selenium.projects.ajax.ui.calendar;
/**
 * 
 */


import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;

/**
 * http://bugzilla.zimbra.com/show_bug.cgi?id=72550
 * 
 * <p>
 */
public class DialogConfirmDeleteAppointments extends DialogWarning {

	// The ID for the main Dialog DIV
	public static final String LocatorDivID = "CONFIRM_DIALOG";

	
	
	public DialogConfirmDeleteAppointments(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(LocatorDivID), application, page);
				
		logger.info("new " + DialogConfirmDeleteAppointments.class.getCanonicalName());
	}


}

