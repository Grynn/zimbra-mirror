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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.attendee.singleday.actions.message;

import java.util.*;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;

@SuppressWarnings("unused")
public class AcceptProposeNewTime extends CalendarWorkWeekTest {

	public AcceptProposeNewTime() {
		logger.info("New "+ AcceptProposeNewTime.class.getCanonicalName());
		super.startingPage =  app.zPageCalendar;
		super.startingAccountPreferences = null;
	}
	
	@Test(description = "Rt-click to appointment -> Propose New Time and accept the new time using message view", 
			groups = { "functional" })
	public void AcceptProposeNewTime_01() throws HarnessException {

		// ------------------------ Test data ------------------------------------

		String organizerEmailAddress, apptAttendee1EmailAddress, apptAttendee2EmailAddress;
		ZimbraAccount organizer, apptAttendee1, apptAttendee2; 
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptBody = ZimbraSeleniumProperties.getUniqueString();
		String modifiedSubject = ZimbraSeleniumProperties.getUniqueString();
		String modifiedBody = ZimbraSeleniumProperties.getUniqueString();
		
		apptAttendee1 = app.zGetActiveAccount();
		apptAttendee1EmailAddress = app.zGetActiveAccount().EmailAddress;
		organizer = ZimbraAccount.AccountA();
		organizerEmailAddress = ZimbraAccount.AccountA().EmailAddress;
		apptAttendee2 = ZimbraAccount.AccountB();
		apptAttendee2EmailAddress = ZimbraAccount.AccountB().EmailAddress;

		Calendar now = this.calendarWeekDayUTC;
		AppointmentItem appt = new AppointmentItem();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0);
		ZDate modifiedStartUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0);
		ZDate modifiedEndUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 16, 0, 0);
		
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
		FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_PROPOSE_NEW_TIME_MENU, apptSubject);
		appt.setStartTime(modifiedStartUTC);
		appt.setEndTime(modifiedEndUTC);
		appt.setContent(modifiedBody);
		apptForm.zFill(appt);
		apptForm.zSubmit();
		
		// ------ Organizer ------
		
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
		
		String  apptAttendee1InvId= apptAttendee1.soapSelectValue("//mail:appt", "invId");
		ZAssert.assertNotNull(apptAttendee1InvId, "Original invite body shouldn't be changed for attendee");
		
		// Logout to attendee and login as organizer to accept the proposed new time
		app.zPageMain.zLogout();
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<ModifyAccountRequest xmlns='urn:zimbraAdmin'>"
			+		"<id>"+ organizer.ZimbraId +"</id>"
			+		"<a n='zimbraPrefGroupMailBy'>message</a>"
			+	"</ModifyAccountRequest>");
		app.zPageLogin.zLogin(organizer);
		
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, apptSubject);
		display.zPressButton(Button.B_ACCEPT_PROPOSE_NEW_TIME);
		SleepUtil.sleepMedium();
		
		appt.setSubject(modifiedSubject);
		appt.setContent(modifiedBody);
		apptForm.zFill(appt);
		apptForm.zSubmit();
		
		// Logout to organizer and login as attendee to accept new time
		app.zPageMain.zLogout();
		app.zPageLogin.zLogin(apptAttendee1);
		display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, modifiedSubject);
		display.zPressButton(Button.B_ACCEPT);
		SleepUtil.sleepLong();
		
		// ------ Attendee1 ------
		
		// Verify that the attendee1 status showing as 'ACCEPTED' for attendee
		apptAttendee1.soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
				+		"<query>" + "subject:(" + modifiedSubject + ")" + " " + "content:(" + modifiedBody +")" + "</query>"
				+	"</SearchRequest>");
		String attendeeInvId = apptAttendee1.soapSelectValue("//mail:appt", "invId");
		apptAttendee1.soapSend(
					"<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ attendeeInvId +"'/>");
		String myStatus = apptAttendee1.soapSelectValue("//mail:at[@a='"+ apptAttendee1EmailAddress +"']", "ptst");
		ZAssert.assertEquals(apptAttendee1.soapSelectValue("//mail:s", "d"), modifiedStartUTC.toyyyyMMddTHHmmss(), "Verify modified start time of the appointment");
		ZAssert.assertEquals(apptAttendee1.soapSelectValue("//mail:e", "d"), modifiedEndUTC.toyyyyMMddTHHmmss(), "Verify modified end time of the appointment");
		ZAssert.assertEquals(myStatus, "AC", "Verify that the attendee1 status showing as 'ACCEPTED' for attendee");

	}
	
}
