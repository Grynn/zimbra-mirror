/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.suggestions;

import java.util.Calendar;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogSuggestionPreferences;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;

public class ModifyTimeSuggestionPreference extends CalendarWorkWeekTest {

	public ModifyTimeSuggestionPreference() {
		logger.info("New "+ ModifyTimeSuggestionPreference.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
		
	@Test(description = "Create a meeting invite by modifying time suggestion preferences",
			groups = { "functional" })
			
	public void ModifyTimeSuggestionPreference_01() throws HarnessException {
		
		// Create appointment data
		Calendar now = this.calendarWeekDayUTC;
		AppointmentItem appt = new AppointmentItem();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 6, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 8, 0, 0);
		
		String apptSubject, apptAttendee, apptContent;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee = ZimbraAccount.AccountC().EmailAddress;
		apptContent = ZimbraSeleniumProperties.getUniqueString();
		
		appt.setSubject(apptSubject);
		appt.setAttendees(apptAttendee);
		appt.setContent(apptContent);
	
		// Compose appointment and send it to invitee
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zSetTomorrowDate();
		apptForm.zFill(appt);
		
		apptForm.zToolbarPressPulldown(Button.B_OPTIONS, Button.O_SUGGESTION_PREFERENCES);
		DialogSuggestionPreferences dialogSuggestionPref = (DialogSuggestionPreferences) new DialogSuggestionPreferences(app, app.zPageCalendar);
		dialogSuggestionPref.zClickButton(Button.B_ONLY_INCLUDE_MY_WORKING_HOURS);
		dialogSuggestionPref.zClickButton(Button.B_ONLY_INCLUDE_OTHER_ATTENDEES_WORKING_HOURS);
		dialogSuggestionPref.zClickButton(Button.B_OK);
		
		apptForm.zToolbarPressButton(Button.B_SUGGESTATIME);		
		apptForm
				.zVerifySpecificTimeExists("12:00 AM,12:30 AM,1:00 AM,1:30 AM,2:00 AM,2:30 AM,3:00 AM,"
						+ "3:30 AM,4:00 AM,4:30 AM,5:00 AM,5:30 AM,6:00 AM,6:30 AM,7:00 AM,7:30 AM," 
						+ "8:00 AM,8:30 AM,9:00 AM,9:30 AM,10:00 AM,10:30 AM,11:00 AM,11:30 AM,12:00 PM,"
						+ "12:30 PM,1:00 PM,1:30 PM,2:00 PM,2:30 PM,3:00 PM,3:30 PM,4:00 PM,4:30 PM,5:00 PM,"
						+ "5:30 PM,6:00 PM,6:30 PM,7:00 PM,7:30 PM,8:00 PM,8:30 PM,9:00 PM,9:30 PM,10:00 PM,"
						+ "10:30 PM,11:00 PM,11:30 PM");
        apptForm.zToolbarPressButton(Button.B_10AM);
		apptForm.zSubmit();
		
		// Verify appointment exists on the server
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")");
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
		ZAssert.assertEquals(actual.getAttendees(), apptAttendee, "Attendees: Verify the appointment data");
		ZAssert.assertEquals(actual.getContent(), appt.getContent(), "Content: Verify the appointment data");

		// Verify start and end time
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(1000).toMillis() +"'>"
				+		"<query>"+ apptSubject +"</query>"
				+	"</SearchRequest>");
		
		String apptId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "id");
		
		app.zGetActiveAccount().soapSend("<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ apptId +"'/>");
		String startDateTime = app.zGetActiveAccount().soapSelectValue("//mail:s", "d");
		String endDateTime = app.zGetActiveAccount().soapSelectValue("//mail:e", "d");
		
		ZAssert.assertEquals(startDateTime, apptForm.zGetTomorrowDate() + "T100000", "Verify start date/time after picking up time from suggest pane'");
		ZAssert.assertEquals(endDateTime, apptForm.zGetTomorrowDate() + "T110000", "Verify end date/time after picking up time from suggest pane'");
		
		// Verify the attendee receives the meeting
		AppointmentItem received = AppointmentItem.importFromSOAP(ZimbraAccount.AccountC(), "subject:("+ appt.getSubject() +")");
		ZAssert.assertNotNull(received, "Verify the new appointment is created");
		ZAssert.assertEquals(received.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
		ZAssert.assertEquals(received.getAttendees(), apptAttendee, "Attendees: Verify the appointment data");
		ZAssert.assertEquals(received.getContent(), appt.getContent(), "Content: Verify the appointment data");

		// Verify the attendee receives the invitation
		MailItem invite = MailItem.importFromSOAP(ZimbraAccount.AccountC	(), "subject:("+ appt.getSubject() +")");
		ZAssert.assertNotNull(invite, "Verify the invite is received");
		ZAssert.assertEquals(invite.dSubject, appt.getSubject(), "Subject: Verify the appointment data");
		
	}
	
}
