package com.zimbra.qa.selenium.projects.ajax.tests.calendar.reminders.mail;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZDate;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class GetReminder extends AjaxCommonTest {

	
	public GetReminder() {
		logger.info("New "+ GetReminder.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;

	}
	
	@Bugs(ids = "69132")
	@Test(	description = "Verify reminder popup when in the mail app",
			groups = { "functional" })
	public void GetReminder_01() throws HarnessException {
		
		// Create the appointment on the server
		// Create the message data to be sent
		String subject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startLocal = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.HOUR_OF_DAY) + 1, 0, 0);
		ZDate finishLocal   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.HOUR_OF_DAY) + 2, 0, 0);
		
		// Create a meeting request from AccountA to the test account
		app.zGetActiveAccount().soapSend(
					"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv>"
				+				"<comp status='CONF' fb='B' class='PUB' transp='O' allDay='0' name='"+ subject +"'>"
				+					"<s d='"+ startLocal.toYYYYMMDDTHHMMSS() +"' tz='"+ now.getTimeZone().getID() +"'/>"
				+					"<e d='"+ finishLocal.toYYYYMMDDTHHMMSS() +"' tz='"+ now.getTimeZone().getID() +"'/>"
				+					"<or a='"+ app.zGetActiveAccount().EmailAddress + "'/>"
				+					"<alarm action='DISPLAY'>"
				+						"<trigger>"
				+							"<rel neg='1' m='60' related='START'/>"
				+						"</trigger>"
				+					"</alarm>"
				+				"</comp>"
				+			"</inv>"
				+			"<su>"+ subject + "</su>"
				+			"<mp ct='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");
		

		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);
		

		// ReminderDialog dialog = (ReminderDialog) app.zPageMain.zGetReminderDialog();
		throw new HarnessException("Implement me: check that the Reminder Dialog Shows Up");
		
	}


}
