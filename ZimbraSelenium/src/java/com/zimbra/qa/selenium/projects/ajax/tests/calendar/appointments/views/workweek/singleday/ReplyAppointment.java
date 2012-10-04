package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.workweek.singleday;

import java.util.Calendar;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar.*;

public class ReplyAppointment extends CalendarWorkWeekTest {	
	
	
	public ReplyAppointment() {
		logger.info("New "+ ReplyAppointment.class.getCanonicalName());
		
	}
	
	@DataProvider(name = "DataProviderReply")
	public Object[][] DataProviderReply() {
		return new Object[][] {
				new Object[] { Locators.ReplyMenu },
				new Object[] { Locators.ReplyToAllMenu },
		};
	}
	
	@Test(description = "Verify Reply & ReplyAll context menu option for saved appt",
			groups = { "functional" },
			dataProvider = "DataProviderReply")
			
	public void ReplyAppointment_01(String menuName) throws HarnessException {
		
		// Creating object for meeting data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
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
                     "</inv>" +
                     "<mp content-type='text/plain'>" +
                     "<content>"+ apptBody +"</content>" +
                     "</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");

        // Refresh the view
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // Select the appointment
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        
        // Right Click to appt -> check Reply and ReplyAll context menu       
        app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, apptSubject);
        ZAssert.assertEquals(app.zPageCalendar.zIsElementDisabled(menuName), false, "Verify Reply and ReplyAll menu remains disabled");

	}
	
	
}
