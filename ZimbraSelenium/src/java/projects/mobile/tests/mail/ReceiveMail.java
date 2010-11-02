package projects.mobile.tests.mail;

import java.util.List;

import org.testng.annotations.Test;

import projects.mobile.core.MobileCommonTest;
import framework.items.ConversationItem;
import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;
import framework.util.ZimbraSeleniumProperties;

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
						"<e t='t' a='"+ app.getActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ body +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		
		// Get the newly received message
		app.zPageMail.getMail();

		// Create the list of messages in the inbox
		List<ConversationItem> conversations = app.zPageMail.getConversationList();
		
		ZAssert.assertGreaterThan(conversations.size(), 0, "Verify that the list contains conversations");

		// Verify that the sent mail is in the list
		boolean found = false;
		for (ConversationItem c : conversations) {
			if ( c.subject.equals(subject)) {
				found = true;		// Found the message!
				break;
			}
		}
		
		ZAssert.assertTrue(found, "Verify that the newly sent message is received in the inbox");
		
	}

}
