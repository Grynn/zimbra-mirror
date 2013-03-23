/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
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

