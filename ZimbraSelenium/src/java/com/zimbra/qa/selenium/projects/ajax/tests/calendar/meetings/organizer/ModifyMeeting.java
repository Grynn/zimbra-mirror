package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer;

import java.util.Calendar;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogConfirm;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;

@SuppressWarnings("unused")
public class ModifyMeeting extends AjaxCommonTest {

	public ModifyMeeting() {
		logger.info("New "+ ModifyMeeting.class.getCanonicalName());
		
		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;
	}
	
	@Bugs(ids = "69132")
	@Test(	description = "Modify meeting by adding more attendees",
			groups = { "smoke" })
	public void ModifyMeeting_01() throws HarnessException {
		
		// Creating object for meeting data
		AppointmentItem appt = new AppointmentItem();
		String tz, apptSubject, apptBody, apptAttendee1, editApptSubject, editApptBody, editApptAttendee1;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		editApptSubject = ZimbraSeleniumProperties.getUniqueString();
        editApptBody = ZimbraSeleniumProperties.getUniqueString();
        editApptAttendee1 = ZimbraAccount.AccountB().EmailAddress;
        
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     "<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     "<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "'/>" + 
                     "</inv>" +
                     "<e a='"+ apptAttendee1 +"' t='t'/>" +
                     "<mp content-type='text/plain'>" +
                     "<content>"+ apptBody +"</content>" +
                     "</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");

		String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        
        // Open appointment and modify subject, attendee and content
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        SleepUtil.sleepMedium();
        FormApptNew apptForm = new FormApptNew(app);
        appt.setSubject(editApptSubject);
        appt.setAttendees(editApptAttendee1);
        appt.setContent(editApptBody);
        apptForm.zFill(appt);
        apptForm.zToolbarPressButton(Button.B_SEND);
        
        // Use GetAppointmentRequest to verify the changes are saved
        AppointmentItem modifyAppt = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ editApptSubject +")", startUTC, endUTC);
        ZAssert.assertEquals(modifyAppt.getSubject(), editApptSubject, "Subject: Verify modified appointment subject");
        ZAssert.assertEquals(modifyAppt.getAttendees(), apptAttendee1 + "," + editApptAttendee1, "Attendees: Verify modified attendees");
        ZAssert.assertEquals(modifyAppt.getContent(), editApptBody, "Body: Verify modified appointment body");
		
	}
	
}
