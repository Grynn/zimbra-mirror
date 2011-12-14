package com.zimbra.qa.selenium.projects.desktop.tests.preferences.mail.signatures;

import java.util.HashMap;

import org.testng.annotations.Test;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.SignatureItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.FormMailNew.Field;

public class ForwardMsgWithHtmlSignature extends AjaxCommonTest {
   String sigName = "signame" + ZimbraSeleniumProperties.getUniqueString();
   String sigCore = "bold" + ZimbraSeleniumProperties.getUniqueString();
   String sigBody = "Signature<strong>" + sigCore + "</strong>Signature";
   String contentHTMLSig = XmlStringUtil.escapeXml("<html>" + "<head></head>"
         + "<body>" + sigBody + "</body>" + "</html>");

   @SuppressWarnings("serial")
   public ForwardMsgWithHtmlSignature() {
      super.startingPage = app.zPageMail;
      super.startingAccountPreferences = new HashMap<String, String>() {
         {
            put("zimbraPrefComposeFormat", "html");
         }
      };
   }

   public void _createSignature(ZimbraAccount account) throws HarnessException {
      account.authenticate(
            SOAP_DESTINATION_HOST_TYPE.SERVER);
      account.soapSend(
            "<CreateSignatureRequest xmlns='urn:zimbraAccount'>"
            + "<signature name='" + this.sigName + "' >"
            + "<content type='text/html'>'" + this.contentHTMLSig
            + "'</content>" + "</signature>"
            + "</CreateSignatureRequest>");

      // Refresh is needed by synching ZD to ZCS, then reload the page by logging out,
      // then relaunch ZD
      GeneralUtility.syncDesktopToZcsWithSoap(account);
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

      app.zPageLogin.zNavigateTo();
      super.startingPage.zNavigateTo();
   }

   /**
    * Test case : Forward Msg with html signature and Verify signature through soap
    * Create signature through soap 
    * Send message with html signature through soap
    * Fwd same message to another account say (accountB())
    * Verify html signature in forwarded msg through soap
    * @throws HarnessException
    */
   @Test(description = "Forward Msg with html signature and Verify html signature through soap", groups = { "functional" })
   public void ForwardMsgWithHtmlSignature_01() throws HarnessException {
      _createSignature(app.zGetActiveAccount());

      SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), this.sigName);
      logger.info(signature.dBodyHtmlText);
      ZAssert.assertEquals(signature.getName(), this.sigName,
            "verified Text Signature is created");

      String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
      String bodyText = "text" + ZimbraSeleniumProperties.getUniqueString();
      String bodyHTML = "text <strong>bold"+ ZimbraSeleniumProperties.getUniqueString() +"</strong> text";
      String contentHTML = XmlStringUtil.escapeXml("<html>" + "<head></head>"
            + "<body>" + bodyHTML + "<br></br>" + "</body>" + "</html>");
      String signatureContent = XmlStringUtil.escapeXml("<html>"
            + "<head></head>" + "<body>" + signature.dBodyHtmlText
            + "</body>" + "</html>");

      // Send a message to the account with html signature
      app.zGetActiveAccount().soapSend(
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
      MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(),"in:inbox subject:(" + subject + ")");

      // Click Get Mail button
      app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

      // Select the item
      app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);

      // Forward the item
      FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_FORWARD);
      ZAssert.assertNotNull(mailform, "Verify the new form opened");

      // Fill out the form with the data
      mailform.zFillField(Field.To, ZimbraAccount.AccountB().EmailAddress, sigCore);

      // Send the message
      mailform.zSubmit();
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

      ZimbraAccount.AccountB().soapSend(
            "<SearchRequest xmlns='urn:zimbraMail' types='message'>"
            + "<query>in:inbox subject:(" + mail.dSubject + ")</query>" + "</SearchRequest>");

      String id = ZimbraAccount.AccountB().soapSelectValue("//mail:SearchResponse/mail:m", "id");

      ZimbraAccount.AccountB().soapSend(
            "<GetMsgRequest xmlns='urn:zimbraMail'>" + "<m id='" + id
            + "' html='1'/>" + "</GetMsgRequest>");
      Element getMsgResponse = ZimbraAccount.AccountB().soapSelectNode("//mail:GetMsgResponse", 1);

      MailItem received = MailItem.importFromSOAP(getMsgResponse);

      logger.debug("===========received is: " + received);
      logger.debug("===========app is: " + app);

      //Verify TO, Fwd'ed Subject, HtmlBody,HtmlSignature
      ZAssert.assertStringContains(received.dSubject, "Fwd", "Verify the subject field contains the 'Fwd' prefix");
      ZAssert.assertEquals(received.dFromRecipient.dEmailAddress, app.zGetActiveAccount().EmailAddress,"Verify the from field is correct");
      ZAssert.assertEquals(received.dToRecipients.get(0).dEmailAddress,ZimbraAccount.AccountB().EmailAddress,"Verify the to field is correct");
      ZAssert.assertStringContains(received.dBodyHtml, bodyHTML,"Verify html body content is correct");
      ZAssert.assertStringContains(received.dBodyHtml, this.sigBody,"Verify html signature is correct");

   }
}

