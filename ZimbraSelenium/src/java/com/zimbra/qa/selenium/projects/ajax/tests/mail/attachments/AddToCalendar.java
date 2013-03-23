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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.attachments;

import java.io.File;
import java.util.List;
import org.testng.annotations.Test;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
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
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email08/mime.txt";
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
		dialog.zChooseCalendarFolder(folder);
		dialog.zClickButton(Button.B_OK);
		SleepUtil.sleepMedium();
		
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
