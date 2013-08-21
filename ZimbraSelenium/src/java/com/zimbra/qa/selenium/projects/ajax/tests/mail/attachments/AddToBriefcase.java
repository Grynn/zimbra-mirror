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

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogCreateCalendarFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;

public class AddToBriefcase extends PrefGroupMailByMessageTest {
	
    	private String subject;
    	private String filename;
    	private ZimbraAccount account;

	public AddToBriefcase() throws HarnessException {
		logger.info("New "+ AddToBriefcase.class.getCanonicalName());
		super.startingPage =  app.zPageMail;
		super.startingAccountPreferences.put("zimbraPrefBriefcaseReadingPaneLocation", "bottom");				
	}
	
	@Test(	description = "Add JPG attachment to Briefcase when viewing email in the current window",
			groups = { "functional" })
			
	public void AddToBriefcase_01() throws HarnessException {

		// -- Data Setup
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email09/mime.txt";
		subject = "subject03431362517016470";
		filename = "screenshot.JPG";
		account = app.zGetActiveAccount();
		
		FolderItem folder = FolderItem.importFromSOAP(account, FolderItem.SystemFolder.Briefcase);
		
		// Inject the message
		LmtpInject.injectFile(account.EmailAddress, new File(mimeFile));

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
			if ( i.getAttachmentName().equals(filename)) {
				item = i;
				break;
			}
		}
		ZAssert.assertNotNull(item, "Verify one attachment is in the message");
		
		// Click to "Briefcase"
		DialogAddToBriefcase dialog = (DialogAddToBriefcase)display.zListAttachmentItem(Button.B_BRIEFCASE, item);
		dialog.zChooseBriefcaseFolder(folder.getId());
		dialog.zClickButton(Button.B_OK);
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='document'>"
			+		"<query>"+ filename +"</query>"
			+	"</SearchRequest>");
	
		String name = account.soapSelectValue("//mail:doc", "name");
		
		//Verify the search response returns the file name
		ZAssert.assertNotNull(name,
			"Verify the search response returns the document name");

		//Verify saved to Briefcase file and mail attachment name are matched
		ZAssert.assertEquals(name, filename, "Verify saved to Briefcase mail attachment name through SOAP");
	}
	
	@Test(description = "Add txt attachment to Briefcase when viewing email in a separate window",
			groups = { "functional" })
			
	public void AddToBriefcase_02() throws HarnessException {
	    	// -- Data Setup
 		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email05/mime01.txt";
 		subject = "subject151615738";
 		filename = "file.txt";
 		account = app.zGetActiveAccount();
	 		
 		FolderItem folder = FolderItem.importFromSOAP(account, FolderItem.SystemFolder.Briefcase);
	 		
 		// Inject the message
 		LmtpInject.injectFile(account.EmailAddress, new File(mimeFile));

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
	 		if ( i.getAttachmentName().equals(filename)) {
	 			item = i;
	 			break;
	 		}
	 	}
	 	ZAssert.assertNotNull(item, "Verify one attachment is in the message");
		
	 	//Open message in a separate window
		SeparateWindow window = (SeparateWindow)app.zPageMail.zToolbarPressButton(Button.B_LAUNCH_IN_SEPARATE_WINDOW);
		try {
			window.zWaitForActive();
			app.zPageMail.zSelectWindow("_blank");
			SleepUtil.sleepLong();
			
			app.zPageCalendar.zWaitForElementAppear("css=div[id*=zv__MSG__MSG][id*=_attLinks_]");
			
			DialogAddToBriefcase dialog = (DialogAddToBriefcase)app.zPageMail.zToolbarPressButton(Button.B_BRIEFCASE);
			dialog.zChooseBriefcaseFolder(folder.getId());
			dialog.zClickButton(Button.B_OK);
			SleepUtil.sleepLong(); //sometime client takes longer time to add the file
			
        } finally {
        	if ( window != null ) {
        		window.zCloseWindow();
    		}
        	app.zPageMail.zSelectWindow(null);
       	}
		
		//-- Verification
		account.soapSend(
			"<SearchRequest xmlns='urn:zimbraMail' types='document'>"
			+		"<query>"+ filename +"</query>"
			+	"</SearchRequest>");
			
		String name = account.soapSelectValue("//mail:doc", "name");
				
		//Verify the search response returns the file name
		ZAssert.assertNotNull(name,
			"Verify the search response returns the document name");
		//Verify saved to Briefcase file and mail attachment name are matched
		ZAssert.assertEquals(name, filename, "Verify saved to Briefcase mail attachment name through SOAP");
	}	
	
	@AfterMethod(groups = { "always" })
	public void afterMethod() throws HarnessException {
		logger.info("AfterMethod cleanup ...");
		
		// delete file upon test completion
		app.zPageBriefcase.deleteFileByName(filename);
		
		// delete message
		MailItem received = MailItem.importFromSOAP(account, "in:inbox subject:("+ subject +")");
		account.soapSend(
			"<ItemActionRequest xmlns='urn:zimbraMail'>" +
			"<action op='move' id='"+ received.getId() +"' l='"+ FolderItem.importFromSOAP(account, SystemFolder.Trash).getId() +"'/>" +
			"</ItemActionRequest>");
	}
}
