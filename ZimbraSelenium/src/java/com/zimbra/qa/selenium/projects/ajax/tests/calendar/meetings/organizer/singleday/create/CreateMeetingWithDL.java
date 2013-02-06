package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.create;


import java.util.Calendar;
import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogFindAttendees;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogFindAttendees.Locators;;

public class CreateMeetingWithDL extends CalendarWorkWeekTest {	
	
	public CreateMeetingWithDL() {
		logger.info("New "+ CreateMeetingWithDL.class.getCanonicalName());
		super.startingPage =  app.zPageCalendar;
	    super.startingAccountPreferences = null;
	}
	

	@Test(description = " Create appointment with DL by choosing DL address from 'Select Addresses' dialog",
			groups = { "functional" })
	public void CreateMeetingWithDL_01() throws HarnessException {
		
		// Create a meeting
		AppointmentItem appt = new AppointmentItem();
	
		String apptSubject, apptContent;
		Calendar now = this.calendarWeekDayUTC;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		
		// Create a DL
		ZimbraAccount account1 = (new ZimbraAccount()).provision().authenticate();
		ZimbraAccount account2 = (new ZimbraAccount()).provision().authenticate();
		ZimbraDistributionList distribution = (new ZimbraDistributionList()).provision();
		distribution.addMember(account1);
		distribution.addMember(account2);
		// Create appointment data
		apptContent = ZimbraSeleniumProperties.getUniqueString();
		appt.setSubject(apptSubject);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0));
		appt.setContent(apptContent);
	
		// Compose appointment by searching DL in Find Addresses dialog and send it to DL
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
        apptForm.zToolbarPressButton(Button.B_TO);
        
        DialogFindAttendees dialogFindAttendees = (DialogFindAttendees) new DialogFindAttendees(app, app.zPageCalendar);
        dialogFindAttendees.zType(Locators.ContactPickerSerachField, distribution.EmailAddress);
        dialogFindAttendees.zClickButton(Button.B_SEARCH);
        dialogFindAttendees.zWaitForBusyOverlay();
        
        // Choose the contact and choose it
        dialogFindAttendees.zClick(Locators.ContactPickerFirstContact);
        dialogFindAttendees.zClickButton(Button.B_CHOOSE_CONTACT_FROM_PICKER);
        dialogFindAttendees.zClickButton(Button.B_OK);
        apptForm.zToolbarPressButton(Button.B_SEND);
		apptForm.zSubmit();

		
        // Verify attendee1 of DL receives meeting invitation message
		account1.soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +")</query>"
			+	"</SearchRequest>");
		
		String idA = account1.soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(idA, "Verify new invitation appears in the inbox for members of DL ");
		
		// Verify attendee2 of DL receives meeting invitation message
		account2.soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +")</query>"
			+	"</SearchRequest>");
		
		String idB = account2.soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(idB, "Verify new invitation appears in the inbox for members of DL ");
	
		// Verify that attendee1 from DL is present in the appointment
        AppointmentItem actualA = AppointmentItem.importFromSOAP(account1, "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actualA.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringContains(actualA.getAttendees(), distribution.EmailAddress, "Attendees: Verify the appointment data");
		
		// Verify that attendee2 from DL is present in the appointment
        AppointmentItem actualB = AppointmentItem.importFromSOAP(account2, "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actualB.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringContains(actualB.getAttendees(), distribution.EmailAddress, "Attendees: Verify the appointment data");
		
		// Logout from organizer and accept meeting from 1 of the DL member
		app.zPageMain.zLogout();
		app.zPageLogin.zLogin(account1);
		
		// Accept the invite from 1 of the DL member
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, apptSubject);
		display.zPressButton(Button.B_ACCEPT);
		
		// Navigate to Calendar tab
		AbsTab startingPage = app.zPageCalendar;
		startingPage.zNavigateTo();
		SleepUtil.sleepMedium();
		
		// Verify after DL member accepts the invite , Attendee name is added at 'Attendee' header when he accepts the meeting
		String msgHeader = Locators.MessageHeader + account1.EmailAddress +"')" ;
		app.zPageCalendar.zListItem(Action.A_DOUBLECLICK , apptSubject);
		ZAssert.assertTrue(app.zPageCalendar.sIsElementPresent(msgHeader), "Verify Attendee name is added at 'Attendee' header when he accepts the meeting");
		
		// Verify appointment is present in attendee1 from DL 's calendar
		AppointmentItem addeddAttendeeA = AppointmentItem.importFromSOAP(account1, "subject:("+ apptSubject +")");
		ZAssert.assertNotNull(addeddAttendeeA, "Verify meeting invite is present in attendee1's calendar");
		
		// Verify appointment is present in attendee2 from DL 's calendar
		AppointmentItem addeddAttendeeB = AppointmentItem.importFromSOAP(account2, "subject:("+ apptSubject +")");
		ZAssert.assertNotNull(addeddAttendeeB, "Verify meeting invite is present in attendee2's calendar");
		
		// Verify DL's free/busy status
		String attendee1Status3 = account1.soapSelectValue("//mail:at[@a='"+ distribution.EmailAddress +"']", "ptst");
		ZAssert.assertEquals(attendee1Status3, "NE", "Verify DL's free/busy status");
		
	
	}
}
