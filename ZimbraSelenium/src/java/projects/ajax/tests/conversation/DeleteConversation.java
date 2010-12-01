package projects.ajax.tests.conversation;

import java.util.List;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import framework.items.ConversationItem;
import framework.items.MailItem;
import framework.items.RecipientItem;
import framework.ui.Action;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;
import framework.util.ZimbraSeleniumProperties;

public class DeleteConversation extends AjaxCommonTest {

	public DeleteConversation() {
		logger.info("New "+ DeleteConversation.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		
		// Make sure we are using an account with conversation view
		ZimbraAccount account = new ZimbraAccount();
		account.provision();
		account.authenticate();
		account.modifyPreference("zimbraPrefGroupMailBy", "conversation");
			
		super.startingAccount = account;		
	}
	
	@Test(	description = "Delete a conversation",
			groups = { "smoke" })
	public void DeleteConversation01() throws HarnessException {
		
		
		// Create the message data to be sent
		MailItem mail = new MailItem();
		mail.aRecipients.add(new RecipientItem(app.getActiveAccount().EmailAddress));
		mail.aSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		mail.gBodyText = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.getActiveAccount().EmailAddress +"'/>" +
							"<su>"+ mail.aSubject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ mail.gBodyText +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.aSubject);
		
		// Click delete
		app.zPageMail.zToolbarPressButton(Button.B_DELETE);
		
		List<ConversationItem> conversations = app.zPageMail.zListGetConversations();
		ZAssert.assertNotNull(conversations, "Verify the conversation list exists");

		boolean found = false;
		for (ConversationItem c : conversations) {
			logger.info("Subject: looking for "+ mail.aSubject +" found: "+ c.subject);
			if ( c.subject.equals(mail.aSubject) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertFalse(found, "Verify the conversation is no longer in the inbox");
		
	}

}
