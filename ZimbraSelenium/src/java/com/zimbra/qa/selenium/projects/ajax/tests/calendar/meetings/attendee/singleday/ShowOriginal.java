package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.attendee.singleday;

import java.util.Calendar;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.SeparateWindowShowOriginal;

public class ShowOriginal extends CalendarWorkWeekTest {

	public ShowOriginal() {
		logger.info("New "+ ShowOriginal.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Test(description = "check context menu for Show Original option and check of the its displayed", 
			groups = { "functional" })
	public void ShowOriginal_01() throws HarnessException {
		
		organizerTest = false;
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptBody = ZimbraSeleniumProperties.getUniqueString();

		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);

		ZimbraAccount.AccountA().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
				+				"<s d='"+ startUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<e d='"+ endUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<or a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>"
				+				"<at role='REQ' ptst='NE' rsvp='1' a='" + app.zGetActiveAccount().EmailAddress + "'/>"
				+			"</inv>"
				+			"<e a='"+ app.zGetActiveAccount().EmailAddress +"' t='t'/>"
				+			"<su>"+ apptSubject +"</su>"
				+			"<mp content-type='text/plain'>"
				+				"<content>" + apptBody + "</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");     
		
		// Refresh the view
		// Switch to work week view
        app.zPageCalendar.zToolbarPressPulldown(Button.B_LISTVIEW, Button.O_LISTVIEW_WORKWEEK);
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

        // Open appointment & click context menu 'Show Original' Option
        
        SeparateWindowShowOriginal window = (SeparateWindowShowOriginal)app.zPageCalendar.zListItem(Action.A_RIGHTCLICK,Button.O_SHOW_ORIGINAL_MENU, apptSubject);
		try{
			window.zWaitForActive();		// Make sure the window is there
			SleepUtil.sleepMedium();
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			//Verify content in Print view.
			String body = window.sGetBodyText();
			ZAssert.assertStringContains(body, apptSubject, "Verify subject in Show original view");
			ZAssert.assertStringContains(body, apptBody, "Verify content in Show original view");
			ZAssert.assertStringContains(body, "BEGIN:VCALENDAR", "Verify Begin Header in Show original view");
			ZAssert.assertStringContains(body, "END:VCALENDAR", "Verify Begin Header in Show original view");
			ZAssert.assertStringContains(body, "ORGANIZER:mailto:"+ZimbraAccount.AccountA().EmailAddress,"Verify organizer is present in Show original view");
	    }finally {
   		 if ( window != null )
			 window.zCloseWindow();
		}
        
	}
	
}
