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

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.items.RecipientItem.RecipientType;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.touch.core.TouchCommonTest;
import com.zimbra.qa.selenium.projects.touch.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.touch.ui.mail.FormMailNew.Field;

public class ForwardMail extends TouchCommonTest {

	public ForwardMail() {
		logger.info("New "+ ForwardMail.class.getCanonicalName());
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "html");
	}
	
	@Test( description = "Forward a html mail",
			groups = { "sanity" })
			
	public void ForwardHtmlMail() throws HarnessException {
		
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		String body = "text <strong>bold"+ ZimbraSeleniumProperties.getUniqueString() +"</strong> text";
		
		MailItem mail = new MailItem();
		mail.dToRecipients.add(new RecipientItem(ZimbraAccount.AccountA(), RecipientType.To));
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
		
		// Forward the mail
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_FORWARD);
		mailform.zFillField(Field.To, ZimbraAccount.AccountB().EmailAddress);
		mailform.zFillField(Field.Body, modifiedContent);
		mailform.zSubmit();

		// Verify received mail
		ZimbraAccount.AccountB().soapSend(
				"<SearchRequest types='message' xmlns='urn:zimbraMail'>"
						+ "<query>subject:(" + mail.dSubject + ")</query>"
						+ "</SearchRequest>");
		String toid = ZimbraAccount.AccountB().soapSelectValue("//mail:m", "id");
		
		ZimbraAccount.AccountB().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" + "<m id='" + toid
						+ "' html='1'/>" + "</GetMsgRequest>");

		String tofrom = ZimbraAccount.AccountB().soapSelectValue("//mail:e[@t='f']", "a");
		String toto = ZimbraAccount.AccountB().soapSelectValue("//mail:e[@t='t']", "a");
		String tosubject = ZimbraAccount.AccountB().soapSelectValue("//mail:su", null);
		String tobody = ZimbraAccount.AccountB().soapSelectValue("//mail:content", null);
		
		ZAssert.assertEquals(tofrom, app.zGetActiveAccount().EmailAddress, "Verify the from field is correct");
		ZAssert.assertEquals(toto, ZimbraAccount.AccountB().EmailAddress, "Verify the to field is correct");
		ZAssert.assertEquals(tosubject, "Fwd: " + mail.dSubject, "Verify the subject field is correct");
		ZAssert.assertStringContains(tobody, body, "Verify the body content");
		ZAssert.assertStringContains(tobody, modifiedContent, "Verify the modified content");
		ZAssert.assertStringContains(tobody, "----- Forwarded Message -----", "Verify the body content");
		
	}

}
