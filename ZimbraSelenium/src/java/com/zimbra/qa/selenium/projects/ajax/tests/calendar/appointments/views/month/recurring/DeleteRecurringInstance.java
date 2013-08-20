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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.month.recurring;

import java.util.Calendar;
import java.util.HashMap;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;

public class DeleteRecurringInstance extends CalendarWorkWeekTest {
	
	public DeleteRecurringInstance() {
		logger.info("New "+ DeleteRecurringInstance.class.getCanonicalName());

		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with month view
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 3028486541122343959L;

			{
				put("zimbraPrefCalendarInitialView", "month");
			}};
	}

	@Test(description = "Verify if deleting a later instance from recurring appt does not causes Wrong/first instace of Appointment being deleted in a monthly view",
			groups = { "functional" })
			public void DeleteRecurringInstance_01() throws HarnessException {

		// Appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		apptBody = "body" + ZimbraSeleniumProperties.getUniqueString();

		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startTime = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endTime   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);

		app.zGetActiveAccount().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
				"<m>"+
				"<inv method='REQUEST' type='event' fb='B' transp='O' allDay='1' name='"+ apptSubject +"'>"+
				"<s d='"+ startTime.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
				"<e d='"+ endTime.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
				"<recur>" +
				"<add>" +
				"<rule freq='WEE'>" +
				"<interval ival='1'/>" +
				"</rule>" +
				"</add>" +
				"</recur>" +
				"</inv>" +
				"<mp content-type='text/plain'>" +
				"<content>"+ apptBody +"</content>" +
				"</mp>" +
				"<su>"+ apptSubject +"</su>" +
				"</m>" +
		"</CreateAppointmentRequest>");

		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		// Get baseCount of total appts present in the current month view 
		int baseCount = app.zPageCalendar.zGetApptCountMonthView(apptSubject);

		// Move to current Month + 2 and delete an instance
		app.zPageCalendar.zToolbarPressButton(Button.B_NEXT_MONTH);
		app.zPageCalendar.zToolbarPressButton(Button.B_NEXT_MONTH);
		app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);

		DialogWarning dialogSeriesOrInstance = (DialogWarning)app.zPageCalendar.zToolbarPressButton(Button.B_DELETE);
		dialogSeriesOrInstance.zClickButton(Button.B_DELETE_THIS_INSTANCE);
		DialogWarning confirmDelete = (DialogWarning)dialogSeriesOrInstance.zClickButton(Button.B_OK);
		confirmDelete.zClickButton(Button.B_YES);

		// Verify correct no. of recurring instances are present in calendar view after deleting an instance
		ZAssert.assertGreaterThanEqualTo(app.zPageCalendar.zGetApptCountMonthView(apptSubject), 5, "Verify correct no. of recurring instances are present in calendar view after deleting an instance");

		// Go to next month and verify correct number of recurring instance
		app.zPageCalendar.zToolbarPressButton(Button.B_NEXT_PAGE);
		SleepUtil.sleepMedium(); //Let UI draw first and important for calendar testcases reliability
		ZAssert.assertGreaterThanEqualTo(app.zPageCalendar.zGetApptCountMonthView(apptSubject), 6, "Verify all 6 appts are present in other months of recurring appt in calendar view");

		// Navigate back and forth and verify correct number of recurring instance
		app.zPageCalendar.zToolbarPressButton(Button.B_NEXT_PAGE);
		app.zPageCalendar.zToolbarPressButton(Button.B_PREVIOUS_PAGE);
		app.zPageCalendar.zToolbarPressButton(Button.B_PREVIOUS_PAGE);

		// Come back the month which has an istance deleted and check if instance count is correct
		SleepUtil.sleepMedium(); //Let UI draw first and important for calendar testcases reliability
		ZAssert.assertEquals(app.zPageCalendar.zGetApptCountMonthView(apptSubject), 5, "Verify correct no. of recurring instances are present in calendar viewafter deleting an instance after navigating back to month which has deleted instance");

		// Verify correct no. of recurring instances are present in start month and no instance is deleted from that month
		app.zPageCalendar.zToolbarPressButton(Button.B_PREVIOUS_PAGE);
		app.zPageCalendar.zToolbarPressButton(Button.B_PREVIOUS_PAGE); // navigate back to current month
		ZAssert.assertEquals(app.zPageCalendar.zGetApptCountMonthView(apptSubject), baseCount, "Verify correct no. of recurring instances are present in start month and no instance is deleted");

	}

}