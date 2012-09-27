package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.scheduler;

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
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Field;

@SuppressWarnings("unused")
public class AddOptionalAttendee extends CalendarWorkWeekTest {	
	
	public AddOptionalAttendee() {
		logger.info("New "+ AddOptionalAttendee.class.getCanonicalName());
		
	}
	
	@Bugs(ids = "77711,48196")
	@DataProvider(name = "DataProviderShortcutKeys")
	public Object[][] DataProviderShortcutKeys() {
		return new Object[][] {
				new Object[] { "VK_ENTER", KeyEvent.VK_ENTER },
	//			new Object[] { "VK_TAB", KeyEvent.VK_TAB },
		};
	}
	@Test(description = "Add optional attendee from scheduler pane using keyboard Enter and Tab key",
			groups = { "sanity" },
			dataProvider = "DataProviderShortcutKeys")
	public void AddOptionalAttendee_01(String name, int keyEvent) throws HarnessException {
		
		// Create a meeting
		AppointmentItem appt = new AppointmentItem();
			
		String tz = ZTimeZone.TimeZoneEST.getID();
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptAttendee = ZimbraAccount.AccountA().EmailAddress;
		
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
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // Add optional attendee using scheduler and send the appointment
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        apptForm.zAddOptionalAttendeeFromScheduler(apptAttendee);
		app.zPageCalendar.zKeyboard.zTypeKeyEvent(keyEvent);
        ZAssert.assertTrue(apptForm.zVerifyOptionalAttendee(apptAttendee), "Verify email address bubble after adding attendee from scheduler");
        apptForm.zToolbarPressButton(Button.B_SEND);
 
        // Verify that attendee present in the appointment
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringContains(actual.getAttendees(), apptAttendee, "Attendees: Verify the appointment data");
		
		// Verify attendee free/busy status
		String attendeeStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptAttendee +"']", "ptst");
		ZAssert.assertEquals(attendeeStatus, "NE", "Verify attendee free/busy status");
		
		// Verify attendee receives meeting invitation message
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +")</query>"
			+	"</SearchRequest>");
		String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify attendee receives meeting invitation message");
		
	}
	
}
