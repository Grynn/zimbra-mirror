package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.attendee.singleday.actions;

import java.util.Calendar;
import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;

public class ReplyToAll extends CalendarWorkWeekTest {	
	
	public ReplyToAll() {
		logger.info("New "+ ReplyToAll.class.getCanonicalName());
	    super.startingPage =  app.zPageCalendar;
	    super.startingAccountPreferences = null;
	}

	@Test(description = "Verify organizer and rest of the attendee receives message while one of the attendee replies to all",
			groups = { "functional" })
	public void ReplyToAll_01() throws HarnessException {
		
		String apptSubject = "appointment" + ZimbraSeleniumProperties.getUniqueString();

		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);

		// Get meeting invite where it has 2 attendees
		ZimbraAccount.AccountA().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
				+				"<s d='"+ startUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<e d='"+ endUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<or a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>"
				+				"<at role='REQ' ptst='NE' rsvp='1' a='" + app.zGetActiveAccount().EmailAddress + "'/>"
				+				"<at role='REQ' ptst='NE' rsvp='1' a='" + ZimbraAccount.AccountB().EmailAddress + "'/>"
				+			"</inv>"
				+			"<e a='"+ app.zGetActiveAccount().EmailAddress +"' t='t'/>"
				+			"<e a='" + ZimbraAccount.AccountB().EmailAddress +"' t='t'/>"
				+			"<su>"+ apptSubject +"</su>"
				+			"<mp content-type='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");        

		

		// --------------- replyAll to invitation ----------------------------------------------------
		//-- GUI actions	
        // Refresh the view
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
  
        
        // When the form opens, add some text and then Submit
        String replyText= "ReplyAllByAttendee";
        FormMailNew mailComposeForm = (FormMailNew)app.zPageCalendar.zListItem(Action.A_RIGHTCLICK,Button.O_REPLY_TO_ALL_MENU, apptSubject);
        mailComposeForm.zFillField(Field.Body, replyText);		
		mailComposeForm.zSubmit();
		
		// Verify the reply appears in the inbox at organizer
        String idA = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id"); 
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +") content:("+ replyText +")</query>"			
			+	"</SearchRequest>");
		idA = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(idA, "Verify the replyall to meeting appears in the organizer's inbox");
		
		// Verify the reply appears in the inbox at attendee 2
		String idB = ZimbraAccount.AccountB().soapSelectValue("//mail:m", "id"); 
		ZimbraAccount.AccountB().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +") content:("+ replyText +")</query>"			
			+	"</SearchRequest>");

		idB = ZimbraAccount.AccountB().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(idB, "Verify the replyall to meeting appears in the attendee 2's inbox");
			
	}
}
