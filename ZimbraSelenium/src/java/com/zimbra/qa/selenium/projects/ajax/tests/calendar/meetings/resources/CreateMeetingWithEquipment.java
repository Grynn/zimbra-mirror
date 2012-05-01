package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.resources;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;

public class CreateMeetingWithEquipment extends CalendarWorkWeekTest {

	public CreateMeetingWithEquipment() {
		logger.info("New "+ CreateMeetingWithEquipment.class.getCanonicalName());
		
	}
	
	@Bugs(ids = "69132")
	@Test(description = "Create simple meeting with equipment",
			groups = { "sanity" })
	public void CreateMeetingWithEquipment_01() throws HarnessException {
		
		
		//-- Data Setup
		
		
		// Create appointment data
		AppointmentItem appt = new AppointmentItem();
		Calendar now = this.calendarWeekDayUTC;
		ZimbraResource equipment1 = new ZimbraResource(ZimbraResource.Type.EQUIPMENT);
		
		String apptSubject, apptAttendee1, apptEquipment1, apptContent;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		apptEquipment1 = equipment1.EmailAddress;
		apptContent = ZimbraSeleniumProperties.getUniqueString();
		
		appt.setSubject(apptSubject);
		appt.setAttendees(apptAttendee1);
		appt.setEquipment(apptEquipment1);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 16, 0, 0));
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
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")", appt.getStartTime().addDays(-7), appt.getEndTime().addDays(7));
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
		ZAssert.assertEquals(actual.getAttendees(), appt.getAttendees(), "Attendees: Verify the appointment data");
		ZAssert.assertEquals(actual.getEquipment(), appt.getEquipment(), "Equipment: Verify the appointment data");
		ZAssert.assertEquals(actual.getContent(), appt.getContent(), "Content: Verify the appointment data");
		
		// Verify equipment free/busy status shows as psts=AC	
		String equipmentStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptEquipment1 +"']", "ptst");
		ZAssert.assertEquals(equipmentStatus, "AC", "Verify that the equipment status shows as 'ACCEPTED'");
		
	}

}
