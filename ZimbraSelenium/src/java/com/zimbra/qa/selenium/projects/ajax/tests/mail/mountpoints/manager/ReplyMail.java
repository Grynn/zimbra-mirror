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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.mountpoints.manager;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;


public class ReplyMail extends PrefGroupMailByMessageTest {

	public ReplyMail() {
		logger.info("New "+ ReplyMail.class.getCanonicalName());
		
	}
	
	@Test(	description = "Reply (on behalf of) to a message in a shared folder (manager rights)",
			groups = { "functional" })
	public void ReplyMail_01() throws HarnessException {

		//-- DATA
		
		// Create the folder owner
		ZimbraAccount owner = (new ZimbraAccount()).provision().authenticate();
		
		// Allow sending rights
		owner.soapSend(
				"<GrantRightsRequest xmlns='urn:zimbraAccount'>"
			+		"<ace gt='usr' d='"+ app.zGetActiveAccount().EmailAddress +"' right='sendOnBehalfOf'/>"
			+	"</GrantRightsRequest>");

		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String mountpointname = "mountpoint" + ZimbraSeleniumProperties.getUniqueString();
		
		FolderItem inbox = FolderItem.importFromSOAP(owner, FolderItem.SystemFolder.Inbox);
		
		// Create a folder to share
		owner.soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" + inbox.getId() + "'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem folder = FolderItem.importFromSOAP(owner, foldername);
		
		// Share it
		owner.soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ folder.getId() +"' op='grant'>"
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='rwidx'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		
		// Add a message to it
		owner.soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
        		+		"<m l='"+ folder.getId() +"' >"
            	+			"<content>From: "+ ZimbraAccount.AccountB().EmailAddress +"\n"
            	+				"To: "+ owner.EmailAddress +"\n"
            	+				"Subject: "+ subject +"\n"
            	+				"MIME-Version: 1.0 \n"
            	+				"Content-Type: text/plain; charset=utf-8 \n"
            	+				"Content-Transfer-Encoding: 7bit\n"
            	+				"\n"
            	+				"simple text string in the body\n"
            	+			"</content>"
            	+		"</m>"
				+	"</AddMsgRequest>");
		

		
		// Mount it
		app.zGetActiveAccount().soapSend(
					"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointname +"'  rid='"+ folder.getId() +"' zid='"+ owner.ZimbraId +"'/>"
				+	"</CreateMountpointRequest>");
		
		FolderMountpointItem mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointname);

		
		



		//-- GUI
		
		// Login to load the rights
		app.zPageLogin.zNavigateTo();
		this.startingPage.zNavigateTo();

		try {
			
			// Click on the mountpoint
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, mountpoint);
	
			// Select the item
			app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
			
			// Reply the item
			FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLY);
			mailform.zSubmit();

		} finally {
			
			// Select the inbox
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox));

		}




		//-- VERIFICATION
		
		// From the receiving end, verify the message details
		// Need 'in:inbox' to separate the message from the sent message
		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "in:sent subject:("+ subject +")");

		ZAssert.assertEquals(sent.dToRecipients.size(), 1, "Verify the message is sent to 1 'to' recipient");
		ZAssert.assertEquals(sent.dToRecipients.get(0).dEmailAddress, ZimbraAccount.AccountB().EmailAddress, "Verify the 'To' field is correct");
		ZAssert.assertEquals(sent.dFromRecipient.dEmailAddress, owner.EmailAddress, "Verify the 'From' field is correct");
		ZAssert.assertEquals(sent.dSenderRecipient.dEmailAddress, app.zGetActiveAccount().EmailAddress, "Verify the 'Sender' field is correct");

	}


	@Test(	description = "Reply (on behalf of) to a message in a shared folder (manager rights) - no SOBO rights",
			groups = { "functional" })
	public void ReplyMail_02() throws HarnessException {

		//-- DATA
		
		// Create the folder owner
		ZimbraAccount owner = (new ZimbraAccount()).provision().authenticate();
		
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String mountpointname = "mountpoint" + ZimbraSeleniumProperties.getUniqueString();
		
		FolderItem inbox = FolderItem.importFromSOAP(owner, FolderItem.SystemFolder.Inbox);
		
		// Create a folder to share
		owner.soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" + inbox.getId() + "'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem folder = FolderItem.importFromSOAP(owner, foldername);
		
		// Share it
		owner.soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ folder.getId() +"' op='grant'>"
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='rwidx'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		
		// Add a message to it
		owner.soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
        		+		"<m l='"+ folder.getId() +"' >"
            	+			"<content>From: "+ ZimbraAccount.AccountB().EmailAddress +"\n"
            	+				"To: "+ owner.EmailAddress +"\n"
            	+				"Subject: "+ subject +"\n"
            	+				"MIME-Version: 1.0 \n"
            	+				"Content-Type: text/plain; charset=utf-8 \n"
            	+				"Content-Transfer-Encoding: 7bit\n"
            	+				"\n"
            	+				"simple text string in the body\n"
            	+			"</content>"
            	+		"</m>"
				+	"</AddMsgRequest>");
		

		
		// Mount it
		app.zGetActiveAccount().soapSend(
					"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointname +"'  rid='"+ folder.getId() +"' zid='"+ owner.ZimbraId +"'/>"
				+	"</CreateMountpointRequest>");
		
		FolderMountpointItem mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointname);

		
		



		//-- GUI
		
		// Login to load the rights
		app.zPageLogin.zNavigateTo();
		this.startingPage.zNavigateTo();

		try {
			
			// Click on the mountpoint
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, mountpoint);
	
			// Select the item
			app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
			
			// Reply the item
			FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLY);
			mailform.zSubmit();

		} finally {
			
			// Select the inbox
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox));

		}




		//-- VERIFICATION
		
		// From the receiving end, verify the message details
		// Need 'in:inbox' to separate the message from the sent message
		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "in:sent subject:("+ subject +")");

		ZAssert.assertEquals(sent.dToRecipients.size(), 1, "Verify the message is sent to 1 'to' recipient");
		ZAssert.assertEquals(sent.dToRecipients.get(0).dEmailAddress, ZimbraAccount.AccountB().EmailAddress, "Verify the 'To' field is correct");
		ZAssert.assertEquals(sent.dFromRecipient.dEmailAddress, app.zGetActiveAccount().EmailAddress, "Verify the 'From' field is correct");
		ZAssert.assertNull(sent.dSenderRecipient, "Verify the 'Sender' field is empty");

	}



}
