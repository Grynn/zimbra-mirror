package projects.ajax.tests.mail.mail;

import java.util.List;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import framework.items.MailItem;
import framework.ui.Action;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;
import framework.util.ZimbraSeleniumProperties;

public class FlagMail extends AjaxCommonTest {

	public FlagMail() {
		logger.info("New "+ FlagMail.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccount = new ZimbraAccount();
		super.startingAccount.provision();
		super.startingAccount.authenticate();
		super.startingAccount.modifyPreference("zimbraPrefGroupMailBy", "message");
		
	}
	
	@Test(	description = "Flag a mail  clicking flagged icon",
			groups = { "smoke" })
	public void FlagMail_01() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");
		
		// Create a mail item to represent the message
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
		
		// Flag the item
		app.zPageMail.zListItem(Action.A_MAIL_FLAG, mail.dSubject);

		// Get the item from the list
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		MailItem listmail = null;
		for (MailItem m : messages) {
			logger.info("Subject: looking for "+ mail.dSubject +" found: "+ m.gSubject);
			if ( mail.dSubject.equals(m.gSubject) ) {
				listmail = m;
				break;
			}
		}

		// Make sure the GUI shows "flagged"
		ZAssert.assertNotNull(listmail, "Verify the message is in the list");
		ZAssert.assertTrue(listmail.gIsFlagged, "Verify the message is flagged in the list");
		
		// Make sure the server shows "flagged"
		mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertStringContains(mail.getFlags(), "f", "Verify the message is flagged in the server");

		
	}

	
	@Test(	description = "Flag a mail by using shortcut 'mf'",
			groups = { "smoke" })
	public void FlagMail_02() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");
		
		// Create a mail item to represent the message
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
		
		// Flag the item
		app.zKeyboard.zTypeCharacters("mf");
		
		// Wait for the client to send the data
		SleepUtil.sleepMedium();

		// Get the item from the list
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		MailItem listmail = null;
		for (MailItem m : messages) {
			logger.info("Subject: looking for "+ mail.dSubject +" found: "+ m.gSubject);
			if ( mail.dSubject.equals(m.gSubject) ) {
				listmail = m;
				break;
			}
		}

		// Make sure the GUI shows "flagged"
		ZAssert.assertNotNull(listmail, "Verify the message is in the list");
		ZAssert.assertTrue(listmail.gIsFlagged, "Verify the message is flagged in the list");
		
		// Make sure the server shows "flagged"
		mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertStringContains(mail.getFlags(), "f", "Verify the message is flagged in the server");

		
	}


}
