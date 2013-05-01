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
package com.zimbra.qa.selenium.projects.ajax.tests.conversation.conversations;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;


public class ReplyAllConversationText extends PrefGroupMailByConversationTest {

	public ReplyAllConversationText() {
		logger.info("New "+ ReplyAllConversationText.class.getCanonicalName());
		
		
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		
	}
	
	@Test(	description = "Reply-All to a conversation",
			groups = { "smoke" })
	public void ReplyAllConversationText_01() throws HarnessException {

		//-- DATA
		
		// Create a conversation
		ZimbraAccount account1 = (new ZimbraAccount()).provision().authenticate();
		ZimbraAccount account2 = (new ZimbraAccount()).provision().authenticate();
		ZimbraAccount account3 = (new ZimbraAccount()).provision().authenticate();
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		account1.soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m >" +
						"<e t='t' a='"+ account2.EmailAddress +"'/>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<e t='c' a='"+ account3.EmailAddress +"'/>" +
						"<su>RE: "+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>body"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		String id = account1.soapSelectValue("//mail:m", "id");

		account1.soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m origid='"+ id +"' rt='r'>" +
						"<e t='t' a='"+ account2.EmailAddress +"'/>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<e t='c' a='"+ account3.EmailAddress +"'/>" +
						"<su>RE: "+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>body"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");

		

		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Click reply
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLYALL);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");

		// Send the message
		mailform.zSubmit();


		//-- Verification
		
		
		// From the test account, check the sent folder for the reply
		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "in:sent subject:("+ subject +")");
		ZAssert.assertNotNull(sent, "Verify the sent message in the sent folder");
		
		// Verify the correct recipients in the Reply-All
		
		// To: should contain the original sender (account1)
		ZAssert.assertEquals(sent.dToRecipients.size(), 1, "Verify 1 'To'");
		boolean found1 = false;
		for (RecipientItem r : sent.dToRecipients) {
			logger.info("Looking for: "+ account1.EmailAddress +" found "+ r.dEmailAddress);
			if ( r.dEmailAddress.equals(account1.EmailAddress) ) {
				found1 = true;
				break;
			}
		}
		ZAssert.assertTrue(found1, "Verify the correct 'To' address was found");

		// CC: should contain the original CC members and the original To members
		ZAssert.assertEquals(sent.dCcRecipients.size(), 2, "Verify 2 'CC'");
		boolean found2 = false;
		boolean found3 = false;
		for (RecipientItem r : sent.dCcRecipients) {
			logger.info("Looking for: "+ account2.EmailAddress +" found "+ r.dEmailAddress);
			logger.info("Looking for: "+ account3.EmailAddress +" found "+ r.dEmailAddress);
			if ( r.dEmailAddress.equals(account2.EmailAddress) ) {
				found2 = true;
			}
			if ( r.dEmailAddress.equals(account3.EmailAddress) ) {
				found3 = true;
			}
		}
		ZAssert.assertTrue(found2, "Verify the correct 'Cc' address was found");
		ZAssert.assertTrue(found3, "Verify the correct 'Cc' address was found");

	}

	

}
