package com.zimbra.qa.selenium.projects.desktop.tests.mail.mail;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DisplayMail.Field;


public class GetMail extends AjaxCommonTest {

	int pollIntervalSeconds = 60;
	
	@SuppressWarnings("serial")
	public GetMail() {
		logger.info("New "+ GetMail.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
				    put("zimbraPrefGroupMailBy", "message");
				    put("zimbraPrefMessageViewHtmlPreferred", "TRUE");
				    put("zimbraPrefMailPollingInterval", "" + pollIntervalSeconds);
				}};


	}
	
	@Test(	description = "Receive a mail",
			groups = { "smoke" })
	public void GetMail_01() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
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

		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");


		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		// Make sure the message appears in the list
		MailItem found = null;
		for (MailItem m : messages) {
			logger.info("Subject: looking for "+ subject +" found: "+ m.gSubject);
			if ( mail.dSubject.equals(m.gSubject) ) {
				found = m;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the message is in the inbox");

		
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
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.Cc), ZimbraAccount.AccountB().EmailAddress, "Verify the Cc matches");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.To), app.zGetActiveAccount().EmailAddress, "Verify the To matches");
		
		// The body could contain HTML, even though it is only displaying text (e.g. <br> may be present)
		// do a contains, rather than equals.
		ZAssert.assertStringContains(	actual.zGetMailProperty(Field.Body), mail.dBodyText, "Verify the body matches");

		
	}

	@Test(	description = "Receive an html mail - verify mail contents",
			groups = { "smoke" })
	public void GetMail_03() throws HarnessException {

		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String bodyText = "text" + ZimbraSeleniumProperties.getUniqueString();
		String bodyHTML = "text <strong>bold"+ ZimbraSeleniumProperties.getUniqueString() +"</strong> text";
		String contentHTML = XmlStringUtil.escapeXml(
			"<html>" +
				"<head></head>" +
				"<body>"+ bodyHTML +"</body>" +
			"</html>");
		
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<e t='c' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='multipart/alternative'>" +
								"<mp ct='text/plain'>" +
									"<content>" + bodyText +"</content>" +
								"</mp>" +
								"<mp ct='text/html'>" +
									"<content>"+ contentHTML +"</content>" +
								"</mp>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");
		
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
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.Cc), ZimbraAccount.AccountB().EmailAddress, "Verify the Cc matches");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.To), app.zGetActiveAccount().EmailAddress, "Verify the To matches");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.Body), bodyHTML, "Verify the body matches");
		
	}


	@Test(	description = "Click 'Get Mail' to receive any new messages",
			groups = { "functional" })
	public void GetMail_04() throws HarnessException {

		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");
		
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get the message list
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the list contains messages");

		MailItem found = null;
		for (MailItem m : messages) {
			if ( mail.dSubject.equals(m.gSubject) ) {
				found = m;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the list contains the new message");
		
	}

	@Test(	description = "Verify new messages are polled based on the preference setting",
			groups = { "functional" })
	public void GetMail_05() throws HarnessException {

		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");

		// Wait for the timeout to expire
		logger.info("waiting for the message to arrive");
		SleepUtil.sleep(1000L * (this.pollIntervalSeconds + 15));

		// Get the message list
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the list contains messages");

		MailItem found = null;
		for (MailItem m : messages) {
			if ( mail.dSubject.equals(m.gSubject) ) {
				found = m;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the list contains the new message");
		
	}


}
