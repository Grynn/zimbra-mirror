package com.zimbra.qa.selenium.projects.ajax.tests.mail.mail;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;


public class EditAsNewMessage extends PrefGroupMailByMessageTest {

	
	public EditAsNewMessage() {
		logger.info("New "+ EditAsNewMessage.class.getCanonicalName());
		
	}
	
	
	
	@Test(	description = "'Edit as new' message, using 'Actions -> Edit as New' toolbar button",
			groups = { "smoke" })
	public void EditAsNewMessage_01() throws HarnessException {
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
	

		// Send a message to the account
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
							"<e t='c' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
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
		FormMailNew form = (FormMailNew)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.O_EDIT_AS_NEW);
		form.zSubmit();
		

		// Verify the redirected message is received
		MailItem original = MailItem.importFromSOAP(ZimbraAccount.AccountB(), "subject:("+subject+") from:("+ZimbraAccount.AccountA().EmailAddress+")");
		ZAssert.assertNotNull(original, "Verify the original message from Account A is received by Account B");
		
		MailItem resent = MailItem.importFromSOAP(ZimbraAccount.AccountB(), "subject:("+subject+") from:("+app.zGetActiveAccount().EmailAddress+")");
		ZAssert.assertNotNull(resent, "Verify the 'edit as new' message from the test account is received by Account B");



	}

	
	@Test(	description = "'Edit as new' message, using 'Right Click' -> 'Edit as new'",
			groups = { "functional" })
	public void EditAsNewMessage_02() throws HarnessException {
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
	

		// Send a message to the account
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
							"<e t='c' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
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
		FormMailNew form = (FormMailNew)app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.O_EDIT_AS_NEW, mail.dSubject);
		form.zSubmit();
		

		// Verify the redirected message is received
		MailItem original = MailItem.importFromSOAP(ZimbraAccount.AccountB(), "subject:("+subject+") from:("+ZimbraAccount.AccountA().EmailAddress+")");
		ZAssert.assertNotNull(original, "Verify the original message from Account A is received by Account B");
		
		MailItem resent = MailItem.importFromSOAP(ZimbraAccount.AccountB(), "subject:("+subject+") from:("+app.zGetActiveAccount().EmailAddress+")");
		ZAssert.assertNotNull(resent, "Verify the 'edit as new' message from the test account is received by Account B");


	}




}
