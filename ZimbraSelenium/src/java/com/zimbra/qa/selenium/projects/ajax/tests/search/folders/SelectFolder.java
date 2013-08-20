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
package com.zimbra.qa.selenium.projects.ajax.tests.search.folders;

import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;


public class SelectFolder extends PrefGroupMailByMessageTest {

	
	public SelectFolder() {
		logger.info("New "+ SelectFolder.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 3685575017990609879L;
		{
				    put("zimbraPrefGroupMailBy", "message");
				}};


	}
	
	@Test(	description = "Left click on folder - verify messages in that folder are shown",
			groups = { "functional" })
	public void SelectFolder_01() throws HarnessException {
		
		
		//-- DATA
		
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		String subject1 = "subjecta" + ZimbraSeleniumProperties.getUniqueString();
		String subject2 = "subjectb" + ZimbraSeleniumProperties.getUniqueString();

		// Create twp folders
		String foldername1 = "folder" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ foldername1 +"' l='"+ inbox.getId() +"'/>" +
                "</CreateFolderRequest>");
		FolderItem folder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername1);

		String foldername2 = "folder" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ foldername2 +"' l='"+ inbox.getId() +"'/>" +
                "</CreateFolderRequest>");
		FolderItem folder2 = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername2);

		
		// Create a message (folder1)
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" + 
					"<m l='"+ folder1.getId() + "'>" +
						"<content>MIME-Version: 1.0 \n" +
							"From: foo@foo.com\n" +
							"To: foo@foo.com \n" +
							"Subject: " + subject1 + "\n" + 
							"Content-Type: text/plain; charset=utf-8 \n" +
							"Content-Transfer-Encoding: 7bit\n" + "\n" +
							"simple text string in the body\n" + 
						"</content>" +
					"</m>" + "</AddMsgRequest>");

		// Create a message (folder1)
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" + 
					"<m l='"+ folder2.getId() + "'>" +
						"<content>MIME-Version: 1.0 \n" +
							"From: foo@foo.com\n" +
							"To: foo@foo.com \n" +
							"Subject: " + subject2 + "\n" + 
							"Content-Type: text/plain; charset=utf-8 \n" +
							"Content-Transfer-Encoding: 7bit\n" + "\n" +
							"simple text string in the body\n" + 
						"</content>" +
					"</m>" + "</AddMsgRequest>");


		
		
		//-- GUI
		

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Click on the tag from the tree
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, folder1);
		

		
		
		
		//-- VERIFICATION

		boolean found = false;
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		
		// Verify the tagged message shows
		for (MailItem m : messages) {
			if ( subject1.equals(m.gSubject) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertTrue(found, "Verify the message in the subfolder appears");
		
		
		// Verify the un-tagged message does not show
		found = false;
		for (MailItem m : messages) {
			if ( subject2.equals(m.gSubject) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertFalse(found, "Verify the message outside the subfolder does not appear");

	}


}
