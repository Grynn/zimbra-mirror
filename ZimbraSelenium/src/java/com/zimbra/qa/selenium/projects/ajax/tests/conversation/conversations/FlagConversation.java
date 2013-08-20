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


public class FlagConversation extends PrefGroupMailByConversationTest {

	public FlagConversation() {
		logger.info("New "+ FlagConversation.class.getCanonicalName());
		
		
		
	}
	
	@Test(	description = "Flag a conversation clicking flagged icon",
			groups = { "smoke" })
	public void FlagConversation_01() throws HarnessException {

		//-- DATA
		
		
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());

		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Flag the item
		app.zPageMail.zListItem(Action.A_MAIL_FLAG, c.getSubject());
		


		//-- Verification
		

		// Each message in the conversation should now be flagged
		
		// Refresh the conversation
		c = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ c.getSubject() +")");
		for (MailItem m : c.getMessageList()) {
			ZAssert.assertStringContains(m.dFlags, "f", "Verify all messges in the conversation are flagged");
		}

	}

	@Test(	description = "Flag a conversation by using shortcut 'mf'",
			groups = { "functional" })
	public void FlagConversation_02() throws HarnessException {

		//-- DATA
		
		
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());

		
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
			ZAssert.assertStringContains(m.dFlags, "f", "Verify all messges in the conversation are flagged");
		}

	}



}
