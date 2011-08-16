package com.zimbra.qa.selenium.projects.desktop.tests.preferences.mail.signatures;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem;
import com.zimbra.qa.selenium.framework.items.SignatureItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.FormMailNew;


public class ComposeHtmlMsgWithHtmlSignature extends AjaxCommonTest {
   String sigName = "signame" + ZimbraSeleniumProperties.getUniqueString();
   String sigBody = "Signature<strong>bold"+ ZimbraSeleniumProperties.getUniqueString() + "</strong>Signature";
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

   private void _createSignature(ZimbraAccount account) throws HarnessException {
      System.out.println(this.sigName);
      account.authenticate(SOAP_DESTINATION_HOST_TYPE.SERVER);
      account.soapSend(
            "<CreateSignatureRequest xmlns='urn:zimbraAccount'>"
            + "<signature name='" + this.sigName + "' >"
            + "<content type='text/html'>'" + this.contentHTML
            + "'</content>" + "</signature>"
            + "</CreateSignatureRequest>");

      // Refresh is needed by synching ZD to ZCS, then reload the page by logging out,
      // then relaunch ZD
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

      app.zPageLogin.zNavigateTo();
      super.startingPage.zNavigateTo();
   }

   /**
    * Test case : Create html signature through soap
    * Compose html message and add html signature 
    * Send mail to self and verify signature through soap. 
    * @throws HarnessException
    */
   @Test(description = " Compose Html Msg with html signature and Verify signature thropugh soap", groups = { "smoke" })
   public void ComposeHtmlMsgWithHtmlSignature_01() throws HarnessException {

      _createSignature(app.zGetActiveAccount());

      // Signature is created
      SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), this.sigName);
      ZAssert.assertEquals(signature.getName(), this.sigName,"verified Text Signature is created");

      // Create the message data to be sent
      MailItem mail = new MailItem();
      mail.dToRecipients.add(new RecipientItem(ZimbraAccount.AccountZWC()));
      mail.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
      mail.dBodyHtml = "body<strong>bold"+ ZimbraSeleniumProperties.getUniqueString()+"</strong>body";

      // Open the new mail form
      FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
      ZAssert.assertNotNull(mailform, "Verify the new form opened");

      // Fill out the form with the data
      mailform.zFill(mail);

      //click Signature drop down and add signature
      app.zPageMail.zToolbarPressPulldown(Button.B_SIGNATURE,Button.O_ADD_SIGNATURE,this.sigName);

      // Add signature     
      // app.zPageMail.zClick("css=td[id*='_title']td:contains('"+ this.sigName + "')");

      // Send the message
      mailform.zSubmit();
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

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

      ZAssert.assertEquals(received.dFromRecipient.dEmailAddress, app.zGetActiveAccount().EmailAddress,"Verify the from field is correct");
      ZAssert.assertEquals(received.dToRecipients.get(0).dEmailAddress,ZimbraAccount.AccountZWC().EmailAddress,"Verify the to field is correct");
      ZAssert.assertEquals(received.dSubject, mail.dSubject,"Verify the subject field is correct");
      ZAssert.assertStringContains(received.dBodyHtml, mail.dBodyHtml,"Verify the body content is correct");
      ZAssert.assertStringContains(received.dBodyHtml, this.sigBody,"Verify the signature is correct");

   }

}
