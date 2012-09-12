package com.zimbra.qa.selenium.projects.ajax.tests.calendar.bugs;

import java.util.Calendar;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZDate;
import com.zimbra.qa.selenium.framework.util.ZTimeZone;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;

public class Bug62137 extends CalendarWorkWeekTest {

	public Bug62137() {
		logger.info("New " + Bug62137.class.getCanonicalName());
		
	}

	@Bugs( ids = "62137")
	@Test(
			description = "Bug 62137 - 'http://<server>?app=calendar' broken with/without login to zcs", 
			groups = { "functional" })
	public void Bug62137_01() throws HarnessException {
		
        
		// Creating object for appointment data
		String tz, apptSubject, apptBody, apptAttendee;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee = ZimbraAccount.AccountA().EmailAddress;
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
        app.zGetActiveAccount().soapSend(
                          "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                               "<m>"+
                               "<inv method='REQUEST' type='event' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                               "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                               "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                               "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                               "<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee + "' d='2'/>" + 
                               "</inv>" +
                               "<mp content-type='text/plain'>" +
                               "<content>"+ apptBody +"</content>" +
                               "</mp>" +
                               "<su>"+ apptSubject +"</su>" +
                               "</m>" +
                         "</CreateAppointmentRequest>");
        
        


        
        // Reload the application, with app=calendar query parameter
        // TODO: need to resolve building the URL ... this test will fail, if there are existing query parameters in the URL
        app.zPageCalendar.sOpen(ZimbraSeleniumProperties.getBaseURL() + "?app=calendar");
        
        
        
        // Verify the page becomes active
        app.zPageMain.zWaitForActive(); 
        app.zPageCalendar.zWaitForActive();
        ZAssert.assertTrue(app.zPageCalendar.zIsActive(), "Verify the page becomes active");
        
        
        
        // Verify the appointment appears in the view
		boolean found = false;
		List<AppointmentItem> items = app.zPageCalendar.zListGetAppointments();
		for (AppointmentItem item : items ) {
			if ( apptSubject.equals(item.getSubject()) ) {
				found = true;
				break;
			}
		}
		
		ZAssert.assertTrue(found, "Verify appt gets displayed in work week view");

		
	}
}