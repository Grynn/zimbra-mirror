package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.day.recurring;

import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.HashMap;
import org.testng.annotations.*;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.*;

@SuppressWarnings("unused")
public class DeleteInstance extends AjaxCommonTest {

	public DeleteInstance() {
		logger.info("New "+ DeleteInstance.class.getCanonicalName());

		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with day view
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -2913827779459595178L;
		{
		    put("zimbraPrefCalendarInitialView", "day");
		}};
	}
	
	
	@Bugs(ids = "69132")
	@Test(	
			description = "Delete instance of recurring appointment (every month) using toolbar button in day view", 
			groups = { "functional" } )
	public void DeleteInstance_04() throws HarnessException {
		
		//-- Data Setup
		
		
		
		// Appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		apptBody = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startTime = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endTime   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
					"<m>"+
						"<inv method='REQUEST' type='event' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
							"<s d='"+ startTime.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
							"<e d='"+ endTime.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
							"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<recur>" +
								"<add>" +
									"<rule freq='MON'>" +
										"<interval ival='1'/>" +
									"</rule>" +
								"</add>" +
							"</recur>" +
						"</inv>" +
						"<mp content-type='text/plain'>" +
							"<content>"+ apptBody +"</content>" +
						"</mp>" +
						"<su>"+ apptSubject +"</su>" +
					"</m>" +
				"</CreateAppointmentRequest>");

        
        //-- GUI actions
        
        
		// Delete instance and verify corresponding UI
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        
        DialogWarning dialogSeriesOrInstance = (DialogWarning)app.zPageCalendar.zToolbarPressButton(Button.B_DELETE);
        dialogSeriesOrInstance.zClickButton(Button.B_DELETE_THIS_INSTANCE);
        DialogWarning confirmDelete = (DialogWarning)dialogSeriesOrInstance.zClickButton(Button.B_OK);
        confirmDelete.zClickButton(Button.B_YES);
        
        
        
        //-- Verification
        
		// On the server, verify the appointment is in the trash
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startTime.addDays(-7).toMillis() +"' calExpandInstEnd='"+ endTime.addDays(7).toMillis() +"'>"
			+		"<query>is:anywhere "+ apptSubject +"</query>"
			+	"</SearchRequest>");

		// http://bugzilla.zimbra.com/show_bug.cgi?id=63412 - "Deleting instance from calendar series does not allow for user restoration from the Trash can"
		// http://bugzilla.zimbra.com/show_bug.cgi?id=13527#c4 - "Moving an instance from one cal to other, moves complete series"
		// For now, nothing should be returned in the SearchResponse
		//		
		//		String folderID = app.zGetActiveAccount().soapSelectValue("//mail:appt", "l");
		//		ZAssert.assertEquals(
		//				folderID,
		//				FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash).getId(),
		//				"Verify appointment is in the trash folder");

		Element[] appts = app.zGetActiveAccount().soapSelectNodes("//mail:appt");
		ZAssert.assertEquals(appts.length, 0, "Verify the appt element does not exist ... See also bug 63412");

		// Verify the appointment is not in the GUI view
		//ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify instance is deleted from the calendar");
		boolean deleted = app.zPageCalendar.zWaitForElementDeleted(app.zPageCalendar.zGetApptLocator(apptSubject), "10000");
		ZAssert.assertEquals(deleted, true, "Verify instance is deleted from the calendar");

        
	}
	
	@Bugs(ids = "69132")
	@Test(	
			description = "Delete instance of recurring appointment (every year) using context menu in day view", 
			groups = { "functional" } )
	public void DeleteInstance_05() throws HarnessException {
		
		//-- Data Setup
		
		
		
		// Appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		apptBody = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startTime = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endTime   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
					"<m>"+
						"<inv method='REQUEST' type='event' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
							"<s d='"+ startTime.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
							"<e d='"+ endTime.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
							"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<recur>" +
								"<add>" +
									"<rule freq='YEA'>" +
										"<interval ival='1'/>" +
									"</rule>" +
								"</add>" +
							"</recur>" +
						"</inv>" +
						"<mp content-type='text/plain'>" +
							"<content>"+ apptBody +"</content>" +
						"</mp>" +
						"<su>"+ apptSubject +"</su>" +
					"</m>" +
				"</CreateAppointmentRequest>");

        
        //-- GUI actions
        
        
		// Delete instance and verify corresponding UI
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        
        DialogWarning dialogSeriesOrInstance = (DialogWarning)app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_DELETE, apptSubject);
        dialogSeriesOrInstance.zClickButton(Button.B_DELETE_THIS_INSTANCE);
        DialogWarning confirmDelete = (DialogWarning)dialogSeriesOrInstance.zClickButton(Button.B_OK);
        confirmDelete.zClickButton(Button.B_YES);
        
        
        
        //-- Verification
        
		// On the server, verify the appointment is in the trash
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startTime.addDays(-7).toMillis() +"' calExpandInstEnd='"+ endTime.addDays(7).toMillis() +"'>"
			+		"<query>is:anywhere "+ apptSubject +"</query>"
			+	"</SearchRequest>");

		// http://bugzilla.zimbra.com/show_bug.cgi?id=63412 - "Deleting instance from calendar series does not allow for user restoration from the Trash can"
		// http://bugzilla.zimbra.com/show_bug.cgi?id=13527#c4 - "Moving an instance from one cal to other, moves complete series"
		// For now, nothing should be returned in the SearchResponse
		//		
		//		String folderID = app.zGetActiveAccount().soapSelectValue("//mail:appt", "l");
		//		ZAssert.assertEquals(
		//				folderID,
		//				FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash).getId(),
		//				"Verify appointment is in the trash folder");

		Element[] appts = app.zGetActiveAccount().soapSelectNodes("//mail:appt");
		ZAssert.assertEquals(appts.length, 0, "Verify the appt element does not exist ... See also bug 63412");

		// Verify the appointment is not in the GUI view
		//ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify instance is deleted from the calendar");
		boolean deleted = app.zPageCalendar.zWaitForElementDeleted(app.zPageCalendar.zGetApptLocator(apptSubject), "10000");
		ZAssert.assertEquals(deleted, true, "Verify instance is deleted from the calendar");

        
	}
	
	@DataProvider(name = "DataProviderShortcutKeys")
	public Object[][] DataProviderShortcutKeys() {
		return new Object[][] {
				new Object[] { "VK_DELETE", KeyEvent.VK_DELETE },
				new Object[] { "VK_BACK_SPACE", KeyEvent.VK_BACK_SPACE },
		};
	}

	@Bugs(ids = "69132")
	@Test(
			description = "Delete instance of series appointment (every week) using keyboard shortcuts Del & Backspace in day view",
			groups = { "functional" },
			dataProvider = "DataProviderShortcutKeys" )
	public void DeleteInstance_06(String name, int keyEvent) throws HarnessException {
		
		//-- Data Setup
		
		
		
		// Appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		apptBody = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startTime = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endTime   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
					"<m>"+
						"<inv method='REQUEST' type='event' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
							"<s d='"+ startTime.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
							"<e d='"+ endTime.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
							"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<recur>" +
								"<add>" +
									"<rule freq='MON'>" +
										"<interval ival='1'/>" +
									"</rule>" +
								"</add>" +
							"</recur>" +
						"</inv>" +
						"<mp content-type='text/plain'>" +
							"<content>"+ apptBody +"</content>" +
						"</mp>" +
						"<su>"+ apptSubject +"</su>" +
					"</m>" +
				"</CreateAppointmentRequest>");

        
        //-- GUI actions
        
        
		// Delete instance and verify corresponding UI
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        
        DialogWarning dialogSeriesOrInstance = (DialogWarning)app.zPageCalendar.zKeyboardKeyEvent(keyEvent);
        dialogSeriesOrInstance.zClickButton(Button.B_DELETE_THIS_INSTANCE);
        DialogWarning confirmDelete = (DialogWarning)dialogSeriesOrInstance.zClickButton(Button.B_OK);
        confirmDelete.zClickButton(Button.B_YES);
        
        
        
        //-- Verification
        
		// On the server, verify the appointment is in the trash
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startTime.addDays(-7).toMillis() +"' calExpandInstEnd='"+ endTime.addDays(7).toMillis() +"'>"
			+		"<query>is:anywhere "+ apptSubject +"</query>"
			+	"</SearchRequest>");

		// http://bugzilla.zimbra.com/show_bug.cgi?id=63412 - "Deleting instance from calendar series does not allow for user restoration from the Trash can"
		// http://bugzilla.zimbra.com/show_bug.cgi?id=13527#c4 - "Moving an instance from one cal to other, moves complete series"
		// For now, nothing should be returned in the SearchResponse
		//		
		//		String folderID = app.zGetActiveAccount().soapSelectValue("//mail:appt", "l");
		//		ZAssert.assertEquals(
		//				folderID,
		//				FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash).getId(),
		//				"Verify appointment is in the trash folder");

		Element[] appts = app.zGetActiveAccount().soapSelectNodes("//mail:appt");
		ZAssert.assertEquals(appts.length, 0, "Verify the appt element does not exist ... See also bug 63412");

		// Verify the appointment is not in the GUI view
		//ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify instance is deleted from the calendar");
		boolean deleted = app.zPageCalendar.zWaitForElementDeleted(app.zPageCalendar.zGetApptLocator(apptSubject), "10000");
		ZAssert.assertEquals(deleted, true, "Verify instance is deleted from the calendar");

        
	}
	
}