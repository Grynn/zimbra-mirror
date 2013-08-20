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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.resources;

import java.awt.event.KeyEvent;
import java.util.Calendar;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogWarningConflictingResources;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Locators;

public class ResourceConflictWhenOOO extends CalendarWorkWeekTest {	
	
	public ResourceConflictWhenOOO() {
		logger.info("New "+ ResourceConflictWhenOOO.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Test(description = "Verify if OOO status of Location causes double booking",
			groups = { "functional" })
			public void LocationConflictWhenOOO_01() throws HarnessException {

		// Creating object for meeting data
		ZimbraResource location = new ZimbraResource(ZimbraResource.Type.LOCATION);

		String tz, apptSubject1,apptSubject2 , apptAttendeeEmail2 , apptAttendeeEmail3 ;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject1 = "app" + ZimbraSeleniumProperties.getUniqueString();
		apptAttendeeEmail2 = ZimbraAccount.AccountB().EmailAddress;
		apptAttendeeEmail3 = ZimbraAccount.AccountC().EmailAddress;
		String apptLocation = location.EmailAddress;
		ZimbraAccount apptAttendee = ZimbraAccount.AccountA();

		String apptContent = ZimbraSeleniumProperties.getUniqueString();
		AppointmentItem appt = new AppointmentItem();
		apptSubject1 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject2 = ZimbraSeleniumProperties.getUniqueString();

		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 18, 0, 0);

		app.zGetActiveAccount().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
				"<m>"+
				"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='O' transp='O' allDay='0' name='"+ apptSubject1 +"' loc='"+ apptLocation+"'>" +
				"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
				"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
				"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendeeEmail2 + "' d='2'/>" +
				"<at cutype='RES' a='" + apptLocation + "' rsvp='1' role='REQ' url='" + apptLocation + "' ptst='NE' fb='O' fba='O'/>" +
				"</inv>" +
				"<e a='"+ apptLocation +"' t='t'/>" +
				"<mp content-type='text/plain'>" +
				"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
				"</mp>" +
				"<su>"+ apptSubject1 +"</su>" +
				"</m>" +
		"</CreateAppointmentRequest>");

		SleepUtil.sleepVeryLong();
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		app.zGetActiveAccount().soapSend(	

				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+		"<query>subject:("+ apptSubject1 +")" + " " + "content:" + apptSubject1 + "</query>"
				+	"</SearchRequest>");
		ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:comp", "fba"), "T", "");

		SleepUtil.sleepVeryLong();

		// Verify location free/busy status shows as psts=AC
		String locationStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptLocation +"']", "ptst");
		ZAssert.assertEquals(locationStatus, "AC", "Verify that the location status shows as 'ACCEPTED'");

		// Logout from organizer and Login as attendee
		app.zPageMain.zLogout();
		app.zPageLogin.zLogin(apptAttendee);
		this.startingPage.zNavigateTo();

		// Create appointment data
		appt.setSubject(apptSubject2);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 10, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0));
		appt.setContent(apptContent);
		appt.setLocation(apptLocation);
		appt.setAttendeeName(apptAttendeeEmail3);

		// Create meeting which has location conflict with above created appointment
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		SleepUtil.sleepVeryLong();

		// Verify the compose page shows note below resource about conflicting resources
		ZAssert.assertTrue(app.zPageCalendar.sIsElementPresent(Locators.ConflictResourceNote),  "Verify that the conflicting resource note appears on appt compose page");
		DialogWarningConflictingResources  dialog = (DialogWarningConflictingResources) app.zPageCalendar.zToolbarPressButton(Button.B_SEND_WITH_CONFLICT);
		String dialogContent = dialog.zGetResourceConflictWarningDialogText();
		ZAssert.assertTrue(dialogContent.contains("The selected resources/location cannot be scheduled for the following instances"), "Verify that the dialog shows expected text");
		ZAssert.assertTrue(dialogContent.contains(apptLocation+"(Busy)"), "Verify that the dialog shows location name on conflict warning");

		// Save appt with location conflict 
		dialog.zClickButton(Button.B_SAVE_WITH_CONFLICT);
		SleepUtil.sleepMedium();

		// Verify that location with conflict and subject are present in the appointment
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject2 +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject2, "Subject: Verify the appointment data");
		ZAssert.assertEquals(appt.getLocation(), apptLocation, "Location: Verify the location is present in the appointment");

		// Verify location free/busy status shows as psts=DE
		String locationStatus2 = ZimbraAccount.AccountA().soapSelectValue("//mail:at[@a='"+ apptLocation +"']", "ptst");
		ZAssert.assertEquals(locationStatus2, "DE", "Verify that the location status shows as 'DECLINED'");

	}
}
