package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.workweek.allday;

import java.util.*;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class GetAppointment extends AjaxCommonTest {

	public GetAppointment() {
		logger.info("New "+ GetAppointment.class.getCanonicalName());
		
		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;

	}
	
	@Bugs(ids = "69132")
	@Test(	description = "View a basic all-day appointment in the work week view",
			groups = { "smoke" })
	public void GetAllDayAppointment_01() throws HarnessException {
		
		// Create the appointment on the server
		String apptSubject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		String apptLocation = "location" + ZimbraSeleniumProperties.getUniqueString();
		String apptBody = "content" + ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		// EST timezone string
		String tz = ZTimeZone.TimeZoneEST.getID();

		// Create a meeting request from AccountA to the test account
		ZimbraAccount.AccountA().soapSend(
					"<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<inv>" +
								"<comp status='CONF' fb='B' class='PUB' transp='O' allDay='1' name='"+ apptSubject +"' loc='"+ apptLocation +"'>" +
									"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
									"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
									"<at role='REQ' ptst='NE' rsvp='1' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
									"<or a='"+ ZimbraAccount.AccountA().EmailAddress + "'/>" +
								"</comp>" +
							"</inv>" +
							"<e a='"+ app.zGetActiveAccount().EmailAddress +"' t='t'/>" +
							"<su>"+ apptSubject + "</su>" +
							"<mp ct='text/plain'>" +
							"<content>"+ apptBody +"</content>" +
							"</mp>" +
						"</m>" +
					"</CreateAppointmentRequest>");
		
		AppointmentItem appt = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")", startUTC.addDays(-7), endUTC.addDays(7));
		ZAssert.assertNotNull(appt, "Verify the new appointment is created");

		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		//wait for the appointment displayed in the view
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetReadOnlyAllDayApptLocator(apptSubject)), true, "Verify all-day appointment is deleted");
	    
	}
}
