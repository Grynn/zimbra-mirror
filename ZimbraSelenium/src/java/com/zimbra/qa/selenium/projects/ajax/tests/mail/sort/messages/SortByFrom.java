package com.zimbra.qa.selenium.projects.ajax.tests.mail.sort.messages;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;


public class SortByFrom extends PrefGroupMailByMessageTest {

	
	public SortByFrom() {
		logger.info("New "+ SortByFrom.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefReadingPaneLocation", "bottom");
	}
	
	@Test(	description = "Sort a list of messages by from (Z->A)",
			groups = { "functional" })
	public void SortByFrom_01() throws HarnessException {
		
		// Create the message data
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		String subjectA = "subjectA" + ZimbraSeleniumProperties.getUniqueString(); // flagged
		String subjectB = "subjectB" + ZimbraSeleniumProperties.getUniqueString(); // not flagged
		
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='"+ inbox.getId() +"' f='f'>"
        	+			"<content>From: aorigination@foo.com\n"
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
        	+			"<content>From: borigination@foo.com\n"
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

	


		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Click on Inbox
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);
		
		// First, sort by subject to clear the order
		app.zPageMail.zToolbarPressButton(Button.B_MAIL_LIST_SORTBY_SUBJECT);
		
		// Now, click on "flagged"
		app.zPageMail.zToolbarPressButton(Button.B_MAIL_LIST_SORTBY_FROM);
		
		// Get all the messages in the inbox
		app.zGetActiveAccount().soapSend(
				"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
    		+		"<pref name='zimbraPrefSortOrder'/>"
			+	"</GetPrefsRequest>");
	
		
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		// Make sure the unflagged message appears in the list
		MailItem itemA = null;
		for (MailItem m : messages) {
			if ( subjectA.equals(m.gSubject) ) {
				itemA = m;
			}
			if ( subjectB.equals(m.gSubject) ) {
				// Item A must be found before Item B (i.e. flagged appears before unflagged)
				ZAssert.assertNotNull(itemA, "Item B is in the list.  Verify Item A has already been found.");
			}
		}
		
		ZAssert.assertNotNull(itemA, "Verify Item B was found.");

	}


	@Test(	description = "Sort a list of messages by from (A->Z)",
			groups = { "functional" })
	public void SortByFrom_02() throws HarnessException {
		
		// Create the message data
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		String subjectA = "subjectA" + ZimbraSeleniumProperties.getUniqueString(); // flagged
		String subjectB = "subjectB" + ZimbraSeleniumProperties.getUniqueString(); // not flagged
		
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='"+ inbox.getId() +"' f='f'>"
        	+			"<content>From: aorigination@foo.com\n"
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
        	+			"<content>From: borigination@foo.com\n"
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

	


		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Click on Inbox
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);
		
		// First, sort by subject to clear the order
		app.zPageMail.zToolbarPressButton(Button.B_MAIL_LIST_SORTBY_SUBJECT);
		
		// Now, click on "flagged" to sort by "unflagged"
		app.zPageMail.zToolbarPressButton(Button.B_MAIL_LIST_SORTBY_FROM);
		
		// Now, click on "flagged" to sort by "flagged"
		app.zPageMail.zToolbarPressButton(Button.B_MAIL_LIST_SORTBY_FROM);
		
		// Get all the messages in the inbox
		app.zGetActiveAccount().soapSend(
				"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
    		+		"<pref name='zimbraPrefSortOrder'/>"
			+	"</GetPrefsRequest>");
	
		
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");


		// Make sure the unflagged message appears in the list
		MailItem itemB = null;
		for (MailItem m : messages) {
			if ( subjectB.equals(m.gSubject) ) {
				itemB = m;
			}
			if ( subjectA.equals(m.gSubject) ) {
				// Item B must be found before Item A (i.e. unflagged appears before flagged)
				ZAssert.assertNotNull(itemB, "Item A is in the list.  Verify Item B has already been found.");
			}
		}
		
		ZAssert.assertNotNull(itemB, "Verify Item B was found.");


		
	}


}
