package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.freebusy.singleday;

import java.util.*;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class GetAppointment extends AjaxCommonTest {
	
	public GetAppointment() {
		logger.info("New "+ GetAppointment.class.getCanonicalName());
		
		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with free busy view
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -637186860416861680L;
		{
		    put("zimbraFeatureFreeBusyViewEnabled", "TRUE");
		}};


	}
	
	@Bugs(ids = "69132")
	@Test(	description = "View a basic appointment in the free busy view",
			groups = { "functional" })
	public void GetAppointment_01() throws HarnessException {
		
		// Create the appointment on the server
		// Create the message data to be sent
		String subject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		// EST timezone string
		String tz = ZTimeZone.TimeZoneEST.getID();

		// Create an appointment
		app.zGetActiveAccount().soapSend(
					"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv>"
				+				"<comp status='CONF' fb='B' class='PUB' transp='O' allDay='0' name='"+ subject +"' >"
				+					"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<or a='"+ app.zGetActiveAccount().EmailAddress + "'/>"
				+				"</comp>"
				+			"</inv>"
				+			"<su>"+ subject + "</su>"
				+			"<mp ct='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");
		
		
		//-- GUI Actions

		// Go to F/B view
		SleepUtil.sleepMedium();
		app.zPageCalendar.zToolbarPressPulldown(Button.B_LISTVIEW, Button.O_LISTVIEW_FREEBUSY);

		//-- Verification
		
		// Verify the appointment appears on the page
		SleepUtil.sleepMedium(); //test fails without sleep because calendar view rendering takes time 
		ZAssert.assertTrue(app.zPageCalendar.zGetApptLocatorFreeBusyView(app.zGetActiveAccount().EmailAddress, subject), "Verify attendee free/busy row exists");
	    
	}
}
