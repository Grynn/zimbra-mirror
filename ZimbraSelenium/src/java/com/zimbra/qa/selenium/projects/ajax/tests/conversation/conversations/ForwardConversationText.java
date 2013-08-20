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
package com.zimbra.qa.selenium.projects.ajax.tests.conversation.conversations;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.*;


public class ForwardConversationText extends PrefGroupMailByConversationTest {

	public ForwardConversationText() {
		logger.info("New "+ ForwardConversationText.class.getCanonicalName());
		
		
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		
	}
	
	@Test(	description = "Forward a conversation",
			groups = { "smoke" })
	public void ForwardConversationText_01() throws HarnessException {

		//-- DATA
		
		
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());

		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click reply
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_FORWARD);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");

		// Send the message
		mailform.zFillField(Field.To, ZimbraAccount.AccountB().EmailAddress);
		mailform.zSubmit();


		//-- Verification
		
		
		// From the test account, check the sent folder for the reply
		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "in:sent subject:("+ c.getSubject() +")");
		ZAssert.assertNotNull(sent, "Verify the sent message in the sent folder");
		
		ZAssert.assertEquals(sent.dToRecipients.size(), 1, "Verify 1 'To'");
		boolean found = false;
		for (RecipientItem r : sent.dToRecipients) {
			logger.info("Looking for: "+ ZimbraAccount.AccountB().EmailAddress +" found "+ r.dEmailAddress);
			if ( r.dEmailAddress.equals(ZimbraAccount.AccountB().EmailAddress) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertTrue(found, "Verify the correct 'To' address was found");

		ZAssert.assertEquals(sent.dCcRecipients.size(), 0, "Verify 0 'Cc'");

	}

	

}
