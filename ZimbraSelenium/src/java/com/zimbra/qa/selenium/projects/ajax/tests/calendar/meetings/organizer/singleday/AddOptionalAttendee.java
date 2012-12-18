package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZDate;
import com.zimbra.qa.selenium.framework.util.ZTimeZone;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogFindAttendees.Locators;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogFindAttendees;

public class AddOptionalAttendee extends CalendarWorkWeekTest {	
	
	public AddOptionalAttendee() {
		logger.info("New "+ AddOptionalAttendee.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Test(description = "Add optional attendee by typing in the field and resend the appointment",
			groups = { "functional" })
		
	public void AddOptionalAttendee_01() throws HarnessException {
		
		// Create a meeting
		AppointmentItem appt = new AppointmentItem();
			
		String tz = ZTimeZone.TimeZoneEST.getID();
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptOptionalAttendee = ZimbraAccount.AccountA().EmailAddress;
		
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
        appt.setOptional(apptOptionalAttendee);
        
        // Add optional attendee by typing in the field and resend the appointment
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        apptForm.zClickAt(Locators.ShowOptionalLink,"");
        apptForm.zFill(appt);
        SleepUtil.sleepMedium();
		apptForm.zSubmit();
        apptForm.zToolbarPressButton(Button.B_SEND);
        SleepUtil.sleepVeryLong(); // test fails while checking free/busy status, waitForPostqueue is not sufficient here
        // Tried sleepLong() as well but although fails so using sleepVeryLong()
   
  	  // Verify that optional attendee present in the appointment
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertEquals(actual.getOptional(),apptOptionalAttendee , "optional attendee: Verify the appointment data");
	
     	
		// Verify optional attendee free/busy status
		String attendeeStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptOptionalAttendee +"']", "ptst");
		ZAssert.assertEquals(attendeeStatus, "NE", "Verify optional attendee free/busy status");
		
		// Verify optional attendee receives meeting invitation message
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +")</query>"
			+	"</SearchRequest>");
		String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify optional attendee receives meeting invitation message");
		
	}
	@Test(description = "Create appt and Add optional attendee to existing appointment from contact picker",
			groups = { "functional" })
	public void CreateMeetingBySelectAttendees_01() throws HarnessException {
		
		// Create a meeting
		// Create appointment data
		AppointmentItem appt = new AppointmentItem();
		
		String apptSubject, apptContent;
		Calendar now = this.calendarWeekDayUTC;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptOptionalAttendee = ZimbraAccount.AccountA().EmailAddress;		
		
		apptContent = ZimbraSeleniumProperties.getUniqueString();
		appt.setSubject(apptSubject);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0));
		appt.setContent(apptContent);
	
		// Compose appointment and send it to invitee
		
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zClickAt(Locators.ShowOptionalLink,"");
		apptForm.zFill(appt);
        
        apptForm.zToolbarPressButton(Button.B_OPTIONAL);
      
        DialogFindAttendees dialogFindAttendees = (DialogFindAttendees) new DialogFindAttendees(app, app.zPageCalendar);

        // Type optional attendee name in search box & perform search
        dialogFindAttendees.zType(Locators.ContactPickerSerachField, apptOptionalAttendee);
        dialogFindAttendees.zClickButton(Button.B_SEARCH);
        dialogFindAttendees.zWaitForBusyOverlay();
        
        // Add optional attendee  and resend the appointment
        dialogFindAttendees.zClick(Locators.ContactPickerFirstContact);
        dialogFindAttendees.zClickButton(Button.B_CHOOSE_CONTACT_FROM_PICKER);
        dialogFindAttendees.zWaitForBusyOverlay();
        dialogFindAttendees.zClickButton(Button.B_OK);
;
        // send the modified appt
        apptForm.zToolbarPressButton(Button.B_SEND);
		SleepUtil.sleepVeryLong();
      
     // Verify optional attendee receives meeting invitation message
		ZimbraAccount.AccountB().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +")</query>"
			+	"</SearchRequest>");
		String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify new invitation appears in the optional attendee's inbox");
 
		
		// Verify that optional attendee present in the appointment
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringContains(actual.getOptional(), apptOptionalAttendee, "optional attendee: Verify the appointment data");
		
		// Verify appointment is present in optional attendee's calendar
		AppointmentItem addedOptionalAttendee = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")");
		ZAssert.assertNotNull(addedOptionalAttendee, "Verify meeting invite is present in optional attendee's calendar");
		
		// Verify optional attendee free/busy status
		String optionalAttendeeStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptOptionalAttendee +"']", "ptst");
		ZAssert.assertEquals(optionalAttendeeStatus, "NE", "Verify optional attendee free/busy status");
		
	}
}
