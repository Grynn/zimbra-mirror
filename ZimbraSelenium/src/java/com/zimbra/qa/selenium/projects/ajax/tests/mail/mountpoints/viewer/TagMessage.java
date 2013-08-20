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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.mountpoints.viewer;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class TagMessage extends PrefGroupMailByMessageTest {

	public TagMessage() {
		logger.info("New "+ TagMessage.class.getCanonicalName());
		
		
		

		
	}
	
	@Test(	description = "Verify Permission Denied on Tag a shared mail (read-only share)",
			groups = { "functional" })
	public void TagMessage_01() throws HarnessException {
		
		
		//-- DATA Setup
		//
		
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String mountpointname = "mountpoint" + ZimbraSeleniumProperties.getUniqueString();
		
		FolderItem inbox = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), FolderItem.SystemFolder.Inbox);
		
		// Create a folder to share
		ZimbraAccount.AccountA().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" + inbox.getId() + "'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem folder = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), foldername);
		
		// Share it
		ZimbraAccount.AccountA().soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ folder.getId() +"' op='grant'>"
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='r'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		
		// Add a message to it
		ZimbraAccount.AccountA().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
        		+		"<m l='"+ folder.getId() +"' >"
            	+			"<content>From: foo@foo.com\n"
            	+				"To: foo@foo.com \n"
            	+				"Subject: "+ subject +"\n"
            	+				"MIME-Version: 1.0 \n"
            	+				"Content-Type: text/plain; charset=utf-8 \n"
            	+				"Content-Transfer-Encoding: 7bit\n"
            	+				"\n"
            	+				"simple text string in the body\n"
            	+			"</content>"
            	+		"</m>"
				+	"</AddMsgRequest>");
		
		MailItem mail = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject +")");

		
		// Mount it
		app.zGetActiveAccount().soapSend(
					"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointname +"'  rid='"+ folder.getId() +"' zid='"+ ZimbraAccount.AccountA().ZimbraId +"'/>"
				+	"</CreateMountpointRequest>");
		
		FolderMountpointItem mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointname);

		// Create Tag
		TagItem tag = TagItem.CreateUsingSoap(app.zGetActiveAccount());
		ZAssert.assertNotNull(tag, "Verify the tag was created");
		
		
		//-- GUI Actions
		//
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		try {

			// Click on the mountpoint
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, mountpoint);
	
			// Select the item
			app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
			
	
			
			//-- VERIFICATION
			//
			
			// Make sure the tag icon is disabled
			String locator = "css=div#zb__TV-main__TAG_MENU div.ImgTag.ZDisabledImage";
			boolean disabled = app.zPageMail.sIsElementPresent(locator);
			ZAssert.assertTrue(disabled, "Verify that the tag menu is disabled: "+ locator);
			
		} finally {
			
			// Select the inbox
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox));

		}

	}

	
	@Test(	description = "Verify Permission Denied on Tag (keyboard='t') a shared mail (read-only share)",
			groups = { "deprecated" }) // See bug: http://bugzilla.zimbra.com/show_bug.cgi?id=79887
	public void TagMessage_02() throws HarnessException {
		
		
		//-- DATA Setup
		//
		
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String mountpointname = "mountpoint" + ZimbraSeleniumProperties.getUniqueString();
		
		FolderItem inbox = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), FolderItem.SystemFolder.Inbox);
		
		// Create a folder to share
		ZimbraAccount.AccountA().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" + inbox.getId() + "'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem folder = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), foldername);
		
		// Share it
		ZimbraAccount.AccountA().soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ folder.getId() +"' op='grant'>"
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='r'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		
		// Add a message to it
		ZimbraAccount.AccountA().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
        		+		"<m l='"+ folder.getId() +"' >"
            	+			"<content>From: foo@foo.com\n"
            	+				"To: foo@foo.com \n"
            	+				"Subject: "+ subject +"\n"
            	+				"MIME-Version: 1.0 \n"
            	+				"Content-Type: text/plain; charset=utf-8 \n"
            	+				"Content-Transfer-Encoding: 7bit\n"
            	+				"\n"
            	+				"simple text string in the body\n"
            	+			"</content>"
            	+		"</m>"
				+	"</AddMsgRequest>");
		
		MailItem mail = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject +")");

		// Mount it
		app.zGetActiveAccount().soapSend(
					"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointname +"'  rid='"+ folder.getId() +"' zid='"+ ZimbraAccount.AccountA().ZimbraId +"'/>"
				+	"</CreateMountpointRequest>");
		
		FolderMountpointItem mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointname);

		// Create Tag
		TagItem tag = TagItem.CreateUsingSoap(app.zGetActiveAccount());
		ZAssert.assertNotNull(tag, "Verify the tag was created");
		
		
		//-- GUI Actions
		//
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		try {

			// Click on the mountpoint
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, mountpoint);
	
			// Select the item
			app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
			
			// Tag the item (shortcut)
			// Maybe a bug? ... the tag dialog opens: https://bugzilla.zimbra.com/show_bug.cgi?id=79887
			DialogTagPicker dialogTag = (DialogTagPicker)app.zPageMail.zKeyboardShortcut(Shortcut.S_MAIL_TAG);
			dialogTag.zClickTreeTag(tag);
			dialogTag.zClickButton(Button.B_OK);
	
			// SEE bug: http://bugzilla.zimbra.com/show_bug.cgi?id=79887
			
			//-- VERIFICATION
			//
			
			// A "Permission Denied" error popup should occur
			DialogError dialog = app.zPageMain.zGetErrorDialog(DialogError.DialogErrorID.Zimbra);
			ZAssert.assertNotNull(dialog, "Verify the PERM DENIED Error Dialog is created");
			ZAssert.assertTrue(dialog.zIsActive(), "Verify the PERM DENIED Error Dialog is active");
			dialog.zClickButton(Button.B_OK);

		} finally {
			
			// Select the inbox
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox));

		}


		
	}


}
