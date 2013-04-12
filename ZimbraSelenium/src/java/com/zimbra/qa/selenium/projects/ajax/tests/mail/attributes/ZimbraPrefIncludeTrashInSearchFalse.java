/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.attributes;

import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.RecipientItem.RecipientType;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;


public class ZimbraPrefIncludeTrashInSearchFalse extends PrefGroupMailByMessageTest {

	public ZimbraPrefIncludeTrashInSearchFalse() {
		logger.info("New "+ ZimbraPrefIncludeTrashInSearchFalse.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefIncludeTrashInSearch", "FALSE");

	}
	

	@Test(	
			description = "Verify when zimbraPrefIncludeTrashInSearch=FALSE, that trash is *not* included in search",
			groups = { "functional" })
	public void ZimbraPrefIncludeTrashInSearchFalse_02() throws HarnessException {
		
		//-- DATA setup
		
		String query = "query" + ZimbraSeleniumProperties.getUniqueString();
		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		FolderItem trashFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Trash);
		
		MailItem message1 = new MailItem();
		message1.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		message1.dFromRecipient = new RecipientItem("foo@example.com", RecipientType.From);
		message1.dToRecipients.add(new RecipientItem("bar@example.com", RecipientType.To));
		message1.dBodyText = query; 
		
		MailItem message2 = new MailItem();
		message2.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		message2.dFromRecipient = new RecipientItem("foo@example.com", RecipientType.From);
		message2.dToRecipients.add(new RecipientItem("bar@example.com", RecipientType.To));
		message2.dBodyText = query; 
		
		
		
		
		// Add a message to the inbox
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" +
                	"<m l='"+ inboxFolder.getId() +"'>" +
                    	"<content>" + message1.generateMimeString() + "</content>" +
                    "</m>" +
                "</AddMsgRequest>");
		
		
		// Add a message to the trash
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" +
                	"<m l='"+ trashFolder.getId() +"'>" +
                    	"<content>" + message2.generateMimeString() + "</content>" +
                    "</m>" +
                "</AddMsgRequest>");

		
		
		//-- GUI Actions
		
		
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Search for the query
		app.zPageSearch.zAddSearchQuery(query);
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		
		
		//-- Verification
		
		// Verify that both messages are in the list
		List<MailItem> items = app.zPageSearch.zListGetMessages();
		
		boolean found1 = false;
		boolean found2 = false;
		for (MailItem c : items) {
			if ( message1.dSubject.equals(c.gSubject) ) {
				found1 = true;
				break;
			}
		}
		for (MailItem c : items) {
			if ( message2.dSubject.equals(c.gSubject) ) {
				found2 = true;
				break;
			}
		}

		/*
		String listItem = "css=div[id*=zl__CLV-SR-Mail][class=DwtListView] [id*=zli__CLV-SR-Mail]";
		ZAssert.assertTrue(app.zPageSearch.sIsElementPresent(listItem + ":contains(" + message1.dSubject + ")"), "Verify the message in the inbox is found");
		ZAssert.assertTrue(app.zPageSearch.sIsElementPresent(listItem + ":contains(" + message2.dSubject + ")"), "Verify the message in the trash is found");
		*/

		ZAssert.assertTrue(found1, "Verify the message in the inbox is found");
		ZAssert.assertFalse(found2, "Verify the message in the trash is *not* found");
		
	}

}
