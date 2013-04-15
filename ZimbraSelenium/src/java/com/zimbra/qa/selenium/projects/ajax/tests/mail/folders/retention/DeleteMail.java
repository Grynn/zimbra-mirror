/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders.retention;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning.DialogWarningID;


public class DeleteMail extends PrefGroupMailByMessageTest {

	public DeleteMail() {
		logger.info("New "+ DeleteMail.class.getCanonicalName());
		
		
		super.startingAccountPreferences.put("zimbraPrefShowSelectionCheckbox", "TRUE");

		
	}
	
	@Test(	description = "Delete a mail that falls within the retention time",
			groups = { "functional" })
	public void DeleteMail_01() throws HarnessException {

		//-- Data
		
		// Create the subfolder
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" +  FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox).getId() + "'/>"
				+	"</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		// Add a retention policy
		app.zGetActiveAccount().soapSend(
				"<FolderActionRequest xmlns='urn:zimbraMail'>"
			+		"<action id='" + folder.getId() + "' op='retentionpolicy'>"
			+			"<retentionPolicy>"
			+				"<keep>"
			+					"<policy lifetime='5d' type='user'/>"
			+				"</keep>"
			+			"</retentionPolicy>"
			+		"</action>"
			+	"</FolderActionRequest>");

		
		// Add a message to the folder
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" +
            		"<m l='"+ folder.getId() +"' f='f'>" +
                		"<content>From: foo@foo.com\n" +
"To: foo@foo.com \n" +
"Subject: "+ subject +"\n" +
"MIME-Version: 1.0 \n" +
"Content-Type: text/plain; charset=utf-8 \n" +
"Content-Transfer-Encoding: 7bit\n" +
"\n" +
"simple text string in the body\n" +
"</content>" +
                	"</m>" +
            	"</AddMsgRequest>");

		
		
		
		
		//-- GUI
		
		try {
				
			// Click Get Mail button
			app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
					
			// Click on the subfolder
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, folder);
			
			// Select the item
			app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
			
			// Click delete
			app.zPageMail.zToolbarPressButton(Button.B_DELETE);
			
			
			//-- Verification
			
			// A dialog will appear confirming deletion
			DialogWarning warning = (DialogWarning) app.zPageMain.zGetWarningDialog(DialogWarningID.DeleteItemWithinRetentionPeriod);
			warning.zWaitForActive();
			
			warning.zClickButton(Button.B_OK);
		
		} finally {
			
			// Select the inbox
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox));

		}

		
		// Verify the message is in the trash
		MailItem message = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +") is:anywhere");
		ZAssert.assertNotNull(message, "Verify message remains in the mailbox");
		ZAssert.assertEquals(
				message.dFolderId, 
				FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Trash).getId(), 
				"Verify message is contained in the trash");
		
	}


	@Test(	description = "Delete a mail that falls within the retention time - click cancel to the confirmation",
			groups = { "functional" })
	public void DeleteMail_02() throws HarnessException {

		//-- Data
		
		// Create the subfolder
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" +  FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox).getId() + "'/>"
				+	"</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		// Add a retention policy
		app.zGetActiveAccount().soapSend(
				"<FolderActionRequest xmlns='urn:zimbraMail'>"
			+		"<action id='" + folder.getId() + "' op='retentionpolicy'>"
			+			"<retentionPolicy>"
			+				"<keep>"
			+					"<policy lifetime='5d' type='user'/>"
			+				"</keep>"
			+			"</retentionPolicy>"
			+		"</action>"
			+	"</FolderActionRequest>");

		
		// Add a message to the folder
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" +
            		"<m l='"+ folder.getId() +"' f='f'>" +
                		"<content>From: foo@foo.com\n" +
"To: foo@foo.com \n" +
"Subject: "+ subject +"\n" +
"MIME-Version: 1.0 \n" +
"Content-Type: text/plain; charset=utf-8 \n" +
"Content-Transfer-Encoding: 7bit\n" +
"\n" +
"simple text string in the body\n" +
"</content>" +
                	"</m>" +
            	"</AddMsgRequest>");

		
		
		
		
		//-- GUI
		
		try {
			
			// Click Get Mail button
			app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
					
			// Click on the subfolder
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, folder);
			
			// Select the item
			app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
			
			// Click delete
			app.zPageMail.zToolbarPressButton(Button.B_DELETE);
			
			
			//-- Verification
			
			// A dialog will appear confirming deletion
			DialogWarning warning = (DialogWarning) app.zPageMain.zGetWarningDialog(DialogWarningID.DeleteItemWithinRetentionPeriod);
			warning.zWaitForActive();
			
			warning.zClickButton(Button.B_CANCEL);
		
		} finally {
			
			// Select the inbox
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox));

		}

		// Verify the message is in the trash
		MailItem message = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +") is:anywhere");
		ZAssert.assertNotNull(message, "Verify message remains in the mailbox");
		ZAssert.assertEquals(
				message.dFolderId, 
				folder.getId(),
				"Verify message remains in the folder");
		
	}


	@Test(	description = "Hard-delete a mail by selecting and typing 'shift-del' shortcut",
			groups = { "functional" } )
	public void HardDeleteMail_01() throws HarnessException {

		//-- Data
		
		// Create the subfolder
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" +  FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox).getId() + "'/>"
				+	"</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		// Add a retention policy
		app.zGetActiveAccount().soapSend(
				"<FolderActionRequest xmlns='urn:zimbraMail'>"
			+		"<action id='" + folder.getId() + "' op='retentionpolicy'>"
			+			"<retentionPolicy>"
			+				"<keep>"
			+					"<policy lifetime='5d' type='user'/>"
			+				"</keep>"
			+			"</retentionPolicy>"
			+		"</action>"
			+	"</FolderActionRequest>");

		
		// Add a message to the folder
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" +
            		"<m l='"+ folder.getId() +"' f='f'>" +
                		"<content>From: foo@foo.com\n" +
"To: foo@foo.com \n" +
"Subject: "+ subject +"\n" +
"MIME-Version: 1.0 \n" +
"Content-Type: text/plain; charset=utf-8 \n" +
"Content-Transfer-Encoding: 7bit\n" +
"\n" +
"simple text string in the body\n" +
"</content>" +
                	"</m>" +
            	"</AddMsgRequest>");

		
		
		
		
		//-- GUI
		
		try {
			
			// Click Get Mail button
			app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
					
			// Click on the subfolder
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, folder);
			
			// Select the item
			app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
			
			// Click shift-del
			
			/* 
			 * Note: there will be two dialogs on this action:
			 * 1. This item is within the retention period (DialogWarningID.DeleteItemWithinRetentionPeriod)
			 * 2. Are you sure you want to permanently delete this item? (DialogWarningID.PermanentlyDeleteTheItem)
			 * 
			 * Luckily, both warning dialogs share the same div (<div id='OkCancel' .../>).  However,
			 * if the ID's change in the future, then the zPageMail.zKeyboardShortcut() method may need
			 * to be reworked.  The test cases for hard delete may need to do the zGetWarningDialog() instead.
			 * 
			 * 
			 */ 
			DialogWarning dialog = (DialogWarning)app.zPageMail.zKeyboardShortcut(Shortcut.S_MAIL_HARDELETE);
			dialog.zClickButton(Button.B_OK);
			
			DialogWarning warning = (DialogWarning) app.zPageMain.zGetWarningDialog(DialogWarningID.PermanentlyDeleteTheItem);
			warning.zWaitForActive();
			warning.zClickButton(Button.B_OK);

		} finally {
			
			// Select the inbox
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox));

		}

		
		//-- Verification
		
		
		// Verify the message is hard deleted
		MailItem message = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +") is:anywhere");
		ZAssert.assertNull(message, "Verify message is hard deleted");

	}

	@Test(	description = "Hard-delete a mail by selecting and typing 'shift-del' shortcut - click cancel to the confirmation",
			groups = { "functional" } )
	public void HardDeleteMail_02() throws HarnessException {

		//-- Data
		
		// Create the subfolder
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" +  FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox).getId() + "'/>"
				+	"</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		// Add a retention policy
		app.zGetActiveAccount().soapSend(
				"<FolderActionRequest xmlns='urn:zimbraMail'>"
			+		"<action id='" + folder.getId() + "' op='retentionpolicy'>"
			+			"<retentionPolicy>"
			+				"<keep>"
			+					"<policy lifetime='5d' type='user'/>"
			+				"</keep>"
			+			"</retentionPolicy>"
			+		"</action>"
			+	"</FolderActionRequest>");

		
		// Add a message to the folder
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" +
            		"<m l='"+ folder.getId() +"' f='f'>" +
                		"<content>From: foo@foo.com\n" +
"To: foo@foo.com \n" +
"Subject: "+ subject +"\n" +
"MIME-Version: 1.0 \n" +
"Content-Type: text/plain; charset=utf-8 \n" +
"Content-Transfer-Encoding: 7bit\n" +
"\n" +
"simple text string in the body\n" +
"</content>" +
                	"</m>" +
            	"</AddMsgRequest>");

		
		
		
		
		//-- GUI
		
		try {
			
			// Click Get Mail button
			app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
					
			// Click on the subfolder
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, folder);
			
			// Select the item
			app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
			
			// Click shift-del
			
			/* 
			 * Note: there will be two dialogs on this action:
			 * 1. This item is within the retention period (DialogWarningID.DeleteItemWithinRetentionPeriod)
			 * 2. Are you sure you want to permanently delete this item? (DialogWarningID.PermanentlyDeleteTheItem)
			 * 
			 * Luckily, both warning dialogs share the same div (<div id='OkCancel' .../>).  However,
			 * if the ID's change in the future, then the zPageMail.zKeyboardShortcut() method may need
			 * to be reworked.  The test cases for hard delete may need to do the zGetWarningDialog() instead.
			 * 
			 * 
			 */ 
			DialogWarning dialog = (DialogWarning)app.zPageMail.zKeyboardShortcut(Shortcut.S_MAIL_HARDELETE);
			dialog.zClickButton(Button.B_CANCEL);
		
		} finally {
			
			// Select the inbox
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox));

		}

		
		//-- Verification
		
		
		// Verify the message is hard deleted
		MailItem message = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +") is:anywhere");
		ZAssert.assertNotNull(message, "Verify message remains in the mailbox");
		ZAssert.assertEquals(
				message.dFolderId, 
				folder.getId(),
				"Verify message remains in the folder");

	}





}
