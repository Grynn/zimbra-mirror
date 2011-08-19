package com.zimbra.qa.selenium.projects.desktop.tests.preferences.mail.signatures;

import java.util.HashMap;
import org.testng.annotations.Test;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.SignatureItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.FormMailNew;

public class ReplyAllMsgWithTextSignature extends AjaxCommonTest {
   String sigName = "signame" + ZimbraSeleniumProperties.getUniqueString();
   String sigBody = "Signature" + ZimbraSeleniumProperties.getUniqueString();

   @SuppressWarnings("serial")
   public ReplyAllMsgWithTextSignature() {
      super.startingPage = app.zPageMail;
      super.startingAccountPreferences = new HashMap<String, String>() {
         {
            put("zimbraPrefComposeFormat", "text");

         }
      };
   }

   public void _createSignature(ZimbraAccount account) throws HarnessException {
      account.authenticate(SOAP_DESTINATION_HOST_TYPE.SERVER);
      account.soapSend(
            "<CreateSignatureRequest xmlns='urn:zimbraAccount'>"
            + "<signature name='" + this.sigName + "' >"
            + "<content type='text/plain'>" + this.sigBody
            + "</content>" + "</signature>"
            + "</CreateSignatureRequest>");

      // Refresh is needed by synching ZD to ZCS, then reload the page by logging out,
      // then relaunch ZD
      GeneralUtility.syncDesktopToZcsWithSoap(account);
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

      app.zPageLogin.zNavigateTo();
      super.startingPage.zNavigateTo();
   }

   /**
    * Test case : Reply All Msg with text signature and Verify signature through soap
    * Create signature through soap 
    * Send message with text signature through soap
    * Reply All same message.
    * Verify text signature in Replied msg through soap
    * @throws HarnessException
    */
   @Test(description = " Reply AllMsg with text signature and Verify signature through soap", groups = { "functional" })
   public void ReplyAllMsgWithTextSignature_01() throws HarnessException {
      _createSignature(app.zGetActiveAccount());

      // Signature is created
      SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), this.sigName);
      ZAssert.assertEquals(signature.getName(), this.sigName,"verified Text Signature is created");

      String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();


      // Send a message to the account(self)
      app.zGetActiveAccount().soapSend(
            "<SendMsgRequest xmlns='urn:zimbraMail'>" +
            "<m>" +
            "<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
            "<su>"+ subject +"</su>" +
            "<mp ct='text/plain'>" +
            "<content>content"+ ZimbraSeleniumProperties.getUniqueString() + "\n\n"+signature.dBodyText+"\n</content>" +
            "</mp>" +
            "</m>" +
      "</SendMsgRequest>");

      SleepUtil.sleepSmall();

      // Get the mail item for the new message
      MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(),"in:inbox subject:(" + subject + ")");

      // Click Get Mail button
      app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

      // Select the item
      app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);

      // Forward the item
      FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLYALL);
      ZAssert.assertNotNull(mailform, "Verify the new form opened");

      SleepUtil.sleepSmall();

      // Send the message
      mailform.zSubmit();
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

      app.zGetActiveAccount().soapSend(
            "<SearchRequest xmlns='urn:zimbraMail' types='message'>"
            + "<query>in:inbox subject:(" + mail.dSubject + ")</query>" + 
      "</SearchRequest>");

      String id = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse/mail:m", "id");
      app.zGetActiveAccount().soapSend(
            "<GetMsgRequest xmlns='urn:zimbraMail'>" +
            "<m id='"+ id +"' />" +
      "</GetMsgRequest>");
      Element getMsgResponse = app.zGetActiveAccount().soapSelectNode("//mail:GetMsgResponse", 1);
      MailItem received = MailItem.importFromSOAP(getMsgResponse);

      logger.debug("===========received is: " + received);
      logger.debug("===========app is: " + app);

      //Verify TO, Subject, Text Body,Text Signature  for replied msg
      ZAssert.assertStringContains(received.dSubject, "Re", "Verify the subject field contains the 'Fwd' prefix");
      ZAssert.assertEquals(received.dFromRecipient.dEmailAddress, app.zGetActiveAccount().EmailAddress,"Verify the from field is correct");
      ZAssert.assertEquals(received.dToRecipients.get(0).dEmailAddress,app.zGetActiveAccount().EmailAddress,"Verify the to field is correct");
      ZAssert.assertStringContains(received.dBodyText, mail.dBodyText,"Verify the body content is correct");
      ZAssert.assertStringContains(received.dBodyText, this.sigBody,"Verify the signature is correct");

   }
}
