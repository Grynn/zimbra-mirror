/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.bugs;

import java.io.File;
import java.util.List;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.AttachmentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogAddToCalendar;
import com.zimbra.qa.selenium.projects.ajax.ui.SeparateWindow;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;

public class Bug49734 extends PrefGroupMailByMessageTest {
	
	public Bug49734() throws HarnessException {
		logger.info("New "+ Bug49734.class.getCanonicalName());
		super.startingPage =  app.zPageMail;
	}
	
	@Test(description = "Bug 49734 - JS error (t is undefined) while click to 'Add to Calendar' when viewing in separate window",
			groups = { "functional" })
			
	public void Bug49734_01() throws HarnessException {

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
			SleepUtil.sleepVeryLong();
			
			app.zPageCalendar.zWaitForElementAppear("id=MSG-1_2_calendar");
			
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
	
	@Test(description = "Bug 49734 - JS error (t is undefined) while click to 'Add to Calendar' when viewing in separate window",
			groups = { "functional" })
			
	public void Bug49734_02() throws HarnessException {

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

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		SeparateWindow window = (SeparateWindow)app.zPageMail.zToolbarPressButton(Button.B_RFC822_ATTACHMENT_LINK);
				
		try {
			window.zWaitForActive();
			app.zPageMail.zSelectWindow("_blank");
			SleepUtil.sleepVeryLong();
			
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
	
}