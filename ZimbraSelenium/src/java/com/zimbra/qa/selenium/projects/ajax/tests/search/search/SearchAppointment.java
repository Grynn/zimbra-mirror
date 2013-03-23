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
package com.zimbra.qa.selenium.projects.ajax.tests.search.search;

import java.util.Calendar;
import java.util.List;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;


public class SearchAppointment extends CalendarWorkWeekTest {

	int pollIntervalSeconds = 60;
	
	public SearchAppointment() {
		logger.info("New "+ SearchAppointment.class.getCanonicalName());
		
	}
	
	@Test(	description = "Search for an appointment by subject",
			groups = { "functional" })
	public void SearchAppointment_01() throws HarnessException {
		
		// Create a meeting
		AppointmentItem appt = new AppointmentItem();
		String subject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		AppointmentItem.createAppointmentSingleDay(
				app.zGetActiveAccount(),
				Calendar.getInstance(),
				120,
				null,
				subject,
				"content" + ZimbraSeleniumProperties.getUniqueString(),
				"location" + ZimbraSeleniumProperties.getUniqueString(),
				null);

		// Refresh the calendar
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		// Verify appointment exists on the server 
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject + ")");
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		
		// Search for the appointment
		app.zPageSearch.zAddSearchQuery("subject:("+ subject +")");
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		
		// Get all the appointment in the default calendar
		List<AppointmentItem> items = app.zPageCalendar.zListGetAppointments();
		ZAssert.assertNotNull(items, "Get the list of appointments");
		
		// Verify the appointment is in the search result on UI
		AppointmentItem found = null;
		for(AppointmentItem item : items) {
			if ( item.getSubject().contains(subject) ) {
				found = item;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the appt list exists");


	}


}
