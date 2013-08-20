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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.create;

import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;

public class CreateMeetingWithRSVPOnOff extends CalendarWorkWeekTest {

	public CreateMeetingWithRSVPOnOff() {
		logger.info("New "+ CreateMeetingWithRSVPOnOff.class.getCanonicalName());
		super.startingPage =  app.zPageCalendar;
	}
	@Test(description = "Verify organizer does not recieve email notification when attendee responds to the meeting invite while 'Request Response' remains OFF", 
			groups = { "functional" })
	public void CreateMeetingWithRSVPOff_01() throws HarnessException {
		
		// Create appointment data 
		ZimbraAccount apptAttendee1,organizer;
		String apptAttendee1EmailAddress; 
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptContent = ZimbraSeleniumProperties.getUniqueString();
		
		organizer = app.zGetActiveAccount();
		apptAttendee1 = ZimbraAccount.AccountA();
		apptAttendee1EmailAddress = ZimbraAccount.AccountA().EmailAddress;
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		AppointmentItem appt = new AppointmentItem();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 13, 0, 0);
		
		// Create appointment data
		appt.setSubject(apptSubject);
		appt.setAttendees(apptAttendee1EmailAddress);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0));
		appt.setContent(apptContent);
	
		// Create meeting with Request Response OFF
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		apptForm.zRequestResponseOFF();
		apptForm.zSubmit();
		
		// Logout from organizer and Login as attendee
		app.zPageMain.zLogout();
		app.zPageLogin.zLogin(apptAttendee1);
		
		// Accept the invite from attendee
		SleepUtil.sleepMedium(); // let the mails sync , tried sleepSmall() but it fails
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, apptSubject);
		display.zPressButton(Button.B_ACCEPT);
		SleepUtil.sleepVeryLong(); // it passes only when I add sleepVeryLong() twice on my local setup, but currently adding only once lets see if it passes
		
		// Search for the appointment (InvId)
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
				+		"<query>"+ apptSubject +"</query>"
				+	"</SearchRequest>");
		String organizerInvId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
				
		// Get the attendee appointment details
		
		app.zGetActiveAccount().soapSend("<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ organizerInvId +"'/>");
		String attendeeStatus = apptAttendee1.soapSelectValue("//mail:at[@a='"+ apptAttendee1EmailAddress +"']", "ptst");

		// Verify attendee status shows as ACCEPTED
		ZAssert.assertEquals(attendeeStatus, "AC", "Verify that the attendee shows as 'ACCEPTED'");
		
		// Organizer: Search for the appointment response
		String inboxId = FolderItem.importFromSOAP(organizer, FolderItem.SystemFolder.Inbox).getId();
		organizer.soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+		"<query>inid:"+ inboxId +" subject:("+ apptSubject +")</query>"
				+	"</SearchRequest>");
		String messageId = organizer.soapSelectValue("//mail:m", "id");
		
		// Verify organizer does not receive email notification because request response was set OFF while creating meeting invite
		ZAssert.assertNull(messageId, "Verify organizer does not recieve email notification because request response was set OFF while creating meeting invite");
	}
	
	@Test(description = "Verify organizer receives email notification when attendee responds to the meeting invite while 'Request Response' remains ON", 
			groups = { "functional" })
	public void CreateMeetingWithRSVPOn_01() throws HarnessException {

		// Create appointment data 
		ZimbraAccount apptAttendee1,organizer;
		String apptAttendee1EmailAddress; 
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptContent = ZimbraSeleniumProperties.getUniqueString();
		
		organizer = app.zGetActiveAccount();
		apptAttendee1 = ZimbraAccount.AccountA();
		apptAttendee1EmailAddress = apptAttendee1.EmailAddress;
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		AppointmentItem appt = new AppointmentItem();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 13, 0, 0);
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		
		// Create appointment data
		appt.setSubject(apptSubject);
		appt.setAttendees(apptAttendee1EmailAddress);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0));
		appt.setContent(apptContent);
	
		// Create meeting with Request Response OFF and then ON
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		apptForm.zRequestResponseON(); // toggle request response option to enable it
		apptForm.zSubmit(); 
		
		// Logout from organizer and Login as attendee
		app.zPageMain.zLogout();
		app.zPageLogin.zLogin(apptAttendee1);
		SleepUtil.sleepLong();
		
		// Accept the invite
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, apptSubject);
		display.zPressButton(Button.B_ACCEPT);
		SleepUtil.sleepVeryLong();
		
		// Search for the appointment (InvId)
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
				+		"<query>"+ apptSubject +"</query>"
				+	"</SearchRequest>");
		
		String invId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");

		// Get the attendee appointment details
		app.zGetActiveAccount().soapSend(
					"<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ invId +"'/>");
		String attendeeStatus = apptAttendee1.soapSelectValue("//mail:at[@a='"+ apptAttendee1EmailAddress +"']", "ptst");

		// Verify attendee status is shows as ACCEPTED
		ZAssert.assertEquals(attendeeStatus, "AC", "Verify that the attendee shows as 'ACCEPTED'");
		
		// Organizer: Search for the appointment response
		String inboxId = FolderItem.importFromSOAP(organizer, FolderItem.SystemFolder.Inbox).getId();
		organizer.soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+		"<query>inid:"+ inboxId +" subject:("+ apptSubject +")</query>"
				+	"</SearchRequest>");
		String messageId = organizer.soapSelectValue("//mail:m", "id");
		
		// Verify organizer receives email notification when attendee responds to the meeting invite while 'Request Response' remains ON
		ZAssert.assertNotNull(messageId, "Verify organizer receives email notification when attendee responds to the meeting invite while 'Request Response' remains ON");
		
	}
	
}
