package com.zimbra.qa.selenium.projects.ajax.tests.conversation.conversations;

import java.awt.event.KeyEvent;
import java.util.List;

import org.testng.annotations.*;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
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


}
