package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.workweek.recurring;

import java.awt.event.KeyEvent;
import java.util.Calendar;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogConfirm;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogConfirmDelete;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogDeleteRecurringItem;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogOpenRecurringItem;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning.DialogWarningID;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Locators;


@SuppressWarnings("unused")
public class DeleteAppointment extends AjaxCommonTest {

	public DeleteAppointment() {
		logger.info("New "+ DeleteAppointment.class.getCanonicalName());
		
		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;

	}
	
	@Bugs(ids = "69132")
	@Test(	description = "Delete entire series of recurring appointment (every day) using toolbar button", groups = { "smoke" } )
	public void DeleteSeries_01() throws HarnessException {
		
		// Appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
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
                               "<rule freq='DAI'>" +
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
        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");

		// Open series and delete it
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.B_DELETE);
        app.zPageCalendar.zCheckRadioButton(Button.B_DELETE_THE_SERIES);
        DialogDeleteRecurringItem dlgDeleteRecurringItem = new DialogDeleteRecurringItem(DialogDeleteRecurringItem.Confirmation.DELETERECURRINGITEM, app, ((AppAjaxClient) app).zPageCalendar);
        dlgDeleteRecurringItem.zClickButton(Button.B_OK);
        DialogConfirmDelete dlgConfirmDelete = new DialogConfirmDelete(DialogConfirmDelete.Confirmation.SENDCANCELLATION, app, ((AppAjaxClient) app).zPageCalendar);
        dlgConfirmDelete.zClickButton(Button.B_YES);
        DialogConfirm dlgConfirm = new DialogConfirm(DialogConfirm.Confirmation.YES, app, ((AppAjaxClient) app).zPageCalendar);
        dlgConfirm.zClickButton(Button.B_NO);
        
        // Verify recurring appt is deleted from calendar
        SleepUtil.sleepMedium(); //importSOAP gives wrong response without sleep
        ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify entire series is deleted from the calendar");      
        AppointmentItem canceledAppt = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")", startTime.addDays(-7), endTime.addDays(7));
        ZAssert.assertNull(canceledAppt, "Verify meeting is deleted");
	}
	
	@Bugs(ids = "69132")
	@Test(	description = "Delete entire series of recurring appointment (every week) using context menu", groups = { "smoke" } )
	public void DeleteSeries_02() throws HarnessException {
		
		// Appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
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
                               "<rule freq='WEE'>" +
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
        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");

		// Open series and delete it
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, apptSubject);
        app.zPageCalendar.sMouseOver(PageCalendar.Locators.SeriesMenu);
        SleepUtil.sleepSmall();
        app.zPageCalendar.zClick(PageCalendar.Locators.DeleteSeriesMenu);
        DialogConfirmDelete dlgConfirmDelete = new DialogConfirmDelete(DialogConfirmDelete.Confirmation.SENDCANCELLATION, app, ((AppAjaxClient) app).zPageCalendar);
        dlgConfirmDelete.zClickButton(Button.B_YES);
        DialogConfirm dlgConfirm = new DialogConfirm(DialogConfirm.Confirmation.YES, app, ((AppAjaxClient) app).zPageCalendar);
        dlgConfirm.zClickButton(Button.B_NO);
        
        // Verify recurring appt is deleted from calendar
        SleepUtil.sleepMedium(); //importSOAP gives wrong response without sleep
        ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify entire series is deleted from the calendar");      
        AppointmentItem canceledAppt = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")", startTime.addDays(-7), endTime.addDays(7));
		ZAssert.assertNull(canceledAppt, "Verify meeting is deleted");
	}
	
	@DataProvider(name = "DataProviderShortcutKeys")
	public Object[][] DataProviderShortcutKeys() {
		return new Object[][] {
				new Object[] { "VK_DELETE", KeyEvent.VK_DELETE },
				new Object[] { "VK_BACK_SPACE", KeyEvent.VK_BACK_SPACE },
		};
	}

	@Bugs(ids = "69132")
	@Test(description = "Delete entire series appointment (every week) using keyboard shortcuts Del & Backspace",
			groups = { "functional" },
			dataProvider = "DataProviderShortcutKeys")
			
	public void DeleteSeries_03(String name, int keyEvent) throws HarnessException {
		
		// Appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
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
                               "<rule freq='WEE'>" +
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
        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        
		// Open series and delete it using keyboard shortcut key
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        DialogDeleteRecurringItem dlgDeleteRecurringItem = (DialogDeleteRecurringItem)app.zPageCalendar.zKeyboardKeyEvent(keyEvent);
        app.zPageCalendar.zCheckRadioButton(Button.B_DELETE_THE_SERIES);
        dlgDeleteRecurringItem.zClickButton(Button.B_OK);
        DialogConfirmDelete dlgConfirmDelete = new DialogConfirmDelete(DialogConfirmDelete.Confirmation.SENDCANCELLATION, app, ((AppAjaxClient) app).zPageCalendar);
        dlgConfirmDelete.zClickButton(Button.B_YES);
        DialogConfirm dlgConfirm = new DialogConfirm(DialogConfirm.Confirmation.YES, app, ((AppAjaxClient) app).zPageCalendar);
        dlgConfirm.zClickButton(Button.B_NO);
        
        // Verify recurring appt is deleted from calendar
        SleepUtil.sleepMedium(); //importSOAP gives wrong response without sleep
        ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify entire series is deleted from the calendar");      
        AppointmentItem canceledAppt = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")", startTime.addDays(-7), endTime.addDays(7));
        ZAssert.assertNull(canceledAppt, "Verify meeting is deleted");
	}
	
	@Bugs(ids = "69132")
	@Test(	description = "Delete instance of recurring appointment (every month) using toolbar button", groups = { "sanity" } )
	public void DeleteInstance_04() throws HarnessException {
		
		// Appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
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
        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");

		// Delete instance and verify corresponding UI
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.B_DELETE);
        app.zPageCalendar.zCheckRadioButton(Button.B_DELETE_THIS_INSTANCE);
        DialogDeleteRecurringItem dlgDeleteRecurringItem = new DialogDeleteRecurringItem(DialogDeleteRecurringItem.Confirmation.DELETERECURRINGITEM, app, ((AppAjaxClient) app).zPageCalendar);
        dlgDeleteRecurringItem.zClickButton(Button.B_OK);
        DialogConfirmDelete dlgConfirmDelete = new DialogConfirmDelete(DialogConfirmDelete.Confirmation.SENDCANCELLATION, app, ((AppAjaxClient) app).zPageCalendar);
        dlgConfirmDelete.zClickButton(Button.B_YES);
        DialogConfirm dlgConfirm = new DialogConfirm(DialogConfirm.Confirmation.YES, app, ((AppAjaxClient) app).zPageCalendar);
        dlgConfirm.zClickButton(Button.B_NO);
        SleepUtil.sleepMedium();
        ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify instance is deleted from the calendar");
	}
	
	@Bugs(ids = "69132")
	@Test(	description = "Delete instance of recurring appointment (every year) using context menu", groups = { "functional" } )
	public void DeleteInstance_05() throws HarnessException {
		
		// Appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
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
        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");

		// Delete instance and verify corresponding UI
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, apptSubject);
        app.zPageCalendar.sMouseOver(PageCalendar.Locators.InstanceMenu);
        app.zPageCalendar.zClick(PageCalendar.Locators.DeleteInstanceMenu);
        SleepUtil.sleepSmall();
        DialogConfirmDelete dlgConfirm = new DialogConfirmDelete(DialogConfirmDelete.Confirmation.SENDCANCELLATION, app, ((AppAjaxClient) app).zPageCalendar);
        dlgConfirm.zClickButton(Button.B_SENDCANCELLATION);
        SleepUtil.sleepMedium();
        ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify instance is deleted from the calendar");
	}
	
	@Bugs(ids = "69132")
	@Test(description = "Delete instance of series appointment (every week) using keyboard shortcuts Del & Backspace",
			groups = { "functional" },
			dataProvider = "DataProviderShortcutKeys")
			
	public void DeleteInstance_06(String name, int keyEvent) throws HarnessException {
		
		// Appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
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
                               "<rule freq='WEE'>" +
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
        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        
		// Open series and delete it using keyboard shortcut key
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        DialogDeleteRecurringItem dlgDeleteRecurringItem = (DialogDeleteRecurringItem)app.zPageCalendar.zKeyboardKeyEvent(keyEvent);
        app.zPageCalendar.zCheckRadioButton(Button.B_DELETE_THIS_INSTANCE);
        dlgDeleteRecurringItem.zClickButton(Button.B_OK);
        DialogConfirmDelete dlgConfirmDelete = new DialogConfirmDelete(DialogConfirmDelete.Confirmation.SENDCANCELLATION, app, ((AppAjaxClient) app).zPageCalendar);
        dlgConfirmDelete.zClickButton(Button.B_YES);
        DialogConfirm dlgConfirm = new DialogConfirm(DialogConfirm.Confirmation.YES, app, ((AppAjaxClient) app).zPageCalendar);
        dlgConfirm.zClickButton(Button.B_NO);
        SleepUtil.sleepMedium();
        ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify instance is deleted from the calendar");
	}
	
}