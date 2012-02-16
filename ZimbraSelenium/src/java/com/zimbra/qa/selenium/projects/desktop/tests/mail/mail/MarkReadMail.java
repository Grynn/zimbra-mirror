package com.zimbra.qa.selenium.projects.desktop.tests.mail.mail;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;


public class MarkReadMail extends AjaxCommonTest {

	public int delaySeconds = 5;
	
	@SuppressWarnings("serial")
	public MarkReadMail() {
		logger.info("New "+ MarkReadMail.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
				    put("zimbraPrefGroupMailBy", "message");
				    put("zimbraPrefMarkMsgRead", "" + delaySeconds);
				}};


	}
	
	@Test(	description = "Mark a message as read by clicking on it then waiting",
			groups = { "smoke" })
	public void MarkReadMail_01() throws HarnessException {
		
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
		
		// Wait to read the message
		SleepUtil.sleep(1000L * (delaySeconds));

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		// Wait the for the client to send the change to the server
		app.zPageMail.zWaitForBusyOverlay();
		
		// Verify the message is marked read in the server (flags attribute should not contain (u)nread)
		mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertStringDoesNotContain(mail.getFlags(), "u", "Verify the message is marked read in the server");
		
		// TODO: Verify the message is not marked unread in the list

		
	}

	@Test(	description = "Verify that if the message is not read for less than zimbraPrefMarkMsgRead, it is not read",
			groups = { "functional" })
	public void MarkReadMail_02() throws HarnessException {
		
		
		// Create the message data to be sent
		String subject1 = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		String subject2 = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject1 +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject2 +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");

		
		// Create a mail item to represent the message
		MailItem mail1 = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject1 +")");
		MailItem mail2 = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject2 +")");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail1.dSubject);
				
		// Select the next item immediately
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail2.dSubject);

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		// Verify the message is marked read in the server (flags attribute should not contain (u)nread)
		mail1 = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject1 +")");
		ZAssert.assertStringContains(mail1.getFlags(), "u", "Verify the message is marked read in the server");
		
		// TODO: Verify the message is not marked unread in the list

		

		
	}


	@Test(	description = "Mark a message as read by clicking on it, then using 'mr' hotkeys",
			groups = { "functional" })
	public void MarkReadMail_03() throws HarnessException {
		

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
		
		// TODO: need to L10N this
		app.zPageMail.zKeyboardShortcut(Shortcut.S_MAIL_MARKREAD);

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		// Verify the message is marked read in the server (flags attribute should not contain (u)nread)
		mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertStringDoesNotContain(mail.getFlags(), "u", "Verify the message is marked read in the server");
		
		// TODO: Verify the message is not marked unread in the list

		
	}

	@Test(	description = "Mark a message as read by context menu -> mark read",
			groups = { "functional" })
			public void MarkReadMail_04() throws HarnessException {
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
		app.zPageMail.zListItem(Action.A_RIGHTCLICK, Button.O_MARK_AS_READ, mail.dSubject);

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageBriefcase.zWaitForDesktopLoadingSpinner(5000);

      // Verify the message is marked read in the server (flags attribute should not contain (u)nread)
		mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertStringDoesNotContain(mail.getFlags(), "u", "Verify the message is marked read in the server");

	}
		


}
