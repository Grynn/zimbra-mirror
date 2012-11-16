package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;


public class ReplyAllMail extends PrefGroupMailByMessageTest {

	ZimbraAccount account1 = null;
	ZimbraAccount account2 = null;
	ZimbraAccount account3 = null;
	ZimbraAccount account4 = null;
	
	public ReplyAllMail() {
		logger.info("New "+ ReplyAllMail.class.getCanonicalName());
	
		account1 = (new ZimbraAccount()).provision().authenticate();
		account2 = (new ZimbraAccount()).provision().authenticate();
		account3 = (new ZimbraAccount()).provision().authenticate();
		account4 = (new ZimbraAccount()).provision().authenticate();
		
	}
	
	@Test(	description = "Reply to all (test account in To field)",
			groups = { "functional" })
	public void ReplyMail_01() throws HarnessException {

		//-- DATA
		
		// Send a message to the account
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<e t='t' a='"+ account1.EmailAddress +"'/>" +
							"<e t='c' a='"+ account2.EmailAddress +"'/>" +
							"<e t='c' a='"+ account3.EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");



		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Reply the item
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLYALL);

		// Send the message
		mailform.zSubmit();



		//-- Verification
		
		// From the receiving end, verify the message details
		// Need 'in:inbox' to separate the message from the sent message
		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "in:sent subject:("+ subject +")");

		boolean foundAccountA = false;
		boolean foundAccount1 = false;
		boolean foundAccount2 = false;
		boolean foundAccount3 = false;
		
		
		// Check the To, which should only contain the original sender
		//
		
		ZAssert.assertEquals(sent.dToRecipients.size(), 1, "Verify the message is sent to 1 'to' recipient");
		for (RecipientItem r : sent.dToRecipients) {
			if ( r.dEmailAddress.equals(ZimbraAccount.AccountA().EmailAddress) ) {
				foundAccountA = true;
			}
		}
		ZAssert.assertTrue(foundAccountA, "Verify the original sender is in the To field");
		
		
		// Check the CC, which should contain the original To (not the sender), the original CC, and not the zimbra test account
		//
		
		ZAssert.assertEquals(sent.dCcRecipients.size(), 3, "Verify the message is sent to 3 'cc' recipients");
		for (RecipientItem r : sent.dCcRecipients) {
			if ( r.dEmailAddress.equals(account1.EmailAddress) ) {
				foundAccount1 = true;
			}
			if ( r.dEmailAddress.equals(account2.EmailAddress) ) {
				foundAccount2 = true;
			}
			if ( r.dEmailAddress.equals(account3.EmailAddress) ) {
				foundAccount3 = true;
			}
		}
		ZAssert.assertTrue(foundAccount1, "Verify the To is in the Cc field");
		ZAssert.assertTrue(foundAccount2, "Verify the Cc is in the Cc field");
		ZAssert.assertTrue(foundAccount3, "Verify the Cc is in the Cc field");


	}


	@Test(	description = "Reply to all (test account in Cc field)",
			groups = { "functional" })
	public void ReplyMail_02() throws HarnessException {

		//-- DATA
		
		// Send a message to the account
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ account1.EmailAddress +"'/>" +
							"<e t='t' a='"+ account2.EmailAddress +"'/>" +
							"<e t='c' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<e t='c' a='"+ account3.EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");



		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Reply the item
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLYALL);

		// Send the message
		mailform.zSubmit();



		//-- Verification
		
		// From the receiving end, verify the message details
		// Need 'in:inbox' to separate the message from the sent message
		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "in:sent subject:("+ subject +")");

		boolean foundAccountA = false;
		boolean foundAccount1 = false;
		boolean foundAccount2 = false;
		boolean foundAccount3 = false;
		
		
		// Check the To, which should only contain the original sender
		//
		
		ZAssert.assertEquals(sent.dToRecipients.size(), 1, "Verify the message is sent to 1 'to' recipient");
		for (RecipientItem r : sent.dToRecipients) {
			if ( r.dEmailAddress.equals(ZimbraAccount.AccountA().EmailAddress) ) {
				foundAccountA = true;
			}
		}
		ZAssert.assertTrue(foundAccountA, "Verify the original sender is in the To field");
		
		
		// Check the CC, which should contain the original To (not the sender), the original CC, and not the zimbra test account
		//
		
		ZAssert.assertEquals(sent.dCcRecipients.size(), 3, "Verify the message is sent to 3 'cc' recipients");
		for (RecipientItem r : sent.dCcRecipients) {
			if ( r.dEmailAddress.equals(account1.EmailAddress) ) {
				foundAccount1 = true;
			}
			if ( r.dEmailAddress.equals(account2.EmailAddress) ) {
				foundAccount2 = true;
			}
			if ( r.dEmailAddress.equals(account3.EmailAddress) ) {
				foundAccount3 = true;
			}
		}
		ZAssert.assertTrue(foundAccount1, "Verify the To is in the Cc field");
		ZAssert.assertTrue(foundAccount2, "Verify the Cc is in the Cc field");
		ZAssert.assertTrue(foundAccount3, "Verify the Cc is in the Cc field");


	}



}
