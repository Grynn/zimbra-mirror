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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.modify;

import java.util.Calendar;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Field;

public class ModifyBySubjectBodyAndAttendees extends CalendarWorkWeekTest {

	public ModifyBySubjectBodyAndAttendees() {
		logger.info("New "+ ModifyBySubjectBodyAndAttendees.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Bugs(ids = "69132")
	@Test(	description = "Modify meeting subject, body and attendees",
			groups = { "smoke" })
			
	public void ModifyMeetingBySubjectBodyAndAttendees_01() throws HarnessException {
		
		// Creating object for meeting data
		organizerTest = true;
		
		String tz, apptSubject, apptBody, apptAttendee1, modifiedApptSubject, modifiedApptBody, apptAttendee2;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		apptAttendee2 = ZimbraAccount.AccountB().EmailAddress;
		
		modifiedApptSubject = ZimbraSeleniumProperties.getUniqueString();
        modifiedApptBody = ZimbraSeleniumProperties.getUniqueString();
        
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     "<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     "<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "'/>" + 
                     "</inv>" +
                     "<e a='"+ apptAttendee1 +"' t='t'/>" +
                     "<mp content-type='text/plain'>" +
                     "<content>"+ apptBody +"</content>" +
                     "</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");

        
        // Open appointment and modify subject, attendee and content
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        apptForm.zFillField(Field.Subject, modifiedApptSubject);
        apptForm.zFillField(Field.Attendees, apptAttendee2);
        apptForm.zFillField(Field.Body, modifiedApptBody);
        apptForm.zToolbarPressButton(Button.B_SEND);
		SleepUtil.sleepVeryLong(); //importFromSOAP fails due to fast execution
        
        // Use GetAppointmentRequest to verify the changes are saved
        AppointmentItem modifyAppt = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ modifiedApptSubject +")");
        ZAssert.assertNotNull(modifyAppt, "Verify the modified appointment appears on the server");
        
        ZAssert.assertEquals(modifyAppt.getSubject(), modifiedApptSubject, "Subject: Verify modified appointment subject");
        ZAssert.assertStringContains(modifyAppt.getAttendees(), apptAttendee1, "Attendee1: Verify modified attendee");
        ZAssert.assertStringContains(modifyAppt.getAttendees(), apptAttendee2, "Attendee2: Verify modified attendee");
        ZAssert.assertStringContains(modifyAppt.getContent(), modifiedApptBody, "Body: Verify modified appointment body");
        
        // Verify attendee1 receives meeting invitation message
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ modifiedApptSubject +")" + " " + "content:" + modifiedApptBody + "</query>"
			+	"</SearchRequest>");
		String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify new invitation appears in the attendee1's inbox");
 
		// Verify attendee2 receives meeting invitation message
		ZimbraAccount.AccountB().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+	"<query>subject:("+ modifiedApptSubject +")" + " " + "content:" + modifiedApptBody + "</query>"
			+	"</SearchRequest>");
		id = ZimbraAccount.AccountB().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify attendee2 receives meeting invitation message");
		
		// Verify attendee2 free/busy status
		String attendee2Status = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptAttendee2 +"']", "ptst");
		ZAssert.assertEquals(attendee2Status, "NE", "Verify attendee2 free/busy status");
		
	}
}
