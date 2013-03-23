/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;


public class ReplyMail extends PrefGroupMailByMessageTest {

	public ReplyMail() {
		logger.info("New "+ ReplyMail.class.getCanonicalName());
		
	}
	
	@Test(	description = "Reply to a message sent to test account (To field) - verify only the sender is used",
			groups = { "functional" })
	public void ReplyMail_01() throws HarnessException {

		//-- DATA
		
		// Send a message to the account
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<e t='c' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");



		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Reply the item
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLY);

		// Send the message
		mailform.zSubmit();



		//-- Verification
		
		// From the receiving end, verify the message details
		// Need 'in:inbox' to separate the message from the sent message
		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "in:sent subject:("+ subject +")");

		ZAssert.assertEquals(sent.dToRecipients.size(), 1, "Verify the message is sent to 1 'to' recipient");
		ZAssert.assertEquals(sent.dToRecipients.get(0).dEmailAddress, ZimbraAccount.AccountA().EmailAddress, "Verify the to field is correct");
		ZAssert.assertEquals(sent.dCcRecipients.size(), 0, "Verify the message is sent to 0 'cc' recipients");

	}

	@Test(	description = "Reply to a message sent to test account (cc field) - verify only the sender is used",
			groups = { "functional" })
	public void ReplyMail_02() throws HarnessException {

		//-- DATA
		
		// Send a message to the account
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
							"<e t='c' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");



		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Reply the item
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLY);

		// Send the message
		mailform.zSubmit();



		//-- Verification
		
		// From the receiving end, verify the message details
		// Need 'in:inbox' to separate the message from the sent message
		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "in:sent subject:("+ subject +")");

		ZAssert.assertEquals(sent.dToRecipients.size(), 1, "Verify the message is sent to 1 'to' recipient");
		ZAssert.assertEquals(sent.dToRecipients.get(0).dEmailAddress, ZimbraAccount.AccountA().EmailAddress, "Verify the to field is correct");
		ZAssert.assertEquals(sent.dCcRecipients.size(), 0, "Verify the message is sent to 0 'cc' recipients");

	}

	@Test(	description = "Reply to a message sent from test account (From field) - verify only the sender is used",
			groups = { "functional" })
	public void ReplyMail_03() throws HarnessException {

		//-- DATA
		
		// Send a message to the account
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>" +
							"<e t='c' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");
		
		
		// AccountA needs to delete the message, so that the next message can be searched
		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "in:inbox subject:("+ subject +")");
		ZimbraAccount.AccountA().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
						"<action op='move' id='"+ received.getId() +"' l='"+ FolderItem.importFromSOAP(ZimbraAccount.AccountA(), SystemFolder.Trash).getId() +"'/>" +
				"</ItemActionRequest>");


		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		try {
			
			// Select the sent folder
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Sent));
					
					// Select the item
			app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

			// Reply the item
			FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLY);

			// Send the message
			mailform.zSubmit();


		} finally {
			
			// Select the inbox folder
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox));

		}


		//-- Verification
		
		// From the receiving end, verify the message details
		// Need 'in:inbox' to separate the message from the sent message
		received = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "in:inbox subject:("+ subject +")");

		ZAssert.assertEquals(received.dToRecipients.size(), 1, "Verify the message is sent to 1 'to' recipient");
		ZAssert.assertEquals(received.dToRecipients.get(0).dEmailAddress, ZimbraAccount.AccountA().EmailAddress, "Verify the to field is correct");
		ZAssert.assertEquals(received.dCcRecipients.size(), 0, "Verify the message is sent to 0 'cc' recipients");

	}

	@Test(	description = "Reply to a message - Verify no drafts saved",
			groups = { "functional" })
	public void ReplyMail_04() throws HarnessException {

		//-- DATA
		FolderItem drafts = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Drafts);
		
		// Send a message to the account
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<e t='c' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");



		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Reply the item
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLY);

		// Send the message
		mailform.zSubmit();



		//-- Verification
		
		// Verify no new drafts
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>inid:"+ drafts.getId() +" subject:("+ subject +")</query>"
			+	"</SearchRequest>");
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:m");
		ZAssert.assertEquals(nodes.length, 0, "Verify no drafts");

	}


}
