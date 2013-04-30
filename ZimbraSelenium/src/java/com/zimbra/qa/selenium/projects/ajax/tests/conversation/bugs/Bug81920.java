/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.conversation.bugs;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;


public class Bug81920 extends PrefGroupMailByConversationTest {

	public Bug81920() {
		logger.info("New "+ Bug81920.class.getCanonicalName());
		
		
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		
	}
	

	
	@Bugs(ids = "81920")
	@Test(	description = "Reply to a conversation with a draft",
			groups = { "functional" })
	public void Bug81920_01() throws HarnessException {

		//-- DATA
		
		// Create a conversation
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());
		
		// Create a draft in the conversation
		// First, need to determine the last message received
		int id = 0;
		for (MailItem m : c.getMessageList()) {
			if ( Integer.parseInt(m.getId()) > id ) {
				id = Integer.parseInt(m.getId());
			}
		}
		String body = "draft"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<SaveDraftRequest xmlns='urn:zimbraMail'>" +
					"<m origid='"+ id +"' rt='r'>" +
						"<e t='t' a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>" +
						"<su>RE: "+ c.getSubject() +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ body +"</content>" +
						"</mp>" +
					"</m>" +
				"</SaveDraftRequest>");

		// Change the whole conversation to be unread
		app.zGetActiveAccount().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
						"<action op='!read' id='"+ c.getId() +"'/>" +
				"</ItemActionRequest>");

		
		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click reply
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLY);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");

		// Send the message
		mailform.zSubmit();


		//-- Verification
		
		
		// From the test account, check the sent folder for the reply
		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "in:sent subject:("+ c.getSubject() +")");
		ZAssert.assertNotNull(sent, "Verify the sent message in the sent folder");
		
		// Verify the draft body does not appear in the reply
		ZAssert.assertStringDoesNotContain(sent.dBodyText, body, "Verify the draft body does not appear in the reply");
	}

	@Bugs(ids = "81920")
	@Test(	description = "Reply to a conversation with a trashed message",
			groups = { "functional" })
	public void Bug81920_02() throws HarnessException {

		//-- DATA
				
		// Create a conversation
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());
		
		// Create a draft in the conversation
		// First, need to determine the last message received
		int id = 0;
		String body = null;
		for (MailItem m : c.getMessageList()) {
			if ( Integer.parseInt(m.getId()) > id ) {
				id = Integer.parseInt(m.getId());
				body = m.dBodyText;
			}
		}
		
		// Move the last message to the trash
		app.zGetActiveAccount().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
						"<action op='trash' id='"+ id +"'/>" +
				"</ItemActionRequest>");

		// Change the whole conversation to be unread
		app.zGetActiveAccount().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
						"<action op='!read' id='"+ c.getId() +"'/>" +
				"</ItemActionRequest>");

		
		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click reply
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLY);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");

		// Send the message
		mailform.zSubmit();


		//-- Verification
		
		
		// From the test account, check the sent folder for the reply
		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "in:sent subject:("+ c.getSubject() +")");
		ZAssert.assertNotNull(sent, "Verify the sent message in the sent folder");
		
		// Verify the draft body does not appear in the reply
		ZAssert.assertStringDoesNotContain(sent.dBodyText, body, "Verify the trash body does not appear in the reply");
	}

	@Bugs(ids = "81920")
	@Test(	description = "Reply to a conversation with a spammed message",
			groups = { "functional" })
	public void Bug81920_03() throws HarnessException {

		//-- DATA
		
		
		// Create a conversation
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());
		
		// Create a draft in the conversation
		// First, need to determine the last message received
		int id = 0;
		String body = null;
		for (MailItem m : c.getMessageList()) {
			if ( Integer.parseInt(m.getId()) > id ) {
				id = Integer.parseInt(m.getId());
				body = m.dBodyText;
			}
		}
		
		// Move the last message to the trash
		app.zGetActiveAccount().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
						"<action op='spam' id='"+ id +"'/>" +
				"</ItemActionRequest>");

		// Change the whole conversation to be unread
		app.zGetActiveAccount().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
						"<action op='!read' id='"+ c.getId() +"'/>" +
				"</ItemActionRequest>");

		
		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click reply
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLY);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");

		// Send the message
		mailform.zSubmit();


		//-- Verification
		
		
		// From the test account, check the sent folder for the reply
		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "in:sent subject:("+ c.getSubject() +")");
		ZAssert.assertNotNull(sent, "Verify the sent message in the sent folder");
		
		// Verify the draft body does not appear in the reply
		ZAssert.assertStringDoesNotContain(sent.dBodyText, body, "Verify the spam body does not appear in the reply");
	}

	@Bugs(ids = "81920")
	@Test(	description = "Reply to a conversation with a sent message",
			groups = { "functional" })
	public void Bug81920_04() throws HarnessException {

		//-- DATA
		
		
		// Create a conversation
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());
		
		// Create a draft in the conversation
		// First, need to determine the last message received
		int id = 0;
		for (MailItem m : c.getMessageList()) {
			if ( Integer.parseInt(m.getId()) > id ) {
				id = Integer.parseInt(m.getId());
			}
		}
		
		// Reply to the last message
		String body = "firstreply"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m origid='"+ id +"' rt='r'>" +
						"<e t='t' a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>" +
						"<su>RE: "+ c.getSubject() +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ body +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");

		// Change the whole conversation to be unread
		app.zGetActiveAccount().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
						"<action op='!read' id='"+ c.getId() +"'/>" +
				"</ItemActionRequest>");

		
		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click reply
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLY);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");

		// Send the message
		mailform.zSubmit();


		//-- Verification
		
		
		// From the test account, check the sent folder for the reply
		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "not content:("+ body + ") in:sent subject:("+ c.getSubject() +") ");
		ZAssert.assertNotNull(sent, "Verify the sent message in the sent folder");
		
		// Verify the draft body does not appear in the reply
		ZAssert.assertStringDoesNotContain(sent.dBodyText, body, "Verify the spam body does not appear in the reply");
	}


}
