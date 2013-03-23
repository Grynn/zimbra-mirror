/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.quickadd;

import java.util.HashMap;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.QuickAddAppointment;

public class DayView extends CalendarWorkWeekTest {

	public DayView() {
		logger.info("New "+ DayView.class.getCanonicalName());

		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with day view
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -2913827779459595178L;
		{
		    put("zimbraPrefCalendarInitialView", "day");
		}};
	}
	
	@Test( description = "Verify quick add dialog opens after hitting new appointment in day view",
			groups = { "functional" } )
	
	public void DayView_01() throws HarnessException {
			
		// Verify quick add dialog opened
		QuickAddAppointment quickAddAppt = new QuickAddAppointment(app) ;
		quickAddAppt.zNewAppointment();
		quickAddAppt.zVerifyQuickAddDialog(true);
		
		/* Meeting invite full verification is already covered by meetings.organizer.singleday.minicalendar testcases so
		 not validating over here and avoiding duplication */
		
		/* If we find out something else in the future then will add verification accordingly */
	}

}
