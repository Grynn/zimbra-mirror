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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.mountpoints.manager;

import java.util.Arrays;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class TagMessage extends PrefGroupMailByMessageTest {

	public TagMessage() {
		logger.info("New "+ TagMessage.class.getCanonicalName());
		
		
		

		
	}
	
	@Test(	description = "Verify success on Tag a shared mail (manager share)",
			groups = { "functional" })
	public void TagMessage_01() throws HarnessException {
		
		
		//-- DATA Setup
		//
		
		String tagName = "tag" + ZimbraSeleniumProperties.getUniqueString();
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
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='rwidx'/>"
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
		
		
		//-- GUI Actions
		//
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		try {

			// Click on the mountpoint
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, mountpoint);
	
			// Select the item
			app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
			
	
			// Click new tag
			DialogTag dialogTag = (DialogTag) app.zPageMail.zToolbarPressPulldown(
					Button.B_TAG, Button.O_TAG_NEWTAG);
			dialogTag.zSetTagName(tagName);
			dialogTag.zClickButton(Button.B_OK);
		
		} finally {
			
			// Select the inbox
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox));

		}

		
		//-- VERIFICATION
		//
		
		// Verify the message is tagged
		mail = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject +")");
		ZAssert.assertNotNull(mail, "Verify the message is found");
		boolean found = false;
		for (String tn : mail.getTagNames()) {
			if ( tn.equalsIgnoreCase(tagName) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertTrue(
				found, 
				"Verify the message contains the tag: " + 
				Arrays.toString(mail.getTagNames().toArray()) + 
				" contains " + 
				tagName);

	}

	@Bugs(ids="79948")
	@Test(	description = "Verify success on Tag (keyboard='t') a shared mail (manager share)",
			groups = { "functional" })
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
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='rwidx'/>"
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
			
			// Tag the item
			DialogTagPicker dialogTag = (DialogTagPicker)app.zPageMail.zKeyboardShortcut(Shortcut.S_MAIL_TAG);
			dialogTag.zClickTreeTag(tag);
			dialogTag.zClickButton(Button.B_OK);
		
		} finally {
			
			// Select the inbox
			app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox));

		}

		
		//-- VERIFICATION
		//
		
		// Verify the message is tagged
		mail = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject +")");
		ZAssert.assertNotNull(mail, "Verify the message is found");
		boolean found = false;
		for (String tn : mail.getTagNames()) {
			if ( tn.equalsIgnoreCase(tag.getName()) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertTrue(found, "Verify the message contains the tag: "
				+ Arrays.toString(mail.getTagNames().toArray()) + " contains "
				+ tag.getName()
				+ "https://bugzilla.zimbra.com/show_bug.cgi?id=79948");
		
	}


}
