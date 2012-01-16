package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.list;

import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZDate;
import com.zimbra.qa.selenium.framework.util.ZTimeZone;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogConfirm;


public class DeleteAppointment extends AjaxCommonTest {


	@SuppressWarnings("serial")
	public DeleteAppointment() {
		logger.info("New "+ DeleteAppointment.class.getCanonicalName());

		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
			put("zimbraPrefCalendarInitialView", "list");
		}};


	}

	@Bugs(ids = "69132")
	@Test(	description = "Delete an appointment in the list view - Toolbar Delete",
			groups = { "functional" })
	public void DeleteAppointment_01() throws HarnessException {

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


		// Refresh the calendar
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		// Select the item
		app.zPageCalendar.zListItem(Action.A_LEFTCLICK, subject);

		// Click delete
		DialogConfirm dialog = (DialogConfirm)app.zPageCalendar.zToolbarPressButton(Button.B_DELETE);

		// Click Yes on the confirmation
		dialog.zClickButton(Button.B_YES);


		// Verify the appointment is gone
		AppointmentItem found = null;
		List<AppointmentItem> appts = app.zPageCalendar.zListGetAppointments();
		for (AppointmentItem item : appts) {
			if ( subject.contains(item.getGSubject()) ) {
				found = item;
				break;
			}
		}

		ZAssert.assertNull(found, "Verify the appointment is no longer in the list");


	}



	@Bugs(ids = "69132")
	@Test(	description = "Delete an appt using checkbox and toolbar delete button",
			groups = { "functional" })
	public void DeleteAppointment_02() throws HarnessException {

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



		// Refresh the calendar
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		// Check the item
		app.zPageCalendar.zListItem(Action.A_CHECKBOX, subject);

		// Click delete
		DialogConfirm dialog = (DialogConfirm)app.zPageCalendar.zToolbarPressButton(Button.B_DELETE);

		// Click Yes on the confirmation
		dialog.zClickButton(Button.B_YES);


		// Verify the appointment is gone
		AppointmentItem found = null;
		List<AppointmentItem> appts = app.zPageCalendar.zListGetAppointments();
		for (AppointmentItem item : appts) {
			if ( subject.contains(item.getGSubject()) ) {
				found = item;
				break;
			}
		}

		ZAssert.assertNull(found, "Verify the appointment is no longer in the list");


	}


	@DataProvider(name = "DataProviderDeleteKeys")
	public Object[][] DataProviderDeleteKeys() {
		return new Object[][] {
				new Object[] { "VK_DELETE", KeyEvent.VK_DELETE },
				new Object[] { "VK_BACK_SPACE", KeyEvent.VK_BACK_SPACE },
		};
	}

	@Bugs(ids = "69132")
	@Test(	description = "Delete a appt by selecting and typing 'delete' keyboard",
			groups = { "functional" },
			dataProvider = "DataProviderDeleteKeys")
	public void DeleteAppointment_03(String name, int keyEvent) throws HarnessException {


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



		// Refresh the calendar
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		// Check the item
		app.zPageCalendar.zListItem(Action.A_LEFTCLICK, subject);

		// Click delete
		logger.info("Typing shortcut key "+ name + " KeyEvent: "+ keyEvent);
		DialogConfirm dialog = (DialogConfirm)app.zPageCalendar.zKeyboardKeyEvent(keyEvent);

		// Click Yes on the confirmation
		dialog.zClickButton(Button.B_YES);


		// Verify the appointment is gone
		AppointmentItem found = null;
		List<AppointmentItem> appts = app.zPageCalendar.zListGetAppointments();
		for (AppointmentItem item : appts) {
			if ( subject.contains(item.getGSubject()) ) {
				found = item;
				break;
			}
		}

		ZAssert.assertNull(found, "Verify the appointment is no longer in the list");

	}

	@Bugs(ids = "69132")
	@Test(	description = "Delete a appt by selecting and typing '.t' shortcut",
			groups = { "functional" } )
	public void DeleteAppointment_04() throws HarnessException {

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



		// Refresh the calendar
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		// Check the item
		app.zPageCalendar.zListItem(Action.A_LEFTCLICK, subject);

		// Click delete
		DialogConfirm dialog = (DialogConfirm)app.zPageCalendar.zKeyboardShortcut(Shortcut.S_MAIL_MOVETOTRASH);

		// Click Yes on the confirmation
		dialog.zClickButton(Button.B_YES);


		// Verify the appointment is gone
		AppointmentItem found = null;
		List<AppointmentItem> appts = app.zPageCalendar.zListGetAppointments();
		for (AppointmentItem item : appts) {
			if ( subject.contains(item.getGSubject()) ) {
				found = item;
				break;
			}
		}

		ZAssert.assertNull(found, "Verify the appointment is no longer in the list");


	}
	
	@Bugs(ids = "69132")
	@Test(	description = "Delete multiple appts (3) by select and toolbar delete",
			groups = { "functional" } )
	public void DeleteAppointment_05() throws HarnessException {

		// Create three appointments on the server
		String subject1 = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 13, 0, 0);
		String tz = ZTimeZone.TimeZoneEST.getID();

		// Create an appointment
		app.zGetActiveAccount().soapSend(
					"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv>"
				+				"<comp status='CONF' fb='B' class='PUB' transp='O' allDay='0' name='"+ subject1 +"' >"
				+					"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<or a='"+ app.zGetActiveAccount().EmailAddress + "'/>"
				+				"</comp>"
				+			"</inv>"
				+			"<su>"+ subject1 + "</su>"
				+			"<mp ct='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");

		String subject2 = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0);

		// Create an appointment
		app.zGetActiveAccount().soapSend(
					"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv>"
				+				"<comp status='CONF' fb='B' class='PUB' transp='O' allDay='0' name='"+ subject2 +"' >"
				+					"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<or a='"+ app.zGetActiveAccount().EmailAddress + "'/>"
				+				"</comp>"
				+			"</inv>"
				+			"<su>"+ subject2 + "</su>"
				+			"<mp ct='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");

		String subject3 = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 16, 0, 0);
		endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 17, 0, 0);

		// Create an appointment
		app.zGetActiveAccount().soapSend(
					"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv>"
				+				"<comp status='CONF' fb='B' class='PUB' transp='O' allDay='0' name='"+ subject3 +"' >"
				+					"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<or a='"+ app.zGetActiveAccount().EmailAddress + "'/>"
				+				"</comp>"
				+			"</inv>"
				+			"<su>"+ subject3 + "</su>"
				+			"<mp ct='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");



		// Refresh the calendar
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		// Check the item
		app.zPageCalendar.zListItem(Action.A_CHECKBOX, subject1);
		app.zPageCalendar.zListItem(Action.A_CHECKBOX, subject2);
		app.zPageCalendar.zListItem(Action.A_CHECKBOX, subject3);

		// Click delete
		DialogConfirm dialog = (DialogConfirm)app.zPageCalendar.zToolbarPressButton(Button.B_DELETE);

		// Click Yes on the confirmation
		dialog.zClickButton(Button.B_YES);


		// Verify the appointment is gone
		AppointmentItem found1 = null;
		AppointmentItem found2 = null;
		AppointmentItem found3 = null;
		List<AppointmentItem> appts = app.zPageCalendar.zListGetAppointments();
		for (AppointmentItem item : appts) {
			if ( subject1.contains(item.getGSubject()) ) {
				found1 = item;
			}
			if ( subject2.contains(item.getGSubject()) ) {
				found2 = item;
			}
			if ( subject3.contains(item.getGSubject()) ) {
				found3 = item;
			}
		}

		ZAssert.assertNull(found1, "Verify the appointment "+ subject1 +" no longer exists");
		ZAssert.assertNull(found2, "Verify the appointment "+ subject2 +" no longer exists");
		ZAssert.assertNull(found3, "Verify the appointment "+ subject3 +" no longer exists");


	}

	@Bugs(ids = "69132")
	@Test(	description = "Delete a appt using context menu delete button",
			groups = { "functional" })
	public void DeleteAppointment_06() throws HarnessException {


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


		// Refresh the calendar
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		// Right click the item, select delete
		DialogConfirm dialog = (DialogConfirm)app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_DELETE, subject);
		dialog.zClickButton(Button.B_YES);


		// Verify the appointment is gone
		AppointmentItem found = null;
		List<AppointmentItem> appts = app.zPageCalendar.zListGetAppointments();
		for (AppointmentItem item : appts) {
			if ( subject.contains(item.getGSubject()) ) {
				found = item;
				break;
			}
		}

		ZAssert.assertNull(found, "Verify the appointment is no longer in the list");

	}

	@Bugs(ids = "69132")
	@Test(	description = "Hard-delete a appt by selecting and typing 'shift-del' shortcut",
			groups = { "functional" } )
	public void HardDeleteAppointment_01() throws HarnessException {


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


		// Refresh the calendar
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		// Select the item
		app.zPageCalendar.zListItem(Action.A_LEFTCLICK, subject);


		// Type shift-delete
		DialogConfirm dialog = (DialogConfirm)app.zPageCalendar.zKeyboardShortcut(Shortcut.S_MAIL_HARDELETE);
		dialog.zClickButton(Button.B_YES);


		// Verify the appointment is gone
		AppointmentItem found = null;
		List<AppointmentItem> appts = app.zPageCalendar.zListGetAppointments();
		for (AppointmentItem item : appts) {
			if ( subject.contains(item.getGSubject()) ) {
				found = item;
				break;
			}
		}

		ZAssert.assertNull(found, "Verify the appointment is no longer in the list");


		// Verify the appointment is not in the calendar or trash
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-7).toMillis() +"' calExpandInstEnd='"+ startUTC.addDays(7).toMillis() +"'>"
				+		"<query>is:anywhere "+ subject +"</query>"
				+	"</SearchRequest>");

		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:appt");
		ZAssert.assertEquals(nodes.length, 0, "Verify the appointment is not in the calendar or trash (trash folder l='3')");

	}

	@Bugs(ids = "69132")
	@Test(	description = "Hard-delete multiple appts (3) by selecting and typing 'shift-del' shortcut",
			groups = { "functional" })
	public void HardDeleteAppointment_02() throws HarnessException {


		// Create three appointments on the server
		String subject1 = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 13, 0, 0);
		String tz = ZTimeZone.TimeZoneEST.getID();

		// Create an appointment
		app.zGetActiveAccount().soapSend(
					"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv>"
				+				"<comp status='CONF' fb='B' class='PUB' transp='O' allDay='0' name='"+ subject1 +"' >"
				+					"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<or a='"+ app.zGetActiveAccount().EmailAddress + "'/>"
				+				"</comp>"
				+			"</inv>"
				+			"<su>"+ subject1 + "</su>"
				+			"<mp ct='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");

		String subject2 = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0);

		// Create an appointment
		app.zGetActiveAccount().soapSend(
					"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv>"
				+				"<comp status='CONF' fb='B' class='PUB' transp='O' allDay='0' name='"+ subject2 +"' >"
				+					"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<or a='"+ app.zGetActiveAccount().EmailAddress + "'/>"
				+				"</comp>"
				+			"</inv>"
				+			"<su>"+ subject2 + "</su>"
				+			"<mp ct='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");

		String subject3 = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 16, 0, 0);
		endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 17, 0, 0);

		// Create an appointment
		app.zGetActiveAccount().soapSend(
					"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv>"
				+				"<comp status='CONF' fb='B' class='PUB' transp='O' allDay='0' name='"+ subject3 +"' >"
				+					"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<or a='"+ app.zGetActiveAccount().EmailAddress + "'/>"
				+				"</comp>"
				+			"</inv>"
				+			"<su>"+ subject3 + "</su>"
				+			"<mp ct='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");

		
		// Refresh the calendar
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		// Check the item
		app.zPageCalendar.zListItem(Action.A_CHECKBOX, subject1);
		app.zPageCalendar.zListItem(Action.A_CHECKBOX, subject2);
		app.zPageCalendar.zListItem(Action.A_CHECKBOX, subject3);

		// Click delete
		DialogConfirm dialog = (DialogConfirm)app.zPageCalendar.zKeyboardShortcut(Shortcut.S_MAIL_HARDELETE);
		dialog.zClickButton(Button.B_YES);


		// Verify the appointment is gone
		AppointmentItem found1 = null;
		AppointmentItem found2 = null;
		AppointmentItem found3 = null;
		List<AppointmentItem> appts = app.zPageCalendar.zListGetAppointments();
		for (AppointmentItem item : appts) {
			if ( subject1.contains(item.getGSubject()) ) {
				found1 = item;
			}
			if ( subject2.contains(item.getGSubject()) ) {
				found2 = item;
			}
			if ( subject3.contains(item.getGSubject()) ) {
				found3 = item;
			}
		}

		ZAssert.assertNull(found1, "Verify the appointment "+ subject1 +" no longer exists");
		ZAssert.assertNull(found2, "Verify the appointment "+ subject2 +" no longer exists");
		ZAssert.assertNull(found3, "Verify the appointment "+ subject3 +" no longer exists");


		// Verify the appointment is not in the calendar or trash
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-7).toMillis() +"' calExpandInstEnd='"+ startUTC.addDays(7).toMillis() +"'>"
				+		"<query>is:anywhere "+ subject1 +"</query>"
				+	"</SearchRequest>");

		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:appt");
		ZAssert.assertEquals(nodes.length, 0, "Verify the appointment is not in the calendar or trash (trash folder l='3')");

		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-7).toMillis() +"' calExpandInstEnd='"+ startUTC.addDays(7).toMillis() +"'>"
				+		"<query>is:anywhere "+ subject2 +"</query>"
				+	"</SearchRequest>");

		nodes = app.zGetActiveAccount().soapSelectNodes("//mail:appt");
		ZAssert.assertEquals(nodes.length, 0, "Verify the appointment is not in the calendar or trash (trash folder l='3')");

		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-7).toMillis() +"' calExpandInstEnd='"+ startUTC.addDays(7).toMillis() +"'>"
				+		"<query>is:anywhere "+ subject3 +"</query>"
				+	"</SearchRequest>");

		nodes = app.zGetActiveAccount().soapSelectNodes("//mail:appt");
		ZAssert.assertEquals(nodes.length, 0, "Verify the appointment is not in the calendar or trash (trash folder l='3')");

	}
}
