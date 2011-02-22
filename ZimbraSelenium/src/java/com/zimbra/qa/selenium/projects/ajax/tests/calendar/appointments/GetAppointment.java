package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class GetAppointment extends AjaxCommonTest {

	int pollIntervalSeconds = 60;
	
	public GetAppointment() {
		logger.info("New "+ GetAppointment.class.getCanonicalName());
		
		// All tests start at the Calendar page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;


	}
	
	@Test(	description = "View an appointment",
			groups = { "smoke" })
	public void GetAppointment_01() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		String location = "location" + ZimbraSeleniumProperties.getUniqueString();
		String content = "content" + ZimbraSeleniumProperties.getUniqueString();
		ZDate startUTC = new ZDate(2014, 12, 25, 12, 0, 0);
		ZDate endUTC   = new ZDate(2014, 12, 25, 14, 0, 0);
		
		// Create an appointment
		app.zGetActiveAccount().soapSend(
					"<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<inv>" +
								"<comp status='CONF' fb='B' class='PUB' transp='O' allDay='0' name='"+ subject +"' loc='"+ location +"'>" +
									"<s d='"+ startUTC.toYYYYMMDDTHHMMSSZ() +"'/>" +
									"<e d='"+ endUTC.toYYYYMMDDTHHMMSSZ() +"'/>" +
									"<or a='"+ app.zGetActiveAccount().EmailAddress + "'/>" +
								"</comp>" +
							"</inv>" +
							"<su>"+ subject + "</su>" +
							"<mp ct='text/plain'>" +
							"<content>"+ content +"</content>" +
							"</mp>" +
						"</m>" +
					"</CreateAppointmentRequest>");
		

		AppointmentItem appt = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), subject, startUTC.addDays(-10), endUTC.addDays(+10));
		ZAssert.assertNotNull(appt, "Verify the appointment is in the mailbox");

		ZAssert.assertEquals(subject, appt.getSubject(), "Verify the appointment subjects match");
		ZAssert.assertEquals(location, appt.getLocation(), "Verify the appointment locations match");
		ZAssert.assertEquals(content, appt.getContent(), "Verify the appointment contents match");

		ZAssert.assertEquals(startUTC, appt.getStartTime(), "Verify the appointment start times match");
		ZAssert.assertEquals(endUTC, appt.getEndTime(), "Verify the appointment end times match");
		
	}



}
