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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.attributes.zimbraPrefShowSelectionCheckboxTrue;

import org.testng.annotations.*;

import com.zimbra.common.soap.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class MoveMessage extends PrefGroupMailByMessageTest {

	@AfterMethod( groups = { "always" } )
	public void afterMethod() throws HarnessException {
		logger.info("Checking for the Move Dialog ...");

		// Check if the "Move Dialog is still open
		DialogMove dialog = new DialogMove(app, ((AppAjaxClient)app).zPageMail);
		if ( dialog.zIsActive() ) {
			logger.warn(dialog.myPageName() +" was still active.  Cancelling ...");
			dialog.zClickButton(Button.B_CANCEL);
		}
		
	}
	
	public MoveMessage() {
		logger.info("New "+ MoveMessage.class.getCanonicalName());
		
		
		super.startingAccountPreferences.put("zimbraPrefShowSelectionCheckbox", "TRUE");
		super.startingAccountPreferences.put("zimbraPrefItemsPerVirtualPage", "10");

		
	}
	
	
	

	@Test(	description = "Move all mails by selecting 'select all', then clicking toolbar 'Move' button",
			groups = { "functional" })
	public void MoveMessage_01() throws HarnessException {

		
		//-- DATA
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		// Create 5 emails in the inbox
		for (int i = 0; i < 5; i++) {
			
			// Send a message to the account
			app.zGetActiveAccount().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
	        		+		"<m l='"+ inbox.getId() +"' >"
	            	+			"<content>From: foo@foo.com\n"
	            	+				"To: foo@foo.com \n"
	            	+				"Subject: "+ subject +" index"+ i +"\n"
	            	+				"MIME-Version: 1.0 \n"
	            	+				"Content-Type: text/plain; charset=utf-8 \n"
	            	+				"Content-Transfer-Encoding: 7bit\n"
	            	+				"\n"
	            	+				"simple text string in the body\n"
	            	+			"</content>"
	            	+		"</m>"
					+	"</AddMsgRequest>");
			
		}
		
		// Create a subfolder to move the message into
		// i.e. Inbox/subfolder
		//
		String foldername = "folder"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='" + foldername +"' l='"+ inbox.getId() +"'/>" +
					"</CreateFolderRequest>");
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);


		

		//-- GUI

		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select all
		app.zPageMail.zToolbarPressButton(Button.B_SELECT_ALL);
				
		// Click move -> subfolder
		app.zPageMail.zToolbarPressPulldown(Button.B_MOVE, subfolder);

		
		//-- VERIFICATION
		
		// Verify no messages remain in the inbox
		app.zGetActiveAccount().soapSend(
                "<SearchRequest xmlns='urn:zimbraMail' types='message'>" +
                   "<query>in:inbox subject:("+ subject +")</query>" +
                "</SearchRequest>");
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:m");
		ZAssert.assertEquals(nodes.length, 0, "Verify 0 messages remain in the inbox");

		
	}


	@Test(	description = "Move all mails by selecting 'shift-select all', then clicking toolbar 'Move' button",
			groups = { "functional" })
	public void MoveMessage_02() throws HarnessException {

		
		//-- DATA
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		// Create 15 emails in the inbox
		for (int i = 0; i < 25; i++) {
			
			// Send a message to the account
			app.zGetActiveAccount().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
	        		+		"<m l='"+ inbox.getId() +"' >"
	            	+			"<content>From: foo@foo.com\n"
	            	+				"To: foo@foo.com \n"
	            	+				"Subject: "+ subject +" index"+ i +"\n"
	            	+				"MIME-Version: 1.0 \n"
	            	+				"Content-Type: text/plain; charset=utf-8 \n"
	            	+				"Content-Transfer-Encoding: 7bit\n"
	            	+				"\n"
	            	+				"simple text string in the body\n"
	            	+			"</content>"
	            	+		"</m>"
					+	"</AddMsgRequest>");
			
		}
		
		// Create a subfolder to move the message into
		// i.e. Inbox/subfolder
		//
		String foldername = "folder"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='" + foldername +"' l='"+ inbox.getId() +"'/>" +
					"</CreateFolderRequest>");
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);


		

		//-- GUI

		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select all
		app.zPageMail.zToolbarPressButton(Button.B_SHIFT_SELECT_ALL);
				
		// Click move -> subfolder
		app.zPageMail.zToolbarPressPulldown(Button.B_MOVE, subfolder);

		
		//-- VERIFICATION
		
		// Verify no messages remain in the inbox
		app.zGetActiveAccount().soapSend(
                "<SearchRequest xmlns='urn:zimbraMail' types='message'>" +
                   "<query>in:inbox subject:("+ subject +")</query>" +
                "</SearchRequest>");
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:m");
		ZAssert.assertEquals(nodes.length, 0, "Verify 0 messages remain in the inbox");

		
	}



}
