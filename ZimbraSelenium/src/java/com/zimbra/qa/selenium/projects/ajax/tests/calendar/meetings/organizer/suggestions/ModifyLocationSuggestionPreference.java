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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.suggestions;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogSuggestionPreferences;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;

public class ModifyLocationSuggestionPreference extends CalendarWorkWeekTest {

	public ModifyLocationSuggestionPreference() {
		logger.info("New "+ ModifyLocationSuggestionPreference.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
		
	@Test(description = "Create a meeting invite by modifying location suggestion preferences",
			groups = { "functional" })
			
	public void ModifyLocationSuggestionPreference_01() throws HarnessException {
		
		// Create appointment data
		AppointmentItem appt = new AppointmentItem();
		ZimbraResource location1 = new ZimbraResource(ZimbraResource.Type.LOCATION);
		ZimbraResource location2 = new ZimbraResource(ZimbraResource.Type.LOCATION);
		
		String apptSubject, apptAttendee, apptLocation1, apptLocation2, apptContent;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee = ZimbraAccount.AccountA().EmailAddress;
		apptContent = ZimbraSeleniumProperties.getUniqueString();
		apptLocation1 = location1.EmailAddress;
		apptLocation2 = location2.EmailAddress;
		
		appt.setSubject(apptSubject);
		appt.setAttendees(apptAttendee);
		appt.setContent(apptContent);
	
		// Compose appointment and send it to invitee
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		
		apptForm.zToolbarPressPulldown(Button.B_OPTIONS, Button.O_SUGGESTION_PREFERENCES);
		DialogSuggestionPreferences dialogSuggestionPref = (DialogSuggestionPreferences) new DialogSuggestionPreferences(app, app.zPageCalendar);
		dialogSuggestionPref.zType(Button.F_NAME_EDIT_FIELD, apptLocation1);
		dialogSuggestionPref.zClickButton(Button.B_OK);
		
		// Verify apptLocation1 shows & apptLocation2 doesn't show in suggestion pane
		apptForm.zToolbarPressButton(Button.B_SUGGESTALOCATION);
		ZAssert.assertEquals(apptForm.zIsLocationExistsInSuggestPane(apptLocation1), true, "Verify location1 is showing according to location preference");
		ZAssert.assertEquals(apptForm.zIsLocationExistsInSuggestPane(apptLocation2), false, "Verify location2 is not showing according to location preference");
		
		apptForm.zPressButton(Button.B_SUGGESTEDLOCATION, apptLocation1);
		apptForm.zSubmit();
		SleepUtil.sleepLong(); //location shows NE instead of AC without sleep
		
		// Verify appointment exists on the server
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")");
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
		ZAssert.assertEquals(actual.getAttendees(), apptAttendee, "Attendees: Verify the appointment data");
		ZAssert.assertEquals(actual.getLocation(), apptLocation1, "Location: Verify the appointment data");
		ZAssert.assertEquals(actual.getContent(), appt.getContent(), "Content: Verify the appointment data");
		
		// Verify location status shows as ACCEPTED
		String locationStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptLocation1 +"']", "ptst");
		ZAssert.assertEquals(locationStatus, "AC", "Verify location status shows accepted");

		// Verify the attendee receives the meeting
		AppointmentItem received = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ appt.getSubject() +")");
		ZAssert.assertNotNull(received, "Verify the new appointment is created");
		ZAssert.assertEquals(received.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
		ZAssert.assertEquals(received.getAttendees(), apptAttendee, "Attendees: Verify the appointment data");
		ZAssert.assertEquals(actual.getLocation(), apptLocation1, "Location: Verify the appointment data");
		ZAssert.assertEquals(received.getContent(), appt.getContent(), "Content: Verify the appointment data");

		// Verify the attendee receives the invitation
		MailItem invite = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ appt.getSubject() +")");
		ZAssert.assertNotNull(invite, "Verify the invite is received");
		ZAssert.assertEquals(invite.dSubject, appt.getSubject(), "Subject: Verify the appointment data");
		
	}
	
}
