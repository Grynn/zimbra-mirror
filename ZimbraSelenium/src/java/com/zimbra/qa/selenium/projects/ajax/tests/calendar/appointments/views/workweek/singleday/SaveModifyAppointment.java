package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.workweek.singleday;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZDate;
import com.zimbra.qa.selenium.framework.util.ZTimeZone;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogSendUpdatetoAttendees;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogConfirmModification;
public class SaveModifyAppointment extends CalendarWorkWeekTest {
	
	public SaveModifyAppointment() {
		logger.info("New " + SaveModifyAppointment.class.getCanonicalName());

	}
	
	@Test(description = "Save modifying appt and take action from warning dialog : Save Chnages and send updates", 
			groups = { "functional12"})
	public void SaveModifyAppointment_01() throws HarnessException {

		String tz, apptSubject, apptAttendee1,apptAttendee2;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
        apptAttendee2 = ZimbraAccount.AccountB().EmailAddress;
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee2 + "' d='2'/>" +
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);


        // Switch to work week view
        app.zPageCalendar.zToolbarPressPulldown(Button.B_LISTVIEW, Button.O_LISTVIEW_WORKWEEK);
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

     // Create appointment with attendee
        app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        SleepUtil.sleepMedium();
        FormApptNew apptForm = new FormApptNew(app);
        apptForm.zRemoveAttendee(apptAttendee2);
        apptForm.zToolbarPressButton(Button.B_SAVE);
        DialogConfirmModification confirmClose = (DialogConfirmModification) new DialogConfirmModification(app, app.zPageCalendar);
        confirmClose.zClickButton(Button.B_OK);
        DialogSendUpdatetoAttendees sendUpdateDialog = (DialogSendUpdatetoAttendees) new DialogSendUpdatetoAttendees(app, app.zPageCalendar);
        sendUpdateDialog.zClickButton(Button.B_OK);
        SleepUtil.sleepLong();
        
        // check if an attendee 
       
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringDoesNotContain(actual.getAttendees(), apptAttendee2, "Attendees: Verify the appointment data");
	}
	
	@Test(description = "Save modifying appt and take action from warning dialog : Discard and close", 
			groups = { "functional12"})
	public void SaveModifyAppointment_02() throws HarnessException {

		// Creating object for appointment data
		AppointmentItem appt = new AppointmentItem();
		String tz, apptSubject, editApptSubject, apptAttendee1,apptAttendee2;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		editApptSubject = ZimbraSeleniumProperties.getUniqueString();
        apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
        apptAttendee2 = ZimbraAccount.AccountB().EmailAddress;

		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee2 + "' d='2'/>" +
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
    
        // Switch to work week view
        app.zPageCalendar.zToolbarPressPulldown(Button.B_LISTVIEW, Button.O_LISTVIEW_WORKWEEK);
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

        // Open appointment & modify subject, body and save it
        app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        SleepUtil.sleepMedium();
        FormApptNew apptForm = new FormApptNew(app);
        
        appt.setSubject(editApptSubject);
       // appt.setContent(editApptBody);
        apptForm.zRemoveAttendee(apptAttendee2);
        apptForm.zToolbarPressButton(Button.B_SAVE);
        DialogConfirmModification confirmClose = (DialogConfirmModification) new DialogConfirmModification(app, app.zPageCalendar);
        String  oldPageName= app.zPageCalendar.sGetTitle();
   
        confirmClose.zClickButton(Button.B_DISCARD_CLOSE);
        confirmClose.zClickButton(Button.B_OK);
        SleepUtil.sleepSmall();
        
        // Use GetAppointmentRequest to verify the changes are saved
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ apptId +"'/>");
        AppointmentItem modifyAppt = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")");

        // check if appt still has old subject and not the modified subject 
        ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetAppointmentResponse//mail:comp", "name", apptSubject), true, "");
        ZAssert.assertStringContains(modifyAppt.getAttendees(), apptAttendee1, "Attendees: Verify modified attendees");
        String  newPageName= app.zPageCalendar.sGetTitle();
        ZAssert.assertNotEqual(oldPageName, newPageName, "the Page has been closed");
	}
	@Test(description = "Save modifying appt and take action from warning dialog : Dont save But keep open", 
			groups = { "functional12"})
	public void SaveModifyAppointment_03() throws HarnessException {
		// Creating object for appointment data
		AppointmentItem appt = new AppointmentItem();
		String tz, apptSubject, apptAttendee1,apptAttendee2,editApptSubject;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		editApptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
        apptAttendee2 = ZimbraAccount.AccountB().EmailAddress;
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee2 + "' d='2'/>" +
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);


        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
    
        // Switch to work week view
        app.zPageCalendar.zToolbarPressPulldown(Button.B_LISTVIEW, Button.O_LISTVIEW_WORKWEEK);
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

     // Create appointment with attendee
        app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        SleepUtil.sleepMedium();
        FormApptNew apptForm = new FormApptNew(app);
        appt.setSubject(editApptSubject);
       // appt.setContent(editApptBody);
        apptForm.zRemoveAttendee(apptAttendee2);
        apptForm.zToolbarPressButton(Button.B_SAVE);
        DialogConfirmModification confirmClose = (DialogConfirmModification) new DialogConfirmModification(app, app.zPageCalendar);
        confirmClose.zClickButton(Button.B_DONTSAVE_KEEP_OPEN);
        confirmClose.zClickButton(Button.B_OK);
        SleepUtil.sleepLong();     
        // Use GetAppointmentRequest to verify the changes are saved
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ apptId +"'/>");
        AppointmentItem modifyAppt = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")");

        // check if appt still has old subject and not the modified subject 
        ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetAppointmentResponse//mail:comp", "name", apptSubject), true, "");
        ZAssert.assertStringContains(modifyAppt.getAttendees(), apptAttendee1, "Attendees: Verify modified attendees");
        ZAssert.assertEquals(app.zPageCalendar.sGetTitle(),"Zimbra: Appointment" , "make sure appointment window is open");
	}
	
	
	
	
	
}