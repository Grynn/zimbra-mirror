package com.zimbra.qa.selenium.projects.desktop.tests.preferences.mail.signatures;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.SignatureItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.TreePreferences.TreeItem;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.signature.FormSignatureNew;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.signature.PageSignature.Locators;

public class DeleteTextSignature extends AjaxCommonTest {
   String sigName = "signame" + ZimbraSeleniumProperties.getUniqueString();
   String sigBody = "sigbody" + ZimbraSeleniumProperties.getUniqueString();

   public DeleteTextSignature() throws HarnessException{
      super.startingPage = app.zPagePreferences;
      super.startingAccountPreferences = null;
   }

   private void _createSignature(ZimbraAccount account) throws HarnessException {
      System.out.println(this.sigName);
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
    * Test case :Create signature through soap then delete and verify signature through soap
    * @Steps:
    * Create signature through soap
    * Delete signature using delete button.
    * Verify signature doesn't exist from soap
    * @throws HarnessException
    */
   @Test(description = " Delete Text signature using Delete button and verify  through soap ", groups = { "smoke" })
   public void DeleteTextSignatures() throws HarnessException {

      _createSignature(app.zGetActiveAccount());

      //Click on Mail/signature
      app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK,TreeItem.MailSignatures);

      //Signature is created
      SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), this.sigName);
      ZAssert.assertEquals(signature.getName(), this.sigName, "verified Text Signature is created");

      FormSignatureNew signew = new FormSignatureNew(app); 

      //Select signature which is to be Delete
      signew.zClick(Locators.zSignatureListView);
      signew.zClick("//td[contains(text(),'"+signature.getName()+"')]");

      //click Delete button
      app.zPageSignature.zToolbarPressButton(Button.B_DELETE);

      //click Save
      signew.zSubmit();
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

      // To check whether deleted signature is exist
      app.zGetActiveAccount().soapSend("<GetSignaturesRequest xmlns='urn:zimbraAccount'/>");

      String signame = app.zGetActiveAccount().soapSelectValue("//acct:signature[@name='" + this.sigName + "']","name");
      ZAssert.assertNull(signame, "Verify  signature is deleted");

   }
}
