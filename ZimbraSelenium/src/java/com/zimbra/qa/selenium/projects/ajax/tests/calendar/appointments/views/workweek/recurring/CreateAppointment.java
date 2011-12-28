package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.workweek.recurring;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;

public class CreateAppointment extends AjaxCommonTest {


	public CreateAppointment() {
		logger.info("New "+ CreateAppointment.class.getCanonicalName());

		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;


	}

	
	@Test(	description = "Create a basic appointment without an attendee",
			groups = { "implement" })  // sanity
	public void CreateAppointment_01() throws HarnessException {
		
		// Create the message data to be sent
		AppointmentItem appt = new AppointmentItem();
		appt.setSubject("appointment" + ZimbraSeleniumProperties.getUniqueString());
		appt.setContent("content" + ZimbraSeleniumProperties.getUniqueString());
		appt.setStartTime(new ZDate(2014, 12, 25, 12, 0, 0));
		appt.setEndTime(new ZDate(2014, 12, 25, 14, 0, 0));


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

	

}
