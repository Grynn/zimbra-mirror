package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.attendee.invitations.message;

import java.util.Calendar;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.PageMail.Locators;

public class DeclineMeeting extends AjaxCommonTest {

	public DeclineMeeting() {
		logger.info("New "+ DeclineMeeting.class.getCanonicalName());
		
		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;
	}
	
	@Test(description = "Decline meeting", groups = { "smoke" })
	public void DeclineMeeting_01() throws HarnessException {
		
		// ------------------------ Test data ------------------------------------
		
		ZimbraAccount organizer;
		organizer = app.zGetActiveAccount();
		
		String apptSubject, apptBody, apptAttendee1, declineSubject;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		declineSubject = "Decline: " + apptSubject;
		
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		
		// --------------- Creating invitation (organizer) ----------------------------
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     "<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     "<s d='"+ startUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>" +
                     "<e d='"+ endUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>" +
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

		// --------------- Login to attendee & decline invitation ----------------------------------------------------

        // Logout to organizer and login to attendee
		startingAccountPreferences.put("zimbraPrefGroupMailBy", "message");
		ModifyAccountPreferences(ZimbraAccount.AccountA().ZimbraId);
		app.zPageLogin.zLoginTo(ZimbraAccount.AccountA());
		
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		app.zPageMail.zListItem(Action.A_LEFTCLICK, apptSubject);
		SleepUtil.sleepMedium();
        app.zPageMail.zToolbarPressButton(Button.B_DECLINE);
        SleepUtil.sleepMedium();
        
        // ---------------- Verification at organizer & invitee side both -------------------------------------       
        
        // --- Verification at invitee side ---
        
        // Verify invitation message goes away after declining appointment
        mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "in:" + DoubleQuoteChar + "inbox" + DoubleQuoteChar + " " + DoubleQuoteChar + declineSubject + DoubleQuoteChar);
        ZAssert.assertNull(mail, "Verify invitation message goes away after declining appointment");
        
        // Verify Accept/Decline button goes away after declining appointment
        ZAssert.assertEquals(app.zPageMail.sIsElementPresent(Locators.DeclineButton), false, "Verify Accept/Decline button goes away after declining appointment");
        
        // Verify sent mail in Sent folder and verify it doesn't contain Accept/Decline buttons
        app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Sent));
        app.zPageMail.zListItem(Action.A_LEFTCLICK, declineSubject);
        ZAssert.assertEquals(app.zPageMail.sIsElementPresent(Locators.DeclineButton), false, "Verify Accept/Decline button not present in sent folder");

        // --- Verification at organizer side ---
        
        // Verify declined confirmation message
        SleepUtil.sleepVeryLong();
        mail = MailItem.importFromSOAP(organizer, "in:" + DoubleQuoteChar + "inbox" + DoubleQuoteChar + " " + DoubleQuoteChar + declineSubject + DoubleQuoteChar);
		ZAssert.assertNotNull(mail, "Verify declined message in organizer's calendar");
	}

}
