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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.group.messages;

import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;


public class GroupByFrom extends PrefGroupMailByMessageTest {

	
	public GroupByFrom() {
		logger.info("New "+ GroupByFrom.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefReadingPaneLocation", "bottom");
	}
	
	
	/**
	 * Steps:
	 * 1. Receive messages from more than 1 user
	 * 2. Right click on the message column area -> Group By -> From
	 * 3. Verify messages are grouped by From
	 * @throws HarnessException
	 */
	@Test(	description = "Group a list of messages by From",
			groups = { "functional" })
	public void GroupByFrom_01() throws HarnessException {
		
		//-- DATA
		
		String subjectA = "subjectA" + ZimbraSeleniumProperties.getUniqueString();
		String subjectB = "subjectB" + ZimbraSeleniumProperties.getUniqueString();

		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		
		// Add the messages
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='"+ inbox.getId() +"'>"
        	+			"<content>From: "+ ZimbraAccount.AccountA().EmailAddress + "\n"
        	+				"To: foo@foo.com \n"
        	+				"Subject: "+ subjectA +"\n"
        	+				"MIME-Version: 1.0 \n"
        	+				"Content-Type: text/plain; charset=utf-8 \n"
        	+				"Content-Transfer-Encoding: 7bit\n"
        	+				"\n"
        	+				"simple text string in the body\n"
        	+			"</content>"
        	+		"</m>"
			+	"</AddMsgRequest>");

	
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='"+ inbox.getId() +"' >"
        	+			"<content>From: "+ ZimbraAccount.AccountB().EmailAddress +"\n"
        	+				"To: foo@foo.com \n"
        	+				"Subject: "+ subjectB +"\n"
        	+				"MIME-Version: 1.0 \n"
        	+				"Content-Type: text/plain; charset=utf-8 \n"
        	+				"Content-Transfer-Encoding: 7bit\n"
        	+				"\n"
        	+				"simple text string in the body\n"
        	+			"</content>"
        	+		"</m>"
			+	"</AddMsgRequest>");

	

		//-- GUI
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Right click on Subject header area -> Group By -> From
		// Since this appears to be a very specific action, don't make a
		// method for it.  Just reuse the Toolbar Press Button method.  Even
		// though the action is right click -> hover -> select
		app.zPageMail.zToolbarPressButton(Button.B_MAIL_LIST_GROUPBY_FROM);
		
		
		
		//-- VERIFICATION
		

		// Verify the preferences are correct
		app.zGetActiveAccount().soapSend(
				"<GetMailboxMetadataRequest xmlns='urn:zimbraMail'>"
	    	+		"<meta section='zwc:implicit'>"
	    	+			"<a n='zimbraPrefGroupByList'/>"
    		+		"</meta>"
			+	"</GetMailboxMetadataRequest>");
		
		String zimbraPrefGroupByList = app.zGetActiveAccount().soapSelectValue("//mail:a[@n='zimbraPrefGroupByList']", null);
		ZAssert.assertStringContains(zimbraPrefGroupByList, "GROUPBY_FROM", "Verify user preference has changed to include Group By = From");
		
		app.zGetActiveAccount().soapSend(
				"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
    		+		"<pref name='zimbraPrefSortOrder'/>"
			+	"</GetPrefsRequest>");
	
		
		// TODO: Verify the grouping
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertGreaterThan(messages.size(), 0, "Verify the messages appear");
		

	}



}
