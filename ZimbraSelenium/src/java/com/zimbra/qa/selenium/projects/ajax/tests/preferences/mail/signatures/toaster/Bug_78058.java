/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.signatures.toaster;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.FormSignatureNew;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.FormSignatureNew.Field;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature.PageSignature.Locators;


public class Bug_78058 extends AjaxCommonTest {
	public Bug_78058() {
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
	}

	@Test(description = "Verify Toast Msg for Empty Signature Name", groups = { "smoke" })
	public void VerifyToastMsgForEmptySigName() throws HarnessException {

		String sigName = "";		
		String sigBody = "sigbody" + ZimbraSeleniumProperties.getUniqueString();

		// click on signature from left pane
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK,TreeItem.MailSignatures);

		//Click on New signature button
		FormSignatureNew signew =(FormSignatureNew) app.zPageSignature.zToolbarPressButton(Button.B_NEW);

		// Empty Signature Name and some text in body
		signew.zFillField(Field.SignatureName, sigName);
		signew.zFillField(Field.SignatureBody, sigBody);
		signew.zSubmit();

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "Signature name is empty. It's required","Verify toast message:Signature name is empty. It's required");

	}

	@Test(description = "Verify Toast Msg for Empty Signature body", groups = { "smoke" })
	public void VerifyToastMsgForEmptySigBody_Bug78058() throws HarnessException {

		String sigName = "signame" + ZimbraSeleniumProperties.getUniqueString();
		String sigBody = "";

		// click on signature from left pane
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK,TreeItem.MailSignatures);

		//Click on New signature button
		FormSignatureNew signew =(FormSignatureNew) app.zPageSignature.zToolbarPressButton(Button.B_NEW);
		SleepUtil.sleepMedium();
		
		// Empty Signature body and some text in Name
		signew.zFillField(Field.SignatureName, sigName);
	
		signew.zFillField(Field.SignatureBody, sigBody);
		
		signew.zSubmit();
		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "Signature value is empty. It's required","Verify toast message:Signature value is empty. It's required");
		
		//This is special case where we need to explicitly delete signature to avoid  failing other test cases  
		//Select signature which is to be Delete
		signew.zClick(Locators.zSignatureListView);
		signew.zClick("//td[contains(text(),'"+sigName+"')]");
		//click Delete button
		app.zPageSignature.zToolbarPressButton(Button.B_DELETE);

	}
}