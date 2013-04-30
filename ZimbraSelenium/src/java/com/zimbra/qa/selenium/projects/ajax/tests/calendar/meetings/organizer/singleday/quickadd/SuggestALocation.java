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

import java.util.Calendar;
import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.QuickAddAppointment;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Field;

public class SuggestALocation extends CalendarWorkWeekTest {	
	
	public SuggestALocation() {
		logger.info("New "+ SuggestALocation.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Bugs(ids = "81945")
	@Test(description = "Suggest a free location while creating appointment from quick add dialog",
			groups = { "functional" })
	public void SuggestALocation_01() throws HarnessException {
		
		AppointmentItem appt = new AppointmentItem();
		Calendar now = this.calendarWeekDayUTC;
		ZimbraResource location = new ZimbraResource(ZimbraResource.Type.LOCATION);
		
		String apptSubject, apptAttendee, apptLocation, apptContent;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee = ZimbraAccount.AccountA().EmailAddress;
		apptLocation = location.EmailAddress;
		apptContent = ZimbraSeleniumProperties.getUniqueString();
		
		appt.setSubject(apptSubject);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 8, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 10, 0, 0));
	
		// Quick add appointment dialog
		QuickAddAppointment quickAddAppt = new QuickAddAppointment(app) ;
		quickAddAppt.zNewAppointmentUsingMiniCal();
		quickAddAppt.zFill(appt);
		quickAddAppt.zClick(Button.B_SUGGESTALOCATION);
		quickAddAppt.zClick(Button.B_SUGGESTEDLOCATION, apptLocation);
		quickAddAppt.zMoreDetails();
		
		// Add attendees and body from main form
		FormApptNew apptForm = new FormApptNew(app);
        apptForm.zFillField(Field.Attendees, apptAttendee);
        apptForm.zFillField(Field.Body, apptContent);
		apptForm.zSubmit();
		SleepUtil.sleepVeryLong(); // test fails while checking free/busy status, waitForPostqueue is not sufficient here
        // Tried sleepLong() as well but although fails so using sleepVeryLong()
		
		// Verify appointment exists on the server
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")", appt.getStartTime().addDays(-7), appt.getEndTime().addDays(7));
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringContains(actual.getAttendees(), apptAttendee, "Attendees: Verify the appointment data");
		ZAssert.assertStringContains(actual.getLocation(), apptLocation, "Loction: Verify the appointment data");
		ZAssert.assertEquals(actual.getContent(), apptContent, "Content: Verify the appointment data");

		// Verify the attendee receives the meeting
		AppointmentItem received = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")", appt.getStartTime().addDays(-7), appt.getEndTime().addDays(7));
		ZAssert.assertEquals(received.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringContains(received.getAttendees(), apptAttendee, "Attendees: Verify the appointment data");
		ZAssert.assertStringContains(actual.getLocation(), apptLocation, "Loction: Verify the appointment data");
		ZAssert.assertEquals(received.getContent(), apptContent, "Content: Verify the appointment data");

		// Verify the attendee receives the invitation
		MailItem invite = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")");
		ZAssert.assertNotNull(invite, "Verify the invite is received");
		ZAssert.assertEquals(invite.dSubject, apptSubject, "Subject: Verify the appointment data");
		
		// Verify location free/busy status shows as psts=AC	
		String locationStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptLocation +"']", "ptst");
		ZAssert.assertEquals(locationStatus, "AC", "Verify that the location status shows as 'ACCEPTED'");
		
	}
	
}
