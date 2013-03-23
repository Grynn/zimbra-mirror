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


public class ReplyAllMail extends PrefGroupMailByMessageTest {

	ZimbraAccount account1 = null;
	ZimbraAccount account2 = null;
	ZimbraAccount account3 = null;
	ZimbraAccount account4 = null;
	
	public ReplyAllMail() {
		logger.info("New "+ ReplyAllMail.class.getCanonicalName());
	
		
	}
	
	@Test(	description = "Reply to all (test account in To field)",
			groups = { "functional" })
	public void ReplyMail_01() throws HarnessException {

		//-- DATA
		
		if ( account1 == null ) {
			account1 = (new ZimbraAccount()).provision().authenticate();
			account2 = (new ZimbraAccount()).provision().authenticate();
			account3 = (new ZimbraAccount()).provision().authenticate();
			account4 = (new ZimbraAccount()).provision().authenticate();
		}
		
		// Send a message to the account
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<e t='t' a='"+ account1.EmailAddress +"'/>" +
							"<e t='c' a='"+ account2.EmailAddress +"'/>" +
							"<e t='c' a='"+ account3.EmailAddress +"'/>" +
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
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLYALL);

		// Send the message
		mailform.zSubmit();



		//-- Verification
		
		// From the receiving end, verify the message details
		// Need 'in:inbox' to separate the message from the sent message
		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "in:sent subject:("+ subject +")");

		boolean foundAccountA = false;
		boolean foundAccount1 = false;
		boolean foundAccount2 = false;
		boolean foundAccount3 = false;
		
		
		// Check the To, which should only contain the original sender
		//
		
		ZAssert.assertEquals(sent.dToRecipients.size(), 1, "Verify the message is sent to 1 'to' recipient");
		for (RecipientItem r : sent.dToRecipients) {
			if ( r.dEmailAddress.equals(ZimbraAccount.AccountA().EmailAddress) ) {
				foundAccountA = true;
			}
		}
		ZAssert.assertTrue(foundAccountA, "Verify the original sender is in the To field");
		
		
		// Check the CC, which should contain the original To (not the sender), the original CC, and not the zimbra test account
		//
		
		ZAssert.assertEquals(sent.dCcRecipients.size(), 3, "Verify the message is sent to 3 'cc' recipients");
		for (RecipientItem r : sent.dCcRecipients) {
			if ( r.dEmailAddress.equals(account1.EmailAddress) ) {
				foundAccount1 = true;
			}
			if ( r.dEmailAddress.equals(account2.EmailAddress) ) {
				foundAccount2 = true;
			}
			if ( r.dEmailAddress.equals(account3.EmailAddress) ) {
				foundAccount3 = true;
			}
		}
		ZAssert.assertTrue(foundAccount1, "Verify the To is in the Cc field");
		ZAssert.assertTrue(foundAccount2, "Verify the Cc is in the Cc field");
		ZAssert.assertTrue(foundAccount3, "Verify the Cc is in the Cc field");


	}


	@Test(	description = "Reply to all (test account in Cc field)",
			groups = { "functional" })
	public void ReplyMail_02() throws HarnessException {

		//-- DATA
		
		if ( account1 == null ) {
			account1 = (new ZimbraAccount()).provision().authenticate();
			account2 = (new ZimbraAccount()).provision().authenticate();
			account3 = (new ZimbraAccount()).provision().authenticate();
			account4 = (new ZimbraAccount()).provision().authenticate();
		}
		

		// Send a message to the account
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ account1.EmailAddress +"'/>" +
							"<e t='t' a='"+ account2.EmailAddress +"'/>" +
							"<e t='c' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<e t='c' a='"+ account3.EmailAddress +"'/>" +
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
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLYALL);

		// Send the message
		mailform.zSubmit();



		//-- Verification
		
		// From the receiving end, verify the message details
		// Need 'in:inbox' to separate the message from the sent message
		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "in:sent subject:("+ subject +")");

		boolean foundAccountA = false;
		boolean foundAccount1 = false;
		boolean foundAccount2 = false;
		boolean foundAccount3 = false;
		
		
		// Check the To, which should only contain the original sender
		//
		
		ZAssert.assertEquals(sent.dToRecipients.size(), 1, "Verify the message is sent to 1 'to' recipient");
		for (RecipientItem r : sent.dToRecipients) {
			if ( r.dEmailAddress.equals(ZimbraAccount.AccountA().EmailAddress) ) {
				foundAccountA = true;
			}
		}
		ZAssert.assertTrue(foundAccountA, "Verify the original sender is in the To field");
		
		
		// Check the CC, which should contain the original To (not the sender), the original CC, and not the zimbra test account
		//
		
		ZAssert.assertEquals(sent.dCcRecipients.size(), 3, "Verify the message is sent to 3 'cc' recipients");
		for (RecipientItem r : sent.dCcRecipients) {
			if ( r.dEmailAddress.equals(account1.EmailAddress) ) {
				foundAccount1 = true;
			}
			if ( r.dEmailAddress.equals(account2.EmailAddress) ) {
				foundAccount2 = true;
			}
			if ( r.dEmailAddress.equals(account3.EmailAddress) ) {
				foundAccount3 = true;
			}
		}
		ZAssert.assertTrue(foundAccount1, "Verify the To is in the Cc field");
		ZAssert.assertTrue(foundAccount2, "Verify the Cc is in the Cc field");
		ZAssert.assertTrue(foundAccount3, "Verify the Cc is in the Cc field");


	}


	@Test(	description = "Reply to all from the sent folder (test account in From field)",
			groups = { "functional" })
	public void ReplyMail_03() throws HarnessException {

		//-- DATA
		
		if ( account1 == null ) {
			account1 = (new ZimbraAccount()).provision().authenticate();
			account2 = (new ZimbraAccount()).provision().authenticate();
			account3 = (new ZimbraAccount()).provision().authenticate();
			account4 = (new ZimbraAccount()).provision().authenticate();
		}
		

		// Send a message from the account
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ account1.EmailAddress +"'/>" +
						"<e t='c' a='"+ account2.EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>content" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
						"</m>" +
				"</SendMsgRequest>");



		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Click in sent
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Sent));
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Reply the item
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLYALL);

		// Send the message
		mailform.zSubmit();



		//-- Verification
		
		// All sent messages should not have TO: include the test account
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>in:sent subject:("+ subject +")</query>"
			+	"</SearchRequest>");

		Element[] messages = app.zGetActiveAccount().soapSelectNodes("//mail:m");
		
		// Make sure there are m nodes
		ZAssert.assertEquals(messages.length, 2, "Verify 2 messages are found in the sent folder");
		
		// Iterate over the sent messages, make sure the test account is not in the To or CC list
		for (Element message : messages) {
			
			String id = message.getAttribute("id", null);
			
			ZAssert.assertNotNull(id, "Verify the sent message ID is not null");
			
			app.zGetActiveAccount().soapSend(
					"<GetMsgRequest xmlns='urn:zimbraMail' >"
				+		"<m id='"+ id +"'/>"
				+	"</GetMsgRequest>");

			Element[] elements = app.zGetActiveAccount().soapSelectNodes("//mail:e");

			/**
			 *     <GetMsgResponse xmlns="urn:zimbraMail">
			 *     		<m id="257" f="sr" rev="2" d="1354142553000" s="545" sd="1354142553000" l="5" cid="259">
			 *             <fr>content135414321527621</fr>
			 *             <e d="enus135414320622919" t="f" a="enus135414320622919@testdomain.com"/>
			 *             <e d="enus13541431881476" t="t" a="enus13541431881476@testdomain.com"/>
			 *             <e d="enus13541431889627" t="c" a="enus13541431889627@testdomain.com"/>
			 *             <su>subject135414321527620</su>
			 *             <mid>&lt;2117099442.365.1354142553368.JavaMail.root@testdomain.com></mid>
			 *             <mp body="1" s="22" part="1" ct="text/plain">
			 *             		<content>content135414321527621</content>
			 *             </mp>
			 *          </m>
			 *    </GetMsgResponse>
			 */
			
			for ( Element e : elements ) {

				String type = e.getAttribute("t", null);
				String address = e.getAttribute("a", null);
				
				// Check To (t='t') and Cc (t='c') that they don't contain the sender
				if ( "t".equals(type) || "c".equals(type) ) {
					
					ZAssert.assertNotEqual(address, app.zGetActiveAccount().EmailAddress, "Verify the sender is not included in To or Cc");
					
				}

			}
			

		}


	}

}
