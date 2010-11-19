package projects.ajax.tests.preferences.general.searches;

import java.util.List;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.TreePreferences.TreeItem;
import framework.items.ConversationItem;
import framework.items.MailItem;
import framework.items.RecipientItem;
import framework.items.RecipientItem.RecipientType;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;
import framework.util.ZimbraSeleniumProperties;

public class ZimbraPrefIncludeTrashInSearch extends AjaxCommonTest {

	public ZimbraPrefIncludeTrashInSearch() {
		logger.info("New "+ ZimbraPrefIncludeTrashInSearch.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPagePreferences;

		// Make sure we are using an account with conversation view
		ZimbraAccount account = new ZimbraAccount();
		account.provision();
		account.authenticate();
		account.modifyPreference("zimbraPrefIncludeTrashInSearch", "TRUE");

			
		super.startingAccount = account;		
		
	}
	

	@Test(	description = "Verify zimbraPrefIncludeTrashInSearch setting when set to TRUE",
			groups = { "functional" })
	public void ZimbraPrefIncludeTrashInSearchTrue_01() throws HarnessException {
		

		// Go to "General"
		app.zTreePreferences.zClickTreeItem(TreeItem.General);
		
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
		
		MailItem message1 = new MailItem();
		message1.subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		message1.recipients.add(new RecipientItem("foo@example.com", RecipientType.From));
		message1.recipients.add(new RecipientItem("bar@example.com", RecipientType.To));
		message1.bodyText = query; 
		
		MailItem message2 = new MailItem();
		message2.subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		message2.recipients.add(new RecipientItem("foo@example.com", RecipientType.From));
		message2.recipients.add(new RecipientItem("bar@example.com", RecipientType.To));
		message2.bodyText = query; 
		
		
		// Determine the folder ID's for inbox and trash
		app.getActiveAccount().soapSend("<GetFolderRequest xmlns = 'urn:zimbraMail'/>");
		String inboxId = app.getActiveAccount().soapSelectValue("//mail:folder[@name='Inbox']", "id");
		String trashId = app.getActiveAccount().soapSelectValue("//mail:folder[@name='Trash']", "id");
		
		
		// Add a message to the inbox
		app.getActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" +
                	"<m l='"+ inboxId +"'>" +
                    	"<content>" + message1.generateMimeString() + "</content>" +
                    "</m>" +
                "</AddMsgRequest>");
		
		
		// Add a message to the trash
		app.getActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" +
                	"<m l='"+ trashId +"'>" +
                    	"<content>" + message2.generateMimeString() + "</content>" +
                    "</m>" +
                "</AddMsgRequest>");


		// Go to mail
		app.zPageMail.navigateTo();
		
		// Search for the query
		app.zPageSearch.zRunSearchQuery(query);
		
		// Verify that both messages are in the list
		List<ConversationItem> items = app.zPageMail.zListGetConversations();
		
		boolean found1 = false;
		boolean found2 = false;
		for (ConversationItem c : items) {
			if ( c.subject.equals(message1.subject) ) {
				found1 = true;
				break;
			}
		}
		for (ConversationItem c : items) {
			if ( c.subject.equals(message2.subject) ) {
				found2 = true;
				break;
			}
		}
		
		ZAssert.assertTrue(found1, "Verify the message in the inbox is found");
		ZAssert.assertTrue(found2, "Verify the message in the trash is found");
		
		
	}

}
