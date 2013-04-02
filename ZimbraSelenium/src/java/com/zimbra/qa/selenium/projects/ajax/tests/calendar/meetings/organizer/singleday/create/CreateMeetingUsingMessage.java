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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.create;

import java.io.File;
import java.util.Calendar;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Field;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogAddAttendees;

public class CreateMeetingUsingMessage extends CalendarWorkWeekTest {

	public CreateMeetingUsingMessage() {
		logger.info("New "+ CreateMeetingUsingMessage.class.getCanonicalName());
		super.startingPage = app.zPageMail;
	}
	
	@Test(description = "Create a meeting invite by right clicking to HTML formatted message by setting zimbraPrefComposeFormat=text & zimbraPrefForwardReplyInOriginalFormat=TRUE",
			groups = { "functional" })
			
	public void CreateMeetingUsingMessage_01() throws HarnessException {
		
		app.zPageMain.zLogout();
		ZimbraAccount.AccountZWC().soapSend(
				"<ModifyPrefsRequest xmlns='urn:zimbraAccount'>"
			+		"<pref name='zimbraPrefComposeFormat'>"+ "text" +"</pref>"
			+		"<pref name='zimbraPrefForwardReplyInOriginalFormat'>"+ "TRUE" +"</pref>"
			+	"</ModifyPrefsRequest>");
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email10/mimeHtmlOnly1.txt";
		final String subject = "1 html format";
		final String content = "Bold and Italics";
		String apptAttendee1 = "foo@example.com";
		String apptAttendee2 = "bar@example.com";
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Click to Refresh button
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Rt-click to message and hit 'Create Appointment'
		DialogAddAttendees dlgAddAttendees = (DialogAddAttendees) app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.O_CREATE_APPOINTMENT, subject);
		dlgAddAttendees.zClickButton(Button.B_YES);
		
		FormApptNew apptForm = new FormApptNew(app);
		apptForm.zFillField(Field.StartDate, startUTC);
		apptForm.zFillField(Field.EndDate, endUTC);
		ZAssert.assertEquals(apptForm.zGetApptSubject(), subject, "Verify populated appointment subject from message");
		ZAssert.assertTrue(apptForm.zVerifyRequiredAttendee(apptAttendee1), "Verify populated email address bubble 1 from message");
		ZAssert.assertTrue(apptForm.zVerifyRequiredAttendee(apptAttendee2), "Verify populated email address bubble 2 from message");
		ZAssert
				.assertEquals(
						apptForm.zGetApptBodyValue(),
						"<html><head><style>p { margin: 0; }</style></head><body><div style="
								+ ((char) 34)
								+ "font-family: arial,helvetica,sans-serif; font-size: 12pt; color: #000000"
								+ ((char) 34)
								+ "><div><strong>Bold</strong> and <em>Italics</em><br><br></div></div></body></html>",
						"Verify populated appointment body from message");
		apptForm.zToolbarPressButton(Button.B_SEND);
		
		// Verify appointment exists on the server
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ subject +")" + " " + "content:(" + content +")</query>"
			+	"</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
		ZAssert.assertNotNull(id, "Verify meeting invite is not null");
		
		// Open appointment again and check from the UI side
		app.zPageCalendar.zNavigateTo();
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		if (app.zPageCalendar.zClickToRefreshOnceIfApptDoesntExists("1 html f...") == false) {
			app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		}
		app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, "1 html f...");
		ZAssert.assertTrue(apptForm.zVerifyRequiredAttendee(apptAttendee1), "Open created appointment again and verify attendee1");
		ZAssert.assertTrue(apptForm.zVerifyRequiredAttendee(apptAttendee2), "Open created appointment again and verify attendee2");
		ZAssert.assertStringContains(apptForm.zGetApptBodyText(), content, "Open created appointment again and verify body text");
		apptForm.zToolbarPressButton(Button.B_CLOSE);
		
	}
	
	@Bugs(ids = "80922")
	@Test(description = "Create a meeting invite by right clicking to HTML formatted message by setting zimbraPrefComposeFormat=text & zimbraPrefForwardReplyInOriginalFormat=FALSE",
			groups = { "functional" })
			
	public void CreateMeetingUsingMessage_02() throws HarnessException {
		
		app.zPageMain.zLogout();
		ZimbraAccount.AccountZWC().soapSend(
				"<ModifyPrefsRequest xmlns='urn:zimbraAccount'>"
			+		"<pref name='zimbraPrefComposeFormat'>"+ "text" +"</pref>"
			+		"<pref name='zimbraPrefForwardReplyInOriginalFormat'>"+ "FALSE" +"</pref>"
			+	"</ModifyPrefsRequest>");
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email10/mimeHtmlOnly2.txt";
		final String subject = "2 html format";
		final String content = "Bold and Italics";
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Click to Refresh button
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Rt-click to message and hit 'Create Appointment'
		DialogAddAttendees dlgAddAttendees = (DialogAddAttendees) app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.O_CREATE_APPOINTMENT, subject);
		dlgAddAttendees.zClickButton(Button.B_YES);
		
		FormApptNew apptForm = new FormApptNew(app);
		apptForm.zFillField(Field.StartDate, startUTC);
		apptForm.zFillField(Field.EndDate, endUTC);
		ZAssert.assertEquals(apptForm.zGetApptSubject(), subject, "Verify populated appointment subject from message");
		ZAssert
				.assertEquals(
						apptForm.zGetApptBodyValue(),
						"<html><head><style>p { margin: 0; }</style></head><body><div style="
								+ ((char) 34)
								+ "font-family: arial,helvetica,sans-serif; font-size: 12pt; color: #000000"
								+ ((char) 34)
								+ "><div><strong>Bold</strong> and <em>Italics</em><br><br></div></div></body></html>",
						"Verify populated appointment body from message");
		apptForm.zToolbarPressButton(Button.B_SEND);
		
		// Verify appointment exists on the server
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ subject +")" + " " + "content:(" + content +")</query>"
			+	"</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
		ZAssert.assertNotNull(id, "Verify meeting invite is not null");
		
		// Open appointment again and check from the UI side
		app.zPageCalendar.zNavigateTo();
		if (app.zPageCalendar.zClickToRefreshOnceIfApptDoesntExists("2 html f...") == false) {
			app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		}	
		app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, "2 html f...");
		ZAssert.assertStringContains(apptForm.zGetApptBodyValue(), content, "Open created appointment again and verify body text");
		apptForm.zToolbarPressButton(Button.B_CLOSE);
		
	}
	
	@Test(description = "Create a meeting invite by right clicking to plain text formatted message by setting zimbraPrefComposeFormat=text & zimbraPrefForwardReplyInOriginalFormat=FALSE",
			groups = { "functional" })
			
	public void CreateMeetingUsingMessage_03() throws HarnessException {
		
		app.zPageMain.zLogout();
		ZimbraAccount.AccountZWC().soapSend(
				"<ModifyPrefsRequest xmlns='urn:zimbraAccount'>"
			+		"<pref name='zimbraPrefComposeFormat'>"+ "text" +"</pref>"
			+		"<pref name='zimbraPrefForwardReplyInOriginalFormat'>"+ "FALSE" +"</pref>"
			+	"</ModifyPrefsRequest>");
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email10/mimeTextOnly1.txt";
		final String subject = "1 plain text format";
		final String content = "The Ming Dynasty";
		final String fullContent = "The Ming Dynasty, also Empire of the Great Ming, was the ruling dynasty of China from 1368 to 1644.";
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Click to Refresh button
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Rt-click to message and hit 'Create Appointment'
		DialogAddAttendees dlgAddAttendees = (DialogAddAttendees) app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.O_CREATE_APPOINTMENT, subject);
		dlgAddAttendees.zClickButton(Button.B_NO);
		
		FormApptNew apptForm = new FormApptNew(app);
		apptForm.zFillField(Field.StartDate, startUTC);
		apptForm.zFillField(Field.EndDate, endUTC);
		ZAssert.assertEquals(apptForm.zGetApptSubject(), subject, "Verify populated appointment subject from message");
		ZAssert.assertStringContains(apptForm.zGetApptBodyValue(), fullContent, "Verify populated appointment body from message");
		apptForm.zToolbarPressButton(Button.B_SEND);
		
		// Verify appointment exists on the server
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ subject +")" + " " + "content:(" + content +")</query>"
			+	"</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
		ZAssert.assertNotNull(id, "Verify meeting invite is not null");
		
		// Open appointment again and check from the UI side
		app.zPageCalendar.zNavigateTo();
		if (app.zPageCalendar.zClickToRefreshOnceIfApptDoesntExists("1 plain ...") == false) {
			app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		}	
		app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, "1 plain ...");
		ZAssert.assertStringContains(apptForm.zGetApptBodyValue(), fullContent, "Open created appointment again and verify body text");
		apptForm.zToolbarPressButton(Button.B_CLOSE);
		
	}
	
	@Test(description = "Create a meeting invite by right clicking to plain text formatted message by setting zimbraPrefComposeFormat=text & zimbraPrefForwardReplyInOriginalFormat=TRUE",
			groups = { "functional" })
			
	public void CreateMeetingUsingMessage_04() throws HarnessException {
		
		app.zPageMain.zLogout();
		ZimbraAccount.AccountZWC().soapSend(
				"<ModifyPrefsRequest xmlns='urn:zimbraAccount'>"
			+		"<pref name='zimbraPrefComposeFormat'>"+ "text" +"</pref>"
			+		"<pref name='zimbraPrefForwardReplyInOriginalFormat'>"+ "TRUE" +"</pref>"
			+	"</ModifyPrefsRequest>");
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email10/mimeTextOnly2.txt";
		final String subject = "2 plain text format";
		final String content = "The Ming Dynasty";
		final String fullContent = "The Ming Dynasty, also Empire of the Great Ming, was the ruling dynasty of China from 1368 to 1644.";
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Click to Refresh button
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Rt-click to message and hit 'Create Appointment'
		DialogAddAttendees dlgAddAttendees = (DialogAddAttendees) app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.O_CREATE_APPOINTMENT, subject);
		dlgAddAttendees.zClickButton(Button.B_NO);
		
		FormApptNew apptForm = new FormApptNew(app);
		apptForm.zFillField(Field.StartDate, startUTC);
		apptForm.zFillField(Field.EndDate, endUTC);
		ZAssert.assertEquals(apptForm.zGetApptSubject(), subject, "Verify populated appointment subject from message");
		ZAssert.assertStringContains(apptForm.zGetApptBodyValue(), fullContent, "Verify populated appointment body from message");
		apptForm.zToolbarPressButton(Button.B_SEND);
		
		// Verify appointment exists on the server
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ subject +")" + " " + "content:(" + content +")</query>"
			+	"</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
		ZAssert.assertNotNull(id, "Verify meeting invite is not null");
		
		// Open appointment again and check from the UI side
		app.zPageCalendar.zNavigateTo();
		if (app.zPageCalendar.zClickToRefreshOnceIfApptDoesntExists("2 plain ...") == false) {
			app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		}	
		app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, "2 plain ...");
		ZAssert.assertStringContains(apptForm.zGetApptBodyValue(), fullContent, "Open created appointment again and verify body text");
		apptForm.zToolbarPressButton(Button.B_CLOSE);
		
	}
	
	@Test(description = "Create a meeting invite by right clicking to HTML formatted message by setting zimbraPrefComposeFormat=html & zimbraPrefForwardReplyInOriginalFormat=TRUE",
			groups = { "functional" })
			
	public void CreateMeetingUsingMessage_05() throws HarnessException {
		
		app.zPageMain.zLogout();
		ZimbraAccount.AccountZWC().soapSend(
				"<ModifyPrefsRequest xmlns='urn:zimbraAccount'>"
			+		"<pref name='zimbraPrefComposeFormat'>"+ "html" +"</pref>"
			+		"<pref name='zimbraPrefForwardReplyInOriginalFormat'>"+ "FALSE" +"</pref>"
			+	"</ModifyPrefsRequest>");
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email10/mimeHtmlOnly3.txt";
		final String subject = "3 html format";
		final String content = "Bold and Italics";
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Click to Refresh button
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Rt-click to message and hit 'Create Appointment'
		DialogAddAttendees dlgAddAttendees = (DialogAddAttendees) app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.O_CREATE_APPOINTMENT, subject);
		dlgAddAttendees.zClickButton(Button.B_YES);
		
		FormApptNew apptForm = new FormApptNew(app);
		apptForm.zFillField(Field.StartDate, startUTC);
		apptForm.zFillField(Field.EndDate, endUTC);
		ZAssert.assertEquals(apptForm.zGetApptSubject(), subject, "Verify populated appointment subject from message");
		ZAssert
				.assertEquals(
						apptForm.zGetApptBodyValue(),
						"<html><head><style>p { margin: 0; }</style></head><body><div style="
								+ ((char) 34)
								+ "font-family: arial,helvetica,sans-serif; font-size: 12pt; color: #000000"
								+ ((char) 34)
								+ "><div><strong>Bold</strong> and <em>Italics</em><br><br></div></div></body></html>",
						"Verify populated appointment body from message");
		apptForm.zToolbarPressButton(Button.B_SEND);
		
		// Verify appointment exists on the server
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ subject +")" + " " + "content:(" + content +")</query>"
			+	"</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
		ZAssert.assertNotNull(id, "Verify meeting invite is not null");
		
		// Open appointment again and check from the UI side
		app.zPageCalendar.zNavigateTo();
		if (app.zPageCalendar.zClickToRefreshOnceIfApptDoesntExists("3 html f...") == false) {
			app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		}	
		app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, "3 html f...");
		ZAssert.assertStringContains(apptForm.zGetApptBodyText(), content, "Open created appointment again and verify body text");
		apptForm.zToolbarPressButton(Button.B_CLOSE);
		
	}
	
	@Test(description = "Create a meeting invite by right clicking to HTML formatted message by setting zimbraPrefComposeFormat=html & zimbraPrefForwardReplyInOriginalFormat=FALSE",
			groups = { "functional" })
			
	public void CreateMeetingUsingMessage_06() throws HarnessException {
		
		app.zPageMain.zLogout();
		ZimbraAccount.AccountZWC().soapSend(
				"<ModifyPrefsRequest xmlns='urn:zimbraAccount'>"
			+		"<pref name='zimbraPrefComposeFormat'>"+ "html" +"</pref>"
			+		"<pref name='zimbraPrefForwardReplyInOriginalFormat'>"+ "FALSE" +"</pref>"
			+	"</ModifyPrefsRequest>");
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email10/mimeHtmlOnly4.txt";
		final String subject = "4 html format";
		final String content = "Bold and Italics";
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Click to Refresh button
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Rt-click to message and hit 'Create Appointment'
		DialogAddAttendees dlgAddAttendees = (DialogAddAttendees) app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.O_CREATE_APPOINTMENT, subject);
		dlgAddAttendees.zClickButton(Button.B_YES);
		
		FormApptNew apptForm = new FormApptNew(app);
		apptForm.zFillField(Field.StartDate, startUTC);
		apptForm.zFillField(Field.EndDate, endUTC);
		ZAssert.assertEquals(apptForm.zGetApptSubject(), subject, "Verify populated appointment subject from message");
		ZAssert
				.assertEquals(
						apptForm.zGetApptBodyValue(),
						"<html><head><style>p { margin: 0; }</style></head><body><div style="
								+ ((char) 34)
								+ "font-family: arial,helvetica,sans-serif; font-size: 12pt; color: #000000"
								+ ((char) 34)
								+ "><div><strong>Bold</strong> and <em>Italics</em><br><br></div></div></body></html>",
						"Verify populated appointment body from message");
		apptForm.zToolbarPressButton(Button.B_SEND);
		
		// Verify appointment exists on the server
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ subject +")" + " " + "content:(" + content +")</query>"
			+	"</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
		ZAssert.assertNotNull(id, "Verify meeting invite is not null");
		
		// Open appointment again and check from the UI side
		app.zPageCalendar.zNavigateTo();
		if (app.zPageCalendar.zClickToRefreshOnceIfApptDoesntExists("4 html f...") == false) {
			app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		}	
		app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, "4 html f...");
		ZAssert.assertStringContains(apptForm.zGetApptBodyText(), content, "Open created appointment again and verify body text");
		apptForm.zToolbarPressButton(Button.B_CLOSE);
		
	}
	
	
	@Test(description = "Create a meeting invite by right clicking to plain text formatted message by setting zimbraPrefComposeFormat=html & zimbraPrefForwardReplyInOriginalFormat=TRUE",
			groups = { "functional" })
			
	public void CreateMeetingUsingMessage_07() throws HarnessException {
		
		app.zPageMain.zLogout();
		ZimbraAccount.AccountZWC().soapSend(
				"<ModifyPrefsRequest xmlns='urn:zimbraAccount'>"
			+		"<pref name='zimbraPrefComposeFormat'>"+ "html" +"</pref>"
			+		"<pref name='zimbraPrefForwardReplyInOriginalFormat'>"+ "TRUE" +"</pref>"
			+	"</ModifyPrefsRequest>");
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email10/mimeTextOnly3.txt";
		final String subject = "3 plain text format";
		final String content = "The Ming Dynasty";
		final String fullContent = "The Ming Dynasty, also Empire of the Great Ming, was the ruling dynasty of China from 1368 to 1644.";
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Click to Refresh button
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Rt-click to message and hit 'Create Appointment'
		DialogAddAttendees dlgAddAttendees = (DialogAddAttendees) app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.O_CREATE_APPOINTMENT, subject);
		dlgAddAttendees.zClickButton(Button.B_YES);
		
		FormApptNew apptForm = new FormApptNew(app);
		apptForm.zFillField(Field.StartDate, startUTC);
		apptForm.zFillField(Field.EndDate, endUTC);
		ZAssert.assertEquals(apptForm.zGetApptSubject(), subject, "Verify populated appointment subject from message");
		apptForm.zToolbarPressButton(Button.B_SEND);
		
		// Verify appointment exists on the server
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ subject +")" + " " + "content:(" + content +")</query>"
			+	"</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
		ZAssert.assertNotNull(id, "Verify meeting invite is not null");
		
		// Open appointment again and check from the UI side
		app.zPageCalendar.zNavigateTo();
		if (app.zPageCalendar.zClickToRefreshOnceIfApptDoesntExists("3 plain ...") == false) {
			app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		}	
		app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, "3 plain ...");
		ZAssert.assertStringContains(apptForm.zGetApptBodyValue(), fullContent, "Open created appointment again and verify body text");
		apptForm.zToolbarPressButton(Button.B_CLOSE);
		
	}
	
	@Bugs(ids = "80922")
	@Test(description = "Create a meeting invite by right clicking to plain text formatted message by setting zimbraPrefComposeFormat=html & zimbraPrefForwardReplyInOriginalFormat=FALSE",
			groups = { "functional" })
			
	public void CreateMeetingUsingMessage_08() throws HarnessException {
		
		app.zPageMain.zLogout();
		ZimbraAccount.AccountZWC().soapSend(
				"<ModifyPrefsRequest xmlns='urn:zimbraAccount'>"
			+		"<pref name='zimbraPrefComposeFormat'>"+ "html" +"</pref>"
			+		"<pref name='zimbraPrefForwardReplyInOriginalFormat'>"+ "FALSE" +"</pref>"
			+	"</ModifyPrefsRequest>");
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email10/mimeTextOnly4.txt";
		final String subject = "4 plain text format";
		final String content = "The Ming Dynasty";
		final String fullContent = "The Ming Dynasty, also Empire of the Great Ming, was the ruling dynasty of China from 1368 to 1644.";
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Click to Refresh button
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Rt-click to message and hit 'Create Appointment'
		DialogAddAttendees dlgAddAttendees = (DialogAddAttendees) app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.O_CREATE_APPOINTMENT, subject);		
		dlgAddAttendees.zClickButton(Button.B_NO);
		
		FormApptNew apptForm = new FormApptNew(app);
		apptForm.zFillField(Field.StartDate, startUTC);
		apptForm.zFillField(Field.EndDate, endUTC);
		ZAssert.assertEquals(apptForm.zGetApptSubject(), subject, "Verify populated appointment subject from message");
		ZAssert
			.assertEquals(
					apptForm.zGetApptBodyValue(),
					"<html><head><style>p { margin: 0; }</style></head><body><div style="
							+ ((char) 34)
							+ "font-family: arial,helvetica,sans-serif; font-size: 12pt; color: #000000"
							+ ((char) 34)
							+ "><div><strong>Bold</strong> and <em>Italics</em><br><br></div></div></body></html>",
					"Verify populated appointment body from message");
		ZAssert.assertStringContains(apptForm.zGetApptBodyValue(), fullContent, "Verify populated appointment body from message");
		apptForm.zToolbarPressButton(Button.B_SEND);
		
		// Verify appointment exists on the server
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ subject +")" + " " + "content:(" + content +")</query>"
			+	"</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
		ZAssert.assertNotNull(id, "Verify meeting invite is not null");
		
		// Open appointment again and check from the UI side
		app.zPageCalendar.zNavigateTo();
		if (app.zPageCalendar.zClickToRefreshOnceIfApptDoesntExists("4 plain ...") == false) {
			app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		}	
		app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, "4 plain ...");
		ZAssert.assertStringContains(apptForm.zGetApptBodyText(), fullContent, "Open created appointment again and verify body text");
		apptForm.zToolbarPressButton(Button.B_CLOSE);
		
	}
	
}
