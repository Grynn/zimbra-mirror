package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.assistant;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogAssistant;


public class CreateAppointment extends AjaxCommonTest {
	
	public CreateAppointment() {
		logger.info("New "+ CreateAppointment.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;


	}
	
	@Test(	description = "Create a basic appointment using the Zimbra Assistant",
			groups = { "deprecated" })
	public void CreateAppointment_01() throws HarnessException {

		Calendar start = Calendar.getInstance();
		start.add(Calendar.DATE, -7);
		Calendar finish = Calendar.getInstance();
		finish.add(Calendar.DATE, +7);
		
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String location = "location" + ZimbraSeleniumProperties.getUniqueString();
		String notes = "notes" + ZimbraSeleniumProperties.getUniqueString();
		String command = "appointment \"" + subject + "\" ["+ location +"] ("+ notes +")";

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		DialogAssistant assistant = (DialogAssistant)app.zPageCalendar.zKeyboardShortcut(Shortcut.S_ASSISTANT);
		assistant.zEnterCommand(command);
		assistant.zClickButton(Button.B_OK);
		
		app.zGetActiveAccount().soapSend(
						"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ start.getTimeInMillis() +"' calExpandInstEnd='"+ finish.getTimeInMillis() +"'>"
					+		"<query>subject:("+ subject +")</query>"
					+	"</SearchRequest>");
		
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:appt");
		ZAssert.assertGreaterThan(nodes.length, 0, "Verify the appointment was created");

		String aSubject = app.zGetActiveAccount().soapSelectValue("//mail:appt", "name");
		String aFragment = app.zGetActiveAccount().soapSelectValue("//mail:fr", null);

		ZAssert.assertEquals(aSubject, subject, "Verify the subject matches");
		ZAssert.assertEquals(aFragment, notes, "Verify the notes matches");

	}

	@Bugs(ids = "53005")
	@Test(	description = "Verify location is saved when using assistant",
			groups = { "deprecated" })
	public void CreateAppointment_02() throws HarnessException {

		Calendar start = Calendar.getInstance();
		start.add(Calendar.DATE, -7);
		Calendar finish = Calendar.getInstance();
		finish.add(Calendar.DATE, +7);
		
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String location = "location" + ZimbraSeleniumProperties.getUniqueString();
		String notes = "notes" + ZimbraSeleniumProperties.getUniqueString();
		String command = "appointment \"" + subject + "\" ["+ location +"] ("+ notes +")";

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		DialogAssistant assistant = (DialogAssistant)app.zPageCalendar.zKeyboardShortcut(Shortcut.S_ASSISTANT);
		assistant.zEnterCommand(command);
		assistant.zClickButton(Button.B_OK);
		
		app.zGetActiveAccount().soapSend(
						"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ start.getTimeInMillis() +"' calExpandInstEnd='"+ finish.getTimeInMillis() +"'>"
					+		"<query>subject:("+ subject +")</query>"
					+	"</SearchRequest>");
		
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:appt");
		ZAssert.assertGreaterThan(nodes.length, 0, "Verify the appointment was created");

		String aLocation = app.zGetActiveAccount().soapSelectValue("//mail:appt", "loc");
		ZAssert.assertEquals(aLocation, location, "Verify the location matches");

	}


}
