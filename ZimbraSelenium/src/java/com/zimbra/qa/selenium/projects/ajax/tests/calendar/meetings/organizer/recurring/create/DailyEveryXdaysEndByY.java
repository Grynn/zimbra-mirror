/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
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
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.recurring.create;

import java.util.Calendar;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;

public class DailyEveryXdaysEndByY extends CalendarWorkWeekTest {

	public DailyEveryXdaysEndByY() {
		logger.info("New "+ DailyEveryXdaysEndByY.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Test(description = "Create daily recurring invite with attendee and location with every day & end by particular date",
			groups = { "smoke" })
			
	public void DailyEveryXdaysEndByY_01() throws HarnessException {
		
		// Create appointment data
		AppointmentItem appt = new AppointmentItem();
		String tz = ZTimeZone.TimeZoneEST.getID();
		String apptSubject, apptAttendee, apptContent, apptLocation;
		ZimbraResource location = new ZimbraResource(ZimbraResource.Type.LOCATION);
		
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee = ZimbraAccount.AccountA().EmailAddress;
		apptContent = ZimbraSeleniumProperties.getUniqueString();
		apptLocation = location.EmailAddress;
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		appt.setSubject(apptSubject);
		appt.setAttendees(apptAttendee);
		appt.setStartTime(startUTC);
		appt.setEndTime(endUTC);
		appt.setLocation(apptLocation);
		appt.setContent(apptContent);
	
		// Compose appointment and send it to invitee
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		apptForm.zRepeat(Button.O_EVERY_DAY_MENU, Button.B_EVERY_X_DAYS_RADIO_BUTTON, "1", Button.B_END_BY_DATE_RADIO_BUTTON, "01/01/2020");
		ZAssert.assertEquals(app.zPageCalendar.zGetRecurringLink(), "Every day End by Jan 1, 2020 Effective " + startUTC.toTimeZone(tz).toMMM_dC_yyyy(), "Recurring link: Verify the appointment data");
				
		apptForm.zSubmit();
		SleepUtil.sleepLong(); //SOAP gives wrong response
		
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>"+ apptSubject +"</query>"
			+	"</SearchRequest>");
			
		String attendeeInvId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
		app.zGetActiveAccount().soapSend("<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ attendeeInvId +"'/>");
		
		String ruleFrequency = app.zGetActiveAccount().soapSelectValue("//mail:appt//mail:rule", "freq");
		String freqUntil = app.zGetActiveAccount().soapSelectValue("//mail:appt//mail:until", "d");
		String interval = app.zGetActiveAccount().soapSelectValue("//mail:appt//mail:interval", "ival");
		
		// Verify appointment exists on server meeting with correct recurring details
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")", appt.getStartTime().addDays(-7), appt.getEndTime().addDays(7));
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
		ZAssert.assertEquals(actual.getAttendees(), apptAttendee, "Attendees: Verify the appointment data");
		ZAssert.assertEquals(actual.getLocation(), apptLocation, "Location: Verify the appointment data");
		ZAssert.assertEquals(ruleFrequency, "DAI", "Repeat frequency: Verify the appointment data");
		ZAssert.assertEquals(freqUntil, "20200102T075959Z", "Recurrence until: Verify the appointment data");
		ZAssert.assertEquals(interval, "1", "Repeat interval: Verify the appointment data");
		ZAssert.assertEquals(actual.getContent(), appt.getContent(), "Content: Verify the appointment data");
		
		// Verify location free/busy status shows as ptst=AC
		String locationStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptLocation +"']", "ptst");
		ZAssert.assertEquals(locationStatus, "AC", "Verify that the location status shows as 'ACCEPTED'");
		
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>"+ apptSubject +"</query>"
			+	"</SearchRequest>");
	
		attendeeInvId = ZimbraAccount.AccountA().soapSelectValue("//mail:appt", "invId");
		ZimbraAccount.AccountA().soapSend("<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ attendeeInvId +"'/>");
		
		ruleFrequency = app.zGetActiveAccount().soapSelectValue("//mail:appt//mail:rule", "freq");
		freqUntil = app.zGetActiveAccount().soapSelectValue("//mail:appt//mail:until", "d");
		interval = app.zGetActiveAccount().soapSelectValue("//mail:appt//mail:interval", "ival");

		// Verify the attendee receives the meeting with correct recurring details
		AppointmentItem received = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ appt.getSubject() +")", appt.getStartTime().addDays(-7), appt.getEndTime().addDays(7));
		ZAssert.assertNotNull(received, "Verify the new appointment is created");
		ZAssert.assertEquals(received.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
		ZAssert.assertEquals(received.getAttendees(), apptAttendee, "Attendees: Verify the appointment data");
		ZAssert.assertEquals(received.getLocation(), apptLocation, "Location: Verify the appointment data");
		ZAssert.assertEquals(ruleFrequency, "DAI", "Repeat frequency: Verify the appointment data");
		ZAssert.assertEquals(freqUntil, "20200102T075959Z", "Recurrence until: Verify the appointment data");
		ZAssert.assertEquals(interval, "1", "Repeat interval: Verify the appointment data");
		ZAssert.assertEquals(received.getContent(), appt.getContent(), "Content: Verify the appointment data");

		// Verify the attendee receives the invitation
		MailItem invite = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ appt.getSubject() +")");
		ZAssert.assertNotNull(invite, "Verify the invite is received");
		ZAssert.assertEquals(invite.dSubject, appt.getSubject(), "Subject: Verify the appointment data");
		
		// UI verification
		ZAssert.assertEquals(app.zPageCalendar.zIsAppointmentExists(apptSubject), true, "Verify meeting invite is present in current calendar view");
		
		// Go to next/previous week and verify correct number of recurring instances
		app.zPageCalendar.zToolbarPressButton(Button.B_NEXT_PAGE);
		SleepUtil.sleepMedium(); //Let UI draw first and important for calendar testcases reliability
		ZAssert.assertEquals(app.zPageCalendar.zGetApptCountWorkWeekView(), 10, "Verify correct no. of recurring instances are present in calendar view");

		app.zPageCalendar.zToolbarPressButton(Button.B_NEXT_PAGE);
		SleepUtil.sleepMedium(); //Let UI draw first and important for calendar testcases reliability
		ZAssert.assertEquals(app.zPageCalendar.zGetApptCountWorkWeekView(), 10, "Verify correct no. of recurring instances are present in calendar view");
		
	}

}
