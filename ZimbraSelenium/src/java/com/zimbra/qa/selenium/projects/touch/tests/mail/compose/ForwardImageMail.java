/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.touch.tests.mail.compose;

import java.io.File;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.touch.core.TouchCommonTest;
import com.zimbra.qa.selenium.projects.touch.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.touch.ui.mail.FormMailNew.Field;

public class ForwardImageMail extends TouchCommonTest {

	public ForwardImageMail() {
		logger.info("New "+ ForwardImageMail.class.getCanonicalName());
	}
	
	@Bugs( ids = "81331")
	@Test( description = "Verify inline image present after hitting Forward from the mail",
			groups = { "smoke" })
			
	public void ForwardInlineImageMail_01() throws HarnessException {
		
		String subject = "inline image testing";
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		
		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email13/inline image.txt";
		LmtpInject.injectFile(ZimbraAccount.AccountZWC().EmailAddress, new File(MimeFolder));
		
		// Select the mail from inbox
		app.zPageMail.zToolbarPressButton(Button.B_FOLDER_TREE);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_FORWARD);
		ZAssert.assertTrue(app.zPageMail.zVerifyBodyContent(), "Verify the content of the mail");
		ZAssert.assertTrue(app.zPageMail.zVerifyInlineImageInReadingPane(), "Verify inline image showing in the reading pane");
		
		mailform =  new FormMailNew(app);
		mailform.zToolbarPressButton(Button.B_CANCEL);
		mailform.zPressButton(Button.B_NO);
		
	}
	
	@Bugs( ids = "81331")
	@Test( description = "Forward a mail which contains inline image and verify it at the receipient side",
			groups = { "smoke" })
			
	public void ForwardInlineImageMail_02() throws HarnessException {
		
		String subject = "inline image testing";
		String startTextOfBody = "body of the image starts..";
		String endTextOfBody = "body of the image ends..";;
		String imgSrc = "cid:c44b200d9264f34d048f41c1280beee5b1e7dd38@zimbra";
		String modifiedContent = " modified body" + ZimbraSeleniumProperties.getUniqueString();
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		
		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email13/inline image.txt";
		LmtpInject.injectFile(ZimbraAccount.AccountZWC().EmailAddress, new File(MimeFolder));
		
		// Select the mail from inbox
		app.zPageMail.zToolbarPressButton(Button.B_FOLDER_TREE);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_FORWARD);
		ZAssert.assertTrue(app.zPageMail.zVerifyInlineImageInComposedMessage(), "Verify image tag in the composed mail");
		
		mailform.zFillField(Field.To, ZimbraAccount.AccountB().EmailAddress);
		mailform.zFillField(Field.Body, modifiedContent);
		mailform.zSubmit();

		// Verify received mail
		ZimbraAccount.AccountB().soapSend(
				"<SearchRequest types='message' xmlns='urn:zimbraMail'>"
						+ "<query>subject:(" + subject + ")</query>"
						+ "</SearchRequest>");
		String toid = ZimbraAccount.AccountB().soapSelectValue("//mail:m", "id");
		
		ZimbraAccount.AccountB().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" + "<m id='" + toid
						+ "' html='1'/>" + "</GetMsgRequest>");

		String tobody = ZimbraAccount.AccountB().soapSelectValue("//mail:content", null);
		ZAssert.assertStringContains(tobody, startTextOfBody, "Verify the start text of the body");
		ZAssert.assertStringContains(tobody, endTextOfBody, "Verify the end text of the body");
		ZAssert.assertStringContains(tobody, modifiedContent, "Verify the modified content");
		ZAssert.assertStringContains(tobody, imgSrc, "Verify the image tag");
		ZAssert.assertTrue(app.zPageMail.zVerifyBodyContent(), "Verify image tag in the composed mail");
		
	}
	
	@Bugs( ids = "81069")
	@Test( description = "Verify external image present after hitting Forward from the mail",
			groups = { "smoke" })
			
	public void ForwardExternalImageMail_03() throws HarnessException {
		
		String subject = "external image testing";
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		
		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email13/external image.txt";
		LmtpInject.injectFile(ZimbraAccount.AccountZWC().EmailAddress, new File(MimeFolder));
		
		// Select the mail from inbox
		app.zPageMail.zToolbarPressButton(Button.B_FOLDER_TREE);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		app.zPageMail.zToolbarPressButton(Button.B_LOAD_IMAGES);
		
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_FORWARD);
		ZAssert.assertTrue(app.zPageMail.zVerifyBodyContent(), "Verify the content of the mail");
		ZAssert.assertTrue(app.zPageMail.zVerifyInlineImageInReadingPane(), "Verify inline image in the reading pane");
		
		mailform =  new FormMailNew(app);
		mailform.zToolbarPressButton(Button.B_CANCEL);
		mailform.zPressButton(Button.B_NO);
		
	}
	
	@Bugs( ids = "81069")
	@Test( description = "Forward a which mail contains external image and verify it at the receipient side",
			groups = { "smoke" })
			
	public void ForwardExternalImageMail_04() throws HarnessException {
		
		String subject = "external image testing";
		String startTextOfBody = "body of the image starts..";
		String endTextOfBody = "body of the image ends..";;
		String imgSrc = "cid:c44b200d9264f34d048f41c1280beee5b1e7dd38@zimbra";
		String modifiedContent = " modified body" + ZimbraSeleniumProperties.getUniqueString();
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		
		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email13/external image.txt";
		LmtpInject.injectFile(ZimbraAccount.AccountZWC().EmailAddress, new File(MimeFolder));
		
		// Select the mail from inbox
		app.zPageMail.zToolbarPressButton(Button.B_FOLDER_TREE);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		app.zPageMail.zToolbarPressButton(Button.B_LOAD_IMAGES);
		
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_FORWARD);
		ZAssert.assertTrue(app.zPageMail.zVerifyInlineImageInComposedMessage(), "Verify image tag in the composed mail");
		
		mailform.zFillField(Field.To, ZimbraAccount.AccountB().EmailAddress);
		mailform.zFillField(Field.Body, modifiedContent);
		mailform.zSubmit();

		// Verify received mail
		ZimbraAccount.AccountB().soapSend(
				"<SearchRequest types='message' xmlns='urn:zimbraMail'>"
						+ "<query>subject:(" + subject + ")</query>"
						+ "</SearchRequest>");
		String toid = ZimbraAccount.AccountB().soapSelectValue("//mail:m", "id");
		
		ZimbraAccount.AccountB().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" + "<m id='" + toid
						+ "' html='1'/>" + "</GetMsgRequest>");

		String tobody = ZimbraAccount.AccountB().soapSelectValue("//mail:content", null);
		ZAssert.assertStringContains(tobody, startTextOfBody, "Verify the start text of the body");
		ZAssert.assertStringContains(tobody, endTextOfBody, "Verify the end text of the body");
		ZAssert.assertStringContains(tobody, modifiedContent, "Verify the modified content");
		ZAssert.assertStringContains(tobody, imgSrc, "Verify the image tag");
		ZAssert.assertTrue(app.zPageMail.zVerifyBodyContent(), "Verify image tag in the composed mail");
		
	}
	
}