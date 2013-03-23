/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.attendee.singleday.viewappt;

import java.util.*;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;

public class Tag extends CalendarWorkWeekTest {

	public Tag() {
		logger.info("New "+ Tag.class.getCanonicalName());
		super.startingPage =  app.zPageCalendar;
		super.startingAccountPreferences = null;
	}
	
	@Bugs(ids = "63455,79016")
	@Test(description = "View meeting invite by opening it and apply tag to the appointment", 
			groups = { "functional" })

	public void TagMeeting_01() throws HarnessException {

		// ------------------------ Test data ------------------------------------

		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptBody = ZimbraSeleniumProperties.getUniqueString();
		String tag = ZimbraSeleniumProperties.getUniqueString();
		
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 16, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 17, 0, 0);


		// --------------- Creating invitation (organizer) ----------------------------

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
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);
		
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ apptSubject + ")" + " " + "content:(" + apptBody +")</query>"
			+	"</SearchRequest>");
		String apptId = ZimbraAccount.AccountA().soapSelectValue("//mail:appt", "invId");
		
		// --------------- Login to attendee & accept invitation ----------------------------------------------------
		app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, Button.O_NEW_TAG, apptSubject);
		DialogTag dialog = new DialogTag(app, startingPage);
        dialog.zSubmit(tag);
        SleepUtil.sleepLong(); //soapSelectValue gives wrong response without delay
        
        app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag +"']", "id");
        
		// http://bugzilla.zimbra.com/show_bug.cgi?id=63455 - Applied tag is not visible in read-only appointment view 
		// http://bugzilla.zimbra.com/show_bug.cgi?id=79016 - Tag dropdown shows blank DWT on first time double clicking to read only appointment 
        //ZAssert.assertTrue(app.zPageCalendar.zVerifyTagBubble(tag), "Verify tag bubble exists in read-only appt view");
        app.zPageCalendar.zToolbarPressButton(Button.B_CLOSE);
        
        app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ apptSubject + ")" + " " + "content:(" + apptBody +")</query>"
			+	"</SearchRequest>");
		apptId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
		
        // Verify applied tag for appointment
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail' id='" + apptId + "'/>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:appt", "t"), tagID, "Verify the appointment is tagged with the correct tag");

	}
	
	@Bugs(ids = "63455,79016")
	@Test(description = "Open meeting invite by double-clicking it, apply tag and remove it later", 
			groups = { "functional" })
			
	public void TagMeeting_02() throws HarnessException {

		// ------------------------ Test data ------------------------------------

		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptBody = ZimbraSeleniumProperties.getUniqueString();
		String tag = ZimbraSeleniumProperties.getUniqueString();
		
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);


		// --------------- Creating invitation (organizer) ----------------------------

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
		
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ apptSubject + ")" + " " + "content:(" + apptBody +")</query>"
			+	"</SearchRequest>");
		String apptId = ZimbraAccount.AccountA().soapSelectValue("//mail:appt", "invId");
		
		// Create tag and apply it to appointment
		app.zPageCalendar.zCreateTag(app, tag, 8);
		app.zGetActiveAccount().soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>" + "<action id='" + apptId +"' op='tag' tn='"+ tag +"'/>" + "</ItemActionRequest>");
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
		// --------------- Login to attendee & accept invitation ----------------------------------------------------

        app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, Button.O_REMOVE_TAG, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		// http://bugzilla.zimbra.com/show_bug.cgi?id=63455 - Applied tag is not visible in read-only appointment view 
		// http://bugzilla.zimbra.com/show_bug.cgi?id=79016 - Tag dropdown shows blank DWT on first time double clicking to read only appointment 
        //ZAssert.assertTrue(app.zPageCalendar.zVerifyTagBubble(tag), "Verify tag bubble exists in read-only appt view");
        app.zPageCalendar.zToolbarPressButton(Button.B_CLOSE);
        
        app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ apptSubject + ")" + " " + "content:(" + apptBody +")</query>"
			+	"</SearchRequest>");
		apptId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
        
        // Verify applied tag for appointment
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail' id='" + apptId + "'/>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:appt", "t"), null, "Verify appointment is not tagged");

	}
	
}