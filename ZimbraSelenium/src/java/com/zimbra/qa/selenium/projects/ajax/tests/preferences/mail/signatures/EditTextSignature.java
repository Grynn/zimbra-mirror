package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.signatures;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.SignatureItem;

import com.zimbra.qa.selenium.framework.ui.Action;

import com.zimbra.qa.selenium.framework.util.HarnessException;

import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.FormSignatureNew;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.PageSignature;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.FormSignatureNew.Field;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.PageSignature.Locators;

public class EditTextSignature extends AjaxCommonTest {
	String sigName = "signame" + ZimbraSeleniumProperties.getUniqueString();
	String sigBody = "sigbody" + ZimbraSeleniumProperties.getUniqueString();

	public EditTextSignature() throws HarnessException {

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
	 * Test case : 
	 * Create signature through soap 
	 * Edit it through GUI and Verify edit text signature through soap
	 * @throws HarnessException
	 */
	@Test(description = " Edit and verify text signature through soap", groups = { "smoke" })
	public void EditTextSignature_01() throws HarnessException {

		String sigEditName = "editsigname"+ ZimbraSeleniumProperties.getUniqueString();
		String sigEditBody = "editsigbody"+ ZimbraSeleniumProperties.getUniqueString();

		//Signature is created
		SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), this.sigName);
		ZAssert.assertEquals(signature.getName(), this.sigName, "verified Text Signature is created");

		//Click on Mail/signature
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK,TreeItem.MailSignatures);
		SleepUtil.sleepSmall();
		FormSignatureNew signew = new FormSignatureNew(app);

		//Select signature which is to be edit
		signew.zClick(Locators.zSignatureListView);
		signew.zClick("//td[contains(text(),'"+signature.getName()+"')]");

		//Verify Body contents
		PageSignature pagesig = new PageSignature(app);
		String signaturebodytext = pagesig.zGetSignatureBodyText();
		ZAssert.assertStringContains(signaturebodytext, this.sigBody,"Verify the text signature body");


		//Edit signame and sigbody
		signew.zFillField(Field.SignatureName, sigEditName);
		signew.zFillField(Field.SignatureBody, sigEditBody);
		signew.zSubmit();

		SignatureItem editsignature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), sigEditName);

		//Verify Edited signature name and body contents
		ZAssert.assertEquals(editsignature.getName(),sigEditName,"Verify Edited signature name");
		ZAssert.assertEquals(editsignature.dBodyText,sigEditBody,"Verify Edited text signature body");
		ZAssert.assertStringDoesNotContain(editsignature.getName(), this.sigName, "Verify after edit 1st signature  does not present");

	}

}
