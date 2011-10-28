package com.zimbra.qa.selenium.projects.ajax.tests.mail.mail;

import java.io.File;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.Field;


public class ViewMail extends PrefGroupMailByMessageTest {

	boolean injected = false;
	final String mimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email00";
	
	public ViewMail() throws HarnessException {
		logger.info("New "+ ViewMail.class.getCanonicalName());
		
		
		

		
		


	}
	
	
	@Bugs(	ids = "57047" )
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

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.OnBehalfOf), from, "Verify the On-Behalf-Of matches the 'From:' header");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.From), sender, "Verify the From matches the 'Sender:' header");
		

		
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
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.ReplyTo), replyto, "Verify the Reply-To matches the 'Reply-To:' header");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.From), from, "Verify the From matches the 'From:' header");
		

		
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
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.ResentFrom), resentfrom, "Verify the Resent-From matches the 'Resent-From:' header");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.From), from, "Verify the From matches the 'From:' header");
		

		
	}

	@Bugs(	ids = "64444")
	@Test(	description = "Receive a mail with only audio/vav content",
			groups = { "functional" })
	public void ViewMail_04() throws HarnessException {
		
		final String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Bugs/Bug64444";
		final String subject = "subject13150123168433";
		final String from = "from13160123168433@testdomain.com";
		final String to = "to3163210168433@testdomain.com";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));


		
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:(" + subject +")");
		ZAssert.assertNotNull(mail, "Verify message is received");
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.From), from, "Verify the From matches the 'From:' header");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.To), to, "Verify the From matches the 'From:' header");
		

		
	}

	@Bugs(	ids = "66565")
	@Test(	description = "Receive a mail formatting in the subject",
			groups = { "functional" })
	public void ViewMail_05() throws HarnessException {
		
		final String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Bugs/Bug66565";
		final String subject = "subject13197565510464";
		final String subjectText = "<u><i> subject13197565510464 </i></u>";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));


		
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:(" + subject +")");
		ZAssert.assertNotNull(mail, "Verify message is received");
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the Subject
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.Subject), subjectText, "Verify the Subject matches");
		
		// Verify the <u> and <i> elements are not in the DOM (only in text)
		//
		// i.e. this woud be wrong:
		//
		// <td width="100%" class="subject">
		//  <u>
		//   <i>
		//    subject13197565510464 
		//   </i>
		//  </u>
		// </td>
		//
		// Expected:
		// <td width="100%" class="subject">
		//  &lt;u&gt;&lt;i&gt; subject13197565510464 &lt;/i&gt;&lt;/u&gt;
		// </td>
		//
		String locator = "css=div[id='zv__TV-main__MSG'] tr[id$='_hdrTableTopRow'] td[class~='SubjectCol']";
		ZAssert.assertFalse( actual.sIsElementPresent(locator + " u"), "Verify the <u> element is not in the DOM");
		ZAssert.assertFalse( actual.sIsElementPresent(locator + " i"), "Verify the <i> element is not in the DOM");



		
	}


}
