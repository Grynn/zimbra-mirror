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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.attendee.singleday.invitations.conversation;

import java.util.*;

import org.testng.annotations.Test;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;

public class AcceptMeetingUsingDifferentCalendarFolder extends CalendarWorkWeekTest {

	public AcceptMeetingUsingDifferentCalendarFolder() {
		logger.info("New "+ AcceptMeetingUsingDifferentCalendarFolder.class.getCanonicalName());
		super.startingPage = app.zPageMail;

	}

	/**
	 * ZimbraAccount.AccountA() sends a two-hour appointment to app.zGetActiveAccount()
	 * with subject and start time
	 * @param subject
	 * @param start
	 * @throws HarnessException 
	 */
	private void SendCreateAppointmentRequest(String subject, ZDate start) throws HarnessException {
				
		ZimbraAccount.AccountA().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ subject +"'>"
				+				"<s d='"+ start.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<e d='"+ start.addHours(2).toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<or a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>"
				+				"<at role='REQ' ptst='NE' rsvp='1' a='" + app.zGetActiveAccount().EmailAddress + "'/>"
				+			"</inv>"
				+			"<e a='"+ app.zGetActiveAccount().EmailAddress +"' t='t'/>"
				+			"<su>"+ subject +"</su>"
				+			"<mp content-type='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");        

	}
	
	@Bugs(ids = "69132")
	@Test(
			description = "Accept a meeting using Accept button from invitation message", 
			groups = { "functional" })
	public void AcceptMeeting_01() throws HarnessException {

		// ------------------------ Test data ------------------------------------

		String apptSubject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);


		// --------------- Creating invitation (organizer) ----------------------------

		ZimbraAccount.AccountA().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
				+				"<s d='"+ startUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<e d='"+ endUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<or a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>"
				+				"<at role='REQ' ptst='NE' rsvp='1' a='" + app.zGetActiveAccount().EmailAddress + "'/>"
				+			"</inv>"
				+			"<e a='"+ app.zGetActiveAccount().EmailAddress +"' t='t'/>"
				+			"<su>"+ apptSubject +"</su>"
				+			"<mp content-type='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");

		// ---------------- Create new calendar folder ---------------------
		
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		String folderName = ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + folderName + "' l='" + root.getId() + "' view='appointment'/>"
				+	"</CreateFolderRequest>");
		FolderItem folderSOAP = FolderItem.importFromSOAP(app.zGetActiveAccount(), folderName);
		
		SleepUtil.sleepMedium(); //created folder not visible after hitting Refresh, lets see after wait..

		
		// --------------- Login to attendee & accept invitation ----------------------------------------------------

		// Refresh the view
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Select the invitation
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, apptSubject);
		
		// Select different calendar folder and click to Accept button
		display.zPressButtonPulldown(Button.B_CALENDAR, folderName);
		display.zPressButton(Button.B_ACCEPT);


		// ---------------- Verification at organizer & invitee side both -------------------------------------       

		// --- Check that the organizer shows the attendee as "ACCEPT" ---

		// Organizer: Search for the appointment (InvId)
		ZimbraAccount.AccountA().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
				+		"<query>"+ apptSubject +"</query>"
				+	"</SearchRequest>");
		
		String organizerInvId = ZimbraAccount.AccountA().soapSelectValue("//mail:appt", "invId");

		// Get the appointment details
		ZimbraAccount.AccountA().soapSend(
					"<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ organizerInvId +"'/>");
		
		String attendeeStatus = ZimbraAccount.AccountA().soapSelectValue("//mail:at[@a='"+ app.zGetActiveAccount().EmailAddress +"']", "ptst");

		// Verify attendee status shows as ptst=AC
		ZAssert.assertEquals(attendeeStatus, "AC", "Verify that the attendee shows as 'ACCEPTED'");


		// --- Check that the attendee showing status as "ACCEPT" ---

		// Attendee: Search for the appointment (InvId)
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
				+		"<query>"+ apptSubject +"</query>"
				+	"</SearchRequest>");
		
		String attendeeInvId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");

		// Get the appointment details
		app.zGetActiveAccount().soapSend(
					"<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ attendeeInvId +"'/>");
		
		String myStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ app.zGetActiveAccount().EmailAddress +"']", "ptst");

		// Verify attendee status shows as ptst=AC
		ZAssert.assertEquals(myStatus, "AC", "Verify that the attendee shows as 'ACCEPTED'");
		
		// Verify accepted appointment present in new calendar folder
		AppointmentItem acceptedAppointment = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(acceptedAppointment.getFolder(), folderSOAP.getId(), "Verify accepted appointment present in new calendar folder");

	}

	@Bugs(ids = "69132")
	@Test(
			description = "Accept meeting - Verify organizer gets notification message", 
			groups = { "functional" })
	public void AcceptMeeting_02() throws HarnessException {

		// ------------------------ Test data ------------------------------------

		String apptSubject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);

		// ---------------- Create new calendar folder ---------------------
		
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		String folderName = ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + folderName + "' l='" + root.getId() + "' view='appointment'/>"
				+	"</CreateFolderRequest>");
		FolderItem folderSOAP = FolderItem.importFromSOAP(app.zGetActiveAccount(), folderName);
		
		SleepUtil.sleepMedium(); //created folder not visible after hitting Refresh, lets see after wait..
		
		// --------------- Creating invitation (organizer) ----------------------------
		
		this.SendCreateAppointmentRequest(apptSubject, startUTC);

		
		// --------------- Login to attendee & accept invitation ----------------------------------------------------

		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Select the invitation
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, apptSubject);
		
		// Select different calendar folder and click to Accept button
		display.zPressButtonPulldown(Button.B_CALENDAR, folderName);
		display.zPressButton(Button.B_ACCEPT);


		// ---------------- Verification at organizer & invitee side both -------------------------------------       


		// --- Check that the organizer shows the attendee as "ACCEPT" ---

		// Organizer: Search for the appointment response
		String inboxId = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), FolderItem.SystemFolder.Inbox).getId();
		
		ZimbraAccount.AccountA().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+		"<query>inid:"+ inboxId +" subject:("+ apptSubject +")</query>"
				+	"</SearchRequest>");
		
		String messageId = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");

		// Get the appointment details
		ZimbraAccount.AccountA().soapSend(
					"<GetMsgRequest  xmlns='urn:zimbraMail'>"
				+		"<m id='"+ messageId +"'/>"
				+	"</GetMsgRequest>");
		
		// Verify accepted appointment present in new calendar folder
		AppointmentItem acceptedAppointment = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(acceptedAppointment.getFolder(), folderSOAP.getId(), "Verify accepted appointment present in new calendar folder");

	}

	@Bugs(ids = "69132")
	@Test(
			description = "Accept meeting using 'Accept -> Notify Organizer'", 
			groups = { "functional" })
	public void AcceptMeeting_03() throws HarnessException {

		// ------------------------ Test data ------------------------------------

		String apptSubject = "appointment" + ZimbraSeleniumProperties.getUniqueString();

		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);


		// --------------- Creating invitation (organizer) ----------------------------

		ZimbraAccount.AccountA().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
				+				"<s d='"+ startUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<e d='"+ endUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<or a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>"
				+				"<at role='REQ' ptst='NE' rsvp='1' a='" + app.zGetActiveAccount().EmailAddress + "'/>"
				+			"</inv>"
				+			"<e a='"+ app.zGetActiveAccount().EmailAddress +"' t='t'/>"
				+			"<su>"+ apptSubject +"</su>"
				+			"<mp content-type='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");        

		// ---------------- Create new calendar folder ---------------------
		
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		String folderName = ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + folderName + "' l='" + root.getId() + "' view='appointment'/>"
				+	"</CreateFolderRequest>");
		FolderItem folderSOAP = FolderItem.importFromSOAP(app.zGetActiveAccount(), folderName);
		
		SleepUtil.sleepMedium(); //created folder not visible after hitting Refresh, lets see after wait..

		// --------------- Login to attendee & accept invitation ----------------------------------------------------

		// Refresh the view
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Select the invitation
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, apptSubject);

		// Select different calendar folder and click to Accept > Notify Organizer
		display.zPressButtonPulldown(Button.B_CALENDAR, folderName);
		display.zPressButtonPulldown(Button.B_ACCEPT, Button.O_ACCEPT_NOTIFY_ORGANIZER);

		// ---------------- Verification at organizer & invitee side both -------------------------------------       


		// --- Check that the organizer shows the attendee as "ACCEPT" ---

		// Organizer: Search for the appointment (InvId)
		ZimbraAccount.AccountA().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
				+		"<query>"+ apptSubject +"</query>"
				+	"</SearchRequest>");
		
		String organizerInvId = ZimbraAccount.AccountA().soapSelectValue("//mail:appt", "invId");

		// Get the appointment details
		ZimbraAccount.AccountA().soapSend(
					"<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ organizerInvId +"'/>");
		
		String attendeeStatus = ZimbraAccount.AccountA().soapSelectValue("//mail:at[@a='"+ app.zGetActiveAccount().EmailAddress +"']", "ptst");

		// Verify attendee status shows as ptst=AC
		ZAssert.assertEquals(attendeeStatus, "AC", "Verify that the attendee shows as 'ACCEPTED'");


		// --- Check that the attendee showing status as "ACCEPT" ---

		// Attendee: Search for the appointment (InvId)
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
				+		"<query>"+ apptSubject +"</query>"
				+	"</SearchRequest>");
		
		String attendeeInvId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");

		// Get the appointment details
		app.zGetActiveAccount().soapSend(
					"<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ attendeeInvId +"'/>");
		
		String myStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ app.zGetActiveAccount().EmailAddress +"']", "ptst");

		// Verify attendee status shows as ptst=AC
		ZAssert.assertEquals(myStatus, "AC", "Verify that the attendee shows as 'ACCEPTED'");

		// Organizer: Search for the appointment response
		String inboxId = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), FolderItem.SystemFolder.Inbox).getId();
		
		ZimbraAccount.AccountA().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+		"<query>inid:"+ inboxId +" subject:("+ apptSubject +")</query>"
				+	"</SearchRequest>");
		
		String messageId = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");

		// Get the appointment details
		ZimbraAccount.AccountA().soapSend(
					"<GetMsgRequest  xmlns='urn:zimbraMail'>"
				+		"<m id='"+ messageId +"'/>"
				+	"</GetMsgRequest>");
		
		// Verify accepted appointment present in new calendar folder
		AppointmentItem acceptedAppointment = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(acceptedAppointment.getFolder(), folderSOAP.getId(), "Verify accepted appointment present in new calendar folder");
		
	}
	
	@Bugs(ids = "69132")
	@Test(
			description = "Accept meeting using 'Accept -> Edit Reply' and verify modified content", 
			groups = { "functional" })
	public void AcceptMeeting_04() throws HarnessException {

		// ------------------------ Test data ------------------------------------

		String apptSubject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		String modifiedBody = "modified" + ZimbraSeleniumProperties.getUniqueString();

		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);


		// --------------- Creating invitation (organizer) ----------------------------

		ZimbraAccount.AccountA().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
				+				"<s d='"+ startUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<e d='"+ endUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<or a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>"
				+				"<at role='REQ' ptst='NE' rsvp='1' a='" + app.zGetActiveAccount().EmailAddress + "'/>"
				+			"</inv>"
				+			"<e a='"+ app.zGetActiveAccount().EmailAddress +"' t='t'/>"
				+			"<su>"+ apptSubject +"</su>"
				+			"<mp content-type='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");        

		// ---------------- Create new calendar folder ---------------------
		
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		String folderName = ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + folderName + "' l='" + root.getId() + "' view='appointment'/>"
				+	"</CreateFolderRequest>");
		FolderItem folderSOAP = FolderItem.importFromSOAP(app.zGetActiveAccount(), folderName);
		
		SleepUtil.sleepMedium(); //created folder not visible after hitting Refresh, lets see after wait..

		// --------------- Login to attendee & accept invitation ----------------------------------------------------

		// Refresh the view
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);
		
		// Select the invitation
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, apptSubject);

		// Select different calendar folder and click to Accept -> Edit Reply, which will open a new reply compose
		display.zPressButtonPulldown(Button.B_CALENDAR, folderName);
        FormMailNew editReply = (FormMailNew)display.zPressButtonPulldown(Button.B_ACCEPT, Button.O_ACCEPT_EDIT_REPLY);
        editReply.zFillField(Field.Body, modifiedBody);
        editReply.zSubmit();
		
		// ---------------- Verification at organizer & invitee side both -------------------------------------       


		// --- Check that the organizer shows the attendee as "ACCEPT" ---

		// Organizer: Search for the appointment (InvId)
		ZimbraAccount.AccountA().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
				+		"<query>"+ apptSubject +"</query>"
				+	"</SearchRequest>");
		
		String organizerInvId = ZimbraAccount.AccountA().soapSelectValue("//mail:appt", "invId");

		// Get the appointment details
		ZimbraAccount.AccountA().soapSend(
					"<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ organizerInvId +"'/>");
		
		String attendeeStatus = ZimbraAccount.AccountA().soapSelectValue("//mail:at[@a='"+ app.zGetActiveAccount().EmailAddress +"']", "ptst");

		// Verify attendee status shows as ptst=AC
		ZAssert.assertEquals(attendeeStatus, "AC", "Verify that the attendee shows as 'ACCEPTED'");


		// --- Check that the attendee showing status as "ACCEPT" ---

		// Attendee: Search for the appointment (InvId)
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
				+		"<query>"+ apptSubject +"</query>"
				+	"</SearchRequest>");
		
		String attendeeInvId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");

		// Get the appointment details
		app.zGetActiveAccount().soapSend(
					"<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ attendeeInvId +"'/>");
		
		String myStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ app.zGetActiveAccount().EmailAddress +"']", "ptst");

		// Verify attendee status shows as ptst=AC
		ZAssert.assertEquals(myStatus, "AC", "Verify that the attendee shows as 'ACCEPTED'");

		// Organizer: Search for the appointment response
		String inboxId = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), FolderItem.SystemFolder.Inbox).getId();
		
		ZimbraAccount.AccountA().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+		"<query>inid:"+ inboxId +" subject:("+ apptSubject +")</query>"
				+	"</SearchRequest>");
		
		// Verify message body content
		String body = ZimbraAccount.AccountA().soapSelectValue("//mail:m/mail:fr", null);
		ZAssert.assertStringContains(body, modifiedBody, "Verify modified body value");
		
		// Verify accepted appointment present in new calendar folder
		AppointmentItem acceptedAppointment = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(acceptedAppointment.getFolder(), folderSOAP.getId(), "Verify accepted appointment present in new calendar folder");
		
	}
	
	@Bugs(ids = "69132,65356,80407")
	@Test(
			description = "Accept meeting using 'Accept -> Don't Notify Organizer'", 
			groups = { "functional" })
	public void AcceptMeeting_05() throws HarnessException {

		// ------------------------ Test data ------------------------------------

		String apptSubject = "appointment" + ZimbraSeleniumProperties.getUniqueString();

		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);


		// --------------- Creating invitation (organizer) ----------------------------

		ZimbraAccount.AccountA().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
				+				"<s d='"+ startUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<e d='"+ endUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<or a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>"
				+				"<at role='REQ' ptst='NE' rsvp='1' a='" + app.zGetActiveAccount().EmailAddress + "'/>"
				+			"</inv>"
				+			"<e a='"+ app.zGetActiveAccount().EmailAddress +"' t='t'/>"
				+			"<su>"+ apptSubject +"</su>"
				+			"<mp content-type='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");        

		// ---------------- Create new calendar folder ---------------------
		
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		String folderName = ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + folderName + "' l='" + root.getId() + "' view='appointment'/>"
				+	"</CreateFolderRequest>");
		FolderItem folderSOAP = FolderItem.importFromSOAP(app.zGetActiveAccount(), folderName);
		
		SleepUtil.sleepMedium(); //created folder not visible after hitting Refresh, lets see after wait..

		// --------------- Login to attendee & accept invitation ----------------------------------------------------

		// Refresh the view
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Select the invitation
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, apptSubject);

		// Select different calendar folder and click to click to Accept > Don't Notify Organizer
		display.zPressButtonPulldown(Button.B_CALENDAR, folderName);
		display.zPressButtonPulldown(Button.B_ACCEPT, Button.O_ACCEPT_DONT_NOTIFY_ORGANIZER);

		// ---------------- Verification at organizer & invitee side both -------------------------------------       


		// --- Check that the organizer shows the attendee as "ACCEPT" ---

		// Organizer: Search for the appointment (InvId)
		ZimbraAccount.AccountA().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
				+		"<query>"+ apptSubject +"</query>"
				+	"</SearchRequest>");
		
		String organizerInvId = ZimbraAccount.AccountA().soapSelectValue("//mail:appt", "invId");

		// Get the appointment details
		ZimbraAccount.AccountA().soapSend(
					"<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ organizerInvId +"'/>");
		
		String attendeeStatus = ZimbraAccount.AccountA().soapSelectValue("//mail:at[@a='"+ app.zGetActiveAccount().EmailAddress +"']", "ptst");

		// Verify attendee status shows as ptst=NE (bug 65356)
		ZAssert.assertEquals(attendeeStatus, "NE", "Verify that the attendee shows as 'ACCEPTED'");


		// --- Check that the attendee showing status as "ACCEPT" ---

		// Attendee: Search for the appointment (InvId)
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
				+		"<query>"+ apptSubject +"</query>"
				+	"</SearchRequest>");
		
		String attendeeInvId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");

		// Get the appointment details
		app.zGetActiveAccount().soapSend(
					"<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ attendeeInvId +"'/>");
		
		String myStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ app.zGetActiveAccount().EmailAddress +"']", "ptst");

		// Verify attendee status shows as ptst=AC
		ZAssert.assertEquals(myStatus, "AC", "Verify that the attendee shows as 'ACCEPTED'");

		// Organizer: Search for the appointment response
		String inboxId = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), FolderItem.SystemFolder.Inbox).getId();
		
		ZimbraAccount.AccountA().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+		"<query>inid:"+ inboxId +" subject:("+ apptSubject +")</query>"
				+	"</SearchRequest>");
		
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>inid:"+ inboxId +" subject:("+ apptSubject +")</query>"
			+	"</SearchRequest>");

		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:m");
		ZAssert.assertEquals(nodes.length, 0, "Verify appointment notification message is not present");
		
		// Verify accepted appointment present in new calendar folder
		AppointmentItem acceptedAppointment = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(acceptedAppointment.getFolder(), folderSOAP.getId(), "Verify accepted appointment present in new calendar folder");
		
	}
}
