package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.month.allday;

import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogConfirmDelete;


public class DeleteAppointment extends AjaxCommonTest {

	
	public DeleteAppointment() {
		logger.info("New "+ DeleteAppointment.class.getCanonicalName());
		
		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 3028486541122343959L;

		{
		    put("zimbraPrefCalendarInitialView", "month");
		}};


	}
	
	@Test(	description = "Delete an all-day appointment in the month view",
			groups = { "functional" })
	public void DeleteAppointment_01() throws HarnessException {
		
		// Create an appointment on the server
		String subject = "appointment" + ZimbraSeleniumProperties.getUniqueString();

		AppointmentItem.createAppointmentAllDay(
				app.zGetActiveAccount(),
				Calendar.getInstance(),
				1,
				subject,
				"content" + ZimbraSeleniumProperties.getUniqueString(),
				"location" + ZimbraSeleniumProperties.getUniqueString(),
				null);
		
		

		// Refresh the calendar
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		// Select the appointment
		app.zPageCalendar.zListItem(Action.A_LEFTCLICK, subject);
		
		// Click the "delete" button
		DialogConfirmDelete dialog = (DialogConfirmDelete)app.zPageCalendar.zToolbarPressButton(Button.B_DELETE);

		// Send the notification immediately
		dialog.zClickButton(Button.B_SEND_CANCELLATION);

		
		
		// Verify that the appointment is in the trash now
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Trash);
		AppointmentItem deleted = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +") is:anywhere");
		ZAssert.assertNotNull(deleted, "Verify the deleted appointment exists");
		ZAssert.assertEquals(deleted.getFolder(), trash.getId(), "Verify the deleted appointment exists");

	}


}
