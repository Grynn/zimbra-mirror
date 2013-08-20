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


public class FlagUnFlagConversation extends PrefGroupMailByConversationTest {

	public FlagUnFlagConversation() {
		logger.info("New "+ FlagUnFlagConversation.class.getCanonicalName());
		
		
		
	}
	
	@Test(	description = "Un-Flag a conversation by clicking flagged icon",
			groups = { "smoke" })
	public void FlagConversation_01() throws HarnessException {

		//-- DATA
		
		
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());
		
		app.zGetActiveAccount().soapSend(
					"<ConvActionRequest xmlns='urn:zimbraMail'>"
				+		"<action op='flag' id='"+ c.getId() + "'/>"
				+	"</ConvActionRequest>"
				);

		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Flag the item
		app.zPageMail.zListItem(Action.A_MAIL_UNFLAG, c.getSubject());
		


		//-- Verification
		

		// Each message in the conversation should now be flagged
		
		// Refresh the conversation
		c = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ c.getSubject() +")");
		for (MailItem m : c.getMessageList()) {
			ZAssert.assertStringDoesNotContain(m.dFlags, "f", "Verify all messges in the conversation are not flagged");
		}

	}

	@Test(	description = "Un-Flag a conversation by using shortcut 'mf'",
			groups = { "functional" })
	public void FlagConversation_02() throws HarnessException {

		//-- DATA
		
		
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());

		app.zGetActiveAccount().soapSend(
				"<ConvActionRequest xmlns='urn:zimbraMail'>"
			+		"<action op='flag' id='"+ c.getId() + "'/>"
			+	"</ConvActionRequest>"
			);

		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Flag the item
		app.zPageMail.zKeyboardShortcut(Shortcut.S_MAIL_MARKFLAG);
		


		//-- Verification
		

		// Each message in the conversation should now be flagged
		
		// Refresh the conversation
		c = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ c.getSubject() +")");
		for (MailItem m : c.getMessageList()) {
			ZAssert.assertStringDoesNotContain(m.dFlags, "f", "Verify all messges in the conversation are not flagged");
		}

	}



}
