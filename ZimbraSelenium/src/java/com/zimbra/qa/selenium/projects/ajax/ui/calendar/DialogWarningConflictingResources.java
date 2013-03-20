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
public class DialogWarningConflictingResources extends DialogWarning {

	public static class Locators {

		// The ID for the main Dialog DIV
		public static final String LocatorDivID = "RESC_CONFLICT_DLG";
		
	}

	public DialogWarningConflictingResources(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(Locators.LocatorDivID), application, page);

		logger.info("new " + DialogWarningConflictingResources.class.getCanonicalName());
	}

	public String zGetResourceConflictWarningDialogText() throws HarnessException {
		String text = null;	
		SleepUtil.sleepMedium();
		text = this.zGetDisplayedText("css=div[id='RESC_CONFLICT_DLG']");
		return text;
	}
}

