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


}
