package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.create;

import java.util.Calendar;
import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogFindAttendees;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogFindAttendees.Locators;;

public class CreateMeetingBySelectOptionalAddresses extends CalendarWorkWeekTest {	
	
	public CreateMeetingBySelectOptionalAddresses() {
		logger.info("New "+ CreateMeetingBySelectOptionalAddresses.class.getCanonicalName());
		super.startingPage =  app.zPageCalendar;
	}
	
	@Test(description = "Compose appointment by selecting optional attendees using 'Select Addresses' dialog and send the appointment",
			groups = { "functional" })
	public void CreateMeetingBySelectOptionalAttendees_01() throws HarnessException {
		
		// Create appointment data
		AppointmentItem appt = new AppointmentItem();
		
		String apptSubject, apptOptionalAttendee, apptContent;
		Calendar now = this.calendarWeekDayUTC;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptOptionalAttendee = ZimbraAccount.AccountA().EmailAddress;	
		
		apptContent = ZimbraSeleniumProperties.getUniqueString();
		appt.setSubject(apptSubject);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 17, 0, 0));
		appt.setContent(apptContent);
	
		// Compose appointment and send it to invitee
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zClickAt(Locators.ShowOptionalLink,"");
		apptForm.zFill(appt);
        apptForm.zToolbarPressButton(Button.B_OPTIONAL);
        DialogFindAttendees dialogFindAttendees = (DialogFindAttendees) new DialogFindAttendees(app, app.zPageCalendar);
        
        // Type optional attendee name in search box & perform search
        AppointmentItem apptSearchForm1 = new AppointmentItem();
        apptSearchForm1.setAttendeeName(apptOptionalAttendee);
        dialogFindAttendees.zFill(apptSearchForm1);
     
        // Choose the contact and select it
        dialogFindAttendees.zClickButton(Button.B_SEARCH);
        SleepUtil.sleepSmall(); 
        dialogFindAttendees.zClickButton(Button.B_SELECT_FIRST_CONTACT);
        dialogFindAttendees.zClickButton(Button.B_CHOOSE_CONTACT_FROM_PICKER);
        dialogFindAttendees.zClickButton(Button.B_OK);
        apptForm.zToolbarPressButton(Button.B_SEND);
        
        SleepUtil.sleepVeryLong(); // Without this the test fails
        
        // Verify new invitation appears in the optional attendee's inbox
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +")</query>"
			+	"</SearchRequest>");
		String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify new invitation appears in the optional attendee's inbox");
		
		// Verify that optional attendee present in the appointment
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringContains(actual.getOptional(), apptOptionalAttendee, "optional Attendees: Verify the appointment data");
		
		// Verify appointment is present in optional attendee's calendar
		AppointmentItem addeddAttendee = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")");
		ZAssert.assertNotNull(addeddAttendee, "Verify meeting invite is present in optional attendee's calendar");
		
	}
}
