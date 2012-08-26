package com.zimbra.qa.selenium.projects.ajax.tests.mail.newwindow.mail;

import java.io.File;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.LmtpInject;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.SeparateWindowDisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.Field;


public class GetMail extends PrefGroupMailByMessageTest {
	protected static Logger logger = LogManager.getLogger(GetMail.class);

	boolean injected = false;
	final String mimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email00";

	public GetMail() throws HarnessException {
		logger.info("New "+ GetMail.class.getCanonicalName());
		


	}
	
	
	@Test(	description = "Open message in separate window",
			groups = { "smoke" })
	public void GetMail_01() throws HarnessException {
		
		final String subject = "subject12996136534962";

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
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		SeparateWindowDisplayMail window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (SeparateWindowDisplayMail)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}
		
		
	}

	@Test(	description = "Open message in separate window - verify mail contents",
			groups = { "smoke" })
	public void GetMail_02() throws HarnessException {
		
		final String subject = "subject1291234112962";
		final String content = "content2291234112962";
		
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<e t='c' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ content +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		SeparateWindowDisplayMail window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (SeparateWindowDisplayMail)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			ZAssert.assertEquals(	window.zGetMailProperty(Field.Subject), subject, "Verify the subject matches");
			ZAssert.assertNotNull(	window.zGetMailProperty(Field.ReceivedDate), "Verify the date is displayed");
			ZAssert.assertNotNull(	window.zGetMailProperty(Field.ReceivedTime), "Verify the time is displayed");
			ZAssert.assertEquals(	window.zGetMailProperty(Field.From), ZimbraAccount.AccountA().EmailAddress, "Verify the From matches");
			ZAssert.assertEquals(	window.zGetMailProperty(Field.Cc), ZimbraAccount.AccountB().EmailAddress, "Verify the Cc matches");
			ZAssert.assertEquals(	window.zGetMailProperty(Field.To), app.zGetActiveAccount().EmailAddress, "Verify the To matches");
			
			// The body could contain HTML, even though it is only displaying text (e.g. <br> may be present)
			// do a contains, rather than equals.
			ZAssert.assertStringContains(	window.zGetMailProperty(Field.Body), content, "Verify the body matches");

		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}
		
		
	}


	@Test(	description = "Open html message in separate window - verify mail contents",
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
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);

		SeparateWindowDisplayMail window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (SeparateWindowDisplayMail)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			// Verify the To, From, Subject, Body		
			ZAssert.assertEquals(	window.zGetMailProperty(Field.Subject), mail.dSubject, "Verify the subject matches");
			ZAssert.assertNotNull(	window.zGetMailProperty(Field.ReceivedDate), "Verify the date is displayed");
			ZAssert.assertNotNull(	window.zGetMailProperty(Field.ReceivedTime), "Verify the time is displayed");
			ZAssert.assertEquals(	window.zGetMailProperty(Field.From), ZimbraAccount.AccountA().EmailAddress, "Verify the From matches");
			ZAssert.assertEquals(	window.zGetMailProperty(Field.Cc), ZimbraAccount.AccountB().EmailAddress, "Verify the Cc matches");
			ZAssert.assertEquals(	window.zGetMailProperty(Field.To), app.zGetActiveAccount().EmailAddress, "Verify the To matches");
			ZAssert.assertEquals(	window.zGetMailProperty(Field.Body), bodyHTML, "Verify the body matches");
			

		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}

		
		
	}


	@Test(	description = "Receive a mail with Sender: specified",
			groups = { "functional" })
	public void ViewMail_01() throws HarnessException {
		
		final String subject = "subject12996131112962";
		final String from = "from12996131112962@example.com";
		final String sender = "sender12996131112962@example.com";

		if ( !injected ) {
			LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFolder));
			injected = true;
		}

		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertNotNull(mail, "Verify message is received");
		ZAssert.assertEquals(from, mail.dFromRecipient.dEmailAddress, "Verify the from matches");
		ZAssert.assertEquals(sender, mail.dSenderRecipient.dEmailAddress, "Verify the sender matches");

		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		
		SeparateWindowDisplayMail window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (SeparateWindowDisplayMail)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			// Verify the To, From, Subject, Body
			//ZAssert.assertEquals(	window.zGetMailProperty(Field.OnBehalfOf), from, "Verify the On-Behalf-Of matches the 'From:' header");
			/* TODO: ... debugging to be removed */
			String fromLocator = "css=div[id='zv__TV-main__MSG'] td[id$='_from'] span[id$='_com_zimbra_email'] span:contains(from)";
			ZAssert.assertEquals(app.zPageMail.sGetText(fromLocator).trim(), from, "Verify the On-Behalf-Of matches the 'From:' header");

			//ZAssert.assertEquals(	window.zGetMailProperty(Field.From), sender, "Verify the From matches the 'Sender:' header");
			/* TODO: ... debugging to be removed */
			String senderLocator = "css=div[id='zv__TV-main__MSG'] td[id$='_from'] span[id$='_com_zimbra_email'] span:contains(sender)";
			ZAssert.assertEquals(app.zPageMail.sGetText(senderLocator).trim(), sender, "Verify the From matches the 'Sender:' header");

		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}

		

		
	}

	@Test(	description = "Receive a mail with Reply-To: specified",
			groups = { "functional" })
	public void ViewMail_02() throws HarnessException {
		
		final String subject = "subject13016959916873";
		final String from = "from13016959916873@example.com";
		final String replyto = "replyto13016959916873@example.com";

		if ( !injected ) {
			LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFolder));
			injected = true;
		}


		
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertNotNull(mail, "Verify message is received");
		ZAssert.assertEquals(from, mail.dFromRecipient.dEmailAddress, "Verify the from matches");
		ZAssert.assertEquals(replyto, mail.dReplyToRecipient.dEmailAddress, "Verify the Reply-To matches");
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		
		SeparateWindowDisplayMail window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (SeparateWindowDisplayMail)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			// Verify the To, From, Subject, Body
			//ZAssert.assertEquals(	window.zGetMailProperty(Field.ReplyTo), replyto, "Verify the Reply-To matches the 'Reply-To:' header");
			/* TODO: ... debugging to be removed */
			String replytoLocator = "css=div[id='zv__TV-main__MSG'] tr[id$='_reply to'] td.LabelColValue span[id$='_com_zimbra_email'] span:contains(replyto)";
			ZAssert.assertEquals(app.zPageMail.sGetText(replytoLocator).trim(), replyto, "Verify the Reply-To matches the 'Reply-To:' header");

			//ZAssert.assertEquals(	window.zGetMailProperty(Field.From), from, "Verify the From matches the 'From:' header");
			/* TODO: ... debugging to be removed */
			String fromLocator = "css=div[id='zv__TV-main__MSG'] td[id$='_from'] span[id$='_com_zimbra_email'] span:contains(from)";
			ZAssert.assertEquals(app.zPageMail.sGetText(fromLocator).trim(), from, "Verify the From matches the 'From:' header");

		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}

		
	}

	@Bugs(	ids = "61575")
	@Test(	description = "Receive a mail with Resent-From: specified",
			groups = { "functional" })
	public void ViewMail_03() throws HarnessException {
		
		final String subject = "subject13147509564213";
		final String from = "from13011239916873@example.com";
		final String resentfrom = "resentfrom13016943216873@example.com";

		if ( !injected ) {
			LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFolder));
			injected = true;
		}


		
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertNotNull(mail, "Verify message is received");
		ZAssert.assertEquals(resentfrom, mail.dRedirectedFromRecipient.dEmailAddress, "Verify the Resent-From matches");
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		
		SeparateWindowDisplayMail window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (SeparateWindowDisplayMail)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			// Verify the To, From, Subject, Body
			//ZAssert.assertEquals(	window.zGetMailProperty(Field.ResentFrom), resentfrom, "Verify the Resent-From matches the 'Resent-From:' header");
			/* TODO: ... debugging to be removed */
			String resentfromLocator = "css=div[id='zv__TV-main__MSG'] td[id$='_from'] span[id$='_com_zimbra_email'] span:contains(resentfrom)";
			ZAssert.assertEquals(app.zPageMail.sGetText(resentfromLocator).trim(), resentfrom, "Verify the From matches the 'From:' header");

			//ZAssert.assertEquals(	window.zGetMailProperty(Field.From), from, "Verify the From matches the 'From:' header");
			/* TODO: ... debugging to be removed */
			String fromLocator = "css=div[id='zv__TV-main__MSG'] td[id$='_from'] span[id$='_com_zimbra_email'] span:contains(from)";
			ZAssert.assertEquals(app.zPageMail.sGetText(fromLocator).trim(), from, "Verify the From matches the 'From:' header");

		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}


		
	}




}
