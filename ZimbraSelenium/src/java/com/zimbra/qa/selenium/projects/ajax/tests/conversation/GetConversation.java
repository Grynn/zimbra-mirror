package com.zimbra.qa.selenium.projects.ajax.tests.conversation;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ConversationItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class GetConversation extends AjaxCommonTest {

	public GetConversation() {
		logger.info("New "+ GetConversation.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with conversation view
		ZimbraAccount account = new ZimbraAccount();
		account.provision();
		account.authenticate();
		account.modifyPreference("zimbraPrefGroupMailBy", "conversation");
			
		super.startingAccount = account;		
		
	}
	
	@Test(	description = "Receive a conversation",
			groups = { "smoke" })
	public void GetConversation01() throws HarnessException {
		
		
		// Create the message data to be sent
		MailItem mail = new MailItem();
		mail.dToRecipients.add(new RecipientItem(app.zGetActiveAccount().EmailAddress));
		mail.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		mail.gBodyText = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ mail.dSubject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ mail.gBodyText +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Get the list of messages
		List<ConversationItem> conversations = app.zPageMail.zListGetConversations();
		ZAssert.assertNotNull(conversations, "Verify the conversation list exists");

		boolean found = false;
		for (ConversationItem c : conversations) {
			logger.info("Subject: looking for "+ mail.dSubject +" found: "+ c.subject);
			if ( c.subject.equals(mail.dSubject) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertTrue(found, "Verify the conversation was received in the inbox");
		

		
	}

}
