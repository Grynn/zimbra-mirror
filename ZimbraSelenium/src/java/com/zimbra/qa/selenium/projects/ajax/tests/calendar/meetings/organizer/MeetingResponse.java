package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer;

import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;

public class MeetingResponse extends AjaxCommonTest {

	public MeetingResponse() {
		logger.info("New "+ MeetingResponse.class.getCanonicalName());
		
		// All tests start at the Calendar page
		super.startingPage = app.zPageMail;
		
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -6085933426219057063L;
			{
				put("zimbraPrefGroupMailBy", "message");
			}};

	}
	
	@Test(	description = "View the meeting response - Response = Accept",
			groups = { "implement" })
	public void MeetingResponse_01() throws HarnessException {
		
		// Create the appointment on the server
		// Create the message data to be sent
		String subject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		// EST timezone string
		String tz = ZTimeZone.TimeZoneEST.getID();

		// Create an appointment with AccountA
		app.zGetActiveAccount().soapSend(
					"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv>"
				+				"<comp status='CONF' fb='B' class='PUB' transp='O' allDay='0' name='"+ subject +"' >"
				+					"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
				+					"<or a='"+ app.zGetActiveAccount().EmailAddress + "'/>"
				+					"<at role='REQ' ptst='NE' rsvp='1' a='" + ZimbraAccount.AccountA().EmailAddress + "'/>"
				+				"</comp>"
				+			"</inv>"
				+			"<su>"+ subject + "</su>"
				+			"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>"
				+			"<mp ct='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");
		
		// AccountA gets the invitation
		ZimbraAccount.AccountA().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+		"<query>subject:(" + subject +")</query>"
				+	"</SearchRequest>");
		
		String inviteId = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		String inviteCompNum = ZimbraAccount.AccountA().soapSelectValue("//mail:comp", "compNum");
		
		// AccountA accepts
		ZimbraAccount.AccountA().soapSend(
					"<SendInviteReplyRequest xmlns='urn:zimbraMail' id='"+ inviteId +"' compNum='"+ inviteCompNum +"' verb='ACCEPT' updateOrganizer='TRUE'>"
			+			"<m >"
			+				"<e a='"+ app.zGetActiveAccount().EmailAddress +"' t='t'/>"
			+				"<su>Accept: "+ subject +"</su>"
			+				"<mp ct='text/plain'>"
			+					"<content>content</content>"
			+				"</mp>"
			+			"</m>"
			+		"</SendInviteReplyRequest>");
		
		
		
		// Refresh the inbox to get the reply
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);
		
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Wait for a bit so the message can be rendered
		SleepUtil.sleep(5000);

		throw new HarnessException("add verification that the appointment appears");
	    
	}


}
