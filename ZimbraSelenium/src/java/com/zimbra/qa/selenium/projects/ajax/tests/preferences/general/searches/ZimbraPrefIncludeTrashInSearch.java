package com.zimbra.qa.selenium.projects.ajax.tests.preferences.general.searches;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ConversationItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem.RecipientType;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ZimbraPrefIncludeTrashInSearch extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public ZimbraPrefIncludeTrashInSearch() {
		logger.info("New "+ ZimbraPrefIncludeTrashInSearch.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPagePreferences;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = new HashMap<String, String>() {{
				    put("zimbraPrefIncludeTrashInSearch", "TRUE");
				}};

			
		
	}
	

	@Test(	description = "Verify zimbraPrefIncludeTrashInSearch setting when set to TRUE",
			groups = { "functional" })
	public void ZimbraPrefIncludeTrashInSearchTrue_01() throws HarnessException {
		

		// Go to "General"
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.General);
		
		// Determine the status of the checkbox
		boolean checked = app.zPagePreferences.zGetCheckboxStatus("zimbraPrefIncludeTrashInSearch");
		
		// Since zimbraPrefIncludeSpamInSearch is set to TRUE, the checkbox should be checked
		ZAssert.assertTrue(checked, "Verify if zimbraPrefIncludeTrashInSearch is TRUE, the preference box is checked" );
		
		// Click cancel to close preferences
		app.zPagePreferences.zNavigateAway(Button.B_NO);
		
	}

	@Test(	description = "Verify when zimbraPrefIncludeTrashInSearch=TRUE, that trash is included in search",
			groups = { "functional" })
	public void ZimbraPrefIncludeTrashInSearchTrue_02() throws HarnessException {
		
		// Check that the setting is correct
		ZimbraPrefIncludeTrashInSearchTrue_01();
		
		String query = "query" + ZimbraSeleniumProperties.getUniqueString();
		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(),
		      FolderItem.SystemFolder.Inbox);
		
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
		
		
		// Determine the folder ID's for inbox and trash
		app.zGetActiveAccount().soapSend("<GetFolderRequest xmlns = 'urn:zimbraMail'/>");
		String inboxId = app.zGetActiveAccount().soapSelectValue("//mail:folder[@name='Inbox']", "id");
		String trashId = app.zGetActiveAccount().soapSelectValue("//mail:folder[@name='Trash']", "id");
		
		
		// Add a message to the inbox
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" +
                	"<m l='"+ inboxId +"'>" +
                    	"<content>" + message1.generateMimeString() + "</content>" +
                    "</m>" +
                "</AddMsgRequest>");
		
		
		// Add a message to the trash
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" +
                	"<m l='"+ trashId +"'>" +
                    	"<content>" + message2.generateMimeString() + "</content>" +
                    "</m>" +
                "</AddMsgRequest>");

		// Go to mail
		app.zPageMail.zNavigateTo();
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inboxFolder);
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Search for the query
		app.zPageSearch.zAddSearchQuery(query);
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		
		// Verify that both messages are in the list
		List<ConversationItem> items = app.zPageMail.zListGetConversations();
		
		boolean found1 = false;
		boolean found2 = false;
		for (ConversationItem c : items) {
			if ( c.subject.equals(message1.dSubject) ) {
				found1 = true;
				break;
			}
		}
		for (ConversationItem c : items) {
			if ( c.subject.equals(message2.dSubject) ) {
				found2 = true;
				break;
			}
		}
		
		ZAssert.assertTrue(found1, "Verify the message in the inbox is found");
		ZAssert.assertTrue(found2, "Verify the message in the trash is found");
		
		
	}

}
