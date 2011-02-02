package com.zimbra.qa.selenium.projects.ajax.tests.mail.mail;

import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class FlagMail extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public FlagMail() {
		logger.info("New "+ FlagMail.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
				    put("zimbraPrefGroupMailBy", "message");
				}};
		
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
		app.zPageMail.zKeyboardShortcut(Shortcut.S_MAIL_MARKFLAG);
		
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
