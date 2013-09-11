/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.touch.ui.calendar;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

public class DialogConfirmationDeleteAppointment extends DialogWarning {

	public static class Locators {

		public static final String LocatorDivID = "CONFIRM_DELETE_APPT_DIALOG";
		public static final String LocatorDivCSS = "css=div#CONFIRM_DELETE_APPT_DIALOG";

		public static final String DeleteAllOccurrencesRadioButton = "css=div[id='CONFIRM_DELETE_APPT_DIALOG_content'] td label:contains('Delete all occurrences')";
		public static final String DeleteThisAndFutureOccurrencesRadioButton = "css=div[id='CONFIRM_DELETE_APPT_DIALOG_content'] td label:contains('Delete this instance and all future occurrences.')";

	}

	public DialogConfirmationDeleteAppointment(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(Locators.LocatorDivID), application, page);

		logger.info("new " + DialogConfirmationDeleteAppointment.class.getCanonicalName());
	}

	
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");
		
		
		AbsPage page = null;
		String locator = null;
		
		if (button == Button.B_DELETE_ALL_OCCURRENCES) {

			locator = Locators.DeleteAllOccurrencesRadioButton;

			sClick(locator);
			this.zWaitForBusyOverlay();
			
			return (page);

		} else if (button == Button.B_DELETE_THIS_AND_FUTURE_OCCURRENCES) {

			locator = Locators.DeleteThisAndFutureOccurrencesRadioButton;

			sClick(locator);
			this.zWaitForBusyOverlay();
			
			return (page);
		
		}
		
		return ( super.zClickButton(button) );
		
	}

}

