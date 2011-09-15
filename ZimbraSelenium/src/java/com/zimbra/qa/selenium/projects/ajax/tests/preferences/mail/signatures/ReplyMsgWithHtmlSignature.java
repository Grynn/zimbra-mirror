package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.signatures;

import java.util.HashMap;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.SignatureItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;

import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;


public class ReplyMsgWithHtmlSignature extends AjaxCommonTest {
	String sigName = "signame" + ZimbraSeleniumProperties.getUniqueString();
	String sigBody = "signature<b>bold"+ ZimbraSeleniumProperties.getUniqueString() + "</b>signature";
	String contentHTMLSig = XmlStringUtil.escapeXml("<html>" + "<head></head>"
			+ "<body>" + sigBody + "</body>" + "</html>");

	@SuppressWarnings("serial")
	public ReplyMsgWithHtmlSignature() {
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefComposeFormat", "html");
			}
		};
	}

	@BeforeClass(groups = { "always" })
	public void CreateSignature() throws HarnessException {
		System.out.println(this.sigName);
		ZimbraAccount.AccountZWC().authenticate(
				SOAP_DESTINATION_HOST_TYPE.SERVER);
		ZimbraAccount.AccountZWC().soapSend(
				"<CreateSignatureRequest xmlns='urn:zimbraAccount'>"
				+ "<signature name='" + this.sigName + "' >"
				+ "<content type='text/html'>'" + this.contentHTMLSig
				+ "'</content>" + "</signature>"
				+ "</CreateSignatureRequest>");


	}

	/**
	 * Test case : Reply Msg with html signature and Verify signature through soap
	 * Create signature through soap 
	 * Send message with html signature through soap
	 * Reply same message.
	 * Verify html signature in Replied msg through soap
	 * @throws HarnessException
	 */
	@Test(description = " Reply Msg with html signature and Verify signature through soap", groups = { "functional" })
	public void ReplyMsgWithHtmlSignature_01() throws HarnessException {
		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(),SystemFolder.Inbox);
		//Verify Signature
		SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), this.sigName);
		ZAssert.assertEquals(signature.getName(), this.sigName,"verified Text Signature is created");

		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String bodyText = "text" + ZimbraSeleniumProperties.getUniqueString();
		String bodyHTML = "text <b>bold"
			+ ZimbraSeleniumProperties.getUniqueString() + "</b> text";
		String contentHTML = XmlStringUtil.escapeXml("<html>" + "<head></head>"
				+ "<body>" + bodyHTML + "<br></br>" + "</body>" + "</html>");
		String signatureContent = XmlStringUtil.escapeXml("<html>"
				+ "<head></head>" + "<body>" + signature.dBodyHtmlText
				+ "</body>" + "</html>");



		// Send a message to the account
		ZimbraAccount.AccountZWC().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
				"<m>" +
				"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
				"<su>"+ subject +"</su>" +
				"<mp ct='multipart/alternative'>" +
				"<mp ct='text/plain'>" +
				"<content>"+ bodyText+"</content>" +
				"</mp>" +
				"<mp ct='text/html'>" +
				"<content>"+contentHTML+signatureContent+"\n</content>" +
				"</mp>" +
				"</mp>" +
				"</m>" +
		"</SendMsgRequest>");


		// Get the mail item for the new message
		MailItem mail = MailItem.importFromSOAP(ZimbraAccount.AccountZWC(),"in:inbox subject:(" + subject + ")");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inboxFolder);


		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);

		// Forward the item
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLY);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");

		SleepUtil.sleepSmall();

		// Send the message
		mailform.zSubmit();

		ZimbraAccount.AccountZWC().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+ "<query>in:inbox subject:(" +mail.dSubject+ ")</query>" + "</SearchRequest>");

		String id = ZimbraAccount.AccountZWC().soapSelectValue("//mail:SearchResponse/mail:m", "id");

		ZimbraAccount.AccountZWC().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" + "<m id='" + id
				+ "' html='1'/>" + "</GetMsgRequest>");
		Element getMsgResponse = ZimbraAccount.AccountZWC().soapSelectNode("//mail:GetMsgResponse", 1);
		MailItem received = MailItem.importFromSOAP(getMsgResponse);

		logger.debug("===========received is: " + received);
		logger.debug("===========app is: " + app);

		//Verify TO, Reply'ed Subject, HtmlBody,HtmlSignature
		ZAssert.assertStringContains(received.dSubject, "Re", "Verify the subject field contains the 'Re' prefix");
		ZAssert.assertEquals(received.dFromRecipient.dEmailAddress, app.zGetActiveAccount().EmailAddress,"Verify the from field is correct");
		ZAssert.assertEquals(received.dToRecipients.get(0).dEmailAddress,ZimbraAccount.AccountZWC().EmailAddress,"Verify the to field is correct");
		ZAssert.assertStringContains(received.dBodyHtml.toLowerCase(), bodyHTML,"Verify the body content is correct");
		ZAssert.assertStringContains(received.dBodyHtml.toLowerCase(), this.sigBody,"Verify the signature is correct");

	}
}
