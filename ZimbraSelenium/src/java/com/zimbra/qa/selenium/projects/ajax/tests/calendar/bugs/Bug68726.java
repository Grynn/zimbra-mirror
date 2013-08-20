/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.bugs;

import java.util.Calendar;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;

public class Bug68726 extends CalendarWorkWeekTest {

	public Bug68726() {
		logger.info("New "+ Bug68726.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
		this.startingAccountPreferences.put("zimbraFeatureGroupCalendarEnabled", "FALSE");
	}

	@Bugs(ids = "68726,71103")
	@Test(	
			description = "Appointment creation broken if 'Group Calendar' feature is disabled for calendar (zimbraFeatureGroupCalendarEnabled)",
			groups = { "functional" }	
		)
	public void Bug68726_01() throws HarnessException {
		
		// Data
		String apptSubject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		String apptBody = "content" + ZimbraSeleniumProperties.getUniqueString();
		
		// Modify the test account
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<ModifyAccountRequest xmlns='urn:zimbraAdmin'>"
			+		"<id>"+ app.zGetActiveAccount().ZimbraId +"</id>"
			+		"<a n='zimbraFeatureGroupCalendarEnabled'>FALSE</a>"
			+	"</ModifyAccountRequest>");

		// Logout and login to pick up the changes
		app.zPageLogin.zNavigateTo();
		this.startingPage.zNavigateTo();

		// Create appointment
		AppointmentItem appt = new AppointmentItem();
		Calendar now = this.calendarWeekDayUTC;
		appt.setSubject(apptSubject);
		appt.setContent(apptBody);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0));
	
		// Open the new mail form
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		apptForm.zSubmit();
			
		// Verify the new appointment exists on the server
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")", appt.getStartTime().addDays(-7), appt.getEndTime().addDays(7));
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
		
		// Verify 'Forward' menu remains disabled (bug http://bugzilla.zimbra.com/show_bug.cgi?id=71103)
		app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, apptSubject);
		ZAssert.assertTrue(app.zPageCalendar.zVerifyDisabledControl(Button.O_FORWARD_DISABLED), "Verify 'Forward' menu is disabled");
	}
	
}
