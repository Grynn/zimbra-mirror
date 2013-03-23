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
package com.zimbra.qa.selenium.projects.ajax.tests.conversation.conversations;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;


public class MoveConversation extends PrefGroupMailByConversationTest {

	@AfterMethod( groups = { "always" } )
	public void afterMethod() throws HarnessException {
		logger.info("Checking for the Move Dialog ...");

		// Check if the "Move Dialog is still open
		DialogMove dialog = new DialogMove(app, ((AppAjaxClient)app).zPageMail);
		if ( dialog.zIsActive() ) {
			logger.warn(dialog.myPageName() +" was still active.  Cancelling ...");
			dialog.zClickButton(Button.B_CANCEL);
		}
		
	}
	
	public MoveConversation() {
		logger.info("New "+ MoveConversation.class.getCanonicalName());
		
		
		

		
	}
	
	@Test(	description = "Move a conversation by selecting message, then clicking toolbar 'Move' button",
			groups = { "smoke" })
	public void MoveConversation_01() throws HarnessException {
		
		
		//-- DATA
		
		// Create a conversation
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());
		
		// Create a subfolder to move the message into
		// i.e. Inbox/subfolder
		//
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		String foldername = "folder"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='" + foldername +"' l='"+ inbox.getId() +"'/>" +
					"</CreateFolderRequest>");
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		
		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox));

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click move -> subfolder
		app.zPageMail.zToolbarPressPulldown(Button.B_MOVE, subfolder);

		
		
		//-- Verification
		
		// Verify all mesages are in the subfolder
		ConversationItem actual = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:"+ c.getSubject());
		
		for (MailItem m : actual.getMessageList()) {
			ZAssert.assertEquals(m.dFolderId, subfolder.getId(), "Verify the conversation message is in the sub folder");
		}

		
	}

	@Test(	description = "Move a conversation by selecting message, then click 'm' shortcut",
			groups = { "functional" })
	public void MoveConversation_02() throws HarnessException {
		
		
		//-- DATA
		
		// Create a conversation
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());
		
		// Create a subfolder to move the message into
		// i.e. Inbox/subfolder
		//
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		String foldername = "folder"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='" + foldername +"' l='"+ inbox.getId() +"'/>" +
					"</CreateFolderRequest>");
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		
		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox));

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click move
		app.zPageMail.zKeyboardShortcut(Shortcut.S_MOVE);
		
		// A move dialog will pop up
		DialogMove dialog = new DialogMove(app, ((AppAjaxClient)app).zPageMail);
		dialog.zClickTreeFolder(subfolder);
		dialog.zClickButton(Button.B_OK);

		
		
		//-- Verification
		
		// Verify all mesages are in the subfolder
		ConversationItem actual = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:"+ c.getSubject());
		
		for (MailItem m : actual.getMessageList()) {
			ZAssert.assertEquals(m.dFolderId, subfolder.getId(), "Verify the conversation message is in the sub folder");
		}

		

	}


	@Test(	description = "Move a conversation by using 'move to trash' shortcut '.t'",
			groups = { "functional" })
	public void MoveConversation_03() throws HarnessException {
		
		
		//-- DATA
		
		// Create a conversation
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());
		

		
		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox));

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click move
		app.zPageMail.zKeyboardShortcut(Shortcut.S_MAIL_MOVETOTRASH);

		
		
		//-- Verification
		
		// Verify all mesages are in the subfolder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		ConversationItem actual = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:"+ c.getSubject());
		
		for (MailItem m : actual.getMessageList()) {
			ZAssert.assertEquals(m.dFolderId, trash.getId(), "Verify the conversation message is in the trash folder");
		}

		

		
	}


	@Test(	description = "Move a conversation by using 'move to inbox' shortcut '.i'",
			groups = { "functional" })
	public void MoveConversation_04() throws HarnessException {
		
		
		//-- DATA
		
		// Create a subfolder
		String foldername = "folder"+ ZimbraSeleniumProperties.getUniqueString();
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='" + foldername +"' l='"+ inbox.getId() +"'/>" +
					"</CreateFolderRequest>");
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		// Create a conversation
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());
		
		// Move the conversation to the subfolder
		app.zGetActiveAccount().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
					"<action op='move' l='"+ subfolder.getId() +"' id='"+ c.getId() + "'/>" +
				"</ItemActionRequest>");

		
		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox));

		// Click the subfolder in the tree
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, subfolder);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click move
		app.zPageMail.zKeyboardShortcut(Shortcut.S_MAIL_MOVETOINBOX);

		
		
		//-- Verification
		
		// Verify all mesages are in the subfolder
		ConversationItem actual = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:"+ c.getSubject());
		
		for (MailItem m : actual.getMessageList()) {
			ZAssert.assertEquals(m.dFolderId, inbox.getId(), "Verify the conversation message is in the inbox folder");
		}

		

		
		
	}

	@Test(	description = "Move a conversation by using Move -> New folder",
			groups = { "functional" })
	public void MoveConversation_05() throws HarnessException {
		
		
		//-- DATA
		
		// Create a subfolder
		String foldername = "folder"+ ZimbraSeleniumProperties.getUniqueString();

		// Create a conversation
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());
		

		
		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox));

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click move
		DialogCreateFolder dialog = (DialogCreateFolder) app.zPageMail.zToolbarPressPulldown(Button.B_MOVE, Button.O_NEW_FOLDER);
		dialog.zEnterFolderName(foldername);
		dialog.zClickButton(Button.B_OK);


		
		
		//-- Verification
		
		// Get the folder
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		// Verify all mesages are in the subfolder
		ConversationItem actual = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:"+ c.getSubject());
		
		for (MailItem m : actual.getMessageList()) {
			ZAssert.assertEquals(m.dFolderId, subfolder.getId(), "Verify the conversation message is in the subfolder");
		}

		

		
	}


	
	@Test(	description = "Move a conversation - 1 message in inbox, 1 message in sent, 1 message in subfolder",
			groups = { "functional" })
	public void MoveConversation_10() throws HarnessException {
		
		//-- DATA
		
		// Create a conversation (3 messages)
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());

		// Put one message in inbox, one in trash, one in subfolder
		
		// Get the system folders
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		FolderItem sent = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Sent);

		// Move the conversation to the trash
		String idTrash = c.getMessageList().get(0).getId();
		app.zGetActiveAccount().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
					"<action op='move' l='"+ trash.getId() +"' id='"+ idTrash + "'/>" +
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

		// Create a folder to move the converation to
		String destinationname = "folder"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='" + destinationname +"' l='"+ inbox.getId() +"'/>" +
					"</CreateFolderRequest>");
		FolderItem destination = FolderItem.importFromSOAP(app.zGetActiveAccount(), destinationname);


		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox));

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, c.getSubject());
		
		// Click move -> subfolder
		app.zPageMail.zToolbarPressPulldown(Button.B_MOVE, destination);
		


		//-- Verification
		
		// Expected: all messages should be in subfolder, except for the sent message and the trash message
		
		ConversationItem actual = ConversationItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:"+ c.getSubject());
		
		for (MailItem m : actual.getMessageList()) {
			if ( idSent.equals(m.getId()) ) {
				
				// Sent message should remain in sent
				ZAssert.assertEquals(m.dFolderId, sent.getId(), "Verify the conversation message is in the sent folder");
				
			} else if ( idTrash.equals(m.getId()) ) {
				
				// Trash message should remain in trash
				ZAssert.assertEquals(m.dFolderId, trash.getId(), "Verify the conversation message is in the trash");
				
			} else {
				
				// All other messages should be moved to trash
				ZAssert.assertEquals(m.dFolderId, destination.getId(), "Verify the conversation message is in the subfolder");
				
			}
		}

		
	}


}
