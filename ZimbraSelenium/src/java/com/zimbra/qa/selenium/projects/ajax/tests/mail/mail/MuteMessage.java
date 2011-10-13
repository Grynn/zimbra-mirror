package com.zimbra.qa.selenium.projects.ajax.tests.mail.mail;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;


public class MuteMessage extends PrefGroupMailByMessageTest {

	
	public MuteMessage() {
		logger.info("New "+ MuteMessage.class.getCanonicalName());
		
		
		


		
	}
	
	@Bugs(ids = "38449")
	@Test(	description = "Mute a message (conversation) using Actions -> Mute",
			groups = { "smoke" })
	public void MuteMessage_01() throws HarnessException {
		
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
		
		// Click "mute"
		app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_MUTE);
		

		// Verify the redirected message is received
		throw new HarnessException("Need to determine how to verify the conversation is muted (from the server) - see bug 38449 and bug 63312");
		
	}

	@Bugs( ids = "65844")
	@Test(	description = "Mute message, using 'Mute' shortcut key",
			groups = { "functional" })
	public void MuteMessage_02() throws HarnessException {
		throw new HarnessException("See bug https://bugzilla.zimbra.com/show_bug.cgi?id=65844");
	}
	
	@Test(	description = "Mute message, using 'Right Click' -> 'Mute'",
			groups = { "smoke" })
	public void MuteMessage_03() throws HarnessException {
		
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
		
		// Click Mute
		app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.B_MUTE, mail.dSubject);
		

		// Verify the redirected message is received
		throw new HarnessException("Need to determine how to verify the conversation is muted (from the server) - see bug 38449 and bug 63312");


	}




}
