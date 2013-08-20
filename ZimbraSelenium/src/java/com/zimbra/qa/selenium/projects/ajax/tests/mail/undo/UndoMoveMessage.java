/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.undo;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;


public class UndoMoveMessage extends PrefGroupMailByMessageTest {

	
	public UndoMoveMessage() {
		logger.info("New "+ UndoMoveMessage.class.getCanonicalName());
		
		
		

		
	}
	
	@Test(	description = "Undo - Move a mail by selecting message, then clicking toolbar 'Move' button",
			groups = { "functional" })
	public void Undo_MoveMail_01() throws HarnessException {

		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		String foldername = "folder"+ ZimbraSeleniumProperties.getUniqueString();

		// Create a subfolder to move the message into
		// i.e. Inbox/subfolder
		//
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='" + foldername +"' l='"+ inbox.getId() +"'/>" +
					"</CreateFolderRequest>");
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		// Send a message to the account
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" 
			+		"<m l='" + inbox.getId() + "'>"
			+			"<content>"
			+				"From: foo@foo.com\n" 
			+ 				"To: foo@foo.com \n"
			+				"Subject: " + subject + "\n" 
			+ 				"MIME-Version: 1.0 \n"
			+				"Content-Type: text/plain; charset=utf-8 \n"
			+				"Content-Transfer-Encoding: 7bit\n" 
			+				"\n"
			+				"content \n"
			+				"\n"
			+				"\n"
			+			"</content>"
			+		"</m>"
			+	"</AddMsgRequest>");

		// Get the mail item for the new message
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertNotNull(mail, "Verify the message was created");

		
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Click move -> subfolder
		app.zPageMail.zToolbarPressPulldown(Button.B_MOVE, subfolder);

		MailItem moved = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertEquals(moved.dFolderId, subfolder.getId(), "Verify the message is moved to the subfolder");

		
		// Click undo
		Toaster toaster = app.zPageMain.zGetToaster();
		toaster.zClickUndo();
		
		
		
		// Verify the message is back in the correct folder
		MailItem undone = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertEquals(undone.dFolderId, inbox.getId(), "Verify the message is moved back to the inbox");
		
	}


}
