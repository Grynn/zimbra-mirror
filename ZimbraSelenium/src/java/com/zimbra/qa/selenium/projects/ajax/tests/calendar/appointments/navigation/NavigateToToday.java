package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.navigation;

import java.util.Calendar;
import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar.Locators;
import java.util.Date;

public class NavigateToToday extends CalendarWorkWeekTest {	
	
	public NavigateToToday() {
		logger.info("New "+ NavigateToToday.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Bugs(ids = "78821")
	@Test(description = "Verify clicking on Today button highlights today when moved to next week",
			groups = { "functional" })
	
	public void NavigateToToday_01() throws HarnessException {
		
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
        
        // navigate to next week and make sure Today is not present 
        app.zPageCalendar.zToolbarPressButton(Button.B_NEXT_WEEK);
        ZAssert.assertFalse(app.zPageCalendar.sIsElementPresent(Locators.TodayHighlighted), "Today is not visible");
        
        // click on Today Button and make sure Today is present 
        app.zPageCalendar.zToolbarPressButton(Button.O_GO_TO_TODAY_MENU);
        ZAssert.assertTrue(app.zPageCalendar.sIsElementPresent(Locators.TodayHighlighted), "Today is visible");
        String todaysDate = app.zPageCalendar.sGetText(Locators.TodayHighlighted);
      
        // Make sure view shows todays date
        String today = todaysDate.replaceAll(",", "");
        Date d = new Date();
        String mydate = d.toString().substring(0,10);
	      
        if(d.toString().charAt(8) == '0'){
	    	   String ad = d.toString().substring(8, 9);
	    	   if(ad.charAt(0)== '0'){
	    		   mydate = d.toString().substring(0,7) + " " + d.toString().charAt(9);
	    	   }   
	       }
        
       ZAssert.assertEquals(today, mydate , "todays date is presnt in the view");
	}
	
	@Test(description = "Verify clicking on Today button highlights today",
			groups = { "functional" })
	
	public void NavigateToToday_02() throws HarnessException {
		
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
        
        // click on Today Button and make sure Today is present 
        app.zPageCalendar.zToolbarPressButton(Button.O_GO_TO_TODAY_MENU);
        ZAssert.assertTrue(app.zPageCalendar.sIsElementPresent(Locators.TodaySelelcted), "Today is visible");
        String todaysDate = app.zPageCalendar.sGetText(Locators.TodaySelelcted);
      
        // Make sure view shows todays date
        String today = todaysDate.replaceAll(",", "");
        Date d = new Date();
        String mydate = d.toString().substring(0,10);
        
       if(d.toString().charAt(8) == '0')
       {
    	   String ad = d.toString().substring(8, 9);
    	   if(ad.charAt(0)== '0'){
    		   mydate = d.toString().substring(0,7) + " " + d.toString().charAt(9);
    	   }   
       }
        ZAssert.assertEquals(today , mydate , "todays date is presnt in the view");
	}
}
