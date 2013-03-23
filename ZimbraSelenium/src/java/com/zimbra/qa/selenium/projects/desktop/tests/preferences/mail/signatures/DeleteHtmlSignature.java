/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.desktop.tests.preferences.mail.signatures;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.SignatureItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.TreePreferences.TreeItem;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.signature.FormSignatureNew;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.signature.PageSignature;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.signature.PageSignature.Locators;

public class DeleteHtmlSignature extends AjaxCommonTest {

   String sigHtmlName = "signame" + ZimbraSeleniumProperties.getUniqueString();
   String bodyHTML = "text<strong>bold"+ ZimbraSeleniumProperties.getUniqueString() + "</strong>text";
   String contentHTML = XmlStringUtil.escapeXml("<html>" + "<head></head>"
         + "<body>" + bodyHTML + "</body>" + "</html>");

   public DeleteHtmlSignature() throws HarnessException {
      super.startingPage = app.zPagePreferences;
      super.startingAccountPreferences = null;
   }

   public void _createHtmlSignature(ZimbraAccount account) throws HarnessException {

      account.authenticate(SOAP_DESTINATION_HOST_TYPE.SERVER);
      account.soapSend(
            "<CreateSignatureRequest xmlns='urn:zimbraAccount'>"
            + "<signature name='" + this.sigHtmlName + "' >"
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
    * Test case :Create Html signature through soap then delete and verify signature through GUI
    * @Steps:
    * Create Html signature through soap
    * Delete signature using delete button.
    * Verify signature doesn't exist from soap
    * @throws HarnessException
    */
   @Test(description = "Delete Html signature using Delete button and verify through soap", groups = { "smoke" })
   public void DeletetHtmlSignature_01() throws HarnessException {

      _createHtmlSignature(app.zGetActiveAccount());

      // Click on Mail/signature
      app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK,TreeItem.MailSignatures);

      //Verify HTML Signature is created
      SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), this.sigHtmlName);
      ZAssert.assertEquals(signature.getName(), this.sigHtmlName,"verified Html Signature name ");    

      PageSignature pagesig = new PageSignature(app);
      FormSignatureNew signew = new FormSignatureNew(app);

      //Select created signature signature 
      pagesig.zClick(Locators.zSignatureListView);
      app.zPageSignature.zClick("//td[contains(text(),'"+signature.getName()+"')]");   

      //click Delete button
      app.zPageSignature.zToolbarPressButton(Button.B_DELETE);

      //click Save
      signew.zSubmit();
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

      // To check whether deleted signature is exist
      app.zGetActiveAccount().soapSend("<GetSignaturesRequest xmlns='urn:zimbraAccount'/>");

      String signame = app.zGetActiveAccount().soapSelectValue("//acct:signature[@name='" + this.sigHtmlName + "']","name");
      ZAssert.assertNull(signame, "Verify  signature is deleted");

   }

}
