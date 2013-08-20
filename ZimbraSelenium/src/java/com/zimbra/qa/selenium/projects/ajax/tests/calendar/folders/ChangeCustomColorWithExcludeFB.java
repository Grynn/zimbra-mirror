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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.folders;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder.FolderColor;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder.Locators;
import com.zimbra.qa.selenium.projects.ajax.core.*;
public class ChangeCustomColorWithExcludeFB extends CalendarWorkWeekTest {

	public ChangeCustomColorWithExcludeFB() {
		logger.info("New " + ChangeCustomColorWithExcludeFB.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageCalendar;
		super.startingAccountPreferences = null;

	}

	@Test(	description = "Verify user can set custom color and marking ON to 'Exclude this Calendar' checkbox.", 
			groups = { "functional" })
	public void ChangeCustomColorWithExcludeFB_01() throws HarnessException {

		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		ZAssert.assertNotNull(root, "Verify the inbox is available");

		// Create the subfolder
		String name = "calendar" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + name + "' l='" + root.getId() + "' view='appointment'/>"
				+	"</CreateFolderRequest>");

		FolderItem calendar = FolderItem.importFromSOAP(app .zGetActiveAccount(), name);
		ZAssert.assertNotNull(calendar, "Verify the subfolder is available");

		// Click on Get Mail to refresh the folder list
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		// Change color of the folder to custom color using context menu
		DialogEditFolder dialog = (DialogEditFolder) app.zTreeCalendar.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, calendar);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");

		// Change the color, click OK
		dialog.zSetNewColor(FolderColor.MoreColors);
		dialog.zExcludeFBEnable();
		dialog.zClickButton(Button.B_OK);

		// Verify that the custom color has been applied to the folder
	    dialog = (DialogEditFolder) app.zTreeCalendar.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, calendar);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");
		
		//check the label on the properties for folder color & Verify the calendar is excluded from FB
		ZAssert.assertTrue(dialog.sIsChecked(Locators.zExcludeFB) ,"Verify the calendar is excluded from FB");
		ZAssert.assertNotNull(Locators.zSetCustom, "Verify the color of the folder is set to Custom color");
		
		dialog.zClickButton(Button.B_OK);
		
		
		
		
		// Create appointment data 
		ZimbraAccount organizer,apptAttendee2;
		String apptAttendee1EmailAddress; 
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptContent = ZimbraSeleniumProperties.getUniqueString();
		
		String apptSubject1 = ZimbraSeleniumProperties.getUniqueString();
		String apptContent1 = ZimbraSeleniumProperties.getUniqueString();
		
		organizer = app.zGetActiveAccount();
		apptAttendee1EmailAddress = ZimbraAccount.AccountB().EmailAddress;
		
		apptAttendee2 = ZimbraAccount.AccountA();
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		AppointmentItem appt = new AppointmentItem();
		
		// Create appointment data
		appt.setSubject(apptSubject);
		appt.setAttendees(apptAttendee1EmailAddress);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 17, 0, 0));
		appt.setFolder(name);
		appt.setContent(apptContent);
	
		// Create meeting in the calndar folder which has property 'Exclude this Calendar' ON so that bf status is not conveyed from this calendar
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		apptForm.zSubmit();
		
		// Logout from organizer and Login as attendee
		app.zPageMain.zLogout();
		app.zPageLogin.zLogin(apptAttendee2);
		
		this.startingPage.zNavigateTo();
		
		// Create appointment data
		AppointmentItem appt1 = new AppointmentItem();
		appt1.setSubject(apptSubject1);
		appt1.setAttendees(organizer.EmailAddress);
		appt1.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0));
		appt1.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 17, 0, 0));
		appt1.setContent(apptContent1);
	
		// Create meeting with attendee who was organizer of the earlier appt and does have conflicing appt 
		FormApptNew apptForm2 = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm2.zFill(appt1);
		
		// Verify the conflicting attendee note does not appear on appt compose page 
		ZAssert.assertFalse(app.zPageCalendar.sIsVisible(Locators.ConflictAttendeeNote),  "Verify that the conflicting attendee note does not appear on appt compose page");	
		apptForm.zSubmit();
	
		// Verify appointment is present in attendee1's calendar
		AppointmentItem addeddAttendee = AppointmentItem.importFromSOAP(organizer, "subject:("+ apptSubject1 +")");
		ZAssert.assertNotNull(addeddAttendee, "Verify meeting invite is present in recipients calendar");
	
	}

	@Test(	description = "Verify user can set custom color and marking OFF to 'Exclude this Calendar' checkbox.", 
			groups = { "functional" })
			public void ChangeCustomColorWithExcludeFB_02() throws HarnessException {

		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		ZAssert.assertNotNull(root, "Verify the inbox is available");

		// Create the subfolder
		String name = "calendar" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + name + "' l='" + root.getId() + "' view='appointment'/>"
				+	"</CreateFolderRequest>");

		FolderItem calendar = FolderItem.importFromSOAP(app .zGetActiveAccount(), name);
		ZAssert.assertNotNull(calendar, "Verify the subfolder is available");

		// Click on Get Mail to refresh the folder list
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		// Change color of the folder to custom color using context menu
		DialogEditFolder dialog = (DialogEditFolder) app.zTreeCalendar.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, calendar);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");

		// Change the color, click OK
		dialog.zSetNewColor(FolderColor.MoreColors);
		dialog.zExcludeFBDisable();
		dialog.zClickButton(Button.B_OK);

		// Verify that the custom color has been applied to the folder
		dialog = (DialogEditFolder) app.zTreeCalendar.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, calendar);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");

		//check the label on the properties for folder color & Verify the calendar is excluded from FB
		ZAssert.assertFalse(dialog.sIsChecked(Locators.zExcludeFB) ,"Verify the calendar is excluded from FB");
		ZAssert.assertNotNull(Locators.zSetCustom, "Verify the color of the folder is set to Custom color");

		dialog.zClickButton(Button.B_OK);

		// Create appointment data 
		ZimbraAccount organizer,apptAttendee2;
		String apptAttendee1EmailAddress; 
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptContent = ZimbraSeleniumProperties.getUniqueString();

		String apptSubject1 = ZimbraSeleniumProperties.getUniqueString();
		String apptContent1 = ZimbraSeleniumProperties.getUniqueString();

		organizer = app.zGetActiveAccount();
		apptAttendee1EmailAddress = ZimbraAccount.AccountB().EmailAddress;
		apptAttendee2 = ZimbraAccount.AccountA();
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		AppointmentItem appt = new AppointmentItem();

		// Create meeting in the calndar folder which has property 'Exclude this Calendar' OFF so that bf status is conveyed from this calendar
		appt.setSubject(apptSubject);
		appt.setAttendees(apptAttendee1EmailAddress);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 9, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 10, 0, 0));
		appt.setFolder(name);
		appt.setContent(apptContent);

		// Create meeting with attendee who was organizer of the earlier appt and does have conflicing appt 
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		apptForm.zSubmit();

		// Logout from organizer and Login as attendee
		app.zPageMain.zLogout();
		app.zPageLogin.zLogin(apptAttendee2);

		this.startingPage.zNavigateTo();

		// Create appointment data
		AppointmentItem appt1 = new AppointmentItem();
		appt1.setSubject(apptSubject1);
		appt1.setAttendees(organizer.EmailAddress);
		appt1.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 9, 0, 0));
		appt1.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 10, 0, 0));
		appt1.setContent(apptContent1);

		// Create meeting with attendee who was organizer of the earlier appt and does have conflicing appt 
		FormApptNew apptForm2 = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm2.zFill(appt1);

		// Verify the conflicting attendee note does not appear on appt compose page 
		ZAssert.assertTrue(app.zPageCalendar.sIsVisible(Locators.ConflictAttendeeNote),  "Verify that the conflicting attendee note appears on appt compose page");	
		apptForm.zSubmit();

		// Verify appointment is present in attendee1's calendar
		AppointmentItem addeddAttendee = AppointmentItem.importFromSOAP(organizer, "subject:("+ apptSubject1 +")");
		ZAssert.assertNotNull(addeddAttendee, "Verify meeting invite is present in recipients calendar");

	}
}