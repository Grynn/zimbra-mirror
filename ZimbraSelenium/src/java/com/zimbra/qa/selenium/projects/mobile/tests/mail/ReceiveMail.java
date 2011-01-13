package com.zimbra.qa.selenium.projects.mobile.tests.mail;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ConversationItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.mobile.core.MobileCommonTest;


public class ReceiveMail extends MobileCommonTest {

	public ReceiveMail() {
		logger.info("New "+ ReceiveMail.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMain;
		super.startingAccount = null;
		
	}
	
	@Test(	description = "Verify a new mail shows up in the message list",
			groups = { "sanity" })
	public void ReceiveMail_01() throws HarnessException, InterruptedException {

		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();

		// Send a message from user1		
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ body +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		
		// Get the newly received message
		app.zPageMail.zRefresh();

		// Create the list of messages in the inbox
		List<ConversationItem> conversations = app.zPageMail.zListGetConversations();
		
		ZAssert.assertGreaterThan(conversations.size(), 0, "Verify that the list contains conversations");

		// Verify that the sent mail is in the list
		boolean found = false;
		for (ConversationItem c : conversations) {
			
			// subject could be truncated and end with "..." ... strip that.
			String s = c.gSubject.trim();
			if ( c.gSubject.trim().endsWith("...") ) {
				s = c.gSubject.trim().replace("...", "");
			}
			
			// subject could be truncated, so check containing rather than equals
			if ( subject.contains(s) ) {
				found = true;		// Found the message!
				break;
			}
		}
		
		ZAssert.assertTrue(found, "Verify that the newly sent message is received in the inbox");
		
	}

}
