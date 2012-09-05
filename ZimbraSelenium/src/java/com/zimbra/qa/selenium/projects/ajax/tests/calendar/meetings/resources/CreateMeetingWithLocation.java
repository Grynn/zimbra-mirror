package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.resources;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;

public class CreateMeetingWithLocation extends CalendarWorkWeekTest {

	public CreateMeetingWithLocation() {
		logger.info("New "+ CreateMeetingWithLocation.class.getCanonicalName());
		
	}
	
	@Bugs(ids = "69132")
	@Test(description = "Create simple meeting with location resource",
			groups = { "sanity" })
	public void CreateMeetingWithSingleLocation_01() throws HarnessException {
		
		
		//-- Data Setup
		
		
		// Create appointment data
		AppointmentItem appt = new AppointmentItem();
		ZimbraResource location = new ZimbraResource(ZimbraResource.Type.LOCATION);
		
		String apptSubject, apptAttendee1, apptLocation1, apptContent;
		Calendar now = this.calendarWeekDayUTC;
		apptSubject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		apptLocation1 = location.EmailAddress;
		apptContent = "content" + ZimbraSeleniumProperties.getUniqueString();
		
		appt.setSubject(apptSubject);
		appt.setAttendees(apptAttendee1);
		appt.setLocation(apptLocation1);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 13, 0, 0));
		appt.setContent(apptContent);
	
		
		
		//-- GUI Actions
		
		
		// Compose appointment and send it to invitee
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		apptForm.zSubmit();
		
		
		// Because the response from the resource may
		// take some time, make sure the response is
		// received in the inbox before proceeding
		for (int i = 0; i < 10; i++) {
			
			app.zGetActiveAccount().soapSend(
						"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
					+		"<query>in:inbox subject:(aa"+ apptSubject +")</query>"
					+	"</SearchRequest>");
			
			String id = app.zGetActiveAccount().soapSelectValue("//mail:m", "id");
			if ( id != null ) {
				// found it
				break;
			}
			
			SleepUtil.sleep(1000);
		}
		
		
		
		//-- Verification
		
		
		// Verify appointment exists on the server
		SleepUtil.sleepSmall(); //test fails without sleep
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")", appt.getStartTime().addDays(-7), appt.getEndTime().addDays(7));
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
		ZAssert.assertEquals(actual.getAttendees(), appt.getAttendees(), "Attendees: Verify the appointment data");
		ZAssert.assertEquals(actual.getLocation(), appt.getLocation(), "Location: Verify the appointment data");
		ZAssert.assertEquals(actual.getContent(), appt.getContent(), "Content: Verify the appointment data");
		
		// Verify location free/busy status shows as psts=AC	
		String locationStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptLocation1 +"']", "ptst");
		ZAssert.assertEquals(locationStatus, "AC", "Verify that the location status shows as 'ACCEPTED'");
		
        
	}
	
	@Bugs(ids = "69132")
	@Test(description = "Create simple meeting with two location resource",
			groups = { "functional" })
	public void CreateMeetingWithMultiLocation_02() throws HarnessException {
		
		// Create appointment data
		AppointmentItem appt = new AppointmentItem();
		ZimbraResource location1 = new ZimbraResource(ZimbraResource.Type.LOCATION);
		ZimbraResource location2 = new ZimbraResource(ZimbraResource.Type.LOCATION);		
		
		String apptSubject, apptAttendee1, apptLocation, apptContent;
		Calendar now = this.calendarWeekDayUTC;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		apptLocation = location1.EmailAddress + " " + location2.EmailAddress;
		apptContent = ZimbraSeleniumProperties.getUniqueString();
		
		appt.setSubject(apptSubject);
		appt.setAttendees(apptAttendee1);
		appt.setLocation(apptLocation);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 13, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0));
		appt.setContent(apptContent);
	
		// Compose appointment and send it to invitee
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		apptForm.zSubmit();
		
		// Because the response from the resource may
		// take some time, make sure the response is
		// received in the inbox before proceeding
		for (int i = 0; i < 10; i++) {
			
			app.zGetActiveAccount().soapSend(
						"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
					+		"<query>in:inbox subject:(aa"+ apptSubject +") from:("+ location1.EmailAddress +")</query>"
					+	"</SearchRequest>");
			
			String id1 = app.zGetActiveAccount().soapSelectValue("//mail:m", "id");

			app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+		"<query>in:inbox subject:(aa"+ apptSubject +") from:("+ location2.EmailAddress +")</query>"
				+	"</SearchRequest>");
		
			String id2 = app.zGetActiveAccount().soapSelectValue("//mail:m", "id");

			if ( (id1 != null) && (id2 != null) ) {
				// found it
				break;
			}
			
			SleepUtil.sleep(1000);
		}

		// Verify appointment exists on the server
		SleepUtil.sleepSmall(); //test fails without sleep
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")", appt.getStartTime().addDays(-7), appt.getEndTime().addDays(7));
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getLocation().replace(";", ""), appt.getLocation(), "Location: Verify the appointment data");
		
		// Verify both location free/busy status
		String locationStatus1 = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ location1.EmailAddress +"']", "ptst");
		String locationStatus2 = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ location2.EmailAddress +"']", "ptst");
		ZAssert.assertEquals(locationStatus1, "AC", "Verify that the location1 status shows as 'ACCEPTED'");
		ZAssert.assertEquals(locationStatus2, "AC", "Verify that the location2 status shows as 'ACCEPTED'");
		
		
	}
	
	@Test(description = "Create simple meeting with floating location resource",
			groups = { "functional" })
	public void CreateMeetingWithFloatingLocation_03() throws HarnessException {
		
		// Create appointment data
		AppointmentItem appt = new AppointmentItem();
		
		String apptSubject, apptAttendee1, apptLocation, apptContent;
		Calendar now = this.calendarWeekDayUTC;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		apptLocation = ZimbraSeleniumProperties.getUniqueString() + " " + ZimbraSeleniumProperties.getUniqueString();
		apptContent = ZimbraSeleniumProperties.getUniqueString();
		
		appt.setSubject(apptSubject);
		appt.setAttendees(apptAttendee1);
		appt.setLocation(apptLocation);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0));
		appt.setContent(apptContent);
	
		// Compose appointment and send it to invitee
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		apptForm.zSubmit();
		
		// Verify appointment exists on the server
		SleepUtil.sleepSmall(); //test fails without sleep
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")", appt.getStartTime().addDays(-7), appt.getEndTime().addDays(7));
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		
	}

}
