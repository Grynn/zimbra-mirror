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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.attendee.singleday.viewappt;

import java.util.*;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;

public class ProposeNewTime extends CalendarWorkWeekTest {

	public ProposeNewTime() {
		logger.info("New "+ ProposeNewTime.class.getCanonicalName());
		super.startingPage =  app.zPageCalendar;
		super.startingAccountPreferences = null;
	}
	
	@Test(description = "View meeting invite by opening it and propose new time to organizer", 
			groups = { "functional" })
			
	public void MeetingProposeNewTime_01() throws HarnessException {

		// ------------------------ Test data ------------------------------------

		String organizerEmailAddress, apptAttendee1EmailAddress, apptAttendee2EmailAddress;
		ZimbraAccount organizer, apptAttendee1, apptAttendee2; 
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptBody = ZimbraSeleniumProperties.getUniqueString();
		String modifiedBody = ZimbraSeleniumProperties.getUniqueString();
		
		apptAttendee1 = app.zGetActiveAccount();
		apptAttendee1EmailAddress = app.zGetActiveAccount().EmailAddress;
		organizer = ZimbraAccount.AccountA();
		organizerEmailAddress = ZimbraAccount.AccountA().EmailAddress;
		apptAttendee2 = ZimbraAccount.AccountB();
		apptAttendee2EmailAddress = ZimbraAccount.AccountB().EmailAddress;

		Calendar now = this.calendarWeekDayUTC;
		AppointmentItem appt = new AppointmentItem();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 11, 0, 0);
		ZDate modifiedStartUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 11, 0, 0);
		ZDate modifiedEndUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		
		// --------------- Creating invitation (apptAttendee1) ----------------------------
		organizer.soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
				+				"<s d='"+ startUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<e d='"+ endUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<or a='" + organizerEmailAddress + "'/>"
				+				"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1EmailAddress + "'/>"
				+				"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee2EmailAddress + "'/>"
				+			"</inv>"
				+			"<e a='"+ apptAttendee1EmailAddress +"' t='t'/>"
				+			"<e a='"+ apptAttendee2EmailAddress +"' t='t'/>"
				+			"<su>"+ apptSubject +"</su>"
				+			"<mp content-type='text/plain'>"
				+				"<content>" + apptBody + "</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");        
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);
		
		// --------------- Login to attendee & propose new time ----------------------------------------------------

		// Modify body content and propose new time
		FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, Button.O_PROPOSE_NEW_TIME_MENU, apptSubject);;
		
		apptForm.zVerifyDisabledControlInProposeNewTimeUI();
		appt.setStartTime(modifiedStartUTC);
		appt.setEndTime(modifiedEndUTC);
		appt.setContent(modifiedBody);
		apptForm.zFill(appt);
		apptForm.zSubmit();
		app.zPageCalendar.zToolbarPressButton(Button.B_CLOSE);
		
		// ------ Organizer ------
		
		// Original invite body shouldn't be changed for organizer
		organizer.soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>" + "subject:(" + apptSubject + ")" + " " + "content:(" + apptBody +")" + "</query>"
			+	"</SearchRequest>");
	
		String organizerInvId = organizer.soapSelectValue("//mail:appt", "invId");
		ZAssert.assertNotNull(organizerInvId, "Original invite body shouldn't be changed for apptAttendee1");
		
		// Verify organizer gets email notification using modified date & content
		String inboxId = FolderItem.importFromSOAP(organizer, FolderItem.SystemFolder.Inbox).getId();
		organizer.soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+       "<query>inid:"+ inboxId +" subject:(" + apptSubject + ")" + " " + "content:(" + modifiedBody +")" + "</query>"
				+	"</SearchRequest>");
		String messageId = organizer.soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(messageId, "Verify organizer gets email notification using modified date & content");
		
		// ------ Attendee1 ------
		
		// Verify that the attendee status still shows as 'NEEDS ACTION'
		apptAttendee1.soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
				+		"<query>" + "subject:(" + apptSubject + ")" + " " + "content:(" + apptBody +")" + "</query>"
				+	"</SearchRequest>");
		
		String apptAttendee1InvId= apptAttendee1.soapSelectValue("//mail:appt", "invId");
		ZAssert.assertNotNull(apptAttendee1InvId, "Original invite body shouldn't be changed for attendee");
		
		apptAttendee1.soapSend(
				"<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ apptAttendee1InvId +"'/>");
		String attendee1Status = apptAttendee1.soapSelectValue("//mail:at[@a='"+ app.zGetActiveAccount().EmailAddress +"']", "ptst");
		ZAssert.assertEquals(attendee1Status, "NE", "Verify that the attendee status still shows as 'NEEDS ACTION'");
	
		
		// ------ Attendee2 ------
		
		// Attendee2 shouldn't receive any mail if Attendee1 proposes new time to Organizer
		apptAttendee2.soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>" + "subject:(" + apptSubject + ")" + "</query>"
			+	"</SearchRequest>");
	
		String proposeNewTimeMsg = apptAttendee2.soapSelectValue("//mail:appt", "invId");
		ZAssert.assertNull(proposeNewTimeMsg, "Attendee2 shouldn't receive any mail if Attendee1 proposes new time to Organizer");
		
		// Logout to attendee and login as organizer to accept proposed new time
		
		// Logout to organizer and login as attendee to accept new time

		// All these verification are already covered in :
		// com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.attendee.singleday.actions.conversation
		// com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.attendee.singleday.actions.message
		// com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.attendee.singleday.invitations.conversation
		// com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.attendee.singleday.invitations.message

	}
	
}
