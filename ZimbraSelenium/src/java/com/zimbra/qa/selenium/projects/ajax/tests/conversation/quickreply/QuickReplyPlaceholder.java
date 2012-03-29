package com.zimbra.qa.selenium.projects.ajax.tests.conversation.quickreply;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByConversationTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;


public class QuickReplyPlaceholder extends PrefGroupMailByConversationTest {

	public QuickReplyPlaceholder() {
		logger.info("New "+ QuickReplyPlaceholder.class.getCanonicalName());

	}
	
	@Test(	description = "Verify the Quick Reply Placeholder text (1 message, 1 recipient)",
			groups = { "smoke" })
	public void QuickReplyPlaceholder_01() throws HarnessException {
		
		ZimbraAccount account1 = new ZimbraAccount();
		account1.provision();
		account1.authenticate();
		
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String content = "content" + ZimbraSeleniumProperties.getUniqueString();
		
		account1.soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ content +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Get the display placeholder helper text
		String placeholder = display.zGetQuickReplyPlaceholder();
		
		ZAssert.assertStringContains(placeholder, account1.getPref("displayName"), "Verify the quick reply placeholder lists the sender");
		ZAssert.assertStringDoesNotContain(placeholder, app.zGetActiveAccount().getPref("displayName"), "Verify the quick reply placeholder does not list the active user");
		

	}

	@Test(	description = "Quick Reply to a conversation (1 message, 1 recipient, 1 CC, 1 BCC)",
			groups = { "functional" })
	public void QuickReplyPlaceholder_02() throws HarnessException {
		
		ZimbraAccount account1 = new ZimbraAccount();
		account1.provision();
		account1.authenticate();
		
		ZimbraAccount account2 = new ZimbraAccount();
		account2.provision();
		account2.authenticate();

		ZimbraAccount account3 = new ZimbraAccount();
		account3.provision();
		account3.authenticate();

		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String content = "content" + ZimbraSeleniumProperties.getUniqueString();
		
		account1.soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ account2.EmailAddress +"'/>" +
							"<e t='c' a='"+ account3.EmailAddress +"'/>" +
							"<e t='b' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ content +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Get the display placeholder helper text
		String placeholder = display.zGetQuickReplyPlaceholder();
		
		
		ZAssert.assertStringContains(placeholder, account1.getPref("displayName"), "Verify the placeholder lists the correct destination");
		ZAssert.assertStringContains(placeholder, account2.getPref("displayName"), "Verify the placeholder lists the correct destination");
		ZAssert.assertStringContains(placeholder, account3.getPref("displayName"), "Verify the placeholder lists the correct destination");
		ZAssert.assertStringDoesNotContain(placeholder, app.zGetActiveAccount().getPref("displayName"), "Verify the quick reply placeholder does not list the active user");

	}

	@Test(	description = "Quick Reply to a conversation (1 message, 2 to, 2 CC, 2 BCC)",
			groups = { "functional" })
	public void QuickReplyPlaceholder_03() throws HarnessException {
		
		ZimbraAccount sender1 = new ZimbraAccount();
		sender1.provision();
		sender1.authenticate();
		
		ZimbraAccount to1 = new ZimbraAccount();
		to1.provision();
		to1.authenticate();

		ZimbraAccount to2 = new ZimbraAccount();
		to2.provision();
		to2.authenticate();

		ZimbraAccount cc1 = new ZimbraAccount();
		cc1.provision();
		cc1.authenticate();

		ZimbraAccount cc2 = new ZimbraAccount();
		cc2.provision();
		cc2.authenticate();

		ZimbraAccount bcc1 = new ZimbraAccount();
		bcc1.provision();
		bcc1.authenticate();


		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String content = "content" + ZimbraSeleniumProperties.getUniqueString();
		
		sender1.soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ to1.EmailAddress +"'/>" +
							"<e t='t' a='"+ to2.EmailAddress +"'/>" +
							"<e t='c' a='"+ cc1.EmailAddress +"'/>" +
							"<e t='c' a='"+ cc2.EmailAddress +"'/>" +
							"<e t='b' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<e t='b' a='"+ bcc1.EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ content +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Get the display placeholder helper text
		String placeholder = display.zGetQuickReplyPlaceholder();
		
		
		ZAssert.assertStringContains(placeholder, sender1.getPref("displayName"), "Verify the placeholder lists the correct destination");
		ZAssert.assertStringContains(placeholder, to1.getPref("displayName"), "Verify the placeholder lists the correct destination");
		ZAssert.assertStringContains(placeholder, to2.getPref("displayName"), "Verify the placeholder lists the correct destination");
		ZAssert.assertStringContains(placeholder, cc1.getPref("displayName"), "Verify the placeholder lists the correct destination");
		ZAssert.assertStringContains(placeholder, cc2.getPref("displayName"), "Verify the placeholder lists the correct destination");
		ZAssert.assertStringDoesNotContain(placeholder, bcc1.getPref("displayName"), "Verify the quick reply placeholder does not list the active user");
		ZAssert.assertStringDoesNotContain(placeholder, app.zGetActiveAccount().getPref("displayName"), "Verify the quick reply placeholder does not list the active user");

	}


}
