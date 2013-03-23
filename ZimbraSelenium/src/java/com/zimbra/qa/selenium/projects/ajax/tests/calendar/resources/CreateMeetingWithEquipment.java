/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.resources;

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
		super.startingPage = app.zPageCalendar;
	}
	
	@Bugs(ids = "69132")
	@Test(description = "Create simple meeting with equipment",
			groups = { "smoke" })
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
		SleepUtil.sleepVeryLong(); // test fails while checking free/busy status, waitForPostqueue is not sufficient here
        // Tried sleepLong() as well but although fails so using sleepVeryLong()
		
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
		ZAssert.assertEquals(actual.getEquipment(), appt.getEquipment(), "Equipment: Verify the appointment data");
		ZAssert.assertEquals(actual.getContent(), appt.getContent(), "Content: Verify the appointment data");
		
		// Verify equipment free/busy status shows as psts=AC	
		String equipmentStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptEquipment1 +"']", "ptst");
		ZAssert.assertEquals(equipmentStatus, "AC", "Verify that the equipment status shows as 'ACCEPTED'");
		
	}

}
