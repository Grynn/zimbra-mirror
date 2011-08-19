package com.zimbra.qa.selenium.projects.desktop.tests.preferences.mail.signatures;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.SignatureItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.TreePreferences.TreeItem;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.signature.FormSignatureNew;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.signature.PageSignature;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.signature.FormSignatureNew.Field;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.signature.PageSignature.Locators;



public class EditHtmlSignature extends AjaxCommonTest {
   String sigName = "signame" + ZimbraSeleniumProperties.getUniqueString();
   String bodyHTML = "text<strong>bold"+ ZimbraSeleniumProperties.getUniqueString() + "</strong>text";
   String contentHTML = XmlStringUtil.escapeXml("<html>" + "<head></head>"
         + "<body>" + bodyHTML + "</body>" + "</html>");

   public EditHtmlSignature() throws HarnessException {
      super.startingPage = app.zPagePreferences;
      super.startingAccountPreferences = null;

   }

   private void _createHtmlSignature(ZimbraAccount account) throws HarnessException {
      account.authenticate(SOAP_DESTINATION_HOST_TYPE.SERVER);
      account.soapSend(
            "<CreateSignatureRequest xmlns='urn:zimbraAccount'>"
            + "<signature name='" + this.sigName + "' >"
            + "<content type='text/html'>'" + this.contentHTML
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
    * Test case : Create html signature through soap then Edit and verify
    * edited html signature through soap
    * 
    * @throws HarnessException
    */

   @Test(description = "Edit signature through GUI and verify through soap", groups = { "smoke" })
   public void EditHtmlSignature_01() throws HarnessException {

      _createHtmlSignature(app.zGetActiveAccount());

      String sigEditName = "editsigname"+ ZimbraSeleniumProperties.getUniqueString();
      String editbodyHTML = "edittextbold"+ ZimbraSeleniumProperties.getUniqueString() + "text";

      // HTML Signature is created
      SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), this.sigName);
      ZAssert.assertEquals(signature.getName(), this.sigName,"verified Html Signature name ");

      // Click on Mail/signature
      app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK,TreeItem.MailSignatures);
      SleepUtil.sleepSmall();

      PageSignature pagesig = new PageSignature(app);

      //Select created signature signature 
      pagesig.zClick(Locators.zSignatureListView);
      app.zPageSignature.zClick("//td[contains(text(),'"+signature.getName()+"')]");

      //Verify Body contents
      String signaturebodytext = pagesig.zGetHtmlSignatureBody();
      ZAssert.assertStringContains(signaturebodytext, this.bodyHTML,"Verify the html signature body");

      FormSignatureNew signew = new FormSignatureNew(app);

      // Edit signame and sigbody
      signew.zFillField(Field.SignatureName, sigEditName);
      signew.zFillField(Field.SignatureHtmlBody, editbodyHTML);
      signew.zSubmit();
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

      SignatureItem editsignature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), sigEditName);

      //Verify signature name and body contents
      ZAssert.assertEquals(editsignature.getName(),sigEditName,"Verify Edited signature name");
      ZAssert.assertEquals(editsignature.dBodyHtmlText,editbodyHTML,"Verify Edited Html signature body");
      ZAssert.assertStringDoesNotContain(editsignature.getName(), this.sigName, "Verify after edit 1st signature  does not present");

   }

}
