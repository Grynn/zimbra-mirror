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
package com.zimbra.qa.selenium.projects.ajax.tests.zimlets.archive.newwindow;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.SeparateWindowDisplayMail;


public class ArchiveMessage extends PrefGroupMailByMessageTest {

	
	public ArchiveMessage() {
		logger.info("New "+ ArchiveMessage.class.getCanonicalName());
		


	}
	
	// See https://bugzilla.zimbra.com/show_bug.cgi?id=79929
	// Archive button removed from new window
	@Test(	description = "Archive a message",
			groups = { "deprecated" })
	public void ArchiveMessage_01() throws HarnessException {
		
		
		
		//-- DATA setup
		
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String foldername = "archive" + ZimbraSeleniumProperties.getUniqueString();
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.UserRoot);
		
		// Add a message to the inbox
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
        		+		"<m l='"+ inbox.getId() +"' >"
            	+			"<content>From: foo@foo.com\n"
            	+				"To: foo@foo.com \n"
            	+				"Subject: "+ subject +"\n"
            	+				"MIME-Version: 1.0 \n"
            	+				"Content-Type: text/plain; charset=utf-8 \n"
            	+				"Content-Transfer-Encoding: 7bit\n"
            	+				"\n"
            	+				"simple text string in the body\n"
            	+			"</content>"
            	+		"</m>"
				+	"</AddMsgRequest>");

		// Create the destination archive folder
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ foldername +"' l='"+ root.getId() +"'/>" +
                "</CreateFolderRequest>");
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		// Configure the Archive feature
		// It may be difficult to keep these meta data in sync with the app.  If there
		// is a better way to set up archive, please update the test case.
		app.zGetActiveAccount().soapSend(
				"<SetMailboxMetadataRequest xmlns='urn:zimbraMail'>" +
					"<meta section='zwc:archiveZimlet'>" +
						"<a n='hideDeleteButton'>false</a>" +
						"<a n='showSendAndArchive'>false</a>" +
						"<a n='archivedFolder'>"+ subfolder.getId() +"</a>" +
					"</meta>" +
				"</SetMailboxMetadataRequest>");



		
		//-- GUI steps
		
		// Logout and login to pick up the new archive zimlet settings
		ZimbraAccount a = app.zGetActiveAccount();
		app.zPageLogin.zNavigateTo();
		app.zPageLogin.zLogin(a);
		app.zPageMail.zNavigateTo();
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		SeparateWindowDisplayMail window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (SeparateWindowDisplayMail)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			window.zToolbarPressButton(Button.B_ARCHIVE);
			
			// See http://bugzilla.zimbra.com/show_bug.cgi?id=79929
			// Does the separate window close automatically after clicking Archive?
			// Or, does it stay open?
			// Need to update test steps if the behavior changes
			if ( window.zIsActive() ) {
				window.zCloseWindow();
			}
			window = null;
			
		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}



		//-- VERIFICATION
		
		MailItem message = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertNotNull(message, "Verify the archived message still exists in the mailbox");
		ZAssert.assertEquals(message.dFolderId, subfolder.getId(), "Verify the archived message is moved to the archive folder");
		

	}

	@Test(	description = "Verify the 'archive' button is not present in separate window",
			groups = { "functional" })
	public void ArchiveMessage_02() throws HarnessException {
		
		
		
		//-- DATA setup
		
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		
		// Add a message to the inbox
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
        		+		"<m l='"+ inbox.getId() +"' >"
            	+			"<content>From: foo@foo.com\n"
            	+				"To: foo@foo.com \n"
            	+				"Subject: "+ subject +"\n"
            	+				"MIME-Version: 1.0 \n"
            	+				"Content-Type: text/plain; charset=utf-8 \n"
            	+				"Content-Transfer-Encoding: 7bit\n"
            	+				"\n"
            	+				"simple text string in the body\n"
            	+			"</content>"
            	+		"</m>"
				+	"</AddMsgRequest>");




		
		//-- GUI steps
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		SeparateWindowDisplayMail window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (SeparateWindowDisplayMail)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			//-- VERIFICATION
			
			// Verify the 'archive' button is not present
			String locator = "css=div[id^='ztb__MSG'] div[id*='ARCHIVE'] td[id$='_title']";
			boolean present = window.sIsElementPresent(locator);
			
			ZAssert.assertFalse(present, "Verify the 'archive' button is not present");

		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}




	}




}
