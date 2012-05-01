package com.zimbra.qa.selenium.projects.ajax.tests.conversation;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByConversationTest;


public class GetConversation extends PrefGroupMailByConversationTest {

	public GetConversation() {
		logger.info("New "+ GetConversation.class.getCanonicalName());
		
	
		
	}
	
	@Test(	description = "Receive a conversation",
			groups = { "smoke" })
	public void GetConversation01() throws HarnessException {
		
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Get the list of messages
		List<MailItem> conversations = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(conversations, "Verify the conversation list exists");

		boolean found = false;
		for (MailItem c : conversations) {
			logger.info("Subject: looking for "+ subject +" found: "+ c.gSubject);
			if ( subject.equals(c.gSubject) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertTrue(found, "Verify the conversation was received in the inbox");
		

		
	}

}
