package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.signatures;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.FormSignatureNew;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.PageSignature;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.FormSignatureNew.Field;

public class CreateSignature extends AjaxCommonTest {
	public CreateSignature() {
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
	}

	@Test(description = "Create Simple text signature", groups = { "sanity" })
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

		// Verify toast message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "Preferences Saved","Verify toast message: Preferences Saved");

		// Verify Created signature name from SignatureListView
		app.zPagePreferences.zNavigateTo();
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK,TreeItem.MailSignatures);

		PageSignature pagesig = new PageSignature(app);
		String SignatureListViewName = pagesig.zGetSignatureNameFromListView();
		ZAssert.assertStringContains(SignatureListViewName, sigName,"Verify signature  is present in SignatureList view");
	}
	
	
	@Test(description = "Create Simple Html signature", groups = { "sanity" })
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

		// Verify toast message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "Preferences Saved","Verify toast message: Preferences Saved");

		// Verify Created signature name from SignatureListView
		app.zPagePreferences.zNavigateTo();
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK,TreeItem.MailSignatures);

		PageSignature pagesig = new PageSignature(app);
		String SignatureListViewName = pagesig.zGetSignatureNameFromListView();
		ZAssert.assertStringContains(SignatureListViewName, sigName,"Verify  html signature  is present in SignatureList view");
	}
}
