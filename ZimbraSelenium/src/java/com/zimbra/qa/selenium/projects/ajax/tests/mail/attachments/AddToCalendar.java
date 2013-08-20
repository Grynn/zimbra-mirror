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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.attachments;

import java.io.File;
import java.util.List;
import org.testng.annotations.Test;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogCreateCalendarFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;

public class AddToCalendar extends PrefGroupMailByMessageTest {
	
	public AddToCalendar() throws HarnessException {
		logger.info("New "+ AddToCalendar.class.getCanonicalName());
		super.startingPage =  app.zPageMail;
	}
	
	@Test(	description = "Receive an ics file in the email and add to calendar",
			groups = { "functional" })
			
	public void AddToCalendar_01() throws HarnessException {

		// -- Data Setup
		String apptSubject = "ics appointment";
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email08/mime01.txt";
		final String subject = "Import ics using add to calendar";
		final String attachmentname = "AddToCalendar.ics";
		ZimbraAccount account = app.zGetActiveAccount();
		
		ZDate startUTC = new ZDate(2013, 02, 21, 12, 0, 0);
		ZDate endUTC   = new ZDate(2013, 02, 21, 12, 0, 0);
		
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Calendar);
		
		// Inject the message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Double check that there is an attachment
		account.soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
		String id = account.soapSelectValue("//mail:m", "id");
		
		account.soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail' >"
			+		"<m id='"+ id +"'/>"
			+	"</GetMsgRequest>");
		Element[] nodes = account.soapSelectNodes("//mail:mp[@cd='attachment']");
		ZAssert.assertGreaterThan(nodes.length, 0, "Verify the message has the attachment");


		// -- GUI actions
		
		// Click to Refresh button
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		AttachmentItem item = null;
		List<AttachmentItem> items = display.zListGetAttachments();
		for (AttachmentItem i : items) {
			if ( i.getAttachmentName().equals(attachmentname)) {
				item = i;
				break;
			}
		}
		ZAssert.assertNotNull(item, "Verify one attachment is in the message");
		
		// Click to "Add to Calendar"
		DialogAddToCalendar dialog = (DialogAddToCalendar)display.zListAttachmentItem(Button.B_ADD_TO_CALENDAR, item);
		dialog.zChooseCalendarFolder(folder.getId());
		dialog.zClickButton(Button.B_OK);
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>"+ apptSubject +"</query>"
			+	"</SearchRequest>");
	
		String organizerInvId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
	
		// Get the appointment details
		app.zGetActiveAccount().soapSend(
					"<GetAppointmentRequest xmlns='urn:zimbraMail' id='"+ organizerInvId +"'/>");
		
		String apptName = app.zGetActiveAccount().soapSelectValue("//mail:comp", "name");
		ZAssert.assertEquals(apptName, apptSubject, "Verify correct appointment returned'");
	}
	
	@Test(description = "Bug 49734 - JS error (t is undefined) while click to 'Add to Calendar' when viewing in separate window",
			groups = { "functional" })
			
	public void AddToCalendar_NewWindow_01() throws HarnessException {

		// -- Data Setup
		String subject = "separate window invite ics attachment";
		String apptSubject = "new window invite ics";
		ZDate startUTC = new ZDate(2013, 06, 25, 12, 0, 0);
		ZDate endUTC   = new ZDate(2013, 07, 10, 12, 0, 0);
		final String attachmentname = "separate window.ics";
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Calendar);
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email08/mime03.txt";
		
		// Inject the message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		AttachmentItem item = null;
		List<AttachmentItem> items = display.zListGetAttachments();
		for (AttachmentItem i : items) {
			if ( i.getAttachmentName().equals(attachmentname)) {
				item = i;
				break;
			}
		}
		ZAssert.assertNotNull(item, "Verify one attachment is in the message");
		
		SeparateWindow window = (SeparateWindow)app.zPageMail.zToolbarPressButton(Button.B_LAUNCH_IN_SEPARATE_WINDOW);
		try {
			window.zWaitForActive();
			app.zPageMail.zSelectWindow("_blank");
			SleepUtil.sleepLong();
			
			app.zPageCalendar.zWaitForElementAppear("id=zv__MSG__MSG-1_attLinks_2_calendar");
			
			DialogAddToCalendar dialog = (DialogAddToCalendar)app.zPageMail.zToolbarPressButton(Button.B_ADD_TO_CALENDAR);
			dialog.zChooseCalendarFolder(folder.getId());
			dialog.zClickButton(Button.B_OK);
			SleepUtil.sleepLong(); //sometime client takes longer time to add the appointment
			
        } finally {
        	if ( window != null ) {
        		window.zCloseWindow();
    		}
        	app.zPageMail.zSelectWindow(null);
       	}
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>"+ apptSubject +"</query>"
			+	"</SearchRequest>");
		
		String organizerInvId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
		
		// Get the appointment details
		app.zGetActiveAccount().soapSend(
					"<GetAppointmentRequest xmlns='urn:zimbraMail' id='"+ organizerInvId +"'/>");
		
		String apptName = app.zGetActiveAccount().soapSelectValue("//mail:comp", "name");
		ZAssert.assertEquals(apptName, apptSubject, "Verify correct appointment returned'");
	
	}
	
	@Bugs(ids = "82961")
	@Test(description = "Bug 51442 - Js error (ZmNewCalendarDialog is not defined) while pressing New Calendar from new window and can't open new calendar dialog",
			groups = { "functional" })
			
	public void AddToCalendar_NewWindow_02() throws HarnessException {

		// -- Data Setup
		String subject = "new window invite ics attachment";
		String apptSubject = "new window invite ics";
		ZDate startUTC = new ZDate(2013, 06, 25, 12, 0, 0);
		ZDate endUTC   = new ZDate(2013, 07, 10, 12, 0, 0);
		final String attachmentname = "separate window.ics";
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email08/mime06.txt";
		
		// Inject the message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);
		SleepUtil.sleepMedium();

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		AttachmentItem item = null;
		List<AttachmentItem> items = display.zListGetAttachments();
		for (AttachmentItem i : items) {
			if ( i.getAttachmentName().equals(attachmentname)) {
				item = i;
				break;
			}
		}
		ZAssert.assertNotNull(item, "Verify one attachment is in the message");
		
		SeparateWindow window = (SeparateWindow)app.zPageMail.zToolbarPressButton(Button.B_LAUNCH_IN_SEPARATE_WINDOW);
		try {
			window.zWaitForActive();
			app.zPageMail.zSelectWindow("_blank");
			SleepUtil.sleepLong();
			
			app.zPageCalendar.zWaitForElementAppear("id=zv__MSG__MSG-1_attLinks_2_calendar");
			
			DialogAddToCalendar dialog = (DialogAddToCalendar)app.zPageMail.zToolbarPressButton(Button.B_ADD_TO_CALENDAR);
			dialog.zClickButton(Button.B_NEW);
			
			DialogCreateCalendarFolder createCalendardialog = new DialogCreateCalendarFolder(app, app.zPageMail);
			SleepUtil.sleepSmall();
			if (createCalendardialog.zIsActive()) {
				createCalendardialog.zEnterFolderName(foldername);
				createCalendardialog.zClickButton(Button.B_OK);
			} else {
				throw new HarnessException("New calendar dialog not opened on clicking New button");
			}
			dialog.zClickButton(Button.B_OK);
			SleepUtil.sleepLong(); //sometime client takes longer time to add the appointment
			
        } finally {
        	if ( window != null ) {
        		window.zCloseWindow();
    		}
        	app.zPageMail.zSelectWindow(null);
       	}
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>"+ "in:" + foldername + " " + apptSubject +"</query>"
			+	"</SearchRequest>");
		
		String organizerInvId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
		
		// Get the appointment details
		app.zGetActiveAccount().soapSend(
					"<GetAppointmentRequest xmlns='urn:zimbraMail' id='"+ organizerInvId +"'/>");
		
		String apptName = app.zGetActiveAccount().soapSelectValue("//mail:comp", "name");
		ZAssert.assertEquals(apptName, apptSubject, "Verify correct appointment returned'");
	
	}
	
	@Test(description = "Bug 49734 - JS error (t is undefined) while click to 'Add to Calendar' when viewing in separate window",
			groups = { "functional" })
			
	public void AddToCalendar_rfc822Attachment_01() throws HarnessException {

		// -- Data Setup
		String subject = "rfc822 attachment invite";
		String apptSubject = "rfc822 invite";
		ZDate startUTC = new ZDate(2013, 06, 25, 12, 0, 0);
		ZDate endUTC   = new ZDate(2013, 07, 10, 12, 0, 0);
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Calendar);
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email08/mime04.txt";
		
		// Inject the message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);
		SleepUtil.sleepMedium();

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		SeparateWindow window = (SeparateWindow)app.zPageMail.zToolbarPressButton(Button.B_RFC822_ATTACHMENT_LINK);
				
		try {
			window.zWaitForActive();
			app.zPageMail.zSelectWindow("_blank");
			SleepUtil.sleepLong();
			
			DialogAddToCalendar dialog = (DialogAddToCalendar)app.zPageMail.zToolbarPressButton(Button.B_ADD_TO_CALENDAR);
			dialog.zChooseCalendarFolder(folder.getId());
			dialog.zClickButton(Button.B_OK);
			SleepUtil.sleepLong(); //sometime client takes longer time to add the appointment
			
        } finally {
        	if ( window != null ) {
        		window.zCloseWindow();
    		}
        	app.zPageMail.zSelectWindow(null);
       	}
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>"+ apptSubject +"</query>"
			+	"</SearchRequest>");
		
		String organizerInvId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
		
		// Get the appointment details
		app.zGetActiveAccount().soapSend(
					"<GetAppointmentRequest xmlns='urn:zimbraMail' id='"+ organizerInvId +"'/>");
		
		String apptName = app.zGetActiveAccount().soapSelectValue("//mail:comp", "name");
		ZAssert.assertEquals(apptName, apptSubject, "Verify correct appointment returned'");
	
	}
	
	@Test(description = "Bug 77131 - Cannot 'add to calendar' an ics into a shared calendar",
			groups = { "functional" })
			
	public void AddToCalendar_SharedCalendar_01() throws HarnessException {
		
		// -- Data Setup
		String apptSubject = "ics appointment";
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		String mountpointname = "mountpoint" + ZimbraSeleniumProperties.getUniqueString();
		ZDate startUTC = new ZDate(2013, 02, 21, 12, 0, 0);
		ZDate endUTC   = new ZDate(2013, 02, 21, 12, 0, 0);
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email08/mime05.txt";
		final String subject = "Importing ics using add to calendar";
		final String attachmentname = "AddToCalendar.ics";
		
		FolderItem calendarFolder = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), FolderItem.SystemFolder.Calendar);
		
		// Create a folder to share
		ZimbraAccount.AccountA().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" + calendarFolder.getId() + "' view='appointment'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem folder = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), foldername);
		
		// Share it
		ZimbraAccount.AccountA().soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ folder.getId() +"' op='grant'>"
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='rwidxa' view='appointment'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		
		// Mount it
		app.zGetActiveAccount().soapSend(
					"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointname +"'  rid='"+ folder.getId() +"' zid='"+ ZimbraAccount.AccountA().ZimbraId +"' view='appointment' color='5'/>"
				+	"</CreateMountpointRequest>");

		FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.UserRoot);
		String mountfolderid = app.zGetActiveAccount().soapSelectValue("//mail:link[@name='"+ mountpointname +"']", "id");
		
		// Inject the message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Double check that there is an attachment
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:m", "id");
		
		app.zGetActiveAccount().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail' >"
			+		"<m id='"+ id +"'/>"
			+	"</GetMsgRequest>");
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:mp[@cd='attachment']");
		ZAssert.assertGreaterThan(nodes.length, 0, "Verify the message has the attachment");

		// Select the message so that it shows in the reading pane
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);		
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		AttachmentItem item = null;
		List<AttachmentItem> items = display.zListGetAttachments();
		for (AttachmentItem i : items) {
			if ( i.getAttachmentName().equals(attachmentname)) {
				item = i;
				break;
			}
		}
		ZAssert.assertNotNull(item, "Verify one attachment is in the message");
		
		// Click to "Add to Calendar"
		DialogAddToCalendar dialog = (DialogAddToCalendar)display.zListAttachmentItem(Button.B_ADD_TO_CALENDAR, item);
		dialog.zChooseCalendarFolder(mountfolderid);
		dialog.zClickButton(Button.B_OK);
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>"+ "in:" + mountpointname + " " + apptSubject +"</query>"
			+	"</SearchRequest>");
	
		String organizerInvId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
	
		// Get the appointment details
		app.zGetActiveAccount().soapSend(
					"<GetAppointmentRequest xmlns='urn:zimbraMail' id='"+ organizerInvId +"'/>");
		
		String apptName = app.zGetActiveAccount().soapSelectValue("//mail:comp", "name");
		ZAssert.assertEquals(apptName, apptSubject, "Verify correct appointment returned'");
	}
	
	@Test(description = "Bug 49715 - Links in email messages to .ics files should provide method to add to calendar",
			groups = { "functional" })
			
	public void AddToCalendar_icsLink_01() throws HarnessException {

		// -- Data Setup
		String subject = "test ics";
		String newCalFolder = ZimbraSeleniumProperties.getUniqueString();
		ZDate startUTC = new ZDate(2013, 02, 21, 12, 0, 0);
		ZDate endUTC   = new ZDate(2013, 02, 21, 12, 0, 0);
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email08/mime02.txt";
		
		// Inject the message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		app.zPageMail.zToolbarPressPulldown(Button.B_ICS_LINK_IN_BODY, Button.B_CREATE_NEW_CALENDAR);
		DialogCreateFolder dialog = new DialogCreateFolder(app, app.zPageCalendar);
		dialog.zEnterFolderName(newCalFolder);
		dialog.zClickButton(Button.B_OK);
		SleepUtil.sleepLong(); //client takes longer time

		// Make sure the folder was created on the ZCS server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), newCalFolder);
		ZAssert.assertNotNull(folder, "Verify the new folder is found");
		ZAssert.assertEquals(folder.getName(), newCalFolder, "Verify the server and client folder names match");
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>"+ "in:" + newCalFolder +"</query>"
			+	"</SearchRequest>");
		
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:appt");
		ZAssert.assertGreaterThan(nodes.length, 0, "Verify imported appointments");
	
	}
	
}
