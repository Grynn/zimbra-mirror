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


public class UndoDeleteMail extends PrefGroupMailByMessageTest {

	public UndoDeleteMail() {
		logger.info("New "+ UndoDeleteMail.class.getCanonicalName());
		
		
		

		
	}
	
	@Test(	description = "Undo a delete",
			groups = { "functional" })
	public void Undo_DeleteMail_01() throws HarnessException {
		
		// Create the message to be deleted/undone
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Trash);
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
				
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
		
		MailItem message = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +") is:anywhere");
		ZAssert.assertNotNull(message, "Verify the message was created");
	
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Click delete
		app.zPageMail.zToolbarPressButton(Button.B_DELETE);
		
		MailItem deleted = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +") is:anywhere");
		ZAssert.assertEquals(deleted.dFolderId, trash.getId(), "Verify message is in trash");

		// Click "undo"
		Toaster toast = app.zPageMain.zGetToaster();	
		toast.zClickUndo();
		
		MailItem undone = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +") is:anywhere");
		ZAssert.assertEquals(undone.dFolderId, inbox.getId(), "Verify message is back in inbox");

	
	}



}
