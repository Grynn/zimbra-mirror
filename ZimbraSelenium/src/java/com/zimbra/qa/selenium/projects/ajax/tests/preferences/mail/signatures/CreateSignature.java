package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.signatures;

import org.testng.annotations.Test;


import com.zimbra.qa.selenium.framework.items.SignatureItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.FormSignatureNew;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.FormSignatureNew.Field;


public class CreateSignature extends AjaxCommonTest {
	public CreateSignature() {
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
	}

	@Test(description = "Create Simple text signature through GUI", groups = { "sanity" })
	public void CreateBasicTextSignature() throws HarnessException {

		String sigName = "signame" + ZimbraSeleniumProperties.getUniqueString();
		String sigBody = "sigbody" + ZimbraSeleniumProperties.getUniqueString();

		// click on signature from left pane
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK,TreeItem.MailSignatures);

		//Click on New signature button
		FormSignatureNew signew =(FormSignatureNew) app.zPageSignature.zToolbarPressButton(Button.B_NEW);

		// Fill Signature Name and body
		signew.zFillField(Field.SignatureName, sigName);
		signew.zFillField(Field.SignatureBody, sigBody);
		signew.zSubmit();

		SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), sigName);
		
		//Verify signature name and body content	
		ZAssert.assertEquals(signature.getName(),sigName,"Verify signature Name");
		ZAssert.assertEquals(signature.dBodyText,sigBody,"Verify Text signature body");
	}


	@Test(description = "Create Simple Html signature through GUI", groups = { "sanity" })
	public void CreateBasicHtmlSignature() throws HarnessException {

		String sigName = "signame" + ZimbraSeleniumProperties.getUniqueString();
		String sigBody = "sigbody" + ZimbraSeleniumProperties.getUniqueString();

		// click on signature from left pane
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK,TreeItem.MailSignatures);

		//Click on New signature button
		FormSignatureNew signew =(FormSignatureNew)app.zPageSignature.zToolbarPressButton(Button.B_NEW);

		//select html format from drop down
		signew.zSelectFormat("html");

		// Fill Signature Name and body
		signew.zFillField(Field.SignatureName, sigName);
		signew.zFillField(Field.SignatureHtmlBody, sigBody);
		signew.zSubmit();

		SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), sigName);
		
		//Verify signature name and body contents
		ZAssert.assertEquals(signature.getName(),sigName,"Verify signature Name");
		ZAssert.assertStringContains(signature.dBodyHtmlText, sigBody, "Verify Html signature body");
		//ZAssert.assertEquals(signature.dBodyHtmlText,sigBody,"Verify Html signature body");


	}
}
