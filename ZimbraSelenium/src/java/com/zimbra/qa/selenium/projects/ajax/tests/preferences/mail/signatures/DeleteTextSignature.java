package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.signatures;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.SignatureItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.FormSignatureNew;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.PageSignature.Locators;

public class DeleteTextSignature extends AjaxCommonTest {
	String sigName = "signame" + ZimbraSeleniumProperties.getUniqueString();
	String sigBody = "sigbody" + ZimbraSeleniumProperties.getUniqueString();

	public DeleteTextSignature() throws HarnessException{
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
	}

	/**
	 * Added @beforeClass because after logged in ,when we try to create signature through soap,
	 * it doesn't shows in (GUI)'Pref/signatures' unless and until we refresh browser.
	 * @throws HarnessException
	 */
	@BeforeClass(groups = { "always" })
	public void CreateSignature() throws HarnessException {
		System.out.println(this.sigName);
		ZimbraAccount.AccountZWC().authenticate(SOAP_DESTINATION_HOST_TYPE.SERVER);
		ZimbraAccount.AccountZWC().soapSend(
				"<CreateSignatureRequest xmlns='urn:zimbraAccount'>"
				+ "<signature name='" + this.sigName + "' >"
				+ "<content type='text/plain'>" + this.sigBody
				+ "</content>" + "</signature>"
				+ "</CreateSignatureRequest>");

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

		// To check whether deleted signature is exist
		app.zGetActiveAccount().soapSend("<GetSignaturesRequest xmlns='urn:zimbraAccount'/>");

		String signame = app.zGetActiveAccount().soapSelectValue("//acct:signature[@name='" + this.sigName + "']","name");
		ZAssert.assertNull(signame, "Verify  signature is deleted");

	}
}
