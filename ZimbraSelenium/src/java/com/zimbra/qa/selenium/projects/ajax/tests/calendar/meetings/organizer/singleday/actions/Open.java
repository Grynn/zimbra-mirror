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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.actions;

import java.util.Calendar;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;

public class Open extends CalendarWorkWeekTest {

	public Open() {
		logger.info("New "+ Open.class.getCanonicalName());
		super.startingPage =  app.zPageCalendar;
		super.startingAccountPreferences = null;
	}
	
	@Test(	description = "Rt-click to invite and open it",
			groups = { "smoke" })
	public void OpenMeeting_01() throws HarnessException {
		
		organizerTest = false;
		
		// Creating object for meeting data
		organizerTest = true;
		ZimbraResource location = new ZimbraResource(ZimbraResource.Type.LOCATION);
		ZimbraResource equipment = new ZimbraResource(ZimbraResource.Type.EQUIPMENT);
		
		String tz, apptSubject, apptBody, apptAttendee, apptOptional, apptLocation, apptEquipment;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee = ZimbraAccount.AccountA().EmailAddress;
		apptOptional = ZimbraAccount.AccountB().EmailAddress;
		apptLocation = location.EmailAddress;
		apptEquipment = equipment.EmailAddress;
		
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
                     "<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee + "' d='2'/>" +
                     "<at role='OPT' ptst='NE' rsvp='1' a='" + apptOptional + "' d='2'/>" +
              		 "<at cutype='RES' a='" + apptLocation + "' rsvp='1' role='NON' url='" + apptLocation + "' ptst='AC'/>" +
              		 "<at cutype='RES' a='" + apptEquipment + "' rsvp='1' role='NON' url='" + apptEquipment + "' ptst='AC'/>" +
                     "</inv>" +
                     "<e a='"+ apptAttendee +"' t='t'/>" +
                     "<mp content-type='text/plain'>" +
                     "<content>"+ apptBody +"</content>" +
                     "</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");

        // Open appointment and cancel it
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_OPEN_MENU, apptSubject);
        
        ZAssert.assertEquals(apptForm.zGetApptSubject(), apptSubject, "Verify appointment subject");
        ZAssert.assertTrue(apptForm.zVerifyRequiredAttendee(apptAttendee), "Verify appointment required attendee");
        ZAssert.assertTrue(apptForm.zVerifyOptionalAttendee(apptOptional), "Verify appointment optional attendee");
        ZAssert.assertTrue(apptForm.zVerifyLocation(apptLocation), "Verify appointment location");
        ZAssert.assertTrue(apptForm.zVerifyEquipment(apptEquipment), "Verify appointment equipment");
        ZAssert.assertEquals(apptForm.zGetApptBodyValue(), apptBody, "Verify appointment body");
        
	}
	
}
