/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
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

import java.awt.event.KeyEvent;
import java.util.List;

import org.testng.annotations.*;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByConversationTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;


public class DeleteConversation extends PrefGroupMailByConversationTest {

	public DeleteConversation() {
		logger.info("New "+ DeleteConversation.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefShowSelectionCheckbox", "TRUE");

	}
	
	@Test(	description = "Delete a conversation",
			groups = { "smoke" })
	public void DeleteConversation_01() throws HarnessException {
		
		
		// Create the message data to be sent
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());
		


		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click delete
		app.zPageMail.zToolbarPressButton(Button.B_DELETE);
		
		List<MailItem> conversations = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(conversations, "Verify the conversation list exists");

		boolean found = false;
		for (MailItem m : conversations) {
			logger.info("Subject: looking for "+ c.getSubject() +" found: "+ m.gSubject);
			if ( c.getSubject().equals(m.getSubject()) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertFalse(found, "Verify the conversation is no longer in the inbox");
		
	}

	
	
	
	@Test(	description = "Delete a conversation using checkbox and toolbar delete button",
			groups = { "functional" })
	public void DeleteConversation_02() throws HarnessException {
		
		// Create the message data to be sent
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());

		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Check the item
		app.zPageMail.zListItem(Action.A_MAIL_CHECKBOX, c.getSubject());
		
		// Click delete
		app.zPageMail.zToolbarPressButton(Button.B_DELETE);
		
		
		
		//-- Verification
		
		// Check each message to verify they exist in the trash
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Trash);
		ConversationItem actual = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:"+ c.getSubject());
		
		for (MailItem m : actual.getMessageList()) {
			ZAssert.assertEquals(m.dFolderId, trash.getId(), "Verify the conversation message is in the trash");
		}

	}


	@DataProvider(name = "DataProviderDeleteKeys")
	public Object[][] DataProviderDeleteKeys() {
	  return new Object[][] {
	    new Object[] { "VK_DELETE", KeyEvent.VK_DELETE },
	    new Object[] { "VK_BACK_SPACE", KeyEvent.VK_BACK_SPACE },
	  };
	}
	
	@Test(	description = "Delete a conversation by selecting and typing 'delete' keyboard",
			groups = { "functional" },
			dataProvider = "DataProviderDeleteKeys")
	public void DeleteConversation_03(String name, int keyEvent) throws HarnessException {
		
		
		// Create the message data to be sent
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());

		
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Check the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click delete
		logger.info("Typing shortcut key "+ name + " KeyEvent: "+ keyEvent);
		app.zPageMail.zKeyboardKeyEvent(keyEvent);
				


		//-- Verification
		
		// Check each message to verify they exist in the trash
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Trash);
		ConversationItem actual = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:"+ c.getSubject());
		
		for (MailItem m : actual.getMessageList()) {
			ZAssert.assertEquals(m.dFolderId, trash.getId(), "Verify the conversation message is in the trash");
		}

		
	}

	@Test(	description = "Delete a conversation by selecting and typing '.t' shortcut",
			groups = { "functional" } )
	public void DeleteConversation_04() throws HarnessException {
		
		
		// Create the message data to be sent
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());

		
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Check the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click delete
		app.zPageMail.zKeyboardShortcut(Shortcut.S_MAIL_MOVETOTRASH);
				
		


		//-- Verification
		
		// Check each message to verify they exist in the trash
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Trash);
		ConversationItem actual = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:"+ c.getSubject());
		
		for (MailItem m : actual.getMessageList()) {
			ZAssert.assertEquals(m.dFolderId, trash.getId(), "Verify the conversation message is in the trash");
		}

		
	}

	@Test(	description = "Delete multiple messages (3) by select and toolbar delete",
			groups = { "functional" })
	public void DeleteConversation_05() throws HarnessException {
		
		
		// Create the message data to be sent
		ConversationItem c1 = ConversationItem.createConversationItem(app.zGetActiveAccount());
		ConversationItem c2 = ConversationItem.createConversationItem(app.zGetActiveAccount());
		ConversationItem c3 = ConversationItem.createConversationItem(app.zGetActiveAccount());

		
		
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Select all three items
		app.zPageMail.zListItem(Action.A_MAIL_CHECKBOX, c1.getSubject());
		app.zPageMail.zListItem(Action.A_MAIL_CHECKBOX, c2.getSubject());
		app.zPageMail.zListItem(Action.A_MAIL_CHECKBOX, c3.getSubject());
		
		// Click toolbar delete button
		app.zPageMail.zToolbarPressButton(Button.B_DELETE);
				

		//-- Verification
		
		// Check each message to verify they exist in the trash
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Trash);
		
		ConversationItem actual1 = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:"+ c1.getSubject());
		for (MailItem m : actual1.getMessageList()) {
			ZAssert.assertEquals(m.dFolderId, trash.getId(), "Verify the conversation message is in the trash");
		}

		ConversationItem actual2 = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:"+ c2.getSubject());
		for (MailItem m : actual2.getMessageList()) {
			ZAssert.assertEquals(m.dFolderId, trash.getId(), "Verify the conversation message is in the trash");
		}

		ConversationItem actual3 = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:"+ c3.getSubject());
		for (MailItem m : actual3.getMessageList()) {
			ZAssert.assertEquals(m.dFolderId, trash.getId(), "Verify the conversation message is in the trash");
		}

	}


	@Test(	description = "Delete a mail using context menu delete button",
			groups = { "functional" })
	public void DeleteConversation_06() throws HarnessException {
		
		
		// Create the message data to be sent
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());

		
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Right click the item, select delete
		app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE, c.getSubject());
		


		//-- Verification
		
		// Check each message to verify they exist in the trash
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Trash);
		ConversationItem actual = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:"+ c.getSubject());
		
		for (MailItem m : actual.getMessageList()) {
			ZAssert.assertEquals(m.dFolderId, trash.getId(), "Verify the conversation message is in the trash");
		}

		
	}


	@Bugs(	ids = "53564")
	@Test(	description = "Hard-delete a mail by selecting and typing 'shift-del' shortcut",
			groups = { "functional" } )
	public void HardDeleteConversation_01() throws HarnessException {
		
		
		// Create the message data to be sent
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());

		
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Check the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click shift-delete
		DialogWarning dialog = (DialogWarning)app.zPageMail.zKeyboardShortcut(Shortcut.S_MAIL_HARDELETE);
		dialog.zClickButton(Button.B_OK);
			
		
		// Verify the message is no longer in the mailbox
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='conversation'>"
				+		"<query>subject:("+ c.getSubject() +") is:anywhere</query>"
				+	"</SearchRequest>");

		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:c");
		ZAssert.assertEquals(nodes.length, 0, "Verify the converastion is no longer in the mailbox");

		
	}

	
	@Bugs(	ids = "53564")
	@Test(	description = "Hard-delete multiple messages (3) by selecting and typing 'shift-del' shortcut",
			groups = { "functional" })
	public void HardDeleteConversation_02() throws HarnessException {
		
		
		// Create the message data to be sent
		ConversationItem c1 = ConversationItem.createConversationItem(app.zGetActiveAccount());
		ConversationItem c2 = ConversationItem.createConversationItem(app.zGetActiveAccount());
		ConversationItem c3 = ConversationItem.createConversationItem(app.zGetActiveAccount());

		
		
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Select all three items
		app.zPageMail.zListItem(Action.A_MAIL_CHECKBOX, c1.getSubject());
		app.zPageMail.zListItem(Action.A_MAIL_CHECKBOX, c2.getSubject());
		app.zPageMail.zListItem(Action.A_MAIL_CHECKBOX, c3.getSubject());
		
		DialogWarning dialog = (DialogWarning)app.zPageMail.zKeyboardShortcut(Shortcut.S_MAIL_HARDELETE);
		dialog.zClickButton(Button.B_OK);
			
		
		// Verify the message is no longer in the mailbox
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='conversation'>"
			+		"<query>subject:("+ c1.getSubject() +") is:anywhere</query>"
			+	"</SearchRequest>");

		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:c");
		ZAssert.assertEquals(nodes.length, 0, "Verify the conversation (subject1) is no longer in the mailbox");

		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='conversation'>"
				+		"<query>subject:("+ c2.getSubject() +") is:anywhere</query>"
				+	"</SearchRequest>");

		nodes = app.zGetActiveAccount().soapSelectNodes("//mail:c");
		ZAssert.assertEquals(nodes.length, 0, "Verify the conversation (subject2) is no longer in the mailbox");

		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='conversation'>"
				+		"<query>subject:("+ c3.getSubject() +") is:anywhere</query>"
				+	"</SearchRequest>");

		nodes = app.zGetActiveAccount().soapSelectNodes("//mail:c");
		ZAssert.assertEquals(nodes.length, 0, "Verify the conversation (subject2) is no longer in the mailbox");

		
	}


	
	@Test(	description = "Delete a conversation - 1 message in inbox, 1 message in sent, 1 message in subfolder",
			groups = { "functional" })
	public void DeleteConversation_10() throws HarnessException {
		
		//-- DATA
		
		// Create a conversation (3 messages)
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());

		// Put one message in inbox, one in trash, one in subfolder
		
		// Get the system folders
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		FolderItem sent = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Sent);

		// Move the conversation to the trash
		app.zGetActiveAccount().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
					"<action op='move' l='"+ trash.getId() +"' id='"+ c.getMessageList().get(0).getId() + "'/>" +
				"</ItemActionRequest>");

		// Create a message in a subfolder
		String foldername = "folder"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='" + foldername +"' l='"+ inbox.getId() +"'/>" +
					"</CreateFolderRequest>");
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		// Move the conversation to the subfolder
		app.zGetActiveAccount().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
					"<action op='move' l='"+ subfolder.getId() +"' id='"+ c.getMessageList().get(1).getId() + "'/>" +
				"</ItemActionRequest>");

		// Reply to one message (putting a message in sent)
		app.zGetActiveAccount().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m origid='"+ c.getMessageList().get(2).getId() +"' rt='r'>" +
							"<e t='t' a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>" +
							"<su>RE: "+ c.getSubject() +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");
		String idSent = app.zGetActiveAccount().soapSelectValue("//mail:m", "id");

		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Right click the item, select delete
		app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE, c.getSubject());
		


		//-- Verification
		
		// Expected: all messages should be in trash, except for the sent message
		// Check each message to verify they exist in the trash
		ConversationItem actual = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:"+ c.getSubject());
		
		for (MailItem m : actual.getMessageList()) {
			if ( idSent.equals(m.getId()) ) {
				// Sent message should remain in sent
				ZAssert.assertEquals(m.dFolderId, sent.getId(), "Verify the conversation message is in the sent folder");
			} else {
				// All other messages should be moved to trash
				ZAssert.assertEquals(m.dFolderId, trash.getId(), "Verify the conversation message is in the trash");
			}
		}

		
	}

	@Bugs(ids = "79188")
	@Test(	description = "Delete a conversation - 1 message in inbox, 1 message in draft",
			groups = { "functional" })
	public void DeleteConversation_11() throws HarnessException {
		
		//-- DATA
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();

		// Create a conversation (1 message in inbox, 1 draft response)
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>body "+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		
		MailItem message1 = MailItem.importFromSOAP( app.zGetActiveAccount(), "subject:("+ subject +")");
		
		app.zGetActiveAccount().soapSend(
				"<SaveDraftRequest xmlns='urn:zimbraMail'>" +
					"<m origid='"+ message1.getId() +"' rt='r'>" +
						"<e t='t' a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>" +
						"<su>RE: "+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>body "+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
					"</m>" +
				"</SaveDraftRequest>");
        


		// Get the system folders
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		FolderItem drafts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Drafts);
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);


		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		try {
				
			// Click in Drafts
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, drafts);
			
			// Select the conversation or message (in 8.X, only messages are shown in drafts, not conversations)
			app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
			
			// Click Delete
			app.zPageMail.zToolbarPressButton(Button.B_DELETE);
		
		} finally {
			
			// Click in Inbox
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);

		}
		


		//-- Verification
		
		// Verify inbox message remains (bug 79188)
		MailItem m = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:(" + subject +") inid:"+ inbox.getId());
		ZAssert.assertNotNull(m, "Verify original message reamins in the inbox");
		
		// Verify draft is no longer in drafts folder
		m = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:(" + subject +") inid:"+ drafts.getId());
		ZAssert.assertNull(m, "Verify message is deleted from drafts");
		
		// Verify draft is in trash folder
		m = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:(" + subject +") inid:"+ trash.getId());
		ZAssert.assertNotNull(m, "Verify message is moved to trash");


		
	}

	
	@Bugs(	ids = "82704")
	@Test(	description = "Delete a conversation (1 message) that receives a new message : Delete Toolbar button",
			groups = { "functional" })
	public void DeleteConversation_21() throws HarnessException {
		
		//-- DATA
		
		
		
		final String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		final String content1 = "content1"+ ZimbraSeleniumProperties.getUniqueString();
		final String content2 = "content2"+ ZimbraSeleniumProperties.getUniqueString();
		final FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		final FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		String folderid;
		
		
		
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ content1 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		MailItem message = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "in:sent subject:("+ subject +")");


		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Select the item (this should refresh)
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Send another message to the conversation
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m origid='"+ message.getId() +"' rt='r'>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>RE: "+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ content2 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");

		// Click delete (no refresh)
		app.zPageMail.zToolbarPressButton(Button.B_DELETE);
		


		//-- Verification
		
		// If conversation is 'selected', then all messages should be refreshed and deleted
		
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>content:("+ content1 +") is:anywhere</query>"
			+	"</SearchRequest>");
		folderid = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");
		ZAssert.assertEquals(folderid, trash.getId(), "Verify the first message is in the trash");

		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>content:("+ content2 +") is:anywhere</query>"
			+	"</SearchRequest>");
		folderid = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");
		ZAssert.assertEquals(folderid, inbox.getId(), "Verify the second message is in the trash");


	}

	@Bugs(	ids = "82704")
	@Test(	description = "Delete a conversation (1 message) that receives a new message : Right click -> Delete",
			groups = { "functional" })
	public void DeleteConversation_22() throws HarnessException {
		
		//-- DATA
		
		
		final String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		final String content1 = "content1"+ ZimbraSeleniumProperties.getUniqueString();
		final String content2 = "content2"+ ZimbraSeleniumProperties.getUniqueString();
		final FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		final FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		String folderid;
		
		
		
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ content1 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		MailItem message = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "in:sent subject:("+ subject +")");


		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Send another message to the conversation
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m origid='"+ message.getId() +"' rt='r'>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>RE: "+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ content2 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");

		// Right click the item, select delete (this should not refresh)
		app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE, subject);
		


		//-- Verification
		
		// If conversation is not refreshed, then only the old message should be deleted
		
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>content:("+ content1 +") is:anywhere</query>"
			+	"</SearchRequest>");
		folderid = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");
		ZAssert.assertEquals(folderid, trash.getId(), "Verify the first message is in the trash");

		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>content:("+ content2 +") is:anywhere</query>"
			+	"</SearchRequest>");
		folderid = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");
		ZAssert.assertEquals(folderid, inbox.getId(), "Verify the second message is in the inbox");


	}

	@Bugs(	ids = "82704")
	@Test(	description = "Delete a conversation (2 messages) that receives a new message : Delete Toolbar button",
			groups = { "functional" })
	public void DeleteConversation_23() throws HarnessException {
		
		//-- DATA
		
		
		final String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		final String content1 = "content1"+ ZimbraSeleniumProperties.getUniqueString();
		final String content2 = "content2"+ ZimbraSeleniumProperties.getUniqueString();
		final String content3 = "content3"+ ZimbraSeleniumProperties.getUniqueString();
		final FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		final FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		String folderid;
		
		
		
		// Send a message to the test account and AccountB
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ content1 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		MailItem message = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "in:sent subject:("+ subject +")");

		// AccountA replies to the message.
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m origid='"+ message.getId() +"' rt='r'>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>RE: "+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ content2 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");


		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Select the item (this should refresh)
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Send another message to the conversation
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m origid='"+ message.getId() +"' rt='r'>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>RE: "+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ content3 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");

		// Click delete
		app.zPageMail.zToolbarPressButton(Button.B_DELETE);
		


		//-- Verification
		
		// If conversation is 'selected', then all messages should be refreshed and deleted
		
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>content:("+ content1 +") is:anywhere</query>"
			+	"</SearchRequest>");
		folderid = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");
		ZAssert.assertEquals(folderid, trash.getId(), "Verify the first message is in the trash");

		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>content:("+ content2 +") is:anywhere</query>"
			+	"</SearchRequest>");
		folderid = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");
		ZAssert.assertEquals(folderid, trash.getId(), "Verify the second message is in the trash");

		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>content:("+ content3 +") is:anywhere</query>"
			+	"</SearchRequest>");
		folderid = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");
		ZAssert.assertEquals(folderid, inbox.getId(), "Verify the third message is in the trash");


	}

	@Bugs(	ids = "82704")
	@Test(	description = "Delete a conversation (2 messages) that receives a new message : Right click -> Delete",
			groups = { "functional" })
	public void DeleteConversation_24() throws HarnessException {
		
		//-- DATA
		
		// Create the conversation
		final String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		final String content1 = "content1"+ ZimbraSeleniumProperties.getUniqueString();
		final String content2 = "content2"+ ZimbraSeleniumProperties.getUniqueString();
		final String content3 = "content3"+ ZimbraSeleniumProperties.getUniqueString();
		final FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		final FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
	
		// Send a message to the test account and AccountB
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ content1 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		MailItem message = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "in:sent subject:("+ subject +")");

		// AccountA replies to the message.
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m origid='"+ message.getId() +"' rt='r'>" +
					"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>RE: "+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ content2 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		




		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Send another message to the conversation
		// AccountA replies to the message.
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m origid='"+ message.getId() +"' rt='r'>" +
					"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>RE: "+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ content3 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");

		// Right click the item, select delete (this should not refresh)
		app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE, subject);
		


		//-- Verification
		
		// If conversation is not refreshed, then only the old messages should be deleted
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>content:("+ content1 +") is:anywhere</query>"
			+	"</SearchRequest>");
		String folderid = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");
		ZAssert.assertEquals(folderid, trash.getId(), "Verify the first message is in the trash");

		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>content:("+ content2 +") is:anywhere</query>"
			+	"</SearchRequest>");
		folderid = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");
		ZAssert.assertEquals(folderid, trash.getId(), "Verify the second message is in the trash");

		// The newest (un-refreshed) message should remain ni inbox
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>content:("+ content3 +") is:anywhere</query>"
			+	"</SearchRequest>");
		folderid = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");
		ZAssert.assertEquals(folderid, inbox.getId(), "Verify the third message is in the inbox");


	}

}
