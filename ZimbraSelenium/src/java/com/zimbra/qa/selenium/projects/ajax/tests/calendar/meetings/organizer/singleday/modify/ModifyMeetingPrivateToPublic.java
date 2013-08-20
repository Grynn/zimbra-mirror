/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.modify;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZDate;
import com.zimbra.qa.selenium.framework.util.ZTimeZone;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar.Locators;

public class ModifyMeetingPrivateToPublic extends CalendarWorkWeekTest {
	
	public ModifyMeetingPrivateToPublic() {
		logger.info("New " + ModifyMeetingPrivateToPublic.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;

	}
	
	@Test( description = "Modify Meeting class from Private to Public", 
			groups = { "functional" })
	public void ModifyMeetingPrivateToPublic_01() throws HarnessException {

		// Creating object for meeting data
		String tz = ZTimeZone.TimeZoneEST.getID();
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		AppointmentItem appt = new AppointmentItem();
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 18, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PRI' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" +
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // Verify the Original Meeting is a Private Meeting and has Private icon
        ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +")" + " " + "content:" + apptSubject + "</query>"
			+	"</SearchRequest>");
		ZAssert.assertEquals(ZimbraAccount.AccountA().soapSelectValue("//mail:comp", "class"), "PRI", "");
		ZAssert.assertTrue(app.zPageCalendar.sIsElementPresent(Locators.ImgPrivateAppt), "Private Image is present on the meeting");
		
        // Open Meeting change the class from Private to Public and Send it
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_OPEN, apptSubject);
        appt.setIsPrivate(false);
        apptForm.zFill(appt);
        apptForm.zToolbarPressButton(Button.B_SEND);
        SleepUtil.sleepMedium();
        
        // Verify the Meeting is now a Public Meeting and does not have Private icon
        ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +")" + " " + "content:" + apptSubject + "</query>"
			+	"</SearchRequest>");
		ZAssert.assertEquals(ZimbraAccount.AccountA().soapSelectValue("//mail:comp", "class"), "PUB", "");
		ZAssert.assertFalse(app.zPageCalendar.sIsElementPresent(Locators.ImgPrivateAppt), "Private Image is Not present");
	}
	
	@Test( description = "Modify Meeting class from Public to Private", 
			groups = { "functional" })
	public void ModifyMeetingPublicToPrivate_01() throws HarnessException {

		// Creating object for meeting data
		String tz = ZTimeZone.TimeZoneEST.getID();
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		AppointmentItem appt = new AppointmentItem();
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 18, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" +
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // Verify the Original Meeting is a public Meeting and does not have Private icon on appt
        ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +")" + " " + "content:" + apptSubject + "</query>"
			+	"</SearchRequest>");
		ZAssert.assertEquals(ZimbraAccount.AccountA().soapSelectValue("//mail:comp", "class"), "PUB", "");
		ZAssert.assertFalse(app.zPageCalendar.sIsElementPresent(Locators.ImgPrivateAppt), "Private Image is Not present");
		
		
        // Open Meeting change the class from Public to Private and save it
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_OPEN, apptSubject);
        appt.setIsPrivate(true);
        apptForm.zFill(appt);
        apptForm.zToolbarPressButton(Button.B_SEND);
        SleepUtil.sleepMedium();
           
        // Verify the Meeting is now a private Meeting and has Private icon
        ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ apptSubject +")" + " " + "content:" + apptSubject + "</query>"
			+	"</SearchRequest>");
		ZAssert.assertEquals(ZimbraAccount.AccountA().soapSelectValue("//mail:comp", "class"), "PRI", "");
		ZAssert.assertTrue(app.zPageCalendar.sIsElementPresent(Locators.ImgPrivateAppt), "Private Image is present");
	}
	
}