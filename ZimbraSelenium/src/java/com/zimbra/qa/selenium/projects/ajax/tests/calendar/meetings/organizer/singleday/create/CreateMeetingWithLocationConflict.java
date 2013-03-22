package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.create;

import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogWarningConflictingResources;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Locators;

public class CreateMeetingWithLocationConflict extends CalendarWorkWeekTest {

	public CreateMeetingWithLocationConflict() {
		logger.info("New "+ CreateMeetingWithLocationConflict.class.getCanonicalName());
		super.startingPage =  app.zPageCalendar;
	}
	
	@Bugs(ids = "81246")
	@Test(description = "Verify sending appt invite when Location resource has conflicts shows conflict dialog", 
			groups = { "functional" })
	public void CreateMeetingWithLocationConflict_01() throws HarnessException {
		
		// Creating object for meeting data
		ZimbraResource location = new ZimbraResource(ZimbraResource.Type.LOCATION);
		
		String tz, apptSubject1,apptSubject2 , apptAttendeeEmail;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject1 = "app" + ZimbraSeleniumProperties.getUniqueString();
		apptAttendeeEmail = ZimbraAccount.AccountA().EmailAddress;
		String apptLocation = location.EmailAddress;
		
		String apptContent = ZimbraSeleniumProperties.getUniqueString();
		AppointmentItem appt = new AppointmentItem();
		apptSubject1 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject2 = ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 18, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject1 +"' loc='"+ apptLocation+"'>" +
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendeeEmail + "' d='2'/>" +
                     		"<at cutype='RES' a='" + apptLocation + "' rsvp='1' role='REQ' url='" + apptLocation + "' ptst='AC'/>" +
                     	"</inv>" +
                     	"<e a='"+ apptLocation +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject1 +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
		// Create appointment data
		appt.setSubject(apptSubject2);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 10, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0));
		appt.setContent(apptContent);
		appt.setLocation(apptLocation);
		
		// Create meeting which has location conflict with above created appointment
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		SleepUtil.sleepVeryLong();
		
		// Verify the compose page shows note below resource about conflicting resources
		ZAssert.assertTrue(app.zPageCalendar.sIsElementPresent(Locators.ConflictResourceNote),  "Verify that the conflicting resource note appears on appt compose page");
		DialogWarningConflictingResources  dialog = (DialogWarningConflictingResources) app.zPageCalendar.zToolbarPressButton(Button.B_SEND_WITH_CONFLICT);
		String dialogContent = dialog.zGetResourceConflictWarningDialogText();
		ZAssert.assertTrue(dialogContent.contains("The selected resources/location cannot be scheduled for the following instances"), "Verify that the dialog shows expected text");
		ZAssert.assertTrue(dialogContent.contains(apptLocation+"(Busy)"), "Verify that the dialog shows location name on conflict warning");

        // Save appt with location conflict 
		dialog.zClickButton(Button.B_SAVE_WITH_CONFLICT);
		
        // Verify that location with conflict and subject are present in the appointment
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject2 +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject2, "Subject: Verify the appointment data");
		ZAssert.assertEquals(appt.getLocation(), apptLocation, "Location: Verify the location is present in the appointment");
		SleepUtil.sleepVeryLong();
		
		// Verify location free/busy status shows as psts=DE
		String locationStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptLocation +"']", "ptst");
		ZAssert.assertEquals(locationStatus, "DE", "Verify that the location status shows as 'DECLINED'");
			
	}
	
	@Test(description = "Verify Cancelling create appt when Location resource has conflicts shows conflict dialog", 
			groups = { "functional" })
	public void CreateMeetingWithLocationConflict_02() throws HarnessException {

		// Creating object for meeting data
		ZimbraResource location = new ZimbraResource(ZimbraResource.Type.LOCATION);
		
		String tz, apptSubject1,apptSubject2 , apptAttendeeEmail;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject1 = "app" + ZimbraSeleniumProperties.getUniqueString();
		apptAttendeeEmail = ZimbraAccount.AccountA().EmailAddress;
		String apptLocation = location.EmailAddress;
		
		String apptContent = ZimbraSeleniumProperties.getUniqueString();
		AppointmentItem appt = new AppointmentItem();
		apptSubject1 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject2 = ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 16, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 18, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject1 +"' loc='"+ apptLocation+"'>" +
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendeeEmail + "' d='2'/>" +
                     		"<at cutype='RES' a='" + apptLocation + "' rsvp='1' role='REQ' url='" + apptLocation + "' ptst='AC'/>" +
                     	"</inv>" +
                     	"<e a='"+ apptLocation +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject1 +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        SleepUtil.sleepSmall();
        
        // Create meeting which has location conflict for above created appointment
		appt.setSubject(apptSubject2);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 16, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 19, 0, 0));
		appt.setContent(apptContent);
		appt.setLocation(apptLocation);
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		SleepUtil.sleepVeryLong();
		
		// Verify the compose page shows note below resource about conflicting resources
		ZAssert.assertTrue(app.zPageCalendar.sIsElementPresent(Locators.ConflictResourceNote),  "Verify that the conflicting resource note appears on appt compose page");
		
		// Verify the compose page shows note below resource about conflicting resources and conflicting resource dialog appears
		DialogWarningConflictingResources  dialog = (DialogWarningConflictingResources) app.zPageCalendar.zToolbarPressButton(Button.B_SEND_WITH_CONFLICT);
		String dialogContent = dialog.zGetResourceConflictWarningDialogText();
		ZAssert.assertTrue(dialogContent.contains("The selected resources/location cannot be scheduled for the following instances"), "Verify that the dialog shows expected text");
		ZAssert.assertTrue(dialogContent.contains(apptLocation+"(Busy)"), "Verify that the dialog shows location name on conflict warning");

        // Verify Canceling the 'Conflicting Resource' closes the dialog
		dialog.zClickButton(Button.B_CANCEL_CONFLICT);
        ZAssert.assertFalse(dialog.zIsActive(), "Verify 'Conflicting Resource' dialog is closed");
        
        // Verify new appt page is still open
        ZAssert.assertFalse(apptForm.zVerifyNewApptTabClosed(), "Verify new appt page is still open");
        
        // Verify that appointment subject is not modified
        AppointmentItem modifyAppt = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject2 +")");
        ZAssert.assertNull(modifyAppt, "Verify new appointment with conflicting Resource has not been created");
        
        // Close window so that next test doesn't fail
        apptForm.zCloseModifiedApptTab();
	}
	
	@Test(description = "Verify Saving meeting invite when Location resource has conflicts shows conflict dialog",  
			groups = { "functional" })
	public void CreateMeetingWithLocationConflict_03() throws HarnessException {
		
		// Creating object for meeting data
		ZimbraResource location = new ZimbraResource(ZimbraResource.Type.LOCATION);
		
		String tz, apptSubject1,apptSubject2 , apptAttendeeEmail;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject1 = "app" + ZimbraSeleniumProperties.getUniqueString();
		apptAttendeeEmail = ZimbraAccount.AccountA().EmailAddress;
		String apptLocation = location.EmailAddress;
		
		String apptContent = ZimbraSeleniumProperties.getUniqueString();
		AppointmentItem appt = new AppointmentItem();
		apptSubject1 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject2 = ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject1 +"' loc='"+ apptLocation+"'>" +
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendeeEmail + "' d='2'/>" +
                     		"<at cutype='RES' a='" + apptLocation + "' rsvp='1' role='REQ' url='" + apptLocation + "' ptst='AC'/>" +
                     	"</inv>" +
                     	"<e a='"+ apptLocation +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject1 +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
		// Create appointment data
		appt.setSubject(apptSubject2);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 10, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0));
		appt.setContent(apptContent);
		appt.setLocation(apptLocation);
		
		// Create meeting which has location conflict for above created appointment
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		SleepUtil.sleepVeryLong();
		
		// Verify the compose page shows note below resource about conflicting resources
		ZAssert.assertTrue(app.zPageCalendar.sIsElementPresent(Locators.ConflictResourceNote),  "Verify that the conflicting resource note appears on appt compose page");	
		
		//verify the compose page shows note below resource about conflicting resources and conflicting resource dialog appears
		DialogWarningConflictingResources  dialog = (DialogWarningConflictingResources) app.zPageCalendar.zToolbarPressButton(Button.B_SAVE_WITH_CONFLICT);
		String dialogContent = dialog.zGetResourceConflictWarningDialogText();
		ZAssert.assertTrue(dialogContent.contains("The selected resources/location cannot be scheduled for the following instances"), "Verify that the dialog shows expected text");
		ZAssert.assertTrue(dialogContent.contains(apptLocation+"(Busy)"), "Verify that the dialog shows location name on conflict warning");
		
		// Save appointment with Location conflict
        ZAssert.assertTrue(dialog.zIsActive(), "Verify 'Conflicting Resource' dialog is Open");
        dialog.zClickButton(Button.B_SAVE_WITH_CONFLICT);
        
        // Verify that modified location and subject are present in the appointment
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject2 +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject2, "Subject: Verify the appointment data");
		ZAssert.assertEquals(appt.getLocation(), apptLocation, "Location: Verify the location is present in the appointment");
		SleepUtil.sleepVeryLong();
		
		// Verify location free/busy status shows as psts=NE	
		String locationStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptLocation +"']", "ptst");
		ZAssert.assertEquals(locationStatus, "NE", "Verify that the location status shows as 'DECLINCED'");
		
		// Close window so that next test doesn't fail
		apptForm.zToolbarPressButton(Button.B_CLOSE);
	}
	
	@Test(description = "Verify organizer can close modified appointment with location Conflict",  
			groups = { "functional" })
	public void CreateMeetingWithLocationConflict_04() throws HarnessException {
		
		// Creating object for meeting data
		ZimbraResource location = new ZimbraResource(ZimbraResource.Type.LOCATION);
		
		String tz, apptSubject1,apptSubject2 , apptAttendeeEmail;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject1 = "app" + ZimbraSeleniumProperties.getUniqueString();
		apptAttendeeEmail = ZimbraAccount.AccountA().EmailAddress;
		String apptLocation = location.EmailAddress;
		
		String apptContent = ZimbraSeleniumProperties.getUniqueString();
		AppointmentItem appt = new AppointmentItem();
		apptSubject1 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject2 = ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject1 +"' loc='"+ apptLocation+"'>" +
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendeeEmail + "' d='2'/>" +
                     		"<at cutype='RES' a='" + apptLocation + "' rsvp='1' role='REQ' url='" + apptLocation + "' ptst='AC'/>" +
                     	"</inv>" +
                     	"<e a='"+ apptLocation +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject1 +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
		// Create appointment data
		appt.setSubject(apptSubject2);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 10, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0));
		appt.setContent(apptContent);
		appt.setLocation(apptLocation);
		
		// Create meeting which has location conflict for above created appointment and then close it w/o saving
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		SleepUtil.sleepVeryLong();
		
		apptForm.zCloseModifiedApptTab();
      
        // Verify that appointment subject is not modified
        AppointmentItem modifyAppt = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject2 +")");
        ZAssert.assertNull(modifyAppt, "Verify new appointment with conflicting Resource has not been created");
	}
	
	
}
