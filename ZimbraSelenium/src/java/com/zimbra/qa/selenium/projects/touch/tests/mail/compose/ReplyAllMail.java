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

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.items.RecipientItem.RecipientType;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.touch.core.TouchCommonTest;
import com.zimbra.qa.selenium.projects.touch.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.touch.ui.mail.FormMailNew.Field;

public class ReplyAllMail extends TouchCommonTest {

	public ReplyAllMail() {
		logger.info("New "+ ReplyAllMail.class.getCanonicalName());		
	}
	
	@Test( description = "Reply all to message and verify it",
			groups = { "sanity" })
			
	public void ReplyAllMail_01() throws HarnessException {

		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		String body = "text <strong>bold"+ ZimbraSeleniumProperties.getUniqueString() +"</strong> text";
		
		MailItem mail = new MailItem();
		mail.dToRecipients.add(new RecipientItem(app.zGetActiveAccount(), RecipientType.To));
		mail.dCcRecipients.add(new RecipientItem(ZimbraAccount.AccountB().EmailAddress, RecipientType.To));
		mail.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		mail.dBodyText = body;
		
		String modifiedContent = " modified body" + ZimbraSeleniumProperties.getUniqueString();
		
		String contentHTML = XmlStringUtil.escapeXml(
				"<html>" +
					"<head></head>" +
					"<body>"+ mail.dBodyText +"</body>" +
				"</html>");

		// Send a message to the account
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<e t='c' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
						"<e t='c' a='"+ ZimbraAccount.AccountC().EmailAddress +"'/>" +
						"<su>"+ mail.dSubject +"</su>" +
						"<mp ct='multipart/alternative'>" +
						"<mp ct='text/plain'>" +
							"<content>"+ mail.dBodyText +"</content>" +
						"</mp>" +
						"<mp ct='text/html'>" +
							"<content>"+ contentHTML +"</content>" +
						"</mp>" +
					"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		
		// Get the mail item for the new message
		mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ mail.dSubject +")");
		
		app.zPageMail.zToolbarPressButton(Button.B_FOLDER_TREE);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);
		
		// Select the mail
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
		
		// Reply to mail
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLYALL);
		mailform.zFillField(Field.Body, modifiedContent);
		mailform.zSubmit();

		// To user verification
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest types='message' xmlns='urn:zimbraMail'>"
						+ "<query>subject:(" + mail.dSubject + ")</query>"
						+ "</SearchRequest>");
		String toid = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		
		ZimbraAccount.AccountA().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" + "<m id='" + toid
						+ "' html='1'/>" + "</GetMsgRequest>");

		String tofrom = ZimbraAccount.AccountA().soapSelectValue("//mail:e[@t='f']", "a");
		String toto = ZimbraAccount.AccountA().soapSelectValue("//mail:e[@t='t']", "a");
		String tocc1 = ZimbraAccount.AccountA().soapSelectValue("//mail:e[@t='c']", "a");
		String tocc2 = ZimbraAccount.AccountA().soapSelectValue("//mail:e[@t='c'][2]", "a");
		String tosubject = ZimbraAccount.AccountA().soapSelectValue("//mail:su", null);
		String tobody = ZimbraAccount.AccountA().soapSelectValue("//mail:content", null);
		
		ZAssert.assertEquals(tofrom, app.zGetActiveAccount().EmailAddress, "Verify the from field is correct");
		ZAssert.assertEquals(toto, ZimbraAccount.AccountA().EmailAddress, "Verify the to field is correct");
		ZAssert.assertEquals(tocc1, ZimbraAccount.AccountB().EmailAddress, "Verify the cc user1 field is correct");
		ZAssert.assertEquals(tocc2, ZimbraAccount.AccountC().EmailAddress, "Verify the cc user2 field is correct");
		ZAssert.assertEquals(tosubject, "Re: " + mail.dSubject, "Verify the subject field is correct");
		ZAssert.assertStringContains(tobody, body, "Verify the body content");
		ZAssert.assertStringContains(tobody.trim(), modifiedContent.trim(), "Verify the modified content");
		ZAssert.assertStringContains(tobody, "----- Original Message -----", "Verify the body content");
		
		// Cc user1 verification
		ZimbraAccount.AccountB().soapSend(
				"<SearchRequest types='message' xmlns='urn:zimbraMail'>"
						+ "<query>subject:(" + mail.dSubject + ")</query>"
						+ "</SearchRequest>");
		toid = ZimbraAccount.AccountB().soapSelectValue("//mail:m", "id");
		
		ZimbraAccount.AccountB().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" + "<m id='" + toid
						+ "' html='1'/>" + "</GetMsgRequest>");

		tofrom = ZimbraAccount.AccountB().soapSelectValue("//mail:e[@t='f']", "a");
		toto = ZimbraAccount.AccountB().soapSelectValue("//mail:e[@t='t']", "a");
		tocc1 = ZimbraAccount.AccountA().soapSelectValue("//mail:e[@t='c']", "a");
		tocc2 = ZimbraAccount.AccountA().soapSelectValue("//mail:e[@t='c'][2]", "a");
		tosubject = ZimbraAccount.AccountB().soapSelectValue("//mail:su", null);
		tobody = ZimbraAccount.AccountB().soapSelectValue("//mail:content", null);
		
		ZAssert.assertEquals(tofrom, app.zGetActiveAccount().EmailAddress, "Verify the from field is correct");
		ZAssert.assertEquals(toto, ZimbraAccount.AccountA().EmailAddress, "Verify the to field is correct");
		ZAssert.assertEquals(tocc1, ZimbraAccount.AccountB().EmailAddress, "Verify the cc user1 field is correct");
		ZAssert.assertEquals(tocc2, ZimbraAccount.AccountC().EmailAddress, "Verify the cc user2 field is correct");
		ZAssert.assertEquals(tosubject, "Re: " + mail.dSubject, "Verify the subject field is correct");
		ZAssert.assertStringContains(tobody, body, "Verify the body content");
		ZAssert.assertStringContains(tobody.trim(), modifiedContent.trim(), "Verify the modified content");
		ZAssert.assertStringContains(tobody, "----- Original Message -----", "Verify the body content");
		
		// Cc user2 verification
		ZimbraAccount.AccountC().soapSend(
				"<SearchRequest types='message' xmlns='urn:zimbraMail'>"
						+ "<query>subject:(" + mail.dSubject + ")</query>"
						+ "</SearchRequest>");
		toid = ZimbraAccount.AccountC().soapSelectValue("//mail:m", "id");
		
		ZimbraAccount.AccountC().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" + "<m id='" + toid
						+ "' html='1'/>" + "</GetMsgRequest>");

		tofrom = ZimbraAccount.AccountC().soapSelectValue("//mail:e[@t='f']", "a");
		toto = ZimbraAccount.AccountC().soapSelectValue("//mail:e[@t='t']", "a");
		tocc1 = ZimbraAccount.AccountA().soapSelectValue("//mail:e[@t='c']", "a");
		tocc2 = ZimbraAccount.AccountA().soapSelectValue("//mail:e[@t='c'][2]", "a");
		tosubject = ZimbraAccount.AccountC().soapSelectValue("//mail:su", null);
		tobody = ZimbraAccount.AccountC().soapSelectValue("//mail:content", null);
		
		ZAssert.assertEquals(tofrom, app.zGetActiveAccount().EmailAddress, "Verify the from field is correct");
		ZAssert.assertEquals(toto, ZimbraAccount.AccountA().EmailAddress, "Verify the to field is correct");
		ZAssert.assertEquals(tocc1, ZimbraAccount.AccountB().EmailAddress, "Verify the cc user1 field is correct");
		ZAssert.assertEquals(tocc2, ZimbraAccount.AccountC().EmailAddress, "Verify the cc user2 field is correct");
		ZAssert.assertEquals(tosubject, "Re: " + mail.dSubject, "Verify the subject field is correct");
		ZAssert.assertStringContains(tobody, body, "Verify the body content");
		ZAssert.assertStringContains(tobody.trim(), modifiedContent.trim(), "Verify the modified content");
		ZAssert.assertStringContains(tobody, "----- Original Message -----", "Verify the body content");

	}
	
}
