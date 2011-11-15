package com.zimbra.qa.selenium.projects.ajax.tests.mail.mail;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;


public class PriorityMail extends PrefGroupMailByMessageTest {

	
	public PriorityMail() {
		logger.info("New "+ PriorityMail.class.getCanonicalName());

	}
	
	@Test(	description = "Receive a high priority mail",
			groups = { "functional" })
	public void GetMail_01() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		// Send the message from AccountA to the ZWC user
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m f='!'>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");


		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		// Make sure the message appears in the list
		MailItem found = null;
		for (MailItem m : messages) {
			logger.info("Subject: looking for "+ subject +" found: "+ m.gSubject);
			if ( subject.equals(m.gSubject) ) {
				found = m;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the message is in the inbox");
		ZAssert.assertEquals(found.gPriority, MailItem.Priority.High, "Verify the message is high priority");

		
	}

	@Test(	description = "Receive a low priority mail",
			groups = { "functional" })
	public void GetMail_02() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m f='?'>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<e t='c' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		// Make sure the message appears in the list
		MailItem found = null;
		for (MailItem m : messages) {
			logger.info("Subject: looking for "+ subject +" found: "+ m.gSubject);
			if ( subject.equals(m.gSubject) ) {
				found = m;
				break;
			}
		}
		
		ZAssert.assertNotNull(found, "Verify the message is in the inbox");
		ZAssert.assertEquals(found.gPriority, MailItem.Priority.Low, "Verify the message is low priority");

	}

	@Test(	description = "Receive a normal priority mail",
			groups = { "functional" })
	public void GetMail_03() throws HarnessException {

		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m >" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<e t='c' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		// Make sure the message appears in the list
		MailItem found = null;
		for (MailItem m : messages) {
			logger.info("Subject: looking for "+ subject +" found: "+ m.gSubject);
			if ( subject.equals(m.gSubject) ) {
				found = m;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the message is in the inbox");
		ZAssert.assertEquals(found.gPriority, MailItem.Priority.Normal, "Verify the message is normal priority");
		
	}



}
