package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer;

import java.awt.event.KeyEvent;
import java.util.Calendar;

import org.testng.annotations.*;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogConfirmDeleteOrganizer;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogSendUpdatetoAttendees;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar.Locators;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;

@SuppressWarnings("unused")
public class RemoveAttendee extends CalendarWorkWeekTest {	
	
	public RemoveAttendee() {
		logger.info("New "+ RemoveAttendee.class.getCanonicalName());
		
	}
	
	@Bugs(ids = "77588,77590")
	@Test(description = "Remove attendee from existing appointment and send updates only to added/removed attendees",
			groups = { "smoke" })
	public void RemoveAttendee_01() throws HarnessException {
		
		// Create a meeting
		AppointmentItem appt = new AppointmentItem();
			
		String tz = ZTimeZone.TimeZoneEST.getID();
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		String apptAttendee2 = ZimbraAccount.AccountB().EmailAddress;		
		
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
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee2 + "' d='2'/>" +
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // Remove attendee2 and resend the appointment
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        apptForm.zRemoveAttendee(apptAttendee2);
        apptForm.zToolbarPressButton(Button.B_SEND);
        DialogSendUpdatetoAttendees sendUpdateDialog = (DialogSendUpdatetoAttendees) new DialogSendUpdatetoAttendees(app, app.zPageCalendar);
        sendUpdateDialog.zClickButton(Button.B_SEND_UPDATES_ONLY_TO_ADDED_OR_REMOVED_ATTENDEES);
        sendUpdateDialog.zClickButton(Button.B_OK);
 
        // Verify that attendee2 doesn't present in the appointment
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringDoesNotContain(actual.getAttendees(), apptAttendee2, "Attendees: Verify the appointment data");
		
		// Verify attendee2 free/busy status
		String attendee2Status = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptAttendee2 +"']", "ptst");
		ZAssert.assertNull(attendee2Status, "Verify that attendee2 status shows null");
		
		// Verify attendee2 receives cancellation message
		ZimbraAccount.AccountB().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:" + (char)34 + "Cancelled: " + apptSubject + (char)34 + "</query>"
			+	"</SearchRequest>");
		String id = ZimbraAccount.AccountB().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify attendee2 receives cancelled meeting message");
		
		// Verify appointment is deleted from attendee2's calendar
		AppointmentItem removedAttendee = AppointmentItem.importFromSOAP(ZimbraAccount.AccountB(), "subject:("+ apptSubject +")");
		ZAssert.assertNull(removedAttendee, "Verify meeting is deleted from attendee2's calendar");
		
	}
	
	@Bugs(ids = "77588,77590")
	@Test(description = "Remove attendee from existing appointment and send updates to all attendees",
			groups = { "smoke" })
	public void RemoveAttendee_02() throws HarnessException {
		
		// Create a meeting
		AppointmentItem appt = new AppointmentItem();
			
		String tz = ZTimeZone.TimeZoneEST.getID();
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		String apptAttendee2 = ZimbraAccount.AccountB().EmailAddress;		
		
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
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee2 + "' d='2'/>" +
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // Delete the invite message from the attendee1's mailbox
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +")</query>"
			+	"</SearchRequest>");
		String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
        
        ZimbraAccount.AccountA().soapSend(
				"<ItemActionRequest  xmlns='urn:zimbraMail'>"
			+		"<action id='"+ id +"' op='delete'/>"
			+	"</ItemActionRequest>");
		
        // Remove attendee2 and resend the appointment
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        apptForm.zRemoveAttendee(apptAttendee2);
        apptForm.zToolbarPressButton(Button.B_SEND);
        DialogSendUpdatetoAttendees sendUpdateDialog = (DialogSendUpdatetoAttendees) new DialogSendUpdatetoAttendees(app, app.zPageCalendar);
        sendUpdateDialog.zClickButton(Button.B_SEND_UPDATES_TO_ALL_ATTENDEES);
        sendUpdateDialog.zClickButton(Button.B_OK);
        
        // Verify meeting invite appears to attendee1
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +")</query>"
			+	"</SearchRequest>");
		id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify new invitation appears in the attendee1's inbox");
 
        // Verify that attendee2 doesn't present in the appointment
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringDoesNotContain(actual.getAttendees(), apptAttendee2, "Attendees: Verify the appointment data");
		
		// Verify attendee2 free/busy status
		String attendee2Status = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptAttendee2 +"']", "ptst");
		ZAssert.assertNull(attendee2Status, "Verify that attendee2 status shows null");
		
		// Verify attendee2 receives cancellation message
		ZimbraAccount.AccountB().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:" + (char)34 + "Cancelled: " + apptSubject + (char)34 + "</query>"
			+	"</SearchRequest>");
		id = ZimbraAccount.AccountB().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify attendee2 receives cancelled meeting message");
		
		// Verify appointment is deleted from attendee2's calendar
		AppointmentItem removedAttendee = AppointmentItem.importFromSOAP(ZimbraAccount.AccountB(), "subject:("+ apptSubject +")");
		ZAssert.assertNull(removedAttendee, "Verify meeting is deleted from attendee2's calendar");
		
	}
}
