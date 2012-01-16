package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.month.singleday;

import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class GetAppointment extends AjaxCommonTest {

	
	@SuppressWarnings("serial")
	public GetAppointment() {
		logger.info("New "+ GetAppointment.class.getCanonicalName());
		
		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
		    put("zimbraPrefCalendarInitialView", "month");
		}};


	}
	
	@Bugs(ids = "69132")
	@Test(	description = "View a basic appointment in the month view",
			groups = { "functional" })
	public void GetAppointment_01() throws HarnessException {
		
		// Create the appointment on the server
		// Create the message data to be sent
		String subject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		
		AppointmentItem.createAppointmentSingleDay(
				app.zGetActiveAccount(),
				Calendar.getInstance(),
				120,
				null,
				subject,
				"content" + ZimbraSeleniumProperties.getUniqueString(),
				"location" + ZimbraSeleniumProperties.getUniqueString(),
				null);

		
		// Refresh the calendar
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		// Get the list of appointments in the current view
		List<AppointmentItem> items = app.zPageCalendar.zListGetAppointments();
		ZAssert.assertNotNull(items, "Get the list of appointments");
		
		// Verify the appointment is in the view
		AppointmentItem found = null;
		for(AppointmentItem item : items) {
			if ( item.getSubject().contains(subject) ) {
				found = item;
				break;
			}
		}
		
		ZAssert.assertNotNull(found, "Verify the new appointment appears in the view");
	    
	}


}
