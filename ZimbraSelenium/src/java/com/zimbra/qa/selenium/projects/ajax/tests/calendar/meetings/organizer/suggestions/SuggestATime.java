package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.suggestions;

import java.util.Calendar;
import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;

public class SuggestATime extends CalendarWorkWeekTest {	
	
	public SuggestATime() {
		logger.info("New "+ SuggestATime.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Bugs(ids = "73966")
	@Test(description = "Suggest a free time while creating appointment",
			groups = { "smoke" })
	public void SuggestATime_01() throws HarnessException {
		
		// Create a meeting
		String tz = ZTimeZone.TimeZoneEST.getID();
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptAttendee = ZimbraAccount.AccountA().EmailAddress;
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 6, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 7, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee + "' d='1'/>" +
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
		String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // Suggest a time, pickup 10AM and send the appointment
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        apptForm.zToolbarPressButton(Button.B_SUGGESTATIME);
        // apptForm.zVerifySpecificTimeNotExists("6:00 AM,7:00 AM,7:30 AM");
        // apptForm.zVerifySpecificTimeExists("8:00 AM,8:30 AM,9:00 AM,3:00 PM,3:30 PM,4:00 PM");
        apptForm.zToolbarPressButton(Button.B_10AM);
        apptForm.zToolbarPressButton(Button.B_SEND);
        SleepUtil.sleepLong(); //importFromSOAP gives wrong response without sleep sometime

        // Verify appointment start time and end time
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringContains(actual.getAttendees(), apptAttendee, "Attendees: Verify the appointment data");
		
		app.zGetActiveAccount().soapSend("<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ apptId +"'/>");
		String startDate = app.zGetActiveAccount().soapSelectValue("//mail:s", "d");//20130109T100000
		String endDate = app.zGetActiveAccount().soapSelectValue("//mail:e", "d");//
		
		ZAssert.assertEquals(startDate, startUTC.toyyyyMMddT() + "100000", "Verify start time after picking up free time from suggest pane'");
		ZAssert.assertEquals(endDate, endUTC.toyyyyMMddT() + "110000", "Verify end time after picking up free time from suggest pane'");;
		
	}
	
}
