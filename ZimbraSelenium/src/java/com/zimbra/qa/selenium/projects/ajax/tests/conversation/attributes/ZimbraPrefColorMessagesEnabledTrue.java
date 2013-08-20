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
package com.zimbra.qa.selenium.projects.ajax.tests.conversation.attributes;

import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;




public class ZimbraPrefColorMessagesEnabledTrue extends PrefGroupMailByConversationTest {

	public ZimbraPrefColorMessagesEnabledTrue() {
		
		this.startingAccountPreferences.put("zimbraPrefColorMessagesEnabled", "TRUE");
		
	}

	
	@Test(	description = "ZimbraPrefColorMessagesEnabledTrue=TRUE: Display messages with 1 tag",
			groups = { "functional" })
	public void ZimbraPrefColorMessagesEnabledTrue_01() throws HarnessException {
		
		//-- DATA
		
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String tagname = "tag" + ZimbraSeleniumProperties.getUniqueString();

		// Create a tag
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" + 
					"<tag name='" + tagname + "' color='1' />" + 
				"</CreateTagRequest>");
		TagItem tag = TagItem.importFromSOAP(app.zGetActiveAccount(), tagname);
		

		// Add a message to the mailbox
		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" +
					"<m l='" + inboxFolder.getId() + "' t='" + tag.getId() + "'>" +
						"<content>" +
							"From: foo@foo.com\n" + 
							"To: foo@foo.com \n" + 
							"Subject: " + subject + "\n" + 
							"MIME-Version: 1.0 \n" + 
							"Content-Type: text/plain; charset=utf-8 \n" + 
							"Content-Transfer-Encoding: 7bit\n" + 
							"\n" + 
							"simple text string in the body\n" + 
						"</content>" + 
					"</m>" + 
				"</AddMsgRequest>");



		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		
		
		//-- VERIFICATION

		// Verify the message is in the list
		List<MailItem> conversations = app.zPageMail.zListGetMessages();

		// Verify the message has the tag color
		MailItem found = null;
		for (MailItem message : conversations) {
			if (subject.equals(message.getSubject())) {
				found = message;
				break;
			}
		}

		ZAssert.assertNotNull(found, "Verify the conversations is found");


//		// Verify the style on the message contains the color
//		String locator = "css=li#zli__TV-main__"+ mail.getId();
//		
//		boolean present = app.zPageMail.sIsElementPresent(locator);
//		ZAssert.assertTrue(present, "Verify the locator is found");
//		
//		String style = app.zPageMail.sGetAttribute(locator + "@style");
//		ZAssert.assertNotNull(style, "Verify the message has a style");
		
		// TODO: Add verification that the color style appears
		
	}
	
	@Test(	description = "PrefGroupMailByMessageTest=TRUE: Display messages with 2 tags",
			groups = { "functional" })
	public void ZimbraPrefColorMessagesEnabledTrue_02() throws HarnessException {
		
		//-- DATA
		
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String tagname1 = "tag" + ZimbraSeleniumProperties.getUniqueString();
		String tagname2 = "tag" + ZimbraSeleniumProperties.getUniqueString();

		// Create a tag
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" + 
					"<tag name='" + tagname1 + "' color='1' />" + 
				"</CreateTagRequest>");
		TagItem tag1 = TagItem.importFromSOAP(app.zGetActiveAccount(), tagname1);
		
		// Create a tag
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" + 
					"<tag name='" + tagname2 + "' color='2' />" + 
				"</CreateTagRequest>");
		TagItem tag2 = TagItem.importFromSOAP(app.zGetActiveAccount(), tagname2);
		

		// Add a message to the mailbox
		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" +
					"<m l='" + inboxFolder.getId() + "' t='" + tag1.getId() + "," + tag2.getId() +"'>" +
						"<content>" +
							"From: foo@foo.com\n" + 
							"To: foo@foo.com \n" + 
							"Subject: " + subject + "\n" + 
							"MIME-Version: 1.0 \n" + 
							"Content-Type: text/plain; charset=utf-8 \n" + 
							"Content-Transfer-Encoding: 7bit\n" + 
							"\n" + 
							"simple text string in the body\n" + 
						"</content>" + 
					"</m>" + 
				"</AddMsgRequest>");



		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		
		
		//-- VERIFICATION

		// Verify the message is in the list
		List<MailItem> conversations = app.zPageMail.zListGetMessages();

		// Verify the message has the tag color
		MailItem found = null;
		for (MailItem message : conversations) {
			if (subject.equals(message.getSubject())) {
				found = message;
				break;
			}
		}

		ZAssert.assertNotNull(found, "Verify the conversations is found");


//		// Verify the style on the message contains the color
//		String locator = "css=li#zli__TV-main__"+ mail.getId();
//		
//		boolean present = app.zPageMail.sIsElementPresent(locator);
//		ZAssert.assertTrue(present, "Verify the locator is found");
//		
//		String style = app.zPageMail.sGetAttribute(locator + "@style");
//		ZAssert.assertNotNull(style, "Verify the message has a style");

		// TODO: Add verification that no color style appears

	}

	
}
