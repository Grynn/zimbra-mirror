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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.workweek.singleday;

import java.util.Calendar;
import org.testng.annotations.Test;
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

	
	@Test(	description = "Create a basic appointment without an attendee",
			groups = { "sanity" }
	)
	public void CreateAppointment_01() throws HarnessException {
		
		// Create appointment
		AppointmentItem appt = new AppointmentItem();
		Calendar now = this.calendarWeekDayUTC;
		appt.setSubject("appointment" + ZimbraSeleniumProperties.getUniqueString());
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0));
		appt.setContent("content" + ZimbraSeleniumProperties.getUniqueString());
		
		// Open the new mail form
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(apptForm, "Verify the new form opened");

		// Fill out the form with the data
		apptForm.zFill(appt);

		// Send the message
		apptForm.zSubmit();
			
		// Verify the new appointment exists on the server
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")", appt.getStartTime().addDays(-7), appt.getEndTime().addDays(7));
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
	}
	
	@Test(	description = "Create appointment with all the fields and verify it",
			groups = { "functional" }
	)
	public void CreateAppointment_02() throws HarnessException {
		
		// Create appointment data
		ZimbraResource location = new ZimbraResource(ZimbraResource.Type.LOCATION);
		ZimbraResource equipment = new ZimbraResource(ZimbraResource.Type.EQUIPMENT);
		
		AppointmentItem appt = new AppointmentItem();
		
		String apptSubject, apptAttendee1, apptOptional1, apptLocation1, apptEquipment1, apptContent;
		Calendar now = this.calendarWeekDayUTC;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		apptOptional1 = ZimbraAccount.AccountB().EmailAddress;
		apptLocation1 = location.EmailAddress;
		apptEquipment1 = equipment.EmailAddress;
		apptContent = ZimbraSeleniumProperties.getUniqueString();
		
		appt.setSubject(apptSubject);
		appt.setAttendees(apptAttendee1);
		appt.setOptional(apptOptional1);
		appt.setLocation(apptLocation1);
		appt.setEquipment(apptEquipment1);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0));
		appt.setContent(apptContent);
	
		// Compose appointment and send it to invitee
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		apptForm.zSubmit();
			
		// Verify appointment exists on the server
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")", appt.getStartTime().addDays(-7), appt.getEndTime().addDays(7));
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
		ZAssert.assertEquals(actual.getAttendees(), appt.getAttendees(), "Attendees: Verify the appointment data");
		ZAssert.assertEquals(actual.getOptional(), appt.getOptional(), "Optional: Verify the appointment data");
		ZAssert.assertEquals(actual.getLocation(), appt.getLocation(), "Location: Verify the appointment data");
		ZAssert.assertEquals(actual.getEquipment(), appt.getEquipment(), "Equipment: Verify the appointment data");
		ZAssert.assertEquals(actual.getContent(), appt.getContent(), "Content: Verify the appointment data");
	}
	
	@Test(	description = "Create private appointment",
			groups = { "functional" }
	)
	public void CreatePrivateAppointment_03() throws HarnessException {
		
		// Create appointment
		String apptSubject;
		Calendar now = this.calendarWeekDayUTC;
		apptSubject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		AppointmentItem appt = new AppointmentItem();
		
		appt.setSubject(apptSubject);
		appt.setContent("content" + ZimbraSeleniumProperties.getUniqueString());
		appt.setAttendees(ZimbraAccount.AccountA().EmailAddress);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0));
		appt.setIsPrivate(true);
	
		// Open the new mail form
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(apptForm, "Verify the new form opened");

		// Fill the data and submit it
		apptForm.zFill(appt);
		apptForm.zSubmit();
			
		// Verify private appointment exists on the server
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")");
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
		ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetAppointmentResponse//mail:comp", "class", "PRI"), true, "");
	}
	
}
