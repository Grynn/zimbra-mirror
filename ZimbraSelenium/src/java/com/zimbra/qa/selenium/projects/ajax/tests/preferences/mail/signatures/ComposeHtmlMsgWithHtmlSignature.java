package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.signatures;

import java.util.HashMap;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem;
import com.zimbra.qa.selenium.framework.items.SignatureItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;


public class ComposeHtmlMsgWithHtmlSignature extends AjaxCommonTest {
	String sigName = "signame" + ZimbraSeleniumProperties.getUniqueString();
	String sigBody = "signature<b>bold"+ ZimbraSeleniumProperties.getUniqueString() + "</b>signature";
	String contentHTML = XmlStringUtil.escapeXml("<html>" + "<head></head>"
			+ "<body>" + sigBody + "</body>" + "</html>");

	@SuppressWarnings("serial")
	public ComposeHtmlMsgWithHtmlSignature() {
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
		ZimbraAccount.AccountZWC().authenticate(SOAP_DESTINATION_HOST_TYPE.SERVER);
		ZimbraAccount.AccountZWC().soapSend(
				"<CreateSignatureRequest xmlns='urn:zimbraAccount'>"
				+ "<signature name='" + this.sigName + "' >"
				+ "<content type='text/html'>'" + this.contentHTML
				+ "'</content>" + "</signature>"
				+ "</CreateSignatureRequest>");

	}

	/**
	 * Test case : Create html signature through soap
	 * Compose html message and add html signature 
	 * Send mail to self and verify signature through soap. 
	 * @throws HarnessException
	 */
	@Test(description = " Compose Html Msg with html signature and Verify signature thropugh soap", groups = { "functional" })
	public void ComposeHtmlMsgWithHtmlSignature_01() throws HarnessException {

		// Signature is created
		SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), this.sigName);
		ZAssert.assertEquals(signature.getName(), this.sigName,"verified Text Signature is created");

		// Create the message data to be sent
		MailItem mail = new MailItem();
		mail.dToRecipients.add(new RecipientItem(ZimbraAccount.AccountZWC()));
		mail.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		//mail.dBodyHtml = "body<b>bold"+ ZimbraSeleniumProperties.getUniqueString()+"</b>body";
		mail.dBodyHtml = "bodybold"+ ZimbraSeleniumProperties.getUniqueString()+"body";

		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");

		// Fill out the form with the data
		mailform.zFill(mail);

		//click Signature drop down and add signature
		app.zPageMail.zToolbarPressPulldown(Button.B_OPTIONS,Button.O_ADD_SIGNATURE,this.sigName);

		// Send the message
		mailform.zSubmit();

		ZimbraAccount.AccountZWC().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+ "<query>in:inbox subject:(" + mail.dSubject + ")</query>" + "</SearchRequest>");

		String id = ZimbraAccount.AccountZWC().soapSelectValue("//mail:SearchResponse/mail:m", "id");

		ZimbraAccount.AccountZWC().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" + "<m id='" + id
				+ "' html='1'/>" + "</GetMsgRequest>");
		Element getMsgResponse = ZimbraAccount.AccountZWC().soapSelectNode("//mail:GetMsgResponse", 1);
		MailItem received = MailItem.importFromSOAP(getMsgResponse);

		// Verify TO, Subject,html Body,html signature
		logger.info(received.dBodyHtml.toLowerCase());
		logger.info(mail.dBodyHtml);

		ZAssert.assertEquals(received.dFromRecipient.dEmailAddress, app.zGetActiveAccount().EmailAddress,"Verify the from field is correct");
		ZAssert.assertEquals(received.dToRecipients.get(0).dEmailAddress,ZimbraAccount.AccountZWC().EmailAddress,"Verify the to field is correct");
		ZAssert.assertEquals(received.dSubject, mail.dSubject,"Verify the subject field is correct");
		ZAssert.assertStringContains(received.dBodyHtml.toLowerCase(), mail.dBodyHtml,"Verify the body content is correct");
		ZAssert.assertStringContains(received.dBodyHtml.toLowerCase(), this.sigBody,"Verify the signature is correct");

	}

}
