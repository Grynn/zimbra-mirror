/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.workweek.allday;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;

public class CreateAppointment extends CalendarWorkWeekTest {
	
	public CreateAppointment() {
		logger.info("New "+ CreateAppointment.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}

	@Bugs(ids = "69132")
	@Test(	description = "Create simple all day appointment",
			groups = { "smoke" }
	)
	public void CreateAllDayAppointment_01() throws HarnessException {
		   
		// Create appointment
		String apptSubject;
		apptSubject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		AppointmentItem appt = new AppointmentItem();
		
		appt.setSubject(apptSubject);
		appt.setStartTime(new ZDate(this.calendarWeekDayUTC));
		appt.setEndTime(new ZDate(this.calendarWeekDayUTC));
		appt.setContent("content" + ZimbraSeleniumProperties.getUniqueString());
		appt.setAttendees(ZimbraAccount.AccountA().EmailAddress);
		appt.setIsAllDay(true);
	
		// Open the new mail form
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(apptForm, "Verify the new form opened");
		
		// Fill the data and submit it
		apptForm.zFill(appt);
		apptForm.zSubmit();
			
		// Verify the new appointment exists on the server
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")");
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
		ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetAppointmentResponse//mail:comp", "allDay", "1"), true, "");
		
		// Verify in UI

		boolean found = false;
		for (AppointmentItem a : app.zPageCalendar.zListGetAppointments()) {
			if ( apptSubject.equals(a.getSubject()) ) {
				found = true;
				break;
			}
		}
		
		ZAssert.assertTrue(found, "Verify the new apointment appears");

	}
	
}
