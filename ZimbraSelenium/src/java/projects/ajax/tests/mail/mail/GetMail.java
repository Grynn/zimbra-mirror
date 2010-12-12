package projects.ajax.tests.mail.mail;

import java.util.List;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.DisplayMail;
import projects.ajax.ui.DisplayMail.Field;
import framework.items.MailItem;
import framework.items.RecipientItem;
import framework.ui.Action;
import framework.ui.Button;
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
		String subject = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		// Send the message from AccountA to the ZWC user
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");


		/*
		 * 
		// Get all the SOAP data for later verification
		MailItem mail = new MailItem();
		mail.importFromSOAP(app.getActiveAccount(), "subject:("+ subject +")");
		
		 */


		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the conversation list exists");

		// Make sure the message appears in the list
		boolean found = false;
		for (MailItem m : messages) {
			logger.info("Subject: looking for "+ subject +" found: "+ m.gSubject);
			if ( m.gSubject.equals(subject) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertTrue(found, "Verify the message is in the inbox");

		
	}

	@Test(	description = "Receive a text mail - verify mail contents",
			groups = { "smoke" })
	public void GetMail_02() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<e t='c' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Get all the SOAP data for later verification
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);

		// Verify the To, From, Subject, Body
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.Subject), mail.dSubject, "Verify the subject matches");
		ZAssert.assertNotNull(	actual.zGetMailProperty(Field.ReceivedDate), "Verify the date is displayed");
		ZAssert.assertNotNull(	actual.zGetMailProperty(Field.ReceivedTime), "Verify the time is displayed");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.From), ZimbraAccount.AccountA().EmailAddress, "Verify the From matches");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.Cc), ZimbraAccount.AccountB().EmailAddress, "Verify the From matches");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.To), app.zGetActiveAccount().EmailAddress, "Verify the To matches");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.Body), mail.dBodyText, "Verify the body matches");

		
	}

	@Test(	description = "Receive an html mail - verify mail contents",
			groups = { "smoke" })
	public void GetMail_03() throws HarnessException {

		
		// Create the message data to be sent
		MailItem mail = new MailItem();
		mail.dToRecipients.add(new RecipientItem(app.zGetActiveAccount().EmailAddress));
		mail.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		mail.gBodyText = "body" + ZimbraSeleniumProperties.getUniqueString();
		String bodyHtml = "body" + ZimbraSeleniumProperties.getUniqueString();
		mail.setBodyHtml("<html><body><bold>" + bodyHtml +"</bold></body></html>");

		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<e t='c' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
							"<su>"+ mail.dSubject +"</su>" +
							"<mp ct='multipart/alternative'>" +
								"<mp ct='text/plain'>" +
									"<content>"+ mail.gBodyText +"</content>" +
								"</mp>" +
								"<mp ct='text/html'>" +
									"<content>"+ mail.getBodyHtml() +"</content>" +
								"</mp>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);

		// Verify the To, From, Subject, Body
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.Subject), mail.dSubject, "Verify the subject matches");
		
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.Subject), mail.dSubject, "Verify the subject matches");
		ZAssert.assertNotNull(	actual.zGetMailProperty(Field.ReceivedDate), "Verify the date is displayed");
		ZAssert.assertNotNull(	actual.zGetMailProperty(Field.ReceivedTime), "Verify the time is displayed");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.From), ZimbraAccount.AccountA().EmailAddress, "Verify the From matches");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.Cc), ZimbraAccount.AccountB().EmailAddress, "Verify the From matches");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.To), app.zGetActiveAccount().EmailAddress, "Verify the To matches");
		ZAssert.assertStringContains(actual.zGetMailProperty(Field.Body), bodyHtml, "Verify the body matches");
		
	}


}
