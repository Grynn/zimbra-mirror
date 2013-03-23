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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.signatures;



import java.util.HashMap;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.SignatureItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;


public class FwdReplyTextSignatureAboveIncludeMsg extends AjaxCommonTest {
	String sigName = "signame" + ZimbraSeleniumProperties.getUniqueString();
	String sigBody = "sigbody" + ZimbraSeleniumProperties.getUniqueString();

	@SuppressWarnings("serial")
	public FwdReplyTextSignatureAboveIncludeMsg() {
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefComposeFormat", "text");
				put("zimbraPrefGroupMailBy", "message");
			}
		};
	}

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
	 * Test case : Verify Text Signature AboveIncludedMsg While Fwd'ing
	 * Create signature through soap 
	 * Send message with text signature through soap
	 * Select Same Msg and click Fwd
	 * Click Options dropdown and select Signature
	 * Verify signature should place above included message while fwd'ing msg
	 * @throws HarnessException
	 */
	@Test(description = "Verify Text Signature AboveIncludedMsg While Fwd'ing- Verify through GUI ", groups = { "functional" })
	public void FwdMsgWithTextSignatureAboveIncludeMsg_01() throws HarnessException {

		// Signature is created
		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(),SystemFolder.Inbox);
		SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), this.sigName);
		ZAssert.assertEquals(signature.getName(), this.sigName,"verified Text Signature is created");

		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();

		// Send a message to the account(self)
		ZimbraAccount.AccountZWC().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
				"<m>" +
				"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
				"<su>"+ subject +"</su>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
				"</mp>" +
				"</m>" +
		"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inboxFolder);

		// Select message
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Forward the item
		actual.zPressButton(Button.B_FORWARD);
		ZAssert.assertTrue(actual.zGetMailPropertyAsText(com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.Field.Subject).contains("Fwd"),"Verify Fwd Window");

		//Click Options Drop Down and select Signature
		app.zPageMail.zToolbarPressPulldown(Button.B_OPTIONS,Button.O_ADD_FWD_SIGNATURE,this.sigName);		

		//Verify Signature is place above included message.
		actual.zVerifySignaturePlaceInText("AboveIncludedMsg", this.sigBody, "Forward");

		//Closing Fwd compose tab
		app.zPageMail.zClickAt(FormMailNew.Locators.zCancelIconBtn, "0,0");

	}
	/**
	 * Test case : Verify Text Signature AboveIncludedMsg While Reply'ing
	 * Create signature through soap 
	 * Send message with text signature through soap
	 * Select Same Msg and click Reply from toolbar
	 * Click Options dropdown and select Signature
	 * Verify signature should place above included message while Replying msg
	 * @throws HarnessException
	 */

	@Test(description = "Verify Text Signature AboveIncludedMsg While Replying Msg ", groups = { "functional" })
	public void ReplyMsgWithTextSignatureAboveIncludeMsg_02() throws HarnessException {

		// Signature is created
		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(),SystemFolder.Inbox);

		SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), this.sigName);
		ZAssert.assertEquals(signature.getName(), this.sigName,"verified Text Signature is created");

		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();


		// Send a message to the account(self)
		ZimbraAccount.AccountZWC().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
				"<m>" +
				"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
				"<su>"+ subject +"</su>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
				"</mp>" +
				"</m>" +
		"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inboxFolder);

		// Select message
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Reply the item
		//	app.zPageMail.zClickAt(Locators.zReplyToolbarButton, "0,0");
		actual.zPressButton(Button.B_REPLY);
		ZAssert.assertTrue(actual.zGetMailPropertyAsText(com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.Field.Subject).contains("Re"),"Verify Reply Window");

		//Click Options Drop Down and select Signature
		app.zPageMail.zToolbarPressPulldown(Button.B_OPTIONS,Button.O_ADD_Reply_SIGNATURE,this.sigName);		

		//Verify Signature is place above included message.
		actual.zVerifySignaturePlaceInText("AboveIncludedMsg", this.sigBody, "Reply");
		//Clsoing Reply Window
		app.zPageMail.zClickAt(FormMailNew.Locators.zCancelIconBtn, "0,0");

	}
	/**
	 * Test case : Verify Text Signature AboveIncludedMsg While ReplyingAll
	 * Create signature through soap 
	 * Send message with text signature through soap
	 * Select Same Msg and click ReplyAll from toolbar
	 * Click Options dropdown and select Signature
	 * Verify signature should place above included message while Replying msg
	 * @throws HarnessException
	 */
	@Test(description = "Verify Text Signature AboveIncludedMsg While ReplyingAll Msg ", groups = { "functional" })
	public void ReplyAllMsgWithTextSignatureAboveIncludeMsg_03() throws HarnessException {

		// Signature is created
		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(),SystemFolder.Inbox);
		SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), this.sigName);
		ZAssert.assertEquals(signature.getName(), this.sigName,"verified Text Signature is created");

		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();


		// Send a message to the account(self)
		ZimbraAccount.AccountZWC().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
				"<m>" +
				"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
				"<su>"+ subject +"</su>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
				"</mp>" +
				"</m>" +
		"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inboxFolder);

		// Select message
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Reply the item
		//app.zPageMail.zClickAt(Locators.zReplyAllToolbarButton, "0,0");
		actual.zPressButton(Button.B_REPLYALL);
		ZAssert.assertTrue(actual.zGetMailPropertyAsText(com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.Field.Subject).contains("Re"),"Verify ReplyAll Window");

		//Click Options Drop Down and select Signature
		app.zPageMail.zToolbarPressPulldown(Button.B_OPTIONS,Button.O_ADD_ReplyAll_SIGNATURE,this.sigName);		

		//Verify Signature is place above included message in ReplyAll window.
		actual.zVerifySignaturePlaceInText("AboveIncludedMsg", this.sigBody, "ReplyAll");	
		app.zPageMail.zClickAt(FormMailNew.Locators.zCancelIconBtn, "0,0");

	}
}

