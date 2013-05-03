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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.signatures;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.SignatureItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.FormSignatureNew;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.PageSignature;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.PageSignature.Locators;

public class DeleteHtmlSignature extends AjaxCommonTest {

	String sigHtmlName = "signame" + ZimbraSeleniumProperties.getUniqueString();
	String bodyHTML = "text<strong>bold"+ ZimbraSeleniumProperties.getUniqueString() + "</strong>text";
	String contentHTML = XmlStringUtil.escapeXml("<html>" + "<head></head>"
			+ "<body>" + bodyHTML + "</body>" + "</html>");

	public DeleteHtmlSignature() throws HarnessException {
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
	}
	/**
	 * Added @beforeClass because after logged in ,when we try to create
	 * signature through soap, it doesn't shows in(GUI) 'Pref/signatures' unless and
	 * until we refresh browser.
	 * 
	 * @throws HarnessException
	 */
	@BeforeMethod(groups = { "always" })
	public void CreateHtmlSignature() throws HarnessException {

		//ZimbraAccount.AccountZWC().authenticate(SOAP_DESTINATION_HOST_TYPE.SERVER);
		ZimbraAccount.AccountZWC().soapSend(
				"<CreateSignatureRequest xmlns='urn:zimbraAccount'>"
				+ "<signature name='" + this.sigHtmlName + "' >"
				+ "<content type='text/html'>'" + this.contentHTML
				+ "'</content>" + "</signature>"
				+ "</CreateSignatureRequest>");

		// Logout and login
		this.app.zPageLogin.zNavigateTo();
		this.app.zPagePreferences.zNavigateTo();

		logger.info("CreateSignature: finish");
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

		// To check whether deleted signature is exist
		app.zGetActiveAccount().soapSend("<GetSignaturesRequest xmlns='urn:zimbraAccount'/>");

		String signame = app.zGetActiveAccount().soapSelectValue("//acct:signature[@name='" + this.sigHtmlName + "']","name");
		ZAssert.assertNull(signame, "Verify  signature is deleted");

	}

}
