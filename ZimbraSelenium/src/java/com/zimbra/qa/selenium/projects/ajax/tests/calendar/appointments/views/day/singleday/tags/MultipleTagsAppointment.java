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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.day.singleday.tags;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.HashMap;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogDeleteTag;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogRenameTag;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogConfirmDeleteAppointment;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Locators;

@SuppressWarnings("unused")
public class MultipleTagsAppointment extends AjaxCommonTest {

	public MultipleTagsAppointment() {
		logger.info("New "+ MultipleTagsAppointment.class.getCanonicalName());

		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -2913827779459595178L;
		{
		    put("zimbraPrefCalendarInitialView", "day");
		}};
	}

	@Test(description = "Apply multiple tags to appointment using toolbar button in day view and remove all tags in day view",
			groups = { "functional" })
	public void MultipleTagsAppointment_01() throws HarnessException {
		
		// Create objects
		String tz, apptSubject, apptBody, tag1, tag2, tagID1, tagID2;
		TagItem getTag1, getTag2;
		tz = ZTimeZone.TimeZoneEST.getID();
		tag1 = ZimbraSeleniumProperties.getUniqueString();
		tag2 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Create new appointment
		AppointmentItem appt = AppointmentItem.createAppointmentSingleDay(app.zGetActiveAccount(), Calendar.getInstance(), 120, null, apptSubject, apptBody, null, null);
        String apptId = appt.dApptID;
        
        // Create new tags and get tag IDs
		app.zPageCalendar.zCreateTag(app, tag1, 5);
		app.zPageCalendar.zCreateTag(app, tag2, 6);
		getTag1 = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		getTag2 = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag2);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		tagID1  = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		tagID2 = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag2 +"']", "id");
		
		// Apply multiple tags to appointment
		app.zGetActiveAccount().soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>" + "<action id='" + apptId +"' op='tag' tn='"+ tag1 +"'/>" + "</ItemActionRequest>");
		app.zGetActiveAccount().soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>" + "<action id='" + apptId +"' op='tag' tn='"+ tag2 +"'/>" + "</ItemActionRequest>");
        SleepUtil.sleepSmall();
        
        // Verify applied tags        
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail' id='" + apptId + "'/>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:appt", "t"), tagID1 + "," + tagID2, "Verify the appointment is tagged with the correct tag");
		
		// Verify search result from UI
		app.zTreeCalendar.zTreeItem(Action.A_LEFTCLICK, tag1);
		ZAssert.assertEquals(app.zPageCalendar.zIsAppointmentExists(apptSubject), true, "Verify search result after clicking to tag1");
		app.zTreeCalendar.zTreeItem(Action.A_LEFTCLICK, tag2);
		ZAssert.assertEquals(app.zPageCalendar.zIsAppointmentExists(apptSubject), true, "Verify search result after clicking to tag2");
	}
	
}
