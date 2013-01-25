package com.zimbra.qa.selenium.projects.ajax.tests.mail.sort.folders;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;


public class SortPerFolder extends PrefGroupMailByMessageTest {

	
	public SortPerFolder() {
		logger.info("New "+ SortPerFolder.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefReadingPaneLocation", "bottom");

	}
	
	@Bugs(ids = "30319")
	@Test(	description = "Sort a list of messages by subject in folderA and by From in folderB",
			groups = { "functional" })
	public void SortPerFolder_01() throws HarnessException {
		
		//-- DATA setup
		
		// Create the message data
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		String subject1 = "bbbb" + ZimbraSeleniumProperties.getUniqueString(); 
		String subject2 = "aaaa" + ZimbraSeleniumProperties.getUniqueString(); 
		String subject3 = "bbbb" + ZimbraSeleniumProperties.getUniqueString(); 
		String subject4 = "aaaa" + ZimbraSeleniumProperties.getUniqueString(); 
		String foldername1 = "folder1" + ZimbraSeleniumProperties.getUniqueString();
		String foldername2 = "folder2" + ZimbraSeleniumProperties.getUniqueString();
		
		// Create the folders
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ foldername1 +"' l='"+ inbox.getId() +"'/>" +
                "</CreateFolderRequest>");
		FolderItem folder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername1);

		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ foldername2 +"' l='"+ inbox.getId() +"'/>" +
                "</CreateFolderRequest>");
		FolderItem folder2 = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername2);

		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='"+ folder1.getId() +"' f='f'>"
        	+			"<content>From: bbbb@foo.com\n" // From: bbbb
        	+				"To: foo@foo.com \n"
        	+				"Subject: "+ subject1 +"\n"
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
    		+		"<m l='"+ folder1.getId() +"' >"
        	+			"<content>From: aaaa@foo.com\n" // From: aaaa
        	+				"To: foo@foo.com \n"
        	+				"Subject: "+ subject2 +"\n"
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
    		+		"<m l='"+ folder2.getId() +"' f='f'>"
        	+			"<content>From: bbbb@foo.com\n" //  From: bbbb
        	+				"To: foo@foo.com \n"
        	+				"Subject: "+ subject3 +"\n"
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
    		+		"<m l='"+ folder2.getId() +"' >"
        	+			"<content>From: aaaa@foo.com\n" //  From: aaaa
        	+				"To: foo@foo.com \n"
        	+				"Subject: "+ subject4 +"\n"
        	+				"MIME-Version: 1.0 \n"
        	+				"Content-Type: text/plain; charset=utf-8 \n"
        	+				"Content-Transfer-Encoding: 7bit\n"
        	+				"\n"
        	+				"simple text string in the body\n"
        	+			"</content>"
        	+		"</m>"
			+	"</AddMsgRequest>");



		//-- GUI Actions
		//
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Click on folder1
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, folder1);
		
		// First, sort by subject to clear the order
		app.zPageMail.zToolbarPressButton(Button.B_MAIL_LIST_SORTBY_FLAGGED);

		// Now, click on "subject"
		app.zPageMail.zToolbarPressButton(Button.B_MAIL_LIST_SORTBY_SUBJECT);
				
		// Click on folder2
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, folder2);
		
		// First, sort by subject to clear the order
		app.zPageMail.zToolbarPressButton(Button.B_MAIL_LIST_SORTBY_FLAGGED);

		// Now, click on "subject"
		app.zPageMail.zToolbarPressButton(Button.B_MAIL_LIST_SORTBY_FROM);
				
		
		
		
		
		//-- VERIFICATION
		
		// Log the preferences
		app.zGetActiveAccount().soapSend(
				"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
    		+		"<pref name='zimbraPrefSortOrder'/>"
			+	"</GetPrefsRequest>");
	
		// It is difficult to determine the format of the zimbraPrefSortOrder pref, since 
		// it is internal  to the web application (i.e. the format could be changed, and
		// the feature still continues to work).  Don't verify the pref data, instead verify
		// the GUI is working.
		
		
		
		// Verify the order in folder1 is by subject

		// Click on folder1
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, folder1);
		
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		// Make sure message1 appears before message2 in the list
		MailItem itemB = null;
		for (MailItem m : messages) {
			if ( subject2.equals(m.gSubject) ) {
				itemB = m;
			}
			if ( subject1.equals(m.gSubject) ) {
				// Item B must be found before Item A (i.e. unflagged appears before flagged)
				ZAssert.assertNotNull(itemB, "Item A is in the list.  Verify Item B has already been found.");
			}
		}
		
		ZAssert.assertNotNull(itemB, "Verify Item B was found.");

		
		
		// Verify the order in folder is by from

		// Click on folder1
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, folder1);
		
		messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		// Make sure message1 appears before message2 in the list
		itemB = null;
		for (MailItem m : messages) {
			if ( subject2.equals(m.gSubject) ) {
				itemB = m;
			}
			if ( subject1.equals(m.gSubject) ) {
				// Item B must be found before Item A (i.e. unflagged appears before flagged)
				ZAssert.assertNotNull(itemB, "Item A is in the list.  Verify Item B has already been found.");
			}
		}
		
		ZAssert.assertNotNull(itemB, "Verify Item B was found.");


	}




}
