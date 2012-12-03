package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.actions;

import java.util.Calendar;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZDate;
import com.zimbra.qa.selenium.framework.util.ZTimeZone;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;

public class ReplyToAll extends CalendarWorkWeekTest {	
	
	public ReplyToAll() {
		logger.info("New "+ ReplyToAll.class.getCanonicalName());
		
	}
	
	@Test(description = "Check when attendees get the reply when organizer ReplyAll to a meeting",
			groups = { "functional" })
	public void ReplyToAll_01() throws HarnessException {
		

		//-- Data Setup

		// Creating object for meeting data
		String tz, apptSubject, apptBody, apptAttendee1;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = "appt" + ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
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
                     "</inv>" +
                     "<e a='"+ apptAttendee1 +"' t='t'/>" +
                     "<mp content-type='text/plain'>" +
                     "<content>"+ apptBody +"</content>" +
                     "</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
		//-- GUI actions	
        // Refresh the view
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
   
        
        // When the form opens, add some text and then Submit
        String replyText= "ReplyBodyByOrganizer";
        FormMailNew mailComposeForm = (FormMailNew)app.zPageCalendar.zListItem(Action.A_RIGHTCLICK,Button.O_REPLY_TO_ALL_MENU, apptSubject);
        mailComposeForm.zFillField(Field.Body, replyText);		
		mailComposeForm.zSubmit();
        
		// Verify the new invitation appears in the inbox
        String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id"); 
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +") content:("+ replyText +")</query>"			
			+	"</SearchRequest>");
		
		id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify the replyall to meeting appears in the attendee's inbox");
		
		
	}
	
	
}
