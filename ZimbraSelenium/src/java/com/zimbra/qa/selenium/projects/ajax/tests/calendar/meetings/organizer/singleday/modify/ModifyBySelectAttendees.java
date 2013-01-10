package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.modify;

import java.awt.event.KeyEvent;
import java.util.Calendar;
import org.testng.annotations.*;

import com.thoughtworks.selenium.DefaultSelenium;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogConfirmDeleteOrganizer;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogFindAttendees;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogFindLocation;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogSendUpdatetoAttendees;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.*;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogFindAttendees.Locators;;

@SuppressWarnings("unused")
public class ModifyBySelectAttendees extends CalendarWorkWeekTest {	
	
	public ModifyBySelectAttendees() {
		logger.info("New "+ ModifyBySelectAttendees.class.getCanonicalName());
		
	}
	

	@Test(description = "modify appt and Add attendee to existing appointment from contact picker",
			groups = { "functional" })
	public void ModifyBySelectAttendees_01() throws HarnessException {
		
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
                     	"</inv>" +
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
        
        // Add attendee2 to existing appt from 'Select Address' dialog and resend the appointment
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        apptForm.zToolbarPressButton(Button.B_TO);
   
        DialogFindAttendees dialogFindAttendees = (DialogFindAttendees) new DialogFindAttendees(app, app.zPageCalendar);
        dialogFindAttendees.zType(Locators.ContactPickerSerachField, ZimbraAccount.AccountB().EmailAddress);
        dialogFindAttendees.zClickButton(Button.B_SEARCH);
        dialogFindAttendees.zWaitForBusyOverlay();
        dialogFindAttendees.zClick(Locators.ContactPickerFirstContact);
        dialogFindAttendees.zClickButton(Button.B_CHOOSE_CONTACT_FROM_PICKER);
        dialogFindAttendees.zWaitForBusyOverlay();
        dialogFindAttendees.zClickButton(Button.B_OK);
        
        // Send the modified appt
        apptForm.zToolbarPressButton(Button.B_SEND);
        SleepUtil.sleepVeryLong();// test fails while checking free/busy status, waitForPostqueue is not sufficient here
        // Tried sleepLong() as well but it still fails so using sleepVeryLong()

        
        // Verify attendee1 receives meeting invitation message
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +")</query>"
			+	"</SearchRequest>");
		id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify new invitation appears in the attendee1's inbox");
 
		
		// Verify that attendee2 present in the appointment
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringContains(actual.getAttendees(), apptAttendee2, "Attendees: Verify the appointment data");
		
		// Verify appointment is present in attendee2's calendar
		AppointmentItem addeddAttendee = AppointmentItem.importFromSOAP(ZimbraAccount.AccountB(), "subject:("+ apptSubject +")");
		ZAssert.assertNotNull(addeddAttendee, "Verify meeting invite is present in attendee2's calendar");
		
		// Verify attendee2 free/busy status
		String attendee2Status = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptAttendee2 +"']", "ptst");
		ZAssert.assertEquals(attendee2Status, "NE", "Verify attendee2 free/busy status");
		
	}
}
