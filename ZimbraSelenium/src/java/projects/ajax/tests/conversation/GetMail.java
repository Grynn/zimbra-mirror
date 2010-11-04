package projects.ajax.tests.conversation;

import java.util.List;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import framework.items.ConversationItem;
import framework.items.MailItem;
import framework.items.RecipientItem;
import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;
import framework.util.ZimbraSeleniumProperties;

public class GetMail extends AjaxCommonTest {

	public GetMail() {
		logger.info("New "+ GetMail.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with conversation view
		ZimbraAccount account = new ZimbraAccount();
		account.provision();
		account.authenticate();
		account.modifyPreference("zimbraPrefGroupMailBy", "conversation");
			
		super.startingAccount = account;		
		
	}
	
	@Test(	description = "Receive a mail",
			groups = { "sanity" })
	public void GetMail_01() throws HarnessException {
		
		
		// Create the message data to be sent
		MailItem mail = new MailItem();
		mail.recipients.add(new RecipientItem(ZimbraAccount.AccountA()));
		mail.subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		mail.bodyText = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.getActiveAccount().EmailAddress +"'/>" +
							"<su>"+ mail.subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ mail.bodyText +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarGetMail();
				
		// Get the list of messages
		List<ConversationItem> conversations = app.zPageMail.zListGetConversations();
		ZAssert.assertNotNull(conversations, "Verify the conversation list exists");

		boolean found = false;
		for (ConversationItem c : conversations) {
			logger.info("Subject: looking for "+ mail.subject +" found: "+ c.subject);
			if ( c.subject.equals(mail.subject) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertTrue(found, "Verify the message was received in the inbox");
		

		
	}

}
