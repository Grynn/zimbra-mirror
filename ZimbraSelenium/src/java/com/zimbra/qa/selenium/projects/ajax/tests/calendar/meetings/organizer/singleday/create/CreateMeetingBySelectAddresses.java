/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
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

import java.util.Calendar;
import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogFindAttendees;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;;

public class CreateMeetingBySelectAddresses extends CalendarWorkWeekTest {	
	
	public CreateMeetingBySelectAddresses() {
		logger.info("New "+ CreateMeetingBySelectAddresses.class.getCanonicalName());
		super.startingPage =  app.zPageCalendar;
	}
	
	@Test(description = "Compose appointment by selecting attendees using 'Select Addresses' dialog and send the appointment",
			groups = { "functional" })
	public void CreateMeetingBySelectAttendees_01() throws HarnessException {
		
		// Create appointment data
		AppointmentItem appt = new AppointmentItem();
		
		String apptSubject, apptAttendee1, apptContent;
		Calendar now = this.calendarWeekDayUTC;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		
		apptContent = ZimbraSeleniumProperties.getUniqueString();
		appt.setSubject(apptSubject);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0));
		appt.setContent(apptContent);
	
		// Compose appointment and select attendees using "Select Addresses" dialog
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt); 
        apptForm.zToolbarPressButton(Button.B_TO);
        DialogFindAttendees dialogFindAttendees = (DialogFindAttendees) new DialogFindAttendees(app, app.zPageCalendar);
   
        // Search any attendee and select it
        AppointmentItem apptSearchForm = new AppointmentItem();
        apptSearchForm.setAttendeeName(apptAttendee1);
        dialogFindAttendees.zFill(apptSearchForm);
        dialogFindAttendees.zClickButton(Button.B_SEARCH);
        dialogFindAttendees.zClickButton(Button.B_SELECT_FIRST_CONTACT);
        dialogFindAttendees.zClickButton(Button.B_CHOOSE_CONTACT_FROM_PICKER);
        dialogFindAttendees.zClickButton(Button.B_OK);
        
        // Send the  appt
        apptForm.zToolbarPressButton(Button.B_SEND);
		apptForm.zSubmit();

        // Verify attendee1 receives meeting invitation message
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +")</query>"
			+	"</SearchRequest>");
		String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify new invitation appears in the attendee1's inbox");
		
		// Verify attendee1 present in the appointment
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringContains(actual.getAttendees(), apptAttendee1, "Attendees: Verify the appointment data");
		
		// Verify appointment is present in attendee1's calendar
		AppointmentItem addeddAttendee = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")");
		ZAssert.assertNotNull(addeddAttendee, "Verify meeting invite is present in attendee1's calendar");
	
	}
}
