package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer;

import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogConfirm;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning.DialogWarningID;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;

@SuppressWarnings("unused")
public class CancelMeeting extends AjaxCommonTest {	
	public CancelMeeting() {
		logger.info("New "+ CancelMeeting.class.getCanonicalName());
		
		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;
	}
	
	@Test(description = "Cancel an meeting using Delete toolbar button",
			groups = { "smoke" })
	public void CancelMeeting_01() throws HarnessException {
		
		// Creating object for meeting data
		String tz, apptSubject, apptBody, apptAttendee1;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     "<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     "<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" + 
                     "</inv>" +
                     "<e a='"+ apptAttendee1 +"' t='t'/>" +
                     "<mp content-type='text/plain'>" +
                     "<content>"+ apptBody +"</content>" +
                     "</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
		String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        
        // Right click to meeting and cancel it
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.B_DELETE);
        DialogConfirm dlgConfirm = new DialogConfirm(DialogConfirm.Confirmation.DELETE, app, ((AppAjaxClient) app).zPageCalendar);
		dlgConfirm.zClickButton(Button.B_NO);
		dlgConfirm.zWaitForClose();
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify meeting is deleted from organizer's calendar");
		
		// Verify meeting is deleted from attendee's calendar
		SleepUtil.sleepVeryLong(); //importSOAP gives wrong response without sleep
		AppointmentItem canceledAppt = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")", startUTC, endUTC);
		ZAssert.assertNull(canceledAppt, "Verify meeting is deleted from attendee's calendar");
	}
	
	@Test(description = "Cancel an meeting using context menu",
			groups = { "functional" })
	public void CancelMeeting_02() throws HarnessException {
		
		// Creating object for meeting data
		String tz, apptSubject, apptBody, apptAttendee1;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     "<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     "<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" + 
                     "</inv>" +
                     "<e a='"+ apptAttendee1 +"' t='t'/>" +
                     "<mp content-type='text/plain'>" +
                     "<content>"+ apptBody +"</content>" +
                     "</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
		String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        
        // Right click to meeting and cancel it
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_CANCEL_MENU, apptSubject);
        DialogConfirm dlgConfirm = new DialogConfirm(DialogConfirm.Confirmation.DELETE, app, ((AppAjaxClient) app).zPageCalendar);
		dlgConfirm.zClickButton(Button.B_NO);
		dlgConfirm.zWaitForClose();
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify meeting is deleted from organizer's calendar");
		
		// Verify meeting is deleted from attendee's calendar
		SleepUtil.sleepLong(); //importSOAP gives wrong response without sleep
		AppointmentItem canceledAppt = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")", startUTC, endUTC);
		ZAssert.assertNull(canceledAppt, "Verify meeting is deleted from attendee's calendar");
	}
	
	@DataProvider(name = "DataProviderShortcutKeys")
	public Object[][] DataProviderShortcutKeys() {
		return new Object[][] {
				new Object[] { "VK_DELETE", KeyEvent.VK_DELETE },
				new Object[] { "VK_BACK_SPACE", KeyEvent.VK_BACK_SPACE },
		};
	}

	@Test(description = "Cancel an meeting using keyboard shortcuts Del & Backspace",
			groups = { "functional" },
			dataProvider = "DataProviderShortcutKeys")
	public void CancelMeeting_03(String name, int keyEvent) throws HarnessException {
		
		// Creating object for meeting data
		String tz, apptSubject, apptBody, apptAttendee1;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     "<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     "<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" + 
                     "</inv>" +
                     "<e a='"+ apptAttendee1 +"' t='t'/>" +
                     "<mp content-type='text/plain'>" +
                     "<content>"+ apptBody +"</content>" +
                     "</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
		String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        
        // Cancel meeting using keyboard Del and Backspace key
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        DialogConfirm dlgConfirm = (DialogConfirm)app.zPageCalendar.zKeyboardKeyEvent(keyEvent);
		dlgConfirm.zClickButton(Button.B_NO);
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-7).toMillis() +"' calExpandInstEnd='"+ startUTC.addDays(7).toMillis() +"'>"
			+	"<query>subject:("+ apptSubject +")</query>"
			+	"</SearchRequest>");
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:appt");
		ZAssert.assertEquals(nodes.length, 0, "Verify meeting is deleted from organizer's calendar");
		
		// Verify meeting is deleted from attendee's calendar
		SleepUtil.sleepLong(); //importSOAP gives wrong response without sleep
		AppointmentItem canceledAppt = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")", startUTC, endUTC);
		ZAssert.assertNull(canceledAppt, "Verify meeting is deleted from attendee's calendar");
	}
	
	@Test(description = "Don't cancel an meeting (press Cancel button in meeting cancellation dialog)",
			groups = { "functional" })
	public void CancelMeeting_04() throws HarnessException {
		
		// Creating object for meeting data
		String tz, apptSubject, apptBody, apptAttendee1;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     "<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     "<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" + 
                     "</inv>" +
                     "<e a='"+ apptAttendee1 +"' t='t'/>" +
                     "<mp content-type='text/plain'>" +
                     "<content>"+ apptBody +"</content>" +
                     "</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
		String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        
        // Right click to meeting and cancel it
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.B_DELETE);
        DialogConfirm dlgConfirm = new DialogConfirm(DialogConfirm.Confirmation.DELETE, app, ((AppAjaxClient) app).zPageCalendar);
		dlgConfirm.zClickButton(Button.B_CANCEL);
		dlgConfirm.zWaitForClose();
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), true, "Verify meeting is not deleted from organizer's calendar");
		
		// Verify meeting is not deleted from attendee's calendar
		SleepUtil.sleepLong(); //importSOAP gives wrong response without sleep
		AppointmentItem canceledAppt = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")", startUTC, endUTC);
		ZAssert.assertNotNull(canceledAppt, "Verify meeting is not deleted from attendee's calendar");
	}
	
	@Test(description = "Send meeting cancellation message to attendee while cancelling appointment",
			groups = { "functional" })
	public void CancelMeeting_05() throws HarnessException {
		
		// Creating object for meeting data
		String tz, apptSubject, apptBody, apptAttendee1;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     "<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     "<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" + 
                     "</inv>" +
                     "<e a='"+ apptAttendee1 +"' t='t'/>" +
                     "<mp content-type='text/plain'>" +
                     "<content>"+ apptBody +"</content>" +
                     "</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
		String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        
        // Right click to meeting and cancel it
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_CANCEL_MENU, apptSubject);
        DialogConfirm dlgConfirm = new DialogConfirm(DialogConfirm.Confirmation.DELETE, app, ((AppAjaxClient) app).zPageCalendar);
		dlgConfirm.zClickButton(Button.B_YES);
		FormMailNew mailComposeForm = new FormMailNew(app);
		SleepUtil.sleepLong();
		mailComposeForm.zToolbarPressButton(Button.B_SEND);
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify meeting is deleted from organizer's calendar");
		
		// Verify meeting is deleted from attendee's calendar && receive meeting cancellation message
		SleepUtil.sleepLong(); //importSOAP gives wrong response without sleep
		MailItem canceledApptMail = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:(" + (char)34 + "Cancelled " + apptSubject + (char)34 + ")");
		ZAssert.assertNotNull(canceledApptMail, "Verify meeting cancellation message received to attendee");
		
		AppointmentItem canceledAppt = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")", startUTC, endUTC);
		ZAssert.assertNull(canceledAppt, "Verify meeting is deleted from attendee's calendar");
	}
	
	@Test(description = "Modify meeting cancellation message while cancelling appointment",
			groups = { "functional" })
	public void CancelMeeting_06() throws HarnessException {
		
		// Creating object for meeting data
		String tz, apptSubject, apptBody, apptAttendee1, modifyApptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		modifyApptBody = "Modifying body to " + ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     "<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     "<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" + 
                     "</inv>" +
                     "<e a='"+ apptAttendee1 +"' t='t'/>" +
                     "<mp content-type='text/plain'>" +
                     "<content>"+ apptBody +"</content>" +
                     "</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
		String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        
        // Right click to meeting and cancel it
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_CANCEL_MENU, apptSubject);
        DialogConfirm dlgConfirm = new DialogConfirm(DialogConfirm.Confirmation.DELETE, app, ((AppAjaxClient) app).zPageCalendar);
		dlgConfirm.zClickButton(Button.B_YES);
		SleepUtil.sleepLong();
		FormMailNew mailComposeForm = new FormMailNew(app);
		mailComposeForm.zFillField(Field.Body, modifyApptBody);		
		mailComposeForm.zSubmit();
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify meeting is deleted from organizer's calendar");
		
		SleepUtil.sleepLong(); //importSOAP gives wrong response without sleep
		MailItem canceledApptMail = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:(" + (char)34 + "Cancelled " + apptSubject + (char)34 + ")");
		
		// Verify meeting cancellation message with exact body content
		ZAssert.assertNotNull(canceledApptMail, "Verify meeting cancellation message received to attendee");
		ZAssert.assertStringContains(canceledApptMail.dBodyText, modifyApptBody, "Verify the body field value is correct");
		
		// Verify meeting is deleted from attendee's calendar
		AppointmentItem canceledAppt = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")", startUTC, endUTC);
		ZAssert.assertNull(canceledAppt, "Verify meeting is deleted from attendee's calendar");
	}
}
