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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.attendee.recurring.series;

import java.util.Calendar;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogInformational;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Field;

public class CreateACopy extends CalendarWorkWeekTest {
	
	public CreateACopy() {
		logger.info("New "+ CreateACopy.class.getCanonicalName());
		super.startingPage =  app.zPageCalendar;
		super.startingAccountPreferences = null;
	
	}
	
	@Test(description = "Create a copy of meeting invite (Series)",
			groups = { "functional" })
			
	public void CopyMeeting_01() throws HarnessException {

		// Create a meeting
		String organizer;
		organizerTest = false;
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		String newSubject = ZimbraSeleniumProperties.getUniqueString();
		String newContent = ZimbraSeleniumProperties.getUniqueString();
		String attendee2 = ZimbraAccount.AccountB().EmailAddress;
		
		// Creating a meeting
		Calendar now = this.calendarWeekDayUTC;
		String tz = ZTimeZone.TimeZoneEST.getID();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 4, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 6, 0, 0);
		
		ZimbraAccount.AccountA().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
					"<m>"+
						"<inv method='REQUEST' type='event' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
							"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
							"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
							"<or a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>" +
							"<at role='REQ' ptst='NE' rsvp='1' a='" + app.zGetActiveAccount().EmailAddress + "'/>" +
							"<recur>" +
								"<add>" +
									"<rule freq='DAI'>" +
										"<interval ival='1'/>" +
									"</rule>" +
								"</add>" +
							"</recur>" +
						"</inv>" +
						"<e a='"+ app.zGetActiveAccount().EmailAddress +"' t='t'/>" +
						"<mp content-type='text/plain'>" +
							"<content>"+ apptBody +"</content>" +
						"</mp>" +
						"<su>"+ apptSubject +"</su>" +
					"</m>" +
				"</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // Copy appointment
        DialogInformational dialog = (DialogInformational)app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_SERIES_MENU, Button.O_CREATE_A_COPY_MENU, apptSubject);;
		dialog.zClickButton(Button.B_OK);
		
        FormApptNew form = new FormApptNew(app);
        form.zFillField(Field.Subject, newSubject);
        form.zFillField(Field.Body, newContent);
        form.zFillField(Field.Attendees, attendee2);
        form.zSubmit();
		
		// Verify the new invitation appears in the attendee1's inbox
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>" + "subject:(" + newSubject + ")" + " " + "content:(" + newContent +")" + "</query>"
			+	"</SearchRequest>");
		String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify the new invitation appears in the attendee's inbox");
		
		// Verify organizer for the copied appointment
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ newSubject +")" + " " + "content:(" + newContent +")</query>"
			+	"</SearchRequest>");
		id = ZimbraAccount.AccountA().soapSelectValue("//mail:appt", "invId");
		organizer = ZimbraAccount.AccountA().soapSelectValue("//mail:appt/mail:or", "a");
		ZAssert.assertEquals(organizer, app.zGetActiveAccount().EmailAddress, "Verify organizer for the copied appointment");
		
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>"+ newSubject +"</query>"
			+	"</SearchRequest>");
	
		String attendeeInvId = ZimbraAccount.AccountA().soapSelectValue("//mail:appt", "invId");
		ZimbraAccount.AccountA().soapSend("<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ attendeeInvId +"'/>");
		
		String ruleFrequency = ZimbraAccount.AccountA().soapSelectValue("//mail:appt//mail:rule", "freq");
		String interval = ZimbraAccount.AccountA().soapSelectValue("//mail:appt//mail:interval", "ival");
		ZAssert.assertEquals(ruleFrequency, "DAI", "Repeat frequency: Verify the appointment data");
		ZAssert.assertEquals(interval, "1", "Repeat interval: Verify the appointment data");

		// Verify the new invitation appears in the attendee2's inbox
		ZimbraAccount.AccountB().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>" + "subject:(" + newSubject + ")" + " " + "content:(" + newContent +")" + "</query>"
			+	"</SearchRequest>");
		id = ZimbraAccount.AccountB().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify the new invitation appears in the attendee's inbox");
		
		// Verify organizer for the copied appointment
		ZimbraAccount.AccountB().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ newSubject +")" + " " + "content:(" + newContent +")</query>"
			+	"</SearchRequest>");
		id = ZimbraAccount.AccountB().soapSelectValue("//mail:appt", "invId");
		organizer = ZimbraAccount.AccountB().soapSelectValue("//mail:appt/mail:or", "a");
		ZAssert.assertEquals(organizer, app.zGetActiveAccount().EmailAddress, "Verify organizer for the copied appointment");

		ZimbraAccount.AccountB().soapSend("<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ id +"'/>");
		ruleFrequency = ZimbraAccount.AccountB().soapSelectValue("//mail:appt//mail:rule", "freq");
		interval = ZimbraAccount.AccountB().soapSelectValue("//mail:appt//mail:interval", "ival");
		ZAssert.assertEquals(ruleFrequency, "DAI", "Repeat frequency: Verify the appointment data");
		ZAssert.assertEquals(interval, "1", "Repeat interval: Verify the appointment data");
		
	}

}
