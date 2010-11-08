package projects.ajax.tests.mail.mail;

import java.util.List;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.Buttons;
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
		account.modifyPreference("zimbraPrefGroupMailBy", "message");
			
		super.startingAccount = account;		
		
	}
	
	@Test(	description = "Receive a mail",
			groups = { "smoke" })
	public void GetMail_01() throws HarnessException {
		
		// Create the message data to be sent
		MailItem mail = new MailItem();
		mail.recipients.add(new RecipientItem(app.getActiveAccount().EmailAddress));
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
		app.zPageMail.zToolbarPressButton(Buttons.B_GETMAIL);

		// Get all the messages in the inbox
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the conversation list exists");

		// Make sure the message appears in the list
		boolean found = false;
		for (MailItem m : messages) {
			logger.info("Subject: looking for "+ mail.subject +" found: "+ m.subject);
			if ( m.subject.equals(mail.subject) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertTrue(found, "Verify the message is in the inbox");

		
	}

}
