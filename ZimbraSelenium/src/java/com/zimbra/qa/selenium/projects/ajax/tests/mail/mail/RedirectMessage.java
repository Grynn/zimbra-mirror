package com.zimbra.qa.selenium.projects.ajax.tests.mail.mail;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogRedirect;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogRedirect.Field;


public class RedirectMessage extends PrefGroupMailByMessageTest {

	
	public RedirectMessage() {
		logger.info("New "+ RedirectMessage.class.getCanonicalName());
		
		
		


		
	}
	
	@Bugs(ids = "14110")
	@Test(	description = "Redirect message, using 'Redirect' toolbar button",
			groups = { "smoke" })
	public void RedirectMessage_01() throws HarnessException {
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
	

		// Send a message to the account
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
		
		// Get the mail item for the new message
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		
		

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
		
		// Click redirect
		DialogRedirect dialog = (DialogRedirect)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_REDIRECT);
		dialog.zFillField(Field.To, ZimbraAccount.AccountB().EmailAddress);
		dialog.zClickButton(Button.B_OK);
		

		// Verify the redirected message is received
		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountB(), "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the redirected message is received");
		ZAssert.assertEquals(received.dRedirectedFromRecipient.dEmailAddress, app.zGetActiveAccount().EmailAddress, "Verify the message shows as redirected from the test account");


	}

	@Bugs( ids = "62170")
	@Test(	description = "Redirect message, using 'Redirect' shortcut key",
			groups = { "functional" })
	public void RedirectMessage_02() throws HarnessException {
		throw new HarnessException("See bug https://bugzilla.zimbra.com/show_bug.cgi?id=62170");
	}
	
	@Test(	description = "Redirect message, using 'Right Click' -> 'Redirect'",
			groups = { "smoke" })
	public void RedirectMessage_03() throws HarnessException {
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
	

		// Send a message to the account
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
		
		// Get the mail item for the new message
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		
		

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Click redirect
		DialogRedirect dialog = (DialogRedirect)app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.B_REDIRECT, mail.dSubject);
		dialog.zFillField(Field.To, ZimbraAccount.AccountB().EmailAddress);
		dialog.zClickButton(Button.B_OK);
		

		// Verify the redirected message is received
		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountB(), "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the redirected message is received");
		ZAssert.assertEquals(received.dRedirectedFromRecipient.dEmailAddress, app.zGetActiveAccount().EmailAddress, "Verify the message shows as redirected from the test account");


	}




}
